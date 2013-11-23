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
import net.xcine.gameserver.GeoData;
import net.xcine.gameserver.ai.CtrlIntention;
import net.xcine.gameserver.datatables.SkillTable;
import net.xcine.gameserver.instancemanager.GrandBossManager;
import net.xcine.gameserver.model.L2CharPosition;
import net.xcine.gameserver.model.L2Skill;
import net.xcine.gameserver.model.actor.L2Npc;
import net.xcine.gameserver.model.actor.L2Playable;
import net.xcine.gameserver.model.actor.instance.L2GrandBossInstance;
import net.xcine.gameserver.model.actor.instance.L2PcInstance;
import net.xcine.gameserver.model.actor.instance.L2PetInstance;
import net.xcine.gameserver.model.zone.type.L2BossZone;
import net.xcine.gameserver.network.serverpackets.PlaySound;
import net.xcine.gameserver.network.serverpackets.SocialAction;
import net.xcine.gameserver.network.serverpackets.SpecialCamera;
import net.xcine.gameserver.templates.StatsSet;
import net.xcine.gameserver.util.Util;
import net.xcine.util.Rnd;

public class Valakas extends L2AttackableAIScript
{
	private long _timeTracker = 0; // Time tracker for last attack on Valakas.
	private L2Playable _actualVictim; // Actual target of Valakas.
	
	private static final int VALAKAS = 29028;
	
	// Valakas status.
	private static final byte DORMANT = 0; // Valakas is spawned and no one has entered yet. Entry is unlocked.
	private static final byte WAITING = 1; // Valakas is spawned and someone has entered, triggering a 30 minute window for additional people to enter. Entry is unlocked.
	private static final byte FIGHTING = 2; // Valakas is engaged in battle, annihilating his foes. Entry is locked.
	private static final byte DEAD = 3; // Valakas has been killed. Entry is locked.
	
	private static final int[] VALAKAS_REGULAR_SKILLS =
	{
		4681,
		4682,
		4683,
		4689
	};
	
	private static final int[] VALAKAS_LOWHP_SKILLS =
	{
		4681,
		4682,
		4683,
		4689,
		4690
	};
	
	private static final int[] VALAKAS_AOE_SKILLS =
	{
		4683,
		4684,
		4685,
		4686,
		4688,
		4689,
		4690
	};
	
	private static final int _teleportCubeLocation[][] =
	{
		{
			214880,
			-116144,
			-1644
		},
		{
			213696,
			-116592,
			-1644
		},
		{
			212112,
			-116688,
			-1644
		},
		{
			211184,
			-115472,
			-1664
		},
		{
			210336,
			-114592,
			-1644
		},
		{
			211360,
			-113904,
			-1644
		},
		{
			213152,
			-112352,
			-1644
		},
		{
			214032,
			-113232,
			-1644
		},
		{
			214752,
			-114592,
			-1644
		},
		{
			209824,
			-115568,
			-1421
		},
		{
			210528,
			-112192,
			-1403
		},
		{
			213120,
			-111136,
			-1408
		},
		{
			215184,
			-111504,
			-1392
		},
		{
			215456,
			-117328,
			-1392
		},
		{
			213200,
			-118160,
			-1424
		}
	};
	
	private static L2BossZone _Zone;
	
