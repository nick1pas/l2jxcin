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
package net.xcine.gameserver.handler.itemhandlers;

import net.xcine.gameserver.ai.CtrlIntention;
import net.xcine.gameserver.handler.IItemHandler;
import net.xcine.gameserver.model.L2ItemInstance;
import net.xcine.gameserver.model.L2Skill;
import net.xcine.gameserver.model.actor.L2Playable;
import net.xcine.gameserver.model.actor.instance.L2PcInstance;
import net.xcine.gameserver.model.actor.instance.L2PetInstance;
import net.xcine.gameserver.model.actor.instance.L2SummonInstance;
import net.xcine.gameserver.network.SystemMessageId;
import net.xcine.gameserver.network.serverpackets.ExUseSharedGroupItem;
import net.xcine.gameserver.network.serverpackets.SystemMessage;
import net.xcine.gameserver.skills.SkillHolder;
import net.xcine.gameserver.templates.item.L2EtcItem;
import net.xcine.gameserver.templates.item.L2EtcItemType;

/**
 * Template for item skills handler.<BR>
 * <BR>
 * Only minimum checks are applied.
 */
public class ItemSkills implements IItemHandler
{
	@Override
	public void useItem(L2Playable playable, L2ItemInstance item, boolean forceUse)
	{
		L2PcInstance activeChar;
		boolean isPet = playable instanceof L2PetInstance;
		if (isPet)
			activeChar = ((L2PetInstance) playable).getOwner();
		else if (playable instanceof L2PcInstance)
			activeChar = (L2PcInstance) playable;
		else
			return;
		
		// pets can use items only when they are tradable
		if (isPet && !item.isTradable())
		{
			activeChar.sendPacket(SystemMessageId.ITEM_NOT_FOR_PETS);
			return;
		}
		
		int skillId;
		int skillLvl;
		
		final SkillHolder[] skills = item.getEtcItem().getSkills();
		if (skills != null)
		{
			for (SkillHolder skillInfo : skills)
			{
				if (skillInfo == null)
					continue;
				
				skillId = skillInfo.getSkillId();
				skillLvl = skillInfo.getSkillLvl();
				L2Skill itemSkill = skillInfo.getSkill();
				
				if (itemSkill != null)
				{
					if (!itemSkill.checkCondition(playable, playable.getTarget(), false))
						return;
					
					// No message on retail, the use is just forgotten.
					if (playable.isSkillDisabled(itemSkill))
					{
						if (item.isEtcItem())
						{
							final int group = item.getEtcItem().getSharedReuseGroup();
							if (group >= 0)
							{
								if (activeChar.getReuseTimeStamp() != null && activeChar.getReuseTimeStamp().containsKey(itemSkill.getReuseHashCode()))
								{
									final long remainingTime = activeChar.getReuseTimeStamp().get(itemSkill.getReuseHashCode()).getRemaining();
									activeChar.sendPacket(new ExUseSharedGroupItem(item.getItemId(), group, (int) remainingTime, itemSkill.getReuseDelay()));
								}
							}
						}
						return;
					}
					
					if (!itemSkill.isPotion() && playable.isCastingNow())
						return;
					
					// Item consumption is setup here
					if ((itemSkill.isPotion() && !item.isHerb()) || itemSkill.isSimultaneousCast())
					{
						// Normal item consumption is 1, if more, it must be given in DP with getItemConsume().
						int itemNumber = 1;
						if (itemSkill.getItemConsumeId() == 0 && itemSkill.getItemConsume() > 0)
							itemNumber = itemSkill.getItemConsume();
						
						if (!playable.destroyItem("Consume", item.getObjectId(), itemNumber, null, false))
						{
							activeChar.sendPacket(SystemMessageId.NOT_ENOUGH_ITEMS);
							return;
						}
					}
					
					// send message to owner
					if (isPet)
						activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.PET_USES_S1).addSkillName(itemSkill));
					else
					{
						switch (skillId)
						{
						// short buff icon for healing potions
							case 2031:
							case 2032:
							case 2037:
								int buffId = activeChar._shortBuffTaskSkillId;
								// greater healing potions
								if (skillId == 2037)
									activeChar.shortBuffStatusUpdate(skillId, skillLvl, itemSkill.getBuffDuration() / 1000);
								// healing potions
								else if ((skillId == 2032) && buffId != 2037)
									activeChar.shortBuffStatusUpdate(skillId, skillLvl, itemSkill.getBuffDuration() / 1000);
								// lesser healing potions
								else
								{
									if (buffId != 2037 && buffId != 2032)
										activeChar.shortBuffStatusUpdate(skillId, skillLvl, itemSkill.getBuffDuration() / 1000);
								}
								break;
						}
					}
					
					if (itemSkill.isPotion() || itemSkill.isSimultaneousCast())
					{
						playable.doSimultaneousCast(itemSkill);
						// Summons should be affected by herbs too, self time effect is handled at L2Effect constructor
						if (!isPet && item.getItemType() == L2EtcItemType.HERB && activeChar.getPet() != null && activeChar.getPet() instanceof L2SummonInstance)
							activeChar.getPet().doSimultaneousCast(itemSkill);
					}
					else
					{
						playable.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
						if (!playable.useMagic(itemSkill, forceUse, false))
							return;
						
						// Normal item consumption is 1, if more, it must be given in DP with getItemConsume().
						int itemNumber = 1;
						if (itemSkill.getItemConsumeId() == 0 && itemSkill.getItemConsume() > 0)
							itemNumber = itemSkill.getItemConsume();
						
						// Item consumption is setup here
						if (!playable.destroyItem("Consume", item.getObjectId(), itemNumber, null, false))
						{
							activeChar.sendPacket(SystemMessageId.NOT_ENOUGH_ITEMS);
							return;
						}
					}
					
					int reuseDelay = itemSkill.getReuseDelay();
					if (item.isEtcItem())
					{
						L2EtcItem etcItem = item.getEtcItem();
						
						if (etcItem.getReuseDelay() > reuseDelay)
							reuseDelay = etcItem.getReuseDelay();
						
						activeChar.addTimeStamp(itemSkill, reuseDelay);
						if (reuseDelay != 0)
							activeChar.disableSkill(itemSkill, reuseDelay);
						
						final int group = etcItem.getSharedReuseGroup();
						if (group >= 0)
							activeChar.sendPacket(new ExUseSharedGroupItem(item.getItemId(), group, reuseDelay, reuseDelay));
					}
					else if (reuseDelay > 0)
					{
						activeChar.addTimeStamp(itemSkill, reuseDelay);
						activeChar.disableSkill(itemSkill, reuseDelay);
					}
				}
			}
		}
		else
			_log.info("Item " + item + " does not have registered any skill for handler.");
	}
}