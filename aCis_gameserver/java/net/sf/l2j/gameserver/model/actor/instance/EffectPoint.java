package net.sf.l2j.gameserver.model.actor.instance;

import net.sf.l2j.gameserver.model.actor.Character;
import net.sf.l2j.gameserver.model.actor.Npc;
import net.sf.l2j.gameserver.model.actor.template.NpcTemplate;

public class EffectPoint extends Npc
{
	private final Player _owner;
	
	public EffectPoint(int objectId, NpcTemplate template, Character owner)
	{
		super(objectId, template);
		_owner = owner == null ? null : owner.getActingPlayer();
	}
	
	@Override
	public Player getActingPlayer()
	{
		return _owner;
	}
	
	@Override
	public void onAction(Player player)
	{
		player.ActionF();
	}
	
	@Override
	public void onActionShift(Player player)
	{
		player.ActionF();
	}
}