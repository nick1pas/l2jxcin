package net.sf.l2j.gameserver.model.actor.instance;

import java.util.Calendar;
import java.util.StringTokenizer;

import net.sf.l2j.commons.concurrent.ThreadPool;
import net.sf.l2j.gameserver.datatables.SkillTable;
import net.sf.l2j.gameserver.handler.admincommandhandlers.AdminVipStatus;
import net.sf.l2j.gameserver.instancemanager.CastleManager;
import net.sf.l2j.gameserver.model.World;
import net.sf.l2j.gameserver.model.actor.ai.CtrlIntention;
import net.sf.l2j.gameserver.model.actor.template.NpcTemplate;
import net.sf.l2j.gameserver.model.base.Sex;
import net.sf.l2j.gameserver.model.entity.Castle;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.network.serverpackets.ExShowScreenMessage;
import net.sf.l2j.gameserver.network.serverpackets.SiegeInfo;
import net.sf.l2j.gameserver.network.serverpackets.SocialAction;

public class Vip extends Folk
{
	public Vip(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}
	
	/** Type the count for clean pk */
	private final static int PK_CLEAN_COUNT = 50;
	
	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if (command.startsWith("vip"))
		{
			if (!player.isVipStatus())
			{
				player.sendMessage("Sorry! Only VIP player can do this action.");
				return;
			}
			StringTokenizer st = new StringTokenizer(command);
			st.nextToken();
			try
			{
				String type = st.nextToken();
				switch (type)
				{
					case "Noblesse":
						Nobless(player);
						break;
					case "ChangeSex":
						Sex(player);
						break;
					case "CleanPk":
						CleanPk(player);
						break;
					case "FullRec":
						Rec(player);
						break;
				}
			}
			catch (Exception e)
			{
			}
		}
		
