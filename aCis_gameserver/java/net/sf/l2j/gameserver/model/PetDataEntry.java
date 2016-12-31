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
package net.sf.l2j.gameserver.model;

public class PetDataEntry
{
	private final long _maxExp;
	
	private final int _maxMeal;
	private final int _expType;
	private final int _mealInBattle;
	private final int _mealInNormal;
	
	private final double _pAtk;
	private final double _pDef;
	private final double _mAtk;
	private final double _mDef;
	private final double _maxHp;
	private final double _maxMp;
	
	private final float _hpRegen;
	private final float _mpRegen;
	
	private final int _ssCount;
	private final int _spsCount;
	
	public PetDataEntry(long maxExp, int maxMeal, int expType, int mealInBattle, int mealInNormal, double pAtk, double pDef, double mAtk, double mDef, double maxHp, double maxMp, float hpRegen, float mpRegen, int ssCount, int spsCount)
	{
		_maxExp = maxExp;
		
		_maxMeal = maxMeal;
		_expType = expType;
		_mealInBattle = mealInBattle;
		_mealInNormal = mealInNormal;
		
		_pAtk = pAtk;
		_pDef = pDef;
		_mAtk = mAtk;
		_mDef = mDef;
		_maxHp = maxHp;
		_maxMp = maxMp;
		
		_hpRegen = hpRegen;
		_mpRegen = mpRegen;
		
		_ssCount = ssCount;
		_spsCount = spsCount;
	}
	
	public long getMaxExp()
	{
		return _maxExp;
	}
	
	public int getMaxMeal()
	{
		return _maxMeal;
	}
	
	public int getExpType()
	{
		return _expType;
	}
	
	public int getMealInBattle()
	{
		return _mealInBattle;
	}
	
	public int getMealInNormal()
	{
		return _mealInNormal;
	}
	
	public double getPAtk()
	{
		return _pAtk;
	}
	
	public double getPDef()
	{
		return _pDef;
	}
	
	public double getMAtk()
	{
		return _mAtk;
	}
	
	public double getMDef()
	{
		return _mDef;
	}
	
	public double getMaxHp()
	{
		return _maxHp;
	}
	
	public double getMaxMp()
	{
		return _maxMp;
	}
	
	public float getHpRegen()
	{
		return _hpRegen;
	}
	
	public float getMpRegen()
	{
		return _mpRegen;
	}
	
	public int getSsCount()
	{
		return _ssCount;
	}
	
	public int getSpsCount()
	{
		return _spsCount;
	}
}
