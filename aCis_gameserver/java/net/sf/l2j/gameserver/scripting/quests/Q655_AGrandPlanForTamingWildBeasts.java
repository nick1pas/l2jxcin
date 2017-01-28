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
package net.sf.l2j.gameserver.scripting.quests;

import net.sf.l2j.gameserver.model.actor.L2Npc;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.scripting.Quest;
import net.sf.l2j.gameserver.scripting.QuestState;

public class Q655_AGrandPlanForTamingWildBeasts extends Quest
{
	// npcId
	private static final int Messenger = 35627;
	
	// ItemId list
	private static final int CrystalPurity = 8084;
	private static final int License = 8293;
	
	public Q655_AGrandPlanForTamingWildBeasts()
	{
		super(655, "A Grand Plan For Taming Wild Beasts");
		
		addStartNpc(Messenger);
		addTalkId(Messenger);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = event;
		QuestState st = player.getQuestState(getName());
		if (st == null)
			return htmltext;
		
		switch (event)
		{
			case "35627-02.htm":
				st.set("cond", "1");
				st.setState(STATE_STARTED);
				st.playSound(QuestState.SOUND_ACCEPT);
				break;
			
			case "35627-04.htm":
				if (st.getQuestItemsCount(CrystalPurity) == 10)
				{
					st.takeItems(CrystalPurity, -10);
					st.giveItems(License, 1);
					st.set("cond", "3");
				}
				else
					htmltext = "35627-05.htm";
				break;
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		String htmltext = getNoQuestMsg();
		QuestState st = player.getQuestState(getName());
		if (st == null)
			return htmltext;
		
		switch (st.getState())
		{
			case STATE_CREATED:
				htmltext = ((player.getClan() == null || player.getClan().getLevel() < 4 || !(player.getClan().getLeaderName() == player.getName()))) ? "35627-06.htm" : "35627-01.htm";
				break;
			
			case STATE_STARTED:
				final int cond = st.getInt("cond");
				switch (npc.getNpcId())
				{
					case Messenger:						
						if (cond == 0)
							htmltext = "35627-01.htm";
						else if (cond > 1)
							htmltext = "35627-03.htm";
						else
							npc.showChatWindow(player, 3);
						break;
				}
				break;
		}
		return htmltext;
	}
}