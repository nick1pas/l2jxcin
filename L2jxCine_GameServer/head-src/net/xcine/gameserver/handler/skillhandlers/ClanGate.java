package net.xcine.gameserver.handler.skillhandlers;

import net.xcine.gameserver.handler.ISkillHandler;
import net.xcine.gameserver.managers.CastleManager;
import net.xcine.gameserver.managers.GrandBossManager;
import net.xcine.gameserver.model.L2Character;
import net.xcine.gameserver.model.L2Clan;
import net.xcine.gameserver.model.L2Effect;
import net.xcine.gameserver.model.L2Object;
import net.xcine.gameserver.model.L2Skill;
import net.xcine.gameserver.model.L2Skill.SkillType;
import net.xcine.gameserver.model.actor.instance.L2PcInstance;
import net.xcine.gameserver.model.entity.siege.Castle;
import net.xcine.gameserver.network.SystemMessageId;
import net.xcine.gameserver.network.serverpackets.SystemMessage;
import net.xcine.gameserver.thread.ThreadPoolManager;

public class ClanGate implements ISkillHandler
{
	private static final SkillType[] SKILL_IDS = { SkillType.CLAN_GATE };
	
	@Override
	public void useSkill(L2Character activeChar, L2Skill skill, L2Object[] targets)
	{
		L2PcInstance player = null;
		if(activeChar instanceof L2PcInstance)
			player = (L2PcInstance) activeChar;
		else
			return;
		if(player.isInFunEvent() || player.isInsideZone(L2Character.ZONE_NOLANDING)
			|| player.isInOlympiadMode() || player.isInsideZone(L2Character.ZONE_PVP)
			|| GrandBossManager.getInstance().getZone(player) != null)
		{
			player.sendMessage("Cannot open the portal here.");
			return;
		}

		L2Clan clan = player.getClan();
		if(clan != null)
		{
			if(CastleManager.getInstance().getCastleByOwner(clan) != null)
			{
				Castle castle = CastleManager.getInstance().getCastleByOwner(clan);
				if(player.isCastleLord(castle.getCastleId()))
				{
					ThreadPoolManager.getInstance().scheduleGeneral(new RemoveClanGate(castle.getCastleId(), player), skill.getTotalLifeTime());
					castle.createClanGate(player.getX(), player.getY(), player.getZ() + 20);
					player.getClan().broadcastToOnlineMembers(new SystemMessage(SystemMessageId.THE_PORTAL_HAS_BEEN_CREATED));
					player.setIsParalyzed(true);
				}
				castle = null;
			}
		}

		L2Effect effect = player.getFirstEffect(skill.getId());
		if(effect != null && effect.isSelfEffect())
			effect.exit(false);
		skill.getEffectsSelf(player);

		player = null;
		clan = null;
	}

	private class RemoveClanGate implements Runnable
	{
		private final int castle;
		private final L2PcInstance player;

		protected RemoveClanGate(int castle, L2PcInstance player)
		{
			this.castle = castle;
			this.player = player;
		}
		
		@Override
		public void run()
		{
			if(player != null)
			{
				player.setIsParalyzed(false);
			}
			CastleManager.getInstance().getCastleById(castle).destroyClanGate();
		}
	}

	@Override
	public SkillType[] getSkillIds()
	{
		return SKILL_IDS;
	}
}
