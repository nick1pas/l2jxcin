/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package quests.Q662_AGameOfCards;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.xcine.gameserver.model.actor.L2Npc;
import net.xcine.gameserver.model.actor.instance.L2PcInstance;
import net.xcine.gameserver.model.quest.Quest;
import net.xcine.gameserver.model.quest.QuestState;
import net.xcine.util.Rnd;

public class Q662_AGameOfCards extends Quest
{
	private static final String qn = "Q662_AGameOfCards";
	
	protected final static Map<Integer, CardGame> Games = new ConcurrentHashMap<>();
	
	// NPC
	private final static int KLUMP = 30845;
	
	// Items
	private final static int RED_GEM = 8765;
	
	private final static int Enchant_Weapon_S = 959;
	private final static int Enchant_Weapon_A = 729;
	private final static int Enchant_Weapon_B = 947;
	private final static int Enchant_Weapon_C = 951;
	private final static int Enchant_Weapon_D = 955;
	private final static int Enchant_Armor_D = 956;
	private final static int ZIGGOS_GEMSTONE = 8868;
	
	// Monsters
	private final static int[] mobs =
	{
		20677,
		21109,
		21112,
		21116,
		21114,
		21004,
		21002,
		21006,
		21008,
		21010,
		18001,
		20672,
		20673,
		20674,
		20955,
		20962,
		20961,
		20959,
		20958,
		20966,
		20965,
		20968,
		20973,
		20972,
		21278,
		21279,
		21280,
		21286,
		21287,
		21288,
		21520,
		21526,
		21530,
		21535,
		21508,
		21510,
		21513,
		21515
	};
	
	public Q662_AGameOfCards(int questId, String name, String descr)
	{
		super(questId, name, descr);
		
		questItemIds = new int[]
		{
			RED_GEM
		};
		
		addStartNpc(KLUMP);
		addTalkId(KLUMP);
		
		addKillId(mobs);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = event;
		QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("30845-02.htm"))
		{
			st.set("cond", "1");
			st.setState(STATE_STARTED);
			st.playSound(QuestState.SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("30845-07.htm"))
		{
			st.playSound(QuestState.SOUND_FINISH);
			st.exitQuest(true);
		}
		else if (event.equalsIgnoreCase("30845-03.htm") && (st.getQuestItemsCount(RED_GEM) >= 50))
		{
			htmltext = "30845-04.htm";
		}
		else if (event.equalsIgnoreCase("30845-10.htm"))
		{
			if (st.getQuestItemsCount(RED_GEM) < 50)
			{
				htmltext = "30845-10a.htm";
			}
			
			st.takeItems(RED_GEM, 50);
			
			int objId = player.getObjectId();
			if (Games.containsKey(objId))
				Games.remove(objId);
			
			Games.put(objId, new CardGame(objId));
		}
		else if (event.equalsIgnoreCase("play"))
		{
			int objId = player.getObjectId();
			if (!Games.containsKey(objId))
				return null;
			
			return Games.get(objId).playField(player);
		}
		else if (event.startsWith("card"))
		{
			int objId = player.getObjectId();
			if (!Games.containsKey(objId))
				return null;
			try
			{
				int cardn = Integer.valueOf(event.replaceAll("card", ""));
				return Games.get(objId).next(cardn, st, player);
			}
			catch (Exception E)
			{
				return null;
			}
		}
		
		return htmltext;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		QuestState st = player.getQuestState(getName());
		String htmltext = getNoQuestMsg();
		if (st == null)
			return htmltext;
		
		switch (st.getState())
		{
			case STATE_CREATED:
				if (player.getLevel() < 61)
				{
					st.exitQuest(true);
					htmltext = "30845-00.htm";
				}
				else
				{
					htmltext = "30845-01.htm";
				}
				break;
			
			case STATE_STARTED:
				if (st.getQuestItemsCount(RED_GEM) < 50)
					htmltext = "30845-03.htm";
				else
					htmltext = "30845-04.htm";
				break;
		}
		
		return htmltext;
	}
	
	@Override
	public final String onKill(L2Npc npc, L2PcInstance player, boolean isSummon)
	{
		QuestState st = checkPlayerState(player, npc, STATE_STARTED);
		if (st == null)
			return null;
		
		if (Rnd.get(100) < 45)
		{
			st.giveItems(RED_GEM, 1);
		}
		
		return null;
	}
	
	private static class CardGame
	{
		private final String[] cards = new String[5];
		private final int player_id;
		private final static String[] card_chars = new String[]
		{
			"A",
			"1",
			"2",
			"3",
			"4",
			"5",
			"6",
			"7",
			"8",
			"9",
			"10",
			"J",
			"Q",
			"K"
		};
		
