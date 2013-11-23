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
package quests.Q325_GrimCollector;

import net.xcine.gameserver.model.actor.L2Npc;
import net.xcine.gameserver.model.actor.instance.L2PcInstance;
import net.xcine.gameserver.model.quest.Quest;
import net.xcine.gameserver.model.quest.QuestState;
import net.xcine.util.Rnd;

public class Q325_GrimCollector extends Quest
{
	private static final String qn = "Q325_GrimCollector";
	
	// Items
	private static final int ANATOMY_DIAGRAM = 1349;
	private static final int ZOMBIE_HEAD = 1350;
	private static final int ZOMBIE_HEART = 1351;
	private static final int ZOMBIE_LIVER = 1352;
	private static final int SKULL = 1353;
	private static final int RIB_BONE = 1354;
	private static final int SPINE = 1355;
	private static final int ARM_BONE = 1356;
	private static final int THIGH_BONE = 1357;
	private static final int COMPLETE_SKELETON = 1358;
	
	// NPCs
	private static final int CURTIS = 30336;
	private static final int VARSAK = 30342;
	private static final int SAMED = 30434;
	
	public Q325_GrimCollector(int questId, String name, String descr)
	{
		super(questId, name, descr);
		
		questItemIds = new int[]
		{
			ZOMBIE_HEAD,
			ZOMBIE_HEART,
			ZOMBIE_LIVER,
			SKULL,
			RIB_BONE,
			SPINE,
			ARM_BONE,
			THIGH_BONE,
			COMPLETE_SKELETON,
			ANATOMY_DIAGRAM
		};
		
		addStartNpc(CURTIS);
		addTalkId(CURTIS, VARSAK, SAMED);
		
		addKillId(20026, 20029, 20035, 20042, 20045, 20457, 20458, 20051, 20514, 20515);
	}
	
	private int getNumberOfPieces(QuestState st)
	{
		return st.getQuestItemsCount(ZOMBIE_HEAD) + st.getQuestItemsCount(SPINE) + st.getQuestItemsCount(ARM_BONE) + st.getQuestItemsCount(ZOMBIE_HEART) + st.getQuestItemsCount(ZOMBIE_LIVER) + st.getQuestItemsCount(SKULL) + st.getQuestItemsCount(RIB_BONE) + st.getQuestItemsCount(THIGH_BONE) + st.getQuestItemsCount(COMPLETE_SKELETON);
	}
	
