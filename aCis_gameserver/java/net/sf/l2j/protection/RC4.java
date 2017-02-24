package net.sf.l2j.protection;

/**
 * This is a simple implementation of the RC4 (tm) encryption algorithm.  The
 * author implemented this class for some simple applications
 * that don't need/want/require the Sun's JCE framework.
 * <p>
 * But if you are looking for encryption algorithms for a
 * full-blown application,
 * it would be better to stick with Sun's JCE framework.  You can find
 * a *free* JCE implementation with RC4 (tm) at
 * Cryptix (http://www.cryptix.org/).
 * <p>
 * Note that RC4 (tm) is a trademark of RSA Data Security, Inc.
 * Also, if you are within USA, you may need to acquire licenses from
 * RSA to use RC4.
 * Please check your local law.  The author is not
 * responsible for any illegal use of this code.
 * <p>
 * @author  Clarence Ho
 */
public class RC4
{
	private final byte state[] = new byte[256];
	private int x;
	private int y;
	private final byte[] _key;
	private boolean _block = false;
	
	/**
	 * Initializes the class with a string key. The length
	 * of a normal key should be between 1 and 2048 bits.  But
	 * this method doens't check the length at all.
	 *
	 * @param key   the encryption/decryption key
	 * @param block 
	 * @throws NullPointerException 
	 */
	public RC4(String key, boolean block) throws NullPointerException
	{
		this(key.getBytes(), block);
	}
	
	/**
	 * Initializes the class with a byte array key.  The length
	 * of a normal key should be between 1 and 2048 bits.  But
	 * this method doens't check the length at all.
	 *
	 * @param key   the encryption/decryption key
	 * @param block 
	 * @throws NullPointerException 
	 */
	public RC4(byte[] key, boolean block) throws NullPointerException
	{
		_key = key;
		_block = block;
		setKey();
	}
	
	private void setKey()
	{
		for (int i = 0; i < 256; i++)
		{
			state[i] = (byte) i;
		}
		
		x = 0;
		y = 0;
		
		int index1 = 0;
		int index2 = 0;
		
		byte tmp;
		
		if ((_key == null) || (_key.length == 0))
		{
			throw new NullPointerException();
		}
		for (int i = 0; i < 256; i++)
		{
			index2 = ((_key[index1] & 0xff) + (state[i] & 0xff) + index2) & 0xff;
			
			tmp = state[i];
			state[i] = state[index2];
			state[index2] = tmp;
			
			index1 = (index1 + 1) % _key.length;
		}
	}
	
	/** 
	 * RC4 encryption/decryption.
	 *
	 * @param data  the data to be encrypted/decrypted
	 * @return the result of the encryption/decryption
	 */
	public byte[] rc4(String data)
	{
		if ((data == null) || (data.length() == 0))
		{
			return null;
		}
		
		return rc4(data.getBytes());
	}
	
	/** 
	 * RC4 encryption/decryption.
	 *
	 * @param buf  the data to be encrypted/decrypted
	 * @return the result of the encryption/decryption
	 */
	public byte[] rc4(byte[] buf)
	{
		
		//int lx = this.x;
		//int ly = this.y;
		int xorIndex;
		byte tmp;
		
		if (buf == null)
		{
			return null;
		}
		
		byte[] result = new byte[buf.length];
		
		for (int i = 0; i < buf.length; i++)
		{
			x = (x + 1) & 0xff;
			y = ((state[x] & 0xff) + y) & 0xff;
			
			tmp = state[x];
			state[x] = state[y];
			state[y] = tmp;
			
			xorIndex = ((state[x] & 0xff) + (state[y] & 0xff)) & 0xff;
			result[i] = (byte) ((buf[i] ^ state[xorIndex]) & 0xff);
		}
		if (_block)
		{
			x = 0;
			y = 0;
		}
		else if ((x > 5000) || (y > 5000))
		{
			x = 0;
			y = 0;
		}
		
		return result;
	}
}