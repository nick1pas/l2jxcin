/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version. This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details. You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package net.xcine.gameserver.network.clientpackets;

import java.util.List;

import javolution.util.FastList;

import net.xcine.gameserver.managers.CursedWeaponsManager;
import net.xcine.gameserver.model.CursedWeapon;
import net.xcine.gameserver.model.L2Character;
import net.xcine.gameserver.network.serverpackets.ExCursedWeaponLocation;
import net.xcine.gameserver.network.serverpackets.ExCursedWeaponLocation.CursedWeaponInfo;
import net.xcine.util.Point3D;

/**
 * Format: (ch)
 * @author ProGramMoS
 */
public final class RequestCursedWeaponLocation extends L2GameClientPacket
{
	@Override
	protected void readImpl()
	{
		//ignore read packet
	}

	@Override
	protected void runImpl()
	{
		L2Character activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		List<CursedWeaponInfo> list = new FastList<>();
		for(CursedWeapon cw : CursedWeaponsManager.getInstance().getCursedWeapons())
		{
			if(!cw.isActive())
			{
				continue;
			}

			Point3D pos = cw.getWorldPosition();

			if(pos != null)
			{
				list.add(new CursedWeaponInfo(pos, cw.getItemId(), cw.isActivated() ? 1 : 0));
			}
		}

		if(!list.isEmpty())
		{
			activeChar.sendPacket(new ExCursedWeaponLocation(list));
		}
	}

	@Override
	public String getType()
	{
		return "[C] D0:23 RequestCursedWeaponLocation";
	}
}
