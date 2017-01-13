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
package net.sf.l2j.gameserver.handler.admincommandhandlers;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.handler.IAdminCommandHandler;
import net.sf.l2j.gameserver.instancemanager.AioManager;
import net.sf.l2j.gameserver.model.L2Object;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.network.serverpackets.ExShowScreenMessage;

/**
 * @author rapfersan92
 */
public class AdminAio implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_add_aio",
		"admin_update_aio",
		"admin_remove_aio"
	};
	
	@Override
	public boolean useAdminCommand(String command, L2PcInstance activeChar)
	{
		final L2Object target = activeChar.getTarget();
		if (target == null || !(target instanceof L2PcInstance))
		{
			activeChar.sendPacket(SystemMessageId.INCORRECT_TARGET);
			return false;
		}
		
		if (command.startsWith("admin_add_aio"))
		{
			try
			{
				int duration = Integer.parseInt(command.substring(14));
				addAio(activeChar, (L2PcInstance) target, duration);
			}
			catch (Exception e)
			{
				activeChar.sendMessage("You must use //add_aio <days>");
			}
		}
		else if (command.startsWith("admin_update_aio"))
		{
			try
			{
				int duration = Integer.parseInt(command.substring(17));
				updateAio(activeChar, (L2PcInstance) target, duration);
			}
			catch (Exception e)
			{
				activeChar.sendMessage("You must use //update_aio <days>");
			}
		}
		else if (command.equalsIgnoreCase("admin_remove_aio"))
			removeAio(activeChar, (L2PcInstance) target);
		
		return true;
	}
	
	private static void addAio(L2PcInstance activeChar, L2PcInstance targetChar, int duration)
	{
		if (duration <= 0)
		{
			activeChar.sendMessage("The value you have entered is incorrect.");
			return;
		}
		else if (AioManager.getInstance().hasAioPrivileges(targetChar.getObjectId()))
		{
			activeChar.sendMessage("Your target already have aio privileges.");
			return;
		}
		else if (Config.LIST_AIO_RESTRICTED_CLASSES.contains(targetChar.getTemplate().getClassId().getId()))
		{
			activeChar.sendMessage("Your target cannot receive aio privileges with a character in their current class.");
			return;
		}
		else if (targetChar.isSubClassActive())
		{
			activeChar.sendMessage("Your target cannot receive aio privileges with a character in their subclass.");
			return;
		}
		
		AioManager.getInstance().addAio(targetChar.getObjectId(), System.currentTimeMillis() + duration * 86400000);
		activeChar.sendMessage("You have added the aio privileges to " + targetChar.getName() + ".");
		targetChar.sendPacket(new ExShowScreenMessage("Your aio privileges were added by the admin.", 10000));
	}
	
	private static void updateAio(L2PcInstance activeChar, L2PcInstance targetChar, int duration)
	{
		if (duration <= 0)
		{
			activeChar.sendMessage("The value you have entered is incorrect.");
			return;
		}
		else if (!AioManager.getInstance().hasAioPrivileges(targetChar.getObjectId()))
		{
			activeChar.sendMessage("Your target does not have aio privileges.");
			return;
		}
		
		AioManager.getInstance().updateAio(targetChar.getObjectId(), duration * 86400000);
		activeChar.sendMessage("You have updated aio privileges from " + targetChar.getName() + ".");
		targetChar.sendPacket(new ExShowScreenMessage("Your aio privileges were updated by the admin.", 10000));
	}
	
	private static void removeAio(L2PcInstance activeChar, L2PcInstance targetChar)
	{
		if (!AioManager.getInstance().hasAioPrivileges(targetChar.getObjectId()))
		{
			activeChar.sendMessage("Your target does not have aio privileges.");
			return;
		}
		
		AioManager.getInstance().removeAio(targetChar.getObjectId());
		activeChar.sendMessage("You have removed aio privileges from " + targetChar.getName() + ".");
		targetChar.sendPacket(new ExShowScreenMessage("Your aio privileges were removed by the admin.", 10000));
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}