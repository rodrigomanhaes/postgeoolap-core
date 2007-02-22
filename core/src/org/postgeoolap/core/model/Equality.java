package org.postgeoolap.core.model;

import java.io.Serializable;

public class Equality implements Serializable 
{
	private static final long serialVersionUID = 7920911116819728038L;

	private Attribute attribute;
	private Object value;
	
	private boolean completeDescription;
	
	public Equality()
	{
		completeDescription = false;
	}
	
	public Equality(Attribute attribute, Object value)
	{
		this.attribute = attribute;
		this.value = value;
	}

	public Attribute getAttribute() 
	{
		return attribute;
	}

	public void setAttribute(Attribute attribute) 
	{
		this.attribute = attribute;
	}

	public Object getValue() 
	{
		return value;
	}

	public void setValue(Object value) 
	{
		this.value = value;
	}
	
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		if (completeDescription)
			builder
				.append(attribute.getDimension())
				.append(".")
				.append(attribute)
				.append(" = ");
		return builder.append(value).toString();
	}

}
