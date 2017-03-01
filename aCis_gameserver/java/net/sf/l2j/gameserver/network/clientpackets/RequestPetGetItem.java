package net.sf.l2j.gameserver.network.clientpackets;

import net.sf.l2j.gameserver.ai.CtrlIntention;
import net.sf.l2j.gameserver.model.L2Object;
import net.sf.l2j.gameserver.model.World;
import net.sf.l2j.gameserver.model.actor.instance.Pet;
import net.sf.l2j.gameserver.model.actor.instance.Player;

public final class RequestPetGetItem extends L2GameClientPacket
{
	private int _objectId;
	
	@Override
	protected void readImpl()
	{
		_objectId = readD();
	}
	
	@Override
	protected void runImpl()
	{
		final Player activeChar = getClient().getActiveChar();
		if (activeChar == null || !activeChar.hasPet())
			return;
		
		final L2Object item = World.getInstance().getObject(_objectId);
		if (item == null)
			return;
		
		final Pet pet = (Pet) activeChar.getPet();
		if (pet.isDead() || pet.isOutOfControl())
		{
			ActionF();
			return;
		}
		
		pet.getAI().setIntention(CtrlIntention.PICK_UP, item);
	}
}