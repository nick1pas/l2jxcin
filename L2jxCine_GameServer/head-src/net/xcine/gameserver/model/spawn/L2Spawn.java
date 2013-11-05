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
package net.xcine.gameserver.model.spawn;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javolution.util.FastList;
import net.xcine.Config;
import net.xcine.gameserver.datatables.xml.TerritoryData;
import net.xcine.gameserver.geo.GeoData;
import net.xcine.gameserver.idfactory.IdFactory;
import net.xcine.gameserver.model.L2Attackable;
import net.xcine.gameserver.model.L2Npc;
import net.xcine.gameserver.model.L2Object;
import net.xcine.gameserver.model.quest.Quest;
import net.xcine.gameserver.templates.L2NpcTemplate;
import net.xcine.gameserver.thread.ThreadPoolManager;
import net.xcine.util.random.Rnd;

public class L2Spawn
{
	protected static final Logger _log = Logger.getLogger(L2Spawn.class.getName());

	private L2NpcTemplate _template;
	private int _id;
	private int _location;
	private int _maximumCount;
	private int _currentCount;
	protected int _scheduledCount;
	private int _locX;
	private int _locY;
	private int _locZ;
	private int _heading;
	private int _respawnDelay;
	private int _respawnMinDelay;
	private int _respawnMaxDelay;
	private Constructor<?> _constructor;
	private boolean _doRespawn;
	private int _instanceId = 0;
	private L2Npc _lastSpawn;
	private static List<SpawnListener> _spawnListeners = new FastList<>();


	class SpawnTask implements Runnable
	{
		private L2Npc _oldNpc;

		public SpawnTask(L2Npc pOldNpc)
		{
			_oldNpc = pOldNpc;
		}

		@Override
		public void run()
		{
			try
			{
				//doSpawn();
				respawnNpc(_oldNpc);
			}
			catch(Exception e)
			{
				if(Config.ENABLE_ALL_EXCEPTIONS)
					e.printStackTrace();
				
				_log.log(Level.WARNING, "", e);
			}

			_scheduledCount--;
		}
	}

	/**
	 * Constructor of L2Spawn.<BR>
	 * <BR>
	 * <B><U> Concept</U> :</B><BR>
	 * <BR>
	 * Each L2Spawn owns generic and static properties (ex : RewardExp, RewardSP, AggroRange...). All of those
	 * properties are stored in a different L2NpcTemplate for each type of L2Spawn. Each template is loaded once in the
	 * server cache memory (reduce memory use). When a new instance of L2Spawn is created, server just create a link
	 * between the instance and the template. This link is stored in <B>_template</B><BR>
	 * <BR>
	 * Each L2Npc is linked to a L2Spawn that manages its spawn and respawn (delay, location...). This link is
	 * stored in <B>_spawn</B> of the L2Npc<BR>
	 * <BR>
	 * <B><U> Actions</U> :</B><BR>
	 * <BR>
	 * <li>Set the _template of the L2Spawn</li> <li>Calculate the implementationName used to generate the generic
	 * constructor of L2Npc managed by this L2Spawn</li> <li>Create the generic constructor of L2Npc
	 * managed by this L2Spawn</li><BR>
	 * <BR>
	 * 
	 * @param mobTemplate The L2NpcTemplate to link to this L2Spawn
	 * @throws SecurityException 
	 * @throws ClassNotFoundException 
	 * @throws NoSuchMethodException 
	 */
	public L2Spawn(L2NpcTemplate mobTemplate) throws SecurityException, ClassNotFoundException, NoSuchMethodException
	{
		// Set the _template of the L2Spawn
		_template = mobTemplate;

		if(_template == null)
			return;

		// The Name of the L2Npc type managed by this L2Spawn
		String implementationName = _template.type; // implementing class name

		if(mobTemplate.npcId == 30995)
		{
			implementationName = "L2RaceManager";
		}

		// if (mobTemplate.npcId == 8050)

		if(mobTemplate.npcId >= 31046 && mobTemplate.npcId <= 31053)
		{
			implementationName = "L2SymbolMaker";
		}

		// Create the generic constructor of L2Npc managed by this L2Spawn
		Class<?>[] parameters =
		{
				int.class, L2NpcTemplate.class
		};
		_constructor = Class.forName("net.xcine.gameserver.model.actor.instance." + implementationName + "Instance").getConstructor(parameters);
		implementationName = null;
	}

	/**
	 * @return the maximum number of L2Npc that this L2Spawn can manage.
	 */
	public int getAmount()
	{
		return _maximumCount;
	}

