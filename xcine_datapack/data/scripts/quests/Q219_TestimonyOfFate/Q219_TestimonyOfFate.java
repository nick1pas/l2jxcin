/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */
package quests.Q219_TestimonyOfFate;

import net.xcine.gameserver.model.actor.L2Npc;
import net.xcine.gameserver.model.actor.instance.L2PcInstance;
import net.xcine.gameserver.model.quest.Quest;
import net.xcine.gameserver.model.quest.QuestState;
import net.xcine.util.Rnd;


public class Q219_TestimonyOfFate extends Quest
{
	public final static String qn = "Q219_TestimonyOfFate";

	// Items
	private final int MARK_OF_FATE_ID = 3172;
	private final int KAIRAS_LETTER1_ID = 3173;
	private final int METHEUS_FUNERAL_JAR_ID = 3174;
	private final int KASANDRAS_REMAINS_ID = 3175;
	private final int HERBALISM_TEXTBOOK_ID = 3176;
	private final int IXIAS_LIST_ID = 3177;
	private final int MEDUSA_ICHOR_ID = 3178;
	private final int M_SPIDER_FLUIDS_ID = 3179;
	private final int DEAD_SEEKER_DUNG_ID = 3180;
	private final int TYRANTS_BLOOD_ID = 3181;
	private final int NIGHTSHADE_ROOT_ID = 3182;
	private final int BELLADONNA_ID = 3183;
	private final int ALDERS_SKULL1_ID = 3184;
	private final int ALDERS_SKULL2_ID = 3185;
	private final int ALDERS_RECEIPT_ID = 3186;
	private final int KAIRAS_RECOMMEND_ID = 3189;
	private final int KAIRAS_INSTRUCTIONS_ID = 3188;
	private final int REVELATIONS_MANUSCRIPT_ID = 3187;
	private final int THIFIELS_LETTER_ID = 3191;
	private final int PALUS_CHARM_ID = 3190;
	private final int ARKENIAS_LETTER_ID = 1246;
	private final int ARKENIAS_NOTE_ID = 3192;
	private final int RED_FAIRY_DUST_ID = 3198;
	private final int TIMIRIRAN_SAP_ID = 3201;
	private final int PIXY_GARNET_ID = 3193;
	private final int GRANDIS_SKULL_ID = 3194;
	private final int KARUL_BUGBEAR_SKULL_ID = 3195;
	private final int BREKA_OVERLORD_SKULL_ID = 3196;
	private final int LETO_OVERLORD_SKULL_ID = 3197;
	private final int BLACK_WILLOW_LEAF_ID = 3200;
	private final int TIMIRIRAN_SEED_ID = 3199;
	
	// Npcs
	private final int KAIRA = 30476;
	private final int BLOODY_PIXY = 31845;
	private final int BLIGHT_TREANT = 31850;
	private final int ROA = 30114;
	private final int NORMAN = 30210;
	private final int THIFIELL = 30358;
	private final int ARKENIA = 30419;
	private final int IXIA = 30463;
	private final int ALDERS_SPIRIT = 30613;
	private final int METHEUS = 30614;

	// Mobs
	private final int HANGMAN_TREE = 20144;
	private final int MARSH_STAKATO = 20157;
	private final int MEDUSA = 20158;
	private final int TYRANT = 20192;
	private final int TYRANT_KINGPIN = 20193;
	private final int DEAD_SEEKER = 20202;
	private final int MARSH_STAKATO_WORKER = 20230;
	private final int MARSH_STAKATO_SOLDIER = 20232;
	private final int MARSH_SPIDER = 20233;
	private final int MARSH_STAKATO_DRONE = 20234;
	private final int BREKA_ORC_OVERLORD = 20270;
	private final int BLACK_WILLOW_LURKER = 27079;
	private final int GRANDIS = 20554;
	private final int LETO_LIZARDMAN_OVERLORD = 20582;
	private final int KARUL_BUGBEAR = 20600;

