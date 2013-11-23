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
package ai.individual;

import ai.L2AttackableAIScript;

import java.util.ArrayList;
import java.util.List;

import net.xcine.Config;
import net.xcine.gameserver.instancemanager.GrandBossManager;
import net.xcine.gameserver.model.actor.L2Attackable;
import net.xcine.gameserver.model.actor.L2Npc;
import net.xcine.gameserver.model.actor.instance.L2GrandBossInstance;
import net.xcine.gameserver.model.actor.instance.L2PcInstance;
import net.xcine.gameserver.network.serverpackets.PlaySound;
import net.xcine.gameserver.templates.StatsSet;
import net.xcine.util.Rnd;

/**
 * Core AI
 * @author DrLecter Revised By Emperorc
 */
public class Core extends L2AttackableAIScript
{
	private static final int CORE = 29006;
	private static final int DEATH_KNIGHT = 29007;
	private static final int DOOM_WRAITH = 29008;
	// private static final int DICOR = 29009;
	// private static final int VALIDUS = 29010;
	private static final int SUSCEPTOR = 29011;
	// private static final int PERUM = 29012;
	// private static final int PREMO = 29013;
	
	// Status Tracking
	private static final byte ALIVE = 0; // Core is spawned.
	private static final byte DEAD = 1; // Core has been killed.
	
	private static boolean _FirstAttacked;
	
	List<L2Attackable> Minions = new ArrayList<>();
	
	public Core(int id, String name, String descr)
	{
		super(id, name, descr);
		
		int[] mobs =
		{
			CORE,
			DEATH_KNIGHT,
			DOOM_WRAITH,
			SUSCEPTOR
		};
		registerMobs(mobs);
		
		_FirstAttacked = false;
		StatsSet info = GrandBossManager.getStatsSet(CORE);
		int status = GrandBossManager.getBossStatus(CORE);
		if (status == DEAD)
		{
			// load the unlock date and time for Core from DB
			long temp = (info.getLong("respawn_time") - System.currentTimeMillis());
			// if Core is locked until a certain time, mark it so and start the unlock timer
			// the unlock time has not yet expired.
			if (temp > 0)
				startQuestTimer("core_unlock", temp, null, null, false);
			else
			{
				// the time has already expired while the server was offline. Immediately spawn Core.
				L2GrandBossInstance core = (L2GrandBossInstance) addSpawn(CORE, 17726, 108915, -6480, 0, false, 0, false);
				GrandBossManager.setBossStatus(CORE, ALIVE);
				spawnBoss(core);
			}
		}
		else
		{
			String test = getGlobalQuestVar("Core_Attacked");
			if (test.equalsIgnoreCase("true"))
				_FirstAttacked = true;
			
			int loc_x = info.getInteger("loc_x");
			int loc_y = info.getInteger("loc_y");
			int loc_z = info.getInteger("loc_z");
			int heading = info.getInteger("heading");
			int hp = info.getInteger("currentHP");
			int mp = info.getInteger("currentMP");
			L2GrandBossInstance core = (L2GrandBossInstance) addSpawn(CORE, loc_x, loc_y, loc_z, heading, false, 0, false);
			core.setCurrentHpMp(hp, mp);
			spawnBoss(core);
		}
	}
	
	@Override
	public void saveGlobalData()
	{
		String val = "" + _FirstAttacked;
		setGlobalQuestVar("Core_Attacked", val);
	}
	
