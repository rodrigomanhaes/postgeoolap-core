package org.postgeoolap.core.model;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.postgeoolap.core.model.exception.ModelException;
import org.postgeoolap.core.util.JDBCHandler;

public class Table implements Serializable
{
	private static final long serialVersionUID = -5545367038058233127L;

	private static final Log log = LogFactory.getLog(Table.class);
	
	@SuppressWarnings("unused")
	private long id;
	private String name;
	private Set<Field> fields;
	private Schema schema;

	// for ORM purposes, compliance to JavaBean standard
	public Table()
	{
	}
	
	public Table(long id, String name, Schema schema)
	{
		this.id = id;
		this.name = name;
		this.schema = schema;
	}
	
	public Set<Field> getFields()
	{
		if (fields == null)
			loadFields();
		return fields;
	}
	
	private void loadFields()
	{
		fields = Field.getFields(this);
	}

	@Override
	public String toString()
	{
		return name;
	}
	
	// static block
	static Set<Table> getTables(Schema schema)
	{
		Set<Table> tables = new HashSet<Table>();
		try
		{
			if (!schema.isConnected())
				return null;
		}
		catch (ModelException e)
		{
			return null;
		}
		
		String sql = 
			"SELECT relfilenode, relname " +
			"  FROM pg_class, pg_namespace " +
			"  WHERE relkind = 'r' " +
			"    AND pg_namespace.nspname = 'public' " +
			"    AND pg_class.relnamespace = pg_namespace.oid";
		
		JDBCHandler handler = JDBCHandler.instance(schema.getConnection());
		ResultSet result = handler.submitQuery(sql);
		
		try
		{
			while (result.next())
			{
				Table table = new Table(
					result.getLong("relfilenode"),
					result.getString("relname"),
					schema
				);
				tables.add(table);
			}
		}
		catch (SQLException e)
		{
			log.error(e.getMessage(), e);
			return null;
		}
		
		return tables;
	}
	
	public Schema getSchema()
	{
		return schema;
	}

	public long getId() 
	{
		return id;
	}

	public void setId(long id) 
	{
		this.id = id;
	}

	public String getName() 
	{
		return name;
	}

	public void setName(String name) 
	{
		this.name = name;
	}
	
}
