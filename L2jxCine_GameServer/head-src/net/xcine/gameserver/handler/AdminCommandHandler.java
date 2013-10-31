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
package net.xcine.gameserver.handler;

import java.util.logging.Level;
import java.util.logging.Logger;

import javolution.util.FastMap;
import net.xcine.Config;
import net.xcine.gameserver.handler.admincommandhandlers.AdminAdmin;
import net.xcine.gameserver.handler.admincommandhandlers.AdminAio;
import net.xcine.gameserver.handler.admincommandhandlers.AdminAnnouncements;
import net.xcine.gameserver.handler.admincommandhandlers.AdminBBS;
import net.xcine.gameserver.handler.admincommandhandlers.AdminBan;
import net.xcine.gameserver.handler.admincommandhandlers.AdminBuffs;
import net.xcine.gameserver.handler.admincommandhandlers.AdminCache;
import net.xcine.gameserver.handler.admincommandhandlers.AdminChangeAccessLevel;
import net.xcine.gameserver.handler.admincommandhandlers.AdminCharSupervision;
import net.xcine.gameserver.handler.admincommandhandlers.AdminChristmas;
import net.xcine.gameserver.handler.admincommandhandlers.AdminCreateItem;
import net.xcine.gameserver.handler.admincommandhandlers.AdminCursedWeapons;
import net.xcine.gameserver.handler.admincommandhandlers.AdminDelete;
import net.xcine.gameserver.handler.admincommandhandlers.AdminDonator;
import net.xcine.gameserver.handler.admincommandhandlers.AdminDoorControl;
import net.xcine.gameserver.handler.admincommandhandlers.AdminEditChar;
import net.xcine.gameserver.handler.admincommandhandlers.AdminEditNpc;
import net.xcine.gameserver.handler.admincommandhandlers.AdminEffects;
import net.xcine.gameserver.handler.admincommandhandlers.AdminEnchant;
import net.xcine.gameserver.handler.admincommandhandlers.AdminEventEngine;
import net.xcine.gameserver.handler.admincommandhandlers.AdminExpSp;
import net.xcine.gameserver.handler.admincommandhandlers.AdminFightCalculator;
import net.xcine.gameserver.handler.admincommandhandlers.AdminGeodata;
import net.xcine.gameserver.handler.admincommandhandlers.AdminGm;
import net.xcine.gameserver.handler.admincommandhandlers.AdminGmChat;
import net.xcine.gameserver.handler.admincommandhandlers.AdminHeal;
import net.xcine.gameserver.handler.admincommandhandlers.AdminHelpPage;
import net.xcine.gameserver.handler.admincommandhandlers.AdminInvul;
import net.xcine.gameserver.handler.admincommandhandlers.AdminKick;
import net.xcine.gameserver.handler.admincommandhandlers.AdminKill;
import net.xcine.gameserver.handler.admincommandhandlers.AdminLevel;
import net.xcine.gameserver.handler.admincommandhandlers.AdminLogin;
import net.xcine.gameserver.handler.admincommandhandlers.AdminMammon;
import net.xcine.gameserver.handler.admincommandhandlers.AdminManor;
import net.xcine.gameserver.handler.admincommandhandlers.AdminMassControl;
import net.xcine.gameserver.handler.admincommandhandlers.AdminMassRecall;
import net.xcine.gameserver.handler.admincommandhandlers.AdminMenu;
import net.xcine.gameserver.handler.admincommandhandlers.AdminMobGroup;
import net.xcine.gameserver.handler.admincommandhandlers.AdminMonsterRace;
import net.xcine.gameserver.handler.admincommandhandlers.AdminNoble;
import net.xcine.gameserver.handler.admincommandhandlers.AdminPForge;
import net.xcine.gameserver.handler.admincommandhandlers.AdminPetition;
import net.xcine.gameserver.handler.admincommandhandlers.AdminPledge;
import net.xcine.gameserver.handler.admincommandhandlers.AdminPolymorph;
import net.xcine.gameserver.handler.admincommandhandlers.AdminQuest;
import net.xcine.gameserver.handler.admincommandhandlers.AdminReload;
import net.xcine.gameserver.handler.admincommandhandlers.AdminRepairChar;
import net.xcine.gameserver.handler.admincommandhandlers.AdminRes;
import net.xcine.gameserver.handler.admincommandhandlers.AdminRideWyvern;
import net.xcine.gameserver.handler.admincommandhandlers.AdminScript;
import net.xcine.gameserver.handler.admincommandhandlers.AdminShop;
import net.xcine.gameserver.handler.admincommandhandlers.AdminShutdown;
import net.xcine.gameserver.handler.admincommandhandlers.AdminSiege;
import net.xcine.gameserver.handler.admincommandhandlers.AdminSkill;
import net.xcine.gameserver.handler.admincommandhandlers.AdminSpawn;
import net.xcine.gameserver.handler.admincommandhandlers.AdminTarget;
import net.xcine.gameserver.handler.admincommandhandlers.AdminTeleport;
import net.xcine.gameserver.handler.admincommandhandlers.AdminTest;
import net.xcine.gameserver.handler.admincommandhandlers.AdminTownWar;
import net.xcine.gameserver.handler.admincommandhandlers.AdminUnblockIp;
import net.xcine.gameserver.handler.admincommandhandlers.AdminVIPEngine;
import net.xcine.gameserver.handler.admincommandhandlers.AdminVip;
import net.xcine.gameserver.handler.admincommandhandlers.AdminWho;
import net.xcine.gameserver.handler.admincommandhandlers.AdminZone;

