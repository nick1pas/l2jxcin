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
package quests.Q105_SkirmishWithTheOrcs;

import net.xcine.gameserver.model.actor.L2Npc;
import net.xcine.gameserver.model.actor.instance.L2PcInstance;
import net.xcine.gameserver.model.base.Race;
import net.xcine.gameserver.model.quest.Quest;
import net.xcine.gameserver.model.quest.QuestState;
import net.xcine.util.Rnd;

public class Q105_SkirmishWithTheOrcs extends Quest
{
	private static final String qn = "Q105_SkirmishWithTheOrcs";
	
	// NPCs
	private static final int KENDELL = 30218;
	
	// Item
	private static final int KENDNELLS_ORDER1 = 1836;
	private static final int KENDNELLS_ORDER2 = 1837;
	private static final int KENDNELLS_ORDER3 = 1838;
	private static final int KENDNELLS_ORDER4 = 1839;
	private static final int KENDNELLS_ORDER5 = 1840;
	private static final int KENDNELLS_ORDER6 = 1841;
	private static final int KENDNELLS_ORDER7 = 1842;
	private static final int KENDNELLS_ORDER8 = 1843;
	private static final int KABOO_CHIEF_TORC1 = 1844;
	private static final int KABOO_CHIEF_TORC2 = 1845;
	
	// Monster
	private static final int KABOO_CHIEF_OUPH = 27059;
	private static final int KABOO_CHIEF_KRACHA = 27060;
	private static final int KABOO_CHIEF_BATOH = 27061;
	private static final int KABOO_CHIEF_TANUKIA = 27062;
	private static final int KABOO_CHIEF_TUREL = 27064;
	private static final int KABOO_CHIEF_ROKO = 27065;
	private static final int KABOO_CHIEF_KAMUT = 27067;
	private static final int KABOO_CHIEF_MURTIKA = 27068;
	
	// Rewards
	private static final int SPIRITSHOT_FOR_BEGINNERS = 5790;
	private static final int SOULSHOT_FOR_BEGINNERS = 5789;
	private static final int RED_SUNSET_STAFF = 754;
	private static final int RED_SUNSET_SWORD = 981;
	private static final int ECHO_BATTLE = 4412;
	private static final int ECHO_LOVE = 4413;
	private static final int ECHO_SOLITUDE = 4414;
	private static final int ECHO_FEAST = 4415;
	private static final int ECHO_CELEBRATION = 4416;
	
