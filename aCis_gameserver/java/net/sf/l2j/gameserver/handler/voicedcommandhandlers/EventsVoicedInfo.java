package net.sf.l2j.gameserver.handler.voicedcommandhandlers;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.cache.HtmCache;
import net.sf.l2j.gameserver.handler.IVoicedCommandHandler;
import net.sf.l2j.gameserver.model.actor.instance.Player;
import net.sf.l2j.gameserver.model.entity.events.DMEvent;
import net.sf.l2j.gameserver.model.entity.events.LMEvent;
import net.sf.l2j.gameserver.model.entity.events.TvTEvent;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;

public class EventsVoicedInfo implements IVoicedCommandHandler
{
	private static final String[] _voicedCommands =
	{
		"lmjoin",
		"lmleave",
		"dminfo", 
		"dmjoin", 
		"dmleave",
		"tvtinfo",
		"tvtjoin",
		"tvtleave"
	};
	private static final boolean USE_STATIC_HTML = true;
	private static final String HTML_DM = HtmCache.getInstance().getHtm("data/html/mods/DMEvent/Status.htm");
	private static final String HTML_TVT = HtmCache.getInstance().getHtm("data/html/mods/TvTEvent/Status.htm");
	
	@Override
	public boolean useVoicedCommand(String command, Player activeChar, String target)
	{
		if (command.equalsIgnoreCase("lmjoin"))
		{
			LMEvent.onBypass("lm_event_participation", activeChar);
		}
		else if (command.equalsIgnoreCase("lmleave"))
		{
			LMEvent.onBypass("lm_event_remove_participation", activeChar);
		}
		else if (command.equalsIgnoreCase("dminfo"))
		{
			if (DMEvent.isStarting() || DMEvent.isStarted())
			{
				String htmContent = (USE_STATIC_HTML && !HTML_DM.isEmpty()) ? HTML_DM : HtmCache.getInstance().getHtm("data/html/mods/DMEvent/Status.htm");			
				try
				{
					String[] firstPositions = DMEvent.getFirstPosition(Config.DM_REWARD_FIRST_PLAYERS);
					NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(5);					
					String htmltext = "";
					Boolean c = true;
					String c1 = "D9CC46";
					String c2 = "FFFFFF";
					if (firstPositions != null)
						for (int i = 0; i < firstPositions.length; i++)
						{
							String[] row = firstPositions[i].split("\\,");
							String color = (c ? c1 : c2);
							htmltext += "<tr>";
							htmltext += "<td width=\"35\" align=\"center\"><font color=\"" + color + "\">" + String.valueOf(i + 1) + "</font></td>";
							htmltext += "<td width=\"100\" align=\"left\"><font color=\"" + color + "\">" + row[0] + "</font></td>";
							htmltext += "<td width=\"125\" align=\"right\"><font color=\"" + color + "\">" + row[1] + "</font></td>";
							htmltext += "</tr>";
							c = !c;
						}					
					npcHtmlMessage.setHtml(htmContent);
					npcHtmlMessage.replace("%toprank%", htmltext);
					activeChar.sendPacket(npcHtmlMessage);	
				}
				catch (Exception e)
				{
					_log.warning("wrong Events voiced: " + e);
				}			
			}
			else
			{
				activeChar.ActionF();
			}
		}
		else if (command.equalsIgnoreCase("dmjoin"))
		{
			DMEvent.onBypass("dm_event_participation", activeChar);
		}
		else if (command.equalsIgnoreCase("dmleave"))
		{
			DMEvent.onBypass("dm_event_remove_participation", activeChar);
		}
		else if (command.equalsIgnoreCase("tvtinfo"))
		{
			if (TvTEvent.isStarting() || TvTEvent.isStarted())
			{
				String htmContent = (USE_STATIC_HTML && !HTML_TVT.isEmpty()) ? HTML_TVT : HtmCache.getInstance().getHtm("data/html/mods/TvTEvent/Status.htm");				
				try
				{
					NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(5);					
					npcHtmlMessage.setHtml(htmContent);
					npcHtmlMessage.replace("%team1name%", Config.TVT_EVENT_TEAM_1_NAME);
					npcHtmlMessage.replace("%team1playercount%", String.valueOf(TvTEvent.getTeamsPlayerCounts()[0]));
					npcHtmlMessage.replace("%team1points%", String.valueOf(TvTEvent.getTeamsPoints()[0]));
					npcHtmlMessage.replace("%team2name%", Config.TVT_EVENT_TEAM_2_NAME);
					npcHtmlMessage.replace("%team2playercount%", String.valueOf(TvTEvent.getTeamsPlayerCounts()[1]));
					npcHtmlMessage.replace("%team2points%", String.valueOf(TvTEvent.getTeamsPoints()[1]));
					activeChar.sendPacket(npcHtmlMessage);
				}
				catch (Exception e)
				{
					_log.warning("wrong Events voiced: " + e);
				}			
			}
			else
			{
				activeChar.ActionF();
			}
		}
		else if (command.equalsIgnoreCase("tvtjoin"))
		{
			TvTEvent.onBypass("tvt_event_participation", activeChar);
		}
		else if (command.equalsIgnoreCase("tvtleave"))
		{
			TvTEvent.onBypass("tvt_event_remove_participation", activeChar);
		}
		return true;
	}
	
	@Override
	public String[] getVoicedCommandList()
	{
		return _voicedCommands;
	}
}