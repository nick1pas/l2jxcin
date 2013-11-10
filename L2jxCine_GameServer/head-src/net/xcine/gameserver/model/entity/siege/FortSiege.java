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
import net.xcine.Config;
import net.xcine.crypt.nProtect;
import net.xcine.crypt.nProtect.RestrictionType;
import net.xcine.gameserver.datatables.csv.MapRegionTable;
import net.xcine.gameserver.datatables.sql.ClanTable;
import net.xcine.gameserver.datatables.sql.NpcTable;
import net.xcine.gameserver.idfactory.IdFactory;
import net.xcine.gameserver.managers.FortSiegeGuardManager;
import net.xcine.gameserver.managers.FortSiegeManager;
import net.xcine.gameserver.managers.FortSiegeManager.SiegeSpawn;
import net.xcine.gameserver.managers.MercTicketManager;
import net.xcine.gameserver.model.L2Character;
import net.xcine.gameserver.model.L2Clan;
import net.xcine.gameserver.model.L2Object;
import net.xcine.gameserver.model.L2SiegeClan;
import net.xcine.gameserver.model.L2SiegeClan.SiegeClanType;
import net.xcine.gameserver.model.L2World;
import net.xcine.gameserver.model.actor.instance.L2ArtefactInstance;
import net.xcine.gameserver.model.actor.instance.L2CommanderInstance;
import net.xcine.gameserver.model.actor.instance.L2NpcInstance;
import net.xcine.gameserver.model.actor.instance.L2PcInstance;
import net.xcine.gameserver.model.entity.Announcements;
import net.xcine.gameserver.network.SystemMessageId;
import net.xcine.gameserver.network.serverpackets.FortressSiegeInfo;
import net.xcine.gameserver.network.serverpackets.RelationChanged;
import net.xcine.gameserver.network.serverpackets.SystemMessage;
import net.xcine.gameserver.network.serverpackets.UserInfo;
import net.xcine.gameserver.thread.ThreadPoolManager;
import net.xcine.util.database.L2DatabaseFactory;

/**
 * The Class FortSiege.
 *
 * @author programmos
 */
public class FortSiege
{
	protected static final Logger _log = Logger.getLogger(FortSiege.class.getName());
	public static enum TeleportWhoType
	{
		All,
		Attacker,
		DefenderNotOwner,
		Owner,
		Spectator
	}

	public class ScheduleEndSiegeTask implements Runnable
	{
		private Fort _fortInst;

		/**
		 * @param pFort the fort
		 */
		public ScheduleEndSiegeTask(Fort pFort)
		{
			_fortInst = pFort;
		}

		@Override
		public void run()
		{
			if(!getIsInProgress())
				return;

			try
			{
				long timeRemaining = _siegeEndDate.getTimeInMillis() - Calendar.getInstance().getTimeInMillis();

				if(timeRemaining > 3600000)
				{
					ThreadPoolManager.getInstance().scheduleGeneral(new ScheduleEndSiegeTask(_fortInst), timeRemaining - 3600000); // Prepare task for 1 hr left.
				}
				else if(timeRemaining <= 3600000 && timeRemaining > 600000)
				{
					announceToPlayer((timeRemaining / 60000) + " minute(s) until " + getFort().getName() + " siege conclusion.", true);
					ThreadPoolManager.getInstance().scheduleGeneral(new ScheduleEndSiegeTask(_fortInst), timeRemaining - 600000); // Prepare task for 10 minute left.
				}
				else if(timeRemaining <= 600000 && timeRemaining > 300000)
				{
					announceToPlayer((timeRemaining / 60000) + " minute(s) until " + getFort().getName() + " siege conclusion.", true);

					ThreadPoolManager.getInstance().scheduleGeneral(new ScheduleEndSiegeTask(_fortInst), timeRemaining - 300000);
				}
				else if(timeRemaining <= 300000 && timeRemaining > 10000)
				{
					announceToPlayer((timeRemaining / 60000) + " minute(s) until " + getFort().getName() + " siege conclusion.", true);

					ThreadPoolManager.getInstance().scheduleGeneral(new ScheduleEndSiegeTask(_fortInst), timeRemaining - 10000);
				}
				else if(timeRemaining <= 10000 && timeRemaining > 0)
				{
					announceToPlayer(getFort().getName() + " siege " + (timeRemaining / 1000) + " second(s) left!", true);

					ThreadPoolManager.getInstance().scheduleGeneral(new ScheduleEndSiegeTask(_fortInst), timeRemaining);
				}
				else
				{
					_fortInst.getSiege().endSiege();
				}
			}
			catch(Throwable t)
			{
				if(Config.ENABLE_ALL_EXCEPTIONS)
					t.printStackTrace();
			}
		}
	}

	public class ScheduleStartSiegeTask implements Runnable
	{
		private Fort _fortInst;

		/**
		 * @param pFort the fort
		 */
		public ScheduleStartSiegeTask(Fort pFort)
		{
			_fortInst = pFort;
		}
		
