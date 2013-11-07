package ai.group_template;

import net.xcine.gameserver.datatables.sql.ItemTable;
import net.xcine.gameserver.model.L2Effect;
import net.xcine.gameserver.model.actor.instance.L2ItemInstance;
import net.xcine.gameserver.model.actor.instance.L2NpcInstance;
import net.xcine.gameserver.model.actor.instance.L2PcInstance;
import net.xcine.util.random.Rnd;
import ai.L2AttackableAIScript;

public class EvaBox extends L2AttackableAIScript
{
    private final static int[] KISS_OF_EVA =
    {
        1073,
        3141,
        3252
    };
    private final static int BOX = 32342;
    private final static int[] REWARDS =
    {
        9692,
        9693
    };

    public EvaBox(int questId, String name, String descr)
    {
    	super(questId, name, descr);

        int mobs[] =
        {
            BOX
        };

        registerMobs(mobs, QuestEventType.ON_KILL);
    }

    public void dropItem(L2NpcInstance npc, int itemId, int count, L2PcInstance player)
    {
        L2ItemInstance ditem = ItemTable.getInstance().createItem("Loot", itemId, count, player);
        ditem.dropMe(npc, npc.getX(), npc.getY(), npc.getZ());
    }

    @Override
    public String onKill(L2NpcInstance npc, L2PcInstance killer, boolean isPet)
    {
        boolean found = false;
        for (L2Effect effect : killer.getAllEffects())
        {
            for (int i = 0; i < 3; i++)
            {
                if (effect.getSkill().getId() == KISS_OF_EVA[i])
                {
                    found = true;
                }
            }
        }

        if (found == true)
        {
            int dropid = Rnd.get(1);
            if (dropid == 1)
            {
                dropItem(npc, REWARDS[dropid], 1, killer);
            }
            else if (dropid == 0)
            {
                dropItem(npc, REWARDS[dropid], 1, killer);
            }
        }

        return super.onKill(npc, killer, isPet);
    }

    public static void main(String[] args)
    {
		// now call the constructor (starts up the ai)
		new EvaBox(-1,"evabox","ai");
    }
}