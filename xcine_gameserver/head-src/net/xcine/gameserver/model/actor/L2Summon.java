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
package net.xcine.gameserver.model.actor;

import net.xcine.Config;
import net.xcine.gameserver.GeoData;
import net.xcine.gameserver.ai.CtrlIntention;
import net.xcine.gameserver.ai.L2CharacterAI;
import net.xcine.gameserver.ai.L2SummonAI;
import net.xcine.gameserver.datatables.ItemTable;
import net.xcine.gameserver.event.EventManager;
import net.xcine.gameserver.handler.IItemHandler;
import net.xcine.gameserver.handler.ItemHandler;
import net.xcine.gameserver.model.L2ItemInstance;
import net.xcine.gameserver.model.L2Object;
import net.xcine.gameserver.model.L2Party;
import net.xcine.gameserver.model.L2Skill;
import net.xcine.gameserver.model.L2Skill.SkillTargetType;
import net.xcine.gameserver.model.L2WorldRegion;
import net.xcine.gameserver.model.ShotType;
import net.xcine.gameserver.model.actor.L2Attackable.AggroInfo;
import net.xcine.gameserver.model.actor.instance.L2DoorInstance;
import net.xcine.gameserver.model.actor.instance.L2PcInstance;
import net.xcine.gameserver.model.actor.instance.L2PetInstance;
import net.xcine.gameserver.model.actor.instance.L2SummonInstance;
import net.xcine.gameserver.model.actor.knownlist.SummonKnownList;
import net.xcine.gameserver.model.actor.stat.SummonStat;
import net.xcine.gameserver.model.actor.status.SummonStatus;
import net.xcine.gameserver.model.base.Experience;
import net.xcine.gameserver.model.itemcontainer.PetInventory;
import net.xcine.gameserver.model.olympiad.OlympiadGameManager;
import net.xcine.gameserver.network.SystemMessageId;
import net.xcine.gameserver.network.serverpackets.AbstractNpcInfo.SummonInfo;
import net.xcine.gameserver.network.serverpackets.ActionFailed;
import net.xcine.gameserver.network.serverpackets.L2GameServerPacket;
import net.xcine.gameserver.network.serverpackets.MoveToPawn;
import net.xcine.gameserver.network.serverpackets.MyTargetSelected;
import net.xcine.gameserver.network.serverpackets.NpcHtmlMessage;
import net.xcine.gameserver.network.serverpackets.PetDelete;
import net.xcine.gameserver.network.serverpackets.PetInfo;
import net.xcine.gameserver.network.serverpackets.PetItemList;
import net.xcine.gameserver.network.serverpackets.PetStatusShow;
import net.xcine.gameserver.network.serverpackets.PetStatusUpdate;
import net.xcine.gameserver.network.serverpackets.RelationChanged;
import net.xcine.gameserver.network.serverpackets.SystemMessage;
import net.xcine.gameserver.network.serverpackets.TeleportToLocation;
import net.xcine.gameserver.taskmanager.DecayTaskManager;
import net.xcine.gameserver.templates.chars.L2NpcTemplate;
import net.xcine.gameserver.templates.item.L2ActionType;
import net.xcine.gameserver.templates.item.L2Weapon;

public abstract class L2Summon extends L2Playable
{
	private L2PcInstance _owner;
	private boolean _follow = true;
	private boolean _previousFollowStatus = true;
	
	private int _shotsMask = 0;
	
	public class AIAccessor extends L2Character.AIAccessor
	{
		protected AIAccessor()
		{
		}
		
		public L2Summon getSummon()
		{
			return L2Summon.this;
		}
		
		public boolean isAutoFollow()
		{
			return getFollowStatus();
		}
		
		public void doPickupItem(L2Object object)
		{
			L2Summon.this.doPickupItem(object);
		}
	}
	
