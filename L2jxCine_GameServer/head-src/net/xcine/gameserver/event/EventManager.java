package net.xcine.gameserver.event;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Random;

import javolution.text.TextBuilder;
import javolution.util.FastList;
import javolution.util.FastMap;
import net.xcine.gameserver.model.L2Character;
import net.xcine.gameserver.model.L2Party;
import net.xcine.gameserver.model.L2World;
import net.xcine.gameserver.model.actor.instance.L2PcInstance;
import net.xcine.gameserver.network.serverpackets.CreatureSay;
import net.xcine.gameserver.network.serverpackets.NpcHtmlMessage;
import net.xcine.gameserver.thread.ThreadPoolManager;
import net.xcine.gameserver.util.Broadcast;

public final class EventManager
{
	
	public class Countdown implements Runnable
	{
		protected String getTime()
		{
			String mins = "" + counter / 60;
			String secs = (counter % 60 < 10 ? "0" + counter % 60 : "" + counter % 60);
			return "" + mins + ":" + secs + "";
		}

		@Override
		@SuppressWarnings("synthetic-access")
		public void run()
		{
			if (status == State.REGISTERING)

				switch (counter)
				{
					case 300:
					case 240:
					case 180:
					case 120:
					case 60:
					    announce("" + counter / 60 + " min(s) left to register, " + getCurrentEvent().getString("eventName"));
						break;
					case 30:
					case 10:
						announce("" + counter + " seconds left to register!");
						break;
				}

			if (status == State.VOTING)
				if (counter == getInt("showVotePopupAt") && getBoolean("votePopupEnabled"))
				{
					NpcHtmlMessage html = new NpcHtmlMessage(0);
					TextBuilder sb = new TextBuilder();
					int count = 0;
					
					sb.append("<html><body><table width=270><tr><td width=200>Event Engine </td><td><a action=\"bypass -h eventstats 1\">Statistics</a></td></tr></table><br>");

					sb.append("<center><table width=270 bgcolor=5A5A5A><tr><td width=70>Voting</td><td width=130><center>Time left: " + cdtask.getTime() + "</td><td width=70>Votes: " + votes.size() + "</td></tr></table><br>");

					for (Map.Entry<Integer, Event> event : events.entrySet())
					{
						count++;
						sb.append("<center><table width=270 " + (count % 2 == 1 ? "" : "bgcolor=5A5A5A") + "><tr><td width=180>" + event.getValue().getString("eventName") + "</td><td width=30>Info</td><td width=30>");
						sb.append("<a action=\"bypass -h eventvote " + event.getKey() + "\">Vote</a>");
						sb.append("</td><td width=30><center>" + getVoteCount(event.getKey()) + "</td></tr></table>");
					}

					sb.append("</body></html>");
					html.setHtml(sb.toString());

					for (L2PcInstance player : L2World.getInstance().getAllPlayers())
					{
						if (votes.containsKey(player) || player.getLevel() < 40)
							continue;

						player.sendPacket(html);
					}
				}

			if (counter == 0)
				schedule(1);
			else
			{
				counter--;
				tpm.scheduleGeneral(cdtask, 1000);
			}

		}
	}

