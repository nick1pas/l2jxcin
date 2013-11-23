package quests.Q335_SongOfTheHunter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.xcine.gameserver.ai.CtrlIntention;
import net.xcine.gameserver.model.actor.L2Attackable;
import net.xcine.gameserver.model.actor.L2Npc;
import net.xcine.gameserver.model.actor.instance.L2PcInstance;
import net.xcine.gameserver.model.itemcontainer.Inventory;
import net.xcine.gameserver.model.quest.Quest;
import net.xcine.gameserver.model.quest.QuestState;
import net.xcine.gameserver.network.clientpackets.Say2;
import net.xcine.gameserver.network.serverpackets.CreatureSay;
import net.xcine.gameserver.util.Util;
import net.xcine.util.Rnd;

public class Q335_SongOfTheHunter  extends Quest
{
    private static final String qn = "Q335_SongOfTheHunter";
    
    // Npc
    private static final int GREY = 30744;
    private static final int TOR = 30745;
    private static final int CYBELLIN = 30746;
    
    // Neutral items
    private static final int ADENA = 57;
    
    // Quest items
    private static final int CYB_DAGGER = 3471;
    private static final int LICENSE_1 = 3692;
    private static final int LICENSE_2 = 3693;
    private static final int LEAF_PIN = 3694;
    private static final int TEST_INSTRUCTIONS_1 = 3695;
    private static final int TEST_INSTRUCTIONS_2 = 3696;
    private static final int CYB_REQ = 3697;
    
    // Blood Crystals
    private static final int BLOOD_CRYSTAL_01 = 3698;
    private static final int BLOOD_CRYSTAL_02 = 3699;
    private static final int BLOOD_CRYSTAL_03 = 3700;
    private static final int BLOOD_CRYSTAL_04 = 3701;
    private static final int BLOOD_CRYSTAL_05 = 3702;
    private static final int BLOOD_CRYSTAL_06 = 3703;
    private static final int BLOOD_CRYSTAL_07 = 3704;
    private static final int BLOOD_CRYSTAL_08 = 3705;
    private static final int BLOOD_CRYSTAL_09 = 3706;
    private static final int BLOOD_CRYSTAL_10 = 3707;
    private static final int BROKEN_BLOOD = 3708;
    
    // Drop request items
    private static final int BASILISK_SCALE = 3709;
    private static final int KARUT_WEED = 3710;
    private static final int HAKAS_HEAD = 3711;
    private static final int JAKAS_HEAD = 3712;
    private static final int MARKAS_HEAD = 3713;
    private static final int ALEPH_SKIN = 3714;
    private static final int INDIGO_RUNESTONE = 3715;
    private static final int SPORESEA_SEED = 3716;
    private static final int ORC_TOTEM = 3717;
    private static final int TRISALIM = 3718;
    private static final int AMBROSIUS_FRUIT = 3719;
    private static final int BALEFIRE_CRYSTAL = 3720;
    private static final int IMPERIAL_ARROWHEAD = 3721;
    private static final int ATHUS_HEAD = 3722;
    private static final int LANKAS_HEAD = 3723;
    private static final int TRISKAS_HEAD = 3724;
    private static final int MOTURAS_HEAD = 3725;
    private static final int KALATHS_HEAD = 3726;
    
    // 1st Request List
    private static final int REQUEST_1ST_1C = 3727;
    private static final int REQUEST_1ST_2C = 3728;
    private static final int REQUEST_1ST_3C = 3729;
    private static final int REQUEST_1ST_4C = 3730;
    private static final int REQUEST_1ST_5C = 3731;
    private static final int REQUEST_1ST_6C = 3732;
    private static final int REQUEST_1ST_7C = 3733;
    private static final int REQUEST_1ST_8C = 3734;
    private static final int REQUEST_1ST_9C = 3735;
    private static final int REQUEST_1ST_10C = 3736;
    private static final int REQUEST_1ST_11C = 3737;
    private static final int REQUEST_1ST_12C = 3738;
    private static final int REQUEST_1ST_1B = 3739;
    private static final int REQUEST_1ST_2B = 3740;
    private static final int REQUEST_1ST_3B = 3741;
    private static final int REQUEST_1ST_4B = 3742;
    private static final int REQUEST_1ST_5B = 3743;
    private static final int REQUEST_1ST_6B = 3744;
    private static final int REQUEST_1ST_1A = 3745;
    private static final int REQUEST_1ST_2A = 3746;
    private static final int REQUEST_1ST_3A = 3747;
    
    // 2nd Request List
    private static final int REQUEST_2ND_1C = 3748;
    private static final int REQUEST_2ND_2C = 3749;
    private static final int REQUEST_2ND_3C = 3750;
    private static final int REQUEST_2ND_4C = 3751;
    private static final int REQUEST_2ND_5C = 3752;
    private static final int REQUEST_2ND_6C = 3753;
    private static final int REQUEST_2ND_7C = 3754;
    private static final int REQUEST_2ND_8C = 3755;
    private static final int REQUEST_2ND_9C = 3756;
    private static final int REQUEST_2ND_10C = 3757;
    private static final int REQUEST_2ND_11C = 3758;
    private static final int REQUEST_2ND_12C = 3759;
    private static final int REQUEST_2ND_1B = 3760;
    private static final int REQUEST_2ND_2B = 3761;
    private static final int REQUEST_2ND_3B = 3762;
    private static final int REQUEST_2ND_4B = 3763;
    private static final int REQUEST_2ND_5B = 3764;
    private static final int REQUEST_2ND_6B = 3765;
    private static final int REQUEST_2ND_1A = 3766;
    private static final int REQUEST_2ND_2A = 3767;
    private static final int REQUEST_2ND_3A = 3768;
    
    // Drop collection items
    private static final int CHARM_OF_CADESH = 3769;
    private static final int TIMAK_JADE_NECKLACE = 3770;
    private static final int ENCHANTED_GOLEM_SHARD = 3771;
    private static final int GIANT_MONSTER_EYE = 3772;
    private static final int DIRE_WYRM_EGG = 3773;
    private static final int GRD_BASILISK_TALON = 3774;
    private static final int REVENANTS_CHAINS = 3775;
    private static final int WINDSUS_TUSK = 3776;
    private static final int GRANDIS_SKULL = 3777;
    private static final int TAIK_OBSIDIAN_AMULET = 3778;
    private static final int KARUL_BUGBEAR_HEAD = 3779;
    private static final int TAMLIN_IVORY_CHARM = 3780;
    private static final int FANG_OF_NARAK = 3781;
    private static final int ENCHANTED_GARGOYLE_HORN = 3782;
    private static final int COILED_SERPENT_TOTEM = 3783;
    private static final int TOTEM_OF_KADESH = 3784;
    private static final int KAIKIS_HEAD = 3785;
    private static final int KRONBE_VENOM_SAC = 3786;
    private static final int EVAS_CHARM = 3787;
    private static final int TITANS_TABLET = 3788;
    private static final int BOOK_OF_SHUNAIMAN = 3789;
    private static final int ROTTING_TREE_SPORES = 3790;
    private static final int TRISALIM_VENOM_SAC = 3791;
    private static final int TAIK_ORC_TOTEM = 3792;
    private static final int HARIT_BARBED_NECKLACE = 3793;
    private static final int COIN_OF_OLD_EMPIRE = 3794;
    private static final int SKIN_OF_FARCRAN = 3795;
    private static final int TEMPEST_SHARD = 3796;
    private static final int TSUNAMI_SHARD = 3797;
    private static final int SATYR_MANE = 3798;
    private static final int HAMADRYAD_SHARD = 3799;
    private static final int VANOR_SILENOS_MANE = 3800;
    private static final int TALK_BUGBEAR_TOTEM = 3801;
    private static final int OKUNS_HEAD = 3802;
    private static final int KAKRANS_HEAD = 3803;
    private static final int NARCISSUS_SOULSTONE = 3804;
    private static final int DEPRIVE_EYE = 3805;
    private static final int UNICORNS_HORN = 3806;
    private static final int KERUNOS_GOLD_MANE = 3807;
    private static final int SKULL_OF_EXECUTED = 3808;
    private static final int BUST_OF_TRAVIS = 3809;
    private static final int SWORD_OF_CADMUS = 3810;
    
