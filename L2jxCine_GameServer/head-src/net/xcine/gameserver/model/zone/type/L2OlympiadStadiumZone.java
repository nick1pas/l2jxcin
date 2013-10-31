/* This program is free software; you can redistribute it and/or modify
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
package net.xcine.gameserver.model.zone.type;

import net.xcine.gameserver.datatables.xml.MapRegionData.TeleportWhereType;
import net.xcine.gameserver.model.L2Character;
import net.xcine.gameserver.model.actor.instance.L2PcInstance;
import net.xcine.gameserver.model.zone.L2ZoneType;
import net.xcine.gameserver.network.SystemMessageId;
import net.xcine.gameserver.network.serverpackets.SystemMessage;


/**
 * An olympiad stadium
 *
 * @author  durgus
 */
public class L2OlympiadStadiumZone extends L2ZoneType
{
	private int _stadiumId;
	
	public L2OlympiadStadiumZone(int id)
	{
		super(id);
	}

	@Override
	public void setParameter(String name, String value)
	{
		if (name.equals("stadiumId"))
		{
			_stadiumId = Integer.parseInt(value);
		}
		else super.setParameter(name, value);
	}

	@Override
	public void onEnter(L2Character character)
	{
		character.setInsideZone(L2Character.ZONE_PVP, true);
		
		if (character instanceof L2PcInstance)
		{
			if ((((L2PcInstance)character).getOlympiadGameId() + 1) == getStadiumId())
				((L2PcInstance)character).sendPacket(new SystemMessage(SystemMessageId.ENTERED_COMBAT_ZONE));
			else
				character.teleToLocation(TeleportWhereType.Town);
		}
	}

	@Override
	public void onExit(L2Character character)
	{
		character.setInsideZone(L2Character.ZONE_PVP, false);
		
		if (character instanceof L2PcInstance)
		{
			((L2PcInstance)character).sendPacket(new SystemMessage(SystemMessageId.LEFT_COMBAT_ZONE));
		}
	}

	@Override
	public void onDieInside(L2Character character) {}

	@Override
	public void onReviveInside(L2Character character) {}

	/**
	 * Returns this zones stadium id (if any)
	 * @return
	 */
	public int getStadiumId()
	{
		return _stadiumId;
	}
}