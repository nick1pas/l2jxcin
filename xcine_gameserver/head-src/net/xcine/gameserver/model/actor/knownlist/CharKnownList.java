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
package net.xcine.gameserver.model.actor.knownlist;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.xcine.gameserver.model.L2Object;
import net.xcine.gameserver.model.actor.L2Character;
import net.xcine.gameserver.model.actor.instance.L2PcInstance;
import net.xcine.gameserver.util.Util;

public class CharKnownList extends ObjectKnownList
{
	protected final Map<Integer, L2PcInstance> _knownPlayers = new ConcurrentHashMap<>();
	protected final Map<Integer, Integer> _knownRelations = new ConcurrentHashMap<>();
	
	public CharKnownList(L2Character activeChar)
	{
		super(activeChar);
	}
	
	@Override
	public boolean addKnownObject(L2Object object)
	{
		if (!super.addKnownObject(object))
			return false;
		
		if (object instanceof L2PcInstance)
		{
			_knownPlayers.put(object.getObjectId(), (L2PcInstance) object);
			_knownRelations.put(object.getObjectId(), -1);
		}
		
		return true;
	}
	
	/**
	 * @param player The L2PcInstance to search in _knownPlayer
	 * @return True if the L2PcInstance is in _knownPlayer of the L2Character.
	 */
	public final boolean knowsThePlayer(L2PcInstance player)
	{
		return getActiveChar() == player || _knownPlayers.containsKey(player.getObjectId());
	}
	
	/** Remove all L2Object from _knownObjects and _knownPlayer of the L2Character then cancel Attak or Cast and notify AI. */
	@Override
	public final void removeAllKnownObjects()
	{
		super.removeAllKnownObjects();
		
		_knownPlayers.clear();
		_knownRelations.clear();
		
		// Set _target of the L2Character to null
		getActiveChar().setTarget(null);
		
		// Cancel AI Task
		if (getActiveChar().hasAI())
			getActiveChar().setAI(null);
	}
	
	@Override
	protected boolean removeKnownObject(L2Object object, boolean forget)
	{
		if (!super.removeKnownObject(object, forget))
			return false;
		
		if (!forget) // on forget objects removed by iterator
		{
			if (object instanceof L2PcInstance)
			{
				_knownPlayers.remove(object.getObjectId());
				_knownRelations.remove(object.getObjectId());
			}
		}
		
		// If object is targeted by the L2Character, cancel Attack or Cast
		if (object == getActiveChar().getTarget())
			getActiveChar().setTarget(null);
		
		return true;
	}
	
	@Override
	public void forgetObjects(boolean fullCheck)
	{
		if (!fullCheck)
		{
			final Iterator<L2PcInstance> pIter = _knownPlayers.values().iterator();
			while (pIter.hasNext())
			{
				L2PcInstance player = pIter.next();
				if (player == null)
					pIter.remove();
				else if (!player.isVisible() || !Util.checkIfInShortRadius(getDistanceToForgetObject(player), _activeObject, player, true))
				{
					pIter.remove();
					removeKnownObject(player, true);
					_knownRelations.remove(player.getObjectId());
					_knownObjects.remove(player.getObjectId());
				}
			}
			
			return;
		}
		
		// Go through knownObjects
		final Iterator<L2Object> oIter = _knownObjects.values().iterator();
		while (oIter.hasNext())
		{
			L2Object object = oIter.next();
			if (object == null)
				oIter.remove();
			else if (!object.isVisible() || !Util.checkIfInShortRadius(getDistanceToForgetObject(object), _activeObject, object, true))
			{
				oIter.remove();
				removeKnownObject(object, true);
				
				if (object instanceof L2PcInstance)
				{
					_knownPlayers.remove(object.getObjectId());
					_knownRelations.remove(object.getObjectId());
				}
			}
		}
	}
	
	public L2Character getActiveChar()
	{
		return (L2Character) super.getActiveObject();
	}
	
	public final Collection<L2PcInstance> getKnownPlayersInRadius(int radius)
	{
		List<L2PcInstance> result = new ArrayList<>();
		
		for (L2PcInstance player : _knownPlayers.values())
		{
			if (Util.checkIfInRange(radius, getActiveChar(), player, true))
				result.add(player);
		}
		return result;
	}
	
	public final Collection<L2PcInstance> getKnownPlayers()
	{
		return _knownPlayers.values();
	}
	
	public final Collection<Integer> getKnownRelations()
	{
		return _knownRelations.values();
	}
	
	public final Map<Integer, L2PcInstance> getKnownPlayersMap()
	{
		return _knownPlayers;
	}
	
	public final Map<Integer, Integer> getKnownRelationsMap()
	{
		return _knownRelations;
	}
}