    // Mobs
    private static final int BREKA_ORC_SHAMAN = 20269;
    private static final int BREKA_ORC_WARRIOR = 20271;
    private static final int GUARDIAN_BASILISK = 20550;
    private static final int FETTERED_SOUL = 20552;
    private static final int WINDSUS = 20553;
    private static final int GRANDIS = 20554;
    private static final int GIANT_FUNGUS = 20555;
    private static final int GIANT_MONSTEREYE = 20556;
    private static final int DIRE_WYRM = 20557;
    private static final int ROTTING_TREE = 20558;
    private static final int TRISALIM_SPIDER = 20560;
    private static final int TRISALIM_TARANTULA = 20561;
    private static final int SPORE_ZOMBIE = 20562;
    private static final int MANASHEN_GARGOYLE = 20563;
    private static final int ENCHANTED_STONE_GOLEM = 20565;
    private static final int ENCHANTED_GARGOYLE = 20567;
    private static final int TARLK_BUGBEAR_WARRIOR = 20571;
    private static final int LETO_LIZARDMAN_ARCHER = 20578;
    private static final int LETO_LIZARDMAN_SOLDIER = 20579;
    private static final int LETO_LIZARDMAN_SHAMAN = 20581;
    private static final int LETO_LIZARDMAN_OVERLORD = 20582;
    private static final int TIMAK_ORC_WARRIOR = 20586;
    private static final int TIMAK_ORC_OVERLORD = 20588;
    private static final int FLINE = 20589;
    private static final int LIELE = 20590;
    private static final int VALLEY_TREANT = 20591;
    private static final int SATYR = 20592;
    private static final int UNICORN = 20593;
    private static final int FOREST_RUNNER = 20594;
    private static final int VALLEY_TREANT_ELDER = 20597;
    private static final int SATYR_ELDER = 20598;
    private static final int UNICORN_ELDER = 20599;
    private static final int KARUL_BUGBEAR = 20600;
    private static final int TAMLIN_ORC = 20601;
    private static final int TAMLIN_ORC_ARCHER = 20602;
    private static final int KRONBE_SPIDER = 20603;
    private static final int TAIK_ORC_ARCHER = 20631;
    private static final int TAIK_ORC_WARRIOR = 20632;
    private static final int TAIK_ORC_SHAMAN = 20633;
    private static final int TAIK_ORC_CAPTAIN = 20634;
    private static final int MIRROR = 20639;
    private static final int HARIT_LIZARDMAN_GRUNT = 20641;
    private static final int HARIT_LIZARDMAN_ARCHER = 20642;
    private static final int HARIT_LIZARDMAN_WARRIOR = 20643;
    private static final int GRAVE_WANDERER = 20659;
    private static final int ARCHER_OF_GREED = 20660;
    private static final int HATAR_RATMAN_THIEF = 20661;
    private static final int HATAR_RATMAN_BOSS = 20662;
    private static final int DEPRIVE = 20664;
    private static final int FARCRAN = 20667;
    private static final int TAIRIM = 20675;
    private static final int JUDGE_OF_MARSH = 20676;
    private static final int VANOR_SILENOS_GRUNT = 20682;
    private static final int VANOR_SILENOS_SCOUT = 20683;
    private static final int VANOR_SILENOS_WARRIOR = 20684;
    private static final int VANOR_SILENOS_CHIEFTAIN = 20686;
    private static final int BREKA_OVERLORD_HAKA = 27140;
    private static final int BREKA_OVERLORD_JAKA = 27141;
    private static final int BREKA_OVERLORD_MARKA = 27142;
    private static final int WINDSUS_ALEPH = 27143;
    private static final int TARLK_RAIDER_ATHU = 27144;
    private static final int TARLK_RAIDER_LANKA = 27145;
    private static final int TARLK_RAIDER_TRISKA = 27146;
    private static final int TARLK_RAIDER_MOTURA = 27147;
    private static final int TARLK_RAIDER_KALATH = 27148;
    private static final int GREMLIN_FILCHER = 27149;
    private static final int BLACK_LEGION_STORMTROOPER = 27150;
    private static final int LETO_SHAMAN_KETZ = 27156;
    private static final int LETO_CHIEF_NARAK = 27157;
    private static final int TIMAK_RAIDER_KAIKEE = 27158;
    private static final int TIMAK_OVERLORD_OKUN = 27159;
    private static final int GOK_MAGOK = 27160;
    private static final int TAIK_OVERLORD_KAKRAN = 27161;
    private static final int HATAR_CHIEFTAIN_KUBEL = 27162;
    private static final int VANOR_ELDER_KERUNOS = 27163;
    private static final int KARUL_CHIEF_OROOTO = 27164;
    
    private static final Map<Integer, int[]> LEVEL_1 = new HashMap<>();
    {
        LEVEL_1.put(GUARDIAN_BASILISK, new int[] {BASILISK_SCALE, 40, 75});
        LEVEL_1.put(GIANT_FUNGUS, new int[] {SPORESEA_SEED, 30, 70});
        LEVEL_1.put(MANASHEN_GARGOYLE, new int[] {INDIGO_RUNESTONE, 20, 50});
        LEVEL_1.put(ENCHANTED_STONE_GOLEM, new int[] {INDIGO_RUNESTONE, 20, 50});
        LEVEL_1.put(LETO_LIZARDMAN_SHAMAN, new int[] {KARUT_WEED, 20, 50});
        LEVEL_1.put(BREKA_OVERLORD_HAKA, new int[] {HAKAS_HEAD, 1, 100});
        LEVEL_1.put(BREKA_OVERLORD_JAKA, new int[] {JAKAS_HEAD, 1, 100});
        LEVEL_1.put(BREKA_OVERLORD_MARKA, new int[] {MARKAS_HEAD, 1, 100});
        LEVEL_1.put(WINDSUS_ALEPH, new int[] {ALEPH_SKIN, 1, 100});
    };
    
    private static final Map<Integer, int[]> LEVEL_2 = new HashMap<>();
    {
        LEVEL_2.put(TIMAK_ORC_WARRIOR, new int[] {ORC_TOTEM, 20, 50});
        LEVEL_2.put(TRISALIM_TARANTULA, new int[] {TRISALIM, 20, 50});
        LEVEL_2.put(VALLEY_TREANT, new int[] {AMBROSIUS_FRUIT, 30, 100});
        LEVEL_2.put(VALLEY_TREANT_ELDER, new int[] {AMBROSIUS_FRUIT, 30, 100});
        LEVEL_2.put(TAIRIM, new int[] {BALEFIRE_CRYSTAL, 20, 50});
        LEVEL_2.put(ARCHER_OF_GREED, new int[] {IMPERIAL_ARROWHEAD, 20, 50});
        LEVEL_2.put(TARLK_RAIDER_ATHU, new int[] {ATHUS_HEAD, 1, 100});
        LEVEL_2.put(TARLK_RAIDER_LANKA, new int[] {LANKAS_HEAD, 1, 100});
        LEVEL_2.put(TARLK_RAIDER_TRISKA, new int[] {TRISKAS_HEAD, 1, 100});
        LEVEL_2.put(TARLK_RAIDER_MOTURA, new int[] {MOTURAS_HEAD, 1, 100});
        LEVEL_2.put(TARLK_RAIDER_KALATH, new int[] {KALATHS_HEAD, 1, 100});
    };
    
    private static final Map<Integer, int[]> GREY_ADVANCE_1 = new HashMap<>();
    {
        GREY_ADVANCE_1.put(BASILISK_SCALE, new int[] {40});
        GREY_ADVANCE_1.put(KARUT_WEED, new int[] {20});
        GREY_ADVANCE_1.put(ALEPH_SKIN, new int[] {1});
        GREY_ADVANCE_1.put(INDIGO_RUNESTONE, new int[] {20});
        GREY_ADVANCE_1.put(SPORESEA_SEED, new int[] {30});
        GREY_ADVANCE_1.put(HAKAS_HEAD, new int[] {1, 1});
        GREY_ADVANCE_1.put(JAKAS_HEAD, new int[] {2, 1});
        GREY_ADVANCE_1.put(MARKAS_HEAD, new int[] {3, 1});
    };
    
    private static final Map<Integer, int[]> GREY_ADVANCE_2 = new HashMap<>();
    {
        GREY_ADVANCE_2.put(ORC_TOTEM, new int[] {20});
        GREY_ADVANCE_2.put(TRISALIM, new int[] {20});
        GREY_ADVANCE_2.put(AMBROSIUS_FRUIT, new int[] {30});
        GREY_ADVANCE_2.put(BALEFIRE_CRYSTAL, new int[] {20});
        GREY_ADVANCE_2.put(IMPERIAL_ARROWHEAD, new int[] {20});
        GREY_ADVANCE_2.put(ATHUS_HEAD, new int[] {1, 1});
        GREY_ADVANCE_2.put(LANKAS_HEAD, new int[] {2, 1});
        GREY_ADVANCE_2.put(TRISKAS_HEAD, new int[] {3, 1});
        GREY_ADVANCE_2.put(MOTURAS_HEAD, new int[] {4, 1});
        GREY_ADVANCE_2.put(KALATHS_HEAD, new int[] {5, 1});
    };
    
    private static final List<Integer> LIZARDMAN = new ArrayList<>();
    {
        LIZARDMAN.add(LETO_LIZARDMAN_ARCHER);
        LIZARDMAN.add(LETO_LIZARDMAN_SOLDIER);
        LIZARDMAN.add(LETO_LIZARDMAN_SHAMAN);
        LIZARDMAN.add(LETO_LIZARDMAN_OVERLORD);
        LIZARDMAN.add(HARIT_LIZARDMAN_GRUNT);
        LIZARDMAN.add(HARIT_LIZARDMAN_ARCHER);
        LIZARDMAN.add(HARIT_LIZARDMAN_WARRIOR);
    }
    
    private static final List<Integer> BLOOD_CRYSTALS = new ArrayList<>();
    {
        BLOOD_CRYSTALS.add(BLOOD_CRYSTAL_01);
        BLOOD_CRYSTALS.add(BLOOD_CRYSTAL_02);
        BLOOD_CRYSTALS.add(BLOOD_CRYSTAL_03);
        BLOOD_CRYSTALS.add(BLOOD_CRYSTAL_04);
        BLOOD_CRYSTALS.add(BLOOD_CRYSTAL_05);
        BLOOD_CRYSTALS.add(BLOOD_CRYSTAL_06);
        BLOOD_CRYSTALS.add(BLOOD_CRYSTAL_07);
        BLOOD_CRYSTALS.add(BLOOD_CRYSTAL_08);
        BLOOD_CRYSTALS.add(BLOOD_CRYSTAL_09);
        BLOOD_CRYSTALS.add(BLOOD_CRYSTAL_10);
    }
    
