package org.postgeoolap.core.gui;

import goitaca.renderer.list.IconListCellRenderer;
import goitaca.utils.SwingUtils;

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

import org.postgeoolap.core.gui.auxiliary.OkCancelDialog;
import org.postgeoolap.core.gui.auxiliary.TableListModel;
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

public class SelectDimensionDialog extends OkCancelDialog 
{
	private static final long serialVersionUID = 6937881044404016046L;
	
	private JList tableList;
	private JList fieldList;
	private Cube cube;
	private boolean many;
	
	private DimensionType dimensionType;

	/**
	 * 
	 * @param cube
	 * @param dimensionDescription to be placed on window's title bar
	 * @param dimensionType 
	 * @param many true if many dimensions can be selected
	 */
	public SelectDimensionDialog(Cube cube, String dimensionDescription, 
		DimensionType dimensionType, boolean many) 
	{
		super(MessageFormat.format(
			Local.getString("title.select_something"), dimensionDescription));
		this.cube = cube;
		this.dimensionType = dimensionType;
		this.many = many;
		
		this.init();
		this.build();
		this.pack();
		SwingUtils.centralize(this);
	}
	
	private void init()
	{
		tableList = new JList();
		tableList.setModel(new TableListModel(cube));
		tableList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		tableList.addListSelectionListener(
			new ListSelectionListener()
			{
				public void valueChanged(ListSelectionEvent e) 
				{
					ok.setEnabled(true);
					loadFields();
				}
			}
		);
		
		ok.setEnabled(false);
		
		fieldList = new JList();
		fieldList.setModel(new DefaultListModel());
		fieldList.setCellRenderer(new IconListCellRenderer(ResourceBox.attributeIcon()));
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
	
	private void build()
	{
		panel.setLayout(new GridBagLayout());
		SwingUtils.addGridBagComponent(panel, new JLabel(Local.getString("label.select_dimensions")),
			0, 0, 2, 1, GridBagConstraints.WEST, GridBagConstraints.NONE, 
			new Insets(3, 3, 3, 3));
		SwingUtils.addGridBagComponent(panel, SwingUtils.scrollComponent(tableList, 200, 250), 
			0, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.NONE);
		
		SwingUtils.addGridBagComponent(panel, SwingUtils.scrollComponent(fieldList, 200, 250), 
			1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.NONE);
	}

	@Override
	public void okAction(ActionEvent e) 
	{
		for (Object object: tableList.getSelectedValues())
		{
			Table table = (Table) object;
			Dimension dimension = new Dimension();
			dimension.setType(this.dimensionType);
			dimension.setTableId(table.getId());
			dimension.setTableName(table.getName());
			
			try
			{
				cube.addDimension(dimension);
			}
			catch (ModelException exception)
			{
				JOptionPane.showMessageDialog(null, exception.getMessage(), 
					Local.getString("title.dimension"), JOptionPane.ERROR_MESSAGE);
				System.exit(0);
			}
			
			String name = JOptionPane.showInputDialog(MessageFormat.format(
				Local.getString("label.enter_dimension_name"), table.getName()),
				table.getName());
			if (name == null)
			{
				if (verifyContinue(table))
					continue;
				else
					break;
			}
			dimension.setName(name);
			
			// build attributes
			for (Field field: table.getFields())
			{
				Attribute attribute = new Attribute();
				attribute.setAggregationType(AggregationType.NON_AGGREGABLE);
				attribute.setName(field.getName());
				attribute.setPhysicalName(field.getName());
				attribute.setSize(0);
				attribute.setLevel(Cube.MOST_AGGREGATED_LEVEL);
				attribute.setType(field.getType());
				dimension.addAttribute(attribute);
			}
			
			OkCancelDialog dialog = new AttributeNameDialog(
				Utils.sortByStringRepresentation(dimension.getAttributes()));
			dialog.setVisible(true);
			if (!dialog.isOk())
			{
				if (many && verifyContinue(table))
					continue;
				else
					break;
			}
			
			if (!DimensionType.NON_AGGREGABLE.equals(dimensionType))
			{
				dialog = new DimensionHierarchyDialog(dimension);
				dialog.setVisible(true);
				if (!dialog.isOk())
				{
					if (many && verifyContinue(table))
						continue;
					else
						break;
				}
			}
			
			try
			{
				dimension.persist();
			}
			catch (ModelException exception)
			{
				JOptionPane.showMessageDialog(null, exception.getMessage(), 
					Local.getString("title.dimension"), JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	
	/**
	 * Asks user if cancel all or only current dimension. Created only for minimizing DRY
	 * (Don't Repeat Yourself).
	 * @return true if user cancels only current dimension; false if user cancels all
	 */
	private boolean verifyContinue(Table table)
	{
		if (table == tableList.getSelectedValues()[tableList.getSelectedValues().length-1])
			return false;
		
		return 
			SwingUtils.yesNoPane(
				this.getTitle(), 
				Local.getString("question.cancel_this_or_all_dimensions"),
				0, 
				Local.getString("command.only_this(female)"), 
				Local.getString("command.all(female)")
			) == JOptionPane.YES_OPTION;
	}

	@Override
	public void cancelAction(ActionEvent e) 
	{
	}
}
