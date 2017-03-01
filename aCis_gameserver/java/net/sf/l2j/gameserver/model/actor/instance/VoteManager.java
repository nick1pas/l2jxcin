package net.sf.l2j.gameserver.model.actor.instance;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.handler.VoteHandler;
import net.sf.l2j.gameserver.model.actor.template.NpcTemplate;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;

/**
 * @author Reborn12
 */

public class VoteManager extends Folk
{
	public VoteManager(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}
	
	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if (command.startsWith("votetopzone"))
		{
			VoteHandler.tzvote(player);
		}
		else if (command.startsWith("votehopzone"))
		{
			VoteHandler.HZvote(player);
		}
		else if (command.startsWith("votenetwork"))
		{
			VoteHandler.NZvote(player);
		}
	}
	
	@Override
	public void showChatWindow(Player player)
	{
		if (Config.VOTE_MANAGER_ENABLED)
			mainWindow(player);
		else
			ShowOfflineWindow(player);
	}
	
	public void ShowOfflineWindow(Player player)
	{
		player.ActionF();
		String filename = "data/html/mods/voteManager/voteOffline.htm";
		NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
		html.setFile(filename);
		html.replace("%objectId%", String.valueOf(getObjectId()));
		html.replace("%charname%", player.getName());
		player.sendPacket(html);
	}
	
	public void mainWindow(Player player)
	{
		player.ActionF();
		String filename = "data/html/mods/voteManager/vote.htm";
		NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
		html.setFile(filename);
		html.replace("%objectId%", String.valueOf(getObjectId()));
		html.replace("%charname%", player.getName());
		html.replace("%whoisvoting%", VoteHandler.whoIsVoting());
		player.sendPacket(html);
	}
	
}
