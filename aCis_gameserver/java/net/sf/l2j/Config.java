package net.sf.l2j;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import net.sf.l2j.commons.config.ExProperties;
import net.sf.l2j.commons.lang.StringUtil;
import net.sf.l2j.commons.math.MathUtil;

import net.sf.l2j.gameserver.model.holder.IntIntHolder;

/**
 * This class contains global server configuration.<br>
 * It has static final fields initialized from configuration files.<br>
 * @author mkizub
 */
public final class Config
{
	private static final Logger _log = Logger.getLogger(Config.class.getName());
	
	public static final String CUSTOM_FILE = "./config/custom.properties";
	public static final String CLANS_FILE = "./config/clans.properties";
	public static final String CUSTOM_EVENTS_FILE = "./config/custom_events.properties";
	public static final String EVENTS_FILE = "./config/events.properties";
	public static final String GEOENGINE_FILE = "./config/geoengine.properties";
	public static final String HEXID_FILE = "./config/hexid.txt";
	public static final String LOGIN_CONFIGURATION_FILE = "./config/loginserver.properties";
	public static final String NPCS_FILE = "./config/npcs.properties";
	public static final String PLAYERS_FILE = "./config/players.properties";
	public static final String SERVER_FILE = "./config/server.properties";
	public static final String SIEGE_FILE = "./config/siege.properties";
	
	// --------------------------------------------------
	// Custom settings
	// --------------------------------------------------
	/** Banking System */
	public static boolean BANKING_SYSTEM_ENABLED;
	public static int BANKING_SYSTEM_GOLDBARS;
	public static int BANKING_SYSTEM_ADENA;
	/** Offline Shop && Craft */
	public static boolean OFFLINE_TRADE_ENABLE;
	public static boolean OFFLINE_CRAFT_ENABLE;
	public static boolean OFFLINE_MODE_IN_PEACE_ZONE;
	public static boolean OFFLINE_MODE_NO_DAMAGE;
	public static boolean RESTORE_OFFLINERS;
	public static int OFFLINE_MAX_DAYS;
	public static boolean OFFLINE_DISCONNECT_FINISHED;
	public static boolean OFFLINE_SET_NAME_COLOR;
	public static int OFFLINE_NAME_COLOR;
	/** Raidboss Info */
	public static int RAID_BOSS_INFO_PAGE_LIMIT;
	public static int RAID_BOSS_DROP_PAGE_LIMIT;
	public static String RAID_BOSS_DATE_FORMAT;
	public static String RAID_BOSS_IDS;
	public static List<Integer> LIST_RAID_BOSS_IDS;
	/** Character Killing Monument settings */
	public static boolean CKM_ENABLED;
	public static long CKM_CYCLE_LENGTH;
	public static String CKM_PVP_NPC_TITLE;
	public static int CKM_PVP_NPC_TITLE_COLOR;
	public static int CKM_PVP_NPC_NAME_COLOR;
	public static String CKM_PK_NPC_TITLE;
	public static int CKM_PK_NPC_TITLE_COLOR;
	public static int CKM_PK_NPC_NAME_COLOR;
	/** Aio System */
	public static String AIO_TITLE;
	public static int AIO_TITLE_COLOR;
	public static String AIO_RESTRICTED_CLASSES;
	public static List<Integer> LIST_AIO_RESTRICTED_CLASSES;
	public static String AIO_ITEMS;
	public static Map<Integer, Integer> LIST_AIO_ITEMS;
	public static String AIO_SKILLS;
	public static Map<Integer, Integer> LIST_AIO_SKILLS;
	/** Vip System */
	public static boolean ENABLE_VIP_ITEM;
	public static int VIP_ITEM_ID;
	public static int VIP_DAYS;
	public static int VIP_XP_SP_RATES;
	public static int VIP_ADENA_RATES;
	public static int VIP_SPOIL_RATES;
	public static int VIP_RATE_DROP_ITEMS_BY_RAID;
	public static int VIP_DROP_RATES;
	/** Clan Manager */
	public static int CLAN_MANAGER_ITEM_ID;
	public static int CLAN_MANAGER_LEVEL_UP_COUNT;
	public static int CLAN_MANAGER_REPUTATION_COUNT;
	public static int CLAN_MANAGER_CLAN_SKILLS_COUNT;
	public static int CLAN_MANAGER_CLAN_LEVEL_REWARD;
	public static int CLAN_MANAGER_CLAN_REPUTATION_REWARD;
	/** PvP Color System */
	public static boolean ENABLE_PVP_COLOR;
	
	/** Trade/Shout voice */
	public static enum ShoutRestrictionType
	{
		PVP,
		LEVEL,
		NONE
	}
	
	public static enum TradeRestrictionType
	{
		PVP,
		LEVEL,
		NONE
	}
	
	public static ShoutRestrictionType SHOUT_RESTRICTION_TYPE;
	public static TradeRestrictionType TRADE_RESTRICTION_TYPE;
	public static int SHOUT_RESTRICTION_VALUE;
	public static int TRADE_RESTRICTION_VALUE;
	/** Raid Boss Defeated Message */
	public static boolean ENABLE_RAID_BOSS_DEFEATED_MSG;
	public static String RAID_BOSS_DEFEATED_BY_CLAN_MEMBER_MSG;
	public static String RAID_BOSS_DEFEATED_BY_PLAYER_MSG;
	/** Global Drop Monsters */
	public static Map<Integer, List<Integer>> GLOBAL_DROP_ITEMS = new HashMap<>();
	public static Map<Integer, List<Integer>> GLOBAL_DROP_ITEMS_CHAMPION = new HashMap<>();
	public static boolean ALLOW_GLOBAL_DROP_RANDOM;
	public static boolean ALLOW_GLOBAL_DROP;
	/** Npc Enchant Items */
	public static boolean npcEnchantItemsEnabled;
	public static int weaponEnchantLevel;
	public static int armorEnchantLevel;
	public static int IngredientID;
	public static int InAmountWeapon;
	public static int InAmountArmor;
	public static int augmentItemChance;
	public static String nonEnchantableItems;
	public static List<Integer> nonEnchantableItemList;
	public static boolean modifyItemEnchant;
	public static Map<Integer, Integer> modifyItemEnchantList;
	/** Open Url */
	public static boolean OPEN_URL_ENABLE;
	public static String OPEN_URL_SITE;
	/** Grandboss Info */
	public static String GRAND_BOSS;
	public static List<Integer> GRAND_BOSS_LIST;
	/** Clan Full */
	public static boolean ENABLE_CLAN_SYSTEM;
	public static byte CLAN_LEVEL;
	public static int REPUTATION_QUANTITY;
	public static int CLAN_ITEM_ID;
	public static long CLAN_ITEM_COUNT;
	/** Buff Command */
	public static String LIST_BUFF_COMMAND;
	public static int[] BUFF_COMMAND_FIGHT_IDBUFFS;
	public static int[] BUFF_COMMAND_MAGE_IDBUFFS;
	/** Armor e Weapon Protection */
	public static boolean ALLOW_LIGHT_USE_HEAVY;
	public static String NOTALLOWCLASS;
	public static List<Integer> NOTALLOWEDUSEHEAVY;
	public static boolean ALLOW_HEAVY_USE_LIGHT;
	public static String NOTALLOWCLASSE;
	public static List<Integer> NOTALLOWEDUSELIGHT;
	public static boolean ALT_DISABLE_BOW_CLASSES;
	public static String DISABLE_BOW_CLASSES_STRING;
	public static List<Integer> DISABLE_BOW_CLASSES;
	public static boolean ALT_DISABLE_DAGGER_CLASSES;
	public static String DISABLE_DAGGER_CLASSES_STRING;
	public static List<Integer> DISABLE_DAGGER_CLASSES;
	public static boolean ALT_DISABLE_SWORD_CLASSES;
	public static String DISABLE_SWORD_CLASSES_STRING;
	public static List<Integer> DISABLE_SWORD_CLASSES;
	public static boolean ALT_DISABLE_BLUNT_CLASSES;
	public static String DISABLE_BLUNT_CLASSES_STRING;
	public static List<Integer> DISABLE_BLUNT_CLASSES;
	public static boolean ALT_DISABLE_DUAL_CLASSES;
	public static String DISABLE_DUAL_CLASSES_STRING;
	public static List<Integer> DISABLE_DUAL_CLASSES;
	public static boolean ALT_DISABLE_POLE_CLASSES;
	public static String DISABLE_POLE_CLASSES_STRING;
	public static List<Integer> DISABLE_POLE_CLASSES;
	public static boolean ALT_DISABLE_BIGSWORD_CLASSES;
	public static String DISABLE_BIGSWORD_CLASSES_STRING;
	public static List<Integer> DISABLE_BIGSWORD_CLASSES;
	/** VoteManager Settings */
	public static boolean VOTE_MANAGER_ENABLED;
	public static String VOTES_SITE_TOPZONE_URL;
	public static String VOTES_SITE_HOPZONE_URL;
	public static String VOTES_SITE_L2NETWORK_URL;
	public static int VOTE_REWARD_ID;
	public static int VOTE_REWARD_ID_COUNT;
	public static int TIME_TO_VOTE;
	
	// --------------------------------------------------
	// Clans settings
	// --------------------------------------------------
	
	/** Clans */
	public static int ALT_CLAN_JOIN_DAYS;
	public static int ALT_CLAN_CREATE_DAYS;
	public static int ALT_CLAN_DISSOLVE_DAYS;
	public static int ALT_ALLY_JOIN_DAYS_WHEN_LEAVED;
	public static int ALT_ALLY_JOIN_DAYS_WHEN_DISMISSED;
	public static int ALT_ACCEPT_CLAN_DAYS_WHEN_DISMISSED;
	public static int ALT_CREATE_ALLY_DAYS_WHEN_DISSOLVED;
	public static int ALT_MAX_NUM_OF_CLANS_IN_ALLY;
	public static int ALT_CLAN_MEMBERS_FOR_WAR;
	public static int ALT_CLAN_WAR_PENALTY_WHEN_ENDED;
	public static boolean ALT_MEMBERS_CAN_WITHDRAW_FROM_CLANWH;
	
	/** Manor */
	public static int ALT_MANOR_REFRESH_TIME;
	public static int ALT_MANOR_REFRESH_MIN;
	public static int ALT_MANOR_APPROVE_TIME;
	public static int ALT_MANOR_APPROVE_MIN;
	public static int ALT_MANOR_MAINTENANCE_MIN;
	public static int ALT_MANOR_SAVE_PERIOD_RATE;
	
	/** Clan Hall function */
	public static long CH_TELE_FEE_RATIO;
	public static int CH_TELE1_FEE;
	public static int CH_TELE2_FEE;
	public static long CH_ITEM_FEE_RATIO;
	public static int CH_ITEM1_FEE;
	public static int CH_ITEM2_FEE;
	public static int CH_ITEM3_FEE;
	public static long CH_MPREG_FEE_RATIO;
	public static int CH_MPREG1_FEE;
	public static int CH_MPREG2_FEE;
	public static int CH_MPREG3_FEE;
	public static int CH_MPREG4_FEE;
	public static int CH_MPREG5_FEE;
	public static long CH_HPREG_FEE_RATIO;
	public static int CH_HPREG1_FEE;
	public static int CH_HPREG2_FEE;
	public static int CH_HPREG3_FEE;
	public static int CH_HPREG4_FEE;
	public static int CH_HPREG5_FEE;
	public static int CH_HPREG6_FEE;
	public static int CH_HPREG7_FEE;
	public static int CH_HPREG8_FEE;
	public static int CH_HPREG9_FEE;
	public static int CH_HPREG10_FEE;
	public static int CH_HPREG11_FEE;
	public static int CH_HPREG12_FEE;
	public static int CH_HPREG13_FEE;
	public static long CH_EXPREG_FEE_RATIO;
	public static int CH_EXPREG1_FEE;
	public static int CH_EXPREG2_FEE;
	public static int CH_EXPREG3_FEE;
	public static int CH_EXPREG4_FEE;
	public static int CH_EXPREG5_FEE;
	public static int CH_EXPREG6_FEE;
	public static int CH_EXPREG7_FEE;
	public static long CH_SUPPORT_FEE_RATIO;
	public static int CH_SUPPORT1_FEE;
	public static int CH_SUPPORT2_FEE;
	public static int CH_SUPPORT3_FEE;
	public static int CH_SUPPORT4_FEE;
	public static int CH_SUPPORT5_FEE;
	public static int CH_SUPPORT6_FEE;
	public static int CH_SUPPORT7_FEE;
	public static int CH_SUPPORT8_FEE;
	public static long CH_CURTAIN_FEE_RATIO;
	public static int CH_CURTAIN1_FEE;
	public static int CH_CURTAIN2_FEE;
	public static long CH_FRONT_FEE_RATIO;
	public static int CH_FRONT1_FEE;
	public static int CH_FRONT2_FEE;
	/** Partisan Hideout */
	public static int PARTISAN_DAY;
	public static int PARTISAN_HOUR;
	public static int PARTISAN_MINUTES;
	// --------------------------------------------------
	// Events settings
	// --------------------------------------------------
	
	/** Olympiad */
	public static int ALT_OLY_START_TIME;
	public static int ALT_OLY_MIN;
	public static long ALT_OLY_CPERIOD;
	public static long ALT_OLY_BATTLE;
	public static long ALT_OLY_WPERIOD;
	public static long ALT_OLY_VPERIOD;
	public static int ALT_OLY_WAIT_TIME;
	public static int ALT_OLY_WAIT_BATTLE;
	public static int ALT_OLY_WAIT_END;
	public static int ALT_OLY_START_POINTS;
	public static int ALT_OLY_WEEKLY_POINTS;
	public static int ALT_OLY_MIN_MATCHES;
	public static int ALT_OLY_CLASSED;
	public static int ALT_OLY_NONCLASSED;
	public static int[][] ALT_OLY_CLASSED_REWARD;
	public static int[][] ALT_OLY_NONCLASSED_REWARD;
	public static int ALT_OLY_GP_PER_POINT;
	public static int ALT_OLY_HERO_POINTS;
	public static int ALT_OLY_RANK1_POINTS;
	public static int ALT_OLY_RANK2_POINTS;
	public static int ALT_OLY_RANK3_POINTS;
	public static int ALT_OLY_RANK4_POINTS;
	public static int ALT_OLY_RANK5_POINTS;
	public static int ALT_OLY_MAX_POINTS;
	public static int ALT_OLY_DIVIDER_CLASSED;
	public static int ALT_OLY_DIVIDER_NON_CLASSED;
	public static boolean ALT_OLY_ANNOUNCE_GAMES;
	public static boolean ALT_OLY_DUAL_BOX;
	public static boolean ALT_OLY_SKILL_PROTECT;
	public static List<Integer> ALT_OLY_SKILL_LIST = new ArrayList<>();
	public static boolean ALT_OLY_END_ANNOUNCE;
    public static boolean ALT_OLY_BACK_REUSE_SKILLS;
    public static int ALT_OLY_ENCHANT_LIMIT;
    
	/** SevenSigns Festival */
	public static boolean ALT_GAME_CASTLE_DAWN;
	public static boolean ALT_GAME_CASTLE_DUSK;
	public static int ALT_FESTIVAL_MIN_PLAYER;
	public static int ALT_MAXIMUM_PLAYER_CONTRIB;
	public static long ALT_FESTIVAL_MANAGER_START;
	public static long ALT_FESTIVAL_LENGTH;
	public static long ALT_FESTIVAL_CYCLE_LENGTH;
	public static long ALT_FESTIVAL_FIRST_SPAWN;
	public static long ALT_FESTIVAL_FIRST_SWARM;
	public static long ALT_FESTIVAL_SECOND_SPAWN;
	public static long ALT_FESTIVAL_SECOND_SWARM;
	public static long ALT_FESTIVAL_CHEST_SPAWN;
	
	/** Four Sepulchers */
	public static int FS_TIME_ATTACK;
	public static int FS_TIME_ENTRY;
	public static int FS_TIME_WARMUP;
	public static int FS_PARTY_MEMBER_COUNT;
	
	/** dimensional rift */
	public static int RIFT_MIN_PARTY_SIZE;
	public static int RIFT_SPAWN_DELAY;
	public static int RIFT_MAX_JUMPS;
	public static int RIFT_AUTO_JUMPS_TIME_MIN;
	public static int RIFT_AUTO_JUMPS_TIME_MAX;
	public static int RIFT_ENTER_COST_RECRUIT;
	public static int RIFT_ENTER_COST_SOLDIER;
	public static int RIFT_ENTER_COST_OFFICER;
	public static int RIFT_ENTER_COST_CAPTAIN;
	public static int RIFT_ENTER_COST_COMMANDER;
	public static int RIFT_ENTER_COST_HERO;
	public static double RIFT_BOSS_ROOM_TIME_MUTIPLY;
	
	/** Wedding system */
	public static boolean ALLOW_WEDDING;
	public static int WEDDING_PRICE;
	public static boolean WEDDING_SAMESEX;
	public static boolean WEDDING_FORMALWEAR;
	
	/** Lottery */
	public static int ALT_LOTTERY_PRIZE;
	public static int ALT_LOTTERY_TICKET_PRICE;
	public static double ALT_LOTTERY_5_NUMBER_RATE;
	public static double ALT_LOTTERY_4_NUMBER_RATE;
	public static double ALT_LOTTERY_3_NUMBER_RATE;
	public static int ALT_LOTTERY_2_AND_1_NUMBER_PRIZE;
	
	/** Fishing tournament */
	public static boolean ALT_FISH_CHAMPIONSHIP_ENABLED;
	public static int ALT_FISH_CHAMPIONSHIP_REWARD_ITEM;
	public static int ALT_FISH_CHAMPIONSHIP_REWARD_1;
	public static int ALT_FISH_CHAMPIONSHIP_REWARD_2;
	public static int ALT_FISH_CHAMPIONSHIP_REWARD_3;
	public static int ALT_FISH_CHAMPIONSHIP_REWARD_4;
	public static int ALT_FISH_CHAMPIONSHIP_REWARD_5;
	
