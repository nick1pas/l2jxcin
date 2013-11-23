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
package net.xcine.gameserver.model.entity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Calendar;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Logger;

import javolution.util.FastMap;

import net.xcine.Config;
import net.xcine.L2DatabaseFactory;
import net.xcine.gameserver.Announcements;
import net.xcine.gameserver.ThreadPoolManager;
import net.xcine.gameserver.datatables.NpcTable;
import net.xcine.gameserver.instancemanager.ClanHallManager;
import net.xcine.gameserver.model.L2Clan;
import net.xcine.gameserver.model.L2Spawn;
import net.xcine.gameserver.model.actor.L2Npc;
import net.xcine.gameserver.model.actor.instance.L2PcInstance;
import net.xcine.gameserver.templates.chars.L2NpcTemplate;

public class FortressOfResistance
{
	private final static Logger _log = Logger.getLogger(FortressOfResistance.class.getName());

	private static FortressOfResistance _instance;
	private FastMap<Integer, DamageInfo> _clansDamageInfo;

	private static int START_DAY = 1;
	private static int HOUR = Config.PARTISAN_HOUR;
	private static int MINUTES = Config.PARTISAN_MINUTES;

	private static final int BOSS_ID = 35368;
	private static final int MESSENGER_ID = 35382;

	private ScheduledFuture<?> _nurka;
	private ScheduledFuture<?> _announce;

	private Calendar _capturetime = Calendar.getInstance();

	public static FortressOfResistance getInstance()
	{
		if(_instance == null)
		{
			_instance = new FortressOfResistance();
		}

		return _instance;
	}

	public class DamageInfo
	{
		public L2Clan _clan;
		public long _damage;
	}

	private FortressOfResistance()
	{
		if(Config.PARTISAN_DAY == 1)
		{
			START_DAY = Calendar.MONDAY;
		}
		else if(Config.PARTISAN_DAY == 2)
		{
			START_DAY = Calendar.TUESDAY;
		}
		else if(Config.PARTISAN_DAY == 3)
		{
			START_DAY = Calendar.WEDNESDAY;
		}
		else if(Config.PARTISAN_DAY == 4)
		{
			START_DAY = Calendar.THURSDAY;
		}
		else if(Config.PARTISAN_DAY == 5)
		{
			START_DAY = Calendar.FRIDAY;
		}
		else if(Config.PARTISAN_DAY == 6)
		{
			START_DAY = Calendar.SATURDAY;
		}
		else if(Config.PARTISAN_DAY == 7)
		{
			START_DAY = Calendar.SUNDAY;
		}
		else
		{
			START_DAY = Calendar.FRIDAY;
		}

		if(HOUR < 0 || HOUR > 23)
		{
			HOUR = 21;
		}
		if(MINUTES < 0 || MINUTES > 59)
		{
			MINUTES = 0;
		}

		_clansDamageInfo = new FastMap<>();

		synchronized (this)
		{
			setCalendarForNextCaprture();
			long milliToCapture = getMilliToCapture();

			RunMessengerSpawn rms = new RunMessengerSpawn();
			ThreadPoolManager.getInstance().scheduleGeneral(rms, milliToCapture);
			_log.info("FortressOfResistance: " + milliToCapture / 60000 + " min. to capture");
		}
	}

	private void setCalendarForNextCaprture()
	{
		int daysToChange = getDaysToCapture();

		if(daysToChange == 7)
		{
			if(_capturetime.get(Calendar.HOUR_OF_DAY) < HOUR)
			{
				daysToChange = 0;
			}
			else if(_capturetime.get(Calendar.HOUR_OF_DAY) == HOUR && _capturetime.get(Calendar.MINUTE) < MINUTES)
			{
				daysToChange = 0;
			}
		}

		if(daysToChange > 0)
		{
			_capturetime.add(Calendar.DATE, daysToChange);
		}

		_capturetime.set(Calendar.HOUR_OF_DAY, HOUR);
		_capturetime.set(Calendar.MINUTE, MINUTES);
	}

	private int getDaysToCapture()
	{
		int numDays = _capturetime.get(Calendar.DAY_OF_WEEK) - START_DAY;

		if(numDays < 0)
		{
			return 0 - numDays;
		}

		return 7 - numDays;
	}

	private long getMilliToCapture()
	{
		long currTimeMillis = System.currentTimeMillis();
		long captureTimeMillis = _capturetime.getTimeInMillis();

		return captureTimeMillis - currTimeMillis;
	}

	protected class RunMessengerSpawn implements Runnable
	{
		@Override
		public void run()
		{
			MessengerSpawn();
		}
	}

