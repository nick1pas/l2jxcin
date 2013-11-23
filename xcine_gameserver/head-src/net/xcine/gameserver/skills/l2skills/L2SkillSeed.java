/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package net.xcine.gameserver.skills.l2skills;

import net.xcine.gameserver.model.L2Effect;
import net.xcine.gameserver.model.L2Object;
import net.xcine.gameserver.model.L2Skill;
import net.xcine.gameserver.model.actor.L2Character;
import net.xcine.gameserver.skills.effects.EffectSeed;
import net.xcine.gameserver.templates.StatsSet;
import net.xcine.gameserver.templates.skills.L2EffectType;

public class L2SkillSeed extends L2Skill
{
	public L2SkillSeed(StatsSet set)
	{
		super(set);
	}
	
	@Override
	public void useSkill(L2Character caster, L2Object[] targets)
	{
		if (caster.isAlikeDead())
			return;
		
		// Update Seeds Effects
		for (L2Object obj : targets)
		{
			if (!(obj instanceof L2Character))
				continue;
			
			final L2Character target = ((L2Character) obj);
			if (target.isAlikeDead() && getTargetType() != SkillTargetType.TARGET_CORPSE_MOB)
				continue;
			
			EffectSeed oldEffect = (EffectSeed) target.getFirstEffect(getId());
			if (oldEffect == null)
				getEffects(caster, target);
			else
				oldEffect.increasePower();
			
			L2Effect[] effects = target.getAllEffects();
			for (L2Effect effect : effects)
				if (effect.getEffectType() == L2EffectType.SEED)
					effect.rescheduleEffect();
		}
	}
}