	// --------------------------------------------------
	// Custom Events
	// --------------------------------------------------
	public static boolean EVENTS_ALLOW_VOICED_COMMAND;
	/** TvT Event */
	public static boolean TVT_EVENT_ENABLED;
	public static String[] TVT_EVENT_INTERVAL;
	public static int TVT_EVENT_PARTICIPATION_TIME;
	public static int TVT_EVENT_RUNNING_TIME;
	public static int TVT_EVENT_PARTICIPATION_NPC_ID;
	public static int[] TVT_EVENT_PARTICIPATION_NPC_COORDINATES = new int[3];
	public static int[] TVT_EVENT_PARTICIPATION_FEE = new int[2];
	public static int TVT_EVENT_MIN_PLAYERS_IN_TEAMS;
	public static int TVT_EVENT_MAX_PLAYERS_IN_TEAMS;
	public static int TVT_EVENT_RESPAWN_TELEPORT_DELAY;
	public static int TVT_EVENT_START_LEAVE_TELEPORT_DELAY;
	public static String TVT_EVENT_TEAM_1_NAME;
	public static int[] TVT_EVENT_TEAM_1_COORDINATES = new int[3];
	public static String TVT_EVENT_TEAM_2_NAME;
	public static int[] TVT_EVENT_TEAM_2_COORDINATES = new int[3];
	public static List<int[]> TVT_EVENT_REWARDS;
	public static List<int[]> TVT_EVENT_REWARDS_KILL;
	public static boolean TVT_EVENT_TARGET_TEAM_MEMBERS_ALLOWED;
	public static boolean TVT_EVENT_SCROLL_ALLOWED;
	public static boolean TVT_EVENT_POTIONS_ALLOWED;
	public static boolean TVT_EVENT_SUMMON_BY_ITEM_ALLOWED;
	public static List<Integer> TVT_DOORS_IDS_TO_OPEN;
	public static List<Integer> TVT_DOORS_IDS_TO_CLOSE;
	public static boolean TVT_REWARD_TEAM_TIE;
	public static byte TVT_EVENT_MIN_LVL;
	public static byte TVT_EVENT_MAX_LVL;
	public static int TVT_EVENT_EFFECTS_REMOVAL;
	public static Map<Integer, Integer> TVT_EVENT_FIGHTER_BUFFS;
	public static Map<Integer, Integer> TVT_EVENT_MAGE_BUFFS;
	/** DM Event */
	public static boolean DM_EVENT_ENABLED;
	public static String[] DM_EVENT_INTERVAL;
	public static int DM_EVENT_PARTICIPATION_TIME;
	public static int DM_EVENT_RUNNING_TIME;
	public static int DM_EVENT_PARTICIPATION_NPC_ID;
	public static int[] DM_EVENT_PARTICIPATION_NPC_COORDINATES = new int[3];
	public static int[] DM_EVENT_PARTICIPATION_FEE = new int[2];
	public static int DM_EVENT_MIN_PLAYERS;
	public static int DM_EVENT_MAX_PLAYERS;
	public static int DM_EVENT_RESPAWN_TELEPORT_DELAY;
	public static int DM_EVENT_START_LEAVE_TELEPORT_DELAY;
	public static List<int[]> DM_EVENT_PLAYER_COORDINATES;
	public static Map<Integer, List<int[]>> DM_EVENT_REWARDS;
	public static int DM_REWARD_FIRST_PLAYERS;
	public static boolean DM_SHOW_TOP_RANK;
	public static int DM_TOP_RANK;
	public static boolean DM_EVENT_SCROLL_ALLOWED;
	public static boolean DM_EVENT_POTIONS_ALLOWED;
	public static boolean DM_EVENT_SUMMON_BY_ITEM_ALLOWED;
	public static List<Integer> DM_DOORS_IDS_TO_OPEN;
	public static List<Integer> DM_DOORS_IDS_TO_CLOSE;
	public static boolean DM_REWARD_PLAYERS_TIE;
	public static boolean DM_EVENT_HIDE_NAME;
	public static Integer DM_COLOR_TITLE;
	public static Integer DM_COLOR_NAME;
	public static byte DM_EVENT_MIN_LVL;
	public static byte DM_EVENT_MAX_LVL;
	public static int DM_EVENT_EFFECTS_REMOVAL;
	public static Map<Integer, Integer> DM_EVENT_FIGHTER_BUFFS;
	public static Map<Integer, Integer> DM_EVENT_MAGE_BUFFS;
	/** LM Event */
	public static boolean LM_EVENT_ENABLED;
	public static String[] LM_EVENT_INTERVAL;
	public static int LM_EVENT_PARTICIPATION_TIME;
	public static int LM_EVENT_RUNNING_TIME;
	public static int LM_EVENT_PARTICIPATION_NPC_ID;
	public static short LM_EVENT_PLAYER_CREDITS;
	public static int[] LM_EVENT_PARTICIPATION_NPC_COORDINATES = new int[3];
	public static int[] LM_EVENT_PARTICIPATION_FEE = new int[2];
	public static int LM_EVENT_MIN_PLAYERS;
	public static int LM_EVENT_MAX_PLAYERS;
	public static int LM_EVENT_RESPAWN_TELEPORT_DELAY;
	public static int LM_EVENT_START_LEAVE_TELEPORT_DELAY;
	public static List<int[]> LM_EVENT_PLAYER_COORDINATES;
	public static List<int[]> LM_EVENT_REWARDS;
	public static boolean LM_EVENT_SCROLL_ALLOWED;
	public static boolean LM_EVENT_POTIONS_ALLOWED;
	public static boolean LM_EVENT_SUMMON_BY_ITEM_ALLOWED;
	public static List<Integer> LM_DOORS_IDS_TO_OPEN;
	public static List<Integer> LM_DOORS_IDS_TO_CLOSE;
	public static boolean LM_REWARD_PLAYERS_TIE;
	public static boolean LM_EVENT_HIDE_NAME;
	public static Integer LM_COLOR_TITLE;
	public static Integer LM_COLOR_NAME;
	public static byte LM_EVENT_MIN_LVL;
	public static byte LM_EVENT_MAX_LVL;
	public static int LM_EVENT_EFFECTS_REMOVAL;
	public static Map<Integer, Integer> LM_EVENT_FIGHTER_BUFFS;
	public static Map<Integer, Integer> LM_EVENT_MAGE_BUFFS;
	// --------------------------------------------------
	// GeoEngine
	// --------------------------------------------------
	
	/** Geodata */
	public static String GEODATA_PATH;
	public static int COORD_SYNCHRONIZE;
	
	/** Path checking */
	public static int PART_OF_CHARACTER_HEIGHT;
	public static int MAX_OBSTACLE_HEIGHT;
	
	/** Path finding */
	public static boolean PATHFINDING;
	public static String PATHFIND_BUFFERS;
	public static int BASE_WEIGHT;
	public static int DIAGONAL_WEIGHT;
	public static int HEURISTIC_WEIGHT;
	public static int OBSTACLE_MULTIPLIER;
	public static int MAX_ITERATIONS;
	public static boolean DEBUG_PATH;
	public static boolean DEBUG_GEO_NODE;
	
	// --------------------------------------------------
	// HexID
	// --------------------------------------------------
	
	public static int SERVER_ID;
	public static byte[] HEX_ID;
	
	// --------------------------------------------------
	// Loginserver
	// --------------------------------------------------
	
	public static String LOGIN_BIND_ADDRESS;
	public static int PORT_LOGIN;

	public static int LOGIN_TRY_BEFORE_BAN;
	public static int LOGIN_BLOCK_AFTER_BAN;
	public static boolean ACCEPT_NEW_GAMESERVER;

	public static boolean SHOW_LICENCE;
	
	public static boolean AUTO_CREATE_ACCOUNTS;
	
	public static boolean LOG_LOGIN_CONTROLLER;
	
	public static boolean FORCE_GGAUTH;
	public static boolean CRYPT_TOKEN;
	
	public static boolean FLOOD_PROTECTION;
	public static int FAST_CONNECTION_LIMIT;
	public static int NORMAL_CONNECTION_TIME;
	public static int FAST_CONNECTION_TIME;
	public static int MAX_CONNECTION_PER_IP;
	
	// --------------------------------------------------
	// NPCs / Monsters
	// --------------------------------------------------
	
	/** Champion Mod */
	public static int CHAMPION_FREQUENCY;
	public static int CHAMP_MIN_LVL;
	public static int CHAMP_MAX_LVL;
	public static int CHAMPION_HP;
	public static int CHAMPION_REWARDS;
	public static int CHAMPION_ADENAS_REWARDS;
	public static double CHAMPION_HP_REGEN;
	public static double CHAMPION_ATK;
	public static double CHAMPION_SPD_ATK;
	public static int CHAMPION_REWARD;
	public static int CHAMPION_REWARD_ID;
	public static int CHAMPION_REWARD_QTY;
	public static int CHAMPION_ENABLE_AURA;
	
	/** Buffer */
	public static int BUFFER_MAX_SCHEMES;
	public static int BUFFER_STATIC_BUFF_COST;
	
	/** Misc */
	public static boolean ALLOW_CLASS_MASTERS;
	public static ClassMasterSettings CLASS_MASTER_SETTINGS;
	public static boolean ALLOW_ENTIRE_TREE;
	public static boolean ALTERNATE_CLASS_MASTER;
	public static boolean ANNOUNCE_MAMMON_SPAWN;
	public static boolean ALT_MOB_AGRO_IN_PEACEZONE;
	public static boolean SHOW_NPC_LVL;
	public static boolean SHOW_NPC_CREST;
	public static boolean SHOW_SUMMON_CREST;
	
	/** Wyvern Manager */
	public static boolean WYVERN_ALLOW_UPGRADER;
	public static int WYVERN_REQUIRED_LEVEL;
	public static int WYVERN_REQUIRED_CRYSTALS;
	
	/** Raid Boss */
	public static double RAID_HP_REGEN_MULTIPLIER;
	public static double RAID_MP_REGEN_MULTIPLIER;
	public static double RAID_DEFENCE_MULTIPLIER;
	public static double RAID_MINION_RESPAWN_TIMER;
	
	public static boolean RAID_DISABLE_CURSE;
	public static int RAID_CHAOS_TIME;
	public static int GRAND_CHAOS_TIME;
	public static int MINION_CHAOS_TIME;
	
	/** Grand Boss */
	public static int SPAWN_INTERVAL_AQ;
	public static int RANDOM_SPAWN_TIME_AQ;
	
	public static int SPAWN_INTERVAL_ANTHARAS;
	public static int RANDOM_SPAWN_TIME_ANTHARAS;
	public static int WAIT_TIME_ANTHARAS;
	
	public static int SPAWN_INTERVAL_BAIUM;
	public static int RANDOM_SPAWN_TIME_BAIUM;
	
	public static int SPAWN_INTERVAL_CORE;
	public static int RANDOM_SPAWN_TIME_CORE;
	
	public static int SPAWN_INTERVAL_FRINTEZZA;
	public static int RANDOM_SPAWN_TIME_FRINTEZZA;
	public static int WAIT_TIME_FRINTEZZA;
	public static int FRINTEZZA_MIN_PARTIES;
	public static int FRINTEZZA_MAX_PARTIES;
	
	public static int SPAWN_INTERVAL_ORFEN;
	public static int RANDOM_SPAWN_TIME_ORFEN;
	
	public static int SPAWN_INTERVAL_SAILREN;
	public static int RANDOM_SPAWN_TIME_SAILREN;
	public static int WAIT_TIME_SAILREN;
	
	public static int SPAWN_INTERVAL_VALAKAS;
	public static int RANDOM_SPAWN_TIME_VALAKAS;
	public static int WAIT_TIME_VALAKAS;
	
	public static int SPAWN_INTERVAL_ZAKEN;
	public static int RANDOM_SPAWN_TIME_ZAKEN;
	
	/** AI */
	public static boolean GUARD_ATTACK_AGGRO_MOB;
	public static int MAX_DRIFT_RANGE;
	public static int MIN_NPC_ANIMATION;
	public static int MAX_NPC_ANIMATION;
	public static int MIN_MONSTER_ANIMATION;
	public static int MAX_MONSTER_ANIMATION;
	
	// --------------------------------------------------
	// Players
	// --------------------------------------------------
	
	/** Misc */
	public static boolean EFFECT_CANCELING;
	public static double HP_REGEN_MULTIPLIER;
	public static double MP_REGEN_MULTIPLIER;
	public static double CP_REGEN_MULTIPLIER;
	public static int PLAYER_SPAWN_PROTECTION;
	public static int PLAYER_FAKEDEATH_UP_PROTECTION;
	public static double RESPAWN_RESTORE_HP;
	public static int MAX_PVTSTORE_SLOTS_DWARF;
	public static int MAX_PVTSTORE_SLOTS_OTHER;
	public static boolean DEEPBLUE_DROP_RULES;
	public static boolean ALT_GAME_DELEVEL;
	public static int DEATH_PENALTY_CHANCE;

	/** Inventory & WH */
	public static int INVENTORY_MAXIMUM_NO_DWARF;
	public static int INVENTORY_MAXIMUM_DWARF;
	public static int INVENTORY_MAXIMUM_QUEST_ITEMS;
	public static int INVENTORY_MAXIMUM_PET;
	public static int MAX_ITEM_IN_PACKET;
	public static double ALT_WEIGHT_LIMIT;
	public static int WAREHOUSE_SLOTS_NO_DWARF;
	public static int WAREHOUSE_SLOTS_DWARF;
	public static int WAREHOUSE_SLOTS_CLAN;
	public static int FREIGHT_SLOTS;
	public static boolean ALT_GAME_FREIGHTS;
	public static int ALT_GAME_FREIGHT_PRICE;
	public static int STARTING_ADENA;
	public static int STARTING_AA;
	
	/** Augmentations */
	public static int AUGMENTATION_NG_SKILL_CHANCE;
	public static int AUGMENTATION_NG_GLOW_CHANCE;
	public static int AUGMENTATION_MID_SKILL_CHANCE;
	public static int AUGMENTATION_MID_GLOW_CHANCE;
	public static int AUGMENTATION_HIGH_SKILL_CHANCE;
	public static int AUGMENTATION_HIGH_GLOW_CHANCE;
	public static int AUGMENTATION_TOP_SKILL_CHANCE;
	public static int AUGMENTATION_TOP_GLOW_CHANCE;
	public static int AUGMENTATION_BASESTAT_CHANCE;
	
	/** Karma & PvP */
	public static boolean KARMA_PLAYER_CAN_BE_KILLED_IN_PZ;
	public static boolean KARMA_PLAYER_CAN_SHOP;
	public static boolean KARMA_PLAYER_CAN_USE_GK;
	public static boolean KARMA_PLAYER_CAN_TELEPORT;
	public static boolean KARMA_PLAYER_CAN_TRADE;
	
	public static boolean KARMA_DROP_GM;
	public static boolean KARMA_AWARD_PK_KILL;
	public static int KARMA_PK_LIMIT;
	
	public static String KARMA_NONDROPPABLE_PET_ITEMS;
	public static String KARMA_NONDROPPABLE_ITEMS;
	public static int[] KARMA_LIST_NONDROPPABLE_PET_ITEMS;
	public static int[] KARMA_LIST_NONDROPPABLE_ITEMS;
	
	public static int PVP_NORMAL_TIME;
	public static int PVP_PVP_TIME;
	
	/** Party */
	public static String PARTY_XP_CUTOFF_METHOD;
	public static int PARTY_XP_CUTOFF_LEVEL;
	public static double PARTY_XP_CUTOFF_PERCENT;
	public static int ALT_PARTY_RANGE;
	public static int ALT_PARTY_RANGE2;
	
	/** GMs & Admin Stuff */
	public static boolean EVERYBODY_HAS_ADMIN_RIGHTS;
	public static int MASTERACCESS_LEVEL;
	public static int MASTERACCESS_NAME_COLOR;
	public static int MASTERACCESS_TITLE_COLOR;
	public static boolean GM_HERO_AURA;
	public static boolean GM_STARTUP_INVULNERABLE;
	public static boolean GM_STARTUP_INVISIBLE;
	public static boolean GM_STARTUP_SILENCE;
	public static boolean GM_STARTUP_AUTO_LIST;
	
	/** petitions */
	public static boolean PETITIONING_ALLOWED;
	public static int MAX_PETITIONS_PER_PLAYER;
	public static int MAX_PETITIONS_PENDING;
	
	/** Crafting **/
	public static boolean IS_CRAFTING_ENABLED;
	public static int DWARF_RECIPE_LIMIT;
	public static int COMMON_RECIPE_LIMIT;
	public static boolean ALT_BLACKSMITH_USE_RECIPES;
	
	/** Skills & Classes **/
	public static boolean AUTO_LEARN_SKILLS;
	public static boolean ALT_GAME_MAGICFAILURES;
	public static boolean ALT_GAME_SHIELD_BLOCKS;
	public static int ALT_PERFECT_SHLD_BLOCK;
	public static boolean LIFE_CRYSTAL_NEEDED;
	public static boolean SP_BOOK_NEEDED;
	public static boolean ES_SP_BOOK_NEEDED;
	public static boolean DIVINE_SP_BOOK_NEEDED;
	public static boolean ALT_GAME_SUBCLASS_WITHOUT_QUESTS;
	public static boolean ALT_GAME_SUBCLASS_EVERYWHERE;
	public static boolean EXPERTISE_PENALTY;
	
	/** Buffs */
	public static boolean STORE_SKILL_COOLTIME;
	public static int BUFFS_MAX_AMOUNT;
	public static boolean ENABLE_MODIFY_SKILL_DURATION;
	public static Map<Integer, Integer> SKILL_DURATION_LIST;
	
	/** Limits */
	public static int RUN_SPD_BOOST;
	public static int MAX_RUN_SPEED;
	public static int MAX_PCRIT_RATE;
	public static int MAX_MCRIT_RATE;
	public static int MAX_PATK_SPEED;
	public static int MAX_MATK_SPEED;
	public static int MAX_EVASION;
	public static int MAX_ACCURACY;
	
	// --------------------------------------------------
	// Sieges
	// --------------------------------------------------
	
	public static int SIEGE_LENGTH;
	public static int MINIMUM_CLAN_LEVEL;
	public static int MAX_ATTACKERS_NUMBER;
	public static int MAX_DEFENDERS_NUMBER;
	public static int ATTACKERS_RESPAWN_DELAY;
	
	// --------------------------------------------------
	// Server
	// --------------------------------------------------
	
	public static String GAMESERVER_HOSTNAME;
	public static int PORT_GAME;
	public static String HOSTNAME;
	public static int GAME_SERVER_LOGIN_PORT;
	public static String GAME_SERVER_LOGIN_HOST;
	public static int REQUEST_ID;
	public static boolean ACCEPT_ALTERNATE_ID;
	
	/** Access to database */
	public static String DATABASE_URL;
	public static String DATABASE_LOGIN;
	public static String DATABASE_PASSWORD;
	public static int DATABASE_MAX_CONNECTIONS;
	
	/** serverList & Test */
	public static boolean SERVER_LIST_BRACKET;
	public static boolean SERVER_LIST_CLOCK;
	public static int SERVER_LIST_AGE;
 	public static boolean SERVER_LIST_TESTSERVER;
	public static boolean SERVER_LIST_PVPSERVER;
	public static boolean SERVER_GMONLY;
	
	/** clients related */
	public static int DELETE_DAYS;
	public static int MAXIMUM_ONLINE_USERS;
	public static int MIN_PROTOCOL_REVISION;
	public static int MAX_PROTOCOL_REVISION;
	
	/** Auto-loot */
	public static boolean AUTO_LOOT;
	public static boolean AUTO_LOOT_HERBS;
	public static boolean AUTO_LOOT_RAID;
	
