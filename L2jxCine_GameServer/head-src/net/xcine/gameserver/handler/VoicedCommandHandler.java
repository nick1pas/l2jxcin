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
package net.xcine.gameserver.handler;

import java.util.Map;
import java.util.logging.Logger;

import javolution.util.FastMap;
import net.xcine.Config;
import net.xcine.gameserver.GameServer;
import net.xcine.gameserver.handler.voicedcommandhandlers.AwayCmd;
import net.xcine.gameserver.handler.voicedcommandhandlers.BankingCmd;
import net.xcine.gameserver.handler.voicedcommandhandlers.CTFCmd;
import net.xcine.gameserver.handler.voicedcommandhandlers.DMCmd;
import net.xcine.gameserver.handler.voicedcommandhandlers.OfflineShop;
import net.xcine.gameserver.handler.voicedcommandhandlers.Online;
import net.xcine.gameserver.handler.voicedcommandhandlers.StatsCmd;
import net.xcine.gameserver.handler.voicedcommandhandlers.TvTCmd;
import net.xcine.gameserver.handler.voicedcommandhandlers.Voting;
import net.xcine.gameserver.handler.voicedcommandhandlers.Wedding;

/**
 * This class ...
 * @version $Revision: 1.1.4.6 $ $Date: 2009/05/12 19:44:09 $
 */
public class VoicedCommandHandler
{
	private static Logger _log = Logger.getLogger(GameServer.class.getName());
	
	private static VoicedCommandHandler _instance;
	
	private Map<String, IVoicedCommandHandler> _datatable;
	
	public static VoicedCommandHandler getInstance()
	{
		if (_instance == null)
		{
			_instance = new VoicedCommandHandler();
		}
		
		return _instance;
	}
	
	private VoicedCommandHandler()
	{
		_datatable = new FastMap<>();
		
		registerVoicedCommandHandler(new Voting());
		
		if (Config.BANKING_SYSTEM_ENABLED)
		{
			registerVoicedCommandHandler(new BankingCmd());
		}
		
		if (Config.CTF_COMMAND)
		{
			registerVoicedCommandHandler(new CTFCmd());
		}
		
		if (Config.TVT_COMMAND)
		{
			registerVoicedCommandHandler(new TvTCmd());
		}
		
		if (Config.DM_COMMAND)
		{
			registerVoicedCommandHandler(new DMCmd());
		}
		
		if (Config.L2JMOD_ALLOW_WEDDING)
		{
			registerVoicedCommandHandler(new Wedding());
		}
		
		registerVoicedCommandHandler(new StatsCmd());

		if (Config.ALLOW_AWAY_STATUS)
		{
			registerVoicedCommandHandler(new AwayCmd());
		}

		if (Config.ALLOW_ONLINE_VIEW)
		{
			registerVoicedCommandHandler(new Online());
		}
		
		if (Config.OFFLINE_TRADE_ENABLE && Config.OFFLINE_COMMAND2)
		{
			registerVoicedCommandHandler(new OfflineShop());
		}
		
		_log.config("VoicedCommandHandler: Loaded " + _datatable.size() + " handlers.");
		
	}
	
	public void registerVoicedCommandHandler(IVoicedCommandHandler handler)
	{
		String[] ids = handler.getVoicedCommandList();
		
		for (String id : ids)
		{
			if (Config.DEBUG)
			{
				_log.fine("Adding handler for command " + id);
			}
			
			_datatable.put(id, handler);
		}
		
		ids = null;
	}
	
	public IVoicedCommandHandler getVoicedCommandHandler(String voicedCommand)
	{
		String command = voicedCommand;
		
		if (voicedCommand.indexOf(" ") != -1)
		{
			command = voicedCommand.substring(0, voicedCommand.indexOf(" "));
		}
		
		if (Config.DEBUG)
		{
			_log.fine("getting handler for command: " + command + " -> " + (_datatable.get(command) != null));
		}
		
		return _datatable.get(command);
	}
	
	/**
	 * @return
	 */
	public int size()
	{
		return _datatable.size();
	}
}