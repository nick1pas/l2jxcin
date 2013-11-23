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
package net.xcine.gameserver.network.serverpackets;

import java.util.ArrayList;
import java.util.List;

import net.xcine.gameserver.model.ItemInfo;
import net.xcine.gameserver.model.L2ItemInstance;
import net.xcine.gameserver.templates.item.L2Item;

/**
 * @author Advi
 */
public class InventoryUpdate extends L2GameServerPacket
{
	private List<ItemInfo> _items;
	
	public InventoryUpdate()
	{
		_items = new ArrayList<>();
	}
	
	public InventoryUpdate(List<ItemInfo> items)
	{
		_items = items;
	}
	
	public void addItem(L2ItemInstance item)
	{
		if (item != null)
			_items.add(new ItemInfo(item));
	}
	
	public void addNewItem(L2ItemInstance item)
	{
		if (item != null)
			_items.add(new ItemInfo(item, 1));
	}
	
	public void addModifiedItem(L2ItemInstance item)
	{
		if (item != null)
			_items.add(new ItemInfo(item, 2));
	}
	
	public void addRemovedItem(L2ItemInstance item)
	{
		if (item != null)
			_items.add(new ItemInfo(item, 3));
	}
	
	public void addItems(List<L2ItemInstance> items)
	{
		if (items != null)
			for (L2ItemInstance item : items)
				if (item != null)
					_items.add(new ItemInfo(item));
	}
	
	@Override
	protected final void writeImpl()
	{
		writeC(0x27);
		writeH(_items.size());
		
		for (ItemInfo temp : _items)
		{
			if (temp == null || temp.getItem() == null)
				continue;
			
			L2Item item = temp.getItem();
			
			writeH(temp.getChange());
			writeH(item.getType1());
			writeD(temp.getObjectId());
			writeD(item.getItemId());
			writeD(temp.getCount());
			writeH(item.getType2());
			writeH(temp.getCustomType1());
			writeH(temp.getEquipped());
			writeD(item.getBodyPart());
			writeH(temp.getEnchant());
			writeH(temp.getCustomType2());
			writeD(temp.getAugmentationBoni());
			writeD(temp.getMana());
		}
		_items.clear();
		_items = null;
	}
}