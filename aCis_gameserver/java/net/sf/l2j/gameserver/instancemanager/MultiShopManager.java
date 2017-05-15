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
package net.sf.l2j.gameserver.instancemanager;
 
import java.util.StringTokenizer;
 
import net.sf.l2j.commons.concurrent.ThreadPool;
 
import net.sf.l2j.Config;
import net.sf.l2j.gameserver.model.L2Skill;
import net.sf.l2j.gameserver.model.World;
import net.sf.l2j.gameserver.model.actor.instance.MultiShop;
import net.sf.l2j.gameserver.model.actor.instance.Player;
import net.sf.l2j.gameserver.model.itemcontainer.Inventory;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;
 
/**
 * @author Baggos
 */
public class MultiShopManager
{
    public static final MultiShopManager getInstance()
    {
        return SingletonHolder._instance;
    }
   
    public static void BaseClass(final Player player)
    {
        final NpcHtmlMessage html = new NpcHtmlMessage(player.getObjectId());
        html.setFile("data/html/mods/donateNpc/50009-2.htm");
        player.sendPacket(html);
    }
   
    public void Classes(String command, final Player player)
    {
                if (!MultiShop.conditionsclass(player))
                    return;
                for (final L2Skill skill : player.getSkills().values())
                {
                    player.removeSkill(skill);
                }
        String classes = command.substring(command.indexOf("_") + 1);
        switch (classes)
        {
            case "duelist":
                player.setClassId(88);
                player.setBaseClass(88);
                player.store();
                player.broadcastUserInfo();
                player.sendSkillList();
                player.giveAvailableSkills();
                player.sendMessage("Your base class has been changed! You will Be Disconected in 5 Seconds!");
                player.destroyItemByItemId("Consume", Config.DONATE_ITEM, Config.CLASS_ITEM_COUNT, player, true);
                ThreadPool.schedule(() -> player.logout(false), 5000);
                break;
            case "dreadnought":
                player.setClassId(89);
                player.setBaseClass(89);
                player.store();
                player.broadcastUserInfo();
                player.sendSkillList();
                player.giveAvailableSkills();
                player.sendMessage("Your base class has been changed! You will Be Disconected in 5 Seconds!");
                player.destroyItemByItemId("Consume", Config.DONATE_ITEM, Config.CLASS_ITEM_COUNT, player, true);
                ThreadPool.schedule(() -> player.logout(false), 5000);
                break;
            case "phoenix":
                player.setClassId(90);
                player.setBaseClass(90);
                player.store();
                player.broadcastUserInfo();
                player.sendSkillList();
                player.giveAvailableSkills();
                player.sendMessage("Your base class has been changed! You will Be Disconected in 5 Seconds!");
                player.destroyItemByItemId("Consume", Config.DONATE_ITEM, Config.CLASS_ITEM_COUNT, player, true);
                ThreadPool.schedule(() -> player.logout(false), 5000);
                break;
            case "hell":
                player.setClassId(91);
                player.setBaseClass(91);
                player.store();
                player.broadcastUserInfo();
                player.sendSkillList();
                player.giveAvailableSkills();
                player.sendMessage("Your base class has been changed! You will Be Disconected in 5 Seconds!");
                player.destroyItemByItemId("Consume", Config.DONATE_ITEM, Config.CLASS_ITEM_COUNT, player, true);
                ThreadPool.schedule(() -> player.logout(false), 5000);
                break;
            case "sagittarius":
                player.setClassId(92);
                player.setBaseClass(92);
                player.store();
                player.broadcastUserInfo();
                player.sendSkillList();
                player.giveAvailableSkills();
                player.sendMessage("Your base class has been changed! You will Be Disconected in 5 Seconds!");
                player.destroyItemByItemId("Consume", Config.DONATE_ITEM, Config.CLASS_ITEM_COUNT, player, true);
                ThreadPool.schedule(() -> player.logout(false), 5000);
                break;
            case "adventurer":
                player.setClassId(93);
                player.setBaseClass(93);
                player.store();
                player.broadcastUserInfo();
                player.sendSkillList();
                player.giveAvailableSkills();
                player.sendMessage("Your base class has been changed! You will Be Disconected in 5 Seconds!");
                player.destroyItemByItemId("Consume", Config.DONATE_ITEM, Config.CLASS_ITEM_COUNT, player, true);
                ThreadPool.schedule(() -> player.logout(false), 5000);
                break;
            case "archmage":
                player.setClassId(94);
                player.setBaseClass(94);
                player.store();
                player.broadcastUserInfo();
                player.sendSkillList();
                player.giveAvailableSkills();
                player.sendMessage("Your base class has been changed! You will Be Disconected in 5 Seconds!");
                player.destroyItemByItemId("Consume", Config.DONATE_ITEM, Config.CLASS_ITEM_COUNT, player, true);
                ThreadPool.schedule(() -> player.logout(false), 5000);
                break;
            case "soultaker":
                player.setClassId(95);
                player.setBaseClass(95);
                player.store();
                player.broadcastUserInfo();
                player.sendSkillList();
                player.giveAvailableSkills();
                player.sendMessage("Your base class has been changed! You will Be Disconected in 5 Seconds!");
                player.destroyItemByItemId("Consume", Config.DONATE_ITEM, Config.CLASS_ITEM_COUNT, player, true);
                ThreadPool.schedule(() -> player.logout(false), 5000);
                break;
            case "arcana":
                player.setClassId(96);
                player.setBaseClass(96);
                player.store();
                player.broadcastUserInfo();
                player.sendSkillList();
                player.giveAvailableSkills();
                player.sendMessage("Your base class has been changed! You will Be Disconected in 5 Seconds!");
                player.destroyItemByItemId("Consume", Config.DONATE_ITEM, Config.CLASS_ITEM_COUNT, player, true);
                ThreadPool.schedule(() -> player.logout(false), 5000);
                break;
            case "cardinal":
                player.setClassId(97);
                player.setBaseClass(97);
                player.store();
                player.broadcastUserInfo();
                player.sendSkillList();
                player.giveAvailableSkills();
                player.sendMessage("Your base class has been changed! You will Be Disconected in 5 Seconds!");
                player.destroyItemByItemId("Consume", Config.DONATE_ITEM, Config.CLASS_ITEM_COUNT, player, true);
                ThreadPool.schedule(() -> player.logout(false), 5000);
                break;
            case "hierophant":
                player.setClassId(98);
                player.setBaseClass(98);
                player.store();
                player.broadcastUserInfo();
                player.sendSkillList();
                player.giveAvailableSkills();
                player.sendMessage("Your base class has been changed! You will Be Disconected in 5 Seconds!");
                player.destroyItemByItemId("Consume", Config.DONATE_ITEM, Config.CLASS_ITEM_COUNT, player, true);
                ThreadPool.schedule(() -> player.logout(false), 5000);
                break;
            case "evas":
                player.setClassId(99);
                player.setBaseClass(99);
                player.store();
                player.broadcastUserInfo();
                player.sendSkillList();
                player.giveAvailableSkills();
                player.sendMessage("Your base class has been changed! You will Be Disconected in 5 Seconds!");
                player.destroyItemByItemId("Consume", Config.DONATE_ITEM, Config.CLASS_ITEM_COUNT, player, true);
                ThreadPool.schedule(() -> player.logout(false), 5000);
                break;
            case "muse":
                player.setClassId(100);
                player.setBaseClass(100);
                player.store();
                player.broadcastUserInfo();
                player.sendSkillList();
                player.giveAvailableSkills();
                player.sendMessage("Your base class has been changed! You will Be Disconected in 5 Seconds!");
                player.destroyItemByItemId("Consume", Config.DONATE_ITEM, Config.CLASS_ITEM_COUNT, player, true);
                ThreadPool.schedule(() -> player.logout(false), 5000);
                break;
            case "windrider":
                player.setClassId(101);
                player.setBaseClass(101);
                player.store();
                player.broadcastUserInfo();
                player.sendSkillList();
                player.giveAvailableSkills();
                player.sendMessage("Your base class has been changed! You will Be Disconected in 5 Seconds!");
                player.destroyItemByItemId("Consume", Config.DONATE_ITEM, Config.CLASS_ITEM_COUNT, player, true);
                ThreadPool.schedule(() -> player.logout(false), 5000);
                break;
            case "sentinel":
                player.setClassId(102);
                player.setBaseClass(102);
                player.store();
                player.broadcastUserInfo();
                player.sendSkillList();
                player.giveAvailableSkills();
                player.sendMessage("Your base class has been changed! You will Be Disconected in 5 Seconds!");
                player.destroyItemByItemId("Consume", Config.DONATE_ITEM, Config.CLASS_ITEM_COUNT, player, true);
                ThreadPool.schedule(() -> player.logout(false), 5000);
                break;
            case "mystic":
                player.setClassId(103);
                player.setBaseClass(103);
                player.store();
                player.broadcastUserInfo();
                player.sendSkillList();
                player.giveAvailableSkills();
                player.sendMessage("Your base class has been changed! You will Be Disconected in 5 Seconds!");
                player.destroyItemByItemId("Consume", Config.DONATE_ITEM, Config.CLASS_ITEM_COUNT, player, true);
                ThreadPool.schedule(() -> player.logout(false), 5000);
                break;
            case "elemental":
                player.setClassId(104);
                player.setBaseClass(104);
                player.store();
                player.broadcastUserInfo();
                player.sendSkillList();
                player.giveAvailableSkills();
                player.sendMessage("Your base class has been changed! You will Be Disconected in 5 Seconds!");
                player.destroyItemByItemId("Consume", Config.DONATE_ITEM, Config.CLASS_ITEM_COUNT, player, true);
                ThreadPool.schedule(() -> player.logout(false), 5000);
                break;
            case "saint":
                player.setClassId(105);
                player.setBaseClass(105);
                player.store();
                player.broadcastUserInfo();
                player.sendSkillList();
                player.giveAvailableSkills();
                player.sendMessage("Your base class has been changed! You will Be Disconected in 5 Seconds!");
                player.destroyItemByItemId("Consume", Config.DONATE_ITEM, Config.CLASS_ITEM_COUNT, player, true);
                ThreadPool.schedule(() -> player.logout(false), 5000);
                break;
            case "templar":
                player.setClassId(106);
                player.setBaseClass(106);
                player.store();
                player.broadcastUserInfo();
                player.sendSkillList();
                player.giveAvailableSkills();
                player.sendMessage("Your base class has been changed! You will Be Disconected in 5 Seconds!");
                player.destroyItemByItemId("Consume", Config.DONATE_ITEM, Config.CLASS_ITEM_COUNT, player, true);
                ThreadPool.schedule(() -> player.logout(false), 5000);
                break;
            case "dancer":
                player.setClassId(107);
                player.setBaseClass(107);
                player.store();
                player.broadcastUserInfo();
                player.sendSkillList();
                player.giveAvailableSkills();
                player.sendMessage("Your base class has been changed! You will Be Disconected in 5 Seconds!");
                player.destroyItemByItemId("Consume", Config.DONATE_ITEM, Config.CLASS_ITEM_COUNT, player, true);
                ThreadPool.schedule(() -> player.logout(false), 5000);
                break;
            case "hunter":
                player.setClassId(108);
                player.setBaseClass(108);
                player.store();
                player.broadcastUserInfo();
                player.sendSkillList();
                player.giveAvailableSkills();
                player.sendMessage("Your base class has been changed! You will Be Disconected in 5 Seconds!");
                player.destroyItemByItemId("Consume", Config.DONATE_ITEM, Config.CLASS_ITEM_COUNT, player, true);
                ThreadPool.schedule(() -> player.logout(false), 5000);
                break;
            case "gsentinel":
                player.setClassId(109);
                player.setBaseClass(109);
                player.store();
                player.broadcastUserInfo();
                player.sendSkillList();
                player.giveAvailableSkills();
                player.sendMessage("Your base class has been changed! You will Be Disconected in 5 Seconds!");
                player.destroyItemByItemId("Consume", Config.DONATE_ITEM, Config.CLASS_ITEM_COUNT, player, true);
                ThreadPool.schedule(() -> player.logout(false), 5000);
                break;
            case "screamer":
                player.setClassId(110);
                player.setBaseClass(110);
                player.store();
                player.broadcastUserInfo();
                player.sendSkillList();
                player.giveAvailableSkills();
                player.sendMessage("Your base class has been changed! You will Be Disconected in 5 Seconds!");
                player.destroyItemByItemId("Consume", Config.DONATE_ITEM, Config.CLASS_ITEM_COUNT, player, true);
                ThreadPool.schedule(() -> player.logout(false), 5000);
                break;
            case "master":
                player.setClassId(111);
                player.setBaseClass(111);
                player.store();
                player.broadcastUserInfo();
                player.sendSkillList();
                player.giveAvailableSkills();
                player.sendMessage("Your base class has been changed! You will Be Disconected in 5 Seconds!");
                player.destroyItemByItemId("Consume", Config.DONATE_ITEM, Config.CLASS_ITEM_COUNT, player, true);
                ThreadPool.schedule(() -> player.logout(false), 5000);
                break;
            case "ssaint":
                player.setClassId(112);
                player.setBaseClass(112);
                player.store();
                player.broadcastUserInfo();
                player.sendSkillList();
                player.giveAvailableSkills();
                player.sendMessage("Your base class has been changed! You will Be Disconected in 5 Seconds!");
                player.destroyItemByItemId("Consume", Config.DONATE_ITEM, Config.CLASS_ITEM_COUNT, player, true);
                ThreadPool.schedule(() -> player.logout(false), 5000);
                break;
            case "titan":
                player.setClassId(113);
                player.setBaseClass(113);
                player.store();
                player.broadcastUserInfo();
                player.sendSkillList();
                player.giveAvailableSkills();
                player.sendMessage("Your base class has been changed! You will Be Disconected in 5 Seconds!");
                player.destroyItemByItemId("Consume", Config.DONATE_ITEM, Config.CLASS_ITEM_COUNT, player, true);
                ThreadPool.schedule(() -> player.logout(false), 5000);
                break;
            case "khavatari":
                player.setClassId(114);
                player.setBaseClass(114);
                player.store();
                player.broadcastUserInfo();
                player.sendSkillList();
                player.giveAvailableSkills();
                player.sendMessage("Your base class has been changed! You will Be Disconected in 5 Seconds!");
                player.destroyItemByItemId("Consume", Config.DONATE_ITEM, Config.CLASS_ITEM_COUNT, player, true);
                ThreadPool.schedule(() -> player.logout(false), 5000);
                break;
            case "domi":
                player.setClassId(115);
                player.setBaseClass(115);
                player.store();
                player.broadcastUserInfo();
                player.sendSkillList();
                player.giveAvailableSkills();
                player.sendMessage("Your base class has been changed! You will Be Disconected in 5 Seconds!");
                player.destroyItemByItemId("Consume", Config.DONATE_ITEM, Config.CLASS_ITEM_COUNT, player, true);
                ThreadPool.schedule(() -> player.logout(false), 5000);
                break;
            case "doom":
                player.setClassId(116);
                player.setBaseClass(116);
                player.store();
                player.broadcastUserInfo();
                player.sendSkillList();
                player.giveAvailableSkills();
                player.sendMessage("Your base class has been changed! You will Be Disconected in 5 Seconds!");
                player.destroyItemByItemId("Consume", Config.DONATE_ITEM, Config.CLASS_ITEM_COUNT, player, true);
                ThreadPool.schedule(() -> player.logout(false), 5000);
                break;
            case "fortune":
                player.setClassId(117);
                player.setBaseClass(117);
                player.store();
                player.broadcastUserInfo();
                player.sendSkillList();
                player.giveAvailableSkills();
                player.sendMessage("Your base class has been changed! You will Be Disconected in 5 Seconds!");
                player.destroyItemByItemId("Consume", Config.DONATE_ITEM, Config.CLASS_ITEM_COUNT, player, true);
                ThreadPool.schedule(() -> player.logout(false), 5000);
                break;
            case "maestro":
                player.setClassId(118);
                player.setBaseClass(118);
                player.store();
                player.broadcastUserInfo();
                player.sendSkillList();
                player.giveAvailableSkills();
                player.sendMessage("Your base class has been changed! You will Be Disconected in 5 Seconds!");
                player.destroyItemByItemId("Consume", Config.DONATE_ITEM, Config.CLASS_ITEM_COUNT, player, true);
                ThreadPool.schedule(() -> player.logout(false), 5000);
                break;
        }
    }
   
