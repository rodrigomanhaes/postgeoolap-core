package org.postgeoolap.core.gui.auxiliary;

import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.postgeoolap.core.i18n.Local;
import org.postgeoolap.core.model.Attribute;

public class AttributeNameTableModel extends AbstractTableModel
{
	private static final long serialVersionUID = 7888280596710465627L;
	
	private List<Attribute> attributes;
	
	public AttributeNameTableModel(List<Attribute> attributes)
	{
		this.attributes = attributes;
	}

	public int getRowCount() 
	{
		return attributes.size();
	}

	public int getColumnCount() 
	{
		return 2;
	}

	public Object getValueAt(int rowIndex, int columnIndex) 
	{
		return columnIndex == 0 ? 
			attributes.get(rowIndex).getPhysicalName() :
			attributes.get(rowIndex).getName();
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex)
	{
		if (columnIndex == 1)
			attributes.get(rowIndex).setName(aValue.toString());
	}
	
	@Override
	public String getColumnName(int columnIndex)
	{
		return columnIndex == 0 ? 
			Local.getString("header.field_name")  :
			Local.getString("header.attribute_name");	
	}
	
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex)
	{
		return columnIndex == 1;
	}
}