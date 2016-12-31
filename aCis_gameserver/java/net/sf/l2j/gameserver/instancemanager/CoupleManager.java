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
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.l2j.L2DatabaseFactory;
import net.sf.l2j.gameserver.idfactory.IdFactory;
import net.sf.l2j.gameserver.model.World;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.holder.IntIntHolder;

public class CoupleManager
{
	private static final Logger _log = Logger.getLogger(CoupleManager.class.getName());
	
	private final Map<Integer, IntIntHolder> _couples = new ConcurrentHashMap<>();
	
	protected CoupleManager()
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement ps = con.prepareStatement("SELECT * FROM mods_wedding");
			
			ResultSet rs = ps.executeQuery();
			while (rs.next())
				_couples.put(rs.getInt("id"), new IntIntHolder(rs.getInt("requesterId"), rs.getInt("partnerId")));
			
			rs.close();
			ps.close();
			
			_log.info("CoupleManager : Loaded " + _couples.size() + " couples.");
		}
		catch (Exception e)
		{
			_log.log(Level.WARNING, "CoupleManager: " + e.getMessage(), e);
		}
	}
	
	public final Map<Integer, IntIntHolder> getCouples()
	{
		return _couples;
	}
	
	public final IntIntHolder getCouple(int coupleId)
	{
		return _couples.get(coupleId);
	}
	
	/**
	 * Add a couple to the couples map. Both players must be logged.
	 * @param requester : The wedding requester.
	 * @param partner : The wedding partner.
	 */
	public void addCouple(L2PcInstance requester, L2PcInstance partner)
	{
		if (requester == null || partner == null)
			return;
		
		final int coupleId = IdFactory.getInstance().getNextId();
		
		_couples.put(coupleId, new IntIntHolder(requester.getObjectId(), partner.getObjectId()));
		
		requester.setCoupleId(coupleId);
		partner.setCoupleId(coupleId);
	}
	
	/**
	 * Delete the couple. If players are logged, reset wedding variables.
	 * @param coupleId : The couple id to delete.
	 */
	public void deleteCouple(int coupleId)
	{
		final IntIntHolder couple = _couples.remove(coupleId);
		if (couple == null)
			return;
		
		final L2PcInstance requester = World.getInstance().getPlayer(couple.getId());
		if (requester != null)
		{
			requester.setCoupleId(0);
			requester.sendMessage("You are now divorced.");
		}
		
		final L2PcInstance partner = World.getInstance().getPlayer(couple.getValue());
		if (partner != null)
		{
			partner.setCoupleId(0);
			partner.sendMessage("You are now divorced.");
		}
	}
	
	/**
	 * Save all couples on shutdown. Delete previous SQL infos.
	 */
	public void save()
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement ps = con.prepareStatement("DELETE FROM mods_wedding");
			ps.execute();
			ps.close();
			
			ps = con.prepareStatement("INSERT INTO mods_wedding (id, requesterId, partnerId) VALUES (?,?,?)");
			for (Entry<Integer, IntIntHolder> coupleEntry : _couples.entrySet())
			{
				final IntIntHolder couple = coupleEntry.getValue();
				
				ps.setInt(1, coupleEntry.getKey());
				ps.setInt(2, couple.getId());
				ps.setInt(3, couple.getValue());
				ps.addBatch();
			}
			ps.executeBatch();
			ps.close();
		}
		catch (Exception e)
		{
			_log.log(Level.WARNING, "CoupleManager: " + e.getMessage(), e);
		}
	}
	
	/**
	 * @param coupleId : The couple id to check.
	 * @param objectId : The player objectId to check.
	 * @return the partner objectId, or 0 if not found.
	 */
	public final int getPartnerId(int coupleId, int objectId)
	{
		final IntIntHolder couple = _couples.get(coupleId);
		if (couple == null)
			return 0;
		
		return (couple.getId() == objectId) ? couple.getValue() : couple.getId();
	}
	
	public static final CoupleManager getInstance()
	{
		return SingletonHolder._instance;
	}
	
	private static class SingletonHolder
	{
		protected static final CoupleManager _instance = new CoupleManager();
	}
}