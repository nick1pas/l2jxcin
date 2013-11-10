/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */
package net.xcine.gameserver.ai;

import static net.xcine.gameserver.ai.CtrlIntention.AI_INTENTION_ATTACK;
import static net.xcine.gameserver.ai.CtrlIntention.AI_INTENTION_FOLLOW;
import static net.xcine.gameserver.ai.CtrlIntention.AI_INTENTION_IDLE;

import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.xcine.Config;
import net.xcine.gameserver.controllers.GameTimeController;
import net.xcine.gameserver.model.L2Attackable;
import net.xcine.gameserver.model.L2Character;
import net.xcine.gameserver.model.L2Object;
import net.xcine.gameserver.model.L2Skill;
import net.xcine.gameserver.model.L2Summon;
import net.xcine.gameserver.model.actor.instance.L2NpcInstance;
import net.xcine.gameserver.model.actor.instance.L2PcInstance;
import net.xcine.gameserver.model.actor.instance.L2PlayableInstance;
import net.xcine.gameserver.model.actor.position.L2CharPosition;
import net.xcine.gameserver.model.extender.BaseExtender.EventType;
import net.xcine.gameserver.network.serverpackets.ActionFailed;
import net.xcine.gameserver.network.serverpackets.AutoAttackStart;
import net.xcine.gameserver.network.serverpackets.AutoAttackStop;
import net.xcine.gameserver.network.serverpackets.CharMoveToLocation;
import net.xcine.gameserver.network.serverpackets.Die;
import net.xcine.gameserver.network.serverpackets.MoveToLocationInVehicle;
import net.xcine.gameserver.network.serverpackets.MoveToPawn;
import net.xcine.gameserver.network.serverpackets.StopMove;
import net.xcine.gameserver.network.serverpackets.StopRotation;
import net.xcine.gameserver.taskmanager.AttackStanceTaskManager;
import net.xcine.gameserver.thread.ThreadPoolManager;

/**
 * Mother class of all objects AI in the world.<BR>
 * <BR>
 * AbastractAI :<BR>
 * <BR>
 * <li>L2CharacterAI</li><BR>
 * <BR>
 */
abstract class AbstractAI implements Ctrl
{

	protected static final Logger _log = Logger.getLogger(AbstractAI.class.getName());

	class FollowTask implements Runnable
	{
		protected int _range = 60;
		protected boolean newtask = true;

		public FollowTask()
		{
		//null
		}

		public FollowTask(int range)
		{
			_range = range;
		}

		@Override
		public void run()
		{
			try
			{
				if(_followTask == null)
					return;
				
				final L2Character follow = getFollowTarget();

				if(follow == null)
				{
					stopFollow();
					return;
				}
				if(!_actor.isInsideRadius(follow, _range, true, false))
				{
					moveToPawn(follow, _range);
				}
				else if(newtask)
				{
					newtask = false;
					_actor.broadcastPacket(new MoveToPawn(_actor, follow, _range));
				}
			}
			catch(Throwable t)
			{
				if(Config.ENABLE_ALL_EXCEPTIONS)
					t.printStackTrace();
				
				_log.log(Level.WARNING, "", t);
			}
		}
	}

	protected final L2Character _actor;
	protected final L2Character.AIAccessor _accessor;
	private CtrlIntention _intention = AI_INTENTION_IDLE;
	private Object _intentionArg0 = null;
	private Object _intentionArg1 = null;
	protected boolean _clientMoving;
	protected boolean _clientAutoAttacking;
	protected int _clientMovingToPawnOffset;
	private L2Object _target;
	private L2Character _castTarget;
	private L2Character _attackTarget;
	private L2Character _followTarget;
	private int _moveToPawnTimeout;
	protected Future<?> _followTask = null;
	private static final int FOLLOW_INTERVAL = 1000;
	private static final int ATTACK_FOLLOW_INTERVAL = 500;

	/**
	 * @param accessor The AI accessor of the L2Character
	 */
	protected AbstractAI(L2Character.AIAccessor accessor)
	{
		_accessor = accessor;

		// Get the L2Character managed by this Accessor AI
		_actor = accessor.getActor();
	}

