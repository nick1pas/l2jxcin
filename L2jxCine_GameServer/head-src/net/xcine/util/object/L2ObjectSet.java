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
package net.xcine.util.object;

import java.util.Iterator;

import net.xcine.Config;
import net.xcine.gameserver.model.L2Object;
import net.xcine.gameserver.model.L2Playable;

/**
 * This class ...
 * 
 * @author luisantonioa
 * @version $Revision: 1.2 $ $Date: 2004/06/27 08:12:59 $
 * @param <T> 
 */
public abstract class L2ObjectSet<T extends L2Object> implements Iterable<T>
{
	public static L2ObjectSet<L2Object> createL2ObjectSet()
	{
		switch(Config.SET_TYPE)
		{
			case WorldObjectSet:
				return new WorldObjectSet<>();
			default:
				return new L2ObjectHashSet<>();
		}
	}

	public static L2ObjectSet<L2Playable> createL2PlayerSet()
	{
		switch(Config.SET_TYPE)
		{
			case WorldObjectSet:
				return new WorldObjectSet<>();
			default:
				return new L2ObjectHashSet<>();
		}
	}

	public abstract int size();

	public abstract boolean isEmpty();

	public abstract void clear();

	public abstract void put(T obj);

	public abstract void remove(T obj);

	public abstract boolean contains(T obj);

	@Override
	public abstract Iterator<T> iterator();

}
