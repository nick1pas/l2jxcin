package net.sf.l2j.gameserver.model.zone.type;

import net.sf.l2j.commons.concurrent.ThreadPool;

import net.sf.l2j.gameserver.datatables.MapRegionTable.TeleportType;
import net.sf.l2j.gameserver.model.actor.Character;
import net.sf.l2j.gameserver.model.actor.Playable;
import net.sf.l2j.gameserver.model.actor.Summon;
import net.sf.l2j.gameserver.model.actor.instance.Player;
import net.sf.l2j.gameserver.model.olympiad.OlympiadGameTask;
import net.sf.l2j.gameserver.model.zone.L2SpawnZone;
import net.sf.l2j.gameserver.model.zone.ZoneId;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.network.serverpackets.ExOlympiadMatchEnd;
import net.sf.l2j.gameserver.network.serverpackets.ExOlympiadUserInfo;
import net.sf.l2j.gameserver.network.serverpackets.L2GameServerPacket;
import net.sf.l2j.gameserver.network.serverpackets.SystemMessage;

/**
 * An olympiad stadium
 * @author durgus, DS
 */
public class L2OlympiadStadiumZone extends L2SpawnZone
{
	OlympiadGameTask _task = null;
	
	public L2OlympiadStadiumZone(int id)
	{
		super(id);
	}
	
	public final void registerTask(OlympiadGameTask task)
	{
		_task = task;
	}
	
	public final void broadcastStatusUpdate(Player player)
	{
		final ExOlympiadUserInfo packet = new ExOlympiadUserInfo(player);
		for (Player plyr : getKnownTypeInside(Player.class))
		{
			if (plyr.isInObserverMode() || plyr.getOlympiadSide() != player.getOlympiadSide())
				plyr.sendPacket(packet);
		}
	}
	
	public final void broadcastPacketToObservers(L2GameServerPacket packet)
	{
		for (Player player : getKnownTypeInside(Player.class))
		{
			if (player.isInObserverMode())
				player.sendPacket(packet);
		}
	}
	
	@Override
	protected final void onEnter(Character character)
	{
		character.setInsideZone(ZoneId.NO_SUMMON_FRIEND, true);
		character.setInsideZone(ZoneId.NO_RESTART, true);
		
		if (_task != null)
		{
			if (_task.isBattleStarted())
			{
				character.setInsideZone(ZoneId.PVP, true);
				if (character instanceof Player)
				{
					character.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.ENTERED_COMBAT_ZONE));
					_task.getGame().sendOlympiadInfo(character);
				}
			}
		}
		
		if (character instanceof Playable)
		{
			final Player player = character.getActingPlayer();
			if (player != null)
			{
				// only participants, observers and GMs allowed
				if (!player.isGM() && !player.isInOlympiadMode() && !player.isInObserverMode())
					ThreadPool.execute(new KickPlayer(player));
			}
		}
	}
	
	@Override
	protected final void onExit(Character character)
	{
		character.setInsideZone(ZoneId.NO_SUMMON_FRIEND, false);
		character.setInsideZone(ZoneId.NO_RESTART, false);
		
		if (_task != null)
		{
			if (_task.isBattleStarted())
			{
				character.setInsideZone(ZoneId.PVP, false);
				if (character instanceof Player)
				{
					character.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.LEFT_COMBAT_ZONE));
					character.sendPacket(ExOlympiadMatchEnd.STATIC_PACKET);
				}
			}
		}
	}
	
	public final void updateZoneStatusForCharactersInside()
	{
		if (_task == null)
			return;
		
		final boolean battleStarted = _task.isBattleStarted();
		final SystemMessage sm;
		if (battleStarted)
			sm = SystemMessage.getSystemMessage(SystemMessageId.ENTERED_COMBAT_ZONE);
		else
			sm = SystemMessage.getSystemMessage(SystemMessageId.LEFT_COMBAT_ZONE);
		
		for (Character character : _characterList.values())
		{
			if (battleStarted)
			{
				character.setInsideZone(ZoneId.PVP, true);
				if (character instanceof Player)
					character.sendPacket(sm);
			}
			else
			{
				character.setInsideZone(ZoneId.PVP, false);
				if (character instanceof Player)
				{
					character.sendPacket(sm);
					character.sendPacket(ExOlympiadMatchEnd.STATIC_PACKET);
				}
			}
		}
	}
	
	@Override
	public void onDieInside(Character character)
	{
	}
	
	@Override
	public void onReviveInside(Character character)
	{
	}
	
	private static final class KickPlayer implements Runnable
	{
		private Player _player;
		
		public KickPlayer(Player player)
		{
			_player = player;
		}
		
		@Override
		public void run()
		{
			if (_player != null)
			{
				final Summon summon = _player.getPet();
				if (summon != null)
					summon.unSummon(_player);
				
				_player.teleToLocation(TeleportType.TOWN);
				_player = null;
			}
		}
	}
}