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
import net.sf.l2j.gameserver.model.entity.clanhallsiege.BanditStrongholdSiege;
import net.sf.l2j.gameserver.scripting.Quest;
import net.sf.l2j.gameserver.scripting.QuestState;

public class Q504_CompetitionfortheBanditStronghold extends Quest
{
	private static final String qn = "Q504_CompetitionfortheBanditStronghold";
	// npcId
	private static final int Messenger = 35437;
	// itemId list
	private static final int TarlkAmulet = 4332;
	private static final int AlianceTrophey = 5009;
	// Quest mobs
	private static final int TarlkBugbear = 20570;
	private static final int TarlkBugbearWarrior = 20571;
	private static final int TarlkBugbearHighWarrior = 20572;
	private static final int TarlkBasilisk = 20573;
	private static final int ElderTarlkBasilisk = 20574;
	
	public Q504_CompetitionfortheBanditStronghold()
	{
		super(504, "Competition for the Bandit Stronghold");
		
		addStartNpc(Messenger);
		addTalkId(Messenger);
		addKillId(TarlkBugbear, TarlkBugbearWarrior, TarlkBugbearHighWarrior, TarlkBasilisk, ElderTarlkBasilisk);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = event;
		QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("a2.htm"))
		{
			st.set("cond", "1");
			st.setState(STATE_STARTED);
			st.playSound(QuestState.SOUND_ACCEPT);
		}
		if (event.equalsIgnoreCase("a4.htm"))
		{
			if (st.getQuestItemsCount(TarlkAmulet) == 30)
			{
				st.takeItems(TarlkAmulet, -30);
				st.giveItems(AlianceTrophey, 1);
				st.playSound(QuestState.SOUND_FINISH);
				st.exitQuest(true);
			}
			else
				htmltext = "a5.htm";
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
		
		int cond = st.getInt("cond");
		if (player.getClan() == null || player.getClan().getLevel() < 4 || !(player.getClan().getLeaderName() == player.getName()))
		{
			htmltext = "a6.htm";
		}
		if (BanditStrongholdSiege.getInstance().isRegistrationPeriod())
		{
			if (npc.getNpcId() == Messenger)
			{
				if (cond == 0)
				{
					htmltext = "a1.htm";
				}
				else if (cond > 1)
				{
					htmltext = "a3.htm";
				}
			}
		}
		else
		{
			htmltext = null;
			npc.showChatWindow(player, 3);
		}
		return htmltext;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance player, boolean isPet)
	{
		QuestState st = checkPlayerState(player, npc, STATE_STARTED);
		if (st == null)
			return null;
		
		if (st.getQuestItemsCount(TarlkAmulet) < 30)
		{
			st.giveItems(TarlkAmulet, 1);
			st.playSound(QuestState.SOUND_ITEMGET);
		}
		if (st.getQuestItemsCount(TarlkAmulet) == 30)
		{
			st.set("cond", "2");
		}
		return null;
	}
	
}