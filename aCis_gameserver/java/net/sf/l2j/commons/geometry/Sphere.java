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

import net.sf.l2j.commons.random.Rnd;

import net.sf.l2j.gameserver.model.Location;

/**
 * @author Hasha
 */
public class Sphere extends Circle
{
	// sphere center Z coordinate
	private final int _z;
	
	/**
	 * Sphere constructor.
	 * @param x : Center X coordinate.
	 * @param y : Center Y coordinate.
	 * @param z : Center Z coordinate.
	 * @param r : Sphere radius.
	 */
	public Sphere(int x, int y, int z, int r)
	{
		super(x, y, r);
		
		_z = z;
	}
	
	@Override
	public final double getArea()
	{
		return 4 * Math.PI * _r * _r;
	}
	
	@Override
	public final double getVolume()
	{
		return (4 * Math.PI * _r * _r * _r) / 3;
	}
	
	@Override
	public final boolean isInside(int x, int y, int z)
	{
		final int dx = x - _x;
		final int dy = y - _y;
		final int dz = z - _z;
		
		return (dx * dx + dy * dy + dz * dz) <= _r * _r;
	}
	
	@Override
	public final Location getRandomLocation()
	{
		// get uniform distance and angles
		final double r = Math.cbrt(Rnd.nextDouble()) * _r;
		final double phi = Rnd.nextDouble() * 2 * Math.PI;
		final double theta = Math.acos(2 * Rnd.nextDouble() - 1);
		
		// calculate coordinates
		final int x = (int) (_x + (r * Math.cos(phi) * Math.sin(theta)));
		final int y = (int) (_y + (r * Math.sin(phi) * Math.sin(theta)));
		final int z = (int) (_z + (r * Math.cos(theta)));
		
		// return
		return new Location(x, y, z);
	}
}
