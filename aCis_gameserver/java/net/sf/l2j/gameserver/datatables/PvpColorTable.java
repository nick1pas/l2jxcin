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

import net.sf.l2j.gameserver.templates.StatsSet;
import net.sf.l2j.gameserver.xmlfactory.XMLDocumentFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * @author rapfersan92
 */
public class PvpColorTable
{
	private static final Logger _log = Logger.getLogger(PvpColorTable.class.getName());
	
	private static List<PvpColor> _pvpColors;
	
	public static PvpColorTable getInstance()
	{
		return SingletonHolder._instance;
	}
	
	private static class SingletonHolder
	{
		protected static final PvpColorTable _instance = new PvpColorTable();
	}
	
	protected PvpColorTable()
	{
		_pvpColors = new ArrayList<>();
		load();
	}
	
	public void reload()
	{
		_pvpColors.clear();
		load();
	}
	
	private void load()
	{
		try
		{
			File f = new File("./data/xml/pvp_color.xml");
			Document doc = XMLDocumentFactory.getInstance().loadDocument(f);
			
			Node n = doc.getFirstChild();
			for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling())
			{
				if (d.getNodeName().equalsIgnoreCase("template"))
				{
					StatsSet pvpColorData = new StatsSet();
					NamedNodeMap attrs = d.getAttributes();
					
					pvpColorData.set("pvpAmount", Integer.valueOf(attrs.getNamedItem("pvpAmount").getNodeValue()));
					pvpColorData.set("nameColor", Integer.decode("0x" + attrs.getNamedItem("nameColor").getNodeValue()));
					pvpColorData.set("titleColor", Integer.decode("0x" + attrs.getNamedItem("titleColor").getNodeValue()));
					pvpColorData.set("skillId", Integer.valueOf(attrs.getNamedItem("skillId").getNodeValue()));
					pvpColorData.set("skillLv", Integer.valueOf(attrs.getNamedItem("skillLv").getNodeValue()));
					
					_pvpColors.add(new PvpColor(pvpColorData));
				}
			}
		}
		catch (Exception e)
		{
			_log.warning("Exception: PvpColorTable load: " + e);
		}
		
		_log.info("PvpColorTable: Loaded " + _pvpColors.size() + " template(s).");
	}
	
	public List<PvpColor> getPvpColorTable()
	{
		return _pvpColors;
	}
	
	public class PvpColor
	{
		private int _pvpAmount;
		private int _nameColor;
		private int _titleColor;
		private int _skillId;
		private int _skillLv;
		
		public PvpColor(StatsSet set)
		{
			_pvpAmount = set.getInteger("pvpAmount");
			_nameColor = set.getInteger("nameColor");
			_titleColor = set.getInteger("titleColor");
			_skillId = set.getInteger("skillId");
			_skillLv = set.getInteger("skillLv");
		}
		
		public int getPvpAmount()
		{
			return _pvpAmount;
		}
		
		public int getNameColor()
		{
			return _nameColor;
		}
		
		public int getTitleColor()
		{
			return _titleColor;
		}
		
		public int getSkillId()
		{
			return _skillId;
		}
		
		public int getSkillLv()
		{
			return _skillLv;
		}
	}
}