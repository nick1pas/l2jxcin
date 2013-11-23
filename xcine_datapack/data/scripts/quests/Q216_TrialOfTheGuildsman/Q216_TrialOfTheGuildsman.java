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
package quests.Q216_TrialOfTheGuildsman;

import net.xcine.gameserver.model.actor.L2Npc;
import net.xcine.gameserver.model.actor.instance.L2PcInstance;
import net.xcine.gameserver.model.base.ClassId;
import net.xcine.gameserver.model.quest.Quest;
import net.xcine.gameserver.model.quest.QuestState;
import net.xcine.util.Rnd;

public class Q216_TrialOfTheGuildsman extends Quest
{
	private static final String qn = "Q216_TrialOfTheGuildsman";
	
	// Items
	private static final int Adena = 57;
	private static final int RecipeJourneymanRing = 3024;
	private static final int RecipeAmberBead = 3025;
	private static final int MarkOfGuildsman = 3119;
	private static final int ValkonsRecommendation = 3120;
	private static final int MandragorasBerry = 3121;
	private static final int AltransInstructions = 3122;
	private static final int AltransRecommendation1 = 3123;
	private static final int AltransRecommendation2 = 3124;
	private static final int NormansInstructions = 3125;
	private static final int NormansReceipt = 3126;
	private static final int DuningsInstructions = 3127;
	private static final int DuningsKey = 3128;
	private static final int NormansList = 3129;
	private static final int GrayBonePowder = 3130;
	private static final int GraniteWhetstone = 3131;
	private static final int RedPigment = 3132;
	private static final int BraidedYarn = 3133;
	private static final int JourneymanGem = 3134;
	private static final int PintersInstructions = 3135;
	private static final int AmberBead = 3136;
	private static final int AmberLump = 3137;
	private static final int JourneymanDecoBeads = 3138;
	private static final int JourneymanRing = 3139;
	
	// NPCs
	private static final int Valkon = 30103;
	private static final int Norman = 30210;
	private static final int Altran = 30283;
	private static final int Pinter = 30298;
	private static final int Duning = 30688;
	
	// Monsters
	private static final int Ant = 20079;
	private static final int AntCaptain = 20080;
	private static final int GraniteGolem = 20083;
	private static final int MandragoraSprout = 20154;
	private static final int MandragoraSapling = 20155;
	private static final int MandragoraBlossom = 20156;
	private static final int Silenos = 20168;
	private static final int Strain = 20200;
	private static final int Ghoul = 20201;
	private static final int DeadSeeker = 20202;
	private static final int BrekaOrcShaman = 20269;
	private static final int BrekaOrcOverlord = 20270;
	private static final int BrekaOrcWarrior = 20271;
	
