package org.postgeoolap.core.metadata;

public class MetadataException extends Exception 
{
	private static final long serialVersionUID = 8603560740027962053L;

	public MetadataException()
	{
		super();
	}
	
	public MetadataException(String message, Throwable cause)
	{
		super(message, cause);
	}
	
	public MetadataException(String message)
	{
		super(message);
	}
	
	public MetadataException(Throwable cause)
	{
		super(cause);
	}
}