	/**
	 * @return the Identifier of this L2Spwan (used as key in the SpawnTable).
	 */
	public int getId()
	{
		return _id;
	}

	/**
	 * @return the Identifier of the location area where L2Npc can be spwaned.
	 */
	public int getLocation()
	{
		return _location;
	}

	/**
	 * @return the X position of the spwan point.
	 */
	public int getLocx()
	{
		return _locX;
	}

	/**
	 * @return the Y position of the spwan point.
	 */
	public int getLocy()
	{
		return _locY;
	}

	/**
	 * @return the Z position of the spwan point.
	 */
	public int getLocz()
	{
		return _locZ;
	}

	/**
	 * @return the Identifier of the L2Npc manage by this L2Spwan contained in the L2NpcTemplate.
	 */
	public int getNpcid()
	{
		if(_template == null)
			return -1;
		
		return _template.npcId;
	}

	/**
	 * @return the heading of L2Npc when they are spawned.
	 */
	public int getHeading()
	{
		return _heading;
	}

	/**
	 * @return the delay between a L2Npc remove and its re-spawn.
	 */
	public int getRespawnDelay()
	{
		return _respawnDelay;
	}

	/**
	 * @return Min RaidBoss Spawn delay.
	 */
	public int getRespawnMinDelay()
	{
		return _respawnMinDelay;
	}

	/**
	 * @return Max RaidBoss Spawn delay.
	 */
	public int getRespawnMaxDelay()
	{
		return _respawnMaxDelay;
	}

	/**
	 * Set the maximum number of L2Npc that this L2Spawn can manage.
	 * @param amount 
	 */
	public void setAmount(int amount)
	{
		_maximumCount = amount;
	}

	/**
	 * Set the Identifier of this L2Spwan (used as key in the SpawnTable).
	 * @param id 
	 */
	public void setId(int id)
	{
		_id = id;
	}

	/**
	 * Set the Identifier of the location area where L2Npc can be spwaned.
	 * @param location 
	 */
	public void setLocation(int location)
	{
		_location = location;
	}

	/**
	 * Set Minimum Respawn Delay.
	 * @param date 
	 */
	public void setRespawnMinDelay(int date)
	{
		_respawnMinDelay = date;
	}

	/**
	 * Set Maximum Respawn Delay.
	 * @param date 
	 */
	public void setRespawnMaxDelay(int date)
	{
		_respawnMaxDelay = date;
	}

	/**
	 * Set the X position of the spwan point.
	 * @param locx 
	 */
	public void setLocx(int locx)
	{
		_locX = locx;
	}

	/**
	 * Set the Y position of the spwan point.
	 * @param locy 
	 */
	public void setLocy(int locy)
	{
		_locY = locy;
	}

	/**
	 * Set the Z position of the spwan point.
	 * @param locz 
	 */
	public void setLocz(int locz)
	{
		_locZ = locz;
	}

	/**
	 * Set the heading of L2Npc when they are spawned.
	 * @param heading 
	 */
	public void setHeading(int heading)
	{
		_heading = heading;
	}

	/**
	 * Kidzor Set the spawn as custom.
	 * @param custom 
	 */
	public void setCustom(boolean custom)
	{
		_customSpawn = custom;
	}

	/**
	 * Kidzor Return type of spawn.
	 * @return 
	 */
	public boolean isCustom()
	{
		return _customSpawn;
	}

	/** If true then spawn is custom */
	private boolean _customSpawn;
	
	private boolean _customBossInstance = false;
	
	

	/**
	 * @return the _customBossInstance
	 */
	public boolean is_customBossInstance()
	{
		return _customBossInstance;
	}

	/**
	 * @param customBossInstance the _customBossInstance to set
	 */
	public void set_customBossInstance(boolean customBossInstance)
	{
		_customBossInstance = customBossInstance;
	}

