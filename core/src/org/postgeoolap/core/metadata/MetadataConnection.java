package org.postgeoolap.core.metadata;

import java.sql.Connection;
import java.sql.DriverManager;

import org.postgeoolap.core.util.PGOUtils;

public class MetadataConnection 
{
	private static Connection connection;
	
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
				System.out.println(e.getClass().getName() + ": " + e.getMessage()); 
			}
		}
		return connection;
	}
		
}
