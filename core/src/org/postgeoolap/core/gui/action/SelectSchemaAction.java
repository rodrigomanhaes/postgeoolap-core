package org.postgeoolap.core.gui.action;

import goitaca.action.CommandAction;

import java.awt.event.ActionEvent;

import org.postgeoolap.core.CoreManager;
import org.postgeoolap.core.gui.SelectSchemaDialog;
import org.postgeoolap.core.i18n.Local;

public class SelectSchemaAction extends CommandAction 
{
	public SelectSchemaAction() 
	{
		super(Local.getString("command.select_schema"), null, null, 
			null, Local.getString("tip.select_schema"));
	}

	private static final long serialVersionUID = 8737308995793436345L;

	public void actionPerformed(ActionEvent e) 
	{
		SelectSchemaDialog dialog = new SelectSchemaDialog();
		dialog.setVisible(true);
		if (CoreManager.instance().getActiveSchema() != null)
		this.notifyListeners(e.getSource(), true, 
			CoreManager.instance().getActiveSchema());
	}

}
