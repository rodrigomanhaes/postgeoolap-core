 package org.postgeoolap.core.metadata;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.swing.JOptionPane;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.postgeoolap.core.i18n.Local;
import org.postgeoolap.core.util.Utils;

public class MetadataConnection 
{
	private static Connection connection;
	
	private static Log log = LogFactory.getLog(MetadataConnection.class);
	
	/**
	 * Note: does not throw an exception, freeing user classes to handle it,
	 * because only once (singleton pattern) there will be a chance of exception 
	 * 
	 * @return a connection to metadata database
	 */
	public static Connection connection()
	{
		if (connection == null)
		{
			try
			{
				Class.forName("org.apache.derby.jdbc.EmbeddedDriver"); 
				String directory = 
					Utils.getPostGeoOlapDirectory() + "metadata";
				connection = DriverManager.getConnection(
					"jdbc:derby:" + directory + ";create=true", "APP", "APP");
			}
			catch (ClassNotFoundException e)
			{
				log.error(e.getMessage(), e);
				JOptionPane.showMessageDialog(null, Local.getString("error.derby_driver_not_found"));
			}
			catch (SQLException e)
			{
				log.error(e.getMessage(), e);
				JOptionPane.showMessageDialog(null, Local.getString("error.cannot_connect_metadata"));
			} 
		}
		return connection;
	}

}