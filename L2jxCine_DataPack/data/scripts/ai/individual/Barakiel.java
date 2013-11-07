package ai.individual;

import ai.L2AttackableAIScript;
import net.xcine.gameserver.model.quest.Quest;

/*
 * @author m095 (L2EmuRT)
 */

public class Barakiel extends L2AttackableAIScript
{
	// Barakiel NpcID
	private static final int BARAKIEL = 25325;
	
	public Barakiel(int questId, String name, String descr)
	{
		super(questId, name, descr);

		addEventId(BARAKIEL, Quest.QuestEventType.ON_ATTACK);
	}
	public static void main(String[] args)
	{
		// now call the constructor (starts up the ai)
		new Barakiel(-1, "barakiel", "ai");
	}
}
