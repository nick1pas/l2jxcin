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
package ai.group_template;

import net.xcine.gameserver.datatables.SkillTable;
import net.xcine.gameserver.model.actor.instance.L2NpcInstance;
import net.xcine.gameserver.model.actor.instance.L2PcInstance;
import ai.L2AttackableAIScript;

/**
 * @author  Maxi
 * to java Kidzor
 */
public class AncientEgg extends L2AttackableAIScript
{
	private final int EGG = 18344;

	public AncientEgg(int questId, String name, String descr)
	{
		super(questId, name, descr);
		addAttackId(EGG);
	}

	@Override
	public String onAttack(L2NpcInstance npc, L2PcInstance player, int damage, boolean isPet)
	{
		player.setTarget(player);
		player.doCast(SkillTable.getInstance().getInfo(5088,1));
		return super.onAttack(npc, player, damage, isPet);
	}

	public static void main(String[] args)
	{
		new AncientEgg(-1, "AncientEgg", "ai");
	}
}