	public class Scheduler implements Runnable
	{
		@Override
		@SuppressWarnings("synthetic-access")
		public void run()
		{
			switch (status)
			{
				case VOTING:
					if (votes.size() > 0)
						setCurrentEvent(getVoteWinner());
					else
						setCurrentEvent(eventIds.get(rnd.nextInt(eventIds.size())));

					announce("The next event will be: " + getCurrentEvent().getString("eventName"));
					announce("Registering phase started! You have " + getInt("registerTime") / 60 + " mins to register!");
                    announce("Event joinable in giran.");
					setStatus(State.REGISTERING);
					debug("Registering phase started.");
					counter = getInt("registerTime") - 1;
					tpm.scheduleGeneral(cdtask, 1);
					break;

				case REGISTERING:
					announce("Registering phase ended!");
					debug("Registering phase ended.");
					if (countOfRegistered() < getCurrentEvent().getInt("minPlayers"))
					{
						debug("Lack of participants.");
						announce("Theres not enough participants! Next event in " + getInt("betweenEventsTime") / 60 + "mins!");
						players.clear();
						colors.clear();
						positions.clear();
						setStatus(State.VOTING);
						counter = getInt("betweenEventsTime") - 1;
						tpm.scheduleGeneral(cdtask, 1);
					}
					else
					{
						debug("Event starts.");
						announce("Event started!");
						setStatus(State.RUNNING);
						msgToAll("You'll be teleported to the event in 10 secs");
						schedule(10000);
					}
					break;

				case RUNNING:
					getCurrentEvent().start();

					for (L2PcInstance player : players)
						EventStats.getInstance().tempTable.put(player.getObjectId(), new int[] { 0, 0, 0, 0 });

					break;

				case TELE_BACK:
					msgToAll("You'll be teleported back in 10 secs");
					setStatus(State.END);
					debug("Teleporting back.");
					schedule(10000);
					break;

				case END:
					teleBackEveryone();
					if (getBoolean("statTrackingEnabled"))
					{
						EventStats.getInstance().applyChanges();
						EventStats.getInstance().tempTable.clear();
						EventStats.getInstance().updateSQL(getCurrentEvent().getPlayerList(), getCurrentEvent().eventId);
					}
					getCurrentEvent().reset();
					setCurrentEvent(0);
					players.clear();
					colors.clear();
					positions.clear();
					titles.clear();
					announce("Event ended! Next event in " + getInt("betweenEventsTime") / 60 + "mins!");
					setStatus(State.VOTING);
					counter = getInt("betweenEventsTime") - 1;
					debug("Event ended.");
					tpm.scheduleGeneral(cdtask, 1);
					break;

			}
		}
	}

	private static class SingletonHolder
	{
		protected static final EventManager _instance = new EventManager();
	}

	// Manager Statuses
	protected enum State
	{
		REGISTERING, VOTING, RUNNING, TELE_BACK, END
	}

	public static EventManager getInstance()
	{
		return SingletonHolder._instance;
	}

	private EventConfig config;
	// Event instances
	public FastMap<Integer, Event> events;

	// The list of registered players
	public FastList<L2PcInstance> players;

	// The current event
	private Event current;

	// Original name colors
	private FastMap<L2PcInstance, Integer> colors;

	//Original titles
	protected FastMap<L2PcInstance, String> titles;

	//Original positions
	protected FastMap<L2PcInstance, int[]> positions;

	// Votes for the events
	private FastMap<L2PcInstance, Integer> votes;

	private State status;

	public int counter;

	private Countdown cdtask;

	// NPC location
	public static int[] npcPos = { 82698, 148638, -3473 };

	// Threadpoolmanager
	private ThreadPoolManager tpm;

	// Scheduler
	private Scheduler task;

	private Random rnd = new Random();

	private FileWriter writer;
	
	private FastList<Integer> eventIds;

	private static final SimpleDateFormat _formatter = new SimpleDateFormat("dd/MM/yyyy H:mm:ss");

	public EventManager()
	{
		config = EventConfig.getInstance();

		events = new FastMap<>();
		players = new FastList<>();
		votes = new FastMap<>();
		titles = new FastMap<>();
		colors = new FastMap<>();
		positions = new FastMap<>();
		eventIds = new FastList<>();
		status = State.VOTING;
		tpm = ThreadPoolManager.getInstance();
		task = new Scheduler();
		cdtask = new Countdown();
		counter = 0;

		// Add the events to the list
		events.put(1, new dm());
		events.put(2, new Domination());
		events.put(3, new DoubleDomination());
		events.put(4, new LMS());
		events.put(5, new Lucky());
		events.put(6, new Simon());
		events.put(7, new TvT());
		events.put(8, new VIPTvT());
		events.put(9, new Zombie());
		events.put(10, new CTF());
		events.put(11, new Russian());
		events.put(12, new Bomb());
		events.put(13, new Mutant());
		events.put(14, new Battlefield());

		for(int eventId : events.keySet())
			eventIds.add(eventId);
		
		debug(events.size() + " event imported.");

		// Start the scheduler
		counter = getInt("firstAfterStartTime") - 1;
		tpm.scheduleGeneral(cdtask, 1);

		System.out.println("Event Engine Started");
	}