	/** Items Management */
	public static boolean ALLOW_DISCARDITEM;
	public static boolean MULTIPLE_ITEM_DROP;
	public static int HERB_AUTO_DESTROY_TIME;
	public static int ITEM_AUTO_DESTROY_TIME;
	public static int EQUIPABLE_ITEM_AUTO_DESTROY_TIME;
	public static Map<Integer, Integer> SPECIAL_ITEM_DESTROY_TIME;
	public static int PLAYER_DROPPED_ITEM_MULTIPLIER;
	
	/** Rate control */
	public static double RATE_XP;
	public static double RATE_SP;
	public static double RATE_PARTY_XP;
	public static double RATE_PARTY_SP;
	public static double RATE_DROP_ADENA;
	public static double RATE_CONSUMABLE_COST;
	public static double RATE_DROP_ITEMS;
	public static double RATE_DROP_ITEMS_BY_RAID;
	public static double RATE_DROP_SPOIL;
	public static int RATE_DROP_MANOR;
	
	public static double RATE_QUEST_DROP;
	public static double RATE_QUEST_REWARD;
	public static double RATE_QUEST_REWARD_XP;
	public static double RATE_QUEST_REWARD_SP;
	public static double RATE_QUEST_REWARD_ADENA;
	public static double RATE_QUEST_DROP_PROF;
	
	public static double RATE_KARMA_EXP_LOST;
	public static double RATE_SIEGE_GUARDS_PRICE;
	
	public static int PLAYER_DROP_LIMIT;
	public static int PLAYER_RATE_DROP;
	public static int PLAYER_RATE_DROP_ITEM;
	public static int PLAYER_RATE_DROP_EQUIP;
	public static int PLAYER_RATE_DROP_EQUIP_WEAPON;
	
	public static int KARMA_DROP_LIMIT;
	public static int KARMA_RATE_DROP;
	public static int KARMA_RATE_DROP_ITEM;
	public static int KARMA_RATE_DROP_EQUIP;
	public static int KARMA_RATE_DROP_EQUIP_WEAPON;
	
	public static double PET_XP_RATE;
	public static int PET_FOOD_RATE;
	public static double SINEATER_XP_RATE;
	
	public static double RATE_DROP_COMMON_HERBS;
	public static double RATE_DROP_HP_HERBS;
	public static double RATE_DROP_MP_HERBS;
	public static double RATE_DROP_SPECIAL_HERBS;
	
	/** Allow types */
	public static boolean ALLOW_FREIGHT;
	public static boolean ALLOW_WAREHOUSE;
	public static boolean ALLOW_WEAR;
	public static int WEAR_DELAY;
	public static int WEAR_PRICE;
	public static boolean ALLOW_LOTTERY;
	public static boolean ALLOW_WATER;
	public static boolean ALLOW_BOAT;
	public static boolean ALLOW_CURSED_WEAPONS;
	public static boolean ALLOW_MANOR;
	public static boolean ENABLE_FALLING_DAMAGE;
	
	/** Debug & Dev */
	public static boolean ALT_DEV_NO_SPAWNS;
	public static boolean DEBUG;
	public static boolean DEVELOPER;
	public static boolean PACKET_HANDLER_DEBUG;
	
	/** Deadlock Detector */
	public static boolean DEADLOCK_DETECTOR;
	public static int DEADLOCK_CHECK_INTERVAL;
	public static boolean RESTART_ON_DEADLOCK;
	
	/** Logs */
	public static boolean LOG_CHAT;
	public static boolean LOG_ITEMS;
	public static boolean GMAUDIT;
	
	/** Community Board */
	public static boolean ENABLE_COMMUNITY_BOARD;
	public static String BBS_DEFAULT;
	
	/** Flood Protectors */
	public static int ROLL_DICE_TIME;
	public static int HERO_VOICE_TIME;
	public static int SUBCLASS_TIME;
	public static int DROP_ITEM_TIME;
	public static int SERVER_BYPASS_TIME;
	public static int MULTISELL_TIME;
	public static int MANUFACTURE_TIME;
	public static int MANOR_TIME;
	public static int SENDMAIL_TIME;
	public static int CHARACTER_SELECT_TIME;
	public static int GLOBAL_CHAT_TIME;
	public static int TRADE_CHAT_TIME;
	public static int SOCIAL_TIME;
	
	/** ThreadPool */
	public static int SCHEDULED_THREAD_POOL_COUNT;
	public static int THREADS_PER_SCHEDULED_THREAD_POOL;
	public static int INSTANT_THREAD_POOL_COUNT;
	public static int THREADS_PER_INSTANT_THREAD_POOL;
	
	/** Misc */
	public static boolean L2WALKER_PROTECTION;
	public static boolean BOTS_PREVENTION;
	public static int KILLS_COUNTER;
	public static int KILLS_COUNTER_RANDOMIZATION;
	public static int VALIDATION_TIME;
	public static int PUNISHMENT;
	public static int PUNISHMENT_TIME;
	public static boolean SERVER_NEWS;
	public static int ZONE_TOWN;
	public static boolean DISABLE_TUTORIAL;
	
	/**Protection Settings */
	public static boolean ENABLED;
	public static int SERVER_KEY;
	public static String LOG_OPTION;
	public static int MAX_SESSIONS;
	public static int MAX_PREMIUM_SESSIONS;
	public static boolean ANNOUNCE_HACK;
	public static String ON_HACK_ATTEMP;
	public static boolean ALLOW_GM_FROM_BANNED_HWID;
	public static boolean LOG_SESSIONS;
	
	// --------------------------------------------------
	// Those "hidden" settings haven't configs to avoid admins to fuck their server
	// You still can experiment changing values here. But don't say I didn't warn you.
	// --------------------------------------------------
	
	/** Reserve Host on LoginServerThread */
	public static boolean RESERVE_HOST_ON_LOGIN = false; // default false
	
	/** MMO settings */
	public static int MMO_SELECTOR_SLEEP_TIME = 20; // default 20
	public static int MMO_MAX_SEND_PER_PASS = 80; // default 80
	public static int MMO_MAX_READ_PER_PASS = 80; // default 80
	public static int MMO_HELPER_BUFFER_COUNT = 20; // default 20
	
	/** Client Packets Queue settings */
	public static int CLIENT_PACKET_QUEUE_SIZE = 14; // default MMO_MAX_READ_PER_PASS + 2
	public static int CLIENT_PACKET_QUEUE_MAX_BURST_SIZE = 13; // default MMO_MAX_READ_PER_PASS + 1
	public static int CLIENT_PACKET_QUEUE_MAX_PACKETS_PER_SECOND = 160; // default 160
	public static int CLIENT_PACKET_QUEUE_MEASURE_INTERVAL = 5; // default 5
	public static int CLIENT_PACKET_QUEUE_MAX_AVERAGE_PACKETS_PER_SECOND = 80; // default 80
	public static int CLIENT_PACKET_QUEUE_MAX_FLOODS_PER_MIN = 2; // default 2
	public static int CLIENT_PACKET_QUEUE_MAX_OVERFLOWS_PER_MIN = 1; // default 1
	public static int CLIENT_PACKET_QUEUE_MAX_UNDERFLOWS_PER_MIN = 1; // default 1
	public static int CLIENT_PACKET_QUEUE_MAX_UNKNOWN_PER_MIN = 5; // default 5
	
	// --------------------------------------------------
	
	/**
	 * Initialize {@link ExProperties} from specified configuration file.
	 * @param filename : File name to be loaded.
	 * @return ExProperties : Initialized {@link ExProperties}.
	 */
	public static final ExProperties initProperties(String filename)
	{
		final ExProperties result = new ExProperties();
		
		try
		{
			result.load(new File(filename));
		}
		catch (IOException e)
		{
			_log.warning("Config: Error loading \"" + filename + "\" config.");
		}
		
		return result;
	}
	
	/**
	 * itemId1,itemNumber1;itemId2,itemNumber2... to the int[n][2] = [itemId1][itemNumber1],[itemId2][itemNumber2]...
	 * @param line
	 * @return an array consisting of parsed items.
	 */
	private static final int[][] parseItemsList(String line)
	{
		final String[] propertySplit = line.split(";");
		if (propertySplit.length == 0)
			return null;
		
		int i = 0;
		String[] valueSplit;
		final int[][] result = new int[propertySplit.length][];
		for (String value : propertySplit)
		{
			valueSplit = value.split(",");
			if (valueSplit.length != 2)
			{
				_log.warning("Config: Error parsing entry -> \"" + valueSplit[0] + "\", should be itemId,itemNumber");
				return null;
			}
			
			result[i] = new int[2];
			try
			{
				result[i][0] = Integer.parseInt(valueSplit[0]);
			}
			catch (NumberFormatException e)
			{
				_log.warning("Config: Error parsing item ID -> \"" + valueSplit[0] + "\"");
				return null;
			}
			
			try
			{
				result[i][1] = Integer.parseInt(valueSplit[1]);
			}
			catch (NumberFormatException e)
			{
				_log.warning("Config: Error parsing item amount -> \"" + valueSplit[1] + "\"");
				return null;
			}
			i++;
		}
		return result;
	}
	
	/**
	 * Loads custom settings
	 */
	private static final void loadCustom()
	{
		final ExProperties custom = initProperties(CUSTOM_FILE);
		BANKING_SYSTEM_ENABLED = custom.getProperty("BankingEnabled", false);
		BANKING_SYSTEM_GOLDBARS = custom.getProperty("BankingGoldbarCount", 1);
		BANKING_SYSTEM_ADENA = custom.getProperty("BankingAdenaCount", 500000000);
		OFFLINE_TRADE_ENABLE = custom.getProperty("OfflineTradeEnable", false);
		OFFLINE_CRAFT_ENABLE = custom.getProperty("OfflineCraftEnable", false);
		OFFLINE_MODE_IN_PEACE_ZONE = custom.getProperty("OfflineModeInPeaceZone", false);
		OFFLINE_MODE_NO_DAMAGE = custom.getProperty("OfflineModeNoDamage", false);
		OFFLINE_SET_NAME_COLOR = custom.getProperty("OfflineSetNameColor", false);
		OFFLINE_NAME_COLOR = Integer.decode("0x" + custom.getProperty("OfflineNameColor", 808080));
		RESTORE_OFFLINERS = custom.getProperty("RestoreOffliners", false);
		OFFLINE_MAX_DAYS = custom.getProperty("OfflineMaxDays", 10);
		OFFLINE_DISCONNECT_FINISHED = custom.getProperty("OfflineDisconnectFinished", true);
		RAID_BOSS_INFO_PAGE_LIMIT = custom.getProperty("RaidBossInfoPageLimit", 15);
		RAID_BOSS_DROP_PAGE_LIMIT = custom.getProperty("RaidBossDropPageLimit", 15);
		RAID_BOSS_DATE_FORMAT = custom.getProperty("RaidBossDateFormat", "MMM dd, HH:mm");
		RAID_BOSS_IDS = custom.getProperty("RaidBossIds", "0,0");
		LIST_RAID_BOSS_IDS = new ArrayList<>();
		for (String val : RAID_BOSS_IDS.split(","))
		{
			int npcId = Integer.parseInt(val);
			LIST_RAID_BOSS_IDS.add(npcId);
		}
		CKM_ENABLED = custom.getProperty("CKMEnabled", false);
		CKM_CYCLE_LENGTH = custom.getProperty("CKMCycleLength", 86400000);
		CKM_PVP_NPC_TITLE = custom.getProperty("CKMPvPNpcTitle", "%kills% PvPs in the last 24h");
		CKM_PVP_NPC_TITLE_COLOR = Integer.decode(StringUtil.concat("0x", custom.getProperty("CKMPvPNpcTitleColor", "00CCFF")));
		CKM_PVP_NPC_NAME_COLOR = Integer.decode(StringUtil.concat("0x", custom.getProperty("CKMPvPNpcNameColor", "FFFFFF")));
		CKM_PK_NPC_TITLE = custom.getProperty("CKMPKNpcTitle", "%kills% PKs in the last 24h");
		CKM_PK_NPC_TITLE_COLOR = Integer.decode(StringUtil.concat("0x", custom.getProperty("CKMPKNpcTitleColor", "00CCFF")));
		CKM_PK_NPC_NAME_COLOR = Integer.decode(StringUtil.concat("0x", custom.getProperty("CKMPKNpcNameColor", "FFFFFF")));
		AIO_TITLE = custom.getProperty("AioTitle", "Aio");
		AIO_TITLE_COLOR = Integer.decode("0x" + custom.getProperty("AioTitleColor", "606060"));
		AIO_RESTRICTED_CLASSES = custom.getProperty("AioRestrictedClasses", "0,0");
		LIST_AIO_RESTRICTED_CLASSES = new ArrayList<>();
		for (String val : AIO_RESTRICTED_CLASSES.split(","))
		{
			int classId = Integer.parseInt(val);
			LIST_AIO_RESTRICTED_CLASSES.add(classId);
		}
		AIO_ITEMS = custom.getProperty("AioItems", "0,0");
		LIST_AIO_ITEMS = new HashMap<>();
		for (String val : AIO_ITEMS.split(";"))
		{
			String[] valSplit = val.split(",");
			int itemId = Integer.parseInt(valSplit[0]);
			int amount = Integer.parseInt(valSplit[1]);
			LIST_AIO_ITEMS.put(itemId, amount);
		}
		AIO_SKILLS = custom.getProperty("AioSkills", "0,0");
		LIST_AIO_SKILLS = new HashMap<>();
		for (String val : AIO_SKILLS.split(";"))
		{
			String[] valSplit = val.split(",");
			int skillId = Integer.parseInt(valSplit[0]);
			int skillLv = Integer.parseInt(valSplit[1]);
			LIST_AIO_SKILLS.put(skillId, skillLv);
		}
		ENABLE_VIP_ITEM = custom.getProperty("VipItemEnabled", true);
		VIP_ITEM_ID = custom.getProperty("VipItemId", 3481);
		VIP_DAYS = custom.getProperty("VipDays", 5);						
		VIP_XP_SP_RATES = custom.getProperty("VipExp/SpRates", 1000);
		VIP_ADENA_RATES = custom.getProperty("VipAdenaDrop", 1000);
		VIP_SPOIL_RATES = custom.getProperty("VipSpoilRates", 1000);
		VIP_RATE_DROP_ITEMS_BY_RAID = custom.getProperty("VipRaidDrop", 1);
		VIP_DROP_RATES = custom.getProperty("VipDrop", 1);				
		CLAN_MANAGER_ITEM_ID = custom.getProperty("ClanManagerItemId", 57);
		CLAN_MANAGER_LEVEL_UP_COUNT = custom.getProperty("ClanManagerLevelUpCount", 10000);
		CLAN_MANAGER_REPUTATION_COUNT = custom.getProperty("ClanManagerReputationCount", 100000);
		CLAN_MANAGER_CLAN_SKILLS_COUNT = custom.getProperty("ClanManagerClanSkillsCouunt", 100000000);
		CLAN_MANAGER_CLAN_LEVEL_REWARD = custom.getProperty("ClanManagerClanLevelReward", 1);
		CLAN_MANAGER_CLAN_REPUTATION_REWARD = custom.getProperty("ClanManagerClanReputationReward", 1000);
		ENABLE_PVP_COLOR = custom.getProperty("EnablePvpColor", false);
		SHOUT_RESTRICTION_TYPE = ShoutRestrictionType.valueOf(custom.getProperty("ShoutRestrictionType", "NONE"));
		TRADE_RESTRICTION_TYPE = TradeRestrictionType.valueOf(custom.getProperty("TradeRestrictionType", "NONE"));
		SHOUT_RESTRICTION_VALUE = custom.getProperty("ShoutRestrictionValue", 0);
		TRADE_RESTRICTION_VALUE = custom.getProperty("TradeRestrictionValue", 0);
		ENABLE_RAID_BOSS_DEFEATED_MSG = custom.getProperty("EnableRaidBossDefeatedMsg", false);
		RAID_BOSS_DEFEATED_BY_CLAN_MEMBER_MSG = custom.getProperty("RaidBossDefeatedByClanMemberMsg", "Raid Boss %raidboss% has been defeated by %player% of clan %clan%.");
		RAID_BOSS_DEFEATED_BY_PLAYER_MSG = custom.getProperty("RaidBossDefeatedByPlayerMsg", "Raid Boss %raidboss% has been defeated by %player%.");
		String globalTemp = custom.getProperty("GlobalDropItems", "");
		String[] globalTemp2 = globalTemp.split(";");
		for (String s : globalTemp2)
		{
			List<Integer> list = new ArrayList<>();
			String[] t = s.split(",");
			list.add(Integer.parseInt(t[1]));
			list.add(Integer.parseInt(t[2]));
			list.add(Integer.parseInt(t[3]));
			GLOBAL_DROP_ITEMS.put(Integer.parseInt(t[0]), list);
		}
		globalTemp = custom.getProperty("ChampionGlobalDropItems", "");
		globalTemp2 = globalTemp.split(";");
		for (String s : globalTemp2)
		{
			List<Integer> list = new ArrayList<>();
			String[] t = s.split(",");
			list.add(Integer.parseInt(t[1]));
			list.add(Integer.parseInt(t[2]));
			list.add(Integer.parseInt(t[3]));
			GLOBAL_DROP_ITEMS_CHAMPION.put(Integer.parseInt(t[0]), list);
		}
		ALLOW_GLOBAL_DROP_RANDOM = custom.getProperty("AllowRandomQuantityDrop", true);
		ALLOW_GLOBAL_DROP = custom.getProperty("AllowGlobalDrop", true);
		
		npcEnchantItemsEnabled = custom.getProperty("EnableNpcEnchantItems", false);
		weaponEnchantLevel = custom.getProperty("WeaponEnchantLevel", 20);
		armorEnchantLevel = custom.getProperty("ArmorEnchantLevel", 16);
		IngredientID = custom.getProperty("IngredientID", 57);
		InAmountWeapon = custom.getProperty("IngredientAmountWeapon", 15000);
		InAmountArmor = custom.getProperty("IngredientAmountArmor", 10000);
		augmentItemChance = custom.getProperty("AugmentItemChane", 30);
		nonEnchantableItems = custom.getProperty("NonEnchantableItemList", "");
		nonEnchantableItemList = new ArrayList<>();
		for (String itemId : nonEnchantableItems.split(","))
			nonEnchantableItemList.add(Integer.parseInt(itemId));
		
		modifyItemEnchant = custom.getProperty("ModifyItemEnchant", false);
		if (modifyItemEnchant)
		{
			modifyItemEnchantList = new HashMap<>();
			String[] propertySplit = custom.getProperty("ModifyItemEnchantList", "").split(";");
			for (String item : propertySplit)
			{
				String[] itemEnchantSplit = item.split(",");
				if (itemEnchantSplit.length != 2)
				{
					System.out.println("invalid config property -> ModifyItemEnchantList \"" + item + "\"");
				}
				else
				{
					try
					{
						modifyItemEnchantList.put(Integer.parseInt(itemEnchantSplit[0]), Integer.parseInt(itemEnchantSplit[1]));
					}
					catch (NumberFormatException nfe)
					{
						nfe.printStackTrace();
					}
				}
			}
		}
		OPEN_URL_ENABLE = custom.getProperty("OpenUrlEnable", true);
		OPEN_URL_SITE = custom.getProperty("OpenUrlSite", "");
		GRAND_BOSS = custom.getProperty("GrandBossList");
		GRAND_BOSS_LIST = new ArrayList<>();
		for (String id : GRAND_BOSS.trim().split(","))
		{
			GRAND_BOSS_LIST.add(Integer.parseInt(id.trim()));
		}
		ENABLE_CLAN_SYSTEM = custom.getProperty("EnableClanSystem", true);
		CLAN_LEVEL = (byte) custom.getProperty("ClanLevel", 8);
		REPUTATION_QUANTITY = custom.getProperty("ClanReputation", 10000);
		CLAN_ITEM_ID = custom.getProperty("ClanItemId", 57);
		CLAN_ITEM_COUNT = custom.getProperty("ClanItemCount", 10000);
		LIST_BUFF_COMMAND = custom.getProperty("buffCommandFightBuffsID", "123,456");
		
		String[] buffCommand = LIST_BUFF_COMMAND.split(",");
		BUFF_COMMAND_FIGHT_IDBUFFS = new int[buffCommand.length];
		for (int i = 0; i < buffCommand.length; i++)
			BUFF_COMMAND_FIGHT_IDBUFFS[i] = Integer.parseInt(buffCommand[i]);
		
		LIST_BUFF_COMMAND = custom.getProperty("buffCommandMageBuffsID", "789,1011112");
		
		buffCommand = LIST_BUFF_COMMAND.split(",");
		BUFF_COMMAND_MAGE_IDBUFFS = new int[buffCommand.length];
		for (int i = 0; i < buffCommand.length; i++)
			BUFF_COMMAND_MAGE_IDBUFFS[i] = Integer.parseInt(buffCommand[i]);
		
		ALLOW_HEAVY_USE_LIGHT = custom.getProperty("AllowHeavyUseLight", false);
		NOTALLOWCLASSE = custom.getProperty("NotAllowedUseLight", "");
		NOTALLOWEDUSELIGHT = new ArrayList<>();
		for (String classId : NOTALLOWCLASSE.split(","))
		{
			NOTALLOWEDUSELIGHT.add(Integer.parseInt(classId));
		}
		ALLOW_LIGHT_USE_HEAVY = custom.getProperty("AllowLightUseHeavy", false);
		NOTALLOWCLASS = custom.getProperty("NotAllowedUseHeavy", "");
		NOTALLOWEDUSEHEAVY = new ArrayList<>();
		for (String classId : NOTALLOWCLASS.split(","))
		{
			NOTALLOWEDUSEHEAVY.add(Integer.parseInt(classId));
		}
		ALT_DISABLE_BOW_CLASSES = custom.getProperty("AltDisableBow", false);
		DISABLE_BOW_CLASSES_STRING = custom.getProperty("DisableBowForClasses", "");
		DISABLE_BOW_CLASSES = new ArrayList<>();
		for (String class_id : DISABLE_BOW_CLASSES_STRING.split(","))
		{
			if (!class_id.equals(""))
				DISABLE_BOW_CLASSES.add(Integer.parseInt(class_id));
		}
		ALT_DISABLE_DAGGER_CLASSES = custom.getProperty("AltDisableDagger", false);
		DISABLE_DAGGER_CLASSES_STRING = custom.getProperty("DisableDaggerForClasses", "");
		DISABLE_DAGGER_CLASSES = new ArrayList<>();
		for (String class_id : DISABLE_DAGGER_CLASSES_STRING.split(","))
		{
			if (!class_id.equals(""))
				DISABLE_DAGGER_CLASSES.add(Integer.parseInt(class_id));
		}
		ALT_DISABLE_SWORD_CLASSES = custom.getProperty("AltDisableSword", false);
		DISABLE_SWORD_CLASSES_STRING = custom.getProperty("DisableSwordForClasses", "");
		DISABLE_SWORD_CLASSES = new ArrayList<>();
		for (String class_id : DISABLE_SWORD_CLASSES_STRING.split(","))
		{
			if (!class_id.equals(""))
				DISABLE_SWORD_CLASSES.add(Integer.parseInt(class_id));
		}
		ALT_DISABLE_BLUNT_CLASSES = custom.getProperty("AltDisableBlunt", false);
		DISABLE_BLUNT_CLASSES_STRING = custom.getProperty("DisableBluntForClasses", "");
		DISABLE_BLUNT_CLASSES = new ArrayList<>();
		for (String class_id : DISABLE_BLUNT_CLASSES_STRING.split(","))
		{
			if (!class_id.equals(""))
				DISABLE_BLUNT_CLASSES.add(Integer.parseInt(class_id));
		}
		ALT_DISABLE_DUAL_CLASSES = custom.getProperty("AltDisableDual", false);
		DISABLE_DUAL_CLASSES_STRING = custom.getProperty("DisableDualForClasses", "");
		DISABLE_DUAL_CLASSES = new ArrayList<>();
		for (String class_id : DISABLE_DUAL_CLASSES_STRING.split(","))
		{
			if (!class_id.equals(""))
				DISABLE_DUAL_CLASSES.add(Integer.parseInt(class_id));
		}
		ALT_DISABLE_POLE_CLASSES = custom.getProperty("AltDisablePolle", false);
		DISABLE_POLE_CLASSES_STRING = custom.getProperty("DisablePolleForClasses", "");
		DISABLE_POLE_CLASSES = new ArrayList<>();
		for (String class_id : DISABLE_POLE_CLASSES_STRING.split(","))
		{
			if (!class_id.equals(""))
				DISABLE_POLE_CLASSES.add(Integer.parseInt(class_id));
		}
		ALT_DISABLE_BIGSWORD_CLASSES = custom.getProperty("AltDisableBigSword", false);
		DISABLE_BIGSWORD_CLASSES_STRING = custom.getProperty("DisableBigSwordForClasses", "");
		DISABLE_BIGSWORD_CLASSES = new ArrayList<>();
		for (String class_id : DISABLE_BIGSWORD_CLASSES_STRING.split(","))
		{
			if (!class_id.equals(""))
				DISABLE_BIGSWORD_CLASSES.add(Integer.parseInt(class_id));
		}
		VOTE_MANAGER_ENABLED = custom.getProperty("EnableVoteManager", false);
		VOTES_SITE_HOPZONE_URL = custom.getProperty("HopzoneUrl", "");
		VOTES_SITE_TOPZONE_URL = custom.getProperty("TopzoneUrl", "");
		VOTES_SITE_L2NETWORK_URL = custom.getProperty("NetworkUrl", "");
		VOTE_REWARD_ID = custom.getProperty("VoteRewardId", 57);
		VOTE_REWARD_ID_COUNT = custom.getProperty("VoteRewardCount", 20);
		TIME_TO_VOTE = custom.getProperty("TimeToVote", 60);
	}
	