		else if (command.startsWith("tp"))
		{
			if (!player.isVipStatus())
			{
				player.sendMessage("Sorry! Only VIP player can do this action.");
				return;
			}
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
				teleportTo(val, player, activeChar);
			}
			catch (Exception e)
			{
				// Case if the player is not in the same party.
				player.sendMessage("Incorrect target");
			}
		}
		
		else if (command.startsWith("clantp"))
		{
			if (!player.isVipStatus())
			{
				player.sendMessage("Sorry! Only VIP player can do this action.");
				return;
			}
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
				teleportToClan(clan, player, activeChar);
			}
			catch (Exception e)
			{
				// Case if the player is not in the same clan.
				player.sendMessage("Incorrect target");
			}
		}
		
		else if (command.startsWith("siege"))
		{
			if (!player.isVipStatus())
			{
				player.sendMessage("Sorry! Only VIP player can do this action.");
				return;
			}
			StringTokenizer st = new StringTokenizer(command);
			st.nextToken();
			try
			{
				String type = st.nextToken();
				int castleId = 0;
				
				if (type.startsWith("Gludio"))
					castleId = 1;
				else if (type.startsWith("Dion"))
					castleId = 2;
				else if (type.startsWith("Giran"))
					castleId = 3;
				else if (type.startsWith("Oren"))
					castleId = 4;
				else if (type.startsWith("Aden"))
					castleId = 5;
				else if (type.startsWith("Innadril"))
					castleId = 6;
				else if (type.startsWith("Goddard"))
					castleId = 7;
				else if (type.startsWith("Rune"))
					castleId = 8;
				else if (type.startsWith("Schuttgart"))
					castleId = 9;
				
				Castle castle = CastleManager.getInstance().getCastleById(castleId);
				
				if (castle != null && castleId != 0)
					player.sendPacket(new SiegeInfo(castle));
			}
			catch (Exception e)
			{
			}
		}
	}
	
	public static void Nobless(Player player)
	{
		if (player.isNoble())
		{
			player.sendMessage("You Are Already A Noblesse!.");
			return;
		}
		player.broadcastPacket(new SocialAction(player, 16));
		player.setNoble(true, true);
		player.sendMessage("You Are Now a Noble,You Are Granted With Noblesse Status , And Noblesse Skills.");
		player.broadcastUserInfo();
	}
	
	public static void Sex(Player player)
	{
		player.getAppearance().setSex(player.getAppearance().getSex() == Sex.MALE ? Sex.FEMALE : Sex.MALE);
		player.sendMessage("Your gender has been changed,You will be disconected in 3 Seconds!");
		player.broadcastUserInfo();
		player.decayMe();
		player.spawnMe();
		ThreadPool.schedule(() -> player.logout(false), 3000);
	}
	
	public static void CleanPk(Player player)
	{
		if (player.getPkKills() < 50)
		{
			player.sendMessage("You do not have enough Pk kills for clean.");
			return;
		}
		player.setPkKills(player.getPkKills() - PK_CLEAN_COUNT);
		player.sendMessage("You have successfully clean " + PK_CLEAN_COUNT + " pks!");
		player.broadcastUserInfo();
	}
	
	public static void Rec(Player player)
	{
		if (player.getRecomHave() == 255)
		{
			player.sendMessage("You already have full recommends.");
			return;
		}
		player.setRecomHave(255);
		player.sendMessage("Added 255 recommends.");
		player.getLastRecomUpdate();
		player.broadcastUserInfo();
	}
	
	public static void teleportTo(String val, Player activeChar, Player target)
	{
		if (target.getObjectId() == activeChar.getObjectId())
			activeChar.sendPacket(SystemMessageId.CANNOT_USE_ON_YOURSELF);
		
		// Check if the player is not in the same party
		else if (!activeChar.getParty().getMembers().contains(target))
			return;
		
		// Simple checks to avoid exploits
		else if (target.isInJail() || target.isInOlympiadMode() || target.isInDuel() || target.isFestivalParticipant() || (target.isInParty() && target.getParty().isInDimensionalRift()) || target.isInObserverMode())
		{
			activeChar.sendMessage("Due to the current friend's status, the teleportation failed.");
			return;
		}
		
		else if (target.getClan() != null && CastleManager.getInstance().getCastleByOwner(target.getClan()) != null && CastleManager.getInstance().getCastleByOwner(target.getClan()).getSiege().isInProgress())
		{
			activeChar.sendMessage("As your friend is in siege, you can't go to him/her.");
			return;
		}
		else if (activeChar.getPvpFlag() > 0 || activeChar.getKarma() > 0)
		{
			activeChar.sendMessage("Go away! Flag or Pk player can not be teleported.");
			return;
		}
		int x = target.getX();
		int y = target.getY();
		int z = target.getZ();
		
		activeChar.getAI().setIntention(CtrlIntention.IDLE);
		activeChar.doCast(SkillTable.getInstance().getInfo(2100, 1));
		activeChar.sendPacket(new ExShowScreenMessage("You will be teleported to " + target.getName() + " in 3 Seconds!", 3000, 2, true));
		ThreadPool.schedule(() -> activeChar.teleToLocation(x, y, z, 0), 3000);
		activeChar.sendMessage("You have teleported to " + target.getName() + ".");
	}
	
	public static void teleportToClan(String clan, Player activeChar, Player target)
	{
		if (target.getObjectId() == activeChar.getObjectId())
			activeChar.sendPacket(SystemMessageId.CANNOT_USE_ON_YOURSELF);
		
		// Check if the player is not in the same clan.
		else if (!activeChar.getClan().isMember(target.getObjectId()))
			return;
		
		// Simple checks to avoid exploits
		else if (target.isInJail() || target.isInOlympiadMode() || target.isInDuel() || target.isFestivalParticipant() || (target.isInParty() && target.getParty().isInDimensionalRift()) || target.isInObserverMode())
		{
			activeChar.sendMessage("Due to the current clan member's status, the teleportation failed.");
			return;
		}
		
		else if (target.getClan() != null && CastleManager.getInstance().getCastleByOwner(target.getClan()) != null && CastleManager.getInstance().getCastleByOwner(target.getClan()).getSiege().isInProgress())
		{
			activeChar.sendMessage("As your clan member is in siege, you can't go to him/her.");
			return;
		}
		else if (activeChar.getPvpFlag() > 0 || activeChar.getKarma() > 0)
		{
			activeChar.sendMessage("Go away! Flag or Pk player can not be teleported.");
			return;
		}
		int x = target.getX();
		int y = target.getY();
		int z = target.getZ();
		
		activeChar.getAI().setIntention(CtrlIntention.IDLE);
		activeChar.doCast(SkillTable.getInstance().getInfo(2100, 1));
		activeChar.sendPacket(new ExShowScreenMessage("You will be teleported to " + target.getName() + " in 3 Seconds!", 3000, 2, true));
		ThreadPool.schedule(() -> activeChar.teleToLocation(x, y, z, 0), 3000);
		activeChar.sendMessage("You have teleported to " + target.getName() + ".");
	}
	
	// Contains to EnterWorld.java for VIP onEnter.
	public static void onEnterVipStatus(Player activeChar)
	{
		long now = Calendar.getInstance().getTimeInMillis();
		long endDay = activeChar.getMemos().getLong("TimeOfVip");
		
		if (now > endDay)
			AdminVipStatus.RemoveVipStatus(activeChar);
		else
		{
			activeChar.setVipStatus(true);
			activeChar.broadcastUserInfo();
		}
	}
	
	@Override
	public String getHtmlPath(int npcId, int val)
	{
		String filename = "";
		
		if (val == 0)
			filename = "" + npcId;
		else
			filename = npcId + "-" + val;
		
		return "data/html/mods/vipNpc/" + filename + ".htm";
	}
}