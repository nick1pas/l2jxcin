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
package net.xcine.gameserver.model.actor.instance;

import java.util.List;

import net.xcine.gameserver.ThreadPoolManager;
import net.xcine.gameserver.ai.CtrlIntention;
import net.xcine.gameserver.ai.L2AttackableAI;
import net.xcine.gameserver.model.L2CharPosition;
import net.xcine.gameserver.model.L2World;
import net.xcine.gameserver.model.L2WorldRegion;
import net.xcine.gameserver.model.actor.L2Attackable;
import net.xcine.gameserver.model.actor.L2Character;
import net.xcine.gameserver.model.actor.L2Npc;
import net.xcine.gameserver.model.actor.knownlist.GuardKnownList;
import net.xcine.gameserver.model.quest.Quest;
import net.xcine.gameserver.model.quest.QuestEventType;
import net.xcine.gameserver.network.serverpackets.ActionFailed;
import net.xcine.gameserver.network.serverpackets.MoveToPawn;
import net.xcine.gameserver.network.serverpackets.MyTargetSelected;
import net.xcine.gameserver.templates.chars.L2NpcTemplate;
import net.xcine.util.Rnd;

/**
 * This class manages all Guards in the world.<br>
 * It inherits all methods from L2Attackable and adds some more such as:
 * <ul>
 * <li>tracking PK</li>
 * <li>aggressive L2MonsterInstance.</li>
 * </ul>
 */
public final class L2GuardInstance extends L2Attackable
{
	private static final int RETURN_INTERVAL = 60000;
	
	public class ReturnTask implements Runnable
	{
		@Override
		public void run()
		{
			if (getAI().getIntention() == CtrlIntention.AI_INTENTION_IDLE)
				returnHome();
		}
	}
	
	public L2GuardInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
		ThreadPoolManager.getInstance().scheduleAiAtFixedRate(new ReturnTask(), RETURN_INTERVAL, RETURN_INTERVAL + Rnd.get(60000));
	}
	
	@Override
	public void initKnownList()
	{
		setKnownList(new GuardKnownList(this));
	}
	
	@Override
	public final GuardKnownList getKnownList()
	{
		return (GuardKnownList) super.getKnownList();
	}
	
	@Override
	public boolean isAutoAttackable(L2Character attacker)
	{
		return attacker instanceof L2MonsterInstance;
	}
	
	/**
	 * Notify the L2GuardInstance to return to its home location (AI_INTENTION_MOVE_TO) and clear its _aggroList.<BR>
	 * <BR>
	 */
	@Override
	public void returnHome()
	{
		if (!isInsideRadius(getSpawn().getLocx(), getSpawn().getLocy(), L2Npc.INTERACTION_DISTANCE, false))
		{
			clearAggroList();
			getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, new L2CharPosition(getSpawn().getLocx(), getSpawn().getLocy(), getSpawn().getLocz(), 0));
		}
	}
	
	@Override
	public void onSpawn()
	{
		setIsNoRndWalk(true);
		super.onSpawn();
		
		// check the region where this mob is, do not activate the AI if region is inactive.
		L2WorldRegion region = L2World.getInstance().getRegion(getX(), getY());
		if (region != null && !region.isActive())
			((L2AttackableAI) getAI()).stopAITask();
	}
	
	@Override
	public String getHtmlPath(int npcId, int val)
	{
		String filename = "";
		if (val == 0)
			filename = "" + npcId;
		else
			filename = npcId + "-" + val;
		
		return "data/html/guard/" + filename + ".htm";
	}
	
	@Override
	public void onAction(L2PcInstance player)
	{
		// Check if the L2PcInstance already target the L2GuardInstance
		if (player.getTarget() != this)
		{
			// Set the target of the L2PcInstance player
			player.setTarget(this);
			
			// Send MyTargetSelected to the L2PcInstance player
			player.sendPacket(new MyTargetSelected(getObjectId(), 0));
		}
		else
		{
			// Check if the L2PcInstance is in the _aggroList of the L2GuardInstance
			if (containsTarget(player))
			{
				// Set the L2PcInstance Intention to AI_INTENTION_ATTACK
				player.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, this);
			}
			else
			{
				// Calculate the distance between the L2PcInstance and the L2Npc
				if (!canInteract(player))
				{
					// Set the L2PcInstance Intention to AI_INTENTION_INTERACT
					player.getAI().setIntention(CtrlIntention.AI_INTENTION_INTERACT, this);
				}
				else
				{
					// Some guards have no HTMs on retail. Bypass the chat window if such guard is met.
					switch (getNpcId())
					{
						case 31671:
						case 31672:
						case 31673:
						case 31674:
							// Send a Server->Client ActionFailed to the L2PcInstance in order to avoid that the client wait another packet
							player.sendPacket(ActionFailed.STATIC_PACKET);
							return;
					}
					
					// Rotate the player to face the instance
					player.sendPacket(new MoveToPawn(player, this, L2Npc.INTERACTION_DISTANCE));
					
					if (hasRandomAnimation())
						onRandomAnimation(Rnd.get(8));
					
					List<Quest> qlsa = getTemplate().getEventQuests(QuestEventType.QUEST_START);
					if (qlsa != null && !qlsa.isEmpty())
						player.setLastQuestNpcObject(getObjectId());
					
					List<Quest> qlst = getTemplate().getEventQuests(QuestEventType.ON_FIRST_TALK);
					if (qlst != null && qlst.size() == 1)
						qlst.get(0).notifyFirstTalk(this, player);
					else
						showChatWindow(player);
				}
			}
		}
	}
	
	@Override
	public boolean isGuard()
	{
		return true;
	}
}