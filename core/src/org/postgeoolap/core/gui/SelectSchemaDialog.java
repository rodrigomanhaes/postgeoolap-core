package org.postgeoolap.core.gui;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.goitaca.model.UpdatableListModel;
import org.goitaca.utils.SwingUtils;
import org.postgeoolap.core.CoreManager;
import org.postgeoolap.core.gui.auxiliary.SchemaCellRenderer;
import org.postgeoolap.core.i18n.Local;
import org.postgeoolap.core.model.Schema;
import org.postgeoolap.core.model.exception.ModelException;
import org.postgeoolap.core.util.Utils;

@SuppressWarnings("serial")
public class SelectSchemaDialog extends JDialog
{
	private JList list;
	private UpdatableListModel model;
	private JButton newButton;
	private JButton editButton;
	private JButton connectButton;
	private JButton deleteButton;
	
	private Schema schema;
	
	public SelectSchemaDialog()
	{
		super();
		this.setTitle(Local.getString("title.select_schema"));
		this.setModal(true);
		
		this.init();
		this.build();
		this.pack();
		this.setResizable(false);
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		
		SwingUtils.centralize(this);
	}
	
	private void init()
	{
		this.addWindowFocusListener(
			new WindowAdapter()
			{
				@Override
				public void windowGainedFocus(WindowEvent e)
				{
					dialogFocusGained();
				}
			}
		);
		
		model = new UpdatableListModel();
		list = new JList(model);
		list.addMouseListener(
			new MouseAdapter()
			{
				@Override
				public void mouseClicked(MouseEvent e)
				{
					if (e.getClickCount() == 2)
						connect();
				}
			}
		);
		
		list.addListSelectionListener(
			new ListSelectionListener()
			{
				public void valueChanged(ListSelectionEvent e)
				{
					listValueChanged();
				}
			}
		);
		
		list.setCellRenderer(new SchemaCellRenderer());
		
		newButton = new JButton(Local.getString("command.new"));
		newButton.addActionListener(
			new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					newSchema();
				}
			}
		);
		
		editButton = new JButton(Local.getString("command.edit"));
		editButton.addActionListener(
			new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					edit();
				}
			}
		);
		
		connectButton = new JButton(Local.getString("command.connect"));
		connectButton.addActionListener(
			new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					connect();
				}
			}
		);
		
		deleteButton = new JButton(Local.getString("command.delete"));
		deleteButton.addActionListener(
			new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					delete();
				}
			}
		);
	}
	
	private void build()
	{
		Container container = this.getContentPane();
		container.setLayout(new GridBagLayout());
		
		SwingUtils.addGridBagComponent(container, new JLabel(Local.getString("label.select_a_schema")), 
			0, 0, 2, 1, GridBagConstraints.WEST, GridBagConstraints.NONE, 
			new Insets(3, 3, 3, 3));
		SwingUtils.addGridBagComponent(container, 
			SwingUtils.scrollComponent(list, 200), 0, 1, 1, 3, 
			GridBagConstraints.WEST, GridBagConstraints.NONE);
		
		SwingUtils.addGridBagComponent(container, newButton, 1, 0, 1, 2, 
			GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL);
		SwingUtils.addGridBagComponent(container, editButton, 1, 2, 1, 1, 
			GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL);
		SwingUtils.addGridBagComponent(container, connectButton, 1, 3, 1, 1, 
			GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL);
		
		editButton.setEnabled(false);
		connectButton.setEnabled(false);
	}
	
	private void dialogFocusGained()
	{
		this.refresh();
	}
	
	private void listValueChanged()
	{
		if (list.getSelectedIndex() !=  -1)
		{
			schema = (Schema) list.getSelectedValue();
			this.editButton.setEnabled(true);
			this.connectButton.setEnabled(true);
		}
		else
		{
			schema = null;
			this.editButton.setEnabled(false);
			this.connectButton.setEnabled(false);
		}
	}
	
	private void connect()
	{
		try	
		{
			schema.connect();
			CoreManager.instance().setActiveSchema(schema);
			this.setVisible(false);
			this.dispose();
		}
		catch (ModelException e)
		{
			JOptionPane.showMessageDialog(null, e.getMessage());
		}
	}
	
	private void refresh()
	{
		try
		{
			model.clear();
			for (Schema schema: Utils.sortByStringRepresentation(Schema.getAll()))
				model.addElement(schema);
		}
		catch (ModelException e)
		{
			JOptionPane.showMessageDialog(null, e.getMessage());
		}
	}
	
	private void newSchema()
	{
		SchemaDialog schemaDialog = new SchemaDialog();
		schemaDialog.setVisible(true);
		if (schemaDialog.isOk())
			((DefaultListModel) list.getModel()).addElement(schemaDialog.getSchema());
	}
	
	private void edit()
	{
		if (list.getSelectedIndex() != -1)
		{
			SchemaDialog schemaDialog = new SchemaDialog(schema);
			schemaDialog.setVisible(true);
			if (schemaDialog.isOk())
				((UpdatableListModel) list.getModel()).update();
		}
	}
	
	private void delete()
	{
		if (schema != null)
		{
			try
			{
				schema.delete();
			}
			catch (ModelException e)
			{
				JOptionPane.showMessageDialog(null, e.getMessage());
			}
			refresh();
		}
	}
}