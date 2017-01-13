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
package net.sf.l2j.gameserver.model.actor.instance;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.datatables.AioManagerTable;
import net.sf.l2j.gameserver.instancemanager.AioManager;
import net.sf.l2j.gameserver.model.actor.template.NpcTemplate;
import net.sf.l2j.gameserver.network.serverpackets.ActionFailed;
import net.sf.l2j.gameserver.network.serverpackets.ExShowScreenMessage;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;
import net.sf.l2j.gameserver.templates.L2Aio;

/**
 * @author rapfersan92
 */
public class L2AioManagerInstance extends L2NpcInstance
{
	public L2AioManagerInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}
	
	@Override
	public void showChatWindow(L2PcInstance player, int val)
	{
		String name = "data/html/mods/aio/" + getNpcId() + ".htm";
		if (val != 0)
			name = "data/html/mods/aio/" + getNpcId() + "-" + val + ".htm";
		
		NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
		html.setFile(name);
		html.replace("%objectId%", getObjectId());
		player.sendPacket(html);
		player.sendPacket(ActionFailed.STATIC_PACKET);
	}
	
	@Override
	public void onBypassFeedback(L2PcInstance player, String command)
	{
		if (command.startsWith("AddAio"))
		{
			int serviceId = Integer.parseInt(command.substring(7));
			addAio(player, serviceId);
		}
		else if (command.startsWith("UpdateAio"))
		{
			int serviceId = Integer.parseInt(command.substring(10));
			updateAio(player, serviceId);
		}
		else if (command.equalsIgnoreCase("RemoveAio"))
			removeAio(player);
		
		super.onBypassFeedback(player, command);
	}
	
	private static void addAio(L2PcInstance player, int serviceId)
	{
		for (L2Aio service : AioManagerTable.getInstance().getAioTable())
		{
			if (service.getId() != serviceId)
				continue;
			
			if (AioManager.getInstance().hasAioPrivileges(player.getObjectId()))
			{
				player.sendMessage("Your already have aio privileges.");
				return;
			}
			else if (Config.LIST_AIO_RESTRICTED_CLASSES.contains(player.getTemplate().getClassId().getId()))
			{
				player.sendMessage("You cannot receive aio privileges with a character in their current class.");
				return;
			}
			else if (player.isSubClassActive())
			{
				player.sendMessage("You cannot receive aio privileges with a character in their subclass.");
				return;
			}
			else if (player.destroyItemByItemId("delete aio fee", service.getFeeId(), service.getFeeVal(), null, true))
			{
				AioManager.getInstance().addAio(player.getObjectId(), System.currentTimeMillis() + service.getDuration() * 86400000);
				player.sendPacket(new ExShowScreenMessage("Your aio privileges were added.", 10000));
			}
		}
	}
	
	private static void updateAio(L2PcInstance player, int serviceId)
	{
		for (L2Aio service : AioManagerTable.getInstance().getAioTable())
		{
			if (service.getId() != serviceId)
				continue;
			
			if (!AioManager.getInstance().hasAioPrivileges(player.getObjectId()))
			{
				player.sendMessage("You do not have aio privileges.");
				return;
			}
			else if (player.destroyItemByItemId("delete aio fee", service.getFeeId(), service.getFeeVal(), null, true))
			{
				AioManager.getInstance().updateAio(player.getObjectId(), service.getDuration() * 86400000);
				player.sendPacket(new ExShowScreenMessage("Your aio privileges were updated.", 10000));
			}
		}
	}
	
	private static void removeAio(L2PcInstance player)
	{
		if (!AioManager.getInstance().hasAioPrivileges(player.getObjectId()))
		{
			player.sendMessage("You do not have aio privileges.");
			return;
		}
		
		AioManager.getInstance().removeAio(player.getObjectId());
		player.sendPacket(new ExShowScreenMessage("Your aio privileges were removed.", 10000));
	}
}