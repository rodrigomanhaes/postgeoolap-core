package org.postgeoolap.core.util;

import junit.framework.Assert;
import junit.framework.TestCase;

public class UtilsTester extends TestCase 
{
	public void testPad() throws Exception
	{
		Assert.assertEquals(Utils.pad("teste", '0', 10, -1), "00000teste");
		Assert.assertEquals(Utils.pad("teste", '0', 10, 0), "00teste000");
		Assert.assertEquals(Utils.pad("teste", '0', 10, 1), "teste00000");
	}
}
