package ai.group;

import ai.AbstractNpcAI;

import java.util.Collection;

import net.xcine.gameserver.GeoData;
import net.xcine.gameserver.ai.CtrlIntention;
import net.xcine.gameserver.model.L2Object;
import net.xcine.gameserver.model.actor.L2Attackable;
import net.xcine.gameserver.model.actor.L2Character;
import net.xcine.gameserver.model.actor.L2Npc;
import net.xcine.gameserver.model.actor.instance.L2MonsterInstance;
import net.xcine.gameserver.model.actor.instance.L2PcInstance;
import net.xcine.gameserver.network.clientpackets.Say2;
import net.xcine.gameserver.network.serverpackets.CreatureSay;

public class GiantScouts extends AbstractNpcAI
{
	final private static int SCOUTS[] = { 20651, 20652 };

	public GiantScouts(String name, String descr)
	{
		super(name, descr);
		for(int id : SCOUTS)
		{
			addAggroRangeEnterId(id);
		}
	}

	@Override
	public String onAggroRangeEnter(L2Npc npc, L2PcInstance player, boolean isPet)
	{
		final L2Character target = isPet ? player.getPet() : player;
		if(target != null && GeoData.getInstance().canSeeTarget(npc, target) && !player.getAppearance().getInvisible())
		{
			if(!npc.isInCombat() && npc.getTarget() == null)
			{
				npc.broadcastPacket(new CreatureSay(npc.getObjectId(), Say2.SHOUT, npc.getName(), "Oh Giants, an intruder has been discovered."));
			}

			npc.setTarget(target);
			npc.setRunning();
			((L2Attackable) npc).addDamageHate(target, 0, 999);
			npc.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, target);

			final Collection<L2Object> objs = npc.getKnownList().getKnownObjects();
			for(L2Object obj : objs)
			{
				if(!(obj instanceof L2MonsterInstance))
				{
					continue;
				}

				final L2MonsterInstance monster = (L2MonsterInstance) obj;
				if((npc.getFactionId() != null && monster.getFactionId() != null) && monster.getFactionId().equals(npc.getFactionId()) && GeoData.getInstance().canSeeTarget(npc, monster))
				{
					monster.setTarget(target);
					monster.setRunning();
					monster.addDamageHate(target, 0, 999);
					monster.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, target);
				}
			}
		}

		return super.onAggroRangeEnter(npc, player, isPet);
	}
	
	public static void main(String[] args)
	{
		new GiantScouts(GiantScouts.class.getSimpleName(), "ai/group");
	}
}