		@Override
		public void run()
		{
			if(getIsInProgress())
				return;

			try
			{
				long timeRemaining = getSiegeDate().getTimeInMillis() - Calendar.getInstance().getTimeInMillis();
				if(timeRemaining > 86400000)
				{
					ThreadPoolManager.getInstance().scheduleGeneral(new ScheduleStartSiegeTask(_fortInst), timeRemaining - 86400000);
				}
				else if(timeRemaining <= 86400000 && timeRemaining > 13600000)
				{
					ThreadPoolManager.getInstance().scheduleGeneral(new ScheduleStartSiegeTask(_fortInst), timeRemaining - 13600000);
				}
				else if(timeRemaining <= 13600000 && timeRemaining > 600000)
				{
					announceToPlayer((timeRemaining / 60000) + " minute(s) until " + getFort().getName() + " siege begin.", false);

					ThreadPoolManager.getInstance().scheduleGeneral(new ScheduleStartSiegeTask(_fortInst), timeRemaining - 600000);
				}
				else if(timeRemaining <= 600000 && timeRemaining > 300000)
				{
					announceToPlayer("The registration term for " + getFort().getName() + " has ended.", false);

					_isRegistrationOver = true;

					clearSiegeWaitingClan();

					ThreadPoolManager.getInstance().scheduleGeneral(new ScheduleStartSiegeTask(_fortInst), timeRemaining - 300000);
				}
				else if(timeRemaining <= 300000 && timeRemaining > 10000)
				{
					announceToPlayer((timeRemaining / 60000) + " minute(s) until " + getFort().getName() + " siege begin.", false);

					ThreadPoolManager.getInstance().scheduleGeneral(new ScheduleStartSiegeTask(_fortInst), timeRemaining - 10000);
				}
				else if(timeRemaining <= 10000 && timeRemaining > 0)
				{
					announceToPlayer(getFort().getName() + " siege " + (timeRemaining / 1000) + " second(s) to start!", false);

					ThreadPoolManager.getInstance().scheduleGeneral(new ScheduleStartSiegeTask(_fortInst), timeRemaining);
				}
				else
				{
					_fortInst.getSiege().startSiege();
				}
			}
			catch(Throwable t)
			{
				if(Config.ENABLE_ALL_EXCEPTIONS)
					t.printStackTrace();
			}
		}
	}
	
	private List<L2SiegeClan> _attackerClans = new FastList<>(); // L2SiegeClan
	private List<L2SiegeClan> _defenderClans = new FastList<>(); // L2SiegeClan
	private List<L2SiegeClan> _defenderWaitingClans = new FastList<>(); // L2SiegeClan
	private int _defenderRespawnDelayPenalty;
	private List<L2CommanderInstance> _commanders = new FastList<>();
	private List<L2ArtefactInstance> _combatflag = new FastList<>();
	private Fort[] _fort;
	private boolean _isInProgress = false;
	private boolean _isScheduled = false;
	private boolean _isNormalSide = true; // true = Atk is Atk, false = Atk is Def
	protected boolean _isRegistrationOver = false;
	protected Calendar _siegeEndDate;
	private FortSiegeGuardManager _siegeGuardManager;
	protected Calendar _siegeRegistrationEndDate;

	/**
	 * @param fort the fort
	 */
	public FortSiege(Fort[] fort)
	{
		_fort = fort;
		checkAutoTask();
	}

	public void endSiege()
	{
		if(getIsInProgress())
		{
			announceToPlayer("The siege of " + getFort().getName() + " has finished!", false);

			if(getFort().getOwnerId() <= 0)
			{
				announceToPlayer("The siege of " + getFort().getName() + " has ended in a draw.", false);
			}

			removeFlags();
			unSpawnFlags();

			teleportPlayer(FortSiege.TeleportWhoType.Attacker, MapRegionTable.TeleportWhereType.Town);

			teleportPlayer(FortSiege.TeleportWhoType.DefenderNotOwner, MapRegionTable.TeleportWhereType.Town);

			teleportPlayer(FortSiege.TeleportWhoType.Spectator, MapRegionTable.TeleportWhereType.Town);

			_isInProgress = false;

			updatePlayerSiegeStateFlags(true);

			saveFortSiege();

			clearSiegeClan();

			removeCommander();

			_siegeGuardManager.unspawnSiegeGuard();

			if(getFort().getOwnerId() > 0)
			{
				_siegeGuardManager.removeMercs();
			}

			getFort().spawnDoor();
			getFort().getZone().updateZoneStatusForCharactersInside();
		}
	}

	/**
	 * @param sc the sc
	 */
	private void removeDefender(L2SiegeClan sc)
	{
		if(sc != null)
		{
			getDefenderClans().remove(sc);
		}
	}

	/**
	 * @param sc the sc
	 */
	private void removeAttacker(L2SiegeClan sc)
	{
		if(sc != null)
		{
			getAttackerClans().remove(sc);
		}
	}

