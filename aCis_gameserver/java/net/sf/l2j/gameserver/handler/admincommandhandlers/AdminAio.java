package net.sf.l2j.gameserver.handler.admincommandhandlers;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.handler.IAdminCommandHandler;
import net.sf.l2j.gameserver.instancemanager.AioManager;
import net.sf.l2j.gameserver.model.L2Object;
import net.sf.l2j.gameserver.model.actor.instance.Player;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.network.serverpackets.ExShowScreenMessage;

/**
 * @author rapfersan92
 */
public class AdminAio implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_add_aio",
		"admin_update_aio",
		"admin_remove_aio"
	};
	
	@Override
	public boolean useAdminCommand(String command, Player activeChar)
	{
		final L2Object target = activeChar.getTarget();
		if (target == null || !(target instanceof Player))
		{
			activeChar.sendPacket(SystemMessageId.INCORRECT_TARGET);
			return false;
		}
		
		if (command.startsWith("admin_add_aio"))
		{
			try
			{
				int duration = Integer.parseInt(command.substring(14));
				addAio(activeChar, (Player) target, duration);
			}
			catch (Exception e)
			{
				activeChar.sendMessage("You must use //add_aio <days>");
			}
		}
		else if (command.startsWith("admin_update_aio"))
		{
			try
			{
				int duration = Integer.parseInt(command.substring(17));
				updateAio(activeChar, (Player) target, duration);
			}
			catch (Exception e)
			{
				activeChar.sendMessage("You must use //update_aio <days>");
			}
		}
		else if (command.equalsIgnoreCase("admin_remove_aio"))
			removeAio(activeChar, (Player) target);
		
		return true;
	}
	
	private static void addAio(Player activeChar, Player targetChar, int duration)
	{
		if (duration <= 0)
		{
			activeChar.sendMessage("The value you have entered is incorrect.");
			return;
		}
		else if (AioManager.getInstance().hasAioPrivileges(targetChar.getObjectId()))
		{
			activeChar.sendMessage("Your target already have aio privileges.");
			return;
		}
		else if (Config.LIST_AIO_RESTRICTED_CLASSES.contains(targetChar.getTemplate().getClassId().getId()))
		{
			activeChar.sendMessage("Your target cannot receive aio privileges with a character in their current class.");
			return;
		}
		else if (targetChar.isSubClassActive())
		{
			activeChar.sendMessage("Your target cannot receive aio privileges with a character in their subclass.");
			return;
		}
		
		AioManager.getInstance().addAio(targetChar.getObjectId(), System.currentTimeMillis() + duration * 86400000);
		activeChar.sendMessage("You have added the aio privileges to " + targetChar.getName() + ".");
		targetChar.sendPacket(new ExShowScreenMessage("Your aio privileges were added by the admin.", 10000));
	}
	
	private static void updateAio(Player activeChar, Player targetChar, int duration)
	{
		if (duration <= 0)
		{
			activeChar.sendMessage("The value you have entered is incorrect.");
			return;
		}
		else if (!AioManager.getInstance().hasAioPrivileges(targetChar.getObjectId()))
		{
			activeChar.sendMessage("Your target does not have aio privileges.");
			return;
		}
		
		AioManager.getInstance().updateAio(targetChar.getObjectId(), duration * 86400000);
		activeChar.sendMessage("You have updated aio privileges from " + targetChar.getName() + ".");
		targetChar.sendPacket(new ExShowScreenMessage("Your aio privileges were updated by the admin.", 10000));
	}
	
	private static void removeAio(Player activeChar, Player targetChar)
	{
		if (!AioManager.getInstance().hasAioPrivileges(targetChar.getObjectId()))
		{
			activeChar.sendMessage("Your target does not have aio privileges.");
			return;
		}
		
		AioManager.getInstance().removeAio(targetChar.getObjectId());
		activeChar.sendMessage("You have removed aio privileges from " + targetChar.getName() + ".");
		targetChar.sendPacket(new ExShowScreenMessage("Your aio privileges were removed by the admin.", 10000));
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}