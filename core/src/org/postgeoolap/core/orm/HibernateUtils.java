package org.postgeoolap.core.orm;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtils 
{
	private static SessionFactory factory;
	
	public static SessionFactory getSessionFactory() 
	{
		if (factory == null) 
        {
			try 
            {
				factory = new Configuration().configure().buildSessionFactory(); 
			}
			catch (Exception exception) 
            {
				throw new ExceptionInInitializerError(exception);
			}
		}
		
		return factory;
	}
	
	public static Session getCurrentSession() 
	{
		return getSessionFactory().getCurrentSession();
	}
	
	public static Session openSession() 
	{
		return getSessionFactory().openSession();
	}
}