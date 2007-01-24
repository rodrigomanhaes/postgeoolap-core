package org.postgeoolap.core.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.postgeoolap.core.i18n.Local;

public class JDBCHandler 
{
	private static Log log = LogFactory.getLog(JDBCHandler.class);
	
	private static Map<Connection, JDBCHandler> handlers = 
		new HashMap<Connection, JDBCHandler>();
	
	private Connection connection;
	
	public static JDBCHandler instance(Connection connection)
	{
		if (!handlers.containsKey(connection))
			handlers.put(connection, new JDBCHandler(connection));
		return handlers.get(connection);
	}
	
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
			log.info(Local.getString("message.submit_command") + ": " + sql);
			return true;
		}
		catch (SQLException e)
		{
			log.error(Local.getString("error.submit_command") + 
				" " + sql + "\n" + e.getMessage());
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
			log.info(Local.getString("message.submit_query") + ": " + sql);
			return result;
		}
		catch (SQLException e)
		{
			log.error(Local.getString("error.submit_query") + 
				" " + sql + "\n" + e.getMessage());
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