		private final static String html_header = "<html><body>";
		private final static String html_footer = "</body></html>";
		private final static String table_header = "<table border=\"1\" cellpadding=\"3\"><tr>";
		private final static String table_footer = "</tr></table><br><br>";
		private final static String td_begin = "<center><td width=\"50\" align=\"center\"><br><br><br> ";
		private final static String td_end = " <br><br><br><br></td></center>";
		
		public CardGame(int _player_id)
		{
			player_id = _player_id;
			for (int i = 0; i < cards.length; i++)
			{
				cards[i] = "<a action=\"bypass -h Quest Q662_AGameOfCards card" + i + "\">?</a>";
			}
		}
		
		public String next(int cardn, QuestState st, L2PcInstance player)
		{
			if ((cardn >= cards.length) || !cards[cardn].startsWith("<a"))
			{
				return null;
			}
			cards[cardn] = card_chars[Rnd.get(card_chars.length)];
			for (String card : cards)
			{
				if (card.startsWith("<a"))
				{
					return playField(player);
				}
			}
			return finish(st, player);
		}
		
		private String finish(QuestState st, L2PcInstance player)
		{
			String result = html_header + table_header;
			Map<String, Integer> matches = new HashMap<>();
			for (String card : cards)
			{
				int count = matches.containsKey(card) ? matches.remove(card) : 0;
				count++;
				matches.put(card, count);
			}
			for (String card : cards)
			{
				if (matches.get(card) < 2)
				{
					matches.remove(card);
				}
			}
			String[] smatches = matches.keySet().toArray(new String[matches.size()]);
			Integer[] cmatches = matches.values().toArray(new Integer[matches.size()]);
			String txt = "Hmmm...? This is... No pair? Tough luck, my friend! Want to try again? Perhaps your luck will take a turn for the better...";
			if (cmatches.length == 1)
			{
				if (cmatches[0] == 5)
				{
					txt = "Hmmm...? This is... Five of a kind!!!! What luck! The goddess of victory must be with you! Here is your prize! Well earned, well played!";
					st.giveItems(ZIGGOS_GEMSTONE, 43);
					st.giveItems(Enchant_Weapon_S, 3);
					st.giveItems(Enchant_Weapon_A, 1);
				}
				else if (cmatches[0] == 4)
				{
					txt = "Hmmm...? This is... Four of a kind! Well done, my young friend! That sort of hand doesn't come up very often, that's for sure. Here's your prize.";
					st.giveItems(Enchant_Weapon_S, 2);
					st.giveItems(Enchant_Weapon_C, 2);
				}
				else if (cmatches[0] == 3)
				{
					txt = "Hmmm...? This is... Three of a kind? Very good, you are very lucky. Here's your prize.";
					st.giveItems(Enchant_Weapon_C, 2);
				}
				else if (cmatches[0] == 2)
				{
					txt = "Hmmm...? This is... Two pairs? You got lucky this time, but I wonder if it'll last. Here's your prize.";
					st.giveItems(Enchant_Armor_D, 2);
				}
			}
			else if (cmatches.length == 2)
			{
				if ((cmatches[0] == 3) || (cmatches[1] == 3))
				{
					txt = "Hmmm...? This is... A full house? Excellent! you're better than I thought. Here's your prize.";
					st.giveItems(Enchant_Weapon_A, 1);
					st.giveItems(Enchant_Weapon_B, 2);
					st.giveItems(Enchant_Weapon_D, 1);
				}
				else
				{
					txt = "Hmmm...? This is... Two pairs? You got lucky this time, but I wonder if it'll last. Here's your prize.";
					st.giveItems(Enchant_Weapon_C, 1);
				}
			}
			
			for (String card : cards)
			{
				if ((smatches.length > 0) && smatches[0].equalsIgnoreCase(card))
				{
					result += td_begin + "<font color=\"55FD44\">" + card + "</font>" + td_end;
				}
				else if ((smatches.length == 2) && smatches[1].equalsIgnoreCase(card))
				{
					result += td_begin + "<font color=\"FE6666\">" + card + "</font>" + td_end;
				}
				else
				{
					result += td_begin + card + td_end;
				}
			}
			
			result += table_footer + txt;
			if (st.getQuestItemsCount(RED_GEM) >= 50)
			{
				result += "<br><br><a action=\"bypass -h Quest Q662_AGameOfCards 30845-10.htm\">" + "Play Again!" + "</a>";
			}
			result += html_footer;
			Games.remove(player_id);
			return result;
		}
		
		public String playField(L2PcInstance player)
		{
			String result = html_header + table_header;
			for (String card : cards)
			{
				result += td_begin + card + td_end;
			}
			result += table_footer + "" + "Check your next card." + "" + html_footer;
			return result;
		}
	}
	
	public static void main(String[] args)
	{
		new Q662_AGameOfCards(662, qn, "A Game Of Cards");
	}
}