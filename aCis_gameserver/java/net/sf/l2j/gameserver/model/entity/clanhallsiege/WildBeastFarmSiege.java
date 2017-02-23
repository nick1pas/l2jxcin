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
package net.sf.l2j.gameserver.model.entity.clanhallsiege;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Logger;

import net.sf.l2j.commons.concurrent.ThreadPool;

import net.sf.l2j.gameserver.datatables.ClanTable;
import net.sf.l2j.gameserver.datatables.DoorTable;
import net.sf.l2j.gameserver.datatables.NpcTable;
import net.sf.l2j.gameserver.idfactory.IdFactory;
import net.sf.l2j.gameserver.instancemanager.ClanHallManager;
import net.sf.l2j.gameserver.model.L2Clan;
import net.sf.l2j.gameserver.model.World;
import net.sf.l2j.gameserver.model.actor.Character;
import net.sf.l2j.gameserver.model.actor.instance.Deco;
import net.sf.l2j.gameserver.model.actor.instance.Monster;
import net.sf.l2j.gameserver.model.actor.instance.Player;
import net.sf.l2j.gameserver.model.actor.template.NpcTemplate;
import net.sf.l2j.gameserver.model.entity.ClanHall;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;
import net.sf.l2j.gameserver.model.zone.type.L2ClanHallZone;
import net.sf.l2j.gameserver.network.serverpackets.CreatureSay;
import net.sf.l2j.gameserver.taskmanager.ExclusiveTask;

public class WildBeastFarmSiege extends ClanHallSiege
{
	private static Logger _log = Logger.getLogger(WildBeastFarmSiege.class.getName());
	
	private boolean _registrationPeriod = false;
	private int _clanCounter = 0;
	Map<Integer, clanPlayersInfo> _clansInfo = new HashMap<>();
	public ClanHall clanhall = ClanHallManager.getInstance().getClanHallById(63);
	clanPlayersInfo _ownerClanInfo = new clanPlayersInfo();
	boolean _finalStage = false;
	ScheduledFuture<?> _midTimer;
	private L2ClanHallZone zone;
	
	public static WildBeastFarmSiege getInstance()
	{
		return SingletonHolder._instance;
	}
	
	private static class SingletonHolder
	{
		protected static final WildBeastFarmSiege _instance = new WildBeastFarmSiege();
	}
	
	protected WildBeastFarmSiege()
	{
		_log.info("Loaded Wild Beasts Farm Siege");
		long siegeDate = restoreSiegeDate(63);
		Calendar tmpDate = Calendar.getInstance();
		tmpDate.setTimeInMillis(siegeDate);
		setSiegeDate(tmpDate);
		setNewSiegeDate(siegeDate, 63, 22);
		_startSiegeTask.schedule(1000);
	}
	
	public void startSiege()
	{
		setRegistrationPeriod(false);
		if (_clansInfo.size() == 0)
		{
			endSiege(false);
			return;
		}
		
		if (_clansInfo.size() == 1 && clanhall.getOwnerClan() == null)
		{
			endSiege(false);
			return;
		}
		
		if (_clansInfo.size() == 1 && clanhall.getOwnerClan() != null)
		{
			L2Clan clan = null;
			for (clanPlayersInfo a : _clansInfo.values())
			{
				clan = ClanTable.getInstance().getClanByName(a._clanName);
			}
			setIsInProgress(true);
			startSecondStep(clan);
			Announce("Take place at the siege of his headquarters.", 1);
			_siegeEndDate = Calendar.getInstance();
			_siegeEndDate.add(Calendar.MINUTE, 30);
			_endSiegeTask.schedule(1000);
			return;
		}
		setIsInProgress(true);
		spawnFlags();
		gateControl(1);
		Announce("Take place at the siege of his headquarters.", 1);
		ThreadPool.schedule(new startFirstStep(), 5 * 60000);
		_midTimer = ThreadPool.schedule(new midSiegeStep(), 25 * 60000);
		_siegeEndDate = Calendar.getInstance();
		_siegeEndDate.add(Calendar.MINUTE, 60);
		_endSiegeTask.schedule(1000);
	}
	
	public void startSecondStep(L2Clan winner)
	{
		List<String> winPlayers = WildBeastFarmSiege.getInstance().getRegisteredPlayers(winner);
		unSpawnAll();
		_clansInfo.clear();
		clanPlayersInfo regPlayers = new clanPlayersInfo();
		regPlayers._clanName = winner.getName();
		regPlayers._players = winPlayers;
		_clansInfo.put(winner.getClanId(), regPlayers);
		_clansInfo.put(clanhall.getOwnerClan().getClanId(), _ownerClanInfo);
		spawnFlags();
		gateControl(1);
		_finalStage = true;
		Announce("Take place at the siege of his headquarters.", 1);
		ThreadPool.schedule(new startFirstStep(), 5 * 60000);
	}
	
