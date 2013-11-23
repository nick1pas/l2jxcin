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
package quests.Q050_LanoscosSpecialBait;

import net.xcine.gameserver.model.actor.L2Npc;
import net.xcine.gameserver.model.actor.instance.L2PcInstance;
import net.xcine.gameserver.model.quest.Quest;
import net.xcine.gameserver.model.quest.QuestState;

public class Q050_LanoscosSpecialBait extends Quest
{
	private static final String qn = "Q050_LanoscosSpecialBait";
	
	// NPC
	private final static int LANOSCO = 31570;
	
	// Item
	private final static int ESSENCE_OF_WIND = 7621;
	
	// Reward
	private final static int WIND_FISHING_LURE = 7610;
	
	// Monster
	private final static int SINGING_WIND = 21026;
	
	public Q050_LanoscosSpecialBait(int questId, String name, String descr)
	{
		super(questId, name, descr);
		
		questItemIds = new int[]
		{
			ESSENCE_OF_WIND
		};
		
		addStartNpc(LANOSCO);
		addTalkId(LANOSCO);
		
		addKillId(SINGING_WIND);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = event;
		QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("31570-03.htm"))
		{
			st.set("cond", "1");
			st.setState(STATE_STARTED);
			st.playSound(QuestState.SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("31570-07.htm") && st.getQuestItemsCount(ESSENCE_OF_WIND) == 100)
		{
			htmltext = "31570-06.htm";
			st.rewardItems(WIND_FISHING_LURE, 4);
			st.takeItems(ESSENCE_OF_WIND, 100);
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
				if (player.getLevel() >= 27)
					htmltext = "31570-01.htm";
				else
				{
					htmltext = "31570-02.htm";
					st.exitQuest(true);
				}
				break;
			
			case STATE_STARTED:
				if (st.getQuestItemsCount(ESSENCE_OF_WIND) == 100)
					htmltext = "31570-04.htm";
				else
					htmltext = "31570-05.htm";
				break;
			
			case STATE_COMPLETED:
				htmltext = getAlreadyCompletedMsg();
				break;
		}
		return htmltext;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance player, boolean isPet)
	{
		QuestState st = checkPlayerCondition(player, npc, "cond", "1");
		if (st == null)
			return null;
		
		if (st.dropItems(ESSENCE_OF_WIND, 1, 100, 300000))
			st.set("cond", "2");
		
		return null;
	}
	
	public static void main(String[] args)
	{
		new Q050_LanoscosSpecialBait(50, qn, "Lanosco's Special Bait");
	}
}