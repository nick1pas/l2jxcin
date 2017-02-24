package net.sf.l2j.gameserver.model.actor.instance;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.StringTokenizer;

import net.sf.l2j.gameserver.ai.CtrlIntention;
import net.sf.l2j.gameserver.model.World;
import net.sf.l2j.gameserver.model.actor.template.NpcTemplate;
import net.sf.l2j.gameserver.network.L2GameClient;
import net.sf.l2j.gameserver.network.clientpackets.Say2;
import net.sf.l2j.gameserver.network.serverpackets.ActionFailed;
import net.sf.l2j.gameserver.network.serverpackets.CreatureSay;
import net.sf.l2j.gameserver.network.serverpackets.MyTargetSelected;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;
import net.sf.l2j.gameserver.network.serverpackets.ValidateLocation;

/**
 * @author squallcs
 * @rework by Leonardo Holanda
 */
public class BugReport extends Folk
{
    private static String _type;
    
    public BugReport(int objectId, NpcTemplate template)
    {
        super(objectId, template);
    }
    
    @Override
    public void onBypassFeedback(Player player, String command)
    {
        if (command.startsWith("send_report"))
        {
            StringTokenizer st = new StringTokenizer(command);
            st.nextToken();
            String msg = null;
            String type = null;
            type = st.nextToken();
            msg = st.nextToken();
            try
            {
                while (st.hasMoreTokens())
                {
                    msg = msg + " " + st.nextToken();
                }
                
                sendReport(player, type, msg);
            }
            catch (StringIndexOutOfBoundsException e)
            {
                _log.warning("Can not leave blank");
            }
        }
    }
    
    static
    {
        new File("log/BugReports/").mkdirs();
    }
    
    private static void sendReport(Player player, String command, String msg)
    {
        String type = command;
        L2GameClient info = player.getClient().getConnection().getClient();
        
        if (type.equals("General"))
            _type = "General";
        if (type.equals("Fatal"))
            _type = "Fatal";
        if (type.equals("Misuse"))
            _type = "Misuse";
        if (type.equals("Balance"))
            _type = "Balance";
        if (type.equals("Other"))
            _type = "Other";
        
        try
        {
            String fname = "log/BugReports/" + player.getName() + ".txt";
            File file = new File(fname);
            boolean exist = file.createNewFile();
            if (!exist)
            {
                player.sendMessage("You have already sent a bug report, GMs must check it first.");
                return;
            }
            FileWriter fstream = new FileWriter(fname);
            BufferedWriter out = new BufferedWriter(fstream);
            out.write("Character Info: " + info + "\r\nBug Type: " + _type + "\r\nMessage: " + msg);
            player.sendMessage("Report sent. GMs will check it soon. Thanks...");
            
            for (Player allgms : World.getAllGMs())
                allgms.sendPacket(new CreatureSay(0, Say2.SHOUT, "Bug Report Manager", player.getName() + " sent a bug report."));
            
            System.out.println("Character: " + player.getName() + " sent a bug report.");
            out.close();
        }
        catch (Exception e)
        {
            player.sendMessage("Something went wrong try again.");
        }
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
                showMessageWindow(player);
            }
        }
        
        // Send a Server->Client ActionFailed to the Player in order to avoid that the client wait another packet
        player.sendPacket(ActionFailed.STATIC_PACKET);
    }

    private void showMessageWindow(Player player)
    {
        String filename = "data/html/mods/BugReport.htm";
        NpcHtmlMessage html = new NpcHtmlMessage(1);
        html.setFile(filename);
        html.replace("%objectId%", String.valueOf(getObjectId()));
        html.replace("%name%", player.getName());
        player.sendPacket(html);
    }
    
}