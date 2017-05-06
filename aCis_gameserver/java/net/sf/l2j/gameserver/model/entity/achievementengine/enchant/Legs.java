package net.sf.l2j.gameserver.model.entity.achievementengine.enchant;

import net.sf.l2j.gameserver.model.actor.instance.Player;
import net.sf.l2j.gameserver.model.entity.achievementengine.Condition;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;
import net.sf.l2j.gameserver.model.itemcontainer.Inventory;

public class Legs extends Condition
{
	public Legs(Object value)
	{
		super(value);
		setName("Legs");
	}
	
	@Override
	public boolean meetConditionRequirements(Player player)
	{
		if (getValue() == null)
			return false;
		
		int val = Integer.parseInt(getValue().toString());
		
		ItemInstance armor = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_LEGS);
		
		if (armor != null)
		{
			if (armor.getEnchantLevel() >= val)
				return true;
		}
		return false;
	}
}
