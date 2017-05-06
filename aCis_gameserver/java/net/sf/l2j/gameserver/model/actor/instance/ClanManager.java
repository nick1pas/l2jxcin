package net.sf.l2j.gameserver.model.actor.instance;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.datatables.SkillTable;
import net.sf.l2j.gameserver.model.L2Skill;
import net.sf.l2j.gameserver.model.actor.Npc;
import net.sf.l2j.gameserver.model.actor.template.NpcTemplate;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;

/**
 * @author Tayran.JavaDev
 * @Modificações by BossForever
 */
public class ClanManager extends Npc
{
	public ClanManager(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}
	
	@Override
	public void showChatWindow(Player player, int val)
	{
		player.ActionF();
		String filename = "data/html/mods/clanManager.htm";
		NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
		html.setFile(filename);
		html.replace("%objectId%", String.valueOf(getObjectId()));
		player.sendPacket(html);
	}
	
	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if (command.equals("clanLevelUp"))
		{
			if (clanConditions(player))
			{
				player.getClan().changeLevel(player.getClan().getLevel() + Config.CLAN_MANAGER_CLAN_LEVEL_REWARD);
				player.getClan().broadcastClanStatus();
				player.getInventory().destroyItemByItemId("Init.", Config.CLAN_MANAGER_ITEM_ID, Config.CLAN_MANAGER_LEVEL_UP_COUNT, player, player);
				player.sendMessage("Your clan's level has been changed to " + player.getClan().getLevel());
			}
			else
			{
				return;
			}
		}
		else if (command.equals("clanReputationPoints"))
		{
			if (clanConditions(player))
			{
				player.getClan().addReputationScore(Config.CLAN_MANAGER_CLAN_REPUTATION_REWARD);
				player.getClan().broadcastClanStatus();
				player.getInventory().destroyItemByItemId("Init.", Config.CLAN_MANAGER_ITEM_ID, Config.CLAN_MANAGER_REPUTATION_COUNT, player, player);
				player.sendMessage("Your clan's reputation score has been changed to " + player.getClan().getReputationScore());
			}
			else
			{
				return;
			}
		}
		else if (command.equals("clanSkills"))
		{
			if (clanConditions(player))
			{
				for (int i = 370; i <= 391; i++)
				{
					L2Skill clanSkill = SkillTable.getInstance().getInfo(i, SkillTable.getInstance().getMaxLevel(i));
					player.getClan().addNewSkill(clanSkill);
				}
				player.getClan().broadcastClanStatus();
				player.getInventory().destroyItemByItemId("Init.", Config.CLAN_MANAGER_ITEM_ID, Config.CLAN_MANAGER_CLAN_SKILLS_COUNT, player, player);
				player.sendMessage("Your clan has learned all clan skills.");
				
			}
			else
			{
				return;
			}
		}
	}
	
	/**
	 * @param player
	 * @return true if clan ok
	 */
	private static boolean clanConditions(Player player)
	{
		if (player.getClan() == null)
		{
			player.sendMessage("You don't have a clan.");
			return false;
		}
		else if (!player.isClanLeader())
		{
			player.sendMessage("You aren't the leader of your clan.");
			return false;
		}
		else if (player.getInventory().getItemByItemId(Config.CLAN_MANAGER_ITEM_ID).getCount() > Config.CLAN_MANAGER_LEVEL_UP_COUNT || player.getInventory().getItemByItemId(Config.CLAN_MANAGER_ITEM_ID).getCount() > Config.CLAN_MANAGER_CLAN_SKILLS_COUNT || player.getInventory().getItemByItemId(Config.CLAN_MANAGER_ITEM_ID).getCount() > Config.CLAN_MANAGER_REPUTATION_COUNT)
		{
			player.sendMessage("You don't have enough items.");
			return false;
		}
		
		return true;
	}
}