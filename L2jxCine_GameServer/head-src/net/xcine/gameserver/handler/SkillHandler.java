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
package net.xcine.gameserver.handler;

import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;

import net.xcine.gameserver.GameServer;
import net.xcine.gameserver.handler.skillhandlers.BalanceLife;
import net.xcine.gameserver.handler.skillhandlers.BeastFeed;
import net.xcine.gameserver.handler.skillhandlers.Blow;
import net.xcine.gameserver.handler.skillhandlers.Charge;
import net.xcine.gameserver.handler.skillhandlers.ClanGate;
import net.xcine.gameserver.handler.skillhandlers.CombatPointHeal;
import net.xcine.gameserver.handler.skillhandlers.Continuous;
import net.xcine.gameserver.handler.skillhandlers.CpDam;
import net.xcine.gameserver.handler.skillhandlers.Craft;
import net.xcine.gameserver.handler.skillhandlers.DeluxeKey;
import net.xcine.gameserver.handler.skillhandlers.Disablers;
import net.xcine.gameserver.handler.skillhandlers.DrainSoul;
import net.xcine.gameserver.handler.skillhandlers.Fishing;
import net.xcine.gameserver.handler.skillhandlers.FishingSkill;
import net.xcine.gameserver.handler.skillhandlers.GetPlayer;
import net.xcine.gameserver.handler.skillhandlers.Harvest;
import net.xcine.gameserver.handler.skillhandlers.Heal;
import net.xcine.gameserver.handler.skillhandlers.ManaHeal;
import net.xcine.gameserver.handler.skillhandlers.Manadam;
import net.xcine.gameserver.handler.skillhandlers.Mdam;
import net.xcine.gameserver.handler.skillhandlers.Pdam;
import net.xcine.gameserver.handler.skillhandlers.Recall;
import net.xcine.gameserver.handler.skillhandlers.Resurrect;
import net.xcine.gameserver.handler.skillhandlers.SiegeFlag;
import net.xcine.gameserver.handler.skillhandlers.Sow;
import net.xcine.gameserver.handler.skillhandlers.Spoil;
import net.xcine.gameserver.handler.skillhandlers.StrSiegeAssault;
import net.xcine.gameserver.handler.skillhandlers.SummonFriend;
import net.xcine.gameserver.handler.skillhandlers.SummonTreasureKey;
import net.xcine.gameserver.handler.skillhandlers.Sweep;
import net.xcine.gameserver.handler.skillhandlers.TakeCastle;
import net.xcine.gameserver.handler.skillhandlers.Unlock;
import net.xcine.gameserver.handler.skillhandlers.ZakenPlayer;
import net.xcine.gameserver.handler.skillhandlers.ZakenSelf;
import net.xcine.gameserver.model.L2Skill;
import net.xcine.gameserver.model.L2Skill.SkillType;

/**
 * This class ...
 * @version $Revision: 1.1.4.4 $ $Date: 2005/04/03 15:55:06 $
 */
public class SkillHandler
{
	private static final Logger _log = Logger.getLogger(GameServer.class.getName());
	
	private static SkillHandler _instance;
	
	private Map<L2Skill.SkillType, ISkillHandler> _datatable;
	
	public static SkillHandler getInstance()
	{
		if (_instance == null)
		{
			_instance = new SkillHandler();
		}
		
		return _instance;
	}
	
	private SkillHandler()
	{
		_datatable = new TreeMap<SkillType, ISkillHandler>();
		registerSkillHandler(new Blow());
		registerSkillHandler(new Pdam());
		registerSkillHandler(new Mdam());
		registerSkillHandler(new CpDam());
		registerSkillHandler(new Manadam());
		registerSkillHandler(new Heal());
		registerSkillHandler(new CombatPointHeal());
		registerSkillHandler(new ManaHeal());
		registerSkillHandler(new BalanceLife());
		registerSkillHandler(new Charge());
		registerSkillHandler(new ClanGate());
		registerSkillHandler(new Continuous());
		registerSkillHandler(new Resurrect());
		registerSkillHandler(new Spoil());
		registerSkillHandler(new Sweep());
		registerSkillHandler(new StrSiegeAssault());
		registerSkillHandler(new SummonFriend());
		registerSkillHandler(new SummonTreasureKey());
		registerSkillHandler(new Disablers());
		registerSkillHandler(new Recall());
		registerSkillHandler(new SiegeFlag());
		registerSkillHandler(new TakeCastle());
		registerSkillHandler(new Unlock());
		registerSkillHandler(new DrainSoul());
		registerSkillHandler(new Craft());
		registerSkillHandler(new Fishing());
		registerSkillHandler(new FishingSkill());
		registerSkillHandler(new BeastFeed());
		registerSkillHandler(new DeluxeKey());
		registerSkillHandler(new Sow());
		registerSkillHandler(new Harvest());
		registerSkillHandler(new GetPlayer());
		registerSkillHandler(new ZakenPlayer());
		registerSkillHandler(new ZakenSelf());
		_log.config("SkillHandler: Loaded " + _datatable.size() + " handlers.");
		
	}
	
	public void registerSkillHandler(ISkillHandler handler)
	{
		SkillType[] types = handler.getSkillIds();
		
		for (SkillType t : types)
		{
			_datatable.put(t, handler);
		}
		types = null;
	}
	
	public ISkillHandler getSkillHandler(SkillType skillType)
	{
		return _datatable.get(skillType);
	}
	
	/**
	 * @return
	 */
	public int size()
	{
		return _datatable.size();
	}
}