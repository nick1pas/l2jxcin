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