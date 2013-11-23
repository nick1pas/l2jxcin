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
package quests.Q602_ShadowOfLight;

import net.xcine.gameserver.model.actor.L2Npc;
import net.xcine.gameserver.model.actor.instance.L2PcInstance;
import net.xcine.gameserver.model.quest.Quest;
import net.xcine.gameserver.model.quest.QuestState;
import net.xcine.util.Rnd;

public class Q602_ShadowOfLight extends Quest
{
	private static final String qn = "Q602_ShadowOfLight";
	
	private static final int EYE_OF_DARKNESS = 7189;
	
	private static final int[][] REWARDS =
	{
		{
			6699,
			40000,
			120000,
			20000,
			19
		},
		{
			6698,
			60000,
			110000,
			15000,
			39
		},
		{
			6700,
			40000,
			150000,
			10000,
			49
		},
		{
			0,
			100000,
			140000,
			11250,
			99
		}
	};
	
	public Q602_ShadowOfLight(int questId, String name, String descr)
	{
		super(questId, name, descr);
		
		questItemIds = new int[]
		{
			EYE_OF_DARKNESS
		};
		
		addStartNpc(31683); // Eye of Argos
		addTalkId(31683);
		
		addKillId(21299, 21304);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = event;
		QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("31683-02.htm"))
		{
			if (player.getLevel() < 68)
			{
				htmltext = "31683-02a.htm";
				st.exitQuest(true);
			}
			else
			{
				st.set("cond", "1");
				st.setState(STATE_STARTED);
				st.playSound(QuestState.SOUND_ACCEPT);
			}
		}
		else if (event.equalsIgnoreCase("31683-05.htm"))
		{
			if (st.getQuestItemsCount(EYE_OF_DARKNESS) < 100)
			{
				htmltext = "31683-06.htm";
				st.set("cond", "1");
			}
			else
			{
				st.takeItems(EYE_OF_DARKNESS, -1);
				
				final int random = Rnd.get(100);
				for (int[] element : REWARDS)
				{
					if (random <= element[4])
					{
						st.rewardExpAndSp(element[2], element[3]);
						st.giveItems(57, element[1]);
						if (element[0] != 0)
							st.giveItems(element[0], 3);
						
						break;
					}
				}
				st.playSound(QuestState.SOUND_FINISH);
				st.exitQuest(true);
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
				htmltext = "31683-01.htm";
				break;
			
			case STATE_STARTED:
				int cond = st.getInt("cond");
				if (cond == 1)
					htmltext = "31683-03.htm";
				else if (cond == 2)
					htmltext = "31683-04.htm";
				break;
		}
		
		return htmltext;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance player, boolean isPet)
	{
		L2PcInstance partyMember = getRandomPartyMember(player, npc, "cond", "1");
		QuestState st = partyMember.getQuestState(qn);
		if (st == null)
			return null;
		
		if (st.dropItems(EYE_OF_DARKNESS, 1, 100, 300000))
			st.set("cond", "2");
		
		return null;
	}
	
	public static void main(String[] args)
	{
		new Q602_ShadowOfLight(602, qn, "Shadow of Light");
	}
}