package net.sf.l2j.gameserver.skills.conditions;

import net.sf.l2j.gameserver.model.actor.instance.Player;
import net.sf.l2j.gameserver.model.entity.events.DMEvent;
import net.sf.l2j.gameserver.skills.Env;

/**
 * @author L0ngh0rn
 *
 */
public class ConditionPlayerDMEvent extends Condition
{
	private final boolean _val;

	/**
	 * Instantiates a new condition player dm event.
	 *
	 * @param val the boolean
	 */
	public ConditionPlayerDMEvent(boolean val)
	{
		_val = val;
	}

	@Override
	public boolean testImpl(Env env)
	{
		final Player player = env.getPlayer();
		if (player == null || !DMEvent.isStarted())
			return !_val;

		return (DMEvent.isPlayerParticipant(player) == _val);
	}
}
