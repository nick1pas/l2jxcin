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
package net.sf.l2j.gameserver.model.item.instance;

import net.sf.l2j.gameserver.model.item.instance.ItemInstance.ItemState;
import net.sf.l2j.gameserver.model.item.kind.Item;

/**
 * Get all information from ItemInstance to generate ItemInfo.
 */
public class ItemInfo
{
	private int _objectId;
	private Item _item;
	private int _enchant;
	private int _augmentation;
	private int _count;
	private int _price;
	private int _type1;
	private int _type2;
	private int _equipped;
	private ItemState _change;
	private int _mana;
	
	/**
	 * Get all information from ItemInstance to generate ItemInfo.
	 * @param item The item instance.
	 */
	public ItemInfo(ItemInstance item)
	{
		if (item == null)
			return;

		_objectId = item.getObjectId();
		_item = item.getItem();
		_enchant = item.getEnchantLevel();
		if (item.isAugmented())
			_augmentation = item.getAugmentation().getAugmentationId();
		else
			_augmentation = 0;
		_count = item.getCount();
		_type1 = item.getCustomType1();
		_type2 = item.getCustomType2();
		_equipped = item.isEquipped() ? 1 : 0;
		_change = item.getLastChange();
		_mana = item.getMana();
	}
	
	public ItemInfo(ItemInstance item, ItemState change)
	{
		if (item == null)
			return;
		
		_objectId = item.getObjectId();
		_item = item.getItem();
		_enchant = item.getEnchantLevel();
		if (item.isAugmented())
			_augmentation = item.getAugmentation().getAugmentationId();
		else
			_augmentation = 0;
		
		_count = item.getCount();
		_type1 = item.getCustomType1();
		_type2 = item.getCustomType2();
		_equipped = item.isEquipped() ? 1 : 0;
		_change = change;
		_mana = item.getMana();
	}
	
	public int getObjectId()
	{
		return _objectId;
	}
	
	public Item getItem()
	{
		return _item;
	}
	
	public int getEnchant()
	{
		return _enchant;
	}
	
	public int getAugmentationBoni()
	{
		return _augmentation;
	}
	
	public int getCount()
	{
		return _count;
	}
	
	public int getPrice()
	{
		return _price;
	}
	
	public int getCustomType1()
	{
		return _type1;
	}
	
	public int getCustomType2()
	{
		return _type2;
	}
	
	public int getEquipped()
	{
		return _equipped;
	}
	
	public ItemState getChange()
	{
		return _change;
	}
	
	public int getMana()
	{
		return _mana;
	}
}