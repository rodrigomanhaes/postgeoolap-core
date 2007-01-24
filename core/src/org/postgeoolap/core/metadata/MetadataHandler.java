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
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.postgeoolap.core.i18n.Local;

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
	
	public boolean createTables() throws MetadataException
	{
		dropTables();
		
		Connection connection = MetadataConnection.connection();
		
		for (MetadataDDL ddl: dependencies)
		{
			try
			{
				Statement statement = connection.createStatement();
				log.info(MessageFormat.format(Local.getString("message.creating_table"),
					ddl.name()));
				statement.execute(ddl.createTable());
				log.info(MessageFormat.format(Local.getString("message.table_created"),
					ddl.name()));
			}
			catch (SQLException e)
			{
				log.error(e.getMessage(), e);
				throw new MetadataException(Local.getString("error.creating_table") , e);
			}
		}
		
		return true;		
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
				log.info(MessageFormat.format(Local.getString("message.table_dropped"), 
					ddl.name()));
			}
			catch (SQLException e)
			{
				String message = MessageFormat.format(
					Local.getString("error.cannot_drop_table"), ddl.name());
				log.error(message + ": " + e.getMessage());
			}
		}
		return true;
	}
	
	public boolean clearEmptyCubes() throws MetadataException
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
			log.info(Local.getString("message.empty_cubes_deleted"));
			return true;
		}
		catch (SQLException e)
		{
			String msg = Local.getString("error.deleting_empty_cubes");
			log.error(msg + ": " + e.getMessage());
			throw new MetadataException(msg, e);
		}
	}
	
	public boolean clearCubes() throws MetadataException
	{
		Connection connection = MetadataConnection.connection();
		try
		{
			Statement statement = connection.createStatement();
			log.info(Local.getString("message.start_delete_cubes"));
			statement.execute("DELETE FROM aggregationitem");
			String deleted = Local.getString("message.clean");
			log.info(MessageFormat.format(deleted, "AGGREGATIONITEM"));
			statement.execute("DELETE FROM aggregation");
			log.info(MessageFormat.format(deleted, "AGGREGATION"));
			statement.execute("DELETE FROM attribute");
			log.info(MessageFormat.format(deleted, "ATTRIBUTE"));
			statement.execute("DELETE FROM dimension");
			log.info(MessageFormat.format(deleted, "DIMENSION"));
			statement.execute("DELETE FROM cube");
			log.info(MessageFormat.format(deleted, "CUBE"));
			return true;
		}
		catch (SQLException e)
		{
			String msg = Local.getString("error.deleting_cubes");
			log.error(msg + ": " + e.getMessage());
			throw new MetadataException(msg, e); 
		}
	}
	
	public void execute(String sql) throws MetadataException
	{
		Connection connection = MetadataConnection.connection();
		try
		{
			Statement statement = connection.createStatement();
			statement.execute(sql);
			log.info(Local.getString("message.submit_command"));
		}
		catch (SQLException e)
		{
			String msg = Local.getString("error.submit_command");
			log.error(msg, e);
			throw new MetadataException(msg, e);
		}		
	}
}