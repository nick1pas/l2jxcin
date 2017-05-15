package net.sf.l2j.gameserver.model.actor.instance;

import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Base64;
import java.util.StringTokenizer;

import net.sf.l2j.commons.concurrent.ThreadPool;

import net.sf.l2j.Config;
import net.sf.l2j.L2DatabaseFactory;
import net.sf.l2j.gameserver.datatables.PlayerNameTable;
import net.sf.l2j.gameserver.datatables.SkillTable;
import net.sf.l2j.gameserver.handler.admincommandhandlers.AdminVipStatus;
import net.sf.l2j.gameserver.instancemanager.CastleManager;
import net.sf.l2j.gameserver.instancemanager.MultiShopManager;
import net.sf.l2j.gameserver.model.L2Augmentation;
import net.sf.l2j.gameserver.model.actor.ai.CtrlIntention;
import net.sf.l2j.gameserver.model.actor.template.NpcTemplate;
import net.sf.l2j.gameserver.model.base.Experience;
import net.sf.l2j.gameserver.model.base.Sex;
import net.sf.l2j.gameserver.model.entity.Castle;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;
import net.sf.l2j.gameserver.model.itemcontainer.Inventory;
import net.sf.l2j.gameserver.model.olympiad.OlympiadManager;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.network.serverpackets.ExShowScreenMessage;
import net.sf.l2j.gameserver.network.serverpackets.InventoryUpdate;
import net.sf.l2j.gameserver.network.serverpackets.ItemList;
import net.sf.l2j.gameserver.network.serverpackets.MagicSkillUse;
import net.sf.l2j.gameserver.network.serverpackets.SiegeInfo;
import net.sf.l2j.gameserver.network.serverpackets.SystemMessage;

/**
 * @author Baggos
 */
public class MultiShop extends Folk
{
	public MultiShop(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}
	
	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if (player == null)
			return;
		
