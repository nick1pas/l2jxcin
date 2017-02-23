package net.sf.l2j.gameserver.ai.model;

import net.sf.l2j.commons.concurrent.ThreadPool;

import net.sf.l2j.gameserver.ai.CtrlEvent;
import net.sf.l2j.gameserver.model.L2Object;
import net.sf.l2j.gameserver.model.L2Skill;
import net.sf.l2j.gameserver.model.Location;
import net.sf.l2j.gameserver.model.SpawnLocation;
import net.sf.l2j.gameserver.model.actor.Character;
import net.sf.l2j.gameserver.model.actor.instance.Door;
import net.sf.l2j.gameserver.model.actor.instance.SiegeGuard;

public class L2DoorAI extends L2CharacterAI
{
	public L2DoorAI(Door door)
	{
		super(door);
	}
	
	@Override
	protected void onIntentionIdle()
	{
	}
	
	@Override
	protected void onIntentionActive()
	{
	}
	
	@Override
	protected void onIntentionRest()
	{
	}
	
	@Override
	protected void onIntentionAttack(Character target)
	{
	}
	
	@Override
	protected void onIntentionCast(L2Skill skill, L2Object target)
	{
	}
	
	@Override
	protected void onIntentionMoveTo(Location loc)
	{
	}
	
	@Override
	protected void onIntentionFollow(Character target)
	{
	}
	
	@Override
	protected void onIntentionPickUp(L2Object item)
	{
	}
	
	@Override
	protected void onIntentionInteract(L2Object object)
	{
	}
	
	@Override
	protected void onEvtThink()
	{
	}
	
	@Override
	protected void onEvtAttacked(Character attacker)
	{
		ThreadPool.execute(new onEventAttackedDoorTask((Door) _actor, attacker));
	}
	
	@Override
	protected void onEvtAggression(Character target, int aggro)
	{
	}
	
	@Override
	protected void onEvtStunned(Character attacker)
	{
	}
	
	@Override
	protected void onEvtSleeping(Character attacker)
	{
	}
	
	@Override
	protected void onEvtRooted(Character attacker)
	{
	}
	
	@Override
	protected void onEvtReadyToAct()
	{
	}
	
	@Override
	protected void onEvtUserCmd(Object arg0, Object arg1)
	{
	}
	
	@Override
	protected void onEvtArrived()
	{
	}
	
	@Override
	protected void onEvtArrivedBlocked(SpawnLocation loc)
	{
	}
	
	@Override
	protected void onEvtCancel()
	{
	}
	
	@Override
	protected void onEvtDead()
	{
	}
	
	private class onEventAttackedDoorTask implements Runnable
	{
		private final Door _door;
		private final Character _attacker;
		
		public onEventAttackedDoorTask(Door door, Character attacker)
		{
			_door = door;
			_attacker = attacker;
		}
		
		@Override
		public void run()
		{
			for (SiegeGuard guard : _door.getKnownType(SiegeGuard.class))
			{
				if (_actor.isInsideRadius(guard, guard.getTemplate().getClanRange(), false, true) && Math.abs(_attacker.getZ() - guard.getZ()) < 200)
					guard.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, _attacker, 15);
			}
		}
	}
}