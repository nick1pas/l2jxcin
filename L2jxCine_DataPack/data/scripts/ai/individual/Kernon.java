package ai.individual;

import ai.AbstractNpcAI;
import net.xcine.gameserver.model.quest.Quest;

/*
 * @author m095 (L2EmuRT)
 */

public class Kernon extends AbstractNpcAI
{
	// Kernon NpcID
	private static final int KERNON = 25054;

	public Kernon(String name, String descr)
	{
		super(name, descr);

		addEventId(KERNON, Quest.QuestEventType.ON_ATTACK);
	}
	public static void main(String[] args)
	{
		new Kernon(Kernon.class.getSimpleName(), "ai/individual");
	}
}
