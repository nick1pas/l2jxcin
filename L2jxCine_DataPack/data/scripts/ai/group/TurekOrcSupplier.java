/*
 * This program is free software; you can redistribute it and/or modify
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
package ai.group;

import net.xcine.gameserver.model.L2Npc;
import net.xcine.gameserver.model.actor.instance.L2PcInstance;
import net.xcine.gameserver.network.serverpackets.CreatureSay;
import net.xcine.util.random.Rnd;
import ai.AbstractNpcAI;

public class TurekOrcSupplier extends AbstractNpcAI
{
	private static final int NPC = 20498;
	private static boolean _FirstAttacked;

	public TurekOrcSupplier(String name, String descr)
	{
		super(name, descr);

		_FirstAttacked = false;

		addEventId(NPC, QuestEventType.ON_ATTACK);
		addEventId(NPC, QuestEventType.ON_KILL);
	}

	@Override
	public String onAttack (L2Npc npc, L2PcInstance attacker, int damage, boolean isPet)
	{
		if(npc.getNpcId() == NPC)
		{
			if(_FirstAttacked)
			{
				if(Rnd.get(40) != 0)
				{
					return null;
				}
				npc.broadcastPacket(new CreatureSay(npc.getObjectId(),0,npc.getName(),"You wont take me down easily."));
			}
			else
			{
				_FirstAttacked = true;
				npc.broadcastPacket(new CreatureSay(npc.getObjectId(),0,npc.getName(),"We shall see about that!"));
			}
		}
		return super.onAttack(npc, attacker, damage, isPet);
	}

	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isPet)
	{
		if(npc.getNpcId() == NPC)
		{
			_FirstAttacked = false;
		}
		else if(_FirstAttacked)
		{
			addSpawn(npc.getNpcId(),npc.getX(), npc.getY(), npc.getZ(),npc.getHeading(),true,0);
		}
		return super.onKill(npc,killer,isPet);
	}
	
	public static void main(String[] args)
	{
		new TurekOrcSupplier(TurekOrcSupplier.class.getSimpleName(), "ai/group");
	}
}