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

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import net.xcine.Config;
import net.xcine.crypt.nProtect;
import net.xcine.crypt.nProtect.RestrictionType;
import net.xcine.gameserver.communitybbs.Manager.RegionBBSManager;
import net.xcine.gameserver.controllers.GameTimeController;
import net.xcine.gameserver.datatables.GmListTable;
import net.xcine.gameserver.datatables.SkillTable;
import net.xcine.gameserver.datatables.csv.MapRegionTable;
import net.xcine.gameserver.datatables.sql.AdminCommandAccessRights;
import net.xcine.gameserver.managers.CastleManager;
import net.xcine.gameserver.managers.ClanHallManager;
import net.xcine.gameserver.managers.CoupleManager;
import net.xcine.gameserver.managers.CrownManager;
import net.xcine.gameserver.managers.DimensionalRiftManager;
import net.xcine.gameserver.managers.FortSiegeManager;
import net.xcine.gameserver.managers.PetitionManager;
import net.xcine.gameserver.managers.SiegeManager;
import net.xcine.gameserver.model.Inventory;
import net.xcine.gameserver.model.L2Character;
import net.xcine.gameserver.model.L2Clan;
import net.xcine.gameserver.model.L2Effect;
import net.xcine.gameserver.model.L2Skill;
import net.xcine.gameserver.model.L2World;
import net.xcine.gameserver.model.actor.instance.L2ClassMasterInstance;
import net.xcine.gameserver.model.actor.instance.L2ItemInstance;
import net.xcine.gameserver.model.actor.instance.L2PcInstance;
import net.xcine.gameserver.model.base.ClassLevel;
import net.xcine.gameserver.model.base.PlayerClass;
import net.xcine.gameserver.model.entity.Announcements;
import net.xcine.gameserver.model.entity.ClanHall;
import net.xcine.gameserver.model.entity.Hero;
import net.xcine.gameserver.model.entity.Wedding;
import net.xcine.gameserver.model.entity.event.CTF;
import net.xcine.gameserver.model.entity.event.DM;
import net.xcine.gameserver.model.entity.event.L2Event;
import net.xcine.gameserver.model.entity.event.TvT;
import net.xcine.gameserver.model.entity.olympiad.Olympiad;
import net.xcine.gameserver.model.entity.sevensigns.SevenSigns;
import net.xcine.gameserver.model.entity.siege.Castle;
import net.xcine.gameserver.model.entity.siege.FortSiege;
import net.xcine.gameserver.model.entity.siege.Siege;
import net.xcine.gameserver.model.quest.Quest;
import net.xcine.gameserver.model.quest.QuestState;
import net.xcine.gameserver.network.Disconnection;
import net.xcine.gameserver.network.SystemMessageId;
import net.xcine.gameserver.network.serverpackets.ClientSetTime;
import net.xcine.gameserver.network.serverpackets.CreatureSay;
import net.xcine.gameserver.network.serverpackets.Die;
import net.xcine.gameserver.network.serverpackets.Earthquake;
import net.xcine.gameserver.network.serverpackets.EtcStatusUpdate;
import net.xcine.gameserver.network.serverpackets.ExShowScreenMessage;
import net.xcine.gameserver.network.serverpackets.ExStorageMaxCount;
import net.xcine.gameserver.network.serverpackets.FriendList;
import net.xcine.gameserver.network.serverpackets.HennaInfo;
import net.xcine.gameserver.network.serverpackets.ItemList;
import net.xcine.gameserver.network.serverpackets.MagicSkillUser;
import net.xcine.gameserver.network.serverpackets.NpcHtmlMessage;
import net.xcine.gameserver.network.serverpackets.PledgeShowMemberListAll;
import net.xcine.gameserver.network.serverpackets.PledgeShowMemberListUpdate;
import net.xcine.gameserver.network.serverpackets.PledgeSkillList;
import net.xcine.gameserver.network.serverpackets.PledgeStatusChanged;
import net.xcine.gameserver.network.serverpackets.QuestList;
import net.xcine.gameserver.network.serverpackets.ShortCutInit;
import net.xcine.gameserver.network.serverpackets.SignsSky;
import net.xcine.gameserver.network.serverpackets.SystemMessage;
import net.xcine.gameserver.network.serverpackets.UserInfo;
import net.xcine.gameserver.thread.TaskPriority;
import net.xcine.gameserver.thread.ThreadPoolManager;
import net.xcine.gameserver.util.Util;

