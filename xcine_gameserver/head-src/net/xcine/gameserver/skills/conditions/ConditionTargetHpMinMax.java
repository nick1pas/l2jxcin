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
package net.xcine.gameserver.skills.conditions;

import net.xcine.gameserver.skills.Env;

/**
 * Used for Trap skills.
 * @author Tryskell
 */
public class ConditionTargetHpMinMax extends Condition
{
	private final int _minHp, _maxHp;
	
	public ConditionTargetHpMinMax(int minHp, int maxHp)
	{
		_minHp = minHp;
		_maxHp = maxHp;
	}
	
	@Override
	public boolean testImpl(Env env)
	{
		if (env.target == null)
			return false;
		
		int _currentHp = (int) env.player.getCurrentHp() * 100 / env.player.getMaxHp();
		return _currentHp >= _minHp && _currentHp <= _maxHp;
	}
}