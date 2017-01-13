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
package net.sf.l2j.gameserver.instancemanager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Logger;

import net.sf.l2j.commons.concurrent.ThreadPool;

import net.sf.l2j.Config;
import net.sf.l2j.L2DatabaseFactory;
import net.sf.l2j.gameserver.datatables.SkillTable;
import net.sf.l2j.gameserver.model.L2Skill;
import net.sf.l2j.gameserver.model.World;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;
import net.sf.l2j.gameserver.network.serverpackets.ExShowScreenMessage;

/**
 * @author rapfersan92
 */
public class AioManager
{
	private static final Logger _log = Logger.getLogger(AioManager.class.getName());
	
	private final Map<Integer, Long> _aios;
	protected final Map<Integer, Long> _aiosTask;
	private ScheduledFuture<?> _scheduler;
	
	public static AioManager getInstance()
	{
		return SingletonHolder._instance;
	}
	
	protected AioManager()
	{
		_aios = new ConcurrentHashMap<>();
		_aiosTask = new ConcurrentHashMap<>();
		_scheduler = ThreadPool.scheduleAtFixedRate(new AioTask(), 1000, 1000);
		load();
	}
	
	public void reload()
	{
		_aios.clear();
		_aiosTask.clear();
		if (_scheduler != null)
			_scheduler.cancel(true);
		_scheduler = ThreadPool.scheduleAtFixedRate(new AioTask(), 1000, 1000);
		load();
	}
	
	public void load()
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement("SELECT objectId, duration FROM character_aio ORDER BY objectId");
			ResultSet rs = statement.executeQuery();
			while (rs.next())
				_aios.put(rs.getInt("objectId"), rs.getLong("duration"));
			rs.close();
			statement.close();
		}
		catch (Exception e)
		{
			_log.warning("Exception: AioManager load: " + e.getMessage());
		}
		
		_log.info("AioManager: Loaded " + _aios.size() + " characters with aio privileges.");
	}
	
	public void addAio(int objectId, long duration)
	{
		_aios.put(objectId, duration);
		_aiosTask.put(objectId, duration);
		manageAioPrivileges(objectId, true);
		
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement("INSERT INTO character_aio (objectId, duration) VALUES (?, ?)");
			statement.setInt(1, objectId);
			statement.setLong(2, duration);
			statement.execute();
			statement.close();
		}
		catch (Exception e)
		{
			_log.warning("Exception: AioManager addAio: " + e.getMessage());
		}
	}
	
	public void updateAio(int objectId, long duration)
	{
		duration += _aios.get(objectId);
		_aios.put(objectId, duration);
		_aiosTask.put(objectId, duration);
		
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement("UPDATE character_aio SET duration = ? WHERE objectId = ?");
			statement.setLong(1, duration);
			statement.setInt(2, objectId);
			statement.execute();
			statement.close();
		}
		catch (Exception e)
		{
			_log.warning("Exception: AioManager updateAio: " + e.getMessage());
		}
	}
	
	public void removeAio(int objectId)
	{
		_aios.remove(objectId);
		_aiosTask.remove(objectId);
		manageAioPrivileges(objectId, false);
		
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement("DELETE FROM character_aio WHERE objectId = ?");
			statement.setInt(1, objectId);
			statement.execute();
			statement.close();
		}
		catch (Exception e)
		{
			_log.warning("Exception: AioManager removeAio: " + e.getMessage());
		}
	}
	
	public boolean hasAioPrivileges(int objectId)
	{
		return _aios.containsKey(objectId);
	}
	
	public long getAioDuration(int objectId)
	{
		return _aios.get(objectId);
	}
	
	public void addAioTask(int objectId, long duration)
	{
		_aiosTask.put(objectId, duration);
	}
	
	public void removeAioTask(int objectId)
	{
		_aiosTask.remove(objectId);
	}
	
	public void manageAioPrivileges(int objectId, boolean apply)
	{
		final L2PcInstance player = World.getInstance().getPlayer(objectId);
		player.broadcastUserInfo();
		
		for (Map.Entry<Integer, Integer> entry : Config.LIST_AIO_ITEMS.entrySet())
		{
			final int itemId = entry.getKey();
			final int amount = entry.getValue();
			if (apply)
				player.addItem("add aio item", itemId, amount, player, true);
			else
			{
				final ItemInstance item = player.getInventory().getItemByItemId(itemId);
				if (item != null && item.isStackable() && amount > 1)
					player.destroyItemByItemId("delete aio item", itemId, item.getCount(), player, true);
				else
				{
					for (int i = 0; i < amount; i++)
						player.destroyItemByItemId("delete aio item", itemId, 1, player, true);
				}
			}
		}
		
		for (int val : Config.LIST_AIO_SKILLS.keySet())
		{
			final L2Skill skill = SkillTable.getInstance().getInfo(val, Config.LIST_AIO_SKILLS.get(val));
			if (apply)
				player.addSkill(skill, true);
			else
				player.removeSkill(skill);
		}
		
		player.giveAvailableSkills();
		player.sendSkillList();
	}
	
	public class AioTask implements Runnable
	{
		@Override
		public final void run()
		{
			if (_aiosTask.isEmpty())
				return;
			
			for (Map.Entry<Integer, Long> entry : _aiosTask.entrySet())
			{
				final long duration = entry.getValue();
				if (System.currentTimeMillis() > duration)
				{
					final int objectId = entry.getKey();
					removeAio(objectId);
					
					final L2PcInstance player = World.getInstance().getPlayer(objectId);
					player.sendPacket(new ExShowScreenMessage("Your aio privileges were removed.", 10000));
				}
			}
		}
	}
	
	private static class SingletonHolder
	{
		protected static final AioManager _instance = new AioManager();
	}
}