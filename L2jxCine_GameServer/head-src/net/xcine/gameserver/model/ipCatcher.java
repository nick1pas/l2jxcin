package net.xcine.gameserver.model;
 
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.StringTokenizer;

import javolution.util.FastList;
import net.xcine.gameserver.model.actor.instance.L2PcInstance;
 
/**
 * @author AbsolutePower
 */
public class ipCatcher
{
	public static FastList<String> ips = new FastList<>();                                               
	static File file = new File("./config/banned_ip.cfg");  
	
	public String getMacAddr(L2PcInstance p)
	{
		StringBuilder sb = new StringBuilder();      
		byte[] mac = null;
		
		if(p != null)            
			try
		{      			
				@SuppressWarnings("static-access")
				final InetAddress ip = p.getClient().getConnection().getInetAddress().getLocalHost();
				NetworkInterface network = NetworkInterface.getByInetAddress(ip);      
				
				if(network !=null)
				{
					mac = network.getHardwareAddress();
				}
				
				if(mac!=null)          
				{
					for (int i = 0; i < mac.length; i++)
					{
						sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
					}
				}
				else
					sb.append("No mac found");
                 	
		}
		catch (SocketException e)
		{
			
			e.printStackTrace();
			
		}
		catch (UnknownHostException e)
		{
			e.printStackTrace();
		}
		
		return sb.toString();
	}
	
	public String getIp(L2PcInstance p)
	{
		return p.getClient().getConnection().getInetAddress().getHostAddress();
	}   
	
	//return true if catched :D
	public boolean isCatched(L2PcInstance p)
	{
		if(ips.contains(getMacAddr(p)) || ips.contains(getIp(p)))
			return true;
		
		return false;
	}
	
	//make textfile if not exist
	public static void MkTiNe()
	{
		if(file == null)
			file.mkdirs();
	}
           
	public void addIp(L2PcInstance p)
	{
		MkTiNe();
		final String ip = getIp(p);
		final String name = p.getName();
		
		if(ip != null && name != null)
			ips.add("Name");
		ips.add(name);
		ips.add("Ip");
		ips.add(ip);
		ips.add("Mac Addres");
		ips.add(getMacAddr(p));
		ips.add("-");
		
		FileWriter s = null;
		try
		{
			s = new FileWriter(file);
			for (int i = 0; i < ips.size(); i ++)
			{
				s.write(ips.get(i));
				s.write("\r\n");
			}
			s.flush();
			s.close();
			s = null;
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		ipsLoad();
	}
	public void removeIp(L2PcInstance p)
	{
		final String ip = getIp(p);
		final String name = p.getName();
		
		if(ip != null && name != null)
			ips.remove("Name");
		ips.remove(name);
		ips.remove("Ip");
		ips.remove(ip);
		ips.remove("Mac Addres");
		ips.remove(getMacAddr(p));
		ips.remove("-");
		
		FileWriter s = null;
			try
			{
				s = new FileWriter(file);
				for (int i = 0; i < ips.size(); i ++)
				{
					s.write(ips.get(i));
					s.write("\r\n");
				}
				s.flush();
				s.close();
				s = null;
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			ipsLoad();
        }	
	
	public static void ipsLoad()
	{
		MkTiNe();
		LineNumberReader l = null;
		try
		{
			String line = null;
			l = new LineNumberReader(new FileReader(file));
			while ( (line = l.readLine()) != null)
			{
				StringTokenizer st = new StringTokenizer(line,"\n\r");
				if (st.hasMoreTokens())
				{
					String n = st.nextToken();
					ips.add(n);
				}                                      
			}      
		}
		catch(Exception e){
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if(l != null)
					l.close();
			}
			catch (Exception e)
			{
			}
		}
	}
}		