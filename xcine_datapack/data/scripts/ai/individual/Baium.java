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
import net.xcine.gameserver.ThreadPoolManager;
import net.xcine.gameserver.ai.CtrlIntention;
import net.xcine.gameserver.datatables.SkillTable;
import net.xcine.gameserver.instancemanager.GrandBossManager;
import net.xcine.gameserver.model.L2Skill;
import net.xcine.gameserver.model.actor.L2Attackable;
import net.xcine.gameserver.model.actor.L2Character;
import net.xcine.gameserver.model.actor.L2Npc;
import net.xcine.gameserver.model.actor.L2Playable;
import net.xcine.gameserver.model.actor.instance.L2GrandBossInstance;
import net.xcine.gameserver.model.actor.instance.L2MonsterInstance;
import net.xcine.gameserver.model.actor.instance.L2PcInstance;
import net.xcine.gameserver.model.actor.instance.L2PetInstance;
import net.xcine.gameserver.model.quest.QuestEventType;
import net.xcine.gameserver.model.zone.type.L2BossZone;
import net.xcine.gameserver.network.serverpackets.Earthquake;
import net.xcine.gameserver.network.serverpackets.PlaySound;
import net.xcine.gameserver.network.serverpackets.SocialAction;
import net.xcine.gameserver.templates.StatsSet;
import net.xcine.gameserver.util.Util;
import net.xcine.util.Rnd;

/**
 * Following animations are handled in that time tempo :
 * <ul>
 * <li>wake(2), 0-13 secs</li>
 * <li>neck(3), 14-24 secs.</li>
 * <li>roar(1), 25-37 secs.</li>
 * </ul>
 * Waker's sacrifice is handled between neck and roar animation.
 */
public class Baium extends L2AttackableAIScript
{
	private L2Character _actualVictim;
	private L2PcInstance _waker;
	
	private static final int STONE_BAIUM = 29025;
	private static final int LIVE_BAIUM = 29020;
	private static final int ARCHANGEL = 29021;
	
	// Baium status tracking
	public static final byte ASLEEP = 0; // baium is in the stone version, waiting to be woken up. Entry is unlocked.
	public static final byte AWAKE = 1; // baium is awake and fighting. Entry is locked.
	public static final byte DEAD = 2; // baium has been killed and has not yet spawned. Entry is locked.
	
	// Archangels spawns
	private final static int ANGEL_LOCATION[][] =
	{
		{
			114239,
			17168,
			10080,
			63544
		},
		{
			115780,
			15564,
			10080,
			13620
		},
		{
			114880,
			16236,
			10080,
			5400
		},
		{
			115168,
			17200,
			10080,
			0
		},
		{
			115792,
			16608,
			10080,
			0
		},
	};
	
	private long _LastAttackVsBaiumTime = 0;
	private final List<L2Npc> _Minions = new ArrayList<>(5);
	private L2BossZone _Zone;
	
