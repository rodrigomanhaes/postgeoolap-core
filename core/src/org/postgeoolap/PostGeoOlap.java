package org.postgeoolap;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.UIManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.postgeoolap.core.gui.MainFrame;
import org.postgeoolap.core.orm.HibernateHelper;
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
			log.warn(e.getMessage(), e);
		}
		
		MainFrame frame = new MainFrame();
		frame.addWindowListener(
			new WindowAdapter()
			{
				@Override
				public void windowClosed(WindowEvent e) 
				{
					try
					{
						HibernateHelper.endUp();
					}
					catch (Exception exception)
					{
					}
				}
			}
		);
		frame.setVisible(true);
	}
	
}
