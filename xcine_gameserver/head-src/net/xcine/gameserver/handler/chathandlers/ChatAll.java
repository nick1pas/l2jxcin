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
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package net.xcine.gameserver.handler.chathandlers;

import net.xcine.gameserver.handler.IChatHandler;
import net.xcine.gameserver.model.BlockList;
import net.xcine.gameserver.model.actor.instance.L2PcInstance;
import net.xcine.gameserver.network.serverpackets.CreatureSay;

/**
 * A chat handler
 * @author durgus
 */
public class ChatAll implements IChatHandler
{
	private static final int[] COMMAND_IDS =
	{
		0
	};
	
	/**
	 * Handle chat type 'all'
	 * @see net.xcine.gameserver.handler.IChatHandler#handleChat(int, net.xcine.gameserver.model.actor.instance.L2PcInstance, java.lang.String, java.lang.String)
	 */
	@Override
	public void handleChat(int type, L2PcInstance activeChar, String params, String text)
	{
		CreatureSay cs = new CreatureSay(activeChar.getObjectId(), type, activeChar.getName(), text);
		
		for (L2PcInstance player : activeChar.getKnownList().getKnownPlayers())
		{
			if (activeChar.isInsideRadius(player, 1250, false, true) && !BlockList.isBlocked(player, activeChar))
				player.sendPacket(cs);
		}
		
		activeChar.sendPacket(cs);
	}
	
	/**
	 * Returns the chat types registered to this handler
	 * @see net.xcine.gameserver.handler.IChatHandler#getChatTypeList()
	 */
	@Override
	public int[] getChatTypeList()
	{
		return COMMAND_IDS;
	}
}