	public void endSiege(boolean par)
	{
		_mobControlTask.cancel();
		_finalStage = false;
		if (par)
		{
			L2Clan winner = checkHaveWinner();
			if (winner != null)
			{
				ClanHallManager.getInstance().setOwner(clanhall.getId(), winner);
				Announce("Wild Beast Reserve was conquered by the clan " + winner.getName() + ".", 2);
			}
			else
			{
				Announce("Wild Beast Reserve did not get new owner", 2);
			}
		}
		setIsInProgress(false);
		unSpawnAll();
		_clansInfo.clear();
		_clanCounter = 0;
		teleportPlayers();
		setNewSiegeDate(getSiegeDate().getTimeInMillis(), 63, 22);
		_startSiegeTask.schedule(1000);
	}
	
	public void unSpawnAll()
	{
		for (String clanName : getRegisteredClans())
		{
			L2Clan clan = ClanTable.getInstance().getClanByName(clanName);
			Monster mob = getQuestMob(clan);
			Deco flag = getSiegeFlag(clan);
			if (mob != null)
				mob.deleteMe();
			
			if (flag != null)
				flag.deleteMe();
		}
	}
	
	public void gateControl(int val)
	{
		switch (val)
		{
			case 1:
				DoorTable.getInstance().getDoor(21150003).openMe();
				DoorTable.getInstance().getDoor(21150004).openMe();
				DoorTable.getInstance().getDoor(21150001).closeMe();
				DoorTable.getInstance().getDoor(21150002).closeMe();
				break;
				
			case 2:
				DoorTable.getInstance().getDoor(21150001).closeMe();
				DoorTable.getInstance().getDoor(21150002).closeMe();
				DoorTable.getInstance().getDoor(21150003).closeMe();
				DoorTable.getInstance().getDoor(21150004).closeMe();
				break;
		}
	}
	
	public void teleportPlayers()
	{
		zone = clanhall.getZone();
		for (Character cha : zone.getCharactersInside())
		{
			if (cha instanceof Player)
			{
				L2Clan clan = ((Player) cha).getClan();
				if (!isPlayerRegister(clan, cha.getName()))
				{
					cha.teleToLocation(53468, -94092, -1634, 0);
				}
			}
		}
	}
	
	public L2Clan checkHaveWinner()
	{
		L2Clan res = null;
		int questMobCount = 0;
		for (String clanName : getRegisteredClans())
		{
			L2Clan clan = ClanTable.getInstance().getClanByName(clanName);
			if (getQuestMob(clan) != null)
			{
				res = clan;
				questMobCount++;
			}
		}
		if (questMobCount > 1)
		{
			return null;
		}
		
		return res;
	}
	
	class midSiegeStep implements Runnable
	{
		@Override
		public void run()
		{
			_mobControlTask.cancel();
			L2Clan winner = checkHaveWinner();
			if (winner != null)
			{
				if (clanhall.getOwnerClan() == null)
				{
					ClanHallManager.getInstance().setOwner(clanhall.getId(), winner);
					Announce("Wild Beast Reserve was conquered by the clan " + winner.getName() + ".", 2);
					endSiege(false);
				}
				else
				{
					startSecondStep(winner);
				}
			}
			else
			{
				endSiege(true);
			}
		}
	}
	
	class startFirstStep implements Runnable
	{
		@Override
		public void run()
		{
			teleportPlayers();
			gateControl(2);
			int mobCounter = 1;
			for (String clanName : getRegisteredClans())
			{
				NpcTemplate template;
				L2Clan clan = ClanTable.getInstance().getClanByName(clanName);
				template = NpcTable.getInstance().getTemplate(35617 + mobCounter);
				Monster questMob = new Monster(IdFactory.getInstance().getNextId(), template);
				questMob.setHeading(100);
				questMob.getStatus().setCurrentHpMp(questMob.getMaxHp(), questMob.getMaxMp());
				
				switch (mobCounter)
				{
					case 1:
						questMob.spawnMe(57069, -91797, -1360);
						break;
						
					case 2:
						questMob.spawnMe(58838, -92232, -1354);
						break;
						
					case 3:
						questMob.spawnMe(57327, -93373, -1365);
						break;
						
					case 4:
						questMob.spawnMe(57327, -93373, -1365);
						break;
						
					case 5:
						questMob.spawnMe(58728, -93487, -1360);
						break;
				}
				clanPlayersInfo regPlayers = _clansInfo.get(clan.getClanId());
				regPlayers._mob = questMob;
				mobCounter++;
			}
			_mobControlTask.schedule(3000);
			Announce("The battle began. Kill the enemy NPC", 1);
		}
	}
	
