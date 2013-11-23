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
package quests.Q212_TrialOfDuty;

import net.xcine.gameserver.model.actor.L2Npc;
import net.xcine.gameserver.model.actor.instance.L2PcInstance;
import net.xcine.gameserver.model.base.ClassId;
import net.xcine.gameserver.model.itemcontainer.Inventory;
import net.xcine.gameserver.model.quest.Quest;
import net.xcine.gameserver.model.quest.QuestState;
import net.xcine.util.Rnd;

public class Q212_TrialOfDuty extends Quest
{
	private final static String qn = "Q212_TrialOfDuty";
	
	// NPCs
	private static final int HANNAVALT = 30109;
	private static final int DUSTIN = 30116;
	private static final int SIR_COLLIN = 30311;
	private static final int SIR_ARON = 30653;
	private static final int SIR_KIEL = 30654;
	private static final int SILVERSHADOW = 30655;
	private static final int SPIRIT_TALIANUS = 30656;
	
	// Items
	private static final int MARK_OF_DUTY = 2633;
	private static final int LETTER_OF_DUSTIN = 2634;
	private static final int KNIGHTS_TEAR = 2635;
	private static final int MIRROR_OF_ORPIC = 2636;
	private static final int TEAR_OF_CONFESSION = 2637;
	private static final int REPORT_PIECE = 2638;
	private static final int TALIANUSS_REPORT = 2639;
	private static final int TEAR_OF_LOYALTY = 2640;
	private static final int MILITAS_ARTICLE = 2641;
	private static final int SAINTS_ASHES_URN = 2642;
	private static final int ATEBALTS_SKULL = 2643;
	private static final int ATEBALTS_RIBS = 2644;
	private static final int ATEBALTS_SHIN = 2645;
	private static final int LETTER_OF_WINDAWOOD = 2646;
	private static final int OLD_KNIGHT_SWORD = 3027;
	
	public Q212_TrialOfDuty(int questId, String name, String descr)
	{
		super(questId, name, descr);
		
		questItemIds = new int[]
		{
			LETTER_OF_DUSTIN,
			KNIGHTS_TEAR,
			MIRROR_OF_ORPIC,
			TEAR_OF_CONFESSION,
			REPORT_PIECE,
			TALIANUSS_REPORT,
			TEAR_OF_LOYALTY,
			MILITAS_ARTICLE,
			SAINTS_ASHES_URN,
			ATEBALTS_SKULL,
			ATEBALTS_RIBS,
			ATEBALTS_SHIN,
			LETTER_OF_WINDAWOOD,
			OLD_KNIGHT_SWORD
		};
		
		addStartNpc(HANNAVALT);
		addTalkId(HANNAVALT, DUSTIN, SIR_COLLIN, SIR_ARON, SIR_KIEL, SILVERSHADOW, SPIRIT_TALIANUS);
		
		addKillId(20144, 20190, 20191, 20200, 20201, 20270, 27119, 20577, 20578, 20579, 20580, 20581, 20582);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = event;
		QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("30109-04.htm"))
		{
			st.set("cond", "1");
			st.setState(STATE_STARTED);
			st.playSound(QuestState.SOUND_ACCEPT);
			st.giveItems(7562, 61);
		}
		else if (event.equalsIgnoreCase("30116-05.htm"))
		{
			st.set("cond", "14");
			st.playSound(QuestState.SOUND_MIDDLE);
			st.takeItems(TEAR_OF_LOYALTY, 1);
		}
		
		return htmltext;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		String htmltext = Quest.getNoQuestMsg();
		QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;
		
