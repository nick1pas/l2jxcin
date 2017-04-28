package net.sf.l2j.gameserver.model.actor.instance;

import java.util.StringTokenizer;

import net.sf.l2j.commons.random.Rnd;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.datatables.AugmentationData;
import net.sf.l2j.gameserver.datatables.IconTable;
import net.sf.l2j.gameserver.datatables.ItemTable;
import net.sf.l2j.gameserver.model.actor.Npc;
import net.sf.l2j.gameserver.model.actor.ai.CtrlIntention;
import net.sf.l2j.gameserver.model.actor.template.NpcTemplate;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.network.serverpackets.InventoryUpdate;
import net.sf.l2j.gameserver.network.serverpackets.MyTargetSelected;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;
import net.sf.l2j.gameserver.network.serverpackets.SystemMessage;
import net.sf.l2j.gameserver.network.serverpackets.ValidateLocation;

public class ItemEnchanter extends Npc
{
	boolean flip = true;
	String IngredientName = ItemTable.getInstance().getTemplate(Config.IngredientID).getName();
	
	/**
	 * @param objectId the object id
	 * @param template the template
	 */
	public ItemEnchanter(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}
	
	@Override
	public void onAction(Player player)
	{
		// Check if the Player already target the Npc
		if (this != player.getTarget())
		{
			// Set the target of the Player player
			player.setTarget(this);
			
			// Send a Server->Client packet MyTargetSelected to the Player player
			MyTargetSelected my = new MyTargetSelected(getObjectId(), 0);
			player.sendPacket(my);
			my = null;
			
			// Send a Server->Client packet ValidateLocation to correct the Npc position and heading on the client
			player.sendPacket(new ValidateLocation(this));
		}
		else
		{
			// Calculate the distance between the Player and the Npc
			if (!canInteract(player))
			{
				// Notify the Player AI with AI_INTENTION_INTERACT
				player.getAI().setIntention(CtrlIntention.INTERACT, this);
			}
			else
			{
				showChatWindow(player);
			}
		}
		
		// Send a Server->Client ActionFailed to the Player in order to avoid that the client wait another packet
		player.ActionF();
	}
	
	@Override
	public void showChatWindow(Player player)
	{
		String filename = "data/html/itemEnchanter/disabled.htm";
		
		if (Config.npcEnchantItemsEnabled)
		{
			filename = "data/html/itemEnchanter/start.htm";
		}
		
		NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
		html.setFile(filename);
		html.replace("%objectId%", String.valueOf(getObjectId()));
		player.sendPacket(html);
	}
	