	public void spawnBoss(L2GrandBossInstance npc)
	{
		GrandBossManager.addBoss(npc);
		npc.broadcastPacket(new PlaySound(1, "BS01_A", 1, npc.getObjectId(), npc.getX(), npc.getY(), npc.getZ()));
		
		// Spawn minions
		L2Attackable mob;
		for (int i = 0; i < 5; i++)
		{
			int x = 16800 + i * 360;
			mob = (L2Attackable) addSpawn(DEATH_KNIGHT, x, 110000, npc.getZ(), 280 + Rnd.get(40), false, 0, false);
			mob.setIsRaidMinion(true);
			Minions.add(mob);
			mob = (L2Attackable) addSpawn(DEATH_KNIGHT, x, 109000, npc.getZ(), 280 + Rnd.get(40), false, 0, false);
			mob.setIsRaidMinion(true);
			Minions.add(mob);
			int x2 = 16800 + i * 600;
			mob = (L2Attackable) addSpawn(DOOM_WRAITH, x2, 109300, npc.getZ(), 280 + Rnd.get(40), false, 0, false);
			mob.setIsRaidMinion(true);
			Minions.add(mob);
		}
		
		for (int i = 0; i < 4; i++)
		{
			int x = 16800 + i * 450;
			mob = (L2Attackable) addSpawn(SUSCEPTOR, x, 110300, npc.getZ(), 280 + Rnd.get(40), false, 0, false);
			mob.setIsRaidMinion(true);
			Minions.add(mob);
		}
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		if (event.equalsIgnoreCase("core_unlock"))
		{
			L2GrandBossInstance core = (L2GrandBossInstance) addSpawn(CORE, 17726, 108915, -6480, 0, false, 0, false);
			GrandBossManager.setBossStatus(CORE, ALIVE);
			spawnBoss(core);
		}
		else if (event.equalsIgnoreCase("spawn_minion"))
		{
			L2Attackable mob = (L2Attackable) addSpawn(npc.getNpcId(), npc.getX(), npc.getY(), npc.getZ(), npc.getHeading(), false, 0, false);
			mob.setIsRaidMinion(true);
			Minions.add(mob);
		}
		else if (event.equalsIgnoreCase("despawn_minions"))
		{
			for (int i = 0; i < Minions.size(); i++)
			{
				L2Attackable mob = Minions.get(i);
				if (mob != null)
					mob.decayMe();
			}
			Minions.clear();
		}
		return super.onAdvEvent(event, npc, player);
	}
	
	@Override
	public String onAttack(L2Npc npc, L2PcInstance attacker, int damage, boolean isPet)
	{
		if (npc.getNpcId() == CORE)
		{
			if (_FirstAttacked)
			{
				if (Rnd.get(100) == 0)
					npc.broadcastNpcSay("Removing intruders.");
			}
			else
			{
				_FirstAttacked = true;
				npc.broadcastNpcSay("A non-permitted target has been discovered.");
				npc.broadcastNpcSay("Starting intruder removal system.");
			}
		}
		return super.onAttack(npc, attacker, damage, isPet);
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isPet)
	{
		int npcId = npc.getNpcId();
		if (npcId == CORE)
		{
			npc.broadcastPacket(new PlaySound(1, "BS02_D", 1, npc.getObjectId(), npc.getX(), npc.getY(), npc.getZ()));
			npc.broadcastNpcSay("A fatal error has occurred.");
			npc.broadcastNpcSay("System is being shut down...");
			npc.broadcastNpcSay("......");
			
			_FirstAttacked = false;
			
			addSpawn(31842, 16502, 110165, -6394, 0, false, 900000, false);
			addSpawn(31842, 18948, 110166, -6397, 0, false, 900000, false);
			GrandBossManager.setBossStatus(CORE, DEAD);
			
			// time is 60hour +/- 23hour
			long respawnTime = (long) Config.SPAWN_INTERVAL_CORE + Rnd.get(Config.RANDOM_SPAWN_TIME_CORE);
			startQuestTimer("core_unlock", respawnTime, null, null, false);
			
			// also save the respawn time so that the info is maintained past reboots
			StatsSet info = GrandBossManager.getStatsSet(CORE);
			info.set("respawn_time", (System.currentTimeMillis() + respawnTime));
			GrandBossManager.setStatsSet(CORE, info);
			startQuestTimer("despawn_minions", 20000, null, null, false);
			cancelQuestTimers("spawn_minion");
		}
		else if (GrandBossManager.getBossStatus(CORE) == ALIVE && Minions != null && Minions.contains(npc))
		{
			Minions.remove(npc);
			startQuestTimer("spawn_minion", 60000, npc, null, false);
		}
		return super.onKill(npc, killer, isPet);
	}
	
	public static void main(String[] args)
	{
		// now call the constructor (starts up the ai)
		new Core(-1, "core", "ai");
	}
}