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
import net.xcine.L2DatabaseFactory;
import net.xcine.Server;
import net.xcine.gameserver.cache.CrestCache;
import net.xcine.gameserver.cache.HtmCache;
import net.xcine.gameserver.communitybbs.Manager.ForumsBBSManager;
import net.xcine.gameserver.datatables.AccessLevels;
import net.xcine.gameserver.datatables.AdminCommandAccessRights;
import net.xcine.gameserver.datatables.ArmorSetsTable;
import net.xcine.gameserver.datatables.AugmentationData;
import net.xcine.gameserver.datatables.BookmarkTable;
import net.xcine.gameserver.datatables.CharNameTable;
import net.xcine.gameserver.datatables.CharTemplateTable;
import net.xcine.gameserver.datatables.ClanTable;
import net.xcine.gameserver.datatables.DoorTable;
import net.xcine.gameserver.datatables.FishTable;
import net.xcine.gameserver.datatables.GmListTable;
import net.xcine.gameserver.datatables.HelperBuffTable;
import net.xcine.gameserver.datatables.HennaTable;
import net.xcine.gameserver.datatables.HerbDropTable;
import net.xcine.gameserver.datatables.ItemTable;
import net.xcine.gameserver.datatables.MapRegionTable;
import net.xcine.gameserver.datatables.NpcTable;
import net.xcine.gameserver.datatables.NpcWalkerRoutesTable;
import net.xcine.gameserver.datatables.PetDataTable;
import net.xcine.gameserver.datatables.SkillTable;
import net.xcine.gameserver.datatables.SkillTreeTable;
import net.xcine.gameserver.datatables.SpawnTable;
import net.xcine.gameserver.datatables.SpellbookTable;
import net.xcine.gameserver.datatables.StaticObjects;
import net.xcine.gameserver.datatables.SummonItemsData;
import net.xcine.gameserver.datatables.TeleportLocationTable;
import net.xcine.gameserver.handler.AdminCommandHandler;
import net.xcine.gameserver.handler.ChatHandler;
import net.xcine.gameserver.handler.ItemHandler;
import net.xcine.gameserver.handler.SkillHandler;
import net.xcine.gameserver.handler.UserCommandHandler;
import net.xcine.gameserver.idfactory.IdFactory;
import net.xcine.gameserver.instancemanager.AuctionManager;
import net.xcine.gameserver.instancemanager.BoatManager;
import net.xcine.gameserver.instancemanager.CastleManager;
import net.xcine.gameserver.instancemanager.CastleManorManager;
import net.xcine.gameserver.instancemanager.ClanHallManager;
import net.xcine.gameserver.instancemanager.CoupleManager;
import net.xcine.gameserver.instancemanager.CursedWeaponsManager;
import net.xcine.gameserver.instancemanager.DayNightSpawnManager;
import net.xcine.gameserver.instancemanager.DimensionalRiftManager;
import net.xcine.gameserver.instancemanager.FourSepulchersManager;
import net.xcine.gameserver.instancemanager.GrandBossManager;
import net.xcine.gameserver.instancemanager.ItemsOnGroundManager;
import net.xcine.gameserver.instancemanager.MercTicketManager;
import net.xcine.gameserver.instancemanager.MovieMakerManager;
import net.xcine.gameserver.instancemanager.PetitionManager;
import net.xcine.gameserver.instancemanager.QuestManager;
import net.xcine.gameserver.instancemanager.RaidBossPointsManager;
import net.xcine.gameserver.instancemanager.RaidBossSpawnManager;
import net.xcine.gameserver.instancemanager.SiegeManager;
import net.xcine.gameserver.instancemanager.ZoneManager;
import net.xcine.gameserver.model.AutoSpawnHandler;
import net.xcine.gameserver.model.L2Manor;
import net.xcine.gameserver.model.L2Multisell;
import net.xcine.gameserver.model.L2World;
import net.xcine.gameserver.model.PartyMatchRoomList;
import net.xcine.gameserver.model.PartyMatchWaitingList;
import net.xcine.gameserver.model.entity.Castle;
import net.xcine.gameserver.model.entity.DevastatedCastle;
import net.xcine.gameserver.model.entity.FortressOfDead;
import net.xcine.gameserver.model.entity.FortressOfResistance;
import net.xcine.gameserver.model.entity.Hero;
import net.xcine.gameserver.model.olympiad.Olympiad;
import net.xcine.gameserver.model.olympiad.OlympiadGameManager;
import net.xcine.gameserver.network.L2GameClient;
import net.xcine.gameserver.network.L2GamePacketHandler;
import net.xcine.gameserver.pathfinding.PathFinding;
import net.xcine.gameserver.scripting.L2ScriptEngineManager;
import net.xcine.gameserver.taskmanager.KnownListUpdateTaskManager;
import net.xcine.gameserver.taskmanager.TaskManager;
import net.xcine.gameserver.xmlfactory.XMLDocumentFactory;
import net.xcine.util.DeadLockDetector;
import net.xcine.util.IPv4Filter;
import net.xcine.util.Util;

