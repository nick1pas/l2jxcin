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
package net.sf.l2j.gameserver.templates;

/**
 * @author rapfersan92
 */
public class L2Aio
{
	private int _id;
	private long _duration;
	private int _feeId;
	private int _feeVal;
	
	public L2Aio(StatsSet set)
	{
		_id = set.getInteger("id");
		_duration = set.getLong("duration");
		_feeId = set.getInteger("feeId");
		_feeVal = set.getInteger("feeVal");
	}
	
	public int getId()
	{
		return _id;
	}
	
	public long getDuration()
	{
		return _duration;
	}
	
	public int getFeeId()
	{
		return _feeId;
	}
	
	public int getFeeVal()
	{
		return _feeVal;
	}
}