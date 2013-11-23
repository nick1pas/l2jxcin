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
package net.xcine.gameserver.handler.admincommandhandlers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.xcine.Config;
import net.xcine.L2DatabaseFactory;
import net.xcine.gameserver.datatables.GmListTable;
import net.xcine.gameserver.datatables.SkillTable;
import net.xcine.gameserver.handler.IAdminCommandHandler;
import net.xcine.gameserver.model.L2Object;
import net.xcine.gameserver.model.L2Skill;
import net.xcine.gameserver.model.L2World;
import net.xcine.gameserver.model.actor.instance.L2PcInstance;
import net.xcine.gameserver.network.clientpackets.Say2;
import net.xcine.gameserver.network.serverpackets.CreatureSay;

/**
 * Give / remove aio status to player
 * Changes name color and title color if enabled
 *
 * Uses:
 * setaio [<player_name>] [<time_duration in days>]
 *
 * If <player_name> is not specified, the current target player is used.
 *
 * @author KhayrusS && SweeTs
 *
 */
public class AdminAio implements IAdminCommandHandler
{
       private static String[] _adminCommands = { "admin_setaio", "admin_removeaio" };
       private final static Logger _log = Logger.getLogger(AdminAio.class.getName());

       @Override
       public boolean useAdminCommand(String command, L2PcInstance activeChar)
      {
               if (command.startsWith("admin_setaio"))
               {
                       StringTokenizer str = new StringTokenizer(command);
                      
                       L2Object target = activeChar.getTarget();
                       L2PcInstance player = null;
                      
                       if (target != null && target instanceof L2PcInstance)
                               player = (L2PcInstance)target;
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
                                       player = L2World.getInstance().getPlayer(playername);
                                       doAio(activeChar, player, playername, time);
                               }
                               else
                               {
                                       String playername = player.getName();
                                       doAio(activeChar, player, playername, time);
                               }
                       }
                       catch(Exception e)
                       {
                               activeChar.sendMessage("Usage: //setaio <char_name> [time](in days)");
                       }

                       player.broadcastUserInfo();
                      
                      if(player.isAio())
                               return true;
               }
               else if(command.startsWith("admin_removeaio"))
               {
                       StringTokenizer str = new StringTokenizer(command);
                       L2Object target = activeChar.getTarget();

                       L2PcInstance player = null;

                       if (target != null && target instanceof L2PcInstance)
                               player = (L2PcInstance)target;
                       else
                               player = activeChar;

                       try
                       {
                               str.nextToken();
                              
                               if (str.hasMoreTokens())
                               {
                                       String playername = str.nextToken();
                                       player = L2World.getInstance().getPlayer(playername);
                                       removeAio(activeChar, player, playername);
                               }
                               else
                               {
                                       String playername = player.getName();
                                       removeAio(activeChar, player, playername);
                               }
                       }
                       catch(Exception e)
                       {
                               activeChar.sendMessage("Usage: //removeaio <char_name>");
                       }
                       player.broadcastUserInfo();
                      
                       if(!player.isAio())
                               return true;
               }
               return false;
       }

       public void doAio(L2PcInstance activeChar, L2PcInstance _player, String _playername, String _time)
       {
               int days = Integer.parseInt(_time);
              
               if (_player == null)
               {
                       activeChar.sendMessage("Character not found.");
                       return;
               }
              
               if (_player.isAio())
               {
                       activeChar.sendMessage("Player " + _playername + " is already an AIO.");
                       return;
               }

               if(days > 0)
               {
                       _player.getStat().addExp(_player.getStat().getExpForLevel(81));
                       L2Skill skill = SkillTable.getInstance().getInfo(2025, 1);
                       _player.doCast(skill);
                       _player.setAio(true);
                       _player.setEndTime("aio", days);
                       _player.sendPacket(new CreatureSay(0,Say2.HERO_VOICE,"System","Dear player, you are now an AIO, congratulations."));
                      
                       try (Connection con = L2DatabaseFactory.getInstance().getConnection())
                       {
                               PreparedStatement statement = con.prepareStatement("UPDATE characters SET aio=1, aio_end=? WHERE obj_id=?");
                               statement.setLong(1, _player.getAioEndTime());
                               statement.setInt(2, _player.getObjectId());
                               statement.execute();
                               statement.close();

                               if(Config.ALLOW_AIO_NCOLOR && _player.isAio())
                                       _player.getAppearance().setNameColor(Config.AIO_NCOLOR);

                               if(Config.ALLOW_AIO_TCOLOR && _player.isAio())
                                       _player.getAppearance().setTitleColor(Config.AIO_TCOLOR);

                               _player.rewardAioSkills();
                              
                               if(Config.ALLOW_AIO_ITEM && _player.isAio())
                               {
                                       _player.getInventory().addItem("", Config.AIO_ITEMID, 1, _player, null);
                                       _player.getInventory().equipItem(_player.getInventory().getItemByItemId(Config.AIO_ITEMID));
                                      
                               }
                               _player.broadcastUserInfo();
                               _player.sendSkillList();
                              
                               GmListTable.broadcastMessageToGMs("GM "+ activeChar.getName()+ " set an AIO status for player "+ _playername + " for " + _time + " day(s)");
                       }
                       catch (Exception e)
                       {
                               _log.log(Level.WARNING,"Something went wrong, check log folder for details", e);
                       }
               }
               else
               {
                       removeAio(activeChar, _player, _playername);
               }
       }

       public void removeAio(L2PcInstance activeChar, L2PcInstance _player, String _playername)
       {
               _player.setAio(false);
               _player.setAioEndTime(0);

               try (Connection con = L2DatabaseFactory.getInstance().getConnection())
               {
                       PreparedStatement statement = con.prepareStatement("UPDATE characters SET Aio=0, Aio_end=0 WHERE obj_id=?");
                       statement.setInt(1, _player.getObjectId());
                       statement.execute();
                       statement.close();

                       _player.lostAioSkills();
                       _player.removeExpAndSp(6299994999L, 366666666);
                      
                       if(Config.ALLOW_AIO_ITEM && activeChar.isAio() == false)
                       {
                               _player.getInventory().destroyItemByItemId("", Config.AIO_ITEMID, 1, _player, null);
                               _player.getWarehouse().destroyItemByItemId("", Config.AIO_ITEMID, 1, _player, null);
                       }
                       _player.getAppearance().setNameColor(0xFFFFFF);
                       _player.getAppearance().setTitleColor(0xFFFFFF);
                       _player.broadcastUserInfo();
                       _player.sendSkillList();
                       GmListTable.broadcastMessageToGMs("GM "+activeChar.getName()+" removed Aio status of player "+ _playername);
               }
               catch (Exception e)
               {
                       _log.log(Level.WARNING,"Something went wrong, check log folder for details", e);
               }
       }

       @Override
       public String[] getAdminCommandList()
       {
               return _adminCommands;
       }
}