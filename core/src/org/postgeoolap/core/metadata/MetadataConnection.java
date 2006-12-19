 package org.postgeoolap.core.metadata;

import java.sql.Connection;
import java.sql.DriverManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.postgeoolap.core.util.PGOUtils;

public class MetadataConnection 
{
	private static Connection connection;
	
	private static Log log = LogFactory.getLog(MetadataConnection.class);
	
	public static Connection connection()
	{
		if (connection == null)
		{
			try
			{
				Class.forName("org.apache.derby.jdbc.EmbeddedDriver"); 
				String directory = 
					PGOUtils.getPostGeoOlapDirectory() + "metadata";
				connection = DriverManager.getConnection("jdbc:derby:" + directory + ";create=true", "APP", "APP");
			}
			catch (Exception e)
			{
				log.error(e.getMessage(), e);
			} 
		}
		return connection;
	}
		
}
