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
package net.sf.l2j.gameserver.model.actor.instance;
 
import net.sf.l2j.Config;
import net.sf.l2j.gameserver.datatables.SkillTable;
import net.sf.l2j.gameserver.model.L2Skill;
import net.sf.l2j.gameserver.network.serverpackets.ActionFailed;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;
import net.sf.l2j.gameserver.model.actor.template.NpcTemplate;
 
/**
 * @author Tayran.JavaDev
 * @Modificações by BossForever
 */
public class L2ClanManagerInstance extends L2NpcInstance
{
    public L2ClanManagerInstance(int objectId, NpcTemplate template)
    {
        super(objectId, template);
    }
   
    @Override
    public void showChatWindow(L2PcInstance player, int val)
    {
        player.sendPacket(ActionFailed.STATIC_PACKET);
        String filename = "data/html/mods/clanManager.htm";
        NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
        html.setFile(filename);
        html.replace("%objectId%", String.valueOf(getObjectId()));
        player.sendPacket(html);
    }

    @Override
    public void onBypassFeedback(L2PcInstance player, String command)
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
                for(int i = 370; i <= 391; i++)
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
    private static boolean clanConditions(L2PcInstance player)
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