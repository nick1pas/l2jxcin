package net.sf.l2j.gameserver.handler.voicedcommandhandlers;

import net.sf.l2j.gameserver.handler.IVoicedCommandHandler;
import net.sf.l2j.gameserver.model.actor.instance.Player;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.network.serverpackets.ExShowVariationCancelWindow;
import net.sf.l2j.gameserver.network.serverpackets.ExShowVariationMakeWindow;

public class Augment implements IVoicedCommandHandler
{
	private static final String[] _voicedCommands =
	{
		"augment",
	};
	
	@Override
	public boolean useVoicedCommand(String command, Player activeChar, String target)
	{
		if (command.startsWith("augment"))
		{
			int cmdChoice = Integer.parseInt(command.substring(15, 16).trim());
			switch (cmdChoice)
			{
				case 1:
					activeChar.sendPacket(SystemMessageId.SELECT_THE_ITEM_TO_BE_AUGMENTED);
					activeChar.sendPacket(ExShowVariationMakeWindow.STATIC_PACKET);
					break;
				case 2:
					activeChar.sendPacket(SystemMessageId.SELECT_THE_ITEM_FROM_WHICH_YOU_WISH_TO_REMOVE_AUGMENTATION);
					activeChar.sendPacket(ExShowVariationCancelWindow.STATIC_PACKET);
					break;
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