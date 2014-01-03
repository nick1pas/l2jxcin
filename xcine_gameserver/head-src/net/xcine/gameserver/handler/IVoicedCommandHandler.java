package net.xcine.gameserver.handler;

import java.util.logging.Logger;

import net.xcine.gameserver.model.actor.instance.L2PcInstance;

public interface IVoicedCommandHandler
{
	public static Logger _log = Logger.getLogger(IVoicedCommandHandler.class.getName());

	public boolean useVoicedCommand(String command, L2PcInstance activeChar, String params);

	public String[] getVoicedCommandList();
}