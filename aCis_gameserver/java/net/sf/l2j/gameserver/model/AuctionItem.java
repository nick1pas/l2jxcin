package net.sf.l2j.gameserver.model;

/**
 * @author Anarchy
 */
public class AuctionItem
{
	private int auctionId, ownerId, itemId, count, enchant, costId, costCount;
	
	public AuctionItem(int auctionId, int ownerId, int itemId, int count, int enchant, int costId, int costCount)
	{
		this.auctionId = auctionId;
		this.ownerId = ownerId;
		this.itemId = itemId;
		this.count = count;
		this.enchant = enchant;
		this.costId = costId;
		this.costCount = costCount;
	}
	
	public int getAuctionId()
	{
		return auctionId;
	}
	
	public int getOwnerId()
	{
		return ownerId;
	}
	
	public int getItemId()
	{
		return itemId;
	}
	
	public int getCount()
	{
		return count;
	}
	
	public int getEnchant()
	{
		return enchant;
	}
	
	public int getCostId()
	{
		return costId;
	}
	
	public int getCostCount()
	{
		return costCount;
	}
}