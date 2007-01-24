package org.postgeoolap.core.model;

import java.util.HashMap;
import java.util.Map;

public enum AggregationType 
{
	NON_AGGREGABLE('N', null),
	COUNT('C', "COUNT({0})"),
	SUM('S', "SUM({0})"),
	MAX('M', "MAX({0})"),
	MIN('I', "MIN({0})"),
	AVERAGE('A', "AVG({0})");
		
	private char id;
	private String clause;
	
	private static Map<Character, AggregationType> map;
	
	static
	{
		map = new HashMap<Character, AggregationType>();
		for (AggregationType type: AggregationType.values())
			map.put(type.id(), type);
	}
	
	private AggregationType(char id, String clause)
	{
		this.id = id;
		this.clause = clause;
	}
	
	public char id()
	{
		return id; 
	}
	
	public static AggregationType byId(char id)
	{
		return map.get(id);
	}
	
	public String getClause()
	{
		return clause;
	}

}
