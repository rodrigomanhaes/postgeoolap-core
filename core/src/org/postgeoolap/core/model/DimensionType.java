package org.postgeoolap.core.model;

import java.util.HashMap;
import java.util.Map;

public enum DimensionType 
{
	FACT('F'),
	DIMENSION('D'),
	NON_AGGREGABLE('N');
	
	private char id;
	private static Map<Character, DimensionType> map;
	
	static
	{
		map = new HashMap<Character, DimensionType>();
		for (DimensionType type: DimensionType.values())
			map.put(type.id(), type);
	}
	
	private DimensionType(char id)
	{
		this.id = id;
	}
	
	public char id()
	{
		return id;
	}
	
	public static DimensionType byId(char id)
	{
		return map.get(id); 
	}
}
