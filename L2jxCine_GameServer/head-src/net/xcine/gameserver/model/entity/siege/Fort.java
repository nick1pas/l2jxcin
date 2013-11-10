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
package net.xcine.gameserver.model.entity.siege;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Logger;

import javolution.util.FastList;
import net.xcine.gameserver.datatables.sql.ClanTable;
import net.xcine.gameserver.datatables.xml.DoorData;
import net.xcine.gameserver.model.L2Clan;
import net.xcine.gameserver.model.L2Object;
import net.xcine.gameserver.model.actor.instance.L2DoorInstance;
import net.xcine.gameserver.model.actor.instance.L2PcInstance;
import net.xcine.gameserver.model.entity.Announcements;
import net.xcine.gameserver.model.entity.sevensigns.SevenSigns;
import net.xcine.gameserver.model.zone.type.L2FortZone;
import net.xcine.gameserver.network.serverpackets.PlaySound;
import net.xcine.gameserver.network.serverpackets.PledgeShowInfoUpdate;
import net.xcine.gameserver.thread.ThreadPoolManager;
import net.xcine.util.database.L2DatabaseFactory;

/**
 * @author programmos
 */

public class Fort
{
	protected static final Logger _log = Logger.getLogger(Fort.class.getName());

	private int _fortId = 0;
	private List<L2DoorInstance> _doors = new FastList<>();
	private List<String> _doorDefault = new FastList<>();
	private String _name = "";
	private int _ownerId = 0;
	private L2Clan _fortOwner = null;
	private FortSiege _siege = null;
	private Calendar _siegeDate;
	private int _siegeDayOfWeek = 7; // Default to saturday
	private int _siegeHourOfDay = 20; // Default to 8 pm server time
	private L2FortZone _zone;
	private L2Clan _formerOwner = null;

	public Fort(int fortId)
	{
		_fortId = fortId;
		load();
		loadDoor();
	}

	public void EndOfSiege(L2Clan clan)
	{
		ThreadPoolManager.getInstance().scheduleGeneral(new endFortressSiege(this, clan), 1000);

	}

	public void Engrave(L2Clan clan, int objId)
	{
		getSiege().announceToPlayer("Clan " + clan.getName() + " has finished to raise the flag.", true);
		setOwner(clan);
	}

	/**
	 * @param amount
	 */
	public void addToTreasury(int amount)
	{
	}

	/**
	 * @param amount 
	 * @return
	 */
	public boolean addToTreasuryNoTax(int amount)
	{
		return true;
	}
	public void banishForeigners()
	{
		_zone.banishForeigners(getOwnerId());
	}

	/**
	 * @param x 
	 * @param y 
	 * @param z 
	 * @return true if object is inside the zone
	 */
	public boolean checkIfInZone(int x, int y, int z)
	{
		return _zone.isInsideZone(x, y, z);
	}
	/**
	 * @param zone
	 */
	public void setZone(L2FortZone zone)
	{
		_zone = zone;
	}

	public L2FortZone getZone()
	{
		return _zone;
	}

	/**
	 * @param obj
	 * @return
	 */
	public double getDistance(L2Object obj)
	{
		return _zone.getDistanceToZone(obj);
	}

	public void closeDoor(L2PcInstance activeChar, int doorId)
	{
		openCloseDoor(activeChar, doorId, false);
	}

	public void openDoor(L2PcInstance activeChar, int doorId)
	{
		openCloseDoor(activeChar, doorId, true);
	}

	public void openCloseDoor(L2PcInstance activeChar, int doorId, boolean open)
	{
		if(activeChar.getClanId() != getOwnerId())
			return;

		L2DoorInstance door = getDoor(doorId);

		if(door != null)
		{
			if(open)
			{
				door.openMe();
			}
			else
			{
				door.closeMe();
			}
		}

		door = null;
	}

	public void removeUpgrade()
	{
		removeDoorUpgrade();
	}

	public void setOwner(L2Clan clan)
	{
		if(getOwnerId() > 0 && (clan == null || clan.getClanId() != getOwnerId()))
		{
			L2Clan oldOwner = ClanTable.getInstance().getClan(getOwnerId());

			if(oldOwner != null)
			{
				if(_formerOwner == null)
				{
					_formerOwner = oldOwner;
				}

				oldOwner.setHasFort(0);
				Announcements.getInstance().announceToAll(oldOwner.getName() + " has lost " + getName() + " fortress!");
			}

			oldOwner = null;
		}

		updateOwnerInDB(clan); 

		if(getSiege().getIsInProgress())
		{
			getSiege().midVictory(); 
		}

		updateClansReputation();

		_fortOwner = clan;
	}

	public void removeOwner(L2Clan clan)
	{
		if(clan != null)
		{
			_formerOwner = clan;

			clan.setHasFort(0);
			Announcements.getInstance().announceToAll(clan.getName() + " has lost " + getName() + " fort");
			clan.broadcastToOnlineMembers(new PledgeShowInfoUpdate(clan));
		}

		updateOwnerInDB(null);

		if(getSiege().getIsInProgress())
		{
			getSiege().midVictory();
		}

		updateClansReputation();

		_fortOwner = null;
	}

