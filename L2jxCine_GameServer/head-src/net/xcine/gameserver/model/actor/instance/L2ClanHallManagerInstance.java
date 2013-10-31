/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */
package net.xcine.gameserver.model.actor.instance;

import java.text.SimpleDateFormat;
import java.util.StringTokenizer;

import net.xcine.Config;
import net.xcine.gameserver.controllers.TradeController;
import net.xcine.gameserver.datatables.SkillTable;
import net.xcine.gameserver.datatables.xml.TeleportLocationData;
import net.xcine.gameserver.managers.ClanHallManager;
import net.xcine.gameserver.managers.SiegeManager;
import net.xcine.gameserver.model.L2Clan;
import net.xcine.gameserver.model.L2Skill;
import net.xcine.gameserver.model.L2Skill.SkillType;
import net.xcine.gameserver.model.L2TeleportLocation;
import net.xcine.gameserver.model.L2TradeList;
import net.xcine.gameserver.model.entity.ClanHall;
import net.xcine.gameserver.network.SystemMessageId;
import net.xcine.gameserver.network.serverpackets.ActionFailed;
import net.xcine.gameserver.network.serverpackets.BuyList;
import net.xcine.gameserver.network.serverpackets.ClanHallDecoration;
import net.xcine.gameserver.network.serverpackets.MyTargetSelected;
import net.xcine.gameserver.network.serverpackets.NpcHtmlMessage;
import net.xcine.gameserver.network.serverpackets.SystemMessage;
import net.xcine.gameserver.network.serverpackets.ValidateLocation;
import net.xcine.gameserver.network.serverpackets.WareHouseDepositList;
import net.xcine.gameserver.network.serverpackets.WareHouseWithdrawalList;
import net.xcine.gameserver.templates.L2NpcTemplate;

/**
 * The Class L2ClanHallManagerInstance.
 */
public class L2ClanHallManagerInstance extends L2NpcInstance
{
	
	/** The Constant COND_OWNER_FALSE. */
	protected static final int COND_OWNER_FALSE = 0;
	
	/** The Constant COND_ALL_FALSE. */
	protected static final int COND_ALL_FALSE = 1;
	
	/** The Constant COND_BUSY_BECAUSE_OF_SIEGE. */
	protected static final int COND_BUSY_BECAUSE_OF_SIEGE = 2;
	
	/** The Constant COND_OWNER. */
	protected static final int COND_OWNER = 3;
	
	/** The _clan hall id. */
	private int _clanHallId = -1;