	@Override
	public void onBypassFeedback(Player player, String command)
	{
		String filename = "data/html/itemEnchanter/template.htm";
		
		StringTokenizer st = new StringTokenizer(command, " ");
		String actualCommand = st.nextToken();
		
		if (actualCommand.equalsIgnoreCase("enchant_weapon"))
		{
			ItemInstance[] weapon = player.getInventory().getWeaponsList();
			final int size = weapon.length;
			String text = "";
			String bgcolor = "";
			
			if (size == 0)
			{
				filename = "data/html/itemEnchanter/empty_list.htm";
			}
			else if (size > 15)
			{
				filename = "data/html/itemEnchanter/long_list.htm";
			}
			
			for (int i = 0; i != size; i++)
			{
				flip = !flip;
				final int itemId = weapon[i].getItem().getItemId();
				String name = weapon[i].getItemName();
				String Action = "Item " + String.valueOf(weapon[i].getObjectId());
				bgcolor = "";
				
				int enchantLevel = Config.weaponEnchantLevel;
				if (Config.modifyItemEnchant)
				{
					if (Config.modifyItemEnchantList.containsKey(weapon[i].getItemId()))
					{
						enchantLevel = Config.modifyItemEnchantList.get(weapon[i].getItemId());
					}
				}
				
				if (flip == true)
				{
					bgcolor = "bgcolor=000000";
				}
				
				text += "<table " + bgcolor + "><tr><td width=40><button action=\"bypass -h npc_" + getObjectId() + "_" + Action + "\" width=32 height=32 back=" + IconTable.getIcon(itemId) + " fore=" + IconTable.getIcon(itemId) + "></td><td width=220><table width=300><tr><td><font color=799BB0>+" + enchantLevel + " " + name + "</font></td><td></td></tr><tr><td><font color=B09B79> " + Config.InAmountWeapon + " " + IngredientName + "</font></td></tr></table></td></tr></table>";
			}
			
			NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
			html.setFile(filename);
			
			html.replace("%objectId%", String.valueOf(getObjectId()));
			html.replace("%list%", String.valueOf(text));
			player.sendPacket(html);
		}
		if (actualCommand.equalsIgnoreCase("enchant_armor"))
		{
			ItemInstance[] armor = player.getInventory().getArmorsList();
			final int size = armor.length;
			String text = "";
			String bgcolor = "";
			
			if (size == 0)
			{
				filename = "data/html/itemEnchanter/empty_list.htm";
			}
			else if (size > 15)
			{
				filename = "data/html/itemEnchanter/long_list.htm";
			}
			
			for (int i = 0; i != size; i++)
			{
				flip = !flip;
				int itemId = armor[i].getItem().getItemId();
				String name = armor[i].getItemName();
				String Action = "Item " + String.valueOf(armor[i].getObjectId());
				bgcolor = "";
				
				int enchantLevel = Config.armorEnchantLevel;
				if (Config.modifyItemEnchant)
				{
					if (Config.modifyItemEnchantList.containsKey(armor[i].getItemId()))
					{
						enchantLevel = Config.modifyItemEnchantList.get(armor[i].getItemId());
					}
				}
				
				if (flip == true)
				{
					bgcolor = "bgcolor=000000";
				}
				
				text += "<table " + bgcolor + "><tr><td width=40><button action=\"bypass -h npc_" + getObjectId() + "_" + Action + "\" width=32 height=32 back=" + IconTable.getIcon(itemId) + " fore=" + IconTable.getIcon(itemId) + "></td><td width=220><table width=300><tr><td><font color=799BB0>+" + enchantLevel + " " + name + "</font></td><td></td></tr><tr><td><font color=B09B79> " + Config.InAmountArmor + " " + IngredientName + "</font></td></tr></table></td></tr></table>";
			}
			
			NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
			html.setFile(filename);
			
			html.replace("%objectId%", String.valueOf(getObjectId()));
			html.replace("%list%", String.valueOf(text));
			player.sendPacket(html);
		}
		
		if (actualCommand.equalsIgnoreCase("Item"))
		{
			String bbx = st.nextToken();
			int itemId = Integer.valueOf(bbx);
			int itemCount = 0;
			
			int enchantLevel = Config.weaponEnchantLevel;
			int amount = Config.InAmountWeapon;
			
			ItemInstance item = player.getInventory().getItemByObjectId(itemId);
			if (item == null)
			{
				player.sendMessage("That item doesn't exist in your inventory.");
				return;
			}
			
			if (item.isArmor())
			{
				enchantLevel = Config.armorEnchantLevel;
				amount = Config.InAmountArmor;
			}
			
			if (Config.modifyItemEnchant)
			{
				if (Config.modifyItemEnchantList.containsKey(item.getItemId()))
				{
					enchantLevel = Config.modifyItemEnchantList.get(item.getItemId());
				}
			}
			
			if (player.getInventory().getItemByItemId(Config.IngredientID) != null)
			{
				itemCount = player.getInventory().getItemByItemId(Config.IngredientID).getCount();
				if (itemCount < amount)
				{
					player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.NOT_ENOUGH_ITEMS));
				}
				else
				{
					player.destroyItemByItemId("enchantItem", Config.IngredientID, amount, player, true);
					item.setEnchantLevel(enchantLevel);
					player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_S2_SUCCESSFULLY_ENCHANTED).addNumber(item.getEnchantLevel()).addItemName(item.getItemId()));
					
					if (item.isAugmentable())
					{
						if (Rnd.get(100) <= Config.augmentItemChance)
						{
							item.setAugmentation(AugmentationData.getInstance().generateRandomAugmentation(Rnd.get(7, 9), Rnd.get(2, 3)));
							player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.THE_ITEM_WAS_SUCCESSFULLY_AUGMENTED));
						}
					}
					
					InventoryUpdate iu = new InventoryUpdate();
					iu.addModifiedItem(item);
					player.sendPacket(iu);
					player.broadcastUserInfo();
				}
			}
			else
			{
				player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.NOT_ENOUGH_ITEMS));
			}
		}
		
		super.onBypassFeedback(player, command);
	}
}