	public void setTaxPercent(L2PcInstance activeChar, int taxPercent)
	{
		int maxTax;

		switch(SevenSigns.getInstance().getSealOwner(SevenSigns.SEAL_STRIFE))
		{
			case SevenSigns.CABAL_DAWN:
				maxTax = 25;
				break;
			case SevenSigns.CABAL_DUSK:
				maxTax = 5;
				break;
			default: 
				maxTax = 15;
		}

		if(taxPercent < 0 || taxPercent > maxTax)
		{
			activeChar.sendMessage("Tax value must be between 0 and " + maxTax + ".");
			return;
		}

		activeChar.sendMessage(getName() + " fort tax changed to " + taxPercent + "%.");
	}

	public void spawnDoor()
	{
		spawnDoor(false);
	}

	/**
	 * @param isDoorWeak 
	 */
	public void spawnDoor(boolean isDoorWeak)
	{
		for(int i = 0; i < getDoors().size(); i++)
		{
			L2DoorInstance door = getDoors().get(i);

			if(door.getCurrentHp() >= 0)
			{
				door.decayMe(); 
				door = DoorData.parseList(_doorDefault.get(i));

				if(isDoorWeak)
				{
					door.setCurrentHp(door.getMaxHp() / 2);
				}
				else {
					door.setCurrentHp(door.getMaxHp());
				}

				door.spawnMe(door.getX(), door.getY(), door.getZ());
				getDoors().set(i, door);
			}
			else if(!door.getOpen())
			{
				door.closeMe();
			}

			door = null;
		}

		loadDoorUpgrade(); 
	}

	public void upgradeDoor(int doorId, int hp, int pDef, int mDef)
	{
		L2DoorInstance door = getDoor(doorId);

		if(door == null)
			return;

		if(door.getDoorId() == doorId)
		{
			door.setCurrentHp(door.getMaxHp() + hp);

			saveDoorUpgrade(doorId, hp, pDef, mDef);
			return;
		}
	}

	private void load()
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement;
			ResultSet rs;
			
			statement = con.prepareStatement("Select * from fort where id = ?");
			statement.setInt(1, getFortId());
			rs = statement.executeQuery();

			while(rs.next())
			{
				_name = rs.getString("name");

				_siegeDate = Calendar.getInstance();
				_siegeDate.setTimeInMillis(rs.getLong("siegeDate"));

				_siegeDayOfWeek = rs.getInt("siegeDayOfWeek");

				if(_siegeDayOfWeek < 1 || _siegeDayOfWeek > 7)
				{
					_siegeDayOfWeek = 7;
				}

				_siegeHourOfDay = rs.getInt("siegeHourOfDay");
				if(_siegeHourOfDay < 0 || _siegeHourOfDay > 23)
				{
					_siegeHourOfDay = 20;
				}

				_ownerId = rs.getInt("owner");
			}

			rs.close();
			statement.close();
			statement = null;
			rs = null;