	/**
	 * @param sc the sc
	 * @param type the type
	 */
	private void addDefender(L2SiegeClan sc, SiegeClanType type)
	{
		if(sc == null)
			return;

		sc.setType(type);
		getDefenderClans().add(sc);
	}

	/**
	 * @param sc the sc
	 */
	private void addAttacker(L2SiegeClan sc)
	{
		if(sc == null)
			return;

		sc.setType(SiegeClanType.ATTACKER);
		getAttackerClans().add(sc);
	}

	public void midVictory()
	{
		if(getIsInProgress()) 
		{
			for(L2SiegeClan sc : getDefenderClans())
			{
				if(sc != null)
				{
					removeDefender(sc);
					addAttacker(sc);
				}
			}

			L2SiegeClan sc_newowner = getAttackerClan(getFort().getOwnerId());
			removeAttacker(sc_newowner);
			addDefender(sc_newowner, SiegeClanType.OWNER);
			endSiege();
			sc_newowner = null;

			return;
		}
	}

	public void startSiege()
	{
		if(!getIsInProgress())
		{
			if(getAttackerClans().size() <= 0)
			{
				SystemMessage sm;

				if(getFort().getOwnerId() <= 0)
				{
					sm = new SystemMessage(SystemMessageId.SIEGE_OF_S1_HAS_BEEN_CANCELED_DUE_TO_LACK_OF_INTEREST);
				}
				else
				{
					sm = new SystemMessage(SystemMessageId.S1_SIEGE_WAS_CANCELED_BECAUSE_NO_CLANS_PARTICIPATED);
				}

				sm.addString(getFort().getName());
				Announcements.getInstance().announceToAll(sm);
				sm = null;

				return;
			}

			_isNormalSide = true; 
			_isInProgress = true; 
			_isScheduled = false;

			loadSiegeClan();
			updatePlayerSiegeStateFlags(false);

			teleportPlayer(FortSiege.TeleportWhoType.Attacker, MapRegionTable.TeleportWhereType.Town);

			spawnCommander(getFort().getFortId());

			getFort().spawnDoor();

			spawnSiegeGuard();

			MercTicketManager.getInstance().deleteTickets(getFort().getFortId());

			_defenderRespawnDelayPenalty = 0;

			getFort().getZone().updateZoneStatusForCharactersInside();

			_siegeEndDate = Calendar.getInstance();
			_siegeEndDate.add(Calendar.MINUTE, FortSiegeManager.getInstance().getSiegeLength());
			nProtect.getInstance().checkRestriction(null, RestrictionType.RESTRICT_EVENT, new Object[]
			{
					FortSiege.class, this
			});
			ThreadPoolManager.getInstance().scheduleGeneral(new ScheduleEndSiegeTask(getFort()), 1000); // Prepare auto end task

			announceToPlayer("The siege of " + getFort().getName() + " has started!", false);
			saveFortSiege();
			FortSiegeManager.getInstance().addSiege(this);

		}
	}
	/**
	 * @param message The String of the message to send to player
	 * @param inAreaOnly The boolean flag to show message to players in area only.
	 */
	public void announceToPlayer(String message, boolean inAreaOnly)
	{
		if(inAreaOnly)
		{
			getFort().getZone().announceToPlayers(message);
			return;
		}

		// Get all players
		for(L2PcInstance player : L2World.getInstance().getAllPlayers())
		{
			player.sendMessage(message);
		}
	}

	/**
	 * @param clear the clear
	 */
	public void updatePlayerSiegeStateFlags(boolean clear)
	{
		L2Clan clan;
		for(L2SiegeClan siegeclan : getAttackerClans())
		{
			clan = ClanTable.getInstance().getClan(siegeclan.getClanId());
			for(L2PcInstance member : clan.getOnlineMembers(""))
			{
				if(clear)
				{
					member.setSiegeState((byte) 0);
				}
				else
				{
					member.setSiegeState((byte) 1);
				}

				member.sendPacket(new UserInfo(member));

				for(L2PcInstance player : member.getKnownList().getKnownPlayers().values())
				{
					player.sendPacket(new RelationChanged(member, member.getRelation(player), member.isAutoAttackable(player)));
				}
			}
		}

		for(L2SiegeClan siegeclan : getDefenderClans())
		{
			clan = ClanTable.getInstance().getClan(siegeclan.getClanId());
			for(L2PcInstance member : clan.getOnlineMembers(""))
			{
				if(clear)
				{
					member.setSiegeState((byte) 0);
				}
				else
				{
					member.setSiegeState((byte) 2);
				}

				member.sendPacket(new UserInfo(member));

				for(L2PcInstance player : member.getKnownList().getKnownPlayers().values())
				{
					player.sendPacket(new RelationChanged(member, member.getRelation(player), member.isAutoAttackable(player)));
				}
			}
		}

		clan = null;
	}

	/**
	 * @param clanId The int of player's clan id
	 */
	public void approveSiegeDefenderClan(int clanId)
	{
		if(clanId <= 0)
			return;

		saveSiegeClan(ClanTable.getInstance().getClan(clanId), 0, true);
		loadSiegeClan();
	}

