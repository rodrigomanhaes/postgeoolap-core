package org.postgeoolap.log4j;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;

import junit.framework.TestCase;

import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.impl.Log4JLogger;
import org.apache.log4j.Appender;
import org.apache.log4j.Logger;
import org.postgeoolap.core.model.Cube;

public class SwingDocumentAppenderTester extends TestCase 
{
	public void testAppender() throws Exception
	{
		JDialog frame = new JDialog();
		JTextArea processingLog = new JTextArea(8, 50);
		JButton button = new JButton("Ok");
		
		frame.getContentPane().setLayout(new FlowLayout());
		frame.getContentPane().add(processingLog);
		frame.getContentPane().add(button);
		frame.pack();
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setModal(true);
		
		final Log4JLogger log4jLogger = (Log4JLogger) LogFactory.getLog(Cube.class);
		Logger logger = log4jLogger.getLogger();
		Appender documentAppender = new SwingDocumentAppender(processingLog.getDocument());
		logger.addAppender(documentAppender);
		
		button.addActionListener(
			new ActionListener()
			{
				public void actionPerformed(ActionEvent e) 
				{
					log4jLogger.info("Funciona!");
				}
			}
		);
		
		frame.setVisible(true);
	}
}
		