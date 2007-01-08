package org.postgeoolap.core.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.Arrays;

import javax.swing.JOptionPane;
import javax.swing.JPasswordField;

import org.postgeoolap.core.gui.auxiliary.OkCancelDialog;
import org.postgeoolap.core.util.SwingUtils;

@SuppressWarnings("serial")
public class PasswordDialog extends OkCancelDialog 
{
	private JPasswordField password;
	private JPasswordField confirm;
	
	public PasswordDialog()
	{
		super("Enter password");
		this.init();
		this.build();
		this.setAutoClose(false);
		this.pack();
	}
	
	private void init()
	{
		password = new JPasswordField(12);
		confirm = new JPasswordField(12);
	}
	
	private void build()
	{
		panel.setLayout(new GridBagLayout());
		
		SwingUtils.addGridBagLabelTextField(panel, "Password", password, 
			0, 0, 1, 1, 1, GridBagConstraints.NONE, new Insets(3, 3, 3, 3));
		SwingUtils.addGridBagLabelTextField(panel, "Confirm", confirm, 
			0, 1, 1, 1, 1, GridBagConstraints.NONE);
	}
	
	public void okAction(ActionEvent e) 
	{
		if (!Arrays.equals(password.getPassword(), confirm.getPassword()))
		{
			JOptionPane.showMessageDialog(null, "Password does not match confirmation");
			confirm.grabFocus();
		}
		else
			this.setVisible(false);
	}

	public void cancelAction(ActionEvent e) 
	{
		
	}
	
	public char[] getPassword()
	{
		return password.getPassword();	
	}
}
