package net.xcine.gameserver.event;

import javolution.text.TextBuilder;
import net.xcine.gameserver.model.actor.instance.L2PcInstance;
import net.xcine.gameserver.network.serverpackets.NpcHtmlMessage;
import net.xcine.util.random.Rnd;

/**
 * @author Rizel
 *
 */
public class LMS extends Event
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
						debug("The event started with " + players.size() + " players.");
						schedule(20000);
						break;

					case FIGHT:
						forceStandAll();
						setStatus(EventState.END);
						debug("The event started.");
						clock.startClock(getInt("matchTime"));
						break;

					case END:
						clock.setTime(0);
						debug("The event ended.");
						L2PcInstance winner = getPlayersWithStatus(0).get(Rnd.get(getPlayersWithStatus(0).size()));
						giveReward(winner, getInt("rewardId"), getInt("rewardAmmount"));
						setStatus(EventState.INACTIVE);
						EventManager.getInstance().end(winner.getName() + " is the Last Man Standing!");
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

	public LMS()
	{
		super();
		eventId = 4;
		createNewTeam(1, "All", getColor("All"), getPosition("All", 1));
		task = new Core();
	}

	@Override
	protected void endEvent()
	{
		setStatus(EventState.END);
		clock.setTime(0);
	}

	@Override
	protected String getScorebar()
	{
		return "Players: " + getPlayersWithStatus(0).size() + "  Time: " + clock.getTime();
	}

	@Override
	public void onKill(L2PcInstance victim, L2PcInstance killer)
	{
		super.onKill(victim, killer);
		increasePlayersScore(killer);
		setStatus(victim, 1);
		if (getPlayersWithStatus(0).size() == 1)
		{
			setStatus(EventState.END);
			clock.setTime(0);
		}
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

	/* (non-Javadoc)
	 * @see net.sf.l2j.gameserver.event.Event#showHtml(net.sf.l2j.gameserver.model.actor.instance.L2PcInstance, int)
	 */
	@Override
	protected void showHtml(L2PcInstance player, int obj)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(obj);
		TextBuilder sb = new TextBuilder();

		sb.append("<html><body><table width=270><tr><td width=200>Event Engine </td><td><a action=\"bypass -h eventstats 1\">Statistics</a></td></tr></table><br>");
		sb.append("<center><table width=270 bgcolor=5A5A5A><tr><td width=70>Running</td><td width=130><center>" + getString("eventName") + "</td><td width=70>Time: " + clock.getTime() + "</td></tr></table>");
		sb.append("<table width=270><tr><td><center>Players left: " + getPlayersWithStatus(0).size() + "</td></tr></table>");
		sb.append("<br><table width=270>");

		for (L2PcInstance p : getPlayersOfTeam(1))
			sb.append("<tr><td>" + p.getName() + "</td><td>lvl " + p.getLevel() + "</td><td>" + p.getTemplate().className + "</td><td>" + (getStatus(p) == 1 ? "Dead" : "Alive") + "</td></tr>");

		sb.append("</table></body></html>");
		html.setHtml(sb.toString());
		player.sendPacket(html);

	}

	@Override
	protected void start()
	{
		setStatus(EventState.START);
		schedule(1);
	}
}