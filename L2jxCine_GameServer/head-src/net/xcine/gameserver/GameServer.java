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
package net.xcine.gameserver;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import net.xcine.Config;
import net.xcine.ServerType;
import net.xcine.crypt.nProtect;
import net.xcine.gameserver.ai.special.manager.AILoader;
import net.xcine.gameserver.cache.CrestCache;
import net.xcine.gameserver.cache.HtmCache;
import net.xcine.gameserver.communitybbs.Manager.ForumsBBSManager;
import net.xcine.gameserver.controllers.GameTimeController;
import net.xcine.gameserver.controllers.RecipeController;
import net.xcine.gameserver.controllers.TradeController;
import net.xcine.gameserver.datatables.GmListTable;
import net.xcine.gameserver.datatables.HeroSkillTable;
import net.xcine.gameserver.datatables.NobleSkillTable;
import net.xcine.gameserver.datatables.OfflineTradeTable;
import net.xcine.gameserver.datatables.SkillTable;
import net.xcine.gameserver.datatables.csv.DoorTable;
import net.xcine.gameserver.datatables.csv.ExtractableItemsData;
import net.xcine.gameserver.datatables.csv.FishTable;
import net.xcine.gameserver.datatables.csv.HennaTable;
import net.xcine.gameserver.datatables.csv.MapRegionTable;
import net.xcine.gameserver.datatables.csv.NpcWalkerRoutesTable;
import net.xcine.gameserver.datatables.csv.RecipeTable;
import net.xcine.gameserver.datatables.csv.StaticObjects;
import net.xcine.gameserver.datatables.csv.SummonItemsData;
import net.xcine.gameserver.datatables.sql.AccessLevels;
import net.xcine.gameserver.datatables.sql.AdminCommandAccessRights;
import net.xcine.gameserver.datatables.sql.ArmorSetsTable;
import net.xcine.gameserver.datatables.sql.CharNameTable;
import net.xcine.gameserver.datatables.sql.CharTemplateTable;
import net.xcine.gameserver.datatables.sql.ClanTable;
import net.xcine.gameserver.datatables.sql.CustomArmorSetsTable;
import net.xcine.gameserver.datatables.sql.HelperBuffTable;
import net.xcine.gameserver.datatables.sql.HennaTreeTable;
import net.xcine.gameserver.datatables.sql.ItemTable;
import net.xcine.gameserver.datatables.sql.L2PetDataTable;
import net.xcine.gameserver.datatables.sql.LevelUpData;
import net.xcine.gameserver.datatables.sql.NpcTable;
import net.xcine.gameserver.datatables.sql.SkillSpellbookTable;
import net.xcine.gameserver.datatables.sql.SkillTreeTable;
import net.xcine.gameserver.datatables.sql.SpawnTable;
import net.xcine.gameserver.datatables.xml.AugmentationData;
import net.xcine.gameserver.datatables.xml.ExperienceData;
import net.xcine.gameserver.datatables.xml.TeleportLocationTable;
import net.xcine.gameserver.datatables.xml.ZoneData;
import net.xcine.gameserver.geo.GeoData;
import net.xcine.gameserver.geo.geoeditorcon.GeoEditorListener;
import net.xcine.gameserver.geo.pathfinding.PathFinding;
import net.xcine.gameserver.handler.AdminCommandHandler;
import net.xcine.gameserver.handler.AutoAnnouncementHandler;
import net.xcine.gameserver.handler.AutoChatHandler;
import net.xcine.gameserver.handler.ItemHandler;
import net.xcine.gameserver.handler.SkillHandler;
import net.xcine.gameserver.handler.UserCommandHandler;
import net.xcine.gameserver.handler.VoicedCommandHandler;
import net.xcine.gameserver.idfactory.IdFactory;
import net.xcine.gameserver.managers.AuctionManager;
import net.xcine.gameserver.managers.AutoSaveManager;
import net.xcine.gameserver.managers.BoatManager;
import net.xcine.gameserver.managers.CastleManager;
import net.xcine.gameserver.managers.CastleManorManager;
import net.xcine.gameserver.managers.ClanHallManager;
import net.xcine.gameserver.managers.ClassDamageManager;
import net.xcine.gameserver.managers.CoupleManager;
import net.xcine.gameserver.managers.CrownManager;
import net.xcine.gameserver.managers.CursedWeaponsManager;
import net.xcine.gameserver.managers.DayNightSpawnManager;
import net.xcine.gameserver.managers.DimensionalRiftManager;
import net.xcine.gameserver.managers.DuelManager;
import net.xcine.gameserver.managers.FortManager;
import net.xcine.gameserver.managers.FortSiegeManager;
import net.xcine.gameserver.managers.FourSepulchersManager;
import net.xcine.gameserver.managers.GrandBossManager;
import net.xcine.gameserver.managers.ItemsOnGroundManager;
import net.xcine.gameserver.managers.MercTicketManager;
import net.xcine.gameserver.managers.PetitionManager;
import net.xcine.gameserver.managers.QuestManager;
import net.xcine.gameserver.managers.RaidBossPointsManager;
import net.xcine.gameserver.managers.RaidBossSpawnManager;
import net.xcine.gameserver.managers.SiegeManager;
import net.xcine.gameserver.model.L2Manor;
import net.xcine.gameserver.model.L2World;
import net.xcine.gameserver.model.PartyMatchRoomList;
import net.xcine.gameserver.model.PartyMatchWaitingList;
import net.xcine.gameserver.model.ipCatcher;
import net.xcine.gameserver.model.entity.Announcements;
import net.xcine.gameserver.model.entity.Hero;
import net.xcine.gameserver.model.entity.MonsterRace;
import net.xcine.gameserver.model.entity.event.manager.EventManager;
import net.xcine.gameserver.model.entity.olympiad.Olympiad;
import net.xcine.gameserver.model.entity.sevensigns.SevenSigns;
import net.xcine.gameserver.model.entity.sevensigns.SevenSignsFestival;
import net.xcine.gameserver.model.entity.siege.clanhalls.BanditStrongholdSiege;
import net.xcine.gameserver.model.entity.siege.clanhalls.DevastatedCastle;
import net.xcine.gameserver.model.entity.siege.clanhalls.FortressOfResistance;
import net.xcine.gameserver.model.multisell.L2Multisell;
import net.xcine.gameserver.model.spawn.AutoSpawn;
import net.xcine.gameserver.network.L2GameClient;
import net.xcine.gameserver.network.L2GamePacketHandler;
import net.xcine.gameserver.script.EventDroplist;
import net.xcine.gameserver.script.faenor.FaenorScriptEngine;
import net.xcine.gameserver.scripting.CompiledScriptCache;
import net.xcine.gameserver.scripting.L2ScriptEngineManager;
import net.xcine.gameserver.taskmanager.TaskManager;
import net.xcine.gameserver.thread.LoginServerThread;
import net.xcine.gameserver.thread.ThreadPoolManager;
import net.xcine.gameserver.thread.daemons.DeadlockDetector;
import net.xcine.gameserver.thread.daemons.ItemsAutoDestroy;
import net.xcine.gameserver.thread.daemons.PcPoint;
import net.xcine.gameserver.util.sql.SQLQueue;
import net.xcine.netcore.SelectorConfig;
import net.xcine.netcore.SelectorThread;
import net.xcine.status.Status;
import net.xcine.util.IPv4Filter;
import net.xcine.util.Memory;
import net.xcine.util.Util;
import net.xcine.util.database.L2DatabaseFactory;

