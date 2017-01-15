package net.sf.l2j.gameserver.handler;

import java.util.HashMap;
import java.util.Map;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.handler.voicedcommandhandlers.Augment;
import net.sf.l2j.gameserver.handler.voicedcommandhandlers.Banking;
import net.sf.l2j.gameserver.handler.voicedcommandhandlers.DMVoicedInfo;
import net.sf.l2j.gameserver.handler.voicedcommandhandlers.LMVoicedInfo;
import net.sf.l2j.gameserver.handler.voicedcommandhandlers.Menu;
import net.sf.l2j.gameserver.handler.voicedcommandhandlers.SymbolMaker;
import net.sf.l2j.gameserver.handler.voicedcommandhandlers.TvTVoicedInfo;

public class VoicedCommandHandler
{
	private final Map<Integer, IVoicedCommandHandler> _datatable = new HashMap<>();
	
	public static VoicedCommandHandler getInstance()
	{
		return SingletonHolder._instance;
	}
	
	protected VoicedCommandHandler()
	{
		registerHandler(new Menu());
		registerHandler(new Augment());
		registerHandler(new SymbolMaker());
		if (Config.TVT_ALLOW_VOICED_COMMAND)
			registerHandler(new TvTVoicedInfo());
		if (Config.DM_ALLOW_VOICED_COMMAND)
			registerHandler(new DMVoicedInfo());
		if (Config.LM_ALLOW_VOICED_COMMAND)
			registerHandler(new LMVoicedInfo());
		if (Config.BANKING_SYSTEM_ENABLED)
			registerHandler(new Banking());
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