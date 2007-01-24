package org.postgeoolap.core.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.postgeoolap.core.util.JDBCHandler;

public class Field 
{
	private static final Log log = LogFactory.getLog(Field.class);
	
	private String name;
	@SuppressWarnings("unused")
	private String type;
	@SuppressWarnings("unused")
	private Table table;
	
	public Field(String name, String type, Table table)
	{
		this.name = name;
		this.type = type;
		this.table = table;
	}
	
	public String getName()
	{
		return name;
	}
	
	public String getType()
	{
		return type;
	}
	
	@Override
	public String toString()
	{
		return name;
	}
	
	static Set<Field> getFields(Table table)
	{
		String sql = 
			"SELECT attname, typname " +
			"  FROM pg_attribute, pg_class, pg_type, pg_namespace " +
			"  WHERE pg_attribute.attrelid = pg_class.relfilenode " +
			"    AND pg_attribute.atttypid = pg_type.oid " +
			"    AND relkind = 'r' AND attnum >= 1 " +
			"    AND pg_namespace.nspname = 'public' " +
			"    AND pg_class.relnamespace = pg_namespace.oid " +
			"    AND relname = ?";
		
		Set<Field> set = new HashSet<Field>();
		
		JDBCHandler handler = JDBCHandler.instance(table.getSchema().getConnection());
		ResultSet result = handler.submitQuery(sql, table.getName());
		
		try
		{
			while (result.next())
			{
				Field field = new Field(
					result.getString("attname"),
					result.getString("typname"),
					table);
				set.add(field);
			}
		}
		catch (SQLException e)
		{
			log.error(e.getMessage(), e);
			return null;
		}
		
		return set;
	}
}
