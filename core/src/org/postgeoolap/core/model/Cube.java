package org.postgeoolap.core.model;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

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

public class Cube implements Serializable 
{
	private static final long serialVersionUID = 1072020468190317644L;
	private static final Log log = LogFactory.getLog(Cube.class);
	
	private static final int FACT_DIMENSION_COUNT = 1;
	
	private long id;
	private String name;
	private String physicalName;
	private Schema schema;
	private long minimumAggregation;
	private Set<Dimension> dimensions;
	private Set<Aggregation> aggregations;
	
	public static final int MOST_AGGREGATED_LEVEL = 9;
	
	public Cube()
	{
		this.id = -1;
		dimensions = new HashSet<Dimension>();
	}
	
	public boolean wasProcessed()
	{
		return false;
	}
	
	public boolean hasDimensions()
	{
		return getDimensions().size() > 0;
	}
	
	public int dimensionCount()
	{
		return getDimensions().size();
	}
	
	public int getFactDimensionCount()
	{
		return FACT_DIMENSION_COUNT;
	}
	
	public Dimension factDimension()
	{
		return Dimension.factDimension(this.getDimensions());
	}
	
	/*
	 * Persistence
	 */
	
	public void persist() throws ModelException
	{
		try
		{
			if (id == -1)
			{
				log.info(MessageFormat.format(Local.getString("message.creating_cube"), 
					this.getName()));
				HibernateHelper.save(this);
			}
			else
			{
				log.info(MessageFormat.format(Local.getString("message.updating_cube"), 
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
			log.info(MessageFormat.format(Local.getString("message.deleting_cube"), 
				this.getName()));
			HibernateHelper.delete(this);
		}
		catch (HelperException e)
		{
			throw new ModelException(e.getMessage(), e);
		}
	}

	public static Set<Cube> getAll(Schema schema) throws ModelException
	{
		try
		{
			return HibernateHelper.get("from Cube c where c.schema = ?", 
				new Object[] { schema }, new Type[] { Hibernate.entity(Schema.class) } );
		}
		catch (HelperException e)
		{
			throw new ModelException(e.getMessage(), e);
		}
	}
	
	/*
	 * Getters e setters
	 */

	public long getId() 
	{
		return id;
	}

	public void setId(long id) 
	{
		this.id = id;
	}

	public long getMinimumAggregation() 
	{
		return minimumAggregation;
	}

	public void setMinimumAggregation(long minimumAggregation) 
	{
		this.minimumAggregation = minimumAggregation;
	}

	public String getName() 
	{
		return name;
	}

	public void setName(String name) 
	{
		this.name = name;
		this.compilePhysicalName();
	}
	
	public String getPhysicalName() 
	{
		return physicalName;
	}

	public void setPhysicalName(String physicalName) 
	{
	}

	public Schema getSchema() 
	{
		return schema;
	}

	public void setSchema(Schema schema) 
	{
		this.schema = schema;
	}
	
	public Set<Dimension> getDimensions() 
	{
		return dimensions;
	}

	public void setDimensions(Set<Dimension> dimensions) 
	{
		this.dimensions = dimensions;
	}
	
	public void addDimension(Dimension dimension)
	{
		dimensions.add(dimension);
		dimension.setCube(this);
	}
	
	public Set<Aggregation> getAggregations() 
	{
		return aggregations;
	}

	public void setAggregations(Set<Aggregation> aggregations) 
	{
		this.aggregations = aggregations;
	}
	
	public void addAggregation(Aggregation aggregation)
	{
		aggregations.add(aggregation);
		aggregation.setCube(this);
	}
	
	private void removeAllAggregations()
	{
		aggregations.clear();
	}
	
	@Override
	public String toString()
	{
		return name;
	}
	
	private void compilePhysicalName()
	{
		StringBuilder builder = new StringBuilder(this.getName());
		for (int i = builder.length() - 1; i >= 0; i--)
		{
			char ch = builder.charAt(i);
			if (Character.isWhitespace(ch) || Character.isDigit(ch))
				builder.deleteCharAt(i);
		}
		
		this.physicalName = builder.toString().toLowerCase();
	}
	
	/**
	 * Checks, for an arbitrary set of attributes, the least costing aggregation
	 * over which the set can be obtained (in worst case, over the base aggregation).
	 * Thus, the function checks if the aggregation must be generated. 
	 * @param attributes
	 * @return the aggregation that matches the attribute set or null if no aggregation must be generated. 
	 */
	private Aggregation verifyAggregationCost(Set<Attribute> attributes)
	{
		Aggregation aggregation = this.queryNavigator(attributes);
		int tupleCount;
		Dimension dimension = null;
		
		if (aggregation.isBase())
		{
			dimension = this.factDimension();
			tupleCount = JDBCHandler.instance(this.getSchema().getConnection())
				.askNumber("SELECT COUNT(*) FROM ?", dimension.getTableName());
		}
		else
			tupleCount = JDBCHandler.instance(this.getSchema().getConnection())
				.askNumber("SELECT COUNT(*) FROM ?", aggregation.getName());
		
		return tupleCount >= this.minimumAggregation ? aggregation : null;
	}
	
	/**
	 * Returns the best aggregation that matches to attributes
	 * @param attributes
	 * @return 
	 */
	private Aggregation queryNavigator(Set<Attribute> attributes)
	{
		Aggregation baseAggregation = null;
		
		for (Aggregation aggregation: this.getAggregations())
		{
			if (aggregation.isBase())
				baseAggregation = aggregation;
			else
			{
				List<Attribute> matchList = new ArrayList<Attribute>();
				for (Attribute attribute: attributes)
				{
					if (!aggregation.getAttributes().contains(attribute))
						return baseAggregation;
					else
						matchList.add(attribute);
					
					// checks if matchList count is equals to the attributes parameter's count.
					if (matchList.size() == attributes.size())
						return aggregation;
				}
			}
		}
		
		return baseAggregation;
	}
	
	public void process() throws ModelException
	{
		JDBCHandler handler = JDBCHandler.instance(CoreManager.instance().getActiveSchema().getConnection());
		
		// 1. deleting preexisting aggregations for this...
		log.info(Local.getString("message.deleting_aggregations") + "...");
		this.removeAllAggregations();
		// ... and saves!
		try
		{
			this.persist();
		}
		catch (ModelException e)
		{
			throw new ModelException(Local.getString("error.deleting_aggregations"), e);
		}
		log.info(Local.getString("message.aggregations_deleted"));
		
		log.info(Local.getString("message.generating_base_aggregation") + "...");
		try
		{
			Aggregation.createBaseAggregation(this);
		}
		catch (ModelException e)
		{
			throw new ModelException(Local.getString("error.creating_base_aggregation"), e);
		}
		
		List<Dimension> dataDimensions = 
			new ArrayList<Dimension>(Dimension.dataDimensions(this.getDimensions()));
		int dimensionCount = dataDimensions.size();
		log.info(MessageFormat.format(Local.getString("message.cube_has_dimensions"), 
			this.getName(), this.getPhysicalName(), dimensionCount));
		
		if (dimensionCount < 2)
		{
			String msg = Local.getString("error.less_than_two_dimensions"); 
			log.error(msg);
			throw new ModelException(msg);
		}
		
		Dimension _d = this.factDimension();
		Set<Attribute> aggregableAttributes = _d.aggregableAttributes();
		
		// HARD WORK BEGINS HERE...
		Stack<Stack<Integer>> stackOfStacks = new Stack<Stack<Integer>>();
		Dimension dimension1 = dataDimensions.get(0);
		int levelCount = dimension1.minimumHierarchicalLevel();
		
		Stack<Integer> stack = new Stack<Integer>();
		stack.push(0);
		stackOfStacks.push(stack);
		
		for (int i = levelCount; i <= Cube.MOST_AGGREGATED_LEVEL; i++)
		{
			stack = new Stack<Integer>();
			stack.push(i);
			stackOfStacks.push(stack);
		}
		
		int j;
		while (stackOfStacks.size() > 0)
		{
			Stack<Integer> activeStack = stackOfStacks.pop();
			
			if (activeStack.size() == dimensionCount)
			{
				boolean canProcess = false;
				StringBuilder sortingName = new StringBuilder();
				
				Set<Attribute> attributeSet = new HashSet<Attribute>(aggregableAttributes);
				
				for (Integer i: activeStack)
					if (i != Cube.MOST_AGGREGATED_LEVEL)
					{
						canProcess = true;
						break;
					}
				
				if (canProcess)
				{
					for (Integer pos: activeStack)
					{
						Dimension posDimension = dataDimensions.get(pos);
						sortingName.append(Integer.toString(pos));
						Set<Attribute> levelAttributes = posDimension.attributesByLevel(pos);
						attributeSet.addAll(levelAttributes);
					}
					
					int sorting = Integer.parseInt(sortingName.toString());
					String aggregationName = this.getPhysicalName() + sortingName;
					
					// Checks the need to create current aggregation
					log.info(MessageFormat.format(
						Local.getString("message.check_cost_benefit_relation"),
						aggregationName));
					
					Aggregation referenceAggregation = this.verifyAggregationCost(attributeSet);
					
					if (referenceAggregation != null)
					{
						log.info(MessageFormat.format("message.creating_aggregation", 
							aggregationName));
						Aggregation aggregation = new Aggregation();
						aggregation.setName(aggregationName);
						aggregation.setAttributes(attributeSet);
						aggregation.setCube(this);
						aggregation.setOrder(sorting);
						
						try
						{
							aggregation.create();
							log.info(MessageFormat.format(Local.getString(
								"message.aggregation_created"), aggregation.getName()));
						}
						catch (ModelException e)
						{
							String msg = Local.getString("message.aggregation_creation_failed");
							log.error(MessageFormat.format(msg, aggregation.getName()), e);
							throw new ModelException(msg, e);
						}
						
						log.info(MessageFormat.format(Local.getString(
							"message.copying_data_to_aggregation"), aggregationName));
						try
						{
							aggregation.load(referenceAggregation);
							log.info(MessageFormat.format(
								Local.getString("message.data_copied_to_aggregation"),
								aggregation.getName()));
						}
						catch (ModelException e)
						{
							log.error(MessageFormat.format(
								Local.getString("error.copying_data_to_aggregation"), 
								aggregation.getName()), e);
							throw e;
						}
						
						log.info(MessageFormat.format(
							Local.getString("message.creating_aggregation_indices"),
							aggregation.getName()));
						try
						{
							aggregation.createIndices();
							log.info(MessageFormat.format(
								Local.getString("message.aggregation_indices_created"), 
								aggregation.getName()));
						}
						catch (ModelException e)
						{
							log.error(MessageFormat.format(
								Local.getString("error.creating_aggregation_indices"), 
								aggregation.getName()), e);
							throw e;
						}
						
						log.info(MessageFormat.format(
							Local.getString("message.updating_aggregation_statistics"), 
							aggregation.getName()));
						
						String sql = "VACUUM ANALYZE " + aggregation.getName();
						handler.submit(sql);
					}
					else
						log.info(MessageFormat.format(
							Local.getString("message.low_cost_benefit_relation"), 
							aggregationName));
				}
			}
			else
			{
				/* Combine stack's items with the next dimension's attributes,
				 * until the attribute count on one stack be the same to dimension count
				 */ 
				int nextDimension = activeStack.size();
				Dimension activeDimension = dataDimensions.get(nextDimension);
				int level = activeDimension.minimumHierarchicalLevel();
				
				for (int i = level; i <= Cube.MOST_AGGREGATED_LEVEL; i++)
				{
					Stack<Integer> s = new Stack<Integer>();
					for (j = 0; j < activeStack.size(); j++)
						s.push(activeStack.get(j));
					s.push(i);
					stackOfStacks.push(s);
				}
				
				Stack<Integer> tempStack = new Stack<Integer>();
				for (j = 0; j < activeStack.size(); j++)
					tempStack.push(activeStack.get(j));
				tempStack.push(0);
				stackOfStacks.push(tempStack);
			}
		}
		
		log.info(Local.getString("message.end_of_cube_processing"));
	}
}