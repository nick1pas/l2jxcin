package net.xcine.gameserver.event;

import java.io.File;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import javolution.util.FastList;
import javolution.util.FastMap;
import net.xcine.util.random.Rnd;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class EventConfig
{

	private static class SingletonHolder
	{
		protected static final EventConfig _instance = new EventConfig();
	}

	public static EventConfig getInstance()
	{
		return SingletonHolder._instance;
	}

	private Logger _log = Logger.getLogger(EventConfig.class.getName());
	public FastMap<Integer, FastMap<String, String>> config;

	public FastMap<Integer, FastMap<String, FastMap<Integer, int[]>>> positions;

	public FastMap<Integer, FastMap<String, int[]>> colors;

	public FastMap<Integer, FastMap<String, FastList<Integer>>> restrictions;

	public EventConfig()
	{
		config = new FastMap<>();
		positions = new FastMap<>();
		colors = new FastMap<>();
		restrictions = new FastMap<>();
		loadConfigs();
	}

	private void addColor(int id, String owner, int[] color)
	{
		if (!colors.containsKey(id))
			colors.put(id, new FastMap<String, int[]>());

		colors.get(id).put(owner, color);
	}

	private void addPosition(int id, String owner, int x, int y, int z, int radius)
	{

		if (!positions.containsKey(id))
			positions.put(id, new FastMap<String, FastMap<Integer, int[]>>());
		if (!positions.get(id).containsKey(owner))
			positions.get(id).put(owner, new FastMap<Integer, int[]>());

		positions.get(id).get(owner).put(positions.get(id).get(owner).size() + 1, new int[] { x, y, z, radius });
	}

	private void addProperty(int id, String propName, String value)
	{
		if (!config.containsKey(id))
			config.put(id, new FastMap<String, String>());

		config.get(id).put(propName, value);
	}

	private void addRestriction(int id, String type, String ids)
	{
		if (!restrictions.containsKey(id))
			restrictions.put(id, new FastMap<String, FastList<Integer>>());

		FastList<Integer> idlist = new FastList<>();
		StringTokenizer st = new StringTokenizer(ids, ",");
		while (st.hasMoreTokens())
			idlist.add(Integer.parseInt(st.nextToken()));

		restrictions.get(id).put(type, idlist);
	}

	public boolean getBoolean(int event, String propName)
	{
		if (!(config.containsKey(event)))
		{
			_log.warning("Event: Try to get a property of a non existing event: ID: " + event);
			return false;
		}

		if (config.get(event).containsKey(propName))
			return Boolean.parseBoolean(config.get(event).get(propName));
		_log.warning("Event: Try to get a non existing property: " + propName);
		return false;

	}

	public int[] getColor(int event, String owner)
	{
		if (!(colors.containsKey(event)))
		{
			_log.warning("Event: Try to get a color of a non existing event: ID: " + event);
			return new int[] { 255, 255, 255 };
		}

		if (colors.get(event).containsKey(owner))
			return colors.get(event).get(owner);
		_log.warning("Event: Try to get a non existing color: " + owner);
		return new int[] { 255, 255, 255 };
	}

	public int getInt(int event, String propName)
	{
		if (!(config.containsKey(event)))
		{
			_log.warning("Event: Try to get a property of a non existing event: ID: " + event);
			return -1;
		}

		if (config.get(event).containsKey(propName))
			return Integer.parseInt(config.get(event).get(propName));
		_log.warning("Event: Try to get a non existing property: " + propName);
		return -1;
	}

	public int[] getPosition(int event, String owner, int num)
	{
		if (!positions.containsKey(event))
		{
			_log.warning("Event: Try to get a position of a non existing event: ID: " + event);
			return new int[] {};
		}
		if (!positions.get(event).containsKey(owner))
		{
			_log.warning("Event: Try to get a position of a non existing owner: " + owner);
			return new int[] {};
		}
		if (!positions.get(event).get(owner).containsKey(num) && num != 0)
		{
			_log.warning("Event: Try to get a non existing position: " + num);
			return new int[] {};
		}

		if (num == 0)
			return positions.get(event).get(owner).get(Rnd.get(positions.get(event).get(owner).size()) + 1);
		return positions.get(event).get(owner).get(num);
	}

	public FastList<Integer> getRestriction(int event, String type)
	{
		if (!(restrictions.containsKey(event)))
		{
			_log.warning("Event: Try to get a restriction of a non existing event: ID: " + event);
			return null;
		}
		if (restrictions.get(event).containsKey(type))
			return restrictions.get(event).get(type);
		_log.warning("Event: Try to get a non existing restriction: " + type);
		return null;
	}

	public String getString(int event, String propName)
	{
		if (!(config.containsKey(event)))
		{
			_log.warning("Event: Try to get a property of a non existing event: ID: " + event);
			return null;
		}

		if (config.get(event).containsKey(propName))
			return config.get(event).get(propName);
		_log.warning("Event: Try to get a non existing property: " + propName);
		return null;

	}

	private void loadConfigs()
	{
		File configFile = new File("./data/Events.xml");
		Document doc = null;
		try
		{
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setIgnoringComments(true);
			dbf.setValidating(false);
			DocumentBuilder db = dbf.newDocumentBuilder();
			doc = db.parse(configFile);

			for (Node root = doc.getFirstChild(); root != null; root = root.getNextSibling())
				if ("events".equalsIgnoreCase(root.getNodeName()))
					for (Node event = root.getFirstChild(); event != null; event = event.getNextSibling())
						if ("event".equalsIgnoreCase(event.getNodeName()))
						{
							NamedNodeMap eventAttrs = event.getAttributes();
							int eventId = Integer.parseInt(eventAttrs.getNamedItem("id").getNodeValue());

							for (Node property = event.getFirstChild(); property != null; property = property.getNextSibling())
							{

								if ("property".equalsIgnoreCase(property.getNodeName()))
								{
									NamedNodeMap propAttrs = property.getAttributes();
									String name = propAttrs.getNamedItem("name").getNodeValue();
									String value = propAttrs.getNamedItem("value").getNodeValue();
									addProperty(eventId, name, value);
								}

								if ("position".equalsIgnoreCase(property.getNodeName()))
								{
									NamedNodeMap propAttrs = property.getAttributes();
									String owner = propAttrs.getNamedItem("owner").getNodeValue();
									String x = propAttrs.getNamedItem("x").getNodeValue();
									String y = propAttrs.getNamedItem("y").getNodeValue();
									String z = propAttrs.getNamedItem("z").getNodeValue();
									String radius = propAttrs.getNamedItem("radius").getNodeValue();
									addPosition(eventId, owner, Integer.parseInt(x), Integer.parseInt(y), Integer.parseInt(z), Integer.parseInt(radius));

								}

								if ("color".equalsIgnoreCase(property.getNodeName()))
								{
									NamedNodeMap propAttrs = property.getAttributes();
									String owner = propAttrs.getNamedItem("owner").getNodeValue();
									int r = Integer.parseInt(propAttrs.getNamedItem("r").getNodeValue());
									int g = Integer.parseInt(propAttrs.getNamedItem("g").getNodeValue());
									int b = Integer.parseInt(propAttrs.getNamedItem("b").getNodeValue());
									addColor(eventId, owner, new int[] { r, g, b });
								}

								if ("restriction".equalsIgnoreCase(property.getNodeName()))
								{
									NamedNodeMap propAttrs = property.getAttributes();
									String type = propAttrs.getNamedItem("type").getNodeValue();
									String ids = propAttrs.getNamedItem("ids").getNodeValue();
									addRestriction(eventId, type, ids);
								}
							}
						}
		}
		catch (Exception e)
		{

		}

	}
}