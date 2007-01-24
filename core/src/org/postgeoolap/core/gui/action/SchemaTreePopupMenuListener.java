package org.postgeoolap.core.gui.action;

import java.util.Map;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.tree.DefaultMutableTreeNode;

import org.postgeoolap.core.model.Cube;

public class SchemaTreePopupMenuListener implements PopupMenuListener 
{
	private JPopupMenu menu;
	private JTree tree;
	private Map<ActionManager, JMenuItem> map;
	
	public SchemaTreePopupMenuListener(JPopupMenu menu, JTree tree, 
		Map<ActionManager, JMenuItem> map)
	{
		super();
		this.menu = menu;
		this.tree = tree;
		this.map = map;
	}

	public void popupMenuWillBecomeVisible(PopupMenuEvent e) 
	{
		menu.removeAll();
		Object component = tree.getLastSelectedPathComponent();
		if (component == null)
			return;
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) component;
		Object object = node.getUserObject();
		if (object.getClass().equals(Cube.class))
		{
			Cube cube = (Cube) object;
			if (!cube.hasDimensions())
				menu.add(map.get(ActionManager.DEFINE_FACT_TABLE));
			else
			{
				menu.add(map.get(ActionManager.ADD_DIMENSION));
				if (cube.dimensionCount() > 1)
					menu.add(map.get(ActionManager.PROCESS_CUBE));
			}
			
		}
	}

	public void popupMenuWillBecomeInvisible(PopupMenuEvent e) 
	{
	}

	public void popupMenuCanceled(PopupMenuEvent e) 
	{
	}

}
