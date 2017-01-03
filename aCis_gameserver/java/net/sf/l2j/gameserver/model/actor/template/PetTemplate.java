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
package net.sf.l2j.gameserver.model.actor.template;

import java.util.Map;

import net.sf.l2j.gameserver.model.PetDataEntry;
import net.sf.l2j.gameserver.templates.StatsSet;

public final class PetTemplate extends NpcTemplate
{
	public static final int MAX_LOAD = 54510;
	
	private final int _food1;
	private final int _food2;
	
	private final double _autoFeedLimit;
	private final double _hungryLimit;
	private final double _unsummonLimit;
	
	private Map<Integer, PetDataEntry> _dataEntries;
	
	public PetTemplate(StatsSet set)
	{
		super(set);
		
		_food1 = set.getInteger("food1");
		_food2 = set.getInteger("food2");
		
		_autoFeedLimit = set.getDouble("autoFeedLimit");
		_hungryLimit = set.getDouble("hungryLimit");
		_unsummonLimit = set.getDouble("unsummonLimit");
		
		_dataEntries = set.getMap("petData");
	}
	
	/**
	 * @return the itemId corresponding to first food type, if any.
	 */
	public int getFood1()
	{
		return _food1;
	}
	
	/**
	 * @return the itemId corresponding to second food type, if any.
	 */
	public int getFood2()
	{
		return _food2;
	}
	
	/**
	 * @return the auto feed limit, used for automatic use of food from pet's inventory (happens if % is reached). The value is under 1.0 format for easier management.
	 */
	public double getAutoFeedLimit()
	{
		return _autoFeedLimit;
	}
	
	/**
	 * @return the hungry limit, used to lower stats (pet is weaker if % reached). The value is under 1.0 format for easier management.
	 */
	public double getHungryLimit()
	{
		return _hungryLimit;
	}
	
	/**
	 * @return the unsummon limit, used to check unsummon case (can't unsummon if % reached). The value is under 1.0 format for easier management.
	 */
	public double getUnsummonLimit()
	{
		return _unsummonLimit;
	}
	
	/**
	 * @param level : The level of pet to retrieve. It can be either actual or any other level.
	 * @return the PetDataEntry corresponding to the level parameter.
	 */
	public PetDataEntry getPetDataEntry(int level)
	{
		return _dataEntries.get(level);
	}
	
	/**
	 * @param itemId : The itemId to check.
	 * @return true if at least one template food id matches with the parameter.
	 */
	public boolean canEatFood(int itemId)
	{
		return _food1 == itemId || _food2 == itemId;
	}
}