	public void spawnFlags()
	{
		int flagCounter = 1;
		for (String clanName : getRegisteredClans())
		{
			NpcTemplate template;
			L2Clan clan = ClanTable.getInstance().getClanByName(clanName);
			if (clan == clanhall.getOwnerClan())
			{
				template = NpcTable.getInstance().getTemplate(35422);
			}
			else
			{
				template = NpcTable.getInstance().getTemplate(35422 + flagCounter);
			}
			Deco flag = new Deco(IdFactory.getInstance().getNextId(), template);
			flag.setTitle(clan.getName());
			flag.setHeading(100);
			flag.getStatus().setCurrentHpMp(flag.getMaxHp(), flag.getMaxMp());
			if (clan == clanhall.getOwnerClan())
			{
				flag.spawnMe(58782, -93180, -1354);
				clanPlayersInfo regPlayers = _clansInfo.get(clan.getClanId());
				regPlayers._flag = flag;
				continue;
			}
			switch (flagCounter)
			{
				case 1:
					flag.spawnMe(56769, -92097, -1360);
					break;
				case 2:
					flag.spawnMe(59138, -92532, -1354);
					break;
				case 3:
					flag.spawnMe(57027, -93673, -1365);
					break;
				case 4:
					flag.spawnMe(58120, -91440, -1354);
					break;
				case 5:
					flag.spawnMe(58428, -93787, -1360);
					break;
			}
			clanPlayersInfo regPlayers = _clansInfo.get(clan.getClanId());
			regPlayers._flag = flag;
			flagCounter++;
		}
	}
	
	public void setRegistrationPeriod(boolean par)
	{
		_registrationPeriod = par;
	}
	
	public boolean isRegistrationPeriod()
	{
		return _registrationPeriod;
	}
	
	public boolean isPlayerRegister(L2Clan playerClan, String playerName)
	{
		if (playerClan == null)
		{
			return false;
		}
		
		clanPlayersInfo regPlayers = _clansInfo.get(playerClan.getClanId());
		if (regPlayers != null)
		{
			if (regPlayers._players.contains(playerName))
			{
				return true;
			}
		}
		
		return false;
	}
	
	public boolean isClanOnSiege(L2Clan playerClan)
	{
		if (playerClan == clanhall.getOwnerClan())
			return true;

		clanPlayersInfo regPlayers = _clansInfo.get(playerClan.getClanId());
		if (regPlayers == null)
		{
			return false;
		}
		
		return true;
	}
	
	public synchronized int registerClanOnSiege(Player player, L2Clan playerClan)
	{
		if (_clanCounter == 5)
		{
			return 2;
		}
		
		ItemInstance item = player.getInventory().getItemByItemId(8293);
		if (item != null && player.destroyItemWithoutTrace("Consume", item.getObjectId(), 1, null, false))
		{
			_clanCounter++;
			clanPlayersInfo regPlayers = _clansInfo.get(playerClan.getClanId());
			if (regPlayers == null)
			{
				regPlayers = new clanPlayersInfo();
				regPlayers._clanName = playerClan.getName();
				_clansInfo.put(playerClan.getClanId(), regPlayers);
			}
		}
		else
		{
			return 1;
		}
		
		return 0;
	}
	
	public boolean unRegisterClan(L2Clan playerClan)
	{
		if (_clansInfo.remove(playerClan.getClanId()) != null)
		{
			_clanCounter--;
			return true;
		}
		
		return false;
	}
	
	public List<String> getRegisteredClans()
	{
		List<String> clans = new ArrayList<>();
		for (clanPlayersInfo a : _clansInfo.values())
		{
			clans.add(a._clanName);
		}
		
		return clans;
	}
	
	public List<String> getRegisteredPlayers(L2Clan playerClan)
	{
		if (playerClan == clanhall.getOwnerClan())
		{
			return _ownerClanInfo._players;
		}
		
		clanPlayersInfo regPlayers = _clansInfo.get(playerClan.getClanId());
		if (regPlayers != null)
		{
			return regPlayers._players;
		}
		
		return null;
	}
	
	public Deco getSiegeFlag(L2Clan playerClan)
	{
		clanPlayersInfo clanInfo = _clansInfo.get(playerClan.getClanId());
		if (clanInfo != null)
		{
			return clanInfo._flag;
		}
		
		return null;
	}
	
	public Monster getQuestMob(L2Clan clan)
	{
		clanPlayersInfo clanInfo = _clansInfo.get(clan.getClanId());
		if (clanInfo != null)
		{
			return clanInfo._mob;
		}
		
		return null;
	}
	
	public int getPlayersCount(String playerClan)
	{
		for (clanPlayersInfo a : _clansInfo.values())
		{
			if (a._clanName == playerClan)
			{
				return a._players.size();
			}
		}
		
		return 0;
	}
	
