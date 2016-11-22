package stview.action.filtering;

import java.util.ArrayList;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * Dialog class of Filtering and so on.
 */
public class ActionDialog extends Dialog{
	private static ActionDialog instance = null;
	private CTabFolder tabf;
	private String text;
	private Text srText;
	private Text flText;
	private Text rmText;
	private Combo trCombo;
	protected ArrayList<String> list;
	public static final int SR = 10;
	public static final int FL = 20;
	public static final int RM = 30;
	public static final int ROOT = 40;

	/**
	 * The constructor.
	 * @param parentShell A shell
	 */
	public ActionDialog(Shell parentShell) {
		super(parentShell);
	}

	/**
	 * Create instance.
	 * @return This object
	 */
	public static ActionDialog getInstance(){
		if(instance == null){
			Shell shell = Display.getCurrent() == null ? 
					new Shell(Display.getDefault()):new Shell(Display.getCurrent(), SWT.NO_TRIM );
					instance = new ActionDialog(shell);
		}
		return instance;
	}
	
	@Override
	protected Point getInitialSize() {
		return new Point(400, 300);
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite)super.createDialogArea(parent);
		composite.setLayout(new FillLayout());
		// install composites of SWT
		tabf = new CTabFolder(composite, SWT.NONE);
		tabSRCreate();
		tabFLCreate();
		tabRMCreate();
		tabRTCreate();
		return composite;
	}
	
	/**
	 * Create a search tab.
	 */
	private void tabSRCreate(){
		CTabItem tab1 = new CTabItem(tabf,SWT.NONE);
		Composite c1 = new Composite(tabf,SWT.NORMAL);
		tab1.setText("Search");
		tab1.setControl(c1);
		
		RowLayout layout = new RowLayout(SWT.VERTICAL);
		layout.marginLeft = 20;
		layout.marginTop = 20;
		c1.setLayout(layout);
		
		Label label = new Label(c1,SWT.NONE);
		label.setLayoutData(new RowData(300,20));
		label.setText("search (regular expression)");
		
		srText = new Text(c1,SWT.BORDER);
		srText.setLayoutData(new RowData(300,20));
	}
	
	/**
	 * Create a filter tab.
	 */
	private void tabFLCreate(){
		CTabItem tab1 = new CTabItem(tabf,SWT.NONE);
		Composite c1 = new Composite(tabf,SWT.NORMAL);
		tab1.setText("Filter");
		tab1.setControl(c1);
		
		RowLayout layout = new RowLayout(SWT.VERTICAL);
		layout.marginLeft = 20;
		layout.marginTop = 20;
		c1.setLayout(layout);
		
		Label label = new Label(c1,SWT.NONE);
		label.setLayoutData(new RowData(300,20));
		label.setText("filtering (regular expression)");
		
		flText = new Text(c1,SWT.BORDER);
		flText.setLayoutData(new RowData(300,20));
	}
	
	/**
	 * Create a remove tab.
	 */
	private void tabRMCreate(){
		CTabItem tab1 = new CTabItem(tabf,SWT.NONE);
		Composite c1 = new Composite(tabf,SWT.NORMAL);
		tab1.setText("Remove");
		tab1.setControl(c1);
		
		RowLayout layout = new RowLayout(SWT.VERTICAL);
		layout.marginLeft = 20;
		layout.marginTop = 20;
		c1.setLayout(layout);
		
		Label label = new Label(c1,SWT.NONE);
		label.setLayoutData(new RowData(300,20));
		label.setText("removing (regular expression)");
		
		rmText = new Text(c1,SWT.BORDER);
		rmText.setLayoutData(new RowData(300,20));
	}
	
	/**
	 * Create a tree change tab.
	 */
	private void tabRTCreate(){
		CTabItem tab = new CTabItem(tabf,SWT.NONE);
		Composite c1 = new Composite(tabf,SWT.NORMAL);
		tab.setText("Root");
		tab.setControl(c1);
		
		RowLayout layout = new RowLayout(SWT.VERTICAL);
		layout.marginLeft = 20;
		layout.marginTop = 20;
		c1.setLayout(layout);
	
		Composite c2 = new Composite(c1,SWT.NORMAL);
		c2.setLayout(new RowLayout(SWT.HORIZONTAL));
		
		Button p = new Button(c2,SWT.CHECK);
		p.setText("program");
		p.setSelection(true);
		p.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				changeList("Program",p.getSelection());
			}}
		);
		Button s = new Button(c2,SWT.CHECK);
		s.setText("subroutine");
		s.setSelection(true);
		s.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				changeList("Subroutine",s.getSelection());
			}}
		);
		Button f = new Button(c2,SWT.CHECK);
		f.setText("function");
		f.setSelection(true);
		f.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				changeList("Function",f.getSelection());
			}}
		);
		
		trCombo = new Combo(c1,SWT.DROP_DOWN | SWT.READ_ONLY);
		if(list == null || list.isEmpty()) return;
		for(int i=0; i<list.size(); i++)
			trCombo.add(list.get(i));
	}
	
	private void changeList(String type, boolean flag){
		if(flag){
			for(int i=0; i<list.size(); i++)
				if(list.get(i).endsWith(type))
					trCombo.add(list.get(i));
		}else{
			for(int i=trCombo.getItemCount()-1; i>=0; i--)
				if(trCombo.getItem(i).endsWith(type))
					trCombo.remove(i);
		}
	}
	
	/**
	 * Set the ProgramUnit names as the root node of the candidate.
	 * @param l A list of the ProgramUnit names
	 */
	public void setRootList(ArrayList<String> l){
		list = l;
	}
	
	/**
	 * Return the string of regular expression which the user input.
	 * @return the string of regular expression
	 */
	public String getText(){
		return text;
	}
	
	@Override
	protected void okPressed(){
		CTabItem item = tabf.getSelection();
		if(item.getText().equals("Search")){
			text = srText.getText();
			setReturnCode(SR);
		}else if(item.getText().equals("Filter")){
			text = flText.getText();
			setReturnCode(FL);
		}else if(item.getText().equals("Remove")){
			text = rmText.getText();
			setReturnCode(RM);
		}else if(item.getText().equals("Root")){
			text = String.valueOf(list.indexOf(trCombo.getText()));
			setReturnCode(ROOT);
		}else
			setReturnCode(CANCEL);
		close();
	}
}