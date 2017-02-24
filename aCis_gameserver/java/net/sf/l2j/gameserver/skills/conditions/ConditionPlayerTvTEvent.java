package net.sf.l2j.gameserver.skills.conditions;

import net.sf.l2j.gameserver.model.actor.instance.Player;
import net.sf.l2j.gameserver.model.entity.events.TvTEvent;
import net.sf.l2j.gameserver.skills.Env;

/**
 * The Class ConditionPlayerTvTEvent.
 */
public class ConditionPlayerTvTEvent extends Condition
{
	private final boolean _val;
	
	/**
	 * Instantiates a new condition player tv t event.
	 *
	 * @param val the val
	 */
	public ConditionPlayerTvTEvent(boolean val)
	{
		_val = val;
	}
	
	@Override
	public boolean testImpl(Env env)
	{
		final Player player = env.getPlayer();
		if (player == null || !TvTEvent.isStarted())
			return !_val;
		
		return (TvTEvent.isPlayerParticipant(player.getObjectId()) == _val);
	}
}
