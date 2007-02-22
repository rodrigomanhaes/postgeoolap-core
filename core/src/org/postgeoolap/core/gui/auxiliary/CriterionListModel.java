package org.postgeoolap.core.gui.auxiliary;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.AbstractListModel;

import org.postgeoolap.core.model.analysis.Criterion;

public class CriterionListModel extends AbstractListModel 
{
	private static final long serialVersionUID = 6365321066729219054L;
	
	private List<Criterion> criteria = new ArrayList<Criterion>();
	
	public CriterionListModel()
	{
	}

	public int getSize() 
	{
		return criteria.size();
	}

	public Object getElementAt(int index) 
	{
		return criteria.get(index);
	}
	
	public void add(Criterion criterion)
	{
		criteria.add(criterion);
		this.fireIntervalAdded(this, criteria.size() - 1, criteria.size() - 1);
	}
	
	public Criterion remove(int index)
	{
		Criterion criterion = criteria.remove(index);
		this.fireIntervalRemoved(this, index, index);
		return criterion;
	}
	
	public List<Criterion> elements()
	{
		return Collections.unmodifiableList(criteria);
	}
}