	@Override
	public L2Character getActor()
	{
		return _actor;
	}

	/** 
	 * @param intention The new Intention to set to the AI
	 * @param arg0 The first parameter of the Intention
	 * @param arg1 The second parameter of the Intention
	 */
	public synchronized void changeIntention(CtrlIntention intention, Object arg0, Object arg1)
	{
		_intention = intention;
		_intentionArg0 = arg0;
		_intentionArg1 = arg1;
	}

	/**
	 * @param intention The new Intention to set to the AI
	 */
	@Override
	public final void setIntention(CtrlIntention intention)
	{
		setIntention(intention, null, null);
	}

	/**
	 * @param intention The new Intention to set to the AI
	 * @param arg0 The first parameter of the Intention (optional target)
	 */
	@Override
	public final void setIntention(CtrlIntention intention, Object arg0)
	{
		setIntention(intention, arg0, null);
	}

	/**
	 * @param intention The new Intention to set to the AI
	 * @param arg0 The first parameter of the Intention (optional target)
	 * @param arg1 The second parameter of the Intention (optional target)
	 */
	@Override
	public final void setIntention(CtrlIntention intention, Object arg0, Object arg1)
	{
		if(!_actor.isVisible() || !_actor.hasAI())
			return;

		if(intention != AI_INTENTION_FOLLOW && intention != AI_INTENTION_ATTACK)
		{
			stopFollow();
		}

		switch(intention)
		{
			case AI_INTENTION_IDLE:
				onIntentionIdle();
				break;
			case AI_INTENTION_ACTIVE:
				onIntentionActive();
				break;
			case AI_INTENTION_REST:
				onIntentionRest();
				break;
			case AI_INTENTION_ATTACK:
				onIntentionAttack((L2Character) arg0);
				break;
			case AI_INTENTION_CAST:
				onIntentionCast((L2Skill) arg0, (L2Object) arg1);
				break;
			case AI_INTENTION_MOVE_TO:
				onIntentionMoveTo((L2CharPosition) arg0);
				break;
			case AI_INTENTION_MOVE_TO_IN_A_BOAT:
				onIntentionMoveToInABoat((L2CharPosition) arg0, (L2CharPosition) arg1);
				break;
			case AI_INTENTION_FOLLOW:
				onIntentionFollow((L2Character) arg0);
				break;
			case AI_INTENTION_PICK_UP:
				onIntentionPickUp((L2Object) arg0);
				break;
			case AI_INTENTION_INTERACT:
				onIntentionInteract((L2Object) arg0);
				break;
		}
		_actor.fireEvent(EventType.SETINTENTION.name, new Object[]
		{
			intention
		});
	}

	/**
	 * @param evt The event whose the AI must be notified
	 */
	@Override
	public final void notifyEvent(CtrlEvent evt)
	{
		notifyEvent(evt, null, null);
	}

	/**
	 * @param evt The event whose the AI must be notified
	 * @param arg0 The first parameter of the Event (optional target)
	 */
	@Override
	public final void notifyEvent(CtrlEvent evt, Object arg0)
	{
		notifyEvent(evt, arg0, null);
	}

