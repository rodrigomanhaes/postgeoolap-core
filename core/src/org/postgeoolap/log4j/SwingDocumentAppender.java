package org.postgeoolap.log4j;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import org.apache.log4j.Appender;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;

public class SwingDocumentAppender extends AppenderSkeleton implements Appender 
{
	private Document document;
	
	public SwingDocumentAppender(Document document)
	{
		super();
		this.document = document;
	}
	
	@Override
	protected void append(LoggingEvent e) 
	{
		try
		{
			document.insertString(document.getLength(), e.getMessage().toString() + "\n", null);
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
