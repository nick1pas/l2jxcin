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
package net.xcine.gameserver.handler.admincommandhandlers;

import net.xcine.gameserver.handler.IAdminCommandHandler;
import net.xcine.gameserver.managers.ChristmasManager;
import net.xcine.gameserver.model.actor.instance.L2PcInstance;

/**
 * @version $Revision: 1.2.4.4 $ $Date: 2007/07/31 10:06:02 $
 */
public class AdminChristmas implements IAdminCommandHandler
{
	//private final static Logger _log = LogFactory.getLog(AdminChristmas.class.getName());

	private static final String[] ADMIN_COMMANDS =
	{
			"admin_christmas_start", "admin_christmas_end"
	};

	@Override
	public boolean useAdminCommand(String command, L2PcInstance activeChar)
	{
		/*
		if(!AdminCommandAccessRights.getInstance().hasAccess(command, activeChar.getAccessLevel())){
			return false;
		}
		
		if(Config.GMAUDIT)
		{
			Logger _logAudit = Logger.getLogger("gmaudit");
			LogRecord record = new LogRecord(Level.INFO, command);
			record.setParameters(new Object[]
			{
					"GM: " + activeChar.getName(), " to target [" + activeChar.getTarget() + "] "
			});
			_logAudit.log(record);
		}
		*/


		if(command.equals("admin_christmas_start"))
		{
			startChristmas(activeChar);
		}

		else if(command.equals("admin_christmas_end"))
		{
			endChristmas(activeChar);
		}

		return true;
	}

	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}

	private void startChristmas(L2PcInstance activeChar)
	{
		ChristmasManager.getInstance().init(activeChar);
	}

	private void endChristmas(L2PcInstance activeChar)
	{
		ChristmasManager.getInstance().end(activeChar);
	}
}
