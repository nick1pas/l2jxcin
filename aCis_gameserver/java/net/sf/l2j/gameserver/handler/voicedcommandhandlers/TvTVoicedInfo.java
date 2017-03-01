package net.sf.l2j.gameserver.handler.voicedcommandhandlers;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.cache.HtmCache;
import net.sf.l2j.gameserver.handler.IVoicedCommandHandler;
import net.sf.l2j.gameserver.model.actor.instance.Player;
import net.sf.l2j.gameserver.model.entity.events.TvTEvent;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;

/**
 * Tvt info.
 * @author denser
 */
public class TvTVoicedInfo implements IVoicedCommandHandler
{
	private static final String[] _voicedCommands =
	{
		"tvtinfo",
		"tvtjoin",
		"tvtleave"
	};
	
	/**
	 * Set this to false and recompile script if you dont want to use string cache. This will decrease performance but will be more consistent against possible html editions during runtime Recompiling the script will get the new html would be enough too [DrHouse]
	 */
	private static final boolean USE_STATIC_HTML = true;
	private static final String HTML = HtmCache.getInstance().getHtm("data/html/mods/TvTEvent/Status.htm");
	
	@Override
	public boolean useVoicedCommand(String command, Player activeChar, String target)
	{
		if (command.equalsIgnoreCase("tvtinfo"))
		{
			if (TvTEvent.isStarting() || TvTEvent.isStarted())
			{
				String htmContent = (USE_STATIC_HTML && !HTML.isEmpty()) ? HTML : HtmCache.getInstance().getHtm("data/html/mods/TvTEvent/Status.htm");
				
				try
				{
					NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(5);
					
					npcHtmlMessage.setHtml(htmContent);
					// npcHtmlMessage.replace("%objectId%",
					// String.valueOf(getObjectId()));
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
					_log.warning("wrong TvT voiced: " + e);
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
