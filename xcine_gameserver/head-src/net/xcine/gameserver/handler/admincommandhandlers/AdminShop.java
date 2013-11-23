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
package net.xcine.gameserver.handler.admincommandhandlers;

import java.util.logging.Logger;

import net.xcine.Config;
import net.xcine.gameserver.TradeController;
import net.xcine.gameserver.handler.IAdminCommandHandler;
import net.xcine.gameserver.model.L2TradeList;
import net.xcine.gameserver.model.actor.instance.L2PcInstance;
import net.xcine.gameserver.network.serverpackets.ActionFailed;
import net.xcine.gameserver.network.serverpackets.BuyList;

/**
 * This class handles following admin commands: - gmshop = shows menu - buy id = shows shop with respective id
 */
public class AdminShop implements IAdminCommandHandler
{
	private static Logger _log = Logger.getLogger(AdminShop.class.getName());
	
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_buy",
		"admin_gmshop"
	};
	
	@Override
	public boolean useAdminCommand(String command, L2PcInstance activeChar)
	{
		if (command.startsWith("admin_buy"))
		{
			try
			{
				handleBuyRequest(activeChar, command.substring(10));
			}
			catch (IndexOutOfBoundsException e)
			{
				activeChar.sendMessage("Please specify buylist.");
			}
		}
		else if (command.equals("admin_gmshop"))
			AdminHelpPage.showHelpPage(activeChar, "gmshops.htm");
		
		return true;
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
	
	private static void handleBuyRequest(L2PcInstance activeChar, String command)
	{
		int val = -1;
		try
		{
			val = Integer.parseInt(command);
		}
		catch (Exception e)
		{
			_log.warning("admin buylist failed:" + command);
		}
		
		L2TradeList list = TradeController.getInstance().getBuyList(val);
		
		if (list != null)
		{
			activeChar.sendPacket(new BuyList(list, activeChar.getAdena(), 0));
			if (Config.DEBUG)
				_log.fine("GM: " + activeChar.getName() + "(" + activeChar.getObjectId() + ") opened GM shop id " + val);
		}
		else
			_log.warning("no buylist with id:" + val);
		
		activeChar.sendPacket(ActionFailed.STATIC_PACKET);
	}
}