import org.mmocore.network.SelectorConfig;
import org.mmocore.network.SelectorThread;

public class GameServer
{
	private static final Logger _log = Logger.getLogger(GameServer.class.getName());
	
	private final SelectorThread<L2GameClient> _selectorThread;
	private final L2GamePacketHandler _gamePacketHandler;
	private final DeadLockDetector _deadDetectThread;
	public static GameServer gameServer;
	private final LoginServerThread _loginThread;
	public static final Calendar dateTimeServerStarted = Calendar.getInstance();
	
	public long getUsedMemoryMB()
	{
		return (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1048576; // 1024 * 1024 = 1048576;
	}
	
	public SelectorThread<L2GameClient> getSelectorThread()
	{
		return _selectorThread;
	}
	
	public GameServer() throws Exception
	{
		gameServer = this;
		
		IdFactory.getInstance();
		ThreadPoolManager.getInstance();
		
		new File("./data/crests").mkdirs();
		
		Util.printSection("World");
		GameTimeController.getInstance();
		L2World.getInstance();
		MapRegionTable.getInstance();
		Announcements.getInstance();
		BookmarkTable.getInstance();
		
		Util.printSection("Skills");
		SkillTable.getInstance();
		SkillTreeTable.getInstance();
		
		Util.printSection("Items");
		ItemTable.getInstance();
		SummonItemsData.getInstance();
		TradeController.getInstance();
		L2Multisell.getInstance();
		RecipeController.getInstance();
		ArmorSetsTable.getInstance();
		FishTable.getInstance();
		SpellbookTable.getInstance();
		
		Util.printSection("Conquerable Halls");
		DevastatedCastle.getInstance();
		FortressOfResistance.getInstance();
		FortressOfDead.getInstance();
		
		Util.printSection("Augments");
		AugmentationData.getInstance();
		
		Util.printSection("Characters");
		AccessLevels.getInstance();
		AdminCommandAccessRights.getInstance();
		CharTemplateTable.getInstance();
		CharNameTable.getInstance();
		GmListTable.getInstance();
		RaidBossPointsManager.getInstance();
		
		Util.printSection("Community server");
		if (Config.ENABLE_COMMUNITY_BOARD) // Forums has to be loaded before clan data
			ForumsBBSManager.getInstance().initRoot();
		else
			_log.config("Community server is disabled.");
		
		Util.printSection("Cache");
		HtmCache.getInstance();
		CrestCache.load();
		TeleportLocationTable.getInstance();
		PartyMatchWaitingList.getInstance();
		PartyMatchRoomList.getInstance();
		PetitionManager.getInstance();
		HennaTable.getInstance();
		HelperBuffTable.getInstance();
		CursedWeaponsManager.getInstance();
		
		Util.printSection("Clans");
		ClanTable.getInstance();
		AuctionManager.getInstance();
		ClanHallManager.getInstance();
		
		Util.printSection("Geodata");
		GeoData.getInstance();
		if (Config.GEODATA == 2)
			PathFinding.getInstance();
		
		Util.printSection("Zones");
		ZoneManager.getInstance();
		
        Util.printSection("World Bosses");
        GrandBossManager.init();
		
		Util.printSection("Castles");
		CastleManager.getInstance().load();
		
		Util.printSection("Seven Signs");
		SevenSigns.getInstance().spawnSevenSignsNPC();
		SevenSignsFestival.getInstance();
		
		Util.printSection("Sieges");
		SiegeManager.getSieges();
		MercTicketManager.getInstance();
		
		Util.printSection("Manor Manager");
		CastleManorManager.getInstance();
		L2Manor.getInstance();
		
		Util.printSection("NPCs");
		HerbDropTable.getInstance();
		PetDataTable.getInstance();
		NpcTable.getInstance();
		NpcWalkerRoutesTable.getInstance();
		DoorTable.getInstance();
		for (Castle castle : CastleManager.getInstance().getCastles())
			castle.loadDoorUpgrade();
		StaticObjects.load();
		SpawnTable.getInstance();
		RaidBossSpawnManager.getInstance();
		DayNightSpawnManager.getInstance().trim().notifyChangeMode();
		DimensionalRiftManager.getInstance();
		
		Util.printSection("Olympiads & Heroes");
		OlympiadGameManager.getInstance();
		Olympiad.getInstance();
		Hero.getInstance();
		
		Util.printSection("Four Sepulchers");
		FourSepulchersManager.getInstance().init();
		
		Util.printSection("Quests & Scripts");
		QuestManager.getInstance();
		BoatManager.getInstance();
		try
		{
			File scripts = new File("./data/scripts.cfg");
			L2ScriptEngineManager.getInstance().executeScriptList(scripts);
		}
		catch (IOException ioe)
		{
			_log.severe("Failed loading scripts.cfg, no script going to be loaded");
		}
		QuestManager.getInstance().report();
		
		if (Config.SAVE_DROPPED_ITEM)
			ItemsOnGroundManager.getInstance();
		
		if (Config.AUTODESTROY_ITEM_AFTER > 0 || Config.HERB_AUTO_DESTROY_TIME > 0)
			ItemsAutoDestroy.getInstance();
		
		MonsterRace.getInstance();
		
		Util.printSection("Handlers");
		_log.config("AutoSpawnHandler: Loaded " + AutoSpawnHandler.getInstance().size() + " handlers.");
		_log.config("AdminCommandHandler: Loaded " + AdminCommandHandler.getInstance().size() + " handlers.");
		_log.config("ChatHandler: Loaded " + ChatHandler.getInstance().size() + " handlers.");
		_log.config("ItemHandler: Loaded " + ItemHandler.getInstance().size() + " handlers.");
		_log.config("SkillHandler: Loaded " + SkillHandler.getInstance().size() + " handlers.");
		_log.config("UserCommandHandler: Loaded " + UserCommandHandler.getInstance().size() + " handlers.");
		
		if (Config.ALLOW_WEDDING)
			CoupleManager.getInstance();
		
		Util.printSection("System");
		TaskManager.getInstance();
		
		Runtime.getRuntime().addShutdownHook(Shutdown.getInstance());
		ForumsBBSManager.getInstance();
		_log.config("IdFactory: Free ObjectIDs remaining: " + IdFactory.getInstance().size());
		
		KnownListUpdateTaskManager.getInstance();
		MovieMakerManager.getInstance();
		
		if (Config.DEADLOCK_DETECTOR)
		{
			_log.info("Deadlock detector is enabled. Timer: " + Config.DEADLOCK_CHECK_INTERVAL + "s.");
			_deadDetectThread = new DeadLockDetector();
			_deadDetectThread.setDaemon(true);
			_deadDetectThread.start();
		}
		else
		{
			_log.info("Deadlock detector is disabled.");
			_deadDetectThread = null;
		}
		
		System.gc();
		
		long usedMem = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1048576;
		long totalMem = Runtime.getRuntime().maxMemory() / 1048576;
		
		_log.info("Gameserver have started, used memory: " + usedMem + " / " + totalMem + " Mo.");
		_log.info("Maximum allowed players: " + Config.MAXIMUM_ONLINE_USERS);
		
		Util.printSection("Login");
		_loginThread = LoginServerThread.getInstance();
		_loginThread.start();
		
		final SelectorConfig sc = new SelectorConfig();
		sc.MAX_READ_PER_PASS = Config.MMO_MAX_READ_PER_PASS;
		sc.MAX_SEND_PER_PASS = Config.MMO_MAX_SEND_PER_PASS;
		sc.SLEEP_TIME = Config.MMO_SELECTOR_SLEEP_TIME;
		sc.HELPER_BUFFER_COUNT = Config.MMO_HELPER_BUFFER_COUNT;
		
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
				_log.log(Level.SEVERE, "WARNING: The GameServer bind address is invalid, using all available IPs. Reason: " + e1.getMessage(), e1);
			}
		}
		
		try
		{
			_selectorThread.openServerSocket(bindAddress, Config.PORT_GAME);
		}
		catch (IOException e)
		{
			_log.log(Level.SEVERE, "FATAL: Failed to open server socket. Reason: " + e.getMessage(), e);
			System.exit(1);
		}
		_selectorThread.start();
	}
	
	public static void main(String[] args) throws Exception
	{
		Server.serverMode = Server.MODE_GAMESERVER;
		
		final String LOG_FOLDER = "./log"; // Name of folder for log file
		final String LOG_NAME = "config/log.cfg"; // Name of log file
		
		// Create log folder
		File logFolder = new File(LOG_FOLDER);
		logFolder.mkdir();
		
		// Create input stream for log file -- or store file data into memory
		InputStream is = new FileInputStream(new File(LOG_NAME));
		LogManager.getLogManager().readConfiguration(is);
		is.close();
		
		Util.printSection("Team");
		Util.team();
		
		Util.printSection("xcine");
		
		// Initialize config
		Config.load();
		
		// Factories
		XMLDocumentFactory.getInstance();
		L2DatabaseFactory.getInstance();
		
		gameServer = new GameServer();
	}
}