package org.postgeoolap.core.metadata;

import java.sql.Connection;

import junit.framework.Assert;

import org.junit.Test;

public class MetadataTester
{
	@Test
	public void testConnection() throws Exception
	{
		Connection connection = MetadataConnection.connection();
		Assert.assertNotNull(connection);
	}
	
	@Test
	public void testCreateTables() throws Exception
	{
		Assert.assertTrue(MetadataHandler.instance().createTables());
	}
}
