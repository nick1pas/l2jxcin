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

package net.xcine.gameserver.model;

import java.util.logging.Logger;

import net.xcine.Config;
import net.xcine.gameserver.datatables.SkillTable;
import net.xcine.gameserver.skills.effects.EffectForce;

/**
 * @author ProGramMoS, L2jxCine
 */
public final class ForceBuff
{
	protected int _forceId;
	protected int _forceLevel;
	protected L2Character _caster;
	protected L2Character _target;

	static final Logger _log = Logger.getLogger(ForceBuff.class.getName());

	public L2Character getCaster()
	{
		return _caster;
	}

	public L2Character getTarget()
	{
		return _target;
	}

	public ForceBuff(L2Character caster, L2Character target, L2Skill skill)
	{
		_caster = caster;
		_target = target;
		_forceId = skill.getTriggeredId();
		_forceLevel = skill.getTriggeredLevel();

		L2Effect effect = _target.getFirstEffect(_forceId);
		if(effect != null)
		{
			((EffectForce) effect).increaseForce();
		}
		else
		{
			L2Skill force = SkillTable.getInstance().getInfo(_forceId, _forceLevel);
			if(force != null)
			{
				force.getEffects(_caster, _target,false,false,false);
			}
			else
			{
				_log.warning("Triggered skill [" + _forceId + ";" + _forceLevel + "] not found!");
			}
		}
		effect = null;
	}

	public void onCastAbort()
	{
		_caster.setForceBuff(null);
		L2Effect effect = _target.getFirstEffect(_forceId);
		if(effect != null)
		{
			if(Config.DEVELOPER){
				_log.info(" -- Removing ForceBuff "+effect.getSkill().getId());
			}
			
			if(effect instanceof EffectForce)
				((EffectForce) effect).decreaseForce();
			else
				effect.exit(false);
		}
		effect = null;
	}
}
