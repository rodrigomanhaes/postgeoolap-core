package org.postgeoolap.core.gui.auxiliary;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import javax.swing.filechooser.FileFilter;

import org.postgeoolap.core.i18n.Local;

public class MapFileFilter extends FileFilter 
{
	private Set<String> extensions;
	
	public MapFileFilter()
	{
		extensions = new HashSet<String>();
	}
	
	public void addExtension(String extension)
	{
		extensions.add(extension.toLowerCase());
	}
	
	@Override
	public boolean accept(File file) 
	{
		return file != null && (file.isDirectory() || extensions.contains(getExtension(file).toLowerCase()));
	}
	
	private String getExtension(File file)
	{
		if (file != null)
		{
			String name = file.getName();
			int i = name.lastIndexOf(".");
			if (i != -1 && i < name.length() - 1)
				return name.substring(i + 1).toLowerCase();
		}
		return "";
	}

	@Override
	public String getDescription() 
	{
		return Local.getString("label.map_files");
	}
	
	
}
