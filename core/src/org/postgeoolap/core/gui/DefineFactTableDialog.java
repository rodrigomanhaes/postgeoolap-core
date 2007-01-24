package org.postgeoolap.core.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.text.MessageFormat;
import java.util.Set;

import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.goitaca.renderer.IconListCellRenderer;
import org.goitaca.utils.SwingUtils;
import org.postgeoolap.core.gui.auxiliary.OkCancelDialog;
import org.postgeoolap.core.i18n.Local;
import org.postgeoolap.core.model.AggregationType;
import org.postgeoolap.core.model.Attribute;
import org.postgeoolap.core.model.Cube;
import org.postgeoolap.core.model.Dimension;
import org.postgeoolap.core.model.DimensionType;
import org.postgeoolap.core.model.Field;
import org.postgeoolap.core.model.Table;
import org.postgeoolap.core.model.exception.ModelException;
import org.postgeoolap.core.resources.ResourceBox;
import org.postgeoolap.core.util.Utils;

@SuppressWarnings("serial")
public class DefineFactTableDialog extends OkCancelDialog 
{
	private JList tableList;
	private JList fieldList;
	private Cube cube;
	
	public DefineFactTableDialog(Cube cube) 
	{
		super(Local.getString("title.define_fact_table"));
		init();
		build();
		pack();
		this.setAutoClose(false);
		ok.setEnabled(false);
		this.cube = cube;
		this.loadTables();
		SwingUtils.centralize(this);
	}
	
	private void init()
	{
		tableList = new JList();
		tableList.setCellRenderer(new IconListCellRenderer(ResourceBox.tableIcon()));
		tableList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		tableList.addListSelectionListener(
			new ListSelectionListener()
			{
				public void valueChanged(ListSelectionEvent e) 
				{
					loadFields();
				}
			}
		);
		tableList.setModel(new DefaultListModel());
		
		fieldList = new JList();
		fieldList.setCellRenderer(new IconListCellRenderer(ResourceBox.attributeIcon()));
		fieldList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		fieldList.addListSelectionListener(
			new ListSelectionListener()
			{
				public void valueChanged(ListSelectionEvent e) 
				{
					ok.setEnabled(true);
				}
			}
		);
		fieldList.setModel(new DefaultListModel());
	}
	
	private void build()
	{
		panel.setLayout(new GridBagLayout());
		
		SwingUtils.addGridBagComponent(panel, new JLabel(Local.getString("label.tables")),
			0, 0, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE, 
			new Insets(3, 3, 2, 3));
		SwingUtils.addGridBagComponent(panel, new JLabel(Local.getString("label.fields")),
			1, 0, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE);
		SwingUtils.addGridBagComponent(panel, 
			SwingUtils.scrollComponent(tableList, 200, 150), 0, 1, 1, 1, 
			GridBagConstraints.CENTER, GridBagConstraints.NONE, 
			new Insets(0, 3, 3, 25));
		SwingUtils.addGridBagComponent(panel, 
			SwingUtils.scrollComponent(fieldList, 200, 150), 1, 1, 1, 1, 
			GridBagConstraints.CENTER, GridBagConstraints.NONE, 
			new Insets(0, 3, 3, 3));
	}

	@Override
	public void okAction(ActionEvent e) 
	{
		// build dimension
		Table table = (Table) tableList.getSelectedValue();
		Dimension fact = new Dimension();
		String name = (String) JOptionPane.showInputDialog(MessageFormat.format(
			Local.getString("label.enter_dimension_name"), table.getName()),
			table.getName());
		if (name == null)
		{
			this.setVisible(false);
			return;
		}
		fact.setName(name);
		fact.setType(DimensionType.FACT);
		fact.setTableId(table.getId());
		fact.setTableName(table.getName());
		
		// build attributes
		Object[] objects = fieldList.getSelectedValues();
		for (Object object: objects)
		{
			Field field = (Field) object;
			Attribute attribute = new Attribute();
			attribute.setAggregationType(AggregationType.SUM);
			attribute.setName(field.getName());
			attribute.setPhysicalName(field.getName());
			attribute.setSize(0);
			attribute.setStandard(false);
			attribute.setType(field.getType());
			fact.addAttribute(attribute);
		}
		
		OkCancelDialog dialog = new AttributeNameDialog(
			Utils.sortByStringRepresentation(fact.getAttributes()));
		dialog.setVisible(true);
		boolean ok = dialog.isOk();
		if (!ok)
		{
			this.setVisible(false);
			return;
		}
		
		cube.addDimension(fact);
		
		try
		{
			fact.persist();
		}
		catch (ModelException exception)
		{
			JOptionPane.showMessageDialog(null, exception.getMessage());
		}
		this.setVisible(false);
	}

	@Override
	public void cancelAction(ActionEvent e) 
	{
		this.setVisible(false);
	}
	
	private void loadTables()
	{
		Set<Table> tables = cube.getSchema().getTables();
		DefaultListModel model = (DefaultListModel) tableList.getModel();
		model.clear();
		
		for (Table table: Utils.sortByStringRepresentation(tables))
			model.addElement(table);
		((DefaultListModel) fieldList.getModel()).clear();
	}
	
	private void loadFields()
	{
		Table table = (Table) tableList.getSelectedValue();
		Set<Field> fields = table.getFields();
		DefaultListModel model = (DefaultListModel) fieldList.getModel();
		model.clear();
		for (Field field: Utils.sortByStringRepresentation(fields))
			model.addElement(field);
	}
}