	public Valakas(int questId, String name, String descr)
	{
		super(questId, name, descr);
		
		int[] mob =
		{
			VALAKAS
		};
		registerMobs(mob);
		
		_Zone = GrandBossManager.getZoneByXYZ(212852, -114842, -1632);
		
		final StatsSet info = GrandBossManager.getStatsSet(VALAKAS);
		final int status = GrandBossManager.getBossStatus(VALAKAS);
		
		if (status == DEAD)
		{
			// load the unlock date and time for valakas from DB
			long temp = (info.getLong("respawn_time") - System.currentTimeMillis());
			if (temp > 0)
			{
				// The time has not yet expired. Mark Valakas as currently locked (dead).
				startQuestTimer("valakas_unlock", temp, null, null, false);
			}
			else
			{
				// The time has expired while the server was offline. Spawn valakas in his cave as DORMANT.
				final L2Npc valakas = addSpawn(VALAKAS, -105200, -253104, -15264, 0, false, 0, false);
				GrandBossManager.setBossStatus(VALAKAS, DORMANT);
				GrandBossManager.addBoss((L2GrandBossInstance) valakas);
				
				valakas.setIsInvul(true);
				valakas.setRunning();
				
				valakas.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
			}
		}
		else
		{
			final int loc_x = info.getInteger("loc_x");
			final int loc_y = info.getInteger("loc_y");
			final int loc_z = info.getInteger("loc_z");
			final int heading = info.getInteger("heading");
			final int hp = info.getInteger("currentHP");
			final int mp = info.getInteger("currentMP");
			
			final L2Npc valakas = addSpawn(VALAKAS, loc_x, loc_y, loc_z, heading, false, 0, false);
			GrandBossManager.addBoss((L2GrandBossInstance) valakas);
			
			valakas.setCurrentHpMp(hp, mp);
			valakas.setRunning();
			
			// Start timers.
			if (status == FIGHTING)
			{
				// stores current time for inactivity task.
				_timeTracker = System.currentTimeMillis();
				
				startQuestTimer("regen_task", 60000, valakas, null, true);
				startQuestTimer("skill_task", 2000, valakas, null, true);
			}
			else
			{
				valakas.setIsInvul(true);
				valakas.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
				
				// Start timer to lock entry after 30 minutes
				if (status == WAITING)
					startQuestTimer("beginning", Config.WAIT_TIME_VALAKAS, valakas, null, false);
			}
		}
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		if (npc != null)
		{
			if (event.equalsIgnoreCase("beginning"))
			{
				// Stores current time
				_timeTracker = System.currentTimeMillis();
				
				// Teleport Valakas to his lair.
				npc.teleToLocation(212852, -114842, -1632, 0);
				
				// Sound + socialAction.
				for (L2PcInstance plyr : _Zone.getPlayersInside())
				{
					plyr.sendPacket(new PlaySound(1, "B03_A", 0, 0, 0, 0, 0));
					plyr.sendPacket(new SocialAction(npc, 3));
				}
				
				// Launch the cinematic, and tasks (regen + skill).
				startQuestTimer("spawn_1", 1700, npc, null, false); // 1700
				startQuestTimer("spawn_2", 3200, npc, null, false); // 1500
				startQuestTimer("spawn_3", 6500, npc, null, false); // 3300
				startQuestTimer("spawn_4", 9400, npc, null, false); // 2900
				startQuestTimer("spawn_5", 12100, npc, null, false); // 2700
				startQuestTimer("spawn_6", 12430, npc, null, false); // 330
				startQuestTimer("spawn_7", 15430, npc, null, false); // 3000
				startQuestTimer("spawn_8", 16830, npc, null, false); // 1400
				startQuestTimer("spawn_9", 23530, npc, null, false); // 6700 - end of cinematic
				startQuestTimer("spawn_10", 26000, npc, null, false); // 2500 - AI + unlock
			}
			// Regeneration && inactivity task
			else if (event.equalsIgnoreCase("regen_task"))
			{
				// Inactivity task - 15min
				if (GrandBossManager.getBossStatus(VALAKAS) == FIGHTING)
				{
					if (_timeTracker + 900000 < System.currentTimeMillis())
					{
						npc.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
						npc.teleToLocation(-105200, -253104, -15264, 0);
						
						GrandBossManager.setBossStatus(VALAKAS, DORMANT);
						npc.setCurrentHpMp(npc.getMaxHp(), npc.getMaxMp());
						
						// Drop all players from the zone.
						_Zone.oustAllPlayers();
						
						// Cancel skill_task and regen_task.
						cancelQuestTimer("regen_task", npc, null);
						cancelQuestTimer("skill_task", npc, null);
						return null;
					}
				}
				
				// Regeneration buff.
				if (Rnd.get(30) == 0)
				{
					L2Skill skillRegen;
					
					// Current HPs are inferior to 25% ; apply lvl 4 of regen skill.
					if (npc.getCurrentHp() < (npc.getMaxHp() / 4))
						skillRegen = SkillTable.getInstance().getInfo(4691, 4);
					// Current HPs are inferior to 50% ; apply lvl 3 of regen skill.
					else if (npc.getCurrentHp() < ((npc.getMaxHp() * 2) / 4.0))
						skillRegen = SkillTable.getInstance().getInfo(4691, 3);
					// Current HPs are inferior to 75% ; apply lvl 2 of regen skill.
					else if (npc.getCurrentHp() < ((npc.getMaxHp() * 3) / 4.0))
						skillRegen = SkillTable.getInstance().getInfo(4691, 2);
					else
						skillRegen = SkillTable.getInstance().getInfo(4691, 1);
					
					skillRegen.getEffects(npc, npc);
				}
			}
			// Spawn cinematic, regen_task and choose of skill.
			else if (event.equalsIgnoreCase("spawn_1"))
				_Zone.broadcastPacket(new SpecialCamera(npc.getObjectId(), 1800, 180, -1, 1500, 10000, 0, 0, 1, 0));
			else if (event.equalsIgnoreCase("spawn_2"))
				_Zone.broadcastPacket(new SpecialCamera(npc.getObjectId(), 1300, 180, -5, 3000, 10000, 0, -5, 1, 0));
			else if (event.equalsIgnoreCase("spawn_3"))
				_Zone.broadcastPacket(new SpecialCamera(npc.getObjectId(), 500, 180, -8, 600, 10000, 0, 60, 1, 0));
			else if (event.equalsIgnoreCase("spawn_4"))
				_Zone.broadcastPacket(new SpecialCamera(npc.getObjectId(), 800, 180, -8, 2700, 10000, 0, 30, 1, 0));
			else if (event.equalsIgnoreCase("spawn_5"))
				_Zone.broadcastPacket(new SpecialCamera(npc.getObjectId(), 200, 250, 70, 0, 10000, 30, 80, 1, 0));
			else if (event.equalsIgnoreCase("spawn_6"))
				_Zone.broadcastPacket(new SpecialCamera(npc.getObjectId(), 1100, 250, 70, 2500, 10000, 30, 80, 1, 0));
			else if (event.equalsIgnoreCase("spawn_7"))
				_Zone.broadcastPacket(new SpecialCamera(npc.getObjectId(), 700, 150, 30, 0, 10000, -10, 60, 1, 0));
			else if (event.equalsIgnoreCase("spawn_8"))
				_Zone.broadcastPacket(new SpecialCamera(npc.getObjectId(), 1200, 150, 20, 2900, 10000, -10, 30, 1, 0));
			else if (event.equalsIgnoreCase("spawn_9"))
				_Zone.broadcastPacket(new SpecialCamera(npc.getObjectId(), 750, 170, -10, 3400, 4000, 10, -15, 1, 0));
			else if (event.equalsIgnoreCase("spawn_10"))
			{
				GrandBossManager.setBossStatus(VALAKAS, FIGHTING);
				npc.setIsInvul(false);
				
				startQuestTimer("regen_task", 60000, npc, null, true);
				startQuestTimer("skill_task", 2000, npc, null, true);
			}
			// Death cinematic, spawn of Teleport Cubes.
			else if (event.equalsIgnoreCase("die_1"))
				_Zone.broadcastPacket(new SpecialCamera(npc.getObjectId(), 2000, 130, -1, 0, 10000, 0, 0, 1, 1));
			else if (event.equalsIgnoreCase("die_2"))
				_Zone.broadcastPacket(new SpecialCamera(npc.getObjectId(), 1100, 210, -5, 3000, 10000, -13, 0, 1, 1));
			else if (event.equalsIgnoreCase("die_3"))
				_Zone.broadcastPacket(new SpecialCamera(npc.getObjectId(), 1300, 200, -8, 3000, 10000, 0, 15, 1, 1));
			else if (event.equalsIgnoreCase("die_4"))
				_Zone.broadcastPacket(new SpecialCamera(npc.getObjectId(), 1000, 190, 0, 500, 10000, 0, 10, 1, 1));
			else if (event.equalsIgnoreCase("die_5"))
				_Zone.broadcastPacket(new SpecialCamera(npc.getObjectId(), 1700, 120, 0, 2500, 10000, 12, 40, 1, 1));
			else if (event.equalsIgnoreCase("die_6"))
				_Zone.broadcastPacket(new SpecialCamera(npc.getObjectId(), 1700, 20, 0, 700, 10000, 10, 10, 1, 1));
			else if (event.equalsIgnoreCase("die_7"))
				_Zone.broadcastPacket(new SpecialCamera(npc.getObjectId(), 1700, 10, 0, 1000, 10000, 20, 70, 1, 1));
			else if (event.equalsIgnoreCase("die_8"))
			{
				_Zone.broadcastPacket(new SpecialCamera(npc.getObjectId(), 1700, 10, 0, 300, 250, 20, -20, 1, 1));
				
				for (int[] element : _teleportCubeLocation)
					addSpawn(31759, element[0], element[1], element[2], 0, false, 900000, false);
				
				startQuestTimer("remove_players", 900000, null, null, false);
			}
			else if (event.equalsIgnoreCase("skill_task"))
				callSkillAI(npc);
		}
		else
		{
			if (event.equalsIgnoreCase("valakas_unlock"))
			{
				final L2Npc valakas = addSpawn(VALAKAS, -105200, -253104, -15264, 32768, false, 0, false);
				GrandBossManager.addBoss((L2GrandBossInstance) valakas);
				GrandBossManager.setBossStatus(VALAKAS, DORMANT);
			}
			else if (event.equalsIgnoreCase("remove_players"))
				_Zone.oustAllPlayers();
		}
		return super.onAdvEvent(event, npc, player);
	}
	
