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
package quests.Q013_ParcelDelivery;

import net.xcine.gameserver.model.actor.L2Npc;
import net.xcine.gameserver.model.actor.instance.L2PcInstance;
import net.xcine.gameserver.model.quest.Quest;
import net.xcine.gameserver.model.quest.QuestState;

public class Q013_ParcelDelivery extends Quest
{
	private static final String qn = "Q013_ParcelDelivery";
	
	// NPCs
	private static final int FUNDIN = 31274;
	private static final int VULCAN = 31539;
	
	// Item
	private static final int PACKAGE = 7263;
	
	public Q013_ParcelDelivery(int questId, String name, String descr)
	{
		super(questId, name, descr);
		
		questItemIds = new int[]
		{
			PACKAGE
		};
		
		addStartNpc(FUNDIN);
		addTalkId(FUNDIN, VULCAN);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = event;
		QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("31274-2.htm"))
		{
			if (st.getInt("cond") == 0)
			{
				st.set("cond", "1");
				st.setState(STATE_STARTED);
				st.giveItems(PACKAGE, 1);
				st.playSound(QuestState.SOUND_ACCEPT);
			}
		}
		else if (event.equalsIgnoreCase("31539-1.htm"))
		{
			if (st.getInt("cond") == 1)
			{
				if (st.getQuestItemsCount(PACKAGE) >= 1)
				{
					st.takeItems(PACKAGE, 1);
					st.rewardItems(57, 82656);
					st.exitQuest(false);
					st.playSound(QuestState.SOUND_FINISH);
				}
				else
					htmltext = "<html><body>Flame Blacksmith Vulcan:<br>You don't have the required items.</body></html>";
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
				if (player.getLevel() < 74)
				{
					htmltext = "31274-1.htm";
					st.exitQuest(true);
				}
				else
					htmltext = "31274-0.htm";
				break;
			
			case STATE_STARTED:
				switch (npc.getNpcId())
				{
					case FUNDIN:
						htmltext = "31274-2.htm";
						break;
					
					case VULCAN:
						htmltext = "31539-0.htm";
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
		new Q013_ParcelDelivery(13, qn, "Parcel Delivery");
	}
}