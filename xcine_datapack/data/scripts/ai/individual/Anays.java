package ai.individual;

import ai.AbstractNpcAI;

import net.xcine.gameserver.ai.CtrlIntention;
import net.xcine.gameserver.instancemanager.GrandBossManager;
import net.xcine.gameserver.model.actor.L2Attackable;
import net.xcine.gameserver.model.actor.L2Npc;
import net.xcine.gameserver.model.actor.instance.L2PcInstance;
import net.xcine.gameserver.model.quest.QuestEventType;
import net.xcine.gameserver.model.zone.type.L2BossZone;

public class Anays extends AbstractNpcAI
{
	private static final int ANAYS = 25517;
	private static L2BossZone _Zone;

	public Anays(String name, String descr)
	{
		super(name, descr);

		//setInitialState(new State("Start", this));
		
		_Zone = GrandBossManager.getZoneByXYZ(113000, -76000, 200);
		addEventId(ANAYS, QuestEventType.ON_ATTACK);
		addEventId(ANAYS, QuestEventType.ON_SPAWN);
		addEventId(ANAYS, QuestEventType.ON_AGGRO_RANGE_ENTER);
	}

	@Override
	public String onAttack(L2Npc npc, L2PcInstance attacker, int damage, boolean isPet)
	{
		if(npc.getNpcId() == ANAYS && !_Zone.isInsideZone(npc.getX(), npc.getY()))
		{
			((L2Attackable) npc).clearAggroList();
			npc.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
			npc.teleToLocation(113000, -76000, 200,0);
		}

		return super.onAttack(npc, attacker, damage, isPet);
	}

	@Override
	public String onSpawn(L2Npc npc)
	{
		if (npc instanceof L2Attackable)
	        	((L2Attackable)npc).seeThroughSilentMove(true);
					return super.onSpawn(npc);
	}

	@Override
	public String onAggroRangeEnter(L2Npc npc, L2PcInstance player, boolean isPet)
	{
		if(npc.getNpcId() == ANAYS && !npc.isInCombat() && npc.getTarget() == null)
		{
			npc.setTarget(player);
			npc.setIsRunning(true);
			((L2Attackable) npc).addDamageHate(player, 0, 999);
			npc.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, player);
		}
		else if(((L2Attackable)npc).getMostHated() == null)
		{
            return null;
		}

		return super.onAggroRangeEnter(npc, player, isPet);
	}
	
	public static void main(String[] args)
	{
		new Anays(Anays.class.getSimpleName(), "ai/individual");
	}

}