package org.postgeoolap.core.model;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.postgeoolap.core.i18n.Local;
import org.postgeoolap.core.model.exception.ModelException;
import org.postgeoolap.core.orm.HelperException;
import org.postgeoolap.core.orm.HibernateHelper;

public class Schema implements Serializable
{
	private static final long serialVersionUID = 8503437137833112454L;

	private static final Log log = LogFactory.getLog(Schema.class.getPackage().getName());
	
	private long id;
	private String name;
	private String databaseName;
	private String user;
	private String password;
	private String server;
	private Set<Mapa> maps;
	private Set<Table> tables;
	private Set<Cube> cubes;
	
	private Connection connection;
	
	public Schema()
	{
		id = -1;
		maps = new HashSet<Mapa>();
		cubes = new HashSet<Cube>();
	}
	
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

	@SuppressWarnings("unchecked")
	public static Set<Schema> getAll() throws ModelException
	{
		try
		{
			return HibernateHelper.get("from Schema");
		}
		catch (HelperException e)
		{
			throw new ModelException(e.getMessage(), e);
		}
	}
	
	private boolean driverLoad = false;
	
	public void connect() throws ModelException 
	{
		if (!driverLoad)
		{
			try
			{
				Class.forName("org.postgresql.Driver");
			}
			catch (ClassNotFoundException e)
			{
				log.error(e.getMessage(), e);
				throw new ModelException(Local.getString("error.postgresql_driver_not_found"));
			}
		}
		
		try
		{
			connection = DriverManager.getConnection(
				"jdbc:postgresql://" + server + ":5432/" + databaseName, user, password.toString());
		}
		catch (SQLException e)
		{
			log.error(e.getMessage(), e);
			throw new ModelException(Local.getString("error.cannot_connect_to_dw"), e);
		}
	}
	
	public void disconnect() throws ModelException
	{
		try
		{
			connection.close();
		}
		catch (SQLException e)
		{
			log.error(e.getMessage(), e);
			throw new ModelException(Local.getString("error.disconnecting_from_dw"), e);
		}
	}
	
	public boolean isConnected() throws ModelException
	{
		try
		{
			return !connection.isClosed();
		}
		catch (SQLException e)
		{
			log.error(e.getMessage(), e);
			throw new ModelException(e.getMessage(), e);
		}
	}
	
	@Override
	public String toString()
	{
		return name;
	}
	
	@Override
	public boolean equals(Object object)
	{
		if (!(object instanceof Schema))
			return false;
		
		Schema schema = (Schema) object;
		return this.name.equals(schema.name);
	}

	
	/* getters and setters */
	
	public Set<Mapa> getMaps() 
	{
		return maps;
	}

	public void setMaps(Set<Mapa> maps) 
	{
		this.maps = maps;
		for (Mapa map: maps)
			map.setSchema(this);
	}

	public String getName() 
	{
		return name;
	}

	public void setName(String name) 
	{
		this.name = name;
	}
	
	public String getDatabaseName() 
	{
		return databaseName;
	}

	public void setDatabaseName(String databaseName) 
	{
		this.databaseName = databaseName;
	}

	public String getPassword() 
	{
		return password;
	}

	public void setPassword(String password) 
	{
		this.password = password;
	}

	public String getServer() 
	{
		return server;
	}

	public void setServer(String server) 
	{
		this.server = server;
	}

	public String getUser() 
	{
		return user;
	}

	public void setUser(String user) 
	{
		this.user = user;
	}
	
	public long getId()
	{
		return id;
	}
	
	public void setId(long id)
	{
		this.id = id;
	}
	
	public Set<Table> getTables()
	{
		if (tables == null)
			loadTables();
		return Collections.unmodifiableSet(tables);
	}
	
	public void addTable(Table table)
	{
		tables.add(table);
	}
	
	private void loadTables()
	{
		tables = Table.getTables(this);
	}
	
	Connection getConnection()
	{
		return connection;
	}
	
	public Set<Cube> getCubes()
	{
		return cubes;
	}
	
	public void setCubes(Set<Cube> cubes)
	{
		this.cubes = cubes;
	}
	
	public void addCube(Cube cube)
	{
		this.cubes.add(cube);
		cube.setSchema(this);
	}
	
}