package org.postgeoolap.core.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextField;

import org.goitaca.event.TextComponentDontType;
import org.goitaca.renderer.FixedTableCellRenderer;
import org.goitaca.utils.SwingUtils;
import org.postgeoolap.core.gui.auxiliary.AttributeNameTableModel;
import org.postgeoolap.core.gui.auxiliary.OkCancelDialog;
import org.postgeoolap.core.i18n.Local;
import org.postgeoolap.core.model.Attribute;

public class AttributeNameDialog extends OkCancelDialog 
{
	private static final long serialVersionUID = 1497782346805600262L;
	
	private JTextField dimension;
	private JTable table;
	private List<Attribute> attributes;
	
	public AttributeNameDialog(List<Attribute> attributes)
	{
		super(Local.getString("title.attribute_names"));
		this.attributes = attributes;
		
		this.init();
		this.build();
		this.pack();
		SwingUtils.centralize(this);
	}
	
	private void init()
	{
		dimension = new JTextField(25);
		dimension.setBackground(SystemColor.control);
		dimension.addKeyListener(TextComponentDontType.instance());
		dimension.setFocusable(false);
		dimension.setText(attributes.get(0).getDimension().getName());
		
		table = new JTable();
		table.setModel(new AttributeNameTableModel(attributes));
		table.setDefaultRenderer(Object.class, new FixedTableCellRenderer(0));
	}
	
	private void build()
	{
		panel.setLayout(new GridBagLayout());
		
		SwingUtils.addGridBagLabelTextField(panel, new JLabel(Local.getString("label.dimension")),
			dimension, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL, new Insets(3, 3, 3, 3));
		
		SwingUtils.addGridBagComponent(panel, SwingUtils.scrollComponent(table, 250, 150),
			0, 1, 2, 1, GridBagConstraints.CENTER, GridBagConstraints.NONE); 
	}
	
	@Override
	public void okAction(ActionEvent e) 
	{
	}

	@Override
	public void cancelAction(ActionEvent e) 
	{
	}
}