			if(getOwnerId() > 0)
			{
				L2Clan clan = ClanTable.getInstance().getClan(getOwnerId()); // Try to find clan instance
				//ThreadPoolManager.getInstance().scheduleGeneral(new FortUpdater(clan, 1), 3600000);     // Schedule owner tasks to start running
				if(clan!=null) {
					clan.setHasFort(getFortId());
					_fortOwner = clan;
				}
				clan = null;
			}
			else
			{
				_fortOwner = null;
			}

		}
		catch(Exception e)
		{
			_log.warning("Exception: loadFortData(): " + e.getMessage());
			e.printStackTrace();
		}
	}

	private void loadDoor()
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement("Select * from fort_door where fortId = ?");
			statement.setInt(1, getFortId());
			ResultSet rs = statement.executeQuery();

			while(rs.next())
			{
				_doorDefault.add(rs.getString("name") + ";" + rs.getInt("id") + ";" + rs.getInt("x") + ";" + rs.getInt("y") + ";" + rs.getInt("z") + ";" + rs.getInt("range_xmin") + ";" + rs.getInt("range_ymin") + ";" + rs.getInt("range_zmin") + ";" + rs.getInt("range_xmax") + ";" + rs.getInt("range_ymax") + ";" + rs.getInt("range_zmax") + ";" + rs.getInt("hp") + ";" + rs.getInt("pDef") + ";" + rs.getInt("mDef"));

				L2DoorInstance door = DoorData.parseList(_doorDefault.get(_doorDefault.size() - 1));
				door.spawnMe(door.getX(), door.getY(), door.getZ());

				_doors.add(door);

				DoorData.getInstance().putDoor(door);
				door = null;
			}

			rs.close();
			statement.close();
			statement = null;
			rs = null;
		}
		catch(Exception e)
		{
			_log.warning("Exception: loadFortDoor(): " + e.getMessage());
			e.printStackTrace();
		}
	}

	private void loadDoorUpgrade()
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement("Select * from fort_doorupgrade where doorId in (Select Id from fort_door where fortId = ?)");
			statement.setInt(1, getFortId());
			ResultSet rs = statement.executeQuery();

			while(rs.next())
			{
				upgradeDoor(rs.getInt("id"), rs.getInt("hp"), rs.getInt("pDef"), rs.getInt("mDef"));
			}
			rs.close();
			statement.close();
			statement = null;
			rs = null;
		}
		catch(Exception e)
		{
			_log.warning("Exception: loadFortDoorUpgrade(): " + e.getMessage());
			e.printStackTrace();
		}
	}

	private void removeDoorUpgrade()
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement("delete from fort_doorupgrade where doorId in (select id from fort_door where fortId=?)");
			statement.setInt(1, getFortId());
			statement.execute();
			statement.close();
			statement = null;
		}
		catch(Exception e)
		{
			_log.warning("Exception: removeDoorUpgrade(): " + e.getMessage());
			e.printStackTrace();
		}
	}

	private void saveDoorUpgrade(int doorId, int hp, int pDef, int mDef)
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement("INSERT INTO fort_doorupgrade (doorId, hp, pDef, mDef) values (?,?,?,?)");
			statement.setInt(1, doorId);
			statement.setInt(2, hp);
			statement.setInt(3, pDef);
			statement.setInt(4, mDef);
			statement.execute();
			statement.close();
			statement = null;
		}
		catch(Exception e)
		{
			_log.warning("Exception: saveDoorUpgrade(int doorId, int hp, int pDef, int mDef): " + e.getMessage());
			e.printStackTrace();
		}
	}

	private void updateOwnerInDB(L2Clan clan)
	{
		if(clan != null)
		{
			_ownerId = clan.getClanId(); 
		}
		else
		{
			_ownerId = 0; 
		}

		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement;

			statement = con.prepareStatement("UPDATE fort SET owner=? where id = ?");
			statement.setInt(1, getOwnerId());
			statement.setInt(2, getFortId());
			statement.execute();
			statement.close();
			statement = null;

			if(clan != null)
			{
				clan.setHasFort(getFortId()); 
				Announcements.getInstance().announceToAll(clan.getName() + " has taken " + getName() + " fort!");
				clan.broadcastToOnlineMembers(new PledgeShowInfoUpdate(clan));
				clan.broadcastToOnlineMembers(new PlaySound(1, "Siege_Victory", 0, 0, 0, 0, 0));
			}
		}
		catch(Exception e)
		{
			_log.warning("Exception: updateOwnerInDB(L2Clan clan): " + e.getMessage());
			e.printStackTrace();
		}
	}

	public final int getFortId()
	{
		return _fortId;
	}

	public final L2Clan getOwnerClan()
	{
		return _fortOwner;
	}

	public final int getOwnerId()
	{
		return _ownerId;
	}

	public final L2DoorInstance getDoor(int doorId)
	{
		if(doorId <= 0)
			return null;

		for(int i = 0; i < getDoors().size(); i++)
		{
			L2DoorInstance door = getDoors().get(i);

			if(door.getDoorId() == doorId)
				return door;

			door = null;
		}
		return null;
	}

	public final List<L2DoorInstance> getDoors()
	{
		return _doors;
	}

	public final FortSiege getSiege()
	{
		if(_siege == null)
		{
			_siege = new FortSiege(new Fort[]
			{
				this
			});
		}

		return _siege;
	}

	public final Calendar getSiegeDate()
	{
		return _siegeDate;
	}

	public final void setSiegeDate(Calendar siegeDate)
	{
		_siegeDate = siegeDate;
	}

	public final int getSiegeDayOfWeek()
	{
		return _siegeDayOfWeek;
	}

	public final int getSiegeHourOfDay()
	{
		return _siegeHourOfDay;
	}

	public final String getName()
	{
		return _name;
	}

	public void updateClansReputation()
	{
		if(_formerOwner != null)
		{
			if(_formerOwner != ClanTable.getInstance().getClan(getOwnerId()))
			{
				int maxreward = Math.max(0, _formerOwner.getReputationScore());

				L2Clan owner = ClanTable.getInstance().getClan(getOwnerId());

				if(owner != null)
				{
					owner.setReputationScore(owner.getReputationScore() + Math.min(500, maxreward), true);
					owner.broadcastToOnlineMembers(new PledgeShowInfoUpdate(owner));
				}

				owner = null;
			}
			else
			{
				_formerOwner.setReputationScore(_formerOwner.getReputationScore() + 250, true);
			}

			_formerOwner.broadcastToOnlineMembers(new PledgeShowInfoUpdate(_formerOwner));
		}
		else
		{
			L2Clan owner = ClanTable.getInstance().getClan(getOwnerId());
			if(owner != null)
			{
				owner.setReputationScore(owner.getReputationScore() + 500, true);
				owner.broadcastToOnlineMembers(new PledgeShowInfoUpdate(owner));
			}

			owner = null;
		}
	}

	private class endFortressSiege implements Runnable
	{
		private Fort _f;
		private L2Clan _clan;

		public endFortressSiege(Fort f, L2Clan clan)
		{
			_f = f;
			_clan = clan;
		}

		@Override
		public void run()
		{
			_f.Engrave(_clan, 0);
		}

	}
}