/**
 * Enter World Packet Handler
 */
public class EnterWorld extends L2GameClientPacket
{
	private static Logger _log = Logger.getLogger(EnterWorld.class.getName());

	private static final SimpleDateFormat fmt = new SimpleDateFormat("HH:mm.");
	private long _daysleft;
	SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
	
	public TaskPriority getPriority()
	{
		return TaskPriority.PR_URGENT;
	}

	@Override
	protected void readImpl()
	{
		// this is just a trigger packet. it has no content
	}

	@Override
	protected void runImpl()
	{
		L2PcInstance activeChar = getClient().getActiveChar();

		if (activeChar == null)
		{
			_log.warning("EnterWorld failed! activeChar is null...");
			getClient().closeNow();
			return;
		}
		
		// Set lock at login
		activeChar.setLocked(true);

		// Register in flood protector
		//FloodProtector.getInstance().registerNewPlayer(activeChar.getObjectId());

		if (L2World.getInstance().findObject(activeChar.getObjectId()) != null)
		{
			if (Config.DEBUG)
			{
				_log.warning("DEBUG "+getType()+": User already exist in OID map! User " + activeChar.getName() + " is character clone");
				//activeChar.closeNetConnection(); // Do nothing?
			}
		}
		
		if (!activeChar.isGM() && !activeChar.isDonator() && Config.CHECK_NAME_ON_LOGIN)
		{
			if (activeChar.getName().length() < 3 || activeChar.getName().length() > 16 || !Util.isAlphaNumeric(activeChar.getName()) || !isValidName(activeChar.getName()))
			{
				_log.warning("Charname: " + activeChar.getName() + " is invalid. EnterWorld failed.");
				getClient().closeNow();
				return;
			}	
		}

		// Set online status
		activeChar.setOnlineStatus(true);

		activeChar.setRunning(); // running is default
		activeChar.standUp(); // standing is default
		
		activeChar.broadcastKarma(); //include UserInfo
		
		// Engage and notify Partner
		if (Config.L2JMOD_ALLOW_WEDDING)
		{
			engage(activeChar);
			notifyPartner(activeChar, activeChar.getPartnerId());
		}

		EnterGM(activeChar);

		Quest.playerEnter(activeChar);
		activeChar.sendPacket(new QuestList());

		if (Config.PLAYER_SPAWN_PROTECTION > 0)
			activeChar.setProtection(true);

		activeChar.spawnMe(activeChar.getX(), activeChar.getY(), activeChar.getZ());

		if (SevenSigns.getInstance().isSealValidationPeriod())
			sendPacket(new SignsSky());

		// Buff and Status icons
		if(Config.STORE_SKILL_COOLTIME)
		{
			activeChar.restoreEffects();
		}

		activeChar.sendPacket(new EtcStatusUpdate(activeChar));
		
		final L2Effect[] effects = activeChar.getAllEffects();
		
		if (effects != null)
		{
			for (L2Effect e : effects)
			{
				if (e.getEffectType() == L2Effect.EffectType.HEAL_OVER_TIME)
				{
					activeChar.stopEffects(L2Effect.EffectType.HEAL_OVER_TIME);
					activeChar.removeEffect(e);
				}
				if (e.getEffectType() == L2Effect.EffectType.COMBAT_POINT_HEAL_OVER_TIME)
				{
					activeChar.stopEffects(L2Effect.EffectType.COMBAT_POINT_HEAL_OVER_TIME);
					activeChar.removeEffect(e);
				}
			}
		}

		// Apply augmentation boni for equipped items
		for (L2ItemInstance temp : activeChar.getInventory().getAugmentedItems())
			if (temp != null && temp.isEquipped())
				temp.getAugmentation().applyBoni(activeChar);
		
		// Apply death penalty
		activeChar.restoreDeathPenaltyBuffLevel();

		if(L2Event.active && L2Event.connectionLossData.containsKey(activeChar.getName()) && L2Event.isOnEvent(activeChar))
			L2Event.restoreChar(activeChar);
		else if (L2Event.connectionLossData.containsKey(activeChar.getName()))
			L2Event.restoreAndTeleChar(activeChar);

		// SECURE FIX - Anti Overenchant Cheat!!
		if(Config.MAX_ITEM_ENCHANT_KICK >0)
		{
			for (L2ItemInstance i : activeChar.getInventory().getItems())
			{
				if (!activeChar.isGM())
				{	
					if (i.isEquipable())
					{	
						if (i.getEnchantLevel() > Config.MAX_ITEM_ENCHANT_KICK)
						{                        
							//Delete Item Over enchanted
							activeChar.getInventory().destroyItem(null, i, activeChar, null);
							//Message to Player
							activeChar.sendMessage("[Server]: You have over enchanted items you will be kicked from server!");
							activeChar.sendMessage("[Server]: Respect our server rules.");
							//Message with screen
							sendPacket(new ExShowScreenMessage(" You have an over enchanted item, you will be kicked from server! ", 6000));
							//Punishment e log in audit
							Util.handleIllegalPlayerAction(activeChar, "Player " + activeChar.getName() + " has Overenchanted  item! Kicked! ", Config.DEFAULT_PUNISH);                     
							//Logger in console
							_log.info("#### ATTENTION ####");
							_log.info(i+" item has been removed from "+activeChar);
						}

					}
				}
			}
		}
  
		// Restores custom status
		activeChar.restoreCustomStatus();

		ColorSystem(activeChar);

		// Expand Skill
		ExStorageMaxCount esmc = new ExStorageMaxCount(activeChar);
		activeChar.sendPacket(esmc);

		activeChar.getMacroses().sendUpdate();
		
		// Send packets info
		sendPacket(new ClientSetTime()); // SetClientTime
		sendPacket(new UserInfo(activeChar)); //
		sendPacket(new HennaInfo(activeChar));
		sendPacket(new FriendList(activeChar));
		sendPacket(new ItemList(activeChar, false));
		sendPacket(new ShortCutInit(activeChar));

		// Send all skills to char
		activeChar.sendSkillList(); 
		
		// Reload inventory to give SA skill
		activeChar.getInventory().reloadEquippedItems();
		
		
		// Welcome to Lineage II
		sendPacket(new SystemMessage(SystemMessageId.WELCOME_TO_LINEAGE));
		
		// Credits to L2JxCine
		if (activeChar.getLevel() == 1)
		{	
			activeChar.sendMessage("This server uses L2JxCine");
			activeChar.sendMessage("www.L2JxCine.com");
   	    }

		SevenSigns.getInstance().sendCurrentPeriodMsg(activeChar);
		Announcements.getInstance().showAnnouncements(activeChar);

		loadTutorial(activeChar);
		
		// Check for crowns
		CrownManager.getInstance().checkCrowns(activeChar);

		// Check player skills
		if (Config.CHECK_SKILLS_ON_ENTER && !Config.ALT_GAME_SKILL_LEARN)
		    if (!activeChar.isAio())
		    		activeChar.checkAllowedSkills();

		PetitionManager.getInstance().checkPetitionMessages(activeChar);

		// Send user info again .. just like the real client
		//sendPacket(ui);

		if (activeChar.getClanId() != 0 && activeChar.getClan() != null)
		{
			sendPacket(new PledgeShowMemberListAll(activeChar.getClan(), activeChar));
			sendPacket(new PledgeStatusChanged(activeChar.getClan()));
		}

		if (activeChar.isAlikeDead())
			sendPacket(new Die(activeChar)); // No broadcast needed since the player will already spawn dead to others

		if (Config.ALLOW_WATER)
			activeChar.checkWaterState();

		if (Hero.getInstance().getHeroes() != null && Hero.getInstance().getHeroes().containsKey(activeChar.getObjectId()))
			activeChar.setHero(true);

		setPledgeClass(activeChar);

		for (String name : activeChar.getFriendList())
		{
			L2PcInstance friend = L2World.getInstance().getPlayer(name);

			if (friend != null) //friend logged in.
				friend.sendPacket(new SystemMessage(SystemMessageId.FRIEND_S1_HAS_LOGGED_IN).addString(activeChar.getName()));
		}
		
		notifyClanMembers(activeChar);
		notifySponsorOrApprentice(activeChar);

		activeChar.setTarget(activeChar);
		
		activeChar.onPlayerEnter();

		if (Config.PCB_ENABLE)
			activeChar.showPcBangWindow();

		if (Config.ANNOUNCE_CASTLE_LORDS)
				notifyCastleOwner(activeChar);
		
		if (Olympiad.getInstance().playerInStadia(activeChar))
		{
			activeChar.teleToLocation(MapRegionTable.TeleportWhereType.Town);
			activeChar.sendMessage("You have been teleported to the nearest town due to you being in an Olympiad Stadium");
		}

		if (DimensionalRiftManager.getInstance().checkIfInRiftZone(activeChar.getX(), activeChar.getY(), activeChar.getZ(), false))
			DimensionalRiftManager.getInstance().teleportToWaitingRoom(activeChar);

		if (activeChar.getClanJoinExpiryTime() > System.currentTimeMillis())
			activeChar.sendPacket(new SystemMessage(SystemMessageId.CLAN_MEMBERSHIP_TERMINATED));

		if (activeChar.getClan() != null)
		{
			activeChar.sendPacket(new PledgeSkillList(activeChar.getClan()));

			for (Siege siege : SiegeManager.getInstance().getSieges())
			{
				if (!siege.getIsInProgress())
					continue;

				if (siege.checkIsAttacker(activeChar.getClan()))
				{
					activeChar.setSiegeState((byte) 1);
					break;
				}
				else if (siege.checkIsDefender(activeChar.getClan()))
				{
					activeChar.setSiegeState((byte) 2);
					break;
				}
			}

			for (FortSiege fortsiege : FortSiegeManager.getInstance().getSieges())
			{
				if (!fortsiege.getIsInProgress())
					continue;

				if (fortsiege.checkIsAttacker(activeChar.getClan()))
				{
					activeChar.setSiegeState((byte) 1);
					break;
				}
				else if (fortsiege.checkIsDefender(activeChar.getClan()))
				{
					activeChar.setSiegeState((byte) 2);
					break;
				}
			}

			// Add message at connexion if clanHall not paid. Possibly this is custom...
			ClanHall clanHall = ClanHallManager.getInstance().getClanHallByOwner(activeChar.getClan());

			if (clanHall != null)
				if (!clanHall.getPaid())
					activeChar.sendPacket(new SystemMessage(SystemMessageId.PAYMENT_FOR_YOUR_CLAN_HALL_HAS_NOT_BEEN_MADE_PLEASE_MAKE_PAYMENT_TO_YOUR_CLAN_WAREHOUSE_BY_S1_TOMORROW));
		}

		if (!activeChar.isGM() && activeChar.getSiegeState() < 2 && activeChar.isInsideZone(L2Character.ZONE_SIEGE))
		{
			// Attacker or spectator logging in to a siege zone. Actually should be checked for inside castle only?
			activeChar.teleToLocation(MapRegionTable.TeleportWhereType.Town);
			activeChar.sendMessage("You have been teleported to the nearest town due to you being in siege zone");
		}

		RegionBBSManager.getInstance().changeCommunityBoard();

		if (TvT._savePlayers.contains(activeChar.getName()))
			TvT.addDisconnectedPlayer(activeChar);

		if (CTF._savePlayers.contains(activeChar.getName()))
			CTF.addDisconnectedPlayer(activeChar);

		if (DM._savePlayers.contains(activeChar.getName()))
			DM.addDisconnectedPlayer(activeChar);

		// Means that it's not ok multiBox situation, so logout
		if (!activeChar.checkMultiBox())
		{
			activeChar.sendMessage("I'm sorry, but multibox is not allowed here.");
			activeChar.logout();
		}
		
		Hellows(activeChar);
		
		if (Config.ALLOW_CLASS_MASTERS && Config.ALLOW_REMOTE_CLASS_MASTERS)
		{
			final L2ClassMasterInstance master_instance = L2ClassMasterInstance.getInstance();
			
			if(master_instance != null){
				
				ClassLevel lvlnow = PlayerClass.values()[activeChar.getClassId().getId()].getLevel();
	
				if(activeChar.getLevel() >= 20 && lvlnow == ClassLevel.First)
					L2ClassMasterInstance.getInstance().onAction(activeChar);
				else if(activeChar.getLevel() >= 40 && lvlnow == ClassLevel.Second)
					L2ClassMasterInstance.getInstance().onAction(activeChar);
				else if(activeChar.getLevel() >= 76 && lvlnow == ClassLevel.Third)
					L2ClassMasterInstance.getInstance().onAction(activeChar);
				
			}else{
				
				_log.info("Attention: Remote ClassMaster is Enabled, but not inserted into DataBase. Remember to install 31288 Custom_Npc ..");
				
			}
		}
		
		// Apply night/day bonus on skill Shadow Sense
		if (activeChar.getRace().ordinal() == 2)
		{
			final L2Skill skill = SkillTable.getInstance().getInfo(294, 1);
			if (skill != null && activeChar.getSkillLevel(294) == 1)
			{
				if (GameTimeController.getInstance().isNowNight())
				{
					SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.NIGHT_EFFECT_APPLIES);
					sm.addSkillName(294);
					sendPacket(sm);
					activeChar.sendSkillList(); 
				}
				else
				{
					SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.DAY_EFFECT_DISAPPEARS);
					sm.addSkillName(294);
					sendPacket(sm);
					activeChar.sendSkillList(); 
				}
			}
		}

		if (!nProtect.getInstance().checkRestriction(activeChar, RestrictionType.RESTRICT_ENTER))
		{
			activeChar.setIsImobilised(true);
			activeChar.disableAllSkills();
			ThreadPoolManager.getInstance().scheduleGeneral(new Disconnection(activeChar), 20000);
		}
		
		// Elrokian Trap like L2OFF
		L2ItemInstance rhand = activeChar.getInventory().getPaperdollItem(Inventory.PAPERDOLL_RHAND);
		if (rhand != null && rhand.getItemId() == 8763)
		{
				activeChar.addSkill(SkillTable.getInstance().getInfo(3626, 1));
				activeChar.addSkill(SkillTable.getInstance().getInfo(3627, 1));
				activeChar.addSkill(SkillTable.getInstance().getInfo(3628, 1));
				activeChar.sendSkillList();
		}
		else
		{
			activeChar.removeSkill(3626, true);
			activeChar.removeSkill(3627, true);
			activeChar.removeSkill(3628, true);
			activeChar.sendSkillList();
		}
		
		// Close lock at login
		activeChar.setLocked(false);
	}

	private boolean isValidName(String text)
	{
		boolean result = true;
		String test = text;
		Pattern pattern;

		try
		{
			pattern = Pattern.compile(Config.CNAME_TEMPLATE);
		}
		catch (PatternSyntaxException e) // case of illegal pattern
		{
			if(Config.ENABLE_ALL_EXCEPTIONS)
				e.printStackTrace();

			_log.warning("ERROR "+getType()+": Character name pattern of config is wrong!");
			pattern = Pattern.compile(".*");
		}

		Matcher regexp = pattern.matcher(test);
		if (!regexp.matches())
			result = false;

		return result;
	}

	private void EnterGM(L2PcInstance activeChar)
	{
		if (activeChar.isGM())
		{
			if (Config.GM_SPECIAL_EFFECT)
				activeChar.broadcastPacket(new Earthquake(activeChar.getX(), activeChar.getY(), activeChar.getZ(), 50, 4));

			if (Config.SHOW_GM_LOGIN)
				Announcements.getInstance().announceToAll("GM " + activeChar.getName() + " has logged on.");

			if (Config.GM_STARTUP_INVULNERABLE && AdminCommandAccessRights.getInstance().hasAccess("admin_invul", activeChar.getAccessLevel()))
				activeChar.setIsInvul(true);

			if (Config.GM_STARTUP_INVISIBLE && AdminCommandAccessRights.getInstance().hasAccess("admin_invisible", activeChar.getAccessLevel()))
				activeChar.getAppearance().setInvisible();

			if (Config.GM_STARTUP_SILENCE && AdminCommandAccessRights.getInstance().hasAccess("admin_silence", activeChar.getAccessLevel()))
				activeChar.setMessageRefusal(true);

			if (Config.GM_STARTUP_AUTO_LIST && AdminCommandAccessRights.getInstance().hasAccess("admin_gmliston", activeChar.getAccessLevel()))
				GmListTable.getInstance().addGm(activeChar, false);
			else
				GmListTable.getInstance().addGm(activeChar, true);

			activeChar.updateGmNameTitleColor();
		}
	}

	private void Hellows(L2PcInstance activeChar)
	{
		if (Config.ALT_SERVER_NAME_ENABLED)
			sendPacket(new SystemMessage(SystemMessageId.S1_S2).addString("Welcome to " + Config.ALT_Server_Name));

		if (Config.ONLINE_PLAYERS_ON_LOGIN)
			sendPacket(new SystemMessage(SystemMessageId.S1_S2).addString("There are " + L2World.getInstance().getAllPlayers().size() + " players online."));

		if (activeChar.getFirstLog() && Config.NEW_PLAYER_EFFECT)
		{
			L2Skill skill = SkillTable.getInstance().getInfo(2025,1);
			if (skill != null)
			{
				MagicSkillUser MSU = new MagicSkillUser(activeChar, activeChar, 2025, 1, 1, 0);
				activeChar.sendPacket(MSU);
				activeChar.broadcastPacket(MSU);
				activeChar.useMagic(skill, false, false);
			}
			activeChar.setFirstLog(false);
			activeChar.updateFirstLog();
		}

		if (Config.WELCOME_HTM && isValidName(activeChar.getName()))
		{
			String Welcome_Path = "data/html/welcome.htm";
			File mainText = new File(Config.DATAPACK_ROOT, Welcome_Path);
			if (mainText.exists())
			{
				NpcHtmlMessage html = new NpcHtmlMessage(1);
				html.setFile(Welcome_Path);
				html.replace("%name%", activeChar.getName());
				sendPacket(html);
			}
		}

		if ((activeChar.getClan() != null) && activeChar.getClan().isNoticeEnabled())
		{
			String clanNotice = "data/html/clanNotice.htm";
			File mainText = new File(Config.DATAPACK_ROOT, clanNotice);
			if (mainText.exists())
			{
				NpcHtmlMessage html = new NpcHtmlMessage(1);
				html.setFile(clanNotice);
				html.replace("%clan_name%", activeChar.getClan().getName());
				html.replace("%notice_text%", activeChar.getClan().getNotice().replaceAll("\r\n", "<br>"));
				sendPacket(html);
			}
		}

		if (Config.PM_MESSAGE_ON_START)
		{
			activeChar.sendPacket(new CreatureSay(2, Say2.HERO_VOICE,Config.PM_TEXT1,Config.PM_SERVER_NAME));
			activeChar.sendPacket(new CreatureSay(15, Say2.PARTYROOM_COMMANDER,activeChar.getName(),Config.PM_TEXT2));
		}
		
		if (Config.SERVER_TIME_ON_START)
			activeChar.sendMessage("SVR time is " + fmt.format(new Date(System.currentTimeMillis())));
	}

	private void ColorSystem(L2PcInstance activeChar)
	{
		// Color System checks - Start
		// Check if the custom PvP and PK color systems are enabled and if so check the character's counters
		// and apply any color changes that must be done. Thankz Kidzor
		/** KidZor: Ammount 1 **/
		if (activeChar.getPvpKills() >= Config.PVP_AMOUNT1 && Config.PVP_COLOR_SYSTEM_ENABLED)
			activeChar.updatePvPColor(activeChar.getPvpKills());
		if (activeChar.getPkKills() >= Config.PK_AMOUNT1 && Config.PK_COLOR_SYSTEM_ENABLED)
			activeChar.updatePkColor(activeChar.getPkKills());

		/** KidZor: Ammount 2 **/
		if (activeChar.getPvpKills() >= Config.PVP_AMOUNT2 && Config.PVP_COLOR_SYSTEM_ENABLED)
			activeChar.updatePvPColor(activeChar.getPvpKills());
		if (activeChar.getPkKills() >= Config.PK_AMOUNT2 && Config.PK_COLOR_SYSTEM_ENABLED)
			activeChar.updatePkColor(activeChar.getPkKills());

		/** KidZor: Ammount 3 **/
		if (activeChar.getPvpKills() >= Config.PVP_AMOUNT3 && Config.PVP_COLOR_SYSTEM_ENABLED)
			activeChar.updatePvPColor(activeChar.getPvpKills());
		if (activeChar.getPkKills() >= Config.PK_AMOUNT3 && Config.PK_COLOR_SYSTEM_ENABLED)
			activeChar.updatePkColor(activeChar.getPkKills());

		/** KidZor: Ammount 4 **/
		if (activeChar.getPvpKills() >= Config.PVP_AMOUNT4 && Config.PVP_COLOR_SYSTEM_ENABLED)
			activeChar.updatePvPColor(activeChar.getPvpKills());
		if (activeChar.getPkKills() >= Config.PK_AMOUNT4 && Config.PK_COLOR_SYSTEM_ENABLED)
			activeChar.updatePkColor(activeChar.getPkKills());

		/** KidZor: Ammount 5 **/
		if (activeChar.getPvpKills() >= Config.PVP_AMOUNT5 && Config.PVP_COLOR_SYSTEM_ENABLED)
			activeChar.updatePvPColor(activeChar.getPvpKills());
		if (activeChar.getPkKills() >= Config.PK_AMOUNT5 && Config.PK_COLOR_SYSTEM_ENABLED)
			activeChar.updatePkColor(activeChar.getPkKills());
		// Color System checks - End
		
		// Apply color settings to clan leader when entering  
		if (activeChar.getClan() != null && activeChar.isClanLeader() && Config.CLAN_LEADER_COLOR_ENABLED && activeChar.getClan().getLevel() >= Config.CLAN_LEADER_COLOR_CLAN_LEVEL)
		{
			if (Config.CLAN_LEADER_COLORED == 1)
				activeChar.getAppearance().setNameColor(Config.CLAN_LEADER_COLOR);
			else
				activeChar.getAppearance().setTitleColor(Config.CLAN_LEADER_COLOR);
		}

		if (Config.ALLOW_AIO_NCOLOR && activeChar.isAio())
			activeChar.getAppearance().setNameColor(Config.AIO_NCOLOR);

		if (Config.ALLOW_AIO_TCOLOR && activeChar.isAio())
			activeChar.getAppearance().setTitleColor(Config.AIO_TCOLOR);

		if (activeChar.isAio())
			onEnterAio(activeChar);

		if(Config.ALLOW_VIP_NCOLOR && activeChar.isVip())
			activeChar.getAppearance().setNameColor(Config.VIP_NCOLOR);
			 
		if(Config.ALLOW_VIP_TCOLOR && activeChar.isVip())
			activeChar.getAppearance().setTitleColor(Config.VIP_TCOLOR);
			
		if(activeChar.isVip())
			onEnterVip(activeChar);
			
		activeChar.updateNameTitleColor();
			
		sendPacket(new UserInfo(activeChar));
		sendPacket(new HennaInfo(activeChar));
		sendPacket(new FriendList(activeChar));
		sendPacket(new ItemList(activeChar, false));
		sendPacket(new ShortCutInit(activeChar));
		activeChar.broadcastUserInfo();
		activeChar.sendPacket(new EtcStatusUpdate(activeChar));
	}

	private void onEnterAio(L2PcInstance activeChar)
	{
		long now = Calendar.getInstance().getTimeInMillis();
		long endDay = activeChar.getAioEndTime();

		if (now > endDay)
    	{
    		activeChar.setAio(false);
    		activeChar.setAioEndTime(0);
    		activeChar.lostAioSkills();
    		activeChar.sendMessage("[Aio System]: Removed your Aio stats... period ends.");
    	}
    	else
    	{
    		Date dt = new Date(endDay);
    		_daysleft = (endDay - now)/86400000;
    		if (_daysleft > 30)
    			activeChar.sendMessage("[Aio System]: Aio period ends in " + df.format(dt) + ". enjoy the Game.");
    		else if (_daysleft > 0)
    			activeChar.sendMessage("[Aio System]: Left " + (int)_daysleft + " for Aio period ends.");
    		else if (_daysleft < 1)
    		{
    			long hour = (endDay - now)/3600000;
    			activeChar.sendMessage("[Aio System]: Left " + (int)hour + " hours to Aio period ends.");
    		}
    	}
	}

	/**
	 * @param cha 
	 */
	private void engage(L2PcInstance cha)
	{
		int _chaid = cha.getObjectId();

		for (Wedding cl : CoupleManager.getInstance().getCouples())
		{
			if (cl.getPlayer1Id() == _chaid || cl.getPlayer2Id() == _chaid)
			{
				if (cl.getMaried())
				{
					cha.setMarried(true);
					cha.setmarriedType(cl.getType());
				}

				cha.setCoupleId(cl.getId());

				if(cl.getPlayer1Id() == _chaid)
					cha.setPartnerId(cl.getPlayer2Id());
				else
					cha.setPartnerId(cl.getPlayer1Id());
			}
		}
	}

	/**
	 * @param cha 
	 * @param partnerId 
	 */
	private void notifyPartner(L2PcInstance cha, int partnerId)
	{
		if (cha.getPartnerId() != 0)
		{
			L2PcInstance partner = null;
			
			if (L2World.getInstance().findObject(cha.getPartnerId()) instanceof L2PcInstance)
				partner = (L2PcInstance) L2World.getInstance().findObject(cha.getPartnerId());
			
			if (partner != null)
				partner.sendMessage("Your partner has logged in");
		}
	}
	 
	       private void onEnterVip(L2PcInstance activeChar)
	       {
	               long curDay = Calendar.getInstance().getTimeInMillis();
	               long endDay = activeChar.getVipEndTime();
	               if(curDay > endDay)
	               {
	                       activeChar.setVip(false);
	                       activeChar.setVipEndTime(0);
	                       activeChar.sendMessage("[Vip System]: Removed your Vip stats... period ends ");
	               }
	               else
	               {
	                       Date dt = new Date(endDay);
	                       _daysleft = (endDay - curDay) / 86400000;
	                       if(_daysleft > 30)
	                               activeChar.sendMessage("[Vip System]: Vip period ends in " + df.format(dt) + ". enjoy the Game");
	                       else if(_daysleft > 0)
	                               activeChar.sendMessage("[Vip System]: Left " + (int)_daysleft + " days for Vip period ends");
	                       else if(_daysleft < 1)
	                       {
	                               long hour = (endDay - curDay) / 3600000;
	                               activeChar.sendMessage("[Vip System]: Left " + (int)hour + " hours to Vip period ends");
	                       }
	               }
	       }
	
	/**
	 * @param activeChar
	 */
	private void notifyClanMembers(L2PcInstance activeChar)
	{
		L2Clan clan = activeChar.getClan();
		if (clan != null)
		{
			clan.getClanMember(activeChar.getObjectId()).setPlayerInstance(activeChar);
			clan.broadcastToOtherOnlineMembers(new SystemMessage(SystemMessageId.CLAN_MEMBER_S1_LOGGED_IN).addString(activeChar.getName()), activeChar);
			clan.broadcastToOtherOnlineMembers(new PledgeShowMemberListUpdate(activeChar), activeChar);
		}
	}

	/**
	 * @param activeChar
	 */
	private void notifySponsorOrApprentice(L2PcInstance activeChar)
	{
		if (activeChar.getSponsor() != 0)
		{
			L2PcInstance sponsor = (L2PcInstance) L2World.getInstance().findObject(activeChar.getSponsor());
			if (sponsor != null)
				sponsor.sendPacket(new SystemMessage(SystemMessageId.YOUR_APPRENTICE_S1_HAS_LOGGED_IN).addString(activeChar.getName()));
		}
		else if (activeChar.getApprentice() != 0)
		{
			L2PcInstance apprentice = (L2PcInstance) L2World.getInstance().findObject(activeChar.getApprentice());
			if (apprentice != null)
				apprentice.sendPacket(new SystemMessage(SystemMessageId.YOUR_SPONSOR_S1_HAS_LOGGED_IN).addString(activeChar.getName()));
		}
	}

	private void loadTutorial(L2PcInstance player)
	{
		QuestState qs = player.getQuestState("255_Tutorial");
		if (qs != null)
			qs.getQuest().notifyEvent("UC", null, player);
	}

	private void setPledgeClass(L2PcInstance activeChar)
	{
		int pledgeClass = 0;

		if (activeChar.getClan() != null)
			pledgeClass = activeChar.getClan().getClanMember(activeChar.getObjectId()).calculatePledgeClass(activeChar);

		if (activeChar.isNoble() && pledgeClass < 5)
			pledgeClass = 5;

		if (activeChar.isHero())
			pledgeClass = 8;

		activeChar.setPledgeClass(pledgeClass);
	}
	private void notifyCastleOwner(L2PcInstance activeChar)
	{
		L2Clan clan = activeChar.getClan();
		if (clan != null)
		{
			if (clan.getHasCastle() > 0)
			{
				Castle castle = CastleManager.getInstance().getCastleById(clan.getHasCastle());
				if ((castle != null) && (activeChar.getObjectId() == clan.getLeaderId()))
					Announcements.getInstance().announceToAll("Lord " + activeChar.getName() + " Ruler Of " + castle.getName() + " Castle is now Online!");
			}
		}
	}

	@Override
	public String getType()
	{
		return "[C] 03 EnterWorld";
	}
}