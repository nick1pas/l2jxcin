/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package net.xcine.gameserver.model.itemcontainer;

import java.util.ArrayList;
import java.util.List;

import net.xcine.gameserver.datatables.ItemTable;
import net.xcine.gameserver.model.L2ItemInstance;
import net.xcine.gameserver.model.L2ItemInstance.ItemLocation;
import net.xcine.gameserver.model.L2Object;
import net.xcine.gameserver.model.L2World;
import net.xcine.gameserver.model.actor.instance.L2PcInstance;
import net.xcine.gameserver.model.actor.instance.L2PetInstance;
import net.xcine.gameserver.templates.item.L2EtcItemType;
import net.xcine.gameserver.templates.item.L2Item;

public class PetInventory extends Inventory
{
	private final L2PetInstance _owner;
	
	public PetInventory(L2PetInstance owner)
	{
		_owner = owner;
	}
	
	@Override
	public L2PetInstance getOwner()
	{
		return _owner;
	}
	
	@Override
	public int getOwnerId()
	{
		// gets the L2PcInstance-owner's ID
		int id;
		try
		{
			id = _owner.getOwner().getObjectId();
		}
		catch (NullPointerException e)
		{
			return 0;
		}
		return id;
	}
	
	/**
	 * Refresh the weight of equipment loaded
	 */
	@Override
	protected void refreshWeight()
	{
		super.refreshWeight();
		getOwner().updateAndBroadcastStatus(1);
	}
	
	public boolean validateCapacity(L2ItemInstance item)
	{
		int slots = 0;
		
		if (!(item.isStackable() && getItemByItemId(item.getItemId()) != null) && item.getItemType() != L2EtcItemType.HERB)
			slots++;
		
		return validateCapacity(slots);
	}
	
	@Override
	public boolean validateCapacity(int slots)
	{
		return (_items.size() + slots <= _owner.getInventoryLimit());
	}
	
	public boolean validateWeight(L2ItemInstance item, long count)
	{
		int weight = 0;
		
		L2Item template = ItemTable.getInstance().getTemplate(item.getItemId());
		if (template == null)
			return false;
		
		weight += count * template.getWeight();
		return validateWeight(weight);
	}
	
	@Override
	public boolean validateWeight(int weight)
	{
		return (_totalWeight + weight <= _owner.getMaxLoad());
	}
	
	@Override
	protected ItemLocation getBaseLocation()
	{
		return ItemLocation.PET;
	}
	
	@Override
	protected ItemLocation getEquipLocation()
	{
		return ItemLocation.PET_EQUIP;
	}
	
	@Override
	public void restore()
	{
		super.restore();
		
		// check for equipped items from other pets
		for (L2ItemInstance item : _items)
		{
			if (item.isEquipped())
			{
				if (!item.getItem().checkCondition(getOwner(), getOwner(), false))
					unEquipItemInSlot(item.getLocationSlot());
			}
		}
	}
	
	@Override
	public void deleteMe()
	{
		// Transfer items only if the items list is feeded.
		if (_items != null)
		{
			// Retrieves the master of the pet owning the inventory.
			final L2PcInstance petOwner = getOwner().getOwner();
			if (petOwner != null)
			{
				// Transfer each item to master's inventory.
				for (L2ItemInstance item : _items)
					getOwner().transferItem("return", item.getObjectId(), item.getCount(), petOwner.getInventory(), petOwner, getOwner());
			}
			
			// Create a clone version (from L2ItemInstance to L2Object) items used just after for remove purpose.
			List<L2Object> items = new ArrayList<L2Object>(_items);
			
			// Clear the internal inventory items list.
			_items.clear();
			
			// Drop those items from the world.
			L2World.getInstance().removeObjects(items);
		}
	}
}