    private static final Map<Integer, Integer> BLOOD_REWARDS = new HashMap<>();
    {
        BLOOD_REWARDS.put(BLOOD_CRYSTAL_02, 3400);
        BLOOD_REWARDS.put(BLOOD_CRYSTAL_03, 6800);
        BLOOD_REWARDS.put(BLOOD_CRYSTAL_04, 13600);
        BLOOD_REWARDS.put(BLOOD_CRYSTAL_05, 37200);
        BLOOD_REWARDS.put(BLOOD_CRYSTAL_06, 54400);
        BLOOD_REWARDS.put(BLOOD_CRYSTAL_07, 108800);
        BLOOD_REWARDS.put(BLOOD_CRYSTAL_08, 217600);
        BLOOD_REWARDS.put(BLOOD_CRYSTAL_09, 435200);
        BLOOD_REWARDS.put(BLOOD_CRYSTAL_10, 870400);
    };
    
    private static final Map<Integer, int[]> TOR_REQUESTS_1 = new HashMap<>();
    {
        TOR_REQUESTS_1.put(LETO_LIZARDMAN_ARCHER, new int[] {REQUEST_1ST_1C, CHARM_OF_CADESH, 1, 40, 80});
        TOR_REQUESTS_1.put(LETO_LIZARDMAN_SOLDIER, new int[] {REQUEST_1ST_1C, CHARM_OF_CADESH, 1, 40, 83});
        TOR_REQUESTS_1.put(TIMAK_ORC_WARRIOR, new int[] {REQUEST_1ST_2C, TIMAK_JADE_NECKLACE, 1, 50, 89});
        TOR_REQUESTS_1.put(TIMAK_ORC_OVERLORD, new int[] {REQUEST_1ST_2C, TIMAK_JADE_NECKLACE, 1, 50, 100});
        TOR_REQUESTS_1.put(ENCHANTED_STONE_GOLEM, new int[] {REQUEST_1ST_3C, ENCHANTED_GOLEM_SHARD, 1, 50, 100});
        TOR_REQUESTS_1.put(GIANT_MONSTEREYE, new int[] {REQUEST_1ST_4C, GIANT_MONSTER_EYE, 1, 30, 50});
        TOR_REQUESTS_1.put(DIRE_WYRM, new int[] {REQUEST_1ST_5C, DIRE_WYRM_EGG, 1, 40, 80});
        TOR_REQUESTS_1.put(GUARDIAN_BASILISK, new int[] {REQUEST_1ST_6C, GRD_BASILISK_TALON, Rnd.nextBoolean() ? 1 : 2, 100, 100});
        TOR_REQUESTS_1.put(FETTERED_SOUL, new int[] {REQUEST_1ST_7C, REVENANTS_CHAINS, 1, 50, 100});
        TOR_REQUESTS_1.put(WINDSUS, new int[] {REQUEST_1ST_8C, WINDSUS_TUSK, 1, 30, 50});
        TOR_REQUESTS_1.put(GRANDIS, new int[] {REQUEST_1ST_9C, GRANDIS_SKULL, 2, 100, 100});
        TOR_REQUESTS_1.put(TAIK_ORC_ARCHER, new int[] {REQUEST_1ST_10C, TAIK_OBSIDIAN_AMULET, 1, 50, 100});
        TOR_REQUESTS_1.put(TAIK_ORC_WARRIOR, new int[] {REQUEST_1ST_10C, TAIK_OBSIDIAN_AMULET, 1, 50, 93});
        TOR_REQUESTS_1.put(KARUL_BUGBEAR, new int[] {REQUEST_1ST_11C, KARUL_BUGBEAR_HEAD, 1, 30, 50});
        TOR_REQUESTS_1.put(TAMLIN_ORC, new int[] {REQUEST_1ST_12C, TAMLIN_IVORY_CHARM, 1, 40, 62});
        TOR_REQUESTS_1.put(TAMLIN_ORC_ARCHER, new int[] {REQUEST_1ST_12C, TAMLIN_IVORY_CHARM, 1, 40, 80});
        TOR_REQUESTS_1.put(LETO_CHIEF_NARAK, new int[] {REQUEST_1ST_1B, FANG_OF_NARAK, 1, 1, 100});
        TOR_REQUESTS_1.put(ENCHANTED_GARGOYLE, new int[] {REQUEST_1ST_2B, ENCHANTED_GARGOYLE_HORN, 1, 50, 50});
        TOR_REQUESTS_1.put(BREKA_ORC_SHAMAN, new int[] {REQUEST_1ST_3B, COILED_SERPENT_TOTEM, 1, 50, 93});
        TOR_REQUESTS_1.put(BREKA_ORC_WARRIOR, new int[] {REQUEST_1ST_3B, COILED_SERPENT_TOTEM, 1, 50, 100});
        TOR_REQUESTS_1.put(LETO_SHAMAN_KETZ, new int[] {REQUEST_1ST_4B, TOTEM_OF_KADESH, 1, 1, 100});
        TOR_REQUESTS_1.put(TIMAK_RAIDER_KAIKEE, new int[] {REQUEST_1ST_5B, KAIKIS_HEAD, 1, 1, 100});
        TOR_REQUESTS_1.put(KRONBE_SPIDER, new int[] {REQUEST_1ST_6B, KRONBE_VENOM_SAC, 1, 30, 50});
        TOR_REQUESTS_1.put(SPORE_ZOMBIE, new int[] {REQUEST_1ST_1A, EVAS_CHARM, 1, 30, 50});
        TOR_REQUESTS_1.put(GOK_MAGOK, new int[] {REQUEST_1ST_2A, TITANS_TABLET, 1, 1, 100});
        TOR_REQUESTS_1.put(KARUL_CHIEF_OROOTO, new int[] {REQUEST_1ST_3A, BOOK_OF_SHUNAIMAN, 1, 1, 100});
    };
    
    private static final Map<Integer, int[]> TOR_REQUESTS_2 = new HashMap<>();
    {
        TOR_REQUESTS_2.put(ROTTING_TREE, new int[] {REQUEST_2ND_1C, ROTTING_TREE_SPORES, 40, 67});
        TOR_REQUESTS_2.put(TRISALIM_SPIDER, new int[] {REQUEST_2ND_2C, TRISALIM_VENOM_SAC, 40, 66});
        TOR_REQUESTS_2.put(TRISALIM_TARANTULA, new int[] {REQUEST_2ND_2C, TRISALIM_VENOM_SAC, 40, 75});
        TOR_REQUESTS_2.put(TAIK_ORC_SHAMAN, new int[] {REQUEST_2ND_3C, TAIK_ORC_TOTEM, 50, 53});
        TOR_REQUESTS_2.put(TAIK_ORC_CAPTAIN, new int[] {REQUEST_2ND_3C, TAIK_ORC_TOTEM, 50, 99});
        TOR_REQUESTS_2.put(HARIT_LIZARDMAN_GRUNT, new int[] {REQUEST_2ND_4C, HARIT_BARBED_NECKLACE, 40, 88});
        TOR_REQUESTS_2.put(HARIT_LIZARDMAN_ARCHER, new int[] {REQUEST_2ND_4C, HARIT_BARBED_NECKLACE, 40, 88});
        TOR_REQUESTS_2.put(HARIT_LIZARDMAN_WARRIOR, new int[] {REQUEST_2ND_4C, HARIT_BARBED_NECKLACE, 40, 91});
        TOR_REQUESTS_2.put(HATAR_RATMAN_THIEF, new int[] {REQUEST_2ND_5C, COIN_OF_OLD_EMPIRE, 20, 50});
        TOR_REQUESTS_2.put(HATAR_RATMAN_BOSS, new int[] {REQUEST_2ND_5C, COIN_OF_OLD_EMPIRE, 20, 52});
        TOR_REQUESTS_2.put(FARCRAN, new int[] {REQUEST_2ND_6C, SKIN_OF_FARCRAN, 30, 90});
        TOR_REQUESTS_2.put(FLINE, new int[] {REQUEST_2ND_7C, TEMPEST_SHARD, 40, 49});
        TOR_REQUESTS_2.put(LIELE, new int[] {REQUEST_2ND_8C, TSUNAMI_SHARD, 40, 51});
        TOR_REQUESTS_2.put(SATYR, new int[] {REQUEST_2ND_9C, SATYR_MANE, 40, 80});
        TOR_REQUESTS_2.put(SATYR_ELDER, new int[] {REQUEST_2ND_9C, SATYR_MANE, 40, 100});
        TOR_REQUESTS_2.put(FOREST_RUNNER, new int[] {REQUEST_2ND_10C, HAMADRYAD_SHARD, 40, 64});
        TOR_REQUESTS_2.put(GREMLIN_FILCHER, new int[] {REQUEST_2ND_10C, HAMADRYAD_SHARD, 40, 50});
        TOR_REQUESTS_2.put(VANOR_SILENOS_GRUNT, new int[] {REQUEST_2ND_11C, VANOR_SILENOS_MANE, 30, 70});
        TOR_REQUESTS_2.put(VANOR_SILENOS_SCOUT, new int[] {REQUEST_2ND_11C, VANOR_SILENOS_MANE, 30, 85});
        TOR_REQUESTS_2.put(VANOR_SILENOS_WARRIOR, new int[] {REQUEST_2ND_11C, VANOR_SILENOS_MANE, 30, 90});
        TOR_REQUESTS_2.put(TARLK_BUGBEAR_WARRIOR, new int[] {REQUEST_2ND_12C, TALK_BUGBEAR_TOTEM, 30, 63});
        TOR_REQUESTS_2.put(TIMAK_OVERLORD_OKUN, new int[] {REQUEST_2ND_1B, OKUNS_HEAD, 1, 100});
        TOR_REQUESTS_2.put(TAIK_OVERLORD_KAKRAN, new int[] {REQUEST_2ND_2B, KAKRANS_HEAD, 1, 100});
        TOR_REQUESTS_2.put(MIRROR, new int[] {REQUEST_2ND_3B, NARCISSUS_SOULSTONE, 40, 86});
        TOR_REQUESTS_2.put(DEPRIVE, new int[] {REQUEST_2ND_4B, DEPRIVE_EYE, 20, 77});
        TOR_REQUESTS_2.put(UNICORN, new int[] {REQUEST_2ND_5B, UNICORNS_HORN, 20, 68});
        TOR_REQUESTS_2.put(UNICORN_ELDER, new int[] {REQUEST_2ND_5B, UNICORNS_HORN, 20, 86});
        TOR_REQUESTS_2.put(VANOR_ELDER_KERUNOS, new int[] {REQUEST_2ND_6B, KERUNOS_GOLD_MANE, 1, 100});
        TOR_REQUESTS_2.put(GRAVE_WANDERER, new int[] {REQUEST_2ND_1A, SKULL_OF_EXECUTED, 20, 73});
        TOR_REQUESTS_2.put(HATAR_CHIEFTAIN_KUBEL, new int[] {REQUEST_2ND_2A, BUST_OF_TRAVIS, 1, 100});
        TOR_REQUESTS_2.put(JUDGE_OF_MARSH, new int[] {REQUEST_2ND_3A, SWORD_OF_CADMUS, 10, 64});
    };
    
