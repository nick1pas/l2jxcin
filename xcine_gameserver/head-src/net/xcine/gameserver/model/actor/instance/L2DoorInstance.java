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
package net.xcine.gameserver.model.actor.instance;

import java.util.concurrent.ScheduledFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.xcine.Config;
import net.xcine.gameserver.SevenSigns;
import net.xcine.gameserver.ThreadPoolManager;
import net.xcine.gameserver.ai.CtrlIntention;
import net.xcine.gameserver.ai.L2CharacterAI;
import net.xcine.gameserver.ai.L2DoorAI;
import net.xcine.gameserver.instancemanager.CastleManager;
import net.xcine.gameserver.model.L2CharPosition;
import net.xcine.gameserver.model.L2Clan;
import net.xcine.gameserver.model.L2ItemInstance;
import net.xcine.gameserver.model.L2Object;
import net.xcine.gameserver.model.L2Skill;
import net.xcine.gameserver.model.actor.L2Character;
import net.xcine.gameserver.model.actor.L2Npc;
import net.xcine.gameserver.model.actor.L2Playable;
import net.xcine.gameserver.model.actor.knownlist.DoorKnownList;
import net.xcine.gameserver.model.actor.stat.DoorStat;
import net.xcine.gameserver.model.actor.status.DoorStatus;
import net.xcine.gameserver.model.entity.Castle;
import net.xcine.gameserver.model.entity.ClanHall;
import net.xcine.gameserver.model.entity.DevastatedCastle;
import net.xcine.gameserver.network.SystemMessageId;
import net.xcine.gameserver.network.serverpackets.ActionFailed;
import net.xcine.gameserver.network.serverpackets.ConfirmDlg;
import net.xcine.gameserver.network.serverpackets.DoorInfo;
import net.xcine.gameserver.network.serverpackets.DoorStatusUpdate;
import net.xcine.gameserver.network.serverpackets.MyTargetSelected;
import net.xcine.gameserver.network.serverpackets.NpcHtmlMessage;
import net.xcine.gameserver.network.serverpackets.SystemMessage;
import net.xcine.gameserver.templates.chars.L2CharTemplate;
import net.xcine.gameserver.templates.item.L2Weapon;

public class L2DoorInstance extends L2Character
{
	protected static final Logger log = Logger.getLogger(L2DoorInstance.class.getName());
	
	/** The castle index in the array of L2Castle this L2Npc belongs to */
	private int _castleIndex = -2;
	private int _mapRegion = -1;
	
	// when door is closed, the dimensions are
	private int _rangeXMin = 0;
	private int _rangeYMin = 0;
	private int _rangeZMin = 0;
	private int _rangeXMax = 0;
	private int _rangeYMax = 0;
	private int _rangeZMax = 0;
	
	// these variables assist in see-through calculation only
	private int _A = 0;
	private int _B = 0;
	private int _C = 0;
	private int _D = 0;
	
	protected final int _doorId;
	protected final String _name;
	private boolean _open;
	private final boolean _unlockable;
	private boolean _isWall = false; // False by default
	private int _upgradeHpRatio = 1;
	
	private ClanHall _clanHall;
	
	protected int _autoActionDelay = -1;
	private ScheduledFuture<?> _autoActionTask;
	
	/** This class may be created only by L2Character and only for AI */
	public class AIAccessor extends L2Character.AIAccessor
	{
		protected AIAccessor()
		{
		}
		
		@Override
		public L2DoorInstance getActor()
		{
			return L2DoorInstance.this;
		}
		
		@Override
		public void moveTo(int x, int y, int z, int offset)
		{
		}
		
		@Override
		public void moveTo(int x, int y, int z)
		{
		}
		
		@Override
		public void stopMove(L2CharPosition pos)
		{
		}
		
		@Override
		public void doAttack(L2Character target)
		{
		}
		
		@Override
		public void doCast(L2Skill skill)
		{
		}
	}
	
	@Override
	public L2CharacterAI getAI()
	{
		L2CharacterAI ai = _ai; // copy handle
		if (ai == null)
		{
			synchronized (this)
			{
				if (_ai == null)
					_ai = new L2DoorAI(new AIAccessor());
				return _ai;
			}
		}
		return ai;
	}
	
	class CloseTask implements Runnable
	{
		@Override
		public void run()
		{
			try
			{
				onClose();
			}
			catch (Throwable e)
			{
				log.log(Level.SEVERE, "", e);
			}
		}
	}
	