public class GameServer
{
	private static Logger _log = Logger.getLogger("Loader");
	private static SelectorThread<L2GameClient> _selectorThread;
	private static LoginServerThread _loginThread;
	private static L2GamePacketHandler _gamePacketHandler;
	private static Status _statusServer;
	
	public static final Calendar dateTimeServerStarted = Calendar.getInstance();
	
	public static void main(String[] args) throws Exception
	{
		ServerType.serverMode = ServerType.MODE_GAMESERVER;
		
		final String LOG_FOLDER_BASE = "log"; // Name of folder for log base file
		File logFolderBase = new File(LOG_FOLDER_BASE);
		logFolderBase.mkdir();
		
		// Local Constants
		final String LOG_FOLDER = "log/game";
		
		// Create log folder
		File logFolder = new File(LOG_FOLDER);
		logFolder.mkdir();
		
		// Create input stream for log file -- or store file data into memory
		
		// check for legacy Implementation
		File log_conf_file = new File(Config.LOG_CONF_FILE);
		if (!log_conf_file.exists())
		{
			// old file position
			log_conf_file = new File(Config.LEGACY_LOG_CONF_FILE);
		}
		
		InputStream is = new FileInputStream(log_conf_file);
		LogManager.getLogManager().readConfiguration(is);
		is.close();
		is = null;
		logFolder = null;
		
		long serverLoadStart = System.currentTimeMillis();
		
		Util.printSection("Team");
		Util.team();
		Config.load();
		
		Util.printSection("Database");
		L2DatabaseFactory.getInstance();
		_log.info("L2DatabaseFactory: loaded.");
		
		Util.printSection("Threads");
		ThreadPoolManager.getInstance();
		if (Config.DEADLOCKCHECK_INTIAL_TIME > 0)
		{
			ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(DeadlockDetector.getInstance(), Config.DEADLOCKCHECK_INTIAL_TIME, Config.DEADLOCKCHECK_DELAY_TIME);
		}
		new File(Config.DATAPACK_ROOT, "data/clans").mkdirs();
		new File(Config.DATAPACK_ROOT, "data/crests").mkdirs();
		new File(Config.DATAPACK_ROOT, "data/pathnode").mkdirs();
		new File(Config.DATAPACK_ROOT, "data/geodata").mkdirs();
		
		HtmCache.getInstance();
		CrestCache.getInstance();
		L2ScriptEngineManager.getInstance();
		
		nProtect.getInstance();
		if (nProtect.isEnabled())
			_log.info("nProtect System Enabled");
		
		Util.printSection("World");
		L2World.getInstance();
		MapRegionTable.getInstance();
		Announcements.getInstance();
		AutoAnnouncementHandler.getInstance();

        if (!IdFactory.getInstance().isInitialized())
        {
            _log.info("Could not read object IDs from DB. Please Check Your Data.");
            throw new Exception("Could not initialize the ID factory");
        }

		StaticObjects.getInstance();
		TeleportLocationTable.getInstance();
		PartyMatchWaitingList.getInstance();
		PartyMatchRoomList.getInstance();
		GameTimeController.getInstance();
		CharNameTable.getInstance();
		ExperienceData.getInstance();
		DuelManager.getInstance();
		
		if (Config.ENABLE_CLASS_DAMAGES)
			ClassDamageManager.loadConfig();
		
		if (Config.AUTOSAVE_DELAY_TIME > 0)
		{
			AutoSaveManager.getInstance().startAutoSaveManager();
		}
		
		Util.printSection("Skills");
		if (!SkillTable.getInstance().isInitialized())
		{
			_log.info("Could not find the extraced files. Please Check Your Data.");
			throw new Exception("Could not initialize the skill table");
		}
		SkillTreeTable.getInstance();
		SkillSpellbookTable.getInstance();
		NobleSkillTable.getInstance();
		HeroSkillTable.getInstance();
		_log.info("Skills: All skills loaded.");
		
		Util.printSection("Items");
		if (!ItemTable.getInstance().isInitialized())
		{
			_log.info("Could not find the extraced files. Please Check Your Data.");
			throw new Exception("Could not initialize the item table");
		}
		ArmorSetsTable.getInstance();
		if (Config.CUSTOM_ARMORSETS_TABLE)
		{
			CustomArmorSetsTable.getInstance();
		}
		ExtractableItemsData.getInstance();
		SummonItemsData.getInstance();
		if (Config.ALLOWFISHING)
			FishTable.getInstance();
		
		Util.printSection("Npc");
		NpcWalkerRoutesTable.getInstance().load();
		if (!NpcTable.getInstance().isInitialized())
		{
			_log.info("Could not find the extraced files. Please Check Your Data.");
			throw new Exception("Could not initialize the npc table");
		}
		
		Util.printSection("Characters");
		if (Config.COMMUNITY_TYPE.equals("full"))
		{
			ForumsBBSManager.getInstance().initRoot();
		}
		
		ClanTable.getInstance();
		CharTemplateTable.getInstance();
		LevelUpData.getInstance();
		if (!HennaTable.getInstance().isInitialized())
		{
			throw new Exception("Could not initialize the Henna Table");
		}
		
		if (!HennaTreeTable.getInstance().isInitialized())
		{
			throw new Exception("Could not initialize the Henna Tree Table");
		}
		
		if (!HelperBuffTable.getInstance().isInitialized())
		{
			throw new Exception("Could not initialize the Helper Buff Table");
		}
		
		Util.printSection("Geodata");
		GeoData.getInstance();
		if (Config.GEODATA == 2)
		{
			PathFinding.getInstance();
		}
		
		Util.printSection("Economy");
		TradeController.getInstance();
		L2Multisell.getInstance();
		_log.info("Multisell: loaded.");
		
		Util.printSection("Clan Halls");
		ClanHallManager.getInstance();
		FortressOfResistance.getInstance();
		DevastatedCastle.getInstance();
		BanditStrongholdSiege.getInstance();
		AuctionManager.getInstance();
		
		Util.printSection("Zone");
		ZoneData.getInstance();
		
		Util.printSection("Spawnlist");
		if (!Config.ALT_DEV_NO_SPAWNS)
		{
			SpawnTable.getInstance();
		}
		else
		{
			_log.info("Spawn: disable load.");
		}
		if (!Config.ALT_DEV_NO_RB)
		{
			RaidBossSpawnManager.getInstance();
			GrandBossManager.getInstance();
			RaidBossPointsManager.init();
		}
		else
		{
			_log.info("RaidBoss: disable load.");
		}
		DayNightSpawnManager.getInstance().notifyChangeMode();
		
		Util.printSection("Dimensional Rift");
		DimensionalRiftManager.getInstance();
		
		Util.printSection("Misc");
		RecipeTable.getInstance();
		RecipeController.getInstance();
		EventDroplist.getInstance();
		AugmentationData.getInstance();
		MonsterRace.getInstance();
		MercTicketManager.getInstance();
		PetitionManager.getInstance();
		CursedWeaponsManager.getInstance();
		TaskManager.getInstance();
		L2PetDataTable.getInstance().loadPetsData();
		SQLQueue.getInstance();
		if (Config.ACCEPT_GEOEDITOR_CONN)
		{
			GeoEditorListener.getInstance();
		}
		if (Config.SAVE_DROPPED_ITEM)
		{
			ItemsOnGroundManager.getInstance();
		}
		if (Config.AUTODESTROY_ITEM_AFTER > 0 || Config.HERB_AUTO_DESTROY_TIME > 0)
		{
			ItemsAutoDestroy.getInstance();
		}
		
		Util.printSection("Manor");
		L2Manor.getInstance();
		CastleManorManager.getInstance();
		
		Util.printSection("Castles");
		CastleManager.getInstance();
		SiegeManager.getInstance();
		FortManager.getInstance();
		FortSiegeManager.getInstance();
		CrownManager.getInstance();
		
		Util.printSection("Boat");
		BoatManager.getInstance();
		
		Util.printSection("Doors");
		DoorTable.getInstance().parseData();
		
		Util.printSection("Four Sepulchers");
		FourSepulchersManager.getInstance();
		
		Util.printSection("Seven Signs");
		SevenSigns.getInstance();
		SevenSignsFestival.getInstance();
		AutoSpawn.getInstance();
		AutoChatHandler.getInstance();
		
		Util.printSection("Olympiad System");
		Olympiad.getInstance();
		Hero.getInstance();
		
		Util.printSection("Access Levels");
		AccessLevels.getInstance();
		AdminCommandAccessRights.getInstance();
		GmListTable.getInstance();
		
		Util.printSection("Handlers");
		ItemHandler.getInstance();
		SkillHandler.getInstance();
		AdminCommandHandler.getInstance();
		UserCommandHandler.getInstance();
		VoicedCommandHandler.getInstance();
		
		_log.info("AutoChatHandler : Loaded " + AutoChatHandler.getInstance().size() + " handlers in total.");
		_log.info("AutoSpawnHandler : Loaded " + AutoSpawn.getInstance().size() + " handlers in total.");
		
		Runtime.getRuntime().addShutdownHook(Shutdown.getInstance());
		
		try
		{
			DoorTable doorTable = DoorTable.getInstance();
			
			// Opened by players like L2OFF
			//doorTable.getDoor(19160010).openMe();
			//doorTable.getDoor(19160011).openMe();
			
			doorTable.getDoor(19160012).openMe();
			doorTable.getDoor(19160013).openMe();
			doorTable.getDoor(19160014).openMe();
			doorTable.getDoor(19160015).openMe();
			doorTable.getDoor(19160016).openMe();
			doorTable.getDoor(19160017).openMe();
			doorTable.getDoor(24190001).openMe();
			doorTable.getDoor(24190002).openMe();
			doorTable.getDoor(24190003).openMe();
			doorTable.getDoor(24190004).openMe();
			doorTable.getDoor(23180001).openMe();
			doorTable.getDoor(23180002).openMe();
			doorTable.getDoor(23180003).openMe();
			doorTable.getDoor(23180004).openMe();
			doorTable.getDoor(23180005).openMe();
			doorTable.getDoor(23180006).openMe();
			doorTable.checkAutoOpen();
			doorTable = null;
		}
		catch (NullPointerException e)
		{
			_log.info("There is errors in your Door.csv file. Update door.csv");
			if (Config.ENABLE_ALL_EXCEPTIONS)
				e.printStackTrace();
		}
		
		Util.printSection("Quests");
		if (!Config.ALT_DEV_NO_QUESTS)
		{
			QuestManager.getInstance();
			QuestManager.getInstance().report();
		}
		else
			_log.info("Quest: disable load.");
		
		Util.printSection("AI");
		if (!Config.ALT_DEV_NO_AI)
		{
			AILoader.init();
		}
		else
		{
			_log.info("AI: disable load.");
		}
		
		Util.printSection("Scripts");
		if (!Config.ALT_DEV_NO_SCRIPT)
		{
			File scripts = new File(Config.DATAPACK_ROOT, "data/scripts.cfg");
			L2ScriptEngineManager.getInstance().executeScriptsList(scripts);
			
			CompiledScriptCache compiledScriptCache = L2ScriptEngineManager.getInstance().getCompiledScriptCache();
			if (compiledScriptCache == null)
				_log.info("Compiled Scripts Cache is disabled.");
			else
			{
				compiledScriptCache.purge();
				if (compiledScriptCache.isModified())
				{
					compiledScriptCache.save();
					_log.info("Compiled Scripts Cache was saved.");
				}
				else
					_log.info("Compiled Scripts Cache is up-to-date.");
			}
			FaenorScriptEngine.getInstance();
		}
		else
		{
			_log.info("Script: disable load.");
		}
		
		Util.printSection("Game Server");

		_log.info("IdFactory: Free ObjectID's remaining: " + IdFactory.getInstance().size());
		
		Util.printSection("Custom Mods");
		
		if (Config.L2JMOD_ALLOW_WEDDING || Config.PCB_ENABLE)
		{
			if (Config.L2JMOD_ALLOW_WEDDING)
				CoupleManager.getInstance();

			if (Config.PCB_ENABLE)
				ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(PcPoint.getInstance(), Config.PCB_INTERVAL * 1000, Config.PCB_INTERVAL * 1000);
		}
		else
			_log.info("All custom mods are Disabled.");
		
		Util.printSection("EventManager");
		EventManager.getInstance().startEventRegistration();
		
		if (EventManager.TVT_EVENT_ENABLED || EventManager.CTF_EVENT_ENABLED || EventManager.DM_EVENT_ENABLED)
		{
			if (EventManager.TVT_EVENT_ENABLED)
				_log.info("TVT Event is Enabled.");
			if (EventManager.CTF_EVENT_ENABLED)
				_log.info("CTF Event is Enabled.");
			if (EventManager.DM_EVENT_ENABLED)
				_log.info("DM Event is Enabled.");
		}
		else
			_log.info("All events are Disabled.");
		
		if ((Config.OFFLINE_TRADE_ENABLE || Config.OFFLINE_CRAFT_ENABLE) && Config.RESTORE_OFFLINERS)
			OfflineTradeTable.restoreOfflineTraders();

		ipCatcher.ipsLoad();
		
		Util.printSection("Info");
		_log.info("Operating System: " + Util.getOSName() + " " + Util.getOSVersion() + " " + Util.getOSArch());
		_log.info("Available CPUs: " + Util.getAvailableProcessors());
		_log.info("Maximum Numbers of Connected Players: " + Config.MAXIMUM_ONLINE_USERS);
		_log.info("GameServer Started, free memory " + Memory.getFreeMemory() + " Mb of " + Memory.getTotalMemory() + " Mb");
		_log.info("Used memory: " + Memory.getUsedMemory() + " MB");
		
		Util.printSection("Java specific");
		_log.info("JRE name: " + System.getProperty("java.vendor"));
		_log.info("JRE specification version: " + System.getProperty("java.specification.version"));
		_log.info("JRE version: " + System.getProperty("java.version"));
		_log.info("--- Detecting Java Virtual Machine (JVM)");
		_log.info("JVM installation directory: " + System.getProperty("java.home"));
		_log.info("JVM Avaible Memory(RAM): " + Runtime.getRuntime().maxMemory() / 1048576 + " MB");
		_log.info("JVM specification version: " + System.getProperty("java.vm.specification.version"));
		_log.info("JVM specification vendor: " + System.getProperty("java.vm.specification.vendor"));
		_log.info("JVM specification name: " + System.getProperty("java.vm.specification.name"));
		_log.info("JVM implementation version: " + System.getProperty("java.vm.version"));
		_log.info("JVM implementation vendor: " + System.getProperty("java.vm.vendor"));
		_log.info("JVM implementation name: " + System.getProperty("java.vm.name"));
		
		Util.printSection("Status");
		System.gc();
		_log.info("Server Loaded in " + (System.currentTimeMillis() - serverLoadStart) / 1000 + " seconds");
		ServerStatus.getInstance();
		
		// Load telnet status
		Util.printSection("Telnet");
		if (Config.IS_TELNET_ENABLED)
		{
			_statusServer = new Status(ServerType.serverMode);
			_statusServer.start();
		}
		else
		{
			_log.info("Telnet server is disabled.");
		}
		
		Util.printSection("Login");
		_loginThread = LoginServerThread.getInstance();
		_loginThread.start();
		
		final SelectorConfig sc = new SelectorConfig();
		sc.MAX_READ_PER_PASS = net.xcine.netcore.Config.getInstance().MMO_MAX_READ_PER_PASS;
		sc.MAX_SEND_PER_PASS = net.xcine.netcore.Config.getInstance().MMO_MAX_SEND_PER_PASS;
		sc.SLEEP_TIME = net.xcine.netcore.Config.getInstance().MMO_SELECTOR_SLEEP_TIME;
		sc.HELPER_BUFFER_COUNT = net.xcine.netcore.Config.getInstance().MMO_HELPER_BUFFER_COUNT;
		
		_gamePacketHandler = new L2GamePacketHandler();
		
		_selectorThread = new SelectorThread<>(sc, _gamePacketHandler, _gamePacketHandler, _gamePacketHandler, new IPv4Filter());
		
		InetAddress bindAddress = null;
		if (!Config.GAMESERVER_HOSTNAME.equals("*"))
		{
			try
			{
				bindAddress = InetAddress.getByName(Config.GAMESERVER_HOSTNAME);
			}
			catch (UnknownHostException e1)
			{
				if (Config.ENABLE_ALL_EXCEPTIONS)
					e1.printStackTrace();
				
				_log.log(Level.SEVERE, "WARNING: The GameServer bind address is invalid, using all avaliable IPs. Reason: " + e1.getMessage(), e1);
			}
		}
		
		try
		{
			_selectorThread.openServerSocket(bindAddress, Config.PORT_GAME);
		}
		catch (IOException e)
		{
			if (Config.ENABLE_ALL_EXCEPTIONS)
				e.printStackTrace();
			
			_log.log(Level.SEVERE, "FATAL: Failed to open server socket. Reason: " + e.getMessage(), e);
			System.exit(1);
		}
		_selectorThread.start();		
	}
	
	public static SelectorThread<L2GameClient> getSelectorThread()
	{
		return _selectorThread;
	}
}