package net.xcine.gameserver.ai.special;

import net.xcine.gameserver.model.quest.Quest;

/*
 * @author m095 (L2EmuRT)
 */

public class Kernon extends Quest implements Runnable
{
	// Kernon NpcID
	private static final int KERNON = 25054;

	public Kernon(int questId, String name, String descr)
	{
		super(questId, name, descr);

		addEventId(KERNON, Quest.QuestEventType.ON_ATTACK);
	}

	@Override
	public void run()
	{}
}
