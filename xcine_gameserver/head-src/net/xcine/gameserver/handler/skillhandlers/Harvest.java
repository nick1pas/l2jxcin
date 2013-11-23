/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package net.xcine.gameserver.handler.skillhandlers;

import net.xcine.gameserver.handler.ISkillHandler;
import net.xcine.gameserver.model.L2ItemInstance;
import net.xcine.gameserver.model.L2Object;
import net.xcine.gameserver.model.L2Skill;
import net.xcine.gameserver.model.actor.L2Attackable.RewardItem;
import net.xcine.gameserver.model.actor.L2Character;
import net.xcine.gameserver.model.actor.instance.L2MonsterInstance;
import net.xcine.gameserver.model.actor.instance.L2PcInstance;
import net.xcine.gameserver.network.SystemMessageId;
import net.xcine.gameserver.network.serverpackets.InventoryUpdate;
import net.xcine.gameserver.network.serverpackets.SystemMessage;
import net.xcine.gameserver.templates.skills.L2SkillType;
import net.xcine.util.Rnd;

/**
 * @author l3x
 */
public class Harvest implements ISkillHandler
{
	private static final L2SkillType[] SKILL_IDS =
	{
		L2SkillType.HARVEST
	};
	
	@Override
	public void useSkill(L2Character activeChar, L2Skill skill, L2Object[] targets)
	{
		if (!(activeChar instanceof L2PcInstance))
			return;
		
		final L2Object object = targets[0];
		if (!(object instanceof L2MonsterInstance))
			return;
		
		final L2PcInstance player = (L2PcInstance) activeChar;
		final L2MonsterInstance target = (L2MonsterInstance) object;
		
		if (player.getObjectId() != target.getSeederId())
		{
			player.sendPacket(SystemMessageId.YOU_ARE_NOT_AUTHORIZED_TO_HARVEST);
			return;
		}
		
		boolean send = false;
		int total = 0;
		int cropId = 0;
		
		if (target.isSeeded())
		{
			if (calcSuccess(player, target))
			{
				final RewardItem[] items = target.takeHarvest();
				if (items != null && items.length > 0)
				{
					InventoryUpdate iu = new InventoryUpdate();
					for (RewardItem ritem : items)
					{
						cropId = ritem.getItemId(); // always got 1 type of crop as reward
						
						if (player.isInParty())
							player.getParty().distributeItem(player, ritem, true, target);
						else
						{
							L2ItemInstance item = player.getInventory().addItem("Manor", ritem.getItemId(), ritem.getCount(), player, target);
							iu.addItem(item);
							
							send = true;
							total += ritem.getCount();
						}
					}
					
					if (send)
					{
						player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.YOU_PICKED_UP_S2_S1).addItemName(cropId).addNumber(total));
						
						if (player.isInParty())
							player.getParty().broadcastToPartyMembers(player, SystemMessage.getSystemMessage(SystemMessageId.S1_HARVESTED_S3_S2S).addPcName(player).addItemName(cropId).addNumber(total));
						
						player.sendPacket(iu);
					}
				}
			}
			else
				player.sendPacket(SystemMessageId.THE_HARVEST_HAS_FAILED);
		}
		else
			player.sendPacket(SystemMessageId.THE_HARVEST_FAILED_BECAUSE_THE_SEED_WAS_NOT_SOWN);
	}
	
	private static boolean calcSuccess(L2Character activeChar, L2Character target)
	{
		int basicSuccess = 100;
		final int levelPlayer = activeChar.getLevel();
		final int levelTarget = target.getLevel();
		
		int diff = (levelPlayer - levelTarget);
		if (diff < 0)
			diff = -diff;
		
		// apply penalty, target <=> player levels, 5% penalty for each level
		if (diff > 5)
			basicSuccess -= (diff - 5) * 5;
		
		// success rate cant be less than 1%
		if (basicSuccess < 1)
			basicSuccess = 1;
		
		return Rnd.get(99) < basicSuccess;
	}
	
	@Override
	public L2SkillType[] getSkillIds()
	{
		return SKILL_IDS;
	}
}