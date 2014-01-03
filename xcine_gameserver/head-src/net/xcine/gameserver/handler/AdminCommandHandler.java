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
package net.xcine.gameserver.handler;

import gnu.trove.map.hash.TIntObjectHashMap;

import java.util.logging.Logger;

import net.xcine.Config;
import net.xcine.gameserver.handler.admincommandhandlers.AdminAdmin;
import net.xcine.gameserver.handler.admincommandhandlers.AdminAnnouncements;
import net.xcine.gameserver.handler.admincommandhandlers.AdminBan;
import net.xcine.gameserver.handler.admincommandhandlers.AdminBookmark;
import net.xcine.gameserver.handler.admincommandhandlers.AdminBuffs;
import net.xcine.gameserver.handler.admincommandhandlers.AdminCache;
import net.xcine.gameserver.handler.admincommandhandlers.AdminCamera;
import net.xcine.gameserver.handler.admincommandhandlers.AdminChangeAccessLevel;
import net.xcine.gameserver.handler.admincommandhandlers.AdminCreateItem;
import net.xcine.gameserver.handler.admincommandhandlers.AdminCursedWeapons;
import net.xcine.gameserver.handler.admincommandhandlers.AdminDelete;
import net.xcine.gameserver.handler.admincommandhandlers.AdminDoorControl;
import net.xcine.gameserver.handler.admincommandhandlers.AdminEditChar;
import net.xcine.gameserver.handler.admincommandhandlers.AdminEditNpc;
import net.xcine.gameserver.handler.admincommandhandlers.AdminEffects;
import net.xcine.gameserver.handler.admincommandhandlers.AdminEnchant;
import net.xcine.gameserver.handler.admincommandhandlers.AdminEvents;
import net.xcine.gameserver.handler.admincommandhandlers.AdminExpSp;
import net.xcine.gameserver.handler.admincommandhandlers.AdminGeodata;
import net.xcine.gameserver.handler.admincommandhandlers.AdminGm;
import net.xcine.gameserver.handler.admincommandhandlers.AdminGmChat;
import net.xcine.gameserver.handler.admincommandhandlers.AdminHeal;
import net.xcine.gameserver.handler.admincommandhandlers.AdminHelpPage;
import net.xcine.gameserver.handler.admincommandhandlers.AdminInvul;
import net.xcine.gameserver.handler.admincommandhandlers.AdminKick;
import net.xcine.gameserver.handler.admincommandhandlers.AdminKill;
import net.xcine.gameserver.handler.admincommandhandlers.AdminLevel;
import net.xcine.gameserver.handler.admincommandhandlers.AdminMaintenance;
import net.xcine.gameserver.handler.admincommandhandlers.AdminMammon;
import net.xcine.gameserver.handler.admincommandhandlers.AdminManor;
import net.xcine.gameserver.handler.admincommandhandlers.AdminMenu;
import net.xcine.gameserver.handler.admincommandhandlers.AdminMonsterRace;
import net.xcine.gameserver.handler.admincommandhandlers.AdminMovieMaker;
import net.xcine.gameserver.handler.admincommandhandlers.AdminOlympiad;
import net.xcine.gameserver.handler.admincommandhandlers.AdminPForge;
import net.xcine.gameserver.handler.admincommandhandlers.AdminPathNode;
import net.xcine.gameserver.handler.admincommandhandlers.AdminPetition;
import net.xcine.gameserver.handler.admincommandhandlers.AdminPledge;
import net.xcine.gameserver.handler.admincommandhandlers.AdminPolymorph;
import net.xcine.gameserver.handler.admincommandhandlers.AdminRepairChar;
import net.xcine.gameserver.handler.admincommandhandlers.AdminRes;
import net.xcine.gameserver.handler.admincommandhandlers.AdminRideWyvern;
import net.xcine.gameserver.handler.admincommandhandlers.AdminShop;
import net.xcine.gameserver.handler.admincommandhandlers.AdminSiege;
import net.xcine.gameserver.handler.admincommandhandlers.AdminSkill;
import net.xcine.gameserver.handler.admincommandhandlers.AdminSpawn;
import net.xcine.gameserver.handler.admincommandhandlers.AdminTarget;
import net.xcine.gameserver.handler.admincommandhandlers.AdminTeleport;
import net.xcine.gameserver.handler.admincommandhandlers.AdminUnblockIp;
import net.xcine.gameserver.handler.admincommandhandlers.AdminZone;
import net.xcine.gameserver.handler.admincommandhandlers.AdminAio;