	/**
	 * Loads clan and clan hall settings.
	 */
	private static final void loadClans()
	{
		final ExProperties clans = initProperties(CLANS_FILE);
		ALT_CLAN_JOIN_DAYS = clans.getProperty("DaysBeforeJoinAClan", 5);
		ALT_CLAN_CREATE_DAYS = clans.getProperty("DaysBeforeCreateAClan", 10);
		ALT_MAX_NUM_OF_CLANS_IN_ALLY = clans.getProperty("AltMaxNumOfClansInAlly", 3);
		ALT_CLAN_MEMBERS_FOR_WAR = clans.getProperty("AltClanMembersForWar", 15);
		ALT_CLAN_WAR_PENALTY_WHEN_ENDED = clans.getProperty("AltClanWarPenaltyWhenEnded", 5);
		ALT_CLAN_DISSOLVE_DAYS = clans.getProperty("DaysToPassToDissolveAClan", 7);
		ALT_ALLY_JOIN_DAYS_WHEN_LEAVED = clans.getProperty("DaysBeforeJoinAllyWhenLeaved", 1);
		ALT_ALLY_JOIN_DAYS_WHEN_DISMISSED = clans.getProperty("DaysBeforeJoinAllyWhenDismissed", 1);
		ALT_ACCEPT_CLAN_DAYS_WHEN_DISMISSED = clans.getProperty("DaysBeforeAcceptNewClanWhenDismissed", 1);
		ALT_CREATE_ALLY_DAYS_WHEN_DISSOLVED = clans.getProperty("DaysBeforeCreateNewAllyWhenDissolved", 10);
		ALT_MEMBERS_CAN_WITHDRAW_FROM_CLANWH = clans.getProperty("AltMembersCanWithdrawFromClanWH", false);
		
		ALT_MANOR_REFRESH_TIME = clans.getProperty("AltManorRefreshTime", 20);
		ALT_MANOR_REFRESH_MIN = clans.getProperty("AltManorRefreshMin", 0);
		ALT_MANOR_APPROVE_TIME = clans.getProperty("AltManorApproveTime", 6);
		ALT_MANOR_APPROVE_MIN = clans.getProperty("AltManorApproveMin", 0);
		ALT_MANOR_MAINTENANCE_MIN = clans.getProperty("AltManorMaintenanceMin", 6);
		ALT_MANOR_SAVE_PERIOD_RATE = clans.getProperty("AltManorSavePeriodRate", 2) * 3600000;
		
		CH_TELE_FEE_RATIO = clans.getProperty("ClanHallTeleportFunctionFeeRatio", 86400000);
		CH_TELE1_FEE = clans.getProperty("ClanHallTeleportFunctionFeeLvl1", 7000);
		CH_TELE2_FEE = clans.getProperty("ClanHallTeleportFunctionFeeLvl2", 14000);
		CH_SUPPORT_FEE_RATIO = clans.getProperty("ClanHallSupportFunctionFeeRatio", 86400000);
		CH_SUPPORT1_FEE = clans.getProperty("ClanHallSupportFeeLvl1", 17500);
		CH_SUPPORT2_FEE = clans.getProperty("ClanHallSupportFeeLvl2", 35000);
		CH_SUPPORT3_FEE = clans.getProperty("ClanHallSupportFeeLvl3", 49000);
		CH_SUPPORT4_FEE = clans.getProperty("ClanHallSupportFeeLvl4", 77000);
		CH_SUPPORT5_FEE = clans.getProperty("ClanHallSupportFeeLvl5", 147000);
		CH_SUPPORT6_FEE = clans.getProperty("ClanHallSupportFeeLvl6", 252000);
		CH_SUPPORT7_FEE = clans.getProperty("ClanHallSupportFeeLvl7", 259000);
		CH_SUPPORT8_FEE = clans.getProperty("ClanHallSupportFeeLvl8", 364000);
		CH_MPREG_FEE_RATIO = clans.getProperty("ClanHallMpRegenerationFunctionFeeRatio", 86400000);
		CH_MPREG1_FEE = clans.getProperty("ClanHallMpRegenerationFeeLvl1", 14000);
		CH_MPREG2_FEE = clans.getProperty("ClanHallMpRegenerationFeeLvl2", 26250);
		CH_MPREG3_FEE = clans.getProperty("ClanHallMpRegenerationFeeLvl3", 45500);
		CH_MPREG4_FEE = clans.getProperty("ClanHallMpRegenerationFeeLvl4", 96250);
		CH_MPREG5_FEE = clans.getProperty("ClanHallMpRegenerationFeeLvl5", 140000);
		CH_HPREG_FEE_RATIO = clans.getProperty("ClanHallHpRegenerationFunctionFeeRatio", 86400000);
		CH_HPREG1_FEE = clans.getProperty("ClanHallHpRegenerationFeeLvl1", 4900);
		CH_HPREG2_FEE = clans.getProperty("ClanHallHpRegenerationFeeLvl2", 5600);
		CH_HPREG3_FEE = clans.getProperty("ClanHallHpRegenerationFeeLvl3", 7000);
		CH_HPREG4_FEE = clans.getProperty("ClanHallHpRegenerationFeeLvl4", 8166);
		CH_HPREG5_FEE = clans.getProperty("ClanHallHpRegenerationFeeLvl5", 10500);
		CH_HPREG6_FEE = clans.getProperty("ClanHallHpRegenerationFeeLvl6", 12250);
		CH_HPREG7_FEE = clans.getProperty("ClanHallHpRegenerationFeeLvl7", 14000);
		CH_HPREG8_FEE = clans.getProperty("ClanHallHpRegenerationFeeLvl8", 15750);
		CH_HPREG9_FEE = clans.getProperty("ClanHallHpRegenerationFeeLvl9", 17500);
		CH_HPREG10_FEE = clans.getProperty("ClanHallHpRegenerationFeeLvl10", 22750);
		CH_HPREG11_FEE = clans.getProperty("ClanHallHpRegenerationFeeLvl11", 26250);
		CH_HPREG12_FEE = clans.getProperty("ClanHallHpRegenerationFeeLvl12", 29750);
		CH_HPREG13_FEE = clans.getProperty("ClanHallHpRegenerationFeeLvl13", 36166);
		CH_EXPREG_FEE_RATIO = clans.getProperty("ClanHallExpRegenerationFunctionFeeRatio", 86400000);
		CH_EXPREG1_FEE = clans.getProperty("ClanHallExpRegenerationFeeLvl1", 21000);
		CH_EXPREG2_FEE = clans.getProperty("ClanHallExpRegenerationFeeLvl2", 42000);
		CH_EXPREG3_FEE = clans.getProperty("ClanHallExpRegenerationFeeLvl3", 63000);
		CH_EXPREG4_FEE = clans.getProperty("ClanHallExpRegenerationFeeLvl4", 105000);
		CH_EXPREG5_FEE = clans.getProperty("ClanHallExpRegenerationFeeLvl5", 147000);
		CH_EXPREG6_FEE = clans.getProperty("ClanHallExpRegenerationFeeLvl6", 163331);
		CH_EXPREG7_FEE = clans.getProperty("ClanHallExpRegenerationFeeLvl7", 210000);
		CH_ITEM_FEE_RATIO = clans.getProperty("ClanHallItemCreationFunctionFeeRatio", 86400000);
		CH_ITEM1_FEE = clans.getProperty("ClanHallItemCreationFunctionFeeLvl1", 210000);
		CH_ITEM2_FEE = clans.getProperty("ClanHallItemCreationFunctionFeeLvl2", 490000);
		CH_ITEM3_FEE = clans.getProperty("ClanHallItemCreationFunctionFeeLvl3", 980000);
		CH_CURTAIN_FEE_RATIO = clans.getProperty("ClanHallCurtainFunctionFeeRatio", 86400000);
		CH_CURTAIN1_FEE = clans.getProperty("ClanHallCurtainFunctionFeeLvl1", 2002);
		CH_CURTAIN2_FEE = clans.getProperty("ClanHallCurtainFunctionFeeLvl2", 2625);
		CH_FRONT_FEE_RATIO = clans.getProperty("ClanHallFrontPlatformFunctionFeeRatio", 86400000);
		CH_FRONT1_FEE = clans.getProperty("ClanHallFrontPlatformFunctionFeeLvl1", 3031);
		CH_FRONT2_FEE = clans.getProperty("ClanHallFrontPlatformFunctionFeeLvl2", 9331);
		PARTISAN_DAY = clans.getProperty("PartisanDay", 5);
		PARTISAN_HOUR = clans.getProperty("PartisanHour", 21);
		PARTISAN_MINUTES = clans.getProperty("PartisanMinutes", 0);
	}
	
