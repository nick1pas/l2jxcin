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
package net.xcine.gameserver.event;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;
import java.util.StringTokenizer;

import javolution.text.TextBuilder;
import javolution.util.FastList;
import javolution.util.FastMap;
import net.xcine.gameserver.datatables.SkillTable;
import net.xcine.gameserver.model.actor.instance.L2PcInstance;
import net.xcine.gameserver.network.serverpackets.NpcHtmlMessage;
import net.xcine.gameserver.thread.ThreadPoolManager;
import net.xcine.util.database.L2DatabaseFactory;

/**
 * @author Rizel
 *
 */
public class EventBuffer
{
	private static class SingletonHolder
	{
		protected static final EventBuffer _instance = new EventBuffer();
	}
	public class UpdateTask implements Runnable
	{
		@Override
		public void run()
		{
			updateSQL();
		}
	}
	public static EventBuffer getInstance()
	{
		return SingletonHolder._instance;
	}

	private FastMap<String, FastList<Integer>> buffTemplates;

	private FastMap<String, Boolean> changes;

	private UpdateTask updateTask;

	public EventBuffer()
	{
		updateTask = new UpdateTask();
		changes = new FastMap<>();
		buffTemplates = new FastMap<>();
		loadSQL();
		ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(updateTask, 600000, 600000);
	}

	protected void buffPlayer(L2PcInstance player)
	{
		boolean bss = player.checkBss();
		boolean sps = player.checkSps();
		boolean ss = player.checkSs();
		
		String playerId = "" + player.getObjectId() + player.getClassIndex();

		if(!buffTemplates.containsKey(playerId))
			{
				EventManager.getInstance().debug("The player : "+player.getName()+" ("+playerId+") without template");
				return;
			}
			             
		for (int skillId : buffTemplates.get(playerId))
			SkillTable.getInstance().getInfo(skillId, SkillTable.getInstance().getMaxLevel(skillId,1)).getEffects(player, player, ss, sps, bss);
	}

	public void changeList(L2PcInstance player, int buff, boolean action)
	{
		String playerId = "" + player.getObjectId() + player.getClassIndex();

		if (!buffTemplates.containsKey(playerId))
		{
			buffTemplates.put(playerId, new FastList<Integer>());
			changes.put(playerId, true);
		}
		else
		{
			if (!changes.containsKey(playerId))
				changes.put(playerId, false);

			if (action)
				buffTemplates.get(playerId).add(buff);
			else
				buffTemplates.get(playerId).remove(buffTemplates.get(playerId).indexOf(buff));

		}

	}