	public void MessengerSpawn()
	{
		if(!ClanHallManager.getInstance().isFree(21))
		{
			ClanHallManager.getInstance().setFree(21);
		}

		Announce("Capture registration of Partisan Hideout has begun!");
		Announce("Now its open for 1 hours!");

		L2Npc result = null;
		try
		{
			L2NpcTemplate template = NpcTable.getInstance().getTemplate(MESSENGER_ID);

			L2Spawn spawn = new L2Spawn(template);
			spawn.setLocx(50335);
			spawn.setLocy(111275);
			spawn.setLocz(-1970);
			spawn.stopRespawn();
			result = spawn.spawnOne();
			template = null;
		}
		catch(Exception e)
		{
			_log.warning("");
		}
		RunBossSpawn rbs = new RunBossSpawn();
		ThreadPoolManager.getInstance().scheduleGeneral(rbs, 3600000);
		_log.info("Fortress of Resistanse: Messenger spawned!");
		ThreadPoolManager.getInstance().scheduleGeneral(new DeSpawnTimer(result), 3600000);
	}

	protected class RunBossSpawn implements Runnable
	{
		@Override
		public void run()
		{
			BossSpawn();
		}
	}

	public void BossSpawn()
	{
		if(!_clansDamageInfo.isEmpty())
		{
			_clansDamageInfo.clear();
		}

		L2Npc result = null;
		try
		{
			L2NpcTemplate template = NpcTable.getInstance().getTemplate(BOSS_ID);

			L2Spawn spawn = new L2Spawn(template);
			spawn.setLocx(44525);
			spawn.setLocy(108867);
			spawn.setLocz(-2020);
			spawn.stopRespawn();
			result = spawn.spawnOne();
			template = null;
		}
		catch(Exception e)
		{
			_log.warning("");
		}

		_log.info("Fortress of Resistanse: Boss spawned!");

		_nurka = ThreadPoolManager.getInstance().scheduleGeneral(new DeSpawnTimer(result), 3600000);
		_announce = ThreadPoolManager.getInstance().scheduleGeneral(new AnnounceInfo("No one can`t kill Nurka! Partisan Hideout set free until next week!"), 3600000);
	}

	protected class DeSpawnTimer implements Runnable
	{
		L2Npc _npc = null;

		public DeSpawnTimer(L2Npc npc)
		{
			_npc = npc;
		}

		@Override
		public void run()
		{
			_npc.onDecay();
		}
	}

	public final static boolean Conditions(L2PcInstance player)
	{
		if(player != null && player.getClan() != null && player.isClanLeader() && player.getClan().getAuctionBiddedAt() <= 0 && ClanHallManager.getInstance().getClanHallByOwner(player.getClan()) == null && player.getClan().getLevel() > 2)
		{
			return true;
		}
		return false;
	}

	protected class AnnounceInfo implements Runnable
	{
		String _message;

		public AnnounceInfo(String message)
		{
			_message = message;
		}

		@Override
		public void run()
		{
			Announce(_message);
		}
	}

	public void Announce(String message)
	{
		Announcements.getInstance();
		Announcements.announceToAll(message);
	}

	public void CaptureFinish()
	{
		L2Clan clanIdMaxDamage = null;
		long tempMaxDamage = 0;
		for(DamageInfo damageInfo : _clansDamageInfo.values())
		{
			if(damageInfo != null)
			{
				if(damageInfo._damage > tempMaxDamage)
				{
					tempMaxDamage = damageInfo._damage;
					clanIdMaxDamage = damageInfo._clan;
				}
			}
		}
		if(clanIdMaxDamage != null)
		{
			ClanHallManager.getInstance().setOwner(21, clanIdMaxDamage);
			clanIdMaxDamage.addReputationScore(clanIdMaxDamage.getReputationScore() + 600);
			update();

			Announce("Capture of Partisan Hideout is over.");
			Announce("Now its belong to: '" + clanIdMaxDamage.getName() + "' until next capture.");
		}
		else
		{
			Announce("Capture of Partisan Hideout is over.");
			Announce("No one can`t capture Partisan Hideout.");
		}

		_nurka.cancel(true);
		_announce.cancel(true);
	}

	public void addSiegeDamage(L2Clan clan, long damage)
	{
		DamageInfo clanDamage = _clansDamageInfo.get(clan.getClanId());
		if(clanDamage != null)
		{
			clanDamage._damage += damage;
		}
		else
		{
			clanDamage = new DamageInfo();
			clanDamage._clan = clan;
			clanDamage._damage += damage;
			_clansDamageInfo.put(clan.getClanId(), clanDamage);
		}
	}

	private static void update()
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement;

			statement = con.prepareStatement("UPDATE clanhall SET paidUntil = ?, paid = ? WHERE id = ?");
			statement.setLong(1, System.currentTimeMillis() + 59760000);
			statement.setInt(2, 1);
			statement.setInt(3, 21);
			statement.execute();
			statement.close();
			statement = null;
		}
		catch(Exception e)
		{
			_log.warning("");
		}
	}
}