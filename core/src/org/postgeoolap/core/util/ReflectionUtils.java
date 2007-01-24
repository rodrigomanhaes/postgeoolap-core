package org.postgeoolap.core.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ReflectionUtils
{
    public static Object getProperty(Object object, String property)
        throws NoSuchFieldException 
    {
        try
        {
            return invokeMethod(getGetMethodName(property), object, 
                new Class[] { getPropertyType(object, property) }, new Object[]{});
        }
        catch (Exception e)
        {
            Class clazz = object.getClass();
        
            Field field = getField(clazz, property);
            field.setAccessible(true);
            try
            {
            	return field.get(object);
            }
            catch(IllegalAccessException iae)
            {
            	// como a acessibilidade do campo e true, esta excecao nao deve ocorrer
            	throw new IllegalArgumentException(e);
            }
        }
    }
    
    public static void setProperty(Object object, String property, Object value)
        throws NoSuchFieldException, IllegalAccessException, InvocationTargetException 
    {
        try
        {
            invokeMethodWithExceptions(getSetMethodName(property), object, 
                new Class[] {getPropertyType(object, property)},new Object[]{ value });
        }
        catch (NoSuchMethodException e)
        {
            Class clazz = object.getClass();
            
            Field field = getField(clazz, property);
            field.setAccessible(true);
            
            field.set(object, value);
        }
    }
    
    public static Object getNestedProperty(Object object, String property)
        throws NoSuchFieldException, IllegalAccessException
    {
        while (property.indexOf('.') != -1) 
        {
            int i = property.indexOf('.');
            String prop = property.substring(0, i); 
            property = property.substring(i + 1);
            
            if (object == null)
                return null;
            object = ReflectionUtils.getProperty(object, prop);
        }
        return ReflectionUtils.getProperty(object, property);
    }
    
    public static void setNestedProperty(Object object, String property, Object value, boolean autoCreate)
        throws NoSuchFieldException, IllegalAccessException, InstantiationException, NoSuchMethodException,
        	InvocationTargetException
    { 
        while (property.indexOf('.') != -1) 
        {
            int i = property.indexOf('.');
            String prop = property.substring(0, i); 
            property = property.substring(i + 1);
            
            Object newObject = ReflectionUtils.getProperty(object, prop);
            if (newObject == null && autoCreate)
            {
                newObject = getPropertyType(object, prop).newInstance();
                setProperty(object, prop, newObject);
            }
            object = newObject;
                
        }
        ReflectionUtils.setProperty(object, property, value);
    }
    
    public static void setNestedProperty(Object object, String property, Object value)
        throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException,
         	InvocationTargetException
    { 
        try
        {
            setNestedProperty(object, property, value, false);
        }
        catch (InstantiationException e)
        {
            // never happens if autoCreate parameter is false in setNestedProperty 
        }
    }
    
    public static Class getPropertyType(Object object, String property)
        throws NoSuchFieldException, NoSuchMethodException
    {
        Class clazz = object.getClass();
        Field field = null;
        try
        {
            field = getNestedField(clazz, property);
            return field.getType();
        }
        catch (NoSuchFieldException e)
        {
            Method method = getMethod(clazz, getGetMethodName(property), (Class[]) null);
            return method.getReturnType();
        }
    }
    
    private static Field getNestedField(Class clazz, String fieldName)
        throws NoSuchFieldException
    {
        while (fieldName.indexOf('.') != -1) 
        {
            int i = fieldName.indexOf('.');
            String prop = fieldName.substring(0, i); 
            fieldName = fieldName.substring(i + 1);
            
            clazz = ReflectionUtils.getField(clazz, prop).getType();
        }
        return ReflectionUtils.getField(clazz, fieldName);
    }
    
    public static Object invokeMethod(String methodName, Object target, Class[] classes, Object... parameters)
    {
        try
        {
            Method method = getMethod(target.getClass(), methodName, classes);
            method.setAccessible(true);
            return method.invoke(target, parameters);
        }
        catch (Exception e)
        {
             throw new IllegalArgumentException(e);
        }
    }
    
    public static Object invokeMethodWithExceptions(String methodName, Object target, Class[] classes, Object... parameters)
    	throws NoSuchMethodException, InvocationTargetException, IllegalAccessException
    {
        Method method = getMethod(target.getClass(), methodName, classes);
        method.setAccessible(true);
        return method.invoke(target, parameters);
   }
    
    @SuppressWarnings("unchecked")
	private static Method getMethod(Class clazz, String methodName, Class... params)
        throws NoSuchMethodException
    {
        Method method = null;
        try
        {
            method = clazz.getMethod(methodName, params);
        }
        catch (NoSuchMethodException e)
        {
            method = clazz.getDeclaredMethod(methodName, params);
        }
        return method;
    }
    
    private static Field getField(Class clazz, String fieldName)
        throws NoSuchFieldException
    {
        Field field = null;
        try
        {
            field = clazz.getDeclaredField(fieldName);
        }
        catch (NoSuchFieldException nsfe1)
        {
            try
            {
                field = clazz.getField(fieldName);
            }
            catch (NoSuchFieldException nsfe2)
            {
                if (clazz.getSuperclass() != null)
                    field = ReflectionUtils.getField(clazz.getSuperclass(), fieldName);
                else
                    throw nsfe2;
            }
        }
        
        return field;
    }
    
    public static String getGetMethodName(String entity)
    {
        return "get" + toUpperFirstCharacter(entity);
    }
    
    public static String getSetMethodName(String entity)
    {
        return "set" + toUpperFirstCharacter(entity);
    }
    
    public static String toUpperFirstCharacter(String string)
    {
        String ret = string.substring(0, 1).toUpperCase();
        ret += string.substring(1, string.length());
        return ret;
    }
}