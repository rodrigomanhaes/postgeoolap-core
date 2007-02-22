package org.postgeoolap.core.model.test;

import java.util.Set;

import junit.framework.TestCase;

import org.postgeoolap.core.model.Schema;

public class SchemaTester extends TestCase 
{
/*	public void testSave() throws Exception
	{
		Schema schema = new Schema();
		schema.setName("PMCI");
		schema.setServer("localhost");
		schema.setUser("postgres");
		schema.setPassword("postgres");
		schema.persist();
		
		Assert.assertTrue(schema.getId() != -1);
		System.out.println(schema.getId());
	}
*/	
	public void testGetAll() throws Exception
	{
		Set<Schema> set = Schema.getAll();
		
		System.out.println("Id\tNome\tServidor\tUsuário\tSenha\n");
		for (Schema schema: set)
		{
			System.out.printf("%s\t%s\t%s\t%s\t%s\n", schema.getId(), 
				schema.getName(), schema.getServer(), schema.getUser(),
				schema.getPassword());
		}
	}
	
	public void testDelete() throws Exception
	{
		Set<Schema> set = Schema.getAll();
		Schema schema = set.iterator().next();
		schema.delete();
	}
}
