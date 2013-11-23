package quests.Q024_InhabitantsOfTheForestOfTheDead;

import net.xcine.gameserver.model.actor.L2Npc;
import net.xcine.gameserver.model.actor.instance.L2PcInstance;
import net.xcine.gameserver.model.quest.Quest;
import net.xcine.gameserver.model.quest.QuestState;

public class Q024_InhabitantsOfTheForestOfTheDead extends Quest
{
	private final static String qn = "Q024_InhabitantsOfTheForrestOfTheDead";
	
	//Items
	private static final int Letter = 7065;
	private static final int Hairpin = 7148;
	private static final int Totem = 7151;
	private static final int Flower = 7152;
	private static final int SilverCross = 7153;
	private static final int BrokenSilverCross = 7154;
	private static final int SuspiciousTotem = 7156;
	
	//NPCs
	private static final int Dorian = 31389;
	private static final int Wizard = 31522;
	private static final int Tombstone = 31531;
	private static final int MaidOfLidia = 31532;
	
	
	public Q024_InhabitantsOfTheForestOfTheDead(int questId, String name, String descr)
	{
		super(questId, name, descr);
		questItemIds = new int[]
				{
				Letter,
				Hairpin,
				Totem,
				Flower,
				SilverCross,
				BrokenSilverCross,
				SuspiciousTotem
				};
		addStartNpc(Dorian);
		addTalkId(Dorian,Wizard,Tombstone,MaidOfLidia);
	}
	
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = event;
		QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;
		if (event.equalsIgnoreCase("31389-02.htm"))
		{
			st.giveItems(Flower,1);
			st.set("cond","1");
			st.playSound("ItemSound.quest_accept");
			st.setState(STATE_STARTED);
		}
		else if (event.equalsIgnoreCase("31389-11.htm"))
		{
			st.set("cond","3");
			st.playSound("ItemSound.quest_middle");
			st.giveItems(SilverCross,1);
		}
		else if (event.equalsIgnoreCase("31389-16.htm"))
		{
			st.playSound("InterfaceSound.charstat_open_01");
		}
		else if (event.equalsIgnoreCase("31389-17.htm"))
		{
			st.takeItems(BrokenSilverCross,-1);
			st.giveItems(Hairpin,1);
			st.set("cond","5");
		}
		else if (event.equalsIgnoreCase("31522-03.htm"))
		{
			st.takeItems(Totem,-1);
		}
		else if (event.equalsIgnoreCase("31522-07.htm"))
		{
			st.set("cond","11");
		}
		else if (event.equalsIgnoreCase("31522-19.htm"))
		{
			st.giveItems(SuspiciousTotem,1);
			st.rewardExpAndSp(242105,22529);
			st.exitQuest(false);
			st.playSound("ItemSound.quest_finish");
		}
		else if (event.equalsIgnoreCase("31531-02.htm"))
		{
			st.playSound("ItemSound.quest_middle");
			st.set("cond","2");
			st.takeItems(Flower,-1);
		}
		else if (event.equalsIgnoreCase("31532-04.htm"))
		{
			st.playSound("ItemSound.quest_middle");
			st.giveItems(Letter,1);
			st.set("cond","6");
		}
		else if (event.equalsIgnoreCase("31532-06.htm"))
		{
			st.takeItems(Hairpin,-1);
			st.takeItems(Letter,-1);
		}
		else if (event.equalsIgnoreCase("31532-16.htm"))
		{
			st.playSound("ItemSound.quest_middle");
			st.set("cond","9");
		}
		
		return htmltext;
	}
	
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		String htmltext = getNoQuestMsg();
		QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;
		switch (st.getState())
		{
		case STATE_CREATED: //0
			QuestState st2 = player.getQuestState("Q023_LidiasHeart");
			if (st2 != null && st2.isCompleted())
			{
				if (player.getLevel() >= 65)
					htmltext = "31389-01.htm";
				else
				{
					htmltext = "31389-00.htm";
					st.exitQuest(true);
				}
			}
				else
				{
					htmltext = "31389-00.htm";
					st.exitQuest(true);
				}
				break;
			
	case STATE_STARTED:
		int cond = st.getInt("cond");
		switch (npc.getNpcId())
		{
		case Dorian:
			if (cond == 1)
				htmltext = "31389-03.htm";
			else if (cond == 2)
				htmltext = "31389-04.htm";
			else if (cond == 3)
				htmltext = "31389-12.htm";
			else if (cond >= 4)
				htmltext = "31389-13.htm";
			else if (cond >= 4)
				htmltext = "31389-18.htm";
			break;
		case Tombstone:
			if (cond == 1)
			{
				st.playSound("AmdSound.d_wind_loot_02");
				htmltext = "31531-01.htm";
			}
			if (cond == 2)
			{
				htmltext = "31531-03.htm";
			}
		case MaidOfLidia:
			if (cond == 5)
				htmltext = "31532-01.htm";
			else if (cond ==6)
			{
				//if (st.getQuestItemsCount(Letter) && st.getQuestItemsCount(Hairpin))
				if (st.getQuestItemsCount(Letter) == st.getQuestItemsCount(Hairpin))	
					htmltext = "31532-05.htm";
				else
					htmltext = "31532-07.htm";
			}
			else if (cond ==9)
			{
				htmltext = "31532-16.htm";
			}
		case Wizard:
			if (cond==10)
				htmltext = "31522-01.htm";
			else if (cond == 11)
				htmltext = "31522-08.htm";
		}
		
		}
		return htmltext;
	}
	public String onKill(L2Npc npc, L2PcInstance player, boolean isPet)
	{
		QuestState st = checkPlayerCondition(player, npc, "cond", "1");
		if (st == null)
			return null;
		
		switch (npc.getNpcId())
		{
		case 21557:
		case 21558:
		case 21560:
		case 21563:
		case 21564:	
		case 21565:
		case 21566:
		case 21567:
			st.giveItems(Totem,1);
			st.set("cond","10");
			st.playSound("ItemSound.quest_middle");
			break;
		}
		
		return null;
	}
	public static void main(String[] args)
	{
		new Q024_InhabitantsOfTheForestOfTheDead(24, qn, "Inhabitants of the forrest of the dead");
	}
}