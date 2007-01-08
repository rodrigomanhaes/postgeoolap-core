package org.postgeoolap;

import javax.swing.UIManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.postgeoolap.core.gui.MainFrame;
import org.postgeoolap.core.util.Utils;

public class PostGeoOlap 
{
	private static final Log log = LogFactory.getLog(PostGeoOlap.class);
	
	public static void main(String[] args)
	{
		Utils.init();
		
		// System-dependent look-and-feel
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (Exception e)
		{
			log.error(e.getMessage(), e);
		}
		
		MainFrame frame = new MainFrame();
		frame.setVisible(true);
	}
	
}
