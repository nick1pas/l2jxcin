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
package net.xcine.gameserver.network.clientpackets;

import net.xcine.Config;
import net.xcine.gameserver.datatables.CharNameTable;
import net.xcine.gameserver.datatables.CharTemplateTable;
import net.xcine.gameserver.datatables.SkillTable;
import net.xcine.gameserver.datatables.SkillTreeTable;
import net.xcine.gameserver.idfactory.IdFactory;
import net.xcine.gameserver.instancemanager.QuestManager;
import net.xcine.gameserver.model.L2ItemInstance;
import net.xcine.gameserver.model.L2ShortCut;
import net.xcine.gameserver.model.L2SkillLearn;
import net.xcine.gameserver.model.L2World;
import net.xcine.gameserver.model.actor.instance.L2PcInstance;
import net.xcine.gameserver.model.quest.Quest;
import net.xcine.gameserver.model.quest.QuestState;
import net.xcine.gameserver.network.L2GameClient;
import net.xcine.gameserver.network.serverpackets.CharCreateFail;
import net.xcine.gameserver.network.serverpackets.CharCreateOk;
import net.xcine.gameserver.network.serverpackets.CharSelectInfo;
import net.xcine.gameserver.templates.chars.L2PcTemplate;
import net.xcine.gameserver.templates.item.L2Item;
import net.xcine.gameserver.util.Util;

@SuppressWarnings("unused")
public final class CharacterCreate extends L2GameClientPacket
{
	// cSdddddddddddd
	private String _name;
	private int _race;
	private byte _sex;
	private int _classId;
	private int _int;
	private int _str;
	private int _con;
	private int _men;
	private int _dex;
	private int _wit;
	private byte _hairStyle;
	private byte _hairColor;
	private byte _face;
	
	@Override
	protected void readImpl()
	{
		_name = readS();
		_race = readD();
		_sex = (byte) readD();
		_classId = readD();
		_int = readD();
		_str = readD();
		_con = readD();
		_men = readD();
		_dex = readD();
		_wit = readD();
		_hairStyle = (byte) readD();
		_hairColor = (byte) readD();
		_face = (byte) readD();
	}
	
	@Override
	protected void runImpl()
	{
		if (_name.length() > 16)
		{
			if (Config.DEBUG)
				_log.fine("Character Creation Failure: name " + _name + " is too long. Creation has failed.");
			
			sendPacket(new CharCreateFail(CharCreateFail.REASON_16_ENG_CHARS));
			return;
		}
		
		if (!Util.isValidPlayerName(_name))
		{
			if (Config.DEBUG)
				_log.fine("Character Creation Failure: name " + _name + " is invalid. Creation has failed.");
			
			sendPacket(new CharCreateFail(CharCreateFail.REASON_INCORRECT_NAME));
			return;
		}
		
		if (_face > 2 || _face < 0)
		{
			_log.warning("Character Creation Failure: Character face " + _face + " is invalid. Possible client hack: " + getClient());
			
			sendPacket(new CharCreateFail(CharCreateFail.REASON_CREATION_FAILED));
			return;
		}
		
		if (_hairStyle < 0 || (_sex == 0 && _hairStyle > 4) || (_sex != 0 && _hairStyle > 6))
		{
			_log.warning("Character Creation Failure: Character hair style " + _hairStyle + " is invalid. Possible client hack: " + getClient());
			
			sendPacket(new CharCreateFail(CharCreateFail.REASON_CREATION_FAILED));
			return;
		}
		
		if (_hairColor > 3 || _hairColor < 0)
		{
			_log.warning("Character Creation Failure: Character hair color " + _hairColor + " is invalid. Possible client hack: " + getClient());
			
			sendPacket(new CharCreateFail(CharCreateFail.REASON_CREATION_FAILED));
			return;
		}
		
		L2PcInstance newChar = null;
		L2PcTemplate template = null;
		
		/*
		 * DrHouse: Since checks for duplicate names are done using SQL, lock must be held until data is written to DB as well.
		 */
		synchronized (CharNameTable.getInstance())
		{
			if (CharNameTable.accountCharNumber(getClient().getAccountName()) >= 7)
			{
				if (Config.DEBUG)
					_log.fine("Max number of characters reached. Creation failed.");
				
				sendPacket(new CharCreateFail(CharCreateFail.REASON_TOO_MANY_CHARACTERS));
				return;
			}
			else if (CharNameTable.doesCharNameExist(_name))
			{
				if (Config.DEBUG)
					_log.fine("Character Creation Failure: " + _name + " already exists. Creation has failed.");
				
				sendPacket(new CharCreateFail(CharCreateFail.REASON_NAME_ALREADY_EXISTS));
				return;
			}
			
			template = CharTemplateTable.getInstance().getTemplate(_classId);
			if (template == null || template.classBaseLevel > 1)
			{
				sendPacket(new CharCreateFail(CharCreateFail.REASON_CREATION_FAILED));
				return;
			}
			
			int objectId = IdFactory.getInstance().getNextId();
			newChar = L2PcInstance.create(objectId, template, getClient().getAccountName(), _name, _hairStyle, _hairColor, _face, _sex != 0);
		}
		
		newChar.setCurrentCp(0);
		newChar.setCurrentHp(newChar.getMaxHp());
		newChar.setCurrentMp(newChar.getMaxMp());
		
		// send acknowledgement
		sendPacket(CharCreateOk.STATIC_PACKET);
		initNewChar(getClient(), newChar);
	}
	
