package gui;

import java.util.Collection;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import domainobjects.ExpenseFilter;
import domainobjects.IDSet;
import domainobjects.SetOperation;
import system.PFSystem;

import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;

public class MinedView implements IDialog 
{
	private Shell shell;
	
	private Table table;
	private Scale thresholdSlider;
	private Label tresholdLabel;
	
	private Collection<IDSet> frequentSets = null;
	
	private ExpenseFilter outputFilter = new ExpenseFilter();
	
	/**
	 * Open the window.
	 */
	public Object open() 
	{
		Display display = Display.getDefault();
		
		createContents();
		shell.open();
		shell.layout();
		
		while (!shell.isDisposed()) 
		{
			if (!display.readAndDispatch()) 
			{
				display.sleep();
			}
		}
		
		return outputFilter;
	}

	/**
	 * Create contents of the window.
	 * @wbp.parser.entryPoint
	 */
	protected void createContents() 
	{
		shell = new Shell();
		shell.setMinimumSize(new Point(800, 600));
		shell.setSize(837, 492);
		shell.setText("Mined Data View");
		shell.setLayout(null);
		
		Composite composite_1 = new Composite(shell, SWT.NONE);
		composite_1.setBounds(10, 420, 795, 80);
		composite_1.setLayout(null);
		
		thresholdSlider = new Scale(composite_1, SWT.NONE);
		thresholdSlider.setBounds(10, 10, 702, 54);
		thresholdSlider.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				tresholdLabel.setText(thresholdSlider.getSelection() + "%");
				refreshList();
			}
		});
		thresholdSlider.setMinimum(1);
		thresholdSlider.setSelection(50);
		
		tresholdLabel = new Label(composite_1, SWT.NONE);
		tresholdLabel.setBounds(718, 10, 67, 54);
		tresholdLabel.setAlignment(SWT.CENTER);
		tresholdLabel.setText(thresholdSlider.getSelection() + "%");
		
		Button btnCancel = new Button(shell, SWT.NONE);
		btnCancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				closeWithReturn(null);
			}
		});
		btnCancel.setBounds(10, 506, 94, 28);
		btnCancel.setText("Cancel");
		
		Button btnView = new Button(shell, SWT.NONE);
		btnView.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				closeWithReturn(generateFilterFromGUI());
			}
		});
		btnView.setBounds(711, 506, 94, 28);
		btnView.setText("View");
		
		table = new Table(shell, SWT.BORDER | SWT.FULL_SELECTION);
		table.setBounds(10, 10, 795, 404);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		refreshList();
	}
	
	private void refreshList()
	{
		final int scaleValue = thresholdSlider.getSelection();
		final double frequency = (double)scaleValue / 100.0;
		
		updateList(frequency);
	}
	
	private void updateList(double inFrequency)
	{
		frequentSets = PFSystem.getCurrent().getAllFrequentLabelCombinations(inFrequency);
		
		table.removeAll();
		
		for(IDSet set : frequentSets)
		{
			TableItem tableItem = new TableItem(table, SWT.NONE);
			
			// make sure that there are enough columns to satisfy the required number of items
			while(table.getColumnCount() < set.getSize())
			{
				TableColumn column = new TableColumn(table, SWT.NONE);
				column.setWidth(100);
				column.setText("" + table.getColumnCount()); 	
			}
			
			for(int i = 0; i < set.getSize(); i++)
			{	
				final int id = set.getValue(i);
				final domainobjects.Label label = (domainobjects.Label)PFSystem.getCurrent().getLabelSystem().getDataByID(id);
				
				assert(label != null);
			
				tableItem.setText(i, label.getLabelName());
			}
		}
	}
	
	private ExpenseFilter generateFilterFromGUI()
	{
		ExpenseFilter output = new ExpenseFilter();
		
		IDSet set = IDSet.empty();	// need to generate the set values
	
		if(frequentSets != null)
		{
			final int selectedIndex = table.getSelectionIndex();
			
			if(selectedIndex >= 0)
			{
				IDSet[] sets = new IDSet[frequentSets.size()];
				frequentSets.toArray(sets);
				
				set = sets[selectedIndex];
			}
		}
		
		output.assignLabels(set, SetOperation.INTERSECTION);
		
		return output;
	}
	
	private void closeWithReturn(ExpenseFilter filter)
	{		
		outputFilter = filter;
		shell.close();
	}
}