	public L2Summon(int objectId, L2NpcTemplate template, L2PcInstance owner)
	{
		super(objectId, template);
		
		_showSummonAnimation = true;
		_owner = owner;
		_ai = new L2SummonAI(new L2Summon.AIAccessor());
		
		setXYZInvisible(owner.getX() + 50, owner.getY() + 100, owner.getZ() + 100);
		
		if (EventManager.getInstance().isRunning() && EventManager.getInstance().isRegistered(owner))
			for (L2Skill skill : EventManager.getInstance().getCurrentEvent().getSummonBuffs(owner))
				skill.getEffects(owner, this);
	}
	
	@Override
	public void initKnownList()
	{
		setKnownList(new SummonKnownList(this));
	}
	
	@Override
	public final SummonKnownList getKnownList()
	{
		return (SummonKnownList) super.getKnownList();
	}
	
	@Override
	public void initCharStat()
	{
		setStat(new SummonStat(this));
	}
	
	@Override
	public SummonStat getStat()
	{
		return (SummonStat) super.getStat();
	}
	
	@Override
	public void initCharStatus()
	{
		setStatus(new SummonStatus(this));
	}
	
	@Override
	public SummonStatus getStatus()
	{
		return (SummonStatus) super.getStatus();
	}
	
	@Override
	public L2CharacterAI getAI()
	{
		L2CharacterAI ai = _ai; // copy handle
		if (ai == null)
		{
			synchronized (this)
			{
				if (_ai == null)
					_ai = new L2SummonAI(new L2Summon.AIAccessor());
				return _ai;
			}
		}
		return ai;
	}
	
	@Override
	public L2NpcTemplate getTemplate()
	{
		return (L2NpcTemplate) super.getTemplate();
	}
	
	// this defines the action buttons, 1 for Summon, 2 for Pets
	public abstract int getSummonType();
	
	@Override
	public void updateAbnormalEffect()
	{
		for (L2PcInstance player : getKnownList().getKnownPlayers())
			player.sendPacket(new SummonInfo(this, player, 1));
	}
	
	/**
	 * @return Returns the mountable.
	 */
	public boolean isMountable()
	{
		return false;
	}
	
