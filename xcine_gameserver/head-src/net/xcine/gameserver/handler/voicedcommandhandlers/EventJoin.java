package net.xcine.gameserver.handler.voicedcommandhandlers;

import net.xcine.gameserver.event.EventManager;
import net.xcine.gameserver.handler.IVoicedCommandHandler;
import net.xcine.gameserver.model.actor.instance.L2PcInstance;

public class EventJoin implements IVoicedCommandHandler
{
	private static final String[] _voicedCommands = { "register","unregister" };
	
	@Override
	public boolean useVoicedCommand(String command, L2PcInstance activeChar, String target)
	{
		if (command.equalsIgnoreCase("register"))
			EventManager.getInstance().registerPlayer(activeChar);
		else if (command.equalsIgnoreCase("unregister"))
			EventManager.getInstance().unregisterPlayer(activeChar);
		return true;
	}
	
	@Override
	public String[] getVoicedCommandList()
	{
		return _voicedCommands;
	}
}