package net.xcine.gameserver.handler.skillhandlers;

import net.xcine.gameserver.event.EventManager;
import net.xcine.gameserver.handler.ISkillHandler;
import net.xcine.gameserver.model.L2Character;
import net.xcine.gameserver.model.L2Object;
import net.xcine.gameserver.model.L2Skill;
import net.xcine.gameserver.model.L2Skill.SkillType;
import net.xcine.gameserver.model.actor.instance.L2PcInstance;

public class Bomb implements ISkillHandler
{
private static final SkillType[] SKILL_IDS =
{ SkillType.BOMB };

@Override
public void useSkill(L2Character activeChar, L2Skill skill,
L2Object[] targets)
{

if (!(activeChar instanceof L2PcInstance))
return;

EventManager.getInstance().getCurrentEvent().dropBomb((L2PcInstance)activeChar);
}

@Override
public SkillType[] getSkillIds()
{
return SKILL_IDS;
}
}