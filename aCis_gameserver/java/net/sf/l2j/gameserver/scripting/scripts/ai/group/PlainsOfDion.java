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

import net.sf.l2j.commons.random.Rnd;
import net.sf.l2j.commons.util.ArraysUtil;

import net.sf.l2j.gameserver.geoengine.GeoEngine;
import net.sf.l2j.gameserver.model.L2Skill;
import net.sf.l2j.gameserver.model.actor.L2Npc;
import net.sf.l2j.gameserver.model.actor.instance.L2MonsterInstance;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.scripting.scripts.ai.L2AttackableAIScript;

/**
 * AI for mobs in Plains of Dion (near Floran Village)
 */
public final class PlainsOfDion extends L2AttackableAIScript
{
	private static final int MONSTERS[] =
	{
		21104, // Delu Lizardman Supplier
		21105, // Delu Lizardman Special Agent
		21107, // Delu Lizardman Commander
	};
	
	private static final String[] MONSTERS_MSG =
	{
		"$s1! How dare you interrupt our fight! Hey guys, help!",
		"$s1! Hey! We're having a duel here!",
		"The duel is over! Attack!",
		"Foul! Kill the coward!",
		"How dare you interrupt a sacred duel! You must be taught a lesson!"
	};
	
	private static final String[] MONSTERS_ASSIST_MSG =
	{
		"Die, you coward!",
		"Kill the coward!",
		"What are you looking at?"
	};
	
	public PlainsOfDion()
	{
		super("ai/group");
	}
	
	@Override
	protected void registerNpcs()
	{
		addAttackId(MONSTERS);
	}
	
	@Override
	public String onAttack(L2Npc npc, L2PcInstance player, int damage, boolean isPet, L2Skill skill)
	{
		if (npc.isScriptValue(0))
		{
			npc.broadcastNpcSay(MONSTERS_MSG[Rnd.get(5)].replace("$s1", player.getName()));
			
			for (L2MonsterInstance obj : npc.getKnownTypeInRadius(L2MonsterInstance.class, 300))
			{
				if (!obj.isAttackingNow() && !obj.isDead() && ArraysUtil.contains(MONSTERS, obj.getNpcId()) && GeoEngine.getInstance().canSeeTarget(npc, obj))
				{
					attack(obj, player);
					obj.broadcastNpcSay(MONSTERS_ASSIST_MSG[Rnd.get(3)]);
				}
			}
			npc.setScriptValue(1);
		}
		return super.onAttack(npc, player, damage, isPet, skill);
	}
}