    public void Name(String command, Player player)
    {
        if (command.startsWith("name"))
        {
            StringTokenizer st = new StringTokenizer(command);
            st.nextToken();
           
            String newName = "";
            try
            {
                if (st.hasMoreTokens())
                {
                    newName = st.nextToken();
                }
            }
            catch (Exception e)
            {
            }
            if (!MultiShop.conditionsname(newName, player))
                return;
            player.destroyItemByItemId("Consume", Config.DONATE_ITEM, Config.NAME_ITEM_COUNT, player, true);
            player.setName(newName);
            player.store();
            player.sendMessage("Your new character name is " + newName);
            player.broadcastUserInfo();
        }
    }
   
    public void Password(String command, Player player)
    {
        if (command.startsWith("password"))
        {
            StringTokenizer st = new StringTokenizer(command);
            st.nextToken();
           
            String newPass = "";
            String repeatNewPass = "";
           
            try
            {
                if (st.hasMoreTokens())
                {
                    newPass = st.nextToken();
                    repeatNewPass = st.nextToken();
                }
            }
            catch (Exception e)
            {
                player.sendMessage("Please fill all the blanks before requesting for a password change.");
                return;
            }
           
            if (!MultiShop.conditions(newPass, repeatNewPass, player))
                return;
           
            MultiShop.changePassword(newPass, repeatNewPass, player);
        }
    }
   