	/** 
	 * @param evt The event whose the AI must be notified
	 * @param arg0 The first parameter of the Event (optional target)
	 * @param arg1 The second parameter of the Event (optional target)
	 */
	@Override
	public final void notifyEvent(CtrlEvent evt, Object arg0, Object arg1)
	{
		if(!_actor.isVisible() || !_actor.hasAI() || (_actor instanceof L2PcInstance && ((L2PcInstance)_actor).isOffline()))
			return;
		
		switch(evt)
		{
			case EVT_THINK:
				onEvtThink();
				break;
			case EVT_ATTACKED:
				onEvtAttacked((L2Character) arg0);
				break;
			case EVT_AGGRESSION:
				onEvtAggression((L2Character) arg0, ((Number) arg1).intValue());
				break;
			case EVT_STUNNED:
				onEvtStunned((L2Character) arg0);
				break;
			case EVT_SLEEPING:
				onEvtSleeping((L2Character) arg0);
				break;
			case EVT_ROOTED:
				onEvtRooted((L2Character) arg0);
				break;
			case EVT_CONFUSED:
				onEvtConfused((L2Character) arg0);
				break;
			case EVT_MUTED:
				onEvtMuted((L2Character) arg0);
				break;
			case EVT_READY_TO_ACT:
				onEvtReadyToAct();
				break;
			case EVT_USER_CMD:
				onEvtUserCmd(arg0, arg1);
				break;
			case EVT_ARRIVED:
				onEvtArrived();
				break;
			case EVT_ARRIVED_REVALIDATE:
				onEvtArrivedRevalidate();
				break;
			case EVT_ARRIVED_BLOCKED:
				onEvtArrivedBlocked((L2CharPosition) arg0);
				break;
			case EVT_FORGET_OBJECT:
				onEvtForgetObject((L2Object) arg0);
				break;
			case EVT_CANCEL:
				onEvtCancel();
				break;
			case EVT_DEAD:
				onEvtDead();
				break;
			case EVT_FAKE_DEATH:
				onEvtFakeDeath();
				break;
			case EVT_FINISH_CASTING:
				onEvtFinishCasting();
				break;
		}
	}

	protected abstract void onIntentionIdle();

	protected abstract void onIntentionActive();

	protected abstract void onIntentionRest();

	protected abstract void onIntentionAttack(L2Character target);

	protected abstract void onIntentionCast(L2Skill skill, L2Object target);

	protected abstract void onIntentionMoveTo(L2CharPosition destination);

	protected abstract void onIntentionMoveToInABoat(L2CharPosition destination, L2CharPosition origin);

	protected abstract void onIntentionFollow(L2Character target);

	protected abstract void onIntentionPickUp(L2Object item);

	protected abstract void onIntentionInteract(L2Object object);

	protected abstract void onEvtThink();

	protected abstract void onEvtAttacked(L2Character attacker);

	protected abstract void onEvtAggression(L2Character target, int aggro);

	protected abstract void onEvtStunned(L2Character attacker);

	protected abstract void onEvtSleeping(L2Character attacker);

	protected abstract void onEvtRooted(L2Character attacker);

	protected abstract void onEvtConfused(L2Character attacker);

	protected abstract void onEvtMuted(L2Character attacker);

	protected abstract void onEvtReadyToAct();

	protected abstract void onEvtUserCmd(Object arg0, Object arg1);

	protected abstract void onEvtArrived();

	protected abstract void onEvtArrivedRevalidate();

	protected abstract void onEvtArrivedBlocked(L2CharPosition blocked_at_pos);

	protected abstract void onEvtForgetObject(L2Object object);

	protected abstract void onEvtCancel();

	protected abstract void onEvtDead();

	protected abstract void onEvtFakeDeath();

	protected abstract void onEvtFinishCasting();

	protected void clientActionFailed()
	{
		if(_actor instanceof L2PcInstance)
		{
			_actor.sendPacket(ActionFailed.STATIC_PACKET);
		}
	}

