package net.sf.l2j.gameserver.model.entity.achievementengine.conditions;

import net.sf.l2j.gameserver.model.actor.instance.Player;
import net.sf.l2j.gameserver.model.entity.achievementengine.Condition;

public class Castle extends Condition
{
	public Castle(Object value)
	{
		super(value);
		setName("Have Castle");
	}
	
	@Override
	public boolean meetConditionRequirements(Player player)
	{
		if (getValue() == null)
			return false;
		
		if (player.getClan() != null)
		{
			if ((player.isCastleLord(1)) || (player.isCastleLord(2)) || (player.isCastleLord(3)) || (player.isCastleLord(4)) || (player.isCastleLord(5)) || (player.isCastleLord(6)) || (player.isCastleLord(7)) || (player.isCastleLord(8)) || (player.isCastleLord(9)))
				return true;
		}
		return false;
	}
}