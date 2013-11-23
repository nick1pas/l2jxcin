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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

import net.xcine.gameserver.SevenSigns;
import net.xcine.gameserver.ThreadPoolManager;
import net.xcine.gameserver.ai.CtrlIntention;
import net.xcine.gameserver.datatables.SkillTable;
import net.xcine.gameserver.model.L2Object;
import net.xcine.gameserver.model.L2Skill;
import net.xcine.gameserver.model.actor.L2Character;
import net.xcine.gameserver.model.actor.L2Npc;
import net.xcine.gameserver.network.SystemMessageId;
import net.xcine.gameserver.network.serverpackets.ActionFailed;
import net.xcine.gameserver.network.serverpackets.MagicSkillUse;
import net.xcine.gameserver.network.serverpackets.MoveToPawn;
import net.xcine.gameserver.network.serverpackets.MyTargetSelected;
import net.xcine.gameserver.network.serverpackets.SystemMessage;
import net.xcine.gameserver.templates.chars.L2NpcTemplate;
import net.xcine.util.Rnd;

/**
 * @author Layane
 */
public class L2CabaleBufferInstance extends L2NpcInstance
{
	protected int _step = 0; // Flag used to delay chat broadcast.
	
	protected static final String[] MESSAGES_LOSER =
	{
		"%player_cabal_loser%! All is lost! Prepare to meet the goddess of death!",
		"%player_cabal_loser%! You bring an ill wind!",
		"%player_cabal_loser%! You might as well give up!",
		"A curse upon you!",
		"All is lost! Prepare to meet the goddess of death!",
		"All is lost! The prophecy of destruction has been fulfilled!",
		"The prophecy of doom has awoken!",
		"This world will soon be annihilated!"
	};
	
	protected static final String[] MESSAGES_WINNER =
	{
		"%player_cabal_winner%! I bestow on you the authority of the abyss!",
		"%player_cabal_winner%, Darkness shall be banished forever!",
		"%player_cabal_winner%, the time for glory is at hand!",
		"All hail the eternal twilight!",
		"As foretold in the prophecy of darkness, the era of chaos has begun!",
		"The day of judgment is near!",
		"The prophecy of darkness has been fulfilled!",
		"The prophecy of darkness has come to pass!"
	};
	
	@Override
	public void onAction(L2PcInstance player)
	{
		if (player.getTarget() != this)
		{
			// Set the target of the L2PcInstance player
			player.setTarget(this);
			
			// Send MyTargetSelected to the L2PcInstance player
			player.sendPacket(new MyTargetSelected(getObjectId(), 0));
		}
		else
		{
			// Calculate the distance between the L2PcInstance and the L2Npc
			if (!canInteract(player))
			{
				// Notify the L2PcInstance AI with AI_INTENTION_INTERACT
				player.getAI().setIntention(CtrlIntention.AI_INTENTION_INTERACT, this);
			}
			else
			{
				// Rotate the player to face the instance
				player.sendPacket(new MoveToPawn(player, this, L2Npc.INTERACTION_DISTANCE));
				
				// Send ActionFailed to the player in order to avoid he stucks
				player.sendPacket(ActionFailed.STATIC_PACKET);
			}
		}
	}
	
	private ScheduledFuture<?> _aiTask;
	
	/**
	 * For each known player in range, cast either the positive or negative buff. <BR>
	 * The stats affected depend on the player type, either a fighter or a mystic. <BR>
	 * <BR>
	 * Curse of Destruction (Loser)<BR>
	 * - Fighters: -25% Accuracy, -25% Effect Resistance<BR>
	 * - Mystics: -25% Casting Speed, -25% Effect Resistance<BR>
	 * <BR>
	 * Blessing of Prophecy (Winner)<BR>
	 * - Fighters: +25% Max Load, +25% Effect Resistance<BR>
	 * - Mystics: +25% Magic Cancel Resist, +25% Effect Resistance<BR>
	 */
	private class CabaleAI implements Runnable
	{
		private final L2CabaleBufferInstance _caster;
		
		protected CabaleAI(L2CabaleBufferInstance caster)
		{
			_caster = caster;
		}
		
