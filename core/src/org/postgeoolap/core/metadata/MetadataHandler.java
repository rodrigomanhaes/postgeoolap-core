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
				log.info(ddl.name() + " table dropped");
			}
			catch (SQLException e)
			{
				log.error("Can't drop " + ddl.name() + ": " + e.getMessage());
			}
		}
		return true;
	}
}
