package net.sf.l2j.gameserver.model.zone.type;

import net.sf.l2j.gameserver.model.actor.Character;
import net.sf.l2j.gameserver.model.actor.instance.Player;
import net.sf.l2j.gameserver.model.zone.L2ZoneType;
import net.sf.l2j.gameserver.model.zone.ZoneId;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.network.serverpackets.SystemMessage;

/**
 * A no landing zone
 * @author durgus
 */
public class L2NoLandingZone extends L2ZoneType
{
	private int dismountDelay = 5;
	
	public L2NoLandingZone(int id)
	{
		super(id);
	}
	
	@Override
	public void setParameter(String name, String value)
	{
		if (name.equals("dismountDelay"))
			dismountDelay = Integer.parseInt(value);
		else
			super.setParameter(name, value);
	}
	
	@Override
	protected void onEnter(Character character)
	{
		if (character instanceof Player)
		{
			character.setInsideZone(ZoneId.NO_LANDING, true);
			if (((Player) character).getMountType() == 2)
			{
				character.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.AREA_CANNOT_BE_ENTERED_WHILE_MOUNTED_WYVERN));
				((Player) character).enteredNoLanding(dismountDelay);
			}
		}
	}
	
	@Override
	protected void onExit(Character character)
	{
		if (character instanceof Player)
		{
			character.setInsideZone(ZoneId.NO_LANDING, false);
			if (((Player) character).getMountType() == 2)
				((Player) character).exitedNoLanding();
		}
	}
	
	@Override
	public void onDieInside(Character character)
	{
	}
	
	@Override
	public void onReviveInside(Character character)
	{
	}
}