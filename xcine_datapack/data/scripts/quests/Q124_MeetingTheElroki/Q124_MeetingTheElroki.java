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
package quests.Q124_MeetingTheElroki;

import net.xcine.gameserver.model.actor.L2Npc;
import net.xcine.gameserver.model.actor.instance.L2PcInstance;
import net.xcine.gameserver.model.quest.Quest;
import net.xcine.gameserver.model.quest.QuestState;

public class Q124_MeetingTheElroki extends Quest
{
	public static final String qn = "Q124_MeetingTheElroki";
	
	// NPCs
	private static final int MARQUEZ = 32113;
	private static final int MUSHIKA = 32114;
	private static final int ASAMAH = 32115;
	private static final int KARAKAWEI = 32117;
	private static final int MANTARASA = 32118;
	
	public Q124_MeetingTheElroki(int questId, String name, String descr)
	{
		super(questId, name, descr);
		
		addStartNpc(MARQUEZ);
		addTalkId(MARQUEZ, MUSHIKA, ASAMAH, KARAKAWEI, MANTARASA);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = event;
		QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("32113-03.htm"))
		{
			st.set("cond", "1");
			st.setState(STATE_STARTED);
			st.playSound(QuestState.SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("32113-04.htm"))
		{
			st.set("cond", "2");
			st.playSound(QuestState.SOUND_MIDDLE);
		}
		else if (event.equalsIgnoreCase("32114-02.htm"))
		{
			st.set("cond", "3");
			st.playSound(QuestState.SOUND_MIDDLE);
		}
		else if (event.equalsIgnoreCase("32115-04.htm"))
		{
			st.set("cond", "4");
			st.playSound(QuestState.SOUND_MIDDLE);
		}
		else if (event.equalsIgnoreCase("32117-02.htm"))
		{
			if (st.getInt("cond") == 4)
				st.set("progress", "1");
		}
		else if (event.equalsIgnoreCase("32117-03.htm"))
		{
			st.set("cond", "5");
			st.playSound(QuestState.SOUND_MIDDLE);
		}
		else if (event.equalsIgnoreCase("32118-02.htm"))
		{
			st.giveItems(8778, 1); // Egg
			st.set("cond", "6");
			st.playSound(QuestState.SOUND_MIDDLE);
		}
		
		return htmltext;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		QuestState st = player.getQuestState(qn);
		String htmltext = getNoQuestMsg();
		if (st == null)
			return htmltext;
		
		switch (st.getState())
		{
			case STATE_CREATED:
				if (player.getLevel() >= 75)
					htmltext = "32113-01.htm";
				else
				{
					htmltext = "32113-01a.htm";
					st.exitQuest(true);
				}
				break;
			
			case STATE_STARTED:
				int cond = st.getInt("cond");
				switch (npc.getNpcId())
				{
					case MARQUEZ:
						if (cond == 1)
							htmltext = "32113-03.htm";
						else if (cond >= 2)
							htmltext = "32113-04a.htm";
						break;
					
					case MUSHIKA:
						if (cond == 2)
							htmltext = "32114-01.htm";
						else if (cond >= 3)
							htmltext = "32114-03.htm";
						break;
					
					case ASAMAH:
						if (cond == 3)
							htmltext = "32115-01.htm";
						else if (cond == 6)
						{
							htmltext = "32115-05.htm";
							st.takeItems(8778, -1);
							st.rewardItems(57, 71318);
							st.exitQuest(false);
							st.playSound(QuestState.SOUND_FINISH);
						}
						break;
					
					case KARAKAWEI:
						if (cond == 4)
						{
							htmltext = "32117-01.htm";
							if (st.getInt("progress") == 1)
								htmltext = "32117-02.htm";
						}
						else if (cond >= 5)
							htmltext = "32117-04.htm";
						break;
					
					case MANTARASA:
						if (cond == 5)
							htmltext = "32118-01.htm";
						else if (cond >= 6)
							htmltext = "32118-03.htm";
						break;
				}
				break;
			
			case STATE_COMPLETED:
				if (npc.getNpcId() == ASAMAH)
					htmltext = "32115-06.htm";
				else
					htmltext = getAlreadyCompletedMsg();
				break;
		}
		
		return htmltext;
	}
	
	public static void main(String[] args)
	{
		new Q124_MeetingTheElroki(124, qn, "Meeting the Elroki");
	}
}