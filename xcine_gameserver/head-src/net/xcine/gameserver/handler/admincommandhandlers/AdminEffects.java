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
package net.xcine.gameserver.handler.admincommandhandlers;

import java.util.StringTokenizer;

import net.xcine.Config;
import net.xcine.gameserver.datatables.SkillTable;
import net.xcine.gameserver.handler.IAdminCommandHandler;
import net.xcine.gameserver.model.L2Object;
import net.xcine.gameserver.model.L2World;
import net.xcine.gameserver.model.actor.L2Character;
import net.xcine.gameserver.model.actor.L2Npc;
import net.xcine.gameserver.model.actor.L2Summon;
import net.xcine.gameserver.model.actor.instance.L2ChestInstance;
import net.xcine.gameserver.model.actor.instance.L2PcInstance;
import net.xcine.gameserver.network.SystemMessageId;
import net.xcine.gameserver.network.serverpackets.AbstractNpcInfo.NpcInfo;
import net.xcine.gameserver.network.serverpackets.Earthquake;
import net.xcine.gameserver.network.serverpackets.ExRedSky;
import net.xcine.gameserver.network.serverpackets.L2GameServerPacket;
import net.xcine.gameserver.network.serverpackets.MagicSkillUse;
import net.xcine.gameserver.network.serverpackets.PlaySound;
import net.xcine.gameserver.network.serverpackets.SignsSky;
import net.xcine.gameserver.network.serverpackets.SocialAction;
import net.xcine.gameserver.network.serverpackets.StopMove;
import net.xcine.gameserver.network.serverpackets.SunRise;
import net.xcine.gameserver.network.serverpackets.SunSet;
import net.xcine.gameserver.util.Broadcast;

/**
 * This class handles following admin commands: <li>hide = makes yourself invisible or visible <li>earthquake = causes an earthquake of a given intensity and duration around you <li>gmspeed = temporary Super Haste effect. <li>para/unpara = paralyze/remove paralysis from target <li>
 * para_all/unpara_all = same as para/unpara, affects the whole world. <li>polyself/unpolyself = makes you look as a specified mob. <li>changename = temporary change name <li>social = forces an L2Character instance to broadcast social action packets. <li>effect = forces an L2Character instance to
 * broadcast MSU packets. <li>abnormal = force changes over an L2Character instance's abnormal state. <li>play_sound/play_sounds = Music broadcasting related commands <li>atmosphere = sky change related commands.
 */