    private static final Map<Integer, int[]> TOR_REQUESTS_SPAWN = new HashMap<>();
    {
        // Level 1
        TOR_REQUESTS_SPAWN.put(LETO_LIZARDMAN_OVERLORD, new int[] {REQUEST_1ST_1B, FANG_OF_NARAK, LETO_CHIEF_NARAK});
        TOR_REQUESTS_SPAWN.put(LETO_LIZARDMAN_SHAMAN, new int[] {REQUEST_1ST_4B, TOTEM_OF_KADESH, LETO_SHAMAN_KETZ});
        TOR_REQUESTS_SPAWN.put(TIMAK_ORC_WARRIOR, new int[] {REQUEST_1ST_5B, KAIKIS_HEAD, TIMAK_RAIDER_KAIKEE});
        TOR_REQUESTS_SPAWN.put(GRANDIS, new int[] {REQUEST_1ST_2A, TITANS_TABLET, GOK_MAGOK});
        
        // Level 2
        TOR_REQUESTS_SPAWN.put(TIMAK_ORC_OVERLORD, new int[] {REQUEST_2ND_1B, OKUNS_HEAD, TIMAK_OVERLORD_OKUN});
        TOR_REQUESTS_SPAWN.put(TAIK_ORC_CAPTAIN, new int[] {REQUEST_2ND_2B, KAKRANS_HEAD, TAIK_OVERLORD_KAKRAN});
        TOR_REQUESTS_SPAWN.put(VANOR_SILENOS_CHIEFTAIN, new int[] {REQUEST_2ND_6B, KERUNOS_GOLD_MANE, VANOR_ELDER_KERUNOS});
        TOR_REQUESTS_SPAWN.put(HATAR_RATMAN_BOSS, new int[] {REQUEST_2ND_2A, BUST_OF_TRAVIS, HATAR_CHIEFTAIN_KUBEL});
    };
    
    private static final Map<Integer, int[]> FLICHER = new HashMap<>();
    {
        FLICHER.put(REQUEST_2ND_5C, new int[] {COIN_OF_OLD_EMPIRE, 20, 3});
        FLICHER.put(REQUEST_2ND_7C, new int[] {TEMPEST_SHARD, 40, 5});
        FLICHER.put(REQUEST_2ND_8C, new int[] {TSUNAMI_SHARD, 40, 5});
        FLICHER.put(REQUEST_2ND_3B, new int[] {NARCISSUS_SOULSTONE, 40, 5});
    };
    
    private static final Map<Integer, int[]> DROPLIST_2 = new HashMap<>();
    {
        DROPLIST_2.put(TIMAK_ORC_WARRIOR, new int[] {ORC_TOTEM, 20, 50});
        DROPLIST_2.put(TRISALIM_SPIDER, new int[] {TRISALIM, 20, 50});
        DROPLIST_2.put(TRISALIM_TARANTULA, new int[] {TRISALIM, 20, 50});
        DROPLIST_2.put(VALLEY_TREANT, new int[] {AMBROSIUS_FRUIT, 30, 100});
        DROPLIST_2.put(VALLEY_TREANT_ELDER, new int[] {AMBROSIUS_FRUIT, 30, 100});
        DROPLIST_2.put(TAIRIM, new int[] {BALEFIRE_CRYSTAL, 20, 50});
        DROPLIST_2.put(ARCHER_OF_GREED, new int[] {IMPERIAL_ARROWHEAD, 20, 50});
        DROPLIST_2.put(TARLK_RAIDER_ATHU, new int[] {ATHUS_HEAD, 1, 100});
        DROPLIST_2.put(TARLK_RAIDER_LANKA, new int[] {LANKAS_HEAD, 1, 100});
        DROPLIST_2.put(TARLK_RAIDER_TRISKA, new int[] {TRISKAS_HEAD, 1, 100});
        DROPLIST_2.put(TARLK_RAIDER_MOTURA, new int[] {MOTURAS_HEAD, 1, 100});
        DROPLIST_2.put(TARLK_RAIDER_KALATH, new int[] {KALATHS_HEAD, 1, 100});
    };
    
    private static final Map<Integer, int[]> TOR_REWARDS_1 = new HashMap<>();
    {
        TOR_REWARDS_1.put(REQUEST_1ST_1C, new int[] {CHARM_OF_CADESH, 40, 2090});
        TOR_REWARDS_1.put(REQUEST_1ST_2C, new int[] {TIMAK_JADE_NECKLACE, 50, 6340});
        TOR_REWARDS_1.put(REQUEST_1ST_3C, new int[] {ENCHANTED_GOLEM_SHARD, 50, 9480});
        TOR_REWARDS_1.put(REQUEST_1ST_4C, new int[] {GIANT_MONSTER_EYE, 30, 9110});
        TOR_REWARDS_1.put(REQUEST_1ST_5C, new int[] {DIRE_WYRM_EGG, 40, 8690});
        TOR_REWARDS_1.put(REQUEST_1ST_6C, new int[] {GRD_BASILISK_TALON, 100, 9480});
        TOR_REWARDS_1.put(REQUEST_1ST_7C, new int[] {REVENANTS_CHAINS, 50, 11280});
        TOR_REWARDS_1.put(REQUEST_1ST_8C, new int[] {WINDSUS_TUSK, 30, 9640});
        TOR_REWARDS_1.put(REQUEST_1ST_9C, new int[] {GRANDIS_SKULL, 100, 9180});
        TOR_REWARDS_1.put(REQUEST_1ST_10C, new int[] {TAIK_OBSIDIAN_AMULET, 50, 5160});
        TOR_REWARDS_1.put(REQUEST_1ST_11C, new int[] {KARUL_BUGBEAR_HEAD, 30, 3140});
        TOR_REWARDS_1.put(REQUEST_1ST_12C, new int[] {TAMLIN_IVORY_CHARM, 40, 3160});
        TOR_REWARDS_1.put(REQUEST_1ST_1B, new int[] {FANG_OF_NARAK, 1, 6370});
        TOR_REWARDS_1.put(REQUEST_1ST_2B, new int[] {ENCHANTED_GARGOYLE_HORN, 50, 19080});
        TOR_REWARDS_1.put(REQUEST_1ST_3B, new int[] {COILED_SERPENT_TOTEM, 50, 17730});
        TOR_REWARDS_1.put(REQUEST_1ST_4B, new int[] {TOTEM_OF_KADESH, 1, 5790});
        TOR_REWARDS_1.put(REQUEST_1ST_5B, new int[] {KAIKIS_HEAD, 1, 8560});
        TOR_REWARDS_1.put(REQUEST_1ST_6B, new int[] {KRONBE_VENOM_SAC, 30, 8320});
        TOR_REWARDS_1.put(REQUEST_1ST_1A, new int[] {EVAS_CHARM, 30, 18000});
        TOR_REWARDS_1.put(REQUEST_1ST_2A, new int[] {TITANS_TABLET, 1, 27540});
        TOR_REWARDS_1.put(REQUEST_1ST_3A, new int[] {BOOK_OF_SHUNAIMAN, 1, 20560});
    };
    