	/**
	 * Instantiates a new l2 clan hall manager instance.
	 *
	 * @param objectId the object id
	 * @param template the template
	 */
	public L2ClanHallManagerInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
	}

	/* (non-Javadoc)
	 * @see net.xcine.gameserver.model.actor.instance.L2FolkInstance#onBypassFeedback(net.xcine.gameserver.model.actor.instance.L2PcInstance, java.lang.String)
	 */
	@Override
	public void onBypassFeedback(L2PcInstance player, String command)
	{
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		int condition = validateCondition(player);
		if(condition <= COND_ALL_FALSE)
			return;
		else if(condition == COND_OWNER)
		{
			StringTokenizer st = new StringTokenizer(command, " ");
			String actualCommand = st.nextToken(); // Get actual command
			String val = "";
			if(st.countTokens() >= 1)
			{
				val = st.nextToken();
			}

			if(actualCommand.equalsIgnoreCase("banish_foreigner"))
			{
				if ((player.getClanPrivileges() & L2Clan.CP_CH_DISMISS) == L2Clan.CP_CH_DISMISS)
				{
					if (val.equalsIgnoreCase("list"))
					{
						NpcHtmlMessage html = new NpcHtmlMessage(1);
						html.setFile("data/html/clanHallManager/banish-list.htm");
						sendHtmlMessage(player, html);
						html = null;		
					}
					else if (val.equalsIgnoreCase("banish"))
					{
						getClanHall().banishForeigners();
						NpcHtmlMessage html = new NpcHtmlMessage(1);
						html.setFile("data/html/clanHallManager/banish.htm");
						sendHtmlMessage(player, html);
						html = null;
					}
				}
				else
				{
					NpcHtmlMessage html = new NpcHtmlMessage(1);
					html.setFile("data/html/clanHallManager/not_authorized.htm");
					sendHtmlMessage(player, html);
					html = null;
				}
				return;
			}
			else if(actualCommand.equalsIgnoreCase("manage_vault"))
			{
				if((player.getClanPrivileges() & L2Clan.CP_CL_VIEW_WAREHOUSE) == L2Clan.CP_CL_VIEW_WAREHOUSE)
				{
					if(val.equalsIgnoreCase("deposit"))
					{
						showVaultWindowDeposit(player);
					}
					else if(val.equalsIgnoreCase("withdraw"))
					{
						showVaultWindowWithdraw(player);
					}
					else
					{
						NpcHtmlMessage html = new NpcHtmlMessage(1);
						html.setFile("data/html/clanHallManager/vault.htm");
						sendHtmlMessage(player, html);
						html = null;
					}
				}
				else
				{
					player.sendMessage("You are not authorized to do this!");
				}
				return;
			}
			else if(actualCommand.equalsIgnoreCase("door"))
			{
				if((player.getClanPrivileges() & L2Clan.CP_CH_OPEN_DOOR) == L2Clan.CP_CH_OPEN_DOOR)
				{
					if(val.equalsIgnoreCase("open"))
					{
						getClanHall().openCloseDoors(true);
					}
					else if(val.equalsIgnoreCase("close"))
					{
						getClanHall().openCloseDoors(false);
					}
					else
					{
						NpcHtmlMessage html = new NpcHtmlMessage(1);
						html.setFile("data/html/clanHallManager/door.htm");
						sendHtmlMessage(player, html);
						html = null;
					}
				}
				else
				{
					player.sendMessage("You are not authorized to do this!");
				}
			}
			else if(actualCommand.equalsIgnoreCase("functions"))
			{
				if(val.equalsIgnoreCase("tele"))
				{
					NpcHtmlMessage html = new NpcHtmlMessage(1);
					if(getClanHall().getFunction(ClanHall.FUNC_TELEPORT) == null)
					{
						html.setFile("data/html/clanHallManager/chamberlain-nac.htm");
					}
					else
					{
						html.setFile("data/html/clanHallManager/tele" + getClanHall().getLocation() + getClanHall().getFunction(ClanHall.FUNC_TELEPORT).getLvl() + ".htm");
					}
					sendHtmlMessage(player, html);
				}
				else if(val.equalsIgnoreCase("item_creation"))
				{
					if(getClanHall().getFunction(ClanHall.FUNC_ITEM_CREATE) == null)
					{
						NpcHtmlMessage html = new NpcHtmlMessage(1);
						html.setFile("data/html/clanHallManager/chamberlain-nac.htm");
						sendHtmlMessage(player, html);
						html = null;
						return;
					}
					if(st.countTokens() < 1)
						return;
					int valbuy = Integer.parseInt(st.nextToken()) + getClanHall().getFunction(ClanHall.FUNC_ITEM_CREATE).getLvl() * 100000;
					showBuyWindow(player, valbuy);
				}
				else if(val.equalsIgnoreCase("support"))
				{

					NpcHtmlMessage html = new NpcHtmlMessage(1);
					if(getClanHall().getFunction(ClanHall.FUNC_SUPPORT) == null)
					{
						html.setFile("data/html/clanHallManager/chamberlain-nac.htm");
					}
					else
					{
						html.setFile("data/html/clanHallManager/support" + getClanHall().getFunction(ClanHall.FUNC_SUPPORT).getLvl() + ".htm");
						html.replace("%mp%", String.valueOf(getCurrentMp()));
					}
					sendHtmlMessage(player, html);
					html = null;
				}
				else if(val.equalsIgnoreCase("back"))
				{
					showMessageWindow(player);
				}
				else
				{
					NpcHtmlMessage html = new NpcHtmlMessage(1);
					html.setFile("data/html/clanHallManager/functions.htm");
					if(getClanHall().getFunction(ClanHall.FUNC_RESTORE_EXP) != null)
					{
						html.replace("%xp_regen%", String.valueOf(getClanHall().getFunction(ClanHall.FUNC_RESTORE_EXP).getLvl()) + "%");
					}
					else
					{
						html.replace("%xp_regen%", "0");
					}
					if(getClanHall().getFunction(ClanHall.FUNC_RESTORE_HP) != null)
					{
						html.replace("%hp_regen%", String.valueOf(getClanHall().getFunction(ClanHall.FUNC_RESTORE_HP).getLvl()) + "%");
					}
					else
					{
						html.replace("%hp_regen%", "0");
					}
					if(getClanHall().getFunction(ClanHall.FUNC_RESTORE_MP) != null)
					{
						html.replace("%mp_regen%", String.valueOf(getClanHall().getFunction(ClanHall.FUNC_RESTORE_MP).getLvl()) + "%");
					}
					else
					{
						html.replace("%mp_regen", "0");
					}
					sendHtmlMessage(player, html);
					html = null;
				}
			}
			else if(actualCommand.equalsIgnoreCase("manage"))
			{
				if((player.getClanPrivileges() & L2Clan.CP_CH_SET_FUNCTIONS) == L2Clan.CP_CH_SET_FUNCTIONS)
				{
					if(val.equalsIgnoreCase("recovery"))
					{
						if(st.countTokens() >= 1)
						{
							if(getClanHall().getOwnerId() == 0)
							{
								player.sendMessage("This clan Hall have no owner, you cannot change configuration");
								return;
							}
							val = st.nextToken();
							if(val.equalsIgnoreCase("hp"))
							{
								if(st.countTokens() >= 1)
								{
									int fee;
									if(Config.DEBUG)
									{
										_log.warning("Hp editing invoked");
									}
									val = st.nextToken();
									int percent = Integer.valueOf(val);
									switch(percent)
									{
										case 0:
											fee = 0;
											break;
										case 20:
											fee = Config.CH_HPREG1_FEE;
											break;
										case 40:
											fee = Config.CH_HPREG2_FEE;
											break;
										case 80:
											fee = Config.CH_HPREG3_FEE;
											break;
										case 100:
											fee = Config.CH_HPREG4_FEE;
											break;
										case 120:
											fee = Config.CH_HPREG5_FEE;
											break;
										case 140:
											fee = Config.CH_HPREG6_FEE;
											break;
										case 160:
											fee = Config.CH_HPREG7_FEE;
											break;
										case 180:
											fee = Config.CH_HPREG8_FEE;
											break;
										case 200:
											fee = Config.CH_HPREG9_FEE;
											break;
										case 220:
											fee = Config.CH_HPREG10_FEE;
											break;
										case 240:
											fee = Config.CH_HPREG11_FEE;
											break;
										case 260:
											fee = Config.CH_HPREG12_FEE;
											break;
										default:
											fee = Config.CH_HPREG13_FEE;
											break;
									}
									if(!getClanHall().updateFunctions(ClanHall.FUNC_RESTORE_HP, percent, fee, Config.CH_HPREG_FEE_RATIO, (getClanHall().getFunction(ClanHall.FUNC_RESTORE_HP) == null)))
									{
										player.sendMessage("You don't have enough adena in your clan's warehouse");
									}
									else
									{
										revalidateDeco(player);
									}
								}
							}
							else if(val.equalsIgnoreCase("mp"))
							{
								if(st.countTokens() >= 1)
								{
									int fee;
									if(Config.DEBUG)
									{
										_log.warning("Mp editing invoked");
									}
									val = st.nextToken();
									int percent = Integer.valueOf(val);
									switch(percent)
									{
										case 0:
											fee = 0;
											break;
										case 5:
											fee = Config.CH_MPREG1_FEE;
											break;
										case 10:
											fee = Config.CH_MPREG2_FEE;
											break;
										case 15:
											fee = Config.CH_MPREG3_FEE;
											break;
										case 30:
											fee = Config.CH_MPREG4_FEE;
											break;
										default:
											fee = Config.CH_MPREG5_FEE;
											break;
									}
									if(!getClanHall().updateFunctions(ClanHall.FUNC_RESTORE_MP, percent, fee, Config.CH_MPREG_FEE_RATIO, (getClanHall().getFunction(ClanHall.FUNC_RESTORE_MP) == null)))
									{
										player.sendMessage("You don't have enough adena in your clan's warehouse");
									}
									else
									{
										revalidateDeco(player);
									}
								}
							}
							else if(val.equalsIgnoreCase("exp"))
							{
								if(st.countTokens() >= 1)
								{
									int fee;
									if(Config.DEBUG)
									{
										_log.warning("Exp editing invoked");
									}
									val = st.nextToken();
									int percent = Integer.valueOf(val);
									switch(percent)
									{
										case 0:
											fee = 0;
											break;
										case 5:
											fee = Config.CH_EXPREG1_FEE;
											break;
										case 10:
											fee = Config.CH_EXPREG2_FEE;
											break;
										case 15:
											fee = Config.CH_EXPREG3_FEE;
											break;
										case 25:
											fee = Config.CH_EXPREG4_FEE;
											break;
										case 35:
											fee = Config.CH_EXPREG5_FEE;
											break;
										case 40:
											fee = Config.CH_EXPREG6_FEE;
											break;
										default:
											fee = Config.CH_EXPREG7_FEE;
											break;
									}
									if(!getClanHall().updateFunctions(ClanHall.FUNC_RESTORE_EXP, percent, fee, Config.CH_EXPREG_FEE_RATIO, (getClanHall().getFunction(ClanHall.FUNC_RESTORE_EXP) == null)))
									{
										player.sendMessage("You don't have enough adena in your clan's warehouse");
									}
									else
									{
										revalidateDeco(player);
									}
								}
							}
						}
						NpcHtmlMessage html = new NpcHtmlMessage(1);
						html.setFile("data/html/clanHallManager/edit_recovery" + getClanHall().getGrade() + ".htm");
						if(getClanHall().getFunction(ClanHall.FUNC_RESTORE_HP) != null)
						{
							html.replace("%hp%", String.valueOf(getClanHall().getFunction(ClanHall.FUNC_RESTORE_HP).getLvl()) + "%");
							html.replace("%hpPrice%", String.valueOf(getClanHall().getFunction(ClanHall.FUNC_RESTORE_HP).getLease()));
							html.replace("%hpDate%", format.format(getClanHall().getFunction(ClanHall.FUNC_RESTORE_HP).getEndTime()));
							html.replace("%hpRate%", String.valueOf(getClanHall().getFunction(ClanHall.FUNC_RESTORE_HP).getRate() / 86400000));
						}
						else
						{
							html.replace("%hp%", "0");
							html.replace("%hpPrice%", "0");
							html.replace("%hpDate%", "0");
							html.replace("%hpRate%", String.valueOf(Config.CH_HPREG_FEE_RATIO / 86400000));
						}
						if(getClanHall().getFunction(ClanHall.FUNC_RESTORE_EXP) != null)
						{
							html.replace("%exp%", String.valueOf(getClanHall().getFunction(ClanHall.FUNC_RESTORE_EXP).getLvl()) + "%");
							html.replace("%expPrice%", String.valueOf(getClanHall().getFunction(ClanHall.FUNC_RESTORE_EXP).getLease()));
							html.replace("%expDate%", format.format(getClanHall().getFunction(ClanHall.FUNC_RESTORE_EXP).getEndTime()));
							html.replace("%expRate%", String.valueOf(getClanHall().getFunction(ClanHall.FUNC_RESTORE_EXP).getRate() / 86400000));
						}
						else
						{
							html.replace("%exp%", "0");
							html.replace("%expPrice%", "0");
							html.replace("%expDate%", "0");
							html.replace("%expRate%", String.valueOf(Config.CH_EXPREG_FEE_RATIO / 86400000));
						}
						if(getClanHall().getFunction(ClanHall.FUNC_RESTORE_MP) != null)
						{
							html.replace("%mp%", String.valueOf(getClanHall().getFunction(ClanHall.FUNC_RESTORE_MP).getLvl()) + "%");
							html.replace("%mpPrice%", String.valueOf(getClanHall().getFunction(ClanHall.FUNC_RESTORE_MP).getLease()));
							html.replace("%mpDate%", format.format(getClanHall().getFunction(ClanHall.FUNC_RESTORE_MP).getEndTime()));
							html.replace("%mpRate%", String.valueOf(getClanHall().getFunction(ClanHall.FUNC_RESTORE_MP).getRate() / 86400000));
						}
						else
						{
							html.replace("%mp%", "0");
							html.replace("%mpPrice%", "0");
							html.replace("%mpDate%", "0");
							html.replace("%mpRate%", String.valueOf(Config.CH_MPREG_FEE_RATIO / 86400000));
						}
						sendHtmlMessage(player, html);
						html = null;
					}
					else if(val.equalsIgnoreCase("other"))
					{
						if(st.countTokens() >= 1)
						{
							if(getClanHall().getOwnerId() == 0)
							{
								player.sendMessage("This clan Hall have no owner, you cannot change configuration");
								return;
							}
							val = st.nextToken();
							if(val.equalsIgnoreCase("item"))
							{
								if(st.countTokens() >= 1)
								{
									if(getClanHall().getOwnerId() == 0)
									{
										player.sendMessage("This clan Hall have no owner, you cannot change configuration");
										return;
									}
									if(Config.DEBUG)
									{
										_log.warning("Item editing invoked");
									}
									val = st.nextToken();
									int fee;
									int lvl = Integer.valueOf(val);
									switch(lvl)
									{
										case 0:
											fee = 0;
											break;
										case 1:
											fee = Config.CH_ITEM1_FEE;
											break;
										case 2:
											fee = Config.CH_ITEM2_FEE;
											break;
										default:
											fee = Config.CH_ITEM3_FEE;
											break;
									}
									if(!getClanHall().updateFunctions(ClanHall.FUNC_ITEM_CREATE, lvl, fee, Config.CH_ITEM_FEE_RATIO, (getClanHall().getFunction(ClanHall.FUNC_ITEM_CREATE) == null)))
									{
										player.sendMessage("You don't have enough adena in your clan's warehouse");
									}
									else
									{
										revalidateDeco(player);
									}
								}
							}
							else if(val.equalsIgnoreCase("tele"))
							{
								if(st.countTokens() >= 1)
								{
									int fee;
									if(Config.DEBUG)
									{
										_log.warning("Tele editing invoked");
									}
									val = st.nextToken();
									int lvl = Integer.valueOf(val);
									switch(lvl)
									{
										case 0:
											fee = 0;
											break;
										case 1:
											fee = Config.CH_TELE1_FEE;
											break;
										default:
											fee = Config.CH_TELE2_FEE;
											break;
									}
									if(!getClanHall().updateFunctions(ClanHall.FUNC_TELEPORT, lvl, fee, Config.CH_TELE_FEE_RATIO, (getClanHall().getFunction(ClanHall.FUNC_TELEPORT) == null)))
									{
										player.sendMessage("You don't have enough adena in your clan's warehouse");
									}
									else
									{
										revalidateDeco(player);
									}
								}
							}
							else if(val.equalsIgnoreCase("support"))
							{
								if(st.countTokens() >= 1)
								{
									int fee;
									if(Config.DEBUG)
									{
										_log.warning("Support editing invoked");
									}
									val = st.nextToken();
									int lvl = Integer.valueOf(val);
									switch(lvl)
									{
										case 0:
											fee = 0;
											break;
										case 1:
											fee = Config.CH_SUPPORT1_FEE;
											break;
										case 2:
											fee = Config.CH_SUPPORT2_FEE;
											break;
										case 3:
											fee = Config.CH_SUPPORT3_FEE;
											break;
										case 4:
											fee = Config.CH_SUPPORT4_FEE;
											break;
										case 5:
											fee = Config.CH_SUPPORT5_FEE;
											break;
										case 6:
											fee = Config.CH_SUPPORT6_FEE;
											break;
										case 7:
											fee = Config.CH_SUPPORT7_FEE;
											break;
										default:
											fee = Config.CH_SUPPORT8_FEE;
											break;
									}
									if(!getClanHall().updateFunctions(ClanHall.FUNC_SUPPORT, lvl, fee, Config.CH_SUPPORT_FEE_RATIO, (getClanHall().getFunction(ClanHall.FUNC_SUPPORT) == null)))
									{
										player.sendMessage("You don't have enough adena in your clan's warehouse");
									}
									else
									{
										revalidateDeco(player);
									}
								}
							}
						}
						NpcHtmlMessage html = new NpcHtmlMessage(1);
						html.setFile("data/html/clanHallManager/edit_other" + getClanHall().getGrade() + ".htm");
						if(getClanHall().getFunction(ClanHall.FUNC_TELEPORT) != null)
						{
							html.replace("%tele%", String.valueOf(getClanHall().getFunction(ClanHall.FUNC_TELEPORT).getLvl()));
							html.replace("%telePrice%", String.valueOf(getClanHall().getFunction(ClanHall.FUNC_TELEPORT).getLease()));
							html.replace("%teleDate%", format.format(getClanHall().getFunction(ClanHall.FUNC_TELEPORT).getEndTime()));
							html.replace("%teleRate%", String.valueOf(getClanHall().getFunction(ClanHall.FUNC_TELEPORT).getRate() / 86400000));
						}
						else
						{
							html.replace("%tele%", "0");
							html.replace("%telePrice%", "0");
							html.replace("%teleDate%", "0");
							html.replace("%teleRate%", String.valueOf(Config.CH_TELE_FEE_RATIO / 86400000));
						}
						if(getClanHall().getFunction(ClanHall.FUNC_SUPPORT) != null)
						{
							html.replace("%support%", String.valueOf(getClanHall().getFunction(ClanHall.FUNC_SUPPORT).getLvl()));
							html.replace("%supportPrice%", String.valueOf(getClanHall().getFunction(ClanHall.FUNC_SUPPORT).getLease()));
							html.replace("%supportDate%", format.format(getClanHall().getFunction(ClanHall.FUNC_SUPPORT).getEndTime()));
							html.replace("%supportRate%", String.valueOf(getClanHall().getFunction(ClanHall.FUNC_SUPPORT).getRate() / 86400000));
						}
						else
						{
							html.replace("%support%", "0");
							html.replace("%supportPrice%", "0");
							html.replace("%supportDate%", "0");
							html.replace("%supportRate%", String.valueOf(Config.CH_SUPPORT_FEE_RATIO / 86400000));
						}
						if(getClanHall().getFunction(ClanHall.FUNC_ITEM_CREATE) != null)
						{
							html.replace("%item%", String.valueOf(getClanHall().getFunction(ClanHall.FUNC_ITEM_CREATE).getLvl()));
							html.replace("%itemPrice%", String.valueOf(getClanHall().getFunction(ClanHall.FUNC_ITEM_CREATE).getLease()));
							html.replace("%itemDate%", format.format(getClanHall().getFunction(ClanHall.FUNC_ITEM_CREATE).getEndTime()));
							html.replace("%itemRate%", String.valueOf(getClanHall().getFunction(ClanHall.FUNC_ITEM_CREATE).getRate() / 86400000));
						}
						else
						{
							html.replace("%item%", "0");
							html.replace("%itemPrice%", "0");
							html.replace("%itemDate%", "0");
							html.replace("%itemRate%", String.valueOf(Config.CH_ITEM_FEE_RATIO / 86400000));
						}
						sendHtmlMessage(player, html);
						html = null;
					}
					else if(val.equalsIgnoreCase("deco"))
					{
						if(st.countTokens() >= 1)
						{
							if(getClanHall().getOwnerId() == 0)
							{
								player.sendMessage("This clan Hall have no owner, you cannot change configuration");
								return;
							}
							val = st.nextToken();
							if(val.equalsIgnoreCase("curtains"))
							{
								if(st.countTokens() >= 1)
								{
									int fee;
									if(Config.DEBUG)
									{
										_log.warning("Deco curtains editing invoked");
									}
									val = st.nextToken();
									int lvl = Integer.valueOf(val);
									switch(lvl)
									{
										case 0:
											fee = 0;
											break;
										case 1:
											fee = Config.CH_CURTAIN1_FEE;
											break;
										default:
											fee = Config.CH_CURTAIN2_FEE;
											break;
									}
									if(!getClanHall().updateFunctions(ClanHall.FUNC_DECO_CURTAINS, lvl, fee, Config.CH_CURTAIN_FEE_RATIO, (getClanHall().getFunction(ClanHall.FUNC_DECO_CURTAINS) == null)))
									{
										player.sendMessage("You don't have enough adena in your clan's warehouse");
									}
									else
									{
										revalidateDeco(player);
									}
								}
							}
							else if(val.equalsIgnoreCase("porch"))
							{
								if(st.countTokens() >= 1)
								{
									int fee;
									if(Config.DEBUG)
									{
										_log.warning("Deco curtains editing invoked");
									}
									val = st.nextToken();
									int lvl = Integer.valueOf(val);
									switch(lvl)
									{
										case 0:
											fee = 0;
											break;
										case 1:
											fee = Config.CH_FRONT1_FEE;
											break;
										default:
											fee = Config.CH_FRONT2_FEE;
											break;
									}
									if(!getClanHall().updateFunctions(ClanHall.FUNC_DECO_FRONTPLATEFORM, lvl, fee, Config.CH_FRONT_FEE_RATIO, (getClanHall().getFunction(ClanHall.FUNC_DECO_FRONTPLATEFORM) == null)))
									{
										player.sendMessage("You don't have enough adena in your clan's warehouse");
									}
									else
									{
										revalidateDeco(player);
									}
								}
							}
						}
						NpcHtmlMessage html = new NpcHtmlMessage(1);
						html.setFile("data/html/clanHallManager/deco.htm");
						if(getClanHall().getFunction(ClanHall.FUNC_DECO_CURTAINS) != null)
						{
							html.replace("%curtain%", String.valueOf(getClanHall().getFunction(ClanHall.FUNC_DECO_CURTAINS).getLvl()));
							html.replace("%curtainPrice%", String.valueOf(getClanHall().getFunction(ClanHall.FUNC_DECO_CURTAINS).getLease()));
							html.replace("%curtainDate%", format.format(getClanHall().getFunction(ClanHall.FUNC_DECO_CURTAINS).getEndTime()));
							html.replace("%curtainRate%", String.valueOf(getClanHall().getFunction(ClanHall.FUNC_DECO_CURTAINS).getRate() / 86400000));
						}
						else
						{
							html.replace("%curtain%", "0");
							html.replace("%curtainPrice%", "0");
							html.replace("%curtainDate%", "0");
							html.replace("%curtainRate%", String.valueOf(Config.CH_CURTAIN_FEE_RATIO / 86400000));
						}
						if(getClanHall().getFunction(ClanHall.FUNC_DECO_FRONTPLATEFORM) != null)
						{
							html.replace("%porch%", String.valueOf(getClanHall().getFunction(ClanHall.FUNC_DECO_FRONTPLATEFORM).getLvl()));
							html.replace("%porchPrice%", String.valueOf(getClanHall().getFunction(ClanHall.FUNC_DECO_FRONTPLATEFORM).getLease()));
							html.replace("%porchDate%", format.format(getClanHall().getFunction(ClanHall.FUNC_DECO_FRONTPLATEFORM).getEndTime()));
							html.replace("%porchRate%", String.valueOf(getClanHall().getFunction(ClanHall.FUNC_DECO_FRONTPLATEFORM).getRate() / 86400000));
						}
						else
						{
							html.replace("%porch%", "0");
							html.replace("%porchPrice%", "0");
							html.replace("%porchDate%", "0");
							html.replace("%porchRate%", String.valueOf(Config.CH_FRONT_FEE_RATIO / 86400000));
						}
						sendHtmlMessage(player, html);
					}
					else if(val.equalsIgnoreCase("back"))
					{
						showMessageWindow(player);
					}
					else
					{
						NpcHtmlMessage html = new NpcHtmlMessage(1);
						html.setFile("data/html/clanHallManager/manage.htm");
						sendHtmlMessage(player, html);
						html = null;
					}
				}
				else
				{
					player.sendMessage("You are not authorized to do this!");
				}
				return;
			}
			else if(actualCommand.equalsIgnoreCase("support"))
			{
				setTarget(player);
				L2Skill skill;
				if(val == "")
					return;

				try
				{
					int skill_id = Integer.parseInt(val);
					try
					{
						int skill_lvl = 0;
						if(st.countTokens() >= 1)
						{
							skill_lvl = Integer.parseInt(st.nextToken());
						}
						skill = SkillTable.getInstance().getInfo(skill_id, skill_lvl);
						if(skill.getSkillType() == SkillType.SUMMON)
						{
							player.doCast(skill);
						}
						else
						{
							if(!(skill.getMpConsume() + skill.getMpInitialConsume() > getCurrentMp()))
							{
								doCast(skill);
							}
							else
							{
								player.sendMessage("The Clanhall Managers MP is to low.");
							}
						}
						if(getClanHall().getFunction(ClanHall.FUNC_SUPPORT) == null)
							return;
						NpcHtmlMessage html = new NpcHtmlMessage(1);
						if(getClanHall().getFunction(ClanHall.FUNC_SUPPORT).getLvl() == 0)
							return;
						html.setFile("data/html/clanHallManager/support" + getClanHall().getFunction(ClanHall.FUNC_SUPPORT).getLvl() + ".htm");
						html.replace("%mp%", String.valueOf(getCurrentMp()));
						sendHtmlMessage(player, html);
						skill = null;
						html = null;
					}
					catch(Exception e)
					{
						if(Config.ENABLE_ALL_EXCEPTIONS)
							e.printStackTrace();
						
						player.sendMessage("Invalid skill level!");
					}
				}
				catch(Exception e)
				{
					if(Config.ENABLE_ALL_EXCEPTIONS)
						e.printStackTrace();
					
					player.sendMessage("Invalid skill!");
				}
				return;
			}
			else if(actualCommand.equalsIgnoreCase("goto"))
			{
				int whereTo = Integer.parseInt(val);
				doTeleport(player, whereTo);
				return;
			}
			st = null;
			actualCommand = null;
			val = null;
		}
		format = null;
		super.onBypassFeedback(player, command);
	}

	/**
	 * this is called when a player interacts with this NPC.
	 *
	 * @param player the player
	 */
	@Override
	public void onAction(L2PcInstance player)
	{
		player.setLastFolkNPC(this);

		if(!canTarget(player))
			return;

		// Check if the L2PcInstance already target the L2NpcInstance
		if(this != player.getTarget())
		{
			// Set the target of the L2PcInstance player
			player.setTarget(this);

			// Send a Server->Client packet MyTargetSelected to the L2PcInstance player
			MyTargetSelected my = new MyTargetSelected(getObjectId(), 0);
			player.sendPacket(my);
			my = null;

			// Send a Server->Client packet ValidateLocation to correct the L2NpcInstance position and heading on the client
			player.sendPacket(new ValidateLocation(this));
		}
		else
		{
			// Calculate the distance between the L2PcInstance and the L2NpcInstance
			if(!canInteract(player))
			{
				// Notify the L2PcInstance AI with AI_INTENTION_INTERACT
				// note: commented out so the player must stand close
				//player.getAI().setIntention(CtrlIntention.AI_INTENTION_INTERACT, this);
			}
			else
			{
				showMessageWindow(player);
			}
		}
		// Send a Server->Client ActionFailed to the L2PcInstance in order to avoid that the client wait another packet
		player.sendPacket(ActionFailed.STATIC_PACKET);
	}

	/**
	 * Send html message.
	 *
	 * @param player the player
	 * @param html the html
	 */
	private void sendHtmlMessage(L2PcInstance player, NpcHtmlMessage html)
	{
		html.replace("%objectId%", String.valueOf(getObjectId()));
		html.replace("%npcname%", getName());
		html.replace("%npcId%", String.valueOf(getNpcId()));
		player.sendPacket(html);
		html = null;
	}

	/**
	 * Show message window.
	 *
	 * @param player the player
	 */
	private void showMessageWindow(L2PcInstance player)
	{
		player.sendPacket(ActionFailed.STATIC_PACKET);
		String filename = "data/html/clanHallManager/chamberlain-no.htm";

		int condition = validateCondition(player);
		if(condition == COND_OWNER)
		{
			filename = "data/html/clanHallManager/chamberlain.htm";// Owner message window
		}
		if(condition == COND_OWNER_FALSE)
		{
			filename = "data/html/clanHallManager/chamberlain-of.htm";
		}
		NpcHtmlMessage html = new NpcHtmlMessage(1);
		html.setFile(filename);
		html.replace("%objectId%", String.valueOf(getObjectId()));
		html.replace("%npcId%", String.valueOf(getNpcId()));
		html.replace("%npcname%", getName());
		player.sendPacket(html);
		html = null;
		filename = null;
	}

	/**
	 * Validate condition.
	 *
	 * @param player the player
	 * @return the int
	 */
	protected int validateCondition(L2PcInstance player)
	{
		if(getClanHall() == null)
			return COND_ALL_FALSE;
		//if (player.isGM()) return COND_OWNER;
		if(player.getClan() != null)
		{
			if(getClanHall().getOwnerId() == player.getClanId())
				return COND_OWNER;
			return COND_OWNER_FALSE;
		}
		return COND_ALL_FALSE;
	}

	/**
	 * Return the L2ClanHall this L2NpcInstance belongs to.
	 *
	 * @return the clan hall
	 */
	public final ClanHall getClanHall()
	{
		if(_clanHallId < 0)
		{
			ClanHall temp = ClanHallManager.getInstance().getNearbyClanHall(getX(), getY(), 500);

			if(temp != null)
			{
				_clanHallId = temp.getId();
				temp = null;
			}

			if(_clanHallId < 0)
				return null;
		}
		return ClanHallManager.getInstance().getClanHallById(_clanHallId);
	}

	/**
	 * Show vault window deposit.
	 *
	 * @param player the player
	 */
	private void showVaultWindowDeposit(L2PcInstance player)
	{
		player.sendPacket(ActionFailed.STATIC_PACKET);
		player.setActiveWarehouse(player.getClan().getWarehouse());
		player.sendPacket(new WareHouseDepositList(player, WareHouseDepositList.CLAN)); //Or Clan Hall??
	}

	/**
	 * Show vault window withdraw.
	 *
	 * @param player the player
	 */
	private void showVaultWindowWithdraw(L2PcInstance player)
	{
		player.sendPacket(ActionFailed.STATIC_PACKET);
		player.setActiveWarehouse(player.getClan().getWarehouse());
		player.sendPacket(new WareHouseWithdrawalList(player, WareHouseWithdrawalList.CLAN)); //Or Clan Hall ??
	}

	/**
	 * Do teleport.
	 *
	 * @param player the player
	 * @param val the val
	 */
	private void doTeleport(L2PcInstance player, int val)
	{
		if(Config.DEBUG)
		{
			player.sendMessage("doTeleport(L2PcInstance player, int val) is called");
		}
		L2TeleportLocation list = TeleportLocationData.getInstance().getTemplate(val);
		if(list != null)
		{
			//you cannot teleport to village that is in siege Not sure about this one though
			if(SiegeManager.getInstance().getSiege(list.getLocX(), list.getLocY(), list.getLocZ()) != null)
			{
				player.sendPacket(new SystemMessage(SystemMessageId.NO_PORT_THAT_IS_IN_SIGE));
				return;
			}
			else if(player.reduceAdena("Teleport", list.getPrice(), this, true))
			{
				if(Config.DEBUG)
				{
					_log.warning("Teleporting player " + player.getName() + " for CH to new location: " + list.getLocX() + ":" + list.getLocY() + ":" + list.getLocZ());
				}
				player.teleToLocation(list.getLocX(), list.getLocY(), list.getLocZ());
			}
			list = null;
		}
		else
		{
			_log.warning("No teleport destination with id:" + val);
		}
		player.sendPacket(ActionFailed.STATIC_PACKET);
	}

	/**
	 * Show buy window.
	 *
	 * @param player the player
	 * @param val the val
	 */
	private void showBuyWindow(L2PcInstance player, int val)
	{
		double taxRate = 0;

		if(getIsInTown())
		{
			taxRate = getCastle().getTaxRate();
		}

		player.tempInvetoryDisable();

		if(Config.DEBUG)
		{
			_log.fine("Showing buylist :" + player.getName() + " List ID :" + val);
		}

		L2TradeList list = TradeController.getInstance().getBuyList(val);

		if(list != null && list.getNpcId().equals(String.valueOf(getNpcId())))
		{
			BuyList bl = new BuyList(list, player.getAdena(), taxRate);
			player.sendPacket(bl);
			list = null;
			bl = null;
		}
		else
		{
			_log.warning("possible client hacker: " + player.getName() + " attempting to buy from GM shop! (L2ClanHallManagerInstance)");
			_log.warning("buylist id:" + val);
		}

		player.sendPacket(ActionFailed.STATIC_PACKET);
	}

	/**
	 * Revalidate deco.
	 *
	 * @param player the player
	 */
	private void revalidateDeco(L2PcInstance player)
	{
		ClanHallDecoration bl = new ClanHallDecoration(ClanHallManager.getInstance().getClanHallByOwner(player.getClan()));
		player.sendPacket(bl);
		bl = null;
	}
}
