package net.xcine.gameserver.handler.skillhandlers;

import net.xcine.gameserver.event.EventManager;
import net.xcine.gameserver.handler.ISkillHandler;
import net.xcine.gameserver.model.L2Character;
import net.xcine.gameserver.model.L2Npc;
import net.xcine.gameserver.model.L2Object;
import net.xcine.gameserver.model.L2Skill;
import net.xcine.gameserver.model.L2Skill.SkillType;
import net.xcine.gameserver.model.actor.instance.L2NpcInstance;
import net.xcine.gameserver.model.actor.instance.L2PcInstance;

public class Capture implements ISkillHandler
{
private static final SkillType[] SKILL_IDS =
{ SkillType.CAPTURE };

@Override
public void useSkill(L2Character activeChar, L2Skill skill, L2Object[] targets)
{
if (!(activeChar instanceof L2PcInstance))
return;

if(!(targets[0] instanceof L2Npc))
return;

L2PcInstance player = (L2PcInstance) activeChar;
L2Npc target = (L2Npc) targets[0];

EventManager.getInstance().getCurrentEvent().useCapture(player, target);
}

@Override
public SkillType[] getSkillIds()
{
return SKILL_IDS;
}
}