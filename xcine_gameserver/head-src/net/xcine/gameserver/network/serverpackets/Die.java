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
package net.xcine.gameserver.network.serverpackets;

import net.xcine.gameserver.datatables.AccessLevels;
import net.xcine.gameserver.event.EventManager;
import net.xcine.gameserver.instancemanager.CastleManager;
import net.xcine.gameserver.model.L2AccessLevel;
import net.xcine.gameserver.model.L2Clan;
import net.xcine.gameserver.model.L2SiegeClan;
import net.xcine.gameserver.model.actor.L2Attackable;
import net.xcine.gameserver.model.actor.L2Character;
import net.xcine.gameserver.model.actor.instance.L2PcInstance;
import net.xcine.gameserver.model.entity.Castle;

public class Die extends L2GameServerPacket
{
	private final int _charObjId;
	private final boolean _fake;
	private boolean _sweepable;
	private L2AccessLevel _access = AccessLevels._userAccessLevel;
	private L2Clan _clan;
	L2Character _activeChar;
	private boolean _event;
	
	public Die(L2Character cha)
	{
		_activeChar = cha;
		if (cha instanceof L2PcInstance)
		{
			L2PcInstance player = (L2PcInstance) cha;
			_access = player.getAccessLevel();
			_clan = player.getClan();
			_event = EventManager.getInstance().isRegistered(cha);
		}
		_charObjId = cha.getObjectId();
		_fake = !cha.isDead();
		if (cha instanceof L2Attackable)
			_sweepable = ((L2Attackable) cha).isSweepActive();
		
	}
	
	@Override
	protected final void writeImpl()
	{
		if (_fake)
			return;
		
		writeC(0x06);
		writeD(_charObjId);
		
		if (_event)
		{
			writeD(0x00);
			writeD(0x00);
			writeD(0x00);
			writeD(0x00);
		}
		else
		{
			writeD(0x01); // to nearest village
				
			if (_clan != null)
			{
				L2SiegeClan siegeClan = null;
				boolean isInDefense = false;
				
				Castle castle = CastleManager.getInstance().getCastle(_activeChar);
				if (castle != null && castle.getSiege().getIsInProgress())
				{
					// siege in progress
					siegeClan = castle.getSiege().getAttackerClan(_clan);
					if (siegeClan == null && castle.getSiege().checkIsDefender(_clan))
						isInDefense = true;
				}
				
				writeD(_clan.hasHideout() ? 0x01 : 0x00); // to hide away
				writeD(_clan.hasCastle() || isInDefense ? 0x01 : 0x00); // to castle
				writeD(siegeClan != null && !isInDefense && !siegeClan.getFlag().isEmpty() ? 0x01 : 0x00); // to siege HQ
			}
			else
			{
				writeD(0x00); // to hide away
				writeD(0x00); // to castle
				writeD(0x00); // to siege HQ
			}
		}
		
		writeD(_sweepable ? 0x01 : 0x00); // sweepable (blue glow)
		writeD(_access.allowFixedRes() ? 0x01 : 0x00); // FIXED
	}
}