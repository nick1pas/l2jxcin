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
package net.sf.l2j.gameserver.model.actor.instance;

import net.sf.l2j.commons.random.Rnd;
import net.sf.l2j.gameserver.ThreadPoolManager;
import net.sf.l2j.gameserver.ai.CtrlIntention;
import net.sf.l2j.gameserver.ai.model.L2CharacterAI;
import net.sf.l2j.gameserver.ai.model.L2DoorAI;
import net.sf.l2j.gameserver.datatables.DoorTable;
import net.sf.l2j.gameserver.geoengine.GeoEngine;
import net.sf.l2j.gameserver.geoengine.geodata.IGeoObject;
import net.sf.l2j.gameserver.instancemanager.CastleManager;
import net.sf.l2j.gameserver.instancemanager.ClanHallManager;
import net.sf.l2j.gameserver.model.L2Clan;
import net.sf.l2j.gameserver.model.L2Skill;
import net.sf.l2j.gameserver.model.SpawnLocation;
import net.sf.l2j.gameserver.model.actor.L2Character;
import net.sf.l2j.gameserver.model.actor.L2Npc;
import net.sf.l2j.gameserver.model.actor.L2Playable;
import net.sf.l2j.gameserver.model.actor.knownlist.DoorKnownList;
import net.sf.l2j.gameserver.model.actor.stat.DoorStat;
import net.sf.l2j.gameserver.model.actor.status.DoorStatus;
import net.sf.l2j.gameserver.model.actor.template.DoorTemplate;
import net.sf.l2j.gameserver.model.actor.template.DoorTemplate.DoorType;
import net.sf.l2j.gameserver.model.actor.template.DoorTemplate.OpenType;
import net.sf.l2j.gameserver.model.entity.Castle;
import net.sf.l2j.gameserver.model.entity.ClanHall;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;
import net.sf.l2j.gameserver.model.item.kind.Weapon;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.network.serverpackets.ActionFailed;
import net.sf.l2j.gameserver.network.serverpackets.ConfirmDlg;
import net.sf.l2j.gameserver.network.serverpackets.DoorInfo;
import net.sf.l2j.gameserver.network.serverpackets.DoorStatusUpdate;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;
import net.sf.l2j.gameserver.network.serverpackets.SystemMessage;

public class L2DoorInstance extends L2Character implements IGeoObject
{
	private final Castle _castle;
	private final ClanHall _clanHall;
	
	private boolean _open;
	
	@Override
	public L2CharacterAI getAI()
	{
		L2CharacterAI ai = _ai;
		if (ai == null)
		{
			synchronized (this)
			{
				if (_ai == null)
					_ai = new L2DoorAI(this);
				
				return _ai;
			}
		}
		return ai;
	}
	
	public L2DoorInstance(int objectId, DoorTemplate template)
	{
		super(objectId, template);
		
		// assign door to a castle
		_castle = CastleManager.getInstance().getCastleById(template.getCastle());
		if (_castle != null)
			_castle.getDoors().add(this);
		
		// assign door to a clan hall
		_clanHall = ClanHallManager.getInstance().getNearbyClanHall(template.getPosX(), template.getPosY(), 500);
		if (_clanHall != null)
			_clanHall.getDoors().add(this);
		
		// set default status and name
		_open = getTemplate().isOpened();
		setName(template.getName());
	}
	
	/**
	 * Returns the {@link L2DoorInstance} ID.
	 * @return int : Returns the ID.
	 */
	public final int getDoorId()
	{
		return getTemplate().getId();
	}
	
	/**
	 * Returns true, when {@link L2DoorInstance} is opened.
	 * @return boolean : True, when opened.
	 */
	public final boolean isOpened()
	{
		return _open;
	}
	
	/**
	 * Returns true, when {@link L2DoorInstance} can be unlocked and opened.
	 * @return boolean : True, when can be unlocked and opened.
	 */
	public final boolean isUnlockable()
	{
		return getTemplate().getOpenType() == OpenType.SKILL;
	}
	
	/**
	 * Returns the actual damage of the door.
	 * @return int : Door damage.
	 */
	public final int getDamage()
	{
		return Math.max(0, Math.min(6, 6 - (int) Math.ceil(getCurrentHp() / getMaxHp() * 6)));
	}
	
