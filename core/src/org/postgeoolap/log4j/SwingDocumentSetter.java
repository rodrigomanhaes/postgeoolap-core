package org.postgeoolap.log4j;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import org.apache.log4j.Appender;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;

public class SwingDocumentSetter extends AppenderSkeleton implements Appender 
{
	private Document document;
	
	public SwingDocumentSetter(Document document)
	{
		super();
		this.document = document;
	}
	
	@Override
	protected void append(LoggingEvent e) 
	{
		try
		{
			document.remove(0, document.getLength());
			document.insertString(0, e.getMessage().toString(), null);
		}
		catch (BadLocationException ble)
		{
			ble.printStackTrace(System.err);
		}
	}

	public void close() 
	{
		
	}

	public boolean requiresLayout() 
	{
		return false;
	}

}