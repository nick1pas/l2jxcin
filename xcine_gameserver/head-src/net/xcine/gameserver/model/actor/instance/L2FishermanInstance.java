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
package net.xcine.gameserver.model.actor.instance;

import net.xcine.gameserver.datatables.SkillTable;
import net.xcine.gameserver.datatables.SkillTreeTable;
import net.xcine.gameserver.model.L2Skill;
import net.xcine.gameserver.model.L2SkillLearn;
import net.xcine.gameserver.network.SystemMessageId;
import net.xcine.gameserver.network.serverpackets.AcquireSkillList;
import net.xcine.gameserver.network.serverpackets.ActionFailed;
import net.xcine.gameserver.network.serverpackets.SystemMessage;
import net.xcine.gameserver.templates.chars.L2NpcTemplate;

public class L2FishermanInstance extends L2MerchantInstance
{
	public L2FishermanInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
	}
	
	@Override
	public String getHtmlPath(int npcId, int val)
	{
		String filename = "";
		
		if (val == 0)
			filename = "" + npcId;
		else
			filename = npcId + "-" + val;
		
		return "data/html/fisherman/" + filename + ".htm";
	}
	
	@Override
	public void onBypassFeedback(L2PcInstance player, String command)
	{
		if (command.startsWith("FishSkillList"))
		{
			player.setSkillLearningClassId(player.getClassId());
			showFishSkillList(player);
		}
		else
			super.onBypassFeedback(player, command);
	}
	
	public static void showFishSkillList(L2PcInstance player)
	{
		AcquireSkillList asl = new AcquireSkillList(AcquireSkillList.SkillType.Fishing);
		boolean empty = true;
		
		for (L2SkillLearn sl : SkillTreeTable.getInstance().getAvailableFishingDwarvenCraftSkills(player))
		{
			L2Skill sk = SkillTable.getInstance().getInfo(sl.getId(), sl.getLevel());
			if (sk == null)
				continue;
			
			asl.addSkill(sl.getId(), sl.getLevel(), sl.getLevel(), sl.getSpCost(), 1);
			empty = false;
		}
		
		if (empty)
		{
			int minlevel = SkillTreeTable.getInstance().getMinLevelForNewFishingDwarvenCraftSkill(player);
			
			if (minlevel > 0)
				player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.DO_NOT_HAVE_FURTHER_SKILLS_TO_LEARN_S1).addNumber(minlevel));
			else
				player.sendPacket(SystemMessageId.NO_MORE_SKILLS_TO_LEARN);
		}
		else
			player.sendPacket(asl);
		
		player.sendPacket(ActionFailed.STATIC_PACKET);
	}
}