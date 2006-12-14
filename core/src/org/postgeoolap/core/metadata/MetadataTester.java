package org.postgeoolap.core.metadata;

import java.sql.Connection;

import junit.framework.TestCase;

public class MetadataTester extends TestCase 
{
	public void testConnection() throws Exception
	{
		Connection connection = MetadataConnection.connection();
		connection.close();
	}
}
