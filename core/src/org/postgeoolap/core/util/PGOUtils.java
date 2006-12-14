package org.postgeoolap.core.util;

import java.io.File;

public class PGOUtils {
	
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

}
