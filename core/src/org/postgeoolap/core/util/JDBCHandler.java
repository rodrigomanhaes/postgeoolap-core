package org.postgeoolap.core.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class JDBCHandler 
{
	private static Log log = LogFactory.getLog(JDBCHandler.class);
	
	private Connection connection;
	
	public static JDBCHandler createInstance(Connection connection)
	{
		return new JDBCHandler(connection);
	}
	
	private JDBCHandler(Connection connection)
	{
		this.connection = connection;
	}
	
	public boolean submit(String sql)
	{
		return submit(sql, new Object[] {});
	}
	
	public boolean submit(String sql, Object... params) 
	{
		try
		{
			PreparedStatement statement = connection.prepareStatement(sql);
			for (int i = 0; i < params.length; i++)
				statement.setObject(i+1, params[i]);
			statement.execute();
			log.info("Submit command: " + sql);
			return true;
		}
		catch (SQLException e)
		{
			log.error("Error submiting command " + sql + "\n" + e.getMessage());
			throw new IllegalArgumentException(e);
		}
	}
	
	public ResultSet submitQuery(String sql)
	{
		return submitQuery(sql, new Object[] {});
	}
	
	public ResultSet submitQuery(String sql, Object... params)
	{
		try
		{
			PreparedStatement statement = connection.prepareStatement(sql);
			for (int i = 0; i < params.length; i++)
				statement.setObject(i+1, params[i]);
			ResultSet result = statement.executeQuery();
			log.info("Submit query: " + sql);
			return result;
		}
		catch (SQLException e)
		{
			log.error("Error submiting query " + sql + "\n" + e.getMessage());
			throw new IllegalArgumentException(e);
		}
	}
	
	public int askNumber(String sql, Object... params)
	{
		ResultSet result = submitQuery(sql, params);
		try
		{
			return result.next() ? result.getInt(1) : Integer.MIN_VALUE;
		}
		catch (SQLException e) 
		{
			log.error(e.getMessage());
			throw new IllegalArgumentException(e);
		}
	}
}