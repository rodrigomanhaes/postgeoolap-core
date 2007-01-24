package org.postgeoolap.core;

import org.postgeoolap.core.model.Schema;

public class CoreManager 
{
	private static final CoreManager manager = new CoreManager();
	
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