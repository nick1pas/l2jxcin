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

package net.xcine.gameserver.handler.usercommandhandlers;

import net.xcine.gameserver.datatables.SkillTable;
import net.xcine.gameserver.geo.GeoData;
import net.xcine.gameserver.handler.IUserCommandHandler;
import net.xcine.gameserver.model.Inventory;
import net.xcine.gameserver.model.L2Summon;
import net.xcine.gameserver.model.actor.instance.L2PcInstance;
import net.xcine.gameserver.network.SystemMessageId;
import net.xcine.gameserver.network.serverpackets.Ride;
import net.xcine.gameserver.network.serverpackets.SystemMessage;
import net.xcine.gameserver.util.Broadcast;

/**
 * Support for /mount command.
 * 
 * @author Tempy
 */
public class Mount implements IUserCommandHandler
{
	private static final int[] COMMAND_IDS =
	{
		61
	};

	@Override
	public synchronized boolean useUserCommand(int id, L2PcInstance activeChar)
	{
		if(id != COMMAND_IDS[0])
			return false;

		L2Summon pet = activeChar.getPet();

		if(pet != null && pet.isMountable() && !activeChar.isMounted())
		{
			if(activeChar.isDead())
			{
				SystemMessage msg = new SystemMessage(SystemMessageId.STRIDER_CANT_BE_RIDDEN_WHILE_DEAD);
				activeChar.sendPacket(msg);
				msg = null;
			}
			else if(pet.isDead())
			{
				SystemMessage msg = new SystemMessage(SystemMessageId.DEAD_STRIDER_CANT_BE_RIDDEN);
				activeChar.sendPacket(msg);
				msg = null;
			}
			else if(pet.isInCombat())
			{
				SystemMessage msg = new SystemMessage(SystemMessageId.STRIDER_IN_BATLLE_CANT_BE_RIDDEN);
				activeChar.sendPacket(msg);
				msg = null;
			}
			else if(activeChar.isInCombat())
			{
				SystemMessage msg = new SystemMessage(SystemMessageId.STRIDER_CANT_BE_RIDDEN_WHILE_IN_BATTLE);
				activeChar.sendPacket(msg);
				msg = null;
			}
			else if(!activeChar.isInsideRadius(pet, 60, true, false))
			{
				activeChar.sendMessage("Too far away from strider to mount.");
				return false;
			}
			else if(!GeoData.getInstance().canSeeTarget(activeChar, pet))
			{
				SystemMessage msg = new SystemMessage(SystemMessageId.CANT_SEE_TARGET);
				activeChar.sendPacket(msg);
				return false;
			}
			else if(activeChar.isSitting() || activeChar.isMoving())
			{
				SystemMessage msg = new SystemMessage(SystemMessageId.STRIDER_CAN_BE_RIDDEN_ONLY_WHILE_STANDING);
				activeChar.sendPacket(msg);
				msg = null;
			}
			else if(!pet.isDead() && !activeChar.isMounted())
			{
				if(!activeChar.disarmWeapons())
					return false;

				Ride mount = new Ride(activeChar.getObjectId(), Ride.ACTION_MOUNT, pet.getTemplate().npcId);
				Broadcast.toSelfAndKnownPlayersInRadius(activeChar, mount, 810000/*900*/);
				activeChar.setMountType(mount.getMountType());
				activeChar.setMountObjectID(pet.getControlItemId());
				pet.unSummon(activeChar);
				mount = null;

				if(activeChar.getInventory().getPaperdollItem(Inventory.PAPERDOLL_RHAND) != null || activeChar.getInventory().getPaperdollItem(Inventory.PAPERDOLL_LRHAND) != null)
				{
					if(activeChar.setMountType(0))
					{
						if(activeChar.isFlying())
						{
							activeChar.removeSkill(SkillTable.getInstance().getInfo(4289, 1));
						}

						Ride dismount = new Ride(activeChar.getObjectId(), Ride.ACTION_DISMOUNT, 0);
						Broadcast.toSelfAndKnownPlayers(activeChar, dismount);
						activeChar.setMountObjectID(0);
						dismount = null;
					}
				}
			}
		}
		else if(activeChar.isRentedPet())
		{
			activeChar.stopRentPet();
		}
		else if(activeChar.isMounted())
		{
			if(activeChar.setMountType(0))
			{
				if(activeChar.isFlying())
				{
					activeChar.removeSkill(SkillTable.getInstance().getInfo(4289, 1));
				}

				Ride dismount = new Ride(activeChar.getObjectId(), Ride.ACTION_DISMOUNT, 0);
				Broadcast.toSelfAndKnownPlayers(activeChar, dismount);
				activeChar.setMountObjectID(0);
				dismount = null;
			}
		}

		pet = null;
		return true;
	}

	@Override
	public int[] getUserCommandList()
	{
		return COMMAND_IDS;
	}
}
