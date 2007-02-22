package org.postgeoolap.core.gui;

import goitaca.utils.SwingUtils;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.postgeoolap.core.CoreManager;
import org.postgeoolap.core.gui.action.ActionManager;
import org.postgeoolap.core.gui.action.DimensionTreePopupMenuListener;
import org.postgeoolap.core.gui.auxiliary.DimensionRootNode;
import org.postgeoolap.core.gui.auxiliary.SchemaTreeCellRenderer;
import org.postgeoolap.core.i18n.Local;
import org.postgeoolap.core.model.Attribute;
import org.postgeoolap.core.model.Cube;
import org.postgeoolap.core.model.analysis.Criterion;

import com.vividsolutions.jump.workbench.ui.ErrorHandler;
import com.vividsolutions.jump.workbench.ui.GUIUtil;

public class DataAnalysisDialog extends JDialog implements ErrorHandler 
{
	private static final long serialVersionUID = 4102290899311248553L;
	
	private static final Log log = LogFactory.getLog(DataAnalysisDialog.class);
	
	private JTree dimensionTree;
	private JList criteriaList;
	private MapPanel mapPanel;
	private JTable olapTable;
	
	private Object chosen;
	
	private static final int WIDTH_1 = (int) Math.round(CoreManager.HORIZONTAL_RESOLUTION * .2);
	private static final int WIDTH_2 = CoreManager.HORIZONTAL_RESOLUTION - WIDTH_1;
	private static final int HEIGHT_1 = (int) Math.round(CoreManager.VERTICAL_RESOLUTION * .5);
	private static final int HEIGHT_2 = CoreManager.VERTICAL_RESOLUTION - HEIGHT_1;
	
	@SuppressWarnings("unused")
	private Cube cube;

	public DataAnalysisDialog(Cube cube)
	{
		super();
		this.cube = cube;
		this.setTitle(
			new StringBuilder(Local.getString("title.data_analysis"))
				.append(" - [")
				.append(cube.getName())
				.append("]")
				.toString());
		this.init();
		this.build();
		this.setModal(true);
		this.setBounds(0, 0, CoreManager.HORIZONTAL_RESOLUTION, CoreManager.VERTICAL_RESOLUTION);
	}
	
	private void init()
	{
		dimensionTree = new JTree();
		dimensionTree.setCellRenderer(new SchemaTreeCellRenderer());
		((DefaultTreeModel) dimensionTree.getModel()).setRoot(new DimensionRootNode(cube));
		dimensionTree.setRootVisible(false);
	
		criteriaList = new JList(new DefaultListModel());
		
		mapPanel = new MapPanel(this, 
			CoreManager.instance().getActiveSchema().getMaps());
		
		this.addWindowListener(
			new WindowAdapter()
			{
				// Adding a layer to a LayerManager before LayerViewPanel is visible
                // raises an NonInvertibleTransformException because LVP tries to do
                // a zoom, but his height is zero, resulting in that exception.
				@Override
				public void windowOpened(WindowEvent e)
				{
					try
					{
						mapPanel.initJUMP();
					}
					catch (Throwable t)
					{
						handleThrowable(t);
					}
				}
			}
		);
		
		olapTable = new JTable();
		
		Map<ActionManager, JMenuItem> map = new HashMap<ActionManager, JMenuItem>();
		JMenuItem defineCriterion = new JMenuItem(Local.getString("command.define_criterion"));		
		defineCriterion.addActionListener(
			new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					defineCriterion();
				}
			}
		);
		map.put(ActionManager.DEFINE_CRITERION, defineCriterion);
		
		final JPopupMenu popup = new JPopupMenu();
		popup.addPopupMenuListener(
			new DimensionTreePopupMenuListener(popup, dimensionTree, map));
		
		
		dimensionTree.addMouseListener(
			new MouseAdapter()
			{
				@Override
				public void mouseClicked(MouseEvent e) 
				{
					dimensionTree.setSelectionRow(dimensionTree.getRowForLocation(e.getX(), e.getY()));
					if (e.getButton() == MouseEvent.BUTTON3)
						popup.show(dimensionTree, e.getX(), e.getY());
					chosen = dimensionTree.getLastSelectedPathComponent() != null ?
						((DefaultMutableTreeNode) dimensionTree.getLastSelectedPathComponent()).getUserObject() :
						null;
				}
			}
		);
	}
	
	private void build()
	{
		JScrollPane treeScroll = SwingUtils.scrollComponent(dimensionTree, HEIGHT_1);
		treeScroll.setBounds(0, 0, WIDTH_1, HEIGHT_1);
		treeScroll.setBorder(BorderFactory.createTitledBorder(
			Local.getString("label.dimensions")));
		JScrollPane criteriaScroll = SwingUtils.scrollComponent(criteriaList, HEIGHT_2);
		criteriaScroll.setBounds(0, HEIGHT_1, WIDTH_1, HEIGHT_2);
		criteriaScroll.setBorder(BorderFactory.createTitledBorder(
			Local.getString("label.criteria")));
		mapPanel.setBounds(WIDTH_1, 0, WIDTH_2, HEIGHT_1);
		JScrollPane tableScroll = SwingUtils.scrollComponent(olapTable, HEIGHT_2);
		tableScroll.setBounds(WIDTH_1, HEIGHT_1, WIDTH_2, HEIGHT_2);
		
		Container container = this.getContentPane();
		container.add(treeScroll);
		container.add(criteriaScroll);
		container.add(mapPanel);
		container.add(tableScroll);
	}

	/* ErrorHandler */
	public void handleThrowable(Throwable t) 
	{
		log.error(t);
		GUIUtil.handleThrowable(t, this);
		JOptionPane.showMessageDialog(null, t.getMessage(), Local.getString("title.error"),
			JOptionPane.ERROR_MESSAGE);
		
	}
	
	/* Events */
	private void defineCriterion()
	{
		CriteriaDialog dialog = new CriteriaDialog((Attribute) chosen);
		dialog.setVisible(true);
		if (dialog.isOk())
			for (Criterion criterion: dialog.getCriteria())
				((DefaultListModel) criteriaList.getModel()).addElement(criterion);
	}
}
