package net.sf.l2j.gameserver.model.actor.stat;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.model.L2Skill;
import net.sf.l2j.gameserver.model.actor.Character;
import net.sf.l2j.gameserver.skills.Calculator;
import net.sf.l2j.gameserver.skills.Env;
import net.sf.l2j.gameserver.skills.Stats;

public class CharStat
{
	private final Character _activeChar;
	
	private long _exp = 0;
	private int _sp = 0;
	private byte _level = 1;
	
	public CharStat(Character activeChar)
	{
		_activeChar = activeChar;
	}
	
	/**
	 * Calculate the new value of the state with modifiers that will be applied on the targeted Character.<BR>
	 * <BR>
	 * <B><U> Concept</U> :</B><BR>
	 * <BR>
	 * A Character owns a table of Calculators called <B>_calculators</B>. Each Calculator (a calculator per state) own a table of Func object. A Func object is a mathematic function that permit to calculate the modifier of a state (ex : REGENERATE_HP_RATE...) : <BR>
	 * <BR>
	 * FuncAtkAccuracy -> Math.sqrt(_player.getDEX())*6+_player.getLevel()<BR>
	 * <BR>
	 * When the calc method of a calculator is launched, each mathematic function is called according to its priority <B>_order</B>. Indeed, Func with lowest priority order is executed firsta and Funcs with the same order are executed in unspecified order. The result of the calculation is stored in
	 * the value property of an Env class instance.<BR>
	 * <BR>
	 * @param stat The stat to calculate the new value with modifiers
	 * @param init The initial value of the stat before applying modifiers
	 * @param target The L2Charcater whose properties will be used in the calculation (ex : CON, INT...)
	 * @param skill The L2Skill whose properties will be used in the calculation (ex : Level...)
	 * @return
	 */
	public final double calcStat(Stats stat, double init, Character target, L2Skill skill)
	{
		if (_activeChar == null || stat == null)
			return init;
		
		final int id = stat.ordinal();
		
		final Calculator c = _activeChar.getCalculators()[id];
		if (c == null || c.size() == 0)
			return init;
		
		// Create and init an Env object to pass parameters to the Calculator
		final Env env = new Env();
		env.setCharacter(_activeChar);
		env.setTarget(target);
		env.setSkill(skill);
		env.setValue(init);
		
		// Launch the calculation
		c.calc(env);
		
		// avoid some troubles with negative stats (some stats should never be negative)
		if (env.getValue() <= 0)
		{
			switch (stat)
			{
				case MAX_HP:
				case MAX_MP:
				case MAX_CP:
				case MAGIC_DEFENCE:
				case POWER_DEFENCE:
				case POWER_ATTACK:
				case MAGIC_ATTACK:
				case POWER_ATTACK_SPEED:
				case MAGIC_ATTACK_SPEED:
				case SHIELD_DEFENCE:
				case STAT_CON:
				case STAT_DEX:
				case STAT_INT:
				case STAT_MEN:
				case STAT_STR:
				case STAT_WIT:
					env.setValue(1);
			}
		}
		return env.getValue();
	}
	
	/**
	 * @return the STR of the Character (base+modifier).
	 */
	public final int getSTR()
	{
		return (int) calcStat(Stats.STAT_STR, _activeChar.getTemplate().getBaseSTR(), null, null);
	}
	
	/**
	 * @return the DEX of the Character (base+modifier).
	 */
	public final int getDEX()
	{
		return (int) calcStat(Stats.STAT_DEX, _activeChar.getTemplate().getBaseDEX(), null, null);
	}
	
	/**
	 * @return the CON of the Character (base+modifier).
	 */
	public final int getCON()
	{
		return (int) calcStat(Stats.STAT_CON, _activeChar.getTemplate().getBaseCON(), null, null);
	}
	
	/**
	 * @return the INT of the Character (base+modifier).
	 */
	public int getINT()
	{
		return (int) calcStat(Stats.STAT_INT, _activeChar.getTemplate().getBaseINT(), null, null);
	}
	
	/**
	 * @return the MEN of the Character (base+modifier).
	 */
	public final int getMEN()
	{
		return (int) calcStat(Stats.STAT_MEN, _activeChar.getTemplate().getBaseMEN(), null, null);
	}
	
	/**
	 * @return the WIT of the Character (base+modifier).
	 */
	public final int getWIT()
	{
		return (int) calcStat(Stats.STAT_WIT, _activeChar.getTemplate().getBaseWIT(), null, null);
	}
	
