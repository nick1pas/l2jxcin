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
package net.xcine.gameserver.network.clientpackets;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import net.xcine.Config;
import net.xcine.gameserver.Announcements;
import net.xcine.gameserver.GameTimeController;
import net.xcine.gameserver.SevenSigns;
import net.xcine.gameserver.datatables.SkillTable;
import net.xcine.gameserver.datatables.AdminCommandAccessRights;
import net.xcine.gameserver.datatables.GmListTable;
import net.xcine.gameserver.datatables.MapRegionTable;
import net.xcine.gameserver.datatables.SkillTable.FrequentSkill;
import net.xcine.gameserver.instancemanager.ClanHallManager;
import net.xcine.gameserver.instancemanager.CoupleManager;
import net.xcine.gameserver.instancemanager.DimensionalRiftManager;
import net.xcine.gameserver.instancemanager.PetitionManager;
import net.xcine.gameserver.instancemanager.QuestManager;
import net.xcine.gameserver.instancemanager.SiegeManager;
import net.xcine.gameserver.model.L2Clan;
import net.xcine.gameserver.model.L2World;
import net.xcine.gameserver.model.L2Skill;
import net.xcine.gameserver.model.actor.L2Character;
import net.xcine.gameserver.model.actor.instance.L2PcInstance;
import net.xcine.gameserver.model.entity.ClanHall;
import net.xcine.gameserver.model.entity.Couple;
import net.xcine.gameserver.model.entity.Siege;
import net.xcine.gameserver.model.olympiad.Olympiad;
import net.xcine.gameserver.model.quest.Quest;
import net.xcine.gameserver.model.quest.QuestState;
import net.xcine.gameserver.network.SystemMessageId;
import net.xcine.gameserver.network.serverpackets.CreatureSay;
import net.xcine.gameserver.network.serverpackets.Die;
import net.xcine.gameserver.network.serverpackets.EtcStatusUpdate;
import net.xcine.gameserver.network.serverpackets.ExStorageMaxCount;
import net.xcine.gameserver.network.serverpackets.FriendList;
import net.xcine.gameserver.network.serverpackets.HennaInfo;
import net.xcine.gameserver.network.serverpackets.ItemList;
import net.xcine.gameserver.network.serverpackets.NpcHtmlMessage;
import net.xcine.gameserver.network.serverpackets.PledgeShowMemberListAll;
import net.xcine.gameserver.network.serverpackets.PledgeShowMemberListUpdate;
import net.xcine.gameserver.network.serverpackets.PledgeSkillList;
import net.xcine.gameserver.network.serverpackets.PledgeStatusChanged;
import net.xcine.gameserver.network.serverpackets.QuestList;
import net.xcine.gameserver.network.serverpackets.ShortCutInit;
import net.xcine.gameserver.network.serverpackets.SkillCoolTime;
import net.xcine.gameserver.network.serverpackets.SystemMessage;
import net.xcine.gameserver.network.serverpackets.UserInfo;

public class EnterWorld extends L2GameClientPacket
{
	      
	long _daysleft;
	SimpleDateFormat df = new SimpleDateFormat("dd MM yyyy");
	     
	@Override
	protected void readImpl()
	{
		// this is just a trigger packet. it has no content
	}
	
