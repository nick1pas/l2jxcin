package net.sf.l2j.gameserver.handler.voicedcommandhandlers;

import net.sf.l2j.gameserver.handler.IVoicedCommandHandler;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.entity.events.LMEvent;

/**
 * @author L0ngh0rn
 *
 */
public class LMVoicedInfo implements IVoicedCommandHandler
{
	private static final String[] _voicedCommands = { "lmjoin", "lmleave" };
	
	@Override
	public boolean useVoicedCommand(String command, L2PcInstance activeChar, String target)
	{
		if (command.equalsIgnoreCase("lmjoin"))
		{
			LMEvent.onBypass("lm_event_participation", activeChar);
		}
		else if (command.equalsIgnoreCase("lmleave"))
		{
			LMEvent.onBypass("lm_event_remove_participation", activeChar);
		}
		return true;
	}
	
	@Override
	public String[] getVoicedCommandList()
	{
		return _voicedCommands;
	}
}
