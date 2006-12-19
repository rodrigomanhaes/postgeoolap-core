package org.postgeoolap.core.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class PGOUtils 
{
	private static Log log = LogFactory.getLog(PGOUtils.class);
	
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
				log.info("File " + path + " loaded");
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
		StringBuffer contents = new StringBuffer();
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
}
