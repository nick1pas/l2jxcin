/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */
package net.xcine.gameserver.skills.conditions;

import javolution.util.FastList;

import net.xcine.gameserver.model.actor.instance.L2NpcInstance;
import net.xcine.gameserver.skills.Env;

/**
 * @author nBd
 */
public class ConditionTargetRaceId extends Condition
{
	private final FastList<Integer> _raceIds;

	public ConditionTargetRaceId(FastList<Integer> raceId)
	{
		_raceIds = raceId;
	}

	@Override
	public boolean testImpl(Env env)
	{
		if(_raceIds==null || env.target==null || !(env.target instanceof L2NpcInstance))
			return false;

		L2NpcInstance target = (L2NpcInstance) env.target;
		if(target.getTemplate()!=null && target.getTemplate().race!=null ){
			return _raceIds.contains(((L2NpcInstance) env.target).getTemplate().race.ordinal()+1);
		}
		return false;
	}
}