	public Q216_TrialOfTheGuildsman(int questId, String name, String descr)
	{
		super(questId, name, descr);
		
		questItemIds = new int[]
		{
			RecipeJourneymanRing,
			RecipeAmberBead,
			ValkonsRecommendation,
			MandragorasBerry,
			AltransInstructions,
			AltransRecommendation1,
			AltransRecommendation2,
			NormansInstructions,
			NormansReceipt,
			DuningsInstructions,
			DuningsKey,
			NormansList,
			GrayBonePowder,
			GraniteWhetstone,
			RedPigment,
			BraidedYarn,
			JourneymanGem,
			PintersInstructions,
			AmberBead,
			AmberLump,
			JourneymanDecoBeads,
			JourneymanRing
		};
		
		addStartNpc(Valkon);
		addTalkId(Valkon, Norman, Altran, Pinter, Duning);
		
		addKillId(Ant, AntCaptain, GraniteGolem, MandragoraSprout, MandragoraSapling, MandragoraBlossom, Silenos, Strain, Ghoul, DeadSeeker, BrekaOrcShaman, BrekaOrcOverlord, BrekaOrcWarrior);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = event;
		
		QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("30103-06.htm"))
		{
			if (st.getQuestItemsCount(Adena) >= 2000)
			{
				st.set("cond", "1");
				st.playSound(QuestState.SOUND_ACCEPT);
				st.setState(STATE_STARTED);
				st.takeItems(Adena, 2000);
				st.giveItems(ValkonsRecommendation, 1);
				st.giveItems(7562, 85);
			}
			else
				htmltext = "30103-05a.htm";
		}
		else if (event.equalsIgnoreCase("30103-06c.htm") || event.equalsIgnoreCase("30103-07c.htm"))
		{
			if (st.getInt("cond") < 3)
			{
				st.set("cond", "3");
				st.playSound(QuestState.SOUND_MIDDLE);
			}
		}
		else if (event.equalsIgnoreCase("30103-09a.htm") || event.equalsIgnoreCase("30103-09b.htm"))
		{
			st.takeItems(AltransInstructions, -1);
			st.takeItems(JourneymanRing, -1);
			st.giveItems(MarkOfGuildsman, 1);
			st.rewardExpAndSp(80993, 12250);
			st.playSound(QuestState.SOUND_FINISH);
			st.exitQuest(true);
		}
		else if (event.equalsIgnoreCase("30210-04.htm"))
		{
			st.takeItems(AltransRecommendation1, -1);
			st.giveItems(NormansInstructions, 1);
			st.giveItems(NormansReceipt, 1);
		}
		else if (event.equalsIgnoreCase("30210-10.htm"))
			st.giveItems(NormansList, 1);
		else if (event.equalsIgnoreCase("30283-03.htm"))
		{
			st.set("cond", "5");
			st.playSound(QuestState.SOUND_MIDDLE);
			st.takeItems(MandragorasBerry, -1);
			st.takeItems(ValkonsRecommendation, -1);
			st.giveItems(AltransInstructions, 1);
			st.giveItems(AltransRecommendation1, 1);
			st.giveItems(AltransRecommendation2, 1);
			st.giveItems(RecipeJourneymanRing, 1);
		}
		else if (event.equalsIgnoreCase("30298-04.htm"))
		{
			st.takeItems(AltransRecommendation2, -1);
			st.giveItems(PintersInstructions, 1);
			
			// Artisan receives a recipe to craft Amber Beads, while spoiler case is handled in onKill section.
			if (player.getClassId() == ClassId.artisan)
			{
				htmltext = "30298-05.htm";
				st.giveItems(RecipeAmberBead, 1);
			}
		}
		else if (event.equalsIgnoreCase("30688-02.htm"))
		{
			st.takeItems(NormansReceipt, -1);
			st.giveItems(DuningsInstructions, 1);
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
				if (st.hasQuestItems(MarkOfGuildsman))
					htmltext = getAlreadyCompletedMsg();
				else if (player.getClassId() != ClassId.scavenger && player.getClassId() != ClassId.artisan)
					htmltext = "30103-01.htm";
				else if (player.getLevel() < 35)
					htmltext = "30103-02.htm";
				else
					htmltext = "30103-03.htm";
				break;
			
			case STATE_STARTED:
				int cond = st.getInt("cond");
				switch (npc.getNpcId())
				{
					case Valkon:
						if (cond == 1)
							htmltext = "30103-06c.htm";
						else if (cond < 5)
							htmltext = "30103-07.htm";
						else if (cond == 5)
							htmltext = "30103-08.htm";
						else if (cond == 6)
							htmltext = (st.getQuestItemsCount(JourneymanRing) == 7) ? "30103-09.htm" : "30103-08.htm";
						break;
					
					case Altran:
						if (cond < 4)
						{
							htmltext = "30283-01.htm";
							if (cond == 1)
							{
								st.set("cond", "2");
								st.playSound(QuestState.SOUND_MIDDLE);
							}
						}
						else if (cond == 4)
							htmltext = "30283-02.htm";
						else if (cond > 4)
							htmltext = "30283-04.htm";
						break;
					
					case Norman:
						if (cond == 5)
						{
							if (st.hasQuestItems(AltransRecommendation1))
								htmltext = "30210-01.htm";
							else if (st.hasQuestItems(NormansReceipt))
								htmltext = "30210-05.htm";
							else if (st.hasQuestItems(DuningsInstructions))
								htmltext = "30210-06.htm";
							else if (st.getQuestItemsCount(DuningsKey) == 30)
							{
								htmltext = "30210-07.htm";
								st.takeItems(DuningsKey, -1);
							}
							else if (st.hasQuestItems(NormansList))
							{
								if (st.getQuestItemsCount(GrayBonePowder) == 70 && st.getQuestItemsCount(GraniteWhetstone) == 70 && st.getQuestItemsCount(RedPigment) == 70 && st.getQuestItemsCount(BraidedYarn) == 70)
								{
									htmltext = "30210-12.htm";
									st.takeItems(NormansInstructions, -1);
									st.takeItems(NormansList, -1);
									st.takeItems(BraidedYarn, -1);
									st.takeItems(GraniteWhetstone, -1);
									st.takeItems(GrayBonePowder, -1);
									st.takeItems(RedPigment, -1);
									st.giveItems(JourneymanGem, 7);
									
									if (st.getQuestItemsCount(JourneymanDecoBeads) == 7)
									{
										st.set("cond", "6");
										st.playSound(QuestState.SOUND_MIDDLE);
									}
								}
								else
									htmltext = "30210-11.htm";
							}
						}
						break;
					
					case Duning:
						if (cond == 5)
						{
							if (st.hasQuestItems(NormansReceipt))
								htmltext = "30688-01.htm";
							else if (st.hasQuestItems(DuningsInstructions))
							{
								if (st.getQuestItemsCount(DuningsKey) < 30)
									htmltext = "30688-03.htm";
								else
								{
									htmltext = "30688-04.htm";
									st.takeItems(DuningsInstructions, -1);
								}
							}
							else
								htmltext = "30688-05.htm";
						}
						break;
					
					case Pinter:
						if (cond == 5)
						{
							if (st.hasQuestItems(AltransRecommendation2))
								htmltext = (player.getLevel() < 36) ? "30298-01.htm" : "30298-02.htm";
							else if (st.hasQuestItems(PintersInstructions))
							{
								if (st.getQuestItemsCount(AmberBead) < 70)
									htmltext = "30298-06.htm";
								else
								{
									htmltext = "30298-07.htm";
									st.takeItems(AmberBead, -1);
									st.takeItems(PintersInstructions, -1);
									st.giveItems(JourneymanDecoBeads, 7);
									
									if (st.getQuestItemsCount(JourneymanGem) == 7)
									{
										st.set("cond", "6");
										st.playSound(QuestState.SOUND_MIDDLE);
									}
								}
							}
						}
						else if (st.hasQuestItems(JourneymanDecoBeads))
							htmltext = "30298-08.htm";
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
			case MandragoraSprout:
			case MandragoraSapling:
			case MandragoraBlossom:
				if (st.getInt("cond") == 3 && st.dropItemsAlways(MandragorasBerry, 1, 1))
					st.set("cond", "4");
				break;
			
			case BrekaOrcWarrior:
			case BrekaOrcOverlord:
			case BrekaOrcShaman:
				if (st.hasQuestItems(DuningsInstructions))
					st.dropItemsAlways(DuningsKey, 1, 30);
				break;
			
			case Ghoul:
			case Strain:
				if (st.hasQuestItems(NormansList))
					st.dropItemsAlways(GrayBonePowder, 5, 70);
				break;
			
			case GraniteGolem:
				if (st.hasQuestItems(NormansList))
					st.dropItemsAlways(GraniteWhetstone, 7, 70);
				break;
			
			case DeadSeeker:
				if (st.hasQuestItems(NormansList))
					st.dropItemsAlways(RedPigment, 7, 70);
				break;
			
			case Silenos:
				if (st.hasQuestItems(NormansList))
					st.dropItemsAlways(BraidedYarn, 10, 70);
				break;
			
			case Ant:
			case AntCaptain:
				if (st.hasQuestItems(PintersInstructions))
				{
					// Different cases if player is a wannabe BH or WS.
					if (st.dropItemsAlways(AmberBead, (player.getClassId() == ClassId.scavenger && npc.getIsSpoiledBy() == player.getObjectId()) ? 10 : 5, 70))
						if (player.getClassId() == ClassId.artisan && Rnd.nextBoolean())
							st.giveItems(AmberLump, 1);
				}
				break;
		}
		
		return null;
	}
	
	public static void main(String[] args)
	{
		new Q216_TrialOfTheGuildsman(216, qn, "Trial of the Guildsman");
	}
}