	public Q105_SkirmishWithTheOrcs(int questId, String name, String descr)
	{
		super(questId, name, descr);
		
		questItemIds = new int[]
		{
			KENDNELLS_ORDER1,
			KENDNELLS_ORDER2,
			KENDNELLS_ORDER3,
			KENDNELLS_ORDER4,
			KENDNELLS_ORDER5,
			KENDNELLS_ORDER6,
			KENDNELLS_ORDER7,
			KENDNELLS_ORDER8,
			KABOO_CHIEF_TORC1,
			KABOO_CHIEF_TORC2
		};
		
		addStartNpc(KENDELL);
		addTalkId(KENDELL);
		
		addKillId(KABOO_CHIEF_OUPH, KABOO_CHIEF_KRACHA, KABOO_CHIEF_BATOH, KABOO_CHIEF_TANUKIA, KABOO_CHIEF_TUREL, KABOO_CHIEF_ROKO, KABOO_CHIEF_KAMUT, KABOO_CHIEF_MURTIKA);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = event;
		QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("30218-03.htm"))
		{
			st.set("cond", "1");
			st.setState(STATE_STARTED);
			st.playSound(QuestState.SOUND_ACCEPT);
			
			int n = Rnd.get(100);
			if (n < 25)
				st.giveItems(KENDNELLS_ORDER1, 1);
			else if (n < 50)
				st.giveItems(KENDNELLS_ORDER2, 1);
			else if (n < 75)
				st.giveItems(KENDNELLS_ORDER3, 1);
			else
				st.giveItems(KENDNELLS_ORDER4, 1);
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
				if (player.getRace() != Race.Elf)
				{
					htmltext = "30218-00.htm";
					st.exitQuest(true);
				}
				else if (player.getLevel() < 10)
				{
					htmltext = "30221-01.htm";
					st.exitQuest(true);
				}
				else
					htmltext = "30218-02.htm";
				break;
			
			case STATE_STARTED:
				int cond = st.getInt("cond");
				if (cond == 1)
					htmltext = "30218-05.htm";
				else if (cond == 2)
				{
					htmltext = "30218-06.htm";
					
					if (st.getQuestItemsCount(KENDNELLS_ORDER1) == 1)
						st.takeItems(KENDNELLS_ORDER1, 1);
					else if (st.getQuestItemsCount(KENDNELLS_ORDER2) == 1)
						st.takeItems(KENDNELLS_ORDER2, 1);
					else if (st.getQuestItemsCount(KENDNELLS_ORDER3) == 1)
						st.takeItems(KENDNELLS_ORDER3, 1);
					else if (st.getQuestItemsCount(KENDNELLS_ORDER4) == 1)
						st.takeItems(KENDNELLS_ORDER4, 1);
					
					st.takeItems(KABOO_CHIEF_TORC1, 1);
					
					int n = Rnd.get(100);
					if (n < 25)
						st.giveItems(KENDNELLS_ORDER5, 1);
					else if (n < 50)
						st.giveItems(KENDNELLS_ORDER6, 1);
					else if (n < 75)
						st.giveItems(KENDNELLS_ORDER7, 1);
					else
						st.giveItems(KENDNELLS_ORDER8, 1);
					
					st.set("cond", "3");
					st.playSound(QuestState.SOUND_MIDDLE);
				}
				else if (cond == 3)
					htmltext = "30218-07.htm";
				else if (cond == 4)
				{
					htmltext = "30218-08.htm";
					
					if (st.getQuestItemsCount(KENDNELLS_ORDER5) == 1)
						st.takeItems(KENDNELLS_ORDER5, 1);
					else if (st.getQuestItemsCount(KENDNELLS_ORDER6) == 1)
						st.takeItems(KENDNELLS_ORDER6, 1);
					else if (st.getQuestItemsCount(KENDNELLS_ORDER7) == 1)
						st.takeItems(KENDNELLS_ORDER7, 1);
					else if (st.getQuestItemsCount(KENDNELLS_ORDER8) == 1)
						st.takeItems(KENDNELLS_ORDER8, 1);
					st.takeItems(KABOO_CHIEF_TORC2, -1);
					
					if (player.isMageClass())
						st.giveItems(RED_SUNSET_STAFF, 1);
					else
						st.giveItems(RED_SUNSET_SWORD, 1);
					
					if (player.isNewbie())
					{
						st.showQuestionMark(26);
						if (player.isMageClass())
						{
							st.playTutorialVoice("tutorial_voice_027");
							st.giveItems(SPIRITSHOT_FOR_BEGINNERS, 3000);
						}
						else
						{
							st.playTutorialVoice("tutorial_voice_026");
							st.giveItems(SOULSHOT_FOR_BEGINNERS, 7000);
						}
					}
					
					st.giveItems(ECHO_BATTLE, 10);
					st.giveItems(ECHO_LOVE, 10);
					st.giveItems(ECHO_SOLITUDE, 10);
					st.giveItems(ECHO_FEAST, 10);
					st.giveItems(ECHO_CELEBRATION, 10);
					st.exitQuest(false);
					st.playSound(QuestState.SOUND_FINISH);
				}
				break;
			
			case STATE_COMPLETED:
				htmltext = getAlreadyCompletedMsg();
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
		
		int cond = st.getInt("cond");
		if (cond == 1)
		{
			switch (npc.getNpcId())
			{
				case KABOO_CHIEF_OUPH:
					if (st.hasQuestItems(KENDNELLS_ORDER1))
					{
						st.giveItems(KABOO_CHIEF_TORC1, 1);
						st.playSound(QuestState.SOUND_MIDDLE);
						st.set("cond", "2");
					}
					break;
				
				case KABOO_CHIEF_KRACHA:
					if (st.hasQuestItems(KENDNELLS_ORDER2))
					{
						st.giveItems(KABOO_CHIEF_TORC1, 1);
						st.playSound(QuestState.SOUND_MIDDLE);
						st.set("cond", "2");
					}
					break;
				
				case KABOO_CHIEF_BATOH:
					if (st.hasQuestItems(KENDNELLS_ORDER3))
					{
						st.giveItems(KABOO_CHIEF_TORC1, 1);
						st.playSound(QuestState.SOUND_MIDDLE);
						st.set("cond", "2");
					}
					break;
				
				case KABOO_CHIEF_TANUKIA:
					if (st.hasQuestItems(KENDNELLS_ORDER4))
					{
						st.giveItems(KABOO_CHIEF_TORC1, 1);
						st.playSound(QuestState.SOUND_MIDDLE);
						st.set("cond", "2");
					}
					break;
			}
		}
		else if (cond == 3)
		{
			switch (npc.getNpcId())
			{
				case KABOO_CHIEF_TUREL:
					if (st.hasQuestItems(KENDNELLS_ORDER5))
					{
						st.giveItems(KABOO_CHIEF_TORC2, 1);
						st.playSound(QuestState.SOUND_MIDDLE);
						st.set("cond", "4");
					}
					break;
				
				case KABOO_CHIEF_ROKO:
					if (st.hasQuestItems(KENDNELLS_ORDER6))
					{
						st.giveItems(KABOO_CHIEF_TORC2, 1);
						st.playSound(QuestState.SOUND_MIDDLE);
						st.set("cond", "4");
					}
					break;
				
				case KABOO_CHIEF_KAMUT:
					if (st.hasQuestItems(KENDNELLS_ORDER7))
					{
						st.giveItems(KABOO_CHIEF_TORC2, 1);
						st.playSound(QuestState.SOUND_MIDDLE);
						st.set("cond", "4");
					}
					break;
				
				case KABOO_CHIEF_MURTIKA:
					if (st.hasQuestItems(KENDNELLS_ORDER8))
					{
						st.giveItems(KABOO_CHIEF_TORC2, 1);
						st.playSound(QuestState.SOUND_MIDDLE);
						st.set("cond", "4");
					}
					break;
			}
		}
		
		return null;
	}
	
	public static void main(String[] args)
	{
		new Q105_SkirmishWithTheOrcs(105, qn, "Skirmish with the Orcs");
	}
}