package org.postgeoolap.core.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.impl.Log4JLogger;
import org.apache.log4j.Appender;
import org.apache.log4j.Logger;
import org.goitaca.event.TextComponentDontType;
import org.goitaca.utils.SwingUtils;
import org.postgeoolap.core.gui.auxiliary.OkCancelDialog;
import org.postgeoolap.core.i18n.Local;
import org.postgeoolap.core.model.Cube;
import org.postgeoolap.core.model.exception.ModelException;
import org.postgeoolap.log4j.SwingDocumentAppender;
import org.postgeoolap.log4j.SwingDocumentSetter;

public class ProcessCubeDialog extends OkCancelDialog 
{
	private static final long serialVersionUID = 3534857060044443955L;
	
	private JTextField processing;
	@SuppressWarnings("unused")
	private JProgressBar progress;
	private JTextArea processingLog;
	
	private Cube cube;
	
	public ProcessCubeDialog(Cube cube) 
	{
		super(Local.getString("title.process_cube") + ":" + cube.getName());
		
		this.init();
		this.build();
		this.pack();
		SwingUtils.centralize(this);
		
		this.setAutoClose(false);
		this.cube = cube;
	}
	
	private void init()
	{
		processing = new JTextField();
		processing.addKeyListener(TextComponentDontType.instance());
		processing.setBackground(panel.getBackground());
		processing.setForeground(SystemColor.textText);
		
		processingLog = new JTextArea();
		processingLog.addKeyListener(TextComponentDontType.instance());
		processingLog.setFont(processing.getFont());
				
		progress = new JProgressBar();
	}
	
	private void build()
	{
		panel.setLayout(new GridBagLayout());
		
		SwingUtils.addGridBagLabelTextField(panel, 
			new JLabel(Local.getString("label.operation_at_execution")),
			processing, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL, 
			new Insets(3, 3, 3, 3));
		SwingUtils.addGridBagComponent(panel, 
			new JLabel(Local.getString("label.processing_log")), 
			0, 1, 2, 1, GridBagConstraints.WEST, GridBagConstraints.NONE);
		SwingUtils.addGridBagComponent(panel, 
			SwingUtils.scrollComponent(processingLog, 400, 500), 
			0, 2, 2, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL);
	}

	@Override
	public void okAction(ActionEvent e) 
	{
		/* 
		 * ATTENTION: this code is log4j-specific; the use of any other underlying 
		 * log framework will demand implementation of a Swing document appender
		 * or something equivalent on the chosen framework.
		 * 
		 * This code will cause a ClassCastException if the underlying
		 * log framework used doesn't be Apache Log4J
		 */
		Log4JLogger log4jLogger = (Log4JLogger) LogFactory.getLog(Cube.class);
		Logger logger = log4jLogger.getLogger();
		Appender documentAppender = new SwingDocumentAppender(processingLog.getDocument());
		Appender documentSetter = new SwingDocumentSetter(processing.getDocument());
		logger.addAppender(documentAppender);
		logger.addAppender(documentSetter);
		
		processing.getDocument().addDocumentListener(
			new DocumentListener()
			{
				public void insertUpdate(DocumentEvent e) 
				{
					pack();
				}

				public void removeUpdate(DocumentEvent e) 
				{
				}

				public void changedUpdate(DocumentEvent e) 
				{
				}
			}
		);
		try
		{
			cube.process();
		}
		catch(ModelException exception)
		{
			JOptionPane.showMessageDialog(null, exception.getMessage());
		}
		finally
		{
			logger.removeAppender(documentAppender);
			logger.removeAppender(documentSetter);
		}
		
		this.pack();
		this.setCancelText(Local.getString("command.close"));
		this.ok.setEnabled(false);
	}

	@Override
	public void cancelAction(ActionEvent e) 
	{
		this.setVisible(false);
	}

}
