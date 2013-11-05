package net.xcine.gameserver.event;

import java.util.Set;

import javolution.util.FastList;
import javolution.util.FastMap;
import net.xcine.gameserver.datatables.SkillTable;
import net.xcine.gameserver.datatables.sql.NpcTable;
import net.xcine.gameserver.datatables.sql.SpawnTable;
import net.xcine.gameserver.model.L2Npc;
import net.xcine.gameserver.model.L2Object;
import net.xcine.gameserver.model.L2Party;
import net.xcine.gameserver.model.L2Skill;
import net.xcine.gameserver.model.L2Summon;
import net.xcine.gameserver.model.actor.instance.L2ItemInstance;
import net.xcine.gameserver.model.actor.instance.L2PcInstance;
import net.xcine.gameserver.model.actor.instance.L2PetInstance;
import net.xcine.gameserver.model.spawn.L2Spawn;
import net.xcine.gameserver.network.serverpackets.CreatureSay;
import net.xcine.gameserver.network.serverpackets.ExShowScreenMessage;
import net.xcine.gameserver.network.serverpackets.Ride;
import net.xcine.gameserver.templates.L2EtcItemType;
import net.xcine.gameserver.templates.L2NpcTemplate;
import net.xcine.gameserver.thread.ThreadPoolManager;
import net.xcine.gameserver.util.Broadcast;
import net.xcine.util.random.Rnd;

/**
 * @author Rizel
 *
 */
public abstract class Event
{
	protected class Clock implements Runnable
	{
		protected String getTime()
		{
			String mins = "" + time / 60;
			String secs = (time % 60 < 10 ? "0" + time % 60 : "" + time % 60);
			return "" + mins + ":" + secs + "";
		}

		@Override
		public void run()
		{
			clockTick();
			scorebartext = getScorebar();
			for (L2PcInstance player : getPlayerList())
				player.sendPacket(new ExShowScreenMessage(1, -1, 2, false, 1, 0, 0, false, 2000, false, scorebartext));

			if (time <= 0)
				schedule(1);
			else
			{
				time--;
				tpm.scheduleGeneral(clock, 1000);
			}
		}

		protected void setTime(int t)
		{
			time = t;
		}

		protected void startClock(int mt)
		{
			time = mt;
			tpm.scheduleGeneral(clock, 1);
		}
	}

	protected class ResurrectorTask implements Runnable
	{
		private L2PcInstance player;

		public ResurrectorTask(L2PcInstance p)
		{
			player = p;
			ThreadPoolManager.getInstance().scheduleGeneral(this, 7000);
			debug("Resurrector task created: " + player.getName());
		}

		@Override
		public void run()
		{
			if (EventManager.getInstance().isRegistered(player))
			{
				debug("Resurrector task executed: " + player.getName());
				player.doRevive();
				
				if(EventManager.getInstance().getBoolean("eventBufferEnabled")) 
				 	EventBuffer.getInstance().buffPlayer(player);
				
				player.setCurrentCp(player.getMaxCp());
				player.setCurrentHp(player.getMaxHp());
				player.setCurrentMp(player.getMaxMp());
				teleportToTeamPos(player);
			}

		}
	}

	protected int eventId;

	//Config
	protected EventConfig config = EventConfig.getInstance();

	protected static int[] startpos = EventManager.npcPos;

	protected FastMap<Integer, EventTeam> teams;

	//TEAM-STATUS-SCORE
	protected FastMap<L2PcInstance, int[]> players;

	protected ThreadPoolManager tpm;

	protected ResurrectorTask resurrectorTask;

	public String scorebartext;

	protected Clock clock;

	protected int time;

	protected int winnerTeam;
	
	protected int loserTeam;

	public Event()
	{
		teams = new FastMap<>();
		clock = new Clock();
		tpm = ThreadPoolManager.getInstance();
		players = new FastMap<>();
		time = 0;
	}

	protected void addToResurrector(L2PcInstance player)
	{
		new ResurrectorTask(player);
	}

	protected void announce(Set<L2PcInstance> list, String text)
	{
		for (L2PcInstance player : list)
			player.sendPacket(new CreatureSay(0, 18, "", "[Event] " + text));
	}

	public boolean canAttack(L2PcInstance player, L2Object target)
	{
		return true;
	}

	protected void clockTick()
	{

	}

	protected int countOfPositiveStatus()
	{
		int count = 0;
		for (L2PcInstance player : getPlayerList())
			if (getStatus(player) >= 0)
				count++;

		return count;
	}

