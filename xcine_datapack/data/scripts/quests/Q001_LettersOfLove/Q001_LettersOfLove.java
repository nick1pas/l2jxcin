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
package quests.Q001_LettersOfLove;

import net.xcine.gameserver.model.actor.L2Npc;
import net.xcine.gameserver.model.actor.instance.L2PcInstance;
import net.xcine.gameserver.model.quest.Quest;
import net.xcine.gameserver.model.quest.QuestState;

public class Q001_LettersOfLove extends Quest
{
	private static final String qn = "Q001_LettersOfLove";
	
	// Npcs
	private final static int DARIN = 30048;
	private final static int ROXXY = 30006;
	private final static int BAULRO = 30033;
	
	// Items
	private final static int DARINGS_LETTER = 687;
	private final static int RAPUNZELS_KERCHIEF = 688;
	private final static int DARINGS_RECEIPT = 1079;
	private final static int BAULROS_POTION = 1080;
	
	// Reward
	private final static int NECKLACE = 906;
	
	public Q001_LettersOfLove(int questId, String name, String descr)
	{
		super(questId, name, descr);
		
		questItemIds = new int[]
		{
			DARINGS_LETTER,
			RAPUNZELS_KERCHIEF,
			DARINGS_RECEIPT,
			BAULROS_POTION
		};
		
		addStartNpc(DARIN);
		addTalkId(DARIN, ROXXY, BAULRO);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = event;
		QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("30048-06.htm"))
		{
			st.set("cond", "1");
			st.setState(STATE_STARTED);
			st.giveItems(DARINGS_LETTER, 1);
			st.playSound(QuestState.SOUND_ACCEPT);
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
				if (player.getLevel() >= 2)
					htmltext = "30048-02.htm";
				else
				{
					htmltext = "30048-01.htm";
					st.exitQuest(true);
				}
				break;
			
			case STATE_STARTED:
				int cond = st.getInt("cond");
				switch (npc.getNpcId())
				{
					case DARIN:
						if (cond == 1)
							htmltext = "30048-07.htm";
						else if (cond == 2 && st.getQuestItemsCount(RAPUNZELS_KERCHIEF) == 1)
						{
							htmltext = "30048-08.htm";
							st.takeItems(RAPUNZELS_KERCHIEF, 1);
							st.giveItems(DARINGS_RECEIPT, 1);
							st.set("cond", "3");
							st.playSound(QuestState.SOUND_MIDDLE);
						}
						else if (cond == 3)
							htmltext = "30048-09.htm";
						else if (cond == 4 && st.getQuestItemsCount(BAULROS_POTION) == 1)
						{
							htmltext = "30048-10.htm";
							st.takeItems(BAULROS_POTION, 1);
							st.giveItems(NECKLACE, 1);
							st.playSound(QuestState.SOUND_FINISH);
							st.exitQuest(false);
						}
						break;
					
					case ROXXY:
						if (cond == 1 && st.getQuestItemsCount(RAPUNZELS_KERCHIEF) == 0 && st.getQuestItemsCount(DARINGS_LETTER) > 0)
						{
							htmltext = "30006-01.htm";
							st.takeItems(DARINGS_LETTER, 1);
							st.giveItems(RAPUNZELS_KERCHIEF, 1);
							st.set("cond", "2");
							st.playSound(QuestState.SOUND_MIDDLE);
						}
						else if (cond == 2 && st.getQuestItemsCount(RAPUNZELS_KERCHIEF) > 0)
							htmltext = "30006-02.htm";
						else if (cond > 2 && (st.getQuestItemsCount(BAULROS_POTION) > 0 || st.getQuestItemsCount(DARINGS_RECEIPT) > 0))
							htmltext = "30006-03.htm";
						break;
					
					case BAULRO:
						if (cond == 3 && st.getQuestItemsCount(DARINGS_RECEIPT) == 1)
						{
							htmltext = "30033-01.htm";
							st.takeItems(DARINGS_RECEIPT, 1);
							st.giveItems(BAULROS_POTION, 1);
							st.set("cond", "4");
							st.playSound(QuestState.SOUND_MIDDLE);
						}
						else if (cond == 4)
							htmltext = "30033-02.htm";
						break;
				}
				break;
			
			case STATE_COMPLETED:
				htmltext = getAlreadyCompletedMsg();
				break;
		}
		
		return htmltext;
	}
	
	public static void main(String[] args)
	{
		new Q001_LettersOfLove(1, qn, "Letters of Love");
	}
}