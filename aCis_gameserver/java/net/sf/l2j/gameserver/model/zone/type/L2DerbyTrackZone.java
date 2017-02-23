package net.sf.l2j.gameserver.model.zone.type;

import net.sf.l2j.gameserver.model.actor.Character;
import net.sf.l2j.gameserver.model.actor.Playable;
import net.sf.l2j.gameserver.model.zone.ZoneId;

/**
 * The Monster Derby Track Zone
 * @author durgus
 */
public class L2DerbyTrackZone extends L2PeaceZone
{
	public L2DerbyTrackZone(int id)
	{
		super(id);
	}
	
	@Override
	protected void onEnter(Character character)
	{
		if (character instanceof Playable)
		{
			character.setInsideZone(ZoneId.MONSTER_TRACK, true);
			character.setInsideZone(ZoneId.PEACE, true);
			character.setInsideZone(ZoneId.NO_SUMMON_FRIEND, true);
		}
	}
	
	@Override
	protected void onExit(Character character)
	{
		if (character instanceof Playable)
		{
			character.setInsideZone(ZoneId.MONSTER_TRACK, false);
			character.setInsideZone(ZoneId.PEACE, false);
			character.setInsideZone(ZoneId.NO_SUMMON_FRIEND, false);
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