	protected void createNewTeam(int id, String name, int[] color, int[] startPos)
	{
		teams.put(id, new EventTeam(id, name, color, startPos));
	}

	protected void createPartyOfTeam(int teamId)
	{
		int count = 0;
		L2Party party = null;

		FastList<L2PcInstance> list = new FastList<>();

		for (L2PcInstance p : players.keySet())
			if (getTeam(p) == teamId)
				list.add(p);

		for (L2PcInstance player : list)
		{
			if (count % 9 == 0 && list.size() - count != 1)
				party = new L2Party(player, 1);
			if (count % 9 < 9)
				player.joinParty(party);
			count++;
		}
	}

	protected void debug(String text)
	{
		EventManager.getInstance().debug(text);
	}

	protected void divideIntoTeams(int number)
	{
		int i = 0;
		while (EventManager.getInstance().players.size() != 0)
		{
			i++;
			L2PcInstance player = EventManager.getInstance().players.get(Rnd.get(EventManager.getInstance().players.size()));
			players.put(player, new int[] { i, 0, 0 });
			EventManager.getInstance().players.remove(player);
			if (i == number)
				i = 0;
		}
	}

	public void dropBomb(L2PcInstance player)
	{

	}

	protected abstract void endEvent();

	protected void forceSitAll()
	{
		for (L2PcInstance player : players.keySet())
		{
			if (player.isCastingNow())
					player.abortCast();
			if (player.isAttackingNow())
				    player.abortAttack();
			player.sitDown();
			player.eventSitForced = true;
		}
	}

	protected void forceStandAll()
	{
		for (L2PcInstance player : players.keySet())
		{
			player.eventSitForced = false;
			player.standUp();
		}

	}

	public boolean getBoolean(String propName)
	{
		return config.getBoolean(eventId, propName);
	}

	public int[] getColor(String owner)
	{
		return config.getColor(eventId, owner);
	}

	public int getInt(String propName)
	{
		return config.getInt(eventId, propName);
	}

	protected Set<L2PcInstance> getPlayerList()
	{
		return players.keySet();
	}

	protected FastList<L2PcInstance> getPlayersOfTeam(int team)
	{
		FastList<L2PcInstance> list = new FastList<>();

		for (L2PcInstance player : getPlayerList())
			if (getTeam(player) == team)
				list.add(player);

		return list;
	}

	protected EventTeam getPlayersTeam(L2PcInstance player)
	{
		return teams.get(players.get(player)[0]);
	}

	protected FastList<L2PcInstance> getPlayersWithStatus(int status)
	{
		FastList<L2PcInstance> list = new FastList<>();

		for (L2PcInstance player : getPlayerList())
			if (getStatus(player) == status)
				list.add(player);

		return list;
	}

	protected L2PcInstance getPlayerWithMaxScore()
	{
		L2PcInstance max;
		max = players.head().getNext().getKey();
		for (L2PcInstance player : players.keySet())
			if (players.get(player)[2] > players.get(max)[2])
				max = player;

		return max;
	}
	
	protected void unequip(){
		for (L2PcInstance player : players.keySet())
		{
		player.getInventory().unEquipItemInSlot(7);
		player.getInventory().unEquipItemInSlot(8);
		player.getInventory().unEquipItemInSlot(14);
		}
	}

	public int[] getPosition(String owner, int num)
	{
		return config.getPosition(eventId, owner, num);
	}

	protected L2PcInstance getRandomPlayer()
	{
		FastList<L2PcInstance> temp = new FastList<>();
		for (L2PcInstance player : players.keySet())
			temp.add(player);

		return temp.get(Rnd.get(temp.size()));
	}

	protected L2PcInstance getRandomPlayerFromTeam(int team)
	{
		FastList<L2PcInstance> temp = new FastList<>();
		for (L2PcInstance player : players.keySet())
			if (getTeam(player) == team)
				temp.add(player);

		return temp.get(Rnd.get(temp.size()));
	}

	public FastList<Integer> getRestriction(String type)
	{
		return config.getRestriction(eventId, type);
	}

	protected int getScore(L2PcInstance player)
	{
		return players.get(player)[2];
	}

	protected abstract String getScorebar();

	protected int getStatus(L2PcInstance player)
	{
		return players.get(player)[1];
	}

	public String getString(String propName)
	{
		return config.getString(eventId, propName);
	}

	public int getTeam(L2PcInstance player)
	{
		return players.get(player)[0];
	}

