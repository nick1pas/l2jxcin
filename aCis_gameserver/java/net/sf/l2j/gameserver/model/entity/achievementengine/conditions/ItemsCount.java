package net.sf.l2j.gameserver.model.entity.achievementengine.conditions;

import java.util.StringTokenizer;

import net.sf.l2j.gameserver.model.actor.instance.Player;
import net.sf.l2j.gameserver.model.entity.achievementengine.Condition;

public class ItemsCount extends Condition
{
	public ItemsCount(Object value)
	{
		super(value);
		setName("Items Count");
	}
	
	@Override
	public boolean meetConditionRequirements(Player player)
	{
		if (getValue() == null)
			return false;
		
		String s = getValue().toString();
		StringTokenizer st = new StringTokenizer(s, ",");
		int id = 0;
		int ammount = 0;
		try
		{
			id = Integer.parseInt(st.nextToken());
			ammount = Integer.parseInt(st.nextToken());
			if (player.getInventory().getInventoryItemCount(id, 0) >= ammount)
				return true;
		}
		catch (NumberFormatException nfe)
		{
			nfe.printStackTrace();
		}
		
		return false;
	}
}