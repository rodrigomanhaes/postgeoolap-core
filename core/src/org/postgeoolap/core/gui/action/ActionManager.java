package org.postgeoolap.core.gui.action;

import goitaca.action.ActionFactory;

import javax.swing.Action;


public enum ActionManager implements ActionFactory
{
	SCHEMA,
	METADATA,
	SELECT_SCHEMA,
	CREATE_CUBE,
	DEFINE_FACT_TABLE,
	ADD_DIMENSION,
	ADD_NON_AGGREGABLE_DIMENSION,
	CREATE_METADATA,
	DELETE_CUBES,
	PROCESS_CUBE,
	ANALYZE_CUBE,
	
	DEFINE_CRITERION;
	
	public Action getAction()
	{
		return new SelectSchemaAction();
	}
	
}
