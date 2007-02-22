package org.postgeoolap.core.model;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Hibernate;
import org.hibernate.type.Type;
import org.postgeoolap.core.i18n.Local;
import org.postgeoolap.core.metadata.MetadataConnection;
import org.postgeoolap.core.model.exception.HierarchicalLevelException;
import org.postgeoolap.core.model.exception.ModelException;
import org.postgeoolap.core.orm.HelperException;
import org.postgeoolap.core.orm.HibernateHelper;
import org.postgeoolap.core.util.JDBCHandler;

public class Dimension implements Serializable 
{
	private static final long serialVersionUID = 3963840253008234751L;
	
	private static final Log log = LogFactory.getLog(Dimension.class);
	
	private long id;
	private String name;
	private String tableName;
	private long tableId;
	private String clause;
	private DimensionType type;
	private char internalType;
	private String sqlCommand;
	private String joinClause;
	private Cube cube;
	private Set<Attribute> attributes;
	
	public Dimension()
	{
		id = -1;
	}

	/* getters and setters, only for ORM and GUI binding purposes */
	public Cube getCube() 
	{
		return cube;
	}

	public void setCube(Cube cube) 
	{
		this.cube = cube;
	}

	public long getId() 
	{
		return id;
	}

	public void setId(long id) 
	{
		this.id = id;
	}

	public String getJoinClause() 
	{
		return joinClause;
	}

	public void setJoinClause(String joinClause) 
	{
		this.joinClause = joinClause;
	}

	public String getName() 
	{
		return name;
	}

	public void setName(String name) 
	{
		this.name = name;
	}
	
	public String getClause() 
	{
		return clause;
	}

	public void setClause(String clause) 
	{
		this.clause = clause;
	}

	public String getSqlCommand() 
	{
		return sqlCommand;
	}

	public void setSqlCommand(String sqlCommand) 
	{
		this.sqlCommand = sqlCommand;
	}

	public DimensionType getType() 
	{
		return type;
	}

	public void setType(DimensionType type) 
	{
		this.type = type;
		this.internalType = type.id();
	}
	
	public long getTableId() 
	{
		return tableId;
	}

	public void setTableId(long tableId) 
	{
		this.tableId = tableId;
	}

	public String getTableName() 
	{
		return tableName;
	}

	public void setTableName(String tableName) 
	{
		this.tableName = tableName;
	}

	public char getInternalType() 
	{
		return internalType;
	}

	public void setInternalType(char internalType) 
	{
		this.internalType = internalType;
		this.type = DimensionType.byId(this.internalType);
	}
	
	public Set<Attribute> getAttributes() 
	{
		return attributes;
	}

	public void setAttributes(Set<Attribute> attributes) 
	{
		this.attributes = attributes;
	}
	
	public void addAttribute(Attribute attribute)
	{
		if (attributes == null)
			attributes = new HashSet<Attribute>();
		attributes.add(attribute);
		attribute.setDimension(this);
	}
	
	public boolean isType(DimensionType type)
	{
		return type.equals(this.getType());
	}

	/* persistence */
	public void persist() throws ModelException
	{
		try
		{
			if (id == -1)
				HibernateHelper.save(this);
			else
				HibernateHelper.update(this);
		}
		catch (HelperException e)
		{
			throw new ModelException(e.getMessage(), e);
		}
	}
	
	public void delete() throws ModelException
	{
		try
		{
			HibernateHelper.delete(this);
		}
		catch (HelperException e)
		{
			throw new ModelException(e.getMessage(), e);
		}
	}
	
	public static Set<Cube> getAll(Cube cube) throws ModelException
	{
		try
		{
			return HibernateHelper.get("from Dimension d where d.cube = ?", 
				new Object[] { cube }, new Type[] { Hibernate.entity(Cube.class) } );
		}
		catch (HelperException e)
		{
			throw new ModelException(e.getMessage(), e);
		}
	}
	
	/**
	 * @return the value of the name attribute as object's String representation  
	 */
	public String toString()
	{
		return name;
	}
	
	public void validateLevels() throws HierarchicalLevelException 
	{
		for (int i = Cube.MOST_AGGREGATED_LEVEL; i > 0; i--)
		{
			int count = 0;
			int existCount = 0;
			for (Attribute attribute: this.getAttributes())
			{
				if (attribute.getLevel() == i)
				{
					existCount++;
					if  (attribute.isStandard())
						count++;
				}
					
			}
			if (count > 1)
				throw new HierarchicalLevelException(
					Local.getString("error.more_than_one_as_standard"));
			else if (count < 1 && existCount > 0)
				throw new HierarchicalLevelException(
					Local.getString("error.none_as_standard"));
		}
	}
	
	public static Dimension factDimension(Iterable<Dimension> dimensions)
	{
		for (Dimension dimension: dimensions)
			if (dimension.getType().equals(DimensionType.FACT))
				return dimension;
		return null;
	}
	