public class AdminEffects implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_hide",
		"admin_earthquake",
		"admin_earthquake_menu",
		"admin_gmspeed",
		"admin_gmspeed_menu",
		"admin_unpara_all",
		"admin_para_all",
		"admin_unpara",
		"admin_para",
		"admin_unpara_all_menu",
		"admin_para_all_menu",
		"admin_unpara_menu",
		"admin_para_menu",
		"admin_changename",
		"admin_changename_menu",
		"admin_social",
		"admin_social_menu",
		"admin_effect",
		"admin_effect_menu",
		"admin_abnormal",
		"admin_abnormal_menu",
		"admin_play_sounds",
		"admin_play_sound",
		"admin_atmosphere",
		"admin_atmosphere_menu"
	};
	
	@Override
	public boolean useAdminCommand(String command, L2PcInstance activeChar)
	{
		StringTokenizer st = new StringTokenizer(command);
		st.nextToken();
		
		if (command.startsWith("admin_hide"))
		{
			if (!activeChar.getAppearance().getInvisible())
			{
				activeChar.getAppearance().setInvisible();
				activeChar.decayMe();
				activeChar.broadcastUserInfo();
				activeChar.spawnMe();
			}
			else
			{
				activeChar.getAppearance().setVisible();
				activeChar.broadcastUserInfo();
			}
		}
		else if (command.startsWith("admin_earthquake"))
		{
			try
			{
				String val1 = st.nextToken();
				int intensity = Integer.parseInt(val1);
				String val2 = st.nextToken();
				int duration = Integer.parseInt(val2);
				Earthquake eq = new Earthquake(activeChar.getX(), activeChar.getY(), activeChar.getZ(), intensity, duration);
				activeChar.broadcastPacket(eq);
			}
			catch (Exception e)
			{
				activeChar.sendMessage("Use: //earthquake <intensity> <duration>");
			}
		}
		else if (command.startsWith("admin_atmosphere"))
		{
			try
			{
				String type = st.nextToken();
				String state = st.nextToken();
				adminAtmosphere(type, state, activeChar);
			}
			catch (Exception ex)
			{
			}
		}
		else if (command.equals("admin_play_sounds"))
			AdminHelpPage.showHelpPage(activeChar, "songs/songs.htm");
		else if (command.startsWith("admin_play_sounds"))
		{
			try
			{
				AdminHelpPage.showHelpPage(activeChar, "songs/songs" + command.substring(17) + ".htm");
			}
			catch (StringIndexOutOfBoundsException e)
			{
			}
		}
		else if (command.startsWith("admin_play_sound"))
		{
			try
			{
				playAdminSound(activeChar, command.substring(17));
			}
			catch (StringIndexOutOfBoundsException e)
			{
			}
		}
		else if (command.startsWith("admin_para") || command.startsWith("admin_para_menu"))
		{
			String type = "1";
			try
			{
				type = st.nextToken();
			}
			catch (Exception e)
			{
			}
			try
			{
				L2Object target = activeChar.getTarget();
				L2Character player = null;
				if (target instanceof L2Character)
				{
					player = (L2Character) target;
					if (type.equals("1"))
						player.startAbnormalEffect(0x0400);
					else
						player.startAbnormalEffect(0x0800);
					player.setIsParalyzed(true);
					StopMove sm = new StopMove(player);
					player.sendPacket(sm);
					player.broadcastPacket(sm);
				}
			}
			catch (Exception e)
			{
			}
		}
		else if (command.equals("admin_unpara") || command.equals("admin_unpara_menu"))
		{
			try
			{
				L2Object target = activeChar.getTarget();
				L2Character player = null;
				if (target instanceof L2Character)
				{
					player = (L2Character) target;
					player.stopAbnormalEffect((short) 0x0400);
					player.stopAbnormalEffect((short) 0x0800);
					player.setIsParalyzed(false);
				}
			}
			catch (Exception e)
			{
			}
		}
		else if (command.startsWith("admin_para_all"))
		{
			for (L2PcInstance player : activeChar.getKnownList().getKnownPlayers())
			{
				if (!player.isGM())
				{
					player.startAbnormalEffect(0x0400);
					player.setIsParalyzed(true);
					StopMove sm = new StopMove(player);
					player.sendPacket(sm);
					player.broadcastPacket(sm);
				}
			}
		}
		else if (command.startsWith("admin_unpara_all"))
		{
			for (L2PcInstance player : activeChar.getKnownList().getKnownPlayers())
			{
				player.stopAbnormalEffect(0x0400);
				player.setIsParalyzed(false);
			}
		}
		else if (command.startsWith("admin_gmspeed"))
		{
			try
			{
				int val = Integer.parseInt(st.nextToken());
				activeChar.stopSkillEffects(7029);
				if (val >= 1 && val <= 4)
					activeChar.doCast(SkillTable.getInstance().getInfo(7029, val));
			}
			catch (Exception e)
			{
				activeChar.sendMessage("Use: //gmspeed value (0-4).");
			}
			finally
			{
				activeChar.updateEffectIcons();
			}
		}
		else if (command.startsWith("admin_changename"))
		{
			try
			{
				String name = st.nextToken();
				String oldName = "null";
				
				L2Object target = activeChar.getTarget();
				L2Character player = null;
				
				if (target instanceof L2Character)
				{
					player = (L2Character) target;
					oldName = player.getName();
				}
				else
				{
					player = activeChar;
					oldName = activeChar.getName();
				}
				
				if (player instanceof L2PcInstance)
					L2World.getInstance().removeFromAllPlayers((L2PcInstance) player);
				
				player.setName(name);
				
				if (player instanceof L2PcInstance)
				{
					L2World.getInstance().addVisibleObject(player, null);
					((L2PcInstance) player).broadcastUserInfo();
				}
				else if (player instanceof L2Npc)
					player.broadcastPacket(new NpcInfo((L2Npc) player, null));
				
				activeChar.sendMessage("Changed name from " + oldName + " to " + name + ".");
			}
			catch (Exception e)
			{
			}
		}
		else if (command.startsWith("admin_social"))
		{
			try
			{
				String target = null;
				L2Object obj = activeChar.getTarget();
				if (st.countTokens() == 2)
				{
					int social = Integer.parseInt(st.nextToken());
					target = st.nextToken();
					if (target != null)
					{
						L2PcInstance player = L2World.getInstance().getPlayer(target);
						if (player != null)
						{
							if (performSocial(social, player, activeChar))
								activeChar.sendMessage(player.getName() + " was affected by your request.");
						}
						else
						{
							try
							{
								int radius = Integer.parseInt(target);
								for (L2Object object : activeChar.getKnownList().getKnownObjects())
									if (activeChar.isInsideRadius(object, radius, false, false))
										performSocial(social, object, activeChar);
								activeChar.sendMessage(radius + " units radius affected by your request.");
							}
							catch (NumberFormatException nbe)
							{
								activeChar.sendMessage("Incorrect parameter");
							}
						}
					}
				}
				else if (st.countTokens() == 1)
				{
					int social = Integer.parseInt(st.nextToken());
					if (obj == null)
						obj = activeChar;
					if (performSocial(social, obj, activeChar))
						activeChar.sendMessage(obj.getName() + " was affected by your request.");
					else
						activeChar.sendPacket(SystemMessageId.NOTHING_HAPPENED);
				}
				else if (!command.contains("menu"))
					activeChar.sendMessage("Usage: //social <social_id> [player_name|radius]");
			}
			catch (Exception e)
			{
				if (Config.DEBUG)
					e.printStackTrace();
			}
		}
		else if (command.startsWith("admin_abnormal"))
		{
			try
			{
				String target = null;
				L2Object obj = activeChar.getTarget();
				if (st.countTokens() == 2)
				{
					String parm = st.nextToken();
					int abnormal = Integer.decode("0x" + parm);
					target = st.nextToken();
					if (target != null)
					{
						L2PcInstance player = L2World.getInstance().getPlayer(target);
						if (player != null)
						{
							if (performAbnormal(abnormal, player))
								activeChar.sendMessage(player.getName() + "'s abnormal status was affected by your request.");
							else
								activeChar.sendPacket(SystemMessageId.NOTHING_HAPPENED);
						}
						else
						{
							try
							{
								int radius = Integer.parseInt(target);
								for (L2Object object : activeChar.getKnownList().getKnownObjects())
									if (activeChar.isInsideRadius(object, radius, false, false))
										performAbnormal(abnormal, object);
								activeChar.sendMessage(radius + " units radius affected by your request.");
							}
							catch (NumberFormatException nbe)
							{
								activeChar.sendMessage("Usage: //abnormal <hex_abnormal_mask> [player|radius]");
							}
						}
					}
				}
				else if (st.countTokens() == 1)
				{
					int abnormal = Integer.decode("0x" + st.nextToken());
					if (obj == null)
						obj = activeChar;
					if (performAbnormal(abnormal, obj))
						activeChar.sendMessage(obj.getName() + "'s abnormal status was affected by your request.");
					else
						activeChar.sendPacket(SystemMessageId.NOTHING_HAPPENED);
				}
				else if (!command.contains("menu"))
					activeChar.sendMessage("Usage: //abnormal <abnormal_mask> [player_name|radius]");
			}
			catch (Exception e)
			{
				if (Config.DEBUG)
					e.printStackTrace();
			}
		}
		else if (command.startsWith("admin_effect"))
		{
			try
			{
				L2Object obj = activeChar.getTarget();
				int level = 1, hittime = 1;
				int skill = Integer.parseInt(st.nextToken());
				
				if (st.hasMoreTokens())
					level = Integer.parseInt(st.nextToken());
				if (st.hasMoreTokens())
					hittime = Integer.parseInt(st.nextToken());
				if (obj == null)
					obj = activeChar;
				if (!(obj instanceof L2Character))
					activeChar.sendPacket(SystemMessageId.INCORRECT_TARGET);
				else
				{
					L2Character target = (L2Character) obj;
					target.broadcastPacket(new MagicSkillUse(target, activeChar, skill, level, hittime, 0));
					activeChar.sendMessage(obj.getName() + " performs MSU " + skill + "/" + level + " by your request.");
				}
			}
			catch (Exception e)
			{
				activeChar.sendMessage("Usage: //effect skill [level | level hittime]");
			}
		}
		if (command.contains("menu"))
			showMainPage(activeChar, command);
		return true;
	}
	
	/**
	 * @param action bitmask that should be applied over target's abnormal
	 * @param target
	 * @return <i>true</i> if target's abnormal state was affected , <i>false</i> otherwise.
	 */
	private static boolean performAbnormal(int action, L2Object target)
	{
		if (target instanceof L2Character)
		{
			L2Character character = (L2Character) target;
			if ((character.getAbnormalEffect() & action) == action)
				character.stopAbnormalEffect(action);
			else
				character.startAbnormalEffect(action);
			return true;
		}
		return false;
	}
	
	private static boolean performSocial(int action, L2Object target, L2PcInstance activeChar)
	{
		try
		{
			if (target instanceof L2Character)
			{
				if ((target instanceof L2Summon) || (target instanceof L2ChestInstance))
				{
					activeChar.sendPacket(SystemMessageId.NOTHING_HAPPENED);
					return false;
				}
				if ((target instanceof L2Npc) && (action < 1 || action > 3))
				{
					activeChar.sendPacket(SystemMessageId.NOTHING_HAPPENED);
					return false;
				}
				if ((target instanceof L2PcInstance) && (action < 2 || action > 16))
				{
					activeChar.sendPacket(SystemMessageId.NOTHING_HAPPENED);
					return false;
				}
				L2Character character = (L2Character) target;
				character.broadcastPacket(new SocialAction(character, action));
			}
			else
				return false;
		}
		catch (Exception e)
		{
		}
		
		return true;
	}
	
	private static void adminAtmosphere(String type, String state, L2PcInstance activeChar)
	{
		L2GameServerPacket packet = null;
		
		if (type.equals("signsky"))
		{
			if (state.equals("dawn"))
				packet = new SignsSky(2);
			else if (state.equals("dusk"))
				packet = new SignsSky(1);
		}
		else if (type.equals("sky"))
		{
			if (state.equals("night"))
				packet = SunSet.STATIC_PACKET;
			else if (state.equals("day"))
				packet = SunRise.STATIC_PACKET;
			else if (state.equals("red"))
				packet = new ExRedSky(10);
		}
		else
			activeChar.sendMessage("Usage: //atmosphere <signsky dawn|dusk>|<sky day|night|red>");
		if (packet != null)
			Broadcast.toAllOnlinePlayers(packet);
	}
	
	private static void playAdminSound(L2PcInstance activeChar, String sound)
	{
		PlaySound _snd;
		
		if (sound.contains("."))
			_snd = new PlaySound(sound);
		else
			_snd = new PlaySound(1, sound, 0, 0, 0, 0, 0);
		
		activeChar.sendPacket(_snd);
		activeChar.broadcastPacket(_snd);
		activeChar.sendMessage("Playing " + sound + ".");
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
	
	private static void showMainPage(L2PcInstance activeChar, String command)
	{
		String filename = "effects_menu";
		if (command.contains("abnormal"))
			filename = "abnormal";
		else if (command.contains("social"))
			filename = "social";
		AdminHelpPage.showHelpPage(activeChar, filename + ".htm");
	}
}