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

import net.xcine.gameserver.model.L2Npc;
import net.xcine.gameserver.model.actor.instance.L2PcInstance;
import net.xcine.gameserver.network.serverpackets.NpcSay;
import net.xcine.util.random.Rnd;
import ai.AbstractNpcAI;

/**
 * @author Maxi
 */
public class TurekOrcFootman extends AbstractNpcAI
{
	private static final int TUREKF = 20499;
	
	private static boolean _FirstAttacked;
	
	public TurekOrcFootman(String name, String descr)
	{
		super(name, descr);
		int[] mobs =
		{
			TUREKF
		};
		registerMobs(mobs);
		_FirstAttacked = false;
	}
	
	@Override
	public String onAttack(L2Npc npc, L2PcInstance attacker, int damage, boolean isPet)
	{
		if (npc.getNpcId() == TUREKF)
		{
			if (_FirstAttacked)
			{
				if (Rnd.get(100) == 40)
				{
					npc.broadcastPacket(new NpcSay(npc.getObjectId(), 0, npc.getNpcId(), "There is no reason for you to kill me! I have nothing you need!"));
				}
			}
			else
			{
				_FirstAttacked = true;
				npc.broadcastPacket(new NpcSay(npc.getObjectId(), 0, npc.getNpcId(), "We shall see about that!"));
			}
		}
		return super.onAttack(npc, attacker, damage, isPet);
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isPet)
	{
		int npcId = npc.getNpcId();
		if (npcId == TUREKF)
		{
			_FirstAttacked = false;
		}
		return super.onKill(npc, killer, isPet);
	}
	
	public static void main(String[] args)
	{
		new TurekOrcFootman(TurekOrcFootman.class.getSimpleName(), "ai/group");
	}
}