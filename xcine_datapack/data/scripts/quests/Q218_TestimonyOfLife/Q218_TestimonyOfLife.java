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
package quests.Q218_TestimonyOfLife;

import net.xcine.gameserver.model.actor.L2Npc;
import net.xcine.gameserver.model.actor.instance.L2PcInstance;
import net.xcine.gameserver.model.base.Race;
import net.xcine.gameserver.model.itemcontainer.Inventory;
import net.xcine.gameserver.model.quest.Quest;
import net.xcine.gameserver.model.quest.QuestState;
import net.xcine.util.Rnd;

public class Q218_TestimonyOfLife extends Quest
{
	private static final String qn = "Q218_TestimonyOfLife";
	
	private static final int ASTERIOS = 30154;
	private static final int PUSKIN = 30300;
	private static final int THALIA = 30371;
	private static final int ADONIUS = 30375;
	private static final int ARKENIA = 30419;
	private static final int CARDIEN = 30460;
	private static final int ISAEL = 30655;
	
	// Items
	private static final int TALINS_SPEAR = 3026;
	private static final int MARK_OF_LIFE = 3140;
	private static final int CARDIENS_LETTER = 3141;
	private static final int CAMOMILE_CHARM = 3142;
	private static final int HIERARCHS_LETTER = 3143;
	private static final int MOONFLOWER_CHARM = 3144;
	private static final int GRAIL_DIAGRAM = 3145;
	private static final int THALIAS_LETTER1 = 3146;
	private static final int THALIAS_LETTER2 = 3147;
	private static final int THALIAS_INSTRUCTIONS = 3148;
	private static final int PUSHKINS_LIST = 3149;
	private static final int PURE_MITHRIL_CUP = 3150;
	private static final int ARKENIAS_CONTRACT = 3151;
	private static final int ARKENIAS_INSTRUCTIONS = 3152;
	private static final int ADONIUS_LIST = 3153;
	private static final int ANDARIEL_SCRIPTURE_COPY = 3154;
	private static final int STARDUST = 3155;
	private static final int ISAELS_INSTRUCTIONS = 3156;
	private static final int ISAELS_LETTER = 3157;
	private static final int GRAIL_OF_PURITY = 3158;
	private static final int TEARS_OF_UNICORN = 3159;
	private static final int WATER_OF_LIFE = 3160;
	private static final int PURE_MITHRIL_ORE = 3161;
	private static final int ANT_SOLDIER_ACID = 3162;
	private static final int WYRMS_TALON1 = 3163;
	private static final int SPIDER_ICHOR = 3164;
	private static final int HARPYS_DOWN = 3165;
	
	private static final int[] TALINS_PIECES =
	{
		3166,
		3167,
		3168,
		3169,
		3170,
		3171
	};
	