	/**
	 * Decrease the current number of L2Npc of this L2Spawn and if necessary create a SpawnTask to launch after
	 * the respawn Delay.<BR>
	 * <BR>
	 * <B><U> Actions</U> :</B><BR>
	 * <BR>
	 * <li>Decrease the current number of L2Npc of this L2Spawn</li> <li>Check if respawn is possible to prevent
	 * multiple respawning caused by lag</li> <li>Update the current number of SpawnTask in progress or stand by of this
	 * L2Spawn</li> <li>Create a new SpawnTask to launch after the respawn Delay</li><BR>
	 * <BR>
	 * <FONT COLOR=#FF0000><B> <U>Caution</U> : A respawn is possible ONLY if _doRespawn=True and _scheduledCount +
	 * _currentCount < _maximumCount</B></FONT><BR>
	 * <BR>
	 * @param oldNpc 
	 */
	public void decreaseCount(/*int npcId*/L2Npc oldNpc)
	{
		// Decrease the current number of L2Npc of this L2Spawn
		_currentCount--;

		// Check if respawn is possible to prevent multiple respawning caused by lag
		if(_doRespawn && _scheduledCount + _currentCount < _maximumCount)
		{
			// Update the current number of SpawnTask in progress or stand by of this L2Spawn
			_scheduledCount++;

			// Create a new SpawnTask to launch after the respawn Delay
			//ClientScheduler.getInstance().scheduleLow(new SpawnTask(npcId), _respawnDelay);
			ThreadPoolManager.getInstance().scheduleGeneral(new SpawnTask(oldNpc), _respawnDelay);
		}
	}

	/**
	 * Create the initial spawning and set _doRespawn to True.<BR>
	 * <BR>
	 * 
	 * @return The number of L2Npc that were spawned
	 */
	public int init()
	{
		while(_currentCount < _maximumCount)
		{
			doSpawn();
		}
		_doRespawn = true;

		return _currentCount;
	}

	/**
	 * Create a L2Npc in this L2Spawn.
	 * @return 
	 */
	public L2Npc spawnOne()
	{
		return doSpawn();
	}

	/**
	 * Set _doRespawn to False to stop respawn in this L2Spawn.
	 */
	public void stopRespawn()
	{
		_doRespawn = false;
	}

	/**
	 * Set _doRespawn to True to start or restart respawn in this L2Spawn.
	 */
	public void startRespawn()
	{
		_doRespawn = true;
	}

	/**
	 * Create the L2Npc, add it to the world and launch its OnSpawn action.<BR>
	 * <BR>
	 * <B><U> Concept</U> :</B><BR>
	 * <BR>
	 * L2Npc can be spawned either in a random position into a location area (if Lox=0 and Locy=0), either at an
	 * exact position. The heading of the L2Npc can be a random heading if not defined (value= -1) or an exact
	 * heading (ex : merchant...).<BR>
	 * <BR>
	 * <B><U> Actions for an random spawn into location area</U> : <I>(if Locx=0 and Locy=0)</I></B><BR>
	 * <BR>
	 * <li>Get L2Npc Init parameters and its generate an Identifier</li> <li>Call the constructor of the
	 * L2Npc</li> <li>Calculate the random position in the location area (if Locx=0 and Locy=0) or get its exact
	 * position from the L2Spawn</li> <li>Set the position of the L2Npc</li> <li>Set the HP and MP of the
	 * L2Npc to the max</li> <li>Set the heading of the L2Npc (random heading if not defined : value=-1)
	 * </li> <li>Link the L2Npc to this L2Spawn</li> <li>Init other values of the L2Npc (ex : from its
	 * L2CharTemplate for INT, STR, DEX...) and add it in the world</li> <li>Lauch the action OnSpawn fo the
	 * L2Npc</li><BR>
	 * <BR>
	 * <li>Increase the current number of L2Npc managed by this L2Spawn</li><BR>
	 * <BR>
	 * @return 
	 */
	public L2Npc doSpawn()
	{
		L2Npc mob = null;
		try
		{
			// Check if the L2Spawn is not a L2Pet or L2Minion spawn
			if(_template.type.equalsIgnoreCase("L2Pet") || _template.type.equalsIgnoreCase("L2Minion"))
			{
				_currentCount++;

				return mob;
			}

			// Get L2Npc Init parameters and its generate an Identifier
			Object[] parameters =
			{
					IdFactory.getInstance().getNextId(), _template
			};

			// Call the constructor of the L2Npc
			// (can be a L2ArtefactInstance, L2FriendlyMobInstance, L2GuardInstance, L2MonsterInstance, L2SiegeGuardInstance, L2BoxInstance,
			// L2FeedableBeastInstance, L2TamedBeastInstance, L2FolkInstance)
			Object tmp = _constructor.newInstance(parameters);

			// Must be done before object is spawned into visible world
			((L2Object) tmp).setInstanceId(_instanceId);

			// Check if the Instance is a L2Npc
			if(!(tmp instanceof L2Npc))
				return mob;

			mob = (L2Npc) tmp;

			return intializeNpcInstance(mob);
		}
		catch(Exception e)
		{
			if(Config.ENABLE_ALL_EXCEPTIONS)
				e.printStackTrace();
			
			_log.log(Level.WARNING, "NPC " + _template.npcId + " class not found", e);
		}
		return mob;
	}

