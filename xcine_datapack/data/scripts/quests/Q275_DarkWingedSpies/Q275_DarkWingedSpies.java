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
package quests.Q275_DarkWingedSpies;

import net.xcine.gameserver.model.actor.L2Npc;
import net.xcine.gameserver.model.actor.instance.L2PcInstance;
import net.xcine.gameserver.model.base.Race;
import net.xcine.gameserver.model.quest.Quest;
import net.xcine.gameserver.model.quest.QuestState;
import net.xcine.util.Rnd;

public class Q275_DarkWingedSpies extends Quest
{
	private final static String qn = "Q275_DarkWingedSpies";
	
	// NPC
	private static final int TANTUS = 30567;
	
	// Monsters
	private static final int DARKWING_BAT = 20316;
	private static final int VARANGKA_TRACKER = 27043;
	
	// Items
	private static final int DARKWING_BAT_FANG = 1478;
	private static final int VARANGKAS_PARASITE = 1479;
	
	// Reward
	private static final int ADENA = 57;
	
	public Q275_DarkWingedSpies(int questId, String name, String descr)
	{
		super(questId, name, descr);
		
		questItemIds = new int[]
		{
			DARKWING_BAT_FANG,
			VARANGKAS_PARASITE
		};
		
		addStartNpc(TANTUS);
		addTalkId(TANTUS);
		
		addKillId(DARKWING_BAT, VARANGKA_TRACKER);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = event;
		QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("30567-03.htm"))
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
				if (player.getRace() == Race.Orc)
				{
					if (player.getLevel() >= 11)
						htmltext = "30567-02.htm";
					else
					{
						htmltext = "30567-01.htm";
						st.exitQuest(true);
					}
				}
				else
				{
					htmltext = "30567-00.htm";
					st.exitQuest(true);
				}
				break;
			
			case STATE_STARTED:
				if (st.getQuestItemsCount(DARKWING_BAT_FANG) < 70)
					htmltext = "30567-04.htm";
				else
				{
					htmltext = "30567-05.htm";
					st.takeItems(DARKWING_BAT_FANG, -1);
					st.takeItems(VARANGKAS_PARASITE, -1);
					st.rewardItems(ADENA, 4220);
					st.playSound(QuestState.SOUND_FINISH);
					st.exitQuest(true);
				}
				break;
		}
		
		return htmltext;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance player, boolean isPet)
	{
		QuestState st = checkPlayerState(player, npc, STATE_STARTED);
		if (st == null)
			return null;
		
		switch (npc.getNpcId())
		{
			case DARKWING_BAT:
				if (st.getQuestItemsCount(DARKWING_BAT_FANG) < 70)
				{
					st.giveItems(DARKWING_BAT_FANG, 1);
					
					if (st.getQuestItemsCount(DARKWING_BAT_FANG) == 70)
					{
						st.playSound(QuestState.SOUND_MIDDLE);
						st.set("cond", "2");
					}
					else
						st.playSound(QuestState.SOUND_ITEMGET);
					
					// Spawn of Varangka Tracker on the npc position.
					if (st.getQuestItemsCount(DARKWING_BAT_FANG) < 66 && Rnd.get(100) < 10)
					{
						addSpawn(VARANGKA_TRACKER, npc, true, 0, true);
						st.giveItems(VARANGKAS_PARASITE, 1);
					}
				}
				break;
			
			case VARANGKA_TRACKER:
				if (st.getQuestItemsCount(DARKWING_BAT_FANG) < 66 && st.getQuestItemsCount(VARANGKAS_PARASITE) == 1)
				{
					st.takeItems(VARANGKAS_PARASITE, -1);
					st.giveItems(DARKWING_BAT_FANG, 5);
					
					if (st.getQuestItemsCount(DARKWING_BAT_FANG) == 70)
					{
						st.playSound(QuestState.SOUND_MIDDLE);
						st.set("cond", "2");
					}
					else
						st.playSound(QuestState.SOUND_ITEMGET);
				}
				break;
		}
		
		return null;
	}
	
	public static void main(String[] args)
	{
		new Q275_DarkWingedSpies(275, qn, "Dark Winged Spies");
	}
}