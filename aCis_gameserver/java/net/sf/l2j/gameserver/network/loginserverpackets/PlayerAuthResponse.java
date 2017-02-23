package net.sf.l2j.gameserver.network.loginserverpackets;

public class PlayerAuthResponse extends LoginServerBasePacket
{
	private final String _account;
	private final boolean _authed;
	
	public PlayerAuthResponse(byte[] decrypt)
	{
		super(decrypt);
		
		_account = readS();
		_authed = readC() != 0;
	}
	
	/**
	 * @return Returns the account.
	 */
	public String getAccount()
	{
		return _account;
	}
	
	/**
	 * @return Returns the authed state.
	 */
	public boolean isAuthed()
	{
		return _authed;
	}
}