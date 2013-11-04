package ai.individual;

import ai.AbstractNpcAI;
import net.xcine.gameserver.model.quest.Quest;

/*
 * @author m095 (L2EmuRT)
 */

public class Barakiel extends AbstractNpcAI
{
	// Barakiel NpcID
	private static final int BARAKIEL = 25325;
	
	public Barakiel(String name, String descr)
	{
		super(name, descr);

		addEventId(BARAKIEL, Quest.QuestEventType.ON_ATTACK);
	}
	public static void main(String[] args)
	{
		new Barakiel(Barakiel.class.getSimpleName(), "ai/individual");
	}
}
