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
package net.xcine.gameserver.handler.itemhandlers;

import net.xcine.gameserver.datatables.DoorTable;
import net.xcine.gameserver.handler.IItemHandler;
import net.xcine.gameserver.model.L2ItemInstance;
import net.xcine.gameserver.model.L2Object;
import net.xcine.gameserver.model.actor.L2Playable;
import net.xcine.gameserver.model.actor.instance.L2DoorInstance;
import net.xcine.gameserver.model.actor.instance.L2PcInstance;
import net.xcine.gameserver.network.SystemMessageId;
import net.xcine.gameserver.network.serverpackets.ActionFailed;

/**
 * @author chris
 */
public class PaganKeys implements IItemHandler
{
	public static final int INTERACTION_DISTANCE = 100;
	
	@Override
	public void useItem(L2Playable playable, L2ItemInstance item, boolean forceUse)
	{
		if (!(playable instanceof L2PcInstance))
			return;
		
		final L2PcInstance activeChar = (L2PcInstance) playable;
		final L2Object target = activeChar.getTarget();
		
		if (!(target instanceof L2DoorInstance))
		{
			activeChar.sendPacket(SystemMessageId.INCORRECT_TARGET);
			activeChar.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		final L2DoorInstance door = (L2DoorInstance) target;
		
		if (!(activeChar.isInsideRadius(door, INTERACTION_DISTANCE, false, false)))
		{
			activeChar.sendPacket(SystemMessageId.DIST_TOO_FAR_CASTING_STOPPED);
			activeChar.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		if (!playable.destroyItem("Consume", item.getObjectId(), 1, null, true))
			return;
		
		final int doorId = door.getDoorId();
		
		switch (item.getItemId())
		{
			case 8056:
				if (doorId == 23150004 || doorId == 23150003)
				{
					DoorTable.getInstance().getDoor(23150003).openMe();
					DoorTable.getInstance().getDoor(23150003).onOpen();
					DoorTable.getInstance().getDoor(23150004).openMe();
					DoorTable.getInstance().getDoor(23150004).onOpen();
				}
				else
					activeChar.sendPacket(SystemMessageId.TARGET_IS_INCORRECT);
				break;
			
			case 8273:
				switch (doorId)
				{
					case 19160002:
					case 19160003:
					case 19160004:
					case 19160005:
					case 19160006:
					case 19160007:
					case 19160008:
					case 19160009:
						DoorTable.getInstance().getDoor(doorId).openMe();
						DoorTable.getInstance().getDoor(doorId).onOpen();
						break;
					
					default:
						activeChar.sendPacket(SystemMessageId.TARGET_IS_INCORRECT);
						break;
				}
				break;
			
			case 8275:
				switch (doorId)
				{
					case 19160012:
					case 19160013:
						DoorTable.getInstance().getDoor(doorId).openMe();
						DoorTable.getInstance().getDoor(doorId).onOpen();
						break;
					
					default:
						activeChar.sendPacket(SystemMessageId.TARGET_IS_INCORRECT);
						break;
				}
				break;
		}
	}
}