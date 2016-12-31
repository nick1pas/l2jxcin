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
package net.sf.l2j.commons.geometry;

import net.sf.l2j.gameserver.model.Location;

/**
 * @author Hasha
 */
public class Cube extends Square
{
	// cube origin coordinates
	private final int _z;
	
	/**
	 * Cube constructor.
	 * @param x : Bottom left lower X coordinate.
	 * @param y : Bottom left lower Y coordinate.
	 * @param z : Bottom left lower Z coordinate.
	 * @param a : Size of cube side.
	 */
	public Cube(int x, int y, int z, int a)
	{
		super(x, y, a);
		
		_z = z;
	}
	
	@Override
	public double getArea()
	{
		return 6 * _a * _a;
	}
	
	@Override
	public double getVolume()
	{
		return _a * _a * _a;
	}
	
	@Override
	public boolean isInside(int x, int y, int z)
	{
		int dz = z - _z;
		if (dz < 0 || dz > _a)
			return false;
		
		int dx = x - _x;
		if (dx < 0 || dx > _a)
			return false;
		
		int dy = y - _y;
		if (dy < 0 || _y > _a)
			return false;
		
		return true;
	}
	
	@Override
	public Location getRandomLocation()
	{
		final int x = (int) (_x + Math.random() * _a);
		final int y = (int) (_y + Math.random() * _a);
		final int z = (int) (_z + Math.random() * _a);
		
		return new Location(x, y, z);
	}
}