	public static Set<Dimension> dataDimensions(Iterable<Dimension> dimensions)
	{
		Set<Dimension> set = new HashSet<Dimension>();
		for (Dimension dimension: dimensions)
			if (dimension.getType().equals(DimensionType.DIMENSION))
				set.add(dimension);
		return set;
	}
	
	/**
	 * Gets the set of aggregable attributes for this dimension
	 * @return the set of aggregable attributes or null if this dimension isn't fact. 
	 */
	public Set<Attribute> aggregableAttributes()
	{
		return DimensionType.FACT.equals(this.getType()) ?
			Attribute.getAggregableAttributes(this.getAttributes()) : null;
	}
	
	public int minimumHierarchicalLevel()
	{
		return Attribute.minimumHierarchicalLevel(this.getAttributes());
	}
	
	public Set<Attribute> attributesByLevel(int level)
	{
		return Attribute.getAttributesByLevel(this.getAttributes(), level);
	}
	
	/**
	 * Returns the set of dimensions whose attributes are associated to this aggregation.
	 * The obtaining of the set requires a query to CnsAggregation view. Here, we used 
	 * JDBC directly rather using Hibernate.
	 * @param aggregation
	 * @return the set of dimensions 
	 */
	
	public static Set<Dimension> getDimensionsInAggregation(Aggregation aggregation)
		throws ModelException
	{
		String sql = 
			"SELECT DISTINCT dimensioncode, dimensionname, tablename, dimensiontype, clause, tablecode " +
			"	FROM cnsaggregation " +
			"	WHERE aggregationcode = ?";
		
		JDBCHandler handler = JDBCHandler.instance(MetadataConnection.connection());
		Set<Dimension> set = new HashSet<Dimension>();
		ResultSet result = handler.submitQuery(sql, aggregation.getId());
		try
		{
			while (result.next())
			{
				Dimension dimension = new Dimension();
				dimension.setId(result.getInt("dimensioncode"));
				dimension.setName(result.getString("dimensionname"));
				dimension.setTableId(result.getLong("tablecode"));
				dimension.setTableName(result.getString("tablename"));
				dimension.setClause(result.getString("clause"));
				dimension.setInternalType(result.getString("dimensiontype").charAt(0));
				
				set.add(dimension);
			}
		}
		catch (SQLException e)
		{
			log.error(e.getMessage(), e);
			throw new ModelException(e.getMessage(), e);
		}
		finally
		{
			try
			{
				result.close();
			}
			catch (SQLException e)
			{
			}
		}
		return set;
	}
	
	public void setClauseWithFactDimension(Dimension factDimension) throws ModelException
	{
		// search for the keys to related fields
		String sql = 
			"SELECT conkey[1] AS factAttributeCode, confkey[1] AS dimensionAttributeCode " +
			"  FROM pg_constraint " +
			"  WHERE conrelid = ? AND confrelid = ? AND contype = 'f'";
		
		JDBCHandler handler = JDBCHandler.instance(this.getCube().getSchema().getConnection());
		
		try
		{
			ResultSet result = handler.submitQuery(sql, 
				factDimension.getTableId(), this.getTableId());
			
			if (!result.next())
				throwConstraintsModelException();
			String factAttributeCode = result.getString("factAttributeCode");
			String dimensionAttributeCode = result.getString("dimensionAttributeCode");
			result.close();
			
			sql = "SELECT attname AS factAttributeName FROM pg_attribute " +
				  "  WHERE attrelid = ? AND attnum = ?";
			result = handler.submitQuery(sql, factDimension.getTableId(), factAttributeCode);
			if (!result.next())
				throwConstraintsModelException();
			String factAttributeName = result.getString("factAttributeName");
			result.close();
			
			sql = "SELECT attname AS dimensionAttributeName FROM pg_attribute " +
				  "  WHERE attrelid = ? AND attnum = ?";
			result = handler.submitQuery(sql, this.getTableId(), dimensionAttributeCode);
			if (!result.next())
				throwConstraintsModelException();
			String dimensionAttributeName = result.getString("dimensionAttributeName");
			result.close();
			
			this.setClause(
				new StringBuilder()
					.append(factDimension.getTableName())
					.append(".")
					.append(factAttributeName)
					.append(" = ")
					.append(this.getTableName())
					.append(".")
					.append(dimensionAttributeName)
					.toString()
			);
		}
		catch (SQLException e)
		{
			log.error(e.getMessage(), e);
			throw new ModelException(e);
		}
	}
	
	private void throwConstraintsModelException() throws ModelException 
	{
		String msg = MessageFormat.format(Local.getString("error.check_dw_constraints"), 
			this.getTableName());
		log.error(msg);
		throw new ModelException(msg);
	}
	
	public List<Attribute> getHierarchy() throws ModelException
	{
		List<Attribute> list = new ArrayList<Attribute>();
		for (int level = Cube.MOST_AGGREGATED_LEVEL; level > 0; level--)
		{
			Attribute attribute = Attribute.getStandard(this, level);
			if (attribute != null)
				list.add(attribute);
		}
		return list;
	}
}