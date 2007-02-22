package org.postgeoolap.core.gui;

import goitaca.event.TextComponentDontType;
import goitaca.factory.FormattedTextFactory;
import goitaca.utils.SwingUtils;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.text.MessageFormat;

import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import org.postgeoolap.core.gui.auxiliary.OkCancelDialog;
import org.postgeoolap.core.i18n.Local;
import org.postgeoolap.core.model.Cube;
import org.postgeoolap.core.model.Schema;
import org.postgeoolap.core.model.exception.ModelException;

@SuppressWarnings("serial")
public class CreateCubeDialog extends OkCancelDialog 
{
	private Schema schema;
	private Cube cube;
	
	private JTextField schemaName;
	private JTextField name;
	private JFormattedTextField minimumAggregation;
	
	public CreateCubeDialog(Schema schema) 
	{
		super(MessageFormat.format(Local.getString("title.create_cube"), schema.getName()));
		this.schema = schema;
		this.cube = new Cube();
		init();
		build();
		pack();
		this.setAutoClose(false);
		SwingUtils.centralize(this);
	}
	
	public CreateCubeDialog(Cube cube) 
	{
		super(MessageFormat.format(Local.getString("title.update_cube"), cube.getSchema().getName()));
		if (cube.wasProcessed())
		{
			JOptionPane.showMessageDialog(null, Local.getString("message.cant_update_processed_cube"));
			return;
		}
		
		this.schema = cube.getSchema();
		this.cube = cube;
		init();
		build();
		pack();
		this.setAutoClose(false);
		this.updateWidgets();
	}
	
	private void init()
	{
		schemaName = new JTextField(30);
		schemaName.setBackground(SystemColor.control);
		schemaName.addKeyListener(TextComponentDontType.instance());
		schemaName.setFocusable(false);
		schemaName.setText(schema.getName());
		name = new JTextField(30);
		minimumAggregation = FormattedTextFactory.getIntegerField(6);
	}
	
	private void build()
	{
		panel.setLayout(new GridBagLayout());
		
		int y = -1;
		SwingUtils.addGridBagLabelTextField(panel, 
			new JLabel(Local.getString("label.schema")),
			schemaName, 0, ++y, 1, 2, 1, GridBagConstraints.NONE, 
			new Insets(3, 3, 3, 3));
		SwingUtils.addGridBagLabelTextField(panel, 
			new JLabel(Local.getString("label.name")),
			name, 0, ++y, 1, 2, 1, GridBagConstraints.NONE);
		SwingUtils.addGridBagLabelTextField(panel, 
			new JLabel(Local.getString("label.minimum_aggregation")),
			minimumAggregation, 0, ++y, 2, 1, 1, GridBagConstraints.NONE);
		name.grabFocus();
	}
	
	@Override
	public void setVisible(boolean visible)
	{
		super.setVisible(visible);
	}

	@Override
	public void okAction(ActionEvent e) 
	{
		if ("".equals(minimumAggregation.getText()))
		{
			JOptionPane.showMessageDialog(null, MessageFormat.format(
				Local.getString("message.field_cant_be_empty"), 
				Local.getString("label.minimum_aggregation")));
			return;
		}
		
		if ("".equals(name.getText()))
		{
			JOptionPane.showMessageDialog(null, MessageFormat.format(
				Local.getString("message.field_cant_be_empty"), 
				Local.getString("label.name")));
			return;
		}

		String s = minimumAggregation.getText();
		StringBuilder sb = new StringBuilder();
		for (int i = s.length() - 1; i >= 0; i--)
			if (Character.isDigit(s.charAt(i)))
				sb.append(s.charAt(i));
		cube.setMinimumAggregation(new Long(sb.toString()));
		cube.setName(name.getText());
		schema.addCube(cube);
		try
		{
			cube.persist();
		}
		catch (ModelException exception)
		{
			JOptionPane.showMessageDialog(null, exception.getMessage(), 
				Local.getString("title.error"), JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		this.setVisible(false);
	}

	@Override
	public void cancelAction(ActionEvent e) 
	{
		this.setVisible(false);
	}
	
	private void updateWidgets()
	{
		name.setText(cube.getName());
		minimumAggregation.setText(new Long(cube.getMinimumAggregation()).toString());
	}
}