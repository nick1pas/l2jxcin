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
package quests.Q313_CollectSpores;

import net.xcine.gameserver.model.actor.L2Npc;
import net.xcine.gameserver.model.actor.instance.L2PcInstance;
import net.xcine.gameserver.model.quest.Quest;
import net.xcine.gameserver.model.quest.QuestState;

public class Q313_CollectSpores extends Quest
{
	private static final String qn = "Q313_CollectSpores";
	
	// NPC
	private static final int Herbiel = 30150;
	
	// Item
	private static final int SporeSac = 1118;
	
	public Q313_CollectSpores(int questId, String name, String descr)
	{
		super(questId, name, descr);
		
		questItemIds = new int[]
		{
			SporeSac
		};
		
		addStartNpc(Herbiel);
		addTalkId(Herbiel);
		
		addKillId(20509); // SporeFungus
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = event;
		QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("30150-05.htm"))
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
				if (player.getLevel() >= 8)
					htmltext = "30150-03.htm";
				else
				{
					htmltext = "30150-02.htm";
					st.exitQuest(true);
				}
				break;
			
			case STATE_STARTED:
				int cond = st.getInt("cond");
				if (cond == 1)
					htmltext = "30150-06.htm";
				else if (cond == 2)
				{
					if (st.getQuestItemsCount(SporeSac) < 10)
					{
						st.set("cond", "1");
						htmltext = "30150-06.htm";
					}
					else
					{
						htmltext = "30150-07.htm";
						st.takeItems(SporeSac, -1);
						st.rewardItems(57, 3500);
						st.playSound(QuestState.SOUND_FINISH);
						st.exitQuest(true);
					}
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
		
		if (st.dropItems(SporeSac, 1, 10, 700000))
			st.set("cond", "2");
		
		return null;
	}
	
	public static void main(String[] args)
	{
		new Q313_CollectSpores(313, qn, "Collect Spores");
	}
}