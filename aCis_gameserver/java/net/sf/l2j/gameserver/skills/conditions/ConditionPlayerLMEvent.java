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
package net.sf.l2j.gameserver.skills.conditions;

import net.sf.l2j.gameserver.model.actor.instance.Player;
import net.sf.l2j.gameserver.model.entity.events.LMEvent;
import net.sf.l2j.gameserver.skills.Env;

/**
 * @author L0ngh0rn
 *
 */
public class ConditionPlayerLMEvent extends Condition
{
	private final boolean _val;

	/**
	 * Instantiates a new condition player lm event.
	 *
	 * @param val the boolean
	 */
	public ConditionPlayerLMEvent(boolean val)
	{
		_val = val;
	}

	@Override
	public boolean testImpl(Env env)
	{
		final Player player = env.getPlayer();
		if (player == null || !LMEvent.isStarted())
			return !_val;

		return (LMEvent.isPlayerParticipant(player) == _val);
	}
}
