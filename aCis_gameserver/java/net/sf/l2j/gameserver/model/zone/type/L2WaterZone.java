package net.sf.l2j.gameserver.model.zone.type;

import net.sf.l2j.gameserver.model.actor.Character;
import net.sf.l2j.gameserver.model.actor.Npc;
import net.sf.l2j.gameserver.model.actor.instance.Player;
import net.sf.l2j.gameserver.model.zone.L2ZoneType;
import net.sf.l2j.gameserver.model.zone.ZoneId;
import net.sf.l2j.gameserver.network.serverpackets.AbstractNpcInfo.NpcInfo;
import net.sf.l2j.gameserver.network.serverpackets.ServerObjectInfo;

public class L2WaterZone extends L2ZoneType
{
	public L2WaterZone(int id)
	{
		super(id);
	}
	
	@Override
	protected void onEnter(Character character)
	{
		character.setInsideZone(ZoneId.WATER, true);
		
		if (character instanceof Player)
			((Player) character).broadcastUserInfo();
		else if (character instanceof Npc)
		{
			for (Player player : character.getKnownType(Player.class))
			{
				if (character.getMoveSpeed() == 0)
					player.sendPacket(new ServerObjectInfo((Npc) character, player));
				else
					player.sendPacket(new NpcInfo((Npc) character, player));
			}
		}
	}
	
	@Override
	protected void onExit(Character character)
	{
		character.setInsideZone(ZoneId.WATER, false);
		
		if (character instanceof Player)
			((Player) character).broadcastUserInfo();
		else if (character instanceof Npc)
		{
			for (Player player : character.getKnownType(Player.class))
			{
				if (character.getMoveSpeed() == 0)
					player.sendPacket(new ServerObjectInfo((Npc) character, player));
				else
					player.sendPacket(new NpcInfo((Npc) character, player));
			}
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
	
	public int getWaterZ()
	{
		return getZone().getHighZ();
	}
}