package org.postgeoolap.core.util;

import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.JOptionPane;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.postgeoolap.core.i18n.Local;
import org.postgeoolap.core.orm.HibernateUtils;

public class Utils 
{
	private static Log log = null;
	
	public static final void init()
	{
		configureLog4JProperties();
		verifyVideo();
		HibernateUtils.getSessionFactory();
	}
	
	private static void verifyVideo()
	{
		int width = Toolkit.getDefaultToolkit().getScreenSize().width;
		int height = Toolkit.getDefaultToolkit().getScreenSize().height;
		
		if (width < 1024 || height < 768)
		{
			String msg = MessageFormat.format(Local.getString("message.video_resolution_too_low"),
				width, height); 
			log.info(msg);
			JOptionPane.showMessageDialog(null, msg, Local.getString("title.video_resolution"), 
				JOptionPane.INFORMATION_MESSAGE);
			System.exit(0);
		}
		else
			log.info(MessageFormat.format(Local.getString("message.video_resolution_ok"),
				width, height));
	}
	
	private static void configureLog4JProperties()
	{
		String fileName = getPostGeoOlapDirectory() + "conf"  + 
			File.separatorChar + "log4j.properties";
		
		try
		{
			BufferedReader reader = new BufferedReader(new FileReader(fileName));
			String line = null;
			StringBuilder contents = new StringBuilder();
			while ((line = reader.readLine()) != null)
			{
				if (line.trim().startsWith("log4j.appender.FileLog.File"))
				{
					contents.append("log4j.appender.FileLog.File=../logs/");
					Calendar calendar = GregorianCalendar.getInstance();
					calendar.setTimeInMillis(System.currentTimeMillis());
					contents.append(calendar.get(Calendar.YEAR));
					contents.append("-");
					contents.append(pad(calendar.get(Calendar.MONTH) + 1, 2));
					contents.append("-");
					contents.append(pad(calendar.get(Calendar.DAY_OF_MONTH), 2));
					contents.append(".log");
				}
				else
					contents.append(line);
				contents.append(System.getProperty("line.separator"));
			}
			reader.close();
			
			new File(fileName).delete();
			
			BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
			writer.append(contents);
			writer.close();
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace(System.err);
		}
		catch (IOException e)
		{
			e.printStackTrace(System.err);
		}
		
		log = LogFactory.getLog(Utils.class);
	}
	
	/**
	 * 
	 * @param source
	 * @param ch
	 * @param size
	 * @param orientation -1 for left, 0 for center, 1 for right
	 * @return
	 */
	public static String pad(String source, char ch, int size, int orientation)
	{
		StringBuilder builder = new StringBuilder();
		
		if (orientation == -1)
		{
			for (int i = 0; i < size - source.length(); i++)
				builder.append(ch);
			builder.append(source);
		}
		else if (orientation == 0)
		{
			for (int i = 0; i < (size - source.length()) / 2; i++)
				builder.append(ch);
			builder.append(source);
			for (int i = 0; i < (size - source.length()) / 2; i++)
				builder.append(ch);
			if ((size - source.length()) % 2 != 0)
				builder.append(ch);
		}
		else
		{
			builder.append(source);
			for (int i = 0; i < size - source.length(); i++)
				builder.append(ch);
		}
		return builder.toString();
	}
	
	public static String pad(int number, int size)
	{
		return pad(Integer.toString(number), '0', size, -1);
	}
	
	public static String getPostGeoOlapDirectory()
    {
        File file = new File(".");
        String s = file.getAbsolutePath();
        
        int flag = 0, i;
        for (i = s.length() - 1; i >= 0; i--)
        {
            if (s.charAt(i) == File.separatorChar)
                flag++;
            if (flag == 2)
                break;
        }
        return s.substring(0, i+1);
    }
	
	public static File loadFile(String path)
	{
		try
		{
			URL url = ClassLoader.getSystemClassLoader().getResource(path);
			if (url != null)
				log.info(MessageFormat.format(Local.getString("message.file_loaded"),
					path));
			return new File(url.toURI());
		}
		catch (URISyntaxException e)
		{
			log.error(e.getMessage(), e);
			return null;
		}
	}
	
	public static String readContent(File file)
	{
		StringBuilder contents = new StringBuilder();
		BufferedReader input = null;
		try
		{
			input = new BufferedReader(new FileReader(file));
			String line = null;
			while ((line = input.readLine()) != null)
			{
				contents.append(line);
				contents.append(System.getProperty("line.separator"));
			}
		}
		catch (FileNotFoundException e)
		{
			log.error(e.getMessage(), e);
		}
		catch (IOException e)
		{
			log.error(e.getMessage(), e);
		}
		finally
		{
			try
			{
				if (input != null)
					input.close();
			}
			catch (IOException e)
			{
				log.error(e.getMessage(), e);
			}
		}
		
		return contents.toString();
	}
	
	public static <T> List<T> sortByStringRepresentation(Collection<T> collection)
	{
		List<T> list = new ArrayList<T>();
		Map<String, T> map = new TreeMap<String, T>();
		for (T t: collection)
			map.put(t.toString(), t);
		for (T t: map.values())
			list.add(t);
		return list;
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T[] revertArray(T[] source)
	{
		for (int i = 0; i < source.length / 2; i++)
			swapElement(source, i, source.length - 1 - i);
		return source;
	}
	
	public static void swapElement(Object[] array, int index1, int index2)
	{
		Object aux = array[index1];
		array[index1] = array[index2];
		array[index2] = aux;
	}
}