	@Override
	protected void runImpl()
	{
		final L2PcInstance activeChar = getClient().getActiveChar();
		if (activeChar == null)
		{
			_log.warning("EnterWorld failed! activeChar is null...");
			getClient().closeNow();
			return;
		}
		
		if (L2World.getInstance().findObject(activeChar.getObjectId()) != null)
		{
			if (Config.DEBUG)
				_log.warning("User already exist in OID map! User " + activeChar.getName() + " is character clone.");
		}
		
		if (activeChar.isGM())
		{
			if (Config.GM_STARTUP_INVULNERABLE && AdminCommandAccessRights.getInstance().hasAccess("admin_invul", activeChar.getAccessLevel()))
				activeChar.setIsInvul(true);
			
			if (Config.GM_STARTUP_INVISIBLE && AdminCommandAccessRights.getInstance().hasAccess("admin_hide", activeChar.getAccessLevel()))
				activeChar.getAppearance().setInvisible();
			
			if (Config.GM_STARTUP_SILENCE && AdminCommandAccessRights.getInstance().hasAccess("admin_silence", activeChar.getAccessLevel()))
				activeChar.setInRefusalMode(true);
			
			if (Config.GM_STARTUP_AUTO_LIST && AdminCommandAccessRights.getInstance().hasAccess("admin_gmliston", activeChar.getAccessLevel()))
				GmListTable.getInstance().addGm(activeChar, false);
			else
				GmListTable.getInstance().addGm(activeChar, true);
		}
		
		// Set dead status if applies
		if (activeChar.getCurrentHp() < 0.5)
			activeChar.setIsDead(true);
		
		if (activeChar.getClan() != null)
		{
			activeChar.sendPacket(new PledgeSkillList(activeChar.getClan()));
			notifyClanMembers(activeChar);
			notifySponsorOrApprentice(activeChar);
			
			// Add message at connexion if clanHall not paid.
			ClanHall clanHall = ClanHallManager.getInstance().getClanHallByOwner(activeChar.getClan());
			if (clanHall != null)
			{
				if (!clanHall.getPaid())
					activeChar.sendPacket(SystemMessageId.PAYMENT_FOR_YOUR_CLAN_HALL_HAS_NOT_BEEN_MADE_PLEASE_MAKE_PAYMENT_TO_YOUR_CLAN_WAREHOUSE_BY_S1_TOMORROW);
			}
			
			for (Siege siege : SiegeManager.getSieges())
			{
				if (!siege.getIsInProgress())
					continue;
				
				if (siege.checkIsAttacker(activeChar.getClan()))
					activeChar.setSiegeState((byte) 1);
				else if (siege.checkIsDefender(activeChar.getClan()))
					activeChar.setSiegeState((byte) 2);
			}
			
			activeChar.sendPacket(new PledgeShowMemberListAll(activeChar.getClan(), activeChar));
			activeChar.sendPacket(new PledgeStatusChanged(activeChar.getClan()));
		}
		
		// Updating Seal of Strife Buff/Debuff
		if (SevenSigns.getInstance().isSealValidationPeriod() && SevenSigns.getInstance().getSealOwner(SevenSigns.SEAL_STRIFE) != SevenSigns.CABAL_NULL)
		{
			int cabal = SevenSigns.getInstance().getPlayerCabal(activeChar.getObjectId());
			if (cabal != SevenSigns.CABAL_NULL)
			{
				if (cabal == SevenSigns.getInstance().getSealOwner(SevenSigns.SEAL_STRIFE))
					activeChar.addSkill(FrequentSkill.THE_VICTOR_OF_WAR.getSkill());
				else
					activeChar.addSkill(FrequentSkill.THE_VANQUISHED_OF_WAR.getSkill());
			}
		}
		else
		{
			activeChar.removeSkill(FrequentSkill.THE_VICTOR_OF_WAR.getSkill());
			activeChar.removeSkill(FrequentSkill.THE_VANQUISHED_OF_WAR.getSkill());
		}
		
		if (Config.PLAYER_SPAWN_PROTECTION > 0)
			activeChar.setProtection(true);
		
		activeChar.spawnMe(activeChar.getX(), activeChar.getY(), activeChar.getZ());
		
		// buff and status icons
		if (Config.STORE_SKILL_COOLTIME)
			activeChar.restoreEffects();
		
		// engage and notify Partner
		if (Config.ALLOW_WEDDING)
			engage(activeChar);
		
		// Welcome to Lineage II
		activeChar.sendPacket(SystemMessageId.WELCOME_TO_LINEAGE);
		
		// Credits to L2JxCine
		if (activeChar.getLevel() == 1)
		{	
			activeChar.sendMessage("This server uses L2JxCine");
			activeChar.sendMessage("www.L2JxCine.com");
   	    }

		SevenSigns.getInstance().sendCurrentPeriodMsg(activeChar);
		Announcements.getInstance().showAnnouncements(activeChar);
		
		// if player is DE, check for shadow sense skill at night
		if (activeChar.getRace().ordinal() == 2)
		{
			// If player got the skill (exemple : low level DEs haven't it)
			if (activeChar.getSkillLevel(294) == 1)
			{
				if (GameTimeController.getInstance().isNowNight())
					activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.NIGHT_EFFECT_APPLIES).addSkillName(294));
				else
					activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.DAY_EFFECT_DISAPPEARS).addSkillName(294));
			}
		}
		
		activeChar.getMacroses().sendUpdate();
		activeChar.sendPacket(new UserInfo(activeChar));
		activeChar.sendPacket(new HennaInfo(activeChar));
		activeChar.sendPacket(new FriendList(activeChar));
		// activeChar.queryGameGuard();
		activeChar.sendPacket(new ItemList(activeChar, false));
		activeChar.sendPacket(new ShortCutInit(activeChar));
		activeChar.sendPacket(new ExStorageMaxCount(activeChar));
		activeChar.sendPacket(new EtcStatusUpdate(activeChar));
		activeChar.sendSkillList();
		
		Quest.playerEnter(activeChar);
		if (!Config.DISABLE_TUTORIAL)
			loadTutorial(activeChar);
		loadTutorial(activeChar);
		   onEnterNewbie(activeChar);
		  
		for (Quest quest : QuestManager.getInstance().getAllManagedScripts())
		{
			if (quest != null && quest.getOnEnterWorld())
				quest.notifyEnterWorld(activeChar);
		}
		activeChar.sendPacket(new QuestList());
        
               if(activeChar.isAio())
                       onEnterAio(activeChar);
                      
               if(Config.ALLOW_AIO_NCOLOR && activeChar.isAio())
                       activeChar.getAppearance().setNameColor(Config.AIO_NCOLOR);
                                      
               if(Config.ALLOW_AIO_TCOLOR && activeChar.isAio())
                       activeChar.getAppearance().setTitleColor(Config.AIO_TCOLOR);
               
		if (Config.SERVER_NEWS)
		{
			NpcHtmlMessage html = new NpcHtmlMessage(0);
			html.setFile("data/html/servnews.htm");
			sendPacket(html);
		}
		
		PetitionManager.getInstance().checkPetitionMessages(activeChar);
		
		// no broadcast needed since the player will already spawn dead to others
		if (activeChar.isAlikeDead())
			sendPacket(new Die(activeChar));
		
		activeChar.onPlayerEnter();
		
		sendPacket(new SkillCoolTime(activeChar));
		
		// If player logs back in a stadium, port him in nearest town.
		if (Olympiad.getInstance().playerInStadia(activeChar))
			activeChar.teleToLocation(MapRegionTable.TeleportWhereType.Town);
		
		if (DimensionalRiftManager.getInstance().checkIfInRiftZone(activeChar.getX(), activeChar.getY(), activeChar.getZ(), false))
			DimensionalRiftManager.getInstance().teleportToWaitingRoom(activeChar);
		
		if (activeChar.getClanJoinExpiryTime() > System.currentTimeMillis())
			activeChar.sendPacket(SystemMessageId.CLAN_MEMBERSHIP_TERMINATED);
		
		// Attacker or spectator logging into a siege zone will be ported at town.
		if (!activeChar.isGM() && (!activeChar.isInSiege() || activeChar.getSiegeState() < 2) && activeChar.isInsideZone(L2Character.ZONE_SIEGE))
			activeChar.teleToLocation(MapRegionTable.TeleportWhereType.Town);
	
	   if(Config.NEW_PLAYER_BUFFS && activeChar.getLevel() == 1)
	   {
	   if(activeChar.isMageClass())
	   {
	       for(Integer skillid : Config.MAGE_BUFF_LIST.keySet())
	       {
	        int skilllvl = Config.MAGE_BUFF_LIST.get(skillid);
	        L2Skill skill = SkillTable.getInstance().getInfo(skillid, skilllvl);
	        if(skill != null)
	         skill.getEffects(activeChar, activeChar);
	       }
	   }
	   else
	   {
	       for(Integer skillid : Config.FIGHTER_BUFF_LIST.keySet())
	       {
	        int skilllvl = Config.FIGHTER_BUFF_LIST.get(skillid);
	       L2Skill skill = SkillTable.getInstance().getInfo(skillid, skilllvl);
	        if(skill != null)
	         skill.getEffects(activeChar, activeChar);
	       }
	   }
	  }
	 }
	
	/**
	 * @param activeChar
	 */
	private void onEnterNewbie(L2PcInstance activeChar)
	{
		
	}
	
	       private void onEnterAio(L2PcInstance activeChar)
	       {
	               long now = Calendar.getInstance().getTimeInMillis();
	               long endDay = activeChar.getAioEndTime();
	               if(now > endDay)
	               {
	                       activeChar.setAio(false);
	                       activeChar.setAioEndTime(0);
	                       activeChar.lostAioSkills();
	                       activeChar.removeExpAndSp(6299994999L, 366666666);
	                       if(Config.ALLOW_AIO_ITEM)
	                       {
	                               activeChar.getInventory().destroyItemByItemId("", Config.AIO_ITEMID, 1, activeChar, null);
	                               activeChar.getWarehouse().destroyItemByItemId("", Config.AIO_ITEMID, 1, activeChar, null);
	                       }
	                       activeChar.sendPacket(new CreatureSay(0,Say2.HERO_VOICE,"System","Your AIO period ends."));
	               }
	               else
	               {
	                       Date dt = new Date(endDay);
	                       _daysleft = (endDay - now)/86400000;
	                       if(_daysleft > 30)
	                               activeChar.sendMessage("AIO period ends in " + df.format(dt) + ".");
	                       else if(_daysleft > 0)
	                               activeChar.sendMessage("Left " + (int)_daysleft + " days for AIO period ends");
	                       else if(_daysleft < 1)
	                       {
	                               long hour = (endDay - now)/3600000;
	                               activeChar.sendMessage("Left " + (int)hour + " hours to AIO period ends");
	                       }
	               }
	       }
	
	private static void engage(L2PcInstance cha)
	{
		int _chaid = cha.getObjectId();
		
		for (Couple cl : CoupleManager.getInstance().getCouples())
		{
			if (cl.getPlayer1Id() == _chaid || cl.getPlayer2Id() == _chaid)
			{
				if (cl.getMaried())
					cha.setMarried(true);
				
				cha.setCoupleId(cl.getId());
			}
		}
	}
	
	private static void notifyClanMembers(L2PcInstance activeChar)
	{
		L2Clan clan = activeChar.getClan();
		clan.getClanMember(activeChar.getName()).setPlayerInstance(activeChar);
		
		SystemMessage msg = SystemMessage.getSystemMessage(SystemMessageId.CLAN_MEMBER_S1_LOGGED_IN);
		msg.addPcName(activeChar);
		
		clan.broadcastToOtherOnlineMembers(msg, activeChar);
		clan.broadcastToOtherOnlineMembers(new PledgeShowMemberListUpdate(activeChar), activeChar);
		
		msg = null;
	}
	
	private static void notifySponsorOrApprentice(L2PcInstance activeChar)
	{
		if (activeChar.getSponsor() != 0)
		{
			L2PcInstance sponsor = L2World.getInstance().getPlayer(activeChar.getSponsor());
			if (sponsor != null)
				sponsor.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.YOUR_APPRENTICE_S1_HAS_LOGGED_IN).addPcName(activeChar));
		}
		else if (activeChar.getApprentice() != 0)
		{
			L2PcInstance apprentice = L2World.getInstance().getPlayer(activeChar.getApprentice());
			if (apprentice != null)
				apprentice.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.YOUR_SPONSOR_S1_HAS_LOGGED_IN).addPcName(activeChar));
		}
	}
	
	private static void loadTutorial(L2PcInstance player)
	{
		QuestState qs = player.getQuestState("Tutorial");
		if (qs != null)
			qs.getQuest().notifyEvent("UC", null, player);
	}
	
	@Override
	protected boolean triggersOnActionRequest()
	{
		return false;
	}
}