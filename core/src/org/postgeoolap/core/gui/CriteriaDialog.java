package org.postgeoolap.core.gui;

import goitaca.dnd.DataRetrieval;
import goitaca.dnd.ObjectTransferHandler;
import goitaca.utils.SwingUtils;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.ListSelectionModel;
import javax.swing.TransferHandler;

import org.postgeoolap.core.gui.auxiliary.CriterionListModel;
import org.postgeoolap.core.gui.auxiliary.OkCancelDialog;
import org.postgeoolap.core.i18n.Local;
import org.postgeoolap.core.model.Attribute;
import org.postgeoolap.core.model.Equality;
import org.postgeoolap.core.model.analysis.Criterion;
import org.postgeoolap.core.model.analysis.LogicalOperator;
import org.postgeoolap.core.model.analysis.Operator;
import org.postgeoolap.core.model.exception.ModelException;
import org.postgeoolap.core.util.Utils;

public class CriteriaDialog extends OkCancelDialog 
{
	private static final long serialVersionUID = -4383451163512885966L;
	
	private JRadioButton and;
	private JRadioButton or;
	private JRadioButton orNot;
	private JRadioButton andNot;
	private ButtonGroup group;
	
	private Map<ButtonModel, JRadioButton> buttonMap;
	private List<Criterion> criterionList;
	
	private JComboBox operator;
	private JList criteria;
	private JList addedCriteria;
	
	private Attribute attribute;
	
	public CriteriaDialog(Attribute attribute) 
	{
		super(MessageFormat.format(
			Local.getString("title.specify_criterion"), attribute.getName()));
		this.attribute = attribute;
		this.criterionList = new ArrayList<Criterion>();
		this.init();
		this.build();
		this.pack();
		SwingUtils.centralize(this);
	}
	
	private void init()
	{
		and = new JRadioButton(LogicalOperator.AND.toString());
		or = new JRadioButton(LogicalOperator.OR.toString());
		andNot = new JRadioButton(LogicalOperator.AND_NOT.toString());
		orNot = new JRadioButton(LogicalOperator.OR_NOT.toString());
		group = new ButtonGroup();
		group.add(and);
		group.add(or);
		group.add(andNot);
		group.add(orNot);
		
		buttonMap = new HashMap<ButtonModel, JRadioButton>();
		buttonMap.put(and.getModel(), and);
		buttonMap.put(or.getModel(), or);
		buttonMap.put(andNot.getModel(), andNot);
		buttonMap.put(orNot.getModel(), orNot);
		
		operator = new JComboBox(
			new Operator[] {
				Operator.EQUAL, Operator.DIFFERENT, Operator.MAJOR, Operator.MINOR, 
   			    Operator.MAJOR_OR_EQUAL, Operator.MINOR_OR_EQUAL, Operator.LIKE }
		);
		
		criteria = new JList(new DefaultListModel());
		criteria.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		
		addedCriteria = new JList();
		addedCriteria.setModel(new CriterionListModel());
		addedCriteria.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
				
		criteria.addMouseListener(
			new MouseAdapter()
			{
				@Override
				public void mouseClicked(MouseEvent e) 
				{
					if (e.getClickCount() == 2)
					{
						if (importCriteria())
							exportCriteria();
					}
				}
			}
		);
		
		addedCriteria.addKeyListener(
			new KeyAdapter()
			{
				@Override
				public void keyPressed(KeyEvent e) 
				{
					if (addedCriteria.getSelectedIndices().length == 0)
						return;
					if(e.getKeyCode() == KeyEvent.VK_DELETE && 
						JOptionPane.showConfirmDialog(null, 
							addedCriteria.getSelectedIndices().length > 1 ?
								Local.getString("question.delete_selected_criteria") :
								Local.getString("question.delete_selected_criterion"), 
							Local.getString("title.criteria"),
							JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == 
								JOptionPane.YES_OPTION)
					{
						unImportCriteria();
						unExportCriteria();
					}
				}
			}
		);
		
		this.fillCriteria();
		
		/* drag n' drop */
		criteria.setDragEnabled(true);
		addedCriteria.setDragEnabled(true);
		 
		ObjectTransferHandler transferHandler = new ObjectTransferHandler(
			TransferHandler.MOVE);
		transferHandler.addImportAction(addedCriteria,
			new ActionListener()
			{
				public void actionPerformed(ActionEvent e) 
				{
					if (!importCriteria())
						throw new RuntimeException();
				}
			}
		);
		
		transferHandler.addExportAction(criteria,
			new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					exportCriteria();
				}
			}
		);
		
		transferHandler.addRetrievalAction(criteria,
			new DataRetrieval()
			{
				public Object getData() 
				{
					return criteria.getSelectedValues();
				}

				public JComponent getSource() 
				{
					return criteria;
				}
			}
		);
		
		transferHandler.addImportAction(criteria,
			new ActionListener()
			{
				public void actionPerformed(ActionEvent e) 
				{
					unImportCriteria();
				}
			}
		);
		
