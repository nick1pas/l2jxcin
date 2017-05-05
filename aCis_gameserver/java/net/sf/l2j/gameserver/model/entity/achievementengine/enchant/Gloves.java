package net.sf.l2j.gameserver.model.entity.achievementengine.enchant;

import net.sf.l2j.gameserver.model.actor.instance.Player;
import net.sf.l2j.gameserver.model.entity.achievementengine.Condition;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;
import net.sf.l2j.gameserver.model.itemcontainer.Inventory;

/**
 * @author Matim,Wallister
 * @version v1
 */
public class Gloves extends Condition
{
	public Gloves(Object value)
	{
		super(value);
		setName("Gloves");
	}
	
	@Override
	public boolean meetConditionRequirements(Player player)
	{
		if (getValue() == null)
			return false;
		
		int val = Integer.parseInt(getValue().toString());
		ItemInstance armor = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_GLOVES);
		if (armor != null)
		{
			if (armor.getEnchantLevel() >= val)
				return true;
		}
		return false;
	}
}