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
package net.sf.l2j.gameserver.network.clientpackets;

import net.sf.l2j.protection.CatsGuard;

/**
 * @author zabbix
 */
public class GameGuardReply extends L2GameClientPacket
{
	private final int[] _reply;
	
	public GameGuardReply()
	{
		_reply = new int[4];
	}
	
	@Override
	protected void readImpl()
	{
		if (CatsGuard.getInstance().isEnabled() && (getClient().getHWid() == null))
		{
			_reply[0] = readD();
			_reply[1] = readD();
			_reply[2] = readD();
			_reply[3] = readD();
		}
		else
		{
			byte[] b = new byte[_buf.remaining()];
			readB(b);
		}
		
	}
	
	@Override
	protected void runImpl()
	{
		if (CatsGuard.getInstance().isEnabled())
		{
			CatsGuard.getInstance().initSession(getClient(), _reply);
		}
	}
}