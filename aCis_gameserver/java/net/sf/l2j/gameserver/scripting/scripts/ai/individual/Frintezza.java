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
package net.sf.l2j.gameserver.scripting.scripts.ai.individual;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sf.l2j.commons.random.Rnd;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.ai.CtrlIntention;
import net.sf.l2j.gameserver.datatables.DoorTable;
import net.sf.l2j.gameserver.datatables.SkillTable;
import net.sf.l2j.gameserver.instancemanager.GrandBossManager;
import net.sf.l2j.gameserver.instancemanager.ZoneManager;
import net.sf.l2j.gameserver.model.L2CommandChannel;
import net.sf.l2j.gameserver.model.L2Party;
import net.sf.l2j.gameserver.model.L2Skill;
import net.sf.l2j.gameserver.model.actor.L2Attackable;
import net.sf.l2j.gameserver.model.actor.L2Character;
import net.sf.l2j.gameserver.model.actor.L2Npc;
import net.sf.l2j.gameserver.model.actor.instance.L2GrandBossInstance;
import net.sf.l2j.gameserver.model.actor.instance.L2MonsterInstance;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.zone.type.L2BossZone;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.network.clientpackets.Say2;
import net.sf.l2j.gameserver.network.serverpackets.AbstractNpcInfo.NpcInfo;
import net.sf.l2j.gameserver.network.serverpackets.CreatureSay;
import net.sf.l2j.gameserver.network.serverpackets.Earthquake;
import net.sf.l2j.gameserver.network.serverpackets.ExShowScreenMessage;
import net.sf.l2j.gameserver.network.serverpackets.MagicSkillCanceld;
import net.sf.l2j.gameserver.network.serverpackets.MagicSkillUse;
import net.sf.l2j.gameserver.network.serverpackets.NpcSay;
import net.sf.l2j.gameserver.network.serverpackets.PlaySound;
import net.sf.l2j.gameserver.network.serverpackets.SocialAction;
import net.sf.l2j.gameserver.network.serverpackets.SpecialCamera;
import net.sf.l2j.gameserver.network.serverpackets.SystemMessage;
import net.sf.l2j.gameserver.scripting.EventType;
import net.sf.l2j.gameserver.scripting.scripts.ai.L2AttackableAIScript;
import net.sf.l2j.gameserver.skills.AbnormalEffect;
import net.sf.l2j.gameserver.templates.StatsSet;

/**
 * @Addapted Reborn12, Dandilo
 * @Rework by Leonardo Holanda (BossForever)
 */
public class Frintezza extends L2AttackableAIScript
{
	// Zone
	private static final L2BossZone FRINTEZZA_LAIR = ZoneManager.getInstance().getZoneById(110011, L2BossZone.class);
	
	 // NPCs and Boss
	private static final int HALL_ALARM_DEVICE = 18328;
	private static final int HALL_KEEPER_CAPTAIN = 18329;
	private static final int HALL_KEEPER_WIZARD = 18330;
	private static final int HALL_KEEPER_GUARD = 18331;
	private static final int HALL_KEEPER_PATROL = 18332;
	private static final int HALL_KEEPER_SUICIDAL_SOLDIER = 18333;
	private static final int DARK_CHOIR_CAPTAIN = 18334;
	private static final int DARK_CHOIR_PRIMA_DONNA = 18335;
	private static final int DARK_CHOIR_LANCER = 18336;
	private static final int DARK_CHOIR_ARCHER = 18337;
	private static final int DARK_CHOIR_WITCH_DOCTOR = 18338;
	private static final int DARK_CHOIR_PLAYER = 18339;
	private static final int FRINTEZZA = 29045;
	private static final int SCARLET1 = 29046;
	private static final int SCARLET2 = 29047;
	private static final int EVIL_SPIRIT = 29048;
	private static final int EVIL_SPIRIT2 = 29049;
	private static final int BREATH_OF_HALISHA = 29050;
	private static final int BREATH_OF_HALISHA2 = 29051;
	private static final int CUBE = 29061;
	private static final int GUIDE = 32011;
		
	// Skills
	private static final int DAEMON_CHARGE = 5015;
	private static final int YOKE_OF_SCARLET = 5016;
	private static final int DAEMON_MORPH = 5017;
	private static final int DAEMON_FIELD = 5018;
	private static final int DAEMON_DRAIN = 5019;
	
	// Frintezza Status Tracking :
	private static final byte DORMANT = 0; // Frintezza is spawned and no one has entered yet. Entry is unlocked
	private static final byte WAITING = 1; // Frintezza is spawend and someone has entered, triggering a 30 minute window for additional people to enter
	private static final byte FIGHTING = 2; // Frintezza is engaged in battle, annihilating his foes. Entry is locked
	private static final byte DEAD = 3; // Frintezza has been killed. Entry is locked
	
	private static long _LastAction = 0;
	private static int _Angle, _Heading, _LocCycle, _Bomber, _CheckDie, _OnCheck, _OnSong,_Abnormal, _OnMorph  = 0;
	private static int _Scarlet_x, _Scarlet_y, _Scarlet_z, _Scarlet_h = 0;
	private static int _SecondMorph, _ThirdMorph = 0;
	private static int _KillHallAlarmDevice, _KillDarkChoirPlayer,_KillDarkChoirCaptain = 0;

	private L2GrandBossInstance frintezza, weakScarlet, strongScarlet, activeScarlet;
	private L2MonsterInstance demon1, demon2, demon3, demon4, portrait1, portrait2, portrait3, portrait4;
	private L2Npc _frintezzaDummy, _overheadDummy, _portraitDummy1, _portraitDummy3, _scarletDummy;
	private static List<L2PcInstance> _PlayersInside = new ArrayList<>();
	private static List<L2Npc> _Room1Mobs = new ArrayList<>();
	private static List<L2Npc> _Room2Mobs = new ArrayList<>();
	private static List<L2Attackable> Minions = new ArrayList<>();
	
	private static final int[][] INVADE_LOC =
	{
		{
			174102,
			-76039,
			-5105
		},
		{
			173235,
			-76884,
			-5105
		},
		{
			175003,
			-76933,
			-5105
		},
		{
			174196,
			-76190,
			-5105
		},
		{
			174013,
			-76120,
			-5105
		},
		{
			173263,
			-75161,
			-5105
		}
	};
	
	private static final int[][] SKILLS =
	{
		{
			DAEMON_CHARGE,
			1,
			5000
		},
		{
			DAEMON_CHARGE,
			4,
			5000
		},
		{
			DAEMON_CHARGE,
			2,
			5000
		},
		{
			DAEMON_CHARGE,
			5,
			5000
		},
		{
			DAEMON_FIELD,
			1,
			10000
		},
		{
			YOKE_OF_SCARLET,
			1,
			5000
		},
		{
			DAEMON_CHARGE,
			3,
			5000
		},
		{
			DAEMON_CHARGE,
			6,
			5000
		},
		{
			DAEMON_FIELD,
			2,
			10000
		},
		{
			DAEMON_DRAIN,
			1,
			10000
		},
		{
			YOKE_OF_SCARLET,
			1,
			5000
		}
	};
	