	/**
	 * Loads event settings.<br>
	 * Such as olympiad, seven signs festival, four sepulchures, dimensional rift, weddings, lottery, fishing championship.
	 */
	private static final void loadEvents()
	{
		final ExProperties events = initProperties(EVENTS_FILE);
		ALT_OLY_START_TIME = events.getProperty("AltOlyStartTime", 18);
		ALT_OLY_MIN = events.getProperty("AltOlyMin", 0);
		ALT_OLY_CPERIOD = events.getProperty("AltOlyCPeriod", 21600000);
		ALT_OLY_BATTLE = events.getProperty("AltOlyBattle", 180000);
		ALT_OLY_WPERIOD = events.getProperty("AltOlyWPeriod", 604800000);
		ALT_OLY_VPERIOD = events.getProperty("AltOlyVPeriod", 86400000);
		ALT_OLY_WAIT_TIME = events.getProperty("AltOlyWaitTime", 30);
		ALT_OLY_WAIT_BATTLE = events.getProperty("AltOlyWaitBattle", 60);
		ALT_OLY_WAIT_END = events.getProperty("AltOlyWaitEnd", 40);
		ALT_OLY_START_POINTS = events.getProperty("AltOlyStartPoints", 18);
		ALT_OLY_WEEKLY_POINTS = events.getProperty("AltOlyWeeklyPoints", 3);
		ALT_OLY_MIN_MATCHES = events.getProperty("AltOlyMinMatchesToBeClassed", 5);
		ALT_OLY_CLASSED = events.getProperty("AltOlyClassedParticipants", 5);
		ALT_OLY_NONCLASSED = events.getProperty("AltOlyNonClassedParticipants", 9);
		ALT_OLY_CLASSED_REWARD = parseItemsList(events.getProperty("AltOlyClassedReward", "6651,50"));
		ALT_OLY_NONCLASSED_REWARD = parseItemsList(events.getProperty("AltOlyNonClassedReward", "6651,30"));
		ALT_OLY_GP_PER_POINT = events.getProperty("AltOlyGPPerPoint", 1000);
		ALT_OLY_HERO_POINTS = events.getProperty("AltOlyHeroPoints", 300);
		ALT_OLY_RANK1_POINTS = events.getProperty("AltOlyRank1Points", 100);
		ALT_OLY_RANK2_POINTS = events.getProperty("AltOlyRank2Points", 75);
		ALT_OLY_RANK3_POINTS = events.getProperty("AltOlyRank3Points", 55);
		ALT_OLY_RANK4_POINTS = events.getProperty("AltOlyRank4Points", 40);
		ALT_OLY_RANK5_POINTS = events.getProperty("AltOlyRank5Points", 30);
		ALT_OLY_MAX_POINTS = events.getProperty("AltOlyMaxPoints", 10);
		ALT_OLY_DIVIDER_CLASSED = events.getProperty("AltOlyDividerClassed", 3);
		ALT_OLY_DIVIDER_NON_CLASSED = events.getProperty("AltOlyDividerNonClassed", 3);
		ALT_OLY_ANNOUNCE_GAMES = events.getProperty("AltOlyAnnounceGames", true);
		ALT_OLY_DUAL_BOX = events.getProperty("AltOlyDualBox", true);
		ALT_OLY_SKILL_PROTECT = events.getProperty("AltOlySkillProtect", true);
		for (String id : events.getProperty("AltOllySkillId", "0").split(","))
		{
			ALT_OLY_SKILL_LIST.add(Integer.parseInt(id));
		}
		ALT_OLY_END_ANNOUNCE = events.getProperty("AltOlyEndAnnounce", false);
        ALT_OLY_BACK_REUSE_SKILLS = events.getProperty("AltOlyBackReuseSkills", false);
        ALT_OLY_ENCHANT_LIMIT = events.getProperty("AltOlyEnchantLimit", -1);
        if (ALT_OLY_ENCHANT_LIMIT > 65535) {
            ALT_OLY_ENCHANT_LIMIT = 65535;
        }
        
		ALT_GAME_CASTLE_DAWN = events.getProperty("AltCastleForDawn", true);
		ALT_GAME_CASTLE_DUSK = events.getProperty("AltCastleForDusk", true);
		ALT_FESTIVAL_MIN_PLAYER = MathUtil.limit(events.getProperty("AltFestivalMinPlayer", 5), 2, 9);
		ALT_MAXIMUM_PLAYER_CONTRIB = events.getProperty("AltMaxPlayerContrib", 1000000);
		ALT_FESTIVAL_MANAGER_START = events.getProperty("AltFestivalManagerStart", 120000);
		ALT_FESTIVAL_LENGTH = events.getProperty("AltFestivalLength", 1080000);
		ALT_FESTIVAL_CYCLE_LENGTH = events.getProperty("AltFestivalCycleLength", 2280000);
		ALT_FESTIVAL_FIRST_SPAWN = events.getProperty("AltFestivalFirstSpawn", 120000);
		ALT_FESTIVAL_FIRST_SWARM = events.getProperty("AltFestivalFirstSwarm", 300000);
		ALT_FESTIVAL_SECOND_SPAWN = events.getProperty("AltFestivalSecondSpawn", 540000);
		ALT_FESTIVAL_SECOND_SWARM = events.getProperty("AltFestivalSecondSwarm", 720000);
		ALT_FESTIVAL_CHEST_SPAWN = events.getProperty("AltFestivalChestSpawn", 900000);
		
		FS_TIME_ATTACK = events.getProperty("TimeOfAttack", 50);
		FS_TIME_ENTRY = events.getProperty("TimeOfEntry", 3);
		FS_TIME_WARMUP = events.getProperty("TimeOfWarmUp", 2);
		FS_PARTY_MEMBER_COUNT = MathUtil.limit(events.getProperty("NumberOfNecessaryPartyMembers", 4), 2, 9);
		
		RIFT_MIN_PARTY_SIZE = events.getProperty("RiftMinPartySize", 2);
		RIFT_MAX_JUMPS = events.getProperty("MaxRiftJumps", 4);
		RIFT_SPAWN_DELAY = events.getProperty("RiftSpawnDelay", 10000);
		RIFT_AUTO_JUMPS_TIME_MIN = events.getProperty("AutoJumpsDelayMin", 480);
		RIFT_AUTO_JUMPS_TIME_MAX = events.getProperty("AutoJumpsDelayMax", 600);
		RIFT_ENTER_COST_RECRUIT = events.getProperty("RecruitCost", 18);
		RIFT_ENTER_COST_SOLDIER = events.getProperty("SoldierCost", 21);
		RIFT_ENTER_COST_OFFICER = events.getProperty("OfficerCost", 24);
		RIFT_ENTER_COST_CAPTAIN = events.getProperty("CaptainCost", 27);
		RIFT_ENTER_COST_COMMANDER = events.getProperty("CommanderCost", 30);
		RIFT_ENTER_COST_HERO = events.getProperty("HeroCost", 33);
		RIFT_BOSS_ROOM_TIME_MUTIPLY = events.getProperty("BossRoomTimeMultiply", 1.);
		
		ALLOW_WEDDING = events.getProperty("AllowWedding", false);
		WEDDING_PRICE = events.getProperty("WeddingPrice", 1000000);
		WEDDING_SAMESEX = events.getProperty("WeddingAllowSameSex", false);
		WEDDING_FORMALWEAR = events.getProperty("WeddingFormalWear", true);
		
		ALT_LOTTERY_PRIZE = events.getProperty("AltLotteryPrize", 50000);
		ALT_LOTTERY_TICKET_PRICE = events.getProperty("AltLotteryTicketPrice", 2000);
		ALT_LOTTERY_5_NUMBER_RATE = events.getProperty("AltLottery5NumberRate", 0.6);
		ALT_LOTTERY_4_NUMBER_RATE = events.getProperty("AltLottery4NumberRate", 0.2);
		ALT_LOTTERY_3_NUMBER_RATE = events.getProperty("AltLottery3NumberRate", 0.2);
		ALT_LOTTERY_2_AND_1_NUMBER_PRIZE = events.getProperty("AltLottery2and1NumberPrize", 200);
		
		ALT_FISH_CHAMPIONSHIP_ENABLED = events.getProperty("AltFishChampionshipEnabled", true);
		ALT_FISH_CHAMPIONSHIP_REWARD_ITEM = events.getProperty("AltFishChampionshipRewardItemId", 57);
		ALT_FISH_CHAMPIONSHIP_REWARD_1 = events.getProperty("AltFishChampionshipReward1", 800000);
		ALT_FISH_CHAMPIONSHIP_REWARD_2 = events.getProperty("AltFishChampionshipReward2", 500000);
		ALT_FISH_CHAMPIONSHIP_REWARD_3 = events.getProperty("AltFishChampionshipReward3", 300000);
		ALT_FISH_CHAMPIONSHIP_REWARD_4 = events.getProperty("AltFishChampionshipReward4", 200000);
		ALT_FISH_CHAMPIONSHIP_REWARD_5 = events.getProperty("AltFishChampionshipReward5", 100000);
	}
	
