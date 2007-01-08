package org.postgeoolap.core.model;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.postgeoolap.core.model.exception.ModelException;
import org.postgeoolap.core.orm.HibernateUtils;

public class Schema implements Serializable
{
	private static final long serialVersionUID = 8503437137833112454L;

	private static final Log log = LogFactory.getLog(Schema.class.getPackage().getName());
	
	private long id;
	private String name;
	private String user;
	private String password;
	private String server;
	private Set<Mapa> maps;
	
	private Connection connection;
	
	public Schema()
	{
		id = -1;
		maps = new HashSet<Mapa>();
	}
	
	public void persist() throws ModelException
	{
		Session session = HibernateUtils.openSession();
		Transaction transaction = session.beginTransaction();
		try
		{
			if (id == -1)
				session.save(this);
			else
				session.update(this);
			transaction.commit();
		}
		catch (HibernateException e)
		{
			transaction.rollback();
		}
		finally
		{
			session.close();
		}
	}
	
	@SuppressWarnings("unchecked")
	public static Set<Schema> getAll() throws ModelException
	{
		Session session = HibernateUtils.openSession();
		Set<Schema> set = null;
		try
		{
			Query query = session.createQuery("from Schema"); 
			set = new HashSet<Schema>(query.list());
		}
		finally
		{
			session.close();
		}
		return set;
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
				throw new ModelException("Cannot find PostGreSQL JDBC driver class");
			}
		}
		
		try
		{
			connection = DriverManager.getConnection(
				"jdbc:postgresql://" + server + ":5432/" + name, user, password.toString());
		}
		catch (SQLException e)
		{
			log.error(e.getMessage(), e);
			throw new ModelException("Cannot connect to data warehouse", e);
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
			throw new ModelException("Error on disconnecting from data warehouse", e);
		}
	}
	
	public void delete()
	{
		Session session = HibernateUtils.openSession();
		Transaction transaction = session.beginTransaction();
		try
		{
			session.delete(this);
			transaction.commit();
		}
		catch (HibernateException e)
		{
			transaction.rollback();
		}
		finally
		{
			session.close();
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
	
}
