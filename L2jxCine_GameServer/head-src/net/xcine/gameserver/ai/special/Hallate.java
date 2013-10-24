package net.xcine.gameserver.ai.special;

import net.xcine.gameserver.model.quest.Quest;

/*
 * @author m095 (L2EmuRT)
 */

public class Hallate extends Quest implements Runnable
{
	// Hallate NpcID
	private static final int HALLATE = 25220;
	
	public Hallate(int questId, String name, String descr)
	{
		super(questId, name, descr);

		addEventId(HALLATE, Quest.QuestEventType.ON_ATTACK);
	}

	@Override
	public void run()
	{}
}