	/**
	 * @param object the object
	 * @return true if object is inside the zone
	 */
	public boolean checkIfInZone(L2Object object)
	{
		return checkIfInZone(object.getX(), object.getY(), object.getZ());
	}

	/**
	 * @param x the x
	 * @param y the y
	 * @param z the z
	 * @return true if object is inside the zone
	 */
	public boolean checkIfInZone(int x, int y, int z)
	{
		return getIsInProgress() && getFort().checkIfInZone(x, y, z); // Fort zone during siege
	}

	/**
	 * @param clan The L2Clan of the player
	 * @return true if clan is attacker
	 */
	public boolean checkIsAttacker(L2Clan clan)
	{
		return getAttackerClan(clan) != null;
	}

	/**
	 * @param clan The L2Clan of the player
	 * @return true if clan is defender
	 */
	public boolean checkIsDefender(L2Clan clan)
	{
		return getDefenderClan(clan) != null;
	}

	/**
	 * @param clan The L2Clan of the player
	 * @return true if clan is defender waiting approval
	 */
	public boolean checkIsDefenderWaiting(L2Clan clan)
	{
		return getDefenderWaitingClan(clan) != null;
	}

	public void clearSiegeClan()
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement("DELETE FROM fortsiege_clans WHERE fort_id=?");
			statement.setInt(1, getFort().getFortId());
			statement.execute();
			statement.close();
			statement = null;

			if(getFort().getOwnerId() > 0)
			{
				PreparedStatement statement2 = con.prepareStatement("DELETE FROM fortsiege_clans WHERE clan_id=?");
				statement2.setInt(1, getFort().getOwnerId());
				statement2.execute();
				statement2.close();
				statement2 = null;
			}