    private static final Map<Integer, int[]> TOR_REWARDS_2 = new HashMap<>();
    {
        TOR_REWARDS_2.put(REQUEST_2ND_1C, new int[] {ROTTING_TREE_SPORES, 40, 6200});
        TOR_REWARDS_2.put(REQUEST_2ND_2C, new int[] {TRISALIM_VENOM_SAC, 40, 7250});
        TOR_REWARDS_2.put(REQUEST_2ND_3C, new int[] {TAIK_ORC_TOTEM, 50, 7160});
        TOR_REWARDS_2.put(REQUEST_2ND_4C, new int[] {HARIT_BARBED_NECKLACE, 40, 6580});
        TOR_REWARDS_2.put(REQUEST_2ND_5C, new int[] {COIN_OF_OLD_EMPIRE, 20, 10100});
        TOR_REWARDS_2.put(REQUEST_2ND_6C, new int[] {SKIN_OF_FARCRAN, 30, 13000});
        TOR_REWARDS_2.put(REQUEST_2ND_7C, new int[] {TEMPEST_SHARD, 40, 7660});
        TOR_REWARDS_2.put(REQUEST_2ND_8C, new int[] {TSUNAMI_SHARD, 40, 7660});
        TOR_REWARDS_2.put(REQUEST_2ND_9C, new int[] {SATYR_MANE, 40, 11260});
        TOR_REWARDS_2.put(REQUEST_2ND_10C, new int[] {HAMADRYAD_SHARD, 40, 7000});
        TOR_REWARDS_2.put(REQUEST_2ND_11C, new int[] {VANOR_SILENOS_MANE, 30, 8810});
        TOR_REWARDS_2.put(REQUEST_2ND_12C, new int[] {TALK_BUGBEAR_TOTEM, 30, 7350});
        TOR_REWARDS_2.put(REQUEST_2ND_1B, new int[] {OKUNS_HEAD, 1, 8760});
        TOR_REWARDS_2.put(REQUEST_2ND_2B, new int[] {KAKRANS_HEAD, 1, 9380});
        TOR_REWARDS_2.put(REQUEST_2ND_3B, new int[] {NARCISSUS_SOULSTONE, 40, 17820});
        TOR_REWARDS_2.put(REQUEST_2ND_4B, new int[] {DEPRIVE_EYE, 20, 17540});
        TOR_REWARDS_2.put(REQUEST_2ND_5B, new int[] {UNICORNS_HORN, 20, 14160});
        TOR_REWARDS_2.put(REQUEST_2ND_6B, new int[] {KERUNOS_GOLD_MANE, 1, 15960});
        TOR_REWARDS_2.put(REQUEST_2ND_1A, new int[] {SKULL_OF_EXECUTED, 20, 39100});
        TOR_REWARDS_2.put(REQUEST_2ND_2A, new int[] {BUST_OF_TRAVIS, 1, 39550});
        TOR_REWARDS_2.put(REQUEST_2ND_3A, new int[] {SWORD_OF_CADMUS, 10, 41200});
    };
    
    private static final String[] TOR_MENU_1 = 
    {
        "<a action=\"bypass -h Quest Q335_SongOfTheHunter 3727\">C: 40 Totems of Kadesh</a><br>",
        "<a action=\"bypass -h Quest Q335_SongOfTheHunter 3728\">C: 50 Jade Necklaces of Timak</a><br>",
        "<a action=\"bypass -h Quest Q335_SongOfTheHunter 3729\">C: 50 Enchanted Golem Shards</a><br>",
        "<a action=\"bypass -h Quest Q335_SongOfTheHunter 3730\">C: 30 Pieces Monster Eye Meat</a><br>",
        "<a action=\"bypass -h Quest Q335_SongOfTheHunter 3731\">C: 40 Eggs of Dire Wyrm</a><br>",
        "<a action=\"bypass -h Quest Q335_SongOfTheHunter 3732\">C: 100 Claws of Guardian Basilisk</a><br>",
        "<a action=\"bypass -h Quest Q335_SongOfTheHunter 3733\">C: 50 Revenant Chains</a><br>",
        "<a action=\"bypass -h Quest Q335_SongOfTheHunter 3734\">C: 30 Windsus Tusks</a><br>",
        "<a action=\"bypass -h Quest Q335_SongOfTheHunter 3735\">C: 100 Skulls of Grandis</a><br>",
        "<a action=\"bypass -h Quest Q335_SongOfTheHunter 3736\">C: 50 Taik Obsidian Amulets</a><br>",
        "<a action=\"bypass -h Quest Q335_SongOfTheHunter 3737\">C: 30 Heads of Karul Bugbear</a><br>",
        "<a action=\"bypass -h Quest Q335_SongOfTheHunter 3738\">C: 40 Ivory Charms of Tamlin</a><br>",
        "<a action=\"bypass -h Quest Q335_SongOfTheHunter 3739\">B: Situation Preparation - Leto Chief</a><br>",
        "<a action=\"bypass -h Quest Q335_SongOfTheHunter 3740\">B: 50 Enchanted Gargoyle Horns</a><br>",
        "<a action=\"bypass -h Quest Q335_SongOfTheHunter 3741\">B: 50 Coiled Serpent Totems</a><br>",
        "<a action=\"bypass -h Quest Q335_SongOfTheHunter 3742\">B: Situation Preparation - Sorcerer Catch of Leto</a><br>",
        "<a action=\"bypass -h Quest Q335_SongOfTheHunter 3743\">B: Situation Preparation - Timak Raider Kaikee</a><br>",
        "<a action=\"bypass -h Quest Q335_SongOfTheHunter 3744\">B: 30 Kronbe Venom Sacs</a><br>",
        "<a action=\"bypass -h Quest Q335_SongOfTheHunter 3745\">A: 30 Charms of Eva</a><br>",
        "<a action=\"bypass -h Quest Q335_SongOfTheHunter 3746\">A: Titan's Tablet</a><br>",
        "<a action=\"bypass -h Quest Q335_SongOfTheHunter 3747\">A: Book of Shunaiman</a><br>"
    };
    
    private static final String[] TOR_MENU_2 = 
    {
        "<a action=\"bypass -h Quest Q335_SongOfTheHunter 3748\">C: 40 Rotted Tree Spores</a><br>",
        "<a action=\"bypass -h Quest Q335_SongOfTheHunter 3749\">C: 40 Trisalim Venom Sacs</a><br>",
        "<a action=\"bypass -h Quest Q335_SongOfTheHunter 3750\">C: 50 Totems of Taik Orc</a><br>",
        "<a action=\"bypass -h Quest Q335_SongOfTheHunter 3751\">C: 40 Harit Barbed Necklaces</a><br>",
        "<a action=\"bypass -h Quest Q335_SongOfTheHunter 3752\">C: 20 Coins of Ancient Empire</a><br>",
        "<a action=\"bypass -h Quest Q335_SongOfTheHunter 3753\">C: 30 Skins of Farkran</a><br>",
        "<a action=\"bypass -h Quest Q335_SongOfTheHunter 3754\">C: 40 Tempest Shards</a><br>",
        "<a action=\"bypass -h Quest Q335_SongOfTheHunter 3755\">C: 40 Tsunami Shards</a><br>",
        "<a action=\"bypass -h Quest Q335_SongOfTheHunter 3756\">C: 40 Satyr Manes</a><br>",
        "<a action=\"bypass -h Quest Q335_SongOfTheHunter 3757\">C: 40 hamadryad shards</a><br>",
        "<a action=\"bypass -h Quest Q335_SongOfTheHunter 3758\">C: 30 Shillien Manes</a><br>",
        "<a action=\"bypass -h Quest Q335_SongOfTheHunter 3759\">C: 30 Totems of Talk Bugbears</a><br>",
        "<a action=\"bypass -h Quest Q335_SongOfTheHunter 3760\">B: Situation Preparation - Overlord Okun of Timak</a><br>",
        "<a action=\"bypass -h Quest Q335_SongOfTheHunter 3761\">B: Situation Preparation - Overlord Kakran of Taik</a><br>",
        "<a action=\"bypass -h Quest Q335_SongOfTheHunter 3762\">B: 40 Narcissus Soulstones</a><br>",
        "<a action=\"bypass -h Quest Q335_SongOfTheHunter 3763\">B: 20 Eyes of Deprived</a><br>",
        "<a action=\"bypass -h Quest Q335_SongOfTheHunter 3764\">B: 20 Unicorn Horns</a><br>",
        "<a action=\"bypass -h Quest Q335_SongOfTheHunter 3765\">B: Kerunos's Gold Mane</a><br>",
        "<a action=\"bypass -h Quest Q335_SongOfTheHunter 3766\">A: 20 Skull of Executed</a><br>",
        "<a action=\"bypass -h Quest Q335_SongOfTheHunter 3767\">A: Recover the stolen bust of the late King Travis</a><br>",
        "<a action=\"bypass -h Quest Q335_SongOfTheHunter 3768\">A: Recover 10 swords of Cadmus</a><br>"
    };
    
    private final List<Integer> NPCS = new ArrayList<>();
    
