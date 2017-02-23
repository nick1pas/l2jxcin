package net.sf.l2j.gameserver.model.zone.type;

import net.sf.l2j.gameserver.model.actor.Character;
import net.sf.l2j.gameserver.model.zone.L2ZoneType;
import net.sf.l2j.gameserver.model.zone.ZoneId;

/**
 * An artifact's castle zone
 * @author Tryskell
 */
public class L2PrayerZone extends L2ZoneType
{
	public L2PrayerZone(int id)
	{
		super(id);
	}
	
	@Override
	protected void onEnter(Character character)
	{
		character.setInsideZone(ZoneId.CAST_ON_ARTIFACT, true);
	}
	
	@Override
	protected void onExit(Character character)
	{
		character.setInsideZone(ZoneId.CAST_ON_ARTIFACT, false);
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