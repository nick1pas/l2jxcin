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

import java.util.ArrayList;

import net.xcine.gameserver.model.L2Clan;
import net.xcine.gameserver.model.actor.instance.L2PcInstance;
import net.xcine.gameserver.skills.Env;

/**
 * The Class ConditionPlayerHasClanHall.
 * @author MrPoke
 */
public final class ConditionPlayerHasClanHall extends Condition
{
	private final ArrayList<Integer> _clanHall;
	
	/**
	 * Instantiates a new condition player has clan hall.
	 * @param clanHall the clan hall
	 */
	public ConditionPlayerHasClanHall(ArrayList<Integer> clanHall)
	{
		_clanHall = clanHall;
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
			return (_clanHall.size() == 1 && _clanHall.get(0) == 0);
		
		// All Clan Hall
		if (_clanHall.size() == 1 && _clanHall.get(0) == -1)
			return clan.hasHideout();
		
		return _clanHall.contains(clan.getHideoutId());
	}
}