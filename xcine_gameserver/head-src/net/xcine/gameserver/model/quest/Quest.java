package net.xcine.gameserver.model.quest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javolution.util.FastList;

import net.xcine.Config;
import net.xcine.L2DatabaseFactory;
import net.xcine.gameserver.ThreadPoolManager;
import net.xcine.gameserver.cache.HtmCache;
import net.xcine.gameserver.datatables.NpcTable;
import net.xcine.gameserver.instancemanager.QuestManager;
import net.xcine.gameserver.instancemanager.ZoneManager;
import net.xcine.gameserver.model.L2Clan;
import net.xcine.gameserver.model.L2Object;
import net.xcine.gameserver.model.L2Skill;
import net.xcine.gameserver.model.L2Spawn;
import net.xcine.gameserver.model.Location;
import net.xcine.gameserver.model.actor.L2Character;
import net.xcine.gameserver.model.actor.L2Npc;
import net.xcine.gameserver.model.actor.instance.L2PcInstance;
import net.xcine.gameserver.model.zone.L2ZoneType;
import net.xcine.gameserver.network.SystemMessageId;
import net.xcine.gameserver.network.serverpackets.ActionFailed;
import net.xcine.gameserver.network.serverpackets.NpcHtmlMessage;
import net.xcine.gameserver.network.serverpackets.SystemMessage;
import net.xcine.gameserver.scripting.ManagedScript;
import net.xcine.gameserver.scripting.ScriptManager;
import net.xcine.gameserver.templates.chars.L2NpcTemplate;
import net.xcine.util.Rnd;

/**
 * @author Luis Arias
 */
public class Quest extends ManagedScript
{
	protected static final Logger _log = Logger.getLogger(Quest.class.getName());
	
	private static final String LOAD_QUEST_STATES = "SELECT name,value FROM character_quests WHERE charId=? AND var='<state>'";
	private static final String LOAD_QUEST_VARIABLES = "SELECT name,var,value FROM character_quests WHERE charId=? AND var<>'<state>'";
	private static final String DELETE_INVALID_QUEST = "DELETE FROM character_quests WHERE name=?";
	
	private static final String SET_GLOBAL_QUEST_VAL = "REPLACE INTO quest_global_data (quest_name,var,value) VALUES (?,?,?)";
	private static final String GET_GLOBAL_QUEST_VAL = "SELECT value FROM quest_global_data WHERE quest_name=? AND var=?";
	private static final String DEL_GLOBAL_QUEST_VAL = "DELETE FROM quest_global_data WHERE quest_name=? AND var=?";
	private static final String DEL_ALL_GLOBAL_QUEST_VAL = "DELETE FROM quest_global_data WHERE quest_name=?";
	
	private static final String HTML_NONE_AVAILABLE = "<html><body>You are either not on a quest that involves this NPC, or you don't meet this NPC's minimum quest requirements.</body></html>";
	private static final String HTML_ALREADY_COMPLETED = "<html><body>This quest has already been completed.</body></html>";
	
	public static final byte STATE_CREATED = 0;
	public static final byte STATE_STARTED = 1;
	public static final byte STATE_COMPLETED = 2;
	
	private final Map<Integer, List<QuestTimer>> _eventTimers = new ConcurrentHashMap<>();
	
	private final int _id;
	private final String _name;
	private final String _descr;
	private boolean _onEnterWorld;
	protected int[] questItemIds = null;

	private Object _questItemIds;

	private State _initialState;

	/**
	 * Return ID of the quest
	 * @return int
	 */
	public int getQuestIntId()
	{
		return STATE_COMPLETED;
	}

	public void setInitialState(State state)
	{
		_initialState = state;
	}

	/**
	 * Add a new QuestState to the database and return it.
	 * @param player
	 * @return QuestState : QuestState created
	 */
	public QuestState newQuestState1(L2PcInstance player)
	{
		if(getInitialState() == null)
			_initialState = new State("Start", this);

		return null ;
	}

	/**
	 * @param qs
	 * @return 
	 */
	static QuestState createQuestInDb(QuestState qs)
	{
		return qs;
		// TODO Auto-generated method stub
		
	}

	/**
	 * Return initial state of the quest
	 * @return State
	 */
	public State getInitialState()
	{
		return _initialState;
	}


	/**
	 * (Constructor)Add values to class variables and put the quest in HashMaps.
	 * @param questId : int pointing out the ID of the quest
	 * @param name : String corresponding to the name of the quest
	 * @param descr : String for the description of the quest
	 */
	public Quest(int questId, String name, String descr)
	{
		_id = questId;
		_name = name;
		_descr = descr;
		_onEnterWorld = false;
		
		QuestManager.getInstance().addQuest(this);
	}
	
	/**
	 * Return ID of the quest.
	 * @return int
	 */
	public int getQuestId()
	{
		return _id;
	}
	
	/**
	 * Return type of the quest.
	 * @return boolean : True for (live) quest, False for script, AI, etc.
	 */
	public boolean isRealQuest()
	{
		return _id > 0;
	}
	
	/**
	 * Return name of the quest.
	 * @return String
	 */
	@Override
	public String getName()
	{
		return _name;
	}
	
	/**
	 * Return description of the quest.
	 * @return String
	 */
	public String getDescr()
	{
		return _descr;
	}
	
	public void setOnEnterWorld(boolean val)
	{
		_onEnterWorld = val;
	}
	
	public boolean getOnEnterWorld()
	{
		return _onEnterWorld;
	}
	
	/**
	 * Return registered quest items.
	 * @return int[]
	 */
	int[] getRegisteredItemIds1()
	{
		return questItemIds;
	}
	
	/**
	 * Add a new QuestState to the database and return it.
	 * @param player
	 * @return QuestState : QuestState created
	 */
	public QuestState newQuestState(L2PcInstance player)
	{
		return new QuestState(player, this, STATE_CREATED);
	}
	
	/**
	 * Add quests to the L2PCInstance of the player.<BR>
	 * <BR>
	 * <U><I>Action : </U></I><BR>
	 * Add state of quests, drops and variables for quests in the HashMap _quest of L2PcInstance
	 * @param player : Player who is entering the world
	 */
	public final static void playerEnter(L2PcInstance player)
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement invalidQuest = con.prepareStatement(DELETE_INVALID_QUEST);
			
			PreparedStatement statement = con.prepareStatement(LOAD_QUEST_STATES);
			statement.setInt(1, player.getObjectId());
			ResultSet rs = statement.executeQuery();
			while (rs.next())
			{
				String questId = rs.getString("name");
				
				Quest q = QuestManager.getInstance().getQuest(questId);
				if (q == null)
				{
					if (Config.AUTODELETE_INVALID_QUEST_DATA)
					{
						invalidQuest.setString(1, questId);
						invalidQuest.executeUpdate();
					}
					
					_log.finer("Unknown  quest " + questId + " for player " + player.getName());
					continue;
				}
				
				new QuestState(player, q, rs.getByte("value"));
			}
			rs.close();
			statement.close();
			
			statement = con.prepareStatement(LOAD_QUEST_VARIABLES);
			statement.setInt(1, player.getObjectId());
			rs = statement.executeQuery();
			while (rs.next())
			{
				String questId = rs.getString("name");
				
				QuestState qs = player.getQuestState(questId);
				if (qs == null)
				{
					if (Config.AUTODELETE_INVALID_QUEST_DATA)
					{
						invalidQuest.setString(1, questId);
						invalidQuest.executeUpdate();
					}
					
					_log.finer("Unknown quest " + questId + " for player " + player.getName());
					continue;
				}
				
				qs.setInternal(rs.getString("var"), rs.getString("value"));
			}
			rs.close();
			statement.close();
			
