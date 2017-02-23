package net.sf.l2j.gameserver.model.zone.type;

import net.sf.l2j.gameserver.model.actor.Character;
import net.sf.l2j.gameserver.model.zone.L2ZoneType;
import net.sf.l2j.gameserver.model.zone.ZoneId;

/**
 * Zone for scripts.
 * @author durgus
 */
public class L2ScriptZone extends L2ZoneType
{
	public L2ScriptZone(int id)
	{
		super(id);
	}
	
	@Override
	protected void onEnter(Character character)
	{
		character.setInsideZone(ZoneId.SCRIPT, true);
	}
	
	@Override
	protected void onExit(Character character)
	{
		character.setInsideZone(ZoneId.SCRIPT, false);
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