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
public class Triangle3D extends Triangle
{
	// min and max Z coorinates
	private final int _minZ;
	private final int _maxZ;
	
	/**
	 * Triangle constructor.
	 * @param A : Point A of the triangle.
	 * @param B : Point B of the triangle.
	 * @param C : Point C of the triangle.
	 */
	public Triangle3D(int[] A, int[] B, int[] C)
	{
		super(A, B, C);
		
		_minZ = Math.min(A[2], Math.min(B[2], C[2]));
		_maxZ = Math.max(A[2], Math.max(B[2], C[2]));
	}
	
	@Override
	public double getArea()
	{
		// returns size
		return Math.abs(_BAx * _CAy - _CAx * _BAy) / 2;
	}
	
	@Override
	public double getVolume()
	{
		// returns size
		return Math.abs(_BAx * _CAy - _CAx * _BAy) / 2 * (_maxZ - _minZ);
	}
	
	@Override
	public final boolean isInside(int x, int y, int z)
	{
		if (z < _minZ || z > _maxZ)
			return false;
		
		// method parameters must be LONG, since whole calculations must be done in LONG...we are doing really big numbers
		final long dx = x - _Ax;
		final long dy = y - _Ay;
		
		final boolean a = (0 - dx) * (_BAy - 0) - (_BAx - 0) * (0 - dy) > 0;
		final boolean b = (_BAx - dx) * (_CAy - _BAy) - (_CAx - _BAx) * (_BAy - dy) > 0;
		final boolean c = (_CAx - dx) * (0 - _CAy) - (0 - _CAx) * (_CAy - dy) > 0;
		
		return a == b && b == c;
	}
	
	@Override
	public final Location getRandomLocation()
	{
		// get relative length of AB and AC vectors
		double ba = Math.random();
		double ca = Math.random();
		
		// adjust length if too long
		if (ba + ca > 1)
		{
			ba = 1 - ba;
			ca = 1 - ca;
		}
		
		// calc coords (take A, add AB and AC)
		final int x = _Ax + (int) (ba * _BAx + ca * _CAx);
		final int y = _Ay + (int) (ba * _BAy + ca * _CAy);
		
		// return
		return new Location(x, y, (_minZ + _maxZ) / 2);
	}
}
