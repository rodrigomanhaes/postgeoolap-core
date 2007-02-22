package org.postgeoolap.core.gui;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.io.File;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.postgeoolap.core.i18n.Local;
import org.postgeoolap.core.model.Attribute;
import org.postgeoolap.core.model.Mapa;

import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import com.vividsolutions.jump.feature.AttributeType;
import com.vividsolutions.jump.feature.BasicFeature;
import com.vividsolutions.jump.feature.Feature;
import com.vividsolutions.jump.feature.FeatureCollection;
import com.vividsolutions.jump.feature.FeatureDataset;
import com.vividsolutions.jump.feature.FeatureSchema;
import com.vividsolutions.jump.io.DriverProperties;
import com.vividsolutions.jump.io.ShapefileReader;
import com.vividsolutions.jump.workbench.model.Category;
import com.vividsolutions.jump.workbench.model.Layer;
import com.vividsolutions.jump.workbench.model.LayerManager;
import com.vividsolutions.jump.workbench.model.LayerTreeModel;
import com.vividsolutions.jump.workbench.ui.ErrorHandler;
import com.vividsolutions.jump.workbench.ui.LayerViewPanel;
import com.vividsolutions.jump.workbench.ui.LayerViewPanelContext;
import com.vividsolutions.jump.workbench.ui.LayerViewPanelListener;
import com.vividsolutions.jump.workbench.ui.LayerViewPanelProxy;
import com.vividsolutions.jump.workbench.ui.TreeLayerNamePanel;
import com.vividsolutions.jump.workbench.ui.WorkbenchToolBar;
import com.vividsolutions.jump.workbench.ui.renderer.style.LabelStyle;
import com.vividsolutions.jump.workbench.ui.zoom.PanTool;
import com.vividsolutions.jump.workbench.ui.zoom.ZoomTool;

public class MapPanel extends JPanel implements LayerViewPanelContext 
{
	private static final long serialVersionUID = 6937253498247633412L;
	
	private static final Log log = LogFactory.getLog(MapPanel.class);
	
	private static final String LAYERS = Local.getString("title.layers");
	private static final String LABEL_ATTRIBUTE = "label";
	
	private ErrorHandler errorHandler;
	private WorkbenchToolBar toolBar;
	private JPanel toolBarPanel;
	private LayerManager layerManager;
	private LayerViewPanel layerViewPanel;
	private TreeLayerNamePanel layerNamePanel;
	private JLabel statusLabel;
	
	private Collection<Mapa> maps;
	private Map<String, Layer> layerMap = new HashMap<String, Layer>();
		
	public MapPanel(ErrorHandler errorHandler, Collection<Mapa> maps)
	{
		super();
		this.errorHandler = errorHandler;
		this.maps = maps;
		this.init();
		this.build();
	}
	
	private void init()
	{
		layerManager = new LayerManager();
		layerViewPanel = new LayerViewPanel(layerManager, this);
		layerNamePanel = new TreeLayerNamePanel(layerViewPanel, 
			new LayerTreeModel(layerViewPanel), 
			layerViewPanel.getRenderingManager(), new HashMap());
		
		toolBar = new WorkbenchToolBar(
			new LayerViewPanelProxy()
			{
				public LayerViewPanel getLayerViewPanel()
				{
					return layerViewPanel;
				}
			}
		);
		toolBar.setFloatable(false);
		
		layerViewPanel.addListener(
			new LayerViewPanelListener()
			{
				public void selectionChanged() 
				{
				}

				public void cursorPositionChanged(String x, String y) 
				{
					StringBuilder builder = new StringBuilder();
					builder.append("(");
					builder.append(x);
					builder.append(", ");
					builder.append(y);
					builder.append(")");
					MapPanel.this.setStatusMessage(builder.toString());
				}

				public void painted(Graphics graphics) 
				{
				}
			}
		);
		
		statusLabel = new JLabel();
		statusLabel.setText(" ");
	}
	
