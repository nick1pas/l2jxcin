package net.xcine.gameserver.event;

import javolution.text.TextBuilder;
import javolution.util.FastList;
import net.xcine.gameserver.datatables.sql.SpawnTable;
import net.xcine.gameserver.model.actor.instance.L2PcInstance;
import net.xcine.gameserver.model.spawn.L2Spawn;
import net.xcine.gameserver.network.serverpackets.NpcHtmlMessage;

public class Domination extends Event
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
						divideIntoTeams(2);
						preparePlayers();
						teleportToTeamPos();
						createPartyOfTeam(1);
						createPartyOfTeam(2);
						forceSitAll();
						debug("The event started with " + players.size() + " player");
						setStatus(EventState.FIGHT);
						schedule(20000);
						break;

					case FIGHT:
						forceStandAll();
						setStatus(EventState.END);
						debug("The event started");
						clock.startClock(getInt("matchTime"));
						break;

					case END:
						clock.setTime(0);
						if (winnerTeam == 0)
							winnerTeam = getWinnerTeam();
						
						giveReward(getPlayersOfTeam(winnerTeam), getInt("rewardId"), getInt("rewardAmmount"));
						unSpawnZones();
						setStatus(EventState.INACTIVE);
						debug("The event ended. Winner: " + winnerTeam);
						EventManager.getInstance().end("Congratulation! The " + teams.get(winnerTeam).getName() + " team won the event with " + teams.get(winnerTeam).getScore() + " Domination points!");
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

	private FastList<L2Spawn> zones;

	public Domination()
	{
		super();
		eventId = 2;
		createNewTeam(1, "Blue", getColor("Blue"), getPosition("Blue", 1));
		createNewTeam(2, "Red", getColor("Red"), getPosition("Red", 1));
		task = new Core();
		zones = new FastList<>();
		winnerTeam = 0;
	}

	@Override
	protected void clockTick()
	{
		int team1 = 0;
		int team2 = 0;

		for (L2PcInstance player : getPlayerList())
			switch (getTeam(player))
			{
				case 1:
					if (Math.sqrt(player.getPlanDistanceSq(zones.getFirst().getLastSpawn())) <= getInt("zoneRadius"))
						team1++;
					break;

				case 2:
					if (Math.sqrt(player.getPlanDistanceSq(zones.getFirst().getLastSpawn())) <= getInt("zoneRadius"))
						team2++;
					break;
			}

		if (team1 > team2)
		{
			for (L2PcInstance player : getPlayersOfTeam(1))
				increasePlayersScore(player);
			teams.get(1).increaseScore();
		}

		if (team2 > team1)
		{
			for (L2PcInstance player : getPlayersOfTeam(2))
				increasePlayersScore(player);
			teams.get(2).increaseScore();
		}

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
		return "" + teams.get(1).getName() + ": " + teams.get(1).getScore() + "  " + teams.get(2).getName() + ": " + teams.get(2).getScore() + "  Time: " + clock.getTime();
	}

	@Override
	public void onDie(L2PcInstance victim, L2PcInstance killer)
	{
		super.onDie(victim, killer);
		killer.addItem("Event", 6392, 1, killer, true);
		addToResurrector(victim);
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
		NpcHtmlMessage html = new NpcHtmlMessage(obj);
		TextBuilder sb = new TextBuilder();

		sb.append("<html><body><table width=270><tr><td width=200>Event Engine </td><td><a action=\"bypass -h eventstats 1\">Statistics</a></td></tr></table><br>");
		sb.append("<center><table width=270 bgcolor=5A5A5A><tr><td width=70>Running</td><td width=130><center>" + getString("eventName") + "</td><td width=70>Time: " + clock.getTime() + "</td></tr></table>");
		sb.append("<table width=270><tr><td><center><font color=" + teams.get(1).getHexaColor() + ">" + teams.get(1).getScore() + "</font> - " + "<font color=" + teams.get(2).getHexaColor() + ">" + teams.get(2).getScore() + "</font></td></tr></table>");
		sb.append("<br><table width=270>");
		int i = 0;
		for (EventTeam team : teams.values())
		{
			i++;
			sb.append("<tr><td><font color=" + team.getHexaColor() + ">" + team.getName() + "</font> team</td><td></td><td></td><td></td></tr>");
			for (L2PcInstance p : getPlayersOfTeam(i))
				sb.append("<tr><td>" + p.getName() + "</td><td>lvl " + p.getLevel() + "</td><td>" + p.getTemplate().className + "</td><td>" + getScore(p) + "</td></tr>");
		}

		sb.append("</table></body></html>");
		html.setHtml(sb.toString());
		player.sendPacket(html);

	}

	@Override
	protected void start()
	{
		int[] npcpos = getPosition("Zone", 1);
		zones.add(spawnNPC(npcpos[0], npcpos[1], npcpos[2], getInt("zoneNpcId")));
		setStatus(EventState.START);
		schedule(1);
	}

	public void unSpawnZones()
	{
		for (L2Spawn s : zones)
		{
			s.getLastSpawn().deleteMe();
			s.stopRespawn();
			SpawnTable.getInstance().deleteSpawn(s, true);
			zones.remove(s);
		}
	}

}