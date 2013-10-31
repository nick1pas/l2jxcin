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
package net.xcine.gameserver.network.serverpackets;

import net.xcine.gameserver.model.actor.instance.L2DoorInstance;

public class DoorStatusUpdate extends L2GameServerPacket
{
	private static final String _S__61_DOORSTATUSUPDATE = "[S] 4d DoorStatusUpdate";

	private L2DoorInstance _door;

	public DoorStatusUpdate(L2DoorInstance door)
	{
		_door = door;
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0x4d);
		writeD(_door.getObjectId());
		writeD(_door.getOpen() ? 0 : 1);
		writeD(_door.getDamage());
		writeD(_door.isEnemy() ? 1 : 0);
		writeD(_door.getDoorId());
		writeD(_door.getMaxHp());
		writeD((int) _door.getStatus().getCurrentHp());
	}

	@Override
	public String getType()
	{
		return _S__61_DOORSTATUSUPDATE;
	}

}