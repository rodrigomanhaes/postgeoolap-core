package org.postgeoolap.core.model;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Hibernate;
import org.hibernate.type.Type;
import org.postgeoolap.core.CoreManager;
import org.postgeoolap.core.i18n.Local;
import org.postgeoolap.core.metadata.MetadataConnection;
import org.postgeoolap.core.model.exception.ModelException;
import org.postgeoolap.core.orm.HelperException;
import org.postgeoolap.core.orm.HibernateHelper;
import org.postgeoolap.core.util.JDBCHandler;

public class Aggregation implements Serializable 
{
	private static final long serialVersionUID = 4739085326521756461L;
	
	private static final Log log = LogFactory.getLog(Aggregation.class);
	
	private long id;
	private String name;
	private boolean base;
	private String sqlBase;
	private Cube cube;
	private int order;
	private Set<Attribute> attributes;
	
	public Aggregation()
	{
		super();
		id = -1;
	}
	
	/**
	 * Creates the base aggregation for a cube, i.e., the aggregation that is
	 * composed by the join operation applied over all tables in the cube
	 * @param cube
	 * @return the base aggregation
	 * @throws ModelException
	 */
	public static Aggregation createBaseAggregation(Cube cube) 
		throws ModelException
	{
		int dimensionCount = cube.dimensionCount() - cube.getFactDimensionCount();
		
		StringBuilder orderString = new StringBuilder();
		for (int i = 0; i < dimensionCount; i++)
			orderString.append("9");
		int order = Integer.parseInt(orderString.toString());
		
		// assemblies FROM clause
		StringBuilder sqlBase = new StringBuilder();
		sqlBase.append("FROM ");
		for (Dimension dimension: cube.getDimensions())
		{
			if (sqlBase.length() != 5)
				sqlBase.append(", ");
			sqlBase.append("\"");
			sqlBase.append(dimension.getTableName());
			sqlBase.append("\"");
		}
		
		// assemblies WHERE clause
		sqlBase.append(" WHERE ");
		boolean first = true;
		for (Dimension dimension: cube.getDimensions())
		{
			if (!dimension.getType().equals(DimensionType.FACT))
			{
				if (first)
					first = false;
				else
					sqlBase.append(" AND ");
				sqlBase.append(dimension.getClause());
			}
		}
		
		Aggregation aggregation = new Aggregation();
		aggregation.setName(cube.getPhysicalName() + "Base");
		aggregation.setBase(true);
		aggregation.setSqlBase(sqlBase.toString());
		aggregation.setOrder(order);
		aggregation.setCube(cube);
		
		aggregation.persist();
		
		cube.addAggregation(aggregation);
		
		return aggregation;
	}
	
	public void load(Aggregation reference) throws ModelException
	{
		Set<Attribute> attributeSet = this.getAttributes();
		int aggregableCount = 0;
		StringBuilder sql = new StringBuilder();
		
		sql.append("INSERT INTO ");
		sql.append(this.getName());
		sql.append(" (");
		
		for (Attribute attribute: attributeSet)
		{
			if (sql.charAt(sql.length() - 1) != '(')
				sql.append(", ");
			sql.append(attribute.getPhysicalName());
			
			if (!AggregationType.NON_AGGREGABLE.equals(attribute.getAggregationType()))
				aggregableCount++;
		}
			
		sql.append(") (SELECT ");
			
		boolean first = true;
		
		for (Attribute attribute: attributeSet)
		{
			String attributeName =
				(reference.isBase() ? attribute.getDimension().getTableName() : reference.getName()) + 
				"." + attribute.getPhysicalName();
			
			if (!AggregationType.NON_AGGREGABLE.equals(attribute.getAggregationType()))
				attributeName = MessageFormat.format(attribute.getAggregationType().getClause(),
					attributeName);
			
			if (!first)
				sql.append(", ");
			else
				first = false;
			sql.append(attributeName);
		}
		
		// FROM clause
		sql.append(" FROM ");
		if (!reference.isBase())
			sql.append(reference.getName());
		else
		{
			first = true;
			Set<Dimension> dimensions = this.getDimensions();
			for (Dimension dimension: dimensions)
			{
				if (!first)
					sql.append(", ");
				else
					first = false;
				sql.append(dimension.getTableName());
			}
			
			int nonFactDimensionCount = dimensions.size();
			if (nonFactDimensionCount > 0)
			{
				sql.append(" WHERE ");
				first = true;
				for (Dimension dimension: dimensions)
				{
					if (!dimension.isType(DimensionType.DIMENSION))
						continue;
					if (!first)
						sql.append(" AND ");
					else
						first = false;
					
					sql.append(dimension.getClause());
				}
			}
			
			//sql = new StringBuilder(sql.substring(0, sql.length() - 9));
			
			// GROUP BY
			if (aggregableCount > 0)
			{
				sql.append(" GROUP BY ");
				first = true;
				for (Attribute attribute: attributeSet)
				{
					if (AggregationType.NON_AGGREGABLE.equals(attribute.getAggregationType()))
					{
						String attributeName = reference.isBase() ?
							attribute.getDimension().getTableName() + "." + attribute.getPhysicalName() :
							reference.getName() + "." + attribute.getPhysicalName();
						if (!first)
							sql.append(", ");
						else
							first = false;
						sql.append(attributeName);
					}
				}
			}
		}
		
		sql.append(")");
		
		JDBCHandler handler = JDBCHandler.instance(CoreManager.instance().getActiveSchema().getConnection());
		handler.submit(sql.toString());
	}
	
