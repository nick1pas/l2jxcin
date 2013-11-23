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
package quests.Q264_KeenClaws;

import net.xcine.gameserver.model.actor.L2Npc;
import net.xcine.gameserver.model.actor.instance.L2PcInstance;
import net.xcine.gameserver.model.quest.Quest;
import net.xcine.gameserver.model.quest.QuestState;
import net.xcine.util.Rnd;

public class Q264_KeenClaws extends Quest
{
	private final static String qn = "Q264_KeenClaws";
	
	// Item
	private static final int WOLF_CLAW = 1367;
	
	// NPC
	private static final int PAYNE = 30136;
	
	// Mobs
	private static final int GOBLIN = 20003;
	private static final int WOLF = 20456;
	
	// Rewards
	private static final int LeatherSandals = 36;
	private static final int WoodenHelmet = 43;
	private static final int Stockings = 462;
	private static final int HealingPotion = 1061;
	private static final int ShortGloves = 48;
	private static final int ClothShoes = 35;
	
	public Q264_KeenClaws(int questId, String name, String descr)
	{
		super(questId, name, descr);
		
		questItemIds = new int[]
		{
			WOLF_CLAW
		};
		
		addStartNpc(PAYNE);
		addTalkId(PAYNE);
		
		addKillId(GOBLIN, WOLF);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = event;
		QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("30136-03.htm"))
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
				if (player.getLevel() >= 3)
					htmltext = "30136-02.htm";
				else
				{
					htmltext = "30136-01.htm";
					st.exitQuest(true);
				}
				break;
			
			case STATE_STARTED:
				int count = st.getQuestItemsCount(WOLF_CLAW);
				
				if (count < 50)
					htmltext = "30136-04.htm";
				else
				{
					st.takeItems(WOLF_CLAW, -1);
					
					int n = Rnd.get(17);
					if (n == 0)
					{
						st.giveItems(WoodenHelmet, 1);
						st.playSound(QuestState.SOUND_JACKPOT);
					}
					else if (n < 2)
						st.giveItems(57, 1000);
					else if (n < 5)
						st.giveItems(LeatherSandals, 1);
					else if (n < 8)
					{
						st.giveItems(Stockings, 1);
						st.giveItems(57, 50);
					}
					else if (n < 11)
						st.giveItems(HealingPotion, 1);
					else if (n < 14)
						st.giveItems(ShortGloves, 1);
					else
						st.giveItems(ClothShoes, 1);
					
					htmltext = "30136-05.htm";
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
		
		if (st.dropItems(WOLF_CLAW, Rnd.get(1, 8), 50, 800000))
			st.set("cond", "2");
		
		return null;
	}
	
	public static void main(String[] args)
	{
		new Q264_KeenClaws(264, qn, "Keen Claws");
	}
}