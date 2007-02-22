package org.postgeoolap.core.gui.auxiliary;

import java.util.List;

import javax.swing.AbstractListModel;

import org.postgeoolap.core.model.Cube;
import org.postgeoolap.core.model.Dimension;
import org.postgeoolap.core.model.Table;
import org.postgeoolap.core.util.Utils;

public class TableListModel extends AbstractListModel 
{
	private static final long serialVersionUID = -8043135129267179931L;
	
	private Cube cube;
	private List<Table> tables;
	
	public TableListModel(Cube cube)
	{
		super();
		this.cube = cube;
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
		tables = Utils.sortByStringRepresentation(cube.getSchema().getTables());
		
		for (int i = tables.size() - 1; i >= 0; i--)
		{
			Table table = tables.get(i);
			for (Dimension dimension: cube.getDimensions())
				if (dimension.getTableName().equals(table.getName()))
					tables.remove(table);
		}
	}
}
