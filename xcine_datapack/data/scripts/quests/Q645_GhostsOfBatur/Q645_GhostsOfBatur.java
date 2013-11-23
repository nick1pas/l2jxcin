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
package quests.Q645_GhostsOfBatur;

import net.xcine.gameserver.model.actor.L2Npc;
import net.xcine.gameserver.model.actor.instance.L2PcInstance;
import net.xcine.gameserver.model.quest.Quest;
import net.xcine.gameserver.model.quest.QuestState;
import net.xcine.gameserver.util.Util;

public class Q645_GhostsOfBatur extends Quest
{
	private static final String qn = "Q645_GhostsOfBatur";
	
	// NPC
	private static final int KARUDA = 32017;
	
	// Item
	private static final int GRAVE_GOODS = 8089;
	
	// Rewards
	private static final int[][] rewards =
	{
		{
			1878,
			18
		},
		{
			1879,
			7
		},
		{
			1880,
			4
		},
		{
			1881,
			6
		},
		{
			1882,
			10
		},
		{
			1883,
			2
		}
	};
	
	public Q645_GhostsOfBatur(int questId, String name, String descr)
	{
		super(questId, name, descr);
		
		addStartNpc(KARUDA);
		addTalkId(KARUDA);
		
		addKillId(22007, 22009, 22010, 22011, 22012, 22013, 22014, 22015, 22016);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = event;
		QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("32017-03.htm"))
		{
			if (player.getLevel() >= 23)
			{
				st.set("cond", "1");
				st.setState(STATE_STARTED);
				st.playSound(QuestState.SOUND_ACCEPT);
			}
			else
			{
				htmltext = "32017-02.htm";
				st.exitQuest(true);
			}
		}
		else if (Util.isDigit(event))
		{
			if (st.getQuestItemsCount(GRAVE_GOODS) == 180)
			{
				htmltext = "32017-07.htm";
				st.takeItems(GRAVE_GOODS, -1);
				
				int reward[] = rewards[Integer.parseInt(event)];
				st.giveItems(reward[0], reward[1]);
				
				st.playSound(QuestState.SOUND_FINISH);
				st.exitQuest(true);
			}
			else
				htmltext = "32017-04.htm";
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
				htmltext = "32017-01.htm";
				break;
			
			case STATE_STARTED:
				int cond = st.getInt("cond");
				if (cond == 1)
					htmltext = "32017-04.htm";
				else if (cond == 2)
				{
					if (st.getQuestItemsCount(GRAVE_GOODS) == 180)
						htmltext = "32017-05.htm";
					else
						htmltext = "32017-01.htm";
				}
				break;
		}
		
		return htmltext;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance player, boolean isPet)
	{
		L2PcInstance partyMember = getRandomPartyMember(player, npc, "1");
		if (partyMember == null)
			return null;
		
		QuestState st = partyMember.getQuestState(qn);
		
		if (st.dropItems(GRAVE_GOODS, 1, 180, 750000))
			st.set("cond", "2");
		
		return null;
	}
	
	public static void main(String[] args)
	{
		new Q645_GhostsOfBatur(645, qn, "Ghosts Of Batur");
	}
}