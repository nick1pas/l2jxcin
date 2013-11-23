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
package quests.Q215_TrialOfThePilgrim;

import net.xcine.gameserver.model.actor.L2Npc;
import net.xcine.gameserver.model.actor.instance.L2PcInstance;
import net.xcine.gameserver.model.base.ClassId;
import net.xcine.gameserver.model.quest.Quest;
import net.xcine.gameserver.model.quest.QuestState;
import net.xcine.util.Rnd;

public class Q215_TrialOfThePilgrim extends Quest
{
	private static final String qn = "Q215_TrialOfThePilgrim";
	
	// Items
	private static final int VoucherOfTrial = 2723;
	private static final int EssenceOfFlame = 2725;
	private static final int SpiritOfFlame = 2724;
	private static final int TagOfRumor = 2733;
	private static final int BookOfGerald = 2726;
	private static final int GrayBadge = 2727;
	private static final int PictureOfNahir = 2728;
	private static final int HairOfNahir = 2729;
	private static final int StatueOfEinhasad = 2730;
	private static final int DebrisOfWillow = 2732;
	private static final int BookOfDarkness = 2731;
	private static final int BookOfSage = 2722;
	private static final int MarkOfPilgrim = 2721;
	
	// NPCs
	private static final int Santiago = 30648;
	private static final int Tanapi = 30571;
	private static final int AncestorMartankus = 30649;
	private static final int GauriTwinklerock = 30550;
	private static final int Dorf = 30651;
	private static final int Gerald = 30650;
	private static final int Primos = 30117;
	private static final int Petron = 30036;
	private static final int Andellia = 30362;
	private static final int Uruha = 30652;
	private static final int Casian = 30612;
	
	// Monsters
	private static final int LavaSalamander = 27116;
	private static final int Nahir = 27117;
	private static final int BlackWillow = 27118;
	
