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
package quests.Q222_TestOfTheDuelist;

import net.xcine.gameserver.model.actor.L2Npc;
import net.xcine.gameserver.model.actor.instance.L2PcInstance;
import net.xcine.gameserver.model.base.Race;
import net.xcine.gameserver.model.quest.Quest;
import net.xcine.gameserver.model.quest.QuestState;

public class Q222_TestOfTheDuelist extends Quest
{
	private static final String qn = "Q222_TestOfTheDuelist";
	
	private static final int Kaien = 30623;
	
	// items
	private static final int OrderGludio = 2763;
	private static final int OrderDion = 2764;
	private static final int OrderGiran = 2765;
	private static final int OrderOren = 2766;
	private static final int OrderAden = 2767;
	private static final int PunchersShard = 2768;
	private static final int NobleAntsFeeler = 2769;
	private static final int DronesChitin = 2770;
	private static final int DeadSeekerFang = 2771;
	private static final int OverlordNecklace = 2772;
	private static final int FetteredSoulsChain = 2773;
	private static final int ChiefsAmulet = 2774;
	private static final int EnchantedEyeMeat = 2775;
	private static final int TamrinOrcsRing = 2776;
	private static final int TamrinOrcsArrow = 2777;
	private static final int FinalOrder = 2778;
	private static final int ExcurosSkin = 2779;
	private static final int KratorsShard = 2780;
	private static final int GrandisSkin = 2781;
	private static final int TimakOrcsBelt = 2782;
	private static final int LakinsMace = 2783;
	
	// reward
	private static final int MarkOfDuelist = 2762;
	
	// monsters
	private static final int Puncher = 20085;
	private static final int NobleAntLeader = 20090;
	private static final int MarshStakatoDrone = 20234;
	private static final int DeadSeeker = 20202;
	private static final int BrekaOrcOverlord = 20270;
	private static final int FetteredSoul = 20552;
	private static final int LetoLizardmanOverlord = 20582;
	private static final int EnchantedMonstereye = 20564;
	private static final int TamlinOrc = 20601;
	private static final int TamlinOrcArcher = 20602;
	private static final int Excuro = 20214;
	private static final int Krator = 20217;
	private static final int Grandis = 20554;
	private static final int TimakOrcOverlord = 20588;
	private static final int Lakin = 20604;
	
