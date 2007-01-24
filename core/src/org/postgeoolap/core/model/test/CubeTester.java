package org.postgeoolap.core.model.test;

import java.util.Set;

import org.postgeoolap.core.model.Cube;
import org.postgeoolap.core.model.Schema;

import junit.framework.TestCase;

public class CubeTester extends TestCase 
{
	public void testGetAll() throws Exception
	{
		Set<Schema> schemata = Schema.getAll();
		for (Schema schema: schemata)
		{
			Set<Cube> cubes = Cube.getAll(schema);
			System.out.println("\nSchema: " + schema.getName());
			for (Cube cube: cubes)
				System.out.printf("Cube: %s\nMinimum aggregation: %s\n", cube.getName(), cube.getMinimumAggregation());
			System.out.println();
		}
	}
}