	protected int getWinnerTeam()
	{
		FastList<EventTeam> t = new FastList<>();

		for (EventTeam team : teams.values())
		{
			if (t.size() == 0)
			{
				t.add(team);
				continue;
			}

			if (team.getScore() > t.getFirst().getScore())
			{
				t.clear();
				t.add(team);
				continue;
			}
			if (team.getScore() == t.getFirst().getScore())
				t.add(team);

		}

		if (t.size() > 1)
			return t.get(Rnd.get(t.size())).getId();
		return t.getFirst().getId();

	}

	protected void giveReward(FastList<L2PcInstance> players, int id, int ammount)
	{
		for (L2PcInstance player : players)
		{
			if(player == null)
				continue;

			player.addItem("Event", id, ammount, player, true);
			EventStats.getInstance().tempTable.get(player.getObjectId())[0] = 1;
		}

	}

	protected void giveReward(L2PcInstance player, int id, int ammount)
	{
		EventStats.getInstance().tempTable.get(player.getObjectId())[0] = 1;

		player.addItem("Event", id, ammount, player, true);
	}

	protected void increasePlayersScore(L2PcInstance player)
	{
		int old = getScore(player);
		setScore(player, old + 1);
		EventStats.getInstance().tempTable.get(player.getObjectId())[3] = EventStats.getInstance().tempTable.get(player.getObjectId())[3] + 1;
	}

	protected void msgToAll(String text)
	{
		for (L2PcInstance player : players.keySet())
			player.sendMessage(text);
	}

	public void onDie(L2PcInstance victim, L2PcInstance killer)
	{
		EventStats.getInstance().tempTable.get(victim.getObjectId())[2] = EventStats.getInstance().tempTable.get(victim.getObjectId())[2] + 1;
		return;
	}

	public void onHit(L2PcInstance actor, L2PcInstance target)
	{
	}

	public void onKill(L2PcInstance victim, L2PcInstance killer)
	{
		EventStats.getInstance().tempTable.get(killer.getObjectId())[1] = EventStats.getInstance().tempTable.get(killer.getObjectId())[1] + 1;
		return;
	}

	public void onLogout(L2PcInstance player)
	{
		if (players.containsKey(player))
			removePlayer(player);
		
		player.setXYZ(EventManager.getInstance().positions.get(player)[0], EventManager.getInstance().positions.get(player)[1], EventManager.getInstance().positions.get(player)[2]);
		player.setTitle(EventManager.getInstance().titles.get(player));

		if (teams.size() == 1)
			if (getPlayerList().size() == 1)
			{
				endEvent();
				return;
			}

		if (teams.size() > 1)
		{
			int t = players.head().getNext().getValue()[0];
			for (L2PcInstance p : getPlayerList())
				if (getTeam(p) != t)
					return;

			endEvent();
			return;

		}
	}

	public void onSay(int type, L2PcInstance player, String text)
	{
		return;
	}

	public boolean onTalkNpc(L2Npc npc, L2PcInstance player)
	{
		return false;
	}

	public boolean onUseItem(L2PcInstance player, L2ItemInstance item)
	{
		if (EventManager.getInstance().getRestriction("item").contains(item.getItemId()) || getRestriction("item").contains(item.getItemId()))
			return false;

		if (item.getItemType() == L2EtcItemType.POTION && !getBoolean("allowPotions"))
			return false;

		if (item.getItemType() == L2EtcItemType.SCROLL)
			return false;

		return true;
	}

	public boolean onUseMagic(L2Skill skill)
	{
		if (EventManager.getInstance().getRestriction("skill").contains(skill.getId()) || getRestriction("skill").contains(skill.getId()))
			return false;

		if (skill.getSkillType() == L2Skill.SkillType.RESURRECT)
			return false;
		
		if (skill.getSkillType() == L2Skill.SkillType.SUMMON_FRIEND) 
			return false; 
		
		if (skill.getSkillType() == L2Skill.SkillType.RECALL) 
			return false; 

		if (skill.getSkillType() == L2Skill.SkillType.FAKE_DEATH)
			return false;

		return true;
	}

