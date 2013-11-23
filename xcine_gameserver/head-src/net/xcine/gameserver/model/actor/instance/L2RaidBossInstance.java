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

import net.xcine.Config;
import net.xcine.gameserver.ThreadPoolManager;
import net.xcine.gameserver.instancemanager.RaidBossPointsManager;
import net.xcine.gameserver.instancemanager.RaidBossSpawnManager;
import net.xcine.gameserver.instancemanager.RaidBossSpawnManager.StatusEnum;
import net.xcine.gameserver.model.L2Spawn;
import net.xcine.gameserver.model.actor.L2Character;
import net.xcine.gameserver.model.entity.Hero;
import net.xcine.gameserver.network.SystemMessageId;
import net.xcine.gameserver.network.serverpackets.PlaySound;
import net.xcine.gameserver.network.serverpackets.SystemMessage;
import net.xcine.gameserver.templates.chars.L2NpcTemplate;
import net.xcine.util.Rnd;

/**
 * This class manages all RaidBoss. In a group mob, there are one master called RaidBoss and several slaves called Minions.
 */
public class L2RaidBossInstance extends L2MonsterInstance
{
	private StatusEnum _raidStatus;
	protected ScheduledFuture<?> _maintenanceTask;
	
	/**
	 * Constructor of L2RaidBossInstance (use L2Character and L2NpcInstance constructor).
	 * <ul>
	 * <li>Call the L2Character constructor to set the _template of the L2RaidBossInstance (copy skills from template to object and link _calculators to NPC_STD_CALCULATOR)</li>
	 * <li>Set the name of the L2RaidBossInstance</li>
	 * <li>Create a RandomAnimation Task that will be launched after the calculated delay if the server allow it</li>
	 * </ul>
	 * @param objectId Identifier of the object to initialized
	 * @param template L2NpcTemplate to apply to the NPC
	 */
	public L2RaidBossInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
		setIsRaid(true);
	}
	
	@Override
	public void onSpawn()
	{
		setIsNoRndWalk(true);
		super.onSpawn();
	}
	
	@Override
	public boolean doDie(L2Character killer)
	{
		if (!super.doDie(killer))
			return false;
		
		if (_maintenanceTask != null)
		{
			_maintenanceTask.cancel(false);
			_maintenanceTask = null;
		}
		
		if (killer != null)
		{
			final L2PcInstance player = killer.getActingPlayer();
			if (player != null)
			{
				broadcastPacket(SystemMessage.getSystemMessage(SystemMessageId.RAID_WAS_SUCCESSFUL));
				broadcastPacket(new PlaySound("systemmsg_e.1209"));
				
				if (player.isInParty())
				{
					for (L2PcInstance member : player.getParty().getPartyMembers())
					{
						RaidBossPointsManager.getInstance().addPoints(member, getNpcId(), (getLevel() / 2) + Rnd.get(-5, 5));
						if (member.isNoble())
							Hero.getInstance().setRBkilled(member.getObjectId(), getNpcId());
					}
				}
				else
				{
					RaidBossPointsManager.getInstance().addPoints(player, getNpcId(), (getLevel() / 2) + Rnd.get(-5, 5));
					if (player.isNoble())
						Hero.getInstance().setRBkilled(player.getObjectId(), getNpcId());
				}
			}
		}
		
		RaidBossSpawnManager.getInstance().updateStatus(this, true);
		return true;
	}
	
	@Override
	public void deleteMe()
	{
		if (_maintenanceTask != null)
		{
			_maintenanceTask.cancel(false);
			_maintenanceTask = null;
		}
		
		super.deleteMe();
	}
	
	/**
	 * Spawn minions.<br>
	 * Also if boss is too far from home location at the time of this check, teleport it to home.
	 */
	@Override
	protected void startMaintenanceTask()
	{
		super.startMaintenanceTask();
		
		_maintenanceTask = ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(new Runnable()
		{
			@Override
			public void run()
			{
				checkAndReturnToSpawn();
			}
		}, 60000, 30000);
	}
	
	protected void checkAndReturnToSpawn()
	{
		if (isDead() || isMovementDisabled())
			return;
		
		// Gordon does not have permanent spawn
		if (getNpcId() == 29095)
			return;
		
		final L2Spawn spawn = getSpawn();
		if (spawn == null)
			return;
		
		final int spawnX = spawn.getLocx();
		final int spawnY = spawn.getLocy();
		final int spawnZ = spawn.getLocz();
		
		if (!isInCombat() && !isMovementDisabled())
		{
			if (!isInsideRadius(spawnX, spawnY, spawnZ, Math.max(Config.MAX_DRIFT_RANGE, 200), true, false))
				teleToLocation(spawnX, spawnY, spawnZ, 0);
		}
	}
	
	public void setRaidStatus(StatusEnum status)
	{
		_raidStatus = status;
	}
	
	public StatusEnum getRaidStatus()
	{
		return _raidStatus;
	}
}