    public Q335_SongOfTheHunter()
    {
        super(335, qn, "Song Of The Hunter");
        
        questItemIds = new int[]
        {
            CYB_DAGGER, LICENSE_1, LICENSE_2, LEAF_PIN, TEST_INSTRUCTIONS_1, TEST_INSTRUCTIONS_2, CYB_REQ,
            BASILISK_SCALE, KARUT_WEED, HAKAS_HEAD, JAKAS_HEAD, MARKAS_HEAD, ALEPH_SKIN, INDIGO_RUNESTONE, SPORESEA_SEED,
            ORC_TOTEM, TRISALIM, AMBROSIUS_FRUIT, BALEFIRE_CRYSTAL, IMPERIAL_ARROWHEAD, ATHUS_HEAD, LANKAS_HEAD, TRISKAS_HEAD, MOTURAS_HEAD, KALATHS_HEAD,
            BLOOD_CRYSTAL_01, BLOOD_CRYSTAL_02, BLOOD_CRYSTAL_03, BLOOD_CRYSTAL_04, BLOOD_CRYSTAL_05, BLOOD_CRYSTAL_06, BLOOD_CRYSTAL_07, BLOOD_CRYSTAL_08, BLOOD_CRYSTAL_09, BLOOD_CRYSTAL_10, BROKEN_BLOOD,
            REQUEST_1ST_1C, REQUEST_1ST_2C, REQUEST_1ST_3C, REQUEST_1ST_4C, REQUEST_1ST_5C, REQUEST_1ST_6C, REQUEST_1ST_7C, REQUEST_1ST_8C, REQUEST_1ST_9C, REQUEST_1ST_10C, REQUEST_1ST_11C, REQUEST_1ST_12C, REQUEST_1ST_1B, REQUEST_1ST_2B, REQUEST_1ST_3B, REQUEST_1ST_4B, REQUEST_1ST_5B, REQUEST_1ST_6B, REQUEST_1ST_1A, REQUEST_1ST_2A, REQUEST_1ST_3A,
            REQUEST_2ND_1C, REQUEST_2ND_2C, REQUEST_2ND_3C, REQUEST_2ND_4C, REQUEST_2ND_5C, REQUEST_2ND_6C, REQUEST_2ND_7C, REQUEST_2ND_8C, REQUEST_2ND_9C, REQUEST_2ND_10C, REQUEST_2ND_11C, REQUEST_2ND_12C, REQUEST_2ND_1B, REQUEST_2ND_2B, REQUEST_2ND_3B, REQUEST_2ND_4B, REQUEST_2ND_5B, REQUEST_2ND_6B, REQUEST_2ND_1A, REQUEST_2ND_2A, REQUEST_2ND_3A,
            CHARM_OF_CADESH, TIMAK_JADE_NECKLACE, ENCHANTED_GOLEM_SHARD, GIANT_MONSTER_EYE, DIRE_WYRM_EGG, GRD_BASILISK_TALON, REVENANTS_CHAINS, WINDSUS_TUSK, 
            GRANDIS_SKULL, TAIK_OBSIDIAN_AMULET, KARUL_BUGBEAR_HEAD, TAMLIN_IVORY_CHARM, FANG_OF_NARAK, ENCHANTED_GARGOYLE_HORN, COILED_SERPENT_TOTEM, TOTEM_OF_KADESH, 
            KAIKIS_HEAD, KRONBE_VENOM_SAC, EVAS_CHARM, TITANS_TABLET, BOOK_OF_SHUNAIMAN, ROTTING_TREE_SPORES, TRISALIM_VENOM_SAC, TAIK_ORC_TOTEM, HARIT_BARBED_NECKLACE, 
            COIN_OF_OLD_EMPIRE, SKIN_OF_FARCRAN, TEMPEST_SHARD, TSUNAMI_SHARD, SATYR_MANE, HAMADRYAD_SHARD, VANOR_SILENOS_MANE, TALK_BUGBEAR_TOTEM, OKUNS_HEAD, 
            KAKRANS_HEAD, NARCISSUS_SOULSTONE, DEPRIVE_EYE, UNICORNS_HORN, KERUNOS_GOLD_MANE, SKULL_OF_EXECUTED, BUST_OF_TRAVIS, SWORD_OF_CADMUS
        };
        
        addStartNpc(GREY);
        addTalkId(GREY, TOR, CYBELLIN);
        
        addKill(LEVEL_1);
        addKill(LEVEL_2);
        addKill(TOR_REQUESTS_1);
        addKill(TOR_REQUESTS_2);
        addKill(TOR_REQUESTS_SPAWN);
    }
    
    @Override
    public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
    {
        String htmltext = event;
        QuestState st = player.getQuestState(qn);
        if (st == null)
            return htmltext;
        
        if (event.equalsIgnoreCase("30744-03.htm"))
        {
            st.set("cond", "1");
            st.setState(STATE_STARTED);
            st.giveItems(TEST_INSTRUCTIONS_1, 1);
            st.playSound(QuestState.SOUND_ACCEPT);
        }
        else if (event.equalsIgnoreCase("30744-32.htm"))
        {
            if (st.getQuestItemsCount(LEAF_PIN) >= 20)
            {
                htmltext = "30744-33.htm";
                st.giveItems(ADENA, 20000);
            }
            
            st.playSound(QuestState.SOUND_FINISH);
            st.exitQuest(true);
        }
        else if (event.equalsIgnoreCase("30744-19.htm"))
        {
            if (!hasItemsLevel(st, GREY_ADVANCE_2, 1))
            {
                htmltext = "30744-18.htm";
                st.giveItems(TEST_INSTRUCTIONS_2, 1);
                st.playSound(QuestState.SOUND_ACCEPT);
            }
            
        }
        else if (event.equalsIgnoreCase("30745-03.htm"))
        {
            if (st.getQuestItemsCount(TEST_INSTRUCTIONS_2) > 0)
                htmltext = "30745-04.htm";
        }
        else if (event.equalsIgnoreCase("Tor_list_1"))
        {
            if (st.getInt("hasTask") == 0)
            {
                int _pins = st.getQuestItemsCount(LEAF_PIN);
                int _reply_0 = Rnd.get(12);
                int _reply_1 = Rnd.get(12);
                int _reply_2 = Rnd.get(12);
                int _reply_3 = Rnd.get(12);
                int _reply_4 = Rnd.get(12);
                
                if (Rnd.get(100) < 20)
                {
                    if (_pins > 0 && _pins < 4)
                    {
                        _reply_0 = Rnd.get(7) + 12;
                        _reply_2 = Rnd.get(7);
                        _reply_3 = Rnd.get(7) + 6;
                    }
                    else if (_pins >= 4)
                    {
                        _reply_0 = Rnd.get(6) + 6;
                        
                        if (Rnd.get(100) < 20)
                            _reply_1 = Rnd.get(4) + 18;
                        
                        _reply_2 = Rnd.get(6);
                        _reply_3 = Rnd.get(6) + 6;
                    }
                }
                else if (_pins >= 4)
                {
                    if (Rnd.get(100) < 20)
                        _reply_1 = Rnd.get(4) + 18;
                    
                    _reply_2 = Rnd.get(6);
                    _reply_3 = Rnd.get(6) + 6;
                }
                
                htmltext = getHtmlText("30745-57.htm")
                    .replace("%reply0%", TOR_MENU_1[_reply_0])
                    .replace("%reply1%", TOR_MENU_1[_reply_1])
                    .replace("%reply2%", TOR_MENU_1[_reply_2])
                    .replace("%reply3%", TOR_MENU_1[_reply_3])
                    .replace("%reply4%", TOR_MENU_1[_reply_4]);
            }
        }
        else if (event.equalsIgnoreCase("Tor_list_2"))
        {
            if (st.getInt("hasTask") == 0)
            {
                int _pins = st.getQuestItemsCount(LEAF_PIN);
                int _reply_0 = Rnd.get(12);
                int _reply_1 = Rnd.get(12);
                int _reply_2 = Rnd.get(12);
                int _reply_3 = Rnd.get(12);
                int _reply_4 = Rnd.get(12);
                
                if (Rnd.get(100) < 20)
                {
                    if (_pins > 0 && _pins < 4)
                    {
                        _reply_0 = Rnd.get(7) + 12;
                        _reply_2 = Rnd.get(7);
                        _reply_3 = Rnd.get(7) + 6;
                    }
                    else if (_pins >= 4)
                    {
                        _reply_0 = Rnd.get(6) + 6;
                        
                        if (Rnd.get(100) < 20)
                            _reply_1 = Rnd.get(4) + 18;
                        
                        _reply_2 = Rnd.get(6);
                        _reply_3 = Rnd.get(6) + 6;
                    }
                }
                else if (_pins >= 4)
                {
                    if (Rnd.get(100) < 20)
                        _reply_1 = Rnd.get(4) + 18;
                    
                    _reply_2 = Rnd.get(6);
                    _reply_3 = Rnd.get(6) + 6;
                }
                
                htmltext = getHtmlText("30745-57.htm")
                    .replace("%reply0%", TOR_MENU_2[_reply_0])
                    .replace("%reply1%", TOR_MENU_2[_reply_1])
                    .replace("%reply2%", TOR_MENU_2[_reply_2])
                    .replace("%reply3%", TOR_MENU_2[_reply_3])
                    .replace("%reply4%", TOR_MENU_2[_reply_4]);
            }
        }
        else if (event.equalsIgnoreCase("30745-10.htm"))
        {
            st.takeItems(LEAF_PIN, 1);
            
            for ( int i = 3727; i < 3811; i++)
                st.takeItems(i, -1);
            
            st.set("hasTask", "0");
        }
        else if (event.equalsIgnoreCase("30746-03.htm"))
        {
            boolean _sound = false;
            
            if (!st.hasQuestItems(CYB_REQ))
            {
                st.giveItems(CYB_REQ, 1);
                _sound = true;
            }
            
            if (!st.hasQuestItems(CYB_DAGGER))
            {
                st.giveItems(CYB_DAGGER, 1);
                _sound = true;
            }
            
            if (!st.hasQuestItems(BLOOD_CRYSTAL_01))
            {
                st.giveItems(BLOOD_CRYSTAL_01, 1);
                _sound = true;
            }
            
            st.takeItems(BROKEN_BLOOD, -1);
            
            if (_sound)
                st.playSound(QuestState.SOUND_MIDDLE);
        }
        else if (event.equalsIgnoreCase("30746-08.htm"))
        {
            for (int i : BLOOD_REWARDS.keySet())
            {
                if (st.hasQuestItems(i))
                {
                    st.takeItems(i, -1);
                    st.giveItems(ADENA, BLOOD_REWARDS.get(i));
                    break;
                }
            }
        }
        else if (event.equalsIgnoreCase("30746-12.htm"))
        {
            st.takeItems(BLOOD_CRYSTAL_01, -1);
            st.takeItems(CYB_REQ, -1);
            st.takeItems(CYB_DAGGER, -1);
        }
        else if (Util.isDigit(event) && event.length() == 4)
        {
            int _item = Integer.parseInt(event);
            
            st.set("hasTask", "1");
            st.giveItems(_item, 1);
            
            _item = _item - 3712;
            
            htmltext = "30745-" + _item + ".htm";
        }
        
        return htmltext;
    }
    