	/**
	 * Loads custom events settings.<br>
	 */
	private static final void loadCustomEvents()
	{
		final ExProperties events_custom = initProperties(CUSTOM_EVENTS_FILE);
		
		EVENTS_ALLOW_VOICED_COMMAND = events_custom.getProperty("EventsAllowVoicedInfoCommand", false);
		// TvT Event
		TVT_EVENT_ENABLED = events_custom.getProperty("TvTEventEnabled", false);
		TVT_EVENT_INTERVAL = events_custom.getProperty("TvTEventInterval", "20:00").split(",");
		TVT_EVENT_PARTICIPATION_TIME = events_custom.getProperty("TvTEventParticipationTime", 3600);
		TVT_EVENT_RUNNING_TIME = events_custom.getProperty("TvTEventRunningTime", 1800);
		TVT_EVENT_PARTICIPATION_NPC_ID = events_custom.getProperty("TvTEventParticipationNpcId", 0);
		
		if (TVT_EVENT_PARTICIPATION_NPC_ID == 0)
		{
			TVT_EVENT_ENABLED = false;
			_log.warning("TvTEventEngine[Config.load()]: invalid config property -> TvTEventParticipationNpcId");
		}
		else
		{
			String[] propertySplit = events_custom.getProperty("TvTEventParticipationNpcCoordinates", "0,0,0").split(",");
			if (propertySplit.length < 3)
			{
				TVT_EVENT_ENABLED = false;
				_log.warning("TvTEventEngine[Config.load()]: invalid config property -> TvTEventParticipationNpcCoordinates");
			}
			else
			{
				TVT_EVENT_REWARDS_KILL = new ArrayList<>();
				TVT_EVENT_REWARDS = new ArrayList<>();
				TVT_DOORS_IDS_TO_OPEN = new ArrayList<>();
				TVT_DOORS_IDS_TO_CLOSE = new ArrayList<>();
				TVT_EVENT_PARTICIPATION_NPC_COORDINATES = new int[4];
				TVT_EVENT_TEAM_1_COORDINATES = new int[3];
				TVT_EVENT_TEAM_2_COORDINATES = new int[3];
				TVT_EVENT_PARTICIPATION_NPC_COORDINATES[0] = Integer.parseInt(propertySplit[0]);
				TVT_EVENT_PARTICIPATION_NPC_COORDINATES[1] = Integer.parseInt(propertySplit[1]);
				TVT_EVENT_PARTICIPATION_NPC_COORDINATES[2] = Integer.parseInt(propertySplit[2]);
				if (propertySplit.length == 4)
					TVT_EVENT_PARTICIPATION_NPC_COORDINATES[3] = Integer.parseInt(propertySplit[3]);
				TVT_EVENT_MIN_PLAYERS_IN_TEAMS = events_custom.getProperty("TvTEventMinPlayersInTeams", 1);
				TVT_EVENT_MAX_PLAYERS_IN_TEAMS = events_custom.getProperty("TvTEventMaxPlayersInTeams", 2);
				TVT_EVENT_MIN_LVL = (byte) events_custom.getProperty("TvTEventMinPlayerLevel", 1);
				TVT_EVENT_MAX_LVL = (byte) events_custom.getProperty("TvTEventMaxPlayerLevel", 80);
				TVT_EVENT_RESPAWN_TELEPORT_DELAY = events_custom.getProperty("TvTEventRespawnTeleportDelay", 20);
				TVT_EVENT_START_LEAVE_TELEPORT_DELAY = events_custom.getProperty("TvTEventStartLeaveTeleportDelay", 20);
				TVT_EVENT_EFFECTS_REMOVAL = events_custom.getProperty("TvTEventEffectsRemoval", 0);
				TVT_EVENT_TEAM_1_NAME = events_custom.getProperty("TvTEventTeam1Name", "Team1");
				propertySplit = events_custom.getProperty("TvTEventTeam1Coordinates", "0,0,0").split(",");
				if (propertySplit.length < 3)
				{
					TVT_EVENT_ENABLED = false;
					_log.warning("TvTEventEngine[Config.load()]: invalid config property -> TvTEventTeam1Coordinates");
				}
				else
				{
					TVT_EVENT_TEAM_1_COORDINATES[0] = Integer.parseInt(propertySplit[0]);
					TVT_EVENT_TEAM_1_COORDINATES[1] = Integer.parseInt(propertySplit[1]);
					TVT_EVENT_TEAM_1_COORDINATES[2] = Integer.parseInt(propertySplit[2]);
					TVT_EVENT_TEAM_2_NAME = events_custom.getProperty("TvTEventTeam2Name", "Team2");
					propertySplit = events_custom.getProperty("TvTEventTeam2Coordinates", "0,0,0").split(",");
					if (propertySplit.length < 3)
					{
						TVT_EVENT_ENABLED = false;
						_log.warning("TvTEventEngine[Config.load()]: invalid config property -> TvTEventTeam2Coordinates");
					}
					else
					{
						TVT_EVENT_TEAM_2_COORDINATES[0] = Integer.parseInt(propertySplit[0]);
						TVT_EVENT_TEAM_2_COORDINATES[1] = Integer.parseInt(propertySplit[1]);
						TVT_EVENT_TEAM_2_COORDINATES[2] = Integer.parseInt(propertySplit[2]);
						propertySplit = events_custom.getProperty("TvTEventParticipationFee", "0,0").split(",");
						try
						{
							TVT_EVENT_PARTICIPATION_FEE[0] = Integer.parseInt(propertySplit[0]);
							TVT_EVENT_PARTICIPATION_FEE[1] = Integer.parseInt(propertySplit[1]);
						}
						catch (NumberFormatException nfe)
						{
							if (propertySplit.length > 0)
								_log.warning("TvTEventEngine[Config.load()]: invalid config property -> TvTEventParticipationFee");
						}
						propertySplit = events_custom.getProperty("TvTEventReward", "57,100000").split(";");
						for (String reward : propertySplit)
						{
							String[] rewardSplit = reward.split(",");
							if (rewardSplit.length != 2)
								_log.warning(StringUtil.concat("TvTEventEngine[Config.load()]: invalid config property -> TvTEventReward \"", reward, "\""));
							else
							{
								try
								{
									TVT_EVENT_REWARDS.add(new int[]
									{
										Integer.parseInt(rewardSplit[0]),
										Integer.parseInt(rewardSplit[1])
									});
								}
								catch (NumberFormatException nfe)
								{
									if (!reward.isEmpty())
										_log.warning(StringUtil.concat("TvTEventEngine[Config.load()]: invalid config property -> TvTEventReward \"", reward, "\""));
								}
							}
						}
						propertySplit = events_custom.getProperty("TvTEventRewardKill", "57,2").split(";");
						for (String reward : propertySplit)
						{
							String[] rewardSplit = reward.split(",");
							if (rewardSplit.length != 2)
								_log.warning(StringUtil.concat("TvTEventEngine[Config.load()]: invalid config property -> TvTEventRewardKill \"", reward, "\""));
							else
							{
								try
								{
									TVT_EVENT_REWARDS_KILL.add(new int[]
									{
										Integer.parseInt(rewardSplit[0]),
										Integer.parseInt(rewardSplit[1])
									});
								}
								catch (NumberFormatException nfe)
								{
									if (!reward.isEmpty())
										_log.warning(StringUtil.concat("TvTEventEngine[Config.load()]: invalid config property -> TvTEventRewardKill \"", reward, "\""));
								}
							}
						}
						
						TVT_EVENT_TARGET_TEAM_MEMBERS_ALLOWED = events_custom.getProperty("TvTEventTargetTeamMembersAllowed", true);
						TVT_EVENT_SCROLL_ALLOWED = events_custom.getProperty("TvTEventScrollsAllowed", false);
						TVT_EVENT_POTIONS_ALLOWED = events_custom.getProperty("TvTEventPotionsAllowed", false);
						TVT_EVENT_SUMMON_BY_ITEM_ALLOWED = events_custom.getProperty("TvTEventSummonByItemAllowed", false);
						TVT_REWARD_TEAM_TIE = events_custom.getProperty("TvTRewardTeamTie", false);
						propertySplit = events_custom.getProperty("TvTDoorsToOpen", "").split(";");
						for (String door : propertySplit)
						{
							try
							{
								TVT_DOORS_IDS_TO_OPEN.add(Integer.parseInt(door));
							}
							catch (NumberFormatException nfe)
							{
								if (!door.isEmpty())
									_log.warning(StringUtil.concat("TvTEventEngine[Config.load()]: invalid config property -> TvTDoorsToOpen \"", door, "\""));
							}
						}
						
						propertySplit = events_custom.getProperty("TvTDoorsToClose", "").split(";");
						for (String door : propertySplit)
						{
							try
							{
								TVT_DOORS_IDS_TO_CLOSE.add(Integer.parseInt(door));
							}
							catch (NumberFormatException nfe)
							{
								if (!door.isEmpty())
									_log.warning(StringUtil.concat("TvTEventEngine[Config.load()]: invalid config property -> TvTDoorsToClose \"", door, "\""));
							}
						}
						
						propertySplit = events_custom.getProperty("TvTEventFighterBuffs", "").split(";");
						if (!propertySplit[0].isEmpty())
						{
							TVT_EVENT_FIGHTER_BUFFS = new HashMap<>(propertySplit.length);
							for (String skill : propertySplit)
							{
								String[] skillSplit = skill.split(",");
								if (skillSplit.length != 2)
									_log.warning(StringUtil.concat("TvTEventEngine[Config.load()]: invalid config property -> TvTEventFighterBuffs \"", skill, "\""));
								else
								{
									try
									{
										TVT_EVENT_FIGHTER_BUFFS.put(Integer.parseInt(skillSplit[0]), Integer.parseInt(skillSplit[1]));
									}
									catch (NumberFormatException nfe)
									{
										if (!skill.isEmpty())
											_log.warning(StringUtil.concat("TvTEventEngine[Config.load()]: invalid config property -> TvTEventFighterBuffs \"", skill, "\""));
									}
								}
							}
						}
						
						propertySplit = events_custom.getProperty("TvTEventMageBuffs", "").split(";");
						if (!propertySplit[0].isEmpty())
						{
							TVT_EVENT_MAGE_BUFFS = new HashMap<>(propertySplit.length);
							for (String skill : propertySplit)
							{
								String[] skillSplit = skill.split(",");
								if (skillSplit.length != 2)
									_log.warning(StringUtil.concat("TvTEventEngine[Config.load()]: invalid config property -> TvTEventMageBuffs \"", skill, "\""));
								else
								{
									try
									{
										TVT_EVENT_MAGE_BUFFS.put(Integer.parseInt(skillSplit[0]), Integer.parseInt(skillSplit[1]));
									}
									catch (NumberFormatException nfe)
									{
										if (!skill.isEmpty())
											_log.warning(StringUtil.concat("TvTEventEngine[Config.load()]: invalid config property -> TvTEventMageBuffs \"", skill, "\""));
									}
								}
							}
						}
					}
				}
			}
		}
		
		// DM Event
		DM_EVENT_ENABLED = events_custom.getProperty("DMEventEnabled", false);
		DM_EVENT_INTERVAL = events_custom.getProperty("DMEventInterval", "8:00,14:00,20:00,2:00").split(",");
		DM_EVENT_PARTICIPATION_TIME = events_custom.getProperty("DMEventParticipationTime", 3600);
		DM_EVENT_RUNNING_TIME = events_custom.getProperty("DMEventRunningTime", 1800);
		DM_EVENT_PARTICIPATION_NPC_ID = events_custom.getProperty("DMEventParticipationNpcId", 0);
		DM_SHOW_TOP_RANK = events_custom.getProperty("DMShowTopRank", false);
		DM_TOP_RANK = events_custom.getProperty("DMTopRank", 10);
		if (DM_EVENT_PARTICIPATION_NPC_ID == 0)
		{
			DM_EVENT_ENABLED = false;
			_log.warning("DMEventEngine[Config.load()]: invalid config property -> DMEventParticipationNpcId");
		}
		else
		{
			String[] propertySplit = events_custom.getProperty("DMEventParticipationNpcCoordinates", "0,0,0").split(",");
			if (propertySplit.length < 3)
			{
				DM_EVENT_ENABLED = false;
				_log.warning("DMEventEngine[Config.load()]: invalid config property -> DMEventParticipationNpcCoordinates");
			}
			else
			{
				if (DM_EVENT_ENABLED)
				{
					DM_EVENT_REWARDS = new HashMap<>();
					DM_DOORS_IDS_TO_OPEN = new ArrayList<>();
					DM_DOORS_IDS_TO_CLOSE = new ArrayList<>();
					DM_EVENT_PLAYER_COORDINATES = new ArrayList<>();
					
					DM_EVENT_PARTICIPATION_NPC_COORDINATES = new int[4];
					DM_EVENT_PARTICIPATION_NPC_COORDINATES[0] = Integer.parseInt(propertySplit[0]);
					DM_EVENT_PARTICIPATION_NPC_COORDINATES[1] = Integer.parseInt(propertySplit[1]);
					DM_EVENT_PARTICIPATION_NPC_COORDINATES[2] = Integer.parseInt(propertySplit[2]);
					
					if (propertySplit.length == 4)
						DM_EVENT_PARTICIPATION_NPC_COORDINATES[3] = Integer.parseInt(propertySplit[3]);
					DM_EVENT_MIN_PLAYERS = events_custom.getProperty("DMEventMinPlayers", 1);
					DM_EVENT_MAX_PLAYERS = events_custom.getProperty("DMEventMaxPlayers", 20);
					DM_EVENT_MIN_LVL = (byte) events_custom.getProperty("DMEventMinPlayerLevel", 1);
					DM_EVENT_MAX_LVL = (byte) events_custom.getProperty("DMEventMaxPlayerLevel", 80);
					DM_EVENT_RESPAWN_TELEPORT_DELAY = events_custom.getProperty("DMEventRespawnTeleportDelay", 20);
					DM_EVENT_START_LEAVE_TELEPORT_DELAY = events_custom.getProperty("DMEventStartLeaveTeleportDelay", 20);
					DM_EVENT_EFFECTS_REMOVAL = events_custom.getProperty("DMEventEffectsRemoval", 0);
					
					propertySplit = events_custom.getProperty("DMEventParticipationFee", "0,0").split(",");
					try
					{
						DM_EVENT_PARTICIPATION_FEE[0] = Integer.parseInt(propertySplit[0]);
						DM_EVENT_PARTICIPATION_FEE[1] = Integer.parseInt(propertySplit[1]);
					}
					catch (NumberFormatException nfe)
					{
						if (propertySplit.length > 0)
							_log.warning("DMEventEngine[Config.load()]: invalid config property -> DMEventParticipationFee");
					}
					
					DM_REWARD_FIRST_PLAYERS = events_custom.getProperty("DMRewardFirstPlayers", 3);
					
					propertySplit = events_custom.getProperty("DMEventReward", "57,100000;5575,5000|57,50000|57,25000").split("\\|");
					int i = 1;
					if (DM_REWARD_FIRST_PLAYERS < propertySplit.length)
						_log.warning("DMEventEngine[Config.load()]: invalid config property -> DMRewardFirstPlayers < DMEventReward");
					else
					{
						for (String pos : propertySplit)
						{
							List<int[]> value = new ArrayList<>();
							String[] rewardSplit = pos.split("\\;");
							for (String rewards : rewardSplit)
							{
								String[] reward = rewards.split("\\,");
								if (reward.length != 2)
									System.out.println("DMEventEngine[Config.load()]: invalid config property -> DMEventReward \"" + pos + "\"");
								else
								{
									try
									{
										value.add(new int[]
										{
											Integer.parseInt(reward[0]),
											Integer.parseInt(reward[1])
										});
									}
									catch (NumberFormatException nfe)
									{
										System.out.println("DMEventEngine[Config.load()]: invalid config property -> DMEventReward \"" + pos + "\"");
									}
								}
								
								try
								{
									if (value.isEmpty())
										DM_EVENT_REWARDS.put(i, DM_EVENT_REWARDS.get(i - 1));
									else
										DM_EVENT_REWARDS.put(i, value);
								}
								catch (Exception e)
								{
									_log.warning("DMEventEngine[Config.load()]: invalid config property -> DMEventReward array index out of bounds (1)");
									e.printStackTrace();
								}
								i++;
							}
						}
						
						int countPosRewards = DM_EVENT_REWARDS.size();
						if (countPosRewards < DM_REWARD_FIRST_PLAYERS)
						{
							for (i = countPosRewards + 1; i <= DM_REWARD_FIRST_PLAYERS; i++)
							{
								try
								{
									DM_EVENT_REWARDS.put(i, DM_EVENT_REWARDS.get(i - 1));
								}
								catch (Exception e)
								{
									_log.warning("DMEventEngine[Config.load()]: invalid config property -> DMEventReward array index out of bounds (2)");
									e.printStackTrace();
								}
							}
						}
					}
					
					propertySplit = events_custom.getProperty("DMEventPlayerCoordinates", "0,0,0").split(";");
					for (String coordPlayer : propertySplit)
					{
						String[] coordSplit = coordPlayer.split(",");
						if (coordSplit.length != 3)
							System.out.println("DMEventEngine[Config.load()]: invalid config property -> DMEventPlayerCoordinates \"" + coordPlayer + "\"");
						else
						{
							try
							{
								DM_EVENT_PLAYER_COORDINATES.add(new int[]
								{
									Integer.parseInt(coordSplit[0]),
									Integer.parseInt(coordSplit[1]),
									Integer.parseInt(coordSplit[2])
								});
							}
							catch (NumberFormatException nfe)
							{
								if (!coordPlayer.isEmpty())
									System.out.println("DMEventEngine[Config.load()]: invalid config property -> DMEventPlayerCoordinates \"" + coordPlayer + "\"");
							}
						}
					}
					
					DM_EVENT_SCROLL_ALLOWED = events_custom.getProperty("DMEventScrollsAllowed", false);
					DM_EVENT_POTIONS_ALLOWED = events_custom.getProperty("DMEventPotionsAllowed", false);
					DM_EVENT_SUMMON_BY_ITEM_ALLOWED = events_custom.getProperty("DMEventSummonByItemAllowed", false);
					DM_REWARD_PLAYERS_TIE = events_custom.getProperty("DMRewardPlayersTie", false);
					DM_EVENT_HIDE_NAME = events_custom.getProperty("DMEventHideName", true);
					DM_COLOR_TITLE = Integer.decode("0x" + events_custom.getProperty("DMColorTitle", "50D6FF"));
					DM_COLOR_NAME = Integer.decode("0x" + events_custom.getProperty("DMColorName", "1509FF"));
					
					propertySplit = events_custom.getProperty("DMDoorsToOpen", "").split(";");
					for (String door : propertySplit)
					{
						try
						{
							DM_DOORS_IDS_TO_OPEN.add(Integer.parseInt(door));
						}
						catch (NumberFormatException nfe)
						{
							if (!door.isEmpty())
								System.out.println("DMEventEngine[Config.load()]: invalid config property -> DMDoorsToOpen \"" + door + "\"");
						}
					}
					
					propertySplit = events_custom.getProperty("DMDoorsToClose", "").split(";");
					for (String door : propertySplit)
					{
						try
						{
							DM_DOORS_IDS_TO_CLOSE.add(Integer.parseInt(door));
						}
						catch (NumberFormatException nfe)
						{
							if (!door.isEmpty())
								System.out.println("DMEventEngine[Config.load()]: invalid config property -> DMDoorsToClose \"" + door + "\"");
						}
					}
					
					propertySplit = events_custom.getProperty("DMEventFighterBuffs", "").split(";");
					if (!propertySplit[0].isEmpty())
					{
						DM_EVENT_FIGHTER_BUFFS = new HashMap<>(propertySplit.length);
						for (String skill : propertySplit)
						{
							String[] skillSplit = skill.split(",");
							if (skillSplit.length != 2)
								System.out.println("DMEventEngine[Config.load()]: invalid config property -> DMEventFighterBuffs \"" + skill + "\"");
							else
							{
								try
								{
									DM_EVENT_FIGHTER_BUFFS.put(Integer.parseInt(skillSplit[0]), Integer.parseInt(skillSplit[1]));
								}
								catch (NumberFormatException nfe)
								{
									if (!skill.isEmpty())
										System.out.println("DMEventEngine[Config.load()]: invalid config property -> DMEventFighterBuffs \"" + skill + "\"");
								}
							}
						}
					}
					
					propertySplit = events_custom.getProperty("DMEventMageBuffs", "").split(";");
					if (!propertySplit[0].isEmpty())
					{
						DM_EVENT_MAGE_BUFFS = new HashMap<>(propertySplit.length);
						for (String skill : propertySplit)
						{
							String[] skillSplit = skill.split(",");
							if (skillSplit.length != 2)
								System.out.println("DMEventEngine[Config.load()]: invalid config property -> DMEventMageBuffs \"" + skill + "\"");
							else
							{
								try
								{
									DM_EVENT_MAGE_BUFFS.put(Integer.parseInt(skillSplit[0]), Integer.parseInt(skillSplit[1]));
								}
								catch (NumberFormatException nfe)
								{
									if (!skill.isEmpty())
										System.out.println("DMEventEngine[Config.load()]: invalid config property -> DMEventMageBuffs \"" + skill + "\"");
								}
							}
						}
					}
				}
			}
		}
		// LM Event
		LM_EVENT_ENABLED = events_custom.getProperty("LMEventEnabled", false);
		LM_EVENT_INTERVAL = events_custom.getProperty("LMEventInterval", "8:00,14:00,20:00,2:00").split(",");
		LM_EVENT_PARTICIPATION_TIME = events_custom.getProperty("LMEventParticipationTime", 3600);
		LM_EVENT_PARTICIPATION_NPC_ID = events_custom.getProperty("LMEventParticipationNpcId", 0);
		short credits = Short.parseShort(events_custom.getProperty("LMEventPlayerCredits", "1"));
		LM_EVENT_PLAYER_CREDITS = (credits > 0 ? credits : 1);
		if (LM_EVENT_PARTICIPATION_NPC_ID == 0)
		{
			LM_EVENT_ENABLED = false;
			_log.warning("LMEventEngine[Config.load()]: invalid config property -> LMEventParticipationNpcId");
		}
		else
		{
			String[] propertySplit = events_custom.getProperty("LMEventParticipationNpcCoordinates", "0,0,0").split(",");
			if (propertySplit.length < 3)
			{
				LM_EVENT_ENABLED = false;
				_log.warning("LMEventEngine[Config.load()]: invalid config property -> LMEventParticipationNpcCoordinates");
			}
			else
			{
				if (LM_EVENT_ENABLED)
				{
					LM_EVENT_REWARDS = new ArrayList<>();
					LM_DOORS_IDS_TO_OPEN = new ArrayList<>();
					LM_DOORS_IDS_TO_CLOSE = new ArrayList<>();
					LM_EVENT_PLAYER_COORDINATES = new ArrayList<>();
					
					LM_EVENT_PARTICIPATION_NPC_COORDINATES = new int[4];
					LM_EVENT_PARTICIPATION_NPC_COORDINATES[0] = Integer.parseInt(propertySplit[0]);
					LM_EVENT_PARTICIPATION_NPC_COORDINATES[1] = Integer.parseInt(propertySplit[1]);
					LM_EVENT_PARTICIPATION_NPC_COORDINATES[2] = Integer.parseInt(propertySplit[2]);
					
					if (propertySplit.length == 4)
						LM_EVENT_PARTICIPATION_NPC_COORDINATES[3] = Integer.parseInt(propertySplit[3]);
					LM_EVENT_MIN_PLAYERS = events_custom.getProperty("LMEventMinPlayers", 1);
					LM_EVENT_MAX_PLAYERS = events_custom.getProperty("LMEventMaxPlayers", 20);
					LM_EVENT_MIN_LVL = (byte) events_custom.getProperty("LMEventMinPlayerLevel", 1);
					LM_EVENT_MAX_LVL = (byte) events_custom.getProperty("LMEventMaxPlayerLevel", 80);
					LM_EVENT_RESPAWN_TELEPORT_DELAY = events_custom.getProperty("LMEventRespawnTeleportDelay", 20);
					LM_EVENT_START_LEAVE_TELEPORT_DELAY = events_custom.getProperty("LMEventStartLeaveTeleportDelay", 20);
					LM_EVENT_EFFECTS_REMOVAL = events_custom.getProperty("LMEventEffectsRemoval", 0);
					
					propertySplit = events_custom.getProperty("LMEventParticipationFee", "0,0").split(",");
					try
					{
						LM_EVENT_PARTICIPATION_FEE[0] = Integer.parseInt(propertySplit[0]);
						LM_EVENT_PARTICIPATION_FEE[1] = Integer.parseInt(propertySplit[1]);
					}
					catch (NumberFormatException nfe)
					{
						if (propertySplit.length > 0)
							_log.warning("LMEventEngine[Config.load()]: invalid config property -> LMEventParticipationFee");
					}
					
					propertySplit = events_custom.getProperty("LMEventReward", "57,100000;5575,5000").split("\\;");
					for (String reward : propertySplit)
					{
						String[] rewardSplit = reward.split("\\,");
						try
						{
							LM_EVENT_REWARDS.add(new int[]
							{
								Integer.parseInt(rewardSplit[0]),
								Integer.parseInt(rewardSplit[1])
							});
						}
						catch (NumberFormatException nfe)
						{
							_log.warning("LMEventEngine[Config.load()]: invalid config property -> LM_EVENT_REWARDS");
						}
					}
					
					propertySplit = events_custom.getProperty("LMEventPlayerCoordinates", "0,0,0").split(";");
					for (String coordPlayer : propertySplit)
					{
						String[] coordSplit = coordPlayer.split(",");
						if (coordSplit.length != 3)
							_log.warning(StringUtil.concat("LMEventEngine[Config.load()]: invalid config property -> LMEventPlayerCoordinates \"", coordPlayer, "\""));
						else
						{
							try
							{
								LM_EVENT_PLAYER_COORDINATES.add(new int[]
								{
									Integer.parseInt(coordSplit[0]),
									Integer.parseInt(coordSplit[1]),
									Integer.parseInt(coordSplit[2])
								});
							}
							catch (NumberFormatException nfe)
							{
								if (!coordPlayer.isEmpty())
									_log.warning(StringUtil.concat("LMEventEngine[Config.load()]: invalid config property -> LMEventPlayerCoordinates \"", coordPlayer, "\""));
							}
						}
					}
					
					LM_EVENT_SCROLL_ALLOWED = events_custom.getProperty("LMEventScrollsAllowed", false);
					LM_EVENT_POTIONS_ALLOWED = events_custom.getProperty("LMEventPotionsAllowed", false);
					LM_EVENT_SUMMON_BY_ITEM_ALLOWED = events_custom.getProperty("LMEventSummonByItemAllowed", false);
					LM_REWARD_PLAYERS_TIE = events_custom.getProperty("LMRewardPlayersTie", false);
					LM_EVENT_HIDE_NAME = events_custom.getProperty("LMEventHideName", true);
					LM_COLOR_TITLE = Integer.decode("0x" + events_custom.getProperty("LMColorTitle", "50D6FF"));
					LM_COLOR_NAME = Integer.decode("0x" + events_custom.getProperty("LMColorName", "1509FF"));
					
					propertySplit = events_custom.getProperty("LMDoorsToOpen", "").split(";");
					for (String door : propertySplit)
					{
						try
						{
							LM_DOORS_IDS_TO_OPEN.add(Integer.parseInt(door));
						}
						catch (NumberFormatException nfe)
						{
							if (!door.isEmpty())
								_log.warning(StringUtil.concat("LMEventEngine[Config.load()]: invalid config property -> LMDoorsToOpen \"", door, "\""));
						}
					}
					
					propertySplit = events_custom.getProperty("LMDoorsToClose", "").split(";");
					for (String door : propertySplit)
					{
						try
						{
							LM_DOORS_IDS_TO_CLOSE.add(Integer.parseInt(door));
						}
						catch (NumberFormatException nfe)
						{
							if (!door.isEmpty())
								_log.warning(StringUtil.concat("LMEventEngine[Config.load()]: invalid config property -> LMDoorsToClose \"", door, "\""));
						}
					}
					
					propertySplit = events_custom.getProperty("LMEventFighterBuffs", "").split(";");
					if (!propertySplit[0].isEmpty())
					{
						LM_EVENT_FIGHTER_BUFFS = new HashMap<>(propertySplit.length);
						for (String skill : propertySplit)
						{
							String[] skillSplit = skill.split(",");
							if (skillSplit.length != 2)
								_log.warning(StringUtil.concat("LMEventEngine[Config.load()]: invalid config property -> LMEventFighterBuffs \"", skill, "\""));
							else
							{
								try
								{
									LM_EVENT_FIGHTER_BUFFS.put(Integer.parseInt(skillSplit[0]), Integer.parseInt(skillSplit[1]));
								}
								catch (NumberFormatException nfe)
								{
									if (!skill.isEmpty())
										_log.warning(StringUtil.concat("LMEventEngine[Config.load()]: invalid config property -> LMEventFighterBuffs \"", skill, "\""));
								}
							}
						}
					}
					
					propertySplit = events_custom.getProperty("LMEventMageBuffs", "").split(";");
					if (!propertySplit[0].isEmpty())
					{
						LM_EVENT_MAGE_BUFFS = new HashMap<>(propertySplit.length);
						for (String skill : propertySplit)
						{
							String[] skillSplit = skill.split(",");
							if (skillSplit.length != 2)
								_log.warning(StringUtil.concat("LMEventEngine[Config.load()]: invalid config property -> LMEventMageBuffs \"", skill, "\""));
							else
							{
								try
								{
									LM_EVENT_MAGE_BUFFS.put(Integer.parseInt(skillSplit[0]), Integer.parseInt(skillSplit[1]));
								}
								catch (NumberFormatException nfe)
								{
									if (!skill.isEmpty())
										_log.warning(StringUtil.concat("LMEventEngine[Config.load()]: invalid config property -> LMEventMageBuffs \"", skill, "\""));
								}
							}
						}
					}
				}
			}
		}
	}
	
	/**
	 * Loads geoengine settings.
	 */
	private static final void loadGeoengine()
	{
		final ExProperties geoengine = initProperties(GEOENGINE_FILE);
		GEODATA_PATH = geoengine.getProperty("GeoDataPath", "./data/geodata/");
		COORD_SYNCHRONIZE = geoengine.getProperty("CoordSynchronize", -1);
		
		PART_OF_CHARACTER_HEIGHT = geoengine.getProperty("PartOfCharacterHeight", 75);
		MAX_OBSTACLE_HEIGHT = geoengine.getProperty("MaxObstacleHeight", 32);
		
		PATHFINDING = geoengine.getProperty("PathFinding", true);
		PATHFIND_BUFFERS = geoengine.getProperty("PathFindBuffers", "100x6;128x6;192x6;256x4;320x4;384x4;500x2");
		BASE_WEIGHT = geoengine.getProperty("BaseWeight", 10);
		DIAGONAL_WEIGHT = geoengine.getProperty("DiagonalWeight", 14);
		OBSTACLE_MULTIPLIER = geoengine.getProperty("ObstacleMultiplier", 10);
		HEURISTIC_WEIGHT = geoengine.getProperty("HeuristicWeight", 20);
		MAX_ITERATIONS = geoengine.getProperty("MaxIterations", 3500);
		DEBUG_PATH = geoengine.getProperty("DebugPath", false);
		DEBUG_GEO_NODE = geoengine.getProperty("DebugGeoNode", false);
	}
	
	/**
	 * Loads hex ID settings.
	 */
	private static final void loadHexID()
	{
		final ExProperties hexid = initProperties(HEXID_FILE);
		SERVER_ID = Integer.parseInt(hexid.getProperty("ServerID"));
		HEX_ID = new BigInteger(hexid.getProperty("HexID"), 16).toByteArray();
	}
	
	/**
	 * Saves hex ID file.
	 * @param serverId : The ID of server.
	 * @param hexId : The hex ID of server.
	 */
	public static final void saveHexid(int serverId, String hexId)
	{
		saveHexid(serverId, hexId, HEXID_FILE);
	}
	
