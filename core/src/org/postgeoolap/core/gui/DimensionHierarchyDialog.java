package org.postgeoolap.core.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.text.MessageFormat;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextArea;

import org.goitaca.utils.SwingUtils;
import org.postgeoolap.core.gui.auxiliary.HierarchyTableModel;
import org.postgeoolap.core.gui.auxiliary.OkCancelDialog;
import org.postgeoolap.core.i18n.Local;
import org.postgeoolap.core.model.Dimension;
import org.postgeoolap.core.model.exception.HierarchicalLevelException;
import org.postgeoolap.core.util.Utils;

public class DimensionHierarchyDialog extends OkCancelDialog
{
	private static final long serialVersionUID = 8150181391924180362L;
	
	private Dimension dimension;
	private JTable table;
	
	public DimensionHierarchyDialog(Dimension dimension)
	{
		super(MessageFormat.format(Local.getString(
			"title.define_hierarchy"), dimension.toString()));
		this.dimension = dimension;
		
		this.setAutoClose(false);
		
		this.init();
		this.build();
		this.pack();
		
		SwingUtils.centralize(this);
	}
	
	private void init()
	{
		table = new JTable();
		table.setModel(new HierarchyTableModel(
			Utils.sortByStringRepresentation(dimension.getAttributes())));
	}
	
	private void build()
	{
		panel.setLayout(new GridBagLayout());
		
		JTextArea area = new JTextArea(Local.getString("text.dimension_hierarchy"), 4, 50);
		area.setFont(new JLabel().getFont());
		area.setBackground(panel.getBackground());
		area.setBorder(BorderFactory.createLineBorder(panel.getBackground()));
		area.setWrapStyleWord(true);
		area.setLineWrap(true);
		SwingUtils.addGridBagComponent(panel, area,
			0, 0, 1, 1, GridBagConstraints.CENTER, 
			GridBagConstraints.HORIZONTAL, new Insets(3, 3, 3, 3));
		
		SwingUtils.addGridBagComponent(panel, 
			SwingUtils.scrollComponent(table, 180),
			0, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL);
		
	}
	
	@Override
	public void okAction(ActionEvent e) 
	{
		try
		{
			dimension.validateLevels();
			this.setVisible(false);
		}
		catch (HierarchicalLevelException exception)
		{
			JOptionPane.showMessageDialog(null, exception.getMessage());
		}
	}

	@Override
	public void cancelAction(ActionEvent e) 
	{
		this.setVisible(false);
	}
}
