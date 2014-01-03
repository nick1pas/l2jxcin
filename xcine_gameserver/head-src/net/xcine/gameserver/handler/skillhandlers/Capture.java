package net.xcine.gameserver.handler.skillhandlers;

import net.xcine.gameserver.event.EventManager;
import net.xcine.gameserver.handler.ISkillHandler;
import net.xcine.gameserver.model.L2Object;
import net.xcine.gameserver.model.L2Skill;
import net.xcine.gameserver.model.actor.L2Character;
import net.xcine.gameserver.model.actor.L2Npc;
import net.xcine.gameserver.model.actor.instance.L2PcInstance;
import net.xcine.gameserver.templates.skills.L2SkillType;

public class Capture implements ISkillHandler
{
	private static final L2SkillType[] SKILL_IDS = {L2SkillType.CAPTURE};
	
	@Override
	public void useSkill(L2Character activeChar, L2Skill skill, L2Object[] targets)
	{
		if (!(activeChar instanceof L2PcInstance))
			return;
		
		if(!(targets[0] instanceof L2Npc))
			return;
		
		EventManager.getInstance().getCurrentEvent().useCapture((L2PcInstance) activeChar, (L2Npc) targets[0]);
	}
	
	@Override
	public L2SkillType[] getSkillIds()
	{
		return SKILL_IDS;
	}
}