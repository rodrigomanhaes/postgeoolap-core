package org.postgeoolap.core.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import org.postgeoolap.core.gui.auxiliary.MapFileFilter;
import org.postgeoolap.core.gui.auxiliary.MapTableModel;
import org.postgeoolap.core.gui.auxiliary.OkCancelDialog;
import org.postgeoolap.core.model.Mapa;
import org.postgeoolap.core.model.Schema;
import org.postgeoolap.core.model.exception.ModelException;
import org.postgeoolap.core.util.SwingUtils;

@SuppressWarnings("serial")
public class SchemaDialog extends OkCancelDialog 
{
	private JTextField name;
	private JTextField server;
	private JTextField user;
	private JTable maps;
	private JPopupMenu menu;
	private MouseListener listener;
	
	private boolean including;
	
	private Schema schema;
	
	public SchemaDialog(String title)
	{
		this(title, null);
	}
	
	public SchemaDialog(String title, Schema schema)
	{
		super(title);
		init();
		build();
		pack();
		
		including = schema == null;
		if (!including)
		{
			this.schema = schema;
			updateWidgets();
		}
	}
	
	private void init()
	{
		name = new JTextField(30);
		server = new JTextField(30);
		user = new JTextField(15);
		maps = new JTable(new MapTableModel(new Mapa[] {}));
		menu = new JPopupMenu();
		
		JMenuItem includeMap = new JMenuItem("Include map");
		includeMap.addActionListener(
			new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					includeMap();
				}
			}
		);
		
		final JMenuItem deleteMap = new JMenuItem("Delete map");
		deleteMap.addActionListener(
			new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					deleteMap();
				}
			}
		);
		
		menu.add(includeMap);
		menu.add(deleteMap);
		
		menu.addPopupMenuListener(
			new PopupMenuListener()
			{
				public void popupMenuWillBecomeVisible(PopupMenuEvent e) 
				{
					deleteMap.setEnabled(maps.getSelectedRowCount() > 0);
				}

				public void popupMenuWillBecomeInvisible(PopupMenuEvent e) 
				{
				}

				public void popupMenuCanceled(PopupMenuEvent e) 
				{
				}
			}
		);
		
		listener =
			new MouseAdapter()
			{
				@Override
				public void mouseClicked(MouseEvent e)
				{
					if (e.getButton() == MouseEvent.BUTTON3)
						menu.show(maps, e.getX(), e.getY());
				}
			};
		
		maps.addMouseListener(listener);
	}
	
	private void build()
	{
		panel.setLayout(new GridBagLayout());
		int y = -1;
		
		SwingUtils.addGridBagLabelTextField(panel, new JLabel("Name: "),name, 
			0, ++y, 1, 1, 1, GridBagConstraints.HORIZONTAL, new Insets(3, 3, 3, 3));
		SwingUtils.addGridBagLabelTextField(panel, new JLabel("Server: "), server, 
			0, ++y, 1, 1, 1, GridBagConstraints.HORIZONTAL);
		SwingUtils.addGridBagLabelTextField(panel, new JLabel("User: "), user, 
			0, ++y, 1, 1, 1, GridBagConstraints.NONE);
		SwingUtils.addGridBagComponent(panel, SwingUtils.scrollComponent(maps, 150),
			0, ++y, 2, 1, GridBagConstraints.CENTER, GridBagConstraints.NONE);
		
		int total = maps.getColumnModel().getColumn(0).getWidth() +  
			maps.getColumnModel().getColumn(1).getWidth();
		maps.getColumnModel().getColumn(0).setPreferredWidth(total * 9 / 10);
		maps.getColumnModel().getColumn(1).setPreferredWidth(total * 1 / 10);
		
		maps.getParent().addMouseListener(listener);
	}

	public void okAction(ActionEvent e) 
	{
		retrieveObject();
		
		PasswordDialog passwordDialog = new PasswordDialog();
		passwordDialog.setVisible(true);
		if (passwordDialog.isOk())
		{
			schema.setPassword(new String(passwordDialog.getPassword()));
			try
			{
				schema.persist();
			}
			catch (ModelException exception)
			{
				JOptionPane.showMessageDialog(null, exception.getMessage() + "\nOperation not committed");
			}
		}
	}

	public void cancelAction(ActionEvent e) 
	{
	}
	
	private void updateWidgets()
	{
		name.setText(schema.getName());
		server.setText(schema.getServer());
		user.setText(schema.getUser());
		((MapTableModel) maps.getModel()).addMaps(schema.getMaps().toArray(new Mapa[] {}));
	}
	
	private void retrieveObject()
	{
		if (schema == null)
			schema = new Schema();
		schema.setName(name.getText());
		schema.setServer(server.getText());
		schema.setUser(user.getText());
		schema.setMaps(((MapTableModel) maps.getModel()).getMaps());
	}
	
	public Schema getSchema()
	{
		return schema;
	}
	
	public void includeMap()
	{
		JFileChooser chooser = new JFileChooser();
		MapFileFilter filter = new MapFileFilter();
		filter.addExtension("gml");
		filter.addExtension("shp");
		chooser.setFileFilter(filter);
		chooser.setApproveButtonText("Ok");
		chooser.setApproveButtonMnemonic('O');
		if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
		{
			int srid = -1;
			do 
			{
				boolean exit = false;
				try
				{
					srid = Integer.parseInt(JOptionPane.showInputDialog(null, "Enter map SRID", "Maps", 
						JOptionPane.INFORMATION_MESSAGE));
				}
				catch (NumberFormatException e)
				{
					exit =  JOptionPane.showConfirmDialog(null, "SRID is an integer number. The value you " +
						"entered was not an integer. Do you want cancel operation?", "Maps", 
						JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
					if (exit)
						break;
					else
						continue;
				}
				Mapa map = new Mapa();
				map.setName(chooser.getSelectedFile().getAbsolutePath());
				map.setSrid(srid);
				((MapTableModel) maps.getModel()).addMap(map);
				break;
			}
			while (true);
		}
	}
	
	public void deleteMap()
	{
		if (maps.getSelectedRowCount() > 0)
			for (int i = maps.getSelectedRows().length - 1; i >= 0; i--)
				((MapTableModel) maps.getModel()).removeMap(maps.getSelectedRows()[i]);
	}
}