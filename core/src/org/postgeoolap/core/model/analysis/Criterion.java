package org.postgeoolap.core.model.analysis;

import org.postgeoolap.core.model.Equality;

public class Criterion 
{
	private Equality equality;
	private Operator operator;
	private LogicalOperator connection;
	
	/**
	 * 
	 * @param equality
	 * @param operator
	 * @param connection operator concerning previous criterion, null if not 
	 * exists (i.e., this is the first criterion)
	 */
	public Criterion(Equality equality, Operator operator, LogicalOperator connection)
	{
		this.equality = equality;
		this.operator = operator;
		this.connection = connection;
	}

	public Equality getEquality() 
	{
		return equality;
	}

	public Operator getOperator() 
	{
		return operator;
	}
	
	public LogicalOperator getConnection() 
	{
		return connection;
	}
	
	@Override
	public String toString()
	{
		StringBuffer buffer = new StringBuffer();
		if (connection != null)
		{
			buffer.append(connection.toString());
			buffer.append(" ");
		}
		buffer.append(equality.getAttribute().getName());
		buffer.append(" ");
		buffer.append(operator.getSymbol());
		buffer.append(" ");
		buffer.append(equality.getValue());
		return buffer.toString();
	}
	
}