	/**
	 * @param target
	 * @param skill
	 * @return the Critical Hit rate (base+modifier) of the Character.
	 */
	public int getCriticalHit(Character target, L2Skill skill)
	{
		if (_activeChar == null)
		{
			return 1;
		}
		
		int criticalHit = Math.min((int) calcStat(Stats.CRITICAL_RATE, _activeChar.getTemplate().getBaseCritRate(), target, skill), 500);
		
		if (criticalHit > Config.MAX_PCRIT_RATE)
		{
			criticalHit = Config.MAX_PCRIT_RATE;
		}
		return criticalHit;
	}
	
	/**
	 * @param target
	 * @param skill
	 * @return the Magic Critical Hit rate (base+modifier) of the Character.
	 */
	public final int getMCriticalHit(Character target, L2Skill skill)
	{
		if (_activeChar == null)
		{
			return 1;
		}
		
		double mrate = calcStat(Stats.MCRITICAL_RATE, 8.0D, target, skill);
		
		if (mrate > Config.MAX_MCRIT_RATE)
		{
			mrate = Config.MAX_MCRIT_RATE;
		}
		return (int) mrate;
	}
	
	/**
	 * @param target
	 * @return the Attack Evasion rate (base+modifier) of the Character.
	 */
	public int getEvasionRate(Character target)
	{
		if (_activeChar == null)
		{
			return 1;
		}
		int val = (int) Math.round(calcStat(Stats.EVASION_RATE, 0.0D, target, null));
		
		if ((val > Config.MAX_EVASION) && (!_activeChar.isGM()))
		{
			val = Config.MAX_EVASION;
		}
		return val;
	}
	
	/**
	 * @return the Accuracy (base+modifier) of the Character in function of the Weapon Expertise Penalty.
	 */
	public int getAccuracy()
	{
		if (_activeChar == null)
		{
			return 1;
		}
		int val = (int) Math.round(calcStat(Stats.ACCURACY_COMBAT, 0.0D, null, null));
		
		if ((val > Config.MAX_ACCURACY) && (!_activeChar.isGM()))
		{
			val = Config.MAX_ACCURACY;
		}
		return val;
	}
	
	public int getMaxHp()
	{
		return (int) calcStat(Stats.MAX_HP, _activeChar.getTemplate().getBaseHpMax(_activeChar.getLevel()), null, null);
	}
	
	public int getMaxCp()
	{
		return 0;
	}
	
	public int getMaxMp()
	{
		return (int) calcStat(Stats.MAX_MP, _activeChar.getTemplate().getBaseMpMax(_activeChar.getLevel()), null, null);
	}
	
	/**
	 * @param target The Character targeted by the skill
	 * @param skill The L2Skill used against the target
	 * @return the MAtk (base+modifier) of the Character for a skill used in function of abnormal effects in progress.
	 */
	public int getMAtk(Character target, L2Skill skill)
	{
		double attack = _activeChar.getTemplate().getBaseMAtk() * ((_activeChar.isChampion()) ? Config.CHAMPION_ATK : 1);
		
		// Add the power of the skill to the attack effect
		if (skill != null)
			attack += skill.getPower();
		
		// Calculate modifiers Magic Attack
		return (int) calcStat(Stats.MAGIC_ATTACK, attack, target, skill);
	}
	
	/**
	 * @return the MAtk Speed (base+modifier) of the Character in function of the Armour Expertise Penalty.
	 */
	public int getMAtkSpd()
	{
		return (int) calcStat(Stats.MAGIC_ATTACK_SPEED, 333.0 * ((_activeChar.isChampion()) ? Config.CHAMPION_SPD_ATK : 1), null, null);
	}
	
	/**
	 * @param target The Character targeted by the skill
	 * @param skill The L2Skill used against the target
	 * @return the MDef (base+modifier) of the Character against a skill in function of abnormal effects in progress.
	 */
	public int getMDef(Character target, L2Skill skill)
	{
		// Calculate modifiers Magic Attack
		return (int) calcStat(Stats.MAGIC_DEFENCE, _activeChar.getTemplate().getBaseMDef() * ((_activeChar.isRaid()) ? Config.RAID_DEFENCE_MULTIPLIER : 1), target, skill);
	}
	
	/**
	 * @param target
	 * @return the PAtk (base+modifier) of the Character.
	 */
	public int getPAtk(Character target)
	{
		return (int) calcStat(Stats.POWER_ATTACK, _activeChar.getTemplate().getBasePAtk() * ((_activeChar.isChampion()) ? Config.CHAMPION_ATK : 1), target, null);
	}
	
	/**
	 * @return the PAtk Speed (base+modifier) of the Character in function of the Armour Expertise Penalty.
	 */
	public int getPAtkSpd()
	{
		return (int) calcStat(Stats.POWER_ATTACK_SPEED, _activeChar.getTemplate().getBasePAtkSpd() * ((_activeChar.isChampion()) ? Config.CHAMPION_SPD_ATK : 1), null, null);
	}
	
