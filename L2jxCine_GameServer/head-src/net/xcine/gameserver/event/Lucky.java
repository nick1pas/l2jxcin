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

import javolution.text.TextBuilder;
import javolution.util.FastList;
import net.xcine.gameserver.datatables.sql.SpawnTable;
import net.xcine.gameserver.model.L2Npc;
import net.xcine.gameserver.model.L2Skill;
import net.xcine.gameserver.model.actor.instance.L2ItemInstance;
import net.xcine.gameserver.model.actor.instance.L2PcInstance;
import net.xcine.gameserver.model.spawn.L2Spawn;
import net.xcine.gameserver.network.serverpackets.CreatureSay;
import net.xcine.gameserver.network.serverpackets.NpcHtmlMessage;
import net.xcine.util.random.Rnd;

/**
 * @author Rizel
 *
 */
public class Lucky extends Event
{
	public class Core implements Runnable
	{
		@Override
		public void run()
		{
			try
			{
				switch (eventState)
				{
					case START:
						divideIntoTeams(1);
						preparePlayers();
						teleportToTeamPos();
						forceSitAll();
						unequip();
						setStatus(EventState.FIGHT);
						schedule(30000);
						break;

					case FIGHT:
						forceStandAll();
						int[] coor = getPosition("Chests", 1);
						for (int i = 0; i < getInt("numberOfChests"); i++)
							chests.add(spawnNPC(coor[0] + (Rnd.get(coor[3] * 2) - coor[3]), coor[1] + (Rnd.get(coor[3] * 2) - coor[3]), coor[2], getInt("chestNpcId")));
						setStatus(EventState.END);
						clock.startClock(getInt("matchTime"));
						break;

					case END:
						clock.setTime(0);
						unSpawnChests();
						L2PcInstance winner = getPlayerWithMaxScore();
						giveReward(winner, getInt("rewardId"), getInt("rewardAmmount"));
						setStatus(EventState.INACTIVE);
						EventManager.getInstance().end("Congratulation! " + winner.getName() + " won the event with " + getScore(winner) + " opened chests!");
						break;
				}
			}
			catch (Throwable e)
			{
				e.printStackTrace();
				EventManager.getInstance().end("Error! Event ended.");
			}
		}
	}

	private enum EventState
	{
		START, FIGHT, END, INACTIVE
	}

	public EventState eventState;

	private Core task;

	public FastList<L2Spawn> chests;

	public Lucky()
	{
		super();
		eventId = 5;
		createNewTeam(1, "All", getColor("All"), getPosition("All", 1));
		task = new Core();
		chests = new FastList<>();
	}

	/**
	 * @see net.xcine.gameserver.event.Event#endEvent()
	 */
	@Override
	protected void endEvent()
	{

		setStatus(EventState.END);
		clock.setTime(0);

	}

	@Override
	protected String getScorebar()
	{
		return "Max: " + getScore(getPlayerWithMaxScore()) + "  Time: " + clock.getTime() + "";
	}

	@Override
	public boolean onTalkNpc(L2Npc npc, L2PcInstance player)
	{
		if (npc.getNpcId() != getInt("chestNpcId"))
			return false;

		if (Rnd.get(3) == 0)
		{
			player.sendPacket(new CreatureSay(npc.getObjectId(), 0, "Chest", "BoOoOoMm!"));
			player.stopAllEffects();
			player.reduceCurrentHp(player.getMaxHp() + player.getMaxCp() + 1, player);
			EventStats.getInstance().tempTable.get(player.getObjectId())[2] = EventStats.getInstance().tempTable.get(player.getObjectId())[2] + 1;
			addToResurrector(player);
		}
		else
		{
			npc.doDie(npc);
			player.addItem("Event", 6392, 1, player, true);
			increasePlayersScore(player);
		}

		npc.deleteMe();
		npc.getSpawn().stopRespawn();
		SpawnTable.getInstance().deleteSpawn(npc.getSpawn(), true);
		chests.remove(npc.getSpawn());

		if (chests.size() == 0)
			clock.setTime(0);

		return true;
	}

	@Override
	protected void schedule(int time)
	{
		tpm.scheduleGeneral(task, time);
	}

	public void setStatus(EventState s)
	{
		eventState = s;
	}
	@Override
	public boolean onUseMagic(L2Skill skill)
	{
		return false;
	}

	@Override
	protected void showHtml(L2PcInstance player, int obj)
	{
		if (players.size() > 0)
		{
			NpcHtmlMessage html = new NpcHtmlMessage(obj);
			TextBuilder sb = new TextBuilder();

			sb.append("<html><body><table width=270><tr><td width=200>Event Engine </td><td><a action=\"bypass -h eventstats 1\">Statistics</a></td></tr></table><br>");
			sb.append("<center><table width=270 bgcolor=5A5A5A><tr><td width=70>Running</td><td width=130><center>" + getString("eventName") + "</td><td width=70>Time: " + clock.getTime() + "</td></tr></table>");
			sb.append("<table width=270><tr><td><center>" + getPlayerWithMaxScore().getName() + " - " + getScore(getPlayerWithMaxScore()) + "</td></tr></table>");
			sb.append("<br><table width=270>");

			for (L2PcInstance p : getPlayersOfTeam(1))
				sb.append("<tr><td>" + p.getName() + "</td><td>lvl " + p.getLevel() + "</td><td>" + p.getTemplate().className + "</td><td>" + getScore(p) + "</td></tr>");

			sb.append("</table></body></html>");
			html.setHtml(sb.toString());
			player.sendPacket(html);

		}

	}

	/* (non-Javadoc)
	 * @see net.sf.l2j.gameserver.event.Event#start()
	 */
	@Override
	protected void start()
	{
		setStatus(EventState.START);
		schedule(1);
	}

	public void unSpawnChests()
	{
		for (L2Spawn s : chests)
		{
			s.getLastSpawn().deleteMe();
			s.stopRespawn();
			SpawnTable.getInstance().deleteSpawn(s, true);
			chests.remove(s);
		}
	}
	@Override
	public boolean onUseItem(L2PcInstance player, L2ItemInstance item)
	{
		return false;
	}

}