	public Q219_TestimonyOfFate(int questId, String name, String descr)
	{
		super(questId, name, descr);

		questItemIds = new int[] 
		{ 
			ALDERS_SKULL1_ID, 
			KAIRAS_INSTRUCTIONS_ID, 
			REVELATIONS_MANUSCRIPT_ID, 
			KAIRAS_LETTER1_ID, 
			KASANDRAS_REMAINS_ID, 
			DEAD_SEEKER_DUNG_ID, 
			NIGHTSHADE_ROOT_ID, 
			ALDERS_SKULL2_ID, 
			ALDERS_RECEIPT_ID, 
			KAIRAS_RECOMMEND_ID, 
			ARKENIAS_LETTER_ID, 
			PALUS_CHARM_ID,
			THIFIELS_LETTER_ID, 
			ARKENIAS_NOTE_ID,
			RED_FAIRY_DUST_ID,
			TIMIRIRAN_SAP_ID,
			PIXY_GARNET_ID,
			GRANDIS_SKULL_ID,
			KARUL_BUGBEAR_SKULL_ID, 
			BREKA_OVERLORD_SKULL_ID, 
			LETO_OVERLORD_SKULL_ID, 
			BLACK_WILLOW_LEAF_ID, 
			TIMIRIRAN_SEED_ID, 
			METHEUS_FUNERAL_JAR_ID
		};

		addStartNpc(KAIRA);

		addTalkId(KAIRA, BLOODY_PIXY,BLIGHT_TREANT, ROA, NORMAN, THIFIELL, ARKENIA, IXIA, ALDERS_SPIRIT, METHEUS);
		addKillId(HANGMAN_TREE, MARSH_STAKATO, MEDUSA, TYRANT, TYRANT_KINGPIN, DEAD_SEEKER, MARSH_STAKATO_WORKER, MARSH_STAKATO_SOLDIER, MARSH_SPIDER, MARSH_STAKATO_DRONE, BREKA_ORC_OVERLORD, BLACK_WILLOW_LURKER, GRANDIS, LETO_LIZARDMAN_OVERLORD, KARUL_BUGBEAR);
	}

	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmlText = event;
		QuestState qs = player.getQuestState(qn);
		if(qs == null)
			return null;
		
		if(event.equals("30476-05.htm"))
		{
			qs.set("cond", "1");
			qs.setState(STATE_STARTED);
			qs.playSound(QuestState.SOUND_ACCEPT);
			qs.giveItems(KAIRAS_LETTER1_ID, 1);
		}
		else if(event.equals("30476_2"))
		{
			if(qs.getPlayer().getLevel() >= 37)
			{
				qs.set("cond", "15");
				htmlText = "30476-12.htm";
			}
			else
			{
				qs.set("cond", "14");
				htmlText = "30476-13.htm";
			}

			qs.giveItems(KAIRAS_RECOMMEND_ID, 1);
			qs.takeItems(REVELATIONS_MANUSCRIPT_ID, 1);
		}
		else if(event.equals("30114-04.htm"))
		{
			qs.set("cond", "12");
			qs.giveItems(ALDERS_RECEIPT_ID, 1);
			qs.takeItems(ALDERS_SKULL2_ID, 1);
		}
		else if(event.equals("30419_1"))
		{
			htmlText = "30419-02.htm";
			qs.set("cond", "17");
			qs.giveItems(ARKENIAS_NOTE_ID, 1);
			qs.takeItems(THIFIELS_LETTER_ID, 1);
		}
		else if(event.equals("30419_2"))
		{
			htmlText = "30419-05.htm";
			qs.set("cond", "18");
			qs.giveItems(ARKENIAS_LETTER_ID, 1);
			qs.takeItems(ARKENIAS_NOTE_ID, 1);
			qs.takeItems(RED_FAIRY_DUST_ID, 1);
			qs.takeItems(TIMIRIRAN_SAP_ID, 1);
		}
		else if(event.equals("31845_1"))
		{
			htmlText = "31845-02.htm";
			qs.giveItems(PIXY_GARNET_ID, 1);
		}
		else if(event.equals("31850_1"))
		{
			htmlText = "31850-02.htm";
			qs.giveItems(TIMIRIRAN_SEED_ID, 1);
		}
		return htmlText;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		String htmlText = getNoQuestMsg();
		QuestState qs = player.getQuestState(qn);
		if(qs == null)
			return htmlText;
		
