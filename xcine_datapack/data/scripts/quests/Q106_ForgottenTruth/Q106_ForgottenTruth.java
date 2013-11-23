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
package quests.Q106_ForgottenTruth;

import net.xcine.gameserver.model.actor.L2Npc;
import net.xcine.gameserver.model.actor.instance.L2PcInstance;
import net.xcine.gameserver.model.base.Race;
import net.xcine.gameserver.model.quest.Quest;
import net.xcine.gameserver.model.quest.QuestState;

public class Q106_ForgottenTruth extends Quest
{
	private static final String qn = "Q106_ForgottenTruth";
	
	// NPCs
	private static final int THIFIELL = 30358;
	private static final int KARTIA = 30133;
	
	// Items
	private static final int ONYX_TALISMAN1 = 984;
	private static final int ONYX_TALISMAN2 = 985;
	private static final int ANCIENT_SCROLL = 986;
	private static final int ANCIENT_CLAY_TABLET = 987;
	private static final int KARTIAS_TRANSLATION = 988;
	
	// Rewards
	private static final int SPIRITSHOT_NO_GRADE = 2509;
	private static final int SOULSHOT_NO_GRADE = 1835;
	private static final int ELDRITCH_DAGGER = 989;
	private static final int SPIRITSHOT_FOR_BEGINNERS = 5790;
	private static final int SOULSHOT_FOR_BEGINNERS = 5789;
	private static final int ECHO_BATTLE = 4412;
	private static final int ECHO_LOVE = 4413;
	private static final int ECHO_SOLITUDE = 4414;
	private static final int ECHO_FEAST = 4415;
	private static final int ECHO_CELEBRATION = 4416;
	private static final int LESSER_HEALING_POTION = 1060;
	
	public Q106_ForgottenTruth(int questId, String name, String descr)
	{
		super(questId, name, descr);
		
		questItemIds = new int[]
		{
			ONYX_TALISMAN1,
			ONYX_TALISMAN2,
			ANCIENT_SCROLL,
			ANCIENT_CLAY_TABLET,
			KARTIAS_TRANSLATION
		};
		
		addStartNpc(THIFIELL);
		addTalkId(THIFIELL, KARTIA);
		
		addKillId(27070); // Tumran Orc Brigand
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		QuestState st = player.getQuestState(qn);
		String htmltext = event;
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("30358-05.htm"))
		{
			st.set("cond", "1");
			st.setState(STATE_STARTED);
			st.playSound(QuestState.SOUND_ACCEPT);
			st.giveItems(ONYX_TALISMAN1, 1);
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
				if (player.getRace() != Race.DarkElf)
				{
					htmltext = "30358-00.htm";
					st.exitQuest(true);
				}
				else if (player.getLevel() < 10)
				{
					htmltext = "30358-02.htm";
					st.exitQuest(true);
				}
				else
					htmltext = "30358-03.htm";
				break;
			
			case STATE_STARTED:
				int cond = st.getInt("cond");
				switch (npc.getNpcId())
				{
					case THIFIELL:
						if (cond == 1)
							htmltext = "30358-06.htm";
						else if (cond == 2)
							htmltext = "30358-06.htm";
						else if (cond == 3)
							htmltext = "30358-06.htm";
						else if (cond == 4)
						{
							htmltext = "30358-07.htm";
							st.takeItems(KARTIAS_TRANSLATION, 1);
							st.giveItems(ELDRITCH_DAGGER, 1);
							st.giveItems(LESSER_HEALING_POTION, 100);
							
							if (player.isMageClass())
								st.giveItems(SPIRITSHOT_NO_GRADE, 500);
							else
								st.giveItems(SOULSHOT_NO_GRADE, 1000);
							
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
									st.giveItems(SOULSHOT_FOR_BEGINNERS, 6000);
								}
							}
							
							st.giveItems(ECHO_BATTLE, 10);
							st.giveItems(ECHO_LOVE, 10);
							st.giveItems(ECHO_SOLITUDE, 10);
							st.giveItems(ECHO_FEAST, 10);
							st.giveItems(ECHO_CELEBRATION, 10);
							st.playSound(QuestState.SOUND_FINISH);
							st.exitQuest(false);
						}
						break;
					
					case KARTIA:
						if (cond == 1)
						{
							htmltext = "30133-01.htm";
							st.set("cond", "2");
							st.playSound(QuestState.SOUND_MIDDLE);
							st.takeItems(ONYX_TALISMAN1, 1);
							st.giveItems(ONYX_TALISMAN2, 1);
						}
						else if (cond == 2)
							htmltext = "30133-02.htm";
						else if (cond == 3)
						{
							htmltext = "30133-03.htm";
							st.set("cond", "4");
							st.playSound(QuestState.SOUND_MIDDLE);
							st.takeItems(ONYX_TALISMAN2, 1);
							st.takeItems(ANCIENT_SCROLL, 1);
							st.takeItems(ANCIENT_CLAY_TABLET, 1);
							st.giveItems(KARTIAS_TRANSLATION, 1);
						}
						else if (cond == 4)
							htmltext = "30133-04.htm";
						break;
				}
				break;
			
			case STATE_COMPLETED:
				htmltext = Quest.getAlreadyCompletedMsg();
				break;
		}
		return htmltext;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance player, boolean isPet)
	{
		QuestState st = checkPlayerCondition(player, npc, "cond", "2");
		if (st == null)
			return null;
		
		if (st.dropItems(ANCIENT_SCROLL, 1, 1, 200000) && st.dropItems(ANCIENT_CLAY_TABLET, 1, 1, 200000))
			st.set("cond", "3");
		
		return null;
	}
	
	public static void main(String[] args)
	{
		new Q106_ForgottenTruth(106, qn, "Forgotten Truth");
	}
}