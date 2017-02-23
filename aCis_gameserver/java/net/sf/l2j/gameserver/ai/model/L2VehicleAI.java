package net.sf.l2j.gameserver.ai.model;

import net.sf.l2j.gameserver.model.L2Object;
import net.sf.l2j.gameserver.model.L2Skill;
import net.sf.l2j.gameserver.model.SpawnLocation;
import net.sf.l2j.gameserver.model.actor.Character;
import net.sf.l2j.gameserver.model.actor.Vehicle;
import net.sf.l2j.gameserver.model.actor.instance.Player;
import net.sf.l2j.gameserver.network.serverpackets.VehicleDeparture;
import net.sf.l2j.gameserver.network.serverpackets.VehicleInfo;
import net.sf.l2j.gameserver.network.serverpackets.VehicleStarted;

public class L2VehicleAI extends L2CharacterAI
{
	public L2VehicleAI(Vehicle boat)
	{
		super(boat);
	}
	
	@Override
	protected void moveTo(int x, int y, int z)
	{
		if (!_actor.isMovementDisabled())
		{
			if (!_clientMoving)
				_actor.broadcastPacket(new VehicleStarted(getActor(), 1));
			
			_clientMoving = true;
			_actor.moveToLocation(x, y, z, 0);
			_actor.broadcastPacket(new VehicleDeparture(getActor()));
		}
	}
	
	@Override
	protected void clientStopMoving(SpawnLocation loc)
	{
		if (_actor.isMoving())
			_actor.stopMove(loc);
		
		if (_clientMoving || loc != null)
		{
			_clientMoving = false;
			_actor.broadcastPacket(new VehicleStarted(getActor(), 0));
			_actor.broadcastPacket(new VehicleInfo(getActor()));
		}
	}
	
	@Override
	public void describeStateToPlayer(Player player)
	{
		if (_clientMoving)
			player.sendPacket(new VehicleDeparture(getActor()));
	}
	
	@Override
	public Vehicle getActor()
	{
		return (Vehicle) _actor;
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
	protected void onEvtAttacked(Character attacker)
	{
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
	protected void onEvtCancel()
	{
	}
	
	@Override
	protected void onEvtDead()
	{
	}
	
	@Override
	protected void onEvtFakeDeath()
	{
	}
	
	@Override
	protected void onEvtFinishCasting()
	{
	}
	
	@Override
	protected void clientActionFailed()
	{
	}
	
	@Override
	protected void moveToPawn(L2Object pawn, int offset)
	{
	}
	
	@Override
	protected void clientStoppedMoving()
	{
	}
}