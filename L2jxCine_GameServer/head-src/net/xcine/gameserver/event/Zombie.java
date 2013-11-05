package net.xcine.gameserver.event;

import javolution.text.TextBuilder;
import net.xcine.gameserver.model.L2Object;
import net.xcine.gameserver.model.actor.instance.L2ItemInstance;
import net.xcine.gameserver.model.actor.instance.L2PcInstance;
import net.xcine.gameserver.network.serverpackets.NpcHtmlMessage;

/**
 * @author Rizel
 *
 */
public class Zombie extends Event
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
						teleportToRandom();
						forceSitAll();
						unequip();
						setStatus(EventState.FIGHT);
						schedule(20000);
						break;

					case FIGHT:
						forceStandAll();
						transform(getRandomPlayer());
						setStatus(EventState.END);
						clock.startClock(getInt("matchTime"));
						break;

					case END:
						setStatus(EventState.INACTIVE);
						clock.setTime(0);

						if (getPlayersWithStatus(0).size() != 1)
						{
							msgToAll("Tie!");
							EventManager.getInstance().end("The match has ended in a tie!");
						}

						else
						{
							L2PcInstance winner = getWinner();
							giveReward(winner, getInt("rewardId"), getInt("rewardAmmount"));
							EventManager.getInstance().end("Congratulation! " + winner.getName() + " won the event!");
						}

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

	public Zombie()
	{
		super();
		eventId = 9;
		createNewTeam(1, "All", getColor("All"), getPosition("All", 1));
		task = new Core();
	}

	@Override
	public boolean canAttack(L2PcInstance player, L2Object target)
	{
		if (target instanceof L2PcInstance)
			if (getStatus(player) == 1 && getStatus((L2PcInstance) target) == 0)
				return true;

		return false;
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
		return "Humans: " + getPlayersWithStatus(0).size() + "  Time: " + clock.getTime();
	}

	public L2PcInstance getWinner()
	{
		return getPlayersWithStatus(0).head().getNext().getValue();
	}

	@Override
	public void onHit(L2PcInstance actor, L2PcInstance target)
	{
		if (eventState == EventState.END)
		{
			if (getStatus(actor) == 1 && getStatus(target) == 0)
			{
				transform(target);
				increasePlayersScore(actor);
				actor.addItem("Event", 6392, 2, actor, true);
			}

			if (getPlayersWithStatus(0).size() == 1)
				schedule(1);
		}

	}

	@Override
	public void onLogout(L2PcInstance player)
	{
		if (getStatus(player) == 1 && getPlayersWithStatus(1).size() == 1)
		{
			super.onLogout(player);
			transform(getRandomPlayer());
		}
		else
		{
			super.onLogout(player);
		}
			
	}

	@Override
	public boolean onUseItem(L2PcInstance player, L2ItemInstance item)
	{
		return false;
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
			sb.append("<tr><td>" + p.getName() + "</td><td>lvl " + p.getLevel() + "</td><td>" + p.getTemplate().className + "</td><td>" + (getStatus(p) == 1 ? "Zombie" : "Human") + "</td></tr>");

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

	public void teleportToRandom()
	{
		for (L2PcInstance player : players.keySet())
		{
			int[] loc = getPosition("All", 0);
			player.teleToLocation(loc[0], loc[1], loc[2]);
		}
	}

	protected void transform(L2PcInstance player)
	{
		setStatus(player, 1);
		polymorph(player,25375);
		player.getAppearance().setNameColor(255, 0, 0);
		player.broadcastUserInfo();
		player.getInventory().unEquipItemInSlot(10);
		player.getInventory().unEquipItemInSlot(16);
	}
}