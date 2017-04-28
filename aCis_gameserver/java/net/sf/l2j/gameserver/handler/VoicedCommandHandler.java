package net.sf.l2j.gameserver.handler;

import java.util.HashMap;
import java.util.Map;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.handler.voicedcommandhandlers.Augment;
import net.sf.l2j.gameserver.handler.voicedcommandhandlers.Banking;
import net.sf.l2j.gameserver.handler.voicedcommandhandlers.BossInfo;
import net.sf.l2j.gameserver.handler.voicedcommandhandlers.Buff;
import net.sf.l2j.gameserver.handler.voicedcommandhandlers.CastleManagers;
import net.sf.l2j.gameserver.handler.voicedcommandhandlers.ChangePassword;
import net.sf.l2j.gameserver.handler.voicedcommandhandlers.ClanFull;
import net.sf.l2j.gameserver.handler.voicedcommandhandlers.EventsVoicedInfo;
import net.sf.l2j.gameserver.handler.voicedcommandhandlers.Menu;

public class VoicedCommandHandler
{
	private final Map<Integer, IVoicedCommandHandler> _datatable = new HashMap<>();
	
	public static VoicedCommandHandler getInstance()
	{
		return SingletonHolder._instance;
	}
	
	protected VoicedCommandHandler()
	{
		registerHandler(new Augment());
		if (Config.BANKING_SYSTEM_ENABLED)
			registerHandler(new Banking());
		registerHandler(new BossInfo());
		registerHandler(new Buff());
		registerHandler(new CastleManagers());
		registerHandler(new ChangePassword());
		registerHandler(new ClanFull());
		if (Config.EVENTS_ALLOW_VOICED_COMMAND)
			registerHandler(new EventsVoicedInfo());
		registerHandler(new Menu());
	}
	
	public void registerHandler(IVoicedCommandHandler handler)
	{
		String[] ids = handler.getVoicedCommandList();
		
		for (int i = 0; i < ids.length; i++)
			_datatable.put(ids[i].hashCode(), handler);
	}
	
	public IVoicedCommandHandler getHandler(String voicedCommand)
	{
		String command = voicedCommand;
		
		if (voicedCommand.indexOf(" ") != -1)
			command = voicedCommand.substring(0, voicedCommand.indexOf(" "));
		
		return _datatable.get(command.hashCode());
	}
	
	public int size()
	{
		return _datatable.size();
	}
	
	private static class SingletonHolder
	{
		protected static final VoicedCommandHandler _instance = new VoicedCommandHandler();
	}
}