	/**
	 * Saves hexID file.
	 * @param serverId : The ID of server.
	 * @param hexId : The hexID of server.
	 * @param filename : The file name.
	 */
	public static final void saveHexid(int serverId, String hexId, String filename)
	{
		try
		{
			Properties hexSetting = new Properties();
			File file = new File(filename);
			file.createNewFile();
			
			OutputStream out = new FileOutputStream(file);
			hexSetting.setProperty("ServerID", String.valueOf(serverId));
			hexSetting.setProperty("HexID", hexId);
			hexSetting.store(out, "the hexID to auth into login");
			out.close();
		}
		catch (Exception e)
		{
			_log.warning("Config: Failed to save hex ID to \"" + filename + "\" file.");
			e.printStackTrace();
		}
	}
	
	/**
	 * Loads NPC settings.<br>
	 * Such as champion monsters, NPC buffer, class master, wyvern, raid bosses and grand bosses, AI.
	 */
	private static final void loadNpcs()
	{
		final ExProperties npcs = initProperties(NPCS_FILE);
		CHAMPION_FREQUENCY = npcs.getProperty("ChampionFrequency", 0);
		CHAMP_MIN_LVL = npcs.getProperty("ChampionMinLevel", 20);
		CHAMP_MAX_LVL = npcs.getProperty("ChampionMaxLevel", 70);
		CHAMPION_HP = npcs.getProperty("ChampionHp", 8);
		CHAMPION_HP_REGEN = npcs.getProperty("ChampionHpRegen", 1.);
		CHAMPION_REWARDS = npcs.getProperty("ChampionRewards", 8);
		CHAMPION_ADENAS_REWARDS = npcs.getProperty("ChampionAdenasRewards", 1);
		CHAMPION_ATK = npcs.getProperty("ChampionAtk", 1.);
		CHAMPION_SPD_ATK = npcs.getProperty("ChampionSpdAtk", 1.);
		CHAMPION_REWARD = npcs.getProperty("ChampionRewardItem", 0);
		CHAMPION_REWARD_ID = npcs.getProperty("ChampionRewardItemID", 6393);
		CHAMPION_REWARD_QTY = npcs.getProperty("ChampionRewardItemQty", 1);
		CHAMPION_ENABLE_AURA = npcs.getProperty("ChampionEnableAura", 0);
		
		BUFFER_MAX_SCHEMES = npcs.getProperty("BufferMaxSchemesPerChar", 4);
		BUFFER_STATIC_BUFF_COST = npcs.getProperty("BufferStaticCostPerBuff", -1);
		
		ALLOW_CLASS_MASTERS = npcs.getProperty("AllowClassMasters", false);
		ALLOW_ENTIRE_TREE = npcs.getProperty("AllowEntireTree", false);
		if (ALLOW_CLASS_MASTERS)
			CLASS_MASTER_SETTINGS = new ClassMasterSettings(npcs.getProperty("ConfigClassMaster"));
		
		ALTERNATE_CLASS_MASTER = npcs.getProperty("AlternateClassMaster", false);
		ANNOUNCE_MAMMON_SPAWN = npcs.getProperty("AnnounceMammonSpawn", true);
		ALT_MOB_AGRO_IN_PEACEZONE = npcs.getProperty("AltMobAgroInPeaceZone", true);
		SHOW_NPC_LVL = npcs.getProperty("ShowNpcLevel", false);
		SHOW_NPC_CREST = npcs.getProperty("ShowNpcCrest", false);
		SHOW_SUMMON_CREST = npcs.getProperty("ShowSummonCrest", false);
		
		WYVERN_ALLOW_UPGRADER = npcs.getProperty("AllowWyvernUpgrader", true);
		WYVERN_REQUIRED_LEVEL = npcs.getProperty("RequiredStriderLevel", 55);
		WYVERN_REQUIRED_CRYSTALS = npcs.getProperty("RequiredCrystalsNumber", 10);
		
		RAID_HP_REGEN_MULTIPLIER = npcs.getProperty("RaidHpRegenMultiplier", 1.);
		RAID_MP_REGEN_MULTIPLIER = npcs.getProperty("RaidMpRegenMultiplier", 1.);
		RAID_DEFENCE_MULTIPLIER = npcs.getProperty("RaidDefenceMultiplier", 1.);
		RAID_MINION_RESPAWN_TIMER = npcs.getProperty("RaidMinionRespawnTime", 300000);
		
		RAID_DISABLE_CURSE = npcs.getProperty("DisableRaidCurse", false);
		RAID_CHAOS_TIME = npcs.getProperty("RaidChaosTime", 30);
		GRAND_CHAOS_TIME = npcs.getProperty("GrandChaosTime", 30);
		MINION_CHAOS_TIME = npcs.getProperty("MinionChaosTime", 30);
		
		SPAWN_INTERVAL_AQ = npcs.getProperty("AntQueenSpawnInterval", 36);
		RANDOM_SPAWN_TIME_AQ = npcs.getProperty("AntQueenRandomSpawn", 17);
		
		SPAWN_INTERVAL_ANTHARAS = npcs.getProperty("AntharasSpawnInterval", 264);
		RANDOM_SPAWN_TIME_ANTHARAS = npcs.getProperty("AntharasRandomSpawn", 72);
		WAIT_TIME_ANTHARAS = npcs.getProperty("AntharasWaitTime", 30) * 60000;
		
		SPAWN_INTERVAL_BAIUM = npcs.getProperty("BaiumSpawnInterval", 168);
		RANDOM_SPAWN_TIME_BAIUM = npcs.getProperty("BaiumRandomSpawn", 48);
		
		SPAWN_INTERVAL_CORE = npcs.getProperty("CoreSpawnInterval", 60);
		RANDOM_SPAWN_TIME_CORE = npcs.getProperty("CoreRandomSpawn", 23);
		
		SPAWN_INTERVAL_FRINTEZZA = npcs.getProperty("FrintezzaSpawnInterval", 48);
		RANDOM_SPAWN_TIME_FRINTEZZA = npcs.getProperty("FrintezzaRandomSpawn", 8);
		WAIT_TIME_FRINTEZZA = npcs.getProperty("FrintezzaWaitTime", 10) * 60000;
		FRINTEZZA_MIN_PARTIES = npcs.getProperty("FrintezzaMinParties", 4);
		FRINTEZZA_MAX_PARTIES = npcs.getProperty("FrintezzaMaxParties", 5);
		
		SPAWN_INTERVAL_ORFEN = npcs.getProperty("OrfenSpawnInterval", 48);
		RANDOM_SPAWN_TIME_ORFEN = npcs.getProperty("OrfenRandomSpawn", 20);
		
		SPAWN_INTERVAL_SAILREN = npcs.getProperty("SailrenSpawnInterval", 36);
		RANDOM_SPAWN_TIME_SAILREN = npcs.getProperty("SailrenRandomSpawn", 24);
		WAIT_TIME_SAILREN = npcs.getProperty("SailrenWaitTime", 5) * 60000;
		
		SPAWN_INTERVAL_VALAKAS = npcs.getProperty("ValakasSpawnInterval", 264);
		RANDOM_SPAWN_TIME_VALAKAS = npcs.getProperty("ValakasRandomSpawn", 72);
		WAIT_TIME_VALAKAS = npcs.getProperty("ValakasWaitTime", 30) * 60000;
		
		SPAWN_INTERVAL_ZAKEN = npcs.getProperty("ZakenSpawnInterval", 60);
		RANDOM_SPAWN_TIME_ZAKEN = npcs.getProperty("ZakenRandomSpawn", 20);
		
		GUARD_ATTACK_AGGRO_MOB = npcs.getProperty("GuardAttackAggroMob", false);
		MAX_DRIFT_RANGE = npcs.getProperty("MaxDriftRange", 300);
		MIN_NPC_ANIMATION = npcs.getProperty("MinNPCAnimation", 20);
		MAX_NPC_ANIMATION = npcs.getProperty("MaxNPCAnimation", 40);
		MIN_MONSTER_ANIMATION = npcs.getProperty("MinMonsterAnimation", 10);
		MAX_MONSTER_ANIMATION = npcs.getProperty("MaxMonsterAnimation", 40);
	}
	
	/**
	 * Loads player settings.<br>
	 * Such as stats, inventory/warehouse, enchant, augmentation, karma, party, admin, petition, skill learn.
	 */
	private static final void loadPlayers()
	{
		final ExProperties players = initProperties(PLAYERS_FILE);
		EFFECT_CANCELING = players.getProperty("CancelLesserEffect", true);
		HP_REGEN_MULTIPLIER = players.getProperty("HpRegenMultiplier", 1.);
		MP_REGEN_MULTIPLIER = players.getProperty("MpRegenMultiplier", 1.);
		CP_REGEN_MULTIPLIER = players.getProperty("CpRegenMultiplier", 1.);
		PLAYER_SPAWN_PROTECTION = players.getProperty("PlayerSpawnProtection", 0);
		PLAYER_FAKEDEATH_UP_PROTECTION = players.getProperty("PlayerFakeDeathUpProtection", 0);
		RESPAWN_RESTORE_HP = players.getProperty("RespawnRestoreHP", 0.7);
		MAX_PVTSTORE_SLOTS_DWARF = players.getProperty("MaxPvtStoreSlotsDwarf", 5);
		MAX_PVTSTORE_SLOTS_OTHER = players.getProperty("MaxPvtStoreSlotsOther", 4);
		DEEPBLUE_DROP_RULES = players.getProperty("UseDeepBlueDropRules", true);
		ALT_GAME_DELEVEL = players.getProperty("Delevel", true);
		DEATH_PENALTY_CHANCE = players.getProperty("DeathPenaltyChance", 20);
		
		INVENTORY_MAXIMUM_NO_DWARF = players.getProperty("MaximumSlotsForNoDwarf", 80);
		INVENTORY_MAXIMUM_DWARF = players.getProperty("MaximumSlotsForDwarf", 100);
		INVENTORY_MAXIMUM_QUEST_ITEMS = players.getProperty("MaximumSlotsForQuestItems", 100);
		INVENTORY_MAXIMUM_PET = players.getProperty("MaximumSlotsForPet", 12);
		MAX_ITEM_IN_PACKET = Math.max(INVENTORY_MAXIMUM_NO_DWARF, INVENTORY_MAXIMUM_DWARF);
		ALT_WEIGHT_LIMIT = players.getProperty("AltWeightLimit", 1);
		WAREHOUSE_SLOTS_NO_DWARF = players.getProperty("MaximumWarehouseSlotsForNoDwarf", 100);
		WAREHOUSE_SLOTS_DWARF = players.getProperty("MaximumWarehouseSlotsForDwarf", 120);
		WAREHOUSE_SLOTS_CLAN = players.getProperty("MaximumWarehouseSlotsForClan", 150);
		FREIGHT_SLOTS = players.getProperty("MaximumFreightSlots", 20);
		ALT_GAME_FREIGHTS = players.getProperty("AltGameFreights", false);
		ALT_GAME_FREIGHT_PRICE = players.getProperty("AltGameFreightPrice", 1000);
		STARTING_ADENA = players.getProperty("StartingAdena", 100);
		STARTING_AA = players.getProperty("StartingAncientAdena", 0);
		
		AUGMENTATION_NG_SKILL_CHANCE = players.getProperty("AugmentationNGSkillChance", 15);
		AUGMENTATION_NG_GLOW_CHANCE = players.getProperty("AugmentationNGGlowChance", 0);
		AUGMENTATION_MID_SKILL_CHANCE = players.getProperty("AugmentationMidSkillChance", 30);
		AUGMENTATION_MID_GLOW_CHANCE = players.getProperty("AugmentationMidGlowChance", 40);
		AUGMENTATION_HIGH_SKILL_CHANCE = players.getProperty("AugmentationHighSkillChance", 45);
		AUGMENTATION_HIGH_GLOW_CHANCE = players.getProperty("AugmentationHighGlowChance", 70);
		AUGMENTATION_TOP_SKILL_CHANCE = players.getProperty("AugmentationTopSkillChance", 60);
		AUGMENTATION_TOP_GLOW_CHANCE = players.getProperty("AugmentationTopGlowChance", 100);
		AUGMENTATION_BASESTAT_CHANCE = players.getProperty("AugmentationBaseStatChance", 1);
		
		KARMA_PLAYER_CAN_BE_KILLED_IN_PZ = players.getProperty("KarmaPlayerCanBeKilledInPeaceZone", false);
		KARMA_PLAYER_CAN_SHOP = players.getProperty("KarmaPlayerCanShop", true);
		KARMA_PLAYER_CAN_USE_GK = players.getProperty("KarmaPlayerCanUseGK", false);
		KARMA_PLAYER_CAN_TELEPORT = players.getProperty("KarmaPlayerCanTeleport", true);
		KARMA_PLAYER_CAN_TRADE = players.getProperty("KarmaPlayerCanTrade", true);
		KARMA_DROP_GM = players.getProperty("CanGMDropEquipment", false);
		KARMA_AWARD_PK_KILL = players.getProperty("AwardPKKillPVPPoint", true);
		KARMA_PK_LIMIT = players.getProperty("MinimumPKRequiredToDrop", 5);
		KARMA_NONDROPPABLE_PET_ITEMS = players.getProperty("ListOfPetItems", "2375,3500,3501,3502,4422,4423,4424,4425,6648,6649,6650");
		KARMA_NONDROPPABLE_ITEMS = players.getProperty("ListOfNonDroppableItemsForPK", "1147,425,1146,461,10,2368,7,6,2370,2369");
		
		String[] array = KARMA_NONDROPPABLE_PET_ITEMS.split(",");
		KARMA_LIST_NONDROPPABLE_PET_ITEMS = new int[array.length];
		
		for (int i = 0; i < array.length; i++)
			KARMA_LIST_NONDROPPABLE_PET_ITEMS[i] = Integer.parseInt(array[i]);
		
		array = KARMA_NONDROPPABLE_ITEMS.split(",");
		KARMA_LIST_NONDROPPABLE_ITEMS = new int[array.length];
		
		for (int i = 0; i < array.length; i++)
			KARMA_LIST_NONDROPPABLE_ITEMS[i] = Integer.parseInt(array[i]);
		
		// sorting so binarySearch can be used later
		Arrays.sort(KARMA_LIST_NONDROPPABLE_PET_ITEMS);
		Arrays.sort(KARMA_LIST_NONDROPPABLE_ITEMS);
		
		PVP_NORMAL_TIME = players.getProperty("PvPVsNormalTime", 15000);
		PVP_PVP_TIME = players.getProperty("PvPVsPvPTime", 30000);
		
		PARTY_XP_CUTOFF_METHOD = players.getProperty("PartyXpCutoffMethod", "level");
		PARTY_XP_CUTOFF_PERCENT = players.getProperty("PartyXpCutoffPercent", 3.);
		PARTY_XP_CUTOFF_LEVEL = players.getProperty("PartyXpCutoffLevel", 20);
		ALT_PARTY_RANGE = players.getProperty("AltPartyRange", 1600);
		ALT_PARTY_RANGE2 = players.getProperty("AltPartyRange2", 1400);
		
		EVERYBODY_HAS_ADMIN_RIGHTS = players.getProperty("EverybodyHasAdminRights", false);
		MASTERACCESS_LEVEL = players.getProperty("MasterAccessLevel", 127);
		MASTERACCESS_NAME_COLOR = Integer.decode("0x" + players.getProperty("MasterNameColor", "00FF00"));
		MASTERACCESS_TITLE_COLOR = Integer.decode("0x" + players.getProperty("MasterTitleColor", "00FF00"));
		GM_HERO_AURA = players.getProperty("GMHeroAura", false);
		GM_STARTUP_INVULNERABLE = players.getProperty("GMStartupInvulnerable", true);
		GM_STARTUP_INVISIBLE = players.getProperty("GMStartupInvisible", true);
		GM_STARTUP_SILENCE = players.getProperty("GMStartupSilence", true);
		GM_STARTUP_AUTO_LIST = players.getProperty("GMStartupAutoList", true);
		
		PETITIONING_ALLOWED = players.getProperty("PetitioningAllowed", true);
		MAX_PETITIONS_PER_PLAYER = players.getProperty("MaxPetitionsPerPlayer", 5);
		MAX_PETITIONS_PENDING = players.getProperty("MaxPetitionsPending", 25);
		
		IS_CRAFTING_ENABLED = players.getProperty("CraftingEnabled", true);
		DWARF_RECIPE_LIMIT = players.getProperty("DwarfRecipeLimit", 50);
		COMMON_RECIPE_LIMIT = players.getProperty("CommonRecipeLimit", 50);
		ALT_BLACKSMITH_USE_RECIPES = players.getProperty("AltBlacksmithUseRecipes", true);
		
		AUTO_LEARN_SKILLS = players.getProperty("AutoLearnSkills", false);
		ALT_GAME_MAGICFAILURES = players.getProperty("MagicFailures", true);
		ALT_GAME_SHIELD_BLOCKS = players.getProperty("AltShieldBlocks", false);
		ALT_PERFECT_SHLD_BLOCK = players.getProperty("AltPerfectShieldBlockRate", 10);
		LIFE_CRYSTAL_NEEDED = players.getProperty("LifeCrystalNeeded", true);
		SP_BOOK_NEEDED = players.getProperty("SpBookNeeded", true);
		ES_SP_BOOK_NEEDED = players.getProperty("EnchantSkillSpBookNeeded", true);
		DIVINE_SP_BOOK_NEEDED = players.getProperty("DivineInspirationSpBookNeeded", true);
		ALT_GAME_SUBCLASS_WITHOUT_QUESTS = players.getProperty("AltSubClassWithoutQuests", false);
		ALT_GAME_SUBCLASS_EVERYWHERE = players.getProperty("AltSubclassEverywhere", false);
		
		BUFFS_MAX_AMOUNT = players.getProperty("MaxBuffsAmount", 20);
		STORE_SKILL_COOLTIME = players.getProperty("StoreSkillCooltime", true);
		EXPERTISE_PENALTY = players.getProperty("ExpertisePenality", true);
		
		ENABLE_MODIFY_SKILL_DURATION = players.getProperty("EnableModifySkillDuration", false);
		if (ENABLE_MODIFY_SKILL_DURATION)
		{
			SKILL_DURATION_LIST = new HashMap<>();
			
			String[] propertySplit;
			propertySplit = players.getProperty("SkillDurationList", "").split(";");
			
			for (String skill : propertySplit)
			{
				String[] skillSplit = skill.split(",");
				if (skillSplit.length != 2)
				{
					System.out.println("[SkillDurationList]: invalid config property -> SkillDurationList \"" + skill + "\"");
				}
				else
				{
					try
					{
						SKILL_DURATION_LIST.put(Integer.parseInt(skillSplit[0]), Integer.parseInt(skillSplit[1]));
					}
					catch (NumberFormatException nfe)
					{
						nfe.printStackTrace();
						
						if (!skill.equals(""))
						{
							System.out.println("[SkillDurationList]: invalid config property -> SkillList \"" + skillSplit[0] + "\"" + skillSplit[1]);
						}
					}
				}
			}
		}
		RUN_SPD_BOOST = players.getProperty("RunSpeedBoost", 0);
		MAX_RUN_SPEED = players.getProperty("MaxRunSpeed", 250);
		MAX_PCRIT_RATE = players.getProperty("MaxPCritRate", 500);
		MAX_MCRIT_RATE = players.getProperty("MaxMCritRate", 200);
		MAX_PATK_SPEED = players.getProperty("MaxPAtkSpeed", 1500);
		MAX_MATK_SPEED = players.getProperty("MaxMAtkSpeed", 1999);
		MAX_EVASION = players.getProperty("MaxEvasion", 250);
		MAX_ACCURACY = players.getProperty("MaxAccuracy", 300);
	}
	
