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

import net.sf.l2j.gameserver.instancemanager.SevenSigns;
import net.sf.l2j.gameserver.instancemanager.SevenSigns.CabalType;
import net.sf.l2j.gameserver.instancemanager.SevenSigns.SealType;
import net.sf.l2j.gameserver.model.actor.template.NpcTemplate;
import net.sf.l2j.gameserver.network.serverpackets.ActionFailed;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;

public class L2DuskPriestInstance extends L2SignsPriestInstance
{
	public L2DuskPriestInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}
	
	@Override
	public void onBypassFeedback(L2PcInstance player, String command)
	{
		if (command.startsWith("Chat"))
			showChatWindow(player);
		else
			super.onBypassFeedback(player, command);
	}
	
	@Override
	public void showChatWindow(L2PcInstance player)
	{
		player.sendPacket(ActionFailed.STATIC_PACKET);
		
		String filename = SevenSigns.SEVEN_SIGNS_HTML_PATH;
		
		final CabalType winningCabal = SevenSigns.getInstance().getCabalHighestScore();
		
		switch (SevenSigns.getInstance().getPlayerCabal(player.getObjectId()))
		{
			case DUSK:
				if (SevenSigns.getInstance().isCompResultsPeriod())
					filename += "dusk_priest_5.htm";
				else if (SevenSigns.getInstance().isRecruitingPeriod())
					filename += "dusk_priest_6.htm";
				else if (SevenSigns.getInstance().isSealValidationPeriod())
				{
					if (winningCabal == CabalType.DUSK)
					{
						if (winningCabal != SevenSigns.getInstance().getSealOwner(SealType.GNOSIS))
							filename += "dusk_priest_2c.htm";
						else
							filename += "dusk_priest_2a.htm";
					}
					else if (winningCabal == CabalType.NORMAL)
						filename += "dusk_priest_2d.htm";
					else
						filename += "dusk_priest_2b.htm";
				}
				else
					filename += "dusk_priest_1b.htm";
				break;
			
			case DAWN:
				if (SevenSigns.getInstance().isSealValidationPeriod())
					filename += "dusk_priest_3a.htm";
				else
					filename += "dusk_priest_3b.htm";
				break;
			
			default:
				if (SevenSigns.getInstance().isCompResultsPeriod())
					filename += "dusk_priest_5.htm";
				else if (SevenSigns.getInstance().isRecruitingPeriod())
					filename += "dusk_priest_6.htm";
				else if (SevenSigns.getInstance().isSealValidationPeriod())
				{
					if (winningCabal == CabalType.DUSK)
						filename += "dusk_priest_4.htm";
					else if (winningCabal == CabalType.NORMAL)
						filename += "dusk_priest_2d.htm";
					else
						filename += "dusk_priest_2b.htm";
				}
				else
					filename += "dusk_priest_1a.htm";
				break;
		}
		
		final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
		html.setFile(filename);
		html.replace("%objectId%", getObjectId());
		player.sendPacket(html);
	}
}