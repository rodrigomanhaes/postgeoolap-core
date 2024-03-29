package org.postgeoolap.core.metadata;

import java.io.File;

import org.postgeoolap.core.util.Utils;

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
	
	String createTable()
	{
		if (createTable == null)
			createTable = loadSQL("createtable");
		return createTable;
	}
	
	String dropTable()
	{
		if (dropTable == null)
			dropTable = loadSQL("droptable");
		return dropTable;
	}
	
	String loadSQL(String action)
	{
		File file = Utils.loadFile("org/postgeoolap/core/metadata/ddl/" +  
			this.name().toLowerCase() + "_" + action + ".sql");
		return Utils.readContent(file);
	}
}