	private static final int[][] MOBS_LOC =
	{
		{
			HALL_ALARM_DEVICE,
			172894,
			-76019,
			-5107,
			243
		},
		{
			HALL_ALARM_DEVICE,
			174095,
			-77279,
			-5107,
			16216
		},
		{
			HALL_ALARM_DEVICE,
			174111,
			-74833,
			-5107,
			49043
		},
		{
			HALL_ALARM_DEVICE,
			175344,
			-76042,
			-5107,
			32847
		},
		{
			HALL_KEEPER_WIZARD,
			173489,
			-76227,
			-5134,
			63565
		},
		{
			HALL_KEEPER_WIZARD,
			173498,
			-75724,
			-5107,
			58498
		},
		{
			HALL_KEEPER_WIZARD,
			174365,
			-76745,
			-5107,
			22424
		},
		{
			HALL_KEEPER_WIZARD,
			174570,
			-75584,
			-5107,
			31968
		},
		{
			HALL_KEEPER_WIZARD,
			174613,
			-76179,
			-5107,
			31471
		},
		{
			HALL_KEEPER_PATROL,
			173620,
			-75981,
			-5107,
			4588
		},
		{
			HALL_KEEPER_PATROL,
			173630,
			-76340,
			-5107,
			62454
		},
		{
			HALL_KEEPER_PATROL,
			173755,
			-75613,
			-5107,
			57892
		},
		{
			HALL_KEEPER_PATROL,
			173823,
			-76688,
			-5107,
			2411
		},
		{
			HALL_KEEPER_PATROL,
			174000,
			-75411,
			-5107,
			54718
		},
		{
			HALL_KEEPER_PATROL,
			174487,
			-75555,
			-5107,
			33861
		},
		{
			HALL_KEEPER_PATROL,
			174517,
			-76471,
			-5107,
			21893
		},
		{
			HALL_KEEPER_PATROL,
			174576,
			-76122,
			-5107,
			31176
		},
		{
			HALL_KEEPER_PATROL,
			174600,
			-75841,
			-5134,
			35927
		},
		{
			HALL_KEEPER_CAPTAIN,
			173481,
			-76043,
			-5107,
			61312
		},
		{
			HALL_KEEPER_CAPTAIN,
			173539,
			-75678,
			-5107,
			59524
		},
		{
			HALL_KEEPER_CAPTAIN,
			173584,
			-76386,
			-5107,
			3041
		},
		{
			HALL_KEEPER_CAPTAIN,
			173773,
			-75420,
			-5107,
			51115
		},
		{
			HALL_KEEPER_CAPTAIN,
			173777,
			-76650,
			-5107,
			12588
		},
		{
			HALL_KEEPER_CAPTAIN,
			174585,
			-76510,
			-5107,
			21704
		},
		{
			HALL_KEEPER_CAPTAIN,
			174623,
			-75571,
			-5107,
			40141
		},
		{
			HALL_KEEPER_CAPTAIN,
			174744,
			-76240,
			-5107,
			29202
		},
		{
			HALL_KEEPER_CAPTAIN,
			174769,
			-75895,
			-5107,
			29572
		},
		{
			HALL_KEEPER_SUICIDAL_SOLDIER,
			173861,
			-76011,
			-5107,
			383
		},
		{
			HALL_KEEPER_SUICIDAL_SOLDIER,
			173872,
			-76461,
			-5107,
			8041
		},
		{
			HALL_KEEPER_SUICIDAL_SOLDIER,
			173898,
			-75668,
			-5107,
			51856
		},
		{
			HALL_KEEPER_SUICIDAL_SOLDIER,
			174422,
			-75689,
			-5107,
			42878
		},
		{
			HALL_KEEPER_SUICIDAL_SOLDIER,
			174460,
			-76355,
			-5107,
			27311
		},
		{
			HALL_KEEPER_SUICIDAL_SOLDIER,
			174483,
			-76041,
			-5107,
			30947
		},
		{
			HALL_KEEPER_GUARD,
			173515,
			-76184,
			-5107,
			6971
		},
		{
			HALL_KEEPER_GUARD,
			173516,
			-75790,
			-5134,
			3142
		},
		{
			HALL_KEEPER_GUARD,
			173696,
			-76675,
			-5107,
			6757
		},
		{
			HALL_KEEPER_GUARD,
			173766,
			-75502,
			-5134,
			60827
		},
		{
			HALL_KEEPER_GUARD,
			174473,
			-75321,
			-5107,
			37147
		},
		{
			HALL_KEEPER_GUARD,
			174493,
			-76505,
			-5107,
			34503
		},
		{
			HALL_KEEPER_GUARD,
			174568,
			-75654,
			-5134,
			41661
		},
		{
			HALL_KEEPER_GUARD,
			174584,
			-76263,
			-5107,
			31729
		},
		{
			DARK_CHOIR_PLAYER,
			173892,
			-81592,
			-5123,
			50849
		},
		{
			DARK_CHOIR_PLAYER,
			173958,
			-81820,
			-5123,
			7459
		},
		{
			DARK_CHOIR_PLAYER,
			174128,
			-81805,
			-5150,
			21495
		},
		{
			DARK_CHOIR_PLAYER,
			174245,
			-81566,
			-5123,
			41760
		},
		{
			DARK_CHOIR_CAPTAIN,
			173264,
			-81529,
			-5072,
			1646
		},
		{
			DARK_CHOIR_CAPTAIN,
			173265,
			-81656,
			-5072,
			441
		},
		{
			DARK_CHOIR_CAPTAIN,
			173267,
			-81889,
			-5072,
			0
		},
		{
			DARK_CHOIR_CAPTAIN,
			173271,
			-82015,
			-5072,
			65382
		},
		{
			DARK_CHOIR_CAPTAIN,
			174867,
			-81655,
			-5073,
			32537
		},
		{
			DARK_CHOIR_CAPTAIN,
			174868,
			-81890,
			-5073,
			32768
		},
		{
			DARK_CHOIR_CAPTAIN,
			174869,
			-81485,
			-5073,
			32315
		},
		{
			DARK_CHOIR_CAPTAIN,
			174871,
			-82017,
			-5073,
			33007
		},
		{
			DARK_CHOIR_PRIMA_DONNA,
			173074,
			-80817,
			-5107,
			8353
		},
		{
			DARK_CHOIR_PRIMA_DONNA,
			173128,
			-82702,
			-5107,
			5345
		},
		{
			DARK_CHOIR_PRIMA_DONNA,
			173181,
			-82544,
			-5107,
			65135
		},
		{
			DARK_CHOIR_PRIMA_DONNA,
			173191,
			-80981,
			-5107,
			6947
		},
		{
			DARK_CHOIR_PRIMA_DONNA,
			174859,
			-80889,
			-5134,
			24103
		},
		{
			DARK_CHOIR_PRIMA_DONNA,
			174924,
			-82666,
			-5107,
			38710
		},
		{
			DARK_CHOIR_PRIMA_DONNA,
			174947,
			-80733,
			-5107,
			22449
		},
		{
			DARK_CHOIR_PRIMA_DONNA,
			175096,
			-82724,
			-5107,
			42205
		},
		{
			DARK_CHOIR_LANCER,
			173435,
			-80512,
			-5107,
			65215
		},
		{
			DARK_CHOIR_LANCER,
			173440,
			-82948,
			-5107,
			417
		},
		{
			DARK_CHOIR_LANCER,
			173443,
			-83120,
			-5107,
			1094
		},
		{
			DARK_CHOIR_LANCER,
			173463,
			-83064,
			-5107,
			286
		},
		{
			DARK_CHOIR_LANCER,
			173465,
			-80453,
			-5107,
			174
		},
		{
			DARK_CHOIR_LANCER,
			173465,
			-83006,
			-5107,
			2604
		},
		{
			DARK_CHOIR_LANCER,
			173468,
			-82889,
			-5107,
			316
		},
		{
			DARK_CHOIR_LANCER,
			173469,
			-80570,
			-5107,
			65353
		},
		{
			DARK_CHOIR_LANCER,
			173469,
			-80628,
			-5107,
			166
		},
		{
			DARK_CHOIR_LANCER,
			173492,
			-83121,
			-5107,
			394
		},
		{
			DARK_CHOIR_LANCER,
			173493,
			-80683,
			-5107,
			0
		},
		{
			DARK_CHOIR_LANCER,
			173497,
			-80510,
			-5134,
			417
		},
		{
			DARK_CHOIR_LANCER,
			173499,
			-82947,
			-5107,
			0
		},
		{
			DARK_CHOIR_LANCER,
			173521,
			-83063,
			-5107,
			316
		},
		{
			DARK_CHOIR_LANCER,
			173523,
			-82889,
			-5107,
			128
		},
		{
			DARK_CHOIR_LANCER,
			173524,
			-80627,
			-5134,
			65027
		},
		{
			DARK_CHOIR_LANCER,
			173524,
			-83007,
			-5107,
			0
		},
		{
			DARK_CHOIR_LANCER,
			173526,
			-80452,
			-5107,
			64735
		},
		{
			DARK_CHOIR_LANCER,
			173527,
			-80569,
			-5134,
			65062
		},
		{
			DARK_CHOIR_LANCER,
			174602,
			-83122,
			-5107,
			33104
		},
		{
			DARK_CHOIR_LANCER,
			174604,
			-82949,
			-5107,
			33184
		},
		{
			DARK_CHOIR_LANCER,
			174609,
			-80514,
			-5107,
			33234
		},
		{
			DARK_CHOIR_LANCER,
			174609,
			-80684,
			-5107,
			32851
		},
		{
			DARK_CHOIR_LANCER,
			174629,
			-80627,
			-5107,
			33346
		},
		{
			DARK_CHOIR_LANCER,
			174632,
			-80570,
			-5107,
			32896
		},
		{
			DARK_CHOIR_LANCER,
			174632,
			-83066,
			-5107,
			32768
		},
		{
			DARK_CHOIR_LANCER,
			174635,
			-82893,
			-5107,
			33594
		},
		{
			DARK_CHOIR_LANCER,
			174636,
			-80456,
			-5107,
			32065
		},
		{
			DARK_CHOIR_LANCER,
			174639,
			-83008,
			-5107,
			33057
		},
		{
			DARK_CHOIR_LANCER,
			174660,
			-80512,
			-5107,
			33057
		},
		{
			DARK_CHOIR_LANCER,
			174661,
			-83121,
			-5107,
			32768
		},
		{
			DARK_CHOIR_LANCER,
			174663,
			-82948,
			-5107,
			32768
		},
		{
			DARK_CHOIR_LANCER,
			174664,
			-80685,
			-5107,
			32676
		},
		{
			DARK_CHOIR_LANCER,
			174687,
			-83008,
			-5107,
			32520
		},
		{
			DARK_CHOIR_LANCER,
			174691,
			-83066,
			-5107,
			32961
		},
		{
			DARK_CHOIR_LANCER,
			174692,
			-80455,
			-5107,
			33202
		},
		{
			DARK_CHOIR_LANCER,
			174692,
			-80571,
			-5107,
			32768
		},
		{
			DARK_CHOIR_LANCER,
			174693,
			-80630,
			-5107,
			32994
		},
		{
			DARK_CHOIR_LANCER,
			174693,
			-82889,
			-5107,
			32622
		},
		{
			DARK_CHOIR_ARCHER,
			172837,
			-82382,
			-5107,
			58363
		},
		{
			DARK_CHOIR_ARCHER,
			172867,
			-81123,
			-5107,
			64055
		},
		{
			DARK_CHOIR_ARCHER,
			172883,
			-82495,
			-5107,
			64764
		},
		{
			DARK_CHOIR_ARCHER,
			172916,
			-81033,
			-5107,
			7099
		},
		{
			DARK_CHOIR_ARCHER,
			172940,
			-82325,
			-5107,
			58998
		},
		{
			DARK_CHOIR_ARCHER,
			172946,
			-82435,
			-5107,
			58038
		},
		{
			DARK_CHOIR_ARCHER,
			172971,
			-81198,
			-5107,
			14768
		},
		{
			DARK_CHOIR_ARCHER,
			172992,
			-81091,
			-5107,
			9438
		},
		{
			DARK_CHOIR_ARCHER,
			173032,
			-82365,
			-5107,
			59041
		},
		{
			DARK_CHOIR_ARCHER,
			173064,
			-81125,
			-5107,
			5827
		},
		{
			DARK_CHOIR_ARCHER,
			175014,
			-81173,
			-5107,
			26398
		},
		{
			DARK_CHOIR_ARCHER,
			175061,
			-82374,
			-5107,
			43290
		},
		{
			DARK_CHOIR_ARCHER,
			175096,
			-81080,
			-5107,
			24719
		},
		{
			DARK_CHOIR_ARCHER,
			175169,
			-82453,
			-5107,
			37672
		},
		{
			DARK_CHOIR_ARCHER,
			175172,
			-80972,
			-5107,
			32315
		},
		{
			DARK_CHOIR_ARCHER,
			175174,
			-82328,
			-5107,
			41760
		},
		{
			DARK_CHOIR_ARCHER,
			175197,
			-81157,
			-5107,
			27617
		},
		{
			DARK_CHOIR_ARCHER,
			175245,
			-82547,
			-5107,
			40275
		},
		{
			DARK_CHOIR_ARCHER,
			175249,
			-81075,
			-5107,
			28435
		},
		{
			DARK_CHOIR_ARCHER,
			175292,
			-82432,
			-5107,
			42225
		},
		{
			DARK_CHOIR_WITCH_DOCTOR,
			173014,
			-82628,
			-5107,
			11874
		},
		{
			DARK_CHOIR_WITCH_DOCTOR,
			173033,
			-80920,
			-5107,
			10425
		},
		{
			DARK_CHOIR_WITCH_DOCTOR,
			173095,
			-82520,
			-5107,
			49152
		},
		{
			DARK_CHOIR_WITCH_DOCTOR,
			173115,
			-80986,
			-5107,
			9611
		},
		{
			DARK_CHOIR_WITCH_DOCTOR,
			173144,
			-80894,
			-5107,
			5345
		},
		{
			DARK_CHOIR_WITCH_DOCTOR,
			173147,
			-82602,
			-5107,
			51316
		},
		{
			DARK_CHOIR_WITCH_DOCTOR,
			174912,
			-80825,
			-5107,
			24270
		},
		{
			DARK_CHOIR_WITCH_DOCTOR,
			174935,
			-80899,
			-5107,
			18061
		},
		{
			DARK_CHOIR_WITCH_DOCTOR,
			175016,
			-82697,
			-5107,
			39533
		},
		{
			DARK_CHOIR_WITCH_DOCTOR,
			175041,
			-80834,
			-5107,
			25420
		},
		{
			DARK_CHOIR_WITCH_DOCTOR,
			175071,
			-82549,
			-5107,
			39163
		},
		{
			DARK_CHOIR_WITCH_DOCTOR,
			175154,
			-82619,
			-5107,
			36345
		}
	};
	
