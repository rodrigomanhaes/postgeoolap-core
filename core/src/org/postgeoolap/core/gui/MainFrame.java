package org.postgeoolap.core.gui;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.WindowConstants;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.goitaca.action.CommandAction;
import org.goitaca.action.CommandActionAdapter;
import org.goitaca.action.MenuBarTool;
import org.goitaca.utils.SwingUtils;
import org.postgeoolap.core.CoreManager;
import org.postgeoolap.core.gui.action.ActionManager;
import org.postgeoolap.core.gui.action.SchemaTreePopupMenuListener;
import org.postgeoolap.core.gui.auxiliary.OkCancelDialog;
import org.postgeoolap.core.gui.auxiliary.SchemaTreeCellRenderer;
import org.postgeoolap.core.gui.auxiliary.SchemaTreeRootNode;
import org.postgeoolap.core.i18n.Local;
import org.postgeoolap.core.metadata.MetadataException;
import org.postgeoolap.core.metadata.MetadataHandler;
import org.postgeoolap.core.model.Cube;
import org.postgeoolap.core.model.Schema;

public class MainFrame extends JFrame 
{
	private static final long serialVersionUID = 2394898615429334095L;
	
	private JMenuBar mainMenu;
	@SuppressWarnings("unused")
	private JMenuItem selectSchema;
	private JMenuItem createCube;
	@SuppressWarnings("unused")
	private JMenuItem createMetadata;
	private Object chosen;
	
	private JTree schemaTree;
	
	private static final String appName = "PostGeoOlap 1.0";
	
	public MainFrame()
	{
		super();
		this.setTitle("PostGeoOlap 1.0");
		this.init();
		this.build();
		
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		this.pack();
		this.setResizable(false);
		SwingUtils.centralize(this);
	}
	
	@SuppressWarnings("serial")
	public void init()
	{
		mainMenu = new JMenuBar();
		
		JMenu menu =
			MenuBarTool.addMenu(
				new CommandActionAdapter(Local.getString("command.schema"), null,  null, 
					Local.getString("shortcut.schema"), null),
					new Object[]
		            {
						new CommandAction(Local.getString("command.select"), null, null, 
							Local.getString("shortcut.schema|select"), 
							Local.getString("tip.select_schema"))
						{
							public void actionPerformed(ActionEvent e)
							{
								selectSchema();
							}
						},
						new CommandAction(Local.getString("command.create_cube"), null, null, 
							Local.getString("shortcut.schema|create_cube"), 
							Local.getString("tip.create_cube"))
						{
							public void actionPerformed(ActionEvent e)
							{
								createCube();
							}
						}
		           }
			);
		
		selectSchema = menu.getItem(0);
		createCube = menu.getItem(1);
		createCube.setEnabled(false);
		
		mainMenu.add(menu);
		
		menu = 
			MenuBarTool.addMenu(
				new CommandActionAdapter(Local.getString("command.metadata"), null,  null, 
					Local.getString("shortcut.metadata"), null),
					new Object[]
		           {
						new CommandAction(Local.getString("command.create"), null, null, 
							Local.getString("shortcut.metadata|create"), 
							Local.getString("tip.create_metadata"))
						{
							public void actionPerformed(ActionEvent e)
							{
								createMetadata();
							}
						},
						new CommandAction(Local.getString("command.delete_cubes"), null, null, 
							Local.getString("shortcut.metadata|delete_cubes"), 
							Local.getString("tip.delete_cubes"))
						{
							public void actionPerformed(ActionEvent e)
							{
								createMetadata();
							}
						}
		           }
			);
		
		createMetadata = menu.getItem(0);
		mainMenu.add(menu);
		
		Map<ActionManager, JMenuItem> map = new HashMap<ActionManager, JMenuItem>();
		JMenuItem defineFactTable = new JMenuItem(Local.getString("command.define_fact_table"));		
		defineFactTable.addActionListener(
			new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					defineFactTable();
				}
			}
		);
		map.put(ActionManager.DEFINE_FACT_TABLE, defineFactTable);
		
		JMenuItem addDimension = new JMenuItem(Local.getString("command.add_dimension"));		
		addDimension.addActionListener(
			new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					addDimension();
				}
			}
		);
		map.put(ActionManager.ADD_DIMENSION, addDimension);
		
		JMenuItem processCube = new JMenuItem(Local.getString("command.process_cube"));		
		processCube.addActionListener(
			new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					processCube();
				}
			}
		);
		map.put(ActionManager.PROCESS_CUBE, processCube);
		
		final JPopupMenu popup = new JPopupMenu();
		
		schemaTree = new JTree(new Object[] {});
		schemaTree.setCellRenderer(new SchemaTreeCellRenderer());
		schemaTree.addMouseListener(
			new MouseAdapter()
			{
				@Override
				public void mouseClicked(MouseEvent e) 
				{
					schemaTree.setSelectionRow(schemaTree.getRowForLocation(e.getX(), e.getY()));
					if (e.getButton() == MouseEvent.BUTTON3)
						popup.show(schemaTree, e.getX(), e.getY());
					chosen = schemaTree.getLastSelectedPathComponent() != null ?
						((DefaultMutableTreeNode) schemaTree.getLastSelectedPathComponent()).getUserObject() :
						null;
				}
			}
		);
		
		popup.addPopupMenuListener(new SchemaTreePopupMenuListener(popup, schemaTree, map));
	}
	
	public void build()
	{
		this.setJMenuBar(mainMenu);
		
		Container container = this.getContentPane();
		container.setLayout(new GridBagLayout());
		
		SwingUtils.addGridBagComponent(container, 
			SwingUtils.scrollComponent(schemaTree, 300, 550), 0, 0, 1, 1,
			GridBagConstraints.CENTER, GridBagConstraints.BOTH, 
			new Insets(3, 3, 3, 3));
	}
	
	private void refresh()
	{
		((DefaultTreeModel) schemaTree.getModel()).setRoot(null);
		((DefaultTreeModel) schemaTree.getModel()).setRoot(
			new SchemaTreeRootNode(CoreManager.instance().getActiveSchema()));
		schemaTree.repaint();
	}
	
	private void selectSchema()
	{
		SelectSchemaDialog dialog = new SelectSchemaDialog();
		dialog.setVisible(true);
		if (CoreManager.instance().getActiveSchema() != null)
		{
			Schema schema = CoreManager.instance().getActiveSchema();
			this.setTitle(appName + " - [" + schema.getName() + "]");
			createCube.setEnabled(true);
			((DefaultTreeModel) schemaTree.getModel()).setRoot(
				new SchemaTreeRootNode(schema));
			schemaTree.setRootVisible(true);
		}
		else
		{
			createCube.setEnabled(false);
			schemaTree.setRootVisible(false);
		}
	}
	
	private void createCube()
	{
		CreateCubeDialog dialog = new CreateCubeDialog(CoreManager.instance().getActiveSchema());
		dialog.setVisible(true);
		if (dialog.isOk())
			this.refresh();
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
	
	private void defineFactTable()
	{
		DefineFactTableDialog dialog = new DefineFactTableDialog((Cube) chosen);
		dialog.setVisible(true);
		if (dialog.isOk())
			this.refresh();
	}
	
	private void addDimension()
	{
		OkCancelDialog dialog = new SelectDimensionDialog((Cube) chosen);
		dialog.setVisible(true);
		if (dialog.isOk())
			this.refresh();
	}
	
	private void processCube()
	{
		OkCancelDialog dialog = new ProcessCubeDialog((Cube) chosen);
		dialog.setVisible(true);
		if (dialog.isOk())
			this.refresh();
	}
}