	/**
	 * @param mob
	 * @return
	 */
	private L2Npc intializeNpcInstance(L2Npc mob)
	{
		int newlocx, newlocy, newlocz;

		boolean doCorrect = false;
		if(Config.GEODATA > 0)
		{
			switch(Config.GEO_CORRECT_Z)
			{
			case ALL:
				doCorrect = true;
				break;
			case TOWN:
				if(mob != null)
					doCorrect = true;
				break;
			case MONSTER:
				if(mob instanceof L2Attackable)
					doCorrect = true;
				break;
			}
		}

		// If Locx=0 and Locy=0, the L2Npc must be spawned in an area defined by location
		if(getLocx() == 0 && getLocy() == 0)
		{
			if(getLocation() == 0)
				return mob;

			// Calculate the random position in the location area
			int p[] = TerritoryData.getInstance().getRandomPoint(getLocation());

			// Set the calculated position of the L2Npc
			newlocx = p[0];
			newlocy = p[1];
			newlocz = GeoData.getInstance().getSpawnHeight(newlocx, newlocy, p[2], p[3], _id);
		}
		else
		{
			// The L2Npc is spawned at the exact position (Lox, Locy, Locz)
			newlocx = getLocx();
			newlocy = getLocy();
			newlocz = doCorrect ? GeoData.getInstance().getSpawnHeight(newlocx, newlocy, getLocz(), getLocz(), _id) : getLocz();
		}
		
		if (mob != null)
		{
			mob.stopAllEffects();

			// Set the HP and MP of the L2Npc to the max
			mob.setCurrentHpMp(mob.getMaxHp(), mob.getMaxMp());

			// Set the heading of the L2Npc (random heading if not defined)
			if(getHeading() == -1)
			{
				mob.setHeading(Rnd.nextInt(61794));
			}
			else
			{
				mob.setHeading(getHeading());
			}

			// Reset decay info
			mob.setDecayed(false);

			// Link the L2Npc to this L2Spawn
			mob.setSpawn(this);

			// Init other values of the L2Npc (ex : from its L2CharTemplate for INT, STR, DEX...) and add it in the world as a visible object
			mob.spawnMe(newlocx, newlocy, newlocz);

			L2Spawn.notifyNpcSpawned(mob);

			_lastSpawn = mob;

			if(Config.DEBUG)
			{
				_log.finest("spawned Mob ID: " + _template.npcId + " ,at: " + mob.getX() + " x, " + mob.getY() + " y, " + mob.getZ() + " z");
			}

			for(Quest quest : mob.getTemplate().getEventQuests(Quest.QuestEventType.ON_SPAWN))
			{
				quest.notifySpawn(mob);
			}

			// Increase the current number of L2Npc managed by this L2Spawn
			_currentCount++;
		}
		return mob;
	}

	public static void addSpawnListener(SpawnListener listener)
	{
		synchronized (_spawnListeners)
		{
			_spawnListeners.add(listener);
		}
	}

	public static void removeSpawnListener(SpawnListener listener)
	{
		synchronized (_spawnListeners)
		{
			_spawnListeners.remove(listener);
		}
	}

	public static void notifyNpcSpawned(L2Npc npc)
	{
		synchronized (_spawnListeners)
		{
			for(SpawnListener listener : _spawnListeners)
			{
				listener.npcSpawned(npc);
			}
		}
	}

	/**
	 * @param i delay in seconds
	 */
	public void setRespawnDelay(int i)
	{
		if(i < 0)
		{
			_log.warning("respawn delay is negative for spawnId:" + _id);
		}

		if(i < 10)
		{
			i = 10;
		}

		_respawnDelay = i * 1000;
	}

	public L2Npc getLastSpawn()
	{
		return _lastSpawn;
	}

	/**
	 * @param oldNpc
	 */
	public void respawnNpc(L2Npc oldNpc)
	{
		oldNpc.refreshID();
		/*L2Npc instance = */intializeNpcInstance(oldNpc);
	}

	public L2NpcTemplate getTemplate()
	{
		return _template;
	}

	public int getInstanceId()
	{
		return _instanceId;
	}

	public void setInstanceId(int instanceId)
	{
		_instanceId = instanceId;
	}

}
