package org.postgeoolap.core.model.analysis;

public enum Operator 
{
    EQUAL("%s = %s", "operator.equal_to", "=", 1),
    DIFFERENT("%s <> %s", "operator.not_equal_to", "<>", 1),
    MINOR("%s < %s", "operator.lesser_than", "<", 1),
    MAJOR("%s > %s", "operator.greater_than", ">", 1),
    MINOR_OR_EQUAL("%s <= %s", "operator.lesser_or_equal_to", "<=", 1),
    MAJOR_OR_EQUAL("%s >= %s", "operator.greater_or_equal_to", ">=", 1),
    LIKE("%s LIKE %s", "operator.contains", "LIKE", 1),
    LIKEALL("%s LIKE %%%s%%", "operator.contains", "LIKE", 1),
    BETWEEN("%s BETWEEN %s AND %s", "operator.between", "BETWEEN", 2),
    IN("%s IN %s", "operator.in", "IN", 1),
    NOT_IN(" %s NOT IN %s", "operator.not_in", "NOT IN", 1);
    
    private String operatorString;
    private String operatorLabel;
    private int valueCount;
    private String symbol;
    
    private Operator(String operatorString, String operatorLabel, 
    	String symbol, int valueCount)
    {
        this.operatorString = operatorString;
        this.operatorLabel = operatorLabel;
        this.symbol = symbol;
        this.valueCount = valueCount;
    }
    
    public String getValue()
    {
        return this.operatorString;
    }
    
    public String getLabel()
    {
        return this.operatorLabel;
    }
    
    public String getSymbol()
    {
        return this.symbol;
    }
    
    public int getValueCount()
    {
        return this.valueCount;
    }
    
    @Override
    public String toString()
    {
    	return symbol;
    }
 
}