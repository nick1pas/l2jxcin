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
package ai.group;

import ai.AbstractNpcAI;

import net.xcine.gameserver.ai.CtrlIntention;
import net.xcine.gameserver.datatables.SkillTable;
import net.xcine.gameserver.model.L2Skill;
import net.xcine.gameserver.model.actor.L2Attackable;
import net.xcine.gameserver.model.actor.L2Character;
import net.xcine.gameserver.model.actor.L2Npc;
import net.xcine.gameserver.model.actor.instance.L2PcInstance;
import net.xcine.gameserver.util.Util;
import net.xcine.util.Rnd;

public class FairyTrees extends AbstractNpcAI
{
	private static final int[] mobs =
	{
		27185,
		27186,
		27187,
		27188
	};
	
	public FairyTrees(String name, String descr)
	{
		super(name, descr);
		this.registerMobs(mobs);
		super.addSpawnId(27189);
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isPet)
	{
		int npcId = npc.getNpcId();
		if (Util.contains(mobs, npcId))
		{
			for (int i = 0; i < 20; i++)
			{
				L2Attackable newNpc = (L2Attackable) addSpawn(27189, npc.getX(), npc.getY(), npc.getZ(), 0, false, 30000, false);
				L2Character originalKiller = isPet ? killer.getPet() : killer;
				newNpc.setRunning();
				newNpc.addDamageHate(originalKiller, 0, 999);
				newNpc.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, originalKiller);
				if (Rnd.get(1, 2) == 1)
				{
					L2Skill skill = SkillTable.getInstance().getInfo(4243, 1);
					if ((skill != null) && (originalKiller != null))
					{
						skill.getEffects(newNpc, originalKiller);
					}
				}
			}
		}
		
		return super.onKill(npc, killer, isPet);
	}
	
	public static void main(String[] args)
	{
		new FairyTrees(FairyTrees.class.getSimpleName(), "ai/group");
	}
}