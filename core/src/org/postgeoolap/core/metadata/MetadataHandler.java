package org.postgeoolap.core.metadata;

import static org.postgeoolap.core.metadata.MetadataDDL.AGGREGATION;
import static org.postgeoolap.core.metadata.MetadataDDL.AGGREGATIONITEM;
import static org.postgeoolap.core.metadata.MetadataDDL.ATTRIBUTE;
import static org.postgeoolap.core.metadata.MetadataDDL.CNSAGGREGATION;
import static org.postgeoolap.core.metadata.MetadataDDL.CUBE;
import static org.postgeoolap.core.metadata.MetadataDDL.DIMENSION;
import static org.postgeoolap.core.metadata.MetadataDDL.ESQUEMA;
import static org.postgeoolap.core.metadata.MetadataDDL.MAP;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MetadataHandler 
{
	private static Log log = LogFactory.getLog(MetadataHandler.class);
	private static MetadataHandler instance = new MetadataHandler();
	
	private static List<MetadataDDL> dependencies = new ArrayList<MetadataDDL>();
	
	static 
	{
		dependencies.add(ESQUEMA);
		dependencies.add(MAP);
		dependencies.add(CUBE);
		dependencies.add(DIMENSION);
		dependencies.add(ATTRIBUTE);
		dependencies.add(AGGREGATION);
		dependencies.add(AGGREGATIONITEM);
		dependencies.add(CNSAGGREGATION);
	}

	
	public static MetadataHandler instance()
	{
		return instance;
	}
	
	private MetadataHandler()
	{
	}
	
	public boolean createTables()
	{
		dropTables();
		
		Connection connection = MetadataConnection.connection();
		boolean result = true;
		
		for (MetadataDDL ddl: dependencies)
		{
			try
			{
				Statement statement = connection.createStatement();
				log.info("Creating " + ddl.name() + " table");
				statement.execute(ddl.createTable());
				log.info(ddl.name() + " table created");
			}
			catch (SQLException e)
			{
				log.error(e.getMessage(), e);
				result = false;
			}
		}
		return result;
	}
	
	public boolean dropTables()
	{
		Connection connection = MetadataConnection.connection();
		
		for (int i = dependencies.size() - 1; i >= 0; i--)
		{
			MetadataDDL ddl = dependencies.get(i);  			
			try
			{
				Statement statement = connection.createStatement();
				statement.execute(ddl.dropTable());
				log.info(ddl.name() + " table dropped");
			}
			catch (SQLException e)
			{
				log.error("Can't drop " + ddl.name() + ": " + e.getMessage());
			}
		}
		return true;
	}
	
	public void clearEmptyCubes()
	{
		Connection connection = MetadataConnection.connection();
		
		String sql = 
			"DELETE FROM cube " +
			"  WHERE cubecode NOT IN ( " +
			"    SELECT cubecode FROM dimension)";
		
		try
		{
			Statement statement = connection.createStatement();
			statement.execute(sql);
			log.info("Empty cubes removed");
		}
		catch (SQLException e)
		{
			log.error("Error on removing empty cubes: " + e.getMessage());
		}
	}
	
	public void clearCubes()
	{
		Connection connection = MetadataConnection.connection();
		try
		{
			Statement statement = connection.createStatement();
			log.info("Cleaning all cubes...");
			statement.execute("DELETE FROM aggregationitem");
			log.info("aggregationitem clean");
			statement.execute("DELETE FROM aggregation");
			log.info("aggregation clean");
			statement.execute("DELETE FROM attribute");
			log.info("attribute clean");
			statement.execute("DELETE FROM dimension");
			log.info("dimension clean");
			statement.execute("DELETE FROM cube");
			log.info("cube clean");
		}
		catch (SQLException e)
		{
			log.error("Error on removing cubes: " + e.getMessage());
		}
	}
	
	public boolean submit(String sql)
	{
		return submit(sql, new Object[] {});
	}
	
	public boolean submit(String sql, Object... params)
	{
		Connection connection = MetadataConnection.connection();
		
		try
		{
			PreparedStatement statement = connection.prepareStatement(sql);
			statement.execute(sql);
			for (int i = 0; i < params.length; i++)
				statement.setObject(i+1, params[i]);
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
		Connection connection = MetadataConnection.connection();
		
		try
		{
			PreparedStatement statement = connection.prepareStatement(sql);
			for (int i = 0; i < params.length; i++)
				statement.setObject(i+1, params[i]);
			ResultSet result = statement.executeQuery(sql);
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
			return result.getInt(0);
		}
		catch (Exception e) 
		{
			log.error(e.getMessage());
			throw new IllegalArgumentException(e);
		}
	}
}