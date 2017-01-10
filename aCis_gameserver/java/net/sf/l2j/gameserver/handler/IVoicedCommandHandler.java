package net.sf.l2j.gameserver.handler;

import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;

public interface IVoicedCommandHandler
{
    public boolean useVoicedCommand(String command, L2PcInstance activeChar, String params);

    public String[] getVoicedCommandList();
}