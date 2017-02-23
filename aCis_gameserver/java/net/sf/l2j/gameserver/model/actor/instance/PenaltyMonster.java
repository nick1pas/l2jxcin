package net.sf.l2j.gameserver.model.actor.instance;

import net.sf.l2j.commons.random.Rnd;

import net.sf.l2j.gameserver.ai.CtrlEvent;
import net.sf.l2j.gameserver.model.actor.Character;
import net.sf.l2j.gameserver.model.actor.template.NpcTemplate;

public class PenaltyMonster extends Monster
{
	private Player _ptk;
	
	public PenaltyMonster(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}
	
	@Override
	public Character getMostHated()
	{
		if (_ptk != null)
			return _ptk; // always attack only one person
			
		return super.getMostHated();
	}
	
	public void setPlayerToKill(Player ptk)
	{
		if (Rnd.get(100) <= 80)
			broadcastNpcSay("Your bait was too delicious! Now, I will kill you!");
		
		_ptk = ptk;
		
		getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, _ptk, Rnd.get(1, 100));
	}
	
	@Override
	public boolean doDie(Character killer)
	{
		if (!super.doDie(killer))
			return false;
		
		if (Rnd.get(100) <= 75)
			broadcastNpcSay("I will tell fish not to take your bait!");
		
		return true;
	}
}