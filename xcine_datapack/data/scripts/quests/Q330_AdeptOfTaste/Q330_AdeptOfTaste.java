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
package quests.Q330_AdeptOfTaste;

import net.xcine.gameserver.model.actor.L2Npc;
import net.xcine.gameserver.model.actor.instance.L2PcInstance;
import net.xcine.gameserver.model.quest.Quest;
import net.xcine.gameserver.model.quest.QuestState;
import net.xcine.util.Rnd;

public class Q330_AdeptOfTaste extends Quest
{
	private static final String qn = "Q330_AdeptOfTaste";
	
	// Items
	private static final int INGREDIENT_LIST = 1420;
	private static final int SONIAS_BOTANYBOOK = 1421;
	private static final int RED_MANDRAGORA_ROOT = 1422;
	private static final int WHITE_MANDRAGORA_ROOT = 1423;
	private static final int RED_MANDRAGORA_SAP = 1424;
	private static final int WHITE_MANDRAGORA_SAP = 1425;
	private static final int JAYCUBS_INSECTBOOK = 1426;
	private static final int NECTAR = 1427;
	private static final int ROYAL_JELLY = 1428;
	private static final int HONEY = 1429;
	private static final int GOLDEN_HONEY = 1430;
	private static final int PANOS_CONTRACT = 1431;
	private static final int HOBGOBLIN_AMULET = 1432;
	private static final int DIONIAN_POTATO = 1433;
	private static final int GLYVKAS_BOTANYBOOK = 1434;
	private static final int GREEN_MARSH_MOSS = 1435;
	private static final int BROWN_MARSH_MOSS = 1436;
	private static final int GREEN_MOSS_BUNDLE = 1437;
	private static final int BROWN_MOSS_BUNDLE = 1438;
	private static final int ROLANTS_CREATUREBOOK = 1439;
	private static final int MONSTER_EYE_BODY = 1440;
	private static final int MONSTER_EYE_MEAT = 1441;
	private static final int JONAS_STEAK_DISH1 = 1442;
	private static final int JONAS_STEAK_DISH2 = 1443;
	private static final int JONAS_STEAK_DISH3 = 1444;
	private static final int JONAS_STEAK_DISH4 = 1445;
	private static final int JONAS_STEAK_DISH5 = 1446;
	private static final int MIRIENS_REVIEW1 = 1447;
	private static final int MIRIENS_REVIEW2 = 1448;
	private static final int MIRIENS_REVIEW3 = 1449;
	private static final int MIRIENS_REVIEW4 = 1450;
	private static final int MIRIENS_REVIEW5 = 1451;
	
	// Rewards
	private static final int ADENA = 57;
	private static final int JONAS_SALAD_RECIPE = 1455;
	private static final int JONAS_SAUCE_RECIPE = 1456;
	private static final int JONAS_STEAK_RECIPE = 1457;
	
