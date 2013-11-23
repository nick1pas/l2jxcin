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
import net.xcine.gameserver.model.actor.L2Attackable;
import net.xcine.gameserver.model.actor.L2Character;
import net.xcine.gameserver.model.actor.L2Npc;
import net.xcine.gameserver.model.actor.instance.L2PcInstance;
import net.xcine.gameserver.model.quest.QuestEventType;
import net.xcine.util.Rnd;

public class AshurasOfDestruction extends AbstractNpcAI
{
	private static final int NPC[] =
	{
		21390,
		21656
	};
	
	public AshurasOfDestruction(String name, String descr)
	{
		super(name, descr);
		
		int mobs[] =
		{
			21390
		};
		
		registerMobs(mobs, QuestEventType.ON_KILL);
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isPet)
	{
		if (npc.getNpcId() == NPC[0])
		{
			if (Rnd.get(100) <= 30)
			{
				for (int i = 0; i < 5; i++)
				{
					L2Attackable newNpc = (L2Attackable) addSpawn(NPC[1], npc.getX(), npc.getY(), npc.getZ(), npc.getHeading(), true, 0, false);
					L2Character originalAttacker = isPet ? killer.getPet() : killer;
					newNpc.setRunning();
					newNpc.addDamageHate(originalAttacker, 0, 999);
					newNpc.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, originalAttacker);
				}
				return super.onKill(npc, killer, isPet);
			}
		}
		return null;
	}
	
	public static void main(String[] args)
	{
		new AshurasOfDestruction(AshurasOfDestruction.class.getSimpleName(), "ai/group");
	}
}