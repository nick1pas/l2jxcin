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
public class Triangle extends AShape
{
	// A point
	protected final int _Ax;
	protected final int _Ay;
	
	// BA vector coordinates
	protected final int _BAx;
	protected final int _BAy;
	
	// CA vector coordinates
	protected final int _CAx;
	protected final int _CAy;
	
	// size
	protected final int _size;
	
	/**
	 * Triangle constructor.
	 * @param A : Point A of the triangle.
	 * @param B : Point B of the triangle.
	 * @param C : Point C of the triangle.
	 */
	public Triangle(int[] A, int[] B, int[] C)
	{
		_Ax = A[0];
		_Ay = A[1];
		
		_BAx = B[0] - A[0];
		_BAy = B[1] - A[1];
		
		_CAx = C[0] - A[0];
		_CAy = C[1] - A[1];
		
		_size = Math.abs(_BAx * _CAy - _CAx * _BAy) / 2;
	}
	
	@Override
	public final int getSize()
	{
		return _size;
	}
	
	@Override
	public double getArea()
	{
		return _size;
	}
	
	@Override
	public double getVolume()
	{
		return 0;
	}
	
	@Override
	public final boolean isInside(int x, int y)
	{
		// method parameters must be LONG, since whole calculations must be done in LONG...we are doing really big numbers
		final long dx = x - _Ax;
		final long dy = y - _Ay;
		
		final boolean a = (0 - dx) * (_BAy - 0) - (_BAx - 0) * (0 - dy) >= 0;
		final boolean b = (_BAx - dx) * (_CAy - _BAy) - (_CAx - _BAx) * (_BAy - dy) >= 0;
		final boolean c = (_CAx - dx) * (0 - _CAy) - (0 - _CAx) * (_CAy - dy) >= 0;
		
		return a == b && b == c;
	}
	
	@Override
	public boolean isInside(int x, int y, int z)
	{
		// method parameters must be LONG, since whole calculations must be done in LONG...we are doing really big numbers
		final long dx = x - _Ax;
		final long dy = y - _Ay;
		
		final boolean a = (0 - dx) * (_BAy - 0) - (_BAx - 0) * (0 - dy) >= 0;
		final boolean b = (_BAx - dx) * (_CAy - _BAy) - (_CAx - _BAx) * (_BAy - dy) >= 0;
		final boolean c = (_CAx - dx) * (0 - _CAy) - (0 - _CAx) * (_CAy - dy) >= 0;
		
		return a == b && b == c;
	}
	
	@Override
	public Location getRandomLocation()
	{
		// get relative length of AB and AC vectors
		double ba = Rnd.nextDouble();
		double ca = Rnd.nextDouble();
		
		// adjust length if too long
		if (ba + ca > 1)
		{
			ba = 1 - ba;
			ca = 1 - ca;
		}
		
		// calculate coordinates (take A, add AB and AC)
		final int x = _Ax + (int) (ba * _BAx + ca * _CAx);
		final int y = _Ay + (int) (ba * _BAy + ca * _CAy);
		
		// return
		return new Location(x, y, 0);
	}
}