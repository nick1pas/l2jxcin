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
package net.xcine.gameserver.network.clientpackets;

import net.xcine.gameserver.model.actor.instance.L2PcInstance;
import net.xcine.gameserver.network.serverpackets.PrivateStoreMsgBuy;

public final class SetPrivateStoreMsgBuy extends L2GameClientPacket
{
	private String _storeMsg;
	
	@Override
	protected void readImpl()
	{
		_storeMsg = readS();
	}
	
	@Override
	protected void runImpl()
	{
		L2PcInstance player = getClient().getActiveChar();
		if (player == null || player.getBuyList() == null)
			return;
		
		if (_storeMsg.length() < 30)
		{
			player.getBuyList().setTitle(_storeMsg);
			player.sendPacket(new PrivateStoreMsgBuy(player));
		}
	}
	
	@Override
	public String getType()
	{
		return "[C] 94 SetPrivateStoreMsgBuy";
	}
	
}