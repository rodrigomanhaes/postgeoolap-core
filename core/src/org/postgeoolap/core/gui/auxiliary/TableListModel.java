package org.postgeoolap.core.gui.auxiliary;

import java.util.List;

import javax.swing.AbstractListModel;

import org.postgeoolap.core.model.Schema;
import org.postgeoolap.core.model.Table;
import org.postgeoolap.core.util.Utils;

public class TableListModel extends AbstractListModel 
{
	private static final long serialVersionUID = -8043135129267179931L;
	
	private Schema schema;
	private List<Table> tables;
	
	public TableListModel(Schema schema)
	{
		super();
		this.schema = schema;
		this.refresh();
	}

	public int getSize()
	{
		return tables.size();
	}
	
	public Object getElementAt(int row)
	{
		return tables.get(row);
	}
	
	public void refresh()
	{
		tables = Utils.sortByStringRepresentation(schema.getTables());
	}
}
