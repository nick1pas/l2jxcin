package net.xcine.gameserver.ai.special;

import net.xcine.gameserver.model.quest.Quest;

/*
 * @author m095 (L2EmuRT)
 */

public class Barakiel extends Quest implements Runnable
{
	// Barakiel NpcID
	private static final int BARAKIEL = 25325;
	
	public Barakiel(int questId, String name, String descr)
	{
		super(questId, name, descr);

		addEventId(BARAKIEL, Quest.QuestEventType.ON_ATTACK);
	}

	@Override
	public void run()
	{}
}
