package org.postgeoolap.core.gui.auxiliary;

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import org.postgeoolap.core.resources.ResourceBox;

public class SchemaCellRenderer extends JLabel implements ListCellRenderer 
{
	private static final long serialVersionUID = -8345075589218334393L;
	
	private static final Icon icon = ResourceBox.schemaIcon(); 
	
	public Component getListCellRendererComponent(JList list, Object value, 
		int index, boolean selected, boolean focus) 
	{
		this.setText(value.toString());
		this.setIcon(icon);
		this.setForeground(selected ? list.getBackground() : list.getForeground());
		this.setBackground(selected ? list.getForeground() : list.getBackground());
		this.setEnabled(list.isEnabled());
		this.setFont(list.getFont());
		this.setOpaque(true);
		return this;
	}
}
