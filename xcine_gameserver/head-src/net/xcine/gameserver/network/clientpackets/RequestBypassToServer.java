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
package net.xcine.gameserver.network.clientpackets;

import java.util.StringTokenizer;
import java.util.logging.Level;

import net.xcine.Config;
import net.xcine.gameserver.communitybbs.CommunityBoard;
import net.xcine.gameserver.datatables.AdminCommandAccessRights;
import net.xcine.gameserver.datatables.ItemTable;
import net.xcine.gameserver.event.EventBuffer;
import net.xcine.gameserver.event.EventManager;
import net.xcine.gameserver.event.EventStats;
import net.xcine.gameserver.handler.AdminCommandHandler;
import net.xcine.gameserver.handler.IAdminCommandHandler;
import net.xcine.gameserver.model.L2Object;
import net.xcine.gameserver.model.L2World;
import net.xcine.gameserver.model.actor.L2Npc;
import net.xcine.gameserver.model.actor.instance.L2OlympiadManagerInstance;
import net.xcine.gameserver.model.actor.instance.L2PcInstance;
import net.xcine.gameserver.model.entity.Hero;
import net.xcine.gameserver.model.olympiad.OlympiadGameManager;
import net.xcine.gameserver.model.olympiad.OlympiadGameTask;
import net.xcine.gameserver.model.olympiad.OlympiadManager;
import net.xcine.gameserver.network.SystemMessageId;
import net.xcine.gameserver.network.serverpackets.ActionFailed;
import net.xcine.gameserver.network.serverpackets.NpcHtmlMessage;
import net.xcine.gameserver.util.GMAudit;

public final class RequestBypassToServer extends L2GameClientPacket
{
	private String _command;
	
	@Override
	protected void readImpl()
	{
		_command = readS();
	}
	