	public Baium(int questId, String name, String descr)
	{
		super(questId, name, descr);
		
		int[] mob =
		{
			LIVE_BAIUM
		};
		
		// Quest NPC starter initialization
		addStartNpc(STONE_BAIUM);
		addTalkId(STONE_BAIUM);
		
		// Baium onAttack, onKill, onSpawn
		registerMobs(mob, QuestEventType.ON_ATTACK, QuestEventType.ON_KILL, QuestEventType.ON_SPAWN);
		
		_Zone = GrandBossManager.getZoneByXYZ(113100, 14500, 10077);
		
		final StatsSet info = GrandBossManager.getStatsSet(LIVE_BAIUM);
		final int status = GrandBossManager.getBossStatus(LIVE_BAIUM);
		
		if (status == DEAD)
		{
			// load the unlock date and time for baium from DB
			long temp = (info.getLong("respawn_time") - System.currentTimeMillis());
			if (temp > 0)
			{
				// The time has not yet expired. Mark Baium as currently locked (dead).
				startQuestTimer("baium_unlock", temp, null, null, false);
			}
			else
			{
				// The time has expired while the server was offline. Spawn the stone-baium as ASLEEP.
				addSpawn(STONE_BAIUM, 116033, 17447, 10104, 40188, false, 0, false);
				GrandBossManager.setBossStatus(LIVE_BAIUM, ASLEEP);
			}
		}
		else if (status == AWAKE)
		{
			final int loc_x = info.getInteger("loc_x");
			final int loc_y = info.getInteger("loc_y");
			final int loc_z = info.getInteger("loc_z");
			final int heading = info.getInteger("heading");
			final int hp = info.getInteger("currentHP");
			final int mp = info.getInteger("currentMP");
			
			final L2Npc baium = addSpawn(LIVE_BAIUM, loc_x, loc_y, loc_z, heading, false, 0, false);
			GrandBossManager.addBoss((L2GrandBossInstance) baium);
			
			baium.setCurrentHpMp(hp, mp);
			baium.setRunning();
			
			// start monitoring baium's inactivity
			_LastAttackVsBaiumTime = System.currentTimeMillis();
			startQuestTimer("baium_despawn", 60000, baium, null, true);
			startQuestTimer("skill_range", 2000, baium, null, true);
			
			// Spawns angels
			for (int[] element : ANGEL_LOCATION)
			{
				L2Npc angel = addSpawn(ARCHANGEL, element[0], element[1], element[2], element[3], false, 0, true);
				((L2Attackable) angel).setIsRaidMinion(true);
				angel.setRunning();
				_Minions.add(angel);
			}
			
			// Angels AI
			startQuestTimer("angels_aggro_reconsider", 5000, null, null, true);
		}
		else
			addSpawn(STONE_BAIUM, 116033, 17447, 10104, 40188, false, 0, false);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		if (npc != null && npc.getNpcId() == LIVE_BAIUM)
		{
			if (event.equalsIgnoreCase("skill_range"))
			{
				callSkillAI(npc);
			}
			else if (event.equalsIgnoreCase("baium_neck"))
			{
				npc.broadcastPacket(new SocialAction(npc, 3));
			}
			else if (event.equalsIgnoreCase("sacrifice_waker"))
			{
				if (_waker != null)
				{
					// If player is far of Baium, teleport him back.
					if (!Util.checkIfInShortRadius(300, _waker, npc, true))
						_waker.teleToLocation(115929, 17349, 10077, 0);
					
					// 60% to die.
					if (Rnd.get(100) < 60)
						_waker.doDie(npc);
				}
			}
			else if (event.equalsIgnoreCase("baium_roar"))
			{
				// Roar animation
				npc.broadcastPacket(new SocialAction(npc, 1));
				
				// Spawn angels
				for (int[] element : ANGEL_LOCATION)
				{
					L2Npc angel = addSpawn(ARCHANGEL, element[0], element[1], element[2], element[3], false, 0, true);
					((L2Attackable) angel).setIsRaidMinion(true);
					angel.setRunning();
					_Minions.add(angel);
				}
				
				// Angels AI
				startQuestTimer("angels_aggro_reconsider", 5000, null, null, true);
			}
			// despawn the live baium after 30 minutes of inactivity
			// also check if the players are cheating, having pulled Baium outside his zone...
			else if (event.equalsIgnoreCase("baium_despawn"))
			{
				// just in case the zone reference has been lost (somehow...), restore the reference
				if (_Zone == null)
					_Zone = GrandBossManager.getZoneByXYZ(113100, 14500, 10077);
				
				if (_LastAttackVsBaiumTime + 1800000 < System.currentTimeMillis())
				{
					// despawn the live-baium
					npc.deleteMe();
					
					// Unspawn angels
					for (L2Npc minion : _Minions)
					{
						if (minion != null)
						{
							minion.getSpawn().stopRespawn();
							minion.deleteMe();
						}
					}
					_Minions.clear();
					
					addSpawn(STONE_BAIUM, 116033, 17447, 10104, 40188, false, 0, false); // spawn stone-baium
					GrandBossManager.setBossStatus(LIVE_BAIUM, ASLEEP); // Baium isn't awaken anymore
					_Zone.oustAllPlayers();
					cancelQuestTimer("baium_despawn", npc, null);
				}
				else if ((_LastAttackVsBaiumTime + 300000 < System.currentTimeMillis()) && npc.getCurrentHp() < ((npc.getMaxHp() * 3) / 4.0))
				{
					npc.setIsCastingNow(false);
					npc.setTarget(npc);
					L2Skill skill = SkillTable.getInstance().getInfo(4135, 1);
					npc.doCast(skill);
					npc.setIsCastingNow(true);
				}
				else if (!_Zone.isInsideZone(npc))
					npc.teleToLocation(116033, 17447, 10104, 0);
			}
		}
		else if (event.equalsIgnoreCase("baium_unlock"))
		{
			GrandBossManager.setBossStatus(LIVE_BAIUM, ASLEEP);
			addSpawn(STONE_BAIUM, 116033, 17447, 10104, 40188, false, 0, false);
		}
		else if (event.equalsIgnoreCase("angels_aggro_reconsider"))
		{
			boolean updateTarget = false; // Update or no the target
			
			for (L2Npc minion : _Minions)
			{
				L2Attackable angel = ((L2Attackable) minion);
				if (angel == null)
					continue;
				
				L2Character victim = angel.getMostHated();
				
				if (Rnd.get(100) < 10) // Chaos time
					updateTarget = true;
				else
				{
					if (victim != null) // Target is a unarmed player ; clean aggro.
					{
						if (victim instanceof L2PcInstance && victim.getActiveWeaponInstance() == null)
						{
							angel.stopHating(victim); // Clean the aggro number of previous victim.
							updateTarget = true;
						}
					}
					else
						// No target currently.
						updateTarget = true;
				}
				
				if (updateTarget)
				{
					L2Character newVictim = getRandomTarget(minion);
					if (newVictim != null && victim != newVictim)
					{
						angel.addDamageHate(newVictim, 0, 10000);
						angel.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, newVictim);
					}
				}
			}
		}
		return super.onAdvEvent(event, npc, player);
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		int npcId = npc.getNpcId();
		String htmltext = "";
		
