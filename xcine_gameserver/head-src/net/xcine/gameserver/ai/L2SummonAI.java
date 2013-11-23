/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package net.xcine.gameserver.ai;

import static net.xcine.gameserver.ai.CtrlIntention.AI_INTENTION_ATTACK;
import static net.xcine.gameserver.ai.CtrlIntention.AI_INTENTION_FOLLOW;
import static net.xcine.gameserver.ai.CtrlIntention.AI_INTENTION_IDLE;

import java.util.concurrent.Future;

import net.xcine.Config;
import net.xcine.gameserver.GeoData;
import net.xcine.gameserver.ThreadPoolManager;
import net.xcine.gameserver.model.L2Object;
import net.xcine.gameserver.model.L2Skill;
import net.xcine.gameserver.model.actor.L2Character;
import net.xcine.gameserver.model.actor.L2Character.AIAccessor;
import net.xcine.gameserver.model.actor.L2Summon;
import net.xcine.util.Rnd;

public class L2SummonAI extends L2PlayableAI implements Runnable
{
	private static final int AVOID_RADIUS = 70;
	
	private volatile boolean _thinking; // to prevent recursive thinking
	private volatile boolean _startFollow = ((L2Summon) _actor).getFollowStatus();
	private L2Character _lastAttack = null;
	
	private volatile boolean _startAvoid = false;
	private Future<?> _avoidTask = null;
	
	public L2SummonAI(AIAccessor accessor)
	{
		super(accessor);
	}
	
	@Override
	protected void onIntentionIdle()
	{
		stopFollow();
		_startFollow = false;
		onIntentionActive();
	}
	
	@Override
	protected void onIntentionActive()
	{
		L2Summon summon = (L2Summon) _actor;
		if (_startFollow)
			setIntention(AI_INTENTION_FOLLOW, summon.getOwner());
		else
			super.onIntentionActive();
	}
	
	@Override
	synchronized void changeIntention(CtrlIntention intention, Object arg0, Object arg1)
	{
		switch (intention)
		{
			case AI_INTENTION_ACTIVE:
			case AI_INTENTION_FOLLOW:
				startAvoidTask();
				break;
			default:
				stopAvoidTask();
		}
		
		super.changeIntention(intention, arg0, arg1);
	}
	
	private void thinkAttack()
	{
		L2Character target = (L2Character) getTarget();
		if (target == null)
			return;
		
		if (checkTargetLostOrDead(target))
		{
			setTarget(null);
			return;
		}
		
		if (maybeMoveToPawn(target, _actor.getPhysicalAttackRange()))
		{
			_actor.breakAttack();
			return;
		}
		
		clientStopMoving(null);
		_accessor.doAttack(target);
	}
	
	private void thinkCast()
	{
		if (checkTargetLost(getTarget()))
		{
			setTarget(null);
			return;
		}
		
		boolean val = _startFollow;
		if (maybeMoveToPawn(getTarget(), _skill.getCastRange()))
			return;
		
		clientStopMoving(null);
		((L2Summon) _actor).setFollowStatus(false);
		setIntention(AI_INTENTION_IDLE);
		
		_startFollow = val;
		_accessor.doCast(_skill);
	}
	
	private void thinkPickUp()
	{
		if (checkTargetLost(getTarget()))
			return;
		
		if (maybeMoveToPawn(getTarget(), 36))
			return;
		
		setIntention(AI_INTENTION_IDLE);
		((L2Summon.AIAccessor) _accessor).doPickupItem(getTarget());
	}
	
	private void thinkInteract()
	{
		if (checkTargetLost(getTarget()))
			return;
		
		if (maybeMoveToPawn(getTarget(), 36))
			return;
		
		setIntention(AI_INTENTION_IDLE);
	}
	
	@Override
	protected void onEvtThink()
	{
		if (_thinking || _actor.isCastingNow() || _actor.isAllSkillsDisabled())
			return;
		
		_thinking = true;
		try
		{
			switch (getIntention())
			{
				case AI_INTENTION_ATTACK:
					thinkAttack();
					break;
				case AI_INTENTION_CAST:
					thinkCast();
					break;
				case AI_INTENTION_PICK_UP:
					thinkPickUp();
					break;
				case AI_INTENTION_INTERACT:
					thinkInteract();
					break;
			}
		}
		finally
		{
			_thinking = false;
		}
	}
	
	@Override
	protected void onEvtFinishCasting()
	{
		if (_lastAttack == null)
			((L2Summon) _actor).setFollowStatus(_startFollow);
		else
		{
			setIntention(CtrlIntention.AI_INTENTION_ATTACK, _lastAttack);
			_lastAttack = null;
		}
	}
	
	@Override
	protected void onEvtAttacked(L2Character attacker)
	{
		super.onEvtAttacked(attacker);
		
		avoidAttack(attacker);
	}
	
	@Override
	protected void onEvtEvaded(L2Character attacker)
	{
		super.onEvtEvaded(attacker);
		
		avoidAttack(attacker);
	}
	
	private void avoidAttack(L2Character attacker)
	{
		// trying to avoid if summon near owner
		if (((L2Summon) _actor).getOwner() != null && ((L2Summon) _actor).getOwner() != attacker && ((L2Summon) _actor).getOwner().isInsideRadius(_actor, 2 * AVOID_RADIUS, true, false))
			_startAvoid = true;
	}
	
	@Override
	public void run()
	{
		if (_startAvoid)
		{
			_startAvoid = false;
			
			if (!_clientMoving && !_actor.isDead() && !_actor.isMovementDisabled())
			{
				final int ownerX = ((L2Summon) _actor).getOwner().getX();
				final int ownerY = ((L2Summon) _actor).getOwner().getY();
				final double angle = Math.toRadians(Rnd.get(-90, 90)) + Math.atan2(ownerY - _actor.getY(), ownerX - _actor.getX());
				
				final int targetX = ownerX + (int) (AVOID_RADIUS * Math.cos(angle));
				final int targetY = ownerY + (int) (AVOID_RADIUS * Math.sin(angle));
				if (Config.GEODATA == 0 || GeoData.getInstance().canMoveFromToTarget(_actor.getX(), _actor.getY(), _actor.getZ(), targetX, targetY, _actor.getZ()))
					moveTo(targetX, targetY, _actor.getZ());
			}
		}
	}
	
	public void notifyFollowStatusChange()
	{
		_startFollow = !_startFollow;
		switch (getIntention())
		{
			case AI_INTENTION_ACTIVE:
			case AI_INTENTION_FOLLOW:
			case AI_INTENTION_IDLE:
			case AI_INTENTION_MOVE_TO:
			case AI_INTENTION_PICK_UP:
				((L2Summon) _actor).setFollowStatus(_startFollow);
		}
	}
	
	public void setStartFollowController(boolean val)
	{
		_startFollow = val;
	}
	
	@Override
	protected void onIntentionCast(L2Skill skill, L2Object target)
	{
		if (getIntention() == AI_INTENTION_ATTACK)
			_lastAttack = (L2Character) getTarget();
		else
			_lastAttack = null;
		
		super.onIntentionCast(skill, target);
	}
	
	private void startAvoidTask()
	{
		if (_avoidTask == null)
			_avoidTask = ThreadPoolManager.getInstance().scheduleAiAtFixedRate(this, 100, 100);
	}
	
	private void stopAvoidTask()
	{
		if (_avoidTask != null)
		{
			_avoidTask.cancel(false);
			_avoidTask = null;
		}
	}
	
	@Override
	public void stopAITask()
	{
		stopAvoidTask();
		super.stopAITask();
	}
}