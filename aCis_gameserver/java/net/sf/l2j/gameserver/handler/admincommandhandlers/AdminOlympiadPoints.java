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
package net.sf.l2j.gameserver.handler.admincommandhandlers;

import net.sf.l2j.gameserver.handler.IAdminCommandHandler;
import net.sf.l2j.gameserver.model.L2Object;
import net.sf.l2j.gameserver.model.actor.instance.Player;
import net.sf.l2j.gameserver.model.olympiad.Olympiad;
import net.sf.l2j.gameserver.templates.StatsSet;

public class AdminOlympiadPoints implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_addolypoints",
		"admin_removeolypoints",
		"admin_setolypoints",
		"admin_getolypoints"
	};
	
	@Override
	public boolean useAdminCommand(String command, Player activeChar)
	{
		if (command.startsWith("admin_addolypoints"))
		{
			try
			{
				String val = command.substring(19);
				L2Object target = activeChar.getTarget();
				Player player = null;
				if (target instanceof Player)
				{
					player = (Player) target;
					if (player.isNoble())
					{
						StatsSet playerStat = Olympiad.getNobleStats(player.getObjectId());
						if (playerStat == null)
						{
							activeChar.sendMessage("Oops! This player hasn't played on Olympiad yet!");
							return false;
						}
						int oldpoints = Olympiad.getInstance().getNoblePoints(player.getObjectId());
						int points = oldpoints + Integer.parseInt(val);
						if (points > 100)
						{
							activeChar.sendMessage("You can't set more than 100 or less than 0 Olympiad points!");
							return false;
						}
						playerStat.set("olympiad_points", points);
						
						activeChar.sendMessage("Player " + player.getName() + " now has " + points + " Olympiad points.");
					}
					else
					{
						activeChar.sendMessage("Oops! This player is not noblesse!");
						return false;
					}
				}
				else
				{
					activeChar.sendMessage("Usage: target a player and write the amount of points you would like to add.");
					activeChar.sendMessage("Example: //addolypoints 10");
					activeChar.sendMessage("However, keep in mind that you can't have less than 0 or more than 100 points.");
				}
			}
			catch (StringIndexOutOfBoundsException e)
			{
				activeChar.sendMessage("Usage: //addolypoints <points>");
			}
		}
		else if (command.startsWith("admin_removeolypoints"))
		{
			try
			{
				String val = command.substring(22);
				L2Object target = activeChar.getTarget();
				Player player = null;
				if (target instanceof Player)
				{
					player = (Player) target;
					if (player.isNoble())
					{
						StatsSet playerStat = Olympiad.getNobleStats(player.getObjectId());
						if (playerStat == null)
						{
							activeChar.sendMessage("Oops! This player hasn't played on Olympiad yet!");
							return false;
						}
						int oldpoints = Olympiad.getInstance().getNoblePoints(player.getObjectId());
						int points = oldpoints - Integer.parseInt(val);
						if (points < 0)
							points = 0;
						playerStat.set("olympiad_points", points);
						activeChar.sendMessage("Player " + player.getName() + " now has " + points + " Olympiad points.");
					}
					else
					{
						activeChar.sendMessage("Oops! This player is not noblesse!");
						return false;
					}
				}
				else
				{
					activeChar.sendMessage("Usage: target a player and write the amount of points you would like to remove.");
					activeChar.sendMessage("Example: //removeolypoints 10");
					activeChar.sendMessage("However, keep in mind that you can't have less than 0 or more than 100 points.");
				}
			}
			catch (StringIndexOutOfBoundsException e)
			{
				activeChar.sendMessage("Usage: //removeolypoints points");
			}
		}
		else if (command.startsWith("admin_setolypoints"))
		{
			try
			{
				String val = command.substring(19);
				L2Object target = activeChar.getTarget();
				Player player = null;
				if (target instanceof Player)
				{
					player = (Player) target;
					if (player.isNoble())
					{
						StatsSet playerStat = Olympiad.getNobleStats(player.getObjectId());
						if (playerStat == null)
						{
							activeChar.sendMessage("Oops! This player hasn't played on Olympiad yet!");
							return false;
						}
						if (Integer.parseInt(val) < 1 && Integer.parseInt(val) > 100)
						{
							activeChar.sendMessage("You can't set more than 100 or less than 0 Olympiad points! or lower then 0");
							return false;
						}
						playerStat.set("olympiad_points", Integer.parseInt(val));
						activeChar.sendMessage("Player " + player.getName() + " now has " + Integer.parseInt(val) + " Olympiad points.");
					}
					else
					{
						activeChar.sendMessage("Oops! This player is not noblesse!");
						return false;
					}
				}
				else
				{
					activeChar.sendMessage("Usage: target a player and write the amount of points you would like to set.");
					activeChar.sendMessage("Example: //setolypoints 10");
					activeChar.sendMessage("However, keep in mind that you can't have less than 0 or more than 100 points.");
				}
			}
			catch (StringIndexOutOfBoundsException e)
			{
				activeChar.sendMessage("Usage: //setolypoints <points>");
			}
		}
		else if (command.startsWith("admin_getolypoints"))
		{
			try
			{
				L2Object target = activeChar.getTarget();
				Player player = null;
				if (target instanceof Player)
				{
					player = (Player) target;
					if (player.isNoble())
					{
						activeChar.sendMessage(">=========>>" + player.getName() + "<<=========");
						activeChar.sendMessage("   Match(s):" + Olympiad.getInstance().getCompetitionDone(player.getObjectId()));
						activeChar.sendMessage("   Win(s):" + Olympiad.getInstance().getCompetitionWon(activeChar.getObjectId()));
						activeChar.sendMessage("   Defeat(s):" + Olympiad.getInstance().getCompetitionLost(activeChar.getObjectId()));
						activeChar.sendMessage("   Point(s) " + Olympiad.getInstance().getNoblePoints(player.getObjectId()));
						activeChar.sendMessage(">=========>>" + player.getName() + "<<=========");
					}
					else
					{
						activeChar.sendMessage("Oops! This player is not noblesse!");
						return false;
					}
				}
				else
					activeChar.sendMessage("You must target a player to use the command.");
			}
			catch (StringIndexOutOfBoundsException e)
			{
				activeChar.sendMessage("Usage: //getolypoints");
			}
		}
		return true;
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}