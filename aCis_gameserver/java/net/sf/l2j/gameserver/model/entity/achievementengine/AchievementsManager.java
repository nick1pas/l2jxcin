package net.sf.l2j.gameserver.model.entity.achievementengine;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilderFactory;

import net.sf.l2j.L2DatabaseFactory;
import net.sf.l2j.gameserver.model.actor.instance.Player;
import net.sf.l2j.gameserver.model.entity.achievementengine.conditions.Adena;
import net.sf.l2j.gameserver.model.entity.achievementengine.conditions.Castle;
import net.sf.l2j.gameserver.model.entity.achievementengine.conditions.ClanLeader;
import net.sf.l2j.gameserver.model.entity.achievementengine.conditions.ClanLevel;
import net.sf.l2j.gameserver.model.entity.achievementengine.conditions.CompleteAchievements;
import net.sf.l2j.gameserver.model.entity.achievementengine.conditions.Crp;
import net.sf.l2j.gameserver.model.entity.achievementengine.conditions.CursedWeapon;
import net.sf.l2j.gameserver.model.entity.achievementengine.conditions.Hero;
import net.sf.l2j.gameserver.model.entity.achievementengine.conditions.ItemsCount;
import net.sf.l2j.gameserver.model.entity.achievementengine.conditions.Karma;
import net.sf.l2j.gameserver.model.entity.achievementengine.conditions.Level;
import net.sf.l2j.gameserver.model.entity.achievementengine.conditions.MageClass;
import net.sf.l2j.gameserver.model.entity.achievementengine.conditions.Marry;
import net.sf.l2j.gameserver.model.entity.achievementengine.conditions.MaxCp;
import net.sf.l2j.gameserver.model.entity.achievementengine.conditions.MaxHp;
import net.sf.l2j.gameserver.model.entity.achievementengine.conditions.MaxMp;
import net.sf.l2j.gameserver.model.entity.achievementengine.conditions.MinCMcount;
import net.sf.l2j.gameserver.model.entity.achievementengine.conditions.Noble;
import net.sf.l2j.gameserver.model.entity.achievementengine.conditions.OnlineTime;
import net.sf.l2j.gameserver.model.entity.achievementengine.conditions.Pk;
import net.sf.l2j.gameserver.model.entity.achievementengine.conditions.Pvp;
import net.sf.l2j.gameserver.model.entity.achievementengine.conditions.RaidKill;
import net.sf.l2j.gameserver.model.entity.achievementengine.conditions.SkillEnchant;
import net.sf.l2j.gameserver.model.entity.achievementengine.conditions.Sub;
import net.sf.l2j.gameserver.model.entity.achievementengine.conditions.Vip;
import net.sf.l2j.gameserver.model.entity.achievementengine.enchant.Chest;
import net.sf.l2j.gameserver.model.entity.achievementengine.enchant.Feet;
import net.sf.l2j.gameserver.model.entity.achievementengine.enchant.Gloves;
import net.sf.l2j.gameserver.model.entity.achievementengine.enchant.Head;
import net.sf.l2j.gameserver.model.entity.achievementengine.enchant.Legs;
import net.sf.l2j.gameserver.model.entity.achievementengine.enchant.WeaponEnchant;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class AchievementsManager
{
	private Map<Integer, Achievement> _achievementList = new HashMap<>();
	
	private static Logger _log = Logger.getLogger(AchievementsManager.class.getName());
	
	public AchievementsManager()
	{
		loadAchievements();
	}
	
	private void loadAchievements()
	{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(false);
		factory.setIgnoringComments(true);
		
		File file = new File("data/xml/achievements.xml");
		if (!file.exists())
		{
			_log.warning("[AchievementsEngine] Error: achievements xml file does not exist, check directory!");
		}
		try
		{
			Document doc = factory.newDocumentBuilder().parse(file);
			
			for (Node list = doc.getFirstChild(); list != null; list = list.getNextSibling())
			{
				if ("list".equalsIgnoreCase(list.getNodeName()))
				{
					for (Node achievement = list.getFirstChild(); achievement != null; achievement = achievement.getNextSibling())
					{
						if ("achievement".equalsIgnoreCase(achievement.getNodeName()))
						{
							int id = checkInt(achievement, "id");
							
							String name = String.valueOf(achievement.getAttributes().getNamedItem("name").getNodeValue());
							String description = String.valueOf(achievement.getAttributes().getNamedItem("description").getNodeValue());
							String reward = String.valueOf(achievement.getAttributes().getNamedItem("reward").getNodeValue());
							boolean repeat = checkBoolean(achievement, "repeatable");
							
							List<Condition> conditions = conditionList(achievement.getAttributes());
							
							_achievementList.put(id, new Achievement(id, name, description, reward, repeat, conditions));
							alterTable(id);
						}
					}
				}
			}
			_log.info("[AchievementsEngine] Successfully loaded: " + getAchievementList().size() + " achievements from xml!");
		}
		catch (Exception e)
		{
			_log.warning("[AchievementsEngine] Error: " + e);
			e.printStackTrace();
		}
	}
	
	public void rewardForAchievement(int achievementID, Player player)
	{
		Achievement achievement = _achievementList.get(achievementID);
		
		for (int id : achievement.getRewardList().keySet())
			player.addItem(achievement.getName(), id, achievement.getRewardList().get(id), player, true);
	}
	
	private static boolean checkBoolean(Node d, String nodename)
	{
		boolean b = false;
		try
		{
			b = Boolean.valueOf(d.getAttributes().getNamedItem(nodename).getNodeValue());
		}
		catch (Exception e)
		{
		}
		return b;
	}
	
	private static int checkInt(Node d, String nodename)
	{
		int i = 0;
		try
		{
			i = Integer.valueOf(d.getAttributes().getNamedItem(nodename).getNodeValue());
		}
		catch (Exception e)
		{
		}
		return i;
	}
	
	/**
	 * Alter table, catch exception if already exist.
	 * @param fieldID
	 */
	private static void alterTable(int fieldID)
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			Statement statement = con.createStatement();
			statement.executeUpdate("ALTER TABLE achievements ADD a" + fieldID + " INT DEFAULT 0");
			statement.close();
		}
		catch (SQLException e)
		{
		}
	}
	
	public List<Condition> conditionList(NamedNodeMap attributesList)
	{
		List<Condition> conditions = new ArrayList<>();
		
		for (int j = 0; j < attributesList.getLength(); j++)
		{
			addToConditionList(attributesList.item(j).getNodeName(), attributesList.item(j).getNodeValue(), conditions);
		}
		return conditions;
	}
	
	public Map<Integer, Achievement> getAchievementList()
	{
		return _achievementList;
	}
	
	public static AchievementsManager getInstance()
	{
		return SingletonHolder._instance;
	}
	
	private static class SingletonHolder
	{
		protected static final AchievementsManager _instance = new AchievementsManager();
	}
	
	private static void addToConditionList(String nodeName, Object value, List<Condition> conditions)
	{
		if (nodeName.equals("minLevel"))
			conditions.add(new Level(value));
		
		else if (nodeName.equals("minPvPCount"))
			conditions.add(new Pvp(value));
		
		else if (nodeName.equals("minPkCount"))
			conditions.add(new Pk(value));
		
		else if (nodeName.equals("minClanLevel"))
			conditions.add(new ClanLevel(value));
		
		else if (nodeName.equals("mustBeHero"))
			conditions.add(new Hero(value));
		
		else if (nodeName.equals("mustBeNoble"))
			conditions.add(new Noble(value));
		
		else if (nodeName.equals("mustBeClanLeader"))
			conditions.add(new ClanLeader(value));
		
		else if (nodeName.equals("minWeaponEnchant"))
			conditions.add(new WeaponEnchant(value));
		
		else if (nodeName.equals("minKarmaCount"))
			conditions.add(new Karma(value));
		
		else if (nodeName.equals("minAdenaCount"))
			conditions.add(new Adena(value));
		
		else if (nodeName.equals("minClanMembersCount"))
			conditions.add(new MinCMcount(value));
		
		else if (nodeName.equals("maxHP"))
			conditions.add(new MaxHp(value));
		
		else if (nodeName.equals("maxMP"))
			conditions.add(new MaxMp(value));
		
		else if (nodeName.equals("maxCP"))
			conditions.add(new MaxCp(value));
		
		else if (nodeName.equals("mustBeMarried"))
			conditions.add(new Marry(value));
		
		else if (nodeName.equals("itemAmmount"))
			conditions.add(new ItemsCount(value));
		
		else if (nodeName.equals("crpAmmount"))
			conditions.add(new Crp(value));
		
		else if (nodeName.equals("lordOfCastle"))
			conditions.add(new Castle(value));
		
		else if (nodeName.equals("mustBeMageClass"))
			conditions.add(new MageClass(value));
		
		else if (nodeName.equals("mustBeVip"))
			conditions.add(new Vip(value));
		
		else if (nodeName.equals("raidToKill"))
			conditions.add(new RaidKill(value));
		
		else if (nodeName.equals("CompleteAchievements"))
			conditions.add(new CompleteAchievements(value));
		
		else if (nodeName.equals("minSubclassCount"))
			conditions.add(new Sub(value));
		
		else if (nodeName.equals("minSkillEnchant"))
			conditions.add(new SkillEnchant(value));
		
		else if (nodeName.equals("minOnlineTime"))
			conditions.add(new OnlineTime(value));
		
		else if (nodeName.equals("Cursedweapon"))
			conditions.add(new CursedWeapon(value));
		
		else if (nodeName.equals("minHeadEnchant"))
			conditions.add(new Head(value));
		
		else if (nodeName.equals("minChestEnchant"))
			conditions.add(new Chest(value));
		
		else if (nodeName.equals("minFeetEnchant"))
			conditions.add(new Feet(value));
		
		else if (nodeName.equals("minLegsEnchant"))
			conditions.add(new Legs(value));
		
		else if (nodeName.equals("minGlovestEnchant"))
			conditions.add(new Gloves(value));
	}
}