	/**
	 * Calls <code>Dimension.getDimensionsInAggregation(Aggregation)</code> in order to 
	 * return the set of dimensions whose attributes are associated to this aggregation.
	 * 
	 * @return the set of dimensions
	 */
	public Set<Dimension> getDimensions() throws ModelException
	{
		return Dimension.getDimensionsInAggregation(this);
	}
	
	/**
	 * 
	 * @throws ModelException
	 */
	public void createIndices() throws ModelException
	{
		boolean combinedIndices = false; /* TODO this must be configured */
		
		JDBCHandler handler = JDBCHandler.instance(CoreManager.instance().getActiveSchema().getConnection());
		
		Set<Dimension> dimensions = this.getDimensions();
		Set<Dimension> nonFactDimensions = Dimension.dataDimensions(dimensions);
		
		if (nonFactDimensions.size() ==  0)
			throw new ModelException(Local.getString("error.aggregation_hasnt_dimensions"));
		
		int i = 1;
		for (Dimension dimension: nonFactDimensions)
		{
			StringBuilder fieldNames = new StringBuilder();
			StringBuilder totalFieldNames = new StringBuilder();
			
			int j = 1; 
			
			for (Attribute attribute: this.getDimensionAttributes(dimension))
			{
				if (!attribute.isGeographical())
				{
					if (combinedIndices)
					{
						if (fieldNames.length() > 0)
							fieldNames.append(", ");
						fieldNames.append(attribute.getName());
						
						if (totalFieldNames.length() > 0)
							totalFieldNames.append(", ");
						totalFieldNames.append(attribute.getName());
					}
					else
					{
						StringBuilder sql = new StringBuilder();
						sql.append("CREATE INDEX ix");
						sql.append(this.getName());
						sql.append(i);
						sql.append(j);
						sql.append(" ON ");
						sql.append(this.getName());
						sql.append(" (");
						sql.append(attribute.getPhysicalName());
						sql.append(") ");
						handler.submit(sql.toString());						
					}
				}
				else // geographical attributes
				{
					StringBuilder sql = new StringBuilder();
					sql.append("CREATE INDEX ixgeo");
					sql.append(this.getName());
					sql.append(attribute.getPhysicalName());
					sql.append(" ON ");
					sql.append(this.getName());
					sql.append(" USING GIST (");
					sql.append(attribute.getPhysicalName());
					sql.append(" GIST_GEOMETRY_OPS)");
					handler.submit(sql.toString());
				}
				
				j++;
			}
			
			if (combinedIndices)
			{
				String indexName = this.getName() + i;
				StringBuilder sql = new StringBuilder();
				sql.append("CREATE INDEX ");
				sql.append(indexName);
				sql.append(" ON ");
				sql.append(this.getName());
				sql.append(" (");
				sql.append(fieldNames);
				sql.append(")");
				handler.submit(sql.toString());
			}
			
			i++;
		}
	}
	
