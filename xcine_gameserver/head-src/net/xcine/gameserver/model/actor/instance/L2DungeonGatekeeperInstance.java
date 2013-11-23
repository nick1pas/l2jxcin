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

import java.util.StringTokenizer;

import net.xcine.gameserver.SevenSigns;
import net.xcine.gameserver.datatables.TeleportLocationTable;
import net.xcine.gameserver.model.L2TeleportLocation;
import net.xcine.gameserver.network.SystemMessageId;
import net.xcine.gameserver.network.serverpackets.ActionFailed;
import net.xcine.gameserver.network.serverpackets.NpcHtmlMessage;
import net.xcine.gameserver.templates.chars.L2NpcTemplate;

public class L2DungeonGatekeeperInstance extends L2NpcInstance
{
	public L2DungeonGatekeeperInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
	}
	
	@Override
	public void onBypassFeedback(L2PcInstance player, String command)
	{
		player.sendPacket(ActionFailed.STATIC_PACKET);
		
		StringTokenizer st = new StringTokenizer(command, " ");
		String actualCommand = st.nextToken(); // Get actual command
		
		String filename = SevenSigns.SEVEN_SIGNS_HTML_PATH;
		int sealAvariceOwner = SevenSigns.getInstance().getSealOwner(SevenSigns.SEAL_AVARICE);
		int sealGnosisOwner = SevenSigns.getInstance().getSealOwner(SevenSigns.SEAL_GNOSIS);
		int playerCabal = SevenSigns.getInstance().getPlayerCabal(player.getObjectId());
		boolean isSealValidationPeriod = SevenSigns.getInstance().isSealValidationPeriod();
		int compWinner = SevenSigns.getInstance().getCabalHighestScore();
		
		if (actualCommand.startsWith("necro"))
		{
			boolean canPort = true;
			if (isSealValidationPeriod)
			{
				if (compWinner == SevenSigns.CABAL_DAWN && (playerCabal != SevenSigns.CABAL_DAWN || sealAvariceOwner != SevenSigns.CABAL_DAWN))
				{
					player.sendPacket(SystemMessageId.CAN_BE_USED_BY_DAWN);
					canPort = false;
				}
				else if (compWinner == SevenSigns.CABAL_DUSK && (playerCabal != SevenSigns.CABAL_DUSK || sealAvariceOwner != SevenSigns.CABAL_DUSK))
				{
					player.sendPacket(SystemMessageId.CAN_BE_USED_BY_DUSK);
					canPort = false;
				}
				else if (compWinner == SevenSigns.CABAL_NULL && playerCabal != SevenSigns.CABAL_NULL)
					canPort = true;
				else if (playerCabal == SevenSigns.CABAL_NULL)
					canPort = false;
			}
			else
			{
				if (playerCabal == SevenSigns.CABAL_NULL)
					canPort = false;
			}
			
			if (!canPort)
			{
				NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
				filename += "necro_no.htm";
				html.setFile(filename);
				player.sendPacket(html);
			}
			else
			{
				doTeleport(player, Integer.parseInt(st.nextToken()));
				player.setIsIn7sDungeon(true);
			}
		}
		else if (actualCommand.startsWith("cata"))
		{
			boolean canPort = true;
			if (isSealValidationPeriod)
			{
				if (compWinner == SevenSigns.CABAL_DAWN && (playerCabal != SevenSigns.CABAL_DAWN || sealGnosisOwner != SevenSigns.CABAL_DAWN))
				{
					player.sendPacket(SystemMessageId.CAN_BE_USED_BY_DAWN);
					canPort = false;
				}
				else if (compWinner == SevenSigns.CABAL_DUSK && (playerCabal != SevenSigns.CABAL_DUSK || sealGnosisOwner != SevenSigns.CABAL_DUSK))
				{
					player.sendPacket(SystemMessageId.CAN_BE_USED_BY_DUSK);
					canPort = false;
				}
				else if (compWinner == SevenSigns.CABAL_NULL && playerCabal != SevenSigns.CABAL_NULL)
					canPort = true;
				else if (playerCabal == SevenSigns.CABAL_NULL)
					canPort = false;
			}
			else
			{
				if (playerCabal == SevenSigns.CABAL_NULL)
					canPort = false;
			}
			
			if (!canPort)
			{
				NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
				filename += "cata_no.htm";
				html.setFile(filename);
				player.sendPacket(html);
			}
			else
			{
				doTeleport(player, Integer.parseInt(st.nextToken()));
				player.setIsIn7sDungeon(true);
			}
		}
		else if (actualCommand.startsWith("exit"))
		{
			doTeleport(player, Integer.parseInt(st.nextToken()));
			player.setIsIn7sDungeon(false);
		}
		else if (actualCommand.startsWith("goto"))
		{
			doTeleport(player, Integer.parseInt(st.nextToken()));
		}
		else
			super.onBypassFeedback(player, command);
	}
	
	@Override
	public String getHtmlPath(int npcId, int val)
	{
		String filename = "";
		if (val == 0)
			filename = "" + npcId;
		else
			filename = npcId + "-" + val;
		
		return "data/html/teleporter/" + filename + ".htm";
	}
	
	private static void doTeleport(L2PcInstance player, int val)
	{
		L2TeleportLocation list = TeleportLocationTable.getInstance().getTemplate(val);
		if (list != null)
		{
			if (player.isAlikeDead())
				return;
			
			player.teleToLocation(list.getLocX(), list.getLocY(), list.getLocZ(), 20);
		}
		else
			_log.warning("No teleport destination with id:" + val);
		
		player.sendPacket(ActionFailed.STATIC_PACKET);
	}
}