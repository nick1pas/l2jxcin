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
package quests.Q164_BloodFiend;

import net.xcine.gameserver.model.actor.L2Npc;
import net.xcine.gameserver.model.actor.instance.L2PcInstance;
import net.xcine.gameserver.model.base.Race;
import net.xcine.gameserver.model.quest.Quest;
import net.xcine.gameserver.model.quest.QuestState;

public class Q164_BloodFiend extends Quest
{
	private final static String qn = "Q164_BloodFiend";
	
	// Item
	private static final int KIRUNAK_SKULL = 1044;
	
	// Reward
	private static final int ADENA = 57;
	
	public Q164_BloodFiend(int questId, String name, String descr)
	{
		super(questId, name, descr);
		
		questItemIds = new int[]
		{
			KIRUNAK_SKULL
		};
		
		addStartNpc(30149);
		addTalkId(30149);
		
		addKillId(27021);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = event;
		QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("30149-04.htm"))
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
		String htmltext = getNoQuestMsg();
		QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;
		
		switch (st.getState())
		{
			case STATE_CREATED:
				if (player.getRace() == Race.DarkElf)
				{
					htmltext = "30149-00.htm";
					st.exitQuest(true);
				}
				else if (player.getLevel() >= 21)
					htmltext = "30149-03.htm";
				else
				{
					htmltext = "30149-02.htm";
					st.exitQuest(true);
				}
				break;
			
			case STATE_STARTED:
				if (st.getQuestItemsCount(KIRUNAK_SKULL) == 1)
				{
					htmltext = "30149-06.htm";
					st.takeItems(KIRUNAK_SKULL, 1);
					st.rewardItems(ADENA, 42130);
					st.playSound(QuestState.SOUND_FINISH);
					st.exitQuest(false);
				}
				else
					htmltext = "30149-05.htm";
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
		
		if (!st.hasQuestItems(KIRUNAK_SKULL))
		{
			st.set("cond", "2");
			st.giveItems(KIRUNAK_SKULL, 1);
			st.playSound(QuestState.SOUND_MIDDLE);
		}
		
		return null;
	}
	
	public static void main(String[] args)
	{
		new Q164_BloodFiend(164, qn, "Blood Fiend");
	}
}