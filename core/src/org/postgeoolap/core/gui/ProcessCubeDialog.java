package org.postgeoolap.core.gui;

import goitaca.event.TextComponentDontType;
import goitaca.utils.SwingUtils;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.impl.Log4JLogger;
import org.apache.log4j.Appender;
import org.apache.log4j.Logger;
import org.postgeoolap.core.gui.auxiliary.OkCancelDialog;
import org.postgeoolap.core.i18n.Local;
import org.postgeoolap.core.model.Cube;
import org.postgeoolap.core.model.exception.ModelException;
import org.postgeoolap.log4j.SwingDocumentAppender;

public class ProcessCubeDialog extends OkCancelDialog 
{
	private static final long serialVersionUID = 3534857060044443955L;
	
	@SuppressWarnings("unused")
	private JProgressBar progress;
	private JTextArea processingLog;
	private JScrollPane scroll;
	
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
		processingLog = new JTextArea();
		processingLog.addKeyListener(TextComponentDontType.instance());
		processingLog.setFont(new JLabel().getFont());
		processingLog.setLineWrap(true);
		processingLog.setWrapStyleWord(true);
		progress = new JProgressBar();
	}
	
	private void build()
	{
		panel.setLayout(new GridBagLayout());
		
		SwingUtils.addGridBagComponent(panel, 
			new JLabel(Local.getString("label.processing_log")), 
			0, 0, 2, 1, GridBagConstraints.WEST, GridBagConstraints.NONE);
		scroll = SwingUtils.scrollComponent(processingLog, 400, 500);
		SwingUtils.addGridBagComponent(panel, scroll,
			0, 1, 2, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL);
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
		final Logger logger = log4jLogger.getLogger();
		final Appender documentAppender = new SwingDocumentAppender(processingLog.getDocument());
		logger.addAppender(documentAppender);
		
		processingLog.getDocument().addDocumentListener(
			new DocumentListener()
			{
				public void insertUpdate(DocumentEvent e) 
				{
					processingLog.scrollRectToVisible(
						new Rectangle(new Point(0, Integer.MAX_VALUE)));
					processingLog.update(processingLog.getGraphics());
				}

				public void removeUpdate(DocumentEvent e) 
				{
				}

				public void changedUpdate(DocumentEvent e) 
				{
				}
			}
		);
		
			new Thread()
			{
				@Override
				public void run()
				{
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
					}
				}
			}.run();
		
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
