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
package quests.Q303_CollectArrowheads;

import net.xcine.gameserver.model.actor.L2Npc;
import net.xcine.gameserver.model.actor.instance.L2PcInstance;
import net.xcine.gameserver.model.quest.Quest;
import net.xcine.gameserver.model.quest.QuestState;

public class Q303_CollectArrowheads extends Quest
{
	private static final String qn = "Q303_CollectArrowheads";
	
	// NPC
	private static final int MINIA = 30029;
	
	// Item
	private static final int ORCISH_ARROWHEAD = 963;
	
	public Q303_CollectArrowheads(int questId, String name, String descr)
	{
		super(questId, name, descr);
		
		questItemIds = new int[]
		{
			ORCISH_ARROWHEAD
		};
		
		addStartNpc(MINIA);
		addTalkId(MINIA);
		
		addKillId(20361);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = event;
		QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("30029-03.htm"))
		{
			st.set("cond", "1");
			st.setState(STATE_STARTED);
			st.playSound(QuestState.SOUND_ACCEPT);
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
				if (player.getLevel() >= 10)
					htmltext = "30029-02.htm";
				else
				{
					htmltext = "30029-01.htm";
					st.exitQuest(true);
				}
				break;
			
			case STATE_STARTED:
				if (st.getQuestItemsCount(ORCISH_ARROWHEAD) < 10)
					htmltext = "30029-04.htm";
				else
				{
					htmltext = "30029-05.htm";
					st.takeItems(ORCISH_ARROWHEAD, -1);
					st.rewardItems(57, 1000);
					st.rewardExpAndSp(2000, 0);
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
		QuestState st = checkPlayerCondition(player, npc, "cond", "1");
		if (st == null)
			return null;
		
		if (st.dropItems(ORCISH_ARROWHEAD, 1, 10, 400000))
			st.set("cond", "2");
		
		return null;
	}
	
	public static void main(String[] args)
	{
		new Q303_CollectArrowheads(303, qn, "Collect Arrowheads");
	}
}