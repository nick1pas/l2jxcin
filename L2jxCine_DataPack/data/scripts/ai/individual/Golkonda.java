package ai.individual;

import ai.L2AttackableAIScript;
import net.xcine.gameserver.model.quest.Quest;

/*
 * @author m095 (L2EmuRT)
 */

public class Golkonda extends L2AttackableAIScript
{
	// Golkonda NpcID
	private static final int GOLKONDA = 25126;
	
	
	public Golkonda(int questId, String name, String descr)
	{
		super(questId, name, descr);

		addEventId(GOLKONDA, Quest.QuestEventType.ON_ATTACK);
	}
	public static void main(String[] args)
	{
		// now call the constructor (starts up the ai)
		new Golkonda(-1, "golkonda", "ai");
	}
}
