/* This program is free software; you can redistribute it and/or modify
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
package net.xcine.util.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Logger;

import net.xcine.Config;
import net.xcine.util.CloseUtil;

public class SqlUtils
{
	private static Logger _log = Logger.getLogger(SqlUtils.class.getName());

	// =========================================================
	// Data Field
	private static SqlUtils _instance;

	// =========================================================
	// Property - Public
	public static SqlUtils getInstance()
	{
		if(_instance == null)
		{
			_instance = new SqlUtils();
		}

		return _instance;
	}

	// =========================================================
	// Method - Public
	public static Integer getIntValue(String resultField, String tableName, String whereClause)
	{
		String query = "";
		Integer res = null;

		PreparedStatement statement = null;
		ResultSet rset = null;

		Connection con = null;
		try
		{
			query = L2DatabaseFactory.getInstance().prepQuerySelect(new String[]
			{
				resultField
			}, tableName, whereClause, true);

			
			con = L2DatabaseFactory.getInstance().getConnection(false);
			statement = con.prepareStatement(query);
			rset = statement.executeQuery();

			if(rset.next())
			{
				res = rset.getInt(1);
			}
		}
		catch(Exception e)
		{
			_log.warning("Error in query '" + query + "':" + e);
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if(rset!=null){
					rset.close();
					rset = null;
				}
				
				if(statement!=null){
					statement.close();
					statement = null;
				}
				
				
				query = null;
			}
			catch(Exception e)
			{
				if(Config.ENABLE_ALL_EXCEPTIONS)
					e.printStackTrace();
				
			}
			
			CloseUtil.close(con);
			con = null;
			
		}

		return res;
	}

	public static Integer[] getIntArray(String resultField, String tableName, String whereClause)
	{
		String query = "";
		Integer[] res = null;

		PreparedStatement statement = null;
		ResultSet rset = null;

		Connection con = null;
		try
		{
			query = L2DatabaseFactory.getInstance().prepQuerySelect(new String[]
			{
				resultField
			}, tableName, whereClause, false);
			con = L2DatabaseFactory.getInstance().getConnection(false);
			statement = con.prepareStatement(query);
			rset = statement.executeQuery();

			int rows = 0;

			while(rset.next())
			{
				rows++;
			}

			if(rows == 0){
				CloseUtil.close(con);
				con = null;
				return new Integer[0];
			}
			res = new Integer[rows - 1];

			rset.first();

			int row = 0;

			while(rset.next())
			{
				res[row] = rset.getInt(1);
			}
		}
		catch(Exception e)
		{
			_log.warning("mSGI: Error in query '" + query + "':" + e);
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if(rset!=null){
					rset.close();
					rset = null;
				}
				
				if(statement!=null){
					statement.close();
					statement = null;
				}
				
				query = null;
			}
			catch(Exception e)
			{
				if(Config.ENABLE_ALL_EXCEPTIONS)
					e.printStackTrace();
				
			}
			
			CloseUtil.close(con);
			con = null;
			
		}

		return res;
	}

	public static Integer[][] get2DIntArray(String[] resultFields, String usedTables, String whereClause)
	{
		long start = System.currentTimeMillis();

		String query = "";

		PreparedStatement statement = null;
		ResultSet rset = null;

		Integer res[][] = null;

		Connection con = null;
		try
		{
			query = L2DatabaseFactory.getInstance().prepQuerySelect(resultFields, usedTables, whereClause, false);
			con = L2DatabaseFactory.getInstance().getConnection(false);
			statement = con.prepareStatement(query);
			rset = statement.executeQuery();

			int rows = 0;

			while(rset.next())
			{
				rows++;
			}

			res = new Integer[rows - 1][resultFields.length];

			rset.first();

			int row = 0;

			while(rset.next())
			{
				for(int i = 0; i < resultFields.length; i++)
				{
					res[row][i] = rset.getInt(i + 1);
				}

				row++;
			}
		}
		catch(Exception e)
		{
			_log.warning("Error in query '" + query + "':" + e);
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if(rset!=null){
					rset.close();
					rset = null;
				}
				
				if(statement!=null){
					statement.close();
					statement = null;
				}
				
				query = null;
			}
			catch(Exception e)
			{
				if(Config.ENABLE_ALL_EXCEPTIONS)
					e.printStackTrace();
				
			}
			
			CloseUtil.close(con);
			con = null;
			
		}

		_log.fine("Get all rows in query '" + query + "' in " + (System.currentTimeMillis() - start) + "ms");

		return res;
	}
	
}
