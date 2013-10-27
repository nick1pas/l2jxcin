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
package net.xcine.gameserver.datatables.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import javolution.util.FastMap;
import net.xcine.Config;
import net.xcine.gameserver.templates.L2Henna;
import net.xcine.gameserver.templates.StatsSet;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class HennaData
{
	private static final Logger _log = Logger.getLogger(HennaData.class.getName());

	private static HennaData _instance;

	private Map<Integer, L2Henna> _henna;

	public static HennaData getInstance()
	{
		if(_instance == null)
		{
			_instance = new HennaData();
		}

		return _instance;
	}

	private HennaData()
	{
		_henna = new FastMap<>();
		restoreHennaData();
	}

	private void restoreHennaData()
	{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(false);
		factory.setIgnoringComments(true);
		File f = new File(Config.DATAPACK_ROOT + "/data/stats/henna.xml");
		if(!f.exists())
		{
			_log.warning("henna.xml could not be loaded: file not found");
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
						if(d.getNodeName().equalsIgnoreCase("henna"))
						{
							StatsSet hennaDat = new StatsSet();
							int id = Integer.valueOf(d.getAttributes().getNamedItem("symbol_id").getNodeValue());
							hennaDat.set("symbol_id", id);
							hennaDat.set("dye", Integer.valueOf(d.getAttributes().getNamedItem("dye_id").getNodeValue()));
							hennaDat.set("amount", Integer.valueOf(d.getAttributes().getNamedItem("dye_amount").getNodeValue()));
							hennaDat.set("price", Integer.valueOf(d.getAttributes().getNamedItem("price").getNodeValue()));
							hennaDat.set("stat_INT", Integer.valueOf(d.getAttributes().getNamedItem("stat_INT").getNodeValue()));
							hennaDat.set("stat_STR", Integer.valueOf(d.getAttributes().getNamedItem("stat_STR").getNodeValue()));
							hennaDat.set("stat_CON", Integer.valueOf(d.getAttributes().getNamedItem("stat_CON").getNodeValue()));
							hennaDat.set("stat_MEM", Integer.valueOf(d.getAttributes().getNamedItem("stat_MEM").getNodeValue()));
							hennaDat.set("stat_DEX", Integer.valueOf(d.getAttributes().getNamedItem("stat_DEX").getNodeValue()));
							hennaDat.set("stat_WIT", Integer.valueOf(d.getAttributes().getNamedItem("stat_WIT").getNodeValue()));

							L2Henna template = new L2Henna(hennaDat);
							_henna.put(id, template);
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

		_log.info("HennaTable: Loaded " + _henna.size() + " templates.");
	}

	public L2Henna getTemplate(int id)
	{
		return _henna.get(id);
	}
}