	public boolean addVote(L2PcInstance player, int eventId)
	{
		if (getStatus() != State.VOTING)
		{
			player.sendMessage("You cant vote now!");
			return false;
		}
		if (votes.containsKey(player))
		{
			player.sendMessage("You already voted for an event!");
			return false;
		}
		if (player.getLevel() < 40)
		{
			player.sendMessage("Your level is too low to vote for events!");
			return false;
		}
		player.sendMessage("You succesfully voted for the event");
		votes.put(player, eventId);
		return true;
	}

	private void announce(String text)
	{
		Broadcast.toAllOnlinePlayers(new CreatureSay(0, 18, "", "[Event] " + text));
	}

	private boolean canRegister(L2PcInstance player)
	{
		if (players.contains(player))
		{
			player.sendMessage("You already registered to the event!");
			return false;
		}

		if (player.isInJail())
		{
			player.sendMessage("You cant register from the jail.");
			return false;
		}

		if (player.isInOlympiadMode())
		{
			player.sendMessage("You cant register while youre in the olympiad.");
			return false;
		}

		if (player.getLevel() > getCurrentEvent().getInt("maxLvl"))
		{
			player.sendMessage("Youre greater than the max allowed lvl.");
			return false;
		}

		if (player.getLevel() < getCurrentEvent().getInt("minLvl"))
		{
			player.sendMessage("Youre lower than the min allowed lvl.");
			return false;
		}

		if (player.getKarma() > 0)
		{
			player.sendMessage("You cant register if you have karma.");
			return false;
		}

		if (player.isCursedWeaponEquiped())
		{
			player.sendMessage("You cant register with a cursed weapon.");
			return false;
		}

		if (player.isDead())
		{
			player.sendMessage("You cant register while youre dead.");
			return false;
		}

		return true;
	} /*
		* If theres a running event and
		*/

	public boolean canTargetPlayer(L2PcInstance target, L2PcInstance self)
	{
		if (getStatus() == State.RUNNING)
		{
			if ((isRegistered(target) && isRegistered(self)) || (!isRegistered(target) && !isRegistered(self)))
				return true;
			return false;
		}
		return true;
	}

	protected int countOfRegistered()
	{
		return players.size();
	}

	protected void debug(String message)
	{
		if (!getBoolean("debug"))
			return;

		String today = _formatter.format(new Date());

		try
		{
			writer = new FileWriter("log/EventEngine.log", true);
			writer.write(today + " - " + message + "\r\n");
		}
		catch (IOException e)
		{

		}
		finally
		{
			try
			{
				writer.close();
			}
			catch (Exception e)
			{
			}
		}
	}

	protected void end(String text)
	{
		announce(text);
		status = State.TELE_BACK;
		schedule(1);
	}

	public boolean getBoolean(String propName)
	{
		return config.getBoolean(0, propName);
	}

	public Event getCurrentEvent()
	{
		return current;
	}

	public FastList<String> getEventNames()
	{
		FastList<String> map = new FastList<>();
		for (Event event : events.values())
			map.add(event.getString("eventName"));
		return map;
	}

	public int getInt(String propName)
	{
		return config.getInt(0, propName);
	}

	protected int[] getPosition(String owner, int num)
	{
		return config.getPosition(0, owner, num);
	}

