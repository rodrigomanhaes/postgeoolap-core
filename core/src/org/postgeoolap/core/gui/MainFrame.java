package org.postgeoolap.core.gui;

import java.awt.event.ActionEvent;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;

import org.postgeoolap.core.gui.action.CommandAction;
import org.postgeoolap.core.gui.action.CommandActionAdapter;
import org.postgeoolap.core.gui.action.MenuBarTool;
import org.postgeoolap.core.metadata.MetadataException;
import org.postgeoolap.core.metadata.MetadataHandler;

@SuppressWarnings("serial")
public class MainFrame extends JFrame 
{
	private JMenuBar mainMenu;
	public MainFrame()
	{
		super();
		this.setTitle("PostGeoOlap 1.0");
		this.init();
		this.build();
		
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		this.pack();
	}
	
	public void init()
	{
		mainMenu = new JMenuBar();
		
		mainMenu.add(
			MenuBarTool.addMenu(
				new CommandActionAdapter("Schema", null,  null, "S", null),
					new Object[]
		           {
						new CommandAction("Select", null, null, "S", "Schema selection and manipulation")
						{
							public void actionPerformed(ActionEvent e)
							{
								selectSchema();
							}
						}
		           }
			)
		);
		
		mainMenu.add(
			MenuBarTool.addMenu(
				new CommandActionAdapter("Metadata", null,  null, "M", null),
					new Object[]
		           {
						new CommandAction("Create", null, null, "C", "Create metadata database")
						{
							public void actionPerformed(ActionEvent e)
							{
								createMetadata();
							}
						}
		           }
			)
		);
	}
	
	public void build()
	{
		this.setJMenuBar(mainMenu);
	}
	
	private void selectSchema()
	{
		JDialog dialog = new SchemaSelectionDialog();
		dialog.setVisible(true);
	}
	
	private void createMetadata()
	{
		try
		{
			MetadataHandler.instance().createTables();
		}
		catch (MetadataException e)
		{
			JOptionPane.showMessageDialog(null, e.getMessage());
		}
	}
}
