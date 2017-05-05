package net.sf.l2j.gameserver.model.entity.achievementengine.conditions;

import java.util.Map;

import net.sf.l2j.gameserver.instancemanager.RaidBossPointsManager;
import net.sf.l2j.gameserver.model.actor.instance.Player;
import net.sf.l2j.gameserver.model.entity.achievementengine.Condition;

public class RaidKill extends Condition
{
	public RaidKill(Object value)
	{
		super(value);
		setName("Raid Kill");
	}
	
	@Override
	public boolean meetConditionRequirements(Player player)
	{
		if (getValue() == null)
			return false;
		
		int val = Integer.parseInt(getValue().toString());
		Map<Integer, Integer> list = RaidBossPointsManager.getInstance().getList(player);
		if (list != null)
		{
			for (int bid : list.keySet())
			{
				if (bid == val)
				{
					if (RaidBossPointsManager.getInstance().getList(player).get(bid) > 0)
						return true;
				}
			}
		}
		return false;
	}
}