package net.sf.l2j.gameserver.model.entity.achievementengine.conditions;

import net.sf.l2j.gameserver.model.actor.instance.Player;
import net.sf.l2j.gameserver.model.entity.achievementengine.Condition;

public class ClanLeader extends Condition
{
	public ClanLeader(Object value)
	{
		super(value);
		setName("Be Clan Leader");
	}
	
	@Override
	public boolean meetConditionRequirements(Player player)
	{
		if (getValue() == null)
			return false;
		
		return (player.getClan() != null) && (player.isClanLeader());
	}
}