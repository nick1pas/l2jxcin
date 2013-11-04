package ai.individual;

import ai.AbstractNpcAI;
import net.xcine.gameserver.model.quest.Quest;

/*
 * @author m095 (L2EmuRT)
 */

public class Hallate extends AbstractNpcAI
{
	// Hallate NpcID
	private static final int HALLATE = 25220;
	
	public Hallate(String name, String descr)
	{
		super(name, descr);

		addEventId(HALLATE, Quest.QuestEventType.ON_ATTACK);
	}
	public static void main(String[] args)
	{
		new Hallate(Hallate.class.getSimpleName(), "ai/individual");
	}
}
