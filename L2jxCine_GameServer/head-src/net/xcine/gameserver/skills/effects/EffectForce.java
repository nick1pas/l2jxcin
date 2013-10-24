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
package net.xcine.gameserver.skills.effects;

import java.util.logging.Logger;

import net.xcine.gameserver.datatables.SkillTable;
import net.xcine.gameserver.model.L2Effect;
import net.xcine.gameserver.model.L2Skill;
import net.xcine.gameserver.skills.Env;
import net.xcine.gameserver.util.Util;

/**
 * @author kombat
 */
public class EffectForce extends L2Effect
{
	protected static final Logger _log = Logger.getLogger(EffectForce.class.getName());
	
	public int forces = 0;
	private int _range = -1;

	public EffectForce(Env env, EffectTemplate template)
	{
		super(env, template);
		forces = getSkill().getLevel();
		_range = getSkill().getCastRange();
	}

	@Override
	public boolean onActionTime()
	{
		return Util.checkIfInRange(_range, getEffector(), getEffected(), true);
	}

	@Override
	public EffectType getEffectType()
	{
		return EffectType.BUFF;
	}

	public void increaseForce()
	{
		forces++;
		updateBuff();
	}

	public void decreaseForce()
	{
		forces--;
		if(forces < 1)
		{
			exit(false);
		}
		else
		{
			updateBuff();
		}
	}

	public void updateBuff()
	{
		exit(false);
		L2Skill newSkill = SkillTable.getInstance().getInfo(getSkill().getId(), forces);
		if(newSkill!=null)
			newSkill.getEffects(getEffector(), getEffected(),false,false,false);
	}

	@Override
	public void onExit()
	{
	//try
	//{
	//	getEffector().abortCast();
	//	if(getEffector().getForceBuff() != null)
	//		getEffector().getForceBuff().delete();
	//}
	//catch(Exception e)
	//{
	//null
	//}
	}
}
