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
package net.xcine.gameserver.ai.special.manager;

import java.util.logging.Logger;

import net.xcine.gameserver.ai.special.Antharas;
import net.xcine.gameserver.ai.special.Baium_l2j;
import net.xcine.gameserver.ai.special.Barakiel;
import net.xcine.gameserver.ai.special.Core;
import net.xcine.gameserver.ai.special.FairyTrees;
import net.xcine.gameserver.ai.special.Frintezza_l2j;
import net.xcine.gameserver.ai.special.Golkonda;
import net.xcine.gameserver.ai.special.Gordon;
import net.xcine.gameserver.ai.special.Hallate;
import net.xcine.gameserver.ai.special.IceFairySirra;
import net.xcine.gameserver.ai.special.Kernon;
import net.xcine.gameserver.ai.special.Monastery_l2j;
import net.xcine.gameserver.ai.special.Orfen;
import net.xcine.gameserver.ai.special.QueenAnt;
import net.xcine.gameserver.ai.special.SummonMinions;
import net.xcine.gameserver.ai.special.Transform;
import net.xcine.gameserver.ai.special.Valakas_l2j;
import net.xcine.gameserver.ai.special.VanHalter;
import net.xcine.gameserver.ai.special.VarkaKetraAlly;
import net.xcine.gameserver.ai.special.Zaken_l2j;
import net.xcine.gameserver.ai.special.ZombieGatekeepers;
import net.xcine.gameserver.thread.ThreadPoolManager;

/**
 * @author qwerty
 */

public class AILoader
{
	private static final Logger _log = Logger.getLogger(AILoader.class.getName());

	public static void init()
	{
		_log.info("AI load:");
		ThreadPoolManager.getInstance().scheduleAi(new Antharas(-1, "antharas", "ai"), 100);
		ThreadPoolManager.getInstance().scheduleAi(new Baium_l2j(-1, "baium", "ai"), 200);
		ThreadPoolManager.getInstance().scheduleAi(new Core(-1, "core", "ai"), 300);
		ThreadPoolManager.getInstance().scheduleAi(new QueenAnt(-1, "queen_ant", "ai"), 400);
		ThreadPoolManager.getInstance().scheduleAi(new VanHalter(-1, "vanhalter", "ai"), 500);
		ThreadPoolManager.getInstance().scheduleAi(new Gordon(-1, "Gordon", "ai"), 600);
		ThreadPoolManager.getInstance().scheduleAi(new Monastery_l2j(-1, "monastery", "ai"), 700);
		ThreadPoolManager.getInstance().scheduleAi(new Transform(-1, "transform", "ai"), 800);
		ThreadPoolManager.getInstance().scheduleAi(new FairyTrees(-1, "FairyTrees", "ai"), 900);
		ThreadPoolManager.getInstance().scheduleAi(new SummonMinions(-1, "SummonMinions", "ai"), 1000);
		ThreadPoolManager.getInstance().scheduleAi(new ZombieGatekeepers(-1, "ZombieGatekeepers", "ai"), 1100);
		ThreadPoolManager.getInstance().scheduleAi(new IceFairySirra(-1, "IceFairySirra", "ai"), 1200);
		ThreadPoolManager.getInstance().scheduleAi(new Golkonda(-1, "Golkonda", "ai"), 1300);
		ThreadPoolManager.getInstance().scheduleAi(new Hallate(-1, "Hallate", "ai"), 1400);
		ThreadPoolManager.getInstance().scheduleAi(new Kernon(-1, "Kernon", "ai"), 1500);
		ThreadPoolManager.getInstance().scheduleAi(new VarkaKetraAlly(-1, "Varka Ketra Ally", "ai"), 1600);
		ThreadPoolManager.getInstance().scheduleAi(new Barakiel(-1, "Barakiel", "ai"), 1700);
		ThreadPoolManager.getInstance().scheduleAi(new Orfen(-1, "Orfen", "ai"), 1800);
		ThreadPoolManager.getInstance().scheduleAi(new Zaken_l2j(-1, "Zaken", "ai"), 1900);
		ThreadPoolManager.getInstance().scheduleAi(new Frintezza_l2j(-1, "Frintezza", "ai"), 2000);
		ThreadPoolManager.getInstance().scheduleAi(new Valakas_l2j(-1, "valakas", "ai"), 2100);
		_log.info("Ai Loads is Successfully.");
	}
}
