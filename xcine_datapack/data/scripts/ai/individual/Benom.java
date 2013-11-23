package ai.individual;

import ai.AbstractNpcAI;

import java.util.ArrayList;

import javolution.util.FastMap;

import net.xcine.gameserver.ai.CtrlIntention;
import net.xcine.gameserver.datatables.DoorTable;
import net.xcine.gameserver.datatables.SpawnTable;
import net.xcine.gameserver.instancemanager.CastleManager;
import net.xcine.gameserver.instancemanager.GrandBossManager;
import net.xcine.gameserver.model.L2CharPosition;
import net.xcine.gameserver.model.L2Spawn;
import net.xcine.gameserver.model.Location;
import net.xcine.gameserver.model.actor.L2Attackable;
import net.xcine.gameserver.model.actor.L2Npc;
import net.xcine.gameserver.model.actor.instance.L2PcInstance;
import net.xcine.gameserver.model.quest.QuestEventType;
import net.xcine.gameserver.network.serverpackets.NpcSay;
import net.xcine.gameserver.network.serverpackets.SocialAction;
import net.xcine.gameserver.network.serverpackets.SpecialCamera;
import net.xcine.util.Rnd;

/**
 * @rework TerryXX
 */
public final class Benom extends AbstractNpcAI
{
	private static final int CASTLE_ID = 8;
	private static final int BENOM = 29054;
	private static final int BENOM_TELEPORT = 35506;
	private static final String[] BENOM_SPEAK = {
			"You should have finished me when you had the chance!!!",
			"I will crush all of you!!!",
			"I am not finished here, come face me!!!",
			"You cowards!!! I will torture each and everyone of you!!!" };
	private static final FastMap<Integer, Location>	BENON_WALK_ROUTES	= new FastMap<Integer, Location>();
	private static final int[] WALK_TIMES = {
			18000,
			17000,
			4500,
			16000,
			22000,
			14000,
			10500,
			14000,
			9500,
			12500,
			20500,
			14500,
			17000,
			20000,
			22000,
			11000,
			11000,
			20000,
			8000,
			5500,
			20000,
			18000,
			25000,
			28000,
			25000,
			25000,
			25000,
			25000,
			10000,
			24000,
			7000,
			12000,
			20000 };

	private L2Npc _Benom;
	private static final byte ALIVE = 0;
	private static final byte DEAD = 1;
	private static byte BenomIsSpawned = 0;
	private static int BenomWalkRouteStep = 0;

	static
	{
		BENON_WALK_ROUTES.put(0, new Location(12565, -49739, -547));
		BENON_WALK_ROUTES.put(1, new Location(11242, -49689, -33));
		BENON_WALK_ROUTES.put(2, new Location(10751, -49702, 83));
		BENON_WALK_ROUTES.put(3, new Location(10824, -50808, 316));
		BENON_WALK_ROUTES.put(4, new Location(9084, -50786, 972));
		BENON_WALK_ROUTES.put(5, new Location(9095, -49787, 1252));
		BENON_WALK_ROUTES.put(6, new Location(8371, -49711, 1252));
		BENON_WALK_ROUTES.put(7, new Location(8423, -48545, 1252));
		BENON_WALK_ROUTES.put(8, new Location(9105, -48474, 1252));
		BENON_WALK_ROUTES.put(9, new Location(9085, -47488, 972));
		BENON_WALK_ROUTES.put(10, new Location(10858, -47527, 316));
		BENON_WALK_ROUTES.put(11, new Location(10842, -48626, 75));
		BENON_WALK_ROUTES.put(12, new Location(12171, -48464, -547));
		BENON_WALK_ROUTES.put(13, new Location(13565, -49145, -535));
		BENON_WALK_ROUTES.put(14, new Location(15653, -49159, -1059));
		BENON_WALK_ROUTES.put(15, new Location(15423, -48402, -839));
		BENON_WALK_ROUTES.put(16, new Location(15066, -47438, -419));
		BENON_WALK_ROUTES.put(17, new Location(13990, -46843, -292));
		BENON_WALK_ROUTES.put(18, new Location(13685, -47371, -163));
		BENON_WALK_ROUTES.put(19, new Location(13384, -47470, -163));
		BENON_WALK_ROUTES.put(20, new Location(14609, -48608, 346));
		BENON_WALK_ROUTES.put(21, new Location(13878, -47449, 747));
		BENON_WALK_ROUTES.put(22, new Location(12894, -49109, 980));
		BENON_WALK_ROUTES.put(23, new Location(10135, -49150, 996));
		BENON_WALK_ROUTES.put(24, new Location(12894, -49109, 980));
		BENON_WALK_ROUTES.put(25, new Location(13738, -50894, 747));
		BENON_WALK_ROUTES.put(26, new Location(14579, -49698, 347));
		BENON_WALK_ROUTES.put(27, new Location(12896, -51135, -166));
		BENON_WALK_ROUTES.put(28, new Location(12971, -52046, -292));
		BENON_WALK_ROUTES.put(29, new Location(15140, -50781, -442));
		BENON_WALK_ROUTES.put(30, new Location(15328, -50406, -603));
		BENON_WALK_ROUTES.put(31, new Location(15594, -49192, -1059));
		BENON_WALK_ROUTES.put(32, new Location(13175, -49153, -537));
	}

