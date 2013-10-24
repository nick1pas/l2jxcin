/* This program is free software; you can redistribute it and/or modify
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

package net.xcine.gameserver.skills.l2skills;

import net.xcine.gameserver.model.L2Character;
import net.xcine.gameserver.model.L2Object;
import net.xcine.gameserver.model.L2Skill;
import net.xcine.gameserver.model.actor.instance.L2PcInstance;
import net.xcine.gameserver.network.SystemMessageId;
import net.xcine.gameserver.network.serverpackets.EtcStatusUpdate;
import net.xcine.gameserver.network.serverpackets.SystemMessage;
import net.xcine.gameserver.skills.effects.EffectCharge;
import net.xcine.gameserver.templates.StatsSet;

public class L2SkillChargeEffect extends L2Skill
{
	final int chargeSkillId;

	public L2SkillChargeEffect(StatsSet set)
	{
		super(set);
		chargeSkillId = set.getInteger("charge_skill_id");
	}

	@Override
	public boolean checkCondition(L2Character activeChar, L2Object target, boolean itemOrWeapon)
	{
		if(activeChar instanceof L2PcInstance)
		{
			L2PcInstance player = (L2PcInstance) activeChar;
			EffectCharge e = (EffectCharge) player.getFirstEffect(chargeSkillId);
			if(e == null || e.numCharges < getNumCharges())
			{
				SystemMessage sm = new SystemMessage(SystemMessageId.S1_CANNOT_BE_USED);
				sm.addSkillName(getId());
				activeChar.sendPacket(sm);
				return false;
			}
		}
		return super.checkCondition(activeChar, target, itemOrWeapon);
	}

	@Override
	public void useSkill(L2Character activeChar, L2Object[] targets)
	{
		if(activeChar.isAlikeDead())
			return;

		// get the effect
		EffectCharge effect = (EffectCharge) activeChar.getFirstEffect(chargeSkillId);
		if(effect == null || effect.numCharges < getNumCharges())
		{
			SystemMessage sm = new SystemMessage(SystemMessageId.S1_CANNOT_BE_USED);
			sm.addSkillName(getId());
			activeChar.sendPacket(sm);
			return;
		}

		// decrease?
		effect.numCharges -= getNumCharges();

		// update icons
		// activeChar.updateEffectIcons();

		// maybe exit? no charge
		if(effect.numCharges == 0)
		{
			effect.exit(false);
		}

		// apply effects
		if(hasEffects())
		{
			for(L2Object target : targets)
			{
				getEffects(activeChar, (L2Character) target,false,false,false);
			}
		}
		if(activeChar instanceof L2PcInstance)
		{
			activeChar.sendPacket(new EtcStatusUpdate((L2PcInstance) activeChar));
		}
	}
}
