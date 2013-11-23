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

import net.xcine.gameserver.handler.IItemHandler;
import net.xcine.gameserver.model.L2ItemInstance;
import net.xcine.gameserver.model.ShotType;
import net.xcine.gameserver.model.actor.L2Playable;
import net.xcine.gameserver.model.actor.instance.L2PcInstance;
import net.xcine.gameserver.network.SystemMessageId;
import net.xcine.gameserver.network.serverpackets.MagicSkillUse;
import net.xcine.gameserver.templates.item.L2Weapon;
import net.xcine.gameserver.templates.item.L2WeaponType;
import net.xcine.gameserver.util.Broadcast;

/**
 * @author -Nemesiss-
 */
public class FishShots implements IItemHandler
{
	private static final int[] SKILL_IDS =
	{
		2181,
		2182,
		2183,
		2184,
		2185,
		2186
	};
	
	@Override
	public void useItem(L2Playable playable, L2ItemInstance item, boolean forceUse)
	{
		if (!(playable instanceof L2PcInstance))
			return;
		
		final L2PcInstance activeChar = (L2PcInstance) playable;
		final L2ItemInstance weaponInst = activeChar.getActiveWeaponInstance();
		final L2Weapon weaponItem = activeChar.getActiveWeaponItem();
		
		if (weaponInst == null || weaponItem.getItemType() != L2WeaponType.FISHINGROD)
			return;
		
		// Fishshot is already active
		if (activeChar.isChargedShot(ShotType.FISH_SOULSHOT))
			return;
		
		// Wrong grade of soulshot for that fishing pole.
		final int grade = weaponItem.getCrystalType();
		if (grade != item.getItem().getCrystalType())
		{
			activeChar.sendPacket(SystemMessageId.WRONG_FISHINGSHOT_GRADE);
			return;
		}
		
		if (!activeChar.destroyItemWithoutTrace("Consume", item.getObjectId(), 1, null, false))
		{
			activeChar.sendPacket(SystemMessageId.NOT_ENOUGH_SOULSHOTS);
			return;
		}
		
		activeChar.setChargedShot(ShotType.FISH_SOULSHOT, true);
		Broadcast.toSelfAndKnownPlayers(activeChar, new MagicSkillUse(activeChar, SKILL_IDS[grade], 1, 0, 0));
	}
}