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
	
	public static final Icon tableIcon()
	{
		return new ImageIcon(
			ClassLoader.getSystemClassLoader().getResource(IMAGE_PACKAGE + "/table.gif"));
	}
	
	public static final Icon attributeIcon()
	{
		return new ImageIcon(
			ClassLoader.getSystemClassLoader().getResource(IMAGE_PACKAGE + "/attribute.gif"));
	}
	
	public static final Icon cubeIcon()
	{
		return new ImageIcon(
			ClassLoader.getSystemClassLoader().getResource(IMAGE_PACKAGE + "/cube.gif"));
	}
	
	public static final Icon processedCubeIcon()
	{
		return new ImageIcon(
			ClassLoader.getSystemClassLoader().getResource(IMAGE_PACKAGE + "/processed_cube.gif"));
	}
	
	public static final Icon dimensionIcon()
	{
		return new ImageIcon(
			ClassLoader.getSystemClassLoader().getResource(IMAGE_PACKAGE + "/dimension.gif"));
	}
	
	public static final Icon factIcon()
	{
		return new ImageIcon(
			ClassLoader.getSystemClassLoader().getResource(IMAGE_PACKAGE + "/fact.gif"));
	}
	
	public static final Icon nonAggregableIcon()
	{
		return new ImageIcon(
			ClassLoader.getSystemClassLoader().getResource(IMAGE_PACKAGE + "/non_aggregable.gif"));
	}
	
}
