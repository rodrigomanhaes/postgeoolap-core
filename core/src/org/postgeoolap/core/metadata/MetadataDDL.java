package org.postgeoolap.core.metadata;

import java.io.File;

import org.postgeoolap.core.util.PGOUtils;

public enum MetadataDDL 
{
	AGGREGATION,
	AGGREGATIONITEM,
	ATTRIBUTE,
	CUBE,
	DIMENSION,
	MAP,
	ESQUEMA,
	CNSAGGREGATION;	 

	private String createTable;
	private String dropTable;
	
	public String createTable()
	{
		if (createTable == null)
			createTable = loadSQL("createtable");
		return createTable;
	}
	
	public String dropTable()
	{
		if (dropTable == null)
			dropTable = loadSQL("droptable");
		return dropTable;
	}
	
	private String loadSQL(String action)
	{
		File file = PGOUtils.loadFile("org/postgeoolap/core/metadata/ddl/" +  
			this.name().toLowerCase() + "_" + action + ".sql");
		return PGOUtils.readContent(file);
	}
}
