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
package net.sf.l2j.gameserver.scripting.quests;

import net.sf.l2j.gameserver.scripting.quests.SagasScripts.SagasSuperClass;

public class Q100_SagaOfTheMaestro extends SagasSuperClass
{
	public Q100_SagaOfTheMaestro()
	{
		super(100, "Saga of the Maestro");
		
		NPC = new int[]
		{
			31592,
			31273,
			31597,
			31597,
			31596,
			31646,
			31648,
			31653,
			31654,
			31655,
			31656,
			31597
		};
		
		Items = new int[]
		{
			7080,
			7607,
			7081,
			7515,
			7298,
			7329,
			7360,
			7391,
			7422,
			7453,
			7108,
			0
		};
		
		Mob = new int[]
		{
			27260,
			27249,
			27308
		};
		
		classid = 118;
		prevclass = 0x39;
		
		X = new int[]
		{
			164650,
			47429,
			47391
		};
		
		Y = new int[]
		{
			-74121,
			-56923,
			-56929
		};
		
		Z = new int[]
		{
			-2871,
			-2383,
			-2370
		};
		
		registerNPCs();
	}
}