	protected void prepare(L2PcInstance player)
	{
		if (player.isCastingNow())
			player.abortCast();
		player.getAppearance().setVisible();
		
		if (player.getPet() != null)
		{
			L2Summon summon = player.getPet();
		    if (summon instanceof L2PetInstance)
			     summon.unSummon(player);
		}
		
		if(player.isMounted()) 
		{ 
			if(player.setMountType(0)) 
			{ 
				if(player.isFlying()) 
				{ 
					player.removeSkill(SkillTable.getInstance().getInfo(4289, 1)); 
				} 
				Ride dismount = new Ride(player.getObjectId(), Ride.ACTION_DISMOUNT, 0); 
				Broadcast.toSelfAndKnownPlayers(player, dismount); 
				player.setMountObjectID(0); 
				dismount = null; 
			}
		} 
		
		if (getBoolean("removeBuffs"))
		{
			player.stopAllEffects();
			if (player.getPet() != null)
			{
				L2Summon summon = player.getPet();
			    if (summon instanceof L2Summon)
				     summon.unSummon(player);
			}
		}

		if (player.getParty() != null)
		{
			L2Party party = player.getParty();
			party.removePartyMember(player);
		}
		int[] nameColor = getPlayersTeam(player).getTeamColor();
		player.getAppearance().setNameColor(nameColor[0], nameColor[1], nameColor[2]);
		player.setTitle("<- 0 ->");
		
		if(EventManager.getInstance().getBoolean("eventBufferEnabled")) 
		 	EventBuffer.getInstance().buffPlayer(player);
		
		player.broadcastUserInfo();
	}

	protected void preparePlayers()
	{
		for (L2PcInstance player : players.keySet())
			prepare(player);
	}

	protected void removePlayer(L2PcInstance player)
	{
		players.remove(player);
	}

	public void reset()
	{
		players.clear();
		tpm.purge();
		winnerTeam = 0;

		for (EventTeam team : teams.values())
			team.setScore(0);

		debug("Event reseted");
	}

	protected abstract void schedule(int time);

	protected void selectPlayers(int teamId, int playerCount)
	{
		for (int i = 0; i < playerCount; i++)
		{
			L2PcInstance player = EventManager.getInstance().players.get(Rnd.get(EventManager.getInstance().players.size()));
			players.put(player, new int[] { teamId, 0, 0 });
			EventManager.getInstance().players.remove(player);
		}

	}

	protected void setScore(L2PcInstance player, int score)
	{
		players.get(player)[2] = score;
		player.setTitle("<- " + score + " ->");
		player.broadcastUserInfo();
	}

	protected void setStatus(L2PcInstance player, int status)
	{
		if (players.containsKey(player))
			players.get(player)[1] = status;
	}

	protected void setTeam(L2PcInstance player, int team)
	{
		players.get(player)[0] = team;
	}

	protected abstract void showHtml(L2PcInstance player, int obj);

	protected L2Spawn spawnNPC(int xPos, int yPos, int zPos, int npcId)
	{
		final L2NpcTemplate template = NpcTable.getInstance().getTemplate(npcId);
		try
		{
			final L2Spawn spawn = new L2Spawn(template);
			spawn.setLocx(xPos);
			spawn.setLocy(yPos);
			spawn.setLocz(zPos);
			spawn.setAmount(1);
			spawn.setHeading(0);
			spawn.setRespawnDelay(1);
			SpawnTable.getInstance().addNewSpawn(spawn, false);
			spawn.init();
			return spawn;
		}
		catch (Exception e)
		{
			return null;
		}
	}

	protected abstract void start();

	protected void teleportPlayer(L2PcInstance player, int[] coordinates)
	{
		player.teleToLocation(coordinates[0] + (Rnd.get(coordinates[3] * 2) - coordinates[3]), coordinates[1] + (Rnd.get(coordinates[3] * 2) - coordinates[3]), coordinates[2]);
	}

	protected void teleportToTeamPos()
	{
		for (L2PcInstance player : players.keySet())
		{
			teleportToTeamPos(player);
		}

	}

	protected void teleportToTeamPos(L2PcInstance player)
	{
		int[] pos = getPosition(teams.get(getTeam(player)).getName(), 0);
		teleportPlayer(player, pos);
	}

	protected void unspawnNPC(L2Spawn npcSpawn)
	{
		if (npcSpawn == null)
			return;

		npcSpawn.getLastSpawn().deleteMe();
		npcSpawn.stopRespawn();
		SpawnTable.getInstance().deleteSpawn(npcSpawn, true);
	}
	
	public int numberOfTeams() 
 	{ 
 		return teams.size(); 
 	} 
 	
 	public void useCapture(L2PcInstance player, L2Npc base){} 
 	
	protected void polymorph(L2PcInstance player, int id)
	{
		player.getPoly().setPolyInfo("", String.valueOf(id));
		player.decayMe();
		player.spawnMe(player.getX(),player.getY(),player.getZ());
	}
	
	protected void unpolymorph(L2PcInstance player)
	{
		player.getPoly().setPolyInfo(null, "1");
		player.decayMe();
		player.spawnMe(player.getX(),player.getY(),player.getZ());
	}
}