	private void payback(QuestState st)
	{
		int count = getNumberOfPieces(st);
		if (count > 0)
		{
			int reward = 30 * st.getQuestItemsCount(ZOMBIE_HEAD) + 20 * st.getQuestItemsCount(ZOMBIE_HEART) + 20 * st.getQuestItemsCount(ZOMBIE_LIVER) + 100 * st.getQuestItemsCount(SKULL) + 40 * st.getQuestItemsCount(RIB_BONE) + 14 * st.getQuestItemsCount(SPINE) + 14 * st.getQuestItemsCount(ARM_BONE) + 14 * st.getQuestItemsCount(THIGH_BONE) + 341 * st.getQuestItemsCount(COMPLETE_SKELETON);
			if (count > 10)
				reward += 1629;
			
			if (st.getQuestItemsCount(COMPLETE_SKELETON) > 0)
				reward += 543;
			
			st.takeItems(ZOMBIE_HEAD, -1);
			st.takeItems(ZOMBIE_HEART, -1);
			st.takeItems(ZOMBIE_LIVER, -1);
			st.takeItems(SKULL, -1);
			st.takeItems(RIB_BONE, -1);
			st.takeItems(SPINE, -1);
			st.takeItems(ARM_BONE, -1);
			st.takeItems(THIGH_BONE, -1);
			st.takeItems(COMPLETE_SKELETON, -1);
			
			st.rewardItems(57, reward);
		}
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = event;
		QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("30336-03.htm"))
		{
			st.set("cond", "1");
			st.setState(STATE_STARTED);
			st.playSound(QuestState.SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("30434-03.htm"))
		{
			st.giveItems(ANATOMY_DIAGRAM, 1);
			st.playSound(QuestState.SOUND_ITEMGET);
		}
		else if (event.equalsIgnoreCase("30434-06.htm"))
		{
			st.takeItems(ANATOMY_DIAGRAM, -1);
			payback(st);
			st.playSound(QuestState.SOUND_FINISH);
			st.exitQuest(true);
		}
		else if (event.equalsIgnoreCase("30434-07.htm"))
		{
			payback(st);
			st.playSound(QuestState.SOUND_MIDDLE);
		}
		else if (event.equalsIgnoreCase("30434-09.htm"))
		{
			int skeletons = st.getQuestItemsCount(COMPLETE_SKELETON);
			if (skeletons > 0)
			{
				st.takeItems(COMPLETE_SKELETON, -1);
				st.playSound(QuestState.SOUND_MIDDLE);
				st.rewardItems(57, 543 + 341 * skeletons);
			}
		}
		else if (event.equalsIgnoreCase("30342-03.htm"))
		{
			if (st.getQuestItemsCount(SPINE) > 0 && st.getQuestItemsCount(ARM_BONE) > 0 && st.getQuestItemsCount(SKULL) > 0 && st.getQuestItemsCount(RIB_BONE) > 0 && st.getQuestItemsCount(THIGH_BONE) > 0)
			{
				st.takeItems(SPINE, 1);
				st.takeItems(SKULL, 1);
				st.takeItems(ARM_BONE, 1);
				st.takeItems(RIB_BONE, 1);
				st.takeItems(THIGH_BONE, 1);
				
				if (Rnd.get(10) < 9)
				{
					st.giveItems(COMPLETE_SKELETON, 1);
					st.playSound(QuestState.SOUND_ITEMGET);
				}
				else
					htmltext = "30342-04.htm";
			}
			else
				htmltext = "30342-02.htm";
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
				if (player.getLevel() >= 15)
					htmltext = "30336-02.htm";
				else
				{
					htmltext = "30336-01.htm";
					st.exitQuest(true);
				}
				break;
			
			case STATE_STARTED:
				switch (npc.getNpcId())
				{
					case CURTIS:
						htmltext = (st.getQuestItemsCount(ANATOMY_DIAGRAM) < 1) ? "30336-04.htm" : "30336-05.htm";
						break;
					
					case SAMED:
						if (st.getQuestItemsCount(ANATOMY_DIAGRAM) == 0)
							htmltext = "30434-01.htm";
						else
						{
							if (getNumberOfPieces(st) == 0)
								htmltext = "30434-04.htm";
							else
								htmltext = (st.getQuestItemsCount(COMPLETE_SKELETON) == 0) ? "30434-05.htm" : "30434-08.htm";
						}
						break;
					
					case VARSAK:
						htmltext = "30342-01.htm";
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
		
		if (st.hasQuestItems(ANATOMY_DIAGRAM))
		{
			int chance = Rnd.get(100);
			switch (npc.getNpcId())
			{
				case 20026:
					if (chance <= 90)
					{
						st.playSound(QuestState.SOUND_ITEMGET);
						if (chance <= 40)
							st.giveItems(ZOMBIE_HEAD, 1);
						else if (chance <= 60)
							st.giveItems(ZOMBIE_HEART, 1);
						else
							st.giveItems(ZOMBIE_LIVER, 1);
					}
					break;
				
				case 20029:
					st.playSound(QuestState.SOUND_ITEMGET);
					if (chance <= 44)
						st.giveItems(ZOMBIE_HEAD, 1);
					else if (chance <= 66)
						st.giveItems(ZOMBIE_HEART, 1);
					else
						st.giveItems(ZOMBIE_LIVER, 1);
					break;
				
				case 20035:
					if (chance <= 79)
					{
						st.playSound(QuestState.SOUND_ITEMGET);
						if (chance <= 5)
							st.giveItems(SKULL, 1);
						else if (chance <= 15)
							st.giveItems(RIB_BONE, 1);
						else if (chance <= 29)
							st.giveItems(SPINE, 1);
						else
							st.giveItems(THIGH_BONE, 1);
					}
					break;
				
				case 20042:
					if (chance <= 86)
					{
						st.playSound(QuestState.SOUND_ITEMGET);
						if (chance <= 6)
							st.giveItems(SKULL, 1);
						else if (chance <= 19)
							st.giveItems(RIB_BONE, 1);
						else if (chance <= 69)
							st.giveItems(ARM_BONE, 1);
						else
							st.giveItems(THIGH_BONE, 1);
					}
					break;
				
				case 20045:
					if (chance <= 97)
					{
						st.playSound(QuestState.SOUND_ITEMGET);
						if (chance <= 9)
							st.giveItems(SKULL, 1);
						else if (chance <= 59)
							st.giveItems(SPINE, 1);
						else if (chance <= 77)
							st.giveItems(ARM_BONE, 1);
						else
							st.giveItems(THIGH_BONE, 1);
					}
					break;
				
				case 20051:
					if (chance <= 99)
					{
						st.playSound(QuestState.SOUND_ITEMGET);
						if (chance <= 9)
							st.giveItems(SKULL, 1);
						else if (chance <= 59)
							st.giveItems(RIB_BONE, 1);
						else if (chance <= 79)
							st.giveItems(SPINE, 1);
						else
							st.giveItems(ARM_BONE, 1);
					}
					break;
				
				case 20514:
					if (chance <= 51)
					{
						st.playSound(QuestState.SOUND_ITEMGET);
						if (chance <= 2)
							st.giveItems(SKULL, 1);
						else if (chance <= 8)
							st.giveItems(RIB_BONE, 1);
						else if (chance <= 17)
							st.giveItems(SPINE, 1);
						else if (chance <= 18)
							st.giveItems(ARM_BONE, 1);
						else
							st.giveItems(THIGH_BONE, 1);
					}
					break;
				
				case 20515:
					if (chance <= 60)
					{
						st.playSound(QuestState.SOUND_ITEMGET);
						if (chance <= 3)
							st.giveItems(SKULL, 1);
						else if (chance <= 11)
							st.giveItems(RIB_BONE, 1);
						else if (chance <= 22)
							st.giveItems(SPINE, 1);
						else if (chance <= 24)
							st.giveItems(ARM_BONE, 1);
						else
							st.giveItems(THIGH_BONE, 1);
					}
					break;
				
				case 20457:
				case 20458:
					st.playSound(QuestState.SOUND_ITEMGET);
					if (chance <= 42)
						st.giveItems(ZOMBIE_HEAD, 1);
					else if (chance <= 67)
						st.giveItems(ZOMBIE_HEART, 1);
					else
						st.giveItems(ZOMBIE_LIVER, 1);
					break;
			}
		}
		
		return null;
	}
	
	public static void main(String[] args)
	{
		new Q325_GrimCollector(325, qn, "Grim Collector");
	}
}