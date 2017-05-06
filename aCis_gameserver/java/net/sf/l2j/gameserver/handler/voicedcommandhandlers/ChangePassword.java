package net.sf.l2j.gameserver.handler.voicedcommandhandlers;

import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Base64;
import java.util.StringTokenizer;

import net.sf.l2j.L2DatabaseFactory;
import net.sf.l2j.gameserver.handler.IVoicedCommandHandler;
import net.sf.l2j.gameserver.model.actor.instance.Player;
import net.sf.l2j.gameserver.network.serverpackets.ExShowScreenMessage;

public class ChangePassword implements IVoicedCommandHandler
{
	private static final String[] _voicedCommands =
	{
		"changepass"
	};
	
	@Override
	public boolean useVoicedCommand(String command, Player activeChar, String target)
	{
		if (command.equalsIgnoreCase("changepass") && target != null)
		{
			StringTokenizer st = new StringTokenizer(target);
			try
			{
				String curpass = null, newpass = null, repeatnewpass = null;
				
				if (st.hasMoreTokens())
					curpass = st.nextToken();
				if (st.hasMoreTokens())
					newpass = st.nextToken();
				if (st.hasMoreTokens())
					repeatnewpass = st.nextToken();
				
				if (!(curpass == null || newpass == null || repeatnewpass == null))
				{
					if (!newpass.equals(repeatnewpass))
					{
						activeChar.sendMessage("The new password doesn't match with the repeated one!");
						return false;
					}
					if (newpass.length() < 4)
					{
						activeChar.sendMessage("The new password is shorter than 4 characters! Please try with a longer one.");
						return false;
					}
					if (newpass.length() > 25)
					{
						activeChar.sendMessage("The new password is longer than 25 characters! Please try with a shorter one.");
						return false;
					}
					
					MessageDigest md = MessageDigest.getInstance("SHA");
					
					byte[] raw = curpass.getBytes("UTF-8");
					raw = md.digest(raw);
					String curpassEnc = Base64.getEncoder().encodeToString(raw);
					String pass = null;
					int passUpdated = 0;
					
					Connection con = null;
					con = L2DatabaseFactory.getInstance().getConnection();
					PreparedStatement statement = con.prepareStatement("SELECT password FROM accounts WHERE login=?");
					statement.setString(1, activeChar.getAccountName());
					ResultSet rset = statement.executeQuery();
					if (rset.next())
					{
						pass = rset.getString("password");
					}
					
					rset.close();
					statement.close();
					con.close();
					
					if (curpassEnc.equals(pass))
					{
						byte[] password = newpass.getBytes("UTF-8");
						password = md.digest(password);
						
						PreparedStatement ps = con.prepareStatement("UPDATE accounts SET password=? WHERE login=?");
						ps.setString(1, Base64.getEncoder().encodeToString(password));
						ps.setString(2, activeChar.getAccountName());
						passUpdated = ps.executeUpdate();
						ps.close();
						con.close();
						
						if (passUpdated > 0)
						{
							activeChar.sendMessage("You have successfully changed your password!");
							activeChar.sendPacket(new ExShowScreenMessage("You have successfully changed your password!#Enjoy :)", 6000));
						}
						else
						{
							activeChar.sendMessage("The password change was unsuccessful!");
							activeChar.sendPacket(new ExShowScreenMessage("The password change was unsuccessful!!#U_u)", 6000));
						}
					}
					else
					{
						activeChar.sendMessage("CurrentPass doesn't match with your current one.");
					}
				}
				else
				{
					activeChar.sendMessage("Invalid pass data! Format: .changepass CurrentPass NewPass NewPass");
					return false;
				}
			}
			catch (Exception e)
			{
				activeChar.sendMessage("A problem occured while changing password!");
			}
		}
		else
		{
			activeChar.sendMessage("To change your current password, you have to type the command in the following format(without the brackets []): [.changepass CurrentPass NewPass NewPass]. You should also know that the password is case sensitive.");
			activeChar.sendPacket(new ExShowScreenMessage("To change your current password, you have to type the command in the following format(without the brackets []):#[.changepass CurrentPass NewPass NewPass]. You should also know that the password is case sensitive.)", 7000));
			return false;
		}
		
		return true;
	}
	
	@Override
	public String[] getVoicedCommandList()
	{
		return _voicedCommands;
	}
	
}