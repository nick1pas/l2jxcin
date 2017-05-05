package net.sf.l2j.gameserver.model.entity.achievementengine.conditions;

import net.sf.l2j.gameserver.model.actor.instance.Player;
import net.sf.l2j.gameserver.model.entity.achievementengine.Condition;

public class Adena extends Condition
{
	public Adena(Object value)
	{
		super(value);
		setName("Adena");
	}
	
	@Override
	public boolean meetConditionRequirements(Player player)
	{
		if (getValue() == null)
			return false;
		
		long val = Integer.parseInt(getValue().toString());
		
		if (player.getInventory().getAdena() >= val)
			return true;
		return false;
	}
}