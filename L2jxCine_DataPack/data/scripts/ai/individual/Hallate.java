package ai.individual;

import ai.L2AttackableAIScript;
import net.xcine.gameserver.model.quest.Quest;

/*
 * @author m095 (L2EmuRT)
 */

public class Hallate extends L2AttackableAIScript
{
	// Hallate NpcID
	private static final int HALLATE = 25220;
	
	public Hallate(int questId, String name, String descr)
	{
		super(questId, name, descr);

		addEventId(HALLATE, Quest.QuestEventType.ON_ATTACK);
	}
	public static void main(String[] args)
	{
		// now call the constructor (starts up the ai)
		new Hallate(-1,"hallate","ai");
	}
}
