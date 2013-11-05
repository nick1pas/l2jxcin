package net.xcine.gameserver.event;

import java.util.Map;

import javolution.text.TextBuilder;
import javolution.util.FastMap;
import net.xcine.gameserver.datatables.SkillTable;
import net.xcine.gameserver.model.L2Npc;
import net.xcine.gameserver.model.actor.instance.L2PcInstance;
import net.xcine.gameserver.model.spawn.L2Spawn;
import net.xcine.gameserver.network.serverpackets.NpcHtmlMessage;
import net.xcine.gameserver.network.serverpackets.NpcInfo;
import net.xcine.util.random.Rnd;

public class Battlefield extends Event
{
	public class Core implements Runnable
	{

		@Override
		public void run()
		{
			try{
			switch (eventState)
			{
				case START:
					divideIntoTeams(2);
					preparePlayers();
					teleportToTeamPos();
					createPartyOfTeam(1);
					createPartyOfTeam(2);
					forceSitAll();
					giveSkill();
					spawnBases();
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
					unspawnBases();
					removeSkill();
					setStatus(EventState.INACTIVE);
					EventManager.getInstance().end("Congratulation! The " + teams.get(winnerTeam).getName() + " team won the event with " + teams.get(winnerTeam).getScore() + " points!");
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

	public Core task;

	public int winnerTeam;
	
	private FastMap<Integer,L2Spawn> bases;
	
	private FastMap<Integer,Integer> owners;

	public Battlefield()
	{
		super();
		eventId = 14;
		createNewTeam(1, "Blue", getColor("Blue"), getPosition("Blue", 1));
		createNewTeam(2, "Red", getColor("Red"), getPosition("Red", 1));
		bases = new FastMap<>();
		owners = new FastMap<>();
		task = new Core();
		winnerTeam = 0;
	}

	@Override
	protected void endEvent()
	{
		winnerTeam = players.head().getNext().getValue()[0];

		setStatus(EventState.END);
		schedule(1);
	}

	@Override
	protected String getScorebar()
	{
		return "" + teams.get(1).getName() + ": " + teams.get(1).getScore() + "  " + teams.get(2).getName() + ": " + teams.get(2).getScore() + "  Time: " + clock.getTime();
	}

	@Override
	protected int getWinnerTeam()
	{
		if (teams.get(1).getScore() > teams.get(2).getScore())
			return 1;
		if (teams.get(2).getScore() > teams.get(1).getScore())
			return 2;
		if (teams.get(1).getScore() == teams.get(2).getScore())
		{
			if (Rnd.nextInt(1) == 1)
				return 1;
			return 2;
		}

		return 1;
	}

	@Override
	public void onDie(L2PcInstance victim, L2PcInstance killer)
	{
		super.onDie(victim, killer);
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
		sb.append("<html><body><table width=300>" + "<tr><td><center>Event phase</td></tr>" + "<tr><td><center>" + getString("eventName") + " - " + clock.getTime() + "</td></tr>" + "<tr><td><center><font color=" + teams.get(1).getHexaColor() + ">" + teams.get(1).getScore() + "</font> - " + "<font color=" + teams.get(2).getHexaColor() + ">" + teams.get(2).getScore() + "</font></td></tr>" + "</table><br><table width=300>");

		int i = 0;
		for (EventTeam team : teams.values())
		{
			i++;
			sb.append("<tr><td><font color=" + team.getHexaColor() + ">" + team.getName() + "</font> team</td><td></td><td></td><td></td></tr>");
			for (L2PcInstance p : getPlayersOfTeam(i))
				sb.append("<tr><td>" + p.getName() + "</td><td>lvl " + p.getLevel() + "</td><td>" + p.getTemplate().className + "</td><td>" + getScore(p) + "</td></tr>");
		}

		sb.append("</table><br></body></html>");
		html.setHtml(sb.toString());
		player.sendPacket(html);

	}

	@Override
	protected void start()
	{
		setStatus(EventState.START);
		schedule(1);
	}
	
	public void spawnBases()
	{
		for(int i = 1; i <= getInt("numOfBases"); i++)
		{
			bases.put(i, spawnNPC(getPosition("Base",i)[0],getPosition("Base",i)[1],getPosition("Base",i)[2],getInt("baseNpcId")));
			bases.get(i).getLastSpawn().setTitle("- Neutral -");
			owners.put(i, 0);
		}
	}
	
	public void unspawnBases()
	{
		for(L2Spawn base: bases.values())
			unspawnNPC(base);
	}
	
	@Override
	public void reset()
	{
		super.reset();
		bases.clear();
		owners.clear();
	}
	
	@Override
	protected void clockTick()
	{
		for(int owner : owners.values())
			if(owner != 0)
				teams.get(owner).increaseScore(1);
	}
	
	@Override
	public void useCapture(L2PcInstance player, L2Npc base)
	{
		if(base.getNpcId() != getInt("baseNpcId"))
			return;
		
		for(Map.Entry<Integer, L2Spawn> baseSpawn: bases.entrySet())
			if(baseSpawn.getValue().getLastSpawn().getObjectId() == base.getObjectId())
				{
					if(owners.get(baseSpawn.getKey()) == getTeam(player))
						return;
					
					owners.getEntry(baseSpawn.getKey()).setValue(getTeam(player));
					baseSpawn.getValue().getLastSpawn().setTitle("- "+teams.get(getTeam(player)).getName()+" -");
					for(L2PcInstance p : getPlayerList())
						p.sendPacket(new NpcInfo(baseSpawn.getValue().getLastSpawn(), p));
					
					announce(getPlayerList(),"The "+teams.get(getTeam(player)).getName()+" team captured a base!");
					increasePlayersScore(player);
					player.addItem("Event", 6392, 2, player, true);
				}
	}
	
	public void removeSkill()
	{
		for (L2PcInstance player : getPlayerList())
			player.removeSkill(SkillTable.getInstance().getInfo(getInt("captureSkillId"), 1), false);
	}
	
	public void giveSkill()
	{
		for (L2PcInstance player : getPlayerList())
			player.addSkill(SkillTable.getInstance().getInfo(getInt("captureSkillId"), 1), false);
	}
}