		switch (st.getState())
		{
			case STATE_CREATED:
				if (st.hasQuestItems(MARK_OF_DUTY))
					htmltext = getAlreadyCompletedMsg();
				else if (player.getClassId() != ClassId.knight && player.getClassId() != ClassId.elvenKnight && player.getClassId() != ClassId.palusKnight)
					htmltext = "30109-02.htm";
				else if (player.getLevel() < 35)
					htmltext = "30109-01.htm";
				else
					htmltext = "30109-03.htm";
				break;
			
			case STATE_STARTED:
				int cond = st.getInt("cond");
				switch (npc.getNpcId())
				{
					case HANNAVALT:
						if (cond == 18)
						{
							htmltext = "30109-05.htm";
							st.takeItems(LETTER_OF_DUSTIN, 1);
							st.giveItems(MARK_OF_DUTY, 1);
							st.rewardExpAndSp(79832, 3750);
							st.playSound(QuestState.SOUND_FINISH);
							st.exitQuest(true);
						}
						else
							htmltext = "30109-04a.htm";
						break;
					
					case SIR_ARON:
						if (cond == 1)
						{
							htmltext = "30653-01.htm";
							st.set("cond", "2");
							st.playSound(QuestState.SOUND_MIDDLE);
							st.giveItems(OLD_KNIGHT_SWORD, 1);
						}
						else if (cond == 2)
							htmltext = "30653-02.htm";
						else if (cond == 3)
						{
							htmltext = "30653-03.htm";
							st.set("cond", "4");
							st.playSound(QuestState.SOUND_MIDDLE);
							st.takeItems(KNIGHTS_TEAR, 1);
							st.takeItems(OLD_KNIGHT_SWORD, 1);
						}
						else if (cond > 3)
							htmltext = "30653-04.htm";
						break;
					
					case SIR_KIEL:
						if (cond == 4)
						{
							htmltext = "30654-01.htm";
							st.set("cond", "5");
							st.playSound(QuestState.SOUND_MIDDLE);
						}
						else if (cond == 5)
							htmltext = "30654-02.htm";
						else if (cond == 6)
						{
							htmltext = "30654-03.htm";
							st.set("cond", "7");
							st.playSound(QuestState.SOUND_MIDDLE);
							st.giveItems(MIRROR_OF_ORPIC, 1);
						}
						else if (cond == 7)
							htmltext = "30654-04.htm";
						else if (cond == 9)
						{
							htmltext = "30654-05.htm";
							st.set("cond", "10");
							st.playSound(QuestState.SOUND_MIDDLE);
							st.takeItems(TEAR_OF_CONFESSION, 1);
						}
						else if (cond > 9)
							htmltext = "30654-06.htm";
						break;
					
					case SPIRIT_TALIANUS:
						if (cond == 8)
						{
							htmltext = "30656-01.htm";
							st.set("cond", "9");
							st.playSound(QuestState.SOUND_MIDDLE);
							st.takeItems(MIRROR_OF_ORPIC, 1);
							st.takeItems(TALIANUSS_REPORT, 1);
							st.giveItems(TEAR_OF_CONFESSION, 1);
							
							// Despawn the spirit.
							npc.deleteMe();
						}
						break;
					
					case SILVERSHADOW:
						if (cond == 10)
						{
							if (player.getLevel() < 35)
								htmltext = "30655-01.htm";
							else
							{
								htmltext = "30655-02.htm";
								st.set("cond", "11");
								st.playSound(QuestState.SOUND_MIDDLE);
							}
						}
						else if (cond == 11)
							htmltext = "30655-03.htm";
						else if (cond == 12)
						{
							htmltext = "30655-04.htm";
							st.set("cond", "13");
							st.playSound(QuestState.SOUND_MIDDLE);
							st.takeItems(MILITAS_ARTICLE, -1);
							st.giveItems(TEAR_OF_LOYALTY, 1);
						}
						else if (cond == 13)
							htmltext = "30655-05.htm";
						break;
					
					case DUSTIN:
						if (cond == 13)
							htmltext = "30116-01.htm";
						else if (cond == 14)
							htmltext = "30116-06.htm";
						else if (cond == 15)
						{
							htmltext = "30116-07.htm";
							st.set("cond", "16");
							st.playSound(QuestState.SOUND_MIDDLE);
							st.takeItems(ATEBALTS_SKULL, 1);
							st.takeItems(ATEBALTS_RIBS, 1);
							st.takeItems(ATEBALTS_SHIN, 1);
							st.giveItems(SAINTS_ASHES_URN, 1);
						}
						else if (cond == 16)
							htmltext = "30116-09.htm";
						else if (cond == 17)
						{
							htmltext = "30116-08.htm";
							st.set("cond", "18");
							st.playSound(QuestState.SOUND_MIDDLE);
							st.takeItems(LETTER_OF_WINDAWOOD, 1);
							st.giveItems(LETTER_OF_DUSTIN, 1);
						}
						else if (cond == 18)
							htmltext = "30116-10.htm";
						break;
					
					case SIR_COLLIN:
						if (cond == 16)
						{
							htmltext = "30311-01.htm";
							st.set("cond", "17");
							st.playSound(QuestState.SOUND_MIDDLE);
							st.takeItems(SAINTS_ASHES_URN, 1);
							st.giveItems(LETTER_OF_WINDAWOOD, 1);
						}
						else if (cond > 16)
							htmltext = "30311-02.htm";
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
		
		int cond = st.getInt("cond");
		switch (npc.getNpcId())
		{
			case 20190:
			case 20191:
				if (cond == 2 && Rnd.get(10) < 1)
				{
					st.playSound(QuestState.SOUND_BEFORE_BATTLE);
					addSpawn(27119, npc, false, 120000, true);
				}
				break;
			
			case 27119:
				if (cond == 2 && st.getItemEquipped(Inventory.PAPERDOLL_RHAND) == OLD_KNIGHT_SWORD)
				{
					st.set("cond", "3");
					st.playSound(QuestState.SOUND_MIDDLE);
					st.giveItems(KNIGHTS_TEAR, 1);
				}
				break;
			
			case 20201:
			case 20200:
				if (cond == 5 && st.dropItemsAlways(REPORT_PIECE, 1, 10))
				{
					st.set("cond", "6");
					st.takeItems(REPORT_PIECE, -1);
					st.giveItems(TALIANUSS_REPORT, 1);
				}
				break;
			
			case 20144:
				if ((cond == 7 || cond == 8) && Rnd.get(100) < 33)
				{
					if (cond == 7)
					{
						st.set("cond", "8");
						st.playSound(QuestState.SOUND_MIDDLE);
					}
					addSpawn(30656, npc, false, 300000, true);
				}
				break;
			
			case 20577:
			case 20578:
			case 20579:
			case 20580:
			case 20581:
			case 20582:
				if (cond == 11 && st.dropItemsAlways(MILITAS_ARTICLE, 1, 20))
					st.set("cond", "12");
				break;
			
			case 20270:
				if (cond == 14 && Rnd.nextBoolean())
				{
					if (!st.hasQuestItems(ATEBALTS_SKULL))
					{
						st.giveItems(ATEBALTS_SKULL, 1);
						st.playSound(QuestState.SOUND_ITEMGET);
					}
					else if (!st.hasQuestItems(ATEBALTS_RIBS))
					{
						st.giveItems(ATEBALTS_RIBS, 1);
						st.playSound(QuestState.SOUND_ITEMGET);
					}
					else if (!st.hasQuestItems(ATEBALTS_SHIN))
					{
						st.giveItems(ATEBALTS_SHIN, 1);
						st.set("cond", "15");
						st.playSound(QuestState.SOUND_MIDDLE);
					}
				}
				break;
		}
		
		return null;
	}
	
	public static void main(String[] args)
	{
		new Q212_TrialOfDuty(212, qn, "Trial of Duty");
	}
}
