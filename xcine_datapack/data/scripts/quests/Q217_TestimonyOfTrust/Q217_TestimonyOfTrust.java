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
package quests.Q217_TestimonyOfTrust;

import net.xcine.gameserver.model.actor.L2Npc;
import net.xcine.gameserver.model.actor.instance.L2PcInstance;
import net.xcine.gameserver.model.base.Race;
import net.xcine.gameserver.model.quest.Quest;
import net.xcine.gameserver.model.quest.QuestState;
import net.xcine.util.Rnd;

public class Q217_TestimonyOfTrust extends Quest
{
	private static final String qn = "Q217_TestimonyOfTrust";
	
	// Items
	private static final int MarkOfTrust = 2734;
	private static final int LetterToElf = 2735;
	private static final int LetterToDarkElf = 2736;
	private static final int LetterToDwarf = 2737;
	private static final int LetterToOrc = 2738;
	private static final int LetterToSeresin = 2739;
	private static final int ScrollOfDarkElfTrust = 2740;
	private static final int ScrollOfElfTrust = 2741;
	private static final int ScrollOfDwarfTrust = 2742;
	private static final int ScrollOfOrcTrust = 2743;
	private static final int RecommendationOfHollint = 2744;
	private static final int OrderOfAsterios = 2745;
	private static final int BreathOfWinds = 2746;
	private static final int SeedOfVerdure = 2747;
	private static final int LetterFromThifiell = 2748;
	private static final int BloodGuardianBasilisk = 2749;
	private static final int GiantAphid = 2750;
	private static final int StakatoFluids = 2751;
	private static final int BasiliskPlasma = 2752;
	private static final int HoneyDew = 2753;
	private static final int StakatoIchor = 2754;
	private static final int OrderOfClayton = 2755;
	private static final int ParasiteOfLota = 2756;
	private static final int LetterToManakia = 2757;
	private static final int LetterOfManakia = 2758;
	private static final int LetterToNikola = 2759;
	private static final int OrderOfNikola = 2760;
	private static final int HeartstoneOfPorta = 2761;
	
	// NPCs
	private static final int Hollint = 30191;
	private static final int Asterios = 30154;
	private static final int Thifiell = 30358;
	private static final int Clayton = 30464;
	private static final int Seresin = 30657;
	private static final int Kakai = 30565;
	private static final int Manakia = 30515;
	private static final int Lockirin = 30531;
	private static final int Nikola = 30621;
	private static final int Biotin = 30031;
	
	// Monsters
	private static final int Dryad = 20013;
	private static final int DryadElder = 20019;
	private static final int Lirein = 20036;
	private static final int LireinElder = 20044;
	private static final int ActeaOfVerdantWilds = 27121;
	private static final int LuellOfZephyrWinds = 27120;
	private static final int GuardianBasilisk = 20550;
	private static final int AntRecruit = 20082;
	private static final int AntPatrol = 20084;
	private static final int AntGuard = 20086;
	private static final int AntSoldier = 20087;
	private static final int AntWarriorCaptain = 20088;
	private static final int MarshStakato = 20157;
	private static final int MarshStakatoWorker = 20230;
	private static final int MarshStakatoSoldier = 20232;
	private static final int MarshStakatoDrone = 20234;
	private static final int Windsus = 20553;
	private static final int Porta = 20213;
	
