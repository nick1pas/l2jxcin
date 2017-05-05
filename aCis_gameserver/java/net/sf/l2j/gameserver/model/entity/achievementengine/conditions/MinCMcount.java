package net.sf.l2j.gameserver.model.entity.achievementengine.conditions;

import net.sf.l2j.gameserver.model.actor.instance.Player;
import net.sf.l2j.gameserver.model.entity.achievementengine.Condition;

public class MinCMcount extends Condition
{
	public MinCMcount(Object value)
	{
		super(value);
		setName("Clan Members Count");
	}
	
	@Override
	public boolean meetConditionRequirements(Player player)
	{
		if (getValue() == null)		
			return false;
		
		if (player.getClan() != null)
		{
			int val = Integer.parseInt(getValue().toString());
			
			if (player.getClan().getMembersCount() >= val)
				return true;
		}
		return false;
	}
}