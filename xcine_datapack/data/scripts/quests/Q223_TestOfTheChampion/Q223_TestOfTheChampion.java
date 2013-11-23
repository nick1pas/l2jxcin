/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package quests.Q223_TestOfTheChampion;

import net.xcine.gameserver.ai.CtrlIntention;
import net.xcine.gameserver.model.actor.L2Attackable;
import net.xcine.gameserver.model.actor.L2Character;
import net.xcine.gameserver.model.actor.L2Npc;
import net.xcine.gameserver.model.actor.instance.L2PcInstance;
import net.xcine.gameserver.model.base.ClassId;
import net.xcine.gameserver.model.quest.Quest;
import net.xcine.gameserver.model.quest.QuestState;
import net.xcine.util.Rnd;

public class Q223_TestOfTheChampion extends Quest
{
	private static final String qn = "Q223_TestOfTheChampion";
	
	// Items
	private static final int MARK_OF_CHAMPION = 3276;
	private static final int ASCALONS_LETTER1 = 3277;
	private static final int MASONS_LETTER = 3278;
	private static final int IRON_ROSE_RING = 3279;
	private static final int ASCALONS_LETTER2 = 3280;
	private static final int WHITE_ROSE_INSIGNIA = 3281;
	private static final int GROOTS_LETTER = 3282;
	private static final int ASCALONS_LETTER3 = 3283;
	private static final int MOUENS_ORDER1 = 3284;
	private static final int MOUENS_ORDER2 = 3285;
	private static final int MOUENS_LETTER = 3286;
	private static final int HARPYS_EGG = 3287;
	private static final int MEDUSA_VENOM = 3288;
	private static final int WINDSUS_BILE = 3289;
	private static final int BLOODY_AXE_HEAD = 3290;
	private static final int ROAD_RATMAN_HEAD = 3291;
	private static final int LETO_LIZARDMAN_FANG = 3292;
	
	// NPCs
	private static final int Ascalon = 30624;
	private static final int Groot = 30093;
	private static final int Mouen = 30196;
	private static final int Mason = 30625;
	
	// Monsters
	private static final int Harpy = 20145;
	private static final int HarpyMatriarch = 27088;
	private static final int Medusa = 20158;
	private static final int Windsus = 20553;
	private static final int RoadCollector = 27089;
	private static final int RoadScavenger = 20551;
	private static final int LetoLizardman = 20577;
	private static final int LetoLizardmanArcher = 20578;
	private static final int LetoLizardmanSoldier = 20579;
	private static final int LetoLizardmanWarrior = 20580;
	private static final int LetoLizardmanShaman = 20581;
	private static final int LetoLizardmanOverlord = 20582;
	private static final int BloodyAxeElite = 20780;
	