	public Q217_TestimonyOfTrust(int questId, String name, String descr)
	{
		super(questId, name, descr);
		
		questItemIds = new int[]
		{
			LetterToElf,
			LetterToDarkElf,
			LetterToDwarf,
			LetterToOrc,
			LetterToSeresin,
			ScrollOfDarkElfTrust,
			ScrollOfElfTrust,
			ScrollOfDwarfTrust,
			ScrollOfOrcTrust,
			RecommendationOfHollint,
			OrderOfAsterios,
			BreathOfWinds,
			SeedOfVerdure,
			LetterFromThifiell,
			BloodGuardianBasilisk,
			GiantAphid,
			StakatoFluids,
			BasiliskPlasma,
			HoneyDew,
			StakatoIchor,
			OrderOfClayton,
			ParasiteOfLota,
			LetterToManakia,
			LetterOfManakia,
			LetterToNikola,
			OrderOfNikola,
			HeartstoneOfPorta
		};
		
		addStartNpc(Hollint);
		addTalkId(Hollint, Asterios, Thifiell, Clayton, Seresin, Kakai, Manakia, Lockirin, Nikola, Biotin);
		
		addKillId(Dryad, DryadElder, Lirein, LireinElder, ActeaOfVerdantWilds, LuellOfZephyrWinds, GuardianBasilisk, AntRecruit, AntPatrol, AntGuard, AntSoldier, AntWarriorCaptain, MarshStakato, MarshStakatoWorker, MarshStakatoSoldier, MarshStakatoDrone, Windsus, Porta);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = event;
		
		QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("30191-04.htm"))
		{
			st.set("cond", "1");
			st.setState(STATE_STARTED);
			st.playSound(QuestState.SOUND_ACCEPT);
			st.giveItems(LetterToElf, 1);
			st.giveItems(LetterToDarkElf, 1);
			st.giveItems(7562, 16);
		}
		else if (event.equalsIgnoreCase("30154-03.htm"))
		{
			st.set("cond", "2");
			st.playSound(QuestState.SOUND_MIDDLE);
			st.takeItems(LetterToElf, -1);
			st.giveItems(OrderOfAsterios, 1);
		}
		else if (event.equalsIgnoreCase("30358-02.htm"))
		{
			st.set("cond", "5");
			st.playSound(QuestState.SOUND_MIDDLE);
			st.takeItems(LetterToDarkElf, -1);
			st.giveItems(LetterFromThifiell, 1);
		}
		else if (event.equalsIgnoreCase("30515-02.htm"))
		{
			st.set("cond", "14");
			st.playSound(QuestState.SOUND_MIDDLE);
			st.takeItems(LetterToManakia, -1);
		}
		else if (event.equalsIgnoreCase("30531-02.htm"))
		{
			st.set("cond", "18");
			st.playSound(QuestState.SOUND_MIDDLE);
			st.takeItems(LetterToDwarf, -1);
			st.giveItems(LetterToNikola, 1);
		}
		else if (event.equalsIgnoreCase("30565-02.htm"))
		{
			st.set("cond", "13");
			st.playSound(QuestState.SOUND_MIDDLE);
			st.takeItems(LetterToOrc, -1);
			st.giveItems(LetterToManakia, 1);
		}
		else if (event.equalsIgnoreCase("30621-02.htm"))
		{
			st.set("cond", "19");
			st.playSound(QuestState.SOUND_MIDDLE);
			st.takeItems(LetterToNikola, -1);
			st.giveItems(OrderOfNikola, 1);
		}
		else if (event.equalsIgnoreCase("30657-03.htm"))
		{
			if (player.getLevel() < 38)
			{
				htmltext = "30657-02.htm";
				if (st.getInt("cond") == 10)
				{
					st.set("cond", "11");
					st.playSound(QuestState.SOUND_MIDDLE);
				}
			}
			else
			{
				st.set("cond", "12");
				st.playSound(QuestState.SOUND_MIDDLE);
				st.takeItems(LetterToSeresin, -1);
				st.giveItems(LetterToOrc, 1);
				st.giveItems(LetterToDwarf, 1);
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
				if (player.getClassId().level() != 1)
					htmltext = "30191-01a.htm";
				else if (st.hasQuestItems(MarkOfTrust))
					htmltext = "30191-01b.htm";
				else if (player.getRace() != Race.Human)
					htmltext = "30191-02.htm";
				else if (player.getLevel() < 37)
					htmltext = "30191-01.htm";
				else
					htmltext = "30191-03.htm";
				break;
			
			case STATE_STARTED:
				int cond = st.getInt("cond");
				switch (npc.getNpcId())
				{
					case Hollint:
						if (cond < 9)
							htmltext = "30191-08.htm";
						else if (cond == 9)
						{
							htmltext = "30191-05.htm";
							st.set("cond", "10");
							st.playSound(QuestState.SOUND_MIDDLE);
							st.takeItems(ScrollOfDarkElfTrust, -1);
							st.takeItems(ScrollOfElfTrust, -1);
							st.giveItems(LetterToSeresin, 1);
						}
						else if (cond > 9 && cond < 22)
							htmltext = "30191-09.htm";
						else if (cond == 22)
						{
							htmltext = "30191-06.htm";
							st.set("cond", "23");
							st.playSound(QuestState.SOUND_MIDDLE);
							st.takeItems(ScrollOfDwarfTrust, -1);
							st.takeItems(ScrollOfOrcTrust, -1);
							st.giveItems(RecommendationOfHollint, 1);
						}
						else if (cond == 23)
							htmltext = "30191-07.htm";
						break;
					
					case Asterios:
						if (cond == 1)
							htmltext = "30154-01.htm";
						else if (cond == 2)
							htmltext = "30154-04.htm";
						else if (cond == 3)
						{
							htmltext = "30154-05.htm";
							st.set("cond", "4");
							st.playSound(QuestState.SOUND_MIDDLE);
							st.takeItems(BreathOfWinds, -1);
							st.takeItems(SeedOfVerdure, -1);
							st.takeItems(OrderOfAsterios, -1);
							st.giveItems(ScrollOfElfTrust, 1);
						}
						else if (cond > 3)
							htmltext = "30154-06.htm";
						break;
					
					case Thifiell:
						if (cond == 4)
							htmltext = "30358-01.htm";
						else if (cond > 4 && cond < 8)
							htmltext = "30358-05.htm";
						else if (cond == 8)
						{
							htmltext = "30358-03.htm";
							st.set("cond", "9");
							st.playSound(QuestState.SOUND_MIDDLE);
							st.takeItems(BasiliskPlasma, -1);
							st.takeItems(HoneyDew, -1);
							st.takeItems(StakatoIchor, -1);
							st.giveItems(ScrollOfDarkElfTrust, 1);
						}
						else if (cond > 8)
							htmltext = "30358-04.htm";
						break;
					
					case Clayton:
						if (cond == 5)
						{
							htmltext = "30464-01.htm";
							st.set("cond", "6");
							st.playSound(QuestState.SOUND_MIDDLE);
							st.takeItems(LetterFromThifiell, -1);
							st.giveItems(OrderOfClayton, 1);
						}
						else if (cond == 6)
							htmltext = "30464-02.htm";
						else if (cond > 6)
						{
							htmltext = "30464-03.htm";
							if (cond == 7)
							{
								st.set("cond", "8");
								st.playSound(QuestState.SOUND_MIDDLE);
								st.takeItems(OrderOfClayton, -1);
							}
						}
						break;
					
					case Seresin:
						if (cond == 10 || cond == 11)
							htmltext = "30657-01.htm";
						else if (cond > 11 && cond < 22)
							htmltext = "30657-04.htm";
						else if (cond == 22)
							htmltext = "30657-05.htm";
						break;
					
					case Kakai:
						if (cond == 12)
							htmltext = "30565-01.htm";
						else if (cond > 12 && cond < 16)
							htmltext = "30565-03.htm";
						else if (cond == 16)
						{
							htmltext = "30565-04.htm";
							st.set("cond", "17");
							st.playSound(QuestState.SOUND_MIDDLE);
							st.takeItems(LetterOfManakia, -1);
							st.giveItems(ScrollOfOrcTrust, 1);
						}
						else if (cond > 16)
							htmltext = "30565-05.htm";
						break;
					
					case Manakia:
						if (cond == 13)
							htmltext = "30515-01.htm";
						else if (cond == 14)
							htmltext = "30515-03.htm";
						else if (cond == 15)
						{
							htmltext = "30515-04.htm";
							st.set("cond", "16");
							st.playSound(QuestState.SOUND_MIDDLE);
							st.takeItems(ParasiteOfLota, -1);
							st.giveItems(LetterOfManakia, 1);
						}
						else if (cond > 15)
							htmltext = "30515-05.htm";
						break;
					
					case Lockirin:
						if (cond == 17)
							htmltext = "30531-01.htm";
						else if (cond > 17 && cond < 21)
							htmltext = "30531-03.htm";
						else if (cond == 21)
						{
							htmltext = "30531-04.htm";
							st.set("cond", "22");
							st.playSound(QuestState.SOUND_MIDDLE);
							st.giveItems(ScrollOfDwarfTrust, 1);
						}
						else if (cond == 22)
							htmltext = "30531-05.htm";
						break;
					
					case Nikola:
						if (cond == 18)
							htmltext = "30621-01.htm";
						else if (cond == 19)
							htmltext = "30621-03.htm";
						else if (cond == 20)
						{
							htmltext = "30621-04.htm";
							st.set("cond", "21");
							st.playSound(QuestState.SOUND_MIDDLE);
							st.takeItems(HeartstoneOfPorta, -1);
							st.takeItems(OrderOfNikola, -1);
						}
						else if (cond > 20)
							htmltext = "30621-05.htm";
						break;
					
					case Biotin:
						if (cond == 23)
						{
							htmltext = "30031-01.htm";
							st.takeItems(RecommendationOfHollint, -1);
							st.giveItems(MarkOfTrust, 1);
							st.rewardExpAndSp(39571, 2500);
							st.playSound(QuestState.SOUND_FINISH);
							st.exitQuest(true);
						}
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
		
		final int npcId = npc.getNpcId();
		switch (npcId)
		{
			case Dryad:
			case DryadElder:
				if (st.getInt("cond") == 2 && !st.hasQuestItems(SeedOfVerdure) && Rnd.get(100) < 33)
				{
					addSpawn(ActeaOfVerdantWilds, npc, true, 600000, true);
					st.playSound(QuestState.SOUND_BEFORE_BATTLE);
				}
				break;
			
			case Lirein:
			case LireinElder:
				if (st.getInt("cond") == 2 && !st.hasQuestItems(BreathOfWinds) && Rnd.get(100) < 33)
				{
					addSpawn(LuellOfZephyrWinds, npc, true, 600000, true);
					st.playSound(QuestState.SOUND_BEFORE_BATTLE);
				}
				break;
			
			case ActeaOfVerdantWilds:
				if (st.getInt("cond") == 2 && !st.hasQuestItems(SeedOfVerdure))
				{
					st.giveItems(SeedOfVerdure, 1);
					if (st.hasQuestItems(BreathOfWinds))
					{
						st.set("cond", "3");
						st.playSound(QuestState.SOUND_MIDDLE);
					}
					else
						st.playSound(QuestState.SOUND_ITEMGET);
				}
				break;
			
			case LuellOfZephyrWinds:
				if (st.getInt("cond") == 2 && !st.hasQuestItems(BreathOfWinds))
				{
					st.giveItems(BreathOfWinds, 1);
					if (st.hasQuestItems(SeedOfVerdure))
					{
						st.set("cond", "3");
						st.playSound(QuestState.SOUND_MIDDLE);
					}
					else
						st.playSound(QuestState.SOUND_ITEMGET);
				}
				break;
			
			case MarshStakato:
			case MarshStakatoWorker:
			case MarshStakatoSoldier:
			case MarshStakatoDrone:
				if (st.getInt("cond") == 6 && !st.hasQuestItems(StakatoIchor) && st.dropItemsAlways(StakatoFluids, 1, 10))
				{
					st.takeItems(StakatoFluids, -1);
					st.giveItems(StakatoIchor, 1);
					
					if (st.hasQuestItems(BasiliskPlasma) && st.hasQuestItems(HoneyDew) && st.hasQuestItems(StakatoIchor))
						st.set("cond", "7");
				}
				break;
			
			case AntRecruit:
			case AntPatrol:
			case AntGuard:
			case AntSoldier:
			case AntWarriorCaptain:
				if (st.getInt("cond") == 6 && !st.hasQuestItems(HoneyDew) && st.dropItemsAlways(GiantAphid, 1, 10))
				{
					st.takeItems(GiantAphid, -1);
					st.giveItems(HoneyDew, 1);
					
					if (st.hasQuestItems(BasiliskPlasma) && st.hasQuestItems(HoneyDew) && st.hasQuestItems(StakatoIchor))
						st.set("cond", "7");
				}
				break;
			
			case GuardianBasilisk:
				if (st.getInt("cond") == 6 && !st.hasQuestItems(BasiliskPlasma) && st.dropItemsAlways(BloodGuardianBasilisk, 1, 10))
				{
					st.takeItems(BloodGuardianBasilisk, -1);
					st.giveItems(BasiliskPlasma, 1);
					
					if (st.hasQuestItems(BasiliskPlasma) && st.hasQuestItems(HoneyDew) && st.hasQuestItems(StakatoIchor))
						st.set("cond", "7");
				}
				break;
			
			case Windsus:
				if (st.getInt("cond") == 14 && st.dropItems(ParasiteOfLota, 1, 10, 500000))
					st.set("cond", "15");
				break;
			
			case Porta:
				if (st.getInt("cond") == 19 && st.dropItemsAlways(HeartstoneOfPorta, 1, 10))
					st.set("cond", "20");
				break;
		}
		
		return null;
	}
	
	public static void main(String[] args)
	{
		new Q217_TestimonyOfTrust(217, qn, "Testimony of Trust");
	}
}
