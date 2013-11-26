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
package quests.Q403_PathToARogue;

import net.xcine.gameserver.model.actor.L2Npc;
import net.xcine.gameserver.model.actor.instance.L2PcInstance;
import net.xcine.gameserver.model.base.ClassId;
import net.xcine.gameserver.model.itemcontainer.Inventory;
import net.xcine.gameserver.model.quest.Quest;
import net.xcine.gameserver.model.quest.QuestState;
import net.xcine.gameserver.network.serverpackets.SocialAction;

public class Q403_PathToARogue extends Quest
{
	private static final String qn = "Q403_PathToARogue";
	
	// Items
	private static final int Letter = 1180;
	private static final int Bones = 1183;
	private static final int Horseshoe = 1184;
	private static final int Bill = 1185;
	private static final int StolenJewelry = 1186;
	private static final int StolenTomes = 1187;
	private static final int StolenRing = 1188;
	private static final int StolenNecklace = 1189;
	private static final int Recommendation = 1190;
	private static final int NetisBow = 1181;
	private static final int NetisDagger = 1182;
	
	// NPCs
	private static final int Bezique = 30379;
	private static final int Neti = 30425;
	
	public Q403_PathToARogue(int questId, String name, String descr)
	{
		super(questId, name, descr);
		
		questItemIds = new int[]
		{
			Letter,
			Bones,
			Horseshoe,
			Bill,
			StolenJewelry,
			StolenTomes,
			StolenRing,
			StolenNecklace,
			NetisBow,
			NetisDagger
		};
		
		addStartNpc(Bezique);
		addTalkId(Bezique, Neti);
		
		addKillId(20035, 20042, 20045, 20051, 20054, 20060, 27038);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = event;
		QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("30379-05.htm"))
		{
			if (player.getClassId() != ClassId.fighter)
			{
				if (player.getClassId() == ClassId.rogue)
					htmltext = "30379-02a.htm";
				else
					htmltext = "30379-02.htm";
				
				st.exitQuest(true);
			}
			else if (player.getLevel() < 19)
			{
				htmltext = "30379-02.htm";
				st.exitQuest(true);
			}
			else if (st.hasQuestItems(Recommendation))
			{
				htmltext = "30379-04.htm";
				st.exitQuest(true);
			}
		}
		else if (event.equalsIgnoreCase("30379-06.htm"))
		{
			st.setState(STATE_STARTED);
			st.set("cond", "1");
			st.giveItems(Letter, 1);
			st.playSound(QuestState.SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("30425-05.htm"))
		{
			st.set("cond", "2");
			st.giveItems(NetisBow, 1);
			st.giveItems(NetisDagger, 1);
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
		
		switch (st.getState())
		{
			case STATE_CREATED:
				htmltext = "30379-01.htm";
				break;
			
			case STATE_STARTED:
				int cond = st.getInt("cond");
				switch (npc.getNpcId())
				{
					case Bezique:
						if (cond == 1)
							htmltext = "30379-07.htm";
						else if (cond == 2 || cond == 3)
							htmltext = "30379-10.htm";
						else if (cond == 4)
						{
							st.set("cond", "5");
							htmltext = "30379-08.htm";
							st.takeItems(Horseshoe, 1);
							st.giveItems(Bill, 1);
							st.playSound(QuestState.SOUND_MIDDLE);
						}
						else if (cond == 5)
							htmltext = "30379-11.htm";
						else if (cond == 6)
						{
							htmltext = "30379-09.htm";
							st.takeItems(StolenRing, 1);
							st.takeItems(StolenNecklace, 1);
							st.takeItems(StolenTomes, 1);
							st.takeItems(StolenJewelry, 1);
							st.takeItems(NetisBow, 1);
							st.takeItems(NetisDagger, 1);
							st.giveItems(Recommendation, 1);
							st.rewardExpAndSp(3200, 1500);
							player.broadcastPacket(new SocialAction(player, 3));
							st.playSound(QuestState.SOUND_FINISH);
							st.exitQuest(true);
						}
						break;
					
					case Neti:
						if (cond == 1)
							htmltext = "30425-01.htm";
						else if (cond == 2)
							htmltext = "30425-06.htm";
						else if (cond == 3)
						{
							st.set("cond", "4");
							htmltext = "30425-07.htm";
							st.playSound(QuestState.SOUND_MIDDLE);
							st.takeItems(Bones, 10);
							st.giveItems(Horseshoe, 1);
						}
						else if (cond >= 4)
							htmltext = "30425-08.htm";
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
			case 20035:
			case 20042:
			case 20045:
			case 20051:
			case 20054:
			case 20060:
				if (st.getInt("cond") == 2 && (st.getItemEquipped(Inventory.PAPERDOLL_RHAND) == NetisBow || st.getItemEquipped(Inventory.PAPERDOLL_RHAND) == NetisDagger))
					if (st.dropItemsAlways(Bones, 1, 10))
						st.set("cond", "3");
				break;
			
			case 27038:
				if (st.getInt("cond") == 5 && (st.getItemEquipped(Inventory.PAPERDOLL_RHAND) == NetisBow || st.getItemEquipped(Inventory.PAPERDOLL_RHAND) == NetisDagger))
				{
					if (st.getQuestItemsCount(StolenRing) == 0)
					{
						st.giveItems(StolenRing, 1);
						st.playSound(QuestState.SOUND_ITEMGET);
					}
					else if (st.getQuestItemsCount(StolenRing) == 1 && st.getQuestItemsCount(StolenNecklace) == 0)
					{
						st.giveItems(StolenNecklace, 1);
						st.playSound(QuestState.SOUND_ITEMGET);
					}
					else if (st.getQuestItemsCount(StolenRing) == 1 && st.getQuestItemsCount(StolenNecklace) == 1 && st.getQuestItemsCount(StolenTomes) == 0)
					{
						st.giveItems(StolenTomes, 1);
						st.playSound(QuestState.SOUND_ITEMGET);
					}
					else if (st.getQuestItemsCount(StolenRing) == 1 && st.getQuestItemsCount(StolenNecklace) == 1 && st.getQuestItemsCount(StolenTomes) == 1 && st.getQuestItemsCount(StolenJewelry) == 0)
					{
						st.set("cond", "6");
						st.giveItems(StolenJewelry, 1);
						st.playSound(QuestState.SOUND_MIDDLE);
					}
				}
				break;
		}
		
		return null;
	}
	
	public static void main(String[] args)
	{
		new Q403_PathToARogue(403, qn, "Path to a Rogue");
	}
}