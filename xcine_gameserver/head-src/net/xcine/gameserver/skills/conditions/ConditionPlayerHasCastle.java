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

import net.xcine.gameserver.model.L2Clan;
import net.xcine.gameserver.model.actor.instance.L2PcInstance;
import net.xcine.gameserver.skills.Env;

/**
 * The Class ConditionPlayerHasCastle.
 * @author MrPoke
 */
public final class ConditionPlayerHasCastle extends Condition
{
	private final int _castle;
	
	/**
	 * Instantiates a new condition player has castle.
	 * @param castle the castle
	 */
	public ConditionPlayerHasCastle(int castle)
	{
		_castle = castle;
	}
	
	/**
	 * @param env the env
	 * @return true, if successful
	 * @see net.xcine.gameserver.skills.conditions.Condition#testImpl(net.xcine.gameserver.skills.Env)
	 */
	@Override
	public boolean testImpl(Env env)
	{
		if (!(env.player instanceof L2PcInstance))
			return false;
		
		L2Clan clan = ((L2PcInstance) env.player).getClan();
		if (clan == null)
			return _castle == 0;
		
		// Any castle
		if (_castle == -1)
			return clan.hasCastle();
		
		return clan.getCastleId() == _castle;
	}
}