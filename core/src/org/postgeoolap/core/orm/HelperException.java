package org.postgeoolap.core.orm;

public class HelperException extends Exception 
{
	private static final long serialVersionUID = -5804861176050009774L;

	public HelperException()
	{
		super();
	}
	
	public HelperException(String message, Throwable cause)
	{
		super(message, cause);
	}
	
	public HelperException(String message)
	{
		super(message);
	}
	
	public HelperException(Throwable cause)
	{
		super(cause);
	}
}
