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
package net.xcine.gameserver.model.actor.instance;

import net.xcine.Config;
import net.xcine.gameserver.ai.CtrlIntention;
import net.xcine.gameserver.ai.L2CharacterAI;
import net.xcine.gameserver.ai.L2SiegeGuardAI;
import net.xcine.gameserver.model.L2CharPosition;
import net.xcine.gameserver.model.actor.L2Attackable;
import net.xcine.gameserver.model.actor.L2Character;
import net.xcine.gameserver.model.actor.L2Npc;
import net.xcine.gameserver.model.actor.knownlist.SiegeGuardKnownList;
import net.xcine.gameserver.model.entity.DevastatedCastle;
import net.xcine.gameserver.network.serverpackets.ActionFailed;
import net.xcine.gameserver.network.serverpackets.MoveToPawn;
import net.xcine.gameserver.network.serverpackets.MyTargetSelected;
import net.xcine.gameserver.network.serverpackets.StatusUpdate;
import net.xcine.gameserver.templates.chars.L2NpcTemplate;

/**
 * This class represents all guards in the world.
 */
public final class L2SiegeGuardInstance extends L2Attackable
{
	public L2SiegeGuardInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
	}
	
	@Override
	public void initKnownList()
	{
		setKnownList(new SiegeGuardKnownList(this));
	}
	
	@Override
	public final SiegeGuardKnownList getKnownList()
	{
		return (SiegeGuardKnownList) super.getKnownList();
	}
	
	@Override
	public L2CharacterAI getAI()
	{
		L2CharacterAI ai = _ai; // copy handle
		if (ai == null)
		{
			synchronized (this)
			{
				if (_ai == null)
					_ai = new L2SiegeGuardAI(new AIAccessor());
				
				return _ai;
			}
		}
		return ai;
	}
	
	/**
	 * Return True if a siege is in progress and the L2Character attacker isn't a Defender.
	 * @param attacker The L2Character that the L2SiegeGuardInstance try to attack
	 */
	@Override
	public boolean isAutoAttackable(L2Character attacker)
	{
		// Attackable during siege by all except defenders
		return (attacker != null && attacker.getActingPlayer() != null && getCastle() != null && getCastle().getCastleId() > 0 && getCastle().getSiege().getIsInProgress() && !getCastle().getSiege().checkIsDefender(attacker.getActingPlayer().getClan()) || DevastatedCastle.getInstance().getIsInProgress());
	}
	
	@Override
	public boolean hasRandomAnimation()
	{
		return false;
	}
	
	/**
	 * This method forces guard to return to home location previously set
	 */
	@Override
	public void returnHome()
	{
		if (getWalkSpeed() <= 0)
			return;
		
		if (!isInsideRadius(getSpawn().getLocx(), getSpawn().getLocy(), 40, false))
		{
			if (Config.DEBUG)
				_log.fine(getObjectId() + ": moving home");
			
			setIsReturningToSpawnPoint(true);
			clearAggroList();
			
			if (hasAI())
				getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, new L2CharPosition(getSpawn().getLocx(), getSpawn().getLocy(), getSpawn().getLocz(), 0));
		}
	}
	
	/**
	 * Note that super() is not called because guards need extra check to see if a player should interact or ATTACK them when clicked.
	 */
	@Override
	public void onAction(L2PcInstance player)
	{
		// Check if the L2PcInstance already target the L2Npc
		if (player.getTarget() != this)
		{
			// Set the target of the L2PcInstance player
			player.setTarget(this);
			
			// Send MyTargetSelected to the L2PcInstance player
			player.sendPacket(new MyTargetSelected(getObjectId(), player.getLevel() - getLevel()));
			
			// Send StatusUpdate of the L2Npc to the L2PcInstance to update its HP bar
			StatusUpdate su = new StatusUpdate(this);
			su.addAttribute(StatusUpdate.CUR_HP, (int) getStatus().getCurrentHp());
			su.addAttribute(StatusUpdate.MAX_HP, getMaxHp());
			player.sendPacket(su);
		}
		else
		{
			if (isAutoAttackable(player))
			{
				if (!isAlikeDead() && (Math.abs(player.getZ() - getZ()) < 600)) // this max heigth difference might need some tweaking
					player.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, this);
			}
			else
			{
				// Notify the L2PcInstance AI with AI_INTENTION_INTERACT
				if (!canInteract(player))
					player.getAI().setIntention(CtrlIntention.AI_INTENTION_INTERACT, this);
				else
				{
					// Rotate the player to face the instance
					player.sendPacket(new MoveToPawn(player, this, L2Npc.INTERACTION_DISTANCE));
					
					// Send ActionFailed to the player in order to avoid he stucks
					player.sendPacket(ActionFailed.STATIC_PACKET);
				}
			}
		}
	}
	
	@Override
	public void addDamageHate(L2Character attacker, int damage, int aggro)
	{
		if (attacker == null)
			return;
		
		if (!(attacker instanceof L2SiegeGuardInstance))
			super.addDamageHate(attacker, damage, aggro);
	}
	
	@Override
	public boolean isGuard()
	{
		return true;
	}
}