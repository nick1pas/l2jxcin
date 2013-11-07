package ai.individual;

import ai.L2AttackableAIScript;
import net.xcine.gameserver.model.quest.Quest;

/*
 * @author m095 (L2EmuRT)
 */

public class Kernon extends L2AttackableAIScript
{
	// Kernon NpcID
	private static final int KERNON = 25054;

	public Kernon(int questId, String name, String descr)
	{
		super(questId, name, descr);

		addEventId(KERNON, Quest.QuestEventType.ON_ATTACK);
	}
	public static void main(String[] args)
	{
		// now call the constructor (starts up the ai)
		new Kernon(-1, "kernon", "ai");
	}
}
