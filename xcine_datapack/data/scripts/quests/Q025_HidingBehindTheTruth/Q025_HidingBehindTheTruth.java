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
package quests.Q025_HidingBehindTheTruth;

import net.xcine.gameserver.model.actor.L2Npc;
import net.xcine.gameserver.model.actor.instance.L2PcInstance;
import net.xcine.gameserver.model.quest.Quest;
import net.xcine.gameserver.model.quest.QuestState;

public class Q025_HidingBehindTheTruth extends Quest
{
	private static final String qn = "Q025_HidingBehindTheTruth";
	
	private final int AGRIPEL = 31348;
	private final int BENEDICT = 31349;
	private final int BROKEN_BOOK_SHELF = 31534;
	private final int COFFIN = 31536;
	private final int MAID_OF_LIDIA = 31532;
	private final int MYSTERIOUS_WIZARD = 31522;
	private final int TOMBSTONE = 31531;
	
	private final int CONTRACT = 7066;
	private final int EARRING_OF_BLESSING = 874;
	private final int GEMSTONE_KEY = 7157;
	private final int LIDIAS_DRESS = 7155;
	private final int MAP_FOREST_OF_DEADMAN = 7063;
	private final int NECKLACE_OF_BLESSING = 936;
	private final int RING_OF_BLESSING = 905;
	private final int SUSPICIOUS_TOTEM_DOLL_1 = 7151;
	private final int SUSPICIOUS_TOTEM_DOLL_2 = 7156;
	private final int SUSPICIOUS_TOTEM_DOLL_3 = 7158;
	private final int TRIOLS_PAWN = 27218;
	private L2Npc COFFIN_SPAWN = null;
	
	public Q025_HidingBehindTheTruth(final int scriptId, final String name, final String descr)
	{
		super(scriptId, name, descr);
		
		addStartNpc(BENEDICT);
		
		addTalkId(AGRIPEL);
		addTalkId(BROKEN_BOOK_SHELF);
		addTalkId(COFFIN);
		addTalkId(MAID_OF_LIDIA);
		addTalkId(MYSTERIOUS_WIZARD);
		addTalkId(TOMBSTONE);
		
		addKillId(TRIOLS_PAWN);
		
		questItemIds = new int[]
		{
			SUSPICIOUS_TOTEM_DOLL_3
		};
	}
	