	/**
	 * @param pawn 
	 * @param offset 
	 */
	public void moveToPawn(L2Object pawn, int offset)
	{
		if(!_actor.isMovementDisabled())
		{
			if(offset < 10)
			{
				offset = 10;
			}

			boolean sendPacket = true;
			if(_clientMoving && getTarget() == pawn)
			{
				if(_clientMovingToPawnOffset == offset)
				{
					if(GameTimeController.getGameTicks() < _moveToPawnTimeout)
						return;

					sendPacket = false;
				}
				else if(_actor.isOnGeodataPath())
				{
					if(GameTimeController.getGameTicks() < _moveToPawnTimeout + 10)
						return;
				}
			}

			_clientMoving = true;
			_clientMovingToPawnOffset = offset;
			
			setTarget(pawn);
			
			_moveToPawnTimeout = GameTimeController.getGameTicks();
			_moveToPawnTimeout += /*1000*/200 / GameTimeController.MILLIS_IN_TICK;

			if(pawn == null || _accessor == null)
				return;

			_accessor.moveTo(pawn.getX(), pawn.getY(), pawn.getZ(), offset);

			if(!_actor.isMoving())
			{
				_actor.sendPacket(ActionFailed.STATIC_PACKET);
				return;
			}

			if(pawn instanceof L2Character)
			{
				if(_actor.isOnGeodataPath())
				{
					_actor.broadcastPacket(new CharMoveToLocation(_actor));
					_clientMovingToPawnOffset = 0;
				}
				else if(sendPacket)
				{
					_actor.broadcastPacket(new MoveToPawn(_actor, (L2Character) pawn, offset));
				}
			}
			else
			{
				_actor.broadcastPacket(new CharMoveToLocation(_actor));
			}
		}
		else
		{
			_actor.sendPacket(ActionFailed.STATIC_PACKET);
		}
	}

	/**
	 * @param x 
	 * @param y 
	 * @param z 
	 */
	public void moveTo(int x, int y, int z)
	{
		if(!_actor.isMovementDisabled())
		{
			_clientMoving = true;
			_clientMovingToPawnOffset = 0;

			_accessor.moveTo(x, y, z);

			CharMoveToLocation msg = new CharMoveToLocation(_actor);
			_actor.broadcastPacket(msg);
			msg = null;

		}
		else
		{
			_actor.sendPacket(ActionFailed.STATIC_PACKET);
		}
	}

	protected void moveToInABoat(L2CharPosition destination, L2CharPosition origin)
	{
		if(!_actor.isMovementDisabled())
		{
			if(((L2PcInstance) _actor).getBoat() != null)
			{
				MoveToLocationInVehicle msg = new MoveToLocationInVehicle(_actor, destination, origin);
				_actor.broadcastPacket(msg);
				msg = null;
			}

		}
		else
		{
			_actor.sendPacket(ActionFailed.STATIC_PACKET);
		}
	}

	/**
	 * @param pos 
	 */
	protected void clientStopMoving(L2CharPosition pos)
	{
		if(_actor.isMoving())
		{
			_accessor.stopMove(pos);
		}

		_clientMovingToPawnOffset = 0;

		if(_clientMoving || pos != null)
		{
			_clientMoving = false;

			StopMove msg = new StopMove(_actor);
			_actor.broadcastPacket(msg);
			msg = null;

			if(pos != null)
			{
				StopRotation sr = new StopRotation(_actor, pos.heading, 0);
				_actor.sendPacket(sr);
				_actor.broadcastPacket(sr);
				sr = null;
			}
		}
	}

	protected void clientStoppedMoving()
	{
		if(_clientMovingToPawnOffset > 0)
		{
			_clientMovingToPawnOffset = 0;
			StopMove msg = new StopMove(_actor);
			_actor.broadcastPacket(msg);
			msg = null;
		}
		_clientMoving = false;
	}

	public void clientStartAutoAttack()
	{
		if(((_actor instanceof L2NpcInstance
			&& !(_actor instanceof L2Attackable))
			&& !(_actor instanceof L2PlayableInstance))){
			return;
		}
		
		if (_actor instanceof L2Summon)
		{
			L2Summon summon = (L2Summon) _actor;
			if (summon.getOwner() != null)
				summon.getOwner().getAI().clientStartAutoAttack();
			return;
		}
		if (!isAutoAttacking())
		{
			if (_actor instanceof L2PcInstance && ((L2PcInstance)_actor).getPet() != null)
				((L2PcInstance)_actor).getPet().broadcastPacket(new AutoAttackStart(((L2PcInstance)_actor).getPet().getObjectId()));
			_actor.broadcastPacket(new AutoAttackStart(_actor.getObjectId()));
			setAutoAttacking(true);
		}
		AttackStanceTaskManager.getInstance().addAttackStanceTask(_actor);
	}

