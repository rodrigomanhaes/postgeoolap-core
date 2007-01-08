package org.postgeoolap.core.gui.auxiliary;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.table.AbstractTableModel;

import org.postgeoolap.core.model.Mapa;

@SuppressWarnings("serial")
public class MapTableModel extends AbstractTableModel 
{
	private List<Mapa> maps;
	
	public MapTableModel(Mapa[] maps)
	{
		super();
		this.maps = new ArrayList<Mapa>(Arrays.asList(maps));
	}
	
	public void addMap(Mapa mapa)
	{
		maps.add(mapa);
		this.fireTableRowsInserted(maps.size() - 1, maps.size() - 1);
	}
	
	public void addMaps(Mapa[] maps)
	{
		this.maps.addAll(Arrays.asList(maps));
		this.fireTableRowsInserted(this.maps.size() - maps.length, this.maps.size() - 1);
	}
	
	public void removeMap(Mapa mapa)
	{
		maps.remove(mapa);
		this.fireTableRowsDeleted(maps.size(), maps.size());
	}
	
	public void removeMap(int index)
	{
		maps.remove(index);
		this.fireTableRowsDeleted(index, index);
	}
	
	@Override
	public String getColumnName(int columnIndex) 
	{
		switch (columnIndex)
		{
			case 0:
				return "Map name";
			case 1:
				return "SRID";
			default:
				return null;
		}
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) 
	{
		return false;
	}

	public int getColumnCount() 
	{
		return 2;
	}

	public int getRowCount() 
	{
		return maps.size();
	}

	public Object getValueAt(int rowIndex, int columnIndex) 
	{
		switch (columnIndex)
		{
			case 0:
				return maps.get(rowIndex).getName();
			case 1:
				return maps.get(rowIndex).getSrid();
			default:
				return null;
		}
	}
	
	public Set<Mapa> getMaps()
	{
		return Collections.unmodifiableSet(new HashSet<Mapa>(maps));
	}

}