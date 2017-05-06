/**
 * 
 */
package net.sf.l2j.gameserver.handler.admincommandhandlers;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.handler.IAdminCommandHandler;
import net.sf.l2j.gameserver.model.WorldObject;
import net.sf.l2j.gameserver.model.actor.instance.Player;
import net.sf.l2j.gameserver.model.entity.events.LMEvent;
import net.sf.l2j.gameserver.model.entity.events.LMEventTeleporter;
import net.sf.l2j.gameserver.model.entity.events.LMManager;

/**
 * @author L0ngh0rn
 */
public class AdminLMEvent implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_lm_add",
		"admin_lm_remove",
		"admin_lm_advance"
	};
	
	@Override
	public boolean useAdminCommand(String command, Player activeChar)
	{
		if (command.equals("admin_lm_add"))
		{
			WorldObject target = activeChar.getTarget();
			
			if (!(target instanceof Player))
			{
				activeChar.sendMessage("You should select a player!");
				return true;
			}
			
			add(activeChar, (Player) target);
		}
		else if (command.equals("admin_lm_remove"))
		{
			WorldObject target = activeChar.getTarget();
			
			if (!(target instanceof Player))
			{
				activeChar.sendMessage("You should select a player!");
				return true;
			}
			
			remove(activeChar, (Player) target);
		}
		else if (command.equals("admin_lm_advance"))
		{
			LMManager.getInstance().skipDelay();
		}
		
		return true;
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
	
	private static void add(Player activeChar, Player playerInstance)
	{
		if (LMEvent.isPlayerParticipant(playerInstance))
		{
			activeChar.sendMessage("Player already participated in the event!");
			return;
		}
		
		if (!LMEvent.addParticipant(playerInstance))
		{
			activeChar.sendMessage("Player instance could not be added, it seems to be null!");
			return;
		}
		
		if (LMEvent.isStarted())
		{
			new LMEventTeleporter(playerInstance, true, false);
		}
	}
	
	private static void remove(Player activeChar, Player playerInstance)
	{
		if (!LMEvent.removeParticipant(playerInstance))
		{
			activeChar.sendMessage("Player is not part of the event!");
			return;
		}
		
		new LMEventTeleporter(playerInstance, Config.LM_EVENT_PARTICIPATION_NPC_COORDINATES, true, true);
	}
}