		@Override
		public void run()
		{
			boolean isBuffAWinner = false;
			boolean isBuffALoser = false;
			
			final int winningCabal = SevenSigns.getInstance().getCabalHighestScore();
			int losingCabal = SevenSigns.CABAL_NULL;
			
			// Defines which cabal is the loser.
			if (winningCabal == SevenSigns.CABAL_DAWN)
				losingCabal = SevenSigns.CABAL_DUSK;
			else if (winningCabal == SevenSigns.CABAL_DUSK)
				losingCabal = SevenSigns.CABAL_DAWN;
			
			// That list stores visible && alive players for the shout.
			final List<L2PcInstance> playersList = new ArrayList<>();
			
			for (L2PcInstance player : getKnownList().getKnownPlayersInRadius(900))
			{
				// Don't go further if player is dead or not visible.
				if (player.isDead() || !player.isVisible())
					continue;
				
				playersList.add(player);
				
				final int playerCabal = SevenSigns.getInstance().getPlayerCabal(player.getObjectId());
				
				// Don't go further if player isn't from Dawn or Dusk sides.
				if (playerCabal != SevenSigns.CABAL_NULL)
				{
					if (!isBuffAWinner && playerCabal == winningCabal && _caster.getNpcId() == SevenSigns.ORATOR_NPC_ID)
					{
						isBuffAWinner = true;
						handleCast(player, (!player.isMageClass() ? 4364 : 4365));
					}
					else if (!isBuffALoser && playerCabal == losingCabal && _caster.getNpcId() == SevenSigns.PREACHER_NPC_ID)
					{
						isBuffALoser = true;
						handleCast(player, (!player.isMageClass() ? 4361 : 4362));
					}
					
					// Buff / debuff only 1 ppl per round.
					if (isBuffAWinner && isBuffALoser)
						break;
				}
			}
			
			// Autochat every 30sec. The actual AI cycle is 5sec, so delay it of 6 steps.
			if (_step >= 6)
			{
				if (!playersList.isEmpty())
				{
					// Pickup a random message from string arrays.
					String text;
					if (_caster.getCollisionHeight() > 30)
						text = MESSAGES_LOSER[Rnd.get(MESSAGES_LOSER.length)];
					else
						text = MESSAGES_WINNER[Rnd.get(MESSAGES_WINNER.length)];
					
					if (text.indexOf("%player_cabal_winner%") > -1)
					{
						for (L2PcInstance nearbyPlayer : playersList)
						{
							if (SevenSigns.getInstance().getPlayerCabal(nearbyPlayer.getObjectId()) == winningCabal)
								text = text.replaceAll("%player_cabal_winner%", nearbyPlayer.getName());
						}
					}
					else if (text.indexOf("%player_cabal_loser%") > -1)
					{
						for (L2PcInstance nearbyPlayer : playersList)
						{
							if (SevenSigns.getInstance().getPlayerCabal(nearbyPlayer.getObjectId()) == losingCabal)
								text = text.replaceAll("%player_cabal_loser%", nearbyPlayer.getName());
						}
					}
					_caster.broadcastNpcSay(text);
				}
				_step = 0;
			}
			else
				_step++;
		}
		
		private void handleCast(L2PcInstance player, int skillId)
		{
			int skillLevel = (player.getLevel() > 40) ? 1 : 2;
			
			final L2Skill skill = SkillTable.getInstance().getInfo(skillId, skillLevel);
			if (player.getFirstEffect(skill) == null)
			{
				skill.getEffects(_caster, player);
				broadcastPacket(new MagicSkillUse(_caster, player, skill.getId(), skillLevel, skill.getHitTime(), 0));
				player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.YOU_FEEL_S1_EFFECT).addSkillName(skillId));
			}
		}
	}
	
	public L2CabaleBufferInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
		
		if (_aiTask != null)
			_aiTask.cancel(true);
		
		_aiTask = ThreadPoolManager.getInstance().scheduleAiAtFixedRate(new CabaleAI(this), 5000, 5000);
	}
	
	@Override
	public void deleteMe()
	{
		if (_aiTask != null)
		{
			_aiTask.cancel(true);
			_aiTask = null;
		}
		
		super.deleteMe();
	}
	
	@Override
	public int getDistanceToWatchObject(L2Object object)
	{
		return 900;
	}
	
	@Override
	public boolean isAutoAttackable(L2Character attacker)
	{
		return false;
	}
}