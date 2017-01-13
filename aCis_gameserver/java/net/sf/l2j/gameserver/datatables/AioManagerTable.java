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
package net.sf.l2j.gameserver.datatables;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import net.sf.l2j.gameserver.templates.L2Aio;
import net.sf.l2j.gameserver.templates.StatsSet;
import net.sf.l2j.gameserver.xmlfactory.XMLDocumentFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * @author rapfersan92
 */
public class AioManagerTable
{
	private static final Logger _log = Logger.getLogger(AioManagerTable.class.getName());
	
	private final List<L2Aio> _services;
	
	public static AioManagerTable getInstance()
	{
		return SingletonHolder._instance;
	}
	
	protected AioManagerTable()
	{
		_services = new ArrayList<>();
		load();
	}
	
	public void reload()
	{
		_services.clear();
		load();
	}
	
	public void load()
	{
		try
		{
			File f = new File("./data/xml/aio_manager.xml");
			Document doc = XMLDocumentFactory.getInstance().loadDocument(f);
			
			Node n = doc.getFirstChild();
			for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling())
			{
				if (d.getNodeName().equalsIgnoreCase("service"))
				{
					NamedNodeMap attrs = d.getAttributes();
					int id = Integer.valueOf(attrs.getNamedItem("id").getNodeValue());
					long duration = Long.valueOf(attrs.getNamedItem("duration").getNodeValue());
					int feeId = Integer.valueOf(attrs.getNamedItem("feeId").getNodeValue());
					int feeVal = Integer.valueOf(attrs.getNamedItem("feeVal").getNodeValue());
					
					StatsSet aioData = new StatsSet();
					aioData.set("id", id);
					aioData.set("duration", duration);
					aioData.set("feeId", feeId);
					aioData.set("feeVal", feeVal);
					
					_services.add(new L2Aio(aioData));
				}
			}
		}
		catch (Exception e)
		{
			_log.severe("Exception: AioManagerTable load: " + e);
		}
		
		_log.info("AioManagerTable: Loaded " + _services.size() + " aio services.");
	}
	
	public List<L2Aio> getAioTable()
	{
		return _services;
	}
	
	private static class SingletonHolder
	{
		protected static final AioManagerTable _instance = new AioManagerTable();
	}
}