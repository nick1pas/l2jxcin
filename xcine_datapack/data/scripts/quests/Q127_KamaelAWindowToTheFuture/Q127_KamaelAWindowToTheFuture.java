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
package quests.Q127_KamaelAWindowToTheFuture;

import net.xcine.gameserver.model.actor.L2Npc;
import net.xcine.gameserver.model.actor.instance.L2PcInstance;
import net.xcine.gameserver.model.quest.Quest;
import net.xcine.gameserver.model.quest.QuestState;
import net.xcine.gameserver.network.serverpackets.ExShowSlideshowKamael;

public class Q127_KamaelAWindowToTheFuture extends Quest
{
	private final static String qn = "Q127_KamaelAWindowToTheFuture";
	
	// Npcs
	private final static int DOMINIC = 31350;
	private final static int KLAUS = 30187;
	private final static int ALDER = 32092;
	private final static int AKLAN = 31288;
	private final static int OLTLIN = 30862;
	private final static int JURIS = 30113;
	private final static int RODEMAI = 30756;
	
	// Items
	private final static int MARK_DOMINIC = 8939;
	private final static int MARK_HUMAN = 8940;
	private final static int MARK_DWARF = 8941;
	private final static int MARK_ORC = 8944;
	private final static int MARK_DELF = 8943;
	private final static int MARK_ELF = 8942;
	
	public Q127_KamaelAWindowToTheFuture(int questId, String name, String descr)
	{
		super(questId, name, descr);
		
		questItemIds = new int[]
		{
			MARK_DOMINIC,
			MARK_HUMAN,
			MARK_DWARF,
			MARK_ORC,
			MARK_DELF,
			MARK_ELF
		};
		
		addStartNpc(DOMINIC);
		addTalkId(DOMINIC, KLAUS, ALDER, AKLAN, OLTLIN, JURIS, RODEMAI);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = event;
		QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("31350-04.htm"))
		{
			st.set("cond", "1");
			st.setState(STATE_STARTED);
			st.giveItems(MARK_DOMINIC, 1);
			st.playSound(QuestState.SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("31350-06.htm"))
		{
			st.takeItems(MARK_HUMAN, -1);
			st.takeItems(MARK_DWARF, -1);
			st.takeItems(MARK_ELF, -1);
			st.takeItems(MARK_DELF, -1);
			st.takeItems(MARK_ORC, -1);
			st.takeItems(MARK_DOMINIC, -1);
			st.rewardItems(57, 159100);
			st.playSound(QuestState.SOUND_FINISH);
			st.exitQuest(false);
		}
		else if (event.equalsIgnoreCase("30187-06.htm"))
			st.set("cond", "2");
		else if (event.equalsIgnoreCase("30187-08.htm"))
		{
			st.set("cond", "3");
			st.giveItems(MARK_HUMAN, 1);
			st.playSound(QuestState.SOUND_MIDDLE);
		}
		else if (event.equalsIgnoreCase("32092-05.htm"))
		{
			st.set("cond", "4");
			st.giveItems(MARK_DWARF, 1);
			st.playSound(QuestState.SOUND_MIDDLE);
		}
		else if (event.equalsIgnoreCase("31288-04.htm"))
		{
			st.set("cond", "5");
			st.giveItems(MARK_ORC, 1);
			st.playSound(QuestState.SOUND_MIDDLE);
		}
		else if (event.equalsIgnoreCase("30862-04.htm"))
		{
			st.set("cond", "6");
			st.giveItems(MARK_DELF, 1);
			st.playSound(QuestState.SOUND_MIDDLE);
		}
		else if (event.equalsIgnoreCase("30113-04.htm"))
		{
			st.set("cond", "7");
			st.giveItems(MARK_ELF, 1);
			st.playSound(QuestState.SOUND_MIDDLE);
		}
		else if (event.equalsIgnoreCase("kamaelstory"))
		{
			st.set("cond", "8");
			st.playSound(QuestState.SOUND_MIDDLE);
			player.sendPacket(ExShowSlideshowKamael.STATIC_PACKET);
			return null;
		}
		else if (event.equalsIgnoreCase("30756-05.htm"))
		{
			st.set("cond", "9");
			st.playSound(QuestState.SOUND_MIDDLE);
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
		
		npc.getNpcId();
		int cond = st.getInt("cond");
		
		switch (st.getState())
		{
			case STATE_CREATED:
				htmltext = "31350-01.htm";
				break;
			
			case STATE_STARTED:
				switch (npc.getNpcId())
				{
					case KLAUS:
						if (cond == 1)
							htmltext = "30187-01.htm";
						else if (cond == 2)
							htmltext = "30187-06.htm";
						break;
					
					case ALDER:
						if (cond == 3)
							htmltext = "32092-01.htm";
						break;
					
					case AKLAN:
						if (cond == 4)
							htmltext = "31288-01.htm";
						break;
					
					case OLTLIN:
						if (cond == 5)
							htmltext = "30862-01.htm";
						break;
					
					case JURIS:
						if (cond == 6)
							htmltext = "30113-01.htm";
						break;
					
					case RODEMAI:
						if (cond == 7)
							htmltext = "30756-01.htm";
						else if (cond == 8)
							htmltext = "30756-04.htm";
						break;
					
					case DOMINIC:
						if (cond == 9)
							htmltext = "31350-05.htm";
						break;
				}
				break;
			
			case STATE_COMPLETED:
				htmltext = getAlreadyCompletedMsg();
				return htmltext;
		}
		
		return htmltext;
	}
	
	public static void main(String[] args)
	{
		new Q127_KamaelAWindowToTheFuture(127, qn, "Kamael: A Window to the Future");
	}
}