	private static void initNewChar(L2GameClient client, L2PcInstance newChar)
	{
		if (Config.DEBUG)
			_log.fine("Character initialization is started.");
		
		L2World.getInstance().storeObject(newChar);
		L2PcTemplate template = newChar.getTemplate();
		
		newChar.addAdena("Init", Config.STARTING_ADENA, null, false);
		newChar.addAncientAdena("Init", Config.STARTING_ANCIENT, null, false);
		newChar.setXYZInvisible(template.spawnX, template.spawnY, template.spawnZ);
		newChar.setTitle("");
		
		newChar.registerShortCut(new L2ShortCut(0, 0, 3, 2, -1, 1)); // attack shortcut
		newChar.registerShortCut(new L2ShortCut(3, 0, 3, 5, -1, 1)); // take shortcut
		newChar.registerShortCut(new L2ShortCut(10, 0, 3, 0, -1, 1)); // sit shortcut
		
		for (L2Item ia : template.getItems())
		{
			L2ItemInstance item = newChar.getInventory().addItem("Init", ia.getItemId(), 1, newChar, null);
			if (item.getItemId() == 5588) // tutorial book shortcut
				newChar.registerShortCut(new L2ShortCut(11, 0, 1, item.getObjectId(), -1, 1));
			
			if (item.isEquipable())
			{
				if (newChar.getActiveWeaponItem() == null || !(item.getItem().getType2() != L2Item.TYPE2_WEAPON))
					newChar.getInventory().equipItemAndRecord(item);
			}
		}
		
		for (L2SkillLearn skill : SkillTreeTable.getInstance().getAvailableSkills(newChar, newChar.getClassId()))
		{
			newChar.addSkill(SkillTable.getInstance().getInfo(skill.getId(), skill.getLevel()), true);
			if (skill.getId() == 1001 || skill.getId() == 1177)
				newChar.registerShortCut(new L2ShortCut(1, 0, 2, skill.getId(), 1, 1));
			
			if (skill.getId() == 1216)
				newChar.registerShortCut(new L2ShortCut(10, 0, 2, skill.getId(), 1, 1));
			
			if (Config.DEBUG)
				_log.fine("adding starter skill:" + skill.getId() + " / " + skill.getLevel());
		}
		
		if (!Config.DISABLE_TUTORIAL)
		{
			if (newChar.getQuestState("Tutorial") == null)
			{
				Quest q = QuestManager.getInstance().getQuest("Tutorial");
				if (q != null)
					q.newQuestState(newChar).setState(Quest.STATE_STARTED);
			}
		}
		
		newChar.setOnlineStatus(true, false);
		newChar.deleteMe();
		
		CharSelectInfo cl = new CharSelectInfo(client.getAccountName(), client.getSessionId().playOkID1);
		client.getConnection().sendPacket(cl);
		client.setCharSelection(cl.getCharInfo());
		
		if (Config.DEBUG)
			_log.fine("Character init end");
	}
}