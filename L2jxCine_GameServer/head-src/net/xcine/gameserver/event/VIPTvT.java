package net.xcine.gameserver.event;

import javolution.text.TextBuilder;
import javolution.util.FastMap;
import net.xcine.gameserver.model.actor.instance.L2PcInstance;
import net.xcine.gameserver.network.serverpackets.NpcHtmlMessage;

public class VIPTvT extends Event
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
						selectNewVipOfTeam(1);
						selectNewVipOfTeam(2);
						forceSitAll();
						setStatus(EventState.FIGHT);
						schedule(20000);
						break;

					case FIGHT:
						forceStandAll();
						setStatus(EventState.END);
						clock.startClock(getInt("matchTime"));
						break;

					case END:
						clock.setTime(0);
						if (winnerTeam == 0)
							winnerTeam = getWinnerTeam();
						
						giveReward(getPlayersOfTeam(winnerTeam), getInt("rewardId"), getInt("rewardAmmount"));
						setStatus(EventState.INACTIVE);
						EventManager.getInstance().end("Congratulation! The " + teams.get(winnerTeam).getName() + " team won the event with " + teams.get(winnerTeam).getScore() + " VIP kills!");
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
		START, FIGHT, END, TELEPORT, INACTIVE
	}

	public EventState eventState;

	private Core task;

	private FastMap<Integer, L2PcInstance> vips;

	public VIPTvT()
	{
		super();
		eventId = 8;
		createNewTeam(1, "Blue", getColor("Blue"), getPosition("Blue", 1));
		createNewTeam(2, "Red", getColor("Red"), getPosition("Red", 1));
		task = new Core();
		vips = new FastMap<>();
		winnerTeam = 0;
	}

	@Override
	protected void endEvent()
	{
		winnerTeam = players.head().getNext().getValue()[0];

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
		if (vips.get(1) == victim)
		{
			teams.get(2).increaseScore();
			increasePlayersScore(killer);
			killer.addItem("Event", 6392, 3, killer, true);
			selectNewVipOfTeam(1);
		}
		if (vips.get(2) == victim)
		{
			teams.get(1).increaseScore();
			increasePlayersScore(killer);
			killer.addItem("Event", 6392, 3, killer, true);
			selectNewVipOfTeam(2);
		}

		addToResurrector(victim);
	}

	@Override
	protected void schedule(int time)
	{
		tpm.scheduleGeneral(task, time);
	}

	public void selectNewVipOfTeam(int team)
	{
		if (vips.get(team) != null)
		{
			int[] nameColor = teams.get(getTeam(vips.get(team))).getTeamColor();
			vips.get(team).getAppearance().setNameColor(nameColor[0], nameColor[1], nameColor[2]);
		}

		L2PcInstance newvip = getRandomPlayerFromTeam(team);
		vips.getEntry(team).setValue(newvip);
		if (team == 1)
		{
			int[] c = getColor("BlueVIP");
			newvip.getAppearance().setNameColor(c[0], c[1], c[2]);
		}

		if (team == 2)
		{
			int[] c = getColor("RedVIP");
			newvip.getAppearance().setNameColor(c[0], c[1], c[2]);
		}

		if (!newvip.isDead())
		{
			newvip.setCurrentCp(newvip.getMaxCp());
			newvip.setCurrentMp(newvip.getMaxMp());
			newvip.setCurrentHp(newvip.getMaxHp());
		}

		newvip.broadcastUserInfo();

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
		vips.put(1, null);
		vips.put(2, null);
		setStatus(EventState.START);
		schedule(1);
	}
	
	@Override 
 	public void onLogout(L2PcInstance player) 
 	{ 
 	super.onLogout(player); 
 	
 	if(vips.get(1) == player) 
 		selectNewVipOfTeam(1); 
 	if(vips.get(2) == player) 
 		selectNewVipOfTeam(2); 
 	       
 	} 
}