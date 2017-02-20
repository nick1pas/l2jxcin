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
package net.sf.l2j.gameserver.scripting.scripts.ai.group;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import net.sf.l2j.gameserver.ai.CtrlIntention;
import net.sf.l2j.gameserver.model.L2Skill;
import net.sf.l2j.gameserver.model.actor.L2Attackable;
import net.sf.l2j.gameserver.model.actor.L2Character;
import net.sf.l2j.gameserver.model.actor.L2Npc;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;
import net.sf.l2j.gameserver.scripting.scripts.ai.L2AttackableAIScript;

public class ZombieGatekeepers extends L2AttackableAIScript
{	
	public ZombieGatekeepers(String name, String descr)
	{
		super("ai/group");
		addAttackId(22136);
		addAggroRangeEnterId(22136);
	}
	
	private final Map<Integer, ArrayList<L2Character>> _attackersList = new LinkedHashMap<>();
	
	@Override
	public String onAttack(L2Npc npc, L2PcInstance attacker, int damage, boolean isPet, L2Skill skill)
	{
		int npcObjId = npc.getObjectId();
		
		L2Character target = isPet ? attacker.getPet() : attacker;
		
		if (_attackersList.get(npcObjId) == null)
		{
			ArrayList<L2Character> player = new ArrayList<>();
			player.add(target);
			_attackersList.put(npcObjId, player);
		}
		else if (!_attackersList.get(npcObjId).contains(target))
		{
			_attackersList.get(npcObjId).add(target);
		}
		
		return super.onAttack(npc, attacker, damage, isPet, skill);
	}
	
	@Override
	public String onAggro(L2Npc npc, L2PcInstance player, boolean isPet)
	{
		if ((player.getLevel() < 73) && !player.isGM())
		{
			((L2Attackable) npc).addDamageHate(player, 0, 999);
			npc.getAI().setIntention(CtrlIntention.ATTACK, player);
			return super.onAggro(npc, player, isPet);
		}
		
		int npcObjId = npc.getObjectId();
		
		L2Character target = isPet ? player.getPet() : player;
		
		ItemInstance VisitorsMark = player.getInventory().getItemByItemId(8064);
		ItemInstance FadedVisitorsMark = player.getInventory().getItemByItemId(8065);
		ItemInstance PagansMark = player.getInventory().getItemByItemId(8067);
		
		long mark1 = VisitorsMark == null ? 0 : VisitorsMark.getCount();
		long mark2 = FadedVisitorsMark == null ? 0 : FadedVisitorsMark.getCount();
		long mark3 = PagansMark == null ? 0 : PagansMark.getCount();
		
		if ((mark1 == 0) && (mark2 == 0) && (mark3 == 0))
		{
			((L2Attackable) npc).addDamageHate(target, 0, 999);
			npc.getAI().setIntention(CtrlIntention.ATTACK, target);
		}
		else
		{
			if ((_attackersList.get(npcObjId) == null) || !_attackersList.get(npcObjId).contains(target))
			{
				((L2Attackable) npc).getAggroList().remove(target);
			}
			else
			{
				((L2Attackable) npc).addDamageHate(target, 0, 999);
				npc.getAI().setIntention(CtrlIntention.ATTACK, target);
			}
		}
		
		return super.onAggro(npc, player, isPet);
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isPet)
	{
		int npcObjId = npc.getObjectId();
		if (_attackersList.get(npcObjId) != null)
		{
			_attackersList.get(npcObjId).clear();
		}
		
		return super.onKill(npc, killer, isPet);
	}
}