	public Q222_TestOfTheDuelist(int questId, String name, String descr)
	{
		super(questId, name, descr);
		
		addStartNpc(Kaien);
		addTalkId(Kaien);
		
		addKillId(Puncher, NobleAntLeader, MarshStakatoDrone, DeadSeeker, BrekaOrcOverlord, FetteredSoul, LetoLizardmanOverlord, EnchantedMonstereye, TamlinOrc, TamlinOrcArcher, Excuro, Krator, Grandis, TimakOrcOverlord, Lakin);
		
		questItemIds = new int[]
		{
			OrderGludio,
			OrderDion,
			OrderGiran,
			OrderOren,
			OrderAden,
			FinalOrder,
			PunchersShard,
			NobleAntsFeeler,
			DronesChitin,
			DeadSeekerFang,
			OverlordNecklace,
			FetteredSoulsChain,
			ChiefsAmulet,
			EnchantedEyeMeat,
			TamrinOrcsRing,
			TamrinOrcsArrow,
			ExcurosSkin,
			KratorsShard,
			GrandisSkin,
			TimakOrcsBelt,
			LakinsMace,
		};
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = event;
		QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("30623-04.htm"))
		{
			if (player.getRace() == Race.Orc)
				htmltext = "30623-05.htm";
		}
		else if (event.equalsIgnoreCase("30623-07.htm"))
		{
			st.set("cond", "1");
			st.set("cond", "2");
			st.setState(STATE_STARTED);
			st.playSound(QuestState.SOUND_ACCEPT);
			
			st.giveItems(OrderGludio, 1);
			st.giveItems(OrderDion, 1);
			st.giveItems(OrderGiran, 1);
			st.giveItems(OrderOren, 1);
			st.giveItems(OrderAden, 1);
			st.giveItems(7562, 72);
		}
		else if (event.equalsIgnoreCase("30623-16.htm"))
		{
			if (st.getInt("cond") == 3)
			{
				st.set("cond", "4");
				st.playSound(QuestState.SOUND_MIDDLE);
				
				st.takeItems(OrderGludio, -1);
				st.takeItems(OrderDion, -1);
				st.takeItems(OrderGiran, -1);
				st.takeItems(OrderOren, -1);
				st.takeItems(OrderAden, -1);
				
				st.takeItems(PunchersShard, -1);
				st.takeItems(NobleAntsFeeler, -1);
				st.takeItems(DronesChitin, -1);
				st.takeItems(DeadSeekerFang, -1);
				st.takeItems(OverlordNecklace, -1);
				st.takeItems(FetteredSoulsChain, -1);
				st.takeItems(ChiefsAmulet, -1);
				st.takeItems(EnchantedEyeMeat, -1);
				st.takeItems(TamrinOrcsRing, -1);
				st.takeItems(TamrinOrcsArrow, -1);
				
				st.giveItems(FinalOrder, 1);
			}
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
				if (st.hasQuestItems(MarkOfDuelist))
					return getAlreadyCompletedMsg();
				
				final int classId = player.getClassId().getId();
				if (classId != 0x01 && classId != 0x2f && classId != 0x13 && classId != 0x20)
					htmltext = "30623-02.htm";
				else if (player.getLevel() < 39)
					htmltext = "30623-01.htm";
				else
					htmltext = "30623-03.htm";
				break;
			
			case STATE_STARTED:
				int cond = st.getInt("cond");
				if (cond == 2)
					htmltext = "30623-07a.htm";
				else if (cond == 3)
					htmltext = "30623-13.htm";
				else if (cond == 4)
					htmltext = "30623-17.htm";
				else if (cond == 5)
				{
					htmltext = "30623-18.htm";
					
					st.takeItems(FinalOrder, -1);
					st.takeItems(ExcurosSkin, -1);
					st.takeItems(KratorsShard, -1);
					st.takeItems(GrandisSkin, -1);
					st.takeItems(TimakOrcsBelt, -1);
					st.takeItems(LakinsMace, -1);
					
					st.giveItems(MarkOfDuelist, 1);
					st.rewardExpAndSp(47015, 20000);
					
					st.playSound(QuestState.SOUND_FINISH);
					st.exitQuest(true);
				}
				break;
		}
		