	public Benom(String name, String descr)
	{
		super(name, descr);

		//setInitialState(new State("Start", this));
		
		addEventId(BENOM, QuestEventType.ON_AGGRO_RANGE_ENTER);
		addEventId(BENOM, QuestEventType.ON_KILL);
		addEventId(BENOM_TELEPORT, QuestEventType.QUEST_START);
		addEventId(BENOM_TELEPORT, QuestEventType.ON_TALK);

		final int castleOwner = CastleManager.getInstance().getCastleById(CASTLE_ID).getOwnerId();
		final long siegeDate = CastleManager.getInstance().getCastleById(CASTLE_ID).getSiegeDate().getTimeInMillis();
		final long currentTime = System.currentTimeMillis();
		long benomTeleporterSpawn = (siegeDate - currentTime) - 86400000;
		final long benomRaidRoomSpawn = (siegeDate - currentTime) - 86400000;
		long benomRaidSiegeSpawn = (siegeDate - currentTime);

		if(benomTeleporterSpawn < 0)
		{
			benomTeleporterSpawn = 1;
		}

		if(benomRaidSiegeSpawn < 0)
		{
			benomRaidSiegeSpawn = 1;
		}

		if(castleOwner > 0)
		{
			if(benomTeleporterSpawn >= 1)
			{
				startQuestTimer("BenomTeleSpawn", benomTeleporterSpawn, null, null, false);
			}

			if((siegeDate - currentTime) > 0)
			{
				startQuestTimer("BenomRaidRoomSpawn", benomRaidRoomSpawn, null, null, false);
			}

			startQuestTimer("BenomRaidSiegeSpawn", benomRaidSiegeSpawn, null, null, false);
		}
	}

	@Override
	public final String onTalk(L2Npc npc, L2PcInstance player)
	{
		String htmltext = null;
		final int castleOwner = CastleManager.getInstance().getCastleById(CASTLE_ID).getOwnerId();
		final int clanId = player.getClanId();

		if(castleOwner != 0 && clanId != 0)
		{
			if(castleOwner == clanId)
			{
		        int X = 12558 + (Rnd.get(200) - 100);
		        int Y = -49279 + (Rnd.get(200) - 100);
		        player.teleToLocation(X, Y, -3007,0);
				return htmltext;
			}
			else
			{
				htmltext = "<html><body>Benom's Avatar:<br>Your clan does not own this castle. Only members of this Castle's owning clan can challenge Benom.</body></html>";
			}
		}
		else
		{
			htmltext = "<html><body>Benom's Avatar:<br>Your clan does not own this castle. Only members of this Castle's owning clan can challenge Benom.</body></html>";
		}

		return htmltext;
	}

	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		final int statusBoss = GrandBossManager.getBossStatus(BENOM);

		if(event.equalsIgnoreCase("BenomTeleSpawn"))
		{
			addSpawn(BENOM_TELEPORT, 11013, -49629, -547, 13400, false, 0, false);
		}

