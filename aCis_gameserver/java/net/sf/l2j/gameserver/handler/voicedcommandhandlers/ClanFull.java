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
package net.sf.l2j.gameserver.handler.voicedcommandhandlers;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.datatables.ItemTable;
import net.sf.l2j.gameserver.datatables.SkillTable;
import net.sf.l2j.gameserver.handler.IVoicedCommandHandler;
import net.sf.l2j.gameserver.model.L2Skill;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.network.serverpackets.ExShowScreenMessage;

/**
 * @author Bluur
 */
public class ClanFull implements IVoicedCommandHandler
{
    private static final String[] _voicedCommands =
    {
        "clanfull"
    };
    
    // id skills
    private final int[] clanSkills =
    {
        370,
        371,
        372,
        373,
        374,
        375,
        376,
        377,
        378,
        379,
        380,
        381,
        382,
        383,
        384,
        385,
        386,
        387,
        388,
        389,
        390,
        391
    };
    
    @Override
    public boolean useVoicedCommand(String command, L2PcInstance activeChar, String params)
    {  
         if (activeChar.isClanLeader())
         {
        	 if (activeChar.getInventory().getInventoryItemCount(Config.CLAN_ITEM_ID, -1) < Config.CLAN_ITEM_COUNT)            
        	 {
        		 activeChar.sendMessage("You does not have this amount of " + ItemTable.getInstance().getTemplate(Config.CLAN_ITEM_ID).getName() + " for use this command.");
        		 return false;            
        	 }          

             if (activeChar.getClan().getLevel() == 8)
             {
                 activeChar.sendMessage("[Clan Full]: The clan of " + activeChar.getName() + " Already used this command");
                 return false;
             }
                        
             activeChar.getClan().changeLevel(Config.CLAN_LEVEL);
             activeChar.getClan().addReputationScore(Config.REPUTATION_QUANTITY);
                
             for (int s : clanSkills)
             {
                  L2Skill clanSkill = SkillTable.getInstance().getInfo(s, SkillTable.getInstance().getMaxLevel(s));
                  activeChar.getClan().addNewSkill(clanSkill);
             }
                
             activeChar.sendSkillList();
             activeChar.getClan().updateClanInDB();
             activeChar.sendPacket(new ExShowScreenMessage("Your Clan is Full! Thanks!", 8000));                
             activeChar.sendMessage("[Clan Full]: Successfully added!");        
         }
         else
             activeChar.sendMessage("[Clan Full]: " + activeChar.getName() + " Needs to be clan leader.");  
 
        return true;
    }
    
    @Override
    public String[] getVoicedCommandList()
    {
        return _voicedCommands;
    }
}