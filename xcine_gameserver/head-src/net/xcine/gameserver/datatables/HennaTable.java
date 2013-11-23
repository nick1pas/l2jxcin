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
package net.xcine.gameserver.datatables;

import gnu.trove.map.hash.TIntObjectHashMap;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.xcine.gameserver.templates.StatsSet;
import net.xcine.gameserver.templates.item.L2Henna;
import net.xcine.gameserver.xmlfactory.XMLDocumentFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class HennaTable
{
	private static Logger _log = Logger.getLogger(HennaTable.class.getName());
	
	private static final L2Henna[] EMPTY_HENNAS = new L2Henna[0];
	
	private final TIntObjectHashMap<L2Henna> _henna;
	private final TIntObjectHashMap<List<L2Henna>> _hennaTrees;
	
	protected HennaTable()
	{
		_henna = new TIntObjectHashMap<>();
		_hennaTrees = new TIntObjectHashMap<>();
		
		restoreHennaData();
	}
	
	private void restoreHennaData()
	{
		try
		{
			final File f = new File("./data/xml/henna.xml");
			final Document doc = XMLDocumentFactory.getInstance().loadDocument(f);
			final Node n = doc.getFirstChild();
			
			for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling())
			{
				if (!d.getNodeName().equalsIgnoreCase("henna"))
					continue;
				
				final StatsSet hennaDat = new StatsSet();
				final Integer id = Integer.valueOf(d.getAttributes().getNamedItem("symbol_id").getNodeValue());
				
				hennaDat.set("symbol_id", id);
				
				hennaDat.set("dye", Integer.valueOf(d.getAttributes().getNamedItem("dye_id").getNodeValue()));
				hennaDat.set("price", Integer.valueOf(d.getAttributes().getNamedItem("price").getNodeValue()));
				
				hennaDat.set("INT", Integer.valueOf(d.getAttributes().getNamedItem("INT").getNodeValue()));
				hennaDat.set("STR", Integer.valueOf(d.getAttributes().getNamedItem("STR").getNodeValue()));
				hennaDat.set("CON", Integer.valueOf(d.getAttributes().getNamedItem("CON").getNodeValue()));
				hennaDat.set("MEN", Integer.valueOf(d.getAttributes().getNamedItem("MEN").getNodeValue()));
				hennaDat.set("DEX", Integer.valueOf(d.getAttributes().getNamedItem("DEX").getNodeValue()));
				hennaDat.set("WIT", Integer.valueOf(d.getAttributes().getNamedItem("WIT").getNodeValue()));
				final String[] classes = d.getAttributes().getNamedItem("classes").getNodeValue().split(",");
				
				final L2Henna template = new L2Henna(hennaDat);
				_henna.put(id, template);
				
				for (String clas : classes)
				{
					final Integer classId = Integer.valueOf(clas);
					if (!_hennaTrees.containsKey(classId))
					{
						List<L2Henna> list = new ArrayList<>();
						list.add(template);
						_hennaTrees.put(classId, list);
					}
					else
						_hennaTrees.get(classId).add(template);
				}
			}
		}
		catch (Exception e)
		{
			_log.log(Level.WARNING, "HennaTable: Error loading from database:" + e.getMessage(), e);
		}
		_log.config("HennaTable: Loaded " + _henna.size() + " templates.");
	}
	
	public L2Henna getTemplate(int id)
	{
		return _henna.get(id);
	}
	
	public L2Henna[] getAvailableHenna(int classId)
	{
		final List<L2Henna> henna = _hennaTrees.get(classId);
		if (henna == null || henna.isEmpty())
			return EMPTY_HENNAS;
		
		return henna.toArray(new L2Henna[henna.size()]);
	}
	
	public static HennaTable getInstance()
	{
		return SingletonHolder._instance;
	}
	
	private static class SingletonHolder
	{
		protected static final HennaTable _instance = new HennaTable();
	}
}