	@Override
	public String onSpawn(L2Npc npc)
	{
		npc.disableCoreAI(true);
		return super.onSpawn(npc);
	}
	
	@Override
	public String onAttack(L2Npc npc, L2PcInstance attacker, int damage, boolean isPet)
	{
		if (!_Zone.isInsideZone(attacker))
		{
			attacker.doDie(attacker);
			return null;
		}
		
		if (npc.isInvul())
			return null;
		
		if (GrandBossManager.getBossStatus(VALAKAS) != FIGHTING)
		{
			attacker.teleToLocation(150037, -57255, -2976, 0);
			return null;
		}
		
		// Debuff strider-mounted players.
		if (attacker.getMountType() == 1)
		{
			final L2Skill skill = SkillTable.getInstance().getInfo(4258, 1);
			if (attacker.getFirstEffect(skill) == null)
			{
				npc.setTarget(attacker);
				npc.doCast(skill);
			}
		}
		_timeTracker = System.currentTimeMillis();
		
		return super.onAttack(npc, attacker, damage, isPet);
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isPet)
	{
		// Cancel skill_task and regen_task.
		cancelQuestTimer("regen_task", npc, null);
		cancelQuestTimer("skill_task", npc, null);
		
		// Launch death animation.
		_Zone.broadcastPacket(new PlaySound(1, "B03_D", 0, 0, 0, 0, 0));
		
		startQuestTimer("die_1", 300, npc, null, false); // 300
		startQuestTimer("die_2", 600, npc, null, false); // 300
		startQuestTimer("die_3", 3800, npc, null, false); // 3200
		startQuestTimer("die_4", 8200, npc, null, false); // 4400
		startQuestTimer("die_5", 8700, npc, null, false); // 500
		startQuestTimer("die_6", 13300, npc, null, false); // 4600
		startQuestTimer("die_7", 14000, npc, null, false); // 700
		startQuestTimer("die_8", 16500, npc, null, false); // 2500
		
		GrandBossManager.setBossStatus(VALAKAS, DEAD);
		
		long respawnTime = (long) Config.SPAWN_INTERVAL_VALAKAS + Rnd.get(Config.RANDOM_SPAWN_TIME_VALAKAS);
		startQuestTimer("valakas_unlock", respawnTime, null, null, false);
		
		// also save the respawn time so that the info is maintained past reboots
		StatsSet info = GrandBossManager.getStatsSet(VALAKAS);
		info.set("respawn_time", (System.currentTimeMillis() + respawnTime));
		GrandBossManager.setStatsSet(VALAKAS, info);
		
		return super.onKill(npc, killer, isPet);
	}
	
