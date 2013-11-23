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
package quests.Q161_FruitOfTheMotherTree;

import net.xcine.gameserver.model.actor.L2Npc;
import net.xcine.gameserver.model.actor.instance.L2PcInstance;
import net.xcine.gameserver.model.base.Race;
import net.xcine.gameserver.model.quest.Quest;
import net.xcine.gameserver.model.quest.QuestState;

public class Q161_FruitOfTheMotherTree extends Quest
{
	private static final String qn = "Q161_FruitOfTheMotherTree";
	
	// NPCs
	private final static int ANDELLIA = 30362;
	private final static int THALIA = 30371;
	
	// Items
	private final static int ANDELLIA_LETTER = 1036;
	private final static int MOTHERTREE_FRUIT = 1037;
	
	// Reward
	private final static int ADENA = 57;
	
	public Q161_FruitOfTheMotherTree(int questId, String name, String descr)
	{
		super(questId, name, descr);
		
		questItemIds = new int[]
		{
			ANDELLIA_LETTER,
			MOTHERTREE_FRUIT
		};
		
		addStartNpc(ANDELLIA);
		addTalkId(ANDELLIA, THALIA);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = event;
		QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("30362-04.htm"))
		{
			st.set("cond", "1");
			st.setState(STATE_STARTED);
			st.giveItems(ANDELLIA_LETTER, 1);
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
				if (player.getRace() == Race.Elf)
				{
					if (player.getLevel() >= 3)
						htmltext = "30362-03.htm";
					else
					{
						htmltext = "30362-02.htm";
						st.exitQuest(true);
					}
				}
				else
				{
					htmltext = "30362-00.htm";
					st.exitQuest(true);
				}
				break;
			
			case STATE_STARTED:
				int cond = st.getInt("cond");
				switch (npc.getNpcId())
				{
					case ANDELLIA:
						if (cond == 1)
							htmltext = "30362-05.htm";
						else if (cond == 2 && st.getQuestItemsCount(MOTHERTREE_FRUIT) == 1)
						{
							htmltext = "30362-06.htm";
							st.takeItems(MOTHERTREE_FRUIT, 1);
							st.rewardItems(ADENA, 1000);
							st.rewardExpAndSp(1000, 0);
							st.exitQuest(false);
							st.playSound(QuestState.SOUND_FINISH);
						}
						break;
					
					case THALIA:
						if (cond == 1 && st.getQuestItemsCount(ANDELLIA_LETTER) == 1)
						{
							htmltext = "30371-01.htm";
							st.takeItems(ANDELLIA_LETTER, 1);
							st.giveItems(MOTHERTREE_FRUIT, 1);
							st.set("cond", "2");
							st.playSound(QuestState.SOUND_MIDDLE);
						}
						else if (cond == 2 && st.getQuestItemsCount(MOTHERTREE_FRUIT) == 1)
							htmltext = "30371-02.htm";
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
		new Q161_FruitOfTheMotherTree(161, qn, "Fruit of the Mothertree");
	}
}