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

import net.sf.l2j.gameserver.ai.CtrlEvent;
import net.sf.l2j.gameserver.model.SpawnLocation;
import net.sf.l2j.gameserver.model.actor.L2Character;

public final class CannotMoveAnymore extends L2GameClientPacket
{
	private int _x;
	private int _y;
	private int _z;
	private int _heading;
	
	@Override
	protected void readImpl()
	{
		_x = readD();
		_y = readD();
		_z = readD();
		_heading = readD();
	}
	
	@Override
	protected void runImpl()
	{
		final L2Character player = getClient().getActiveChar();
		if (player == null)
			return;
		
		if (player.hasAI())
			player.getAI().notifyEvent(CtrlEvent.EVT_ARRIVED_BLOCKED, new SpawnLocation(_x, _y, _z, _heading));
	}
}