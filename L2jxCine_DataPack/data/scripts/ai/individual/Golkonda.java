package ai.individual;

import ai.AbstractNpcAI;
import net.xcine.gameserver.model.quest.Quest;

/*
 * @author m095 (L2EmuRT)
 */

public class Golkonda extends AbstractNpcAI
{
	// Golkonda NpcID
	private static final int GOLKONDA = 25126;
	
	
	public Golkonda(String name, String descr)
	{
		super(name, descr);

		addEventId(GOLKONDA, Quest.QuestEventType.ON_ATTACK);
	}
	public static void main(String[] args)
	{
		new Golkonda(Golkonda.class.getSimpleName(), "ai/individual");
	}
}
