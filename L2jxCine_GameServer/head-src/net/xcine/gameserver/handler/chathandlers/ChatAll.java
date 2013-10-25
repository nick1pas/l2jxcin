/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */
package net.xcine.gameserver.handler.chathandlers;

import java.util.StringTokenizer;

import net.xcine.gameserver.handler.IChatHandler;
import net.xcine.gameserver.handler.IVoicedCommandHandler;
import net.xcine.gameserver.handler.VoicedCommandHandler;
import net.xcine.gameserver.model.actor.instance.L2PcInstance;
import net.xcine.gameserver.network.serverpackets.CreatureSay;

public class ChatAll implements IChatHandler
{
	private static final int[] COMMAND_IDS =
	{
		0
	};
	
	@Override
	public void handleChat(int type, L2PcInstance activeChar, String target, String text)
	{
		if(text.startsWith("."))
		{
			StringTokenizer st = new StringTokenizer(text);
			IVoicedCommandHandler vch;
			String command = "";

			if(st.countTokens() > 1)
			{
				command = st.nextToken().substring(1);
				target = text.substring(command.length() + 2);
				vch = VoicedCommandHandler.getInstance().getVoicedCommandHandler(command);
			}
			else
			{
				command = text.substring(1);
				vch = VoicedCommandHandler.getInstance().getVoicedCommandHandler(command);
			}

			if(vch != null)
			{
				vch.useVoicedCommand(command, activeChar, target);
			}
		}
		else
		{
			CreatureSay cs = new CreatureSay(activeChar.getObjectId(), type, activeChar.getName(), text);

			for(L2PcInstance player : activeChar.getKnownList().getKnownTypeInRadius(L2PcInstance.class, 1250))
			{
				if(player != null && activeChar.isInsideRadius(player, 1250, false, true))
				{
					player.sendPacket(cs);
				}
			}

			activeChar.sendPacket(cs);
		}
	}

	@Override
	public int[] getChatTypeList()
	{
		return COMMAND_IDS;
	}
}