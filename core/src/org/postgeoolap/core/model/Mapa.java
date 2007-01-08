package org.postgeoolap.core.model;

import java.io.Serializable;

public class Mapa implements Serializable
{
	private static final long serialVersionUID = -2501911010293573430L;
	
	private long id;
	private String name;
	private int srid;
	private Schema schema;
	
	public Mapa()
	{
		
	}

	public String getName() 
	{
		return name;
	}

	public void setName(String name) 
	{
		this.name = name;
	}

	public Schema getSchema() 
	{
		return schema;
	}

	public void setSchema(Schema schema) 
	{
		this.schema = schema;
	}

	public int getSrid() 
	{
		return srid;
	}

	public void setSrid(int srid) 
	{
		this.srid = srid;
	}
	
	public long getId()
	{
		return id;
	}
	
	public void setId(long id)
	{
		this.id = id;
	}
	
}