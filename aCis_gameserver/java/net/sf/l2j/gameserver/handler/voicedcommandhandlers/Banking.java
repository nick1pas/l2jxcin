package net.sf.l2j.gameserver.handler.voicedcommandhandlers;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.handler.IVoicedCommandHandler;
import net.sf.l2j.gameserver.model.actor.instance.Player;

public class Banking implements IVoicedCommandHandler
{
	private static final String[] _voicedCommands =
	{
		"bank",
		"withdraw",
		"deposit"
	};

	@Override
	public boolean useVoicedCommand(String command, Player activeChar, String params)
	{
		if (command.equalsIgnoreCase("bank"))
		{
			activeChar.sendMessage(".deposit (" + Config.BANKING_SYSTEM_ADENA + " Adena = " + Config.BANKING_SYSTEM_GOLDBARS + " Goldbar) / .withdraw (" + Config.BANKING_SYSTEM_GOLDBARS + " Goldbar = " + Config.BANKING_SYSTEM_ADENA + " Adena)");
		}
		else if (command.equalsIgnoreCase("deposit"))
		{
			if (activeChar.getInventory().getInventoryItemCount(57, 0) >= Config.BANKING_SYSTEM_ADENA)
			{
				if (!activeChar.reduceAdena("Goldbar", Config.BANKING_SYSTEM_ADENA, activeChar, false))
					return false;
				activeChar.getInventory().addItem("Goldbar", 3470, Config.BANKING_SYSTEM_GOLDBARS, activeChar, null);
				activeChar.getInventory().updateDatabase();
				activeChar.sendMessage("Thank you, you now have " + Config.BANKING_SYSTEM_GOLDBARS + " Goldbar(s), and " + Config.BANKING_SYSTEM_ADENA + " less adena.");
			}
			else
			{
				activeChar.sendMessage("You do not have enough Adena to convert to Goldbar(s), you need " + Config.BANKING_SYSTEM_ADENA + " Adena.");
			}
		}
		else if (command.equalsIgnoreCase("withdraw"))
		{
			if (activeChar.getInventory().getInventoryItemCount(3470, 0) >= Config.BANKING_SYSTEM_GOLDBARS)
			{
				if (!activeChar.destroyItemByItemId("Adena", 3470, Config.BANKING_SYSTEM_GOLDBARS, activeChar, false))
					return false;
				activeChar.getInventory().addAdena("Adena", Config.BANKING_SYSTEM_ADENA, activeChar, null);
				activeChar.getInventory().updateDatabase();
				activeChar.sendMessage("Thank you, you now have " + Config.BANKING_SYSTEM_ADENA + " Adena, and " + Config.BANKING_SYSTEM_GOLDBARS + " less Goldbar(s).");
			}
			else
			{
				activeChar.sendMessage("You do not have any Goldbars to turn into " + Config.BANKING_SYSTEM_ADENA + " Adena.");
			}
		}
		return true;
	}

	@Override
	public String[] getVoicedCommandList()
	{
		return _voicedCommands;
	}
}