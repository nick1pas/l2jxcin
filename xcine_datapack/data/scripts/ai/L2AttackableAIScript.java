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
package ai;

import static net.xcine.gameserver.ai.CtrlIntention.AI_INTENTION_ATTACK;

import java.util.List;

import net.xcine.gameserver.ai.CtrlEvent;
import net.xcine.gameserver.datatables.NpcTable;
import net.xcine.gameserver.instancemanager.DimensionalRiftManager;
import net.xcine.gameserver.model.L2Object;
import net.xcine.gameserver.model.L2Skill;
import net.xcine.gameserver.model.actor.L2Attackable;
import net.xcine.gameserver.model.actor.L2Character;
import net.xcine.gameserver.model.actor.L2Npc;
import net.xcine.gameserver.model.actor.instance.L2MonsterInstance;
import net.xcine.gameserver.model.actor.instance.L2PcInstance;
import net.xcine.gameserver.model.actor.instance.L2RiftInvaderInstance;
import net.xcine.gameserver.model.quest.Quest;
import net.xcine.gameserver.model.quest.QuestEventType;
import net.xcine.gameserver.templates.chars.L2NpcTemplate;
import net.xcine.gameserver.util.Util;

/**
 * Overarching Superclass for all mob AI
 * @author Fulminus
 */
public class L2AttackableAIScript extends Quest
{
	/**
	 * This is used to register all monsters contained in mobs for a particular script<BR>
	 * <FONT COLOR=#FF0000><B> <U>Caution</U> : This method register ID for all QuestEventTypes<BR>
	 * Do not use for group_template AIs</B></FONT><BR>
	 * @param mobs
	 * @see #registerMobs(int[], QuestEventType...)
	 */
	public void registerMobs(int[] mobs)
	{
		for (int id : mobs)
		{
			addEventId(id, QuestEventType.ON_ATTACK);
			addEventId(id, QuestEventType.ON_KILL);
			addEventId(id, QuestEventType.ON_SPAWN);
			addEventId(id, QuestEventType.ON_SPELL_FINISHED);
			addEventId(id, QuestEventType.ON_SKILL_SEE);
			addEventId(id, QuestEventType.ON_FACTION_CALL);
			addEventId(id, QuestEventType.ON_AGGRO_RANGE_ENTER);
		}
	}
	
	/**
	 * This is used to register all monsters contained in mobs for a particular script event types defined in types.
	 * @param mobs
	 * @param types
	 */
	public void registerMobs(int[] mobs, QuestEventType... types)
	{
		for (int id : mobs)
		{
			for (QuestEventType type : types)
				addEventId(id, type);
		}
	}
	
	public L2AttackableAIScript(int questId, String name, String descr)
	{
		super(questId, name, descr);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		return null;
	}
	
	@Override
	public String onSpellFinished(L2Npc npc, L2PcInstance player, L2Skill skill)
	{
		return null;
	}
	
	@Override
	public String onSkillSee(L2Npc npc, L2PcInstance caster, L2Skill skill, L2Object[] targets, boolean isPet)
	{
		if (caster == null)
			return null;
		
		if (!(npc instanceof L2Attackable))
			return null;
		
		L2Attackable attackable = (L2Attackable) npc;
		int skillAggroPoints = skill.getAggroPoints();
		
		if (caster.getPet() != null)
		{
			if (targets.length == 1 && Util.contains(targets, caster.getPet()))
				skillAggroPoints = 0;
		}
		
		if (skillAggroPoints > 0)
		{
			if (attackable.hasAI() && (attackable.getAI().getIntention() == AI_INTENTION_ATTACK))
			{
				L2Object npcTarget = attackable.getTarget();
				for (L2Object skillTarget : targets)
				{
					if (npcTarget == skillTarget || npc == skillTarget)
					{
						L2Character originalCaster = isPet ? caster.getPet() : caster;
						attackable.addDamageHate(originalCaster, 0, (skillAggroPoints * 150) / (attackable.getLevel() + 7));
					}
				}
			}
		}
		return null;
	}
	
	@Override
	public String onFactionCall(L2Npc npc, L2Npc caller, L2PcInstance attacker, boolean isPet)
	{
		if (attacker == null)
			return null;
		
		L2Character originalAttackTarget = (isPet ? attacker.getPet() : attacker);
		if (attacker.isInParty() && attacker.getParty().isInDimensionalRift())
		{
			byte riftType = attacker.getParty().getDimensionalRift().getType();
			byte riftRoom = attacker.getParty().getDimensionalRift().getCurrentRoom();
			
			if (caller instanceof L2RiftInvaderInstance && !DimensionalRiftManager.getInstance().getRoom(riftType, riftRoom).checkIfInZone(npc.getX(), npc.getY(), npc.getZ()))
				return null;
		}
		
		// By default, when a faction member calls for help, attack the caller's attacker.
		// Notify the AI with EVT_AGGRESSION
		npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, originalAttackTarget, 1);
		
		return null;
	}
	
	@Override
	public String onAggroRangeEnter(L2Npc npc, L2PcInstance player, boolean isPet)
	{
		if (player == null)
			return null;
		
		((L2Attackable) npc).addDamageHate(isPet ? player.getPet() : player, 0, 1);
		return null;
	}
	
	@Override
	public String onSpawn(L2Npc npc)
	{
		return null;
	}
	
	@Override
	public String onAttack(L2Npc npc, L2PcInstance attacker, int damage, boolean isPet)
	{
		if ((attacker != null) && (npc instanceof L2Attackable))
		{
			L2Attackable attackable = (L2Attackable) npc;
			L2Character originalAttacker = isPet ? attacker.getPet() : attacker;
			
			attackable.getAI().notifyEvent(CtrlEvent.EVT_ATTACKED, originalAttacker);
			attackable.addDamageHate(originalAttacker, damage, (damage * 100) / (attackable.getLevel() + 7));
		}
		return null;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isPet)
	{
		if (npc instanceof L2MonsterInstance)
		{
			final L2MonsterInstance mob = (L2MonsterInstance) npc;
			if (mob.getLeader() != null)
				mob.getLeader().getMinionList().onMinionDie(mob, -1);
			
			if (mob.hasMinions())
				mob.getMinionList().onMasterDie(false);
		}
		return null;
	}
	
	public static void main(String[] args)
	{
		L2AttackableAIScript ai = new L2AttackableAIScript(-1, "L2AttackableAIScript", "L2AttackableAIScript");
		
		// register all mobs here...
		for (int level = 1; level < 100; level++)
		{
			final List<L2NpcTemplate> templates = NpcTable.getInstance().getAllOfLevel(level);
			for (L2NpcTemplate t : templates)
			{
				try
				{
					if (L2Attackable.class.isAssignableFrom(Class.forName("net.xcine.gameserver.model.actor.instance." + t.getType() + "Instance")))
					{
						ai.addEventId(t.getNpcId(), QuestEventType.ON_ATTACK);
						ai.addEventId(t.getNpcId(), QuestEventType.ON_KILL);
						ai.addEventId(t.getNpcId(), QuestEventType.ON_SPAWN);
						ai.addEventId(t.getNpcId(), QuestEventType.ON_SKILL_SEE);
						ai.addEventId(t.getNpcId(), QuestEventType.ON_FACTION_CALL);
						ai.addEventId(t.getNpcId(), QuestEventType.ON_AGGRO_RANGE_ENTER);
					}
				}
				catch (ClassNotFoundException ex)
				{
					_log.info("Class not found: " + t.getType() + "Instance");
				}
			}
		}
	}
}