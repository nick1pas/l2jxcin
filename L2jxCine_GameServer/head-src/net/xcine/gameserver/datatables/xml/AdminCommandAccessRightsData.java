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
import net.xcine.gameserver.datatables.AccessLevel;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class AdminCommandAccessRightsData
{
	private final static Logger _log = Logger.getLogger(AdminCommandAccessRightsData.class.getName());

	private static AdminCommandAccessRightsData _instance = null;

	private Map<String, Integer> _adminCommandAccessRights = new FastMap<>();

	private AdminCommandAccessRightsData()
	{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(false);
		factory.setIgnoringComments(true);
		String adminCommand = null;
		int accessLevels = 1;
		File f = new File(Config.DATAPACK_ROOT + "/data/stats/admin_command_access_rights.xml");
		if(!f.exists())
		{
			_log.warning("admin_command_access_rights.xml could not be loaded: file not found");
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
						if(d.getNodeName().equalsIgnoreCase("adm_cmd"))
						{
							adminCommand = String.valueOf(d.getAttributes().getNamedItem("adminCommand").getNodeValue());
							accessLevels = Integer.valueOf(d.getAttributes().getNamedItem("accessLevel").getNodeValue());
							_adminCommandAccessRights.put(adminCommand, accessLevels);
						}
					}
				}
			}
		}
		catch(SAXException e)
		{
			_log.warning("Admin Access Rights: Error loading from database");
		}
		catch(IOException e)
		{
			_log.warning("Admin Access Rights: Error loading from database");
		}
		catch(ParserConfigurationException e)
		{
			_log.warning("Admin Access Rights: Error loading from database");
		}

		_log.info("AdminAccessRights: Loaded " + _adminCommandAccessRights.size() + " access rigths from database.");
	}

	public static AdminCommandAccessRightsData getInstance()
	{
		return _instance == null ? (_instance = new AdminCommandAccessRightsData()) : _instance;
	}

	public boolean hasAccess(String adminCommand, AccessLevel accessLevel)
	{
		if(accessLevel.getLevel() <= 0)
		{
			return false;
		}

		if(!accessLevel.isGm())
		{
			return false;
		}

		if(accessLevel.getLevel() == Config.MASTERACCESS_LEVEL)
		{
			return true;
		}

		String command = adminCommand;

		if(adminCommand.indexOf(" ") != -1)
		{
			command = adminCommand.substring(0, adminCommand.indexOf(" "));
		}

		int acar = 0;

		if(_adminCommandAccessRights.get(command) != null)
		{
			acar = _adminCommandAccessRights.get(command);
		}

		if(acar == 0)
		{
			_log.info("Admin Access Rights: No rights defined for admin command " + command + ".");
			return false;
		}
		else if(acar >= accessLevel.getLevel())
		{
			return true;
		}
		else
		{
			return false;
		}
	}
}