	public Q215_TrialOfThePilgrim(int questId, String name, String descr)
	{
		super(questId, name, descr);
		
		questItemIds = new int[]
		{
			VoucherOfTrial,
			EssenceOfFlame,
			SpiritOfFlame,
			TagOfRumor,
			BookOfGerald,
			GrayBadge,
			PictureOfNahir,
			HairOfNahir,
			StatueOfEinhasad,
			DebrisOfWillow,
			BookOfDarkness,
			BookOfSage
		};
		
		addStartNpc(Santiago);
		addTalkId(Santiago, Tanapi, AncestorMartankus, GauriTwinklerock, Dorf, Gerald, Primos, Petron, Andellia, Uruha, Casian);
		
		addKillId(LavaSalamander, Nahir, BlackWillow);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = event;
		
		QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("30648-04.htm"))
		{
			st.set("cond", "1");
			st.setState(STATE_STARTED);
			st.playSound(QuestState.SOUND_ACCEPT);
			st.giveItems(VoucherOfTrial, 1);
			st.giveItems(7562, 49);
		}
		else if (event.equalsIgnoreCase("30649-04.htm"))
		{
			st.set("cond", "5");
			st.playSound(QuestState.SOUND_MIDDLE);
			st.takeItems(EssenceOfFlame, -1);
			st.giveItems(SpiritOfFlame, 1);
		}
		else if (event.equalsIgnoreCase("30650-02.htm"))
		{
			if (st.getQuestItemsCount(57) >= 100000)
			{
				st.takeItems(57, 100000);
				st.giveItems(BookOfGerald, 1);
			}
			else
				htmltext = "30650-03.htm";
		}
		else if (event.equalsIgnoreCase("30652-02.htm"))
		{
			st.set("cond", "15");
			st.playSound(QuestState.SOUND_MIDDLE);
			st.takeItems(DebrisOfWillow, -1);
			st.giveItems(BookOfDarkness, 1);
		}
		else if (event.equalsIgnoreCase("30362-04.htm"))
		{
			st.set("cond", "16");
			st.playSound(QuestState.SOUND_MIDDLE);
		}
		else if (event.equalsIgnoreCase("30362-05.htm"))
		{
			st.set("cond", "16");
			st.playSound(QuestState.SOUND_MIDDLE);
			st.takeItems(BookOfDarkness, -1);
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
				if (st.hasQuestItems(MarkOfPilgrim))
					htmltext = getAlreadyCompletedMsg();
				else if (player.getClassId() != ClassId.cleric && player.getClassId() != ClassId.oracle && player.getClassId() != ClassId.shillienOracle && player.getClassId() != ClassId.orcShaman)
					htmltext = "30648-02.htm";
				else if (player.getLevel() < 35)
					htmltext = "30648-01.htm";
				else
					htmltext = "30648-03.htm";
				break;
			
			case STATE_STARTED:
				int cond = st.getInt("cond");
				switch (npc.getNpcId())
				{
					case Santiago:
						if (cond < 17)
							htmltext = "30648-09.htm";
						else if (cond == 17)
						{
							htmltext = "30648-10.htm";
							st.takeItems(BookOfSage, -1);
							st.giveItems(MarkOfPilgrim, 1);
							st.rewardExpAndSp(77382, 16000);
							st.playSound(QuestState.SOUND_FINISH);
							st.exitQuest(true);
						}
						break;
					
					case Tanapi:
						if (cond == 1)
						{
							htmltext = "30571-01.htm";
							st.set("cond", "2");
							st.playSound(QuestState.SOUND_MIDDLE);
							st.takeItems(VoucherOfTrial, -1);
						}
						else if (cond < 5)
							htmltext = "30571-02.htm";
						else if (cond >= 5)
						{
							htmltext = "30571-03.htm";
							
							if (cond == 5)
							{
								st.set("cond", "6");
								st.playSound(QuestState.SOUND_MIDDLE);
							}
						}
						break;
					
					case AncestorMartankus:
						if (cond == 2)
						{
							htmltext = "30649-01.htm";
							st.set("cond", "3");
							st.playSound(QuestState.SOUND_MIDDLE);
						}
						else if (cond == 3)
							htmltext = "30649-02.htm";
						else if (cond == 4)
							htmltext = "30649-03.htm";
						break;
					
					case GauriTwinklerock:
						if (cond == 6)
						{
							htmltext = "30550-01.htm";
							st.set("cond", "7");
							st.playSound(QuestState.SOUND_MIDDLE);
							st.giveItems(TagOfRumor, 1);
						}
						else if (cond > 6)
							htmltext = "30550-02.htm";
						break;
					
					case Dorf:
						if (cond == 7)
						{
							htmltext = (!st.hasQuestItems(BookOfGerald)) ? "30651-01.htm" : "30651-02.htm";
							st.set("cond", "8");
							st.playSound(QuestState.SOUND_MIDDLE);
							st.takeItems(TagOfRumor, -1);
							st.giveItems(GrayBadge, 1);
						}
						else if (cond > 7)
							htmltext = "30651-03.htm";
						break;
					
					case Gerald:
						if (cond == 7 && !st.hasQuestItems(BookOfGerald))
							htmltext = "30650-01.htm";
						else if (cond == 8 && st.hasQuestItems(BookOfGerald))
						{
							htmltext = "30650-04.htm";
							st.takeItems(BookOfGerald, -1);
							st.giveItems(57, 100000);
						}
						break;
					
					case Primos:
						if (cond == 8)
						{
							htmltext = "30117-01.htm";
							st.set("cond", "9");
							st.playSound(QuestState.SOUND_MIDDLE);
						}
						else if (cond > 8)
							htmltext = "30117-02.htm";
						break;
					
					case Petron:
						if (cond == 9)
						{
							htmltext = "30036-01.htm";
							st.set("cond", "10");
							st.playSound(QuestState.SOUND_MIDDLE);
							st.giveItems(PictureOfNahir, 1);
						}
						else if (cond == 10)
							htmltext = "30036-02.htm";
						else if (cond == 11)
						{
							htmltext = "30036-03.htm";
							st.set("cond", "12");
							st.playSound(QuestState.SOUND_MIDDLE);
							st.takeItems(HairOfNahir, -1);
							st.takeItems(PictureOfNahir, -1);
							st.giveItems(StatueOfEinhasad, 1);
						}
						else if (cond > 11)
							htmltext = "30036-04.htm";
						break;
					
					case Andellia:
						if (cond == 12)
						{
							if (player.getLevel() < 36)
								htmltext = "30362-01a.htm";
							else
							{
								htmltext = "30362-01.htm";
								st.set("cond", "13");
								st.playSound(QuestState.SOUND_MIDDLE);
							}
						}
						else if (cond == 13)
							htmltext = (Rnd.nextBoolean()) ? "30362-02.htm" : "30362-02a.htm";
						else if (cond == 14)
							htmltext = "30362-07.htm";
						else if (cond == 15)
							htmltext = "30362-03.htm";
						else if (cond == 16)
							htmltext = "30362-06.htm";
						break;
					
					case Uruha:
						if (cond == 14)
							htmltext = "30652-01.htm";
						else if (cond == 15)
							htmltext = "30652-03.htm";
						break;
					
					case Casian:
						if (cond == 16)
						{
							htmltext = "30612-01.htm";
							st.set("cond", "17");
							st.playSound(QuestState.SOUND_MIDDLE);
							st.takeItems(BookOfDarkness, -1);
							st.takeItems(GrayBadge, -1);
							st.takeItems(SpiritOfFlame, -1);
							st.takeItems(StatueOfEinhasad, -1);
							st.giveItems(BookOfSage, 1);
						}
						else if (cond == 17)
							htmltext = "30612-02.htm";
						break;
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
		
		switch (npc.getNpcId())
		{
			case LavaSalamander:
				if (st.getInt("cond") == 3 && st.dropItems(EssenceOfFlame, 1, 1, 300000))
					st.set("cond", "4");
				break;
			
			case Nahir:
				if (st.getInt("cond") == 10 && st.dropItemsAlways(HairOfNahir, 1, 1))
					st.set("cond", "11");
				break;
			
			case BlackWillow:
				if (st.getInt("cond") == 13 && st.dropItems(DebrisOfWillow, 1, 1, 200000))
					st.set("cond", "14");
				break;
		}
		
		return null;
	}
	
	public static void main(String[] args)
	{
		new Q215_TrialOfThePilgrim(215, qn, "Trial of the Pilgrim");
	}
}
