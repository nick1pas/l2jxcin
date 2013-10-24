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

import net.xcine.Config;
import net.xcine.gameserver.handler.ISkillHandler;
import net.xcine.gameserver.handler.SkillHandler;
import net.xcine.gameserver.model.L2Character;
import net.xcine.gameserver.model.L2Object;
import net.xcine.gameserver.model.L2Skill;
import net.xcine.gameserver.model.L2Skill.SkillType;
import net.xcine.gameserver.network.SystemMessageId;
import net.xcine.gameserver.network.serverpackets.StatusUpdate;
import net.xcine.gameserver.network.serverpackets.SystemMessage;

public class CombatPointHeal implements ISkillHandler
{
	private static final SkillType[] SKILL_IDS = { SkillType.COMBATPOINTHEAL, SkillType.COMBATPOINTPERCENTHEAL };
	
	@Override
	public void useSkill(L2Character actChar, L2Skill skill, L2Object[] targets)
	{
		//check for other effects
		try
		{
			ISkillHandler handler = SkillHandler.getInstance().getSkillHandler(SkillType.BUFF);

			if(handler != null)
				handler.useSkill(actChar, skill, targets);

			handler = null;
		}
		catch(Exception e)
		{
			if(Config.ENABLE_ALL_EXCEPTIONS)
				e.printStackTrace();
		}
		
		for(L2Object object : targets)
		{
			if (!(object instanceof L2Character))
			{
				continue;
			}
			
			L2Character target = (L2Character) object;
			double cp = skill.getPower();
			if(skill.getSkillType() == SkillType.COMBATPOINTPERCENTHEAL)
			{
				cp = target.getMaxCp() * cp / 100.0;
			}
			SystemMessage sm = new SystemMessage(SystemMessageId.S1_CP_WILL_BE_RESTORED);
			sm.addNumber((int) cp);
			target.sendPacket(sm);
			
			target.setCurrentCp(cp + target.getCurrentCp());
			StatusUpdate sump = new StatusUpdate(target.getObjectId());
			sump.addAttribute(StatusUpdate.CUR_CP, (int) target.getCurrentCp());
			target.sendPacket(sump);
		}
	}

	@Override
	public SkillType[] getSkillIds()
	{
		return SKILL_IDS;
	}
}
