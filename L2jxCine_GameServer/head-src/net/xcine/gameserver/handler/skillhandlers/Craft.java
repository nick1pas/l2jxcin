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
package net.xcine.gameserver.handler.skillhandlers;

import net.xcine.gameserver.controllers.RecipeController;
import net.xcine.gameserver.handler.ISkillHandler;
import net.xcine.gameserver.model.L2Character;
import net.xcine.gameserver.model.L2Object;
import net.xcine.gameserver.model.L2Skill;
import net.xcine.gameserver.model.L2Skill.SkillType;
import net.xcine.gameserver.model.actor.instance.L2PcInstance;
import net.xcine.gameserver.network.SystemMessageId;
import net.xcine.gameserver.network.serverpackets.SystemMessage;


public class Craft implements ISkillHandler
{
	private static final SkillType[] SKILL_IDS = { SkillType.COMMON_CRAFT, SkillType.DWARVEN_CRAFT };
	
	@Override
	public void useSkill(L2Character activeChar, L2Skill skill, L2Object[] targets)
	{
		if(activeChar == null || !(activeChar instanceof L2PcInstance))
			return;

		L2PcInstance player = (L2PcInstance) activeChar;

		if (!player.getFloodProtectors().getManufacture().tryPerformAction("craft"))
		{
			player.sendMessage("You Cannot craft So Fast!");
			return;
		}

		if(player.getPrivateStoreType() != 0)
		{
			player.sendPacket(new SystemMessage(SystemMessageId.CANNOT_CREATED_WHILE_ENGAGED_IN_TRADING));
			return;
		}
		RecipeController.getInstance().requestBookOpen(player, (skill.getSkillType() == SkillType.DWARVEN_CRAFT) ? true : false);

		player = null;
	}

	@Override
	public SkillType[] getSkillIds()
	{
		return SKILL_IDS;
	}
}
