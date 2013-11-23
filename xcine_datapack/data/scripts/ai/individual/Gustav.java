package ai.individual;

import ai.AbstractNpcAI;
import javolution.util.FastList;

import net.xcine.gameserver.model.actor.L2Npc;
import net.xcine.gameserver.model.actor.instance.L2PcInstance;
import net.xcine.gameserver.model.entity.DevastatedCastle;
import net.xcine.gameserver.model.quest.QuestEventType;

public class Gustav extends AbstractNpcAI
{
	private int GUSTAV = 35410;
	private int MESSENGER = 35420;
	private static FastList<String> CLAN_LEADERS = new FastList<String>();
	public Gustav(String name, String descr)
	{
		super(name, descr);
		
		//setInitialState(new State("Start", this));
		
		addEventId(MESSENGER, QuestEventType.QUEST_START);
		addEventId(MESSENGER, QuestEventType.ON_FIRST_TALK);
		addEventId(GUSTAV, QuestEventType.ON_ATTACK);
		addEventId(GUSTAV, QuestEventType.ON_KILL);
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		if(npc.getNpcId() == MESSENGER)
		{
			if(player != null && CLAN_LEADERS.contains(player.getName()))
			{
				return "<html><body>Messenger:<br>You already registered!</body></html>";
			}
			else
			{
				DevastatedCastle.getInstance();
				if(DevastatedCastle.Conditions(player))
				{
					CLAN_LEADERS.add(player.getName());
					return "<html><body>Messenger:<br>You have successful registered on a siege!</body></html>";
				}
				else
				{
					return "<html><body>Messenger:<br>Condition are not allow to do that!</body></html>";
				}
			}
		}

		return super.onTalk(npc, player);
	}

	@Override
	public String onAttack(L2Npc npc, L2PcInstance attacker, int damage, boolean isPet)
	{
		int npcId = npc.getNpcId();

		if(attacker != null && npcId == GUSTAV && CLAN_LEADERS.contains(attacker.getName()));
		{
			DevastatedCastle.getInstance().addSiegeDamage(attacker.getClan(),damage);
		}

		return super.onAttack(npc, attacker, damage, isPet);
	}

	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isPet)
	{
		DevastatedCastle.getInstance().SiegeFinish();
			return super.onKill(npc, killer, isPet);
	}
	
	public static void main(String[] args)
	{
		new Gustav(Gustav.class.getSimpleName(), "ai/individual");
	}
	
}