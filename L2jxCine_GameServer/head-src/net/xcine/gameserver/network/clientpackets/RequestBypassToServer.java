/*
 * This program is free software; you can redistribute it and/or modify
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
package net.xcine.gameserver.network.clientpackets;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.xcine.Config;
import net.xcine.gameserver.ai.CtrlIntention;
import net.xcine.gameserver.communitybbs.CommunityBoard;
import net.xcine.gameserver.datatables.xml.AdminCommandAccessRightsData;
import net.xcine.gameserver.event.EventBuffer;
import net.xcine.gameserver.event.EventManager;
import net.xcine.gameserver.event.EventStats;
import net.xcine.gameserver.handler.AdminCommandHandler;
import net.xcine.gameserver.handler.IAdminCommandHandler;
import net.xcine.gameserver.handler.custom.CustomBypassHandler;
import net.xcine.gameserver.model.L2Npc;
import net.xcine.gameserver.model.L2Object;
import net.xcine.gameserver.model.L2World;
import net.xcine.gameserver.model.actor.instance.L2ClassMasterInstance;
import net.xcine.gameserver.model.actor.instance.L2NpcInstance;
import net.xcine.gameserver.model.actor.instance.L2PcInstance;
import net.xcine.gameserver.model.actor.instance.L2SymbolMakerInstance;
import net.xcine.gameserver.model.actor.position.L2CharPosition;
import net.xcine.gameserver.model.entity.event.L2Event;
import net.xcine.gameserver.model.entity.event.VIP;
import net.xcine.gameserver.model.entity.olympiad.Olympiad;
import net.xcine.gameserver.network.serverpackets.ActionFailed;
import net.xcine.gameserver.network.serverpackets.NpcHtmlMessage;
import net.xcine.gameserver.util.GMAudit;
import net.xcine.util.database.L2DatabaseFactory;

public final class RequestBypassToServer extends L2GameClientPacket
{
	private static Logger _log = Logger.getLogger(RequestBypassToServer.class.getName());

	// S
	private String _command;
	
	@Override
	protected void readImpl()
	{
		_command = readS();
	}

	@Override
	protected void runImpl()
	{
		L2PcInstance activeChar = getClient().getActiveChar();

		if(activeChar == null)
			return;

		if (!getClient().getFloodProtectors().getServerBypass().tryPerformAction(_command))
			return;
		

		try
		{
			if(_command.startsWith("admin_"))
			{
				// DaDummy: this way we log _every_ admincommand with all related info
				String command;

				if(_command.indexOf(" ") != -1)
				{
					command = _command.substring(0, _command.indexOf(" "));
				}
				else
				{
					command = _command;
				}

				IAdminCommandHandler ach = AdminCommandHandler.getInstance().getAdminCommandHandler(command);

				if(ach == null)
				{
					if(activeChar.isGM())
					{
						activeChar.sendMessage("The command " + command + " does not exists!");
					}

					_log.warning("No handler registered for admin command '" + command + "'");
					return;
				}

				if(!AdminCommandAccessRightsData.getInstance().hasAccess(command, activeChar.getAccessLevel()))
				{
					activeChar.sendMessage("You don't have the access right to use this command!");
					if(Config.DEBUG)
					{
						_log.warning("Character " + activeChar.getName() + " tried to use admin command " + command + ", but doesn't have access to it!");
					}
					return;
				}

				if(Config.GMAUDIT)
				{
					GMAudit.auditGMAction(activeChar.getName()+" ["+activeChar.getObjectId()+"]", command, (activeChar.getTarget() != null?activeChar.getTarget().getName():"no-target"),_command.replace(command, ""));
					
				}

				ach.useAdminCommand(_command, activeChar);
			}

			else if(_command.startsWith("event_vote"))
			{
				EventManager.getInstance().addVote(activeChar, Integer.parseInt(_command.substring(11)));
			}
			else if(_command.equals("event_register"))
			{
				EventManager.getInstance().registerPlayer(activeChar);
			}
			else if(_command.equals("event_unregister"))
			{
				EventManager.getInstance().unregisterPlayer(activeChar);
			}
			else if(_command.equals("come_here") && activeChar.isGM())
			{
				comeHere(activeChar);
			}
			else if(_command.startsWith("player_help "))
			{
				playerHelp(activeChar, _command.substring(12));
			}
			else if(_command.startsWith("npc_"))
			{
				if(!activeChar.validateBypass(_command))
					return;

				int endOfId = _command.indexOf('_', 5);
				String id;

				if(endOfId > 0)
				{
					id = _command.substring(4, endOfId);
				}
				else
				{
					id = _command.substring(4);
				}

				try
				{
					L2Object object = L2World.getInstance().findObject(Integer.parseInt(id));

					if(_command.substring(endOfId + 1).startsWith("event_participate"))
					{
						L2Event.inscribePlayer(activeChar);
					}

					if(_command.substring(endOfId+1).startsWith("vip_joinVIPTeam"))
					{
							VIP.addPlayerVIP(activeChar);
					}

					if(_command.substring(endOfId+1).startsWith("vip_joinNotVIPTeam"))
					{
							VIP.addPlayerNotVIP(activeChar);
					}

					if(_command.substring(endOfId+1).startsWith("vip_finishVIP"))
					{
							VIP.vipWin(activeChar);
					}

					if(_command.substring(endOfId+1).startsWith("event_participate"))
					{
						L2Event.inscribePlayer(activeChar);
					}

					else if((Config.ALLOW_CLASS_MASTERS && Config.ALLOW_REMOTE_CLASS_MASTERS && object instanceof L2ClassMasterInstance)
						|| (object instanceof L2NpcInstance && endOfId > 0 && activeChar.isInsideRadius(object, L2Npc.INTERACTION_DISTANCE, false, false)))
					{
						((L2NpcInstance) object).onBypassFeedback(activeChar, _command.substring(endOfId + 1));
					}

					activeChar.sendPacket(ActionFailed.STATIC_PACKET);
				}
				catch(NumberFormatException nfe)
				{
					if(Config.ENABLE_ALL_EXCEPTIONS)
						nfe.printStackTrace();
					
				}
			}
			//	Draw a Symbol
			else if(_command.equals("Draw"))
			{
				L2Object object = activeChar.getTarget();
				if(object instanceof L2NpcInstance)
				{
					((L2SymbolMakerInstance) object).onBypassFeedback(activeChar, _command);
				}
			}
			else if(_command.equals("RemoveList"))
			{
				L2Object object = activeChar.getTarget();
				if(object instanceof L2NpcInstance)
				{
					((L2SymbolMakerInstance) object).onBypassFeedback(activeChar, _command);
				}
			}
			else if(_command.equals("Remove "))
			{
				L2Object object = activeChar.getTarget();

				if(object instanceof L2NpcInstance)
				{
					((L2SymbolMakerInstance) object).onBypassFeedback(activeChar, _command);
				}
			}
			// Navigate throught Manor windows
			else if(_command.startsWith("manor_menu_select?"))
			{
				L2Object object = activeChar.getTarget();
				if(object instanceof L2NpcInstance)
				{
					((L2NpcInstance) object).onBypassFeedback(activeChar, _command);
				}
			}
			else if(_command.startsWith("bbs_"))
			{
				CommunityBoard.getInstance().handleCommands(getClient(), _command);
			}
			else if(_command.startsWith("_bbs"))
			{
				CommunityBoard.getInstance().handleCommands(getClient(), _command);
			}
			else if(_command.startsWith("Quest "))
			{
				if(!activeChar.validateBypass(_command))
					return;

				L2PcInstance player = getClient().getActiveChar();
				if(player == null)
					return;

				String p = _command.substring(6).trim();
				int idx = p.indexOf(' ');

				if(idx < 0)
				{
					player.processQuestEvent(p, "");
				}
				else
				{
					player.processQuestEvent(p.substring(0, idx), p.substring(idx).trim());
				}
			}

			else if (_command.startsWith("eventvote ")) 
		 	{ 
		 	        EventManager.getInstance().addVote(activeChar, Integer.parseInt(_command.substring(10))); 
		 	} 
		 	else if (_command.startsWith("eventstats ")) 
		 	{
		 			Connection con = null;
		 			PreparedStatement statement = null;
		 			con = L2DatabaseFactory.getInstance().getConnection();
		 			statement = con.prepareStatement("SELECT characters.char_name, event_stats_full.* FROM event_stats_full INNER JOIN characters ON characters.obj_Id = event_stats_full.player ORDER BY event_stats_full.wins DESC");
		 			ResultSet rset = statement.executeQuery();
		 			if (!rset.last())
		 			{
		 				rset.close();
		 				statement.close();
		 				con.close();
		 				this.getClient().activeChar.sendMessage("Currently there are no statistics to show.");
		 				return;
		 			}
		 			rset.close();
	 				statement.close();
	 				con.close();
		 	        EventStats.getInstance().showHtml(Integer.parseInt(_command.substring(11)),activeChar); 
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
				html.setFile("data/html/eventinfo/"+eventId+".htm");
				activeChar.sendPacket(html);
				activeChar.sendPacket(ActionFailed.STATIC_PACKET);
			}
			
			// Jstar's Custom Bypass Caller!
			else if(_command.startsWith("custom_"))
			{
				L2PcInstance player = getClient().getActiveChar();
				CustomBypassHandler.getInstance().handleBypass(player, _command);
			}
			else if (_command.startsWith("OlympiadArenaChange"))
			{
				Olympiad.bypassChangeArena(_command, activeChar);
			}
		}
		catch(Exception e)
		{
			if(Config.ENABLE_ALL_EXCEPTIONS)
				e.printStackTrace();
			
			_log.log(Level.WARNING, "Bad RequestBypassToServer: ", e);
		}
		//		finally
		//		{
		//			activeChar.clearBypass();
		//		}
	}

	/**
	 * @param activeChar 
	 */
	private void comeHere(L2PcInstance activeChar)
	{
		L2Object obj = activeChar.getTarget();
		if(obj == null)
			return;

		if(obj instanceof L2NpcInstance)
		{
			L2NpcInstance temp = (L2NpcInstance) obj;
			temp.setTarget(activeChar);
			temp.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, new L2CharPosition(activeChar.getX(), activeChar.getY(), activeChar.getZ(), 0));
			//			temp.moveTo(player.getX(),player.getY(), player.getZ(), 0 );
		}

	}

	private void playerHelp(L2PcInstance activeChar, String path)
	{
		if(path.indexOf("..") != -1)
			return;

		String filename = "data/html/help/" + path;
		NpcHtmlMessage html = new NpcHtmlMessage(1);
		html.setFile(filename);
		activeChar.sendPacket(html);
	}

	@Override
	public String getType()
	{
		return "[C] 21 RequestBypassToServer";
	}
}
