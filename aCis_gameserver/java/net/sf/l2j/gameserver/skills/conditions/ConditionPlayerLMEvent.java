package net.sf.l2j.gameserver.skills.conditions;

import net.sf.l2j.gameserver.model.actor.instance.Player;
import net.sf.l2j.gameserver.model.entity.events.LMEvent;
import net.sf.l2j.gameserver.skills.Env;

/**
 * @author L0ngh0rn
 */
public class ConditionPlayerLMEvent extends Condition
{
	private final boolean _val;
	
	/**
	 * Instantiates a new condition player lm event.
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
