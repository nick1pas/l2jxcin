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
package net.xcine.gameserver.handler.custom;

import java.util.logging.Level;
import java.util.logging.Logger;

import net.xcine.Config;
import net.xcine.gameserver.handler.ICustomByPassHandler;
import net.xcine.gameserver.handler.IItemHandler;
import net.xcine.gameserver.handler.ItemHandler;
import net.xcine.gameserver.handler.itemhandlers.ExtractableItems;
import net.xcine.gameserver.model.actor.instance.L2ItemInstance;
import net.xcine.gameserver.model.actor.instance.L2PcInstance;

/**
 * @author Nick
 */
public class ExtractableByPassHandler implements ICustomByPassHandler
{
	protected static final Logger _log = Logger.getLogger(ExtractableByPassHandler.class.getName());
	private static String[] _IDS =
	{
			"extractOne", "extractAll"
	};

	@Override
	public String[] getByPassCommands()
	{
		return _IDS;
	}

	// custom_extractOne <objectID> custom_extractAll <objectID>  
	@Override
	public void handleCommand(String command, L2PcInstance player, String parameters)
	{
		try
		{
			int objId = Integer.parseInt(parameters);
			L2ItemInstance item = player.getInventory().getItemByObjectId(objId);
			if(item == null)
				return;
			IItemHandler ih = ItemHandler.getInstance().getItemHandler(item.getItemId());
			if(ih == null || !(ih instanceof ExtractableItems))
				return;
			if(command.compareTo(_IDS[0]) == 0)
			{
				((ExtractableItems) ih).doExtract(player, item, 1);
			}
			else if(command.compareTo(_IDS[1]) == 0)
			{
				((ExtractableItems) ih).doExtract(player, item, item.getCount());
			}
		}
		catch(Exception e)
		{
			if(Config.ENABLE_ALL_EXCEPTIONS)
				e.printStackTrace();
			
			_log.log(Level.WARNING, "ExtractableByPassHandler: Error while running ", e);
		}

	}

}
