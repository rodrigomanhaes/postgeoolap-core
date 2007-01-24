package org.postgeoolap.core.orm;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.type.Type;
import org.postgeoolap.core.i18n.Local;

public class HibernateHelper 
{
	private static final Log log = LogFactory.getLog(HibernateHelper.class);
	
	private static Session session;
	
	private static Session session()
	{
		if (session == null)
			session = HibernateUtils.openSession();
		return session;
	}
	
	public static void save(Object object) throws HelperException
	{
		Transaction transaction = session().beginTransaction();
		try
		{
			session().save(object);
			transaction.commit();
		}
		catch (HibernateException e)
		{
			transaction.rollback();
			log.error(e.getMessage(), e);
			throw new HelperException(Local.getString("error.cant_save") + "\n" + 
				e.getMessage(), e);
		}
	}
	
	public static void update(Object object) throws HelperException
	{
		Transaction transaction = session().beginTransaction();
		try
		{
			session().update(object);
			transaction.commit();
		}
		catch (HibernateException e)
		{
			transaction.rollback();
			log.error(e.getMessage(), e);
			throw new HelperException(Local.getString("error.cant_update") + "\n" + 
				e.getMessage(), e);
		}
	}
	
	public static void delete(Object object) throws HelperException
	{
		Transaction transaction = session().beginTransaction();
		try
		{
			session().delete(object);
			transaction.commit();
		}
		catch (HibernateException e)
		{
			transaction.rollback();
			log.error(e.getMessage(), e);
			throw new HelperException(Local.getString("error.cant_delete") + "\n" + 
				e.getMessage(), e);
		}
	}
	
	public static <T> Set<T> get(String sQuery) throws HelperException
	{
		return get(sQuery, new Object[] {}, null);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> Set<T> get(String sQuery, Object[] objects, Type[] types)
		throws HelperException
	{
		Set<T> set = null;
		try
		{
			Query query = session().createQuery(sQuery); 
			for (int i = 0; i < objects.length; i++)
				query.setParameter(i, objects[i], types[i]);
			set = new HashSet<T>(query.list());
		}
		catch (HibernateException e)
		{
			log.error(e.getMessage(), e);
			throw new HelperException(Local.getString("error.cant_get") + "\n" + 
				e.getMessage(), e);
		}
		return set;
	}
	
	public static void endUp()
	{
		session.close();
	}
}