	/**
	 * @param target
	 * @return the PDef (base+modifier) of the Character.
	 */
	public int getPDef(Character target)
	{
		return (int) calcStat(Stats.POWER_DEFENCE, _activeChar.getTemplate().getBasePDef() * ((_activeChar.isRaid()) ? Config.RAID_DEFENCE_MULTIPLIER : 1), target, null);
	}
	
	/**
	 * @param target
	 * @return the PAtk Modifier against animals.
	 */
	public final double getPAtkAnimals(Character target)
	{
		return calcStat(Stats.PATK_ANIMALS, 1, target, null);
	}
	
	/**
	 * @param target
	 * @return the PAtk Modifier against dragons.
	 */
	public final double getPAtkDragons(Character target)
	{
		return calcStat(Stats.PATK_DRAGONS, 1, target, null);
	}
	
	/**
	 * @param target
	 * @return the PAtk Modifier against insects.
	 */
	public final double getPAtkInsects(Character target)
	{
		return calcStat(Stats.PATK_INSECTS, 1, target, null);
	}
	
	/**
	 * @param target
	 * @return the PAtk Modifier against monsters.
	 */
	public final double getPAtkMonsters(Character target)
	{
		return calcStat(Stats.PATK_MONSTERS, 1, target, null);
	}
	
	/**
	 * @param target
	 * @return the PAtk Modifier against plants.
	 */
	public final double getPAtkPlants(Character target)
	{
		return calcStat(Stats.PATK_PLANTS, 1, target, null);
	}
	
	/**
	 * @param target
	 * @return the PAtk Modifier against giants.
	 */
	public final double getPAtkGiants(Character target)
	{
		return calcStat(Stats.PATK_GIANTS, 1, target, null);
	}
	
	/**
	 * @param target
	 * @return the PAtk Modifier against magic creatures
	 */
	public final double getPAtkMagicCreatures(Character target)
	{
		return calcStat(Stats.PATK_MCREATURES, 1, target, null);
	}
	
	/**
	 * @param target
	 * @return the PDef Modifier against animals.
	 */
	public final double getPDefAnimals(Character target)
	{
		return calcStat(Stats.PDEF_ANIMALS, 1, target, null);
	}
	
	/**
	 * @param target
	 * @return the PDef Modifier against dragons.
	 */
	public final double getPDefDragons(Character target)
	{
		return calcStat(Stats.PDEF_DRAGONS, 1, target, null);
	}
	
	/**
	 * @param target
	 * @return the PDef Modifier against insects.
	 */
	public final double getPDefInsects(Character target)
	{
		return calcStat(Stats.PDEF_INSECTS, 1, target, null);
	}
	
	/**
	 * @param target
	 * @return the PDef Modifier against monsters.
	 */
	public final double getPDefMonsters(Character target)
	{
		return calcStat(Stats.PDEF_MONSTERS, 1, target, null);
	}
	
	/**
	 * @param target
	 * @return the PDef Modifier against plants.
	 */
	public final double getPDefPlants(Character target)
	{
		return calcStat(Stats.PDEF_PLANTS, 1, target, null);
	}
	
	/**
	 * @param target
	 * @return the PDef Modifier against giants.
	 */
	public final double getPDefGiants(Character target)
	{
		return calcStat(Stats.PDEF_GIANTS, 1, target, null);
	}
	
	/**
	 * @param target
	 * @return the PDef Modifier against giants.
	 */
	public final double getPDefMagicCreatures(Character target)
	{
		return calcStat(Stats.PDEF_MCREATURES, 1, target, null);
	}
	
	/**
	 * @return the Physical Attack range (base+modifier) of the Character.
	 */
	public int getPhysicalAttackRange()
	{
		return getActiveChar().getAttackType().getRange();
	}
	
	/**
	 * @return the ShieldDef rate (base+modifier) of the Character.
	 */
	public final int getShldDef()
	{
		return (int) calcStat(Stats.SHIELD_DEFENCE, 0, null, null);
	}
	
	/**
	 * @param skill
	 * @return the mpConsume.
	 */
	public final int getMpConsume(L2Skill skill)
	{
		if (skill == null)
			return 1;
		
		double mpConsume = skill.getMpConsume();
		if (skill.isDance())
		{
			if (_activeChar != null && _activeChar.getDanceCount() > 0)
				mpConsume += _activeChar.getDanceCount() * skill.getNextDanceMpCost();
		}
		
		if (skill.isDance())
			return (int) calcStat(Stats.DANCE_MP_CONSUME_RATE, mpConsume, null, null);
		
		if (skill.isMagic())
			return (int) calcStat(Stats.MAGICAL_MP_CONSUME_RATE, mpConsume, null, null);
		
		return (int) calcStat(Stats.PHYSICAL_MP_CONSUME_RATE, mpConsume, null, null);
	}
	
