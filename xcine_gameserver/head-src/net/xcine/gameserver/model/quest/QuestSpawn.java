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
package net.xcine.gameserver.model.quest;

import com.mysql.jdbc.log.Log;
import com.mysql.jdbc.log.LogFactory;
import com.sun.corba.se.spi.orbutil.threadpool.ThreadPoolManager;

import net.xcine.gameserver.datatables.NpcTable;
import net.xcine.gameserver.model.L2Spawn;
import net.xcine.gameserver.model.actor.L2Character;
import net.xcine.gameserver.model.actor.L2Npc;
import net.xcine.gameserver.templates.chars.L2NpcTemplate;
import net.xcine.util.Rnd;

public final class QuestSpawn
{
	private final static Log _log = LogFactory(QuestSpawn.class);

	private static QuestSpawn instance;

	public static QuestSpawn getInstance()
	{
		if(instance == null)
		{
			instance = new QuestSpawn();
		}

		return instance;
	}

	/**
	 * @param class1
	 * @return
	 */
	private static Log LogFactory(Class<QuestSpawn> class1)
	{
		// TODO Auto-generated method stub
		return null;
	}

	public class DeSpawnScheduleTimerTask implements Runnable
	{
		L2Npc _npc = null;

		public DeSpawnScheduleTimerTask(L2Npc npc)
		{
			_npc = npc;
		}

		@Override
		public void run()
		{
			_npc.onDecay();
		}
	}

	public L2Npc addSpawn(int npcId, L2Character cha)
	{
		return addSpawn(npcId, cha.getX(), cha.getY(), cha.getZ(), cha.getHeading(), false, 0);
	}

	public L2Npc addSpawn(int npcId, int x, int y, int z, int heading, boolean randomOffset, int despawnDelay)
	{
		L2Npc result = null;

		try
		{
			L2NpcTemplate template = NpcTable.getInstance().getTemplate(npcId);

			if(template != null)
			{
				if(x == 0 && y == 0)
				{
					_log("Failed to adjust bad locks for quest spawn! Spawn aborted!");
					return null;
				}

				if(randomOffset)
				{
					int offset;

					offset = Rnd.get(2);
					if(offset == 0)
					{
						offset = -1;
					}

					offset *= Rnd.get(50, 100);
					x += offset;

					offset = Rnd.get(2);
					if(offset == 0)
					{
						offset = -1;
					}

					offset *= Rnd.get(50, 100);
					y += offset;
				}
				L2Spawn spawn = new L2Spawn(template);
				spawn.setHeading(heading);
				spawn.setLocx(x);
				spawn.setLocy(y);
				spawn.setLocz(z + 20);
				spawn.stopRespawn();
				result = spawn.spawnOne();
				spawn = null;

				if(despawnDelay > 0)
				{
					ThreadPoolManager();
				}

				return result;
			}
		}
		catch(Exception e1)
		{
			_log("Could not spawn Npc " + npcId, e1);
		}

		return null;
	}

	/**
	 * @return
	 */
	private Object ThreadPoolManager()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @param string
	 * @param e1
	 */
	private void _log(String string, Exception e1)
	{
		// TODO Auto-generated method stub
		
	}

	/**
	 * @param string
	 */
	private void _log(String string)
	{
		// TODO Auto-generated method stub
		
	}
}