	@Override
	public String onAdvEvent(final String event, final L2Npc npc, final L2PcInstance player)
	{
		QuestState st = player.getQuestState(qn);
		
		if (st == null)
		{
			return event;
		}
		
		if (event.equalsIgnoreCase("StartQuest"))
		{
			if (st.getInt("cond") == 0)
			{
				st.setState(Quest.STATE_STARTED);
			}
			QuestState qs_24 = st.getPlayer().getQuestState("Q024_InhabitantsOfTheForrestOfTheDead");
			if ((qs_24 == null) || !qs_24.isCompleted())
			{
				st.set("cond", "1");
				st.playSound(QuestState.SOUND_ACCEPT);
				return "31349-02.htm";
			}
			st.playSound(QuestState.SOUND_ACCEPT);
			if (st.getQuestItemsCount(SUSPICIOUS_TOTEM_DOLL_1) == 0)
			{
				st.set("cond", "2");
				st.playSound(QuestState.SOUND_MIDDLE);
				return "31349-03a.htm";
			}
			return "31349-03.htm";
		}
		else if (event.equalsIgnoreCase("31349-10.htm"))
		{
			st.set("cond", "4");
		}
		else if (event.equalsIgnoreCase("31348-08.htm"))
		{
			if (st.getInt("cond") == 4)
			{
				st.set("cond", "5");
				st.playSound(QuestState.SOUND_MIDDLE);
				st.takeItems(SUSPICIOUS_TOTEM_DOLL_1, -1);
				st.takeItems(SUSPICIOUS_TOTEM_DOLL_2, -1);
				if (st.getQuestItemsCount(GEMSTONE_KEY) == 0)
				{
					st.giveItems(GEMSTONE_KEY, 1);
				}
			}
			else if (st.getInt("cond") == 5)
			{
				return "31348-08a.htm";
			}
		}
		else if (event.equalsIgnoreCase("31522-04.htm"))
		{
			st.set("cond", "6");
			st.playSound(QuestState.SOUND_MIDDLE);
			if (st.getQuestItemsCount(MAP_FOREST_OF_DEADMAN) == 0)
			{
				st.giveItems(MAP_FOREST_OF_DEADMAN, 1);
			}
		}
		else if (event.equalsIgnoreCase("31534-07.htm"))
		{
			addSpawn(TRIOLS_PAWN, player.getX() + 50, player.getY() + 50, player.getZ(), player.getHeading(), false, 0, false);
			st.set("cond", "7");
			st.playSound(QuestState.SOUND_MIDDLE);
		}
		else if (event.equalsIgnoreCase("31534-11.htm"))
		{
			st.set("id", "8");
			st.giveItems(CONTRACT, 1);
		}
		else if (event.equalsIgnoreCase("31532-07.htm"))
		{
			st.set("cond", "11");
		}
		else if (event.equalsIgnoreCase("31531-02.htm"))
		{
			st.set("cond", "12");
			st.playSound(QuestState.SOUND_MIDDLE);
			if (COFFIN_SPAWN != null)
			{
				COFFIN_SPAWN.deleteMe();
			}
			COFFIN_SPAWN = addSpawn(COFFIN, player.getX() + 50, player.getY() + 50, player.getZ(), player.getHeading(), false, 0, false);
			
			startQuestTimer("Coffin_Despawn", 120000, null, null, false);
		}
		else if (event.equalsIgnoreCase("Coffin_Despawn"))
		{
			if (COFFIN_SPAWN != null)
			{
				COFFIN_SPAWN.deleteMe();
			}
			
			if (st.getInt("cond") == 12)
			{
				st.set("cond", "11");
				st.playSound(QuestState.SOUND_MIDDLE);
			}
			return null;
		}
		else if (event.equalsIgnoreCase("Lidia_wait"))
		{
			st.set("id", "14");
			return null;
		}
		else if (event.equalsIgnoreCase("31532-21.htm"))
		{
			st.set("cond", "15");
		}
		else if (event.equalsIgnoreCase("31522-13.htm"))
		{
			st.set("cond", "16");
		}
		else if (event.equalsIgnoreCase("31348-16.htm"))
		{
			st.set("cond", "16");
		}
		else if (event.equalsIgnoreCase("31348-17.htm"))
		{
			st.set("cond", "17");
		}
		else if (event.equalsIgnoreCase("31348-14.htm"))
		{
			st.set("id", "16");
		}
		else if (event.equalsIgnoreCase("End1"))
		{
			if (st.getInt("cond") != 17)
			{
				return "31532-24.htm";
			}
			st.giveItems(RING_OF_BLESSING, 2);
			st.giveItems(EARRING_OF_BLESSING, 1);
			st.rewardExpAndSp(572277, 53750);
			st.exitQuest(false);
			st.unset("cond");
			return "31532-25.htm";
		}
		else if (event.equalsIgnoreCase("End2"))
		{
			if (st.getInt("cond") != 18)
			{
				return "31522-15a.htm";
			}
			st.giveItems(NECKLACE_OF_BLESSING, 1);
			st.giveItems(EARRING_OF_BLESSING, 1);
			st.rewardExpAndSp(572277, 53750);
			st.playSound("ItemSound.quest_finish");
			st.exitQuest(false);
			st.unset("cond");
			return "31522-16.htm";
		}
		return event;
	}
	
