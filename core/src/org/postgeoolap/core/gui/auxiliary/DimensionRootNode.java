package org.postgeoolap.core.gui.auxiliary;

import java.util.Collection;

import javax.swing.JOptionPane;
import javax.swing.tree.DefaultMutableTreeNode;

import org.postgeoolap.core.i18n.Local;
import org.postgeoolap.core.model.Attribute;
import org.postgeoolap.core.model.Cube;
import org.postgeoolap.core.model.Dimension;
import org.postgeoolap.core.model.DimensionType;
import org.postgeoolap.core.model.exception.ModelException;
import org.postgeoolap.core.util.Utils;

public class DimensionRootNode extends DefaultMutableTreeNode 
{
	private static final long serialVersionUID = 935431951776764919L;
	
	private Cube cube;
	
	public DimensionRootNode(Cube cube)
	{
		super();
		this.cube = cube;
		this.loadAll();
	}
	
	private void loadAll()
	{
		this.setUserObject("dimensions");

		try
		{
			for (Dimension dimension: cube.getDimensions())
			{
				DefaultMutableTreeNode dimensionNode = new DefaultMutableTreeNode(dimension);
				this.add(dimensionNode);
				Collection<Attribute> collection = dimension.isType(DimensionType.FACT) ?
					Utils.sortByStringRepresentation(dimension.getAttributes()) :
					dimension.getHierarchy();
				for (Attribute attribute: collection)
				{
					DefaultMutableTreeNode attributeNode = new DefaultMutableTreeNode(attribute);
					dimensionNode.add(attributeNode);
				}
			}
		}
		catch (ModelException e)
		{
			JOptionPane.showMessageDialog(null, e.getMessage(), Local.getString("title.error"),
				JOptionPane.ERROR_MESSAGE);
		}
	}
}
