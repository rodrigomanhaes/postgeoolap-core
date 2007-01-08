package org.postgeoolap.core.gui.auxiliary;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import org.postgeoolap.core.util.SwingUtils;

@SuppressWarnings("serial")
public abstract class OkCancelDialog extends JDialog 
{
	protected JPanel panel;
	private JButton ok;
	private JButton cancel;
	
	private boolean okFlag;
	private boolean autoClose;
	
	public OkCancelDialog(String title)
	{
		super();
		
		this.autoClose = true;
		
		this.setTitle(title);
		
		this.panel = new JPanel();
		ok = new JButton("Ok");
		cancel = new JButton("Cancel");
		
		this.build();
		this.setModal(true);
		this.pack();
		SwingUtils.centralize(this);
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.setResizable(false);
		
		ActionListener closeAction =   
			new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					if (autoClose)
						OkCancelDialog.this.setVisible(false);
				}
			};
		
		ok.addActionListener(closeAction);
		cancel.addActionListener(closeAction);
		
		ok.addActionListener(
			new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					okAction(e);
					okFlag = true;
				}
			}
		);

		
		cancel.addActionListener(
			new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					cancelAction(e);
					okFlag = false;
				}
			}
		);
		
	}
	
	private void build()
	{
		this.setLayout(new BorderLayout());
		this.add(panel, BorderLayout.CENTER);
		
		JPanel bottomPanel = new JPanel();
		FlowLayout layout = new FlowLayout();
		layout.setHgap(50);
		layout.setAlignment(FlowLayout.CENTER);
		bottomPanel.setLayout(layout);
		bottomPanel.add(ok);
		bottomPanel.add(cancel);
		this.add(bottomPanel, BorderLayout.SOUTH);
	}
	
	public void addOkListener(ActionListener listener)
	{
		ok.addActionListener(listener);
	}
	
	public void addCancelListener(ActionListener listener)
	{
		cancel.addActionListener(listener);
	}
	
	public boolean isOk()
	{
		return okFlag;
	}
	
	public boolean isAutoClose() 
	{
		return autoClose;
	}

	public void setAutoClose(boolean autoClose) 
	{
		this.autoClose = autoClose;
	}
	
	public abstract void okAction(ActionEvent e);
	public abstract void cancelAction(ActionEvent e);
}
