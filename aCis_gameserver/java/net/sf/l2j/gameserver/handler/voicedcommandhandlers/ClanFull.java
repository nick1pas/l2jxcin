package net.sf.l2j.gameserver.handler.voicedcommandhandlers;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.datatables.ItemTable;
import net.sf.l2j.gameserver.datatables.SkillTable;
import net.sf.l2j.gameserver.handler.IVoicedCommandHandler;
import net.sf.l2j.gameserver.model.L2Skill;
import net.sf.l2j.gameserver.model.actor.instance.Player;
import net.sf.l2j.gameserver.network.serverpackets.ExShowScreenMessage;

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
	public boolean useVoicedCommand(String command, Player activeChar, String params)
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