    public void Teleport(String command, Player player)
    {
        if (command.startsWith("tp"))
        {
            StringTokenizer st = new StringTokenizer(command);
            st.nextToken();
           
            String val = "";
            try
            {
                if (st.hasMoreTokens())
                {
                    val = st.nextToken();
                }
                Player activeChar = World.getInstance().getPlayer(val);
                MultiShop.teleportTo(val, player, activeChar);
            }
            catch (Exception e)
            {
                // Case of empty or missing coordinates
                player.sendMessage("Incorrect target");
            }
        }
    }
   
    public void TeleportClan(String command, Player player)
    {
        if (command.startsWith("clan"))
        {
            StringTokenizer st = new StringTokenizer(command);
            st.nextToken();
           
            String clan = "";
            try
            {
                if (st.hasMoreTokens())
                {
                    clan = st.nextToken();
                }
                Player activeChar = World.getInstance().getPlayer(clan);
                MultiShop.teleportToClan(clan, player, activeChar);
            }
            catch (Exception e)
            {
                // Case if the player is not in the same clan.
                player.sendMessage("Incorrect target");
            }
        }
    }
   
    public boolean Enchants(String command, Player player)
    {
        if (command.startsWith("enchant"))
        {
            StringTokenizer st = new StringTokenizer(command);
            st.nextToken();
            try
            {
                String type = st.nextToken();
                switch (type)
                {
                    case "Weapon":
                        MultiShop.Enchant(player, Config.ENCHANT_MAX_VALUE, Inventory.PAPERDOLL_RHAND);
                        break;
                    case "Shield":
                        MultiShop.Enchant(player, Config.ENCHANT_MAX_VALUE, Inventory.PAPERDOLL_LHAND);
                        break;
                    case "R-Earring":
                        MultiShop.Enchant(player, Config.ENCHANT_MAX_VALUE, Inventory.PAPERDOLL_REAR);
                        break;
                    case "L-Earring":
                        MultiShop.Enchant(player, Config.ENCHANT_MAX_VALUE, Inventory.PAPERDOLL_LEAR);
                        break;
                    case "R-Ring":
                        MultiShop.Enchant(player, Config.ENCHANT_MAX_VALUE, Inventory.PAPERDOLL_RFINGER);
                        break;
                    case "L-Ring":
                        MultiShop.Enchant(player, Config.ENCHANT_MAX_VALUE, Inventory.PAPERDOLL_LFINGER);
                        break;
                    case "Necklace":
                        MultiShop.Enchant(player, Config.ENCHANT_MAX_VALUE, Inventory.PAPERDOLL_NECK);
                        break;
                    case "Helmet":
                        MultiShop.Enchant(player, Config.ENCHANT_MAX_VALUE, Inventory.PAPERDOLL_HEAD);
                        break;
                    case "Boots":
                        MultiShop.Enchant(player, Config.ENCHANT_MAX_VALUE, Inventory.PAPERDOLL_FEET);
                        break;
                    case "Gloves":
                        MultiShop.Enchant(player, Config.ENCHANT_MAX_VALUE, Inventory.PAPERDOLL_GLOVES);
                        break;
                    case "Chest":
                        MultiShop.Enchant(player, Config.ENCHANT_MAX_VALUE, Inventory.PAPERDOLL_CHEST);
                        break;
                    case "Legs":
                        MultiShop.Enchant(player, Config.ENCHANT_MAX_VALUE, Inventory.PAPERDOLL_LEGS);
                        break;
                    case "Tattoo":
                        MultiShop.Enchant(player, Config.ENCHANT_MAX_VALUE, Inventory.PAPERDOLL_UNDER);
                        break;
                }
            }
           
            catch (Exception e)
            {
            }
        }
        return true;
    }
   
    public void Page(String command, Player player)
    {
        if (command.startsWith("services"))
        {
            NpcHtmlMessage html = new NpcHtmlMessage(1);
            html.setFile("data/html/mods/donateNpc/50009.htm");
            player.sendPacket(html);
        }
    }
   
    public void Page2(String command, Player player)
    {
        if (command.startsWith("next"))
        {
            NpcHtmlMessage html = new NpcHtmlMessage(1);
            html.setFile("data/html/mods/donateNpc/50009-1.htm");
            player.sendPacket(html);
        }
    }
   
    private static class SingletonHolder
    {
        protected static final MultiShopManager _instance = new MultiShopManager();
    }
}