	// Boss: Frintezza
	public Frintezza()
	{
		super("ai/individual");
		registerNpcs();
		addStartNpc(GUIDE, CUBE);
		addTalkId(GUIDE,CUBE);
		StatsSet info = GrandBossManager.getInstance().getStatsSet(FRINTEZZA);
		
		switch (GrandBossManager.getInstance().getBossStatus(FRINTEZZA))
		{
			case DEAD: // Launch the timer to set DORMANT, or set DORMANT directly if timer expired while offline.
				long temp = (info.getLong("respawn_time") - System.currentTimeMillis());
				if (temp > 0)
					startQuestTimer("frintezza_unlock", temp, null, null, false);
				else
					GrandBossManager.getInstance().setBossStatus(FRINTEZZA, DORMANT);
				break;
		}		
	}
	
	@Override
	protected void registerNpcs()
	{
		addEventIds(SCARLET1, EventType.ON_ATTACK, EventType.ON_KILL, EventType.ON_SPAWN, EventType.ON_SPELL_FINISHED, EventType.ON_SKILL_SEE, EventType.ON_FACTION_CALL, EventType.ON_AGGRO);
		addEventIds(SCARLET2, EventType.ON_ATTACK, EventType.ON_KILL, EventType.ON_SPAWN, EventType.ON_SPELL_FINISHED, EventType.ON_SKILL_SEE, EventType.ON_FACTION_CALL, EventType.ON_AGGRO);
		addEventIds(FRINTEZZA, EventType.ON_ATTACK, EventType.ON_KILL, EventType.ON_SPAWN, EventType.ON_SPELL_FINISHED, EventType.ON_SKILL_SEE, EventType.ON_FACTION_CALL, EventType.ON_AGGRO);
		addEventIds(HALL_ALARM_DEVICE, EventType.ON_ATTACK, EventType.ON_KILL, EventType.ON_SPAWN, EventType.ON_SPELL_FINISHED, EventType.ON_SKILL_SEE, EventType.ON_FACTION_CALL, EventType.ON_AGGRO);
		addEventIds(HALL_KEEPER_CAPTAIN, EventType.ON_ATTACK, EventType.ON_KILL, EventType.ON_SPAWN, EventType.ON_SPELL_FINISHED, EventType.ON_SKILL_SEE, EventType.ON_FACTION_CALL, EventType.ON_AGGRO);
		addEventIds(HALL_KEEPER_WIZARD, EventType.ON_ATTACK, EventType.ON_KILL, EventType.ON_SPAWN, EventType.ON_SPELL_FINISHED, EventType.ON_SKILL_SEE, EventType.ON_FACTION_CALL, EventType.ON_AGGRO);
		addEventIds(HALL_KEEPER_GUARD, EventType.ON_ATTACK, EventType.ON_KILL, EventType.ON_SPAWN, EventType.ON_SPELL_FINISHED, EventType.ON_SKILL_SEE, EventType.ON_FACTION_CALL, EventType.ON_AGGRO);
		addEventIds(HALL_KEEPER_PATROL, EventType.ON_ATTACK, EventType.ON_KILL, EventType.ON_SPAWN, EventType.ON_SPELL_FINISHED, EventType.ON_SKILL_SEE, EventType.ON_FACTION_CALL, EventType.ON_AGGRO);
		addEventIds(HALL_KEEPER_SUICIDAL_SOLDIER, EventType.ON_ATTACK, EventType.ON_KILL, EventType.ON_SPAWN, EventType.ON_SPELL_FINISHED, EventType.ON_SKILL_SEE, EventType.ON_FACTION_CALL, EventType.ON_AGGRO);
		addEventIds(DARK_CHOIR_CAPTAIN, EventType.ON_ATTACK, EventType.ON_KILL, EventType.ON_SPAWN, EventType.ON_SPELL_FINISHED, EventType.ON_SKILL_SEE, EventType.ON_FACTION_CALL, EventType.ON_AGGRO);
		addEventIds(DARK_CHOIR_PRIMA_DONNA, EventType.ON_ATTACK, EventType.ON_KILL, EventType.ON_SPAWN, EventType.ON_SPELL_FINISHED, EventType.ON_SKILL_SEE, EventType.ON_FACTION_CALL, EventType.ON_AGGRO);
		addEventIds(DARK_CHOIR_LANCER, EventType.ON_ATTACK, EventType.ON_KILL, EventType.ON_SPAWN, EventType.ON_SPELL_FINISHED, EventType.ON_SKILL_SEE, EventType.ON_FACTION_CALL, EventType.ON_AGGRO);
		addEventIds(DARK_CHOIR_ARCHER, EventType.ON_ATTACK, EventType.ON_KILL, EventType.ON_SPAWN, EventType.ON_SPELL_FINISHED, EventType.ON_SKILL_SEE, EventType.ON_FACTION_CALL, EventType.ON_AGGRO);
		addEventIds(DARK_CHOIR_WITCH_DOCTOR, EventType.ON_ATTACK, EventType.ON_KILL, EventType.ON_SPAWN, EventType.ON_SPELL_FINISHED, EventType.ON_SKILL_SEE, EventType.ON_FACTION_CALL, EventType.ON_AGGRO);
		addEventIds(DARK_CHOIR_PLAYER, EventType.ON_ATTACK, EventType.ON_KILL, EventType.ON_SPAWN, EventType.ON_SPELL_FINISHED, EventType.ON_SKILL_SEE, EventType.ON_FACTION_CALL, EventType.ON_AGGRO);
		addEventIds(EVIL_SPIRIT, EventType.ON_ATTACK, EventType.ON_KILL, EventType.ON_SPAWN, EventType.ON_SPELL_FINISHED, EventType.ON_SKILL_SEE, EventType.ON_FACTION_CALL, EventType.ON_AGGRO);
		addEventIds(EVIL_SPIRIT2, EventType.ON_ATTACK, EventType.ON_KILL, EventType.ON_SPAWN, EventType.ON_SPELL_FINISHED, EventType.ON_SKILL_SEE, EventType.ON_FACTION_CALL, EventType.ON_AGGRO);
		addEventIds(BREATH_OF_HALISHA, EventType.ON_ATTACK, EventType.ON_KILL, EventType.ON_SPAWN, EventType.ON_SPELL_FINISHED, EventType.ON_SKILL_SEE, EventType.ON_FACTION_CALL, EventType.ON_AGGRO);
		addEventIds(BREATH_OF_HALISHA2, EventType.ON_ATTACK, EventType.ON_KILL, EventType.ON_SPAWN, EventType.ON_SPELL_FINISHED, EventType.ON_SKILL_SEE, EventType.ON_FACTION_CALL, EventType.ON_AGGRO);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		long temp = 0;
		if (event.equalsIgnoreCase("waiting"))
		{
			startQuestTimer("close", 27000, npc, null, false);
			startQuestTimer("camera_1", 30000, npc, null, false);
			FRINTEZZA_LAIR.broadcastPacket(new Earthquake(174232, -88020, -5116, 45, 27));
		}
		else if (event.equalsIgnoreCase("room1_spawn"))
		{
			CreatureSay cs = new CreatureSay(0, Say2.SHOUT, "Hall Alarm Device", "Intruders! Sound the alarm!");
			FRINTEZZA_LAIR.broadcastPacket(cs);
			for (int i = 0; i <= 17; i++)
			{
				L2Npc mob = addSpawn(MOBS_LOC[i][0], MOBS_LOC[i][1], MOBS_LOC[i][2], MOBS_LOC[i][3], MOBS_LOC[i][4], false, 0, false);
				_Room1Mobs.add(mob);
			}
		}
		else if (event.equalsIgnoreCase("room1_spawn2"))
		{
			for (int i = 18; i <= 26; i++)
			{
				L2Npc mob = addSpawn(MOBS_LOC[i][0], MOBS_LOC[i][1], MOBS_LOC[i][2], MOBS_LOC[i][3], MOBS_LOC[i][4], false, 0, false);
				_Room1Mobs.add(mob);
			}
		}
		else if (event.equalsIgnoreCase("room1_spawn3"))
		{
			for (int i = 27; i <= 32; i++)
			{
				L2Npc mob = addSpawn(MOBS_LOC[i][0], MOBS_LOC[i][1], MOBS_LOC[i][2], MOBS_LOC[i][3], MOBS_LOC[i][4], false, 0, false);
				_Room1Mobs.add(mob);
			}
		}
		else if (event.equalsIgnoreCase("room1_spawn4"))
		{
			for (int i = 33; i <= 40; i++)
			{
				L2Npc mob = addSpawn(MOBS_LOC[i][0], MOBS_LOC[i][1], MOBS_LOC[i][2], MOBS_LOC[i][3], MOBS_LOC[i][4], false, 0, false);
				_Room1Mobs.add(mob);
			}
		}
		else if (event.equalsIgnoreCase("room2_spawn"))
		{
			for (int i = 41; i <= 44; i++)
			{
				L2Npc mob = addSpawn(MOBS_LOC[i][0], MOBS_LOC[i][1], MOBS_LOC[i][2], MOBS_LOC[i][3], MOBS_LOC[i][4], false, 0, false);
				_Room2Mobs.add(mob);
			}
		}
		else if (event.equalsIgnoreCase("room2_spawn2"))
		{
			for (int i = 45; i <= 131; i++)
			{
				L2Npc mob = addSpawn(MOBS_LOC[i][0], MOBS_LOC[i][1], MOBS_LOC[i][2], MOBS_LOC[i][3], MOBS_LOC[i][4], false, 0, false);
				_Room2Mobs.add(mob);
			}
		}
		else if (event.equalsIgnoreCase("room1_del"))
		{
			for (L2Npc mob : _Room1Mobs)
			{
				if (mob != null)
					mob.deleteMe();
			}
			_Room1Mobs.clear();
		}
		else if (event.equalsIgnoreCase("room2_del"))
		{
			for (L2Npc mob : _Room2Mobs)
			{
				if (mob != null)
					mob.deleteMe();
			}
			_Room2Mobs.clear();
		}
		else if (event.equalsIgnoreCase("room3_del"))
		{
			if (demon1 != null)
				demon1.deleteMe();
			if (demon2 != null)
				demon2.deleteMe();
			if (demon3 != null)
				demon3.deleteMe();
			if (demon4 != null)
				demon4.deleteMe();
			if (portrait1 != null)
				portrait1.deleteMe();
			if (portrait2 != null)
				portrait2.deleteMe();
			if (portrait3 != null)
				portrait3.deleteMe();
			if (portrait4 != null)
				portrait4.deleteMe();
			if (frintezza != null)
				frintezza.deleteMe();
			if (weakScarlet != null)
				weakScarlet.deleteMe();
			if (strongScarlet != null)
				strongScarlet.deleteMe();
			
			demon1 = null;
			demon2 = null;
			demon3 = null;
			demon4 = null;
			portrait1 = null;
			portrait2 = null;
			portrait3 = null;
			portrait4 = null;
			frintezza = null;
			weakScarlet = null;
			strongScarlet = null;
			activeScarlet = null;
		}
		else if (event.equalsIgnoreCase("clean"))
		{
			_LastAction = 0;
			_LocCycle = 0;
			_CheckDie = 0;
			_OnCheck = 0;
			_Abnormal = 0;
			_OnMorph = 0;
			_SecondMorph = 0;
			_ThirdMorph = 0;
			_KillHallAlarmDevice = 0;
			_KillDarkChoirPlayer = 0;
			_KillDarkChoirCaptain = 0;
			_PlayersInside.clear();
		}
		else if (event.equalsIgnoreCase("close"))
		{
			for (int i = 25150051; i <= 25150058; i++)
				DoorTable.getInstance().getDoor(i).closeMe();
			for (int i = 25150061; i <= 25150070; i++)
				DoorTable.getInstance().getDoor(i).closeMe();
			
			DoorTable.getInstance().getDoor(25150042).closeMe();
			DoorTable.getInstance().getDoor(25150043).closeMe();
			DoorTable.getInstance().getDoor(25150045).closeMe();
			DoorTable.getInstance().getDoor(25150046).closeMe();
		}
		else if (event.equalsIgnoreCase("loc_check"))
		{
			if (GrandBossManager.getInstance().getBossStatus(FRINTEZZA) == FIGHTING)
			{
				if (!FRINTEZZA_LAIR.isInsideZone(npc))
					npc.teleToLocation(174232, -88020, -5116, 0);
				if (npc.getX() < 171932 || npc.getX() > 176532 || npc.getY() < -90320 || npc.getY() > -85720 || npc.getZ() < -5130)
					npc.teleToLocation(174232, -88020, -5116, 0);
			}
		}
		else if (event.equalsIgnoreCase("camera_1"))
		{
			GrandBossManager.getInstance().setBossStatus(FRINTEZZA, 2);
			_frintezzaDummy = addSpawn(29052, 0x2a8a0, 0xfffea133, -5022, 16048, false, 0L, false);
			_frintezzaDummy.setIsInvul(false);
			_frintezzaDummy.setIsImmobilized(false);
			_overheadDummy = addSpawn(29052, 0x2a898, 0xfffea82c, -5110, 16384, false, 0L, false);
			_overheadDummy.setIsInvul(false);
			_overheadDummy.setIsImmobilized(false);
			_overheadDummy.setCollisionHeight(600);
			FRINTEZZA_LAIR.broadcastPacket(new NpcInfo(_overheadDummy, null));
			_portraitDummy1 = addSpawn(29052, 0x2a1a2, 0xfffea8ae, -5100, 16048, false, 0L, false);
			_portraitDummy1.setIsImmobilized(false);
			_portraitDummy1.setIsInvul(false);
			_portraitDummy3 = addSpawn(29052, 0x2af8c, 0xfffea8ae, -5100, 16048, false, 0L, false);
			_portraitDummy3.setIsImmobilized(false);
			_portraitDummy3.setIsInvul(false);
			_scarletDummy = addSpawn(29053, 0x2a898, 0xfffea82c, -5110, 16384, false, 0L, false);
			_scarletDummy.setIsInvul(false);
			_scarletDummy.setIsImmobilized(false);
			startQuestTimer("stop_pc", 0L, npc, null, false);
			startQuestTimer("camera_2", 1000L, _overheadDummy, null, false);
		}
		else if (event.equalsIgnoreCase("camera_2"))
		{
			FRINTEZZA_LAIR.broadcastPacket(new SpecialCamera(_overheadDummy.getObjectId(), 0, 75, -89, 0, 100, 0, 0, 1, 0));
			startQuestTimer("camera_2b", 0L, _overheadDummy, null, false);
		}
		else if (event.equalsIgnoreCase("camera_2b"))
		{
			FRINTEZZA_LAIR.broadcastPacket(new SpecialCamera(_overheadDummy.getObjectId(), 0, 75, -89, 0, 100, 0, 0, 1, 0));
			startQuestTimer("camera_3", 0L, _overheadDummy, null, false);
		}
		else if (event.equalsIgnoreCase("camera_3"))
		{
			FRINTEZZA_LAIR.broadcastPacket(new SpecialCamera(_overheadDummy.getObjectId(), 300, 90, -10, 6500, 7000, 0, 0, 1, 0));
			frintezza = (L2GrandBossInstance) addSpawn(FRINTEZZA, 0x2a8a0, 0xfffea133, -5022, 16048, false, 0L, false);
			GrandBossManager.getInstance().addBoss(frintezza);
			frintezza.setIsImmobilized(true);
			frintezza.setIsInvul(true);
			frintezza.disableAllSkills();
			FRINTEZZA_LAIR.updateKnownList(frintezza);
			demon2 = (L2MonsterInstance) addSpawn(BREATH_OF_HALISHA2, 0x2af04, 0xfffea577, -5100, 28205, false, 0L, false);
			demon2.setIsImmobilized(true);
			demon2.disableAllSkills();
			FRINTEZZA_LAIR.updateKnownList(demon2);
			demon3 = (L2MonsterInstance) addSpawn(BREATH_OF_HALISHA2, 0x2a240, 0xfffea582, -5100, 64817, false, 0L, false);
			demon3.setIsImmobilized(true);
			demon3.disableAllSkills();
			FRINTEZZA_LAIR.updateKnownList(demon3);
			demon1 = (L2MonsterInstance) addSpawn(BREATH_OF_HALISHA, 0x2aed9, 0xfffeab83, -5100, 35048, false, 0L, false);
			demon1.setIsImmobilized(true);
			demon1.disableAllSkills();
			FRINTEZZA_LAIR.updateKnownList(demon1);
			demon4 = (L2MonsterInstance) addSpawn(BREATH_OF_HALISHA, 0x2a25a, 0xfffeab83, -5100, 57730, false, 0L, false);
			demon4.setIsImmobilized(true);
			demon4.disableAllSkills();
			FRINTEZZA_LAIR.updateKnownList(demon4);
			startQuestTimer("camera_4", 6500L, _overheadDummy, null, false);
		}
		else if (event.equalsIgnoreCase("camera_4"))
		{
			FRINTEZZA_LAIR.broadcastPacket(new SpecialCamera(_frintezzaDummy.getObjectId(), 1800, 90, 8, 6500, 7000, 0, 0, 1, 0));
			startQuestTimer("camera_5", 900L, _frintezzaDummy, null, false);
		}
		else if (event.equalsIgnoreCase("camera_5"))
		{
			FRINTEZZA_LAIR.broadcastPacket(new SpecialCamera(_frintezzaDummy.getObjectId(), 140, 90, 10, 2500, 4500, 0, 0, 1, 0));
			startQuestTimer("camera_5b", 4000L, _frintezzaDummy, null, false);
		}
		else if (event.equalsIgnoreCase("camera_5b"))
		{
			FRINTEZZA_LAIR.broadcastPacket(new SpecialCamera(frintezza.getObjectId(), 40, 75, -10, 0, 1000, 0, 0, 1, 0));
			startQuestTimer("camera_6", 0L, frintezza, null, false);
		}
		else if (event.equalsIgnoreCase("camera_6"))
		{
			FRINTEZZA_LAIR.broadcastPacket(new SpecialCamera(frintezza.getObjectId(), 40, 75, -10, 0, 12000, 0, 0, 1, 0));
			startQuestTimer("camera_7", 1350L, frintezza, null, false);
		}
		else if (event.equalsIgnoreCase("camera_7"))
		{
			FRINTEZZA_LAIR.broadcastPacket(new SocialAction(frintezza, 2));
			startQuestTimer("camera_8", 7000L, frintezza, null, false);
		}
		else if (event.equalsIgnoreCase("camera_8"))
		{
			startQuestTimer("camera_9", 1000L, frintezza, null, false);
			_frintezzaDummy.deleteMe();
			_frintezzaDummy = null;
		}
		else if (event.equalsIgnoreCase("camera_9"))
		{
			FRINTEZZA_LAIR.broadcastPacket(new SocialAction(demon2, 1));
			FRINTEZZA_LAIR.broadcastPacket(new SocialAction(demon3, 1));
			startQuestTimer("camera_9b", 400L, frintezza, null, false);
		}
		else if (event.equalsIgnoreCase("camera_9b"))
		{
			FRINTEZZA_LAIR.broadcastPacket(new SocialAction(demon1, 1));
			FRINTEZZA_LAIR.broadcastPacket(new SocialAction(demon4, 1));
			Iterator<L2Character> cam9b = FRINTEZZA_LAIR.getCharactersInside().iterator();
			do
			{
				if (!cam9b.hasNext())
					break;
				L2Character pc = cam9b.next();
				if (pc instanceof L2PcInstance)
					if (pc.getX() < 0x2a898)
						pc.broadcastPacket(new SpecialCamera(_portraitDummy1.getObjectId(), 1000, 118, 0, 0, 1000, 0, 0, 1, 0));
					else
						pc.broadcastPacket(new SpecialCamera(_portraitDummy3.getObjectId(), 1000, 62, 0, 0, 1000, 0, 0, 1, 0));
			}
			while (true);
			startQuestTimer("camera_9c", 0L, frintezza, null, false);
		}
		else if (event.equalsIgnoreCase("camera_9c"))
		{
			Iterator<L2Character> Cam9c = FRINTEZZA_LAIR.getCharactersInside().iterator();
			do
			{
				if (!Cam9c.hasNext())
					break;

				L2Character pc = Cam9c.next();
				if (pc instanceof L2PcInstance)
					if (pc.getX() < 0x2a898)
						pc.broadcastPacket(new SpecialCamera(_portraitDummy1.getObjectId(), 1000, 118, 0, 0, 10000, 0, 0, 1, 0));
					else
						pc.broadcastPacket(new SpecialCamera(_portraitDummy3.getObjectId(), 1000, 62, 0, 0, 10000, 0, 0, 1, 0));
			}
			while (true);
			startQuestTimer("camera_10", 2000L, frintezza, null, false);
		}
		else if (event.equalsIgnoreCase("camera_10"))
		{
			FRINTEZZA_LAIR.broadcastPacket(new SpecialCamera(frintezza.getObjectId(), 240, 90, 0, 0, 1000, 0, 0, 1, 0));
			startQuestTimer("camera_11", 0L, frintezza, null, false);
		}
		else if (event.equalsIgnoreCase("camera_11"))
		{
			FRINTEZZA_LAIR.broadcastPacket(new SpecialCamera(frintezza.getObjectId(), 240, 90, 25, 5500, 10000, 0, 0, 1, 0));
			FRINTEZZA_LAIR.broadcastPacket(new SocialAction(frintezza, 3));
			_portraitDummy1.deleteMe();
			_portraitDummy3.deleteMe();
			_portraitDummy1 = null;
			_portraitDummy3 = null;
			startQuestTimer("camera_12", 4500L, frintezza, null, false);
		}
		else if (event.equalsIgnoreCase("camera_12"))
		{
			FRINTEZZA_LAIR.broadcastPacket(new SpecialCamera(frintezza.getObjectId(), 100, 195, 35, 0, 10000, 0, 0, 1, 0));
			startQuestTimer("camera_13", 700L, frintezza, null, false);
		}
		else if (event.equalsIgnoreCase("camera_13"))
		{
			FRINTEZZA_LAIR.broadcastPacket(new SpecialCamera(frintezza.getObjectId(), 100, 195, 35, 0, 10000, 0, 0, 1, 0));
			startQuestTimer("camera_14", 1300L, frintezza, null, false);
		}
		else if (event.equalsIgnoreCase("camera_14"))
		{
			FRINTEZZA_LAIR.broadcastPacket(new SpecialCamera(frintezza.getObjectId(), 120, 180, 45, 1500, 10000, 0, 0, 1, 0));
			FRINTEZZA_LAIR.broadcastPacket(new MagicSkillUse(frintezza, frintezza, 5006, 1, 34000, 0));
			FRINTEZZA_LAIR.broadcastPacket(new ExShowScreenMessage(1,0,2,false,1,0,0,false,5000,true,"Mournful Chorale Prelude"));
			startQuestTimer("camera_16", 1500L, frintezza, null, false);
		}
		else if (event.equalsIgnoreCase("camera_16"))
		{
			FRINTEZZA_LAIR.broadcastPacket(new SpecialCamera(frintezza.getObjectId(), 520, 135, 45, 8000, 10000, 0, 0, 1, 0));
			startQuestTimer("camera_17", 7500L, frintezza, null, false);
		}
		else if (event.equalsIgnoreCase("camera_17"))
		{
			FRINTEZZA_LAIR.broadcastPacket(new SpecialCamera(frintezza.getObjectId(), 1500, 110, 25, 10000, 13000, 0, 0, 1, 0));
			startQuestTimer("camera_18", 9500L, frintezza, null, false);
		}
		else if (event.equalsIgnoreCase("camera_18"))
		{
			FRINTEZZA_LAIR.broadcastPacket(new SpecialCamera(_overheadDummy.getObjectId(), 930, 160, -20, 0, 1000, 0, 0, 1, 0));
			startQuestTimer("camera_18b", 0L, _overheadDummy, null, false);
		}
		else if (event.equalsIgnoreCase("camera_18b"))
		{
			FRINTEZZA_LAIR.broadcastPacket(new SpecialCamera(_overheadDummy.getObjectId(), 600, 180, -25, 0, 10000, 0, 0, 1, 0));
			FRINTEZZA_LAIR.broadcastPacket(new MagicSkillUse(_scarletDummy, _overheadDummy, 5004, 1, 5800, 0));
			weakScarlet = (L2GrandBossInstance) addSpawn(SCARLET1, 0x2a898, 0xfffea82c, -5110, 16384, false, 0L, true);
			weakScarlet.setIsInvul(true);
			weakScarlet.setIsImmobilized(true);
			weakScarlet.disableAllSkills();
			FRINTEZZA_LAIR.updateKnownList(weakScarlet);
			activeScarlet = weakScarlet;
			startQuestTimer("camera_19", 2400L, _scarletDummy, null, false);
			startQuestTimer("camera_19b", 5000L, _scarletDummy, null, false);
		}
		else if (event.equalsIgnoreCase("camera_19"))
			weakScarlet.teleToLocation(0x2a898, 0xfffea82c, -5110, 0);
		else if (event.equalsIgnoreCase("camera_19b"))
		{
			FRINTEZZA_LAIR.broadcastPacket(new SpecialCamera(_scarletDummy.getObjectId(), 800, 180, 10, 1000, 10000, 0, 0, 1, 0));
			startQuestTimer("camera_20", 2100L, _scarletDummy, null, false);
		}
		else if (event.equalsIgnoreCase("camera_20"))
		{
			FRINTEZZA_LAIR.broadcastPacket(new SpecialCamera(weakScarlet.getObjectId(), 300, 60, 8, 0, 10000, 0, 0, 1, 0));
			startQuestTimer("camera_21", 2000L, weakScarlet, null, false);
		}
		else if (event.equalsIgnoreCase("camera_21"))
		{
			FRINTEZZA_LAIR.broadcastPacket(new SpecialCamera(weakScarlet.getObjectId(), 500, 90, 10, 3000, 5000, 0, 0, 1, 0));
			startQuestTimer("camera_22", 3000L, weakScarlet, null, false);
		}
		else if (event.equalsIgnoreCase("camera_22"))
		{
			portrait2 = (L2MonsterInstance) addSpawn(EVIL_SPIRIT2, 0x2af04, 0xfffea577, -5000, 28205, false, 0L, false);
			portrait2.setIsImmobilized(true);
			portrait2.disableAllSkills();
			FRINTEZZA_LAIR.updateKnownList(portrait2);
			portrait3 = (L2MonsterInstance) addSpawn(EVIL_SPIRIT2, 0x2a240, 0xfffea582, -5000, 64817, false, 0L, false);
			portrait3.setIsImmobilized(true);
			portrait3.disableAllSkills();
			FRINTEZZA_LAIR.updateKnownList(portrait3);
			portrait1 = (L2MonsterInstance) addSpawn(EVIL_SPIRIT, 0x2aed9, 0xfffeab83, -5000, 35048, false, 0L, false);
			portrait1.setIsImmobilized(true);
			portrait1.disableAllSkills();
			FRINTEZZA_LAIR.updateKnownList(portrait1);
			portrait4 = (L2MonsterInstance) addSpawn(EVIL_SPIRIT, 0x2a25a, 0xfffeab83, -5000, 57730, false, 0L, false);
			portrait4.setIsImmobilized(true);
			portrait4.disableAllSkills();
			FRINTEZZA_LAIR.updateKnownList(portrait4);
			_overheadDummy.deleteMe();
			_scarletDummy.deleteMe();
			_overheadDummy = null;
			_scarletDummy = null;
			startQuestTimer("camera_23", 2000L, weakScarlet, null, false);
			startQuestTimer("start_pc", 2000L, weakScarlet, null, false);
			startQuestTimer("loc_check", 60000L, weakScarlet, null, true);
			startQuestTimer("songs_play", 10000 + Rnd.get(10000), frintezza, null, false);
			startQuestTimer("skill01", 10000 + Rnd.get(10000), weakScarlet, null, false);
		}
		else if (event.equalsIgnoreCase("camera_23"))
		{
			demon1.setIsImmobilized(false);
			demon2.setIsImmobilized(false);
			demon3.setIsImmobilized(false);
			demon4.setIsImmobilized(false);
			demon1.enableAllSkills();
			demon2.enableAllSkills();
			demon3.enableAllSkills();
			demon4.enableAllSkills();
			portrait1.setIsImmobilized(false);
			portrait2.setIsImmobilized(false);
			portrait3.setIsImmobilized(false);
			portrait4.setIsImmobilized(false);
			portrait1.enableAllSkills();
			portrait2.enableAllSkills();
			portrait3.enableAllSkills();
			portrait4.enableAllSkills();
			weakScarlet.setIsInvul(false);
			weakScarlet.setIsImmobilized(false);
			weakScarlet.enableAllSkills();
			weakScarlet.setRunning();
			startQuestTimer("spawn_minion", 20000L, portrait1, null, false);
			startQuestTimer("spawn_minion", 20000L, portrait2, null, false);
			startQuestTimer("spawn_minion", 20000L, portrait3, null, false);
			startQuestTimer("spawn_minion", 20000L, portrait4, null, false);
		}
		else if (event.equalsIgnoreCase("stop_pc"))
		{
			for (L2Character cha : FRINTEZZA_LAIR.getCharactersInside())
			{
				cha.abortAttack();
				cha.abortCast();
				cha.disableAllSkills();
				cha.setTarget(null);
				cha.stopMove(null);
				cha.setIsImmobilized(true);
				cha.getAI().setIntention(CtrlIntention.IDLE);
			}
		}
		else if (event.equalsIgnoreCase("stop_npc"))
		{
			_Heading = npc.getHeading();
			if (_Heading < 32768)
				_Angle = Math.abs(180 - (int) (_Heading / 182.044444444));
			else
				_Angle = Math.abs(540 - (int) (_Heading / 182.044444444));
		}
		else if (event.equalsIgnoreCase("start_pc"))
		{
			for (L2Character cha : FRINTEZZA_LAIR.getCharactersInside())
			{
				if (cha != frintezza)
				{
					cha.enableAllSkills();
					cha.setIsImmobilized(false);
				}
			}
		}
		else if (event.equalsIgnoreCase("start_npc"))
		{
			npc.setRunning();
			npc.setIsInvul(false);
		}
		else if (event.equalsIgnoreCase("morph_end"))
		{
			_OnMorph = 0;
		}
		else if (event.equalsIgnoreCase("morph_01"))
		{
			FRINTEZZA_LAIR.broadcastPacket(new SpecialCamera(weakScarlet.getObjectId(), 250, _Angle, 12, 2000, 15000, 0, 0, 1, 0));
			startQuestTimer("morph_02", 3000, weakScarlet, null, false);
		}
		else if (event.equalsIgnoreCase("morph_02"))
		{
			FRINTEZZA_LAIR.broadcastPacket(new SocialAction(weakScarlet, 1));
			frintezza.setRHandId(500); //fake weapon
			startQuestTimer("morph_03", 4000, weakScarlet, null, false);
		}
		else if (event.equalsIgnoreCase("morph_03"))
		{
			startQuestTimer("morph_04", 1500, weakScarlet, null, false);
		}
		else if (event.equalsIgnoreCase("morph_04"))
		{
			FRINTEZZA_LAIR.broadcastPacket(new SocialAction(weakScarlet, 4));
			L2Skill skill = SkillTable.getInstance().getInfo(DAEMON_MORPH, 1);
			if (skill != null)
				skill.getEffects(weakScarlet, weakScarlet);
			
			startQuestTimer("morph_end", 6000, weakScarlet, null, false);
			startQuestTimer("start_pc", 3000, weakScarlet, null, false);
			startQuestTimer("start_npc", 3000, weakScarlet, null, false);
			startQuestTimer("songs_play", 10000 + Rnd.get(10000), frintezza, null, false);
			startQuestTimer("skill02", 10000 + Rnd.get(10000), weakScarlet, null, false);
		}
		else if (event.equalsIgnoreCase("morph_05a"))
		{
			FRINTEZZA_LAIR.broadcastPacket(new SocialAction(frintezza, 4));
		}
		else if (event.equalsIgnoreCase("morph_05"))
		{
			FRINTEZZA_LAIR.broadcastPacket(new SpecialCamera(frintezza.getObjectId(), 250, 120, 15, 0, 1000, 0, 0, 1, 0));
			startQuestTimer("morph_06", 0, frintezza, null, false);
		}
		else if (event.equalsIgnoreCase("morph_06"))
		{
			FRINTEZZA_LAIR.broadcastPacket(new SpecialCamera(frintezza.getObjectId(), 250, 120, 15, 0, 10000, 0, 0, 1, 0));
			
			cancelQuestTimers("loc_check");
			
			_Scarlet_x = weakScarlet.getX();
			_Scarlet_y = weakScarlet.getY();
			_Scarlet_z = weakScarlet.getZ();
			_Scarlet_h = weakScarlet.getHeading();
			weakScarlet.deleteMe();
			weakScarlet = null;
			activeScarlet = null;
			weakScarlet = (L2GrandBossInstance) addSpawn(SCARLET1, _Scarlet_x, _Scarlet_y, _Scarlet_z, _Scarlet_h, false, 0, false);
			weakScarlet.setIsInvul(true);
			weakScarlet.setIsImmobilized(true);
			weakScarlet.disableAllSkills();
			weakScarlet.setRHandId(7903);
			FRINTEZZA_LAIR.updateKnownList(weakScarlet);
			
			startQuestTimer("morph_07", 7000, frintezza, null, false);
		}
		else if (event.equalsIgnoreCase("morph_07"))
		{
			FRINTEZZA_LAIR.broadcastPacket(new MagicSkillUse(frintezza, frintezza, 5006, 1, 34000, 0));
			FRINTEZZA_LAIR.broadcastPacket(new SpecialCamera(frintezza.getObjectId(), 500, 70, 15, 3000, 10000, 0, 0, 1, 0));
			startQuestTimer("morph_08", 3000, frintezza, null, false);
		}
		else if (event.equalsIgnoreCase("morph_08"))
		{
			FRINTEZZA_LAIR.broadcastPacket(new SpecialCamera(frintezza.getObjectId(), 2500, 90, 12, 6000, 10000, 0, 0, 1, 0));
			startQuestTimer("morph_09", 3000, frintezza, null, false);
		}
		else if (event.equalsIgnoreCase("morph_09"))
		{
			FRINTEZZA_LAIR.broadcastPacket(new SpecialCamera(weakScarlet.getObjectId(), 250, _Angle, 12, 0, 1000, 0, 0, 1, 0));
			startQuestTimer("morph_10", 0, weakScarlet, null, false);
		}
		else if (event.equalsIgnoreCase("morph_10"))
		{
			FRINTEZZA_LAIR.broadcastPacket(new SpecialCamera(weakScarlet.getObjectId(), 250, _Angle, 12, 0, 10000, 0, 0, 1, 0));
			startQuestTimer("morph_11", 500, weakScarlet, null, false);
		}
		else if (event.equalsIgnoreCase("morph_11"))
		{
			weakScarlet.doDie(weakScarlet);
			FRINTEZZA_LAIR.broadcastPacket(new SpecialCamera(weakScarlet.getObjectId(), 450, _Angle, 14, 8000, 8000, 0, 0, 1, 0));
			
			startQuestTimer("morph_12", 6250, weakScarlet, null, false);
			startQuestTimer("morph_13", 7200, weakScarlet, null, false);
		}
		else if (event.equalsIgnoreCase("morph_12"))
		{
			weakScarlet.deleteMe();
			weakScarlet = null;
		}
		else if (event.equalsIgnoreCase("morph_13"))
		{
			strongScarlet = (L2GrandBossInstance) addSpawn(SCARLET2, _Scarlet_x, _Scarlet_y, _Scarlet_z, _Scarlet_h, false, 0, false);
			strongScarlet.setIsInvul(true);
			strongScarlet.setIsImmobilized(true);
			strongScarlet.disableAllSkills();
			FRINTEZZA_LAIR.updateKnownList(strongScarlet);
			activeScarlet = strongScarlet;
			
			FRINTEZZA_LAIR.broadcastPacket(new SpecialCamera(strongScarlet.getObjectId(), 450, _Angle, 12, 500, 14000, 0, 0, 1, 0));
			
			startQuestTimer("morph_14", 3000, strongScarlet, null, false);
			startQuestTimer("loc_check", 60000, strongScarlet, null, true);
		}
		else if (event.equalsIgnoreCase("morph_14"))
		{
			startQuestTimer("morph_15", 5100, strongScarlet, null, false);
		}
		else if (event.equalsIgnoreCase("morph_15"))
		{
			FRINTEZZA_LAIR.broadcastPacket(new SocialAction(strongScarlet, 2));
			L2Skill skill = SkillTable.getInstance().getInfo(0, 1);
			if (skill != null)
				skill.getEffects(strongScarlet, strongScarlet);
			
			startQuestTimer("morph_end", 9000, strongScarlet, null, false);
			startQuestTimer("start_pc", 6000, strongScarlet, null, false);
			startQuestTimer("start_npc", 6000, strongScarlet, null, false);
			startQuestTimer("songs_play", 10000 + Rnd.get(10000), frintezza, null, false);
			startQuestTimer("skill03", 10000 + Rnd.get(10000), strongScarlet, null, false);
		}
		else if (event.equalsIgnoreCase("morph_16"))
		{
			FRINTEZZA_LAIR.broadcastPacket(new SpecialCamera(strongScarlet.getObjectId(), 300, _Angle - 180, 5, 0, 7000, 0, 0, 1, 0));
			startQuestTimer("morph_17", 0, strongScarlet, null, false);
		}
		else if (event.equalsIgnoreCase("morph_17"))
		{
			FRINTEZZA_LAIR.broadcastPacket(new SpecialCamera(strongScarlet.getObjectId(), 200, _Angle, 85, 4000, 10000, 0, 0, 1, 0));
			startQuestTimer("morph_17b", 7400, frintezza, null, false);
			startQuestTimer("morph_18", 7500, frintezza, null, false);
		}
		else if (event.equalsIgnoreCase("morph_17b"))
		{
			frintezza.doDie(frintezza);
		}
		else if (event.equalsIgnoreCase("morph_18"))
		{
			FRINTEZZA_LAIR.broadcastPacket(new SpecialCamera(frintezza.getObjectId(), 100, 120, 5, 0, 7000, 0, 0, 1, 0));
			startQuestTimer("morph_19", 0, frintezza, null, false);
		}
		else if (event.equalsIgnoreCase("morph_19"))
		{
			FRINTEZZA_LAIR.broadcastPacket(new SpecialCamera(frintezza.getObjectId(), 100, 90, 5, 5000, 15000, 0, 0, 1, 0));
			startQuestTimer("morph_20", 7000, frintezza, null, false);
			startQuestTimer("spawn_cubes", 7000, frintezza, null, false);
		}
		else if (event.equalsIgnoreCase("morph_20"))
		{
			FRINTEZZA_LAIR.broadcastPacket(new MagicSkillUse(frintezza, frintezza, 5007, 2, 32000, 0));
			FRINTEZZA_LAIR.broadcastPacket(new ExShowScreenMessage(1,0,2,false,1,0,0,false,5000,true,"Frenetic Toccata"));
			FRINTEZZA_LAIR.broadcastPacket(new SpecialCamera(frintezza.getObjectId(), 900, 70, 15, 3000, 10000, 0, 0, 1, 0));
			startQuestTimer("start_pc", 7000, frintezza, null, false);
		}
		else if (event.equalsIgnoreCase("songs_play"))
		{
			if (frintezza != null && !frintezza.isDead() && _OnMorph == 0)
			{
				_OnSong = Rnd.get(1, 5);
				if (_OnSong == 1 && _ThirdMorph == 1 && strongScarlet.getCurrentHp() < strongScarlet.getMaxHp() * 0.6 && Rnd.get(100) < 80)
				{
					FRINTEZZA_LAIR.broadcastPacket(new MagicSkillUse(frintezza, frintezza, 5007, 1, 32000, 0));
					startQuestTimer("songs_effect", 5000, frintezza, null, false);
					startQuestTimer("songs_play", 32000 + Rnd.get(10000), frintezza, null, false);
				}
				else if (_OnSong == 2 || _OnSong == 3)
				{
					FRINTEZZA_LAIR.broadcastPacket(new MagicSkillUse(frintezza, frintezza, 5007, _OnSong, 32000, 0));
					startQuestTimer("songs_effect", 5000, frintezza, null, false);
					startQuestTimer("songs_play", 32000 + Rnd.get(10000), frintezza, null, false);
				}
				else if (_OnSong == 4 && _SecondMorph == 1)
				{
					FRINTEZZA_LAIR.broadcastPacket(new MagicSkillUse(frintezza, frintezza, 5007, 4, 31000, 0));
					startQuestTimer("songs_effect", 5000, frintezza, null, false);
					startQuestTimer("songs_play", 31000 + Rnd.get(10000), frintezza, null, false);
				}
				else if (_OnSong == 5 && _ThirdMorph == 1 && _Abnormal == 0)
				{
					_Abnormal = 1;
					FRINTEZZA_LAIR.broadcastPacket(new MagicSkillUse(frintezza, frintezza, 5007, 5, 35000, 0));
					startQuestTimer("songs_effect", 5000, frintezza, null, false);
					startQuestTimer("songs_play", 35000 + Rnd.get(10000), frintezza, null, false);
				}
				else
					startQuestTimer("songs_play", 5000 + Rnd.get(5000), frintezza, null, false);
			}
		}
		else if (event.equalsIgnoreCase("songs_effect"))
		{
			L2Skill skill = SkillTable.getInstance().getInfo(5008, _OnSong);
			if (skill == null)
				return null;
			
			if (_OnSong == 1 || _OnSong == 2 || _OnSong == 3)
			{
				if (frintezza != null && !frintezza.isDead() && activeScarlet != null && !activeScarlet.isDead())
					skill.getEffects(frintezza, activeScarlet);
			}
			else if (_OnSong == 4)
			{
				for (L2Character cha : FRINTEZZA_LAIR.getCharactersInside())
				{
					if (cha instanceof L2PcInstance && Rnd.get(100) < 80)
					{
						skill.getEffects(frintezza, cha);
						cha.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.YOU_FEEL_S1_EFFECT).addSkillName(5008, 4));
					}
				}
			}
			else if (_OnSong == 5)
			{
				for (L2Character cha : FRINTEZZA_LAIR.getCharactersInside())
				{
					if (cha instanceof L2PcInstance && Rnd.get(100) < 70)
					{
						cha.abortAttack();
						cha.abortCast();
						cha.disableAllSkills();
						cha.stopMove(null);
						cha.setIsParalyzed(true);
						cha.setIsImmobilized(true);
						cha.getAI().setIntention(CtrlIntention.IDLE);
						skill.getEffects(frintezza, cha);
						cha.startAbnormalEffect(AbnormalEffect.DANCE_STUNNED);
						cha.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.YOU_FEEL_S1_EFFECT).addSkillName(5008, 5));
					}
				}
				startQuestTimer("stop_effect", 25000, frintezza, null, false);
			}
		}
		else if (event.equalsIgnoreCase("stop_effect"))
		{
			for (L2Character cha : FRINTEZZA_LAIR.getCharactersInside())
			{
				if (cha instanceof L2PcInstance)
				{
					cha.stopAbnormalEffect(AbnormalEffect.DANCE_STUNNED);
					cha.stopAbnormalEffect(AbnormalEffect.FLOATING_ROOT);
					cha.enableAllSkills();
					cha.setIsImmobilized(false);
					cha.setIsParalyzed(false);
				}
			}
			_Abnormal = 0;
		}
		else if (event.equalsIgnoreCase("attack_stop"))
		{
			cancelQuestTimers("skill01");
			cancelQuestTimers("skill02");
			cancelQuestTimers("skill03");
			cancelQuestTimers("songs_play");
			cancelQuestTimers("songs_effect");
			
			FRINTEZZA_LAIR.broadcastPacket(new MagicSkillCanceld(frintezza.getObjectId()));
		}
		else if (event.equalsIgnoreCase("check_hp"))
		{
			if (npc.isDead())
			{
				_OnMorph = 1;
				FRINTEZZA_LAIR.broadcastPacket(new PlaySound(1, "BS01_D", 1, npc.getObjectId(), npc.getX(), npc.getY(), npc.getZ()));
				
				startQuestTimer("attack_stop", 0, frintezza, null, false);
				startQuestTimer("stop_pc", 0, npc, null, false);
				startQuestTimer("stop_npc", 0, npc, null, false);
				startQuestTimer("morph_16", 0, npc, null, false);
			}
			else
			{
				_CheckDie = _CheckDie + 10;
				if (_CheckDie < 3000)
					startQuestTimer("check_hp", 10, npc, null, false);
				else
				{
					_OnCheck = 0;
					_CheckDie = 0;
				}
			}
		}
		else if (event.equalsIgnoreCase("skill01"))
		{
			if (weakScarlet != null && !weakScarlet.isDead() && _SecondMorph == 0 && _ThirdMorph == 0 && _OnMorph == 0)
			{
				int i = Rnd.get(0, 1);
				L2Skill skill = SkillTable.getInstance().getInfo(SKILLS[i][0], SKILLS[i][1]);
				if (skill != null)
				{
					weakScarlet.stopMove(null);
					weakScarlet.setIsCastingNow(true);
					weakScarlet.doCast(skill);
				}
				startQuestTimer("skill01", SKILLS[i][2] + 5000 + Rnd.get(10000), npc, null, false);
			}
		}
		else if (event.equalsIgnoreCase("skill02"))
		{
			if (weakScarlet != null && !weakScarlet.isDead() && _SecondMorph == 1 && _ThirdMorph == 0 && _OnMorph == 0)
			{
				int i = 0;
				if (_Abnormal == 0)
					i = Rnd.get(2, 5);
				else
					i = Rnd.get(2, 4);
				
				L2Skill skill = SkillTable.getInstance().getInfo(SKILLS[i][0], SKILLS[i][1]);
				if (skill != null)
				{
					weakScarlet.stopMove(null);
					weakScarlet.setIsCastingNow(true);
					weakScarlet.doCast(skill);
				}
				startQuestTimer("skill02", SKILLS[i][2] + 5000 + Rnd.get(10000), npc, null, false);
				
				if (i == 5)
				{
					_Abnormal = 1;
					startQuestTimer("float_effect", 4000, weakScarlet, null, false);
				}
			}
		}
		else if (event.equalsIgnoreCase("skill03"))
		{
			if (strongScarlet != null && !strongScarlet.isDead() && _SecondMorph == 1 && _ThirdMorph == 1 && _OnMorph == 0)
			{
				int i = 0;
				if (_Abnormal == 0)
					i = Rnd.get(6, 10);
				else
					i = Rnd.get(6, 9);
				
				L2Skill skill = SkillTable.getInstance().getInfo(SKILLS[i][0], SKILLS[i][1]);
				if (skill != null)
				{
					strongScarlet.stopMove(null);
					strongScarlet.setIsCastingNow(true);
					strongScarlet.doCast(skill);
				}
				startQuestTimer("skill03", SKILLS[i][2] + 5000 + Rnd.get(10000), npc, null, false);
				
				if (i == 10)
				{
					_Abnormal = 1;
					startQuestTimer("float_effect", 3000, npc, null, false);
				}
			}
		}
		else if (event.equalsIgnoreCase("float_effect"))
		{
			if (npc.isCastingNow())
			{
				startQuestTimer("float_effect", 500, npc, null, false);
			}
			else
			{
				for (L2Character cha : FRINTEZZA_LAIR.getCharactersInside())
				{
					if (cha instanceof L2PcInstance)
					{
						if (cha.getFirstEffect(5016) != null)
						{
							cha.abortAttack();
							cha.abortCast();
							cha.disableAllSkills();
							cha.stopMove(null);
							cha.setIsParalyzed(true);
							cha.setIsImmobilized(true);
							cha.getAI().setIntention(CtrlIntention.IDLE);
							cha.startAbnormalEffect(AbnormalEffect.FLOATING_ROOT);
						}
					}
				}
				startQuestTimer("stop_effect", 25000, npc, null, false);
			}
		}
		else if (event.equalsIgnoreCase("action"))
		{
			FRINTEZZA_LAIR.broadcastPacket(new SocialAction(npc, 1));
		}
		else if (event.equalsIgnoreCase("bomber"))
		{
			_Bomber = 0;
		}
		else if (event.equalsIgnoreCase("room_final"))
		{
			FRINTEZZA_LAIR.broadcastPacket(new NpcSay(npc.getObjectId(), 1, npc.getNpcId(), "Exceeded his time limit, challenge failed!"));
			FRINTEZZA_LAIR.oustAllPlayers();
			
			cancelQuestTimers("waiting");
			cancelQuestTimers("frintezza_despawn");
			startQuestTimer("clean", 1000, npc, null, false);
			startQuestTimer("close", 1000, npc, null, false);
			startQuestTimer("room1_del", 1000, npc, null, false);
			startQuestTimer("room2_del", 1000, npc, null, false);
			
			GrandBossManager.getInstance().setBossStatus(FRINTEZZA, DORMANT);
		}
		else if (event.equalsIgnoreCase("frintezza_despawn"))
		{
			temp = (System.currentTimeMillis() - _LastAction);
			if (temp > Config.DESPAWN_TIME_FRINTEZZA)
			{
				FRINTEZZA_LAIR.oustAllPlayers();
				
				cancelQuestTimers("waiting");
				cancelQuestTimers("loc_check");
				cancelQuestTimers("room_final");
				cancelQuestTimers("spawn_minion");
				startQuestTimer("clean", 1000, npc, null, false);
				startQuestTimer("close", 1000, npc, null, false);
				startQuestTimer("attack_stop", 1000, npc, null, false);
				startQuestTimer("room1_del", 1000, npc, null, false);
				startQuestTimer("room2_del", 1000, npc, null, false);
				startQuestTimer("room3_del", 1000, npc, null, false);
				startQuestTimer("minions_despawn", 1000, npc, null, false);
				
				GrandBossManager.getInstance().setBossStatus(FRINTEZZA, DORMANT);
				
				cancelQuestTimers("frintezza_despawn");
			}
		}
		else if (event.equalsIgnoreCase("minions_despawn"))
		{
			for (int i = 0; i < Minions.size(); i++)
			{
				L2Attackable mob = Minions.get(i);
				if (mob != null)
					mob.decayMe();
			}
			Minions.clear();
		}
		else if (event.equalsIgnoreCase("spawn_minion"))
		{
			if (npc != null && !npc.isDead() && frintezza != null && !frintezza.isDead())
			{
				L2Npc mob = addSpawn(npc.getNpcId() + 2, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading(), false, 0, false);
				((L2Attackable) mob).setIsRaidMinion(true);
				Minions.add((L2Attackable) mob);
				startQuestTimer("action", 200, mob, null, false);
				startQuestTimer("spawn_minion", 18000, npc, null, false);
			}
		}
		else if (event.equalsIgnoreCase("spawn_cubes"))
		{
			addSpawn(CUBE, 174232, -88020, -5114, 16384, false, 900000, false);
		}
		else if (event.equalsIgnoreCase("frintezza_unlock"))
		{
			GrandBossManager.getInstance().setBossStatus(FRINTEZZA, DORMANT);
		}
		else if (event.equalsIgnoreCase("remove_players"))
		{
			FRINTEZZA_LAIR.oustAllPlayers();
		}
		
		return super.onAdvEvent(event, npc, player);
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		if (npc.getNpcId() == CUBE)
		{
			int x = 150037 + Rnd.get(500);
			int y = -57720 + Rnd.get(500);
			player.teleToLocation(x, y, -2976, 0);
			return null;
		}
		
		String htmltext = "";
		
		Integer status = GrandBossManager.getInstance().getBossStatus(FRINTEZZA);
		
		if (status == DEAD)
		{
			htmltext = "<html><body>There is nothing beyond the Magic Force Field. Come back later.<br>(You may not enter because Frintezza is not inside the Imperial Tomb.)</body></html>";
		}
		else if (status == DORMANT)
		{
			boolean party_check_success = true;
			
			if (!Config.BYPASS_FRINTEZZA_PARTIES_CHECK)
			{
				
				if ((!player.isInParty() || !player.getParty().isLeader(player)) || (player.getParty().getCommandChannel() == null) || (player.getParty().getCommandChannel().getChannelLeader() != player))
				{
					htmltext = "<html><body>No reaction. Contact must be initiated by the Command Channel Leader.</body></html>";
					party_check_success = false;
				}
				else if (player.getParty().getCommandChannel().getPartys().size() < Config.FRINTEZZA_MIN_PARTIES || player.getParty().getCommandChannel().getPartys().size() > Config.FRINTEZZA_MAX_PARTIES)
				{
					htmltext = "<html><body>Your command channel needs to have at least " + Config.FRINTEZZA_MIN_PARTIES + " parties and a maximum of " + Config.FRINTEZZA_MAX_PARTIES + ".</body></html>";
					party_check_success = false;
				}
				
			}
			
			if (party_check_success)
			{
				
				if (player.getInventory().getItemByItemId(8073) == null)
				{
					htmltext = "<html><body>You dont have required item.</body></html>";
				}
				else
				{
					player.destroyItemByItemId("Quest", 8073, 1, player, true);
					GrandBossManager.getInstance().setBossStatus(FRINTEZZA, WAITING);
					
					startQuestTimer("close", 0, npc, null, false);
					startQuestTimer("room1_spawn", 5000, npc, null, false);
					startQuestTimer("room_final", Config.FRINTEZZA_TIME_CHALLENGE, null, null, false);
					startQuestTimer("frintezza_despawn", 60000, null, null, true);
					
					_LastAction = System.currentTimeMillis();
					
					if (Config.BYPASS_FRINTEZZA_PARTIES_CHECK)
					{
						
						if (player.getParty() != null)
						{
							
							L2CommandChannel CC = player.getParty().getCommandChannel();
							
							if (CC != null)
							{ // teleport all parties into CC reb12
								
								for (L2Party party : CC.getPartys())
								{
									if (party == null)
										continue;
									
									synchronized (_PlayersInside)
									{
										
										for (L2PcInstance member : party.getPartyMembers())
										{
											if (member == null || member.getLevel() < 74)
												continue;
											if (!member.isInsideRadius(npc, 700, false, false))
												continue;
											if (_PlayersInside.size() > 45)
											{
												member.sendMessage("The number of challenges have been full, so can not enter.");
												break;
											}
											_PlayersInside.add(member);
											FRINTEZZA_LAIR.allowPlayerEntry(member, 300);
											member.teleToLocation(INVADE_LOC[_LocCycle][0] + Rnd.get(50), INVADE_LOC[_LocCycle][1] + Rnd.get(50), INVADE_LOC[_LocCycle][2], 0);
										}
										if (_PlayersInside.size() > 45)
											break;
										
									}
									
									_LocCycle++;
									if (_LocCycle >= 6)
										_LocCycle = 1;
								}
								
							}
							else
							{ // teleport just actual party reb12
								
								L2Party party = player.getParty();
								
								for (L2PcInstance member : party.getPartyMembers())
								{
									if (member == null || member.getLevel() < 74)
										continue;
									if (!member.isInsideRadius(npc, 700, false, false))
										continue;
									
									synchronized (_PlayersInside)
									{
										if (_PlayersInside.size() > 45)
										{
											member.sendMessage("The number of challenges have been full, so can not enter.");
											break;
										}
										_PlayersInside.add(member);
									}
									
									FRINTEZZA_LAIR.allowPlayerEntry(member, 300);
									member.teleToLocation(INVADE_LOC[_LocCycle][0] + Rnd.get(50), INVADE_LOC[_LocCycle][1] + Rnd.get(50), INVADE_LOC[_LocCycle][2], 0);
								}
								
								_LocCycle++;
								if (_LocCycle >= 6)
									_LocCycle = 1;
								
							}
							
						}
						else
						{ // teleport just player reb12
							
							if (player.isInsideRadius(npc, 700, false, false))
							{
								
								synchronized (_PlayersInside)
								{
									_PlayersInside.add(player);
									
								}
								player.teleToLocation(INVADE_LOC[_LocCycle][0] + Rnd.get(50), INVADE_LOC[_LocCycle][1] + Rnd.get(50), INVADE_LOC[_LocCycle][2], 0);
								
							}
							
						}
						
					}
					else
					{
						
						L2CommandChannel CC = player.getParty().getCommandChannel();
						
						for (L2Party party : CC.getPartys())
						{
							if (party == null)
								continue;
							
							synchronized (_PlayersInside)
							{
								for (L2PcInstance member : party.getPartyMembers())
								{
									if (member == null || member.getLevel() < 74)
										continue;
									if (!member.isInsideRadius(npc, 700, false, false))
										continue;
									if (_PlayersInside.size() > 45)
									{
										member.sendMessage("The number of challenges have been full, so can not enter.");
										break;
									}
									_PlayersInside.add(member);
									FRINTEZZA_LAIR.allowPlayerEntry(member, 300);
									member.teleToLocation(INVADE_LOC[_LocCycle][0] + Rnd.get(50), INVADE_LOC[_LocCycle][1] + Rnd.get(50), INVADE_LOC[_LocCycle][2], 0);
								}
								if (_PlayersInside.size() > 45)
									break;
								
							}
							
							_LocCycle++;
							if (_LocCycle >= 6)
								_LocCycle = 1;
						}
						
					}
					
				}
				
			}
			
		}
		else
			htmltext = "<html><body>Someone else is already inside the Magic Force Field. Try again later.</body></html>";
		
		return htmltext;
	}
	
	@Override
	public String onAttack(L2Npc npc, L2PcInstance attacker, int damage, boolean isPet, L2Skill skill)
	{
		_LastAction = System.currentTimeMillis();
		if (npc.getNpcId() == FRINTEZZA)
		{
			npc.setCurrentHpMp(npc.getMaxHp(), 0);
			return null;
		}
		if (npc.getNpcId() == SCARLET1 && _SecondMorph == 0 && _ThirdMorph == 0 && _OnMorph == 0 && npc.getCurrentHp() < npc.getMaxHp() * 0.75 && GrandBossManager.getInstance().getBossStatus(FRINTEZZA) == FIGHTING)
		{
			startQuestTimer("attack_stop", 0, frintezza, null, false);
			
			_SecondMorph = 1;
			_OnMorph = 1;
			
			startQuestTimer("stop_pc", 1000, npc, null, false);
			startQuestTimer("stop_npc", 1000, npc, null, false);
			startQuestTimer("morph_01", 1100, npc, null, false);
		}
		else if (npc.getNpcId() == SCARLET1 && _SecondMorph == 1 && _ThirdMorph == 0 && _OnMorph == 0 && npc.getCurrentHp() < npc.getMaxHp() * 0.5 && GrandBossManager.getInstance().getBossStatus(FRINTEZZA) == FIGHTING)
		{
			startQuestTimer("attack_stop", 0, frintezza, null, false);
			
			_ThirdMorph = 1;
			_OnMorph = 1;
			
			startQuestTimer("stop_pc", 2000, npc, null, false);
			startQuestTimer("stop_npc", 2000, npc, null, false);
			startQuestTimer("morph_05a", 2000, npc, null, false);
			startQuestTimer("morph_05", 2100, npc, null, false);
		}
		else if (npc.getNpcId() == SCARLET2 && _SecondMorph == 1 && _ThirdMorph == 1 && _OnCheck == 0 && damage >= npc.getCurrentHp() && GrandBossManager.getInstance().getBossStatus(FRINTEZZA) == FIGHTING)
		{
			_OnCheck = 1;
			startQuestTimer("check_hp", 0, npc, null, false);
		}
		else if ((npc.getNpcId() == BREATH_OF_HALISHA || npc.getNpcId() == BREATH_OF_HALISHA2) && _Bomber == 0)
		{
			if (npc.getCurrentHp() < npc.getMaxHp() * 0.1)
			{
				if (Rnd.get(100) < 30)
				{
					_Bomber = 1;
					startQuestTimer("bomber", 3000, npc, null, false);
					
					L2Skill skill2 = SkillTable.getInstance().getInfo(5011, 1);
					if (skill2 != null)
					{
						npc.doCast(skill2);
					}
				}
			}
		}
		
		return super.onAttack(npc, attacker, damage, isPet, skill);
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isPet)
	{
		if (npc.getNpcId() == SCARLET2)
		{
			FRINTEZZA_LAIR.broadcastPacket(new PlaySound(1, "BS01_D", 1, npc.getObjectId(), npc.getX(), npc.getY(), npc.getZ()));
			
			startQuestTimer("stop_pc", 0, null, null, false);
			startQuestTimer("stop_npc", 0, npc, null, false);
			startQuestTimer("morph_16", 0, npc, null, false);
			
			GrandBossManager.getInstance().setBossStatus(FRINTEZZA, DEAD);
			long respawnTime = (long) Config.SPAWN_INTERVAL_FRINTEZZA + Rnd.get(-Config.RANDOM_SPAWN_TIME_FRINTEZZA, Config.RANDOM_SPAWN_TIME_FRINTEZZA);
			respawnTime *= 3600000;
			
			cancelQuestTimers("spawn_minion");
			cancelQuestTimers("frintezza_despawn");
			startQuestTimer("close", 0, null, null, false);
			startQuestTimer("rooms_del", 0, npc, null, false);
			startQuestTimer("minions_despawn", 0, null, null, false);
			startQuestTimer("remove_players", 900000, null, null, false);
			startQuestTimer("frintezza_unlock", respawnTime, null, null, false);
			
			StatsSet info = GrandBossManager.getInstance().getStatsSet(FRINTEZZA);
			info.set("respawn_time", System.currentTimeMillis() + respawnTime);
			GrandBossManager.getInstance().setStatsSet(FRINTEZZA, info);
		}
		else if (npc.getNpcId() == HALL_ALARM_DEVICE)
		{
			_KillHallAlarmDevice++;
			if (_KillHallAlarmDevice == 3) // open walls reb12
			{
				for (int i = 25150051; i <= 25150058; i++)
					DoorTable.getInstance().getDoor(i).openMe();
			}
			else if (_KillHallAlarmDevice == 4)
			{
				startQuestTimer("room1_del", 100, npc, null, false);
				startQuestTimer("room2_spawn", 100, npc, null, false);
				DoorTable.getInstance().getDoor(25150042).openMe();
				DoorTable.getInstance().getDoor(25150043).openMe();
			}
		}
		else if (npc.getNpcId() == DARK_CHOIR_PLAYER)
		{
			_KillDarkChoirPlayer++;
			if (_KillDarkChoirPlayer == 2)
			{
				DoorTable.getInstance().getDoor(25150042).closeMe();
				DoorTable.getInstance().getDoor(25150043).closeMe();
				
				for (int i = 25150061; i <= 25150070; i++)
					DoorTable.getInstance().getDoor(i).openMe();
				
				startQuestTimer("room2_spawn2", 1000, npc, null, false);
			}
		}
		else if (npc.getNpcId() == DARK_CHOIR_CAPTAIN)
		{
			_KillDarkChoirCaptain++;
			if (_KillDarkChoirCaptain == 8)
			{
				startQuestTimer("room2_del", 100, npc, null, false);
				
				DoorTable.getInstance().getDoor(25150045).openMe();
				DoorTable.getInstance().getDoor(25150046).openMe();
				
				startQuestTimer("waiting", Config.WAIT_TIME_FRINTEZZA, npc, null, false);
				cancelQuestTimers("room_final");
			}
		}
		
		return super.onKill(npc, killer, isPet);
	}
}