package org.postgeoolap.core.gui.auxiliary;

import java.awt.Component;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.postgeoolap.core.model.Attribute;
import org.postgeoolap.core.model.Cube;
import org.postgeoolap.core.model.Dimension;
import org.postgeoolap.core.model.DimensionType;
import org.postgeoolap.core.model.Schema;
import org.postgeoolap.core.resources.ResourceBox;

public class SchemaTreeCellRenderer extends DefaultTreeCellRenderer 
{
	private static final long serialVersionUID = -4166163178735467221L;
	
	private static Map<Object, Icon> icons;
	
	static
	{
		icons = new HashMap<Object, Icon>();
		icons.put(Schema.class, ResourceBox.schemaIcon());
		icons.put(Attribute.class, ResourceBox.attributeIcon());
		icons.put(Cube.class, ResourceBox.cubeIcon());
		icons.put(DimensionType.FACT, ResourceBox.factIcon());
		icons.put(DimensionType.DIMENSION, ResourceBox.dimensionIcon());
		icons.put(DimensionType.NON_AGGREGABLE, ResourceBox.nonAggregableIcon());
	}
	
	public Component getTreeCellRendererComponent(JTree tree, Object value, 
		boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) 
	{
		super.getTreeCellRendererComponent(tree, value, selected,
			expanded, leaf, row, hasFocus);
		
		Object object = ((DefaultMutableTreeNode) value).getUserObject();
		if (object instanceof Dimension)
			this.setIcon(icons.get(((Dimension) object).getType()));
		else if (object instanceof Cube)
			this.setIcon(((Cube) object).wasProcessed() ? 
				ResourceBox.processedCubeIcon() : ResourceBox.cubeIcon());
		else if (object instanceof Attribute && ((Attribute) object).isGeographical())
			this.setIcon(ResourceBox.nonAggregableIcon());
		else
			this.setIcon(icons.get(object.getClass()));
		
		return this;
	}
}