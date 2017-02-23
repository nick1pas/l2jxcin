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
import net.sf.l2j.gameserver.cache.HtmCache;
import net.sf.l2j.gameserver.model.actor.Npc;
import net.sf.l2j.gameserver.model.actor.template.NpcTemplate;
import net.sf.l2j.gameserver.model.entity.events.DMEvent;
import net.sf.l2j.gameserver.network.serverpackets.ActionFailed;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;

/**
 * @author L0ngh0rn
 */
public class DMEventManager extends Npc
{
	private static final String htmlPath = "data/html/mods/DMEvent/";
	
	/**
	 * @param objectId
	 * @param template
	 */
	public DMEventManager(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void onBypassFeedback(Player playerInstance, String command)
	{
		DMEvent.onBypass(command, playerInstance);
	}
	
	@Override
	public void showChatWindow(Player activeChar)
	{
		if (activeChar == null)
			return;
		
		if (DMEvent.isParticipating())
		{
			final boolean isParticipant = DMEvent.isPlayerParticipant(activeChar.getObjectId());
			final String htmContent;
			
			if (!isParticipant)
				htmContent = HtmCache.getInstance().getHtm(htmlPath + "Participation.htm");
			else
				htmContent = HtmCache.getInstance().getHtm(htmlPath + "RemoveParticipation.htm");
			
			if (htmContent != null)
			{
				int PlayerCounts = DMEvent.getPlayerCounts();
				NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(getObjectId());
				
				npcHtmlMessage.setHtml(htmContent);
				npcHtmlMessage.replace("%objectId%", String.valueOf(getObjectId()));
				npcHtmlMessage.replace("%playercount%", String.valueOf(PlayerCounts));
				if (!isParticipant)
					npcHtmlMessage.replace("%fee%", DMEvent.getParticipationFee());
				
				activeChar.sendPacket(npcHtmlMessage);
			}
		}
		else if (DMEvent.isStarting() || DMEvent.isStarted())
		{
			final String htmContent = HtmCache.getInstance().getHtm(htmlPath + "Status.htm");
			
			if (htmContent != null)
			{
				String[] firstPositions = DMEvent.getFirstPosition(Config.DM_REWARD_FIRST_PLAYERS);
				NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(getObjectId());
				
				String htmltext = "";
				htmltext += "<table width=\"250\">";
				htmltext += "<tr><td width=\"150\">Name</td><td width=\"100\" align=\"center\">Points</td></tr>";
				if (firstPositions != null)
					for (int i = 0; i < firstPositions.length; i++)
					{
						String[] row = firstPositions[i].split("\\,");
						htmltext += "<tr><td>" + row[0] + "</td><td align=\"center\">" + row[1] + "</td></tr>";
					}
				htmltext += "</table>";
				
				npcHtmlMessage.setHtml(htmContent);
				// npcHtmlMessage.replace("%objectId%", String.valueOf(getObjectId()));
				npcHtmlMessage.replace("%positions%", htmltext);
				activeChar.sendPacket(npcHtmlMessage);
			}
		}
		
		activeChar.sendPacket(ActionFailed.STATIC_PACKET);
	}
}