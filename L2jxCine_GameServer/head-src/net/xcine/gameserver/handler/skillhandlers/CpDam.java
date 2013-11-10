/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */
package net.xcine.gameserver.handler.skillhandlers;

import net.xcine.gameserver.handler.ISkillHandler;
import net.xcine.gameserver.model.L2Character;
import net.xcine.gameserver.model.L2Object;
import net.xcine.gameserver.model.L2Skill;
import net.xcine.gameserver.model.L2Skill.SkillType;
import net.xcine.gameserver.model.actor.instance.L2PcInstance;
import net.xcine.gameserver.model.actor.instance.L2PlayableInstance;
import net.xcine.gameserver.skills.Formulas;


public class CpDam implements ISkillHandler
{
	private static final SkillType[] SKILL_IDS = { SkillType.CPDAM };
	
	@Override
	public void useSkill(L2Character activeChar, L2Skill skill, L2Object[] targets)
	{
		if(!(activeChar instanceof L2PlayableInstance)){
			return;
		}
		
		if(activeChar.isAlikeDead())
			return;

		boolean bss = activeChar.checkBss();
		boolean sps = activeChar.checkSps();
		boolean ss = activeChar.checkSs();
	
		for(L2Object target2 : targets)
		{
			L2Character target = (L2Character) target2;

			if(activeChar instanceof L2PcInstance && target instanceof L2PcInstance && target.isAlikeDead() && target.isFakeDeath())
			{
				target.stopFakeDeath(null);
			}
			else if(target.isAlikeDead())
			{
				continue;
			}

			if(target.isInvul()){
				continue;
			}
			
			if(!Formulas.getInstance().calcSkillSuccess(activeChar, target, skill, ss, sps, bss))
				return;

			int damage = (int) (target.getCurrentCp() * (1 - skill.getPower()));

			if(!target.isRaid() && Formulas.calcAtkBreak(target, damage))
			{
				target.breakAttack();
				target.breakCast();
			}
			skill.getEffects(activeChar, target, ss, sps, bss);
			activeChar.sendDamageMessage(target, damage, false, false, false);
			target.setCurrentCp(target.getCurrentCp() - damage);

			target = null;
		}
		
		if (skill.isMagic())
		{
			if (bss){
				activeChar.removeBss();
			}else if(sps){
				activeChar.removeSps();
			}
		}else{
			
			activeChar.removeSs();
		}
		
	}

	@Override
	public SkillType[] getSkillIds()
	{
		return SKILL_IDS;
	}
}