		return htmltext;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance player, boolean isPet)
	{
		QuestState st = checkPlayerState(player, npc, STATE_STARTED);
		if (st == null)
			return null;
		
		if (st.getInt("cond") == 2)
		{
			switch (npc.getNpcId())
			{
				case Puncher:
					if (st.dropItemsAlways(PunchersShard, 1, 10))
					{
						if (st.getQuestItemsCount(NobleAntsFeeler) >= 10 && st.getQuestItemsCount(DronesChitin) >= 10 && st.getQuestItemsCount(DeadSeekerFang) >= 10 && st.getQuestItemsCount(OverlordNecklace) >= 10 && st.getQuestItemsCount(FetteredSoulsChain) >= 10 && st.getQuestItemsCount(ChiefsAmulet) >= 10 && st.getQuestItemsCount(EnchantedEyeMeat) >= 10 && st.getQuestItemsCount(TamrinOrcsRing) >= 10 && st.getQuestItemsCount(TamrinOrcsArrow) >= 10)
							st.set("cond", "3");
					}
					break;
				
				case NobleAntLeader:
					if (st.dropItemsAlways(NobleAntsFeeler, 1, 10))
					{
						if (st.getQuestItemsCount(PunchersShard) >= 10 && st.getQuestItemsCount(DronesChitin) >= 10 && st.getQuestItemsCount(DeadSeekerFang) >= 10 && st.getQuestItemsCount(OverlordNecklace) >= 10 && st.getQuestItemsCount(FetteredSoulsChain) >= 10 && st.getQuestItemsCount(ChiefsAmulet) >= 10 && st.getQuestItemsCount(EnchantedEyeMeat) >= 10 && st.getQuestItemsCount(TamrinOrcsRing) >= 10 && st.getQuestItemsCount(TamrinOrcsArrow) >= 10)
							st.set("cond", "3");
					}
					break;
				
				case MarshStakatoDrone:
					if (st.dropItemsAlways(DronesChitin, 1, 10))
					{
						if (st.getQuestItemsCount(PunchersShard) >= 10 && st.getQuestItemsCount(NobleAntsFeeler) >= 10 && st.getQuestItemsCount(DeadSeekerFang) >= 10 && st.getQuestItemsCount(OverlordNecklace) >= 10 && st.getQuestItemsCount(FetteredSoulsChain) >= 10 && st.getQuestItemsCount(ChiefsAmulet) >= 10 && st.getQuestItemsCount(EnchantedEyeMeat) >= 10 && st.getQuestItemsCount(TamrinOrcsRing) >= 10 && st.getQuestItemsCount(TamrinOrcsArrow) >= 10)
							st.set("cond", "3");
					}
					break;
				
				case DeadSeeker:
					if (st.dropItemsAlways(DeadSeekerFang, 1, 10))
					{
						if (st.getQuestItemsCount(PunchersShard) >= 10 && st.getQuestItemsCount(NobleAntsFeeler) >= 10 && st.getQuestItemsCount(DronesChitin) >= 10 && st.getQuestItemsCount(OverlordNecklace) >= 10 && st.getQuestItemsCount(FetteredSoulsChain) >= 10 && st.getQuestItemsCount(ChiefsAmulet) >= 10 && st.getQuestItemsCount(EnchantedEyeMeat) >= 10 && st.getQuestItemsCount(TamrinOrcsRing) >= 10 && st.getQuestItemsCount(TamrinOrcsArrow) >= 10)
							st.set("cond", "3");
					}
					break;
				
				case BrekaOrcOverlord:
					if (st.dropItemsAlways(OverlordNecklace, 1, 10))
					{
						if (st.getQuestItemsCount(PunchersShard) >= 10 && st.getQuestItemsCount(NobleAntsFeeler) >= 10 && st.getQuestItemsCount(DronesChitin) >= 10 && st.getQuestItemsCount(DeadSeekerFang) >= 10 && st.getQuestItemsCount(FetteredSoulsChain) >= 10 && st.getQuestItemsCount(ChiefsAmulet) >= 10 && st.getQuestItemsCount(EnchantedEyeMeat) >= 10 && st.getQuestItemsCount(TamrinOrcsRing) >= 10 && st.getQuestItemsCount(TamrinOrcsArrow) >= 10)
							st.set("cond", "3");
					}
					break;
				
				case FetteredSoul:
					if (st.dropItemsAlways(FetteredSoulsChain, 1, 10))
					{
						if (st.getQuestItemsCount(PunchersShard) >= 10 && st.getQuestItemsCount(NobleAntsFeeler) >= 10 && st.getQuestItemsCount(DronesChitin) >= 10 && st.getQuestItemsCount(DeadSeekerFang) >= 10 && st.getQuestItemsCount(OverlordNecklace) >= 10 && st.getQuestItemsCount(ChiefsAmulet) >= 10 && st.getQuestItemsCount(EnchantedEyeMeat) >= 10 && st.getQuestItemsCount(TamrinOrcsRing) >= 10 && st.getQuestItemsCount(TamrinOrcsArrow) >= 10)
							st.set("cond", "3");
					}
					break;
				
				case LetoLizardmanOverlord:
					if (st.dropItemsAlways(ChiefsAmulet, 1, 10))
					{
						if (st.getQuestItemsCount(PunchersShard) >= 10 && st.getQuestItemsCount(NobleAntsFeeler) >= 10 && st.getQuestItemsCount(DronesChitin) >= 10 && st.getQuestItemsCount(DeadSeekerFang) >= 10 && st.getQuestItemsCount(OverlordNecklace) >= 10 && st.getQuestItemsCount(FetteredSoulsChain) >= 10 && st.getQuestItemsCount(EnchantedEyeMeat) >= 10 && st.getQuestItemsCount(TamrinOrcsRing) >= 10 && st.getQuestItemsCount(TamrinOrcsArrow) >= 10)
							st.set("cond", "3");
					}
					break;
				
				case EnchantedMonstereye:
					if (st.dropItemsAlways(EnchantedEyeMeat, 1, 10))
					{
						if (st.getQuestItemsCount(PunchersShard) >= 10 && st.getQuestItemsCount(NobleAntsFeeler) >= 10 && st.getQuestItemsCount(DronesChitin) >= 10 && st.getQuestItemsCount(DeadSeekerFang) >= 10 && st.getQuestItemsCount(OverlordNecklace) >= 10 && st.getQuestItemsCount(FetteredSoulsChain) >= 10 && st.getQuestItemsCount(ChiefsAmulet) >= 10 && st.getQuestItemsCount(TamrinOrcsRing) >= 10 && st.getQuestItemsCount(TamrinOrcsArrow) >= 10)
							st.set("cond", "3");
					}
					break;
				
				case TamlinOrc:
					if (st.dropItemsAlways(TamrinOrcsRing, 1, 10))
					{
						if (st.getQuestItemsCount(PunchersShard) >= 10 && st.getQuestItemsCount(NobleAntsFeeler) >= 10 && st.getQuestItemsCount(DronesChitin) >= 10 && st.getQuestItemsCount(DeadSeekerFang) >= 10 && st.getQuestItemsCount(OverlordNecklace) >= 10 && st.getQuestItemsCount(FetteredSoulsChain) >= 10 && st.getQuestItemsCount(ChiefsAmulet) >= 10 && st.getQuestItemsCount(EnchantedEyeMeat) >= 10 && st.getQuestItemsCount(TamrinOrcsArrow) >= 10)
							st.set("cond", "3");
					}
					break;
				
				case TamlinOrcArcher:
					if (st.dropItemsAlways(TamrinOrcsArrow, 1, 10))
					{
						if (st.getQuestItemsCount(PunchersShard) >= 10 && st.getQuestItemsCount(NobleAntsFeeler) >= 10 && st.getQuestItemsCount(DronesChitin) >= 10 && st.getQuestItemsCount(DeadSeekerFang) >= 10 && st.getQuestItemsCount(OverlordNecklace) >= 10 && st.getQuestItemsCount(FetteredSoulsChain) >= 10 && st.getQuestItemsCount(ChiefsAmulet) >= 10 && st.getQuestItemsCount(EnchantedEyeMeat) >= 10 && st.getQuestItemsCount(TamrinOrcsRing) >= 10)
							st.set("cond", "3");
					}
					break;
			}
		}
		else if (st.getInt("cond") == 4)
		{
			switch (npc.getNpcId())
			{
				case Excuro:
					if (st.dropItemsAlways(ExcurosSkin, 1, 3))
					{
						if (st.getQuestItemsCount(KratorsShard) >= 3 && st.getQuestItemsCount(LakinsMace) >= 3 && st.getQuestItemsCount(GrandisSkin) >= 3 && st.getQuestItemsCount(TimakOrcsBelt) >= 3)
							st.set("cond", "5");
					}
					break;
				
				case Krator:
					if (st.dropItemsAlways(KratorsShard, 1, 3))
					{
						if (st.getQuestItemsCount(ExcurosSkin) >= 3 && st.getQuestItemsCount(LakinsMace) >= 3 && st.getQuestItemsCount(GrandisSkin) >= 3 && st.getQuestItemsCount(TimakOrcsBelt) >= 3)
							st.set("cond", "5");
					}
					break;
				
				case Lakin:
					if (st.dropItemsAlways(LakinsMace, 1, 3))
					{
						if (st.getQuestItemsCount(ExcurosSkin) >= 3 && st.getQuestItemsCount(KratorsShard) >= 3 && st.getQuestItemsCount(GrandisSkin) >= 3 && st.getQuestItemsCount(TimakOrcsBelt) >= 3)
							st.set("cond", "5");
					}
					break;
				
				case Grandis:
					if (st.dropItemsAlways(GrandisSkin, 1, 3))
					{
						if (st.getQuestItemsCount(ExcurosSkin) >= 3 && st.getQuestItemsCount(KratorsShard) >= 3 && st.getQuestItemsCount(LakinsMace) >= 3 && st.getQuestItemsCount(TimakOrcsBelt) >= 3)
							st.set("cond", "5");
					}
					break;
				
				case TimakOrcOverlord:
					if (st.dropItemsAlways(TimakOrcsBelt, 1, 3))
					{
						if (st.getQuestItemsCount(ExcurosSkin) >= 3 && st.getQuestItemsCount(KratorsShard) >= 3 && st.getQuestItemsCount(LakinsMace) >= 3 && st.getQuestItemsCount(GrandisSkin) >= 3)
							st.set("cond", "5");
					}
					break;
			}
		}
		
		return null;
	}
	
	public static void main(String[] args)
	{
		new Q222_TestOfTheDuelist(222, qn, "Test of the Duelist");
	}
}