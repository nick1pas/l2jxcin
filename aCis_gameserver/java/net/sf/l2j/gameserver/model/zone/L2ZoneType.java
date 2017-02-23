package net.sf.l2j.gameserver.model.zone;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import net.sf.l2j.gameserver.model.L2Object;
import net.sf.l2j.gameserver.model.actor.Character;
import net.sf.l2j.gameserver.model.actor.instance.Player;
import net.sf.l2j.gameserver.network.serverpackets.L2GameServerPacket;
import net.sf.l2j.gameserver.scripting.EventType;
import net.sf.l2j.gameserver.scripting.Quest;

/**
 * Abstract base class for any zone type.
 */
public abstract class L2ZoneType
{
	protected static final Logger _log = Logger.getLogger(L2ZoneType.class.getName());
	
	private final int _id;
	protected final Map<Integer, Character> _characterList = new ConcurrentHashMap<>();
	
	private Map<EventType, List<Quest>> _questEvents;
	private L2ZoneForm _zone;
	
	protected L2ZoneType(int id)
	{
		_id = id;	
	}
 	
	protected abstract void onEnter(Character character);
	
	protected abstract void onExit(Character character);
	
	public abstract void onDieInside(Character character);
	
	public abstract void onReviveInside(Character character);
	
	@Override
	public String toString()
	{
		return getClass().getSimpleName() + "[" + _id + "]";
	}

	/**
	 * @return Returns the id.
	 */
	public int getId()
	{
		return _id;
	}
	
	/**
	 * @return this zone form.
	 */
	public L2ZoneForm getZone()
	{
		return _zone;
	}

	/**
	 * Set the zone for this L2ZoneType Instance
	 * @param zone
	 */
	public void setZone(L2ZoneForm zone)
	{
		if (_zone != null)
			throw new IllegalStateException("Zone already set");
		_zone = zone;
	}
	
	/**
	 * @param x
	 * @param y
	 * @return true if the given coordinates are within zone's plane
	 */
	public boolean isInsideZone(int x, int y)
	{
		return _zone.isInsideZone(x, y, _zone.getHighZ());
	}
	
	/**
	 * @param x
	 * @param y
	 * @param z
	 * @return true if the given coordinates are within the zone
	 */
	public boolean isInsideZone(int x, int y, int z)
	{
		return _zone.isInsideZone(x, y, z);
	}
	
	/**
	 * @param object check object's X/Y positions.
	 * @return true if the given object is inside the zone.
	 */
	public boolean isInsideZone(L2Object object)
	{
		return isInsideZone(object.getX(), object.getY(), object.getZ());
	}
	
	public double getDistanceToZone(int x, int y)
	{
		return getZone().getDistanceToZone(x, y);
	}
	
	public double getDistanceToZone(L2Object object)
	{
		return getZone().getDistanceToZone(object.getX(), object.getY());
	}
	
	public void revalidateInZone(Character character)
	{
		// If the character can't be affected by this zone return
		if (!isAffected(character))
			return;
		
		// If the object is inside the zone...
		if (isInsideZone(character))
		{
			// Was the character not yet inside this zone?
			if (!_characterList.containsKey(character.getObjectId()))
			{
				// Notify to scripts.
				final List<Quest> quests = getQuestByEvent(EventType.ON_ENTER_ZONE);
				if (quests != null)
				{
					for (Quest quest : quests)
						quest.notifyEnterZone(character, this);
				}
				// Register player.
				_characterList.put(character.getObjectId(), character);
				
				// Notify Zone implementation.
				onEnter(character);
			}
		}
		else
			removeCharacter(character);
	}
	
	/**
	 * Removes a character from the zone.
	 * @param character : The character to remove.
	 */
	public void removeCharacter(Character character)
	{
		// Was the character inside this zone?
		if (_characterList.containsKey(character.getObjectId()))
		{
			// Notify to scripts.
			final List<Quest> quests = getQuestByEvent(EventType.ON_EXIT_ZONE);
			if (quests != null)
			{
				for (Quest quest : quests)
					quest.notifyExitZone(character, this);
			}
						
			// Unregister player.
			_characterList.remove(character.getObjectId());
			
			// Notify Zone implementation.
			onExit(character);
		}
	}
	
	/**
	 * @param character The character to test.
	 * @return True if the character is in the zone.
	 */
	public boolean isCharacterInZone(Character character)
	{
		return _characterList.containsKey(character.getObjectId());
	}
	
	public Collection<Character> getCharactersInside()
	{
		return _characterList.values();
	}
	
	/**
	 * @param <A> : The generic type.
	 * @param type : The instance type to filter.
	 * @return a List of filtered type characters within this zone. Generate a temporary List.
	 */
	@SuppressWarnings("unchecked")
	public final <A> List<A> getKnownTypeInside(Class<A> type)
	{
		List<A> result = new ArrayList<>();
		
		for (L2Object obj : _characterList.values())
		{
			if (type.isAssignableFrom(obj.getClass()))
				result.add((A) obj);
		}
		return result;
	}
	
	public void addQuestEvent(EventType eventType, Quest quest)
	{
		if (_questEvents == null)
			_questEvents = new HashMap<>();
		
		List<Quest> eventList = _questEvents.get(eventType);
		if (eventList == null)
		{
			eventList = new ArrayList<>();
			eventList.add(quest);
			_questEvents.put(eventType, eventList);
		}
		else
		{
			eventList.remove(quest);
			eventList.add(quest);
		}
	}
	
	public List<Quest> getQuestByEvent(EventType EventType)
	{
		return (_questEvents == null) ? null : _questEvents.get(EventType);
	}
	
	/**
	 * Broadcasts packet to all players inside the zone
	 * @param packet The packet to use.
	 */
	public void broadcastPacket(L2GameServerPacket packet)
	{
		for (Character character : _characterList.values())
		{
			if (character instanceof Player)
				character.sendPacket(packet);
		}
	}
	
	/**
	 * Setup new parameters for this zone
	 * @param name parameter name.
	 * @param value new parameter value.
	 */
	public void setParameter(String name, String value)
	{
		_log.info(getClass().getSimpleName() + ": Unknown parameter - " + name + " in zone: " + getId());
	}
 	
	/**
	 * @param character The character to test.
	 * @return True if the given character is affected by this zone.
	 */
	protected boolean isAffected(Character character)
	{
		// Overriden in children classes.
		return true;
	}

	public void visualizeZone(int z)
	{
		getZone().visualizeZone(_id, z);
	}
}