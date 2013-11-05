package net.xcine.gameserver.event;

import javolution.text.TextBuilder;
import net.xcine.gameserver.datatables.sql.SpawnTable;
import net.xcine.gameserver.model.L2Npc;
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
public class Simon extends Event
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
						setStatus(EventState.SAY);
						schedule(30000);
						break;

					case SAY:
						round++;
						say = createNewRandomString(getInt("lengthOfFirstWord") + getInt("increasePerRound") * round);
						sendToPlayers(say.toUpperCase());
						setStatus(EventState.CHECK);
						schedule(getInt("roundTime") * 1000);
						break;

					case CHECK:
						if (removeAfkers())
						{
							setAllToFalse();
							setStatus(EventState.SAY);
							schedule(getInt("roundTime") * 1000);
						}
						break;

					case END:
						setStatus(EventState.INACTIVE);
						forceStandAll();

						if (winner != null)
						{
							giveReward(winner, getInt("rewardId"), getInt("rewardAmmount"));
							EventManager.getInstance().end("Congratulation! " + winner.getName() + " won the event!");
						}
						else
							EventManager.getInstance().end("The match ended in a tie!");
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
		START, SAY, CHECK, END, INACTIVE
	}

	public EventState eventState;

	private Core task;

	public int round;

	public String say;

	private L2Npc npc;

	private L2Spawn spawn;

	private CreatureSay message;

	public L2PcInstance winner;

	public Simon()
	{
		super();
		eventId = 6;
		createNewTeam(1, "All", getColor("All"), getPosition("All", 1));
		task = new Core();
		round = 0;
		spawn = null;
		npc = null;
		winner = null;
	}

	public String createNewRandomString(int size)
	{
		String str = "";

		for (int i = 0; i < size; i++)
			str = str + (char) (Rnd.nextInt(26) + 97);

		return str;
	}

	@Override
	protected void endEvent()
	{
		winner = players.head().getNext().getKey();
		setStatus(EventState.END);
		schedule(1);

	}

	@Override
	protected String getScorebar()
	{
		return "";
	}

	@Override
	public void onSay(int type, L2PcInstance player, String text)
	{
		if (eventState == EventState.CHECK && getStatus(player) != -1)
		{
			if (text.equalsIgnoreCase(say))
			{
				setStatus(player, 1);
				player.sendMessage("Correct!");
				increasePlayersScore(player);
				player.getAppearance().setNameColor(0, 255, 0);
				player.broadcastUserInfo();
			}

			else
			{
				setStatus(player, -1);
				player.sendMessage("Wrong!");
				player.getAppearance().setNameColor(255, 0, 0);
				player.broadcastUserInfo();
			}

			int falses = 0;
			L2PcInstance falsed = null;
			for (L2PcInstance p : getPlayerList())
				if (getStatus(p) == 0)
				{
					falses++;
					falsed = p;
				}

			if (falses == 1)
			{
				int count = 0;
				for (L2PcInstance pla : getPlayerList())
					if (getStatus(pla) == 1)
						count++;

				if (count >= 1)
				{
					falsed.sendMessage("Last one!");
					falsed.getAppearance().setNameColor(255, 0, 0);
					falsed.broadcastUserInfo();
					setStatus(falsed, -1);
				}

				if (count == 0)
				{
					winner = getPlayersWithStatus(0).head().getNext().getValue();
					setStatus(EventState.END);
					schedule(1);
				}

			}

			if (countOfPositiveStatus() == 1)
			{
				winner = getPlayersWithStatus(1).head().getNext().getValue();
				setStatus(EventState.END);
				schedule(1);
			}

		}

	}

	public boolean removeAfkers()
	{

		for (L2PcInstance player : getPlayerList())
		{
			if (getStatus(player) == 0)
			{

				player.sendMessage("Timeout!");
				player.getAppearance().setNameColor(255, 0, 0);
				player.broadcastUserInfo();
				setStatus(player, -1);
			}

			if (countOfPositiveStatus() == 1)
			{
				if (getPlayersWithStatus(1).size() == 1)
					winner = getPlayersWithStatus(1).head().getNext().getValue();
				else
					winner = null;

				setStatus(EventState.END);
				schedule(1);
				return false;
			}
		}
		return true;
	}

	@Override
	public void reset()
	{
		super.reset();
		round = 0;
		players.clear();
		say = "";
		npc.deleteMe();
		spawn.stopRespawn();
		SpawnTable.getInstance().deleteSpawn(spawn, true);
		npc = null;
		spawn = null;
	}

	@Override
	protected void schedule(int time)
	{
		tpm.scheduleGeneral(task, time);
	}

	public void sendToPlayers(String text)
	{
		message = new CreatureSay(npc.getObjectId(), 1, "Simon", text);
		for (L2PcInstance player : getPlayerList())
			player.sendPacket(message);
	}

	public void setAllToFalse()
	{
		for (L2PcInstance player : getPlayerList())
			if (getStatus(player) != -1)
			{
				setStatus(player, 0);
				player.getAppearance().setNameColor(255, 255, 255);
				player.broadcastUserInfo();
			}
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
		sb.append("<center><table width=270 bgcolor=5A5A5A><tr><td width=70>Running</td><td width=130><center>" + getString("eventName") + "</td><td width=70>Time: ?</td></tr></table>");
		sb.append("<table width=270><tr><td><center>Players left: " + getPlayersWithStatus(0).size() + "</td></tr></table>");
		sb.append("<br><table width=270>");

		for (L2PcInstance p : getPlayersOfTeam(1))
			sb.append("<tr><td>" + p.getName() + "</td><td>lvl " + p.getLevel() + "</td><td>" + p.getTemplate().className + "</td><td>" + getScore(p) + "</td></tr>");

		sb.append("</table></body></html>");
		html.setHtml(sb.toString());
		player.sendPacket(html);
	}

	@Override
	protected void start()
	{
		int[] npcpos = getPosition("Simon", 1);
		spawn = spawnNPC(npcpos[0], npcpos[1], npcpos[2], getInt("simonNpcId"));
		npc = spawn.getLastSpawn();
		setStatus(EventState.START);
		schedule(1);
	}
	
	@Override
	public boolean onUseItem(L2PcInstance player, L2ItemInstance item)
	{
		return false;
	}
}