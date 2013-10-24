package net.xcine.gameserver.skills.effects;

import net.xcine.gameserver.model.L2Character;
import net.xcine.gameserver.model.L2Effect;
import net.xcine.gameserver.model.actor.instance.L2NpcInstance;
import net.xcine.gameserver.skills.Env;

public class EffectGrow extends L2Effect
{

	public EffectGrow(Env env, EffectTemplate template)
	{
		super(env, template);
	}

	@Override
	public EffectType getEffectType()
	{
		return EffectType.BUFF;
	}

	@Override
	public void onStart()
	{
		if(getEffected() instanceof L2NpcInstance)
		{
			L2NpcInstance npc = (L2NpcInstance) getEffected();
			npc.setCollisionHeight((int) (npc.getCollisionHeight() * 1.24));
			npc.setCollisionRadius((int) (npc.getCollisionRadius() * 1.19));

			getEffected().startAbnormalEffect(L2Character.ABNORMAL_EFFECT_GROW);
		}
	}

	@Override
	public boolean onActionTime()
	{
		if(getEffected() instanceof L2NpcInstance)
		{
			L2NpcInstance npc = (L2NpcInstance) getEffected();
			npc.setCollisionHeight(npc.getTemplate().collisionHeight);
			npc.setCollisionRadius(npc.getTemplate().collisionRadius);

			getEffected().stopAbnormalEffect(L2Character.ABNORMAL_EFFECT_GROW);
		}
		return false;
	}

	@Override
	public void onExit()
	{
		if(getEffected() instanceof L2NpcInstance)
		{
			L2NpcInstance npc = (L2NpcInstance) getEffected();
			npc.setCollisionHeight(npc.getTemplate().collisionHeight);
			npc.setCollisionRadius(npc.getTemplate().collisionRadius);

			getEffected().stopAbnormalEffect(L2Character.ABNORMAL_EFFECT_GROW);
		}
	}
}