    @Override
    public String onTalk(L2Npc npc, L2PcInstance player)
    {
        String htmltext = getNoQuestMsg();
        QuestState st = player.getQuestState(qn);
        if (st == null)
            return htmltext;
        
        int _npc = npc.getNpcId();
        int _cond = st.getInt("cond");
        
        switch (st.getState())
        {
            case STATE_CREATED:
                if (player.getLevel() >= 35)
                    htmltext = "30744-02.htm";
                else
                    htmltext = "30744-01.htm";
                break;
                
            case STATE_STARTED:
                switch (_npc)
                {
                    case GREY:
                        if (_cond == 1)
                        {
                            if (hasItemsLevel(st, GREY_ADVANCE_2, 3))
                            {
                                htmltext = "30744-12.htm";
                                st.set("cond", "2");
                                
                                for (int i = 3709; i < 3717; i++)
                                    st.takeItems(i, -1);
                                
                                st.playSound(QuestState.SOUND_ACCEPT);
                                st.takeItems(TEST_INSTRUCTIONS_1, -1);
                                st.giveItems(LICENSE_1, 1);
                            }
                            else
                                htmltext = "30744-11.htm";
                        }
                        else if (_cond == 2)
                        {
                            if (player.getLevel() < 45)
                                htmltext = "30744-15.htm";
                            else if (st.hasQuestItems(LICENSE_1) && !st.hasQuestItems(TEST_INSTRUCTIONS_2))
                                htmltext = "30744-16.htm";
                            else if (st.hasQuestItems(TEST_INSTRUCTIONS_2))
                            {
                                if (hasItemsLevel(st, GREY_ADVANCE_2, 3))
                                {
                                    htmltext = "30744-28.htm";
                                    st.set("cond", "3");
                                    
                                    for (int i = 3717; i < 3727; i++)
                                        st.takeItems(i, -1);
                                    
                                    st.playSound(QuestState.SOUND_ACCEPT);
                                    st.takeItems(TEST_INSTRUCTIONS_2, -1);
                                    st.takeItems(LICENSE_1, -1);
                                    st.giveItems(LICENSE_2, 1);
                                }
                                else
                                    htmltext = "30744-27.htm";
                            }
                        }
                        else if (_cond == 3)
                            htmltext = "30744-29.htm";
                        break;
                        
                    case TOR:
                        if (!st.hasQuestItems(LICENSE_1) && !st.hasQuestItems(LICENSE_2))
                            htmltext = "30745-01.htm";
                        else if (st.hasQuestItems(LICENSE_1))
                        {
                            int _request = hasRequest(st, TOR_REWARDS_1);
                            
                            if (st.getInt("hasTask") == 0)
                            {
                                if (player.getLevel() >= 45)
                                {
                                    if (st.getQuestItemsCount(TEST_INSTRUCTIONS_2) > 0)
                                        htmltext = "30745-04.htm";
                                    else
                                        htmltext = "30745-05.htm";
                                }
                            }
                            else if (_request > 0)
                            {
                                htmltext = "30745-12.htm";
                                
                                int _item = TOR_REWARDS_1.get(_request)[0];
                                int _reward = TOR_REWARDS_1.get(_request)[2];
                                
                                st.set("hasTask", "0");
                                st.takeItems(_request, -1);
                                st.takeItems(_item, -1);
                                st.giveItems(LEAF_PIN, 1);
                                st.giveItems(ADENA, _reward);
                                st.playSound(QuestState.SOUND_MIDDLE);
                            }
                            else
                                htmltext = "30745-08.htm";
                        }
                        else if (st.hasQuestItems(LICENSE_2))
                        {
                            int _request = hasRequest(st, TOR_REWARDS_2);
                            
                            if (st.getInt("hasTask") == 0)
                                htmltext = "30745-06.htm";
                            else if (_request > 0)
                            {
                                htmltext = "30745-13.htm";
                                
                                int _item = TOR_REWARDS_2.get(_request)[0];
                                int _reward = TOR_REWARDS_2.get(_request)[2];
                                
                                st.set("hasTask", "0");
                                st.takeItems(_request, -1);
                                st.takeItems(_item, -1);
                                st.giveItems(LEAF_PIN, 1);
                                st.giveItems(ADENA, _reward);
                                st.playSound(QuestState.SOUND_MIDDLE);
                            }
                            else
                                htmltext = "30745-08.htm";
                        }
                        break;
                        
                    case CYBELLIN:
                        if (!st.hasQuestItems(LICENSE_1) && !st.hasQuestItems(LICENSE_2))
                            htmltext = "30746-01.htm";
                        else if (st.hasQuestItems(LICENSE_1) || st.hasQuestItems(LICENSE_2))
                        {
                            if (!st.hasQuestItems(CYB_REQ))
                                htmltext = "30746-02.htm";
                            else if (st.hasQuestItems(BLOOD_CRYSTAL_01))
                                htmltext = "30746-05.htm";
                            else if (st.hasQuestItems(BLOOD_CRYSTAL_10))
                            {
                                htmltext = "30746-07.htm";
                                st.takeItems(BLOOD_CRYSTAL_10, -1);
                                st.giveItems(ADENA, BLOOD_REWARDS.get(BLOOD_CRYSTAL_10));
                            }
                            else if (st.hasQuestItems(BROKEN_BLOOD))
                            {
                                htmltext = "30746-11.htm";
                                st.takeItems(BROKEN_BLOOD, -1);
                            }
                            else if (getBloodLevel(st) > 1 && getBloodLevel(st) < 10)
                                htmltext = "30746-06.htm";
                            else
                                htmltext = "30746-10.htm";
                        }
                        break;
                }
                break;
        }
        
        return htmltext;
    }
    
