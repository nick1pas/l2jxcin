package net.sf.l2j.gameserver.model.actor.instance;

import net.sf.l2j.gameserver.ai.CtrlIntention;
import net.sf.l2j.gameserver.model.L2Skill;
import net.sf.l2j.gameserver.model.actor.Character;
import net.sf.l2j.gameserver.model.actor.Npc;
import net.sf.l2j.gameserver.model.actor.template.NpcTemplate;
import net.sf.l2j.gameserver.network.serverpackets.MoveToPawn;

public final class HolyThing extends Folk
{
	public HolyThing(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}
	
	@Override
	public void onAction(Player player)
	{
		// Set the target of the Player player
		if (player.getTarget() != this)
			player.setTarget(this);
		else
		{
			// Calculate the distance between the Player and the Npc
			if (!canInteract(player))
			{
				// Notify the Player AI with INTERACT
				player.getAI().setIntention(CtrlIntention.INTERACT, this);
			}
			else
			{
				// Rotate the player to face the instance
				player.sendPacket(new MoveToPawn(player, this, Npc.INTERACTION_DISTANCE));
				
				// Send ActionFailed to the player in order to avoid he stucks
				player.ActionF();
			}
		}
	}
	
	@Override
	public boolean isAttackable()
	{
		return false;
	}
	
	@Override
	public void onForcedAttack(Player player)
	{
		player.ActionF();
	}
	
	@Override
	public void reduceCurrentHp(double damage, Character attacker, L2Skill skill)
	{
	}
	
	@Override
	public void reduceCurrentHp(double damage, Character attacker, boolean awake, boolean isDOT, L2Skill skill)
	{
	}
}
