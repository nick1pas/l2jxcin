package net.sf.l2j.gameserver.handler.admincommandhandlers;

import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;

import net.sf.l2j.gameserver.handler.IAdminCommandHandler;
import net.sf.l2j.gameserver.model.World;
import net.sf.l2j.gameserver.model.actor.instance.Player;
import net.sf.l2j.gameserver.network.clientpackets.Say2;
import net.sf.l2j.gameserver.network.serverpackets.CreatureSay;
import net.sf.l2j.gameserver.network.serverpackets.SocialAction;
import net.sf.l2j.gameserver.taskmanager.VipTimeTaskManager;

public class AdminVipStatus implements IAdminCommandHandler
{
	private static String[] _adminCommands = new String[]
	{
		"admin_vipon",
		"admin_vipoff"
	};
	
	@Override
	public boolean useAdminCommand(String command, Player activeChar)
	{
		StringTokenizer st = new StringTokenizer(command);
		st.nextToken();
		String player = "";
		int time = 1;
		Player target = null;
		if (st.hasMoreTokens())
		{
			player = st.nextToken();
			target = World.getInstance().getPlayer(player);
			if (st.hasMoreTokens())
			{
				try
				{
					time = Integer.parseInt(st.nextToken());
				}
				catch (NumberFormatException nfe)
				{
					activeChar.sendMessage("Invalid number format used: " + nfe);
					return false;
				}
			}
		}
		else if (activeChar.getTarget() != null && activeChar.getTarget() instanceof Player)
			target = (Player) activeChar.getTarget();
		
		if (command.startsWith("admin_vipon"))
		{
			if (target == null && player.equals(""))
			{
				activeChar.sendMessage("Usage: //vipon <char_name> [days]");
				return false;
			}
			if (target != null)
			{
				AdminVipStatus.AddVipStatus(target, activeChar, time);
				activeChar.sendMessage(target.getName() + " got VIP Status for " + time + " day(s).");
			}
		}
		else if (command.startsWith("admin_vipoff"))
		{
			if (target == null && player.equals(""))
			{
				activeChar.sendMessage("Usage: //removevip <char_name>");
				return false;
			}
			if (target != null)
			{
				if (target.isVipStatus())
				{
					AdminVipStatus.RemoveVipStatus(target);
					activeChar.sendMessage(target.getName() + "'s VIP Status has been removed.");
				}
				else
					activeChar.sendMessage("Player " + target.getName() + " is not an VIP.");
			}
		}
		return true;
	}
	
	public static void AddVipStatus(Player target, Player player, int time)
	{
		target.broadcastPacket(new SocialAction(target, 16));
		target.setVipStatus(true);
		VipTimeTaskManager.getInstance().add(target);
		long remainingTime = target.getMemos().getLong("TimeOfVip", 0);
		if (remainingTime > 0)
		{
			target.getMemos().set("TimeOfVip", remainingTime + TimeUnit.DAYS.toMillis(time));
			target.sendPacket(new CreatureSay(0, Say2.HERO_VOICE, "VIP Manager", "Dear " + player.getName() + ", your VIP status has been extended by " + time + " day(s)."));
		}
		else
		{
			target.getMemos().set("TimeOfVip", System.currentTimeMillis() + TimeUnit.DAYS.toMillis(time));
			target.sendPacket(new CreatureSay(0, Say2.HERO_VOICE, "VIP Manager", "Dear " + player.getName() + ", you got VIP Status for " + time + " day(s)."));
			target.broadcastUserInfo();
		}
	}
	
	public static void RemoveVipStatus(Player target)
	{
		VipTimeTaskManager.getInstance().remove(target);
		target.getMemos().set("TimeOfVip", 0);
		target.setVipStatus(false);
		target.broadcastPacket(new SocialAction(target, 13));
		target.broadcastUserInfo();
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return _adminCommands;
	}
}