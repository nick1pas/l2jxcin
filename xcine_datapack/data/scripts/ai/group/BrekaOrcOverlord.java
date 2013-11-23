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
import net.xcine.gameserver.model.actor.L2Npc;
import net.xcine.gameserver.model.actor.instance.L2PcInstance;
import net.xcine.gameserver.model.quest.QuestEventType;
import net.xcine.gameserver.network.serverpackets.CreatureSay;
import net.xcine.util.Rnd;

public class BrekaOrcOverlord extends AbstractNpcAI
{
	private static boolean _FirstAttacked;
	
	public BrekaOrcOverlord(String name, String descr)
	{
		super(name, descr);
		_FirstAttacked = false;
		
		int mobs[] =
		{
			20270
		};
		
		registerMobs(mobs, QuestEventType.ON_ATTACK, QuestEventType.ON_KILL);
	}
	
	@Override
	public String onAttack(L2Npc npc, L2PcInstance attacker, int damage, boolean isPet)
	{
		if (npc.getNpcId() == 20270)
		{
			if (_FirstAttacked)
			{
				if (Rnd.get(100) < 50)
				{
					npc.broadcastPacket(new CreatureSay(npc.getObjectId(), 0, npc.getName(), "Extreme strength! ! ! !"));
				}
				else if (Rnd.get(100) < 50)
				{
					npc.broadcastPacket(new CreatureSay(npc.getObjectId(), 0, npc.getName(), "Humph, wanted to win me to be also in tender!"));
				}
				else if (Rnd.get(100) < 50)
				{
					npc.broadcastPacket(new CreatureSay(npc.getObjectId(), 0, npc.getName(), "Haven't thought to use this unique skill for this small thing!"));
				}
			}
			else
			{
				_FirstAttacked = true;
			}
		}
		
		return super.onAttack(npc, attacker, damage, isPet);
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isPet)
	{
		if (npc.getNpcId() == 20270)
		{
			_FirstAttacked = false;
		}
		else if (_FirstAttacked)
		{
			addSpawn(npc.getNpcId(), npc.getX(), npc.getY(), npc.getZ(), npc.getHeading(), true, 0, true);
		}
		
		return super.onKill(npc, killer, isPet);
	}
	
	public static void main(String[] args)
	{
		new BrekaOrcOverlord(BrekaOrcOverlord.class.getSimpleName(), "ai/group");
	}
}