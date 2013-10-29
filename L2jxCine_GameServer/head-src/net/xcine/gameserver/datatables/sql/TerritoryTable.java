/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package net.xcine.gameserver.datatables.sql;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import net.xcine.Config;
import net.xcine.gameserver.controllers.TradeController;
import net.xcine.gameserver.model.L2Territory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class TerritoryTable
{
	private static final Logger _log = Logger.getLogger(TradeController.class.getName());

	private static final TerritoryTable _instance = new TerritoryTable();
	private static Map<String, L2Territory> _territory;

	public static TerritoryTable getInstance()
	{
		return _instance;
	}

	private TerritoryTable()
	{
		_territory = new HashMap<>();
		// load all data at server start
		reload_data();
	}

	public int[] getRandomPoint(int terr)
	{
		return _territory.get(terr).getRandomPoint();
	}

	public int getProcMax(int terr)
	{
		return _territory.get(terr).getProcMax();
	}

	public void reload_data()
	{
		_territory.clear();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(false);
		factory.setIgnoringComments(true);
		File f = new File(Config.DATAPACK_ROOT + "/data/stats/location.xml");
		if(!f.exists())
		{
			_log.warning("location.xml could not be loaded: file not found");
			return;
		}
		try
		{
			InputSource in = new InputSource(new InputStreamReader(new FileInputStream(f), "UTF-8"));
			in.setEncoding("UTF-8");
			Document doc = factory.newDocumentBuilder().parse(in);
			for(Node n = doc.getFirstChild(); n != null; n = n.getNextSibling())
			{
				if(n.getNodeName().equalsIgnoreCase("list"))
				{
					for(Node d = n.getFirstChild(); d != null; d = d.getNextSibling())
					{
						if(d.getNodeName().equalsIgnoreCase("loc"))
						{
							String terr = "sql_terr_" + String.valueOf(d.getAttributes().getNamedItem("id").getNodeValue());
							int loc_x = Integer.valueOf(d.getAttributes().getNamedItem("x").getNodeValue());
							int loc_y = Integer.valueOf(d.getAttributes().getNamedItem("y").getNodeValue());
							int loc_zmin = Integer.valueOf(d.getAttributes().getNamedItem("Zmin").getNodeValue());
							int loc_zmax = Integer.valueOf(d.getAttributes().getNamedItem("Zmax").getNodeValue());
							int proc = Integer.valueOf(d.getAttributes().getNamedItem("proc").getNodeValue());

							if(_territory.get(terr) == null)
							{
								L2Territory t = new L2Territory();
								_territory.put(terr, t);
							}
							_territory.get(terr).add(loc_x, loc_y, loc_zmin, loc_zmax, proc);

							terr = null;
						}
					}
				}
			}
		}
		catch(SAXException e)
		{
			_log.warning("Error while creating table");
		}
		catch(IOException e)
		{
			_log.warning("Error while creating table");
		}
		catch(ParserConfigurationException e)
		{
			_log.warning("Error while creating table");
		}

		_log.info("TerritoryTable: Loaded " + _territory.size() + " locations");
	}
}