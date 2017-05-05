package net.sf.l2j.gameserver.model.entity.achievementengine.conditions;

import net.sf.l2j.gameserver.model.actor.instance.Player;
import net.sf.l2j.gameserver.model.entity.achievementengine.Condition;

public class MaxMp extends Condition
{
	public MaxMp(Object value)
	{
		super(value);
		setName("Max MP");
	}
	
	@Override
	public boolean meetConditionRequirements(Player player)
	{
		if (getValue() == null)
			return false;
		
		int val = Integer.parseInt(getValue().toString());
		
		return player.getMaxMp() >= val;
	}
}