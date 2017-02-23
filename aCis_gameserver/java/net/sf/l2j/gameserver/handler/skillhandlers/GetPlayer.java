package net.sf.l2j.gameserver.handler.skillhandlers;

import net.sf.l2j.gameserver.handler.ISkillHandler;
import net.sf.l2j.gameserver.model.L2Object;
import net.sf.l2j.gameserver.model.L2Skill;
import net.sf.l2j.gameserver.model.actor.Character;
import net.sf.l2j.gameserver.model.actor.instance.Player;
import net.sf.l2j.gameserver.templates.skills.L2SkillType;

/**
 * Mobs can teleport players to them.
 */
public class GetPlayer implements ISkillHandler
{
	private static final L2SkillType[] SKILL_IDS =
	{
		L2SkillType.GET_PLAYER
	};
	
	@Override
	public void useSkill(Character activeChar, L2Skill skill, L2Object[] targets)
	{
		if (activeChar.isAlikeDead())
			return;
		
		for (L2Object target : targets)
		{
			final Player victim = target.getActingPlayer();
			if (victim == null || victim.isAlikeDead())
				continue;
			
			victim.teleToLocation(activeChar.getX(), activeChar.getY(), activeChar.getZ(), 0);
		}
	}
	
	@Override
	public L2SkillType[] getSkillIds()
	{
		return SKILL_IDS;
	}
}