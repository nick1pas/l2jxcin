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
package quests.Q340_SubjugationOfLizardmen;

import net.xcine.gameserver.model.actor.L2Npc;
import net.xcine.gameserver.model.actor.instance.L2PcInstance;
import net.xcine.gameserver.model.quest.Quest;
import net.xcine.gameserver.model.quest.QuestState;

public class Q340_SubjugationOfLizardmen extends Quest
{
	private final static String qn = "Q340_SubjugationOfLizardmen";
	
	// NPCs
	private static final int WEISZ = 30385;
	private static final int ADONIUS = 30375;
	private static final int LEVIAN = 30037;
	private static final int CHEST = 30989;
	
	// Items
	private static final int CARGO = 4255;
	private static final int HOLY = 4256;
	private static final int ROSARY = 4257;
	private static final int TOTEM = 4258;
	
	public Q340_SubjugationOfLizardmen(int questId, String name, String descr)
	{
		super(questId, name, descr);
		
		questItemIds = new int[]
		{
			CARGO,
			HOLY,
			ROSARY,
			TOTEM
		};
		
		addStartNpc(WEISZ);
		addTalkId(WEISZ, ADONIUS, LEVIAN, CHEST);
		
		addKillId(20008, 20010, 20014, 20357, 21100, 20356, 21101, 25146);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = event;
		QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("30385-03.htm"))
		{
			st.set("cond", "1");
			st.setState(STATE_STARTED);
			st.playSound(QuestState.SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("30385-07.htm"))
		{
			st.set("cond", "2");
			st.playSound(QuestState.SOUND_MIDDLE);
			st.takeItems(CARGO, -1);
		}
		else if (event.equalsIgnoreCase("30385-09.htm"))
		{
			st.takeItems(CARGO, -1);
			st.rewardItems(57, 4090);
		}
		else if (event.equalsIgnoreCase("30385-10.htm"))
		{
			st.takeItems(CARGO, -1);
			st.rewardItems(57, 4090);
			st.exitQuest(true);
		}
		else if (event.equalsIgnoreCase("30375-02.htm"))
		{
			st.set("cond", "3");
			st.playSound(QuestState.SOUND_MIDDLE);
		}
		else if (event.equalsIgnoreCase("30037-02.htm"))
		{
			st.set("cond", "5");
			st.playSound(QuestState.SOUND_MIDDLE);
		}
		else if (event.equalsIgnoreCase("30989-02.htm"))
		{
			st.set("cond", "6");
			st.giveItems(TOTEM, 1);
			st.playSound(QuestState.SOUND_MIDDLE);
		}
		
		return htmltext;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		String htmltext = Quest.getNoQuestMsg();
		QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;
		
		switch (st.getState())
		{
			case STATE_CREATED:
				if (player.getLevel() < 17)
				{
					htmltext = "30385-01.htm";
					st.exitQuest(true);
				}
				else
					htmltext = "30385-02.htm";
				break;
			
			case STATE_STARTED:
				int cond = st.getInt("cond");
				switch (npc.getNpcId())
				{
					case WEISZ:
						if (cond == 1)
						{
							if (st.getQuestItemsCount(CARGO) < 30)
								htmltext = "30385-05.htm";
							else
								htmltext = "30385-06.htm";
						}
						else if (cond == 2)
							htmltext = "30385-11.htm";
						else if (cond == 7)
						{
							htmltext = "30385-13.htm";
							st.rewardItems(57, 14700);
							st.playSound(QuestState.SOUND_FINISH);
							st.exitQuest(false);
						}
						break;
					
					case ADONIUS:
						if (cond == 2)
							htmltext = "30375-01.htm";
						else if (cond == 3)
						{
							if (st.hasQuestItems(ROSARY) && st.hasQuestItems(HOLY))
							{
								htmltext = "30375-04.htm";
								st.set("cond", "4");
								st.playSound(QuestState.SOUND_MIDDLE);
								st.takeItems(HOLY, -1);
								st.takeItems(ROSARY, -1);
							}
							else
								htmltext = "30375-03.htm";
						}
						else if (cond == 4)
							htmltext = "30375-05.htm";
						break;
					
					case LEVIAN:
						if (cond == 4)
							htmltext = "30037-01.htm";
						else if (cond == 5)
							htmltext = "30037-03.htm";
						else if (cond == 6)
						{
							htmltext = "30037-04.htm";
							st.set("cond", "7");
							st.playSound(QuestState.SOUND_MIDDLE);
							st.takeItems(TOTEM, -1);
						}
						else if (cond == 7)
							htmltext = "30037-05.htm";
						break;
					
					case CHEST:
						if (cond == 5)
							htmltext = "30989-01.htm";
						else
							htmltext = "30989-03.htm";
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
		// Bifron's Chest.
		if (npc.getNpcId() == 25146)
		{
			addSpawn(CHEST, npc, false, 30000, false);
			return null;
		}
		
		QuestState st = checkPlayerState(player, npc, STATE_STARTED);
		if (st == null)
			return null;
		
		switch (npc.getNpcId())
		{
			case 20008:
			case 20010:
			case 20014:
				if (st.getInt("cond") == 1)
					st.dropItems(CARGO, 1, 30, 400000);
				break;
			
			case 20357:
			case 21100:
			case 20356:
			case 21101:
				if (st.getInt("cond") == 3)
				{
					st.dropItems(HOLY, 1, 1, 150000);
					st.dropItems(ROSARY, 1, 1, 150000);
				}
				break;
		}
		return null;
	}
	
	public static void main(String[] args)
	{
		new Q340_SubjugationOfLizardmen(340, qn, "Subjugation of Lizardmen");
	}
}