	/**
	 * @param skill
	 * @return the mpInitialConsume.
	 */
	public final int getMpInitialConsume(L2Skill skill)
	{
		if (skill == null)
			return 1;
		
		double mpConsume = skill.getMpInitialConsume();
		
		if (skill.isDance())
			return (int) calcStat(Stats.DANCE_MP_CONSUME_RATE, mpConsume, null, null);
		
		if (skill.isMagic())
			return (int) calcStat(Stats.MAGICAL_MP_CONSUME_RATE, mpConsume, null, null);
		
		return (int) calcStat(Stats.PHYSICAL_MP_CONSUME_RATE, mpConsume, null, null);
	}
	
	public int getAttackElementValue(byte attackAttribute)
	{
		switch (attackAttribute)
		{
			case 1: // wind
				return (int) calcStat(Stats.WIND_POWER, 0, null, null);
			case 2: // fire
				return (int) calcStat(Stats.FIRE_POWER, 0, null, null);
			case 3: // water
				return (int) calcStat(Stats.WATER_POWER, 0, null, null);
			case 4: // earth
				return (int) calcStat(Stats.EARTH_POWER, 0, null, null);
			case 5: // holy
				return (int) calcStat(Stats.HOLY_POWER, 0, null, null);
			case 6: // dark
				return (int) calcStat(Stats.DARK_POWER, 0, null, null);
			default:
				return 0;
		}
	}
	
	public int getDefenseElementValue(byte defenseAttribute)
	{
		switch (defenseAttribute)
		{
			case 1: // wind
				return (int) calcStat(Stats.WIND_RES, 0, null, null);
			case 2: // fire
				return (int) calcStat(Stats.FIRE_RES, 0, null, null);
			case 3: // water
				return (int) calcStat(Stats.WATER_RES, 0, null, null);
			case 4: // earth
				return (int) calcStat(Stats.EARTH_RES, 0, null, null);
			case 5: // holy
				return (int) calcStat(Stats.HOLY_RES, 0, null, null);
			case 6: // dark
				return (int) calcStat(Stats.DARK_RES, 0, null, null);
			default:
				return 0;
		}
	}
	
	/**
	 * Returns base running speed, given by owner template.<br>
	 * Player is affected by mount type.
	 * @return int : Base running speed.
	 */
	public int getBaseRunSpeed()
	{
		return _activeChar.getTemplate().getBaseRunSpeed();
	}
	
	/**
	 * Returns base walking speed, given by owner template.<br>
	 * Player is affected by mount type.
	 * @return int : Base walking speed.
	 */
	public int getBaseWalkSpeed()
	{
		return _activeChar.getTemplate().getBaseWalkSpeed();
	}
	
	/**
	 * Returns base movement speed, given by owner template and owner movement status.<br>
	 * Player is affected by mount type and by being in L2WaterZone.
	 * @return int : Base walking speed.
	 */
	protected final int getBaseMoveSpeed()
	{
		return _activeChar.isRunning() ? getBaseRunSpeed() : getBaseWalkSpeed();
	}
	
	/**
	 * Returns movement speed multiplier, which is used by client to set correct character/object movement speed.
	 * @return float : Movement speed multiplier.
	 */
	public final float getMovementSpeedMultiplier()
	{
		return getMoveSpeed() / getBaseMoveSpeed();
	}
	
	/**
	 * Returns attack speed multiplier, which is used by client to set correct character/object attack speed.
	 * @return float : Attack speed multiplier.
	 */
	public final float getAttackSpeedMultiplier()
	{
		return (float) ((1.1) * getPAtkSpd() / _activeChar.getTemplate().getBasePAtkSpd());
	}
	
	/**
	 * Returns final movement speed, given by owner template, owner status and effects.<br>
	 * Playable is affected by L2SwampZone.<br>
	 * Player is affected by L2SwampZone and armor grade penalty.
	 * @return float : Final movement speed.
	 */
	public float getMoveSpeed()
	{
		return (float) calcStat(Stats.RUN_SPEED, getBaseMoveSpeed(), null, null);
	}
	
	public long getExp()
	{
		return _exp;
	}
	
	public void setExp(long value)
	{
		_exp = value;
	}
	
	public int getSp()
	{
		return _sp;
	}
	
	public void setSp(int value)
	{
		_sp = value;
	}
	
	public byte getLevel()
	{
		return _level;
	}
	
	public void setLevel(byte value)
	{
		_level = value;
	}
	
	public Character getActiveChar()
	{
		return _activeChar;
	}
}