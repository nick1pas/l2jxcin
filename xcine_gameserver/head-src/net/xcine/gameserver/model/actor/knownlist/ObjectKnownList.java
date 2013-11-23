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
import net.xcine.gameserver.model.actor.L2Playable;
import net.xcine.gameserver.util.Util;

public class ObjectKnownList
{
	protected final L2Object _activeObject;
	protected final Map<Integer, L2Object> _knownObjects = new ConcurrentHashMap<>();
	
	public ObjectKnownList(L2Object activeObject)
	{
		_activeObject = activeObject;
	}
	
	public boolean addKnownObject(L2Object object)
	{
		if (object == null)
			return false;
		
		// Check if already know object
		if (knowsObject(object))
			return false;
		
		// Check if object is not inside distance to watch object
		if (!Util.checkIfInShortRadius(getDistanceToWatchObject(object), _activeObject, object, true))
			return false;
		
		return _knownObjects.put(object.getObjectId(), object) == null;
	}
	
	public final boolean knowsObject(L2Object object)
	{
		if (object == null)
			return false;
		
		return _activeObject == object || _knownObjects.containsKey(object.getObjectId());
	}
	
	/** Remove all L2Object from _knownObjects */
	public void removeAllKnownObjects()
	{
		_knownObjects.clear();
	}
	
	public final boolean removeKnownObject(L2Object object)
	{
		return removeKnownObject(object, false);
	}
	
	protected boolean removeKnownObject(L2Object object, boolean forget)
	{
		if (object == null)
			return false;
		
		if (forget) // on forget objects removed from list by iterator
			return true;
		
		return _knownObjects.remove(object.getObjectId()) != null;
	}
	
	// Remove invisible and too far L2Object from _knowObject and if necessary from _knownPlayers of the L2Character
	public void forgetObjects(boolean fullCheck)
	{
		// Go through knownObjects
		final Iterator<L2Object> oIter = _knownObjects.values().iterator();
		
		while (oIter.hasNext())
		{
			final L2Object object = oIter.next();
			if (object == null)
			{
				oIter.remove();
				continue;
			}
			
			if (!fullCheck && !(object instanceof L2Playable))
				continue;
			
			// Remove all objects invisible or too far
			if (!object.isVisible() || !Util.checkIfInShortRadius(getDistanceToForgetObject(object), _activeObject, object, true))
			{
				oIter.remove();
				removeKnownObject(object, true);
			}
		}
	}
	
	public L2Object getActiveObject()
	{
		return _activeObject;
	}
	
	public int getDistanceToForgetObject(L2Object object)
	{
		return 0;
	}
	
	public int getDistanceToWatchObject(L2Object object)
	{
		return 0;
	}
	
	/**
	 * @return the _knownObjects containing all L2Object known by the L2Character.
	 */
	public final Collection<L2Object> getKnownObjects()
	{
		return _knownObjects.values();
	}
	
	@SuppressWarnings("unchecked")
	public final <A> Collection<A> getKnownType(Class<A> type)
	{
		List<A> result = new ArrayList<>();
		
		for (L2Object obj : _knownObjects.values())
		{
			if (type.isAssignableFrom(obj.getClass()))
				result.add((A) obj);
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public final <A> Collection<A> getKnownTypeInRadius(Class<A> type, int radius)
	{
		List<A> result = new ArrayList<>();
		
		for (L2Object obj : _knownObjects.values())
		{
			if (type.isAssignableFrom(obj.getClass()) && Util.checkIfInRange(radius, getActiveObject(), obj, true))
				result.add((A) obj);
		}
		return result;
	}
}