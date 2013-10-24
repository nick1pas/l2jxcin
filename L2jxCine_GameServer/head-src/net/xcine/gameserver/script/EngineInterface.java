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
package net.xcine.gameserver.script;

import net.xcine.gameserver.controllers.GameTimeController;
import net.xcine.gameserver.controllers.RecipeController;
import net.xcine.gameserver.datatables.SkillTable;
import net.xcine.gameserver.datatables.csv.MapRegionTable;
import net.xcine.gameserver.datatables.sql.CharNameTable;
import net.xcine.gameserver.datatables.sql.CharTemplateTable;
import net.xcine.gameserver.datatables.sql.ClanTable;
import net.xcine.gameserver.datatables.sql.ItemTable;
import net.xcine.gameserver.datatables.sql.LevelUpData;
import net.xcine.gameserver.datatables.sql.NpcTable;
import net.xcine.gameserver.datatables.sql.SkillTreeTable;
import net.xcine.gameserver.datatables.sql.SpawnTable;
import net.xcine.gameserver.datatables.sql.TeleportLocationTable;
import net.xcine.gameserver.idfactory.IdFactory;
import net.xcine.gameserver.model.L2World;
import net.xcine.gameserver.model.entity.Announcements;

/**
 * @author Luis Arias
 */
public interface EngineInterface
{
	//*  keep the references of Singletons to prevent garbage collection
	public CharNameTable charNametable = CharNameTable.getInstance();

	public IdFactory idFactory = IdFactory.getInstance();
	public ItemTable itemTable = ItemTable.getInstance();

	public SkillTable skillTable = SkillTable.getInstance();

	public RecipeController recipeController = RecipeController.getInstance();

	public SkillTreeTable skillTreeTable = SkillTreeTable.getInstance();
	public CharTemplateTable charTemplates = CharTemplateTable.getInstance();
	public ClanTable clanTable = ClanTable.getInstance();

	public NpcTable npcTable = NpcTable.getInstance();

	public TeleportLocationTable teleTable = TeleportLocationTable.getInstance();
	public LevelUpData levelUpData = LevelUpData.getInstance();
	public L2World world = L2World.getInstance();
	public SpawnTable spawnTable = SpawnTable.getInstance();
	public GameTimeController gameTimeController = GameTimeController.getInstance();
	public Announcements announcements = Announcements.getInstance();
	public MapRegionTable mapRegions = MapRegionTable.getInstance();

	//public ArrayList getAllPlayers();
	//public Player getPlayer(String characterName);
	public void addQuestDrop(int npcID, int itemID, int min, int max, int chance, String questID, String[] states);

	public void addEventDrop(int[] items, int[] count, double chance, DateRange range);

	public void onPlayerLogin(String[] message, DateRange range);

}
