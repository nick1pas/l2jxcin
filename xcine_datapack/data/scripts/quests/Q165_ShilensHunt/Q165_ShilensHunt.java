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
package quests.Q165_ShilensHunt;

import net.xcine.gameserver.model.actor.L2Npc;
import net.xcine.gameserver.model.actor.instance.L2PcInstance;
import net.xcine.gameserver.model.base.Race;
import net.xcine.gameserver.model.quest.Quest;
import net.xcine.gameserver.model.quest.QuestState;

public class Q165_ShilensHunt extends Quest
{
	private final static String qn = "Q165_ShilensHunt";
	
	// Items
	private static final int DARK_BEZOAR = 1160;
	private static final int LESSER_HEALING_POTION = 1060;
	
	// NPC
	private static final int NELSYA = 30348;
	
	public Q165_ShilensHunt(int questId, String name, String descr)
	{
		super(questId, name, descr);
		
		questItemIds = new int[]
		{
			DARK_BEZOAR
		};
		
		addStartNpc(NELSYA);
		addTalkId(NELSYA);
		
		addKillId(20456, 20529, 20532, 20536);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = event;
		QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("30348-03.htm"))
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
					if (player.getLevel() >= 3)
						htmltext = "30348-02.htm";
					else
					{
						htmltext = "30348-01.htm";
						st.exitQuest(true);
					}
				}
				else
				{
					htmltext = "30348-00.htm";
					st.exitQuest(true);
				}
				break;
			
			case STATE_STARTED:
				if (st.getQuestItemsCount(DARK_BEZOAR) >= 13)
				{
					htmltext = "30348-05.htm";
					st.takeItems(DARK_BEZOAR, -1);
					st.rewardItems(LESSER_HEALING_POTION, 5);
					st.rewardExpAndSp(1000, 0);
					st.exitQuest(false);
					st.playSound(QuestState.SOUND_FINISH);
				}
				else
					htmltext = "30348-04.htm";
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
		
		if (st.dropItems(DARK_BEZOAR, 1, 13, 200000))
			st.set("cond", "2");
		
		return null;
	}
	
	public static void main(String[] args)
	{
		new Q165_ShilensHunt(165, qn, "Shilen's Hunt");
	}
}