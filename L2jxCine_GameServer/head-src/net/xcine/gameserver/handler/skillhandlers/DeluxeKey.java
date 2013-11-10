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
package net.xcine.gameserver.handler.skillhandlers;

import java.util.logging.Logger;

import net.xcine.gameserver.handler.ISkillHandler;
import net.xcine.gameserver.model.L2Character;
import net.xcine.gameserver.model.L2Object;
import net.xcine.gameserver.model.L2Skill;
import net.xcine.gameserver.model.L2Skill.SkillType;
import net.xcine.gameserver.model.actor.instance.L2PcInstance;

/**
 * @author Julian
 */
public class DeluxeKey implements ISkillHandler
{
	private static Logger _log = Logger.getLogger(BeastFeed.class.getName());

	private static final SkillType[] SKILL_IDS = { SkillType.DELUXE_KEY_UNLOCK };

	@Override
	public void useSkill(L2Character activeChar, L2Skill skill, L2Object[] targets)
	{
		if(!(activeChar instanceof L2PcInstance))
			return;

		L2Object[] targetList = skill.getTargetList(activeChar);

		if(targetList == null){ return; }

		targetList = null;

		_log.fine("Delux key casting succeded.");
	}

	@Override
	public SkillType[] getSkillIds()
	{
		return SKILL_IDS;
	}
}
