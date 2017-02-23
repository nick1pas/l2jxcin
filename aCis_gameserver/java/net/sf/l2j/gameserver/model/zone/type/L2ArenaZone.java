package net.sf.l2j.gameserver.model.zone.type;

import net.sf.l2j.gameserver.model.actor.Character;
import net.sf.l2j.gameserver.model.actor.instance.Player;
import net.sf.l2j.gameserver.model.zone.L2SpawnZone;
import net.sf.l2j.gameserver.model.zone.ZoneId;
import net.sf.l2j.gameserver.network.SystemMessageId;

/**
 * An arena
 * @author durgus
 */
public class L2ArenaZone extends L2SpawnZone
{
	public L2ArenaZone(int id)
	{
		super(id);
	}
	
	@Override
	protected void onEnter(Character character)
	{
		if (character instanceof Player)
		{
			if (!character.isInsideZone(ZoneId.PVP))
				((Player) character).sendPacket(SystemMessageId.ENTERED_COMBAT_ZONE);
		}
		
		character.setInsideZone(ZoneId.PVP, true);
		character.setInsideZone(ZoneId.NO_SUMMON_FRIEND, true);
	}
	
	@Override
	protected void onExit(Character character)
	{
		character.setInsideZone(ZoneId.PVP, false);
		character.setInsideZone(ZoneId.NO_SUMMON_FRIEND, false);
		
		if (character instanceof Player)
		{
			if (!character.isInsideZone(ZoneId.PVP))
				((Player) character).sendPacket(SystemMessageId.LEFT_COMBAT_ZONE);
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