			getAttackerClans().clear();
			getDefenderClans().clear();
			getDefenderWaitingClans().clear();
		}
		catch(Exception e)
		{
			_log.warning("Exception: clearSiegeClan(): " + e.getMessage());
			e.printStackTrace();
		}
	}

	private void clearSiegeDate()
	{
		getFort().getSiegeDate().setTimeInMillis(0);
		_isRegistrationOver = false; // Allow registration for next siege
	}

	public void clearSiegeWaitingClan()
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement("DELETE FROM fortsiege_clans WHERE fort_id=? and type = 2");
			statement.setInt(1, getFort().getFortId());
			statement.execute();
			statement.close();
			statement = null;

			getDefenderWaitingClans().clear();
		}
		catch(Exception e)
		{
			_log.warning("Exception: clearSiegeWaitingClan(): " + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * @return list of L2PcInstance registered as attacker in the zone.
	 */
	public List<L2PcInstance> getAttackersInZone()
	{
		List<L2PcInstance> players = new FastList<>();
		L2Clan clan;

		for(L2SiegeClan siegeclan : getAttackerClans())
		{
			clan = ClanTable.getInstance().getClan(siegeclan.getClanId());

			for(L2PcInstance player : clan.getOnlineMembers(""))
			{
				if(checkIfInZone(player.getX(), player.getY(), player.getZ()))
				{
					players.add(player);
				}
			}
		}

		clan = null;

		return players;
	}

	/**
	 * @return the defenders but not owners in zone
	 */
	public List<L2PcInstance> getDefendersButNotOwnersInZone()
	{
		List<L2PcInstance> players = new FastList<>();
		L2Clan clan;

		for(L2SiegeClan siegeclan : getDefenderClans())
		{
			clan = ClanTable.getInstance().getClan(siegeclan.getClanId());

			if(clan.getClanId() == getFort().getOwnerId())
			{
				continue;
			}

			for(L2PcInstance player : clan.getOnlineMembers(""))
			{
				if(checkIfInZone(player.getX(), player.getY(), player.getZ()))
				{
					players.add(player);
				}
			}
		}

		clan = null;

		return players;
	}

	/**
	 * @return the players in zone
	 */
	public List<L2PcInstance> getPlayersInZone()
	{
		return getFort().getZone().getAllPlayers();
	}

	/**
	 * @return the owners in zone
	 */
	public List<L2PcInstance> getOwnersInZone()
	{
		List<L2PcInstance> players = new FastList<>();
		L2Clan clan;

		for(L2SiegeClan siegeclan : getDefenderClans())
		{
			clan = ClanTable.getInstance().getClan(siegeclan.getClanId());

			if(clan.getClanId() != getFort().getOwnerId())
			{
				continue;
			}

			for(L2PcInstance player : clan.getOnlineMembers(""))
			{
				if(checkIfInZone(player.getX(), player.getY(), player.getZ()))
				{
					players.add(player);
				}
			}
		}

		clan = null;

		return players;
	}

	/**
	 * @return the spectators in zone
	 */
	public List<L2PcInstance> getSpectatorsInZone()
	{
		List<L2PcInstance> players = new FastList<>();

		for(L2PcInstance player : L2World.getInstance().getAllPlayers())
		{
			// quick check from player states, which don't include siege number however
			if(!player.isInsideZone(L2Character.ZONE_SIEGE) || player.getSiegeState() != 0)
			{
				continue;
			}

			if(checkIfInZone(player.getX(), player.getY(), player.getZ()))
			{
				players.add(player);
			}
		}

		return players;
	}

	/**
	 * @param ct the ct
	 */
	public void killedCT(L2NpcInstance ct)
	{
		_defenderRespawnDelayPenalty += FortSiegeManager.getInstance().getControlTowerLosePenalty(); // Add respawn penalty to defenders for each control tower lose
	}

	/**
	 * @param ct the ct
	 */
	public void killedCommander(L2CommanderInstance ct)
	{
		if(_commanders != null)
		{
			_commanders.remove(ct);

			if(_commanders.size() == 0)
			{
				spawnFlag(getFort().getFortId());
				//System.out.println("Commander empty !");
			}
		}

	}

	/**
	 * @param flag the flag
	 */
	public void killedFlag(L2NpcInstance flag)
	{
		if(flag == null)
			return;

		for(int i = 0; i < getAttackerClans().size(); i++)
		{
			if(getAttackerClan(i).removeFlag(flag))
				return;
		}
	}

	/**
	 * @param player the player
	 */
	public void listRegisterClan(L2PcInstance player)
	{
		player.sendPacket(new FortressSiegeInfo(getFort()));
	}

	/**
	 * @param player The L2PcInstance of the player trying to register
	 */
	public void registerAttacker(L2PcInstance player)
	{
		registerAttacker(player, false);
	}

	/**
	 * @param player the player
	 * @param force the force
	 */
	public void registerAttacker(L2PcInstance player, boolean force)
	{

		if(player.getClan() == null)
			return;

		int allyId = 0;

		if(getFort().getOwnerId() != 0)
		{
			allyId = ClanTable.getInstance().getClan(getFort().getOwnerId()).getAllyId();
		}

		if(allyId != 0)
		{
			if(player.getClan().getAllyId() == allyId && !force)
			{
				player.sendMessage("You cannot register as an attacker because your alliance owns the fort");
				return;
			}
		}

		if(player.getInventory().getItemByItemId(57) != null && player.getInventory().getItemByItemId(57).getCount() < 250000)
		{
			player.sendMessage("You do not have enough adena.");
			return;
		}

		if(force || checkIfCanRegister(player))
		{
			player.getInventory().destroyItemByItemId("Siege", 57, 250000, player, player.getTarget());
			player.getInventory().updateDatabase();

			saveSiegeClan(player.getClan(), 1, false); // Save to database

			// if the first registering we start the timer
			if(getAttackerClans().size() == 1)
			{
				startAutoTask(true);
			}
		}
	}

	/**
	 * @param player The L2PcInstance of the player trying to register
	 */
	public void registerDefender(L2PcInstance player)
	{
		registerDefender(player, false);
	}

	/**
	 * @param player the player
	 * @param force the force
	 */
	public void registerDefender(L2PcInstance player, boolean force)
	{
		if(getFort().getOwnerId() <= 0)
		{
			player.sendMessage("You cannot register as a defender because " + getFort().getName() + " is owned by NPC.");
		}
		else if(force || checkIfCanRegister(player))
		{
			saveSiegeClan(player.getClan(), 2, false); // Save to database
		}
	}

	/**
	 * @param clanId The int of player's clan id
	 */
	public void removeSiegeClan(int clanId)
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement;

			if(clanId != 0)
			{
				statement = con.prepareStatement("DELETE FROM fortsiege_clans WHERE fort_id=? and clan_id=?");
			}
			else
			{
				statement = con.prepareStatement("DELETE FROM fortsiege_clans WHERE fort_id=?");
			}

			statement.setInt(1, getFort().getFortId());

			if(clanId != 0)
			{
				statement.setInt(2, clanId);
			}

			statement.execute();
			statement.close();
			statement = null;

			loadSiegeClan();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * @param clan the clan
	 */
	public void removeSiegeClan(L2Clan clan)
	{
		if(clan == null || clan.getHasFort() == getFort().getFortId() || !FortSiegeManager.getInstance().checkIsRegistered(clan, getFort().getFortId()))
			return;

		removeSiegeClan(clan.getClanId());
	}

	/**
	 * @param player The L2PcInstance of player/clan being removed
	 */
	public void removeSiegeClan(L2PcInstance player)
	{
		removeSiegeClan(player.getClan());
	}

	public void checkAutoTask()
	{
		if(getFort().getSiegeDate().getTimeInMillis() < Calendar.getInstance().getTimeInMillis())
		{
			clearSiegeDate();
			saveSiegeDate();
			removeSiegeClan(0); // remove all clans
			return;
		}

		startAutoTask(false);
	}

	/**
	 * @param setTime the set time
	 */
	public void startAutoTask(boolean setTime)
	{
		if(setTime)
		{
			setSiegeDateTime();
		}

		System.out.println("Siege of " + getFort().getName() + ": " + getFort().getSiegeDate().getTime());
		setIsScheduled(true);
		loadSiegeClan();

		_siegeRegistrationEndDate = Calendar.getInstance();
		_siegeRegistrationEndDate.setTimeInMillis(getFort().getSiegeDate().getTimeInMillis());
		_siegeRegistrationEndDate.add(Calendar.MINUTE, -10);

		ThreadPoolManager.getInstance().scheduleGeneral(new FortSiege.ScheduleStartSiegeTask(getFort()), 1000);
	}

	/**
	 * @param teleportWho the teleport who
	 * @param teleportWhere the teleport where
	 */
	public void teleportPlayer(TeleportWhoType teleportWho, MapRegionTable.TeleportWhereType teleportWhere)
	{
		List<L2PcInstance> players;
		switch(teleportWho)
		{
			case Owner:
				players = getOwnersInZone();
				break;
			case Attacker:
				players = getAttackersInZone();
				break;
			case DefenderNotOwner:
				players = getDefendersButNotOwnersInZone();
				break;
			case Spectator:
				players = getSpectatorsInZone();
				break;
			default:
				players = getPlayersInZone();
		}

		for(L2PcInstance player : players)
		{
			if(player.isGM() || player.isInJail())
			{
				continue;
			}

			player.teleToLocation(teleportWhere);
		}

		players = null;
	}

	/**
	 * @param clanId The int of clan's id
	 */
	private void addAttacker(int clanId)
	{
		getAttackerClans().add(new L2SiegeClan(clanId, SiegeClanType.ATTACKER)); // Add registered attacker to attacker list
	}

	/**
	 * @param clanId The int of clan's id
	 */
	private void addDefender(int clanId)
	{
		getDefenderClans().add(new L2SiegeClan(clanId, SiegeClanType.DEFENDER)); // Add registered defender to defender list
	}

	/**
	 * @param clanId The int of clan's id
	 * @param type the type of the clan
	 */
	private void addDefender(int clanId, SiegeClanType type)
	{
		getDefenderClans().add(new L2SiegeClan(clanId, type));
	}

	/**
	 * @param clanId The int of clan's id
	 */
	private void addDefenderWaiting(int clanId)
	{
		getDefenderWaitingClans().add(new L2SiegeClan(clanId, SiegeClanType.DEFENDER_PENDING)); // Add registered defender to defender list
	}

	/**
	 * @param player The L2PcInstance of the player trying to register
	 * @return true, if successful
	 */
	private boolean checkIfCanRegister(L2PcInstance player)
	{
		if(getIsRegistrationOver())
		{
			player.sendMessage("The deadline to register for the siege of " + getFort().getName() + " has passed.");
		}
		else if(getIsInProgress())
		{
			player.sendMessage("This is not the time for siege registration and so registration and cancellation cannot be done.");
		}
		else if(player.getClan() == null || player.getClan().getLevel() < FortSiegeManager.getInstance().getSiegeClanMinLevel())
		{
			player.sendMessage("Only clans with Level " + FortSiegeManager.getInstance().getSiegeClanMinLevel() + " and higher may register for a fort siege.");
		}
		else if(player.getClan().getHasFort() > 0)
		{
			player.sendMessage("You cannot register because your clan already own a fort.");
		}
		else if(player.getClan().getHasCastle() > 0)
		{
			player.sendMessage("You cannot register because your clan already own a castle.");
		}
		else if(player.getClan().getClanId() == getFort().getOwnerId())
		{
			player.sendPacket(new SystemMessage(SystemMessageId.CLAN_THAT_OWNS_CASTLE_IS_AUTOMATICALLY_REGISTERED_DEFENDING));
		}
		else if(FortSiegeManager.getInstance().checkIsRegistered(player.getClan(), getFort().getFortId()))
		{
			player.sendMessage("You are already registered in a Siege.");
		}
		else
			return true;

		return false;
	}

	private void setSiegeDateTime()
	{
		Calendar newDate = Calendar.getInstance();
		newDate.add(Calendar.MINUTE, 60);
		getFort().setSiegeDate(newDate);
		saveSiegeDate();
		newDate = null;
	}

	private void loadSiegeClan()
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			getAttackerClans().clear();
			getDefenderClans().clear();
			getDefenderWaitingClans().clear();

			if(getFort().getOwnerId() > 0)
			{
				addDefender(getFort().getOwnerId(), SiegeClanType.OWNER);
			}

			PreparedStatement statement = null;
			ResultSet rs = null;

			statement = con.prepareStatement("SELECT clan_id,type FROM fortsiege_clans where fort_id=?");
			statement.setInt(1, getFort().getFortId());
			rs = statement.executeQuery();

			int typeId;

			while(rs.next())
			{
				typeId = rs.getInt("type");

				if(typeId == 0)
				{
					addDefender(rs.getInt("clan_id"));
				}
				else if(typeId == 1)
				{
					addAttacker(rs.getInt("clan_id"));
				}
				else if(typeId == 2)
				{
					addDefenderWaiting(rs.getInt("clan_id"));
				}
			}

			rs.close();
			statement.close();
			statement = null;
			rs = null;
		}
		catch(Exception e)
		{
			_log.warning("Exception: loadSiegeClan(): " + e.getMessage());
			e.printStackTrace();
		}
	}

	private void removeCommander()
	{
		if(_commanders != null)
		{
			for(L2CommanderInstance commander : _commanders)
			{
				if(commander != null)
				{
					commander.decayMe();
				}
			}
			_commanders = null;
		}
	}

	private void removeFlags()
	{
		for(L2SiegeClan sc : getAttackerClans())
		{
			if(sc != null)
			{
				sc.removeFlags();
			}
		}
		for(L2SiegeClan sc : getDefenderClans())
		{
			if(sc != null)
			{
				sc.removeFlags();
			}
		}
	}

	private void saveFortSiege()
	{
		clearSiegeDate();
		saveSiegeDate(); 
		setIsScheduled(false);
	}

	private void saveSiegeDate()
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement("Update fort set siegeDate = ? where id = ?");
			statement.setLong(1, getSiegeDate().getTimeInMillis());
			statement.setInt(2, getFort().getFortId());
			statement.execute();

			statement.close();
			statement = null;
		}
		catch(Exception e)
		{
			_log.warning("Exception: saveSiegeDate(): " + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * @param clan The L2Clan of player
	 * @param typeId -1 = owner 0 = defender, 1 = attacker, 2 = defender waiting
	 * @param isUpdateRegistration the is update registration
	 */
	private void saveSiegeClan(L2Clan clan, int typeId, boolean isUpdateRegistration)
	{
		if(clan.getHasFort() > 0)
			return;

		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			if(typeId == 0 || typeId == 2 || typeId == -1)
			{
				if(getDefenderClans().size() + getDefenderWaitingClans().size() >= FortSiegeManager.getInstance().getDefenderMaxClans())
					return;
			}
			else
			{
				if(getAttackerClans().size() >= FortSiegeManager.getInstance().getAttackerMaxClans())
					return;
			}

			PreparedStatement statement;
			if(!isUpdateRegistration)
			{
				statement = con.prepareStatement("INSERT INTO fortsiege_clans (clan_id,fort_id,type,fort_owner) values (?,?,?,0)");
				statement.setInt(1, clan.getClanId());
				statement.setInt(2, getFort().getFortId());
				statement.setInt(3, typeId);
				statement.execute();
				statement.close();
				statement = null;
			}
			else
			{
				statement = con.prepareStatement("Update fortsiege_clans set type = ? where fort_id = ? and clan_id = ?");
				statement.setInt(1, typeId);
				statement.setInt(2, getFort().getFortId());
				statement.setInt(3, clan.getClanId());
				statement.execute();
				statement.close();
				statement = null;
			}

			if(typeId == 0 || typeId == -1)
			{
				addDefender(clan.getClanId());
				announceToPlayer(clan.getName() + " has been registered to defend " + getFort().getName(), false);
			}
			else if(typeId == 1)
			{
				addAttacker(clan.getClanId());
				announceToPlayer(clan.getName() + " has been registered to attack " + getFort().getName(), false);
			}
			else if(typeId == 2)
			{
				addDefenderWaiting(clan.getClanId());
				announceToPlayer(clan.getName() + " has requested to defend " + getFort().getName(), false);
			}
		}
		catch(Exception e)
		{
			_log.warning("Exception: saveSiegeClan(L2Clan clan, int typeId, boolean isUpdateRegistration): " + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * @param Id the id
	 */
	private void spawnCommander(int Id)
	{
		//Set commanders array size if one does not exist
		if(_commanders == null)
		{
			_commanders = new FastList<>();
		}

		for(SiegeSpawn _sp : FortSiegeManager.getInstance().getCommanderSpawnList(Id))
		{
			L2CommanderInstance commander;

			commander = new L2CommanderInstance(IdFactory.getInstance().getNextId(), NpcTable.getInstance().getTemplate(_sp.getNpcId()));
			commander.setCurrentHpMp(commander.getMaxHp(), commander.getMaxMp());
			commander.setHeading(_sp.getLocation().getHeading());
			commander.spawnMe(_sp.getLocation().getX(), _sp.getLocation().getY(), _sp.getLocation().getZ() + 50);

			_commanders.add(commander);
			commander = null;
		}
	}

	/**
	 * Spawn flag.
	 *
	 * @param Id the id
	 */
	private void spawnFlag(int Id)
	{
		if(_combatflag == null)
		{
			_combatflag = new FastList<>();
		}

		for(SiegeSpawn _sp : FortSiegeManager.getInstance().getFlagList(Id))
		{
			L2ArtefactInstance combatflag;

			combatflag = new L2ArtefactInstance(IdFactory.getInstance().getNextId(), NpcTable.getInstance().getTemplate(_sp.getNpcId()));
			combatflag.setCurrentHpMp(combatflag.getMaxHp(), combatflag.getMaxMp());
			combatflag.setHeading(_sp.getLocation().getHeading());
			combatflag.spawnMe(_sp.getLocation().getX(), _sp.getLocation().getY(), _sp.getLocation().getZ() + 10);

			_combatflag.add(combatflag);
			combatflag = null;
		}

	}

	/**
	 * Un spawn flags.
	 */
	private void unSpawnFlags()
	{

		if(_combatflag != null)
		{
			for(L2ArtefactInstance _sp : _combatflag)
			{
				if(_sp != null)
				{
					_sp.decayMe();
				}
			}
			_combatflag = null;
		}

	}

	private void spawnSiegeGuard()
	{
		getSiegeGuardManager().spawnSiegeGuard();
	}

	/**
	 * @param clan the clan
	 * @return the attacker clan
	 */
	public final L2SiegeClan getAttackerClan(L2Clan clan)
	{
		if(clan == null)
			return null;

		return getAttackerClan(clan.getClanId());
	}

	/**
	 * @param clanId the clan id
	 * @return the attacker clan
	 */
	public final L2SiegeClan getAttackerClan(int clanId)
	{
		for(L2SiegeClan sc : getAttackerClans())
			if(sc != null && sc.getClanId() == clanId)
				return sc;

		return null;
	}

	/**
	 * @return the attacker clans
	 */
	public final List<L2SiegeClan> getAttackerClans()
	{
		if(_isNormalSide)
			return _attackerClans;

		return _defenderClans;
	}

	/**
	 * @return the attacker respawn delay
	 */
	public final int getAttackerRespawnDelay()
	{
		return FortSiegeManager.getInstance().getAttackerRespawnDelay();
	}

	/**
	 * @return the fort
	 */
	public final Fort getFort()
	{
		if(_fort == null || _fort.length <= 0)
			return null;

		return _fort[0];
	}

	/**
	 * @param clan the clan
	 * @return the defender clan
	 */
	public final L2SiegeClan getDefenderClan(L2Clan clan)
	{
		if(clan == null)
			return null;

		return getDefenderClan(clan.getClanId());
	}

	/**
	 * @param clanId the clan id
	 * @return the defender clan
	 */
	public final L2SiegeClan getDefenderClan(int clanId)
	{
		for(L2SiegeClan sc : getDefenderClans())
			if(sc != null && sc.getClanId() == clanId)
				return sc;

		return null;
	}

	/**
	 * @return the defender clans
	 */
	public final List<L2SiegeClan> getDefenderClans()
	{
		if(_isNormalSide)
			return _defenderClans;

		return _attackerClans;
	}

	/**
	 * @param clan the clan
	 * @return the defender waiting clan
	 */
	public final L2SiegeClan getDefenderWaitingClan(L2Clan clan)
	{
		if(clan == null)
			return null;

		return getDefenderWaitingClan(clan.getClanId());
	}

	/**
	 * @param clanId the clan id
	 * @return the defender waiting clan
	 */
	public final L2SiegeClan getDefenderWaitingClan(int clanId)
	{
		for(L2SiegeClan sc : getDefenderWaitingClans())
			if(sc != null && sc.getClanId() == clanId)
				return sc;

		return null;
	}

	/**
	 * @return the defender waiting clans
	 */
	public final List<L2SiegeClan> getDefenderWaitingClans()
	{
		return _defenderWaitingClans;
	}

	/**
	 * @return the defender respawn delay
	 */
	public final int getDefenderRespawnDelay()
	{
		return FortSiegeManager.getInstance().getDefenderRespawnDelay() + _defenderRespawnDelayPenalty;
	}

	/**
	 * @return the checks if is in progress
	 */
	public final boolean getIsInProgress()
	{
		return _isInProgress;
	}

	/**
	 * @return the checks if is scheduled
	 */
	public final boolean getIsScheduled()
	{
		return _isScheduled;
	}

	/**
	 * @param isScheduled the new checks if is scheduled
	 */
	public final void setIsScheduled(boolean isScheduled)
	{
		_isScheduled = isScheduled;
	}

	/**
	 * @return the checks if is registration over
	 */
	public final boolean getIsRegistrationOver()
	{
		return _isRegistrationOver;
	}

	/**
	 * @return the siege date
	 */
	public final Calendar getSiegeDate()
	{
		return getFort().getSiegeDate();
	}

	/**
	 * @param clan the clan
	 * @return the flag
	 */
	public List<L2NpcInstance> getFlag(L2Clan clan)
	{
		if(clan != null)
		{
			L2SiegeClan sc = getAttackerClan(clan);
			if(sc != null)
				return sc.getFlag();
		}

		return null;
	}

	/**
	 * @return the siege guard manager
	 */
	public final FortSiegeGuardManager getSiegeGuardManager()
	{
		if(_siegeGuardManager == null)
		{
			_siegeGuardManager = new FortSiegeGuardManager(getFort());
		}

		return _siegeGuardManager;
	}
}