	/**
	 * Opens the {@link L2DoorInstance}.
	 */
	public final void openMe()
	{
		// open door using external action
		changeState(true, false);
	}
	
	/**
	 * Closes the {@link L2DoorInstance}.
	 */
	public final void closeMe()
	{
		// close door using external action
		changeState(false, false);
	}
	
	/**
	 * Open/closes the {@link L2DoorInstance}, triggers other {@link L2DoorInstance} and schedules automatic open/close task.
	 * @param open : Requested status change.
	 * @param triggered : The status change was triggered by other.
	 */
	final void changeState(boolean open, boolean triggered)
	{
		// door is dead or already in requested state, return
		if (isDead() || _open == open)
			return;
		
		// change door state and broadcast change
		_open = open;
		if (open)
			GeoEngine.getInstance().removeGeoObject(this);
		else
			GeoEngine.getInstance().addGeoObject(this);
		
		broadcastStatusUpdate();
		
		// door controls another door
		int triggerId = getTemplate().getTriggerId();
		if (triggerId > 0)
		{
			// get door and trigger state change
			L2DoorInstance door = DoorTable.getInstance().getDoor(triggerId);
			if (door != null)
				door.changeState(open, true);
		}
		
		// request is not triggered
		if (!triggered)
		{
			// calculate time for automatic state change
			int time = open ? getTemplate().getCloseTime() : getTemplate().getOpenTime();
			if (getTemplate().getRandomTime() > 0)
				time += Rnd.get(getTemplate().getRandomTime());
			
			// try to schedule automatic state change
			if (time > 0)
				ThreadPoolManager.getInstance().scheduleGeneral(new Runnable()
				{
					@Override
					public void run()
					{
						changeState(!open, false);
					}
				}, time * 1000);
		}
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
	
	@Override
	public final DoorTemplate getTemplate()
	{
		return (DoorTemplate) super.getTemplate();
	}
	
	@Override
	public void addFuncsToNewCharacter()
	{
	}
	
	@Override
	public final int getLevel()
	{
		return getTemplate().getLevel();
	}
	
	@Override
	public void updateAbnormalEffect()
	{
	}
	
	@Override
	public ItemInstance getActiveWeaponInstance()
	{
		return null;
	}
	
	@Override
	public Weapon getActiveWeaponItem()
	{
		return null;
	}
	
	@Override
	public ItemInstance getSecondaryWeaponInstance()
	{
		return null;
	}
	
	@Override
	public Weapon getSecondaryWeaponItem()
	{
		return null;
	}
	
	@Override
	public boolean isAutoAttackable(L2Character attacker)
	{
		// Doors can't be attacked by NPCs
		if (!(attacker instanceof L2Playable))
			return false;
		
		if (isUnlockable())
			return true;
		
		// Attackable during siege by attacker only
		final boolean isCastle = (_castle != null && _castle.getSiege().isInProgress());
		if (isCastle)
		{
			final L2Clan clan = attacker.getActingPlayer().getClan();
			if (clan != null && clan.getClanId() == _castle.getOwnerId())
				return false;
		}
		return isCastle;
	}
	
	@Override
	public void onAction(L2PcInstance player)
	{
		// Set the target of the L2PcInstance player
		if (player.getTarget() != this)
		{
			player.setTarget(this);
			player.sendPacket(new DoorStatusUpdate(this, player));
		}
		else
		{
			if (isAutoAttackable(player))
			{
				if (Math.abs(player.getZ() - getZ()) < 400) // this max heigth difference might need some tweaking
					player.getAI().setIntention(CtrlIntention.ATTACK, this);
			}
			else if (!isInsideRadius(player, L2Npc.INTERACTION_DISTANCE, false, false))
				player.getAI().setIntention(CtrlIntention.INTERACT, this);
			else if (player.getClan() != null && _clanHall != null && player.getClanId() == _clanHall.getOwnerId())
			{
				player.setRequestedGate(this);
				player.sendPacket(new ConfirmDlg((!isOpened()) ? 1140 : 1141));
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
			final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
			html.setFile("data/html/admin/doorinfo.htm");
			html.replace("%name%", getName());
			html.replace("%objid%", getObjectId());
			html.replace("%doorid%", getTemplate().getId());
			html.replace("%doortype%", getTemplate().getType().toString());
			html.replace("%doorlvl%", getTemplate().getLevel());
			html.replace("%castle%", _castle != null ? _castle.getName() : "none");
			html.replace("%opentype%", getTemplate().getOpenType().toString());
			html.replace("%initial%", getTemplate().isOpened() ? "Opened" : "Closed");
			html.replace("%ot%", getTemplate().getOpenTime());
			html.replace("%ct%", getTemplate().getCloseTime());
			html.replace("%rt%", getTemplate().getRandomTime());
			html.replace("%controlid%", getTemplate().getTriggerId());
			html.replace("%hp%", (int) getCurrentHp());
			html.replace("%hpmax%", getMaxHp());
			html.replace("%hpratio%", getStat().getUpgradeHpRatio());
			html.replace("%pdef%", getPDef(null));
			html.replace("%mdef%", getMDef(null, null));
			html.replace("%spawn%", getX() + " " + getY() + " " + getZ());
			html.replace("%height%", getTemplate().getCollisionHeight());
			player.sendPacket(html);
			player.sendPacket(ActionFailed.STATIC_PACKET);
		}
		
		if (player.getTarget() != this)
		{
			player.setTarget(this);
			
			if (isAutoAttackable(player))
				player.sendPacket(new DoorStatusUpdate(this, player));
		}
		else
			player.sendPacket(ActionFailed.STATIC_PACKET);
	}
	
	@Override
	public void reduceCurrentHp(double damage, L2Character attacker, boolean awake, boolean isDOT, L2Skill skill)
	{
		if (getTemplate().getType() == DoorType.WALL && !(attacker instanceof L2SiegeSummonInstance))
			return;
		
		if (!(_castle != null && _castle.getSiege().isInProgress()))
			return;
		
		super.reduceCurrentHp(damage, attacker, awake, isDOT, skill);
	}
	
	@Override
	public void reduceCurrentHpByDOT(double i, L2Character attacker, L2Skill skill)
	{
		// Doors can't be damaged by DOTs.
	}
	
	@Override
	public void onSpawn()
	{
		if (!_open)
			GeoEngine.getInstance().addGeoObject(this);
		
		super.onSpawn();
	}
	
	@Override
	public boolean doDie(L2Character killer)
	{
		if (!super.doDie(killer))
			return false;
		
		if (!_open)
			GeoEngine.getInstance().removeGeoObject(this);
		
		if (_castle != null && _castle.getSiege().isInProgress())
			_castle.getSiege().announceToPlayer(SystemMessage.getSystemMessage((getTemplate().getType() == DoorType.WALL) ? SystemMessageId.CASTLE_WALL_DAMAGED : SystemMessageId.CASTLE_GATE_BROKEN_DOWN), false);
		
		return true;
	}
	
	@Override
	public void doRevive()
	{
		_open = getTemplate().isOpened();
		
		if (!_open)
			GeoEngine.getInstance().addGeoObject(this);
		
		super.doRevive();
	}
	
	@Override
	public void broadcastStatusUpdate()
	{
		for (L2PcInstance player : getKnownList().getKnownType(L2PcInstance.class))
			player.sendPacket(new DoorStatusUpdate(this, player));
	}
	
	@Override
	public void moveToLocation(int x, int y, int z, int offset)
	{
	}
	
	@Override
	public void stopMove(SpawnLocation loc)
	{
	}
	
	@Override
	public synchronized void doAttack(L2Character target)
	{
	}
	
	@Override
	public void doCast(L2Skill skill)
	{
	}
	
	@Override
	public void sendInfo(L2PcInstance activeChar)
	{
		activeChar.sendPacket(new DoorInfo(this, activeChar));
		activeChar.sendPacket(new DoorStatusUpdate(this, activeChar));
	}
	
	@Override
	public int getGeoX()
	{
		return getTemplate().getGeoX();
	}
	
	@Override
	public int getGeoY()
	{
		return getTemplate().getGeoY();
	}
	
	@Override
	public int getGeoZ()
	{
		return getTemplate().getGeoZ();
	}
	
	@Override
	public int getHeight()
	{
		return (int) getTemplate().getCollisionHeight();
	}
	
	@Override
	public byte[][] getObjectGeoData()
	{
		return getTemplate().getGeoData();
	}
	
	@Override
	public double getCollisionHeight()
	{
		return getTemplate().getCollisionHeight() / 2;
	}
}