	public void addPlayer(L2Clan playerClan, String playerName)
	{
		if (playerClan == clanhall.getOwnerClan())
		{
			if (_ownerClanInfo._players.size() < 18)
			{
				if (!_ownerClanInfo._players.contains(playerName))
				{
					_ownerClanInfo._players.add(playerName);
					return;
				}
			}
		}
		
		clanPlayersInfo regPlayers = _clansInfo.get(playerClan.getClanId());
		if (regPlayers != null)
		{
			if (regPlayers._players.size() < 18)
			{
				if (!regPlayers._players.contains(playerName))
				{
					regPlayers._players.add(playerName);
				}
			}
		}
	}
	
	public void removePlayer(L2Clan playerClan, String playerName)
	{
		if (playerClan == clanhall.getOwnerClan())
		{
			if (_ownerClanInfo._players.contains(playerName))
			{
				_ownerClanInfo._players.remove(playerName);
				return;
			}
		}
		
		clanPlayersInfo regPlayers = _clansInfo.get(playerClan.getClanId());
		if (regPlayers != null)
		{
			if (regPlayers._players.contains(playerName))
			{
				regPlayers._players.remove(playerName);
			}
		}
	}
	
	private final ExclusiveTask _startSiegeTask = new ExclusiveTask()
	{
		@Override
		protected void onElapsed()
		{
			if (getIsInProgress())
			{
				cancel();
				return;
			}
			
			Calendar siegeStart = Calendar.getInstance();
			siegeStart.setTimeInMillis(getSiegeDate().getTimeInMillis());
			final long registerTimeRemaining = siegeStart.getTimeInMillis() - System.currentTimeMillis();
			siegeStart.add(Calendar.HOUR, 1);
			final long siegeTimeRemaining = siegeStart.getTimeInMillis() - System.currentTimeMillis();
			long remaining = registerTimeRemaining;
			if (registerTimeRemaining <= 0)
			{
				if (!isRegistrationPeriod())
				{
					if (clanhall.getOwnerClan() != null)
					{
						_ownerClanInfo._clanName = clanhall.getOwnerClan().getName();
					}
					else
					{
						_ownerClanInfo._clanName = "";
					}
					setRegistrationPeriod(true);
					Announce("Registration period for the Wild Beast Reserve clan hall is open.", 2);
					remaining = siegeTimeRemaining;
				}
			}
			if (siegeTimeRemaining <= 0)
			{
				startSiege();
				cancel();
				return;
			}
			schedule(remaining);
		}
	};
	
	public void Announce(String text, int type)
	{
		if (type == 1)
		{
			CreatureSay cs = new CreatureSay(0, 1, "Journal", text);
			for (String clanName : getRegisteredClans())
			{
				L2Clan clan = ClanTable.getInstance().getClanByName(clanName);
				for (String playerName : getRegisteredPlayers(clan))
				{
					Player cha = World.getInstance().getPlayer(playerName);
					if (cha != null)
					{
						cha.sendPacket(cs);
					}
				}
			}
		}
		else
		{
			CreatureSay cs = new CreatureSay(0, 1, "Journal", text);
			for (Player player : World.getInstance().getPlayers())
			{
				player.sendPacket(cs);
			}
		}
	}
	
	final ExclusiveTask _endSiegeTask = new ExclusiveTask()
	{
		@Override
		protected void onElapsed()
		{
			if (!getIsInProgress())
			{
				cancel();
				return;
			}
			
			final long timeRemaining = _siegeEndDate.getTimeInMillis() - System.currentTimeMillis();
			if (timeRemaining <= 0)
			{
				endSiege(true);
				cancel();
				return;
			}
			schedule(timeRemaining);
		}
	};
	final ExclusiveTask _mobControlTask = new ExclusiveTask()
	{
		@Override
		protected void onElapsed()
		{
			int mobCount = 0;
			for (clanPlayersInfo cl : _clansInfo.values())
			{
				if (cl._mob.isDead())
				{
					L2Clan clan = ClanTable.getInstance().getClanByName(cl._clanName);
					unRegisterClan(clan);
				}
				else
				{
					mobCount++;
				}
			}
			teleportPlayers();
			if (mobCount < 2)
			{
				if (_finalStage)
				{
					_siegeEndDate = Calendar.getInstance();
					_endSiegeTask.cancel();
					_endSiegeTask.schedule(5000);
				}
				else
				{
					_midTimer.cancel(false);
					ThreadPool.schedule(new midSiegeStep(), 5000);
				}
			}
			else
			{
				schedule(3000);
			}
		}
	};
	
	class clanPlayersInfo
	{
		public String _clanName;
		public Deco _flag = null;
		public Monster _mob = null;
		public List<String> _players = new ArrayList<>();
	}
	
}