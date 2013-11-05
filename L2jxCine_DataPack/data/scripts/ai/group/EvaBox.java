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

import net.xcine.gameserver.datatables.sql.ItemTable;
import net.xcine.gameserver.model.L2Effect;
import net.xcine.gameserver.model.L2Npc;
import net.xcine.gameserver.model.actor.instance.L2ItemInstance;
import net.xcine.gameserver.model.actor.instance.L2PcInstance;
import net.xcine.util.random.Rnd;
import ai.AbstractNpcAI;

public class EvaBox extends AbstractNpcAI
{
	private final static int[] KISS_OF_EVA =
	{
		1073,
		3141,
		3252
	};
	private final static int BOX = 32342;
	private final static int[] REWARDS =
	{
		9692,
		9693
	};
	
	public EvaBox(String name, String descr)
	{
		super(name, descr);
		
		int mobs[] =
		{
			BOX
		};
		
		registerMobs(mobs, QuestEventType.ON_KILL);
	}
	
	public void dropItem(L2Npc npc, int itemId, int count, L2PcInstance player)
	{
		L2ItemInstance ditem = ItemTable.getInstance().createItem("Loot", itemId, count, player);
		ditem.dropMe(npc, npc.getX(), npc.getY(), npc.getZ());
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isPet)
	{
		boolean found = false;
		for (L2Effect effect : killer.getAllEffects())
		{
			for (int i = 0; i < 3; i++)
			{
				if (effect.getSkill().getId() == KISS_OF_EVA[i])
				{
					found = true;
				}
			}
		}
		
		if (found == true)
		{
			int dropid = Rnd.get(1);
			if (dropid == 1)
			{
				dropItem(npc, REWARDS[dropid], 1, killer);
			}
			else if (dropid == 0)
			{
				dropItem(npc, REWARDS[dropid], 1, killer);
			}
		}
		
		return super.onKill(npc, killer, isPet);
	}
	
	public static void main(String[] args)
	{
		new EvaBox(EvaBox.class.getSimpleName(), "ai/group");
	}
}