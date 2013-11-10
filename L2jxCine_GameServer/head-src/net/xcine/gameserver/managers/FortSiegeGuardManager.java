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
package net.xcine.gameserver.managers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.logging.Logger;

import javolution.util.FastList;
import net.xcine.Config;
import net.xcine.gameserver.datatables.sql.NpcTable;
import net.xcine.gameserver.model.actor.instance.L2PcInstance;
import net.xcine.gameserver.model.entity.siege.Fort;
import net.xcine.gameserver.model.spawn.L2Spawn;
import net.xcine.gameserver.templates.L2NpcTemplate;
import net.xcine.util.database.L2DatabaseFactory;

/**
 * @author programmos, scoria dev
 */

public class FortSiegeGuardManager
{
	private static final Logger _log = Logger.getLogger(FortSiegeGuardManager.class.getName());

	private Fort _fort;
	private List<L2Spawn> _siegeGuardSpawn = new FastList<>();

	public FortSiegeGuardManager(Fort fort)
	{
		_fort = fort;
	}

	/**
	 * @param activeChar 
	 * @param npcId 
	 */
	public void addSiegeGuard(L2PcInstance activeChar, int npcId)
	{
		if(activeChar == null)
			return;

		addSiegeGuard(activeChar.getX(), activeChar.getY(), activeChar.getZ(), activeChar.getHeading(), npcId);
	}

	/**
	 * @param x 
	 * @param y 
	 * @param z 
	 * @param heading 
	 * @param npcId 
	 */
	public void addSiegeGuard(int x, int y, int z, int heading, int npcId)
	{
		saveSiegeGuard(x, y, z, heading, npcId, 0);
	}

	/**
	 * @param activeChar 
	 * @param npcId 
	 */
	public void hireMerc(L2PcInstance activeChar, int npcId)
	{
		if(activeChar == null)
			return;

		hireMerc(activeChar.getX(), activeChar.getY(), activeChar.getZ(), activeChar.getHeading(), npcId);
	}

	/**
	 * @param x 
	 * @param y 
	 * @param z 
	 * @param heading 
	 * @param npcId 
	 */
	public void hireMerc(int x, int y, int z, int heading, int npcId)
	{
		saveSiegeGuard(x, y, z, heading, npcId, 1);
	}

	/**
	 * @param npcId 
	 * @param x 
	 * @param y 
	 * @param z 
	 */
	public void removeMerc(int npcId, int x, int y, int z)
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement("Delete From fort_siege_guards Where npcId = ? And x = ? AND y = ? AND z = ? AND isHired = 1");
			statement.setInt(1, npcId);
			statement.setInt(2, x);
			statement.setInt(3, y);
			statement.setInt(4, z);
			statement.execute();
			statement.close();
			statement = null;
		}
		catch(Exception e1)
		{
			if(Config.ENABLE_ALL_EXCEPTIONS)
				e1.printStackTrace();
			
			_log.warning("Error deleting hired siege guard at " + x + ',' + y + ',' + z + ":" + e1);
		}
	}

	/**
	 * Remove mercs.<BR>
	 * <BR>
	 */
	public void removeMercs()
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement("Delete From fort_siege_guards Where fortId = ? And isHired = 1");
			statement.setInt(1, getFort().getFortId());
			statement.execute();
			statement.close();
			statement = null;
		}
		catch(Exception e1)
		{
			if(Config.ENABLE_ALL_EXCEPTIONS)
				e1.printStackTrace();
			_log.warning("Error deleting hired siege guard for fort " + getFort().getName() + ":" + e1);
		}
	}

	/**
	 * Spawn guards.<BR>
	 * <BR>
	 */
	public void spawnSiegeGuard()
	{
		try
		{
			/*int hiredCount = 0;
			hiredMax    = MercTicketManager.getInstance().getMaxAllowedMerc(_fort.getFortId());  
			boolean isHired = getFort().getOwnerId() > 0 ? true : false;
			*/
			loadSiegeGuard();
			for(L2Spawn spawn : getSiegeGuardSpawn())
			{
				if(spawn != null)
				{
					spawn.init();
					/*if(isHired)
					{
						hiredCount++;
						//if (hiredCount > hiredMax)
						//    return;
					}
					*/
				}
			}
		}
		catch(Throwable t)
		{
			if(Config.ENABLE_ALL_EXCEPTIONS)
				t.printStackTrace();
			
			
			_log.warning("Error spawning siege guards for fort " + getFort().getName() + ":" + t.toString());
		}
	}

	public void unspawnSiegeGuard()
	{
		for(L2Spawn spawn : getSiegeGuardSpawn())
		{
			if(spawn == null)
			{
				continue;
			}

			spawn.stopRespawn();
			spawn.getLastSpawn().doDie(spawn.getLastSpawn());
		}

		getSiegeGuardSpawn().clear();
	}

	private void loadSiegeGuard()
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement("SELECT * FROM fort_siege_guards Where fortId = ? ");
			statement.setInt(1, getFort().getFortId());
			ResultSet rs = statement.executeQuery();

			L2Spawn spawn1;
			L2NpcTemplate template1;

			while(rs.next())
			{
				template1 = NpcTable.getInstance().getTemplate(rs.getInt("npcId"));
				if(template1 != null)
				{
					spawn1 = new L2Spawn(template1);
					spawn1.setId(rs.getInt("id"));
					spawn1.setAmount(1);
					spawn1.setLocx(rs.getInt("x"));
					spawn1.setLocy(rs.getInt("y"));
					spawn1.setLocz(rs.getInt("z"));
					spawn1.setHeading(rs.getInt("heading"));
					spawn1.setRespawnDelay(rs.getInt("respawnDelay"));
					spawn1.setLocation(0);
					_siegeGuardSpawn.add(spawn1);
					spawn1 = null;
				}
				else
				{
					_log.warning("Missing npc data in npc table for id: " + rs.getInt("npcId"));
				}
				template1 = null;
			}
			rs.close();
			statement.close();
			statement = null;
			rs = null;
		}
		catch(Exception e1)
		{
			if(Config.ENABLE_ALL_EXCEPTIONS)
				e1.printStackTrace();
			
			
			_log.warning("Error loading siege guard for fort " + getFort().getName() + ":" + e1);
		}
	}

	/**
	 * @param x 
	 * @param y 
	 * @param z 
	 * @param heading 
	 * @param npcId 
	 * @param isHire 
	 */
	private void saveSiegeGuard(int x, int y, int z, int heading, int npcId, int isHire)
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement("Insert Into fort_siege_guards (fortId, npcId, x, y, z, heading, respawnDelay, isHired) Values (?, ?, ?, ?, ?, ?, ?, ?)");
			statement.setInt(1, getFort().getFortId());
			statement.setInt(2, npcId);
			statement.setInt(3, x);
			statement.setInt(4, y);
			statement.setInt(5, z);
			statement.setInt(6, heading);
			if(isHire == 1)
			{
				statement.setInt(7, 0);
			}
			else
			{
				statement.setInt(7, 600);
			}
			statement.setInt(8, isHire);
			statement.execute();
			statement.close();
			statement = null;
		}
		catch(Exception e1)
		{
			if(Config.ENABLE_ALL_EXCEPTIONS)
				e1.printStackTrace();
			
			_log.warning("Error adding siege guard for fort " + getFort().getName() + ":" + e1);
		}
	}

	public final Fort getFort()
	{
		return _fort;
	}

	public final List<L2Spawn> getSiegeGuardSpawn()
	{
		return _siegeGuardSpawn;
	}
}