			invalidQuest.close();
		}
		catch (Exception e)
		{
			_log.log(Level.WARNING, "could not insert char quest:", e);
		}
	}
	
	/**
	 * Insert (or Update) in the database variables that need to stay persistant for this quest after a reboot. This function is for storage of values that do not related to a specific player but are global for all characters. For example, if we need to disable a quest-gatekeeper until a certain
	 * time (as is done with some grand-boss gatekeepers), we can save that time in the DB.
	 * @param var : String designating the name of the variable for the quest
	 * @param value : String designating the value of the variable for the quest
	 */
	public final void setGlobalQuestVar(String var, String value)
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement(SET_GLOBAL_QUEST_VAL);
			statement.setString(1, getName());
			statement.setString(2, var);
			statement.setString(3, value);
			statement.executeUpdate();
			statement.close();
		}
		catch (Exception e)
		{
			_log.log(Level.WARNING, "could not set global quest variable:", e);
		}
	}
	
	/**
	 * Read from the database a previously saved variable for this quest. Due to performance considerations, this function should best be used only when the quest is first loaded. Subclasses of this class can define structures into which these loaded values can be saved. However, on-demand usage of
	 * this function throughout the script is not prohibited, only not recommended. Values read from this function were entered by calls to "saveGlobalQuestVar"
	 * @param var : String designating the name of the variable for the quest
	 * @return String : String representing the loaded value for the passed var, or an empty string if the var was invalid
	 */
	public final String getGlobalQuestVar(String var)
	{
		String result = "";
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement(GET_GLOBAL_QUEST_VAL);
			statement.setString(1, getName());
			statement.setString(2, var);
			ResultSet rs = statement.executeQuery();
			if (rs.first())
				result = rs.getString(1);
			rs.close();
			statement.close();
		}
		catch (Exception e)
		{
			_log.log(Level.WARNING, "could not load global quest variable:", e);
		}
		return result;
	}
	
	/**
	 * Permanently delete from the database a global quest variable that was previously saved for this quest.
	 * @param var : String designating the name of the variable for the quest
	 */
	public final void deleteGlobalQuestVar(String var)
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement(DEL_GLOBAL_QUEST_VAL);
			statement.setString(1, getName());
			statement.setString(2, var);
			statement.executeUpdate();
			statement.close();
		}
		catch (Exception e)
		{
			_log.log(Level.WARNING, "could not delete global quest variable:", e);
		}
	}
	
	/**
	 * Permanently delete from the database all global quest variables that was previously saved for this quest.
	 */
	public final void deleteAllGlobalQuestVars()
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement(DEL_ALL_GLOBAL_QUEST_VAL);
			statement.setString(1, getName());
			statement.executeUpdate();
			statement.close();
		}
		catch (Exception e)
		{
			_log.log(Level.WARNING, "could not delete all global quest variables:", e);
		}
	}
	
	/**
	 * @param player : The player to make checks on.
	 * @param object : to take range reference from
	 * @return A random party member or the passed player if he has no party.
	 */
	public L2PcInstance getRandomPartyMember(L2PcInstance player, L2Object object)
	{
		// No valid player instance is passed, there is nothing to check.
		if (player == null)
			return null;
		
		// No party or no object, return player.
		if (object == null || !player.isInParty())
			return player;
		
		// Player's party.
		List<L2PcInstance> members = new ArrayList<>();
		for (L2PcInstance member : player.getParty().getPartyMembers())
		{
			if (member.isInsideRadius(object, Config.ALT_PARTY_RANGE, true, false))
				members.add(member);
		}
		
		// No party members, return. (note: player is party member too, in most cases he is included in members too)
		if (members.isEmpty())
			return null;
		
		// Random party member.
		return members.get(Rnd.get(members.size()));
	}
	
	/**
	 * Auxiliary function for party quests. Checks the player's condition. Player member must be within Config.ALT_PARTY_RANGE distance from the npc. If npc is null, distance condition is ignored.
	 * @param player : the instance of a player whose party is to be searched
	 * @param npc : the instance of a L2Npc to compare distance
	 * @param var : a tuple specifying a quest condition that must be satisfied for a party member to be considered.
	 * @param value : a tuple specifying a quest condition that must be satisfied for a party member to be considered.
	 * @return QuestState : The QuestState of that player.
	 */
	public QuestState checkPlayerCondition(L2PcInstance player, L2Npc npc, String var, String value)
	{
		// No valid player instance is passed, there is nothing to check.
		if (player == null)
			return null;
		
		// Check player's quest conditions.
		QuestState st = player.getQuestState(getName());
		if (st == null)
			return null;
		
		// Condition exists? Condition has correct value?
		if (st.get(var) == null || !value.equalsIgnoreCase(st.get(var)))
			return null;
		
		// Invalid npc instance?
		if (npc == null)
			return null;
		
		// Player is in range?
		if (!player.isInsideRadius(npc, Config.ALT_PARTY_RANGE, true, false))
			return null;
		
		return st;
	}
	
	/**
	 * Auxiliary function for party quests. Note: This function is only here because of how commonly it may be used by quest developers. For any variations on this function, the quest script can always handle things on its own
	 * @param player : the instance of a player whose party is to be searched
	 * @param npc : the instance of a L2Npc to compare distance
	 * @param var : a tuple specifying a quest condition that must be satisfied for a party member to be considered.
	 * @param value : a tuple specifying a quest condition that must be satisfied for a party member to be considered.
	 * @return List<L2PcInstance> : List of party members that matches the specified condition, empty list if none matches. If the var is null, empty list is returned (i.e. no condition is applied). The party member must be within Config.ALT_PARTY_RANGE distance from the npc. If npc is null,
	 *         distance condition is ignored.
	 */
	public List<L2PcInstance> getPartyMembers(L2PcInstance player, L2Npc npc, String var, String value)
	{
		// Output list.
		List<L2PcInstance> candidates = new ArrayList<>();
		
		// Valid player instance is passed and player is in a party? Check party.
		if (player != null && player.isInParty())
		{
			// Filter candidates from player's party.
			for (L2PcInstance partyMember : player.getParty().getPartyMembers())
			{
				if (partyMember == null)
					continue;
				
				// Check party members' quest condition.
				if (checkPlayerCondition(partyMember, npc, var, value) != null)
					candidates.add(partyMember);
			}
		}
		// Player is solo, check the player
		else if (checkPlayerCondition(player, npc, var, value) != null)
			candidates.add(player);
		
		return candidates;
	}
	
	/**
	 * Auxiliary function for party quests. Note: This function is only here because of how commonly it may be used by quest developers. For any variations on this function, the quest script can always handle things on its own
	 * @param player : the instance of a player whose party is to be searched
	 * @param npc : the instance of a L2Npc to compare distance
	 * @param var : a tuple specifying a quest condition that must be satisfied for a party member to be considered.
	 * @param value : a tuple specifying a quest condition that must be satisfied for a party member to be considered.
	 * @return L2PcInstance : L2PcInstance for a random party member that matches the specified condition, or null if no match. If the var is null, null is returned (i.e. no condition is applied). The party member must be within 1500 distance from the npc. If npc is null, distance condition is
	 *         ignored.
	 */
	public L2PcInstance getRandomPartyMember(L2PcInstance player, L2Npc npc, String var, String value)
	{
		// No valid player instance is passed, there is nothing to check.
		if (player == null)
			return null;
		
		// Get all candidates fulfilling the condition.
		final List<L2PcInstance> candidates = getPartyMembers(player, npc, var, value);
		
		// No candidate, return.
		if (candidates.isEmpty())
			return null;
		
		// Return random candidate.
		return candidates.get(Rnd.get(candidates.size()));
	}
	
	/**
	 * Auxiliary function for party quests. Note: This function is only here because of how commonly it may be used by quest developers. For any variations on this function, the quest script can always handle things on its own.
	 * @param player : the instance of a player whose party is to be searched
	 * @param npc : the instance of a L2Npc to compare distance
	 * @param value : the value of the "cond" variable that must be matched
	 * @return L2PcInstance : L2PcInstance for a random party member that matches the specified condition, or null if no match.
	 */
	public L2PcInstance getRandomPartyMember(L2PcInstance player, L2Npc npc, String value)
	{
		return getRandomPartyMember(player, npc, "cond", value);
	}
	
	/**
	 * Auxiliary function for party quests. Checks the player's condition. Player member must be within Config.ALT_PARTY_RANGE distance from the npc. If npc is null, distance condition is ignored.
	 * @param player : the instance of a player whose party is to be searched
	 * @param npc : the instance of a L2Npc to compare distance
	 * @param state : the state in which the party member's QuestState must be in order to be considered.
	 * @return QuestState : The QuestState of that player.
	 */
	public QuestState checkPlayerState(L2PcInstance player, L2Npc npc, byte state)
	{
		// No valid player instance is passed, there is nothing to check.
		if (player == null)
			return null;
		
		// Check player's quest conditions.
		QuestState st = player.getQuestState(getName());
		if (st == null)
			return null;
		
		// State correct?
		if (st.getState() != state)
			return null;
		
		// Invalid npc instance?
		if (npc == null)
			return null;
		
		// Player is in range?
		if (!player.isInsideRadius(npc, Config.ALT_PARTY_RANGE, true, false))
			return null;
		
		return st;
	}
	
	/**
	 * Auxiliary function for party quests. Note: This function is only here because of how commonly it may be used by quest developers. For any variations on this function, the quest script can always handle things on its own.
	 * @param player : the instance of a player whose party is to be searched
	 * @param npc : the instance of a L2Npc to compare distance
	 * @param state : the state in which the party member's QuestState must be in order to be considered.
	 * @return List<L2PcInstance> : List of party members that matches the specified quest state, empty list if none matches. The party member must be within Config.ALT_PARTY_RANGE distance from the npc. If npc is null, distance condition is ignored.
	 */
	public List<L2PcInstance> getPartyMembersState(L2PcInstance player, L2Npc npc, byte state)
	{
		// Output list.
		List<L2PcInstance> candidates = new ArrayList<>();
		
		// Valid player instance is passed and player is in a party? Check party.
		if (player != null && player.isInParty())
		{
			// Filter candidates from player's party.
			for (L2PcInstance partyMember : player.getParty().getPartyMembers())
			{
				if (partyMember == null)
					continue;
				
				// Check party members' quest state.
				if (checkPlayerState(partyMember, npc, state) != null)
					candidates.add(partyMember);
			}
		}
		// Player is solo, check the player
		else if (checkPlayerState(player, npc, state) != null)
			candidates.add(player);
		
		return candidates;
	}
	
	/**
	 * Auxiliary function for party quests. Note: This function is only here because of how commonly it may be used by quest developers. For any variations on this function, the quest script can always handle things on its own.
	 * @param player : the instance of a player whose party is to be searched
	 * @param npc : the instance of a monster to compare distance
	 * @param state : the state in which the party member's QuestState must be in order to be considered.
	 * @return L2PcInstance: L2PcInstance for a random party member that matches the specified condition, or null if no match. If the var is null, any random party member is returned (i.e. no condition is applied).
	 */
	public L2PcInstance getRandomPartyMemberState(L2PcInstance player, L2Npc npc, byte state)
	{
		// No valid player instance is passed, there is nothing to check.
		if (player == null)
			return null;
		
		// Get all candidates fulfilling the condition.
		final List<L2PcInstance> candidates = getPartyMembersState(player, npc, state);
		
		// No candidate, return.
		if (candidates.isEmpty())
			return null;
		
		// Return random candidate.
		return candidates.get(Rnd.get(candidates.size()));
	}

	public String showHtmlFile1(L2PcInstance player, String fileName)
	{
		String questId = getName();

		String directory = getDescr().toLowerCase();
		String content = HtmCache.getInstance().getHtm("data/scripts/" + directory + "/" + questId + "/" + fileName);

		if(content == null)
		{
			content = HtmCache.getInstance().getHtmForce("data/scripts/quests/" + questId + "/" + fileName);
		}

		if(player != null && player.getTarget() != null)
		{
			content = content.replaceAll("%objectId%", String.valueOf(player.getTarget().getObjectId()));
		}

		if(content != null)
		{
			NpcHtmlMessage npcReply = new NpcHtmlMessage(5);
			npcReply.setHtml(content);
			npcReply.replace("%playername%", player.getName());
			player.sendPacket(npcReply);
			npcReply = null;
		}

		if ((player.isGM()) && (player.getAccessLevel().getLevel() == Config.GM_ACCESS_LEVEL))
			player.sendChatMessage(0, 0, "HTML", "scripts/" + directory + "/" + questId + "/" + fileName);
		return content;
	}

	public L2Npc addSpawn(int npcId, L2Character cha)
	{
		return QuestSpawn.getInstance().addSpawn(npcId, cha.getX(), cha.getY(), cha.getZ(), cha.getHeading(), false, 0);
	}

	public L2Npc addSpawn(int npcId, int x, int y, int z, int heading, boolean randomOffset, int despawnDelay)
	{
		return QuestSpawn.getInstance().addSpawn(npcId, x, y, z, heading, randomOffset, despawnDelay);
	}


	public void registerItem1(L2PcInstance itemId)
	{
		if(_questItemIds == null)
		{
			_questItemIds = new FastList<Integer>();
		}

		((ArrayList<L2PcInstance>) _questItemIds).add(itemId);
	}

	public Object getRegisteredItemIds()
	{
		return _questItemIds;
	}

	@Override
	public ScriptManager<?> getScriptManager1()
	{
		return QuestManager.getInstance();
	}

	public boolean unload1()
	{
		saveGlobalData();
		Map<Integer, List<QuestTimer>> _allEventTimers = null;
		for(List<QuestTimer> timers : _allEventTimers.values())
		{
			for(QuestTimer timer : timers)
			{
				timer.cancel();
			}
		}
		_allEventTimers.clear();
		return QuestManager.getInstance().removeQuest(this);
	}

	@Override
	public boolean reload1()
	{
		unload1();
		return super.reload1();
	}

	public String getScriptName()
	{
		return getName();
	}

	@SuppressWarnings("synthetic-access")
	public class tmpOnSkillSee implements Runnable
	{
		private final L2Npc _npc;
		private final L2PcInstance _caster;
		private final L2Skill _skill;
		private final L2Object[] _targets;
		private final boolean _isPet;

		public tmpOnSkillSee(L2Npc npc, L2PcInstance caster, L2Skill skill, L2Object[] targets, boolean isPet)
		{
			_npc = npc;
			_caster = caster;
			_skill = skill;
			_targets = targets;
			_isPet = isPet;
		}

		@Override
		public void run()
		{
			String res = null;
			try
			{
				res = onSkillSee(_npc, _caster, _skill, _targets, _isPet);
			}
			catch(Exception e)
			{
				showError(_caster, e);
			}
			showResult(_caster, res);
		}
	}

	public void registerMobs(int[] mobs)
	{
		for(int id : mobs)
		{
			addEventId(id, QuestEventType.ON_ATTACK);
			addEventId(id, QuestEventType.ON_KILL);
			addEventId(id, QuestEventType.ON_SPAWN);
			addEventId(id, QuestEventType.ON_SPELL_FINISHED);
			addEventId(id, QuestEventType.ON_FACTION_CALL);
			addEventId(id, QuestEventType.ON_AGGRO_RANGE_ENTER);
		}
	}

	public void registerMobs(int[] mobs, QuestEventType... types)
	{
		for(int id : mobs)
		{
			for(QuestEventType type : types)
			{
				addEventId(id, type);
			}
		}
	}

	public static <T> boolean contains(T[] array, T obj)
	{
		for(int i = 0; i < array.length; i++)
		{
			if(array[i] == obj)
			{
				return true;
			}
		}
		return false;
	}

	public static boolean contains(int[] array, int obj)
	{
		for(int i = 0; i < array.length; i++)
		{
			if(array[i] == obj)
			{
				return true;
			}
		}
		return false;
	}
	/**
	 * Retrieves the clan leader quest state.
	 * @param player : the player to test
	 * @param npc : the npc to test distance
	 * @return the QuestState of the leader, or null if not found
	 */
	public QuestState getClanLeaderQuestState(L2PcInstance player, L2Npc npc)
	{
		// If player is the leader, retrieves directly the qS and bypass others checks
		if (player.isClanLeader() && player.isInsideRadius(npc, Config.ALT_PARTY_RANGE, true, false))
			return player.getQuestState(getName());
		
		// Verify if the player got a clan
		L2Clan clan = player.getClan();
		if (clan == null)
			return null;
		
		// Verify if the leader is online
		L2PcInstance leader = clan.getLeader().getPlayerInstance();
		if (leader == null)
			return null;
		
		// Verify if the player is on the radius of the leader. If true, send leader's quest state.
		if (leader.isInsideRadius(npc, Config.ALT_PARTY_RANGE, true, false))
			return leader.getQuestState(getName());
		
		return null;
	}
	
	/**
	 * Add a timer to the quest, if it doesn't exist already. If the timer is repeatable, it will auto-fire automatically, at a fixed rate, until explicitly canceled.
	 * @param name name of the timer (also passed back as "event" in onAdvEvent)
	 * @param time time in ms for when to fire the timer
	 * @param npc npc associated with this timer (can be null)
	 * @param player player associated with this timer (can be null)
	 * @param repeating indicates if the timer is repeatable or one-time.
	 */
	public void startQuestTimer(String name, long time, L2Npc npc, L2PcInstance player, boolean repeating)
	{
		// Get quest timers for this timer type.
		List<QuestTimer> timers = _eventTimers.get(name.hashCode());
		if (timers == null)
		{
			// None timer exists, create new list.
			timers = new CopyOnWriteArrayList<>();
			
			// Add new timer to the list.
			timers.add(new QuestTimer(this, name, npc, player, time, repeating));
			
			// Add timer list to the map.
			_eventTimers.put(name.hashCode(), timers);
		}
		else
		{
			// Check, if specific timer already exists.
			for (QuestTimer timer : timers)
			{
				// If so, return.
				if (timer != null && timer.equals(this, name, npc, player))
					return;
			}
			
			// Add new timer to the list.
			timers.add(new QuestTimer(this, name, npc, player, time, repeating));
		}
	}
	
	public QuestTimer getQuestTimer(String name, L2Npc npc, L2PcInstance player)
	{
		// Get quest timers for this timer type.
		List<QuestTimer> timers = _eventTimers.get(name.hashCode());
		
		// Timer list does not exists or is empty, return.
		if (timers == null || timers.isEmpty())
			return null;
		
		// Check, if specific timer exists.
		for (QuestTimer timer : timers)
		{
			// If so, return him.
			if (timer != null && timer.equals(this, name, npc, player))
				return timer;
		}
		return null;
	}
	
	public void cancelQuestTimer(String name, L2Npc npc, L2PcInstance player)
	{
		// If specified timer exists, cancel him.
		QuestTimer timer = getQuestTimer(name, npc, player);
		if (timer != null)
			timer.cancel();
	}
	
	public void cancelQuestTimers(String name)
	{
		// Get quest timers for this timer type.
		List<QuestTimer> timers = _eventTimers.get(name.hashCode());
		
		// Timer list does not exists or is empty, return.
		if (timers == null || timers.isEmpty())
			return;
		
		// Cancel all quest timers.
		for (QuestTimer timer : timers)
		{
			if (timer != null)
				timer.cancel();
		}
	}
	
	// Note, keep it default. It is used withing QuestTimer, when it terminates.
	/**
	 * Removes QuestTimer from timer list, when it terminates.
	 * @param timer : QuestTimer, which is beeing terminated.
	 */
	void removeQuestTimer(QuestTimer timer)
	{
		// Timer does not exist, return.
		if (timer == null)
			return;
		
		// Get quest timers for this timer type.
		List<QuestTimer> timers = _eventTimers.get(timer.getName().hashCode());
		
		// Timer list does not exists or is empty, return.
		if (timers == null || timers.isEmpty())
			return;
		
		// Remove timer from the list.
		timers.remove(timer);
	}
	
	/**
	 * Add a temporary (quest) spawn on the location of a character.
	 * @param npcId the NPC template to spawn.
	 * @param cha the position where to spawn it.
	 * @param randomOffset
	 * @param despawnDelay
	 * @param isSummonSpawn if true, spawn with animation (if any exists).
	 * @return instance of the newly spawned npc with summon animation.
	 */
	public L2Npc addSpawn(int npcId, L2Character cha, boolean randomOffset, long despawnDelay, boolean isSummonSpawn)
	{
		return addSpawn(npcId, cha.getX(), cha.getY(), cha.getZ(), cha.getHeading(), randomOffset, despawnDelay, isSummonSpawn);
	}
	
	/**
	 * Add a temporary (quest) spawn on the Location object.
	 * @param npcId the NPC template to spawn.
	 * @param loc the position where to spawn it.
	 * @param randomOffset
	 * @param despawnDelay
	 * @param isSummonSpawn if true, spawn with animation (if any exists).
	 * @return instance of the newly spawned npc with summon animation.
	 */
	public L2Npc addSpawn(int npcId, Location loc, boolean randomOffset, long despawnDelay, boolean isSummonSpawn)
	{
		return addSpawn(npcId, loc.getX(), loc.getY(), loc.getZ(), loc.getHeading(), randomOffset, despawnDelay, isSummonSpawn);
	}
	
	/**
	 * Add a temporary (quest) spawn on the location of a character.
	 * @param npcId the NPC template to spawn.
	 * @param x
	 * @param y
	 * @param z
	 * @param heading
	 * @param randomOffset
	 * @param despawnDelay
	 * @param isSummonSpawn if true, spawn with animation (if any exists).
	 * @return instance of the newly spawned npc with summon animation.
	 */
	public L2Npc addSpawn(int npcId, int x, int y, int z, int heading, boolean randomOffset, long despawnDelay, boolean isSummonSpawn)
	{
		L2Npc result = null;
		try
		{
			L2NpcTemplate template = NpcTable.getInstance().getTemplate(npcId);
			if (template != null)
			{
				// Sometimes, even if the quest script specifies some xyz (for example npc.getX() etc) by the time the code
				// reaches here, xyz have become 0! Also, a questdev might have purposely set xy to 0,0...however,
				// the spawn code is coded such that if x=y=0, it looks into location for the spawn loc! This will NOT work
				// with quest spawns! For both of the above cases, we need a fail-safe spawn. For this, we use the
				// default spawn location, which is at the player's loc.
				if ((x == 0) && (y == 0))
				{
					_log.log(Level.SEVERE, "Failed to adjust bad locks for quest spawn!  Spawn aborted!");
					return null;
				}
				
				if (randomOffset)
				{
					if (Rnd.get(2) == 0)
						x += Rnd.get(50, 100);
					else
						x += Rnd.get(-100, -50);
					
					if (Rnd.get(2) == 0)
						y += Rnd.get(50, 100);
					else
						y += Rnd.get(-100, -50);
				}
				L2Spawn spawn = new L2Spawn(template);
				spawn.setHeading(heading);
				spawn.setLocx(x);
				spawn.setLocy(y);
				spawn.setLocz(z + 20);
				spawn.stopRespawn();
				result = spawn.doSpawn(isSummonSpawn);
				
				if (despawnDelay > 0)
					result.scheduleDespawn(despawnDelay);
				
				return result;
			}
		}
		catch (Exception e1)
		{
			_log.warning("Could not spawn Npc " + npcId);
		}
		
		return null;
	}
	
	/**
	 * @return default html page "You are either not on a quest that involves this NPC, or you don't meet this NPC's minimum quest requirements."
	 */
	public static String getNoQuestMsg()
	{
		return HTML_NONE_AVAILABLE;
	}
	
	/**
	 * @return default html page "This quest has already been completed."
	 */
	public static String getAlreadyCompletedMsg()
	{
		return HTML_ALREADY_COMPLETED;
	}
	
	/**
	 * Show a message to player.<BR>
	 * <BR>
	 * <U><I>Concept : </I></U><BR>
	 * 3 cases are managed according to the value of the parameter "res" :<BR>
	 * <LI><U>"res" ends with string ".html" :</U> an HTML is opened in order to be shown in a dialog box</LI> <LI><U>"res" starts with "<html>" :</U> the message hold in "res" is shown in a dialog box</LI> <LI><U>otherwise :</U> the message held in "res" is shown in chat box</LI>
	 * @param npc : which launches the dialog, null in case of random scripts
	 * @param player : the player.
	 * @param result : String pointing out the message to show at the player
	 * @return boolean
	 */
	public boolean showResult(L2Npc npc, L2PcInstance player, String result)
	{
		if (player == null || result == null || result.isEmpty())
			return false;
		
		if (result.endsWith(".htm") || result.endsWith(".html"))
		{
			NpcHtmlMessage npcReply = new NpcHtmlMessage(npc == null ? 0 : npc.getNpcId());
			if (isRealQuest())
				npcReply.setFile("./data/scripts/quests/" + getName() + "/" + result);
			else
				npcReply.setFile("./data/scripts/" + getDescr() + "/" + getName() + "/" + result);
			
			if (npc != null)
				npcReply.replace("%objectId%", String.valueOf(npc.getObjectId()));
			
			player.sendPacket(npcReply);
			player.sendPacket(ActionFailed.STATIC_PACKET);
		}
		else if (result.startsWith("<html>"))
		{
			NpcHtmlMessage npcReply = new NpcHtmlMessage(npc == null ? 0 : npc.getNpcId());
			npcReply.setHtml(result);
			
			if (npc != null)
				npcReply.replace("%objectId%", String.valueOf(npc.getObjectId()));
			
			player.sendPacket(npcReply);
			player.sendPacket(ActionFailed.STATIC_PACKET);
		}
		else
			player.sendMessage(result);
		
		return true;
	}
	
	/**
	 * Show message error to player who has an access level greater than 0
	 * @param player : L2PcInstance
	 * @param e : Throwable
	 * @return boolean
	 */
	public boolean showError(L2PcInstance player, Throwable e)
	{
		_log.log(Level.WARNING, getScriptFile().getAbsolutePath(), e);
		
		if (e.getMessage() == null)
			e.printStackTrace();
		
		if (player != null && player.isGM())
		{
			NpcHtmlMessage npcReply = new NpcHtmlMessage(0);
			npcReply.setHtml("<html><body><title>Script error</title>" + e.getMessage() + "</body></html>");
			player.sendPacket(npcReply);
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return true;
		}
		return false;
	}
	
	/**
	 * Returns String representation of given quest html.
	 * @param fileName : the filename to send.
	 * @return String : message sent to client.
	 */
	public String getHtmlText(String fileName)
	{
		if (isRealQuest())
			return HtmCache.getInstance().getHtmForce("./data/scripts/quests/" + getName() + "/" + fileName);
		
		return HtmCache.getInstance().getHtmForce("./data/scripts/" + getDescr() + "/" + getName() + "/" + fileName);
	}
	
	/**
	 * Add this quest to the list of quests that the passed mob will respond to for the specified Event type.<BR>
	 * <BR>
	 * @param npcId : id of the NPC to register
	 * @param eventType : type of event being registered
	 * @return L2NpcTemplate : Npc Template corresponding to the npcId, or null if the id is invalid
	 */
	public L2NpcTemplate addEventId(int npcId, QuestEventType eventType)
	{
		try
		{
			L2NpcTemplate t = NpcTable.getInstance().getTemplate(npcId);
			if (t != null)
			{
				t.addQuestEvent(eventType, this);
			}
			return t;
		}
		catch (Exception e)
		{
			_log.log(Level.WARNING, "Exception on addEventId(): " + e.getMessage(), e);
			return null;
		}
	}
	
	/**
	 * Add the quest to the NPC's startQuest
	 * @param npcIds A serie of ids.
	 * @return L2NpcTemplate : Start NPC
	 */
	public L2NpcTemplate[] addStartNpc(int... npcIds)
	{
		L2NpcTemplate[] value = new L2NpcTemplate[npcIds.length];
		int i = 0;
		for (int npcId : npcIds)
			value[i++] = addEventId(npcId, QuestEventType.QUEST_START);
		
		return value;
	}
	
	public L2NpcTemplate addStartNpc(int npcId)
	{
		return addEventId(npcId, QuestEventType.QUEST_START);
	}
	
	private boolean showResult(L2Character object, String res)
	{
		if(res == null)
		{
			return true;
		}

		if(object instanceof L2PcInstance)
		{
			L2PcInstance player = (L2PcInstance) object;

			if(res.endsWith(".htm"))
			{
				showHtmlFile1(player, res);
			}
			else if(res.startsWith("<html>"))
			{
				NpcHtmlMessage npcReply = new NpcHtmlMessage(5);
				npcReply.setHtml(res);
				npcReply.replace("%playername%", player.getName());
				player.sendPacket(npcReply);
				npcReply = null;
			}
			else
			{
				player.sendPacket(new SystemMessage(SystemMessageId.S1_S2).addString(res));
			}

			player = null;
		}

		return false;
	}
	public String showHtmlFile(L2PcInstance player, String fileName)
	{
		String questId = getName();

		String directory = getDescr().toLowerCase();
		String content = HtmCache.getInstance().getHtm("./data/scripts/" + directory + "/" + questId + "/" + fileName);

		if(content == null)
		{
			content = HtmCache.getInstance().getHtmForce("./data/scripts/quests/" + questId + "/" + fileName);
		}

		if(player != null && player.getTarget() != null)
		{
			content = content.replaceAll("%objectId%", String.valueOf(player.getTarget().getObjectId()));
		}

		if(content != null)
		{
			NpcHtmlMessage npcReply = new NpcHtmlMessage(5);
			npcReply.setHtml(content);
			npcReply.replace("%playername%", player.getName());
			player.sendPacket(npcReply);
			npcReply = null;
		}

		if ((player.isGM()) && (player.getAccessLevel().getLevel() == Config.GM_ACCESS_LEVEL))
			player.sendChatMessage(0, 0, "HTML", "scripts/" + directory + "/" + questId + "/" + fileName);
		return content;
	}

	/**
	 * Add this quest to the list of quests that the passed mob will respond to for Attack Events.<BR>
	 * <BR>
	 * @param npcIds A serie of ids.
	 * @return int : attackId
	 */
	public L2NpcTemplate[] addAttackId(int... npcIds)
	{
		L2NpcTemplate[] value = new L2NpcTemplate[npcIds.length];
		int i = 0;
		for (int npcId : npcIds)
			value[i++] = addEventId(npcId, QuestEventType.ON_ATTACK);
		return value;
	}

	public L2NpcTemplate addAttackId(int attackId)
	{
		return addEventId(attackId, QuestEventType.ON_ATTACK);
	}
	
	/**
	 * Quest event notifycator for player's or player's pet attack.
	 * @param npc Attacked npc instace.
	 * @param attacker Attacker or pet owner.
	 * @param damage Given damage.
	 * @param isPet Player summon attacked?
	 * @return boolean
	 */
	public final boolean notifyAttack(L2Npc npc, L2PcInstance attacker, int damage, boolean isPet)
	{
		String res = null;
		try
		{
			res = onAttack(npc, attacker, damage, isPet);
		}
		catch (Exception e)
		{
			return showError(attacker, e);
		}
		return showResult(npc, attacker, res);
	}
	
	public String onAttack(L2Npc npc, L2PcInstance attacker, int damage, boolean isPet)
	{
		return null;
	}
	
	/**
	 * Add this quest to the list of quests that the passed mob will respond to for AttackAct Events.<BR>
	 * <BR>
	 * @param npcIds A serie of ids.
	 * @return int : attackId
	 */
	public L2NpcTemplate[] addAttackActId(int... npcIds)
	{
		L2NpcTemplate[] value = new L2NpcTemplate[npcIds.length];
		int i = 0;
		for (int npcId : npcIds)
			value[i++] = addEventId(npcId, QuestEventType.ON_ATTACK_ACT);
		return value;
	}
	
	public L2NpcTemplate addAttackActId(int attackId)
	{
		return addEventId(attackId, QuestEventType.ON_ATTACK_ACT);
	}
	
	/**
	 * Quest event notifycator for player being attacked by NPC.
	 * @param npc Npc providing attack.
	 * @param victim Attacked npc player.
	 * @return boolean
	 */
	public final boolean notifyAttackAct(L2Npc npc, L2PcInstance victim)
	{
		String res = null;
		try
		{
			res = onAttackAct(npc, victim);
		}
		catch (Exception e)
		{
			return showError(victim, e);
		}
		return showResult(npc, victim, res);
	}
	
	public String onAttackAct(L2Npc npc, L2PcInstance victim)
	{
		return null;
	}
	
	/**
	 * Add this quest to the list of quests that the passed npc will respond to for Character See Events.<BR>
	 * <BR>
	 * @param npcIds : A serie of ids.
	 * @return int : ID of the NPC
	 */
	public L2NpcTemplate[] addAggroRangeEnterId(int... npcIds)
	{
		L2NpcTemplate[] value = new L2NpcTemplate[npcIds.length];
		int i = 0;
		for (int npcId : npcIds)
			value[i++] = addEventId(npcId, QuestEventType.ON_AGGRO_RANGE_ENTER);
		return value;
	}
	
	public L2NpcTemplate addAggroRangeEnterId(int npcId)
	{
		return addEventId(npcId, QuestEventType.ON_AGGRO_RANGE_ENTER);
	}
	
	private class TmpOnAggroEnter implements Runnable
	{
		private final L2Npc _npc;
		private final L2PcInstance _pc;
		private final boolean _isPet;
		
		public TmpOnAggroEnter(L2Npc npc, L2PcInstance pc, boolean isPet)
		{
			_npc = npc;
			_pc = pc;
			_isPet = isPet;
		}
		
		@Override
		public void run()
		{
			String res = null;
			try
			{
				res = onAggroRangeEnter(_npc, _pc, _isPet);
			}
			catch (Exception e)
			{
				showError(_pc, e);
			}
			showResult(_npc, _pc, res);
			
		}
	}
	
	public final boolean notifyAggroRangeEnter(L2Npc npc, L2PcInstance player, boolean isPet)
	{
		ThreadPoolManager.getInstance().executeAi(new TmpOnAggroEnter(npc, player, isPet));
		return true;
	}
	
	public String onAggroRangeEnter(L2Npc npc, L2PcInstance player, boolean isPet)
	{
		return null;
	}
	
	public final boolean notifyAcquireSkill(L2Npc npc, L2PcInstance player, L2Skill skill)
	{
		String res = null;
		try
		{
			res = onAcquireSkill(npc, player, skill);
			if (res == "true")
				return true;
			else if (res == "false")
				return false;
		}
		catch (Exception e)
		{
			return showError(player, e);
		}
		return showResult(npc, player, res);
	}
	
	public String onAcquireSkill(L2Npc npc, L2PcInstance player, L2Skill skill)
	{
		return null;
	}
	
	public final boolean notifyAcquireSkillInfo(L2Npc npc, L2PcInstance player, L2Skill skill)
	{
		String res = null;
		try
		{
			res = onAcquireSkillInfo(npc, player, skill);
		}
		catch (Exception e)
		{
			return showError(player, e);
		}
		return showResult(npc, player, res);
	}
	
	public String onAcquireSkillInfo(L2Npc npc, L2PcInstance player, L2Skill skill)
	{
		return null;
	}
	
	public final boolean notifyAcquireSkillList(L2Npc npc, L2PcInstance player)
	{
		String res = null;
		try
		{
			res = onAcquireSkillList(npc, player);
		}
		catch (Exception e)
		{
			return showError(player, e);
		}
		return showResult(npc, player, res);
	}
	
	public String onAcquireSkillList(L2Npc npc, L2PcInstance player)
	{
		return null;
	}
	
	public final boolean notifyDeath(L2Character killer, L2Character victim, L2PcInstance player)
	{
		String res = null;
		try
		{
			res = onDeath(killer, victim, player);
		}
		catch (Exception e)
		{
			return showError(player, e);
		}
		if (killer instanceof L2Npc)
			return showResult((L2Npc) killer, player, res);
		
		return showResult(null, player, res);
	}
	
	public String onDeath(L2Character killer, L2Character victim, L2PcInstance player)
	{
		if (killer instanceof L2Npc)
			return onAdvEvent("", (L2Npc) killer, player);
		
		return onAdvEvent("", null, player);
	}
	
	public final boolean notifyEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String res = null;
		try
		{
			res = onAdvEvent(event, npc, player);
		}
		catch (Exception e)
		{
			return showError(player, e);
		}
		return showResult(npc, player, res);
	}
	
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		// if not overridden by a subclass, then default to the returned value of the simpler (and older) onEvent override
		// if the player has a state, use it as parameter in the next call, else return null
		if (player != null)
		{
			QuestState qs = player.getQuestState(getName());
			if (qs != null)
				return onEvent(event, qs);
		}
		return null;
	}
	
	public String onEvent(String event, QuestState qs)
	{
		return null;
	}
	
	public final boolean notifyEnterWorld(L2PcInstance player)
	{
		String res = null;
		try
		{
			res = onEnterWorld(player);
		}
		catch (Exception e)
		{
			return showError(player, e);
		}
		return showResult(null, player, res);
	}
	
	public String onEnterWorld(L2PcInstance player)
	{
		return null;
	}
	
	/**
	 * Add this quest to the list of quests that triggers, when player enters specified zones.<BR>
	 * <BR>
	 * @param zoneIds : A serie of zone ids.
	 * @return int[] : ID of the L2ZoneType
	 */
	public L2ZoneType[] addEnterZoneId(int... zoneIds)
	{
		L2ZoneType[] value = new L2ZoneType[zoneIds.length];
		int i = 0;
		for (int zoneId : zoneIds)
		{
			try
			{
				L2ZoneType zone = ZoneManager.getInstance().getZoneById(zoneId);
				if (zone != null)
					zone.addQuestEvent(QuestEventType.ON_ENTER_ZONE, this);
				
				value[i++] = zone;
			}
			catch (Exception e)
			{
				_log.log(Level.WARNING, "Exception on addEnterZoneId(): " + e.getMessage(), e);
				continue;
			}
		}
		
		return value;
	}
	
	public L2ZoneType addEnterZoneId(int zoneId)
	{
		try
		{
			L2ZoneType zone = ZoneManager.getInstance().getZoneById(zoneId);
			if (zone != null)
				zone.addQuestEvent(QuestEventType.ON_ENTER_ZONE, this);
			
			return zone;
		}
		catch (Exception e)
		{
			_log.log(Level.WARNING, "Exception on addEnterZoneId(): " + e.getMessage(), e);
			return null;
		}
	}
	
	public final boolean notifyEnterZone(L2Character character, L2ZoneType zone)
	{
		L2PcInstance player = character.getActingPlayer();
		String res = null;
		try
		{
			res = onEnterZone(character, zone);
		}
		catch (Exception e)
		{
			if (player != null)
				return showError(player, e);
		}
		if (player != null)
			return showResult(null, player, res);
		return true;
	}
	
	public String onEnterZone(L2Character character, L2ZoneType zone)
	{
		return null;
	}
	
	/**
	 * Add this quest to the list of quests that triggers, when player leaves specified zones.<BR>
	 * <BR>
	 * @param zoneIds : A serie of zone ids.
	 * @return int[] : ID of the L2ZoneType
	 */
	public L2ZoneType[] addExitZoneId(int... zoneIds)
	{
		L2ZoneType[] value = new L2ZoneType[zoneIds.length];
		int i = 0;
		for (int zoneId : zoneIds)
		{
			try
			{
				L2ZoneType zone = ZoneManager.getInstance().getZoneById(zoneId);
				if (zone != null)
					zone.addQuestEvent(QuestEventType.ON_EXIT_ZONE, this);
				
				value[i++] = zone;
			}
			catch (Exception e)
			{
				_log.log(Level.WARNING, "Exception on addEnterZoneId(): " + e.getMessage(), e);
				continue;
			}
		}
		
		return value;
	}
	
	public L2ZoneType addExitZoneId(int zoneId)
	{
		try
		{
			L2ZoneType zone = ZoneManager.getInstance().getZoneById(zoneId);
			if (zone != null)
				zone.addQuestEvent(QuestEventType.ON_EXIT_ZONE, this);
			
			return zone;
		}
		catch (Exception e)
		{
			_log.log(Level.WARNING, "Exception on addExitZoneId(): " + e.getMessage(), e);
			return null;
		}
	}
	
	public final boolean notifyExitZone(L2Character character, L2ZoneType zone)
	{
		L2PcInstance player = character.getActingPlayer();
		String res = null;
		try
		{
			res = onExitZone(character, zone);
		}
		catch (Exception e)
		{
			if (player != null)
				return showError(player, e);
		}
		if (player != null)
			return showResult(null, player, res);
		return true;
	}
	
	public String onExitZone(L2Character character, L2ZoneType zone)
	{
		return null;
	}
	
	/**
	 * Add this quest to the list of quests that the passed npc will respond to for Faction Call Events.<BR>
	 * <BR>
	 * @param npcIds : A serie of ids.
	 * @return int : ID of the NPC
	 */
	public L2NpcTemplate[] addFactionCallId(int... npcIds)
	{
		L2NpcTemplate[] value = new L2NpcTemplate[npcIds.length];
		int i = 0;
		for (int npcId : npcIds)
			value[i++] = addEventId(npcId, QuestEventType.ON_FACTION_CALL);
		return value;
	}
	
	public L2NpcTemplate addFactionCallId(int npcId)
	{
		return addEventId(npcId, QuestEventType.ON_FACTION_CALL);
	}
	
	public final boolean notifyFactionCall(L2Npc npc, L2Npc caller, L2PcInstance attacker, boolean isPet)
	{
		String res = null;
		try
		{
			res = onFactionCall(npc, caller, attacker, isPet);
		}
		catch (Exception e)
		{
			return showError(attacker, e);
		}
		return showResult(npc, attacker, res);
	}
	
	public String onFactionCall(L2Npc npc, L2Npc caller, L2PcInstance attacker, boolean isPet)
	{
		return null;
	}
	
	/**
	 * Add the quest to the NPC's first-talk (default action dialog)
	 * @param npcIds A serie of ids.
	 * @return L2NpcTemplate : Start NPC
	 */
	public L2NpcTemplate[] addFirstTalkId(int... npcIds)
	{
		L2NpcTemplate[] value = new L2NpcTemplate[npcIds.length];
		int i = 0;
		for (int npcId : npcIds)
			value[i++] = addEventId(npcId, QuestEventType.ON_FIRST_TALK);
		return value;
	}
	
	public L2NpcTemplate addFirstTalkId(int npcId)
	{
		return addEventId(npcId, QuestEventType.ON_FIRST_TALK);
	}
	
	public final boolean notifyFirstTalk(L2Npc npc, L2PcInstance player)
	{
		String res = null;
		try
		{
			res = onFirstTalk(npc, player);
		}
		catch (Exception e)
		{
			return showError(player, e);
		}
		
		// if the quest returns text to display, display it.
		if (res != null && res.length() > 0)
			return showResult(npc, player, res);
		
		player.sendPacket(ActionFailed.STATIC_PACKET);
		return true;
	}
	
	public String onFirstTalk(L2Npc npc, L2PcInstance player)
	{
		return null;
	}
	
	/**
	 * Add this quest to the list of quests that the passed mob will respond to for Kill Events.<BR>
	 * <BR>
	 * @param killIds A serie of ids.
	 * @return int : killId
	 */
	public L2NpcTemplate[] addKillId(int... killIds)
	{
		L2NpcTemplate[] value = new L2NpcTemplate[killIds.length];
		int i = 0;
		for (int killId : killIds)
			value[i++] = addEventId(killId, QuestEventType.ON_KILL);
		return value;
	}
	
	public L2NpcTemplate addKillId(int killId)
	{
		return addEventId(killId, QuestEventType.ON_KILL);
	}
	
	public final boolean notifyKill(L2Npc npc, L2PcInstance killer, boolean isPet)
	{
		String res = null;
		try
		{
			res = onKill(npc, killer, isPet);
		}
		catch (Exception e)
		{
			return showError(killer, e);
		}
		return showResult(npc, killer, res);
	}
	
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isPet)
	{
		return null;
	}
	
	/**
	 * Add this quest to the list of quests that the passed npc will respond to for Spawn Events.<BR>
	 * <BR>
	 * @param npcIds : A serie of ids.
	 * @return int : ID of the NPC
	 */
	public L2NpcTemplate[] addSpawnId(int... npcIds)
	{
		L2NpcTemplate[] value = new L2NpcTemplate[npcIds.length];
		int i = 0;
		for (int npcId : npcIds)
			value[i++] = addEventId(npcId, QuestEventType.ON_SPAWN);
		return value;
	}
	
	public L2NpcTemplate addSpawnId(int npcId)
	{
		return addEventId(npcId, QuestEventType.ON_SPAWN);
	}
	
	public final boolean notifySpawn(L2Npc npc)
	{
		try
		{
			onSpawn(npc);
		}
		catch (Exception e)
		{
			_log.log(Level.WARNING, "Exception on onSpawn() in notifySpawn(): " + e.getMessage(), e);
			return true;
		}
		return false;
	}
	
	public String onSpawn(L2Npc npc)
	{
		return null;
	}
	
	/**
	 * Add this quest to the list of quests that the passed npc will respond to for Skill-See Events.<BR>
	 * <BR>
	 * @param npcIds : A serie of ids.
	 * @return int : ID of the NPC
	 */
	public L2NpcTemplate[] addSkillSeeId(int... npcIds)
	{
		L2NpcTemplate[] value = new L2NpcTemplate[npcIds.length];
		int i = 0;
		for (int npcId : npcIds)
			value[i++] = addEventId(npcId, QuestEventType.ON_SKILL_SEE);
		return value;
	}
	
	public L2NpcTemplate addSkillSeeId(int npcId)
	{
		return addEventId(npcId, QuestEventType.ON_SKILL_SEE);
	}
	
	public class TmpOnSkillSee implements Runnable
	{
		private final L2Npc _npc;
		private final L2PcInstance _caster;
		private final L2Skill _skill;
		private final L2Object[] _targets;
		private final boolean _isPet;
		
		public TmpOnSkillSee(L2Npc npc, L2PcInstance caster, L2Skill skill, L2Object[] targets, boolean isPet)
		{
			_npc = npc;
			_caster = caster;
			_skill = skill;
			_targets = targets;
			_isPet = isPet;
		}
		
		@Override
		public void run()
		{
			String res = null;
			try
			{
				res = onSkillSee(_npc, _caster, _skill, _targets, _isPet);
			}
			catch (Exception e)
			{
				showError(_caster, e);
			}
			showResult(_npc, _caster, res);
			
		}
	}
	
	public final boolean notifySkillSee(L2Npc npc, L2PcInstance caster, L2Skill skill, L2Object[] targets, boolean isPet)
	{
		ThreadPoolManager.getInstance().executeAi(new TmpOnSkillSee(npc, caster, skill, targets, isPet));
		return true;
	}
	
	public String onSkillSee(L2Npc npc, L2PcInstance caster, L2Skill skill, L2Object[] targets, boolean isPet)
	{
		return null;
	}
	
	/**
	 * Add this quest to the list of quests that the passed npc will respond to any skill being used by other npcs or players.<BR>
	 * <BR>
	 * @param npcIds : A serie of ids.
	 * @return int : ID of the NPC
	 */
	public L2NpcTemplate[] addSpellFinishedId(int... npcIds)
	{
		L2NpcTemplate[] value = new L2NpcTemplate[npcIds.length];
		int i = 0;
		for (int npcId : npcIds)
			value[i++] = addEventId(npcId, QuestEventType.ON_SPELL_FINISHED);
		return value;
	}
	
	public L2NpcTemplate addSpellFinishedId(int npcId)
	{
		return addEventId(npcId, QuestEventType.ON_SPELL_FINISHED);
	}
	
	public final boolean notifySpellFinished(L2Npc npc, L2PcInstance player, L2Skill skill)
	{
		String res = null;
		try
		{
			res = onSpellFinished(npc, player, skill);
		}
		catch (Exception e)
		{
			return showError(player, e);
		}
		return showResult(npc, player, res);
	}
	
	public String onSpellFinished(L2Npc npc, L2PcInstance player, L2Skill skill)
	{
		return null;
	}
	
	/**
	 * Add this quest to the list of quests that the passed npc will respond to for Talk Events.<BR>
	 * <BR>
	 * @param talkIds : A serie of ids.
	 * @return int : ID of the NPC
	 */
	public L2NpcTemplate[] addTalkId(int... talkIds)
	{
		L2NpcTemplate[] value = new L2NpcTemplate[talkIds.length];
		int i = 0;
		for (int talkId : talkIds)
			value[i++] = addEventId(talkId, QuestEventType.ON_TALK);
		return value;
	}
	
	public L2NpcTemplate addTalkId(int talkId)
	{
		return addEventId(talkId, QuestEventType.ON_TALK);
	}
	
	public final boolean notifyTalk(L2Npc npc, L2PcInstance player)
	{
		String res = null;
		try
		{
			res = onTalk(npc, player);
		}
		catch (Exception e)
		{
			return showError(player, e);
		}
		player.setLastQuestNpcObject(npc.getObjectId());
		return showResult(npc, player, res);
	}
	
	public String onTalk(L2Npc npc, L2PcInstance talker)
	{
		return null;
	}
	
	@Override
	public ScriptManager<?> getScriptManager()
	{
		return QuestManager.getInstance();
	}
	
	@Override
	public void setActive(boolean status)
	{
	}
	
	@Override
	public boolean reload()
	{
		unload1();
		return super.reload1();
	}
	
	@Override
	public boolean unload()
	{
		return unload(true);
	}
	
	public boolean unload(boolean removeFromList)
	{
		saveGlobalData();
		
		for (List<QuestTimer> timers : _eventTimers.values())
			for (QuestTimer timer : timers)
				timer.cancel();
		
		_eventTimers.clear();
		
		if (removeFromList)
			return QuestManager.getInstance().removeQuest(this);
		
		return true;
	}
	
	/**
	 * This function is, by default, called at shutdown, for all quests, by the QuestManager.<br>
	 * Children of this class can implement this function in order to convert their structures into <var, value> tuples and make calls to save them to the database, if needed.<br>
	 * <br>
	 * By default, nothing is saved.
	 */
	public void saveGlobalData()
	{
		
	}

	/**
	 * @param itemId
	 */
	public void registerItem(int itemId)
	{
		if(_questItemIds == null)
		{
			_questItemIds = new FastList<Integer>();
		}

		_questItemIds(itemId);
	}

	/**
	 * @param itemId
	 */
	private void _questItemIds(int itemId)
	{
		
	}

	/**
	 * @param questState
	 */
	public static void updateQuestInDb(QuestState questState)
	{
		// TODO Auto-generated method stub
		
	}
}