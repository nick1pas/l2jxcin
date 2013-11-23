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

import java.util.Collection;
import java.util.concurrent.ScheduledFuture;

import net.xcine.gameserver.ThreadPoolManager;
import net.xcine.gameserver.datatables.SkillTable;
import net.xcine.gameserver.model.L2Object;
import net.xcine.gameserver.model.L2Skill;
import net.xcine.gameserver.model.actor.L2Character;
import net.xcine.gameserver.network.serverpackets.ActionFailed;
import net.xcine.gameserver.templates.chars.L2NpcTemplate;

/**
 * @author Drunkard Zabb0x Lets drink2code!
 */
public class L2XmassTreeInstance extends L2NpcInstance
{
	public static final int SPECIAL_TREE_ID = 13007;
	protected ScheduledFuture<?> _aiTask;
	
	class XmassAI implements Runnable
	{
		private final L2XmassTreeInstance _caster;
		private final L2Skill _skill;
		
		protected XmassAI(L2XmassTreeInstance caster, L2Skill skill)
		{
			_caster = caster;
			_skill = skill;
		}
		
		@Override
		public void run()
		{
			if (_skill == null || _caster.isInsideZone(ZONE_TOWN))
			{
				_caster._aiTask.cancel(false);
				_caster._aiTask = null;
				return;
			}
			Collection<L2PcInstance> plrs = getKnownList().getKnownPlayersInRadius(200);
			for (L2PcInstance player : plrs)
				if (player.getFirstEffect(_skill.getId()) == null)
					_skill.getEffects(player, player);
		}
	}
	
	public L2XmassTreeInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
		if (template.isSpecialTree())
			_aiTask = ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(new XmassAI(this, SkillTable.getInstance().getInfo(2139, 1)), 3000, 3000);
	}
	
	@Override
	public void deleteMe()
	{
		if (_aiTask != null)
			_aiTask.cancel(true);
		
		super.deleteMe();
	}
	
	@Override
	public int getDistanceToWatchObject(L2Object object)
	{
		return 900;
	}
	
	@Override
	public boolean isAutoAttackable(L2Character attacker)
	{
		return false;
	}
	
	@Override
	public void onAction(L2PcInstance player)
	{
		player.sendPacket(ActionFailed.STATIC_PACKET);
	}
}