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
package net.xcine.gameserver.skills.effects;

import net.xcine.gameserver.model.L2Effect;
import net.xcine.gameserver.skills.Env;
import net.xcine.gameserver.templates.skills.L2EffectType;

public final class EffectSeed extends L2Effect
{
	private int _power = 1;
	
	public EffectSeed(Env env, EffectTemplate template)
	{
		super(env, template);
	}
	
	@Override
	public L2EffectType getEffectType()
	{
		return L2EffectType.SEED;
	}
	
	@Override
	public boolean onActionTime()
	{
		// just stop this effect
		return false;
	}
	
	public int getPower()
	{
		return _power;
	}
	
	public void increasePower()
	{
		_power++;
	}
}