		if (_Zone == null)
		{
			_Zone = GrandBossManager.getZoneByXYZ(113100, 14500, 10077);
			
			// If the zone is still null, it means the area is disabled / missing in DP.
			if (_Zone == null)
				return "<html><body>Angelic Vortex:<br>You may not enter while admin disabled this zone.</body></html>";
		}
		
		if (npcId == STONE_BAIUM && GrandBossManager.getBossStatus(LIVE_BAIUM) == ASLEEP)
		{
			if (_Zone.isPlayerAllowed(player))
			{
				// once Baium is awaken, no more people may enter until he dies, the server reboots, or
				// 30 minutes pass with no attacks made against Baium.
				GrandBossManager.setBossStatus(LIVE_BAIUM, AWAKE);
				npc.deleteMe();
				
				final L2Npc baium = addSpawn(LIVE_BAIUM, npc, false, 0, true);
				GrandBossManager.addBoss((L2GrandBossInstance) baium);
				
				// Baium is stuck for the following time : 35secs
				ThreadPoolManager.getInstance().scheduleGeneral(new Runnable()
				{
					@Override
					public void run()
					{
						baium.setIsInvul(false);
						baium.setIsImmobilized(false);
						
						// Start monitoring baium's inactivity and activate the AI
						_LastAttackVsBaiumTime = System.currentTimeMillis();
						startQuestTimer("baium_despawn", 60000, baium, null, true);
						startQuestTimer("skill_range", 2000, baium, null, true);
					}
				}, 35000L);
				
				// First animation
				baium.setIsInvul(true);
				baium.setRunning();
				baium.broadcastPacket(new SocialAction(baium, 2));
				baium.broadcastPacket(new Earthquake(baium.getX(), baium.getY(), baium.getZ(), 40, 10));
				
				_waker = player;
				
				// Second animation, waker sacrifice, followed by angels spawn and third animation.
				startQuestTimer("baium_neck", 13000, baium, null, false);
				startQuestTimer("sacrifice_waker", 24000, baium, null, false);
				startQuestTimer("baium_roar", 28000, baium, null, false);
				
				baium.setShowSummonAnimation(false);
			}
			else
				htmltext = "Conditions are not right to wake up Baium";
		}
		return htmltext;
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
		
