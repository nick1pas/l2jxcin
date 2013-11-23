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
package quests.Q154_SacrificeToTheSea;

import net.xcine.gameserver.model.actor.L2Npc;
import net.xcine.gameserver.model.actor.instance.L2PcInstance;
import net.xcine.gameserver.model.quest.Quest;
import net.xcine.gameserver.model.quest.QuestState;

public class Q154_SacrificeToTheSea extends Quest
{
	private final static String qn = "Q154_SacrificeToTheSea";
	
	// NPCs
	private static final int ROCKSWELL = 30312;
	private static final int CRISTEL = 30051;
	private static final int ROLFE = 30055;
	
	// Items
	private static final int FOX_FUR = 1032;
	private static final int FOX_FUR_YARN = 1033;
	private static final int MAIDEN_DOLL = 1034;
	
	// Reward
	private static final int EARING = 113;
	
	public Q154_SacrificeToTheSea(int questId, String name, String descr)
	{
		super(questId, name, descr);
		
		questItemIds = new int[]
		{
			FOX_FUR,
			FOX_FUR_YARN,
			MAIDEN_DOLL
		};
		
		addStartNpc(ROCKSWELL);
		addTalkId(ROCKSWELL, CRISTEL, ROLFE);
		
		// Following Keltirs can be found near Talking Island.
		addKillId(20481, 20544, 20545);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = event;
		QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("30312-04.htm"))
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
				if (player.getLevel() >= 2)
					htmltext = "30312-03.htm";
				else
				{
					htmltext = "30312-02.htm";
					st.exitQuest(true);
				}
				break;
			
			case STATE_STARTED:
				int cond = st.getInt("cond");
				switch (npc.getNpcId())
				{
					case ROCKSWELL:
						if (cond == 1)
							htmltext = "30312-05.htm";
						else if (cond == 2 && st.getQuestItemsCount(FOX_FUR) >= 10)
							htmltext = "30312-08.htm";
						else if (cond == 3 && st.getQuestItemsCount(FOX_FUR_YARN) >= 1)
							htmltext = "30312-06.htm";
						else if (cond == 4 && st.getQuestItemsCount(MAIDEN_DOLL) >= 1)
						{
							htmltext = "30312-07.htm";
							st.giveItems(EARING, 1);
							st.takeItems(MAIDEN_DOLL, -1);
							st.rewardExpAndSp(100, 0);
							st.playSound(QuestState.SOUND_FINISH);
							st.exitQuest(false);
						}
						break;
					
					case CRISTEL:
						if (cond == 1)
						{
							if (st.getQuestItemsCount(FOX_FUR) > 0)
								htmltext = "30051-01.htm";
							else
								htmltext = "30051-01a.htm";
						}
						else if (cond == 2 && st.getQuestItemsCount(FOX_FUR) >= 10)
						{
							htmltext = "30051-02.htm";
							st.giveItems(FOX_FUR_YARN, 1);
							st.takeItems(FOX_FUR, -1);
							st.set("cond", "3");
							st.playSound(QuestState.SOUND_MIDDLE);
						}
						else if (cond == 3 && st.getQuestItemsCount(FOX_FUR_YARN) >= 1)
							htmltext = "30051-03.htm";
						else if (cond == 4 && st.getQuestItemsCount(MAIDEN_DOLL) >= 1)
							htmltext = "30051-04.htm";
						break;
					
					case ROLFE:
						if (cond == 3 && st.getQuestItemsCount(FOX_FUR_YARN) >= 1)
						{
							htmltext = "30055-01.htm";
							st.giveItems(MAIDEN_DOLL, 1);
							st.takeItems(FOX_FUR_YARN, -1);
							st.set("cond", "4");
							st.playSound(QuestState.SOUND_MIDDLE);
						}
						else if (cond == 4 && st.getQuestItemsCount(MAIDEN_DOLL) >= 1)
							htmltext = "30055-02.htm";
						else if (cond >= 1 && cond <= 2)
							htmltext = "30055-03.htm";
						break;
				}
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
		
		if (st.dropItems(FOX_FUR, 1, 10, 400000))
			st.set("cond", "2");
		
		return null;
	}
	
	public static void main(String[] args)
	{
		new Q154_SacrificeToTheSea(154, qn, "Sacrifice to the Sea");
	}
}