	public FastList<Integer> getRestriction(String type)
	{
		return config.getRestriction(0, type);
	}

	private State getStatus()
	{
		return status;
	}

	protected String getString(String propName)
	{
		return config.getString(0, propName);
	}

	private int getVoteCount(int event)
	{
		int count = 0;
		for (int e : votes.values())
			if (e == event)
				count++;

		return count;
	}

	private int getVoteWinner()
	{
		int old = 0;
		FastMap<Integer, Integer> temp = new FastMap<>();

		for (int vote : votes.values())
			if (!temp.containsKey(vote))
				temp.put(vote, 1);
			else
			{
				old = temp.get(vote);
				old++;
				temp.getEntry(vote).setValue(old);
			}

		int max = temp.head().getNext().getValue();
		int result = temp.head().getNext().getKey();

		for (Map.Entry<Integer, Integer> entry : temp.entrySet())
			if (entry.getValue() > max)
			{
				max = entry.getValue();
				result = entry.getKey();
			}

		votes.clear();
		temp = null;
		return result;

	}

	public boolean isRegistered(L2PcInstance player)
	{
		if (getCurrentEvent() != null)
			return getCurrentEvent().players.containsKey(player);
		return false;
	}
	
	public boolean isRegistered(L2Character player)
	{
		if (getCurrentEvent() != null)
			return getCurrentEvent().players.containsKey(player);
		return false;
	}
	
	public boolean isRunning()
	{
		if (getStatus() == State.RUNNING)
			return true;
		return false;
	}

	private void msgToAll(String text)
	{
		for (L2PcInstance player : players)
			player.sendMessage(text);
	}
	
	public void onLogout(L2PcInstance player)
	{
		if (votes.containsKey(player))
			votes.remove(player);
		if (players.contains(player))
		{
			players.remove(player);
			colors.remove(player);
			titles.remove(player);
			positions.remove(player);
		}
	}

	public boolean registerPlayer(L2PcInstance player)
	{
		if (getStatus() != State.REGISTERING)
		{
			player.sendMessage("You can't register now!");
			return false;
		}

		if(getBoolean("eventBufferEnabled")) 
		 	if (!EventBuffer.getInstance().playerHaveTemplate(player)) 
		 	{ 
		 		player.sendMessage("You have to set a buff template first!"); 
		 		EventBuffer.getInstance().showHtml(player); 
		 		return false; 
		 	}
		
		if (canRegister(player))
		{
			player.sendMessage("You succesfully registered to the event!");
			players.add(player);
			titles.put(player, player.getTitle());
			colors.put(player, player.getAppearance().getNameColor());
			positions.put(player, new int[] { player.getX(), player.getY(), player.getZ() });
			return true;
		}
		player.sendMessage("You failed on registering to the event!");
		return false;
	}

	protected void schedule(int time)
	{
		tpm.scheduleGeneral(task, time);
	}

	private void setCurrentEvent(int eventId)
	{
		if (eventId == 0)
			current = null;
		else
			current = events.get(eventId);

		debug("Changed current event to: " + (current == null ? "null" : current.getString("eventName")));
	}

	protected void setStatus(State s)
	{
		status = s;
	}

	public void showFirstHtml(L2PcInstance player, int obj)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(obj);
		TextBuilder sb = new TextBuilder();
		int count = 0;

		sb.append("<html><body><table width=270><tr><td width=130>Event Engine </td><td width=70>"+(getBoolean("eventBufferEnabled") ? "<a action=\"bypass -h eventbuffershow\">Buffer</a>" : "")+"</td><td width=70><a action=\"bypass -h eventstats 1\">Statistics</a></td></tr></table><br>");

