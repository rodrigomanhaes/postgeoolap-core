package org.postgeoolap.core.gui.auxiliary;

import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.postgeoolap.core.i18n.Local;
import org.postgeoolap.core.model.Attribute;

public class HierarchyTableModel extends AbstractTableModel 
{
	private static final long serialVersionUID = 5449189109860671654L;
	
	private List<Attribute> attributes;
	
	public HierarchyTableModel(List<Attribute> attributes)
	{
		this.attributes = attributes;
	}

	public int getRowCount() 
	{
		return attributes.size();
	}

	public int getColumnCount() 
	{
		return 3;
	}

	public Object getValueAt(int rowIndex, int columnIndex) 
	{
		switch (columnIndex)
		{
			case 0:
				return attributes.get(rowIndex).getName();
			case 1:
				return attributes.get(rowIndex).getLevel();
			case 2:
				return attributes.get(rowIndex).isStandard();
			default:
				return null;
		}
	}
	
	@Override
	public void setValueAt(Object value, int rowIndex, int columnIndex)
	{
		switch (columnIndex)
		{
			case 0:
				attributes.get(rowIndex).setName(value.toString());
				break;
			case 1:
				attributes.get(rowIndex).setLevel((Integer) value);
				break;
			case 2:
				attributes.get(rowIndex).setStandard((Boolean) value);
		}
	}
	
	@Override
	public Class<?> getColumnClass(int columnIndex)
	{
		switch (columnIndex)
		{
			case 0:
				return String.class;
			case 1:
				return Integer.class;
			case 2:
				return Boolean.class;
			default:
				return null;
		}
	}
	
	@Override
	public String getColumnName(int columnIndex)
	{
		switch (columnIndex)
		{
			case 0:
				return Local.getString("header.name");
			case 1:
				return Local.getString("header.hierarchical_level");
			case 2:
				return Local.getString("header.standard");
			default:
				return null;
		}
	}
	
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex)
	{
		return columnIndex != 0;
	}
	
}