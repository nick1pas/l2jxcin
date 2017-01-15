/*
* This program is free software: you can redistribute it and/or modify it under
* the terms of the GNU General Public License as published by the Free Software
* Foundation, either version 3 of the License, or (at your option) any later
* version.
*
* This program is distributed in the hope that it will be useful, but WITHOUT
* ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
* FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
* details.
*
* You should have received a copy of the GNU General Public License along with
* this program. If not, see .
*/
package net.sf.l2j.gameserver.handler.voicedcommandhandlers;

import net.sf.l2j.gameserver.datatables.HennaTable;
import net.sf.l2j.gameserver.handler.IVoicedCommandHandler;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.network.serverpackets.HennaEquipList;
import net.sf.l2j.gameserver.network.serverpackets.HennaRemoveList;

public class SymbolMaker implements IVoicedCommandHandler
{
	private static final String[] _voicedCommands =
	{
		"voiced_drawn",
		"voiced_removelist"
	};
	
	@Override
	public boolean useVoicedCommand(String command, L2PcInstance activeChar, String target)
	{
		if (command.equals("voiced_drawn"))
			activeChar.sendPacket(new HennaEquipList(activeChar, HennaTable.getInstance().getAvailableHenna(activeChar.getClassId().getId())));
		else if (command.equals("voiced_removelist"))
		{
			boolean hasHennas = false;
			for (int i = 1; i <= 3; i++)
			{
				if (activeChar.getHenna(i) != null)
					hasHennas = true;
			}
			
			if (hasHennas)
				activeChar.sendPacket(new HennaRemoveList(activeChar));
			else
				activeChar.sendPacket(SystemMessageId.SYMBOL_NOT_FOUND);
		}
		return true;
	}
	
	@Override
	public String[] getVoicedCommandList()
	{
		return _voicedCommands;
	}
}