		else if(event.equalsIgnoreCase("BenomRaidRoomSpawn"))
		{
			if(BenomIsSpawned == 0 && statusBoss == 0)
			{
				_Benom = addSpawn(BENOM, 12047, -49211, -3009, 0, false, 0, false);
			}

			BenomIsSpawned = 1;
		}
		else if(event.equalsIgnoreCase("BenomRaidSiegeSpawn"))
		{
			if(statusBoss == 0)
			{
				switch(BenomIsSpawned)
				{
					case 0:
						_Benom = addSpawn(BENOM, 11025, -49152, -537, 0, false, 0, false);
						BenomIsSpawned = 1;
						break;
					case 1:
						_Benom.teleToLocation(11025, -49152, -537,0);
						break;
				}

				startQuestTimer("BenomSpawnEffect", 100, _Benom, null, false);
				startQuestTimer("BenomBossDespawn", 5400000 , _Benom, null, false);
				cancelQuestTimer("BenomSpawn", _Benom, null);
				unSpawnNpc(BENOM_TELEPORT);
			}
		}
		else if(event.equalsIgnoreCase("BenomSpawnEffect"))
		{
			npc.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
			npc.broadcastPacket(new SpecialCamera(npc.getObjectId(), 200, 0, 150, 0, 5000));
			npc.broadcastPacket(new SocialAction(npc, 3));
			startQuestTimer("BenomWalk", 5000, npc, null, false);
			BenomWalkRouteStep = 0;
		}
		else if(event.equalsIgnoreCase("Attacking"))
		{
			ArrayList<L2PcInstance> NumPlayers = new ArrayList<L2PcInstance>();
			for(L2PcInstance plr : npc.getKnownList().getKnownType(L2PcInstance.class))
			{
				NumPlayers.add(plr);
			}

			if(NumPlayers.size() > 0)
			{
				L2PcInstance target = NumPlayers.get(Rnd.get(NumPlayers.size()));
				((L2Attackable) npc).addDamageHate(target, 0, 999);
				npc.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, target);
				startQuestTimer("Attacking", 2000, npc, player, false);
			}
			else if(NumPlayers.size() == 0)
			{
				startQuestTimer("BenomWalkFinish", 2000, npc, null, false);
			}
		}
		else if(event.equalsIgnoreCase("BenomWalkFinish"))
		{
			if(npc.getCastle().getSiege().getIsInProgress())
			{
				cancelQuestTimer("Attacking", npc, player);
			}

			npc.teleToLocation(BENON_WALK_ROUTES.get(BenomWalkRouteStep),0);
			npc.setWalking();
			BenomWalkRouteStep = 0;
			startQuestTimer("BenomWalk", 2200, npc, null, false);
		}
		else if(event.equalsIgnoreCase("BenomWalk"))
		{
			if(BenomWalkRouteStep == 33)
			{
				BenomWalkRouteStep = 0;
				startQuestTimer("BenomWalk", 100, npc, null, false);
			}
			else
			{
				startQuestTimer("Talk", 100, npc, null, false);
				switch(BenomWalkRouteStep)
				{
					case 14:
						startQuestTimer("DoorOpen", 15000, null, null, false);
						startQuestTimer("DoorClose", 23000, null, null, false);
						break;
					case 32:
						startQuestTimer("DoorOpen", 500, null, null, false);
						startQuestTimer("DoorClose", 4000, null, null, false);
						break;
				}

				npc.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
				npc.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, 
					new L2CharPosition(BENON_WALK_ROUTES.get(BenomWalkRouteStep)));
				BenomWalkRouteStep++;
				startQuestTimer("BenomWalk", WALK_TIMES[BenomWalkRouteStep], npc, null, false);
			}
		}
		else if(event.equalsIgnoreCase("DoorOpen"))
		{
			DoorTable.getInstance().getDoor(20160005).openMe();
		}
		else if(event.equalsIgnoreCase("DoorClose"))
		{
			DoorTable.getInstance().getDoor(20160005).closeMe();
		}
		else if(event.equalsIgnoreCase("Talk"))
		{
			if(Rnd.get(100) < 40)
			{
				npc.broadcastPacket(new NpcSay(npc.getObjectId(), 0, npc.getNpcId(), BENOM_SPEAK[Rnd.get(BENOM_SPEAK.length)]));
			}
		}
		else if (event.equalsIgnoreCase("BenomBossDespawn"))
		{
			GrandBossManager.setBossStatus(BENOM,ALIVE);
			BenomIsSpawned = 0;
			unSpawnNpc(BENOM);
		}

		return super.onAdvEvent(event, npc, player);
	}

	@Override
	public String onAggroRangeEnter(L2Npc npc, L2PcInstance player, boolean isPet)
	{
		cancelQuestTimer("BenomWalk", npc, null);
		cancelQuestTimer("BenomWalkFinish", npc, null);
		startQuestTimer("Attacking", 100, npc, player, false);
		return super.onAggroRangeEnter(npc, player, isPet);
	}

	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isPet)
	{
		GrandBossManager.setBossStatus(BENOM,DEAD);
		cancelQuestTimer("BenomWalk", npc, null);
		cancelQuestTimer("BenomWalkFinish", npc, null);
		cancelQuestTimer("BenomBossDespawn", npc, null);
		cancelQuestTimer("Talk", npc, null);
		cancelQuestTimer("Attacking", npc, null);
		return super.onKill(npc, killer, isPet);
	}

	private void unSpawnNpc(int npcId)
	{
		for(L2Spawn spawn : SpawnTable.getInstance().getSpawnTable())
		{
			if(spawn.getNpcId() == npcId)
			{
				SpawnTable.getInstance().deleteSpawn(spawn, false);
				L2Npc npc = spawn.getLastSpawn();
				npc.deleteMe();
			}
		}
	}
	
	public static void main(String[] args)
	{
		new Benom(Benom.class.getSimpleName(), "ai/individual");
	}
}