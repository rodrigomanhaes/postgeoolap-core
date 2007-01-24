package org.postgeoolap.core.i18n;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Local 
{
	private static final Log log = LogFactory.getLog(Local.class);
	private static Locale locale;
	private static ResourceBundle resources;
	
	public static void setLocale(String language, String country)
	{
		Local.locale = new Locale(language, country);
		Local.loadResources();
	}
	
	static
	{
		Local.initialize();
	}
	
	private static void initialize()
	{
	    Local.locale = Locale.getDefault();
	    Local.loadResources();
	}
	
	private static void loadResources()
	{
	    try
	    {
	        Local.resources = ResourceBundle.getBundle("org.postgeoolap.core.i18n.Resources", new Locale("pt"));
	    }
	    catch (MissingResourceException e)
	    {
	        log.error("Language resource file not found: " + e.getMessage(), e);
	        System.exit(0);
	    }
	}
	
	public static Locale getLocale()
	{
		return Local.locale;
	}
	
	public static String getString(String key)
	{
		return Local.resources.getString(key);
	}
	
	public static Object getObject(String key)
	{
		return Local.resources.getObject(key);
	}
}