	/**
	 * Loads siege settings.
	 */
	private static final void loadSieges()
	{
		final ExProperties sieges = initProperties(Config.SIEGE_FILE);
		
		SIEGE_LENGTH = sieges.getProperty("SiegeLength", 120);
		MINIMUM_CLAN_LEVEL = sieges.getProperty("SiegeClanMinLevel", 4);
		MAX_ATTACKERS_NUMBER = sieges.getProperty("AttackerMaxClans", 10);
		MAX_DEFENDERS_NUMBER = sieges.getProperty("DefenderMaxClans", 10);
		ATTACKERS_RESPAWN_DELAY = sieges.getProperty("AttackerRespawn", 10000);
	}
	
	/**
	 * Loads gameserver settings.<br>
	 * IP addresses, database, rates, feature enabled/disabled, misc.
	 */
	private static final void loadServer()
	{
		final ExProperties server = initProperties(SERVER_FILE);
		
		GAMESERVER_HOSTNAME = server.getProperty("GameserverHostname");
		PORT_GAME = server.getProperty("GameserverPort", 7777);
		
		HOSTNAME = server.getProperty("Hostname", "*");
		
		GAME_SERVER_LOGIN_PORT = server.getProperty("LoginPort", 9014);
		GAME_SERVER_LOGIN_HOST = server.getProperty("LoginHost", "127.0.0.1");
		
		REQUEST_ID = server.getProperty("RequestServerID", 0);
		ACCEPT_ALTERNATE_ID = server.getProperty("AcceptAlternateID", true);
		
		DATABASE_URL = server.getProperty("URL", "jdbc:mysql://localhost/acis");
		DATABASE_LOGIN = server.getProperty("Login", "root");
		DATABASE_PASSWORD = server.getProperty("Password", "");
		DATABASE_MAX_CONNECTIONS = server.getProperty("MaximumDbConnections", 10);
	
		SERVER_LIST_BRACKET = server.getProperty("ServerListBrackets", false);
		SERVER_LIST_CLOCK = server.getProperty("ServerListClock", false);
		SERVER_GMONLY = server.getProperty("ServerGMOnly", false);
		SERVER_LIST_AGE = server.getProperty("ServerListAgeLimit", 0);
 		SERVER_LIST_TESTSERVER = server.getProperty("TestServer", false);
		SERVER_LIST_PVPSERVER = server.getProperty("PvpServer", true);
		
		DELETE_DAYS = server.getProperty("DeleteCharAfterDays", 7);
		MAXIMUM_ONLINE_USERS = server.getProperty("MaximumOnlineUsers", 100);
		MIN_PROTOCOL_REVISION = server.getProperty("MinProtocolRevision", 730);
		MAX_PROTOCOL_REVISION = server.getProperty("MaxProtocolRevision", 746);
		if (MIN_PROTOCOL_REVISION > MAX_PROTOCOL_REVISION)
			throw new Error("MinProtocolRevision is bigger than MaxProtocolRevision in server.properties.");

		AUTO_LOOT = server.getProperty("AutoLoot", false);
		AUTO_LOOT_HERBS = server.getProperty("AutoLootHerbs", false);
		AUTO_LOOT_RAID = server.getProperty("AutoLootRaid", false);
		
		ALLOW_DISCARDITEM = server.getProperty("AllowDiscardItem", true);
		MULTIPLE_ITEM_DROP = server.getProperty("MultipleItemDrop", true);
		HERB_AUTO_DESTROY_TIME = server.getProperty("AutoDestroyHerbTime", 15) * 1000;
		ITEM_AUTO_DESTROY_TIME = server.getProperty("AutoDestroyItemTime", 600) * 1000;
		EQUIPABLE_ITEM_AUTO_DESTROY_TIME = server.getProperty("AutoDestroyEquipableItemTime", 0) * 1000;
		SPECIAL_ITEM_DESTROY_TIME = new HashMap<>();
		String[] data = server.getProperty("AutoDestroySpecialItemTime", (String[]) null, ",");
		if (data != null)
		{
			for (String itemData : data)
			{
				String[] item = itemData.split("-");
				SPECIAL_ITEM_DESTROY_TIME.put(Integer.parseInt(item[0]), Integer.parseInt(item[1]) * 1000);
			}
		}
		PLAYER_DROPPED_ITEM_MULTIPLIER = server.getProperty("PlayerDroppedItemMultiplier", 1);
		
		RATE_XP = server.getProperty("RateXp", 1.);
		RATE_SP = server.getProperty("RateSp", 1.);
		RATE_PARTY_XP = server.getProperty("RatePartyXp", 1.);
		RATE_PARTY_SP = server.getProperty("RatePartySp", 1.);
		RATE_DROP_ADENA = server.getProperty("RateDropAdena", 1.);
		RATE_CONSUMABLE_COST = server.getProperty("RateConsumableCost", 1.);
		RATE_DROP_ITEMS = server.getProperty("RateDropItems", 1.);
		RATE_DROP_ITEMS_BY_RAID = server.getProperty("RateRaidDropItems", 1.);
		RATE_DROP_SPOIL = server.getProperty("RateDropSpoil", 1.);
		RATE_DROP_MANOR = server.getProperty("RateDropManor", 1);
		RATE_QUEST_DROP = server.getProperty("RateQuestDrop", 1.);
		RATE_QUEST_DROP_PROF = server.getProperty("RateQuestDropProf", 1.);
		RATE_QUEST_REWARD = server.getProperty("RateQuestReward", 1.);
		RATE_QUEST_REWARD_XP = server.getProperty("RateQuestRewardXP", 1.);
		RATE_QUEST_REWARD_SP = server.getProperty("RateQuestRewardSP", 1.);
		RATE_QUEST_REWARD_ADENA = server.getProperty("RateQuestRewardAdena", 1.);
		RATE_KARMA_EXP_LOST = server.getProperty("RateKarmaExpLost", 1.);
		RATE_SIEGE_GUARDS_PRICE = server.getProperty("RateSiegeGuardsPrice", 1.);
		RATE_DROP_COMMON_HERBS = server.getProperty("RateCommonHerbs", 1.);
		RATE_DROP_HP_HERBS = server.getProperty("RateHpHerbs", 1.);
		RATE_DROP_MP_HERBS = server.getProperty("RateMpHerbs", 1.);
		RATE_DROP_SPECIAL_HERBS = server.getProperty("RateSpecialHerbs", 1.);
		PLAYER_DROP_LIMIT = server.getProperty("PlayerDropLimit", 3);
		PLAYER_RATE_DROP = server.getProperty("PlayerRateDrop", 5);
		PLAYER_RATE_DROP_ITEM = server.getProperty("PlayerRateDropItem", 70);
		PLAYER_RATE_DROP_EQUIP = server.getProperty("PlayerRateDropEquip", 25);
		PLAYER_RATE_DROP_EQUIP_WEAPON = server.getProperty("PlayerRateDropEquipWeapon", 5);
		PET_XP_RATE = server.getProperty("PetXpRate", 1.);
		PET_FOOD_RATE = server.getProperty("PetFoodRate", 1);
		SINEATER_XP_RATE = server.getProperty("SinEaterXpRate", 1.);
		KARMA_DROP_LIMIT = server.getProperty("KarmaDropLimit", 10);
		KARMA_RATE_DROP = server.getProperty("KarmaRateDrop", 70);
		KARMA_RATE_DROP_ITEM = server.getProperty("KarmaRateDropItem", 50);
		KARMA_RATE_DROP_EQUIP = server.getProperty("KarmaRateDropEquip", 40);
		KARMA_RATE_DROP_EQUIP_WEAPON = server.getProperty("KarmaRateDropEquipWeapon", 10);
		
		ALLOW_FREIGHT = server.getProperty("AllowFreight", true);
		ALLOW_WAREHOUSE = server.getProperty("AllowWarehouse", true);
		ALLOW_WEAR = server.getProperty("AllowWear", true);
		WEAR_DELAY = server.getProperty("WearDelay", 5);
		WEAR_PRICE = server.getProperty("WearPrice", 10);
		ALLOW_LOTTERY = server.getProperty("AllowLottery", true);
		ALLOW_WATER = server.getProperty("AllowWater", true);
		ALLOW_MANOR = server.getProperty("AllowManor", true);
		ALLOW_BOAT = server.getProperty("AllowBoat", true);
		ALLOW_CURSED_WEAPONS = server.getProperty("AllowCursedWeapons", true);
		
		ENABLE_FALLING_DAMAGE = server.getProperty("EnableFallingDamage", true);
		
		ALT_DEV_NO_SPAWNS = server.getProperty("NoSpawns", false);
		DEBUG = server.getProperty("Debug", false);
		DEVELOPER = server.getProperty("Developer", false);
		PACKET_HANDLER_DEBUG = server.getProperty("PacketHandlerDebug", false);
		
		DEADLOCK_DETECTOR = server.getProperty("DeadLockDetector", false);
		DEADLOCK_CHECK_INTERVAL = server.getProperty("DeadLockCheckInterval", 20);
		RESTART_ON_DEADLOCK = server.getProperty("RestartOnDeadlock", false);
		
		LOG_CHAT = server.getProperty("LogChat", false);
		LOG_ITEMS = server.getProperty("LogItems", false);
		GMAUDIT = server.getProperty("GMAudit", false);
		
		ENABLE_COMMUNITY_BOARD = server.getProperty("EnableCommunityBoard", false);
		BBS_DEFAULT = server.getProperty("BBSDefault", "_bbshome");
		
		ROLL_DICE_TIME = server.getProperty("RollDiceTime", 4200);
		HERO_VOICE_TIME = server.getProperty("HeroVoiceTime", 10000);
		SUBCLASS_TIME = server.getProperty("SubclassTime", 2000);
		DROP_ITEM_TIME = server.getProperty("DropItemTime", 1000);
		SERVER_BYPASS_TIME = server.getProperty("ServerBypassTime", 500);
		MULTISELL_TIME = server.getProperty("MultisellTime", 100);
		MANUFACTURE_TIME = server.getProperty("ManufactureTime", 300);
		MANOR_TIME = server.getProperty("ManorTime", 3000);
		SENDMAIL_TIME = server.getProperty("SendMailTime", 10000);
		CHARACTER_SELECT_TIME = server.getProperty("CharacterSelectTime", 3000);
		GLOBAL_CHAT_TIME = server.getProperty("GlobalChatTime", 0);
		TRADE_CHAT_TIME = server.getProperty("TradeChatTime", 0);
		SOCIAL_TIME = server.getProperty("SocialTime", 2000);
		
		SCHEDULED_THREAD_POOL_COUNT = server.getProperty("ScheduledThreadPoolCount", -1);
		THREADS_PER_SCHEDULED_THREAD_POOL = server.getProperty("ThreadsPerScheduledThreadPool", 4);
		INSTANT_THREAD_POOL_COUNT = server.getProperty("InstantThreadPoolCount", -1);
		THREADS_PER_INSTANT_THREAD_POOL = server.getProperty("ThreadsPerInstantThreadPool", 2);
		
		L2WALKER_PROTECTION = server.getProperty("L2WalkerProtection", false);
		BOTS_PREVENTION = server.getProperty("EnableBotsPrevention", false);
		KILLS_COUNTER = server.getProperty("KillsCounter", 60);
		KILLS_COUNTER_RANDOMIZATION = server.getProperty("KillsCounterRandomization", 50);
		VALIDATION_TIME = server.getProperty("ValidationTime", 60);
		PUNISHMENT = server.getProperty("Punishment", 0);
		PUNISHMENT_TIME = server.getProperty("PunishmentTime", 60);
		ZONE_TOWN = server.getProperty("ZoneTown", 0);
		SERVER_NEWS = server.getProperty("ShowServerNews", false);
		DISABLE_TUTORIAL = server.getProperty("DisableTutorial", false);
		ENABLED = server.getProperty("Enabled", true);
		SERVER_KEY = server.getProperty("ServerKey", 7958915);
		LOG_OPTION = server.getProperty("LogOption", "NOSPS HACK");
		MAX_SESSIONS = server.getProperty("MaxSessionsFromHWID", -1);
		MAX_PREMIUM_SESSIONS = server.getProperty("MaxSessionsForPremium", -1);
		ANNOUNCE_HACK = server.getProperty("AnnounceHackAttempt", true);
		ON_HACK_ATTEMP = server.getProperty("OnHackAttempt", "kick");
		ALLOW_GM_FROM_BANNED_HWID = server.getProperty("AllowGMFromBannedHWID", false);
		LOG_SESSIONS = server.getProperty("LogSessions", false);
	}
	
	/**
	 * Loads loginserver settings.<br>
	 * IP addresses, database, account, misc.
	 */
	private static final void loadLogin()
	{
		final ExProperties server = initProperties(LOGIN_CONFIGURATION_FILE);
		HOSTNAME = server.getProperty("Hostname", "localhost");
		
		LOGIN_BIND_ADDRESS = server.getProperty("LoginserverHostname", "*");
		PORT_LOGIN = server.getProperty("LoginserverPort", 2106);
		
		GAME_SERVER_LOGIN_HOST = server.getProperty("LoginHostname", "*");
		GAME_SERVER_LOGIN_PORT = server.getProperty("LoginPort", 9014);
		
		LOGIN_TRY_BEFORE_BAN = server.getProperty("LoginTryBeforeBan", 3);
		LOGIN_BLOCK_AFTER_BAN = server.getProperty("LoginBlockAfterBan", 600);
		ACCEPT_NEW_GAMESERVER = server.getProperty("AcceptNewGameServer", false);
		
		SHOW_LICENCE = server.getProperty("ShowLicence", true);

		DATABASE_URL = server.getProperty("URL", "jdbc:mysql://localhost/acis");
		DATABASE_LOGIN = server.getProperty("Login", "root");
		DATABASE_PASSWORD = server.getProperty("Password", "");
		DATABASE_MAX_CONNECTIONS = server.getProperty("MaximumDbConnections", 10);

		FORCE_GGAUTH = server.getProperty("ForceGGAuth", false);
		
		AUTO_CREATE_ACCOUNTS = server.getProperty("AutoCreateAccounts", true);

		LOG_LOGIN_CONTROLLER = server.getProperty("LogLoginController", false);
		
		CRYPT_TOKEN = server.getProperty("CryptToken", true);
		FLOOD_PROTECTION = server.getProperty("EnableFloodProtection", true);
		FAST_CONNECTION_LIMIT = server.getProperty("FastConnectionLimit", 15);
		NORMAL_CONNECTION_TIME = server.getProperty("NormalConnectionTime", 700);
		FAST_CONNECTION_TIME = server.getProperty("FastConnectionTime", 350);
		MAX_CONNECTION_PER_IP = server.getProperty("MaxConnectionPerIP", 50);
				
		DEBUG = server.getProperty("Debug", false);
		DEVELOPER = server.getProperty("Developer", false);
		PACKET_HANDLER_DEBUG = server.getProperty("PacketHandlerDebug", false);
	}
	
	public static final void loadGameServer()
	{
		_log.info("Loading gameserver configuration files.");
		
		// custom settings
		loadCustom();
		
		// clans settings
		loadClans();
		
		// events settings
		loadEvents();
		loadCustomEvents();
		
		// geoengine settings
		loadGeoengine();
		
		// hexID
		loadHexID();
		
		// NPCs/monsters settings
		loadNpcs();
		
		// players settings
		loadPlayers();
		
		// siege settings
		loadSieges();
		
		// server settings
		loadServer();
	}
	
	public static final void loadLoginServer()
	{
		_log.info("Loading loginserver configuration files.");
		
		// login settings
		loadLogin();
	}
	
	public static final void loadAccountManager()
	{
		_log.info("Loading account manager configuration files.");
		
		// login settings
		loadLogin();
	}
	
	public static final void loadGameServerRegistration()
	{
		_log.info("Loading gameserver registration configuration files.");
		
		// login settings
		loadLogin();
	}
	
	public static final void loadGeodataConverter()
	{
		_log.info("Loading geodata converter configuration files.");
		
		// geoengine settings
		loadGeoengine();
	}
	
	public static final class ClassMasterSettings
	{
		private final Map<Integer, Boolean> _allowedClassChange;
		private final Map<Integer, List<IntIntHolder>> _claimItems;
		private final Map<Integer, List<IntIntHolder>> _rewardItems;
		
		public ClassMasterSettings(String configLine)
		{
			_allowedClassChange = new HashMap<>(3);
			_claimItems = new HashMap<>(3);
			_rewardItems = new HashMap<>(3);
			
			if (configLine != null)
				parseConfigLine(configLine.trim());
		}
		
		private void parseConfigLine(String configLine)
		{
			StringTokenizer st = new StringTokenizer(configLine, ";");
			while (st.hasMoreTokens())
			{
				// Get allowed class change.
				int job = Integer.parseInt(st.nextToken());
				
				_allowedClassChange.put(job, true);
				
				List<IntIntHolder> items = new ArrayList<>();
				
				// Parse items needed for class change.
				if (st.hasMoreTokens())
				{
					StringTokenizer st2 = new StringTokenizer(st.nextToken(), "[],");
					while (st2.hasMoreTokens())
					{
						StringTokenizer st3 = new StringTokenizer(st2.nextToken(), "()");
						items.add(new IntIntHolder(Integer.parseInt(st3.nextToken()), Integer.parseInt(st3.nextToken())));
					}
				}
				
				// Feed the map, and clean the list.
				_claimItems.put(job, items);
				items = new ArrayList<>();
				
				// Parse gifts after class change.
				if (st.hasMoreTokens())
				{
					StringTokenizer st2 = new StringTokenizer(st.nextToken(), "[],");
					while (st2.hasMoreTokens())
					{
						StringTokenizer st3 = new StringTokenizer(st2.nextToken(), "()");
						items.add(new IntIntHolder(Integer.parseInt(st3.nextToken()), Integer.parseInt(st3.nextToken())));
					}
				}
				
				_rewardItems.put(job, items);
			}
		}
		
		public boolean isAllowed(int job)
		{
			if (_allowedClassChange == null)
				return false;
			
			if (_allowedClassChange.containsKey(job))
				return _allowedClassChange.get(job);
			
			return false;
		}
		
		public List<IntIntHolder> getRewardItems(int job)
		{
			return _rewardItems.get(job);
		}
		
		public List<IntIntHolder> getRequiredItems(int job)
		{
			return _claimItems.get(job);
		}
	}
}