    @Override
    public String onKill(L2Npc npc, L2PcInstance player, boolean isPet)
    {
        QuestState st = checkPlayerState(player, npc, STATE_STARTED);
        if (st == null)
            return null;
        
        int _npc = npc.getNpcId();
        int _cond = st.getInt("cond");
        int _rnd = Rnd.get(100);
        
        if (_cond == 1 && st.hasQuestItems(TEST_INSTRUCTIONS_1))
        {
            if (LEVEL_1.containsKey(_npc))
            {
                int _item = LEVEL_1.get(_npc)[0];
                int _amount = LEVEL_1.get(_npc)[1];
                int _chance = LEVEL_1.get(_npc)[2];
                
                if (_rnd < _chance && st.getQuestItemsCount(_item) < _amount)
                {
                    st.giveItems(_item, 1);
                    
                    if (st.getQuestItemsCount(_item) == _amount)
                        st.playSound(QuestState.SOUND_MIDDLE);
                    else
                        st.playSound(QuestState.SOUND_ITEMGET);
                }
            }
            else if (_npc == BREKA_ORC_WARRIOR && _rnd < 10)
            {
                if (!st.hasQuestItems(HAKAS_HEAD))
                    addSpawn(BREKA_OVERLORD_HAKA, player, false, 300000, true);
                else if (!st.hasQuestItems(JAKAS_HEAD))
                    addSpawn(BREKA_OVERLORD_JAKA, player, false, 300000, true);
                else if (!st.hasQuestItems(MARKAS_HEAD))
                    addSpawn(BREKA_OVERLORD_MARKA, player, false, 300000, true);
            }
            else if (_npc == WINDSUS && !st.hasQuestItems(ALEPH_SKIN) && _rnd < 10)
                addSpawn(WINDSUS_ALEPH, player, false, 300000, true);
        }
        else if (_cond == 2)
        {
            if (st.hasQuestItems(TEST_INSTRUCTIONS_2))
            {
                if (LEVEL_2.containsKey(_npc))
                {
                    int _item = LEVEL_2.get(_npc)[0];
                    int _amount = LEVEL_2.get(_npc)[1];
                    int _chance = LEVEL_2.get(_npc)[2];
                    
                    if (_rnd < _chance && st.getQuestItemsCount(_item) < _amount)
                    {
                        st.giveItems(_item, 1);
                        
                        if (st.getQuestItemsCount(_item) == _amount)
                            st.playSound(QuestState.SOUND_MIDDLE);
                        else
                            st.playSound(QuestState.SOUND_ITEMGET);
                    }
                }
                else if (_npc == TARLK_BUGBEAR_WARRIOR && _rnd < 10)
                {
                    if (!st.hasQuestItems(ATHUS_HEAD))
                        addSpawn(TARLK_RAIDER_ATHU, player, false, 300000, true);
                    else if (!st.hasQuestItems(LANKAS_HEAD))
                        addSpawn(TARLK_RAIDER_LANKA, player, false, 300000, true);
                    else if (!st.hasQuestItems(TRISKAS_HEAD))
                        addSpawn(TARLK_RAIDER_TRISKA, player, false, 300000, true);
                    else if (!st.hasQuestItems(MOTURAS_HEAD))
                        addSpawn(TARLK_RAIDER_MOTURA, player, false, 300000, true);
                    else if (!st.hasQuestItems(KALATHS_HEAD))
                        addSpawn(TARLK_RAIDER_KALATH, player, false, 300000, true);
                }
            }
            else if (st.hasQuestItems(LICENSE_1))
            {
                if (TOR_REQUESTS_1.containsKey(_npc))
                {
                    _log.info("onKill TOR_REQUESTS_1");
                    
                    int _itemRequired = TOR_REQUESTS_1.get(_npc)[0];
                    int _itemGive = TOR_REQUESTS_1.get(_npc)[1];
                    int _itemToGiveAmount = TOR_REQUESTS_1.get(_npc)[2];
                    int _itemAmount = TOR_REQUESTS_1.get(_npc)[3];
                    int _chance = TOR_REQUESTS_1.get(_npc)[4];
                    
                    if (_rnd < _chance && st.getQuestItemsCount(_itemRequired) > 0 && st.getQuestItemsCount(_itemGive) < _itemAmount)
                    {
                        st.giveItems(_itemGive, _itemToGiveAmount);
                        
                        if (st.getQuestItemsCount(_itemGive) == _itemAmount)
                            st.playSound(QuestState.SOUND_MIDDLE);
                        else
                            st.playSound(QuestState.SOUND_ITEMGET);
                        
                        if (Rnd.nextBoolean() && (_npc >= GOK_MAGOK && _npc <= KARUL_CHIEF_OROOTO))
                        {
                            String _string = "We will destroy the legacy of the ancient empire!";
                            
                            spawnedAggro(player, _string, BLACK_LEGION_STORMTROOPER, true);
                            spawnedAggro(player, _string, BLACK_LEGION_STORMTROOPER, true);
                        }
                    }
                }
            }
        }
        else if (_cond == 3)
        {
            if (st.hasQuestItems(LICENSE_2))
            {
                if (TOR_REQUESTS_2.containsKey(_npc))
                {
                    int _itemRequired = TOR_REQUESTS_2.get(_npc)[0];
                    int _itemGive = TOR_REQUESTS_2.get(_npc)[1];
                    int _itemAmount = TOR_REQUESTS_2.get(_npc)[2];
                    int _chance = TOR_REQUESTS_2.get(_npc)[3];
                    
                    if (st.getQuestItemsCount(_itemRequired) > 0 && st.getQuestItemsCount(_itemGive) < _itemAmount)
                    {
                        if (_rnd < _chance)
                        {
                            st.giveItems(_itemGive, 1);
                            
                            if (st.getQuestItemsCount(_itemGive) == _itemAmount)
                                st.playSound(QuestState.SOUND_MIDDLE);
                            else
                                st.playSound(QuestState.SOUND_ITEMGET);
                            
                            if (Rnd.nextBoolean() && _npc == HATAR_CHIEFTAIN_KUBEL)
                            {
                                String _string = "We'll take the property of the ancient empire!";
                                
                                spawnedAggro(player, _string, BLACK_LEGION_STORMTROOPER, true);
                                spawnedAggro(player, _string, BLACK_LEGION_STORMTROOPER, true);
                            }
                        }
                        if (_rnd < 10 && (_npc == HATAR_RATMAN_THIEF || _npc == HATAR_RATMAN_BOSS || _npc == FLINE || _npc == LIELE || _npc == MIRROR))
                            spawnedAggro(player, "Show me the pretty sparkling things! They're all mine!", GREMLIN_FILCHER, true);
                    }
                }
                else if (_npc == GREMLIN_FILCHER)
                {
                    int _request = 0;
                    
                    for (int[] i : FLICHER.values())
                        if (st.getQuestItemsCount(FLICHER.get(i)[0]) > 0)
                            _request = FLICHER.get(i)[0];
                    
                    if (_request > 0)
                    {
                        int _item = FLICHER.get(_request)[0];
                        int _amount = FLICHER.get(_request)[1];
                        int _bonus = FLICHER.get(_request)[2];
                        
                        if (st.getQuestItemsCount(_item) < _amount)
                        {
                            st.giveItems(_item, _bonus);
                            
                            if (st.getQuestItemsCount(_item) == _amount)
                                st.playSound(QuestState.SOUND_MIDDLE);
                            else
                                st.playSound(QuestState.SOUND_ITEMGET);
                            
                            autoChat(npc, "Pretty good!");
                        }
                    }
                }
            }
        }
        
        if (LIZARDMAN.contains(_npc) && (st.getItemEquipped(Inventory.PAPERDOLL_RHAND) == CYB_DAGGER) && st.hasQuestItems(CYB_REQ) && !st.hasQuestItems(BROKEN_BLOOD) && _cond >= 2)
        {
            if (Rnd.nextBoolean())
            {
                for (int i : BLOOD_CRYSTALS)
                {
                    if (st.getQuestItemsCount(i) > 0)
                    {
                        st.takeItems(i, -1);
                        st.giveItems(i + 1, 1);
                        
                        if (i >= BLOOD_CRYSTAL_06)
                            st.playSound(QuestState.SOUND_JACKPOT);
                        
                        break;
                    }	
                }
            }
            else
            {
                for (int i : BLOOD_CRYSTALS)
                    st.takeItems(i, -1);
                
                st.giveItems(BROKEN_BLOOD, 1);
            }
        }
        
        if (_rnd < 10 && TOR_REQUESTS_SPAWN.containsKey(_npc))
        {
            int _item1 = TOR_REQUESTS_SPAWN.get(_npc)[0];
            int _item2 = TOR_REQUESTS_SPAWN.get(_npc)[1];
            int _npcId = TOR_REQUESTS_SPAWN.get(_npc)[2];
            
            if (st.hasQuestItems(_item1) && !st.hasQuestItems(_item2))
                spawnedAggro(player, "", _npcId, false);
        }
        
        return null;
    }
    
    public boolean hasItemsLevel(QuestState st, Map<Integer, int[]> array, int check)
    {
        int _count = 0;
        int _massive = 0;
        int _valid = 0;
        
        for (Integer i : array.keySet())
        {
            if (array.get(i).length > 1)
            {
                _massive += 1;
                
                if (st.getQuestItemsCount(i) >= array.get(i)[1])
                    _valid += 1;
            }
            else if (st.getQuestItemsCount(i) >= array.get(i)[0])
                _count += 1;
        }
        
        if (_massive > 1 && _massive == _valid)
            _count += 1;
        
        if (_count >= check)
            return true;
        
        return false;
    }
    
    public int hasRequest(QuestState st, Map<Integer, int[]> _rewards)
    {
        for (int i : _rewards.keySet())
            if (st.hasQuestItems(i))
                if (st.getQuestItemsCount(_rewards.get(i)[0]) >= _rewards.get(i)[1])
                    return i;
        
        return 0;
    }
    
    public int getBloodLevel(QuestState st)
    {
        int _level = 1;
        
        for (int i : BLOOD_CRYSTALS)
        {
            if (st.hasQuestItems(i))
                return _level;
            
            _level += 1;
        }
        
        return -1;
    }
    
    public void addKill(Map<Integer, int[]> map)
    {
        for (Integer i : map.keySet())
        {
            if (!NPCS.contains(i))
            {
                addKillId(i);
                NPCS.add(i);
            }
        }
    }
    
    public void spawnedAggro(L2PcInstance player, String string, int npcId, boolean say)
    {
        L2Npc _tmp = addSpawn(npcId, player, false, 300000, true);
        
        ((L2Attackable) _tmp).addDamageHate(player, 0, 99999);
        _tmp.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, player, null);
        
        if (say)
            autoChat(_tmp, string);
    }
    
    public static void autoChat(L2Npc npc, String text)
    {
        npc.broadcastPacket(new CreatureSay(npc.getObjectId(), Say2.ALL, npc.getName(), text));
    }
    
    public static void main(String[] args)
    {
        new Q335_SongOfTheHunter();
    }
}