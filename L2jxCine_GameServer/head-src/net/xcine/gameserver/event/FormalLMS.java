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
package net.xcine.gameserver.event;


import net.xcine.gameserver.datatables.sql.ItemTable;
import net.xcine.gameserver.model.actor.instance.L2ItemInstance;

/**
 * 
 * 
 * @author Rizel
 */
public class FormalLMS
{
	private L2ItemInstance formalWear;
	
	private static class SingletonHolder
	{
		protected static final FormalLMS _instance = new FormalLMS();
	}

	public static FormalLMS getInstance()
	{
		return SingletonHolder._instance;
	}
	
	public FormalLMS()
	{
		formalWear = ItemTable.getInstance().createItem("", 6408, 1, null, null);
	}
	
	public int getOID()
	{
		return formalWear.getObjectId();
	}
}