		if (getStatus() == State.VOTING)
		{

			sb.append("<center><table width=270 bgcolor=5A5A5A><tr><td width=70>Events</td><td width=130><center>Time left: " + cdtask.getTime() + "</td>"+/*<td width=70>Votes: " + votes.size() + "</td>*/"</tr></table><br>");

			for (Map.Entry<Integer, Event> event : events.entrySet())
				{
					count++;
					sb.append("<center><table width=270 " + (count % 2 == 1 ? "" : "bgcolor=5A5A5A") + "><tr><td width=180>" + event.getValue().getString("eventName") + "</td><td width=30><a action=\"bypass -h eventinfo "+event.getKey()+"\">Info</a></td>"/*<td width=30>*/);
	
					/*if (!votes.containsKey(player))
						sb.append("<a action=\"bypass -h npc_" + obj + "_" + event.getKey() + "\">Vote</a>");
				else
						sb.append("Vote");*/
	
					sb.append(/*</td><td width=30><center>" + getVoteCount(event.getKey()) + "</td>*/"</tr></table>");
				}
			sb.append("</body></html>");
			html.setHtml(sb.toString());
			player.sendPacket(html);
		}

		if (getStatus() == State.REGISTERING)
		{

			sb.append("<center><table width=270 bgcolor=5A5A5A><tr><td width=70>");
			if (players.contains(player))
				sb.append("<a action=\"bypass -h npc_" + obj + "_unreg\">Unregister</a>");
			else
				sb.append("<a action=\"bypass -h npc_" + obj + "_reg\">Register</a>");
			
			sb.append("</td><td width=130><center><a action=\"bypass -h eventinfo "+getCurrentEvent().getInt("ids")+"\">" + getCurrentEvent().getString("eventName") + "</a></td><td width=70>Time: " + cdtask.getTime() + "</td></tr></table><br>");

			for (L2PcInstance p : EventManager.getInstance().players)
			{
				count++;
				sb.append("<center><table width=270 " + (count % 2 == 1 ? "" : "bgcolor=5A5A5A") + "><tr><td width=120>" + p.getName() + "</td><td width=40>lvl " + p.getLevel() + "</td><td width=110>" + p.getTemplate().className + "</td></tr></table>");
			}

			sb.append("</body></html>");
			html.setHtml(sb.toString());
			player.sendPacket(html);
		}
		if (getStatus() == State.RUNNING)
			getCurrentEvent().showHtml(player, obj);

	}

	private void teleBackEveryone()
	{
		for (L2PcInstance player : getCurrentEvent().getPlayerList())
		{
			if(player.getPoly().isMorphed())
			{
				player.getPoly().setPolyInfo(null, "1");
				player.decayMe();
				player.spawnMe(player.getX(),player.getY(),player.getZ());
			}
			player.teleToLocation(positions.get(player)[0], positions.get(player)[1], positions.get(player)[2]);
			player.getAppearance().setNameColor(colors.get(player));
			player.setTitle(titles.get(player));
			if (player.getParty() != null)
			{
				L2Party party = player.getParty();
				party.removePartyMember(player);
			}
			
			player.broadcastUserInfo();
			if (player.isDead())
				player.doRevive();
		}

	}

	public boolean unregisterPlayer(L2PcInstance player)
	{
		if (!players.contains(player))
		{
			player.sendMessage("You're not registered to the event!");
			return false;
		}
		if (getStatus() != State.REGISTERING)
		{
			player.sendMessage("You can't unregister now!");
			return false;
		}
		player.sendMessage("You succesfully unregistered from the event!");
		players.remove(player);
		colors.remove(player);
		positions.remove(player);
		return true;
	}
	
	public boolean areTeammates(L2PcInstance player, L2PcInstance target) 
 	{ 
 		if(getCurrentEvent() == null) 
 			return false; 
 	
 		if(getCurrentEvent().numberOfTeams() == 1) 
 			return false; 
 	
		if(getCurrentEvent().numberOfTeams() > 1)
		{
			if(getCurrentEvent().getTeam(player) == getCurrentEvent().getTeam(target)) 
 				return true;
			return false;
		} 
 	
 		return false; 
 	}
}
