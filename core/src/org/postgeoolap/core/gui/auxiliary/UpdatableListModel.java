package org.postgeoolap.core.gui.auxiliary;

import javax.swing.DefaultListModel;

@SuppressWarnings("serial")
public class UpdatableListModel extends DefaultListModel 
{
	public void update()
	{
		this.fireContentsChanged(this, 0, this.getSize() - 1);
	}
}