	private Set<Attribute> getDimensionAttributes(Dimension dimension)
		throws ModelException
	{
		String sql = 
			"SELECT attributecode, attributename, attributephysicalname, " +
			"    attributetype, level, standard, aggregationtype, geographic " +
			"  FROM cnsaggregation " +
			"  WHERE aggregationcode = ? AND dimensioncode = ?";

		Set<Attribute> set = new HashSet<Attribute>();
		ResultSet result = JDBCHandler.instance(MetadataConnection.connection())
			.submitQuery(sql, this.getId(), dimension.getId());
		
		try
		{
			while (result.next())
			{
				Attribute attribute = new Attribute();
				attribute.setId(result.getLong("attributecode"));
				attribute.setName(result.getString("attributename"));
				attribute.setPhysicalName(result.getString("attributephysicalname"));
				attribute.setType(result.getString("attributetype"));
				attribute.setLevel(result.getInt("level"));
				attribute.setStandard(result.getBoolean("standard"));
				attribute.setAggregationType(
					AggregationType.byId(result.getString("aggregationtype").charAt(0)));
				attribute.setDimension(dimension);
				set.add(attribute);
			}
		}
		catch (SQLException e)
		{
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
	
	public void create() throws ModelException
	{
		StringBuilder sql = new StringBuilder();
		sql.append("CREATE TABLE ");
		sql.append(this.getName());
		sql.append(" (");
		
		boolean first = true;
		for (Attribute attribute: this.getAttributes())
		{
			if (attribute.isGeographical())
				continue;
			
			String attributeType =
				"char".equals(attribute.getType()) || "varchar".equals(attribute.getType()) ?
					"varchar(255)" : attribute.getType(); 
			if (!first)
				sql.append(", ");
			else
				first = false;
			
			sql.append(attribute.getPhysicalName());
			sql.append(" ");
			sql.append(attributeType);
		}
		sql.append(")");
		
		JDBCHandler handler = JDBCHandler.instance(CoreManager.instance().getActiveSchema().getConnection());
		handler.submit(sql.toString());
		
		for (Attribute attribute: this.getAttributes())
		{
			if (attribute.isGeographical())
			{
				sql = new StringBuilder();
				sql.append("SELECT addgeometrycolumn('");
				sql.append(this.getCube().getSchema().getDatabaseName());
				sql.append("', '");
				sql.append(this.getName());
				sql.append("', '");
				sql.append(attribute.getPhysicalName());
				sql.append("', '");
				if (this.getCube().getSchema().getMaps() != null && 
					this.getCube().getSchema().getMaps().size() > 0)
					sql.append(this.getCube().getSchema().getMaps().iterator().next().getSrid());
				else
					sql.append(-1);
				sql.append("', '");
				sql.append(attribute.getType().toUpperCase());
				sql.append("', 2)");
				
				handler.submit(sql.toString());
			}
		}
		
		this.persist();
	}
	
	/* Persistence */
	public void persist() throws ModelException
	{
		try
		{
			if (id == -1)
			{
				log.info(MessageFormat.format(Local.getString("message.creating_aggregation"), 
					this.getName()));
				HibernateHelper.save(this);
			}
			else
			{
				log.info(MessageFormat.format(Local.getString("message.updating_aggregation"), 
					this.getName()));
				HibernateHelper.update(this);
			}
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
			log.info(MessageFormat.format(Local.getString("message.deleting_aggregation"), 
				this.getName()));
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
			return HibernateHelper.get("from Aggregation a where a.cube = ?", 
				new Object[] { cube }, new Type[] { Hibernate.entity(Cube.class) } );
		}
		catch (HelperException e)
		{
			throw new ModelException(e.getMessage(), e);
		}
	}
	
	/* Getters/setters for ORM purposes */
	public Set<Attribute> getAttributes() {
		return attributes;
	}
	public void setAttributes(Set<Attribute> attributes) {
		this.attributes = attributes;
	}
	public boolean isBase() {
		return base;
	}
	public void setBase(boolean base) {
		this.base = base;
	}
	public Cube getCube() {
		return cube;
	}
	public void setCube(Cube cube) {
		this.cube = cube;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getOrder() {
		return order;
	}
	public void setOrder(int order) {
		this.order = order;
	}
	public String getSqlBase() {
		return sqlBase;
	}
	public void setSqlBase(String sqlBase) {
		this.sqlBase = sqlBase;
	}
	
	@Override
	public String toString()
	{
		return name;
	}
}