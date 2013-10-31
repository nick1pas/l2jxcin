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

import javolution.util.FastList;
import net.xcine.gameserver.ai.CtrlEvent;
import net.xcine.gameserver.datatables.sql.NpcTable;
import net.xcine.gameserver.idfactory.IdFactory;
import net.xcine.gameserver.model.L2Attackable;
import net.xcine.gameserver.model.L2Character;
import net.xcine.gameserver.model.L2Effect;
import net.xcine.gameserver.model.L2Playable;
import net.xcine.gameserver.model.L2Skill;
import net.xcine.gameserver.model.L2Summon;
import net.xcine.gameserver.model.L2World;
import net.xcine.gameserver.model.actor.instance.L2EffectPointInstance;
import net.xcine.gameserver.model.actor.instance.L2PcInstance;
import net.xcine.gameserver.network.SystemMessageId;
import net.xcine.gameserver.network.serverpackets.MagicSkillLaunched;
import net.xcine.gameserver.network.serverpackets.SystemMessage;
import net.xcine.gameserver.skills.Env;
import net.xcine.gameserver.skills.Formulas;
import net.xcine.gameserver.skills.l2skills.L2SkillSignetCasttime;
import net.xcine.gameserver.templates.L2NpcTemplate;
import net.xcine.util.Point3D;

/**
 * @author Shyla
 */
public final class EffectSignetMDam extends L2Effect
{
	private L2EffectPointInstance _actor;
	private boolean bss;
	private boolean sps;
//	private SigmetMDAMTask skill_task;

	public EffectSignetMDam(Env env, EffectTemplate template)
	{
		super(env, template);
	}

	@Override
	public EffectType getEffectType()
	{
		return EffectType.SIGNET_GROUND;
	}

	@Override
	public void onStart()
	{
		L2NpcTemplate template;
		if(getSkill() instanceof L2SkillSignetCasttime)
		{
			template = NpcTable.getInstance().getTemplate(((L2SkillSignetCasttime) getSkill())._effectNpcId);
		}
		else
			return;

		L2EffectPointInstance effectPoint = new L2EffectPointInstance(IdFactory.getInstance().getNextId(), template, getEffector());
		effectPoint.getStatus().setCurrentHp(effectPoint.getMaxHp());
		effectPoint.getStatus().setCurrentMp(effectPoint.getMaxMp());
		
		L2World.getInstance().storeObject(effectPoint);

		int x = getEffector().getX();
		int y = getEffector().getY();
		int z = getEffector().getZ();

		if(getEffector() instanceof L2PcInstance && getSkill().getTargetType() == L2Skill.SkillTargetType.TARGET_GROUND)
		{
			Point3D wordPosition = ((L2PcInstance) getEffector()).getCurrentSkillWorldPosition();

			if(wordPosition != null)
			{
				x = wordPosition.getX();
				y = wordPosition.getY();
				z = wordPosition.getZ();
			}
		}
		effectPoint.setIsInvul(true);
		effectPoint.spawnMe(x, y, z);

		_actor = effectPoint;
		
//		skill_task = new SigmetMDAMTask();
	}

	@Override
	public boolean onActionTime()
	{
		if(getCount() >= getTotalCount() - 2)
			return true; // do nothing first 2 times
		
		int mpConsume = getSkill().getMpConsume();
		L2PcInstance caster = (L2PcInstance) getEffector();

		sps = caster.checkSps();
		bss = caster.checkBss();
		
		FastList<L2Character> targets = new FastList<>();

		for(L2Character cha : _actor.getKnownList().getKnownCharactersInRadius(getSkill().getSkillRadius()))
		{
			if(cha == null || cha == caster)
			{
				continue;
			}

			if(cha instanceof L2Attackable || cha instanceof L2Playable)
			{
				if(cha.isAlikeDead())
				{
					continue;
				}

				if(mpConsume > caster.getStatus().getCurrentMp())
				{
					caster.sendPacket(new SystemMessage(SystemMessageId.SKILL_REMOVED_DUE_LACK_MP));
					return false;
				}

				caster.reduceCurrentMp(mpConsume);

				if(cha instanceof L2Playable)
				{
					if(!(cha instanceof L2Summon 
						&& ((L2Summon) cha).getOwner() == caster))
					{
						caster.updatePvPStatus(cha);
					}
				}

				targets.add(cha);
			}
		}

		if(!targets.isEmpty())
		{
			caster.broadcastPacket(new MagicSkillLaunched(caster, getSkill().getDisplayId(), getSkill().getLevel(), targets.toArray(new L2Character[targets.size()])));
			for(L2Character target : targets)
			{
				boolean mcrit = Formulas.calcMCrit(caster.getMCriticalHit(target, getSkill()));
				int mdam = (int) Formulas.calcMagicDam(caster, target, getSkill(), sps, bss, mcrit);

				if(target instanceof L2Summon)
				{
					target.broadcastStatusUpdate();
				}

				if(mdam > 0)
				{
					if(!target.isRaid() && Formulas.calcAtkBreak(target, mdam))
					{
						target.breakAttack();
						target.breakCast();
					}
					caster.sendDamageMessage(target, mdam, mcrit, false, false);
					target.reduceCurrentHp(mdam, caster);
				}
				target.getAI().notifyEvent(CtrlEvent.EVT_ATTACKED, caster);
			}
		}
		
		return true;
	}

	@Override
	public void onExit()
	{
		if(_actor != null)
		{
			L2PcInstance caster = (L2PcInstance) getEffector();

			//remove shots
			if (bss){
				caster.removeBss();
				
			}else if(sps){
				caster.removeSps();
			}
			_actor.deleteMe();
		}
	}
	
	
}
