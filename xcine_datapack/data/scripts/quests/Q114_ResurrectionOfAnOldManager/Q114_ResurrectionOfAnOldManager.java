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
package quests.Q114_ResurrectionOfAnOldManager;

import net.xcine.gameserver.ai.CtrlIntention;
import net.xcine.gameserver.model.actor.L2Attackable;
import net.xcine.gameserver.model.actor.L2Npc;
import net.xcine.gameserver.model.actor.instance.L2PcInstance;
import net.xcine.gameserver.model.quest.Quest;
import net.xcine.gameserver.model.quest.QuestState;
import net.xcine.gameserver.network.serverpackets.CreatureSay;

public class Q114_ResurrectionOfAnOldManager extends Quest
{
	private static final String qn = "Q114_ResurrectionOfAnOldManager";
	
	// NPCs
	private static final int NPC_NEWYEAR = 31961;
	private static final int NPC_YUMI = 32041;
	private static final int NPC_STONES = 32046;
	private static final int NPC_WENDY = 32047;
	private static final int NPC_BOX = 32050;
	
	private static final int MOB_GUARDIAN = 27318;
	
	private static final int ITEM_DETECTOR = 8090;
	private static final int ITEM_DETECTOR2 = 8091;
	private static final int ITEM_STARSTONE = 8287;
	private static final int ITEM_LETTER = 8288;
	private static final int ITEM_STARSTONE2 = 8289;
	
	private L2Attackable golem;
	
	public Q114_ResurrectionOfAnOldManager(final int scriptId, final String name, final String descr)
	{
		super(scriptId, name, descr);
		
		addStartNpc(NPC_YUMI);
		addTalkId(NPC_BOX, NPC_NEWYEAR, NPC_STONES, NPC_WENDY);
		addKillId(MOB_GUARDIAN);
		addFirstTalkId(NPC_STONES);
		
		questItemIds = new int[]
		{
			ITEM_DETECTOR,
			ITEM_DETECTOR2,
			ITEM_STARSTONE,
			ITEM_LETTER,
			ITEM_STARSTONE2
		};
	}
	