	public Q223_TestOfTheChampion(int questId, String name, String descr)
	{
		super(questId, name, descr);
		
		questItemIds = new int[]
		{
			MASONS_LETTER,
			MEDUSA_VENOM,
			WINDSUS_BILE,
			WHITE_ROSE_INSIGNIA,
			HARPYS_EGG,
			GROOTS_LETTER,
			MOUENS_LETTER,
			ASCALONS_LETTER1,
			IRON_ROSE_RING,
			BLOODY_AXE_HEAD,
			ASCALONS_LETTER2,
			ASCALONS_LETTER3,
			MOUENS_ORDER1,
			ROAD_RATMAN_HEAD,
			MOUENS_ORDER2,
			LETO_LIZARDMAN_FANG
		};
		
		addStartNpc(Ascalon);
		addTalkId(Ascalon, Groot, Mouen, Mason);
		
		addAttackId(Harpy, RoadScavenger);
		addKillId(Harpy, Medusa, HarpyMatriarch, RoadCollector, RoadScavenger, Windsus, LetoLizardman, LetoLizardmanArcher, LetoLizardmanSoldier, LetoLizardmanWarrior, LetoLizardmanShaman, LetoLizardmanOverlord, BloodyAxeElite);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = event;
		QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;
		
		if (event.equals("30624-06.htm"))
		{
			st.set("cond", "1");
			st.setState(STATE_STARTED);
			st.playSound(QuestState.SOUND_ACCEPT);
			st.giveItems(ASCALONS_LETTER1, 1);
			st.giveItems(7562, 64);
		}
		else if (event.equals("30624-10.htm"))
		{
			st.set("cond", "5");
			st.playSound(QuestState.SOUND_MIDDLE);
			st.takeItems(MASONS_LETTER, -1);
			st.giveItems(ASCALONS_LETTER2, 1);
		}
		else if (event.equals("30624-14.htm"))
		{
			st.set("cond", "9");
			st.playSound(QuestState.SOUND_MIDDLE);
			st.takeItems(GROOTS_LETTER, -1);
			st.giveItems(ASCALONS_LETTER3, 1);
		}
		else if (event.equals("30625-03.htm"))
		{
			st.set("cond", "2");
			st.playSound(QuestState.SOUND_MIDDLE);
			st.takeItems(ASCALONS_LETTER1, -1);
			st.giveItems(IRON_ROSE_RING, 1);
		}
		else if (event.equals("30093-02.htm"))
		{
			st.set("cond", "6");
			st.playSound(QuestState.SOUND_MIDDLE);
			st.takeItems(ASCALONS_LETTER2, -1);
			st.giveItems(WHITE_ROSE_INSIGNIA, 1);
		}
		else if (event.equals("30196-03.htm"))
		{
			st.set("cond", "10");
			st.playSound(QuestState.SOUND_MIDDLE);
			st.takeItems(ASCALONS_LETTER3, -1);
			st.giveItems(MOUENS_ORDER1, 1);
		}
		else if (event.equals("30196-06.htm"))
		{
			st.set("cond", "12");
			st.playSound(QuestState.SOUND_MIDDLE);
			st.takeItems(MOUENS_ORDER1, -1);
			st.takeItems(ROAD_RATMAN_HEAD, -1);
			st.giveItems(MOUENS_ORDER2, 1);
		}
		
		return htmltext;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		String htmltext = getNoQuestMsg();
		QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;
		
		switch (st.getState())
		{
			case STATE_CREATED:
				final ClassId classId = player.getClassId();
				if ((classId != ClassId.warrior && classId != ClassId.orcRaider) || st.hasQuestItems(MARK_OF_CHAMPION))
					htmltext = "30624-01.htm";
				else if (player.getLevel() < 39)
					htmltext = "30624-02.htm";
				else
					htmltext = (classId == ClassId.warrior) ? "30624-03.htm" : "30624-04.htm";
				break;
			
			case STATE_STARTED:
				int cond = st.getInt("cond");
				switch (npc.getNpcId())
				{
					case Ascalon:
						if (cond == 1)
							htmltext = "30624-07.htm";
						else if (cond > 1 && cond < 4)
							htmltext = "30624-08.htm";
						else if (cond == 4)
							htmltext = "30624-09.htm";
						else if (cond == 5)
							htmltext = "30624-11.htm";
						else if (cond > 5 && cond < 8)
							htmltext = "30624-12.htm";
						else if (cond == 8)
							htmltext = "30624-13.htm";
						else if (cond == 9)
							htmltext = "30624-15.htm";
						else if (cond > 9 && cond < 14)
							htmltext = "30624-16.htm";
						else if (cond == 14)
						{
							htmltext = "30624-17.htm";
							st.takeItems(MOUENS_LETTER, -1);
							st.giveItems(MARK_OF_CHAMPION, 1);
							st.rewardExpAndSp(117454, 25000);
							st.playSound(QuestState.SOUND_FINISH);
							st.exitQuest(true);
						}
						break;
					
					case Mason:
						if (cond == 1)
							htmltext = "30625-01.htm";
						else if (cond == 2)
							htmltext = "30625-04.htm";
						else if (cond == 3)
						{
							htmltext = "30625-05.htm";
							st.set("cond", "4");
							st.playSound(QuestState.SOUND_MIDDLE);
							st.takeItems(BLOODY_AXE_HEAD, -1);
							st.takeItems(IRON_ROSE_RING, -1);
							st.giveItems(MASONS_LETTER, 1);
						}
						else if (cond == 4)
							htmltext = "30625-06.htm";
						else if (cond > 4)
							htmltext = "30625-07.htm";
						break;
					
					case Groot:
						if (cond == 5)
							htmltext = "30093-01.htm";
						else if (cond == 6)
							htmltext = "30093-03.htm";
						else if (cond == 7)
						{
							htmltext = "30093-04.htm";
							st.set("cond", "8");
							st.playSound(QuestState.SOUND_MIDDLE);
							st.takeItems(WHITE_ROSE_INSIGNIA, -1);
							st.takeItems(HARPYS_EGG, -1);
							st.takeItems(MEDUSA_VENOM, -1);
							st.takeItems(WINDSUS_BILE, -1);
							st.giveItems(GROOTS_LETTER, 1);
						}
						else if (cond == 8)
							htmltext = "30093-05.htm";
						else if (cond > 8)
							htmltext = "30093-06.htm";
						break;
					
					case Mouen:
						if (cond == 9)
							htmltext = "30196-01.htm";
						else if (cond == 10)
							htmltext = "30196-04.htm";
						else if (cond == 11)
							htmltext = "30196-05.htm";
						else if (cond == 12)
							htmltext = "30196-07.htm";
						else if (cond == 13)
						{
							htmltext = "30196-08.htm";
							st.set("cond", "14");
							st.playSound(QuestState.SOUND_MIDDLE);
							st.takeItems(LETO_LIZARDMAN_FANG, -1);
							st.takeItems(MOUENS_ORDER2, -1);
							st.giveItems(MOUENS_LETTER, 1);
						}
						else if (cond > 13)
							htmltext = "30196-09.htm";
						break;
				}
				break;
		}
		
		return htmltext;
	}
	