public class AdminCommandHandler
{
	private static Logger _log = Logger.getLogger(AdminCommandHandler.class.getName());
	private final TIntObjectHashMap<IAdminCommandHandler> _datatable;
	
	public static AdminCommandHandler getInstance()
	{
		return SingletonHolder._instance;
	}
	
	protected AdminCommandHandler()
	{
		_datatable = new TIntObjectHashMap<>();
		registerAdminCommandHandler(new AdminAdmin());
		registerAdminCommandHandler(new AdminAnnouncements());
		registerAdminCommandHandler(new AdminBan());
		registerAdminCommandHandler(new AdminBookmark());
		registerAdminCommandHandler(new AdminBuffs());
		registerAdminCommandHandler(new AdminCache());
		registerAdminCommandHandler(new AdminCamera());
		registerAdminCommandHandler(new AdminChangeAccessLevel());
		registerAdminCommandHandler(new AdminCreateItem());
		registerAdminCommandHandler(new AdminCursedWeapons());
		registerAdminCommandHandler(new AdminDelete());
		registerAdminCommandHandler(new AdminDoorControl());
		registerAdminCommandHandler(new AdminEditChar());
		registerAdminCommandHandler(new AdminEditNpc());
		registerAdminCommandHandler(new AdminEffects());
		registerAdminCommandHandler(new AdminEnchant());
		registerAdminCommandHandler(new AdminExpSp());
		registerAdminCommandHandler(new AdminGeodata());
		registerAdminCommandHandler(new AdminGm());
		registerAdminCommandHandler(new AdminGmChat());
		registerAdminCommandHandler(new AdminHeal());
		registerAdminCommandHandler(new AdminHelpPage());
		registerAdminCommandHandler(new AdminInvul());
		registerAdminCommandHandler(new AdminKick());
		registerAdminCommandHandler(new AdminKill());
		registerAdminCommandHandler(new AdminLevel());
		registerAdminCommandHandler(new AdminMaintenance());
		registerAdminCommandHandler(new AdminMammon());
		registerAdminCommandHandler(new AdminManor());
		registerAdminCommandHandler(new AdminMenu());
		registerAdminCommandHandler(new AdminMonsterRace());
		registerAdminCommandHandler(new AdminMovieMaker());
		registerAdminCommandHandler(new AdminOlympiad());
		registerAdminCommandHandler(new AdminPathNode());
		registerAdminCommandHandler(new AdminPetition());
		registerAdminCommandHandler(new AdminPForge());
		registerAdminCommandHandler(new AdminPledge());
		registerAdminCommandHandler(new AdminPolymorph());
		registerAdminCommandHandler(new AdminRepairChar());
		registerAdminCommandHandler(new AdminRes());
		registerAdminCommandHandler(new AdminRideWyvern());
		registerAdminCommandHandler(new AdminShop());
		registerAdminCommandHandler(new AdminSiege());
		registerAdminCommandHandler(new AdminSkill());
		registerAdminCommandHandler(new AdminSpawn());
		registerAdminCommandHandler(new AdminTarget());
		registerAdminCommandHandler(new AdminTeleport());
		registerAdminCommandHandler(new AdminUnblockIp());
		registerAdminCommandHandler(new AdminZone());
		registerAdminCommandHandler(new AdminAio());
		registerAdminCommandHandler(new AdminEvents());
	}
	
	public void registerAdminCommandHandler(IAdminCommandHandler handler)
	{
		String[] ids = handler.getAdminCommandList();
		for (String id : ids)
		{
			if (Config.DEBUG)
				_log.fine("Adding handler for command " + id);
			_datatable.put(id.hashCode(), handler);
		}
	}
	
	public IAdminCommandHandler getAdminCommandHandler(String adminCommand)
	{
		String command = adminCommand;
		
		if (adminCommand.indexOf(" ") != -1)
			command = adminCommand.substring(0, adminCommand.indexOf(" "));
		
		if (Config.DEBUG)
			_log.fine("getting handler for command: " + command + " -> " + (_datatable.get(command.hashCode()) != null));
		return _datatable.get(command.hashCode());
	}
	
	/**
	 * @return
	 */
	public int size()
	{
		return _datatable.size();
	}
	
	private static class SingletonHolder
	{
		protected static final AdminCommandHandler _instance = new AdminCommandHandler();
	}
}