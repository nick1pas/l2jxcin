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
package quests.Q011_SecretMeetingWithKetraOrcs;

import net.xcine.gameserver.model.actor.L2Npc;
import net.xcine.gameserver.model.actor.instance.L2PcInstance;
import net.xcine.gameserver.model.quest.Quest;
import net.xcine.gameserver.model.quest.QuestState;

public class Q011_SecretMeetingWithKetraOrcs extends Quest
{
	private static final String qn = "Q011_SecretMeetingWithKetraOrcs";
	
	// Npcs
	private final static int CADMON = 31296;
	private final static int LEON = 31256;
	private final static int WAHKAN = 31371;
	
	// Items
	private final static int MUNITIONS_BOX = 7231;
	
	public Q011_SecretMeetingWithKetraOrcs(int questId, String name, String descr)
	{
		super(questId, name, descr);
		
		questItemIds = new int[]
		{
			MUNITIONS_BOX
		};
		
		addStartNpc(CADMON);
		addTalkId(CADMON, LEON, WAHKAN);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = event;
		QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("31296-03.htm"))
		{
			st.set("cond", "1");
			st.setState(STATE_STARTED);
			st.playSound(QuestState.SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("31256-02.htm"))
		{
			st.giveItems(MUNITIONS_BOX, 1);
			st.set("cond", "2");
			st.playSound(QuestState.SOUND_MIDDLE);
		}
		else if (event.equalsIgnoreCase("31371-02.htm"))
		{
			st.takeItems(MUNITIONS_BOX, 1);
			st.rewardExpAndSp(79787, 0);
			st.playSound(QuestState.SOUND_FINISH);
			st.exitQuest(false);
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
				if (player.getLevel() >= 74)
					htmltext = "31296-01.htm";
				else
				{
					htmltext = "31296-02.htm";
					st.exitQuest(true);
				}
				break;
			
			case STATE_STARTED:
				int cond = st.getInt("cond");
				switch (npc.getNpcId())
				{
					case CADMON:
						if (cond == 1)
							htmltext = "31296-04.htm";
						break;
					
					case LEON:
						if (cond == 1)
							htmltext = "31256-01.htm";
						else if (cond == 2)
							htmltext = "31256-03.htm";
						break;
					
					case WAHKAN:
						if (cond == 2 && st.getQuestItemsCount(MUNITIONS_BOX) > 0)
							htmltext = "31371-01.htm";
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
		new Q011_SecretMeetingWithKetraOrcs(11, qn, "Secret Meeting With Ketra Orcs");
	}
}