		switch(qs.getState())
		{
			case STATE_CREATED:
				if(player.getRace().ordinal() == 2 && player.getLevel() >= 37)
					htmlText = "30476-03.htm";
				else if(player.getRace().ordinal() == 2)
				{
					htmlText = "30476-02.htm";
					qs.exitQuest(true);
				}
				else
				{
					htmlText = "30476-01.htm";
					qs.exitQuest(true);
				}
				break;
			case STATE_STARTED:
				int cond = qs.getInt("cond");
				switch(npc.getNpcId())
				{
					case KAIRA:
						if(cond == 1)
							htmlText = "30476-06.htm";
						else if(cond == 3)
							htmlText = "30476-07.htm";
						else if(cond == 4)
							htmlText = "30476-08.htm";
						else if(cond == 9)
						{
							htmlText = "30476-09.htm";
							qs.set("cond", "10");
							qs.giveItems(ALDERS_SKULL2_ID, 1);
							qs.takeItems(ALDERS_SKULL1_ID, 1);
							addSpawn(ALDERS_SPIRIT, 78977, 149036, -3597, 0 , false, 300000, false);
						}
						else if(cond == 10)
							htmlText = "30476-10.htm";
						else if(cond == 13)
							htmlText = "30476-11.htm";
						else if(cond == 14)
							htmlText = "30476-14.htm";
						else if(cond == 15)
						{
							htmlText = "30476-15.htm";
							qs.set("cond", "15");
							qs.giveItems(KAIRAS_RECOMMEND_ID, 1);
							qs.takeItems(KAIRAS_INSTRUCTIONS_ID, 1);
						}
						else if(cond == 15)
						{
							if(qs.getQuestItemsCount(PALUS_CHARM_ID) > 0)
								htmlText = "30476-17.htm";
							else
								htmlText = "30476-16.htm";
						}
						break;
					case BLOODY_PIXY:
						if(cond == 17)
						{
							if(qs.getQuestItemsCount(PIXY_GARNET_ID) == 0)
								htmlText = "31845-01.htm";
							else
							{
								if(qs.getQuestItemsCount(GRANDIS_SKULL_ID) >= 10 && qs.getQuestItemsCount(KARUL_BUGBEAR_SKULL_ID) >= 10 && qs.getQuestItemsCount(BREKA_OVERLORD_SKULL_ID) >= 10 && qs.getQuestItemsCount(LETO_OVERLORD_SKULL_ID) >= 10)
								{
									htmlText = "31845-04.htm";
									qs.giveItems(RED_FAIRY_DUST_ID, 1);
									qs.takeItems(PIXY_GARNET_ID, 1);
									qs.takeItems(GRANDIS_SKULL_ID, qs.getQuestItemsCount(GRANDIS_SKULL_ID));
									qs.takeItems(KARUL_BUGBEAR_SKULL_ID, qs.getQuestItemsCount(KARUL_BUGBEAR_SKULL_ID));
									qs.takeItems(BREKA_OVERLORD_SKULL_ID, qs.getQuestItemsCount(BREKA_OVERLORD_SKULL_ID));
									qs.takeItems(LETO_OVERLORD_SKULL_ID, qs.getQuestItemsCount(LETO_OVERLORD_SKULL_ID));
								}
								else if(qs.getQuestItemsCount(RED_FAIRY_DUST_ID) > 0)
									htmlText = "31845-05.htm";
								else
									htmlText = "31845-03.htm";
							}
						}
						break;
					case BLIGHT_TREANT:
						if(cond == 17)
						{
							if(qs.getQuestItemsCount(TIMIRIRAN_SEED_ID) == 0)
								htmlText = "31850-01.htm";
							else
							{
								if(qs.getQuestItemsCount(BLACK_WILLOW_LEAF_ID) > 0)
								{
									htmlText = "31850-04.htm";
									qs.giveItems(TIMIRIRAN_SAP_ID, 1);
									qs.takeItems(BLACK_WILLOW_LEAF_ID, 1);
									qs.takeItems(TIMIRIRAN_SEED_ID, 1);
								}
								else if(qs.getQuestItemsCount(TIMIRIRAN_SAP_ID) > 0)
									htmlText = "31850-05.htm";
								else
									htmlText = "31850-03.htm";
							}
						}
						break;
					case ROA:
						if(cond == 11)
							htmlText = "30114-01.htm";
						else if(cond == 12)
							htmlText = "30114-05.htm";
						else if(cond > 12)
							htmlText = "30114-06.htm";
						break;
					case NORMAN:
						if(cond == 12)
						{
							htmlText = "30210-01.htm";
							qs.set("cond", "13");
							qs.giveItems(REVELATIONS_MANUSCRIPT_ID, 1);
							qs.takeItems(ALDERS_RECEIPT_ID, 1);
						}
						else if(cond == 13)
							htmlText = "30210-02.htm";
						break;
					case THIFIELL:
						if(cond == 15)
						{
							htmlText = "30358-01.htm";
							qs.set("cond", "16");
							qs.giveItems(THIFIELS_LETTER_ID, 1);
							qs.giveItems(PALUS_CHARM_ID, 1);
							qs.takeItems(KAIRAS_RECOMMEND_ID, 1);
						}
						else if(cond == 16)
							htmlText = "30358-02.htm";
						else if(cond == 17)
							htmlText = "30358-03.htm";
						else if(cond == 18)
						{
							htmlText = "30358-04.htm";
							qs.giveItems(MARK_OF_FATE_ID, 1);
							qs.giveItems(7562, 16);
							qs.rewardExpAndSp(682735, 45562);
							qs.giveItems(57, 123854);
							qs.takeItems(ARKENIAS_LETTER_ID, 1);
							qs.takeItems(PALUS_CHARM_ID, 1);
							qs.exitQuest(false);
							qs.playSound(QuestState.SOUND_FINISH);
						}
						break;
					case ARKENIA:
						if(cond == 16)
						{
							htmlText = "30419-01.htm";
							qs.set("cond", "17");
						}
						else if(cond == 17)
						{
							if(qs.getQuestItemsCount(TIMIRIRAN_SAP_ID) == 0 || qs.getQuestItemsCount(RED_FAIRY_DUST_ID) == 0)
								htmlText = "30419-03.htm";
							else
							{
								htmlText = "30419-04.htm";
								qs.set("cond", "18");
							}
						}
						else if(cond == 18)
							htmlText = "30419-06.htm";
						break;
					case IXIA:
						if(cond == 5)
						{
							htmlText = "30463-01.htm";
							qs.set("cond", "6");
							qs.giveItems(IXIAS_LIST_ID, 1);
							qs.takeItems(HERBALISM_TEXTBOOK_ID, 1);
						}
						else if(cond == 6 && (qs.getQuestItemsCount(MEDUSA_ICHOR_ID) < 10 || qs.getQuestItemsCount(M_SPIDER_FLUIDS_ID) < 10 || qs.getQuestItemsCount(DEAD_SEEKER_DUNG_ID) < 10 || qs.getQuestItemsCount(TYRANTS_BLOOD_ID) < 10 || qs.getQuestItemsCount(NIGHTSHADE_ROOT_ID) < 10))
						{
							htmlText = "30463-02.htm";
						}
						else if(cond == 7)
						{
							if(qs.getQuestItemsCount(BELLADONNA_ID) > 0)
								htmlText = "30463-04.htm";
							else if(qs.getQuestItemsCount(MEDUSA_ICHOR_ID) >= 10 && qs.getQuestItemsCount(M_SPIDER_FLUIDS_ID) >= 10 && qs.getQuestItemsCount(DEAD_SEEKER_DUNG_ID) >= 10 && qs.getQuestItemsCount(TYRANTS_BLOOD_ID) >= 10 && qs.getQuestItemsCount(NIGHTSHADE_ROOT_ID) >= 10)
							{
								htmlText = "30463-03.htm";
								qs.set("cond", "8");
								qs.giveItems(BELLADONNA_ID, 1);
								qs.takeItems(IXIAS_LIST_ID, 1);
								qs.takeItems(MEDUSA_ICHOR_ID, -1);
								qs.takeItems(TYRANTS_BLOOD_ID, -1);
								qs.takeItems(M_SPIDER_FLUIDS_ID, -1);
								qs.takeItems(DEAD_SEEKER_DUNG_ID, -1);
								qs.takeItems(NIGHTSHADE_ROOT_ID, -1);
							}
							else
								qs.set("cond", "6");
						}
						else if(cond > 7)
							htmlText = "30463-05.htm";
						break;
					case ALDERS_SPIRIT:
						if(cond == 10)
						{
							htmlText = "30613.htm";
							qs.set("cond", "11");
						}
						break;
					case METHEUS:
						if(cond == 1)
						{
							qs.set("cond", "2");
							htmlText = "30614-01.htm";
							qs.giveItems(METHEUS_FUNERAL_JAR_ID, 1);
							qs.takeItems(KAIRAS_LETTER1_ID, 1);
						}
						else if(cond == 2)
							htmlText = "30614-02.htm";
						else if(cond == 3)
						{
							qs.set("cond", "4");
							htmlText = "30614-03.htm";
							qs.giveItems(HERBALISM_TEXTBOOK_ID, 1);
							qs.takeItems(KASANDRAS_REMAINS_ID, 1);
						}
						else if(cond == 4)
						{
							htmlText = "30614-04.htm";
							qs.set("cond", "5");
						}
						else if(cond == 8)
						{
							htmlText = "30614-05.htm";
							qs.set("cond", "9");
							qs.giveItems(ALDERS_SKULL1_ID, 1);
							qs.takeItems(BELLADONNA_ID, 1);
						}
						else if(cond > 8)
							htmlText = "30614-06.htm";
						break;
				
				}
				break;
			case STATE_COMPLETED:
				htmlText = Quest.getAlreadyCompletedMsg();
				break;		
		}
		return htmlText;
	}

	@Override
	public String onKill(L2Npc npc, L2PcInstance player, boolean isPet)
	{
		QuestState qs = checkPlayerState(player, npc, STATE_STARTED);
		if(qs == null)
			return null;

		int cond = qs.getInt("cond");
		switch(npc.getNpcId())
		{
			case HANGMAN_TREE:
				if(cond == 2 && qs.getQuestItemsCount(METHEUS_FUNERAL_JAR_ID) > 0)
				{
					qs.giveItems(KASANDRAS_REMAINS_ID, 1);
					qs.takeItems(METHEUS_FUNERAL_JAR_ID, 1);
					qs.set("cond", "3");
					qs.playSound(QuestState.SOUND_MIDDLE);
				}
				break;
			case MEDUSA:
				if(cond == 6 && qs.getQuestItemsCount(MEDUSA_ICHOR_ID) < 10)
				{
					if(Rnd.get(2) == 1)
					{
						if(qs.getQuestItemsCount(MEDUSA_ICHOR_ID) == 9)
						{
							qs.giveItems(MEDUSA_ICHOR_ID, 1);
							qs.playSound(QuestState.SOUND_MIDDLE);
							if(qs.getQuestItemsCount(MEDUSA_ICHOR_ID) >= 10 && qs.getQuestItemsCount(M_SPIDER_FLUIDS_ID) >= 10 && qs.getQuestItemsCount(DEAD_SEEKER_DUNG_ID) >= 10 && qs.getQuestItemsCount(TYRANTS_BLOOD_ID) >= 10 && qs.getQuestItemsCount(NIGHTSHADE_ROOT_ID) >= 10)
								qs.set("cond", "7");
						}
						else
						{
							qs.giveItems(MEDUSA_ICHOR_ID, 1);
							qs.playSound(QuestState.SOUND_ITEMGET);
						}
					}
				}					
				break;
			case MARSH_SPIDER:
				if(cond == 6 && qs.getQuestItemsCount(M_SPIDER_FLUIDS_ID) < 10)
				{
					if(Rnd.get(2) == 1)
					{
						if(qs.getQuestItemsCount(M_SPIDER_FLUIDS_ID) == 9)
						{
							qs.giveItems(M_SPIDER_FLUIDS_ID, 1);
							qs.playSound(QuestState.SOUND_MIDDLE);
							if(qs.getQuestItemsCount(MEDUSA_ICHOR_ID) >= 10 && qs.getQuestItemsCount(M_SPIDER_FLUIDS_ID) >= 10 && qs.getQuestItemsCount(DEAD_SEEKER_DUNG_ID) >= 10 && qs.getQuestItemsCount(TYRANTS_BLOOD_ID) >= 10 && qs.getQuestItemsCount(NIGHTSHADE_ROOT_ID) >= 10)
								qs.set("cond", "7");
						}
					}
					else
					{
						qs.giveItems(M_SPIDER_FLUIDS_ID, 1);
						qs.playSound(QuestState.SOUND_ITEMGET);
					}
				}					
				break;
			case DEAD_SEEKER:
				if(cond == 6 && qs.getQuestItemsCount(DEAD_SEEKER_DUNG_ID) < 10)
				{
					if(Rnd.get(2) == 1)
					{
						if(qs.getQuestItemsCount(DEAD_SEEKER_DUNG_ID) == 9)
						{
							qs.giveItems(DEAD_SEEKER_DUNG_ID, 1);
							qs.playSound(QuestState.SOUND_MIDDLE);
							if(qs.getQuestItemsCount(MEDUSA_ICHOR_ID) >= 10 && qs.getQuestItemsCount(M_SPIDER_FLUIDS_ID) >= 10 && qs.getQuestItemsCount(DEAD_SEEKER_DUNG_ID) >= 10 && qs.getQuestItemsCount(TYRANTS_BLOOD_ID) >= 10 && qs.getQuestItemsCount(NIGHTSHADE_ROOT_ID) >= 10)
								qs.set("cond", "7");
						}
						else
						{
							qs.giveItems(DEAD_SEEKER_DUNG_ID, 1);
							qs.playSound(QuestState.SOUND_ITEMGET);
						}
					}
				}
				break;
			case TYRANT:
			case TYRANT_KINGPIN:
				if(cond == 6 && qs.getQuestItemsCount(TYRANTS_BLOOD_ID) < 10)
				{
					if((npc.getNpcId() == TYRANT && Rnd.get(2) == 1) || (npc.getNpcId() == TYRANT_KINGPIN && Rnd.get(10) < 6))
					{
						if(qs.getQuestItemsCount(TYRANTS_BLOOD_ID) == 9)
						{
							qs.giveItems(TYRANTS_BLOOD_ID, 1);
							qs.playSound(QuestState.SOUND_MIDDLE);
							if(qs.getQuestItemsCount(MEDUSA_ICHOR_ID) >= 10 && qs.getQuestItemsCount(M_SPIDER_FLUIDS_ID) >= 10 && qs.getQuestItemsCount(DEAD_SEEKER_DUNG_ID) >= 10 && qs.getQuestItemsCount(TYRANTS_BLOOD_ID) >= 10 && qs.getQuestItemsCount(NIGHTSHADE_ROOT_ID) >= 10)
								qs.set("cond", "7");
						}
						else
						{
							qs.giveItems(TYRANTS_BLOOD_ID, 1);
							qs.playSound(QuestState.SOUND_ITEMGET);
						}
					}
				}
				break;
			case MARSH_STAKATO:
			case MARSH_STAKATO_WORKER:
			case MARSH_STAKATO_SOLDIER:
			case MARSH_STAKATO_DRONE:
				if(cond == 6 && qs.getQuestItemsCount(NIGHTSHADE_ROOT_ID) < 10)
				{
					if((npc.getNpcId() == MARSH_STAKATO && Rnd.get(10) < 3) || (npc.getNpcId() == MARSH_STAKATO_WORKER && Rnd.get(10) < 4) || (npc.getNpcId() == MARSH_STAKATO_SOLDIER && Rnd.get(10) < 5) || (npc.getNpcId() == MARSH_STAKATO_DRONE && Rnd.get(10) < 10))
					{
						if(qs.getQuestItemsCount(NIGHTSHADE_ROOT_ID) == 9)
						{
							qs.giveItems(NIGHTSHADE_ROOT_ID, 1);
							qs.playSound(QuestState.SOUND_MIDDLE);
							if(qs.getQuestItemsCount(MEDUSA_ICHOR_ID) >= 10 && qs.getQuestItemsCount(M_SPIDER_FLUIDS_ID) >= 10 && qs.getQuestItemsCount(DEAD_SEEKER_DUNG_ID) >= 10 && qs.getQuestItemsCount(TYRANTS_BLOOD_ID) >= 10 && qs.getQuestItemsCount(NIGHTSHADE_ROOT_ID) >= 10)
								qs.set("cond", "7");
						}	
						else
						{
							qs.giveItems(NIGHTSHADE_ROOT_ID, 1);
							qs.playSound(QuestState.SOUND_ITEMGET);
						}
					}
				}
				break;
			case GRANDIS:
				if(cond == 17 && qs.getQuestItemsCount(GRANDIS_SKULL_ID) < 10)
				{
					if(qs.getQuestItemsCount(GRANDIS_SKULL_ID) == 9)
					{
						qs.giveItems(GRANDIS_SKULL_ID, 1);
						qs.playSound(QuestState.SOUND_MIDDLE);
						if(qs.getQuestItemsCount(GRANDIS_SKULL_ID) >= 10 && qs.getQuestItemsCount(KARUL_BUGBEAR_SKULL_ID) >= 10 && qs.getQuestItemsCount(BREKA_OVERLORD_SKULL_ID) >= 10 && qs.getQuestItemsCount(LETO_OVERLORD_SKULL_ID) >= 10)
							qs.set("cond", "19");
					}
					else
					{
						qs.giveItems(GRANDIS_SKULL_ID, 1);
						qs.playSound(QuestState.SOUND_ITEMGET);
					}
				}
				break;
			case KARUL_BUGBEAR:
				if(cond == 17 && qs.getQuestItemsCount(KARUL_BUGBEAR_SKULL_ID) < 10)
				{
					if(qs.getQuestItemsCount(KARUL_BUGBEAR_SKULL_ID) == 9)
					{
						qs.giveItems(KARUL_BUGBEAR_SKULL_ID, 1);
						qs.playSound(QuestState.SOUND_MIDDLE);
						if(qs.getQuestItemsCount(GRANDIS_SKULL_ID) >= 10 && qs.getQuestItemsCount(KARUL_BUGBEAR_SKULL_ID) >= 10 && qs.getQuestItemsCount(BREKA_OVERLORD_SKULL_ID) >= 10 && qs.getQuestItemsCount(LETO_OVERLORD_SKULL_ID) >= 10)
							qs.set("cond", "19");
					}
					else
					{
						qs.giveItems(KARUL_BUGBEAR_SKULL_ID, 1);
						qs.playSound(QuestState.SOUND_ITEMGET);
					}
				}
				break;
			case BREKA_ORC_OVERLORD:
				if(cond == 17 && qs.getQuestItemsCount(BREKA_OVERLORD_SKULL_ID) < 10)
				{
					if(qs.getQuestItemsCount(BREKA_OVERLORD_SKULL_ID) == 9)
					{
						qs.giveItems(BREKA_OVERLORD_SKULL_ID, 1);
						qs.playSound(QuestState.SOUND_MIDDLE);
					}
					else
					{
						qs.giveItems(BREKA_OVERLORD_SKULL_ID, 1);
						qs.playSound(QuestState.SOUND_ITEMGET);
					}
				}
				break;
			case LETO_LIZARDMAN_OVERLORD:
				if(cond == 17 && qs.getQuestItemsCount(LETO_OVERLORD_SKULL_ID) < 10)
				{
					if(qs.getQuestItemsCount(LETO_OVERLORD_SKULL_ID) == 9)
					{
						qs.giveItems(LETO_OVERLORD_SKULL_ID, 1);
						qs.playSound(QuestState.SOUND_MIDDLE);						
					}
					else
					{
						qs.giveItems(LETO_OVERLORD_SKULL_ID, 1);
						qs.playSound(QuestState.SOUND_ITEMGET);
					}
				}
				break;
			case BLACK_WILLOW_LURKER:
				if(cond == 17 && qs.getQuestItemsCount(BLACK_WILLOW_LEAF_ID) == 0)
				{
					qs.giveItems(BLACK_WILLOW_LEAF_ID, 1);
					qs.playSound(QuestState.SOUND_MIDDLE);
				}
				break;		
		}
		return null;
	}

	public static void main(String[] args)
	{
		new Q219_TestimonyOfFate(219, qn, "Testimony Of Fate");
	}
}