	private void loadSQL()
	{
		if (!EventManager.getInstance().getBoolean("eventBufferEnabled"))
			return;

		PreparedStatement statement = null;
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			statement = con.prepareStatement("SELECT * FROM event_buffs");
			ResultSet rset = statement.executeQuery();
			int count = 0;
			while (rset.next())
			{
				count++;

				buffTemplates.put(rset.getString("player"), new FastList<Integer>());

				StringTokenizer st = new StringTokenizer(rset.getString("buffs"), ",");

				FastList<Integer> templist = new FastList<>();

				while (st.hasMoreTokens())
					templist.add(Integer.parseInt(st.nextToken()));

				buffTemplates.getEntry(rset.getString("player")).setValue(templist);
			}
			rset.close();
			statement.close();

			EventManager.getInstance().debug("Buffer loaded: " + count + " players template.");
		}
		catch (Exception e)
		{
			System.out.println("EventBuffs SQL catch");
		}
	}

	protected boolean playerHaveTemplate(L2PcInstance player)
	{
		String playerId = "" + player.getObjectId() + player.getClassIndex();

		if (buffTemplates.containsKey(playerId))
			return true;
		return false;
	}

	public void showHtml(L2PcInstance player)
	{
		try{
		String playerId = "" + player.getObjectId() + player.getClassIndex();

		if (!buffTemplates.containsKey(playerId))
		{
			buffTemplates.put(playerId, new FastList<Integer>());
			changes.put(playerId, true);
		}

		StringTokenizer st = new StringTokenizer(EventManager.getInstance().getString("allowedBuffsList"), ",");

		FastList<Integer> skillList = new FastList<>();

		while (st.hasMoreTokens())
			skillList.add(Integer.parseInt(st.nextToken()));

		NpcHtmlMessage html = new NpcHtmlMessage(0);
		TextBuilder sb = new TextBuilder();

		sb.append("<html><body><table width=270><tr><td width=200>Event Engine </td><td><a action=\"bypass -h eventstats 1\">Statistics</a></td></tr></table><br>");
		sb.append("<center><table width=270 bgcolor=5A5A5A><tr><td width=70>Edit Buffs</td><td width=80></td><td width=120>Remaining slots: " + (EventManager.getInstance().getInt("maxBuffNum") - buffTemplates.get(playerId).size()) + "</td></tr></table><br><br>");
		sb.append("<center><table width=270 bgcolor=5A5A5A><tr><td>Added buffs:</td></tr></table><br>");
		sb.append("<center><table width=270>");

		int c = 0;
		for (int skillId : buffTemplates.get(playerId))
		{
			c++;
			String skillStr = "0000";
			if (skillId < 100)
				skillStr = "00" + skillId;
			else if (skillId > 99 && skillId < 1000)
				skillStr = "0" + skillId;
			else if (skillId > 4698 && skillId < 4701)
				skillStr = "1331";
			else if (skillId > 4701 && skillId < 4704)
				skillStr = "1332";
			else
				skillStr = "" + skillId;

			if (c % 2 == 1)
				sb.append("<tr><td width=33><img src=\"Icon.skill" + skillStr + "\" width=32 height=32></td><td width=100><a action=\"bypass -h eventbuffer " + skillId + " 0\">" + SkillTable.getInstance().getInfo(skillId, SkillTable.getInstance().getMaxLevel(skillId,1)).getName() + "</a></td>");
			if (c % 2 == 0)
				sb.append("<td width=33><img src=\"Icon.skill" + skillStr + "\" width=32 height=32></td><td width=100><a action=\"bypass -h eventbuffer " + skillId + " 0\">" + SkillTable.getInstance().getInfo(skillId, SkillTable.getInstance().getMaxLevel(skillId,1)).getName() + "</a></td></tr>");
		}

		if (c % 2 == 1)
			sb.append("<td width=33></td><td width=100></td></tr>");

		sb.append("</table><br>");

		sb.append("<br><br><center><table width=270 bgcolor=5A5A5A><tr><td>Available buffs:</td></tr></table><br>");
		sb.append("<center><table width=270>");

		c = 0;
		for (int skillId : skillList)
		{
			String skillStr = "0000";
			if (skillId < 100)
				skillStr = "00" + skillId;
			else if (skillId > 99 && skillId < 1000)
				skillStr = "0" + skillId;
			else if (skillId > 4698 && skillId < 4701)
				skillStr = "1331";
			else if (skillId > 4701 && skillId < 4704)
				skillStr = "1332";
			else
				skillStr = "" + skillId;

			if (!buffTemplates.get(playerId).contains(skillId))
			{
				c++;
				if (c % 2 == 1)
					sb.append("<tr><td width=32><img src=\"Icon.skill" + skillStr + "\" width=32 height=32></td><td width=100>" + ((EventManager.getInstance().getInt("maxBuffNum") - buffTemplates.get(playerId).size()) != 0 ? "<a action=\"bypass -h eventbuffer " + skillId + " 1\">" : "") + SkillTable.getInstance().getInfo(skillId, SkillTable.getInstance().getMaxLevel(skillId,1)).getName() + ((EventManager.getInstance().getInt("maxBuffNum") - buffTemplates.get(playerId).size()) != 0 ? "</a>" : "") + "</td>");
				if (c % 2 == 0)
					sb.append("<td width=32><img src=\"Icon.skill" + skillStr + "\" width=32 height=32></td><td width=100>" + ((EventManager.getInstance().getInt("maxBuffNum") - buffTemplates.get(playerId).size()) != 0 ? "<a action=\"bypass -h eventbuffer " + skillId + " 1\">" : "") + SkillTable.getInstance().getInfo(skillId, SkillTable.getInstance().getMaxLevel(skillId,1)).getName() + ((EventManager.getInstance().getInt("maxBuffNum") - buffTemplates.get(playerId).size()) != 0 ? "</a>" : "") + "</td></tr>");
			}
		}

		if (c % 2 == 1)
			sb.append("<td width=33></td><td width=100></td></tr>");

		sb.append("</table>");

		sb.append("</body></html>");
		html.setHtml(sb.toString());
		player.sendPacket(html);
		}
		catch(Throwable e)
		{
			e.printStackTrace();
		}
	}

	public void updateSQL()
	{
		PreparedStatement statement = null;
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			for (Map.Entry<String, Boolean> player : changes.entrySet())
			{

				TextBuilder sb = new TextBuilder();

				int c = 0;
				for (int buffid : buffTemplates.get(player.getKey()))
					if (c == 0)
					{
						sb.append(buffid);
						c++;
					}
					else
						sb.append("," + buffid);

				if (player.getValue())
				{
					statement = con.prepareStatement("INSERT INTO event_buffs(player,buffs) VALUES (?,?)");
					statement.setString(1, player.getKey());
					statement.setString(2, sb.toString());

					statement.executeUpdate();
					statement.close();
				}
				else
				{
					statement = con.prepareStatement("UPDATE event_buffs SET buffs=? WHERE player=?");
					statement.setString(1, sb.toString());
					statement.setString(2, player.getKey());

					statement.executeUpdate();
					statement.close();
				}
			}
		}
		catch (Exception e)
		{
			System.out.println("EventBuffs SQL catch");
		}

		changes.clear();
	}
}