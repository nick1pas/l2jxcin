package net.sf.l2j.gameserver.handler;

import java.util.logging.Logger;

import net.sf.l2j.gameserver.model.L2Object;
import net.sf.l2j.gameserver.model.L2Skill;
import net.sf.l2j.gameserver.model.actor.Character;
import net.sf.l2j.gameserver.templates.skills.L2SkillType;

public interface ISkillHandler
{
	public static Logger _log = Logger.getLogger(ISkillHandler.class.getName());
	
	/**
	 * this is the worker method that is called when using a skill.
	 * @param activeChar The Character who uses that skill.
	 * @param skill The skill object itself.
	 * @param targets Eventual targets.
	 */
	public void useSkill(Character activeChar, L2Skill skill, L2Object[] targets);
	
	/**
	 * this method is called at initialization to register all the skill ids automatically
	 * @return all known itemIds
	 */
	public L2SkillType[] getSkillIds();
}