	public Q330_AdeptOfTaste(int questId, String name, String descr)
	{
		super(questId, name, descr);
		
		questItemIds = new int[]
		{
			INGREDIENT_LIST,
			RED_MANDRAGORA_SAP,
			WHITE_MANDRAGORA_SAP,
			HONEY,
			GOLDEN_HONEY,
			DIONIAN_POTATO,
			GREEN_MOSS_BUNDLE,
			BROWN_MOSS_BUNDLE,
			MONSTER_EYE_MEAT,
			MIRIENS_REVIEW1,
			MIRIENS_REVIEW2,
			MIRIENS_REVIEW3,
			MIRIENS_REVIEW4,
			MIRIENS_REVIEW5,
			JONAS_STEAK_DISH1,
			JONAS_STEAK_DISH2,
			JONAS_STEAK_DISH3,
			JONAS_STEAK_DISH4,
			JONAS_STEAK_DISH5,
			SONIAS_BOTANYBOOK,
			RED_MANDRAGORA_ROOT,
			WHITE_MANDRAGORA_ROOT,
			JAYCUBS_INSECTBOOK,
			NECTAR,
			ROYAL_JELLY,
			PANOS_CONTRACT,
			HOBGOBLIN_AMULET,
			GLYVKAS_BOTANYBOOK,
			GREEN_MARSH_MOSS,
			BROWN_MARSH_MOSS,
			ROLANTS_CREATUREBOOK,
			MONSTER_EYE_BODY
		};
		
		addStartNpc(30469); // Jonas
		addTalkId(30469, 30062, 30067, 30069, 30073, 30078, 30461);
		
		addKillId(20147, 20154, 20155, 20156, 20204, 20223, 20226, 20228, 20229, 20265, 20266);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = event;
		QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("30469-03.htm"))
		{
			st.set("cond", "1");
			st.setState(STATE_STARTED);
			st.playSound(QuestState.SOUND_ACCEPT);
			st.giveItems(INGREDIENT_LIST, 1);
		}
		else if (event.equalsIgnoreCase("30062-05.htm"))
		{
			st.takeItems(SONIAS_BOTANYBOOK, 1);
			st.takeItems(RED_MANDRAGORA_ROOT, -1);
			st.takeItems(WHITE_MANDRAGORA_ROOT, -1);
			st.giveItems(RED_MANDRAGORA_SAP, 1);
			st.playSound(QuestState.SOUND_ITEMGET);
		}
		else if (event.equalsIgnoreCase("30073-05.htm"))
		{
			st.takeItems(JAYCUBS_INSECTBOOK, 1);
			st.takeItems(NECTAR, -1);
			st.takeItems(ROYAL_JELLY, -1);
			st.giveItems(HONEY, 1);
			st.playSound(QuestState.SOUND_ITEMGET);
		}
		else if (event.equalsIgnoreCase("30067-05.htm"))
		{
			st.takeItems(GLYVKAS_BOTANYBOOK, 1);
			st.takeItems(GREEN_MARSH_MOSS, -1);
			st.takeItems(BROWN_MARSH_MOSS, -1);
			st.giveItems(GREEN_MOSS_BUNDLE, 1);
			st.playSound(QuestState.SOUND_ITEMGET);
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
				if (player.getLevel() < 24)
				{
					htmltext = "30469-01.htm";
					st.exitQuest(true);
				}
				else
					htmltext = "30469-02.htm";
				break;
			
			case STATE_STARTED:
				switch (npc.getNpcId())
				{
					case 30469:
						if (st.getQuestItemsCount(INGREDIENT_LIST) > 0)
						{
							if (ingredients_count(st) < 5)
								htmltext = "30469-04.htm";
							else if (ingredients_count(st) >= 5)
							{
								switch (special_ingredients(st))
								{
									case 0:
										if (Rnd.get(10) < 1)
										{
											htmltext = "30469-05t2.htm";
											st.giveItems(JONAS_STEAK_DISH2, 1);
											
										}
										else
										{
											htmltext = "30469-05t1.htm";
											st.giveItems(JONAS_STEAK_DISH1, 1);
										}
										break;
									
									case 1:
										if (Rnd.get(10) < 1)
										{
											htmltext = "30469-05t3.htm";
											st.giveItems(JONAS_STEAK_DISH3, 1);
										}
										else
										{
											htmltext = "30469-05t2.htm";
											st.giveItems(JONAS_STEAK_DISH2, 1);
										}
										break;
									
									case 2:
										if (Rnd.get(10) < 1)
										{
											htmltext = "30469-05t4.htm";
											st.giveItems(JONAS_STEAK_DISH4, 1);
										}
										else
										{
											htmltext = "30469-05t3.htm";
											st.giveItems(JONAS_STEAK_DISH3, 1);
										}
										break;
									
									case 3:
										if (Rnd.get(10) < 1)
										{
											htmltext = "30469-05t5.htm";
											st.giveItems(JONAS_STEAK_DISH5, 1);
											st.playSound(QuestState.SOUND_JACKPOT);
										}
										else
										{
											htmltext = "30469-05t4.htm";
											st.giveItems(JONAS_STEAK_DISH4, 1);
										}
										break;
								}
								st.takeItems(INGREDIENT_LIST, 1);
								st.takeItems(RED_MANDRAGORA_SAP, 1);
								st.takeItems(WHITE_MANDRAGORA_SAP, 1);
								st.takeItems(HONEY, 1);
								st.takeItems(GOLDEN_HONEY, 1);
								st.takeItems(DIONIAN_POTATO, 1);
								st.takeItems(GREEN_MOSS_BUNDLE, 1);
								st.takeItems(BROWN_MOSS_BUNDLE, 1);
								st.takeItems(MONSTER_EYE_MEAT, 1);
								st.playSound(QuestState.SOUND_ITEMGET);
							}
						}
						else if (st.getQuestItemsCount(INGREDIENT_LIST) == 0 && ingredients_count(st) == 0)
						{
							if (has_dish(st) > 0 && has_review(st) == 0)
								htmltext = "30469-06.htm";
							else if (has_dish(st) == 0 && has_review(st) > 0)
							{
								if (st.hasQuestItems(MIRIENS_REVIEW1))
								{
									htmltext = "30469-06t1.htm";
									st.takeItems(MIRIENS_REVIEW1, 1);
									st.rewardItems(ADENA, 7500);
									st.rewardExpAndSp(6000, 0);
								}
								else if (st.hasQuestItems(MIRIENS_REVIEW2))
								{
									htmltext = "30469-06t2.htm";
									st.takeItems(MIRIENS_REVIEW2, 1);
									st.rewardItems(ADENA, 9000);
									st.rewardExpAndSp(7000, 0);
								}
								else if (st.hasQuestItems(MIRIENS_REVIEW3))
								{
									htmltext = "30469-06t3.htm";
									st.takeItems(MIRIENS_REVIEW3, 1);
									st.rewardItems(ADENA, 5800);
									st.giveItems(JONAS_SALAD_RECIPE, 1);
									st.rewardExpAndSp(9000, 0);
								}
								else if (st.hasQuestItems(MIRIENS_REVIEW4))
								{
									htmltext = "30469-06t4.htm";
									st.takeItems(MIRIENS_REVIEW4, 1);
									st.rewardItems(ADENA, 6800);
									st.giveItems(JONAS_SAUCE_RECIPE, 1);
									st.rewardExpAndSp(10500, 0);
								}
								else if (st.hasQuestItems(MIRIENS_REVIEW5))
								{
									htmltext = "30469-06t5.htm";
									st.takeItems(MIRIENS_REVIEW5, 1);
									st.rewardItems(ADENA, 7800);
									st.giveItems(JONAS_STEAK_RECIPE, 1);
									st.rewardExpAndSp(12000, 0);
								}
								st.playSound(QuestState.SOUND_FINISH);
								st.exitQuest(true);
							}
						}
						break;
					
					case 30461:
						if (st.getQuestItemsCount(INGREDIENT_LIST) > 0)
							htmltext = "30461-01.htm";
						else if (st.getQuestItemsCount(INGREDIENT_LIST) == 0 && ingredients_count(st) == 0)
						{
							if (has_dish(st) > 0 && has_review(st) == 0)
							{
								if (st.hasQuestItems(JONAS_STEAK_DISH1))
								{
									htmltext = "30461-02t1.htm";
									st.takeItems(JONAS_STEAK_DISH1, 1);
									st.giveItems(MIRIENS_REVIEW1, 1);
									st.playSound(QuestState.SOUND_ITEMGET);
								}
								else if (st.hasQuestItems(JONAS_STEAK_DISH2))
								{
									htmltext = "30461-02t2.htm";
									st.takeItems(JONAS_STEAK_DISH2, 1);
									st.giveItems(MIRIENS_REVIEW2, 1);
									st.playSound(QuestState.SOUND_ITEMGET);
								}
								else if (st.hasQuestItems(JONAS_STEAK_DISH3))
								{
									htmltext = "30461-02t3.htm";
									st.takeItems(JONAS_STEAK_DISH3, 1);
									st.giveItems(MIRIENS_REVIEW3, 1);
									st.playSound(QuestState.SOUND_ITEMGET);
								}
								else if (st.hasQuestItems(JONAS_STEAK_DISH4))
								{
									htmltext = "30461-02t4.htm";
									st.takeItems(JONAS_STEAK_DISH4, 1);
									st.giveItems(MIRIENS_REVIEW4, 1);
									st.playSound(QuestState.SOUND_ITEMGET);
								}
								else if (st.hasQuestItems(JONAS_STEAK_DISH5))
								{
									htmltext = "30461-02t5.htm";
									st.takeItems(JONAS_STEAK_DISH5, 1);
									st.giveItems(MIRIENS_REVIEW5, 1);
									st.playSound(QuestState.SOUND_ITEMGET);
								}
							}
							else if (has_dish(st) == 0 && has_review(st) > 0)
								htmltext = "30461-04.htm";
						}
						break;
					
					case 30062:
						if (ingredients_count(st) < 5)
						{
							if (!st.hasQuestItems(SONIAS_BOTANYBOOK))
							{
								if (st.hasQuestItems(RED_MANDRAGORA_SAP) || st.hasQuestItems(WHITE_MANDRAGORA_SAP))
									htmltext = "30062-07.htm";
								else
								{
									htmltext = "30062-01.htm";
									st.giveItems(SONIAS_BOTANYBOOK, 1);
									st.playSound(QuestState.SOUND_ITEMGET);
								}
							}
							else
							{
								if (st.getQuestItemsCount(RED_MANDRAGORA_ROOT) < 40 || st.getQuestItemsCount(WHITE_MANDRAGORA_ROOT) < 40)
									htmltext = "30062-02.htm";
								else if (st.getQuestItemsCount(WHITE_MANDRAGORA_ROOT) >= 40)
								{
									htmltext = "30062-06.htm";
									st.takeItems(SONIAS_BOTANYBOOK, 1);
									st.takeItems(RED_MANDRAGORA_ROOT, -1);
									st.takeItems(WHITE_MANDRAGORA_ROOT, -1);
									st.giveItems(WHITE_MANDRAGORA_SAP, 1);
									st.playSound(QuestState.SOUND_ITEMGET);
								}
								else
									htmltext = "30062-03.htm";
							}
						}
						else
							htmltext = "30062-07.htm";
						break;
					
					case 30073:
						if (ingredients_count(st) < 5)
						{
							if (!st.hasQuestItems(JAYCUBS_INSECTBOOK))
							{
								if (st.hasQuestItems(HONEY) || st.hasQuestItems(GOLDEN_HONEY))
									htmltext = "30073-07.htm";
								else
								{
									htmltext = "30073-01.htm";
									st.giveItems(JAYCUBS_INSECTBOOK, 1);
									st.playSound(QuestState.SOUND_ITEMGET);
								}
							}
							else
							{
								if (st.getQuestItemsCount(NECTAR) < 20)
									htmltext = "30073-02.htm";
								else
								{
									if (st.getQuestItemsCount(ROYAL_JELLY) < 10)
										htmltext = "30073-03.htm";
									else
									{
										htmltext = "30073-06.htm";
										st.takeItems(JAYCUBS_INSECTBOOK, 1);
										st.takeItems(NECTAR, -1);
										st.takeItems(ROYAL_JELLY, -1);
										st.giveItems(GOLDEN_HONEY, 1);
										st.playSound(QuestState.SOUND_ITEMGET);
									}
								}
							}
						}
						else
							htmltext = "30073-07.htm";
						break;
					
					case 30078:
						if (ingredients_count(st) < 5)
						{
							if (!st.hasQuestItems(PANOS_CONTRACT))
							{
								if (!st.hasQuestItems(DIONIAN_POTATO))
								{
									htmltext = "30078-01.htm";
									st.giveItems(PANOS_CONTRACT, 1);
									st.playSound(QuestState.SOUND_ITEMGET);
								}
								else
									htmltext = "30078-04.htm";
							}
							else
							{
								if (st.getQuestItemsCount(HOBGOBLIN_AMULET) < 30)
									htmltext = "30078-02.htm";
								else
								{
									htmltext = "30078-03.htm";
									st.takeItems(PANOS_CONTRACT, 1);
									st.takeItems(HOBGOBLIN_AMULET, -1);
									st.giveItems(DIONIAN_POTATO, 1);
									st.playSound(QuestState.SOUND_ITEMGET);
								}
							}
						}
						else
							htmltext = "30078-04.htm";
						break;
					
					case 30067:
						if (ingredients_count(st) < 5)
						{
							if (!st.hasQuestItems(GLYVKAS_BOTANYBOOK))
							{
								if (st.hasQuestItems(GREEN_MOSS_BUNDLE) || st.hasQuestItems(BROWN_MOSS_BUNDLE))
									htmltext = "30067-07.htm";
								else
								{
									st.giveItems(GLYVKAS_BOTANYBOOK, 1);
									htmltext = "30067-01.htm";
									st.playSound(QuestState.SOUND_ITEMGET);
								}
							}
							else
							{
								if (st.getQuestItemsCount(GREEN_MARSH_MOSS) < 20 || st.getQuestItemsCount(BROWN_MARSH_MOSS) < 20)
									htmltext = "30067-02.htm";
								else if (st.getQuestItemsCount(BROWN_MARSH_MOSS) >= 20)
								{
									htmltext = "30067-06.htm";
									st.takeItems(GLYVKAS_BOTANYBOOK, 1);
									st.takeItems(GREEN_MARSH_MOSS, -1);
									st.takeItems(BROWN_MARSH_MOSS, -1);
									st.giveItems(BROWN_MOSS_BUNDLE, 1);
									st.playSound(QuestState.SOUND_ITEMGET);
								}
								else
									htmltext = "30067-03.htm";
							}
						}
						else
							htmltext = "30067-07.htm";
						break;
					
					case 30069:
						if (ingredients_count(st) < 5)
						{
							if (!st.hasQuestItems(ROLANTS_CREATUREBOOK))
							{
								if (!st.hasQuestItems(MONSTER_EYE_MEAT))
								{
									htmltext = "30069-01.htm";
									st.giveItems(ROLANTS_CREATUREBOOK, 1);
									st.playSound(QuestState.SOUND_ITEMGET);
								}
								else
									htmltext = "30069-04.htm";
							}
							else
							{
								if (st.getQuestItemsCount(MONSTER_EYE_BODY) < 30)
									htmltext = "30069-02.htm";
								else
								{
									htmltext = "30069-03.htm";
									st.takeItems(ROLANTS_CREATUREBOOK, 1);
									st.takeItems(MONSTER_EYE_BODY, -1);
									st.giveItems(MONSTER_EYE_MEAT, 1);
									st.playSound(QuestState.SOUND_ITEMGET);
								}
							}
						}
						else
							htmltext = "30069-04.htm";
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
			case 20265:
			case 20266:
				if (st.hasQuestItems(ROLANTS_CREATUREBOOK))
					st.dropItemsAlways(MONSTER_EYE_BODY, Rnd.get(2, 3), 30);
				break;
			
			case 20226:
			case 20228:
				if (st.hasQuestItems(GLYVKAS_BOTANYBOOK))
					st.dropItemsAlways(((Rnd.get(10) < 9) ? GREEN_MARSH_MOSS : BROWN_MARSH_MOSS), 1, 20);
				break;
			
			case 20147:
				if (st.hasQuestItems(PANOS_CONTRACT))
					st.dropItemsAlways(HOBGOBLIN_AMULET, 1, 30);
				break;
			
			case 20204:
			case 20229:
				if (st.hasQuestItems(JAYCUBS_INSECTBOOK))
				{
					if (!st.dropItems(ROYAL_JELLY, 1, 10, 50000))
						st.dropItemsAlways(NECTAR, 1, 20);
				}
				break;
			
			case 20223:
			case 20154:
			case 20155:
			case 20156:
				if (st.hasQuestItems(SONIAS_BOTANYBOOK))
					st.dropItemsAlways(((Rnd.get(1000) < 975) ? RED_MANDRAGORA_ROOT : WHITE_MANDRAGORA_ROOT), 1, 40);
				break;
		}
		
		return null;
	}
	
