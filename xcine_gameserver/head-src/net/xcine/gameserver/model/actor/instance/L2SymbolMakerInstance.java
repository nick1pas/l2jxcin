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
package net.xcine.gameserver.model.actor.instance;

import net.xcine.gameserver.datatables.HennaTable;
import net.xcine.gameserver.model.actor.L2Character;
import net.xcine.gameserver.network.SystemMessageId;
import net.xcine.gameserver.network.serverpackets.HennaEquipList;
import net.xcine.gameserver.network.serverpackets.HennaRemoveList;
import net.xcine.gameserver.templates.chars.L2NpcTemplate;

public class L2SymbolMakerInstance extends L2NpcInstance
{
	public L2SymbolMakerInstance(int objectID, L2NpcTemplate template)
	{
		super(objectID, template);
	}
	
	@Override
	public void onBypassFeedback(L2PcInstance player, String command)
	{
		if (command.equals("Draw"))
			player.sendPacket(new HennaEquipList(player, HennaTable.getInstance().getAvailableHenna(player.getClassId().getId())));
		else if (command.equals("RemoveList"))
		{
			boolean hasHennas = false;
			for (int i = 1; i <= 3; i++)
			{
				if (player.getHenna(i) != null)
					hasHennas = true;
			}
			
			if (hasHennas)
				player.sendPacket(new HennaRemoveList(player));
			else
				player.sendPacket(SystemMessageId.SYMBOL_NOT_FOUND);
		}
		else
			super.onBypassFeedback(player, command);
	}
	
	@Override
	public String getHtmlPath(int npcId, int val)
	{
		return "data/html/symbolmaker/SymbolMaker.htm";
	}
	
	@Override
	public boolean isAutoAttackable(L2Character attacker)
	{
		return false;
	}
}