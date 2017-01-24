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
package net.sf.l2j.gameserver.handler.skillhandlers;

import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.l2j.commons.random.Rnd;

import net.sf.l2j.gameserver.handler.ISkillHandler;
import net.sf.l2j.gameserver.model.L2Object;
import net.sf.l2j.gameserver.model.L2Skill;
import net.sf.l2j.gameserver.model.actor.L2Character;
import net.sf.l2j.gameserver.templates.skills.L2SkillType;

public class ZakenPlayer implements ISkillHandler
{
	private final static Logger _log = Logger.getLogger(ZakenPlayer.class.getName());
	
	private static final L2SkillType[] SKILL_IDS =
	{
		L2SkillType.ZAKENPLAYER
	};
	
	@Override
	public void useSkill(L2Character activeChar, L2Skill skill, L2Object[] targets)
	{
		try
		{
			for (int index = 0; index < targets.length; index++)
			{
				if (!(targets[index] instanceof L2Character))
					continue;
				
				L2Character target = (L2Character) targets[index];
				int ch = (Rnd.get(14) + 1);
				
				switch (ch)
				{
					case 1:
						target.teleToLocation(55299, 219120, -2952, 0);
						break;
					case 2:
						target.teleToLocation(56363, 218043, -2952, 0);
						break;
					case 3:
						target.teleToLocation(54245, 220162, -2952, 0);
						break;
					case 4:
						target.teleToLocation(56289, 220126, -2952, 0);
						break;
					case 5:
						target.teleToLocation(55299, 219120, -3224, 0);
						break;
					case 6:
						target.teleToLocation(56363, 218043, -3224, 0);
						break;
					case 7:
						target.teleToLocation(54245, 220162, -3224, 0);
						break;
					case 8:
						target.teleToLocation(56289, 220126, -3224, 0);
						break;
					case 9:
						target.teleToLocation(55299, 219120, -3496, 0);
						break;
					case 10:
						target.teleToLocation(56363, 218043, -3496, 0);
						break;
					case 11:
						target.teleToLocation(54245, 220162, -3496, 0);
						break;
					case 12:
						target.teleToLocation(56289, 220126, -3496, 0);
						break;
					
					default:
						target.teleToLocation(53930, 217760, -2944, 0);
						break;
					
				}
			}
		}
		catch (Throwable e)
		{
			_log.log(Level.SEVERE, "", e);
		}
	}
	
	@Override
	public L2SkillType[] getSkillIds()
	{
		return SKILL_IDS;
	}
}