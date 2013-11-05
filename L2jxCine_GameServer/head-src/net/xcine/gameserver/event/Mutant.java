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
import net.xcine.gameserver.datatables.SkillTable;
import net.xcine.gameserver.model.L2Object;
import net.xcine.gameserver.model.actor.instance.L2PcInstance;
import net.xcine.gameserver.network.serverpackets.NpcHtmlMessage;

/**
 * @author Rizel
 *
 */
public class Mutant extends Event
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
						setStatus(EventState.FIGHT);
						schedule(20000);
						break;

					case FIGHT:
						forceStandAll();
						transformMutant(getRandomPlayer());
						setStatus(EventState.END);
						clock.startClock(getInt("matchTime"));
						break;

					case END:
						clock.setTime(0);
						untransformMutant();
						L2PcInstance winner = getPlayerWithMaxScore();
						giveReward(winner, getInt("rewardId"), getInt("rewardAmmount"));
						setStatus(EventState.INACTIVE);
						EventManager.getInstance().end("Congratulation! " + winner.getName() + " won the event with " + getScore(winner) + " kills!");
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

	private L2PcInstance mutant;

	public Mutant()
	{
		super();
		eventId = 13;
		createNewTeam(1, "All", getColor("All"), getPosition("All", 1));
		task = new Core();
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
	public void onDie(L2PcInstance victim, L2PcInstance killer)
	{
		super.onDie(victim, killer);
		addToResurrector(victim);
	}

	@Override
	public void onKill(L2PcInstance victim, L2PcInstance killer)
	{
		super.onKill(victim, killer);
		if (getStatus(killer) == 1)
			increasePlayersScore(killer);
		if (getStatus(killer) == 0 && getStatus(victim) == 1)
			transformMutant(killer);
	    	killer.addItem("Event", 6392, 1, killer, true);
	}

	@Override
	public void onLogout(L2PcInstance player)
	{
		super.onLogout(player);

		if (mutant == player)
			transformMutant(getRandomPlayer());
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

	@Override
	protected void start()
	{
		setStatus(EventState.START);
		schedule(1);
	}

	public void transformMutant(L2PcInstance player)
	{
		player.addSkill(SkillTable.getInstance().getInfo(getInt("mutantBuffId"), 1), false);
		setStatus(player, 1);
		untransformMutant();
		polymorph(player,25286);
		player.getAppearance().setNameColor(255, 0, 0);
		player.broadcastUserInfo();
		mutant = player;

	}

	public void untransformMutant()
	{
		if (mutant != null)
		{
			mutant.getAppearance().setNameColor(getColor("All")[0], getColor("All")[1], getColor("All")[2]);
			mutant.removeSkill(SkillTable.getInstance().getInfo(getInt("mutantBuffId"), 1), false);
			setStatus(mutant, 0);
			unpolymorph(mutant);
			mutant.broadcastUserInfo();
			mutant = null;
		}
	}
	@Override
	public boolean canAttack(L2PcInstance player, L2Object target)
	{
		if (target instanceof L2PcInstance)
		{
			if (getStatus(player) == 0 && getStatus((L2PcInstance) target) == 0)
				return false;
			return true;
		}

		return false;
	}

}