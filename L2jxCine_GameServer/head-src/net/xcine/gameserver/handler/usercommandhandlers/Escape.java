/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */
package net.xcine.gameserver.handler.usercommandhandlers;

import net.xcine.Config;
import net.xcine.gameserver.ai.CtrlIntention;
import net.xcine.gameserver.controllers.GameTimeController;
import net.xcine.gameserver.datatables.csv.MapRegionTable;
import net.xcine.gameserver.handler.IUserCommandHandler;
import net.xcine.gameserver.managers.GrandBossManager;
import net.xcine.gameserver.model.actor.instance.L2PcInstance;
import net.xcine.gameserver.model.entity.event.CTF;
import net.xcine.gameserver.model.entity.event.DM;
import net.xcine.gameserver.model.entity.event.TvT;
import net.xcine.gameserver.model.entity.event.VIP;
import net.xcine.gameserver.network.SystemMessageId;
import net.xcine.gameserver.network.serverpackets.MagicSkillUser;
import net.xcine.gameserver.network.serverpackets.SetupGauge;
import net.xcine.gameserver.network.serverpackets.SystemMessage;
import net.xcine.gameserver.thread.ThreadPoolManager;
import net.xcine.gameserver.util.Broadcast;


public class Escape implements IUserCommandHandler
{
	private static final int[] COMMAND_IDS =
	{
		52
	};

	@Override
	public boolean useUserCommand(int id, L2PcInstance activeChar)
	{

		int unstuckTimer = activeChar.getAccessLevel().isGm() ? 1000 : Config.UNSTUCK_INTERVAL * 1000;

		if(activeChar.isFestivalParticipant())
		{
			activeChar.sendMessage("You may not use an escape command in a festival.");
			return false;
		}

		if(activeChar._inEventTvT && TvT.is_started())
		{
			activeChar.sendMessage("You may not use an escape skill in TvT.");
			return false;
		}

		if(activeChar._inEventCTF && CTF.is_started())
		{
			activeChar.sendMessage("You may not use an escape skill in CTF.");
			return false;
		}

		if(activeChar._inEventDM && DM.is_started())
		{
			activeChar.sendMessage("You may not use an escape skill in DM.");
			return false;
		}

		if(activeChar._inEventVIP && VIP._started)
		{
			activeChar.sendMessage("You may not use an escape skill in VIP.");
			return false;
		}

		if(GrandBossManager.getInstance().getZone(activeChar) != null && !activeChar.isGM())
		{
			activeChar.sendMessage("You may not use an escape command in Grand boss zone.");
			return false;
		}

		if(activeChar.isInJail())
		{
			activeChar.sendMessage("You can not escape from jail.");
			return false;
		}

		if(activeChar.isInFunEvent())
		{
			activeChar.sendMessage("You may not escape from an Event.");
			return false;
		}
		
		if(activeChar.inObserverMode())
		{
			activeChar.sendMessage("You may not escape during Observer mode.");
			return false;
		}

		if(activeChar.isSitting())
		{
		    activeChar.sendMessage("You may not escape when you sitting.");
		    return false;
		}

		if(activeChar.isCastingNow() || activeChar.isMovementDisabled() || activeChar.isMuted() || activeChar.isAlikeDead() || activeChar.isInOlympiadMode() || activeChar.isAwaying())
			return false;

		SystemMessage sm = new SystemMessage(SystemMessageId.S1_S2);
		
		if(unstuckTimer<60000)
	         sm.addString("You use Escape: "+ unstuckTimer / 1000 +" seconds.");
	    else
	    	 sm.addString("You use Escape: "+ unstuckTimer / 60000 +" minutes.");
		
		activeChar.sendPacket(sm);
		sm = null;

		activeChar.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
		activeChar.setTarget(activeChar);
		activeChar.disableAllSkills();

		MagicSkillUser msk = new MagicSkillUser(activeChar, 1050, 1, unstuckTimer, 0);
		activeChar.setTarget(null);
		Broadcast.toSelfAndKnownPlayersInRadius(activeChar, msk, 810000/*900*/);
		SetupGauge sg = new SetupGauge(0, unstuckTimer);
		activeChar.sendPacket(sg);
		msk = null;
		sg = null;
		EscapeFinalizer ef = new EscapeFinalizer(activeChar);
		activeChar.setSkillCast(ThreadPoolManager.getInstance().scheduleGeneral(ef, unstuckTimer));
		activeChar.setSkillCastEndTime(10 + GameTimeController.getGameTicks() + unstuckTimer / GameTimeController.MILLIS_IN_TICK);

		ef = null;

		return true;
	}

	static class EscapeFinalizer implements Runnable
	{
		private L2PcInstance _activeChar;

		EscapeFinalizer(L2PcInstance activeChar)
		{
			_activeChar = activeChar;
		}

		@Override
		public void run()
		{
			if(_activeChar.isDead())
				return;

			_activeChar.setIsIn7sDungeon(false);
			_activeChar.enableAllSkills();

			try
			{
				if(_activeChar.getKarma()>0 && Config.ALT_KARMA_TELEPORT_TO_FLORAN){
					_activeChar.teleToLocation(17836, 170178, -3507, true); // Floran
					return;
				}
				
				_activeChar.teleToLocation(MapRegionTable.TeleportWhereType.Town);
			}
			catch(Throwable e)
			{
				if(Config.ENABLE_ALL_EXCEPTIONS)
					e.printStackTrace();
			}
		}
	}

	@Override
	public int[] getUserCommandList()
	{
		return COMMAND_IDS;
	}
}
