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
package net.sf.l2j.gameserver.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import net.sf.l2j.gameserver.ai.CtrlIntention;
import net.sf.l2j.gameserver.model.actor.L2Attackable;
import net.sf.l2j.gameserver.model.actor.L2Character;
import net.sf.l2j.gameserver.model.actor.L2Npc;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.zone.L2ZoneType;
import net.sf.l2j.gameserver.model.zone.type.L2DerbyTrackZone;
import net.sf.l2j.gameserver.model.zone.type.L2PeaceZone;
import net.sf.l2j.gameserver.model.zone.type.L2TownZone;

public final class WorldRegion
{
	private final Map<Integer, L2Object> _objects = new ConcurrentHashMap<>();
	
	private final List<WorldRegion> _surroundingRegions = new ArrayList<>();
	private final List<L2ZoneType> _zones = new ArrayList<>();
	
	private final int _tileX;
	private final int _tileY;
	
	private boolean _active;
	private AtomicInteger _playersCount = new AtomicInteger();
	
	public WorldRegion(int x, int y)
	{
		_tileX = x;
		_tileY = y;
	}
	
	@Override
	public String toString()
	{
		return "WorldRegion " + _tileX + "_" + _tileY + ", _active=" + _active + ", _playersCount=" + _playersCount.get() + "]";
	}
	
	public Collection<L2Object> getObjects()
	{
		return _objects.values();
	}
	
	public void addSurroundingRegion(WorldRegion region)
	{
		_surroundingRegions.add(region);
	}
	
	public List<WorldRegion> getSurroundingRegions()
	{
		return _surroundingRegions;
	}
	
	public List<L2ZoneType> getZones()
	{
		return _zones;
	}
	
	public void addZone(L2ZoneType zone)
	{
		_zones.add(zone);
	}
	
	public void removeZone(L2ZoneType zone)
	{
		_zones.remove(zone);
	}
	
	public void revalidateZones(L2Character character)
	{
		// Do NOT update the world region while the character is still in the process of teleporting
		if (character.isTeleporting())
			return;
		
		_zones.forEach(z -> z.revalidateInZone(character));
	}
	
	public void removeFromZones(L2Character character)
	{
		_zones.forEach(z -> z.removeCharacter(character));
	}
	
	public boolean containsZone(int zoneId)
	{
		for (L2ZoneType z : _zones)
		{
			if (z.getId() == zoneId)
				return true;
		}
		return false;
	}
	
	public boolean checkEffectRangeInsidePeaceZone(L2Skill skill, final int x, final int y, final int z)
	{
		final int range = skill.getEffectRange();
		final int up = y + range;
		final int down = y - range;
		final int left = x + range;
		final int right = x - range;
		
		for (L2ZoneType e : _zones)
		{
			if ((e instanceof L2TownZone && ((L2TownZone) e).isPeaceZone()) || e instanceof L2DerbyTrackZone || e instanceof L2PeaceZone)
			{
				if (e.isInsideZone(x, up, z))
					return false;
				
				if (e.isInsideZone(x, down, z))
					return false;
				
				if (e.isInsideZone(left, y, z))
					return false;
				
				if (e.isInsideZone(right, y, z))
					return false;
				
				if (e.isInsideZone(x, y, z))
					return false;
			}
		}
		return true;
	}
	
	public void onDeath(L2Character character)
	{
		_zones.stream().filter(z -> z.isCharacterInZone(character)).forEach(z -> z.onDieInside(character));
	}
	
	public void onRevive(L2Character character)
	{
		_zones.stream().filter(z -> z.isCharacterInZone(character)).forEach(z -> z.onReviveInside(character));
	}
	
	public boolean isActive()
	{
		return _active;
	}
	
	public int getPlayersCount()
	{
		return _playersCount.get();
	}
	
	/**
	 * Check if neighbors (including self) aren't inhabited.
	 * @return true if the above condition is met.
	 */
	public boolean isEmptyNeighborhood()
	{
		for (WorldRegion neighbor : _surroundingRegions)
		{
			if (neighbor.getPlayersCount() != 0)
				return false;
		}
		return true;
	}
	
	/**
	 * This function turns this region's AI on or off.
	 * @param value : if true, activate hp/mp regen and random animation. If false, clean aggro/attack list, set objects on IDLE and drop their AI tasks.
	 */
	public void setActive(boolean value)
	{
		if (_active == value)
			return;
		
		_active = value;
		
		if (!value)
		{
			for (L2Object o : _objects.values())
			{
				if (o instanceof L2Attackable)
				{
					L2Attackable mob = (L2Attackable) o;
					
					// Set target to null and cancel Attack or Cast
					mob.setTarget(null);
					
					// Stop movement
					mob.stopMove(null);
					
					// Stop all active skills effects in progress on the L2Character
					mob.stopAllEffects();
					
					mob.getAggroList().clear();
					mob.getAttackByList().clear();
					
					// stop the ai tasks
					if (mob.hasAI())
					{
						mob.getAI().setIntention(CtrlIntention.IDLE);
						mob.getAI().stopAITask();
					}
				}
			}
		}
		else
		{
			for (L2Object o : _objects.values())
			{
				if (o instanceof L2Attackable)
					((L2Attackable) o).getStatus().startHpMpRegeneration();
				else if (o instanceof L2Npc)
					((L2Npc) o).startRandomAnimationTimer();
			}
		}
	}
	
	/**
	 * Put the given object into WorldRegion objects map. If it's a player, increment the counter (used for region activation/desactivation).
	 * @param object : The object to register into this region.
	 */
	public void addVisibleObject(L2Object object)
	{
		if (object == null)
			return;
		
		_objects.put(object.getObjectId(), object);
		
		if (object instanceof L2PcInstance)
			_playersCount.incrementAndGet();
	}
	
	/**
	 * Remove the given object from WorldRegion objects map. If it's a player, decrement the counter (used for region activation/desactivation).
	 * @param object : The object to remove from this region.
	 */
	public void removeVisibleObject(L2Object object)
	{
		if (object == null)
			return;
		
		_objects.remove(object.getObjectId());
		
		if (object instanceof L2PcInstance)
			_playersCount.decrementAndGet();
	}
}