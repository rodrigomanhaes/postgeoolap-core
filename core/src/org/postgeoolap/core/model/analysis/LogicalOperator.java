package org.postgeoolap.core.model.analysis;

import java.util.HashMap;
import java.util.Map;

import org.postgeoolap.core.i18n.Local;

public enum LogicalOperator 
{
	AND("AND", "operator.and"),
	OR("OR", "operator.or"),
	AND_NOT("AND NOT", "operator.and_not"),
	OR_NOT("OR NOT", "operator.or_not");
	
	private static Map<String, LogicalOperator> map;	
	static
	{
		map = new HashMap<String, LogicalOperator>();
		map.put(AND.toString(), AND);
		map.put(AND_NOT.toString(), AND_NOT);
		map.put(OR.toString(), OR);
		map.put(OR_NOT.toString(), OR_NOT);
	}
	
	private String operator;
	private String description;
	
	private LogicalOperator(String operator, String description)
	{
		this.operator = operator;
		this.description = description;
	}

	public String getDescription() 
	{
		return description;
	}

	public String getOperator() 
	{
		return operator;
	}
	
	@Override
	public String toString()
	{
		return Local.getString(description);
	}
	
	public static LogicalOperator get(String key)
	{
		return map.get(key);
	}
	
}