		if (npc.getNpcId() == LIVE_BAIUM)
		{
			if (attacker.getMountType() == 1)
			{
				L2Skill skill = SkillTable.getInstance().getInfo(4258, 1);
				if (attacker.getFirstEffect(skill) == null)
				{
					npc.setTarget(attacker);
					npc.doCast(skill);
				}
			}
			// update a variable with the last action against baium
			_LastAttackVsBaiumTime = System.currentTimeMillis();
		}
		return super.onAttack(npc, attacker, damage, isPet);
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isPet)
	{
		cancelQuestTimer("baium_despawn", npc, null);
		npc.broadcastPacket(new PlaySound(1, "BS01_D", 1, npc.getObjectId(), npc.getX(), npc.getY(), npc.getZ()));
		
		// spawn the "Teleportation Cubic" for 15 minutes (to allow players to exit the lair)
		addSpawn(29055, 115203, 16620, 10078, 0, false, 900000, false);
		
		// "lock" baium for 5 days + 1-8 hours
		long respawnTime = (long) Config.SPAWN_INTERVAL_BAIUM + Rnd.get(Config.RANDOM_SPAWN_TIME_BAIUM);
		GrandBossManager.setBossStatus(LIVE_BAIUM, DEAD);
		startQuestTimer("baium_unlock", respawnTime, null, null, false);
		
		// also save the respawn time so that the info is maintained past reboots
		StatsSet info = GrandBossManager.getStatsSet(LIVE_BAIUM);
		info.set("respawn_time", (System.currentTimeMillis()) + respawnTime);
		GrandBossManager.setStatsSet(LIVE_BAIUM, info);
		
		// Unspawn angels.
		for (L2Npc minion : _Minions)
		{
			if (minion != null)
			{
				minion.getSpawn().stopRespawn();
				minion.deleteMe();
			}
		}
		_Minions.clear();
		
		// Clean Baium AI
		cancelQuestTimer("skill_range", npc, null);
		
		// Clean angels AI
		cancelQuestTimer("angels_aggro_reconsider", null, null);
		
		return super.onKill(npc, killer, isPet);
	}
	
	/**
	 * This method allows to select a random target, and is used both for Baium and angels.
	 * @param npc to check.
	 * @return the random target.
	 */
	private L2Character getRandomTarget(L2Npc npc)
	{
		int npcId = npc.getNpcId();
		List<L2Character> result = new ArrayList<>();
		
		for (L2Character obj : npc.getKnownList().getKnownType(L2Character.class))
		{
			if (obj instanceof L2Playable)
			{
				if (obj.isDead() || !(GeoData.getInstance().canSeeTarget(npc, obj)))
					continue;
				
				if (obj instanceof L2PcInstance)
				{
					if (((L2PcInstance) obj).getAppearance().getInvisible())
						continue;
					
					if (npcId == ARCHANGEL && ((L2PcInstance) obj).getActiveWeaponInstance() == null)
						continue;
				}
				else if (obj instanceof L2PetInstance)
					continue;
				
				result.add(obj);
			}
			// Case of Archangels, they can hit Baium.
			else if (obj instanceof L2GrandBossInstance && npcId == ARCHANGEL)
				result.add(obj);
		}
		
		// If there's no players available, Baium and Angels are hitting each other.
		if (result.isEmpty())
		{
			if (npcId == LIVE_BAIUM) // Case of Baium. Angels should never be without target.
			{
				for (L2Npc minion : _Minions)
					if (minion != null)
						result.add(minion);
			}
		}
		
		return (result.isEmpty()) ? null : result.get(Rnd.get(result.size()));
	}
	
	/**
	 * That method checks if angels are near.
	 * @param npc : baium.
	 * @return the number of angels surrounding the target.
	 */
	private int getSurroundingAngelsNumber(L2Npc npc)
	{
		int count = 0;
		
		for (L2MonsterInstance obj : npc.getKnownList().getKnownTypeInRadius(L2MonsterInstance.class, 600))
		{
			if (obj.getNpcId() == ARCHANGEL)
				count++;
		}
		return count;
	}
	
	/**
	 * The personal casting AI for Baium.
	 * @param npc baium, basically...
	 */
	private void callSkillAI(L2Npc npc)
	{
		if (npc.isInvul() || npc.isCastingNow())
			return;
		
		// Pickup a target if no or dead victim. If Baium was hitting an angel, 15% luck he reconsiders his target. 10% luck he decides to reconsiders his target.
		if (_actualVictim == null || _actualVictim.isDead() || !(npc.getKnownList().knowsObject(_actualVictim)) || (_actualVictim instanceof L2MonsterInstance && Rnd.get(100) < 15) || Rnd.get(10) == 0)
			_actualVictim = getRandomTarget(npc);
		
		// If result is null, return directly.
		if (_actualVictim == null)
			return;
		
		final L2Skill skill = SkillTable.getInstance().getInfo(getRandomSkill(npc), 1);
		
		// Adapt the skill range, because Baium is fat.
		if (Util.checkIfInRange(skill.getCastRange() + npc.getCollisionRadius(), npc, _actualVictim, true))
		{
			npc.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
			npc.setIsCastingNow(true);
			npc.setTarget(skill.getId() == 4135 ? npc : _actualVictim);
			npc.doCast(skill);
		}
		else
		{
			npc.getAI().setIntention(CtrlIntention.AI_INTENTION_FOLLOW, _actualVictim, null);
			npc.setIsCastingNow(false);
		}
	}
	
	/**
	 * Pick a random skill through that list.<br>
	 * If Baium feels surrounded, he will use AoE skills. Same behavior if he is near 2+ angels.<br>
	 * @param npc baium
	 * @return a usable skillId
	 */
	private int getRandomSkill(L2Npc npc)
	{
		// Baium's selfheal. It happens exceptionaly.
		if (npc.getCurrentHp() < (npc.getMaxHp() / 10))
		{
			if (Rnd.get(10000) == 777) // His lucky day.
				return 4135;
		}
		
		int skill = 4127; // Default attack if nothing is possible.
		final int chance = Rnd.get(100); // Remember, it's 0 to 99, not 1 to 100.
		
		// If Baium feels surrounded or see 2+ angels, he unleashes his wrath upon heads :).
		if (Util.getPlayersCountInRadius(600, npc, false) >= 20 || getSurroundingAngelsNumber(npc) >= 2)
		{
			if (chance < 25)
				skill = 4130;
			else if (chance >= 25 && chance < 50)
				skill = 4131;
			else if (chance >= 50 && chance < 75)
				skill = 4128;
			else if (chance >= 75 && chance < 100)
				skill = 4129;
		}
		else
		{
			if (npc.getCurrentHp() > ((npc.getMaxHp() * 3) / 4)) // > 75%
			{
				if (chance < 10)
					skill = 4128;
				else if (chance >= 10 && chance < 20)
					skill = 4129;
			}
			else if (npc.getCurrentHp() > ((npc.getMaxHp() * 2) / 4)) // > 50%
			{
				if (chance < 10)
					skill = 4131;
				else if (chance >= 10 && chance < 20)
					skill = 4128;
				else if (chance >= 20 && chance < 30)
					skill = 4129;
			}
			else if (npc.getCurrentHp() > (npc.getMaxHp() / 4)) // > 25%
			{
				if (chance < 10)
					skill = 4130;
				else if (chance >= 10 && chance < 20)
					skill = 4131;
				else if (chance >= 20 && chance < 30)
					skill = 4128;
				else if (chance >= 30 && chance < 40)
					skill = 4129;
			}
			else
			// < 25%
			{
				if (chance < 10)
					skill = 4130;
				else if (chance >= 10 && chance < 20)
					skill = 4131;
				else if (chance >= 20 && chance < 30)
					skill = 4128;
				else if (chance >= 30 && chance < 40)
					skill = 4129;
			}
		}
		return skill;
	}
	
	public static void main(String[] args)
	{
		// Quest class and state definition
		new Baium(-1, "baium", "ai");
	}
}