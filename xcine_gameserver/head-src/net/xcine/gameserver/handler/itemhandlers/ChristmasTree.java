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

import net.xcine.gameserver.datatables.NpcTable;
import net.xcine.gameserver.handler.IItemHandler;
import net.xcine.gameserver.model.L2ItemInstance;
import net.xcine.gameserver.model.L2Object;
import net.xcine.gameserver.model.L2Spawn;
import net.xcine.gameserver.model.actor.L2Playable;
import net.xcine.gameserver.model.actor.instance.L2PcInstance;
import net.xcine.gameserver.network.SystemMessageId;
import net.xcine.gameserver.templates.chars.L2NpcTemplate;

public class ChristmasTree implements IItemHandler
{
	@Override
	public void useItem(L2Playable playable, L2ItemInstance item, boolean forceUse)
	{
		L2PcInstance activeChar = (L2PcInstance) playable;
		L2NpcTemplate template = null;
		
		switch (item.getItemId())
		{
			case 5560:
				template = NpcTable.getInstance().getTemplate(13006);
				break;
			case 5561:
				template = NpcTable.getInstance().getTemplate(13007);
				break;
		}
		
		if (template == null)
			return;
		
		L2Object target = activeChar.getTarget();
		if (target == null)
			target = activeChar;
		
		try
		{
			L2Spawn spawn = new L2Spawn(template);
			spawn.setLocx(target.getX());
			spawn.setLocy(target.getY());
			spawn.setLocz(target.getZ());
			spawn.doSpawn();
			
			activeChar.destroyItem("Consume", item.getObjectId(), 1, null, false);
		}
		catch (Exception e)
		{
			activeChar.sendPacket(SystemMessageId.TARGET_CANT_FOUND);
		}
	}
}