	private static int has_review(QuestState st)
	{
		return st.getQuestItemsCount(MIRIENS_REVIEW1) + st.getQuestItemsCount(MIRIENS_REVIEW2) + st.getQuestItemsCount(MIRIENS_REVIEW3) + st.getQuestItemsCount(MIRIENS_REVIEW4) + st.getQuestItemsCount(MIRIENS_REVIEW5);
	}
	
	private static int has_dish(QuestState st)
	{
		return st.getQuestItemsCount(JONAS_STEAK_DISH1) + st.getQuestItemsCount(JONAS_STEAK_DISH2) + st.getQuestItemsCount(JONAS_STEAK_DISH3) + st.getQuestItemsCount(JONAS_STEAK_DISH4) + st.getQuestItemsCount(JONAS_STEAK_DISH5);
	}
	
	private static int special_ingredients(QuestState st)
	{
		return st.getQuestItemsCount(WHITE_MANDRAGORA_SAP) + st.getQuestItemsCount(GOLDEN_HONEY) + st.getQuestItemsCount(BROWN_MOSS_BUNDLE);
	}
	
	private static int ingredients_count(QuestState st)
	{
		return st.getQuestItemsCount(RED_MANDRAGORA_SAP) + st.getQuestItemsCount(HONEY) + st.getQuestItemsCount(DIONIAN_POTATO) + st.getQuestItemsCount(GREEN_MOSS_BUNDLE) + st.getQuestItemsCount(MONSTER_EYE_MEAT) + special_ingredients(st);
	}
	
	public static void main(String[] args)
	{
		new Q330_AdeptOfTaste(330, qn, "Adept of Taste");
	}
}