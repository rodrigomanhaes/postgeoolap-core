package org.postgeoolap.core.model.exception;

public class ModelException extends Exception 
{
	private static final long serialVersionUID = -3088269378007301898L;
	
	public ModelException()
	{
		super();
	}
	
	public ModelException(String message, Throwable cause)
	{
		super(message, cause);
	}
	
	public ModelException(String message)
	{
		super(message);
	}
	
	public ModelException(Throwable cause)
	{
		super(cause);
	}
}
