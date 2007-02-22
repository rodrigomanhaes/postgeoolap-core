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
import org.postgeoolap.core.model.exception.ModelException;
import org.postgeoolap.core.orm.HelperException;
import org.postgeoolap.core.orm.HibernateHelper;
import org.postgeoolap.core.util.JDBCHandler;

public class Attribute implements Serializable 
{
	private static final long serialVersionUID = 5688522124133523295L;
	
	private static final Log log = LogFactory.getLog(Attribute.class);
	
	private long id;
	private String name;
	private String physicalName;
	private int size;
	private int level;
	private boolean standard;
	private AggregationType aggregationType;
	private char internalAggregationType;
	private boolean geographical;
	private String type;
	
	private Dimension dimension;
	
	public Attribute()
	{
		id = -1;
	}
	
	public static Set<Attribute> getAggregableAttributes(Iterable<Attribute> attributes)
	{
		Set<Attribute> set = new HashSet<Attribute>();
		for (Attribute attribute: attributes)
			if (!"N".equals(attribute.getType()))
				set.add(attribute);
		return set;
	}
	
	public static int minimumHierarchicalLevel(Iterable<Attribute> attributes)
	{
		int min = Integer.MAX_VALUE;
		for (Attribute attribute: attributes)
			if (attribute.getLevel() < min)
				min = attribute.getLevel();
		return min;
	}
	
	public static Set<Attribute> getAttributesByLevel(
		Iterable<Attribute> attributes, int level)
	{
		Set<Attribute> set = new HashSet<Attribute>();
		for (Attribute attribute: attributes)
			if (attribute.getLevel() == level)
				set.add(attribute);
		return set;
	}
	
	public Set<Equality> getInstanceSet() throws ModelException
	{
		JDBCHandler handler = JDBCHandler.createInstance(CoreManager.instance().getActiveSchema().getConnection());
		ResultSet result =  handler.submitQuery("SELECT DISTINCT " + this.getPhysicalName() + 
			" FROM " + this.getDimension().getTableName());
		Set<Equality> set = new HashSet<Equality>();
		try
		{
			while (result.next())
				set.add(new Equality(this, result.getObject(1)));
		}
		catch (SQLException e)
		{
			log.error(e.getMessage(), e);
			throw new ModelException(e.getMessage(), e);
		}
		
		return set;
	}
	
	public static Attribute getStandard(Dimension dimension, int level)
		throws ModelException
	{
		Set<Attribute> attributes = null;
		try
		{
			attributes = HibernateHelper.get(
				"from Attribute a where a.dimension = ? and a.standard = ? and a.level = ?",
				new Object[] {dimension, true, level}, 
				new Type[] 
		        { 
					Hibernate.entity(Dimension.class),
					Hibernate.BOOLEAN,
					Hibernate.INTEGER 
				}
			);
		}
		catch (HelperException e)
		{
			throw new ModelException(e.getMessage(), e);
		}
		
		return attributes.size() > 0 ?
			attributes.iterator().next() : null;
	}
	
	/* Persistence */
	
	public void persist() throws ModelException
	{
		try
		{
			if (id == -1)
			{
				log.info(MessageFormat.format(Local.getString("message.creating_attribute"), 
					this.getName()));
				HibernateHelper.save(this);
			}
			else
			{
				log.info(MessageFormat.format(Local.getString("message.updating_attribute"), 
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
			log.info(MessageFormat.format(Local.getString("message.deleting_attribute"), 
				this.getName()));
			HibernateHelper.delete(this);
		}
		catch (HelperException e)
		{
			throw new ModelException(e.getMessage(), e);
		}
	}

	public static Set<Attribute> getAll(Dimension dimension) throws ModelException
	{
		try
		{
			return HibernateHelper.get("from Attribute a where a.dimension = ?", 
				new Object[] { dimension }, new Type[] { Hibernate.entity(Dimension.class) } );
		}
		catch (HelperException e)
		{
			throw new ModelException(e.getMessage(), e);
		}
	}
	
	@Override
	public String toString()
	{
		return name;
	}
	
	/* Getters/setters */

	public AggregationType getAggregationType() {
		return aggregationType;
	}

	public void setAggregationType(AggregationType aggregationType) 
	{
		this.aggregationType = aggregationType;
		this.internalAggregationType = aggregationType.id();
	}
	
	public char getInternalAggregationType() {
		return internalAggregationType;
	}

	public void setInternalAggregationType(char internalAggregationType) 
	{
		this.internalAggregationType = internalAggregationType;
		this.aggregationType = AggregationType.byId(internalAggregationType);
	}

	public boolean isGeographical() {
		return geographical;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhysicalName() {
		return physicalName;
	}

	public void setPhysicalName(String physicalName) {
		this.physicalName = physicalName;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public boolean isStandard() {
		return standard;
	}

	public boolean setStandard(boolean standard) 
	{
		if (dimension != null && standard)
		{
			Set<Attribute> attributes = dimension.getAttributes();
			for (Attribute attribute: attributes)
				if (attribute.getLevel() == this.getLevel() && attribute.isStandard())
					return false; 
		}
		this.standard = standard;
		return true;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) 
	{
		this.type = type;
		String upperType = type.toUpperCase();
		this.geographical = 
			"GEOMETRY".equals(upperType) || "POINT".equals(upperType) || 
			"LINESTRING".equals(upperType) || "POLYGON".equals(upperType) || 
			"MULTIPOLYGON".equals(upperType) || "MULTILINESTRING".equals(upperType) || 
			"MULTIPOINT".equals(upperType);
	}

	public Dimension getDimension() {
		return dimension;
	}

	public void setDimension(Dimension dimension) {
		this.dimension = dimension;
	}
}