	@Override
	public String onAggroRangeEnter(L2Npc npc, L2PcInstance player, boolean isPet)
	{
		return null;
	}
	
	private void callSkillAI(L2Npc npc)
	{
		if (npc.isInvul() || npc.isCastingNow())
			return;
		
		// Pickup a target if no or dead victim. 10% luck he decides to reconsiders his target.
		if (_actualVictim == null || _actualVictim.isDead() || !(npc.getKnownList().knowsObject(_actualVictim)) || Rnd.get(10) == 0)
			_actualVictim = getRandomTarget(npc);
		
		// If result is still null, Valakas will roam. Don't go deeper in skill AI.
		if (_actualVictim == null)
		{
			if (Rnd.get(10) == 0)
			{
				int x = npc.getX();
				int y = npc.getY();
				int z = npc.getZ();
				
				int posX = x + Rnd.get(-1400, 1400);
				int posY = y + Rnd.get(-1400, 1400);
				
				if (GeoData.getInstance().canMoveFromToTarget(x, y, z, posX, posY, z))
					npc.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, new L2CharPosition(posX, posY, z, 0));
			}
			return;
		}
		
		final L2Skill skill = SkillTable.getInstance().getInfo(getRandomSkill(npc), 1);
		
		// Cast the skill or follow the target.
		if (Util.checkIfInRange((skill.getCastRange() < 600) ? 600 : skill.getCastRange(), npc, _actualVictim, true))
		{
			npc.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
			npc.setIsCastingNow(true);
			npc.setTarget(_actualVictim);
			npc.doCast(skill);
		}
		else
		{
			npc.getAI().setIntention(CtrlIntention.AI_INTENTION_FOLLOW, _actualVictim, null);
			npc.setIsCastingNow(false);
		}
	}
	
	/**
	 * Pick a random skill.<br>
	 * Valakas will mostly use utility skills. If Valakas feels surrounded, he will use AoE skills.<br>
	 * Lower than 50% HPs, he will begin to use Meteor skill.
	 * @param npc valakas
	 * @return a usable skillId
	 */
	private int getRandomSkill(L2Npc npc)
	{
		final int hpRatio = (int) (npc.getCurrentHp() / npc.getMaxHp() * 100);
		
		// Valakas Lava Skin is prioritary.
		if (hpRatio < 75 && Rnd.get(150) == 0 && npc.getFirstEffect(4680) == null)
			return 4680;
		
		// Valakas will use mass spells if he feels surrounded.
		if (Util.getPlayersCountInRadius(1200, npc, false) >= 20)
			return VALAKAS_AOE_SKILLS[Rnd.get(7)];
		
		if (hpRatio > 50)
			return VALAKAS_REGULAR_SKILLS[Rnd.get(4)];
		
		return VALAKAS_LOWHP_SKILLS[Rnd.get(5)];
	}
	
	/**
	 * Pickup a random L2Playable from the zone, deads targets aren't included.
	 * @return a random L2Playable.
	 */
	private L2Playable getRandomTarget(L2Npc npc)
	{
		List<L2Playable> result = new ArrayList<>();
		
		for (L2Playable obj : npc.getKnownList().getKnownType(L2Playable.class))
		{
			if (obj instanceof L2PetInstance || obj.isDead())
				continue;
			
			result.add(obj);
		}
		
		return (result.isEmpty()) ? null : result.get(Rnd.get(result.size()));
	}
	
	public static void main(String[] args)
	{
		new Valakas(-1, "valakas", "ai");
	}
}