	@Override
	protected void runImpl()
	{
		final L2PcInstance activeChar = getClient().getActiveChar();
		if (activeChar == null)
			return;
		
		if (!getClient().getFloodProtectors().getServerBypass().tryPerformAction(_command))
			return;
		
		if (_command.isEmpty())
		{
			_log.info(activeChar.getName() + " sent an empty requestBypass packet.");
			activeChar.logout();
			return;
		}
		
		try
		{
			if (_command.startsWith("admin_"))
			{
				String command = _command.split(" ")[0];
				
				IAdminCommandHandler ach = AdminCommandHandler.getInstance().getAdminCommandHandler(command);
				if (ach == null)
				{
					if (activeChar.isGM())
						activeChar.sendMessage("The command " + command.substring(6) + " doesn't exist.");
					
					_log.warning("No handler registered for admin command '" + command + "'");
					return;
				}
				
				if (!AdminCommandAccessRights.getInstance().hasAccess(command, activeChar.getAccessLevel()))
				{
					activeChar.sendMessage("You don't have the access rights to use this command.");
					_log.warning(activeChar.getName() + " tried to use admin command " + command + " without proper Access Level.");
					return;
				}
				
				if (Config.GMAUDIT)
					GMAudit.auditGMAction(activeChar.getName() + " [" + activeChar.getObjectId() + "]", _command, (activeChar.getTarget() != null ? activeChar.getTarget().getName() : "no-target"));
				
				ach.useAdminCommand(_command, activeChar);
			}
			else if (_command.startsWith("event_vote"))
			{
				EventManager.getInstance().addVote(activeChar, Integer.parseInt(_command.substring(11)));
			}
			else if (_command.equals("event_register"))
			{
				EventManager.getInstance().registerPlayer(activeChar);
			}
			else if (_command.equals("event_unregister"))
			{
				EventManager.getInstance().unregisterPlayer(activeChar);
			}
			else if (_command.startsWith("player_help "))
			{
				playerHelp(activeChar, _command.substring(12));
			}
			else if (_command.startsWith("npc_"))
			{
				if (!activeChar.validateBypass(_command))
					return;
				
				int endOfId = _command.indexOf('_', 5);
				String id;
				if (endOfId > 0)
					id = _command.substring(4, endOfId);
				else
					id = _command.substring(4);
				
				try
				{
					L2Object object = L2World.getInstance().findObject(Integer.parseInt(id));
					
					if (object != null && object instanceof L2Npc && endOfId > 0 && activeChar.isInsideRadius(object, L2Npc.INTERACTION_DISTANCE, false, false))
						((L2Npc) object).onBypassFeedback(activeChar, _command.substring(endOfId + 1));
					
					activeChar.sendPacket(ActionFailed.STATIC_PACKET);
				}
				catch (NumberFormatException nfe)
				{
				}
			}
			// Navigate throught Manor windows
			else if (_command.startsWith("manor_menu_select?"))
			{
				L2Object object = activeChar.getTarget();
				if (object instanceof L2Npc)
					((L2Npc) object).onBypassFeedback(activeChar, _command);
			}
			else if (_command.startsWith("bbs_") || _command.startsWith("_bbs") || _command.startsWith("_friend") || _command.startsWith("_mail") || _command.startsWith("_block"))
			{
				CommunityBoard.getInstance().handleCommands(getClient(), _command);
			}
			else if (_command.startsWith("Quest "))
			{
				if (!activeChar.validateBypass(_command))
					return;
				
				String[] str = _command.substring(6).trim().split(" ");
				if (str.length == 1)
					activeChar.processQuestEvent(str[0], "");
				else
					activeChar.processQuestEvent(str[0], str[1]);
			}
			else if (_command.startsWith("eventvote ")) 
			{
				EventManager.getInstance().addVote(activeChar, Integer.parseInt(_command.substring(10)));
			}
			else if (_command.startsWith("eventstats "))
			{
				try
				{
					EventStats.getInstance().showHtml(Integer.parseInt(_command.substring(11)),activeChar);
				}
				catch (Exception e)
				{
					activeChar.sendMessage("Currently there are no statistics to show.");
				}
			}
			else if (_command.startsWith("eventstats_show "))
			{
				EventStats.getInstance().showPlayerStats(Integer.parseInt(_command.substring(16)),activeChar);
			}
			else if (_command.equals("eventbuffershow"))
			{
				EventBuffer.getInstance().showHtml(activeChar);
			}
			else if (_command.startsWith("eventbuffer "))
			{
				EventBuffer.getInstance().changeList(activeChar, Integer.parseInt(_command.substring(12,_command.length()-2)), (Integer.parseInt(_command.substring(_command.length()-1)) == 0 ? false : true)); 
				EventBuffer.getInstance().showHtml(activeChar);
			}
			else if (_command.startsWith("eventinfo "))
			{
				int eventId = Integer.valueOf(_command.substring(10));
				
				NpcHtmlMessage html = new NpcHtmlMessage(0);
				html.setFile("data/html/eventinfo/" + eventId + ".htm");
				html.replace("%amount%", String.valueOf(EventManager.getInstance().getInt(eventId, "rewardAmmount")));
				html.replace("%item%", ItemTable.getInstance().createDummyItem(EventManager.getInstance().getInt(eventId, "rewardId")).getItemName());
				html.replace("%minlvl%", String.valueOf(EventManager.getInstance().getInt(eventId, "minLvl")));
				html.replace("%maxlvl%", String.valueOf(EventManager.getInstance().getInt(eventId, "maxLvl")));
				html.replace("%time%", String.valueOf(EventManager.getInstance().getInt(eventId, "matchTime") / 60));
				html.replace("%players%", String.valueOf(EventManager.getInstance().getInt(eventId, "minPlayers")));
				html.replace("%url%", EventManager.getInstance().getString("siteUrl"));
				html.replace("%buffs%", EventManager.getInstance().getBoolean(eventId, "removeBuffs") ? "Self" : "Full");
				activeChar.sendPacket(html);
				activeChar.sendPacket(ActionFailed.STATIC_PACKET);
			}
			else if (_command.startsWith("_match"))
			{
				String params = _command.substring(_command.indexOf("?") + 1);
				StringTokenizer st = new StringTokenizer(params, "&");
				int heroclass = Integer.parseInt(st.nextToken().split("=")[1]);
				int heropage = Integer.parseInt(st.nextToken().split("=")[1]);
				int heroid = Hero.getInstance().getHeroByClass(heroclass);
				if (heroid > 0)
					Hero.getInstance().showHeroFights(activeChar, heroclass, heroid, heropage);
			}
			else if (_command.startsWith("_diary"))
			{
				String params = _command.substring(_command.indexOf("?") + 1);
				StringTokenizer st = new StringTokenizer(params, "&");
				int heroclass = Integer.parseInt(st.nextToken().split("=")[1]);
				int heropage = Integer.parseInt(st.nextToken().split("=")[1]);
				int heroid = Hero.getInstance().getHeroByClass(heroclass);
				if (heroid > 0)
					Hero.getInstance().showHeroDiary(activeChar, heroclass, heroid, heropage);
			}
			else if (_command.startsWith("arenachange")) // change
			{
				final boolean isManager = activeChar.getCurrentFolkNPC() instanceof L2OlympiadManagerInstance;
				if (!isManager)
				{
					// Without npc, command can be used only in observer mode on arena
					if (!activeChar.inObserverMode() || activeChar.isInOlympiadMode() || activeChar.getOlympiadGameId() < 0)
						return;
				}
				
				if (OlympiadManager.getInstance().isRegisteredInComp(activeChar))
				{
					activeChar.sendPacket(SystemMessageId.WHILE_YOU_ARE_ON_THE_WAITING_LIST_YOU_ARE_NOT_ALLOWED_TO_WATCH_THE_GAME);
					return;
				}
				
				if (EventManager.getInstance().players.contains(activeChar))
				{
					activeChar.sendMessage("You can not observe games while registered for an event!");
					return;
				}
				
				final int arenaId = Integer.parseInt(_command.substring(12).trim());
				final OlympiadGameTask nextArena = OlympiadGameManager.getInstance().getOlympiadTask(arenaId);
				if (nextArena != null)
				{
					activeChar.enterOlympiadObserverMode(nextArena.getZone().getSpawns().get(0), arenaId);
					return;
				}
			}
		}
		catch (Exception e)
		{
			_log.log(Level.WARNING, "Bad RequestBypassToServer: ", e);
		}
	}
	
	private static void playerHelp(L2PcInstance activeChar, String path)
	{
		if (path.indexOf("..") != -1)
			return;
		
		final StringTokenizer st = new StringTokenizer(path);
		final String[] cmd = st.nextToken().split("#");
		
		NpcHtmlMessage html = new NpcHtmlMessage(0);
		html.setFile("data/html/help/" + cmd[0]);
		if (cmd.length > 1)
			html.setItemId(Integer.parseInt(cmd[1]));
		html.disableValidation();
		activeChar.sendPacket(html);
	}
}