	/**
	 * Manages the auto open and closing of a door.
	 */
	class AutoOpenClose implements Runnable
	{
		@Override
		public void run()
		{
			try
			{
				String doorAction;
				
				if (!isOpened())
				{
					doorAction = "opened";
					openMe();
				}
				else
				{
					doorAction = "closed";
					closeMe();
				}
				
				if (Config.DEBUG)
					log.info("Auto " + doorAction + " door ID " + _doorId + " (" + _name + ") for " + (_autoActionDelay / 60000) + " minute(s).");
			}
			catch (Exception e)
			{
				log.warning("Could not auto open/close door ID " + _doorId + " (" + _name + ")");
			}
		}
	}
	
	public L2DoorInstance(int objectId, L2CharTemplate template, int doorId, String name, boolean unlockable)
	{
		super(objectId, template);
		_doorId = doorId;
		_name = name;
		_unlockable = unlockable;
	}
	
	@Override
	public void initKnownList()
	{
		setKnownList(new DoorKnownList(this));
	}
	
	@Override
	public final DoorKnownList getKnownList()
	{
		return (DoorKnownList) super.getKnownList();
	}
	
	@Override
	public void initCharStat()
	{
		setStat(new DoorStat(this));
	}
	
	@Override
	public final DoorStat getStat()
	{
		return (DoorStat) super.getStat();
	}
	
	@Override
	public void initCharStatus()
	{
		setStatus(new DoorStatus(this));
	}
	
	@Override
	public final DoorStatus getStatus()
	{
		return (DoorStatus) super.getStatus();
	}
	
	public final boolean isUnlockable()
	{
		return _unlockable;
	}
	
	@Override
	public final int getLevel()
	{
		return 1;
	}
	
	/**
	 * @return Returns the doorId.
	 */
	public int getDoorId()
	{
		return _doorId;
	}
	
	/**
	 * @return Returns the open.
	 */
	public boolean isOpened()
	{
		return _open;
	}
	
	/**
	 * @param open The open to set.
	 */
	public void setOpen(boolean open)
	{
		_open = open;
	}
	
	/**
	 * Sets the delay for automatic opening/closing of this door instance.<BR>
	 * <B>Note:</B> A value of -1 cancels the auto open/close task.
	 * @param actionDelay Delay in milliseconds.
	 */
	public void setAutoActionDelay(int actionDelay)
	{
		if (_autoActionDelay == actionDelay)
			return;
		
		if (actionDelay > -1)
		{
			AutoOpenClose ao = new AutoOpenClose();
			ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(ao, actionDelay, actionDelay);
		}
		else
		{
			if (_autoActionTask != null)
				_autoActionTask.cancel(false);
		}
		
		_autoActionDelay = actionDelay;
	}
	
	public int getDamage()
	{
		int dmg = 6 - (int) Math.ceil(getCurrentHp() / getMaxHp() * 6);
		if (dmg > 6)
			return 6;
		if (dmg < 0)
			return 0;
		return dmg;
	}
	
	public final Castle getCastle()
	{
		if (_castleIndex < 0)
			_castleIndex = CastleManager.getInstance().getCastleIndex(this);
		if (_castleIndex < 0)
			return null;
		return CastleManager.getInstance().getCastles().get(_castleIndex);
	}
	
	public void setClanHall(ClanHall clanhall)
	{
		_clanHall = clanhall;
	}
	
	public ClanHall getClanHall()
	{
		return _clanHall;
	}
	
	public boolean isEnemy()
	{
		if (getCastle() != null && getCastle().getCastleId() > 0 && getCastle().getSiege().getIsInProgress())
			return true;
		return false;
	}
	
	@Override
	public boolean isAutoAttackable(L2Character attacker)
	{
		if (isUnlockable())
			return true;
		
		// Doors can`t be attacked by NPCs
		if (!(attacker instanceof L2Playable))
			return false;
		
		// Attackable during siege by attacker only
		boolean isCastle = (getCastle() != null && getCastle().getCastleId() > 0 && getCastle().getSiege().getIsInProgress());
		L2PcInstance actingPlayer = attacker.getActingPlayer();
		
		if (isCastle)
		{
			L2Clan clan = actingPlayer.getClan();
			if (clan != null && clan.getClanId() == getCastle().getOwnerId())
				return false;
		}
		
		return isCastle || DevastatedCastle.getInstance().getIsInProgress();
	}
	
	public boolean isAttackable(L2Character attacker)
	{
		return isAutoAttackable(attacker);
	}
	
	@Override
	public void updateAbnormalEffect()
	{
	}
	
	public int getDistanceToWatchObject(L2Object object)
	{
		if (!(object instanceof L2PcInstance))
			return 0;
		
		return 6000;
	}
	