		transferHandler.addExportAction(addedCriteria,
			new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					unExportCriteria();
				}
			}
		);
		
		transferHandler.addRetrievalAction(addedCriteria,
			new DataRetrieval()
			{
				public Object getData() 
				{
					return addedCriteria.getSelectedValues();
				}

				public JComponent getSource() 
				{
					return addedCriteria;
				}
			}
		);
		
		SwingUtils.installDragAndDrop(criteria, transferHandler, TransferHandler.MOVE);
		SwingUtils.installDragAndDrop(addedCriteria, transferHandler, TransferHandler.MOVE);
	}
	
	private void fillCriteria()
	{
		try
		{
			Collection<Equality> equalities = 
				Utils.sortByStringRepresentation(attribute.getInstanceSet());
			for (Equality equality: equalities)
				((DefaultListModel) criteria.getModel()).addElement(equality);
		}
		catch (ModelException e)
		{
			JOptionPane.showMessageDialog(null, e.getMessage(), Local.getString("title.error"),
				JOptionPane.ERROR_MESSAGE);
		}
	}
	
	private void build()
	{
		panel.setLayout(new GridBagLayout());
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setBorder(BorderFactory.createTitledBorder(
			Local.getString("label.logical_operator_relating_to_preexisting_clause")));
		buttonPanel.setLayout(new GridLayout(1, 4));
		buttonPanel.add(and);
		buttonPanel.add(or);
		buttonPanel.add(andNot);
		buttonPanel.add(orNot);
		buttonPanel.addKeyListener(
			new KeyAdapter()
			{
				@Override
				public void keyPressed(KeyEvent e) 
				{
					if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
					{
						Enumeration<AbstractButton> buttons = group.getElements();
						while (buttons.hasMoreElements())
							buttons.nextElement().getModel().setSelected(false);
					}
				}
			}
		);
		
		int y = -1;
		
		SwingUtils.addGridBagComponent(panel, buttonPanel, 0, ++y, 2, 1, 
			GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, 
			new Insets(10, 10, 3, 10));
		
		SwingUtils.addGridBagLabelTextField(panel, 
			new JLabel(Local.getString("label.comparation_operator")),
			operator, 0, ++y, 1, 1, 1, GridBagConstraints.NONE, 
			new Insets(3, 10, 3, 10));
		
		SwingUtils.addGridBagComponent(panel, 
			new JLabel(Local.getString("label.criteria_for_query")), 0, ++y, 2, 1, 
			GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL);
		
		SwingUtils.addGridBagComponent(panel, 
			SwingUtils.scrollComponent(criteria, 250),	0, ++y, 2, 1, 
			GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL);
		
		SwingUtils.addGridBagComponent(panel, 
			SwingUtils.scrollComponent(addedCriteria, 400, 150),	0, ++y, 2, 1, 
			GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, 
			new Insets(3, 10, 10, 10));
	}

	@SuppressWarnings("unchecked")
	@Override
	public void okAction(ActionEvent e) 
	{
		this.criterionList.addAll(((CriterionListModel) addedCriteria.getModel()).elements());
	}
	
	public List<Criterion> getCriteria()
	{
		return this.criterionList;
	}

	@Override
	public void cancelAction(ActionEvent e) 
	{
	}
	
	private boolean importCriteria()
	{
		int[] selected = criteria.getSelectedIndices();
		
		// checks relational operator
		if (operator.getSelectedItem() == null)
	    {
			JOptionPane.showMessageDialog(null, 
				Local.getString("message.criteria_must_have_relational_operator"),
				Local.getString("title.error"), JOptionPane.ERROR_MESSAGE);
			return false;
		}
		
		// checks logical operator if this is not the first criterion
		if (group.getSelection() == null && addedCriteria.getModel().getSize() > 0)
		{
			JOptionPane.showMessageDialog(null, 
				Local.getString("message.criteria_must_have_logical_operator"),
				Local.getString("title.error"), JOptionPane.ERROR_MESSAGE);
			return false;
		}
		
		for (int i = selected.length - 1; i >= 0; i--)
		{
			Criterion criterion = 
				new Criterion((Equality) criteria.getSelectedValues()[i], 
					(Operator) operator.getSelectedItem(),
					group.getSelection() != null && addedCriteria.getModel().getSize() > 0 ?
					LogicalOperator.get(
						buttonMap.get(group.getSelection()).getText()) : null);
			((CriterionListModel) addedCriteria.getModel()).add(criterion);
		}
		return true;
	}
	
	private void exportCriteria()
	{
		int[] selected = criteria.getSelectedIndices();
		
		for (int i = selected.length - 1; i >= 0; i--)
			((DefaultListModel) criteria.getModel()).remove(selected[i]);
	}
	
	private void unImportCriteria()
	{
		List<Equality> equalities = new ArrayList<Equality>();
		for (int i = 0; i < criteria.getModel().getSize(); i++)
			equalities.add((Equality) criteria.getModel().getElementAt(i));
		((DefaultListModel) criteria.getModel()).removeAllElements();
		
		int[] selected = addedCriteria.getSelectedIndices();
		for (int i = selected.length - 1; i >= 0; i--)
		{
			Object element = ((CriterionListModel) addedCriteria.getModel()).getElementAt(selected[i]); 
			Criterion criterion = (Criterion) element;
			equalities.add(criterion.getEquality());
		}
		
		for (Equality equality: Utils.sortByStringRepresentation(equalities))
			((DefaultListModel) criteria.getModel()).addElement(equality);
	}
	
	private void unExportCriteria()
	{
		int[] selected = addedCriteria.getSelectedIndices();
		for (int i = selected.length - 1; i >= 0; i--)
			((CriterionListModel) addedCriteria.getModel()).remove(selected[i]);
	}

}