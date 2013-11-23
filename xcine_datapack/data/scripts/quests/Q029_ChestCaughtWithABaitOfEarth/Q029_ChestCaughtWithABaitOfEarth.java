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
package quests.Q029_ChestCaughtWithABaitOfEarth;

import net.xcine.gameserver.model.actor.L2Npc;
import net.xcine.gameserver.model.actor.instance.L2PcInstance;
import net.xcine.gameserver.model.quest.Quest;
import net.xcine.gameserver.model.quest.QuestState;

public class Q029_ChestCaughtWithABaitOfEarth extends Quest
{
	private static final String qn = "Q029_ChestCaughtWithABaitOfEarth";
	
	// NPCs
	private final static int Willie = 31574;
	private final static int Anabel = 30909;
	
	// Items
	private final static int SmallPurpleTreasureChest = 6507;
	private final static int SmallGlassBox = 7627;
	private final static int PlatedLeatherGloves = 2455;
	
	public Q029_ChestCaughtWithABaitOfEarth(int questId, String name, String descr)
	{
		super(questId, name, descr);
		
		questItemIds = new int[]
		{
			SmallGlassBox
		};
		
		addStartNpc(Willie);
		addTalkId(Willie, Anabel);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = event;
		QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("31574-04.htm"))
		{
			st.set("cond", "1");
			st.setState(STATE_STARTED);
			st.playSound(QuestState.SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("31574-07.htm"))
		{
			if (st.getQuestItemsCount(SmallPurpleTreasureChest) == 1)
			{
				st.set("cond", "2");
				st.takeItems(SmallPurpleTreasureChest, 1);
				st.giveItems(SmallGlassBox, 1);
			}
			else
				htmltext = "31574-08.htm";
		}
		else if (event.equalsIgnoreCase("30909-02.htm"))
		{
			if (st.getQuestItemsCount(SmallGlassBox) == 1)
			{
				htmltext = "30909-02.htm";
				st.takeItems(SmallGlassBox, 1);
				st.giveItems(PlatedLeatherGloves, 1);
				st.playSound(QuestState.SOUND_FINISH);
				st.exitQuest(false);
			}
			else
				htmltext = ("30909-03.htm");
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
				if (player.getLevel() < 48)
				{
					htmltext = "31574-02.htm";
					st.exitQuest(true);
				}
				else
				{
					QuestState st2 = player.getQuestState("Q052_WilliesSpecialBait");
					if (st2 != null && st2.isCompleted())
						htmltext = "31574-01.htm";
					else
					{
						htmltext = "31574-03.htm";
						st.exitQuest(true);
					}
				}
				break;
			
			case STATE_STARTED:
				int cond = st.getInt("cond");
				switch (npc.getNpcId())
				{
					case Willie:
						if (cond == 1)
						{
							htmltext = ("31574-05.htm");
							if (st.getQuestItemsCount(SmallPurpleTreasureChest) == 0)
								htmltext = ("31574-06.htm");
						}
						else if (cond == 2)
							htmltext = ("31574-09.htm");
						break;
					
					case Anabel:
						if (cond == 2)
							htmltext = ("30909-01.htm");
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
		new Q029_ChestCaughtWithABaitOfEarth(29, qn, "Chest caught with a bait of earth");
	}
}