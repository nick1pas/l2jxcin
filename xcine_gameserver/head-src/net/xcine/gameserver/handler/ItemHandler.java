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
package net.xcine.gameserver.handler;

import gnu.trove.map.hash.TIntObjectHashMap;

import net.xcine.gameserver.handler.itemhandlers.BeastSoulShot;
import net.xcine.gameserver.handler.itemhandlers.BeastSpice;
import net.xcine.gameserver.handler.itemhandlers.BeastSpiritShot;
import net.xcine.gameserver.handler.itemhandlers.BlessedSpiritShot;
import net.xcine.gameserver.handler.itemhandlers.Book;
import net.xcine.gameserver.handler.itemhandlers.Elixir;
import net.xcine.gameserver.handler.itemhandlers.EnchantScrolls;
import net.xcine.gameserver.handler.itemhandlers.FishShots;
import net.xcine.gameserver.handler.itemhandlers.Harvester;
import net.xcine.gameserver.handler.itemhandlers.ItemSkills;
import net.xcine.gameserver.handler.itemhandlers.Maps;
import net.xcine.gameserver.handler.itemhandlers.MercTicket;
import net.xcine.gameserver.handler.itemhandlers.PaganKeys;
import net.xcine.gameserver.handler.itemhandlers.PetFood;
import net.xcine.gameserver.handler.itemhandlers.Recipes;
import net.xcine.gameserver.handler.itemhandlers.RollingDice;
import net.xcine.gameserver.handler.itemhandlers.ScrollOfResurrection;
import net.xcine.gameserver.handler.itemhandlers.Seed;
import net.xcine.gameserver.handler.itemhandlers.SevenSignsRecord;
import net.xcine.gameserver.handler.itemhandlers.SoulShots;
import net.xcine.gameserver.handler.itemhandlers.SpecialXMas;
import net.xcine.gameserver.handler.itemhandlers.SpiritShot;
import net.xcine.gameserver.handler.itemhandlers.SummonItems;
import net.xcine.gameserver.templates.item.L2EtcItem;

public class ItemHandler
{
	private final TIntObjectHashMap<IItemHandler> _datatable;
	
	public static ItemHandler getInstance()
	{
		return SingletonHolder._instance;
	}
	
	/**
	 * Returns the number of elements contained in datatable
	 * @return int : Size of the datatable
	 */
	public int size()
	{
		return _datatable.size();
	}
	
	/**
	 * Constructor of ItemHandler
	 */
	protected ItemHandler()
	{
		_datatable = new TIntObjectHashMap<>();
		registerItemHandler(new ScrollOfResurrection());
		registerItemHandler(new SoulShots());
		registerItemHandler(new SpiritShot());
		registerItemHandler(new BlessedSpiritShot());
		registerItemHandler(new BeastSoulShot());
		registerItemHandler(new BeastSpiritShot());
		registerItemHandler(new PaganKeys());
		registerItemHandler(new Maps());
		registerItemHandler(new Recipes());
		registerItemHandler(new RollingDice());
		registerItemHandler(new EnchantScrolls());
		registerItemHandler(new Book());
		registerItemHandler(new SevenSignsRecord());
		registerItemHandler(new ItemSkills());
		registerItemHandler(new Seed());
		registerItemHandler(new Harvester());
		registerItemHandler(new MercTicket());
		registerItemHandler(new FishShots());
		registerItemHandler(new PetFood());
		registerItemHandler(new SpecialXMas());
		registerItemHandler(new SummonItems());
		registerItemHandler(new BeastSpice());
		registerItemHandler(new Elixir());
	}
	
	public void registerItemHandler(IItemHandler handler)
	{
		_datatable.put(handler.getClass().getSimpleName().intern().hashCode(), handler);
	}
	
	public IItemHandler getItemHandler(L2EtcItem item)
	{
		if (item == null || item.getHandlerName() == null)
			return null;
		
		return _datatable.get(item.getHandlerName().hashCode());
	}
	
	private static class SingletonHolder
	{
		protected static final ItemHandler _instance = new ItemHandler();
	}
}