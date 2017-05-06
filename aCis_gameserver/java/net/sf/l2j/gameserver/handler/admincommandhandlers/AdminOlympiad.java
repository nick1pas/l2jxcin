package net.sf.l2j.gameserver.handler.admincommandhandlers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.l2j.L2DatabaseFactory;
import net.sf.l2j.gameserver.handler.IAdminCommandHandler;
import net.sf.l2j.gameserver.model.World;
import net.sf.l2j.gameserver.model.WorldObject;
import net.sf.l2j.gameserver.model.actor.instance.Player;
import net.sf.l2j.gameserver.model.olympiad.Olympiad;
import net.sf.l2j.gameserver.network.clientpackets.Say2;
import net.sf.l2j.gameserver.network.serverpackets.CreatureSay;
import net.sf.l2j.gameserver.network.serverpackets.EtcStatusUpdate;

/**
 * This class handles following admin commands:
 * <ul>
 * <li>endoly : ends olympiads manually.</li>
 * <li>sethero : set the target as a eternal hero.</li>
 * <li>setnoble : set the target as a noble.</li>
 * </ul>
 **/
public class AdminOlympiad implements IAdminCommandHandler
{
	private static final Logger _log = Logger.getLogger(AdminOlympiad.class.getName());
	
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_endoly",
		"admin_sethero",
		"admin_removehero",
		"admin_setnoble"
	};
	
	@Override
	public boolean useAdminCommand(String command, Player activeChar)
	{
		if (command.startsWith("admin_endoly"))
		{
			Olympiad.getInstance().manualSelectHeroes();
			activeChar.sendMessage("Heroes have been formed.");
		}
		else if (command.startsWith("admin_sethero"))
		{
			StringTokenizer str = new StringTokenizer(command);
			WorldObject target = activeChar.getTarget();
			
			Player player = null;
			
			if (target != null && target instanceof Player)
				player = (Player) target;
			else
				player = activeChar;
			
			try
			{
				str.nextToken();
				String time = str.nextToken();
				if (str.hasMoreTokens())
				{
					String playername = time;
					time = str.nextToken();
					player = World.getInstance().getPlayer(playername);
					doHero(activeChar, player, playername, time);
				}
				else
				{
					String playername = player.getName();
					doHero(activeChar, player, playername, time);
				}
				if (!time.equals("0"))
				{
					player.sendMessage("You are now a Hero , congratulations!");
				}
			}
			catch (Exception e)
			{
				activeChar.sendMessage("Usage: //sethero <char_name> [time](in days)");
			}
			
			player.broadcastUserInfo();
			if (player.isHero())
				return true;
		}
		
		else if (command.startsWith("admin_removehero"))
		{
			StringTokenizer str = new StringTokenizer(command);
			WorldObject target = activeChar.getTarget();
			
			Player player = null;
			
			if (target != null && target instanceof Player)
				player = (Player) target;
			else
				player = activeChar;
			
			try
			{
				str.nextToken();
				if (str.hasMoreTokens())
				{
					String playername = str.nextToken();
					player = World.getInstance().getPlayer(playername);
					removeHero(activeChar, player, playername);
				}
				else
				{
					String playername = player.getName();
					removeHero(activeChar, player, playername);
				}
			}
			catch (Exception e)
			{
				activeChar.sendMessage("Usage: //removehero <char_name>");
			}
			player.broadcastUserInfo();
			if (!player.isHero())
				return true;
		}
		else if (command.startsWith("admin_setnoble"))
		{
			Player target = null;
			if (activeChar.getTarget() instanceof Player)
				target = (Player) activeChar.getTarget();
			else
				target = activeChar;
			
			target.setNoble(!target.isNoble(), true);
			activeChar.sendMessage("You have modified " + target.getName() + "'s noble status.");
		}
		
		return true;
	}
	
	public void doHero(Player activeChar, Player _player, String _playername, String _time)
	{
		int days = Integer.parseInt(_time);
		if (_player == null)
		{
			activeChar.sendMessage("not found char" + _playername);
			return;
		}
		
		if (days > 0)
		{
			_player.setHero(true);
			_player.setEndTime("hero", days);
			
			try (Connection con = L2DatabaseFactory.getInstance().getConnection())
			{
				PreparedStatement statement = con.prepareStatement("UPDATE characters SET hero=1, hero_end=? WHERE obj_id=?");
				statement.setLong(1, _player.getHeroEndTime());
				statement.setInt(2, _player.getObjectId());
				statement.execute();
				statement.close();
				con.close();
				
				_player.broadcastUserInfo();
				_player.sendPacket(new EtcStatusUpdate(_player));
				_player.sendPacket(new CreatureSay(0, Say2.HERO_VOICE, "System", "Dear player, you are now an Hero, congratulations."));
			}
			catch (Exception e)
			{
				_log.log(Level.SEVERE, "could not set hero stats of char:", e);
			}
		}
		else
		{
			removeHero(activeChar, _player, _playername);
		}
	}
	
	public void removeHero(Player activeChar, Player _player, String _playername)
	{
		_player.setHero(false);
		_player.setHeroEndTime(0);
		
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement("UPDATE characters SET hero=0, hero_end=0 WHERE obj_id=?");
			statement.setInt(1, _player.getObjectId());
			statement.execute();
			statement.close();
			con.close();
			
			_player.broadcastUserInfo();
			_player.sendPacket(new EtcStatusUpdate(_player));
			_player.sendPacket(new CreatureSay(0, Say2.PARTYROOM_COMMANDER, "System", "Your Hero period is over."));
		}
		catch (Exception e)
		{
			_log.log(Level.WARNING, "could not remove hero stats of char:", e);
		}
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}