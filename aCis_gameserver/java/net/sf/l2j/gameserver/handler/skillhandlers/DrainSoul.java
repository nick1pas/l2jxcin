package net.sf.l2j.gameserver.handler.skillhandlers;

import net.sf.l2j.gameserver.handler.ISkillHandler;
import net.sf.l2j.gameserver.model.L2Object;
import net.sf.l2j.gameserver.model.L2Skill;
import net.sf.l2j.gameserver.model.actor.Attackable;
import net.sf.l2j.gameserver.model.actor.Character;
import net.sf.l2j.gameserver.model.actor.instance.Player;
import net.sf.l2j.gameserver.scripting.QuestState;
import net.sf.l2j.gameserver.templates.skills.L2SkillType;

/**
 * @author _drunk_
 */
public class DrainSoul implements ISkillHandler
{
	private static final String qn = "Q350_EnhanceYourWeapon";
	
	private static final L2SkillType[] SKILL_IDS =
	{
		L2SkillType.DRAIN_SOUL
	};
	
	@Override
	public void useSkill(Character activeChar, L2Skill skill, L2Object[] targets)
	{
		// Check player.
		if (activeChar == null || activeChar.isDead() || !(activeChar instanceof Player))
			return;
		
		// Check quest condition.
		final Player player = (Player) activeChar;
		QuestState st = player.getQuestState(qn);
		if (st == null || !st.isStarted())
			return;
		
		// Get target.
		L2Object target = targets[0];
		if (target == null || !(target instanceof Attackable))
			return;
		
		// Check monster.
		final Attackable mob = (Attackable) target;
		if (mob.isDead())
			return;
		
		// Range condition, cannot be higher than skill's effectRange.
		if (!player.isInsideRadius(mob, skill.getEffectRange(), true, true))
			return;
		
		// Register.
		mob.registerAbsorber(player);
	}
	
	@Override
	public L2SkillType[] getSkillIds()
	{
		return SKILL_IDS;
	}
}