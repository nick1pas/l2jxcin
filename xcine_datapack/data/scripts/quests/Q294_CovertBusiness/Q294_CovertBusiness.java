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
package quests.Q294_CovertBusiness;

import net.xcine.gameserver.model.actor.L2Npc;
import net.xcine.gameserver.model.actor.instance.L2PcInstance;
import net.xcine.gameserver.model.base.Race;
import net.xcine.gameserver.model.quest.Quest;
import net.xcine.gameserver.model.quest.QuestState;
import net.xcine.util.Rnd;

public class Q294_CovertBusiness extends Quest
{
	private static final String qn = "Q294_CovertBusiness";
	
	// Item
	private static final int BatFang = 1491;
	
	// Reward
	private static final int RingOfRaccoon = 1508;
	
	// Mobs
	private static final int Barded = 20370;
	private static final int Blade = 20480;
	
	// NPCs
	private static final int Keef = 30534;
	
	public Q294_CovertBusiness(int questId, String name, String descr)
	{
		super(questId, name, descr);
		
		questItemIds = new int[]
		{
			BatFang
		};
		
		addStartNpc(Keef);
		addTalkId(Keef);
		
		addKillId(Barded, Blade);
		
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = event;
		QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("30534-03.htm"))
		{
			st.setState(STATE_STARTED);
			st.set("cond", "1");
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
				if (player.getRace() == Race.Dwarf && player.getLevel() >= 10)
					htmltext = "30534-02.htm";
				else
				{
					htmltext = "30534-01.htm";
					st.exitQuest(true);
				}
				break;
			
			case STATE_STARTED:
				int cond = st.getInt("cond");
				if (cond == 1)
					htmltext = "30534-04.htm";
				else if (cond == 2)
				{
					htmltext = "30534-05.htm";
					st.takeItems(BatFang, -1);
					st.giveItems(RingOfRaccoon, 1);
					st.rewardExpAndSp(0, 600);
					st.exitQuest(true);
					st.playSound(QuestState.SOUND_FINISH);
				}
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
		
		if (st.dropItemsAlways(BatFang, Rnd.get(1, 4), 100))
			st.set("cond", "2");
		
		return null;
	}
	
	public static void main(String[] args)
	{
		new Q294_CovertBusiness(294, qn, "Covert Business");
	}
}