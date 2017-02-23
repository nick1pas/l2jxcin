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

import net.sf.l2j.gameserver.handler.IAdminCommandHandler;
import net.sf.l2j.gameserver.model.actor.instance.Player;
import net.sf.l2j.protection.CatsGuard;

/**
 * Developers: Silentium Team<br>
 * Official Website: http://silentium.by/<br>
 * <br>
 * Author: SoFace<br>
 * <br>
 */

public class AdminHwidBan implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_hwid",
		"admin_hwidban",
		"admin_hwidunban"
	};
	
	@Override
	public boolean useAdminCommand(String command, Player activeChar)
	{
		if (command.equals("admin_hwid"))
		{
			AdminHelpPage.showHelpPage(activeChar, "hwid-ban/hwid.htm");
		}
		else if (command.equals("admin_hwidban"))
		{
			String hwid = ((Player) activeChar.getTarget()).getHWid();
			if (hwid != null)
			{
				CatsGuard.getInstance().ban(hwid);
				activeChar.sendMessage("The player with HWID'om:" + hwid + "was banned.");
			}
			else
			{
				activeChar.sendMessage("This HWID'a does not exist.");
			}
		}
		else if (command.equals("admin_hwidunban"))
		{
			String hwid = ((Player) activeChar.getTarget()).getHWid();
			if (hwid != null)
			{
				CatsGuard.getInstance().unban(hwid);
				activeChar.sendMessage("The player with HWID'om:" + hwid + "was unban.");
			}
			else
			{
				activeChar.sendMessage("This HWID'a does not exist.");
			}
		}
		return true;
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}