	/**
	 * @param object The object to check on. If L2PcInstance : 9000, otherwise 0.
	 * @return The distance after which the object must be remove from _knownObject according to the type of the object.
	 */
	public int getDistanceToForgetObject(L2Object object)
	{
		if (!(object instanceof L2PcInstance))
			return 0;
		
		return 9000;
	}
	
	/**
	 * Return null.<BR>
	 * <BR>
	 */
	@Override
	public L2ItemInstance getActiveWeaponInstance()
	{
		return null;
	}
	
	@Override
	public L2Weapon getActiveWeaponItem()
	{
		return null;
	}
	
	@Override
	public L2ItemInstance getSecondaryWeaponInstance()
	{
		return null;
	}
	
	@Override
	public L2Weapon getSecondaryWeaponItem()
	{
		return null;
	}
	
	@Override
	public void onAction(L2PcInstance player)
	{
		// Check if the L2PcInstance already target the L2Npc
		if (player.getTarget() != this)
		{
			// Set the target of the L2PcInstance player
			player.setTarget(this);
			
			// Send MyTargetSelected to the L2PcInstance player
			player.sendPacket(new MyTargetSelected(getObjectId(), 0));
			
			player.sendPacket(new DoorStatusUpdate(this));
		}
		else
		{
			if (isAutoAttackable(player))
			{
				if (Math.abs(player.getZ() - getZ()) < 400) // this max heigth difference might need some tweaking
					player.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, this);
			}
			else if (!isInsideRadius(player, L2Npc.INTERACTION_DISTANCE, false, false))
				player.getAI().setIntention(CtrlIntention.AI_INTENTION_INTERACT, this);
			else if (player.getClan() != null && getClanHall() != null && player.getClanId() == getClanHall().getOwnerId())
			{
				player.gatesRequest(this);
				if (!isOpened())
					player.sendPacket(new ConfirmDlg(1140));
				else
					player.sendPacket(new ConfirmDlg(1141));
				
				player.sendPacket(ActionFailed.STATIC_PACKET);
			}
			else
				// Send a Server->Client ActionFailed to the L2PcInstance in order to avoid that the client wait another packet
				player.sendPacket(ActionFailed.STATIC_PACKET);
		}
	}
	
	@Override
	public void onActionShift(L2PcInstance player)
	{
		if (player.isGM())
		{
			NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
			html.setFile("data/html/admin/infos/doorinfo.htm");
			
			html.replace("%class%", getClass().getSimpleName());
			html.replace("%hp%", String.valueOf((int) getCurrentHp()));
			html.replace("%hpmax%", String.valueOf(getMaxHp()));
			html.replace("%objid%", String.valueOf(getObjectId()));
			html.replace("%doorid%", String.valueOf(getDoorId()));
			
			html.replace("%minx%", String.valueOf(getXMin()));
			html.replace("%miny%", String.valueOf(getYMin()));
			html.replace("%minz%", String.valueOf(getZMin()));
			
			html.replace("%maxx%", String.valueOf(getXMax()));
			html.replace("%maxy%", String.valueOf(getYMax()));
			html.replace("%maxz%", String.valueOf(getZMax()));
			html.replace("%unlock%", isUnlockable() ? "<font color=00FF00>YES<font>" : "<font color=FF0000>NO</font>");
			html.replace("%isWall%", isWall() ? "<font color=00FF00>YES<font>" : "<font color=FF0000>NO</font>");
			
			player.sendPacket(html);
			player.sendPacket(ActionFailed.STATIC_PACKET);
		}
		
		if (player.getTarget() != this)
		{
			player.setTarget(this);
			player.sendPacket(new MyTargetSelected(getObjectId(), 0));
			
			if (isAutoAttackable(player))
				player.sendPacket(new DoorStatusUpdate(this));
		}
		else
			player.sendPacket(ActionFailed.STATIC_PACKET);
	}
	
	@Override
	public void broadcastStatusUpdate()
	{
		DoorStatusUpdate su = new DoorStatusUpdate(this);
		for (L2PcInstance player : getKnownList().getKnownPlayers())
			player.sendPacket(su);
	}
	
	public void onOpen()
	{
		ThreadPoolManager.getInstance().scheduleGeneral(new CloseTask(), 60000);
	}
	
	public void onClose()
	{
		closeMe();
	}
	
	public final void closeMe()
	{
		setOpen(false);
		broadcastStatusUpdate();
	}
	
	public final void openMe()
	{
		setOpen(true);
		broadcastStatusUpdate();
	}
	
	@Override
	public String toString()
	{
		return "door " + _doorId;
	}
	
	public String getDoorName()
	{
		return _name;
	}
	
	public int getXMin()
	{
		return _rangeXMin;
	}
	
	public int getYMin()
	{
		return _rangeYMin;
	}
	
	public int getZMin()
	{
		return _rangeZMin;
	}
	
	public int getXMax()
	{
		return _rangeXMax;
	}
	
	public int getYMax()
	{
		return _rangeYMax;
	}
	
	public int getZMax()
	{
		return _rangeZMax;
	}
	
	public void setRange(int xMin, int yMin, int zMin, int xMax, int yMax, int zMax)
	{
		_rangeXMin = xMin;
		_rangeYMin = yMin;
		_rangeZMin = zMin;
		
		_rangeXMax = xMax;
		_rangeYMax = yMax;
		_rangeZMax = zMax;
		
		_A = _rangeYMax * (_rangeZMax - _rangeZMin) + _rangeYMin * (_rangeZMin - _rangeZMax);
		_B = _rangeZMin * (_rangeXMax - _rangeXMin) + _rangeZMax * (_rangeXMin - _rangeXMax);
		_C = _rangeXMin * (_rangeYMax - _rangeYMin) + _rangeXMin * (_rangeYMin - _rangeYMax);
		_D = -1 * (_rangeXMin * (_rangeYMax * _rangeZMax - _rangeYMin * _rangeZMax) + _rangeXMax * (_rangeYMin * _rangeZMin - _rangeYMin * _rangeZMax) + _rangeXMin * (_rangeYMin * _rangeZMax - _rangeYMax * _rangeZMin));
	}
	
	public int getMapRegion()
	{
		return _mapRegion;
	}
	
	public void setMapRegion(int region)
	{
		_mapRegion = region;
	}
	
	public int getA()
	{
		return _A;
	}
	
	public int getB()
	{
		return _B;
	}
	
	public int getC()
	{
		return _C;
	}
	
	public int getD()
	{
		return _D;
	}
	
	public void setIsWall(boolean isWall)
	{
		_isWall = isWall;
	}
	
	public boolean isWall()
	{
		return _isWall;
	}
	
	@Override
	public void reduceCurrentHp(double damage, L2Character attacker, boolean awake, boolean isDOT, L2Skill skill)
	{
		if (isWall() && !(attacker instanceof L2SiegeSummonInstance))
			return;
		
		if (!(getCastle() != null && getCastle().getCastleId() > 0 && getCastle().getSiege().getIsInProgress()))
			return;
		
		super.reduceCurrentHp(damage, attacker, awake, isDOT, skill);
	}
	
	@Override
	public void reduceCurrentHpByDOT(double i, L2Character attacker, L2Skill skill)
	{
		// doors can't be damaged by DOTs
	}
	
	@Override
	public boolean doDie(L2Character killer)
	{
		if (!super.doDie(killer))
			return false;
		
		boolean isCastle = (getCastle() != null && getCastle().getCastleId() > 0 && getCastle().getSiege().getIsInProgress());
		if (isCastle)
			broadcastPacket(SystemMessage.getSystemMessage((isWall()) ? SystemMessageId.CASTLE_WALL_DAMAGED : SystemMessageId.CASTLE_GATE_BROKEN_DOWN));
		
		return true;
	}
	
	@Override
	public int getMaxHp()
	{
		return super.getMaxHp() * _upgradeHpRatio;
	}
	
	public void setUpgradeHpRatio(int hpRatio)
	{
		_upgradeHpRatio = hpRatio;
	}
	
	public boolean getOpen()
	{
		return _open;
	}
	@Override
	public int getPDef(L2Character target)
	{
		switch (SevenSigns.getInstance().getSealOwner(SevenSigns.SEAL_STRIFE))
		{
			case SevenSigns.CABAL_DAWN:
				return (int) (super.getPDef(target) * 1.2);
				
			case SevenSigns.CABAL_DUSK:
				return (int) (super.getPDef(target) * 0.3);
		}
		return super.getPDef(target);
	}
	
	@Override
	public int getMDef(L2Character target, L2Skill skill)
	{
		switch (SevenSigns.getInstance().getSealOwner(SevenSigns.SEAL_STRIFE))
		{
			case SevenSigns.CABAL_DAWN:
				return (int) (super.getMDef(target, skill) * 1.2);
				
			case SevenSigns.CABAL_DUSK:
				return (int) (super.getMDef(target, skill) * 0.3);
		}
		return super.getMDef(target, skill);
	}
	
	@Override
	public void sendInfo(L2PcInstance activeChar)
	{
		activeChar.sendPacket(new DoorInfo(this));
		activeChar.sendPacket(new DoorStatusUpdate(this));
	}
}