package org.postgeoolap.core.gui.auxiliary;

import javax.swing.tree.DefaultMutableTreeNode;

import org.postgeoolap.core.model.Attribute;
import org.postgeoolap.core.model.Cube;
import org.postgeoolap.core.model.Dimension;
import org.postgeoolap.core.model.Schema;
import org.postgeoolap.core.util.Utils;

public class SchemaRootNode extends DefaultMutableTreeNode 
{
	private static final long serialVersionUID = -7635782108063934113L;
	
	private Schema schema;
	
	public SchemaRootNode(Schema schema)
	{
		this.schema = schema;
		loadAll();
	}
	
	private void loadAll()
	{
		this.setUserObject(schema);
		
		for (Cube cube: Utils.sortByStringRepresentation(schema.getCubes()))
		{
			DefaultMutableTreeNode cubeNode = new DefaultMutableTreeNode(cube);
			this.add(cubeNode);
			for (Dimension dimension: Utils.sortByStringRepresentation(cube.getDimensions()))
			{
				DefaultMutableTreeNode dimensionNode = new DefaultMutableTreeNode(dimension);
				cubeNode.add(dimensionNode);
				for (Attribute attribute: Utils.sortByStringRepresentation(dimension.getAttributes()))
				{
					DefaultMutableTreeNode attributeNode = new DefaultMutableTreeNode(attribute);
					dimensionNode.add(attributeNode);
				}
			}
		}
	}
}
