package net.sf.l2j.gameserver.scripting.scripts.custom;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.l2j.commons.random.Rnd;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.datatables.NpcTable;
import net.sf.l2j.gameserver.model.actor.Attackable;
import net.sf.l2j.gameserver.model.actor.Npc;
import net.sf.l2j.gameserver.model.actor.instance.Player;
import net.sf.l2j.gameserver.model.actor.template.NpcTemplate;
import net.sf.l2j.gameserver.model.holder.IntIntHolder;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;
import net.sf.l2j.gameserver.scripting.Quest;

/**
 * @author Tayran.JavaDev
 * @version 4
 */
public class GlobalDropMonsters extends Quest
{
	
	@Override
	public String onKill(Npc mob, Player killer, boolean isPet)
	{
		int levelMobMin = 0;
		for (int i = 1; i < 81; i++)
		{
			levelMobMin = killer.getLevel() - 8;
			if (i > 10)
			{
				if (killer.getLevel() == i && mob.getLevel() < levelMobMin)
					return "";
			}
		}
		if (mob.isChampion())
			dropItem(mob, killer, Config.GLOBAL_DROP_ITEMS_CHAMPION);
		else
			dropItem(mob, killer, Config.GLOBAL_DROP_ITEMS);
		return super.onKill(mob, killer, isPet);
	}
	
	private static void dropItem(final Npc mob, final Player player, final Map<Integer, List<Integer>> droplist)
	{
		Integer key;
		Integer chance;
		Integer min;
		Integer max;
		Integer itemMin;
		Integer itemMax;
		Integer count;
		Integer rnd;
		for (Entry<Integer, List<Integer>> entry : droplist.entrySet())
		{
			key = entry.getKey();
			List<Integer> valueList = entry.getValue();
			
			chance = valueList.get(0);
			min = valueList.get(1);
			max = valueList.get(2);
			
			if (mob.getLevel() > 9 && Config.ALLOW_GLOBAL_DROP_RANDOM)
			{
				itemMin = mob.getLevel() * min / 5;
				itemMax = mob.getLevel() * max / 6;
			}
			else
			{
				itemMin = min;
				itemMax = max;
			}
			count = Rnd.get(itemMin, itemMax);
			
			rnd = Rnd.get(100);
			
			if (rnd < chance)
			{
				IntIntHolder item = new IntIntHolder(key, count);
				dropItem(mob, player, item);
				continue;
			}
		}
		
	}
	
	/**
	 * Drop item.
	 * @author Tayran.JavaDev
	 * @param mob 
	 * @param lastAttacker The player who made ultimate damage.
	 * @param item instance IntIntHolder.
	 * @return the dropped item instance.
	 */
	 public static ItemInstance dropItem(Npc mob, Player lastAttacker, IntIntHolder item)
	 {
		 if (Config.AUTO_LOOT)
			 lastAttacker.doAutoLoot((Attackable) mob, item);
		 else
			 return  ((Attackable) mob).dropItem(lastAttacker, item);
		return null;		 
	 }

	
	public GlobalDropMonsters()
	{
		super(-1, GlobalDropMonsters.class.getSimpleName());
		if (Config.ALLOW_GLOBAL_DROP)
		{
			for (NpcTemplate npcTemplate : NpcTable.getInstance().getAllNpcs())
			{
				if (npcTemplate.getType().equalsIgnoreCase("L2Monster"))
					super.addKillId(npcTemplate.getIdTemplate());
			}
			System.out.println("[Drop Global Activated] All L2Monster have been added with Global Drop Items");
		}
		else
		{
			System.out.println("[Drop Global Disabled] No L2Monster was changed");
		}
	}
}