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
package quests.Q366_SilverHairedShaman;

import net.xcine.gameserver.model.actor.L2Npc;
import net.xcine.gameserver.model.actor.instance.L2PcInstance;
import net.xcine.gameserver.model.quest.Quest;
import net.xcine.gameserver.model.quest.QuestState;

public class Q366_SilverHairedShaman extends Quest
{
	private static final String qn = "Q366_SilverHairedShaman";
	
	// NPC
	private static final int DIETER = 30111;
	
	// Item
	private static final int HAIR = 5874;
	
	public Q366_SilverHairedShaman(int questId, String name, String descr)
	{
		super(questId, name, descr);
		
		questItemIds = new int[]
		{
			HAIR
		};
		
		addStartNpc(DIETER);
		addTalkId(DIETER);
		
		addKillId(20986, 20987, 20988);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = event;
		QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("30111-2.htm"))
		{
			st.set("cond", "1");
			st.setState(STATE_STARTED);
			st.playSound(QuestState.SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("30111-6.htm"))
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
				if (player.getLevel() >= 48)
					htmltext = "30111-1.htm";
				else
				{
					htmltext = "30111-0.htm";
					st.exitQuest(true);
				}
				break;
			
			case STATE_STARTED:
				int count = st.getQuestItemsCount(HAIR);
				if (count == 0)
					htmltext = "30111-3.htm";
				else
				{
					htmltext = "30111-4.htm";
					st.takeItems(HAIR, -1);
					st.rewardItems(57, 12070 + 500 * count);
				}
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
		
		st.dropItems(HAIR, 1, -1, 550000);
		
		return null;
	}
	
	public static void main(String[] args)
	{
		new Q366_SilverHairedShaman(366, qn, "Silver Haired Shaman");
	}
}