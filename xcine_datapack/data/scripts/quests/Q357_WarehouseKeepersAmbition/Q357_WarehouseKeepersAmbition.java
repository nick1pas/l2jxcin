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
package quests.Q357_WarehouseKeepersAmbition;

import net.xcine.gameserver.model.actor.L2Npc;
import net.xcine.gameserver.model.actor.instance.L2PcInstance;
import net.xcine.gameserver.model.quest.Quest;
import net.xcine.gameserver.model.quest.QuestState;

public class Q357_WarehouseKeepersAmbition extends Quest
{
	private final static String qn = "Q357_WarehouseKeepersAmbition";
	
	// NPC
	private static final int SILVA = 30686;
	
	// Item
	private static final int JADE_CRYSTAL = 5867;
	
	// Rewards
	private static final int REWARD1 = 900;
	private static final int REWARD2 = 10000;
	
	public Q357_WarehouseKeepersAmbition(int questId, String name, String descr)
	{
		super(questId, name, descr);
		
		questItemIds = new int[]
		{
			JADE_CRYSTAL
		};
		
		addStartNpc(SILVA);
		addTalkId(SILVA);
		
		addKillId(20594, 20595, 20596, 20597);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = event;
		QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("30686-2.htm"))
		{
			st.set("cond", "1");
			st.setState(STATE_STARTED);
			st.playSound(QuestState.SOUND_ACCEPT);
		}
		// If number of qItems are >= 100, give the extra reward, else, give the normal reward.
		else if (event.equalsIgnoreCase("30686-7.htm"))
		{
			int count = st.getQuestItemsCount(JADE_CRYSTAL);
			if (count >= 1)
			{
				int reward;
				if (count >= 100)
					reward = (st.getQuestItemsCount(JADE_CRYSTAL) * REWARD1) + REWARD2;
				else
					reward = st.getQuestItemsCount(JADE_CRYSTAL) * REWARD1;
				
				st.takeItems(JADE_CRYSTAL, -1);
				st.rewardItems(57, reward);
			}
			else
				htmltext = "30686-4.htm";
		}
		else if (event.equalsIgnoreCase("30686-8.htm"))
		{
			st.playSound(QuestState.SOUND_FINISH);
			st.exitQuest(true);
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
				if (player.getLevel() >= 47)
					htmltext = "30686-0.htm";
				else
				{
					htmltext = "30686-0a.htm";
					st.exitQuest(true);
				}
				break;
			
			case STATE_STARTED:
				if (st.getQuestItemsCount(JADE_CRYSTAL) == 0)
					htmltext = "30686-4.htm";
				else if (st.getQuestItemsCount(JADE_CRYSTAL) >= 1)
					htmltext = "30686-6.htm";
				break;
		}
		return htmltext;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance player, boolean isPet)
	{
		L2PcInstance partyMember = getRandomPartyMemberState(player, npc, STATE_STARTED);
		if (partyMember == null)
			return null;
		
		QuestState st = partyMember.getQuestState(qn);
		
		st.dropItems(JADE_CRYSTAL, 1, -1, 500000);
		
		return null;
	}
	
	public static void main(String[] args)
	{
		new Q357_WarehouseKeepersAmbition(357, qn, "Warehouse Keeper's Ambition");
	}
}