	public void clientStopAutoAttack()
	{
		if (_actor instanceof L2Summon)
		{
			L2Summon summon = (L2Summon) _actor;
			if (summon.getOwner() != null)
				summon.getOwner().getAI().clientStopAutoAttack();
			return;
		}
		
		final boolean isAutoAttacking = isAutoAttacking();
		
		if (_actor instanceof L2PcInstance)
		{
			if (!AttackStanceTaskManager.getInstance().getAttackStanceTask(_actor) && isAutoAttacking)
				AttackStanceTaskManager.getInstance().addAttackStanceTask(_actor);
		}
		else if (isAutoAttacking)
		{
			_actor.broadcastPacket(new AutoAttackStop(_actor.getObjectId()));
			setAutoAttacking(false);
		}
	}
	
	protected void clientNotifyDead()
	{
		Die msg = new Die(_actor);
		_actor.broadcastPacket(msg);
		msg = null;

		setIntention(AI_INTENTION_IDLE);
		setTarget(null);
		setAttackTarget(null);
		setCastTarget(null);

		stopFollow();
	}

	/**
	 * @param player The L2PcIstance to notify with state of this L2Character
	 */
	public void describeStateToPlayer(L2PcInstance player)
	{
		if(_clientMoving)
		{
			final L2Character follow = getFollowTarget();
			
			if(_clientMovingToPawnOffset != 0 &&  follow!= null)
			{
				MoveToPawn msg = new MoveToPawn(_actor, follow, _clientMovingToPawnOffset);
				player.sendPacket(msg);
				msg = null;
			}
			else
			{
				CharMoveToLocation msg = new CharMoveToLocation(_actor);
				player.sendPacket(msg);
				msg = null;
			}
		}
	}

	/**
	 * @param target The L2Character to follow
	 */
	public synchronized void startFollow(L2Character target)
	{
		if(_followTask != null)
		{
			_followTask.cancel(false);
			_followTask = null;
		}

		_followTarget = target;
		_followTask = ThreadPoolManager.getInstance().scheduleAiAtFixedRate(new FollowTask(), 5, FOLLOW_INTERVAL);
	}

	/**
	 * @param target The L2Character to follow
	 * @param range 
	 */
	public synchronized void startFollow(L2Character target, int range)
	{
		if(_followTask != null)
		{
			_followTask.cancel(false);
			_followTask = null;
		}

		_followTarget = target;
		_followTask = ThreadPoolManager.getInstance().scheduleAiAtFixedRate(new FollowTask(range), 5, ATTACK_FOLLOW_INTERVAL);
	}

	public synchronized void stopFollow()
	{
		if(_followTask != null)
		{
			_followTask.cancel(false);
			_followTask = null;
		}
		_followTarget = null;
	}

	protected synchronized L2Character getFollowTarget()
	{
		return _followTarget;
	}

	protected synchronized L2Object getTarget()
	{
		return _target;
	}

	protected synchronized void setTarget(L2Object target)
	{
		_target = target;
	}
	
	protected synchronized void setCastTarget(L2Character target)
	{
		_castTarget = target;
	}

	public synchronized L2Character getCastTarget()
	{
		return _castTarget;
	}

	protected synchronized void setAttackTarget(L2Character target)
	{
		_attackTarget = target;
	}

	@Override
	public synchronized L2Character getAttackTarget()
	{
		return _attackTarget;
	}

	public synchronized  boolean isAutoAttacking()
	{
		return _clientAutoAttacking;
	}

	public synchronized void setAutoAttacking(boolean isAutoAttacking)
	{
		_clientAutoAttacking = isAutoAttacking;
	}

	public synchronized Object get_intentionArg0()
	{
		return _intentionArg0;
	}

	public synchronized void set_intentionArg0(Object _intentionArg0)
	{
		this._intentionArg0 = _intentionArg0;
	}

	public synchronized Object get_intentionArg1()
	{
		return _intentionArg1;
	}

	public synchronized void set_intentionArg1(Object _intentionArg1)
	{
		this._intentionArg1 = _intentionArg1;
	}

	@Override
	public synchronized CtrlIntention getIntention()
	{
		return _intention;
	}
}
