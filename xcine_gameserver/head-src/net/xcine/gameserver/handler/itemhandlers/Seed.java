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
package net.xcine.gameserver.handler.itemhandlers;

import net.xcine.gameserver.datatables.MapRegionTable;
import net.xcine.gameserver.handler.IItemHandler;
import net.xcine.gameserver.instancemanager.CastleManorManager;
import net.xcine.gameserver.model.L2ItemInstance;
import net.xcine.gameserver.model.L2Manor;
import net.xcine.gameserver.model.L2Object;
import net.xcine.gameserver.model.actor.L2Character;
import net.xcine.gameserver.model.actor.L2Playable;
import net.xcine.gameserver.model.actor.instance.L2ChestInstance;
import net.xcine.gameserver.model.actor.instance.L2MonsterInstance;
import net.xcine.gameserver.model.actor.instance.L2PcInstance;
import net.xcine.gameserver.network.SystemMessageId;
import net.xcine.gameserver.skills.SkillHolder;

/**
 * @author l3x
 */
public class Seed implements IItemHandler
{
	@Override
	public void useItem(L2Playable playable, L2ItemInstance item, boolean forceUse)
	{
		if (!(playable instanceof L2PcInstance))
			return;
		
		if (CastleManorManager.getInstance().isDisabled())
			return;
		
		final L2Object tgt = playable.getTarget();
		
		if (!(tgt instanceof L2MonsterInstance) || tgt instanceof L2ChestInstance || ((L2Character) tgt).isRaid())
		{
			playable.sendPacket(SystemMessageId.THE_TARGET_IS_UNAVAILABLE_FOR_SEEDING);
			return;
		}
		
		final L2MonsterInstance target = (L2MonsterInstance) tgt;
		if (target.isDead() || target.isSeeded())
		{
			playable.sendPacket(SystemMessageId.INCORRECT_TARGET);
			return;
		}
		
		final int seedId = item.getItemId();
		if (areaValid(seedId, MapRegionTable.getInstance().getAreaCastle(playable)))
		{
			target.setSeeded(seedId, (L2PcInstance) playable);
			final SkillHolder[] skills = item.getEtcItem().getSkills();
			if (skills != null)
			{
				if (skills[0] == null)
					return;
				
				playable.useMagic(skills[0].getSkill(), false, false);
			}
		}
		else
			playable.sendPacket(SystemMessageId.THIS_SEED_MAY_NOT_BE_SOWN_HERE);
	}
	
	private static boolean areaValid(int seedId, int castleId)
	{
		return L2Manor.getInstance().getCastleIdForSeed(seedId) == castleId;
	}
}