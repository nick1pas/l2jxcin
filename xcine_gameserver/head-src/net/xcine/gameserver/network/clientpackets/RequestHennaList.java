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
package net.xcine.gameserver.network.clientpackets;

import net.xcine.gameserver.datatables.HennaTable;
import net.xcine.gameserver.model.actor.instance.L2PcInstance;
import net.xcine.gameserver.network.serverpackets.HennaEquipList;
import net.xcine.gameserver.templates.item.L2Henna;

/**
 * RequestHennaList
 * @author Tempy
 */
public final class RequestHennaList extends L2GameClientPacket
{
	@SuppressWarnings("unused")
	private int _unknown;
	
	@Override
	protected void readImpl()
	{
		_unknown = readD(); // ??
	}
	
	@Override
	protected void runImpl()
	{
		final L2PcInstance activeChar = getClient().getActiveChar();
		if (activeChar == null)
			return;
		
		final L2Henna[] henna = HennaTable.getInstance().getAvailableHenna(activeChar.getClassId().getId());
		if (henna == null)
			return;
		
		activeChar.sendPacket(new HennaEquipList(activeChar, henna));
	}
}