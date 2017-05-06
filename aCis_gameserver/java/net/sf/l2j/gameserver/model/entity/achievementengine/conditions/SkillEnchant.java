package net.sf.l2j.gameserver.model.entity.achievementengine.conditions;

import net.sf.l2j.gameserver.model.L2Skill;
import net.sf.l2j.gameserver.model.actor.instance.Player;
import net.sf.l2j.gameserver.model.entity.achievementengine.Condition;

public class SkillEnchant extends Condition
{
	public SkillEnchant(Object value)
	{
		super(value);
		setName("Skill Enchant");
	}
	
	@Override
	public boolean meetConditionRequirements(Player player)
	{
		if (getValue() == null)
			return false;
		
		for (L2Skill s : player.getSkills().values())
		{
			String lvl = String.valueOf(s.getLevel());
			if (lvl.length() <= 2)
				continue;
			if (Integer.parseInt(lvl.substring(1)) >= Integer.parseInt(getValue().toString()))
				return true;
		}
		return false;
	}
}