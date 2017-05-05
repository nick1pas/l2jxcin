package net.sf.l2j.gameserver.handler.voicedcommandhandlers;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.datatables.SkillTable;
import net.sf.l2j.gameserver.handler.IVoicedCommandHandler;
import net.sf.l2j.gameserver.model.actor.instance.Player;
import net.sf.l2j.gameserver.model.zone.ZoneId;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;

public class Buff implements IVoicedCommandHandler
{
    private final String[] _voicedCommands =
    {
        "buff"
    };
    
    @Override
    public boolean useVoicedCommand(String command, Player activeChar, String target)
    {    
        if (check(activeChar))              
            showHtml(activeChar);    
        
        return true;
    }
    
    public static void getFullBuff(Player p, boolean isClassMage)
    {
        if (check(p))
        {
            if (isClassMage)
            {
                for (int b : Config.BUFF_COMMAND_MAGE_IDBUFFS)    
                     SkillTable.getInstance().getInfo(b, SkillTable.getInstance().getMaxLevel(b)).getEffects(p, p);        
            }
            else
            {
                for (int b : Config.BUFF_COMMAND_FIGHT_IDBUFFS)    
                     SkillTable.getInstance().getInfo(b, SkillTable.getInstance().getMaxLevel(b)).getEffects(p, p);        
            }
            p.sendMessage("[Buff Command]: You have been buffed!");
        }        
    }
    
    public static boolean check(Player p)
    {        
    	p.sendMessage("You are in combat mode or outside the peace zone");
        return p.isInsideZone(ZoneId.PEACE) && !p.isInCombat() && !p.isInOlympiadMode(); //restrições       
    }
    
    public static void showHtml(Player player)
    {      
        NpcHtmlMessage html = new NpcHtmlMessage(0);    
        html.setFile("data/html/mods/buffCommand.htm");
        html.replace("%currentBuffs%", player.getBuffCount());
        html.replace("%getMaxBuffs%", player.getMaxBuffCount());
        player.sendPacket(html);
    }
    
    @Override
    public String[] getVoicedCommandList()
    {
        return _voicedCommands;
    }
}