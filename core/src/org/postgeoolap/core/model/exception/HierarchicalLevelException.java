package org.postgeoolap.core.model.exception;

public class HierarchicalLevelException extends Exception 
{
	private static final long serialVersionUID = 561776380605939541L;

	public HierarchicalLevelException()
	{
		super();
	}
	
	public HierarchicalLevelException(String message, Throwable cause)
	{
		super(message, cause);
	}
	
	public HierarchicalLevelException(String message)
	{
		super(message);
	}
	
	public HierarchicalLevelException(Throwable cause)
	{
		super(cause);
	}
}