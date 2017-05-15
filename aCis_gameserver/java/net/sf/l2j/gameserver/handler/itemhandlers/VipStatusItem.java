package net.sf.l2j.gameserver.handler.itemhandlers;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.handler.IItemHandler;
import net.sf.l2j.gameserver.handler.admincommandhandlers.AdminVipStatus;
import net.sf.l2j.gameserver.model.actor.Playable;
import net.sf.l2j.gameserver.model.actor.instance.Player;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.network.serverpackets.SocialAction;

/**
 * @author Baggos
 */
public class VipStatusItem implements IItemHandler
{
	@Override
	public void useItem(Playable playable, ItemInstance item, boolean forceUse)
	{
		if (Config.ENABLE_VIP_ITEM)
		{
			if (!(playable instanceof Player))
				return;
			
			Player activeChar = (Player) playable;
			if (activeChar.isVipStatus())
				activeChar.sendMessage("Your character has already VIP Status!");
			else if (activeChar.isInOlympiadMode())
				activeChar.sendPacket(SystemMessageId.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
			else
			{
				AdminVipStatus.AddVipStatus(activeChar, activeChar, Config.VIP_DAYS);
				activeChar.broadcastPacket(new SocialAction(activeChar, 16));
				playable.destroyItem("Consume", item.getObjectId(), 1, null, false);
			}
		}
	}
	
	public int getItemIds()
	{
		return Config.VIP_ITEM_ID;
	}
}