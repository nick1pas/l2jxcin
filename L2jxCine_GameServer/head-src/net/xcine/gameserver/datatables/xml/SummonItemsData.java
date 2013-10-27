package net.xcine.gameserver.datatables.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import javolution.util.FastMap;
import net.xcine.Config;
import net.xcine.gameserver.model.L2SummonItem;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class SummonItemsData
{
    protected static final Logger _log = Logger.getLogger(SummonItemsData.class.getName());

    private FastMap<Integer, L2SummonItem> _summonitems;

    private static SummonItemsData _instance;

    public static SummonItemsData getInstance()
    {
        if (_instance == null)
        {
            _instance = new SummonItemsData();
        }

        return _instance;
    }

    public SummonItemsData()
    {
        _summonitems = new FastMap<>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(false);
        factory.setIgnoringComments(true);
        File f = new File(Config.DATAPACK_ROOT, "data/stats/summon_items.xml");
        if (!f.exists())
        {
            _log.warning("summon_items.xml could not be loaded: file not found");
            return;
        }
        int itemID = 0, npcID = 0;
        byte summonType = 0;
        try
        {
            InputSource in = new InputSource(new InputStreamReader(new FileInputStream(f), "UTF-8"));
            in.setEncoding("UTF-8");
            Document doc = factory.newDocumentBuilder().parse(in);
            for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling())
            {
                if (n.getNodeName().equalsIgnoreCase("list"))
                {
                    for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling())
                    {
                        if (d.getNodeName().equalsIgnoreCase("summon_item"))
                        {
                            itemID = Integer.valueOf(d.getAttributes().getNamedItem("itemID").getNodeValue());
                            npcID = Integer.valueOf(d.getAttributes().getNamedItem("npcID").getNodeValue());
                            summonType = Byte.valueOf(d.getAttributes().getNamedItem("summonType").getNodeValue());
                            L2SummonItem summonitem = new L2SummonItem(itemID, npcID, summonType);
                            _summonitems.put(itemID, summonitem);
                        }
                    }
                }
            }
        }
        catch (SAXException e)
        {
            _log.warning("Error while creating table");
        }
        catch (IOException e)
        {
            _log.warning("Error while creating table");
        }
        catch (ParserConfigurationException e)
        {
            _log.warning("Error while creating table");
        }

        _log.info("Summon: Loaded " + _summonitems.size() + " summon items.");
    }

    public L2SummonItem getSummonItem(int itemId)
    {
        return _summonitems.get(itemId);
    }

    public int[] itemIDs()
    {
        int size = _summonitems.size();
        int[] result = new int[size];
        int i = 0;

        for (L2SummonItem si : _summonitems.values())
        {
            result[i] = si.getItemId();
            i++;
        }
        return result;
    }
}