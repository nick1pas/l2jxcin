package net.sf.l2j.gameserver.model.actor.instance;

import net.sf.l2j.gameserver.ai.CtrlIntention;
import net.sf.l2j.gameserver.ai.model.L2CharacterAI;
import net.sf.l2j.gameserver.ai.model.L2SiegeGuardAI;
import net.sf.l2j.gameserver.model.actor.Attackable;
import net.sf.l2j.gameserver.model.actor.Character;
import net.sf.l2j.gameserver.model.actor.Npc;
import net.sf.l2j.gameserver.model.actor.template.NpcTemplate;
import net.sf.l2j.gameserver.model.entity.Siege.SiegeSide;
import net.sf.l2j.gameserver.network.serverpackets.MoveToPawn;

/**
 * This class represents all guards in the world.
 */
public final class SiegeGuard extends Attackable
{
	public SiegeGuard(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}
	
	@Override
	public L2CharacterAI getAI()
	{
		L2CharacterAI ai = _ai;
		if (ai == null)
		{
			synchronized (this)
			{
				if (_ai == null)
					_ai = new L2SiegeGuardAI(this);
				
				return _ai;
			}
		}
		return ai;
	}
	
	/**
	 * Return True if a siege is in progress and the Character attacker isn't a Defender.
	 * @param attacker The Character that the SiegeGuard try to attack
	 */
	@Override
	public boolean isAutoAttackable(Character attacker)
	{
		// Attackable during siege by all except defenders
		return (attacker != null && attacker.getActingPlayer() != null && getCastle() != null && getCastle().getSiege().isInProgress() && !getCastle().getSiege().checkSides(attacker.getActingPlayer().getClan(), SiegeSide.DEFENDER, SiegeSide.OWNER));
	}
	
	@Override
	public boolean hasRandomAnimation()
	{
		return false;
	}
	
	/**
	 * Note that super() is not called because guards need extra check to see if a player should interact or ATTACK them when clicked.
	 */
	@Override
	public void onAction(Player player)
	{
		// Set the target of the Player player
		if (player.getTarget() != this)
			player.setTarget(this);
		else
		{
			if (isAutoAttackable(player))
			{
				if (!isAlikeDead() && (Math.abs(player.getZ() - getZ()) < 600)) // this max heigth difference might need some tweaking
					player.getAI().setIntention(CtrlIntention.ATTACK, this);
			}
			else
			{
				// Notify the Player AI with INTERACT
				if (!canInteract(player))
					player.getAI().setIntention(CtrlIntention.INTERACT, this);
				else
				{
					// Rotate the player to face the instance
					player.sendPacket(new MoveToPawn(player, this, Npc.INTERACTION_DISTANCE));
					
					// Send ActionFailed to the player in order to avoid he stucks
					player.ActionF();
				}
			}
		}
	}
	
	@Override
	public void addDamageHate(Character attacker, int damage, int aggro)
	{
		if (attacker == null)
			return;
		
		if (!(attacker instanceof SiegeGuard))
			super.addDamageHate(attacker, damage, aggro);
	}
	
	@Override
	public boolean isGuard()
	{
		return true;
	}
	
	@Override
	public int getDriftRange()
	{
		return 20;
	}
}