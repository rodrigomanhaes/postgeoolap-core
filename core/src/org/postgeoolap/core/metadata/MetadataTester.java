package org.postgeoolap.core.metadata;

import java.sql.Connection;

import junit.framework.Assert;
import junit.framework.TestCase;

public class MetadataTester extends TestCase
{
	public void testConnection() throws Exception
	{
		Connection connection = MetadataConnection.connection();
		Assert.assertNotNull(connection);
	}
	
	public void testCreateTables() throws Exception
	{
		Assert.assertTrue(MetadataHandler.instance().createTables());
	}
	
	public void testClearCubes() throws Exception
	{
		Assert.assertTrue(MetadataHandler.instance().clearCubes());
	}
	
	public void testClearEmptyCubes() throws Exception
	{
		Assert.assertTrue(MetadataHandler.instance().clearEmptyCubes());
	}
}