	@Override
	public String onAttack(L2Npc npc, L2PcInstance attacker, int damage, boolean isPet)
	{
		QuestState st = checkPlayerState(attacker, npc, STATE_STARTED);
		if (st == null)
			return null;
		
		switch (npc.getNpcId())
		{
			case Harpy: // Possibility to spawn an Harpy Matriarch.
				if (st.getInt("cond") == 6 && Rnd.nextBoolean() && !npc.isScriptValue(1))
				{
					final L2Character originalKiller = isPet ? attacker.getPet() : attacker;
					
					// Spawn one or two matriarchs.
					for (int i = 1; i < ((Rnd.get(10) < 7) ? 2 : 3); i++)
					{
						final L2Attackable collector = (L2Attackable) addSpawn(HarpyMatriarch, npc, true, 0, false);
						
						collector.setRunning();
						collector.addDamageHate(originalKiller, 0, 999);
						collector.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, originalKiller);
					}
					npc.setScriptValue(1);
				}
				break;
			
			case RoadScavenger: // Possibility to spawn a Road Collector.
				if (st.getInt("cond") == 10 && Rnd.nextBoolean() && !npc.isScriptValue(1))
				{
					final L2Character originalKiller = isPet ? attacker.getPet() : attacker;
					
					// Spawn one or two collectors.
					for (int i = 1; i < ((Rnd.get(10) < 7) ? 2 : 3); i++)
					{
						final L2Attackable collector = (L2Attackable) addSpawn(RoadCollector, npc, true, 0, false);
						
						collector.setRunning();
						collector.addDamageHate(originalKiller, 0, 999);
						collector.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, originalKiller);
					}
					npc.setScriptValue(1);
				}
				break;
		}
		
		return null;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance player, boolean isPet)
	{
		QuestState st = checkPlayerState(player, npc, STATE_STARTED);
		if (st == null)
			return null;
		
		switch (npc.getNpcId())
		{
			case BloodyAxeElite:
				if (st.getInt("cond") == 2)
					if (st.dropItemsAlways(BLOODY_AXE_HEAD, 1, 100))
						st.set("cond", "3");
				break;
			
			case Harpy:
			case HarpyMatriarch:
				if (st.getInt("cond") == 6)
					if (st.dropItems(HARPYS_EGG, Rnd.get(2, 3), 30, 500000))
						if (st.getQuestItemsCount(MEDUSA_VENOM) == 30 && st.getQuestItemsCount(WINDSUS_BILE) == 30)
							st.set("cond", "7");
				break;
			
			case Medusa:
				if (st.getInt("cond") == 6)
					if (st.dropItems(MEDUSA_VENOM, Rnd.get(2, 3), 30, 500000))
						if (st.getQuestItemsCount(HARPYS_EGG) == 30 && st.getQuestItemsCount(WINDSUS_BILE) == 30)
							st.set("cond", "7");
				break;
			
			case Windsus:
				if (st.getInt("cond") == 6)
					if (st.dropItems(WINDSUS_BILE, Rnd.get(2, 3), 30, 500000))
						if (st.getQuestItemsCount(HARPYS_EGG) == 30 && st.getQuestItemsCount(MEDUSA_VENOM) == 30)
							st.set("cond", "7");
				break;
			
			case RoadCollector:
			case RoadScavenger:
				if (st.getInt("cond") == 10)
					if (st.dropItemsAlways(ROAD_RATMAN_HEAD, 1, 100))
						st.set("cond", "11");
				break;
			
			case LetoLizardman:
			case LetoLizardmanArcher:
			case LetoLizardmanSoldier:
			case LetoLizardmanWarrior:
			case LetoLizardmanShaman:
			case LetoLizardmanOverlord:
				if (st.getInt("cond") == 12)
					if (st.dropItems(LETO_LIZARDMAN_FANG, 1, 100, 500000 + (50000 * (npc.getNpcId() - 20577))))
						st.set("cond", "13");
				break;
		}
		
		return null;
	}
	
	public static void main(String[] args)
	{
		new Q223_TestOfTheChampion(223, qn, "Test of the Champion");
	}
}