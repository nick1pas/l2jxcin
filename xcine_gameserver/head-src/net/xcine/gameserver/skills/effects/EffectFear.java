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
package net.xcine.gameserver.skills.effects;

import net.xcine.Config;
import net.xcine.gameserver.GeoData;
import net.xcine.gameserver.ai.CtrlIntention;
import net.xcine.gameserver.model.CharEffectList;
import net.xcine.gameserver.model.L2CharPosition;
import net.xcine.gameserver.model.L2Effect;
import net.xcine.gameserver.model.Location;
import net.xcine.gameserver.model.actor.instance.L2NpcInstance;
import net.xcine.gameserver.model.actor.instance.L2PcInstance;
import net.xcine.gameserver.model.actor.instance.L2PetInstance;
import net.xcine.gameserver.model.actor.instance.L2SiegeFlagInstance;
import net.xcine.gameserver.model.actor.instance.L2SiegeSummonInstance;
import net.xcine.gameserver.skills.Env;
import net.xcine.gameserver.templates.skills.L2EffectType;

/**
 * @author littlecrow Implementation of the Fear Effect
 */
public class EffectFear extends L2Effect
{
	public static final int FEAR_RANGE = 500;
	
	public EffectFear(Env env, EffectTemplate template)
	{
		super(env, template);
	}
	
	@Override
	public L2EffectType getEffectType()
	{
		return L2EffectType.FEAR;
	}
	
	/** Notify started */
	@Override
	public boolean onStart()
	{
		if (getEffected() instanceof L2PcInstance && getEffector() instanceof L2PcInstance)
		{
			switch (getSkill().getId())
			{
				case 1376:
				case 1169:
				case 65:
				case 1092:
				case 98:
				case 1272:
				case 1381:
				case 763:
					break;
				default:
					return false;
			}
		}
		
		if (getEffected() instanceof L2NpcInstance || getEffected() instanceof L2SiegeFlagInstance || getEffected() instanceof L2SiegeSummonInstance)
			return false;
		
		if (getEffected().isAfraid())
			return false;
		
		getEffected().startFear();
		onActionTime();
		return true;
	}
	
	/** Notify exited */
	@Override
	public void onExit()
	{
		getEffected().stopFear(true);
	}
	
	@Override
	public boolean onActionTime()
	{
		int posX = getEffected().getX() + (((getEffected().getX() > getEffector().getX()) ? 1 : -1) * FEAR_RANGE);
		int posY = getEffected().getY() + (((getEffected().getY() > getEffector().getY()) ? 1 : -1) * FEAR_RANGE);
		int posZ = getEffected().getZ();
		
		if (Config.GEODATA > 0)
		{
			Location destiny = GeoData.getInstance().moveCheck(getEffected().getX(), getEffected().getY(), getEffected().getZ(), posX, posY, posZ);
			posX = destiny.getX();
			posY = destiny.getY();
		}
		
		if (!(getEffected() instanceof L2PetInstance))
			getEffected().setRunning();
		
		getEffected().getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, new L2CharPosition(posX, posY, posZ, 0));
		return true;
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.xcine.gameserver.model.L2Effect#getEffectFlags()
	 */
	@Override
	public int getEffectFlags()
	{
		return CharEffectList.EFFECT_FLAG_FEAR;
	}
}