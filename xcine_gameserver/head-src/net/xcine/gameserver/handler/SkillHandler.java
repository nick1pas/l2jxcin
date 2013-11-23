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
package net.xcine.gameserver.handler;

import gnu.trove.map.hash.TIntObjectHashMap;

import net.xcine.gameserver.handler.skillhandlers.BalanceLife;
import net.xcine.gameserver.handler.skillhandlers.Blow;
import net.xcine.gameserver.handler.skillhandlers.Cancel;
import net.xcine.gameserver.handler.skillhandlers.CombatPointHeal;
import net.xcine.gameserver.handler.skillhandlers.Continuous;
import net.xcine.gameserver.handler.skillhandlers.CpDamPercent;
import net.xcine.gameserver.handler.skillhandlers.Craft;
import net.xcine.gameserver.handler.skillhandlers.DeluxeKey;
import net.xcine.gameserver.handler.skillhandlers.Disablers;
import net.xcine.gameserver.handler.skillhandlers.DrainSoul;
import net.xcine.gameserver.handler.skillhandlers.Dummy;
import net.xcine.gameserver.handler.skillhandlers.Extractable;
import net.xcine.gameserver.handler.skillhandlers.Fishing;
import net.xcine.gameserver.handler.skillhandlers.FishingSkill;
import net.xcine.gameserver.handler.skillhandlers.GetPlayer;
import net.xcine.gameserver.handler.skillhandlers.GiveSp;
import net.xcine.gameserver.handler.skillhandlers.Harvest;
import net.xcine.gameserver.handler.skillhandlers.Heal;
import net.xcine.gameserver.handler.skillhandlers.HealPercent;
import net.xcine.gameserver.handler.skillhandlers.InstantJump;
import net.xcine.gameserver.handler.skillhandlers.ManaHeal;
import net.xcine.gameserver.handler.skillhandlers.Manadam;
import net.xcine.gameserver.handler.skillhandlers.Mdam;
import net.xcine.gameserver.handler.skillhandlers.Pdam;
import net.xcine.gameserver.handler.skillhandlers.Resurrect;
import net.xcine.gameserver.handler.skillhandlers.Sow;
import net.xcine.gameserver.handler.skillhandlers.Spoil;
import net.xcine.gameserver.handler.skillhandlers.StrSiegeAssault;
import net.xcine.gameserver.handler.skillhandlers.SummonFriend;
import net.xcine.gameserver.handler.skillhandlers.Sweep;
import net.xcine.gameserver.handler.skillhandlers.TakeCastle;
import net.xcine.gameserver.handler.skillhandlers.Unlock;
import net.xcine.gameserver.templates.skills.L2SkillType;

public class SkillHandler
{
	private final TIntObjectHashMap<ISkillHandler> _datatable;
	
	public static SkillHandler getInstance()
	{
		return SingletonHolder._instance;
	}
	
	protected SkillHandler()
	{
		_datatable = new TIntObjectHashMap<>();
		registerSkillHandler(new BalanceLife());
		registerSkillHandler(new Blow());
		registerSkillHandler(new Cancel());
		registerSkillHandler(new CombatPointHeal());
		registerSkillHandler(new Continuous());
		registerSkillHandler(new CpDamPercent());
		registerSkillHandler(new Craft());
		registerSkillHandler(new DeluxeKey());
		registerSkillHandler(new Disablers());
		registerSkillHandler(new DrainSoul());
		registerSkillHandler(new Dummy());
		registerSkillHandler(new Extractable());
		registerSkillHandler(new Fishing());
		registerSkillHandler(new FishingSkill());
		registerSkillHandler(new GetPlayer());
		registerSkillHandler(new GiveSp());
		registerSkillHandler(new Harvest());
		registerSkillHandler(new Heal());
		registerSkillHandler(new HealPercent());
		registerSkillHandler(new InstantJump());
		registerSkillHandler(new Manadam());
		registerSkillHandler(new ManaHeal());
		registerSkillHandler(new Mdam());
		registerSkillHandler(new Pdam());
		registerSkillHandler(new Resurrect());
		registerSkillHandler(new Sow());
		registerSkillHandler(new Spoil());
		registerSkillHandler(new StrSiegeAssault());
		registerSkillHandler(new SummonFriend());
		registerSkillHandler(new Sweep());
		registerSkillHandler(new TakeCastle());
		registerSkillHandler(new Unlock());
	}
	
	public void registerSkillHandler(ISkillHandler handler)
	{
		L2SkillType[] types = handler.getSkillIds();
		for (L2SkillType t : types)
			_datatable.put(t.ordinal(), handler);
	}
	
	public ISkillHandler getSkillHandler(L2SkillType skillType)
	{
		return _datatable.get(skillType.ordinal());
	}
	
	/**
	 * @return
	 */
	public int size()
	{
		return _datatable.size();
	}
	
	private static class SingletonHolder
	{
		protected static final SkillHandler _instance = new SkillHandler();
	}
}