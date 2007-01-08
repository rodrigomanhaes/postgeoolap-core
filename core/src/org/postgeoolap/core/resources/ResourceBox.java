package org.postgeoolap.core.resources;

import javax.swing.Icon;
import javax.swing.ImageIcon;

public class ResourceBox 
{
	private static final String IMAGE_PACKAGE = "org/postgeoolap/core/resources/images";
	
	public static final Icon schemaIcon()
	{
		return new ImageIcon(
			ClassLoader.getSystemClassLoader().getResource(IMAGE_PACKAGE + "/schema.gif"));
	}
}