	@Override
	public String onAdvEvent(final String event, final L2Npc npc, final L2PcInstance player)
	{
		String htmltext = event;
		final QuestState st = player.getQuestState(qn);
		if (st == null)
		{
			return htmltext;
		}
		
		if ("31961-02.htm".equalsIgnoreCase(event))
		{
			st.set("cond", "22");
			st.takeItems(ITEM_LETTER, 1);
			st.giveItems(ITEM_STARSTONE2, 1);
			st.playSound(QuestState.SOUND_MIDDLE);
		}
		else if ("32041-02.htm".equalsIgnoreCase(event))
		{
			st.setState(Quest.STATE_STARTED);
			st.playSound(QuestState.SOUND_ACCEPT);
			st.set("cond", "1");
			st.set("talk", "0");
		}
		else if ("32041-06.htm".equalsIgnoreCase(event))
		{
			st.set("talk", "1");
		}
		else if ("32041-07.htm".equalsIgnoreCase(event))
		{
			st.set("cond", "2");
			st.playSound(QuestState.SOUND_MIDDLE);
			st.set("talk", "0");
		}
		else if ("32041-10.htm".equalsIgnoreCase(event))
		{
			int choice = st.getInt("choice");
			if (choice == 1)
			{
				htmltext = "32041-10.htm";
			}
			else if (choice == 2)
			{
				htmltext = "32041-10a.htm";
			}
			else if (choice == 3)
			{
				htmltext = "32041-10b.htm";
			}
		}
		else if ("32041-11.htm".equalsIgnoreCase(event))
		{
			st.set("talk", "1");
		}
		else if ("32041-18.htm".equalsIgnoreCase(event))
		{
			st.set("talk", "2");
		}
		else if ("32041-20.htm".equalsIgnoreCase(event))
		{
			st.set("cond", "6");
			st.playSound(QuestState.SOUND_MIDDLE);
			st.set("talk", "0");
		}
		else if ("32041-25.htm".equalsIgnoreCase(event))
		{
			st.set("cond", "17");
			st.playSound(QuestState.SOUND_MIDDLE);
			st.giveItems(ITEM_DETECTOR, 1);
		}
		else if ("32041-28.htm".equalsIgnoreCase(event))
		{
			st.takeItems(ITEM_DETECTOR2, 1);
			st.set("talk", "1");
		}
		else if ("32041-31.htm".equalsIgnoreCase(event))
		{
			if (st.getInt("choice") > 1)
			{
				htmltext = "32041-37.htm";
			}
		}
		else if ("32041-32.htm".equalsIgnoreCase(event))
		{
			st.set("cond", "21");
			st.giveItems(ITEM_LETTER, 1);
			st.playSound(QuestState.SOUND_MIDDLE);
		}
		else if ("32041-36.htm".equalsIgnoreCase(event))
		{
			st.set("cond", "20");
			st.playSound(QuestState.SOUND_MIDDLE);
		}
		else if ("32046-02.htm".equalsIgnoreCase(event))
		{
			st.set("cond", "19");
			st.playSound(QuestState.SOUND_MIDDLE);
		}
		else if ("32046-06.htm".equalsIgnoreCase(event))
		{
			st.exitQuest(false);
			st.rewardExpAndSp(410358, 32060);
			st.playSound("ItemSound.quest_finish");
		}
		else if ("32047-01.htm".equalsIgnoreCase(event))
		{
			if ((st.getInt("talk") + st.getInt("talk1")) == 2)
			{
				htmltext = "32047-04.htm";
			}
			else if ((st.getInt("talk") + st.getInt("talk1") + st.getInt("talk2")) == 6)
			{
				htmltext = "32047-08.htm";
			}
		}
		else if ("32047-02.htm".equalsIgnoreCase(event))
		{
			if (st.getInt("talk") == 0)
			{
				st.set("talk", "1");
			}
		}
		else if ("32047-03.htm".equalsIgnoreCase(event))
		{
			if (st.getInt("talk1") == 0)
			{
				st.set("talk1", "1");
			}
		}
		else if ("32047-05.htm".equalsIgnoreCase(event))
		{
			st.set("cond", "3");
			st.playSound(QuestState.SOUND_MIDDLE);
			st.set("talk", "0");
			st.set("choice", "1");
			st.unset("talk1");
		}
		else if ("32047-06.htm".equalsIgnoreCase(event))
		{
			st.set("cond", "4");
			st.playSound(QuestState.SOUND_MIDDLE);
			st.set("talk", "0");
			st.set("choice", "2");
			st.unset("talk1");
		}
		else if ("32047-07.htm".equalsIgnoreCase(event))
		{
			st.set("cond", "5");
			st.playSound(QuestState.SOUND_MIDDLE);
			st.set("talk", "0");
			st.set("choice", "3");
			st.unset("talk1");
		}
		else if ("32047-13.htm".equalsIgnoreCase(event))
		{
			st.set("cond", "7");
			st.playSound(QuestState.SOUND_MIDDLE);
		}
		else if ("32047-13a.htm".equalsIgnoreCase(event))
		{
			st.set("cond", "10");
			st.playSound(QuestState.SOUND_MIDDLE);
		}
		else if ("32047-15.htm".equalsIgnoreCase(event))
		{
			if (st.getInt("talk") == 0)
			{
				st.set("talk", "1");
			}
		}
		else if ("32047-15a.htm".equalsIgnoreCase(event))
		{
			if (st.getInt("golemSpawned") == 0)
			{
				golem = (L2Attackable) addSpawn(MOB_GUARDIAN, 96977, -110625, -3280, 0, true, 900000, false);
				golem.broadcastPacket(new CreatureSay(golem.getObjectId(), 0, golem.getName(), "You, " + st.getPlayer().getName() + ", you attacked Wendy. Prepare to die!"));
				golem.setRunning();
				golem.addDamageHate(player, 0, 999);
				golem.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, player);
				st.set("golemSpawned", "1");
				startQuestTimer("golemCleanup", 900000, null, null, false);
			}
			else
			{
				htmltext = "32047-19a.htm";
			}
		}
		else if ("32047-17a.htm".equalsIgnoreCase(event))
		{
			st.set("cond", "12");
			st.playSound(QuestState.SOUND_MIDDLE);
		}
		else if ("32047-20.htm".equalsIgnoreCase(event))
		{
			st.set("talk", "2");
		}
		else if ("32047-23.htm".equalsIgnoreCase(event))
		{
			st.set("cond", "13");
			st.playSound(QuestState.SOUND_MIDDLE);
			st.set("talk", "0");
		}
		else if ("32047-25.htm".equalsIgnoreCase(event))
		{
			st.set("cond", "15");
			st.playSound(QuestState.SOUND_MIDDLE);
			st.takeItems(ITEM_STARSTONE, 1);
		}
		else if ("32047-30.htm".equalsIgnoreCase(event))
		{
			st.set("talk", "2");
		}
		else if ("32047-33.htm".equalsIgnoreCase(event))
		{
			if (st.getInt("cond") == 7)
			{
				st.set("cond", "8");
				st.set("talk", "0");
				st.playSound(QuestState.SOUND_MIDDLE);
			}
			else if (st.getInt("cond") == 8)
			{
				st.set("cond", "9");
				st.playSound(QuestState.SOUND_MIDDLE);
			}
		}
		else if ("32047-34.htm".equalsIgnoreCase(event))
		{
			st.set("cond", "9");
			st.playSound(QuestState.SOUND_MIDDLE);
		}
		else if ("32047-38.htm".equalsIgnoreCase(event))
		{
			st.giveItems(ITEM_STARSTONE2, 1);
			st.takeItems(57, 3000); // Adena
			st.set("cond", "26");
			st.playSound(QuestState.SOUND_MIDDLE);
		}
		else if ("32050-02.htm".equalsIgnoreCase(event))
		{
			st.playSound("ItemSound.armor_wood_3");
			st.set("talk", "1");
		}
		else if ("32050-04.htm".equalsIgnoreCase(event))
		{
			st.set("cond", "14");
			st.giveItems(ITEM_STARSTONE, 1);
			st.playSound(QuestState.SOUND_MIDDLE);
			st.set("talk", "0");
		}
		else if ("golemCleanup".equalsIgnoreCase(event))
		{
			if (golem != null)
			{
				golem.deleteMe();
			}
			st.set("golemSpawned", "0");
		}
		