	private void build()
	{
		toolBar.setOrientation(SwingConstants.VERTICAL);
		toolBarPanel = new JPanel();
		toolBarPanel.setBorder(BorderFactory.createLoweredBevelBorder());
		toolBarPanel.add(toolBar);
		
		this.setLayout(new BorderLayout());
		
		JPanel statusPanel = new JPanel();
		statusPanel.add(statusLabel);
		/*layerViewPanel.setPreferredSize(new Dimension(500, 350));
		layerViewPanel.setSize(new Dimension(500, 350));*/
		
		this.add(this.toolBarPanel, BorderLayout.WEST);
		this.add(this.layerViewPanel, BorderLayout.CENTER);
		this.add(statusPanel, BorderLayout.SOUTH);
		
		this.setBorder(BorderFactory.createLoweredBevelBorder());
	}
	
	public void initJUMP() throws Exception
	{
		toolBar.addCursorTool(Local.getString("label.zoom_in_out"), new ZoomTool());
		toolBar.addCursorTool(Local.getString("label.pan"), new PanTool());
		//this.loadData();
	}
	
	@SuppressWarnings("unused")
	private void loadData() throws Exception
	{
		this.removeAllCategories(layerManager);
		
		for (Mapa map: maps)
		{
			layerManager.addLayer(LAYERS, 
				new File(map.getName()).getName(),
				new ShapefileReader().read(new DriverProperties(map.getName())));
		}
	}
	
	private void removeAllCategories(LayerManager manager)
	{
		// old-fashioned verbose for: JUMP doesn't use generics
		for (Iterator iterator = layerManager.getCategories().iterator(); iterator.hasNext(); )
		{
			Category category = (Category) iterator.next();
			layerManager.removeIfEmpty(category);
		}
	}
	
	public void repaintMap()
	{
		layerViewPanel.repaint();
	}
	
	public JPanel getNamePanel()
	{
		return layerNamePanel;
	}
	
	public void applyLabeling(String layerName, List<String> labels)
	{
		Layer layer = layerMap.get(layerName);
		FeatureCollection featureCollection = layer.getFeatureCollectionWrapper().getWrappee();
		// old-fashioned iteration: JUMP is Java 1.4!!!
		List features = featureCollection.getFeatures();
		for (int i = 0; i < features.size(); i++)
		{
			Feature feature = (Feature) features.get(i);
			feature.setAttribute(LABEL_ATTRIBUTE, labels.get(i));
		}
		this.setLabeling(layerName, true);
	}
	
	private void setLabeling(String layerName, boolean enabled)
	{
		Layer layer = layerMap.get(layerName);
		layer.getLabelStyle().setEnabled(!enabled);
		layer.getLabelStyle().setEnabled(enabled);
	}
	
	public void clearLabels()
	{
		layerMap.clear();
	}
	
	public void plotAttribute(Attribute attribute, List<String> geometries)
	{
		FeatureSchema featureSchema = new FeatureSchema();
		featureSchema.addAttribute(attribute.getName(), AttributeType.GEOMETRY);
		featureSchema.addAttribute(LABEL_ATTRIBUTE, AttributeType.STRING);
		FeatureCollection featureCollection = new FeatureDataset(featureSchema);

		String expGeometry = null;
		try
		{
			for (String geometry: geometries)
			{
				expGeometry = geometry;
				Feature feature = new BasicFeature(featureCollection.getFeatureSchema());
				feature.setAttribute(attribute.getName(), 
					new WKTReader().read(geometry));
				featureCollection.add(feature);
			}
		}
		catch (ParseException e)
		{
			log.error(MessageFormat.format(
				Local.getString("error.invalid_geometry"), expGeometry), e);
			JOptionPane.showMessageDialog(null, e.getMessage(), Local.getString("title.error"),
				JOptionPane.ERROR_MESSAGE);
		}
		
		Layer layer = layerManager.addLayer(LAYERS, attribute.getName(), 
			featureCollection);
		layerMap.put(attribute.getName(), layer);
		LabelStyle labelStyle = layerManager.getLayer(attribute.getName()).getLabelStyle();
		labelStyle.setAttribute(LABEL_ATTRIBUTE);
		labelStyle.setEnabled(true);
	}

	/* LayerViewPanelContext */
	public void setStatusMessage(String message) 
	{
		statusLabel.setText(
			message == null || message.length() == 0 ? " " : message);
	}

	public void warnUser(String warning) 
	{
		this.setStatusMessage(warning);
	}

	public void handleThrowable(Throwable throwable) 
	{
		errorHandler.handleThrowable(throwable);
	}
}