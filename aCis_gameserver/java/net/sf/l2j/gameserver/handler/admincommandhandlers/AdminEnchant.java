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
package net.sf.l2j.gameserver.handler.admincommandhandlers;

import net.sf.l2j.gameserver.datatables.ArmorSetsTable;
import net.sf.l2j.gameserver.datatables.SkillTable;
import net.sf.l2j.gameserver.handler.IAdminCommandHandler;
import net.sf.l2j.gameserver.model.L2Object;
import net.sf.l2j.gameserver.model.L2Skill;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.item.ArmorSet;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;
import net.sf.l2j.gameserver.model.item.kind.Armor;
import net.sf.l2j.gameserver.model.item.kind.Item;
import net.sf.l2j.gameserver.model.item.kind.Weapon;
import net.sf.l2j.gameserver.model.itemcontainer.Inventory;
import net.sf.l2j.gameserver.network.serverpackets.ItemList;

/**
 * This class handles following admin commands: - enchant_armor
 */
public class AdminEnchant implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_seteh", // 6
		"admin_setec", // 10
		"admin_seteg", // 9
		"admin_setel", // 11
		"admin_seteb", // 12
		"admin_setew", // 7
		"admin_setes", // 8
		"admin_setle", // 1
		"admin_setre", // 2
		"admin_setlf", // 4
		"admin_setrf", // 5
		"admin_seten", // 3
		"admin_setun", // 0
		"admin_setba", // 13
		"admin_enchant"
	};
	
	@Override
	public boolean useAdminCommand(String command, L2PcInstance activeChar)
	{
		if (command.equals("admin_enchant"))
			showMainPage(activeChar);
		else
		{
			int armorType = -1;
			
			if (command.startsWith("admin_seteh"))
				armorType = Inventory.PAPERDOLL_HEAD;
			else if (command.startsWith("admin_setec"))
				armorType = Inventory.PAPERDOLL_CHEST;
			else if (command.startsWith("admin_seteg"))
				armorType = Inventory.PAPERDOLL_GLOVES;
			else if (command.startsWith("admin_seteb"))
				armorType = Inventory.PAPERDOLL_FEET;
			else if (command.startsWith("admin_setel"))
				armorType = Inventory.PAPERDOLL_LEGS;
			else if (command.startsWith("admin_setew"))
				armorType = Inventory.PAPERDOLL_RHAND;
			else if (command.startsWith("admin_setes"))
				armorType = Inventory.PAPERDOLL_LHAND;
			else if (command.startsWith("admin_setle"))
				armorType = Inventory.PAPERDOLL_LEAR;
			else if (command.startsWith("admin_setre"))
				armorType = Inventory.PAPERDOLL_REAR;
			else if (command.startsWith("admin_setlf"))
				armorType = Inventory.PAPERDOLL_LFINGER;
			else if (command.startsWith("admin_setrf"))
				armorType = Inventory.PAPERDOLL_RFINGER;
			else if (command.startsWith("admin_seten"))
				armorType = Inventory.PAPERDOLL_NECK;
			else if (command.startsWith("admin_setun"))
				armorType = Inventory.PAPERDOLL_UNDER;
			else if (command.startsWith("admin_setba"))
				armorType = Inventory.PAPERDOLL_BACK;
			
			if (armorType != -1)
			{
				try
				{
					int ench = Integer.parseInt(command.substring(12));
					
					// check value
					if (ench < 0 || ench > 65535)
						activeChar.sendMessage("You must set the enchant level to be between 0-65535.");
					else
						setEnchant(activeChar, ench, armorType);
				}
				catch (Exception e)
				{
					activeChar.sendMessage("Please specify a new enchant value.");
				}
			}
			
			// show the enchant menu after an action
			showMainPage(activeChar);
		}
		
		return true;
	}
	
	private static void setEnchant(L2PcInstance activeChar, int ench, int armorType)
	{
		L2Object target = activeChar.getTarget();
		if (!(target instanceof L2PcInstance))
			target = activeChar;
		
		final L2PcInstance player = (L2PcInstance) target;
		
		final ItemInstance item = player.getInventory().getPaperdollItem(armorType);
		if (item != null && item.getLocationSlot() == armorType)
		{
			final Item it = item.getItem();
			final int oldEnchant = item.getEnchantLevel();
			
			item.setEnchantLevel(ench);
			item.updateDatabase();
			
			// If item is equipped, verify the skill obtention/drop (+4 duals, +6 armorset).
			if (item.isEquipped())
			{
				final int currentEnchant = item.getEnchantLevel();
				
				// Skill bestowed by +4 duals.
				if (it instanceof Weapon)
				{
					// Old enchant was >= 4 and new is lower : we drop the skill.
					if (oldEnchant >= 4 && currentEnchant < 4)
					{
						final L2Skill enchant4Skill = ((Weapon) it).getEnchant4Skill();
						if (enchant4Skill != null)
						{
							player.removeSkill(enchant4Skill, false);
							player.sendSkillList();
						}
					}
					// Old enchant was < 4 and new is 4 or more : we add the skill.
					else if (oldEnchant < 4 && currentEnchant >= 4)
					{
						final L2Skill enchant4Skill = ((Weapon) it).getEnchant4Skill();
						if (enchant4Skill != null)
						{
							player.addSkill(enchant4Skill, false);
							player.sendSkillList();
						}
					}
				}
				// Add skill bestowed by +6 armorset.
				else if (it instanceof Armor)
				{
					// Old enchant was >= 6 and new is lower : we drop the skill.
					if (oldEnchant >= 6 && currentEnchant < 6)
					{
						// Checks if player is wearing a chest item
						final ItemInstance chestItem = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_CHEST);
						if (chestItem != null)
						{
							final ArmorSet armorSet = ArmorSetsTable.getInstance().getSet(chestItem.getItemId());
							if (armorSet != null)
							{
								final int skillId = armorSet.getEnchant6skillId();
								if (skillId > 0)
								{
									final L2Skill skill = SkillTable.getInstance().getInfo(skillId, 1);
									if (skill != null)
									{
										player.removeSkill(skill, false);
										player.sendSkillList();
									}
								}
							}
						}
					}
					// Old enchant was < 6 and new is 6 or more : we add the skill.
					else if (oldEnchant < 6 && currentEnchant >= 6)
					{
						// Checks if player is wearing a chest item
						final ItemInstance chestItem = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_CHEST);
						if (chestItem != null)
						{
							final ArmorSet armorSet = ArmorSetsTable.getInstance().getSet(chestItem.getItemId());
							if (armorSet != null && armorSet.isEnchanted6(player)) // has all parts of set enchanted to 6 or more
							{
								final int skillId = armorSet.getEnchant6skillId();
								if (skillId > 0)
								{
									final L2Skill skill = SkillTable.getInstance().getInfo(skillId, 1);
									if (skill != null)
									{
										player.addSkill(skill, false);
										player.sendSkillList();
									}
								}
							}
						}
					}
				}
			}
			
			player.sendPacket(new ItemList(player, false));
			player.broadcastUserInfo();
			
			activeChar.sendMessage("Changed enchantment of " + player.getName() + "'s " + it.getName() + " from " + oldEnchant + " to " + ench + ".");
			if (player != activeChar)
				player.sendMessage("A GM has changed the enchantment of your " + it.getName() + " from " + oldEnchant + " to " + ench + ".");
		}
	}
	
	private static void showMainPage(L2PcInstance activeChar)
	{
		AdminHelpPage.showHelpPage(activeChar, "enchant.htm");
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}