	public Q218_TestimonyOfLife(int questId, String name, String descr)
	{
		super(questId, name, descr);
		
		questItemIds = new int[]
		{
			TALINS_SPEAR,
			CARDIENS_LETTER,
			CAMOMILE_CHARM,
			HIERARCHS_LETTER,
			MOONFLOWER_CHARM,
			GRAIL_DIAGRAM,
			THALIAS_LETTER1,
			THALIAS_LETTER2,
			THALIAS_INSTRUCTIONS,
			PUSHKINS_LIST,
			PURE_MITHRIL_CUP,
			ARKENIAS_CONTRACT,
			ARKENIAS_INSTRUCTIONS,
			ADONIUS_LIST,
			ANDARIEL_SCRIPTURE_COPY,
			STARDUST,
			ISAELS_INSTRUCTIONS,
			ISAELS_LETTER,
			GRAIL_OF_PURITY,
			TEARS_OF_UNICORN,
			WATER_OF_LIFE,
			PURE_MITHRIL_ORE,
			ANT_SOLDIER_ACID,
			WYRMS_TALON1,
			SPIDER_ICHOR,
			HARPYS_DOWN,
			3166,
			3167,
			3168,
			3169,
			3170,
			3171
		};
		
		addStartNpc(CARDIEN);
		addTalkId(ASTERIOS, PUSKIN, THALIA, ADONIUS, ARKENIA, CARDIEN, ISAEL);
		
		addKillId(20145, 20176, 20233, 27077, 20550, 20581, 20582, 20082, 20084, 20086, 20087, 20088);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = event;
		QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("30460-04.htm"))
		{
			st.set("cond", "1");
			st.setState(STATE_STARTED);
			st.playSound(QuestState.SOUND_ACCEPT);
			st.giveItems(CARDIENS_LETTER, 1);
			st.giveItems(7562, 16);
		}
		else if (event.equalsIgnoreCase("30154-07.htm"))
		{
			st.set("cond", "2");
			st.playSound(QuestState.SOUND_MIDDLE);
			st.takeItems(CARDIENS_LETTER, 1);
			st.giveItems(HIERARCHS_LETTER, 1);
			st.giveItems(MOONFLOWER_CHARM, 1);
		}
		else if (event.equalsIgnoreCase("30371-03.htm"))
		{
			st.set("cond", "3");
			st.playSound(QuestState.SOUND_MIDDLE);
			st.takeItems(HIERARCHS_LETTER, 1);
			st.giveItems(GRAIL_DIAGRAM, 1);
		}
		else if (event.equalsIgnoreCase("30371-11.htm"))
		{
			st.takeItems(STARDUST, 1);
			st.playSound(QuestState.SOUND_MIDDLE);
			
			if (player.getLevel() < 38)
			{
				htmltext = "30371-10.htm";
				st.set("cond", "13");
				st.giveItems(THALIAS_INSTRUCTIONS, 1);
			}
			else
			{
				st.set("cond", "14");
				st.giveItems(THALIAS_LETTER2, 1);
			}
		}
		else if (event.equalsIgnoreCase("30300-06.htm"))
		{
			st.set("cond", "4");
			st.playSound(QuestState.SOUND_MIDDLE);
			st.takeItems(GRAIL_DIAGRAM, 1);
			st.giveItems(PUSHKINS_LIST, 1);
		}
		else if (event.equalsIgnoreCase("30300-10.htm"))
		{
			st.set("cond", "6");
			st.playSound(QuestState.SOUND_MIDDLE);
			st.takeItems(PUSHKINS_LIST, 1);
			st.takeItems(ANT_SOLDIER_ACID, -1);
			st.takeItems(PURE_MITHRIL_ORE, -1);
			st.takeItems(WYRMS_TALON1, -1);
			st.giveItems(PURE_MITHRIL_CUP, 1);
		}
		else if (event.equalsIgnoreCase("30419-04.htm"))
		{
			st.set("cond", "8");
			st.playSound(QuestState.SOUND_MIDDLE);
			st.takeItems(THALIAS_LETTER1, 1);
			st.giveItems(ARKENIAS_CONTRACT, 1);
			st.giveItems(ARKENIAS_INSTRUCTIONS, 1);
		}
		else if (event.equalsIgnoreCase("30375-02.htm"))
		{
			st.set("cond", "9");
			st.playSound(QuestState.SOUND_MIDDLE);
			st.takeItems(ARKENIAS_INSTRUCTIONS, 1);
			st.giveItems(ADONIUS_LIST, 1);
		}
		else if (event.equalsIgnoreCase("30655-02.htm"))
		{
			st.set("cond", "15");
			st.playSound(QuestState.SOUND_MIDDLE);
			st.takeItems(THALIAS_LETTER2, 1);
			st.giveItems(ISAELS_INSTRUCTIONS, 1);
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
				if (st.hasQuestItems(MARK_OF_LIFE))
					htmltext = getAlreadyCompletedMsg();
				else if (player.getRace() != Race.Elf)
					htmltext = "30460-01.htm";
				else if (player.getLevel() < 37 || player.getClassId().level() != 1)
					htmltext = "30460-02.htm";
				else
					htmltext = "30460-03.htm";
				break;
			
			case STATE_STARTED:
				int cond = st.getInt("cond");
				switch (npc.getNpcId())
				{
					case ASTERIOS:
						if (cond == 1)
							htmltext = "30154-01.htm";
						else if (cond == 2)
							htmltext = "30154-08.htm";
						else if (cond == 20)
						{
							htmltext = "30154-09.htm";
							st.set("cond", "21");
							st.playSound(QuestState.SOUND_MIDDLE);
							st.takeItems(MOONFLOWER_CHARM, 1);
							st.takeItems(WATER_OF_LIFE, 1);
							st.giveItems(CAMOMILE_CHARM, 1);
						}
						else if (cond == 21)
							htmltext = "30154-10.htm";
						break;
					
					case PUSKIN:
						if (cond == 3)
							htmltext = "30300-01.htm";
						else if (cond == 4)
							htmltext = "30300-07.htm";
						else if (cond == 5)
							htmltext = "30300-08.htm";
						else if (cond == 6)
							htmltext = "30300-11.htm";
						else if (cond > 6)
							htmltext = "30300-12.htm";
						break;
					
					case THALIA:
						if (cond == 2)
							htmltext = "30371-01.htm";
						else if (cond == 3)
							htmltext = "30371-04.htm";
						else if (cond > 3 && cond < 6)
							htmltext = "30371-05.htm";
						else if (cond == 6)
						{
							htmltext = "30371-06.htm";
							st.set("cond", "7");
							st.playSound(QuestState.SOUND_MIDDLE);
							st.takeItems(PURE_MITHRIL_CUP, 1);
							st.giveItems(THALIAS_LETTER1, 1);
						}
						else if (cond == 7)
							htmltext = "30371-07.htm";
						else if (cond > 7 && cond < 12)
							htmltext = "30371-08.htm";
						else if (cond == 12)
							htmltext = "30371-09.htm";
						else if (cond == 13)
						{
							if (player.getLevel() < 38)
								htmltext = "30371-12.htm";
							else
							{
								htmltext = "30371-13.htm";
								st.set("cond", "14");
								st.playSound(QuestState.SOUND_MIDDLE);
								st.takeItems(THALIAS_INSTRUCTIONS, 1);
								st.giveItems(THALIAS_LETTER2, 1);
							}
						}
						else if (cond == 14)
							htmltext = "30371-14.htm";
						else if (cond > 14 && cond < 17)
							htmltext = "30371-15.htm";
						else if (cond == 17)
						{
							htmltext = "30371-16.htm";
							st.set("cond", "18");
							st.playSound(QuestState.SOUND_MIDDLE);
							st.takeItems(ISAELS_LETTER, 1);
							st.giveItems(GRAIL_OF_PURITY, 1);
						}
						else if (cond == 18)
							htmltext = "30371-17.htm";
						else if (cond == 19)
						{
							htmltext = "30371-18.htm";
							st.set("cond", "20");
							st.playSound(QuestState.SOUND_MIDDLE);
							st.takeItems(TEARS_OF_UNICORN, 1);
							st.giveItems(WATER_OF_LIFE, 1);
						}
						else if (cond > 19)
							htmltext = "30371-19.htm";
						break;
					
					case ADONIUS:
						if (cond == 8)
							htmltext = "30375-01.htm";
						else if (cond == 9)
							htmltext = "30375-03.htm";
						else if (cond == 10)
						{
							htmltext = "30375-04.htm";
							st.set("cond", "11");
							st.playSound(QuestState.SOUND_MIDDLE);
							st.takeItems(ADONIUS_LIST, 1);
							st.takeItems(HARPYS_DOWN, -1);
							st.takeItems(SPIDER_ICHOR, -1);
							st.giveItems(ANDARIEL_SCRIPTURE_COPY, 1);
						}
						else if (cond == 11)
							htmltext = "30375-05.htm";
						else if (cond > 11)
							htmltext = "30375-06.htm";
						break;
					
					case ARKENIA:
						if (cond == 7)
							htmltext = "30419-01.htm";
						else if (cond > 7 && cond < 11)
							htmltext = "30419-05.htm";
						else if (cond == 11)
						{
							htmltext = "30419-06.htm";
							st.set("cond", "12");
							st.playSound(QuestState.SOUND_MIDDLE);
							st.takeItems(ANDARIEL_SCRIPTURE_COPY, 1);
							st.takeItems(ARKENIAS_CONTRACT, 1);
							st.giveItems(STARDUST, 1);
						}
						else if (cond == 12)
							htmltext = "30419-07.htm";
						else if (cond > 12)
							htmltext = "30419-08.htm";
						break;
					
					case CARDIEN:
						if (cond == 1)
							htmltext = "30460-05.htm";
						else if (cond > 1 && cond < 21)
							htmltext = "30460-06.htm";
						else if (cond == 21)
						{
							htmltext = "30460-07.htm";
							st.takeItems(CAMOMILE_CHARM, 1);
							st.giveItems(MARK_OF_LIFE, 1);
							st.rewardExpAndSp(104591, 11250);
							st.playSound(QuestState.SOUND_FINISH);
							st.exitQuest(true);
						}
						break;
					
					case ISAEL:
						if (cond == 14)
							htmltext = "30655-01.htm";
						else if (cond == 15)
							htmltext = "30655-03.htm";
						else if (cond == 16)
						{
							if (gotAllSpearPieces(st))
							{
								htmltext = "30655-04.htm";
								st.set("cond", "17");
								st.playSound(QuestState.SOUND_MIDDLE);
								
								for (int itemId : TALINS_PIECES)
									st.takeItems(itemId, 1);
								
								st.takeItems(ISAELS_INSTRUCTIONS, 1);
								st.giveItems(ISAELS_LETTER, 1);
								st.giveItems(TALINS_SPEAR, 1);
							}
							else
								htmltext = "30655-03.htm";
						}
						else if (cond == 17)
							htmltext = "30655-05.htm";
						else if (cond > 17)
							htmltext = "30655-06.htm";
						break;
				
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
			case 20550:
				if (st.getInt("cond") == 4)
					if (st.dropItems(PURE_MITHRIL_ORE, 1, 10, 500000))
						if (st.getQuestItemsCount(WYRMS_TALON1) >= 20 && st.getQuestItemsCount(ANT_SOLDIER_ACID) >= 20)
							st.set("cond", "5");
				break;
			
			case 20176:
				if (st.getInt("cond") == 4)
					if (st.dropItems(WYRMS_TALON1, 1, 20, 500000))
						if (st.getQuestItemsCount(PURE_MITHRIL_ORE) >= 10 && st.getQuestItemsCount(ANT_SOLDIER_ACID) >= 20)
							st.set("cond", "5");
				break;
			
			case 20082:
			case 20084:
			case 20086:
			case 20087:
			case 20088:
				if (st.getInt("cond") == 4)
					if (st.dropItems(ANT_SOLDIER_ACID, 1, 20, 800000))
						if (st.getQuestItemsCount(PURE_MITHRIL_ORE) >= 10 && st.getQuestItemsCount(WYRMS_TALON1) >= 20)
							st.set("cond", "5");
				break;
			
			case 20233:
				if (st.getInt("cond") == 9)
					if (st.dropItems(SPIDER_ICHOR, 1, 20, 500000))
						if (st.getQuestItemsCount(HARPYS_DOWN) >= 20)
							st.set("cond", "10");
				break;
			
			case 20145:
				if (st.getInt("cond") == 9)
					if (st.dropItems(HARPYS_DOWN, 1, 20, 500000))
						if (st.getQuestItemsCount(SPIDER_ICHOR) >= 20)
							st.set("cond", "10");
				break;
			
			case 27077:
				if (st.getInt("cond") == 18 && st.getItemEquipped(Inventory.PAPERDOLL_RHAND) == TALINS_SPEAR)
				{
					st.set("cond", "19");
					st.playSound(QuestState.SOUND_MIDDLE);
					st.takeItems(GRAIL_OF_PURITY, 1);
					st.takeItems(TALINS_SPEAR, 1);
					st.giveItems(TEARS_OF_UNICORN, 1);
				}
				break;
			
			case 20581:
			case 20582:
				if (st.getInt("cond") == 15 && Rnd.get(100) < 50)
				{
					final int randomItemId = Rnd.get(3166, 3171);
					if (!st.hasQuestItems(randomItemId))
					{
						st.giveItems(randomItemId, 1);
						if (gotAllSpearPieces(st))
						{
							st.set("cond", "16");
							st.playSound(QuestState.SOUND_MIDDLE);
						}
						else
							st.playSound(QuestState.SOUND_ITEMGET);
					}
				}
				break;
		}
		
		return null;
	}
	
	private static boolean gotAllSpearPieces(QuestState st)
	{
		for (int itemId : TALINS_PIECES)
		{
			if (!st.hasQuestItems(itemId))
				return false;
		}
		return true;
	}
	
	public static void main(String[] args)
	{
		new Q218_TestimonyOfLife(218, qn, "Testimony of Life");
	}
}