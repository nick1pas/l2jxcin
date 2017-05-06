package net.sf.l2j.gameserver.datatables;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import net.sf.l2j.gameserver.model.L2EnchantScroll;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;
import net.sf.l2j.gameserver.model.item.type.CrystalType;
import net.sf.l2j.gameserver.xmlfactory.XMLDocumentFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * @author Thug
 */
public class EnchantTable
{
	private static Logger _log = Logger.getLogger(EnchantTable.class.getName());
	
	private static final Map<Integer, L2EnchantScroll> _map = new HashMap<>();
	
	public static EnchantTable getInstance()
	{
		return SingletonHolder._instance;
	}
	
	protected EnchantTable()
	{
		try
		{
			File f = new File("./data/xml/enchants.xml");
			Document doc = XMLDocumentFactory.getInstance().loadDocument(f);
			
			for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling())
			{
				if ("list".equalsIgnoreCase(n.getNodeName()))
				{
					for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling())
					{
						if ("enchant".equalsIgnoreCase(d.getNodeName()))
						{
							NamedNodeMap attrs = d.getAttributes();
							
							int id = Integer.valueOf(attrs.getNamedItem("id").getNodeValue());
							byte grade = Byte.valueOf(attrs.getNamedItem("grade").getNodeValue());
							boolean weapon = Boolean.valueOf(attrs.getNamedItem("weapon").getNodeValue());
							boolean breaks = Boolean.valueOf(attrs.getNamedItem("break").getNodeValue());
							boolean maintain = Boolean.valueOf(attrs.getNamedItem("maintain").getNodeValue());
							
							String[] list = attrs.getNamedItem("chance").getNodeValue().split(";");
							byte[] chance = new byte[list.length];
							for (int i = 0; i < list.length; i++)
								chance[i] = Byte.valueOf(list[i]);
							
							CrystalType grade_test = CrystalType.NONE;
							switch (grade)
							{
								case 1:
									grade_test = CrystalType.D;
									break;
								case 2:
									grade_test = CrystalType.C;
									break;
								case 3:
									grade_test = CrystalType.B;
									break;
								case 4:
									grade_test = CrystalType.A;
									break;
								case 5:
									grade_test = CrystalType.S;
									break;
							}
							
							_map.put(id, new L2EnchantScroll(grade_test, weapon, breaks, maintain, chance));
						}
					}
				}
			}
			
			_log.info("EnchantTable: Loaded " + _map.size() + " enchants.");
		}
		catch (Exception e)
		{
			_log.warning("EnchantTable: Error while loading enchant table: " + e);
		}
	}
	
	public L2EnchantScroll getEnchantScroll(ItemInstance item)
	{
		return _map.get(item.getItemId());
	}
	
	private static class SingletonHolder
	{
		protected static final EnchantTable _instance = new EnchantTable();
	}
}