		return htmltext;
	}
	
	// atm custom, on retail it is when you walk to npcs radius
	@Override
	public String onFirstTalk(final L2Npc npc, final L2PcInstance player)
	{
		String htmltext = "";
		final QuestState st = player.getQuestState(qn);
		if (st != null)
		{
			if ((npc.getNpcId() == NPC_STONES) && (st.getInt("cond") == 17))
			{
				st.playSound(QuestState.SOUND_MIDDLE);
				st.takeItems(ITEM_DETECTOR, 1);
				st.giveItems(ITEM_DETECTOR2, 1);
				st.set("cond", "18");
				return "The radio signal detector is responding. # A suspicious pile of stones catches your eye.";
			}
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(final L2Npc npc, final L2PcInstance player)
	{
		String htmltext = getNoQuestMsg();
		final QuestState st = player.getQuestState(qn);
		if (st == null)
		{
			return htmltext;
		}
		
		byte state = st.getState();
		int npcId = npc.getNpcId();
		int cond = st.getInt("cond");
		int talk = st.getInt("talk");
		int talk1 = st.getInt("talk1");
		
		if (state == Quest.STATE_COMPLETED)
		{
			return getAlreadyCompletedMsg();
		}
		else if (npcId == NPC_YUMI)
		{
			if (state == Quest.STATE_CREATED)
			{
				QuestState qs121 = player.getQuestState("_121_PavelTheGiant");
				if ((qs121 != null) && qs121.isCompleted())
				{
					if (player.getLevel() >= 49)
					{
						htmltext = "32041-01.htm";
					}
					else
					{
						htmltext = "32041-00.htm";
						st.exitQuest(true);
					}
					
				}
				else
				{
					htmltext = "32041-00.htm";
					st.exitQuest(true);
				}
			}
			else if (cond == 1)
			{
				if (talk == 0)
				{
					htmltext = "32041-02.htm";
				}
				else
				{
					htmltext = "32041-06.htm";
				}
			}
			else if (cond == 2)
			{
				htmltext = "32041-08.htm";
			}
			else if ((cond >= 3) && (cond <= 5))
			{
				if (talk == 0)
				{
					htmltext = "32041-09.htm";
				}
				else if (talk == 1)
				{
					htmltext = "32041-11.htm";
				}
				else
				{
					htmltext = "32041-18.htm";
				}
			}
			else if (cond == 6)
			{
				htmltext = "32041-21.htm";
			}
			else if ((cond == 9) || (cond == 12) || (cond == 16))
			{
				htmltext = "32041-22.htm";
			}
			else if (cond == 19)
			{
				if (talk == 0)
				{
					htmltext = "32041-27.htm";
				}
				else
				{
					htmltext = "32041-28.htm";
				}
			}
			else if (cond == 20)
			{
				htmltext = "32041-36.htm";
			}
			else if (cond == 21)
			{
				htmltext = "32041-33.htm";
			}
			else if ((cond == 22) || (cond == 26))
			{
				htmltext = "32041-34.htm";
				st.set("cond", "27");
				st.playSound(QuestState.SOUND_MIDDLE);
			}
			else if (cond == 27)
			{
				htmltext = "32041-35.htm";
			}
		}
		else if (npcId == NPC_WENDY)
		{
			if (cond == 2)
			{
				if ((talk + talk1) < 2)
				{
					htmltext = "32047-01.htm";
				}
				else if ((talk + talk1) == 2)
				{
					htmltext = "32047-04.htm";
				}
			}
			else if (cond == 3)
			{
				htmltext = "32047-09.htm";
			}
			else if ((cond == 4) || (cond == 5))
			{
				htmltext = "32047-09a.htm";
			}
			else if (cond == 6)
			{
				int choice = st.getInt("choice");
				if (choice == 1)
				{
					if (talk == 0)
					{
						htmltext = "32047-10.htm";
					}
					else if (talk == 1)
					{
						htmltext = "32047-20.htm";
					}
					else
					{
						htmltext = "32047-30.htm";
					}
				}
				else if (choice == 2)
				{
					htmltext = "32047-10a.htm";
				}
				else if (choice == 3)
				{
					if (talk == 0)
					{
						htmltext = "32047-14.htm";
					}
					else if (talk == 1)
					{
						htmltext = "32047-15.htm";
					}
					else
					{
						htmltext = "32047-20.htm";
					}
				}
			}
			else if (cond == 7)
			{
				if (talk == 0)
				{
					htmltext = "32047-14.htm";
				}
				else if (talk == 1)
				{
					htmltext = "32047-15.htm";
				}
				else
				{
					htmltext = "32047-20.htm";
				}
			}
			else if (cond == 8)
			{
				htmltext = "32047-30.htm";
			}
			else if (cond == 9)
			{
				htmltext = "32047-27.htm";
			}
			else if (cond == 10)
			{
				htmltext = "32047-14a.htm";
			}
			else if (cond == 11)
			{
				htmltext = "32047-16a.htm";
			}
			else if (cond == 12)
			{
				htmltext = "32047-18a.htm";
			}
			else if (cond == 13)
			{
				htmltext = "32047-23.htm";
			}
			else if (cond == 14)
			{
				htmltext = "32047-24.htm";
			}
			else if (cond == 15)
			{
				htmltext = "32047-26.htm";
				st.set("cond", "16");
				st.playSound(QuestState.SOUND_MIDDLE);
			}
			else if (cond == 16)
			{
				htmltext = "32047-27.htm";
			}
			else if (cond == 20)
			{
				htmltext = "32047-35.htm";
			}
			else if (cond == 26)
			{
				htmltext = "32047-40.htm";
			}
		}
		else if (npcId == NPC_BOX)
		{
			if (cond == 13)
			{
				if (talk == 0)
				{
					htmltext = "32050-01.htm";
				}
				else
				{
					htmltext = "32050-03.htm";
				}
			}
			else if (cond == 14)
			{
				htmltext = "32050-05.htm";
			}
		}
		else if (npcId == NPC_STONES)
		{
			if (cond == 18)
			{
				htmltext = "32046-01.htm";
			}
			else if (cond == 19)
			{
				htmltext = "32046-02.htm";
			}
			else if (cond == 27)
			{
				htmltext = "32046-03.htm";
			}
		}
		else if (npcId == NPC_NEWYEAR)
		{
			if (cond == 21)
			{
				htmltext = "31961-01.htm";
			}
			else if (cond == 22)
			{
				htmltext = "31961-03.htm";
			}
		}
		
		return htmltext;
	}
	
	@Override
	public String onKill(final L2Npc npc, final L2PcInstance player, final boolean isPet)
	{
		
		final QuestState st = player.getQuestState(qn);
		
		if (st == null)
		{
			return null;
		}
		
		if (st.getState() != Quest.STATE_STARTED)
		{
			return null;
		}
		
		if ((st.getState() == Quest.STATE_STARTED) && (st.getInt("cond") == 10))
		{
			if (npc.getNpcId() == MOB_GUARDIAN)
			{
				npc.broadcastPacket(new CreatureSay(npc.getObjectId(), 0, npc.getName(), "This enemy is far too powerful for me to fight. I must withdraw"));
				st.set("cond", "11");
				st.playSound(QuestState.SOUND_MIDDLE);
			}
		}
		
		return null;
	}
	
	public static void main(String[] args)
	{
		new Q114_ResurrectionOfAnOldManager(114, "Q114_ResurrectionOfAnOldManager", "Resurrection Of An Old Manager");
	}
}