package org.postgeoolap.core.gui.action;

import javax.swing.Action;

import org.goitaca.action.ActionFactory;

public enum ActionManager implements ActionFactory
{
	SCHEMA,
	METADATA,
	SELECT_SCHEMA,
	CREATE_CUBE,
	DEFINE_FACT_TABLE,
	ADD_DIMENSION,
	CREATE_METADATA,
	DELETE_CUBES,
	PROCESS_CUBE;
	
	public Action getAction()
	{
		return new SelectSchemaAction();
	}
	
}
