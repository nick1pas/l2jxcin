package net.sf.l2j.gameserver.handler.skillhandlers;

import net.sf.l2j.gameserver.ai.CtrlEvent;
import net.sf.l2j.gameserver.ai.CtrlIntention;
import net.sf.l2j.gameserver.datatables.SkillTable;
import net.sf.l2j.gameserver.handler.ISkillHandler;
import net.sf.l2j.gameserver.instancemanager.DuelManager;
import net.sf.l2j.gameserver.model.L2Effect;
import net.sf.l2j.gameserver.model.L2Object;
import net.sf.l2j.gameserver.model.L2Skill;
import net.sf.l2j.gameserver.model.ShotType;
import net.sf.l2j.gameserver.model.actor.Attackable;
import net.sf.l2j.gameserver.model.actor.Character;
import net.sf.l2j.gameserver.model.actor.Playable;
import net.sf.l2j.gameserver.model.actor.instance.ClanHallManagerNpc;
import net.sf.l2j.gameserver.model.actor.instance.Player;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.network.serverpackets.SystemMessage;
import net.sf.l2j.gameserver.skills.Env;
import net.sf.l2j.gameserver.skills.Formulas;
import net.sf.l2j.gameserver.templates.skills.L2EffectType;
import net.sf.l2j.gameserver.templates.skills.L2SkillType;

public class Continuous implements ISkillHandler
{
	private static final L2SkillType[] SKILL_IDS =
	{
		L2SkillType.BUFF,
		L2SkillType.DEBUFF,
		L2SkillType.DOT,
		L2SkillType.MDOT,
		L2SkillType.POISON,
		L2SkillType.BLEED,
		L2SkillType.HOT,
		L2SkillType.CPHOT,
		L2SkillType.MPHOT,
		L2SkillType.FEAR,
		L2SkillType.CONT,
		L2SkillType.WEAKNESS,
		L2SkillType.REFLECT,
		L2SkillType.UNDEAD_DEFENSE,
		L2SkillType.AGGDEBUFF,
		L2SkillType.FUSION
	};
	
	@Override
	public void useSkill(Character activeChar, L2Skill skill, L2Object[] targets)
	{
		final Player player = activeChar.getActingPlayer();
		
		if (skill.getEffectId() != 0)
		{
			L2Skill sk = SkillTable.getInstance().getInfo(skill.getEffectId(), skill.getEffectLvl() == 0 ? 1 : skill.getEffectLvl());
			if (sk != null)
				skill = sk;
		}
		
		final boolean ss = activeChar.isChargedShot(ShotType.SOULSHOT);
		final boolean sps = activeChar.isChargedShot(ShotType.SPIRITSHOT);
		final boolean bsps = activeChar.isChargedShot(ShotType.BLESSED_SPIRITSHOT);
		
		for (L2Object obj : targets)
		{
			if (!(obj instanceof Character))
				continue;
			
			Character target = ((Character) obj);
			if (Formulas.calcSkillReflect(target, skill) == Formulas.SKILL_REFLECT_SUCCEED)
				target = activeChar;
			
			switch (skill.getSkillType())
			{
				case BUFF:
					// Target under buff immunity.
					if (target.getFirstEffect(L2EffectType.BLOCK_BUFF) != null)
						continue;
					
					// Anti-Buff Protection prevents you from getting buffs by other players
					if (activeChar instanceof Player && target != activeChar && target.isBuffProtected() && !skill.isHeroSkill() && (skill.getSkillType() == L2SkillType.BUFF || skill.getSkillType() == L2SkillType.HEAL_PERCENT || skill.getSkillType() == L2SkillType.MANAHEAL_PERCENT || skill.getSkillType() == L2SkillType.COMBATPOINTHEAL || skill.getSkillType() == L2SkillType.REFLECT))
						continue;
					
					// Player holding a cursed weapon can't be buffed and can't buff
					if (!(activeChar instanceof ClanHallManagerNpc) && target != activeChar)
					{
						if (target instanceof Player)
						{
							if (((Player) target).isCursedWeaponEquipped())
								continue;
						}
						else if (player != null && player.isCursedWeaponEquipped())
							continue;
					}
					break;
				
				case HOT:
				case CPHOT:
				case MPHOT:
					if (activeChar.isInvul())
						continue;
					break;
			}
			
			// Target under debuff immunity.
			if (skill.isDebuff() && target.getFirstEffect(L2EffectType.BLOCK_DEBUFF) != null)
				continue;
			
			boolean acted = true;
			byte shld = 0;
			
			if (skill.isOffensive() || skill.isDebuff())
			{
				shld = Formulas.calcShldUse(activeChar, target, skill);
				acted = Formulas.calcSkillSuccess(activeChar, target, skill, shld, bsps);
			}
			
			if (acted)
			{
				if (skill.isToggle())
					target.stopSkillEffects(skill.getId());
				
				// if this is a debuff let the duel manager know about it so the debuff
				// can be removed after the duel (player & target must be in the same duel)
				if (target instanceof Player && ((Player) target).isInDuel() && (skill.getSkillType() == L2SkillType.DEBUFF || skill.getSkillType() == L2SkillType.BUFF) && player != null && player.getDuelId() == ((Player) target).getDuelId())
				{
					DuelManager dm = DuelManager.getInstance();
					for (L2Effect buff : skill.getEffects(activeChar, target, new Env(shld, ss, sps, bsps)))
						if (buff != null)
							dm.onBuff(((Player) target), buff);
				}
				else
					skill.getEffects(activeChar, target, new Env(shld, ss, sps, bsps));
				
				if (skill.getSkillType() == L2SkillType.AGGDEBUFF)
				{
					if (target instanceof Attackable)
						target.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, activeChar, (int) skill.getPower());
					else if (target instanceof Playable)
					{
						if (target.getTarget() == activeChar)
							target.getAI().setIntention(CtrlIntention.ATTACK, activeChar);
						else
							target.setTarget(activeChar);
					}
				}
			}
			else
				activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.ATTACK_FAILED));
			
			// Possibility of a lethal strike
			Formulas.calcLethalHit(activeChar, target, skill);
		}
		
		if (skill.hasSelfEffects())
		{
			final L2Effect effect = activeChar.getFirstEffect(skill.getId());
			if (effect != null && effect.isSelfEffect())
				effect.exit();
			
			skill.getEffectsSelf(activeChar);
		}
		
		if (!skill.isPotion())
			activeChar.setChargedShot(bsps ? ShotType.BLESSED_SPIRITSHOT : ShotType.SPIRITSHOT, skill.isStaticReuse());
	}
	
	@Override
	public L2SkillType[] getSkillIds()
	{
		return SKILL_IDS;
	}
}