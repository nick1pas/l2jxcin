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
package quests.Q005_MinersFavor;

import net.xcine.gameserver.model.actor.L2Npc;
import net.xcine.gameserver.model.actor.instance.L2PcInstance;
import net.xcine.gameserver.model.quest.Quest;
import net.xcine.gameserver.model.quest.QuestState;

public class Q005_MinersFavor extends Quest
{
	private static final String qn = "Q005_MinersFavor";
	
	// Npcs
	private final static int BOLTER = 30554;
	private final static int SHARI = 30517;
	private final static int GARITA = 30518;
	private final static int REED = 30520;
	private final static int BRUNON = 30526;
	
	// Items
	private final static int BOLTERS_LIST = 1547;
	private final static int MINING_BOOTS = 1548;
	private final static int MINERS_PICK = 1549;
	private final static int BOOMBOOM_POWDER = 1550;
	private final static int REDSTONE_BEER = 1551;
	private final static int BOLTERS_SMELLY_SOCKS = 1552;
	
	// Reward
	private final static int NECKLACE = 906;
	
	public Q005_MinersFavor(int questId, String name, String descr)
	{
		super(questId, name, descr);
		
		questItemIds = new int[]
		{
			BOLTERS_LIST,
			MINING_BOOTS,
			MINERS_PICK,
			BOOMBOOM_POWDER,
			REDSTONE_BEER,
			BOLTERS_SMELLY_SOCKS
		};
		
		addStartNpc(BOLTER);
		addTalkId(BOLTER, SHARI, GARITA, REED, BRUNON);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = event;
		QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("30554-03.htm"))
		{
			st.set("cond", "1");
			st.setState(STATE_STARTED);
			st.giveItems(BOLTERS_LIST, 1);
			st.giveItems(BOLTERS_SMELLY_SOCKS, 1);
			st.playSound(QuestState.SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("30526-02.htm"))
		{
			st.takeItems(BOLTERS_SMELLY_SOCKS, 1);
			st.giveItems(MINERS_PICK, 1);
			if (st.getQuestItemsCount(BOLTERS_LIST) > 0 && (st.getQuestItemsCount(MINING_BOOTS) + st.getQuestItemsCount(MINERS_PICK) + st.getQuestItemsCount(BOOMBOOM_POWDER) + st.getQuestItemsCount(REDSTONE_BEER) >= 4))
			{
				st.set("cond", "2");
				st.playSound(QuestState.SOUND_MIDDLE);
			}
			else
				st.playSound(QuestState.SOUND_ITEMGET);
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
				if (player.getLevel() >= 2)
					htmltext = "30554-02.htm";
				else
				{
					htmltext = "30554-01.htm";
					st.exitQuest(true);
				}
				break;
			
			case STATE_STARTED:
				int cond = st.getInt("cond");
				switch (npc.getNpcId())
				{
					case BOLTER:
						if (cond == 1)
							htmltext = "30554-04.htm";
						else if (cond == 2)
						{
							htmltext = "30554-06.htm";
							st.takeItems(MINING_BOOTS, 1);
							st.takeItems(MINERS_PICK, 1);
							st.takeItems(BOOMBOOM_POWDER, 1);
							st.takeItems(REDSTONE_BEER, 1);
							st.takeItems(BOLTERS_LIST, 1);
							st.giveItems(NECKLACE, 1);
							st.playSound(QuestState.SOUND_FINISH);
							st.exitQuest(false);
						}
						break;
					
					case SHARI:
						if (cond == 1 && st.getQuestItemsCount(BOLTERS_LIST) >= 1)
						{
							if (st.getQuestItemsCount(BOOMBOOM_POWDER) == 0)
							{
								htmltext = "30517-01.htm";
								st.giveItems(BOOMBOOM_POWDER, 1);
								st.playSound(QuestState.SOUND_ITEMGET);
							}
							else
								htmltext = "30517-02.htm";
						}
						else if (cond == 2)
							htmltext = "30517-02.htm";
						break;
					
					case GARITA:
						if (cond == 1 && st.getQuestItemsCount(BOLTERS_LIST) >= 1)
						{
							if (st.getQuestItemsCount(MINING_BOOTS) == 0)
							{
								htmltext = "30518-01.htm";
								st.giveItems(MINING_BOOTS, 1);
								st.playSound(QuestState.SOUND_ITEMGET);
							}
							else
								htmltext = "30518-02.htm";
						}
						else if (cond == 2)
							htmltext = "30518-02.htm";
						break;
					
					case REED:
						if (cond == 1 && st.getQuestItemsCount(BOLTERS_LIST) >= 1)
						{
							if (st.getQuestItemsCount(REDSTONE_BEER) == 0)
							{
								htmltext = "30520-01.htm";
								st.giveItems(REDSTONE_BEER, 1);
								st.playSound(QuestState.SOUND_ITEMGET);
							}
							else
								htmltext = "30520-02.htm";
						}
						else if (cond == 2)
							htmltext = "30520-02.htm";
						break;
					
					case BRUNON:
						if (cond == 1 && st.getQuestItemsCount(BOLTERS_LIST) >= 1)
						{
							if (st.getQuestItemsCount(MINERS_PICK) == 0)
								htmltext = "30526-01.htm";
							else
								htmltext = "30526-03.htm";
						}
						else if (cond == 2)
							htmltext = "30526-03.htm";
						break;
				}
				
				if (cond == 1 && st.getQuestItemsCount(BOLTERS_LIST) >= 1 && (st.getQuestItemsCount(MINING_BOOTS) + st.getQuestItemsCount(MINERS_PICK) + st.getQuestItemsCount(BOOMBOOM_POWDER) + st.getQuestItemsCount(REDSTONE_BEER) >= 4))
				{
					st.set("cond", "2");
					st.playSound(QuestState.SOUND_MIDDLE);
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
		new Q005_MinersFavor(5, qn, "Miner's Favor");
	}
}