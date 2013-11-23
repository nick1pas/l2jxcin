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
package quests.Q344_1000YearsTheEndOfLamentation;

import net.xcine.gameserver.model.actor.L2Npc;
import net.xcine.gameserver.model.actor.instance.L2PcInstance;
import net.xcine.gameserver.model.quest.Quest;
import net.xcine.gameserver.model.quest.QuestState;
import net.xcine.util.Rnd;

public class Q344_1000YearsTheEndOfLamentation extends Quest
{
	private static final String qn = "Q344_1000YearsTheEndOfLamentation";
	
	// NPCs
	private static final int GILMORE = 30754;
	private static final int RODEMAI = 30756;
	private static final int ORVEN = 30857;
	private static final int KAIEN = 30623;
	private static final int GARVARENTZ = 30704;
	
	// Items
	private static final int ARTICLES_DEAD_HEROES = 4269;
	private static final int OLD_KEY = 4270;
	private static final int OLD_HILT = 4271;
	private static final int OLD_TOTEM = 4272;
	private static final int CRUCIFIX = 4273;
	
	public Q344_1000YearsTheEndOfLamentation(int questId, String name, String descr)
	{
		super(questId, name, descr);
		
		questItemIds = new int[]
		{
			ARTICLES_DEAD_HEROES,
			OLD_KEY,
			OLD_HILT,
			OLD_TOTEM,
			CRUCIFIX
		};
		
		addStartNpc(GILMORE);
		addTalkId(GILMORE, RODEMAI, ORVEN, GARVARENTZ, KAIEN);
		
		addKillId(20236, 20237, 20238, 20239, 20240);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = event;
		QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("30754-04.htm"))
		{
			st.set("cond", "1");
			st.setState(STATE_STARTED);
			st.playSound(QuestState.SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("30754-07.htm"))
		{
			if (st.get("success") != null)
			{
				st.set("cond", "1");
				st.unset("success");
				st.playSound(QuestState.SOUND_MIDDLE);
			}
		}
		else if (event.equalsIgnoreCase("30754-08.htm"))
		{
			st.playSound(QuestState.SOUND_FINISH);
			st.exitQuest(true);
		}
		else if (event.equalsIgnoreCase("30754-06.htm"))
		{
			if (!st.hasQuestItems(ARTICLES_DEAD_HEROES))
				htmltext = "30754-06a.htm";
			else
			{
				final int amount = st.getQuestItemsCount(ARTICLES_DEAD_HEROES);
				
				st.takeItems(ARTICLES_DEAD_HEROES, -1);
				st.giveItems(57, amount * 60);
				
				// Special item, % based on actual number of qItems.
				if (Rnd.get(1000) < Math.min(10, Math.max(1, amount / 10)))
					htmltext = "30754-10.htm";
			}
		}
		else if (event.equalsIgnoreCase("30754-11.htm"))
		{
			final int random = Rnd.get(4);
			if (random < 1)
			{
				htmltext = "30754-12.htm";
				st.giveItems(OLD_KEY, 1);
			}
			else if (random < 2)
			{
				htmltext = "30754-13.htm";
				st.giveItems(OLD_HILT, 1);
			}
			else if (random < 3)
			{
				htmltext = "30754-14.htm";
				st.giveItems(OLD_TOTEM, 1);
			}
			else
				st.giveItems(CRUCIFIX, 1);
			
			st.set("cond", "2");
			st.playSound(QuestState.SOUND_MIDDLE);
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
				if (player.getLevel() < 48)
				{
					htmltext = "30754-01.htm";
					st.exitQuest(true);
				}
				else
					htmltext = "30754-02.htm";
				break;
			
			case STATE_STARTED:
				int cond = st.getInt("cond");
				switch (npc.getNpcId())
				{
					case GILMORE:
						if (cond == 1)
						{
							if (st.hasQuestItems(ARTICLES_DEAD_HEROES))
								htmltext = "30754-05.htm";
							else
								htmltext = "30754-09.htm";
						}
						else if (cond == 2)
							htmltext = (st.get("success") != null) ? "30754-16.htm" : "30754-15.htm";
						break;
					
					default:
						if (cond == 2)
						{
							if (st.get("success") != null)
								htmltext = npc.getNpcId() + "-02.htm";
							else
							{
								rewards(st, npc.getNpcId());
								htmltext = npc.getNpcId() + "-01.htm";
							}
						}
						break;
				}
				break;
		}
		return htmltext;
	}
	
	private void rewards(QuestState st, int npcId)
	{
		switch (npcId)
		{
			case ORVEN:
				if (st.hasQuestItems(CRUCIFIX))
				{
					st.set("success", "1");
					st.takeItems(CRUCIFIX, -1);
					
					final int chance = Rnd.get(100);
					if (chance < 80)
						st.giveItems(1875, 19);
					else if (chance < 95)
						st.giveItems(952, 5);
					else
						st.giveItems(2437, 1);
				}
				break;
			
			case GARVARENTZ:
				if (st.hasQuestItems(OLD_TOTEM))
				{
					st.set("success", "1");
					st.takeItems(OLD_TOTEM, -1);
					
					final int chance = Rnd.get(100);
					if (chance < 55)
						st.giveItems(1882, 70);
					else if (chance < 99)
						st.giveItems(1881, 50);
					else
						st.giveItems(191, 1);
				}
				break;
			
			case KAIEN:
				if (st.hasQuestItems(OLD_HILT))
				{
					st.set("success", "1");
					st.takeItems(OLD_HILT, -1);
					
					final int chance = Rnd.get(100);
					if (chance < 60)
						st.giveItems(1874, 25);
					else if (chance < 85)
						st.giveItems(1887, 10);
					else if (chance < 99)
						st.giveItems(951, 1);
					else
						st.giveItems(133, 1);
				}
				break;
			
			case RODEMAI:
				if (st.hasQuestItems(OLD_KEY))
				{
					st.set("success", "1");
					st.takeItems(OLD_KEY, -1);
					
					final int chance = Rnd.get(100);
					if (chance < 80)
						st.giveItems(1879, 55);
					else if (chance < 95)
						st.giveItems(951, 1);
					else
						st.giveItems(885, 1);
				}
				break;
		}
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance player, boolean isPet)
	{
		QuestState st = checkPlayerCondition(player, npc, "cond", "1");
		if (st == null)
			return null;
		
		st.dropItems(ARTICLES_DEAD_HEROES, 1, -1, (36 + ((npc.getNpcId() - 20234) * 2)) * 10000);
		return null;
	}
	
	public static void main(String[] args)
	{
		new Q344_1000YearsTheEndOfLamentation(344, qn, "1000 Years, the End of Lamentation");
	}
}