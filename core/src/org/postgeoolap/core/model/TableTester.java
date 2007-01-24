package org.postgeoolap.core.model;

import java.util.Set;

import junit.framework.Assert;
import junit.framework.TestCase;

public class TableTester extends TestCase 
{
	public void testGetTables() throws Exception
	{
		Schema schema = Schema.getAll().iterator().next();
		schema.connect();
		Set<Table> tables = Table.getTables(schema);
		Assert.assertNotNull(tables);
		for (Table table: tables)
			System.out.println(table);
	}
}