	@Override
	public String onTalk(final L2Npc npc, final L2PcInstance player)
	{
		String htmltext = getNoQuestMsg();
		QuestState st = player.getQuestState(qn);
		
		if (st == null)
		{
			return htmltext;
		}
		int npcId = npc.getNpcId();
		int cond = st.getInt("cond");
		int IntId = st.getInt("id");
		switch (npcId)
		{
			case BENEDICT:
				if ((cond == 0) || (cond == 1))
				{
					return "31349-01.htm";
				}
				if (cond == 2)
				{
					return st.getQuestItemsCount(SUSPICIOUS_TOTEM_DOLL_1) == 0 ? "31349-03a.htm" : "31349-03.htm";
				}
				if (cond == 3)
				{
					return "31349-03.htm";
				}
				if (cond == 4)
				{
					return "31349-11.htm";
				}
				break;
			
			case MYSTERIOUS_WIZARD:
				if (cond == 2)
				{
					st.set("cond", "3");
					st.giveItems(SUSPICIOUS_TOTEM_DOLL_2, 1);
					return "31522-01.htm";
				}
				if (cond == 3)
				{
					return "31522-02.htm";
				}
				if (cond == 5)
				{
					return "31522-03.htm";
				}
				if (cond == 6)
				{
					return "31522-05.htm";
				}
				if (cond == 8)
				{
					if (IntId != 8)
					{
						return "31522-05.htm";
					}
					st.set("cond", "9");
					st.playSound(QuestState.SOUND_MIDDLE);
					return "31522-06.htm";
				}
				if (cond == 15)
				{
					return "31522-06a.htm";
				}
				if (cond == 16)
				{
					return "31522-12.htm";
				}
				if (cond == 17)
				{
					return "31522-15a.htm";
				}
				if (cond == 18)
				{
					st.set("id", "18");
					return "31522-15.htm";
				}
				break;
			
			case AGRIPEL:
				if (cond == 4)
				{
					return "31348-01.htm";
				}
				if (cond == 5)
				{
					return "31348-03.htm";
				}
				if (cond == 16)
				{
					return IntId == 16 ? "31348-15.htm" : "31348-09.htm";
				}
				if ((cond == 17) || (cond == 18))
				{
					return "31348-15.htm";
				}
				break;
			
			case BROKEN_BOOK_SHELF:
				if (cond == 6)
				{
					return "31534-01.htm";
				}
				if (cond == 7)
				{
					return "31534-08.htm";
				}
				if (cond == 8)
				{
					return IntId == 8 ? "31534-06.htm" : "31534-10.htm";
				}
				break;
			
			case MAID_OF_LIDIA:
				if (cond == 9)
				{
					return st.getQuestItemsCount(CONTRACT) > 0 ? "31532-01.htm" : "You have no Contract...";
				}
				if ((cond == 11) || (cond == 12))
				{
					return "31532-08.htm";
				}
				if (cond == 13)
				{
					if (st.getQuestItemsCount(LIDIAS_DRESS) == 0)
					{
						return "31532-08.htm";
					}
					st.set("cond", "14");
					st.playSound(QuestState.SOUND_MIDDLE);
					startQuestTimer("Lidia_wait", 60000, null, null, false);
					st.takeItems(LIDIAS_DRESS, 1);
					return "31532-09.htm";
				}
				if (cond == 14)
				{
					return IntId == 14 ? "31532-10.htm" : "31532-09.htm";
				}
				if (cond == 17)
				{
					st.set("id", "17");
					return "31532-23.htm";
				}
				if (cond == 18)
				{
					return "31532-24.htm";
				}
				break;
			
			case TOMBSTONE:
				if (cond == 11)
				{
					return "31531-01.htm";
				}
				if (cond == 12)
				{
					return "31531-02.htm";
				}
				if (cond == 13)
				{
					return "31531-03.htm";
				}
				break;
			
			case COFFIN:
				if (cond == 12)
				{
					st.set("cond", "13");
					st.playSound(QuestState.SOUND_MIDDLE);
					st.giveItems(LIDIAS_DRESS, 1);
					return "31536-01.htm";
				}
				if (cond == 13)
				{
					return "31531-03.htm";
				}
				break;
		}
		return getNoQuestMsg();
	}
	
	@Override
	public final String onKill(final L2Npc npc, final L2PcInstance player, final boolean isPet)
	{
		QuestState st = player.getQuestState(qn);
		if (st == null)
		{
			return null;
		}
		if (st.getState() != Quest.STATE_STARTED)
		{
			return null;
		}
		
		int npcId = npc.getNpcId();
		int cond = st.getInt("cond");
		
		if ((npcId == TRIOLS_PAWN) && (cond == 7))
		{
			st.giveItems(SUSPICIOUS_TOTEM_DOLL_3, 1);
			st.playSound(QuestState.SOUND_MIDDLE);
			st.set("cond", "7");
		}
		
		return null;
	}
	
	public static void main(String[] args)
	{
		new Q025_HidingBehindTheTruth(25, "Q025_HidingBehindTheTruth", "Hiding Behind the Truth");
	}
}