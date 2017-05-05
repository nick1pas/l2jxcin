package net.sf.l2j.gameserver.model.entity.achievementengine.conditions;

import net.sf.l2j.gameserver.model.actor.instance.Player;
import net.sf.l2j.gameserver.model.entity.achievementengine.Condition;

public class CursedWeapon extends Condition
{
	public CursedWeapon(Object value)
	{
		super(value);
		setName("Cursed Weapon");
	}
	
	@Override
	public boolean meetConditionRequirements(Player player)
	{
		if (getValue() == null)
			return false;
		
		return player.isCursedWeaponEquipped();
	}
}