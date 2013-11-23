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
package net.xcine.gameserver.model;

import net.xcine.gameserver.templates.chars.L2NpcTemplate.AIType;

/**
 * Model used for NPC AI related attributes.
 * @author ShanSoft from L2JTW.
 */
public class L2NpcAIData
{
	private int _canMove;
	private int _soulshot;
	private int _spiritshot;
	private int _soulshotChance;
	private int _spiritshotChance;
	private String _clan;
	private int _clanRange;
	private AIType _aiType = AIType.DEFAULT;
	private int _aggroRange;
	
	public void setCanMove(int canMove)
	{
		_canMove = canMove;
	}
	
	public void setSoulShot(int soulshot)
	{
		_soulshot = soulshot;
	}
	
	public void setSpiritShot(int spiritshot)
	{
		_spiritshot = spiritshot;
	}
	
	public void setSoulShotChance(int soulshotchance)
	{
		_soulshotChance = soulshotchance;
	}
	
	public void setSpiritShotChance(int spiritshotchance)
	{
		_spiritshotChance = spiritshotchance;
	}
	
	public void setClan(String clan)
	{
		if (clan != null && !clan.equals("") && !clan.equalsIgnoreCase("null"))
			_clan = clan.intern();
	}
	
	public void setClanRange(int clanRange)
	{
		_clanRange = clanRange;
	}
	
	public void setAi(String ai)
	{
		if (ai.equalsIgnoreCase("archer"))
			_aiType = AIType.ARCHER;
		else if (ai.equalsIgnoreCase("mage"))
			_aiType = AIType.MAGE;
		else if (ai.equalsIgnoreCase("healer"))
			_aiType = AIType.HEALER;
		else if (ai.equalsIgnoreCase("corpse"))
			_aiType = AIType.CORPSE;
		else
			_aiType = AIType.DEFAULT;
	}
	
	public void setAggro(int val)
	{
		_aggroRange = val;
	}
	
	public int getCanMove()
	{
		return _canMove;
	}
	
	public int getSoulShot()
	{
		return _soulshot;
	}
	
	public int getSpiritShot()
	{
		return _spiritshot;
	}
	
	public int getSoulShotChance()
	{
		return _soulshotChance;
	}
	
	public int getSpiritShotChance()
	{
		return _spiritshotChance;
	}
	
	public String getClan()
	{
		return _clan;
	}
	
	public int getClanRange()
	{
		return _clanRange;
	}
	
	public AIType getAiType()
	{
		return _aiType;
	}
	
	public int getAggroRange()
	{
		return _aggroRange;
	}
}