		if (command.startsWith("donate"))
		{
			StringTokenizer st = new StringTokenizer(command);
			st.nextToken();
			try
			{
				String type = st.nextToken();
				switch (type)
				{
					case "Noblesse":
						Nobless(player);
						break;
					case "ChangeSex":
						Sex(player);
						break;
					case "CleanPk":
						CleanPk(player);
						break;
					case "FullRec":
						Rec(player);
						break;
					case "ChangeClass":
						MultiShopManager.BaseClass(player);
						break;
					case "SetLevel":
						SetLevel(player);
						break;
				}
			}
			catch (Exception e)
			{
			}
		}
		else if (command.startsWith("clan"))
		{
			StringTokenizer st = new StringTokenizer(command);
			st.nextToken();
			try
			{
				String type = st.nextToken();
				switch (type)
				{
					case "ClanLevel":
						Clanlvl(player);
						break;
					case "ClanRep_20k":
						ClanRep(player);
						break;
					case "ClanSkills":
						ClanSkill(player);
						break;
				}
			}
			catch (Exception e)
			{
			}
		}
		else if (command.startsWith("siege"))
		{
			
			StringTokenizer st = new StringTokenizer(command);
			st.nextToken();
			try
			{
				String type = st.nextToken();
				int castleId = 0;
				
				if (type.startsWith("Gludio"))
					castleId = 1;
				else if (type.startsWith("Dion"))
					castleId = 2;
				else if (type.startsWith("Giran"))
					castleId = 3;
				else if (type.startsWith("Oren"))
					castleId = 4;
				else if (type.startsWith("Aden"))
					castleId = 5;
				else if (type.startsWith("Innadril"))
					castleId = 6;
				else if (type.startsWith("Goddard"))
					castleId = 7;
				else if (type.startsWith("Rune"))
					castleId = 8;
				else if (type.startsWith("Schuttgart"))
					castleId = 9;
				
				Castle castle = CastleManager.getInstance().getCastleById(castleId);
				
				if (castle != null && castleId != 0)
					player.sendPacket(new SiegeInfo(castle));
			}
			catch (Exception e)
			{
			}
		}
		else if (command.startsWith("color"))
		{
			StringTokenizer st = new StringTokenizer(command);
			st.nextToken();
			try
			{
				String type = st.nextToken();
				switch (type)
				{
					case "Green":
						GreenColor(player);
						break;
					case "Blue":
						BlueColor(player);
						break;
					case "Purple":
						PurpleColor(player);
						break;
					case "Yellow":
						YellowColor(player);
						break;
					case "Gold":
						GoldColor(player);
						break;
				}
			}
			catch (Exception e)
			{
			}
		}
		else if (command.startsWith("vip"))
		{
			StringTokenizer st = new StringTokenizer(command);
			st.nextToken();
			try
			{
				String type = st.nextToken();
				switch (type)
				{
					case "Vip7Days":
						Vip7(player);
						break;
					case "Vip15Days":
						Vip15(player);
						break;
					case "Vip30Days":
						Vip30(player);
						break;
				}
			}
			catch (Exception e)
			{
			}
		}
		else if (command.startsWith("vip"))
		{
			StringTokenizer st = new StringTokenizer(command);
			st.nextToken();
			try
			{
				String type = st.nextToken();
				switch (type)
				{
					case "Vip7Days":
						Vip7(player);
						break;
					case "Vip15Days":
						Vip15(player);
						break;
					case "Vip30Days":
						Vip30(player);
						break;
				}
			}
			catch (Exception e)
			{
			}
		}
		else if (command.startsWith("active"))
		{
			StringTokenizer st = new StringTokenizer(command);
			st.nextToken();
			try
			{
				String type = st.nextToken();
				switch (type)
				{
					case "Might":
						augments(player, 1062079106, 3132, 10);
						break;
					case "Empower":
						augments(player, 1061423766, 3133, 10);
						break;
					case "DuelMight":
						augments(player, 1062406807, 3134, 10);
						break;
					case "Shield":
						augments(player, 968884225, 3135, 10);
						break;
					case "MagicBarrier":
						augments(player, 956760065, 3136, 10);
						break;
					case "WildMagic":
						augments(player, 1067850844, 3142, 10);
						break;
					case "Focus":
						augments(player, 1067523168, 3141, 10);
						break;
					case "BattleRoad":
						augments(player, 968228865, 3125, 10);
						break;
					case "BlessedBody":
						augments(player, 991625216, 3124, 10);
						break;
					case "Agility":
						augments(player, 1060444351, 3139, 10);
						break;
					case "Heal":
						augments(player, 1061361888, 3123, 10);
						break;
					case "HydroBlast":
						augments(player, 1063590051, 3167, 10);
						break;
					case "AuraFlare":
						augments(player, 1063455338, 3172, 10);
						break;
					case "Hurricane":
						augments(player, 1064108032, 3168, 10);
						break;
					case "ReflectDamage":
						augments(player, 1067588698, 3204, 3);
						break;
					case "Celestial":
						augments(player, 974454785, 3158, 1);
						break;
					case "Stone":
						augments(player, 1060640984, 3169, 10);
						break;
					case "HealEmpower":
						augments(player, 1061230760, 3138, 10);
						break;
					case "ShadowFlare":
						augments(player, 1063520931, 3171, 10);
						break;
					case "Prominence":
						augments(player, 1063327898, 3165, 10);
						break;
				}
			}
			catch (Exception e)
			{
				player.sendMessage("Usage : Bar>");
			}
		}
		else if (command.startsWith("passive"))
		{
			StringTokenizer st = new StringTokenizer(command);
			st.nextToken();
			try
			{
				String type = st.nextToken();
				switch (type)
				{
					case "DuelMight":
						augments(player, 1067260101, 3243, 10);
						break;
					case "Might":
						augments(player, 1067125363, 3240, 10);
						break;
					case "Shield":
						augments(player, 1067194549, 3244, 10);
						break;
					case "MagicBarrier":
						augments(player, 962068481, 3245, 10);
						break;
					case "Empower":
						augments(player, 1066994296, 3241, 10);
						break;
					case "Agility":
						augments(player, 965279745, 3247, 10);
						break;
					case "Guidance":
						augments(player, 1070537767, 3248, 10);
						break;
					case "Focus":
						augments(player, 1070406728, 3249, 10);
						break;
					case "WildMagic":
						augments(player, 1070599653, 3250, 10);
						break;
					case "ReflectDamage":
						augments(player, 1070472227, 3259, 3);
						break;
					case "HealEmpower":
						augments(player, 1066866909, 3246, 10);
						break;
					case "Prayer":
						augments(player, 1066932422, 3238, 10);
						break;
					
				}
			}
			catch (Exception e)
			{
				player.sendMessage("Usage : Bar>");
			}
		}
	}
	
	public static void Nobless(Player player)
	{
		if (player.isNoble())
		{
			player.sendMessage("You Are Already A Noblesse!.");
			return;
		}
		if (player.getInventory().getInventoryItemCount(Config.DONATE_ITEM, -1) < Config.NOBL_ITEM_COUNT)
		{
			player.sendMessage("You do not have enough Donate Coins.");
			return;
		}
		player.setNoble(true, true);
		player.sendMessage("You Are Now a Noble,You Are Granted With Noblesse Status , And Noblesse Skills.");
		player.broadcastUserInfo();
		player.destroyItemByItemId("Consume", Config.DONATE_ITEM, Config.NOBL_ITEM_COUNT, player, true);
		
	}
	
	public static void SetLevel(Player player)
	{
		if (player.getLevel() >= Config.MULTI_LEVEL)
		{
			player.sendMessage("Action failed.");
			return;
		}
		if (player.getInventory().getInventoryItemCount(Config.DONATE_ITEM, -1) < Config.LEVEL_ITEM_COUNT)
		{
			player.sendMessage("You do not have enough Donate Coins.");
			return;
		}
		player.addExpAndSp(Experience.LEVEL[Config.MULTI_LEVEL], 0);
		player.sendMessage("Your level has increased to " + Config.MULTI_LEVEL + ".");
		player.destroyItemByItemId("Consume", Config.DONATE_ITEM, Config.LEVEL_ITEM_COUNT, player, true);
		
	}
	
	public static void Vip7(Player player)
	{
		if (player.isVipStatus())
		{
			player.sendMessage("You are Already A Vip");
			return;
		}
		
		if (player.getInventory().getInventoryItemCount(Config.DONATE_ITEM, -1) < Config.VIP7_ITEM_COUNT)
		{
			player.sendMessage("You do not have enough Donate Coins.");
			return;
		}
		AdminVipStatus.AddVipStatus(player, player, 7);
		player.sendMessage("You engage VIP Status for 7 Days.");
		player.destroyItemByItemId("Consume", Config.DONATE_ITEM, Config.VIP7_ITEM_COUNT, player, true);
		
	}
	
	public static void Vip15(Player player)
	{
		if (player.isVipStatus())
		{
			player.sendMessage("You are Already A Vip");
			return;
		}
		
		if (player.getInventory().getInventoryItemCount(Config.DONATE_ITEM, -1) < Config.VIP15_ITEM_COUNT)
		{
			player.sendMessage("You do not have enough Donate Coins.");
			return;
		}
		AdminVipStatus.AddVipStatus(player, player, 15);
		player.sendMessage("You engage VIP Status for 15 Days.");
		player.destroyItemByItemId("Consume", Config.DONATE_ITEM, Config.VIP15_ITEM_COUNT, player, true);
		
	}
	
	public static void Vip30(Player player)
	{
		if (player.isVipStatus())
		{
			player.sendMessage("Your character has already Vip Status.");
			return;
		}
		
		if (player.getInventory().getInventoryItemCount(Config.DONATE_ITEM, -1) < Config.VIP30_ITEM_COUNT)
		{
			player.sendMessage("You do not have enough Donate Coins.");
			return;
		}
		AdminVipStatus.AddVipStatus(player, player, 30);
		player.sendMessage("You engage VIP Status for 30 Days.");
		player.destroyItemByItemId("Consume", Config.DONATE_ITEM, Config.VIP30_ITEM_COUNT, player, true);
		
	}
	
	public static void GreenColor(Player player)
	{
		if (player.getInventory().getInventoryItemCount(Config.DONATE_ITEM, -1) < Config.COLOR_ITEM_COUNT)
		{
			player.sendMessage("You do not have enough Donate Coins.");
			return;
		}
		player.setColor(1);
		player.getAppearance().setNameColor(0x009900);
		player.broadcastUserInfo();
		player.sendMessage("Your color name has changed!");
		player.destroyItemByItemId("Consume", Config.DONATE_ITEM, Config.COLOR_ITEM_COUNT, player, true);
	}
	
	public static void BlueColor(Player player)
	{
		if (player.getInventory().getInventoryItemCount(Config.DONATE_ITEM, -1) < Config.COLOR_ITEM_COUNT)
		{
			player.sendMessage("You do not have enough Donate Coins.");
			return;
		}
		player.setColor(2);
		player.getAppearance().setNameColor(0xff7f00);
		player.broadcastUserInfo();
		player.sendMessage("Your color name has changed!");
		player.destroyItemByItemId("Consume", Config.DONATE_ITEM, Config.COLOR_ITEM_COUNT, player, true);
	}
	
	public static void PurpleColor(Player player)
	{
		if (player.getInventory().getInventoryItemCount(Config.DONATE_ITEM, -1) < Config.COLOR_ITEM_COUNT)
		{
			player.sendMessage("You do not have enough Donate Coins.");
			return;
		}
		player.setColor(3);
		player.getAppearance().setNameColor(0xff00ff);
		player.broadcastUserInfo();
		player.sendMessage("Your color name has changed!");
		player.destroyItemByItemId("Consume", Config.DONATE_ITEM, Config.COLOR_ITEM_COUNT, player, true);
	}
	
	public static void YellowColor(Player player)
	{
		if (player.getInventory().getInventoryItemCount(Config.DONATE_ITEM, -1) < Config.COLOR_ITEM_COUNT)
		{
			player.sendMessage("You do not have enough Donate Coins.");
			return;
		}
		player.setColor(4);
		player.getAppearance().setNameColor(0x00ffff);
		player.broadcastUserInfo();
		player.sendMessage("Your color name has changed!");
		player.destroyItemByItemId("Consume", Config.DONATE_ITEM, Config.COLOR_ITEM_COUNT, player, true);
	}
	
	public static void GoldColor(Player player)
	{
		if (player.getInventory().getInventoryItemCount(Config.DONATE_ITEM, -1) < Config.COLOR_ITEM_COUNT)
		{
			player.sendMessage("You do not have enough Donate Coins.");
			return;
		}
		player.setColor(5);
		player.getAppearance().setNameColor(0x0099ff);
		player.broadcastUserInfo();
		player.sendMessage("Your color name has changed!");
		player.destroyItemByItemId("Consume", Config.DONATE_ITEM, Config.COLOR_ITEM_COUNT, player, true);
	}
	
	public static void Sex(Player player)
	{
		if (player.getInventory().getInventoryItemCount(Config.DONATE_ITEM, -1) < Config.SEX_ITEM_COUNT)
		{
			player.sendMessage("You do not have enough Donate Coins.");
			return;
		}
		player.getAppearance().setSex(player.getAppearance().getSex() == Sex.MALE ? Sex.FEMALE : Sex.MALE);
		player.sendMessage("Your gender has been changed,You will Be Disconected in 3 Seconds!");
		player.broadcastUserInfo();
		player.decayMe();
		player.spawnMe();
		ThreadPool.schedule(() -> player.logout(false), 3000);
		player.destroyItemByItemId("Consume", Config.DONATE_ITEM, Config.SEX_ITEM_COUNT, player, true);
	}
	
	public static void Rec(Player player)
	{
		if (player.getInventory().getInventoryItemCount(Config.DONATE_ITEM, -1) < Config.REC_ITEM_COUNT)
		{
			player.sendMessage("You do not have enough Donate Coins.");
			return;
		}
		if (player.getRecomHave() == 255)
		{
			player.sendMessage("You already have full recommends.");
			return;
		}
		player.setRecomHave(255);
		player.getLastRecomUpdate();
		player.broadcastUserInfo();
		player.destroyItemByItemId("Consume", Config.DONATE_ITEM, Config.REC_ITEM_COUNT, player, true);
	}
	
	public static void CleanPk(Player player)
	{
		if (player.getInventory().getInventoryItemCount(Config.DONATE_ITEM, -1) < Config.PK_ITEM_COUNT)
		{
			player.sendMessage("You do not have enough Donate Coins.");
			return;
		}
		if (player.getPkKills() < 50)
		{
			player.sendMessage("You do not have enough Pk kills for clean.");
			return;
		}
		player.setPkKills(player.getPkKills() - Config.PK_CLEAN);
		player.sendMessage("You have successfully clean " + Config.PK_CLEAN + " pks!");
		player.broadcastUserInfo();
		player.destroyItemByItemId("Consume", Config.DONATE_ITEM, Config.PK_ITEM_COUNT, player, true);
	}
	
	public static void ClanRep(Player player)
	{
		if (player.getInventory().getInventoryItemCount(Config.DONATE_ITEM, -1) < Config.CLAN_REP_ITEM_COUNT)
		{
			player.sendMessage("You do not have enough Donate Coins.");
			return;
		}
		if (player.getClan() == null)
		{
			player.sendPacket(SystemMessageId.YOU_ARE_NOT_A_CLAN_MEMBER);
			return;
		}
		if (!player.isClanLeader())
		{
			player.sendPacket(SystemMessageId.NOT_AUTHORIZED_TO_BESTOW_RIGHTS);
			return;
		}
		player.destroyItemByItemId("Consume", Config.DONATE_ITEM, Config.CLAN_REP_ITEM_COUNT, player, true);
		player.getClan().addReputationScore(Config.CLAN_REPS);
		player.getClan().broadcastClanStatus();
		player.sendMessage("Your clan reputation score has been increased.");
	}
	
	public static void Clanlvl(Player player)
	{
		if (player.getInventory().getInventoryItemCount(Config.DONATE_ITEM, -1) < Config.CLAN_ITEM_COUNT)
		{
			player.sendMessage("You do not have enough Donate Coins.");
			return;
		}
		else if (player.getClan() == null)
		{
			player.sendPacket(SystemMessageId.YOU_ARE_NOT_A_CLAN_MEMBER);
			return;
		}
		if (!player.isClanLeader())
		{
			player.sendPacket(SystemMessageId.NOT_AUTHORIZED_TO_BESTOW_RIGHTS);
			return;
		}
		if (player.getClan().getLevel() == 8)
		{
			player.sendMessage("Your clan is already level 8.");
			return;
		}
		player.getClan().changeLevel(player.getClan().getLevel() + 1);
		player.getClan().broadcastClanStatus();
		player.broadcastPacket(new MagicSkillUse(player, player, 5103, 1, 1000, 0));
		player.destroyItemByItemId("Consume", Config.DONATE_ITEM, Config.CLAN_ITEM_COUNT, player, true);
	}
	
	public static void augments(Player player, int attributes, int idaugment, int levelaugment)
	{
		ItemInstance rhand = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_RHAND);
		
		if (rhand == null)
		{
			player.sendMessage(player.getName() + " have to equip a weapon.");
			return;
		}
		else if (rhand.getItem().getCrystalType().getId() == 0 || rhand.getItem().getCrystalType().getId() == 1 || rhand.getItem().getCrystalType().getId() == 2)
		{
			player.sendMessage("You can't augment under " + rhand.getItem().getCrystalType() + " Grade Weapon!");
			return;
		}
		else if (rhand.isHeroItem())
		{
			player.sendMessage("You Cannot be add Augment On " + rhand.getItemName() + " !");
			return;
		}
		else if (player.getInventory().getInventoryItemCount(Config.DONATE_ITEM, -1) < Config.AUGM_ITEM_COUNT)
		{
			player.sendMessage("You do not have enough Donate Coins.");
			return;
		}
		if (!rhand.isAugmented())
		{
			player.sendMessage("Successfully To Add " + SkillTable.getInstance().getInfo(idaugment, levelaugment).getName() + ".");
			augmentweapondatabase(player, attributes, idaugment, levelaugment);
		}
		else
		{
			player.sendMessage("You Have Augment on weapon!");
			return;
		}
	}
	
	public static void augmentweapondatabase(Player player, int attributes, int id, int level)
	{
		ItemInstance item = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_RHAND);
		L2Augmentation augmentation = new L2Augmentation(attributes, id, level);
		augmentation.applyBonus(player);
		player.destroyItemByItemId("Consume", Config.DONATE_ITEM, Config.AUGM_ITEM_COUNT, player, true);
		item.setAugmentation(augmentation);
		disarm(player);
		
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement("REPLACE INTO augmentations VALUES(?,?,?,?)");
			statement.setInt(1, item.getObjectId());
			statement.setInt(2, attributes);
			statement.setInt(3, id);
			statement.setInt(4, level);
			InventoryUpdate iu = new InventoryUpdate();
			player.sendPacket(iu);
			statement.execute();
			statement.close();
		}
		catch (SQLException e)
		{
			System.out.println(e);
		}
	}
	
	public static void disarm(Player player)
	{
		// Unequip the weapon
		ItemInstance wpn = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_RHAND);
		if (wpn != null)
		{
			ItemInstance[] unequipped = player.getInventory().unEquipItemInBodySlotAndRecord(wpn.getItem().getBodyPart());
			InventoryUpdate iu = new InventoryUpdate();
			for (ItemInstance itm : unequipped)
				iu.addModifiedItem(itm);
			player.sendPacket(iu);
			
			player.abortAttack();
			player.broadcastUserInfo();
		}
	}
	
	/**
	 * @param player
	 */
	public static void removeAugmentation(Player player)
	{
		ItemInstance item = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_RHAND);
		
		if (item == null)
		{
			player.sendMessage("Equipped first a weapon!");
			return;
		}
		
		if (!item.isAugmented())
		{
			player.sendMessage("The weapon is not augmented.");
			return;
		}
		
		item.getAugmentation().removeBonus(player);
		item.removeAugmentation();
		{
			player.sendMessage("Your augmented has been removed!");
			// Unequip the weapon
			ItemInstance wpn = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_RHAND);
			ItemInstance[] unequipped = player.getInventory().unEquipItemInBodySlotAndRecord(wpn.getItem().getBodyPart());
			InventoryUpdate iu = new InventoryUpdate();
			for (ItemInstance itm : unequipped)
				iu.addModifiedItem(itm);
			player.sendPacket(iu);
			
			player.abortAttack();
			player.broadcastUserInfo();
		}
	}
	
	public static void ClanSkill(Player player)
	{
		
		if (!player.isClanLeader())
		{
			player.sendMessage("Only a clan leader can add clan skills.!");
			return;
		}
		if (player.getInventory().getInventoryItemCount(Config.DONATE_ITEM, -1) < Config.CLAN_SKILL_ITEM_COUNT)
		{
			player.sendMessage("You do not have enough Donate Coins.");
			return;
		}
		player.getClan().addNewSkill(SkillTable.getInstance().getInfo(370, 3));
		player.getClan().addNewSkill(SkillTable.getInstance().getInfo(371, 3));
		player.getClan().addNewSkill(SkillTable.getInstance().getInfo(372, 3));
		player.getClan().addNewSkill(SkillTable.getInstance().getInfo(373, 3));
		player.getClan().addNewSkill(SkillTable.getInstance().getInfo(374, 3));
		player.getClan().addNewSkill(SkillTable.getInstance().getInfo(375, 3));
		player.getClan().addNewSkill(SkillTable.getInstance().getInfo(376, 3));
		player.getClan().addNewSkill(SkillTable.getInstance().getInfo(377, 3));
		player.getClan().addNewSkill(SkillTable.getInstance().getInfo(378, 3));
		player.getClan().addNewSkill(SkillTable.getInstance().getInfo(379, 3));
		player.getClan().addNewSkill(SkillTable.getInstance().getInfo(380, 3));
		player.getClan().addNewSkill(SkillTable.getInstance().getInfo(381, 3));
		player.getClan().addNewSkill(SkillTable.getInstance().getInfo(382, 3));
		player.getClan().addNewSkill(SkillTable.getInstance().getInfo(383, 3));
		player.getClan().addNewSkill(SkillTable.getInstance().getInfo(384, 3));
		player.getClan().addNewSkill(SkillTable.getInstance().getInfo(385, 3));
		player.getClan().addNewSkill(SkillTable.getInstance().getInfo(386, 3));
		player.getClan().addNewSkill(SkillTable.getInstance().getInfo(387, 3));
		player.getClan().addNewSkill(SkillTable.getInstance().getInfo(388, 3));
		player.getClan().addNewSkill(SkillTable.getInstance().getInfo(389, 3));
		player.getClan().addNewSkill(SkillTable.getInstance().getInfo(390, 3));
		player.getClan().addNewSkill(SkillTable.getInstance().getInfo(391, 1));
		player.destroyItemByItemId("Consume", Config.DONATE_ITEM, Config.CLAN_SKILL_ITEM_COUNT, player, true);
	}
	
	public static void Enchant(Player player, int enchant, int type)
	{
		ItemInstance item = player.getInventory().getPaperdollItem(type);
		
		if (item == null)
		{
			player.sendMessage("That item doesn't exist in your inventory.");
			return;
		}
		else if (item.getEnchantLevel() == Config.ENCHANT_MAX_VALUE || item.getEnchantLevel() == Config.ENCHANT_MAX_VALUE)
		{
			player.sendMessage("Your " + item.getItemName() + " is already on maximun enchant!");
			return;
		}
		else if (item.getItem().getCrystalType().getId() == 0)
		{
			player.sendMessage("You can't Enchant under " + item.getItem().getCrystalType() + " Grade Weapon!");
			return;
		}
		else if (item.isHeroItem())
		{
			player.sendMessage("You Cannot be Enchant On " + item.getItemName() + " !");
			return;
		}
		else if (player.getInventory().getInventoryItemCount(Config.DONATE_ITEM, -1) < Config.ENCHANT_ITEM_COUNT)
		{
			player.sendMessage("You do not have enough Donate Coins.");
			return;
		}
		if (item.isEquipped())
		{
			player.destroyItemByItemId("Consume", Config.DONATE_ITEM, Config.ENCHANT_ITEM_COUNT, player, true);
			item.setEnchantLevel(enchant);
			item.updateDatabase();
			player.sendPacket(new ItemList(player, false));
			player.broadcastUserInfo();
			player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_S2_SUCCESSFULLY_ENCHANTED).addNumber(item.getEnchantLevel()).addItemName(item.getItemId()));
		}
	}
	
	public static boolean conditionsclass(Player player)
	{
		if (player.isSubClassActive())
		{
			player.sendMessage("You cannot change your Main Class while you're with Sub Class.");
			return false;
		}
		else if (OlympiadManager.getInstance().isRegisteredInComp(player))
		{
			player.sendMessage("You cannot change your Main Class while you have been registered for olympiad match.");
			return false;
		}
		else if (player.getInventory().getInventoryItemCount(Config.DONATE_ITEM, -1) < Config.CLASS_ITEM_COUNT)
		{
			player.sendMessage("You do not have enough Donate Coins.");
			return false;
		}
		return true;
	}
	
	public static boolean conditionsname(String newName, Player player)
	{
		if (!newName.matches("^[a-zA-Z0-9]+$"))
		{
			player.sendMessage("Incorrect name. Please try again.");
			return false;
		}
		else if (newName.equals(player.getName()))
		{
			player.sendMessage("Please, choose a different name.");
			return false;
		}
		else if (PlayerNameTable.getInstance().getPlayerObjectId(newName) > 0)
		{
			player.sendMessage("The name " + newName + " already exists.");
			return false;
		}
		else if (player.getInventory().getInventoryItemCount(Config.DONATE_ITEM, -1) < Config.NAME_ITEM_COUNT)
		{
			player.sendMessage("You do not have enough Donate Coins.");
			return false;
		}
		return true;
	}
	
	public static boolean conditions(String newPass, String repeatNewPass, Player player)
	{
		if (newPass.length() < 3)
		{
			player.sendMessage("The new password is too short!");
			return false;
		}
		else if (newPass.length() > 45)
		{
			player.sendMessage("The new password is too long!");
			return false;
		}
		else if (!newPass.equals(repeatNewPass))
		{
			player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.PASSWORD_ENTERED_INCORRECT2));
			return false;
		}
		else if (player.getInventory().getInventoryItemCount(Config.DONATE_ITEM, -1) < Config.PASSWORD_ITEM_COUNT)
		{
			player.sendMessage("You do not have enough Donate Coins.");
			return false;
		}
		return true;
	}
	
	public static void changePassword(String newPass, String repeatNewPass, Player activeChar)
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection(); PreparedStatement ps = con.prepareStatement("UPDATE accounts SET password=? WHERE login=?"))
		{
			byte[] newPassword = MessageDigest.getInstance("SHA").digest(newPass.getBytes("UTF-8"));
			
			ps.setString(1, Base64.getEncoder().encodeToString(newPassword));
			ps.setString(2, activeChar.getAccountName());
			ps.executeUpdate();
			
			activeChar.sendMessage("Congratulations! Your password has been changed. You will now be disconnected for security reasons. Please login again.");
			activeChar.destroyItemByItemId("Consume", Config.DONATE_ITEM, Config.PASSWORD_ITEM_COUNT, activeChar, true);
			ThreadPool.schedule(() -> activeChar.logout(false), 3000);
		}
		catch (Exception e)
		{
			
		}
	}
	
	public static void teleportTo(String val, Player activeChar, Player target)
	{
		if (target.getObjectId() == activeChar.getObjectId())
			activeChar.sendPacket(SystemMessageId.CANNOT_USE_ON_YOURSELF);
		
		// Check if the attacker is not in the same party
		if (!activeChar.getParty().getMembers().contains(target))
		{
			activeChar.sendMessage("Your target Isn't in your party.");
			return;
		}
		// Simple checks to avoid exploits
		if (target.isInJail() || target.isInOlympiadMode() || target.isInDuel() || target.isFestivalParticipant() || (target.isInParty() && target.getParty().isInDimensionalRift()) || target.isInObserverMode())
		{
			activeChar.sendMessage("Due to the current friend's status, the teleportation failed.");
			return;
		}
		if (target.getClan() != null && CastleManager.getInstance().getCastleByOwner(target.getClan()) != null && CastleManager.getInstance().getCastleByOwner(target.getClan()).getSiege().isInProgress())
		{
			activeChar.sendMessage("As your friend is in siege, you can't go to him/her.");
			return;
		}
		if (activeChar.getPvpFlag() > 0 || activeChar.getKarma() > 0)
		{
			activeChar.sendMessage("Go away! Flag or Pk player can not be teleported.");
			return;
		}
		int x = target.getX();
		int y = target.getY();
		int z = target.getZ();
		
		activeChar.getAI().setIntention(CtrlIntention.IDLE);
		activeChar.doCast(SkillTable.getInstance().getInfo(2100, 1));
		activeChar.sendPacket(new ExShowScreenMessage("You will be teleported to " + target.getName() + " in 5 Seconds!", 5000, 2, true));
		ThreadPool.schedule(() -> activeChar.teleToLocation(x, y, z, 0), 5000);
		activeChar.sendMessage("You have teleported to " + target.getName() + ".");
	}
	
	public static void teleportToClan(String clan, Player activeChar, Player target)
	{
		if (target.getObjectId() == activeChar.getObjectId())
			activeChar.sendPacket(SystemMessageId.CANNOT_USE_ON_YOURSELF);
		
		// Check if the player is not in the same clan.
		if (!activeChar.getClan().isMember(target.getObjectId()))
			return;
		
		// Simple checks to avoid exploits
		if (target.isInJail() || target.isInOlympiadMode() || target.isInDuel() || target.isFestivalParticipant() || (target.isInParty() && target.getParty().isInDimensionalRift()) || target.isInObserverMode())
		{
			activeChar.sendMessage("Due to the current clan member's status, the teleportation failed.");
			return;
		}
		if (target.getClan() != null && CastleManager.getInstance().getCastleByOwner(target.getClan()) != null && CastleManager.getInstance().getCastleByOwner(target.getClan()).getSiege().isInProgress())
		{
			activeChar.sendMessage("As your clan member is in siege, you can't go to him/her.");
			return;
		}
		if (activeChar.getPvpFlag() > 0 || activeChar.getKarma() > 0)
		{
			activeChar.sendMessage("Go away! Flag or Pk player can not be teleported.");
			return;
		}
		int x = target.getX();
		int y = target.getY();
		int z = target.getZ();
		
		activeChar.getAI().setIntention(CtrlIntention.IDLE);
		activeChar.doCast(SkillTable.getInstance().getInfo(2100, 1));
		activeChar.sendPacket(new ExShowScreenMessage("You will be teleported to " + target.getName() + " in 5 Seconds!", 5000, 2, true));
		ThreadPool.schedule(() -> activeChar.teleToLocation(x, y, z, 0), 5000);
		activeChar.sendMessage("You have teleported to " + target.getName() + ".");
	}
	
	@Override
	public String getHtmlPath(int npcId, int val)
	{
		String filename = "";
		
		if (val == 0)
			filename = "" + npcId;
		else
			filename = npcId + "-" + val;
		
		return "data/html/mods/donateNpc/" + filename + ".htm";
	}
}