/**
 * This class ...
 * @version $Revision: 1.1.4.5 $ $Date: 2005/03/27 15:30:09 $
 */
public class AdminCommandHandler
{
	protected static final Logger _log = Logger.getLogger(AdminCommandHandler.class.getName());
	
	private static AdminCommandHandler _instance;
	
	private FastMap<String, IAdminCommandHandler> _datatable;
	
	public static AdminCommandHandler getInstance()
	{
		if (_instance == null)
		{
			_instance = new AdminCommandHandler();
		}
		return _instance;
	}
	
	private AdminCommandHandler()
	{
		_datatable = new FastMap<>();
		registerAdminCommandHandler(new AdminAdmin());
		registerAdminCommandHandler(new AdminInvul());
		registerAdminCommandHandler(new AdminDelete());
		registerAdminCommandHandler(new AdminKill());
		registerAdminCommandHandler(new AdminTarget());
		registerAdminCommandHandler(new AdminShop());
		registerAdminCommandHandler(new AdminVIPEngine());
		registerAdminCommandHandler(new AdminAnnouncements());
		registerAdminCommandHandler(new AdminCreateItem());
		registerAdminCommandHandler(new AdminHeal());
		registerAdminCommandHandler(new AdminHelpPage());
		registerAdminCommandHandler(new AdminShutdown());
		registerAdminCommandHandler(new AdminSpawn());
		registerAdminCommandHandler(new AdminSkill());
		registerAdminCommandHandler(new AdminScript());
		registerAdminCommandHandler(new AdminExpSp());
		registerAdminCommandHandler(new AdminEventEngine());
		registerAdminCommandHandler(new AdminGmChat());
		registerAdminCommandHandler(new AdminEditChar());
		registerAdminCommandHandler(new AdminGm());
		registerAdminCommandHandler(new AdminTeleport());
		registerAdminCommandHandler(new AdminRepairChar());
		registerAdminCommandHandler(new AdminChangeAccessLevel());
		registerAdminCommandHandler(new AdminChristmas());
		registerAdminCommandHandler(new AdminBan());
		registerAdminCommandHandler(new AdminPolymorph());
		// registerAdminCommandHandler(new AdminBanChat());
		registerAdminCommandHandler(new AdminReload());
		registerAdminCommandHandler(new AdminKick());
		registerAdminCommandHandler(new AdminMonsterRace());
		registerAdminCommandHandler(new AdminEditNpc());
		registerAdminCommandHandler(new AdminFightCalculator());
		registerAdminCommandHandler(new AdminMenu());
		registerAdminCommandHandler(new AdminSiege());
		registerAdminCommandHandler(new AdminPetition());
		registerAdminCommandHandler(new AdminPForge());
		registerAdminCommandHandler(new AdminBBS());
		registerAdminCommandHandler(new AdminEffects());
		registerAdminCommandHandler(new AdminDoorControl());
		registerAdminCommandHandler(new AdminTest());
		registerAdminCommandHandler(new AdminEnchant());
		registerAdminCommandHandler(new AdminMassRecall());
		registerAdminCommandHandler(new AdminMassControl());
		registerAdminCommandHandler(new AdminMobGroup());
		registerAdminCommandHandler(new AdminRes());
		registerAdminCommandHandler(new AdminMammon());
		registerAdminCommandHandler(new AdminUnblockIp());
		registerAdminCommandHandler(new AdminPledge());
		registerAdminCommandHandler(new AdminRideWyvern());
		registerAdminCommandHandler(new AdminLogin());
		registerAdminCommandHandler(new AdminCache());
		registerAdminCommandHandler(new AdminLevel());
		registerAdminCommandHandler(new AdminQuest());
		registerAdminCommandHandler(new AdminZone());
		registerAdminCommandHandler(new AdminCursedWeapons());
		registerAdminCommandHandler(new AdminGeodata());
		registerAdminCommandHandler(new AdminManor());
		registerAdminCommandHandler(new AdminTownWar());
		registerAdminCommandHandler(new AdminDonator());
		registerAdminCommandHandler(new AdminNoble());
		registerAdminCommandHandler(new AdminBuffs());
		registerAdminCommandHandler(new AdminAio());
		registerAdminCommandHandler(new AdminVip());
		registerAdminCommandHandler(new AdminCharSupervision());
		registerAdminCommandHandler(new AdminWho()); // L2OFF command
		
		_log.info("AdminCommandHandler: Loaded " + _datatable.size() + " handlers.");
	}
	
	public void registerAdminCommandHandler(IAdminCommandHandler handler)
	{
		String[] ids = handler.getAdminCommandList();
		for (String element : ids)
		{
			if (Config.DEBUG)
			{
				_log.info("Adding handler for command " + element);
			}
			
			if (_datatable.keySet().contains(new String(element)))
			{
				_log.log(Level.WARNING, "Duplicated command \"" + element + "\" definition in " + handler.getClass().getName() + ".");
			}
			else
			{
				_datatable.put(element, handler);
			}
		}
		ids = null;
	}
	
	public IAdminCommandHandler getAdminCommandHandler(String adminCommand)
	{
		String command = adminCommand;
		
		if (adminCommand.indexOf(" ") != -1)
		{
			command = adminCommand.substring(0, adminCommand.indexOf(" "));
		}
		
		if (Config.DEBUG)
		{
			_log.info("getting handler for command: " + command + " -> " + (_datatable.get(command) != null));
		}
		
		return _datatable.get(command);
	}
}