	@Override
	public void onAction(L2PcInstance player)
	{
		if (player.getTarget() != this)
		{
			player.setTarget(this);
			player.sendPacket(new MyTargetSelected(getObjectId(), player.getLevel() - getLevel()));
		}
		else if (player == _owner && player.getTarget() == this)
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
				
				player.sendPacket(new PetStatusShow(this));
				
				// Send ActionFailed to the player in order to avoid he stucks
				player.sendPacket(ActionFailed.STATIC_PACKET);
			}
		}
		else
		{
			if (isAutoAttackable(player))
			{
				if (Config.GEODATA == 0 || GeoData.getInstance().canSeeTarget(player, this))
				{
					player.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, this);
					player.onActionRequest();
				}
			}
			else
			{
				// Rotate the player to face the instance
				player.sendPacket(new MoveToPawn(player, this, L2Npc.INTERACTION_DISTANCE));
				
				// Send ActionFailed to the player in order to avoid he stucks
				player.sendPacket(ActionFailed.STATIC_PACKET);
				
				if (Config.GEODATA == 0 || GeoData.getInstance().canSeeTarget(player, this))
					player.getAI().setIntention(CtrlIntention.AI_INTENTION_FOLLOW, this);
			}
		}
	}
	
	@Override
	public void onActionShift(L2PcInstance player)
	{
		if (player.isGM())
		{
			NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
			html.setFile("data/html/admin/petinfo.htm");
			String name = getName();
			html.replace("%name%", name == null ? "N/A" : name);
			html.replace("%level%", Integer.toString(getLevel()));
			html.replace("%exp%", Long.toString(getStat().getExp()));
			String owner = getActingPlayer().getName();
			html.replace("%owner%", " <a action=\"bypass -h admin_character_info " + owner + "\">" + owner + "</a>");
			html.replace("%class%", getClass().getSimpleName());
			html.replace("%ai%", hasAI() ? String.valueOf(getAI().getIntention().name()) : "NULL");
			html.replace("%hp%", (int) getStatus().getCurrentHp() + "/" + getStat().getMaxHp());
			html.replace("%mp%", (int) getStatus().getCurrentMp() + "/" + getStat().getMaxMp());
			html.replace("%karma%", Integer.toString(getKarma()));
			html.replace("%undead%", isUndead() ? "yes" : "no");
			
			if (this instanceof L2PetInstance)
			{
				int objId = getActingPlayer().getObjectId();
				html.replace("%inv%", " <a action=\"bypass admin_show_pet_inv " + objId + "\">view</a>");
				html.replace("%food%", ((L2PetInstance) this).getCurrentFed() + "/" + ((L2PetInstance) this).getPetLevelData().getPetMaxFeed());
				html.replace("%load%", ((L2PetInstance) this).getInventory().getTotalWeight() + "/" + ((L2PetInstance) this).getMaxLoad());
			}
			else
			{
				html.replace("%inv%", "none");
				html.replace("%food%", "N/A");
				html.replace("%load%", "N/A");
			}
			
			player.sendPacket(html);
			player.sendPacket(ActionFailed.STATIC_PACKET);
		}
		super.onActionShift(player);
	}
	
	public long getExpForThisLevel()
	{
		if (getLevel() >= Experience.LEVEL.length)
			return 0;
		
		return Experience.LEVEL[getLevel()];
	}
	
	public long getExpForNextLevel()
	{
		if (getLevel() >= Experience.LEVEL.length - 1)
			return 0;
		
		return Experience.LEVEL[getLevel() + 1];
	}
	
	@Override
	public final int getKarma()
	{
		return getOwner() != null ? getOwner().getKarma() : 0;
	}
	
	@Override
	public final byte getPvpFlag()
	{
		return getOwner() != null ? getOwner().getPvpFlag() : 0;
	}
	
	public final int getTeam()
	{
		return getOwner() != null ? getOwner().getTeam() : 0;
	}
	
	public final L2PcInstance getOwner()
	{
		return _owner;
	}
	
	public final int getNpcId()
	{
		return getTemplate().getNpcId();
	}
	
	public int getMaxLoad()
	{
		return 0;
	}
	
	public short getSoulShotsPerHit()
	{
		if (getTemplate().getAIDataStatic().getSoulShot() > 0)
			return (short) getTemplate().getAIDataStatic().getSoulShot();
		
		return 1;
	}
	
	public short getSpiritShotsPerHit()
	{
		if (getTemplate().getAIDataStatic().getSpiritShot() > 0)
			return (short) getTemplate().getAIDataStatic().getSpiritShot();
		
		return 1;
	}
	
	public void followOwner()
	{
		setFollowStatus(true);
	}
	
	@Override
	public boolean doDie(L2Character killer)
	{
		if (!super.doDie(killer))
			return false;
		
		final L2PcInstance owner = getOwner();
		if (owner != null)
		{
			for (L2Attackable mob : getKnownList().getKnownType(L2Attackable.class))
			{
				// get the mobs which have aggro on the this instance
				if (mob.isDead())
					continue;
				
				final AggroInfo info = mob.getAggroList().get(this);
				if (info != null)
					mob.addDamageHate(owner, info.getDamage(), info.getHate());
			}
			
			// Popup for summon if phoenix buff was on
			if (isPhoenixBlessed())
				owner.reviveRequest(owner, null, true);
		}
		
		DecayTaskManager.getInstance().addDecayTask(this);
		return true;
	}
	
	public boolean doDie(L2Character killer, boolean decayed)
	{
		if (!super.doDie(killer))
			return false;
		
		if (!decayed)
			DecayTaskManager.getInstance().addDecayTask(this);
		
		return true;
	}
	
	public void stopDecay()
	{
		DecayTaskManager.getInstance().cancelDecayTask(this);
	}
	
	@Override
	public void onDecay()
	{
		deleteMe(_owner);
	}
	
	@Override
	public void broadcastStatusUpdate()
	{
		super.broadcastStatusUpdate();
		updateAndBroadcastStatus(1);
	}
	
	public void deleteMe(L2PcInstance owner)
	{
		owner.sendPacket(new PetDelete(getSummonType(), getObjectId()));
		
		decayMe();
		getKnownList().removeAllKnownObjects();
		owner.setPet(null);
		super.deleteMe();
	}
	
	public void unSummon(L2PcInstance owner)
	{
		if (isVisible() && !isDead())
		{
			abortCast();
			abortAttack();
			
			stopHpMpRegeneration();
			getAI().stopFollow();
			
			owner.sendPacket(new PetDelete(getSummonType(), getObjectId()));
			
			store();
			owner.setPet(null);
			
			// Stop AI tasks
			if (hasAI())
				getAI().stopAITask();
			
			stopAllEffects();
			L2WorldRegion oldRegion = getWorldRegion();
			
			decayMe();
			
			if (oldRegion != null)
				oldRegion.removeFromZones(this);
			
			getKnownList().removeAllKnownObjects();
			setTarget(null);
			
			// Disable beastshots
			for (int itemId : owner.getAutoSoulShot())
			{
				switch (ItemTable.getInstance().getTemplate(itemId).getDefaultAction())
				{
					case summon_soulshot:
					case summon_spiritshot:
						owner.disableAutoShot(itemId);
						break;
				}
			}
		}
	}
	
	public int getAttackRange()
	{
		return 36;
	}
	
	public void setFollowStatus(boolean state)
	{
		_follow = state;
		if (_follow)
			getAI().setIntention(CtrlIntention.AI_INTENTION_FOLLOW, getOwner());
		else
			getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE, null);
	}
	
	public boolean getFollowStatus()
	{
		return _follow;
	}
	
	@Override
	public boolean isAutoAttackable(L2Character attacker)
	{
		return _owner.isAutoAttackable(attacker);
	}
	
	public int getControlItemId()
	{
		return 0;
	}
	
	public L2Weapon getActiveWeapon()
	{
		return null;
	}
	
	@Override
	public PetInventory getInventory()
	{
		return null;
	}
	
	protected void doPickupItem(L2Object object)
	{
	}
	
	public void store()
	{
	}
	
	@Override
	public L2ItemInstance getActiveWeaponInstance()
	{
		return null;
	}
	
	@Override
	public L2Weapon getActiveWeaponItem()
	{
		return null;
	}
	
	@Override
	public L2ItemInstance getSecondaryWeaponInstance()
	{
		return null;
	}
	
	@Override
	public L2Weapon getSecondaryWeaponItem()
	{
		return null;
	}
	
	/**
	 * Return True if the L2Summon is invulnerable or if the summoner is in spawn protection.<BR>
	 * <BR>
	 */
	@Override
	public boolean isInvul()
	{
		return super.isInvul() || getOwner().isSpawnProtected();
	}
	
	/**
	 * Return the L2Party object of its L2PcInstance owner or null.<BR>
	 * <BR>
	 */
	@Override
	public L2Party getParty()
	{
		if (_owner == null)
			return null;
		
		return _owner.getParty();
	}
	
	/**
	 * Return True if the L2Character has a Party in progress.<BR>
	 * <BR>
	 */
	@Override
	public boolean isInParty()
	{
		if (_owner == null)
			return false;
		
		return _owner.getParty() != null;
	}
	
	/**
	 * Check if the active L2Skill can be casted.<BR>
	 * <BR>
	 * <B><U> Actions</U> :</B><BR>
	 * <BR>
	 * <li>Check if the target is correct</li> <li>Check if the target is in the skill cast range</li> <li>Check if the summon owns enough HP and MP to cast the skill</li> <li>Check if all skills are enabled and this skill is enabled</li><BR>
	 * <BR>
	 * <li>Check if the skill is active</li><BR>
	 * <BR>
	 * <li>Notify the AI with AI_INTENTION_CAST and target</li><BR>
	 * <BR>
	 * @param skill The L2Skill to use
	 * @param forceUse used to force ATTACK on players
	 * @param dontMove used to prevent movement, if not in range
	 */
	@Override
	public boolean useMagic(L2Skill skill, boolean forceUse, boolean dontMove)
	{
		if (skill == null || isDead())
			return false;
		
		// Check if the skill is active and ignore the passive skill request
		if (skill.isPassive())
			return false;
		
		// ************************************* Check Casting in Progress *******************************************
		
		// If a skill is currently being used
		if (isCastingNow())
			return false;
		
		// Set current pet skill
		getOwner().setCurrentPetSkill(skill, forceUse, dontMove);
		
		// ************************************* Check Target *******************************************
		
		// Get the target for the skill
		L2Object target = null;
		
		switch (skill.getTargetType())
		{
		// OWNER_PET should be cast even if no target has been found
			case TARGET_OWNER_PET:
				target = getOwner();
				break;
			// PARTY, AURA, SELF should be cast even if no target has been found
			case TARGET_PARTY:
			case TARGET_AURA:
			case TARGET_FRONT_AURA:
			case TARGET_BEHIND_AURA:
			case TARGET_SELF:
				target = this;
				break;
			default:
				// Get the first target of the list
				target = skill.getFirstOfTargetList(this);
				break;
		}
		
		// Check the validity of the target
		if (target == null)
		{
			sendPacket(SystemMessageId.TARGET_CANT_FOUND);
			return false;
		}
		
		// ************************************* Check skill availability *******************************************
		
		// Check if this skill is enabled (e.g. reuse time)
		if (isSkillDisabled(skill))
		{
			sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_PREPARED_FOR_REUSE).addString(skill.getName()));
			return false;
		}
		
		// ************************************* Check Consumables *******************************************
		
		// Check if the summon has enough MP
		if (getCurrentMp() < getStat().getMpConsume(skill) + getStat().getMpInitialConsume(skill))
		{
			// Send a System Message to the caster
			sendPacket(SystemMessageId.NOT_ENOUGH_MP);
			return false;
		}
		
		// Check if the summon has enough HP
		if (getCurrentHp() <= skill.getHpConsume())
		{
			// Send a System Message to the caster
			sendPacket(SystemMessageId.NOT_ENOUGH_HP);
			return false;
		}
		
		// ************************************* Check Summon State *******************************************
		
		// Check if this is offensive magic skill
		if (skill.isOffensive())
		{
			if (isInsidePeaceZone(this, target) && getOwner() != null && (!getOwner().getAccessLevel().allowPeaceAttack()))
			{
				// If summon or target is in a peace zone, send a system message TARGET_IN_PEACEZONE
				sendPacket(SystemMessage.getSystemMessage(SystemMessageId.TARGET_IN_PEACEZONE));
				return false;
			}
			
			if (getOwner() != null && getOwner().isInOlympiadMode() && !getOwner().isOlympiadStart())
			{
				// if L2PcInstance is in Olympia and the match isn't already start, send ActionFailed
				sendPacket(ActionFailed.STATIC_PACKET);
				return false;
			}
			
			// Check if the target is attackable
			if (target instanceof L2DoorInstance)
			{
				if (!((L2DoorInstance) target).isAttackable(getOwner()))
					return false;
			}
			else
			{
				if (!target.isAttackable() && getOwner() != null && !getOwner().getAccessLevel().allowPeaceAttack())
					return false;
				
				// Check if a Forced ATTACK is in progress on non-attackable target
				if (!target.isAutoAttackable(this) && !forceUse && skill.getTargetType() != SkillTargetType.TARGET_AURA && skill.getTargetType() != SkillTargetType.TARGET_FRONT_AURA && skill.getTargetType() != SkillTargetType.TARGET_BEHIND_AURA && skill.getTargetType() != SkillTargetType.TARGET_CLAN && skill.getTargetType() != SkillTargetType.TARGET_ALLY && skill.getTargetType() != SkillTargetType.TARGET_PARTY && skill.getTargetType() != SkillTargetType.TARGET_SELF)
					return false;
			}
		}
		
		// Notify the AI with AI_INTENTION_CAST and target
		getAI().setIntention(CtrlIntention.AI_INTENTION_CAST, skill, target);
		return true;
	}
	
	@Override
	public void setIsImmobilized(boolean value)
	{
		super.setIsImmobilized(value);
		
		if (value)
		{
			_previousFollowStatus = getFollowStatus();
			// if immobilized, disable follow mode
			if (_previousFollowStatus)
				setFollowStatus(false);
		}
		else
		{
			// if not more immobilized, restore follow mode
			setFollowStatus(_previousFollowStatus);
		}
	}
	
	public void setOwner(L2PcInstance newOwner)
	{
		_owner = newOwner;
	}
	
	@Override
	public void sendDamageMessage(L2Character target, int damage, boolean mcrit, boolean pcrit, boolean miss)
	{
		if (miss || getOwner() == null)
			return;
		
		// Prevents the double spam of system messages, if the target is the owning player.
		if (target.getObjectId() != getOwner().getObjectId())
		{
			if (pcrit || mcrit)
				if (this instanceof L2SummonInstance)
					sendPacket(SystemMessageId.CRITICAL_HIT_BY_SUMMONED_MOB);
				else
					sendPacket(SystemMessageId.CRITICAL_HIT_BY_PET);
			
			final SystemMessage sm;
			
			if (target.isInvul())
			{
				if (target.isParalyzed())
					sm = SystemMessage.getSystemMessage(SystemMessageId.OPPONENT_PETRIFIED);
				else
					sm = SystemMessage.getSystemMessage(SystemMessageId.ATTACK_WAS_BLOCKED);
			}
			else
				sm = SystemMessage.getSystemMessage(SystemMessageId.PET_HIT_FOR_S1_DAMAGE).addNumber(damage);
			
			sendPacket(sm);
			
			if (getOwner().isInOlympiadMode() && target instanceof L2PcInstance && ((L2PcInstance) target).isInOlympiadMode() && ((L2PcInstance) target).getOlympiadGameId() == getOwner().getOlympiadGameId())
			{
				OlympiadGameManager.getInstance().notifyCompetitorDamage(getOwner(), damage);
			}
		}
	}
	
	@Override
	public void reduceCurrentHp(double damage, L2Character attacker, L2Skill skill)
	{
		super.reduceCurrentHp(damage, attacker, skill);
	}
	
	@Override
	public void doCast(L2Skill skill)
	{
		final L2PcInstance actingPlayer = getActingPlayer();
		if (!actingPlayer.checkPvpSkill(getTarget(), skill, true) && !actingPlayer.getAccessLevel().allowPeaceAttack())
		{
			// Send a System Message to the L2PcInstance
			actingPlayer.sendPacket(SystemMessageId.TARGET_IS_INCORRECT);
			
			// Send ActionFailed to the L2PcInstance
			actingPlayer.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		super.doCast(skill);
	}
	
	@Override
	public boolean isInCombat()
	{
		return getOwner() != null ? getOwner().isInCombat() : false;
	}
	
	@Override
	public final boolean isAttackingNow()
	{
		return isInCombat();
	}
	
	@Override
	public L2PcInstance getActingPlayer()
	{
		return getOwner();
	}
	
	@Override
	public String toString()
	{
		return super.toString() + "(" + getNpcId() + ") Owner: " + getOwner();
	}
	
	@Override
	public boolean isUndead()
	{
		return getTemplate().isUndead();
	}
	
	@Override
	public void sendPacket(L2GameServerPacket mov)
	{
		if (getOwner() != null)
			getOwner().sendPacket(mov);
	}
	
	@Override
	public void sendPacket(SystemMessageId id)
	{
		if (getOwner() != null)
			getOwner().sendPacket(id);
	}
	
	public int getWeapon()
	{
		return 0;
	}
	
	public int getArmor()
	{
		return 0;
	}
	
	@Override
	public void onTeleported()
	{
		super.onTeleported();
		sendPacket(new TeleportToLocation(this, getPosition().getX(), getPosition().getY(), getPosition().getZ()));
	}
	
	public void updateAndBroadcastStatusAndInfos(int val)
	{
		sendPacket(new PetInfo(this, val));
		
		// The PetInfo packet wipes the PartySpelled (list of active spells' icons). Re-add them
		updateEffectIcons(true);
		
		updateAndBroadcastStatus(val);
	}
	
	public void sendPetInfosToOwner()
	{
		sendPacket(new PetInfo(this, 2));
		
		// The PetInfo packet wipes the PartySpelled (list of active spells' icons). Re-add them
		updateEffectIcons(true);
	}
	
	public void updateAndBroadcastStatus(int val)
	{
		sendPacket(new PetStatusUpdate(this));
		
		if (isVisible())
			broadcastNpcInfo(val);
	}
	
	public void broadcastNpcInfo(int val)
	{
		for (L2PcInstance player : getKnownList().getKnownPlayers())
		{
			if (player == getOwner())
				continue;
			
			player.sendPacket(new SummonInfo(this, player, val));
		}
	}
	
	public boolean isHungry()
	{
		return false;
	}
	
	@Override
	public void onSpawn()
	{
		super.onSpawn();
		
		// Need it only for "crests on summons" custom.
		if (Config.SHOW_SUMMON_CREST)
			sendPacket(new SummonInfo(this, getOwner(), 0));
		
		sendPacket(new RelationChanged(this, getOwner().getRelation(getOwner()), false));
		broadcastRelationsChanges();
	}
	
	@Override
	public void broadcastRelationsChanges()
	{
		for (L2PcInstance player : getOwner().getKnownList().getKnownPlayersInRadius(800))
		{
			if (player != null)
				player.sendPacket(new RelationChanged(this, getOwner().getRelation(player), isAutoAttackable(player)));
		}
	}
	
	@Override
	public void sendInfo(L2PcInstance activeChar)
	{
		// Check if the L2PcInstance is the owner of the Pet
		if (activeChar.equals(getOwner()))
		{
			activeChar.sendPacket(new PetInfo(this, 0));
			
			// The PetInfo packet wipes the PartySpelled (list of active spells' icons). Re-add them
			updateEffectIcons(true);
			
			if (this instanceof L2PetInstance)
				activeChar.sendPacket(new PetItemList((L2PetInstance) this));
		}
		else
			activeChar.sendPacket(new SummonInfo(this, activeChar, 0));
	}
	
	@Override
	public boolean isChargedShot(ShotType type)
	{
		return (_shotsMask & type.getMask()) == type.getMask();
	}
	
	@Override
	public void setChargedShot(ShotType type, boolean charged)
	{
		if (charged)
			_shotsMask |= type.getMask();
		else
			_shotsMask &= ~type.getMask();
	}
	
	@Override
	public void rechargeShots(boolean physical, boolean magic)
	{
		if (getOwner().getAutoSoulShot() == null || getOwner().getAutoSoulShot().isEmpty())
			return;
		
		for (int itemId : getOwner().getAutoSoulShot())
		{
			L2ItemInstance item = getOwner().getInventory().getItemByItemId(itemId);
			if (item != null)
			{
				if (magic && item.getItem().getDefaultAction() == L2ActionType.summon_spiritshot)
				{
					IItemHandler handler = ItemHandler.getInstance().getItemHandler(item.getEtcItem());
					if (handler != null)
						handler.useItem(getOwner(), item, false);
				}
				
				if (physical && item.getItem().getDefaultAction() == L2ActionType.summon_soulshot)
				{
					IItemHandler handler = ItemHandler.getInstance().getItemHandler(item.getEtcItem());
					if (handler != null)
						handler.useItem(getOwner(), item, false);
				}
			}
			else
				getOwner().removeAutoSoulShot(itemId);
		}
	}
}