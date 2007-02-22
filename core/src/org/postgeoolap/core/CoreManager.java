package org.postgeoolap.core;

import java.awt.Toolkit;

import org.postgeoolap.core.model.Schema;

public class CoreManager 
{
	private static final CoreManager manager = new CoreManager();
	
	public static final int HORIZONTAL_RESOLUTION = 
		Toolkit.getDefaultToolkit().getScreenSize().width;
	public static final int VERTICAL_RESOLUTION = 
		Toolkit.getDefaultToolkit().getScreenSize().height;
	
	private CoreManager()
	{
	}
	
	public static CoreManager instance()
	{
		return manager;
	}
	
	private Schema activeSchema;
	
	public void setActiveSchema(Schema schema)
	{
		this.activeSchema = schema;
	}
	
	public Schema getActiveSchema()
	{
		return activeSchema;
	}
}