package stview.profiler.dprofiler;

import java.io.File;
import java.util.ArrayList;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.part.ViewPart;

import stview.Activator;

/**
 * View for the profiler data.
 */
public class ProfilerView extends ViewPart {
	public static final String ID = "stview.profiler.ProfilerView";

	private Table table;
	private Action load;
	private ArrayList<DProfDataList> datalist = null;
	private int state = 0;
	private final int PROCEDURES = 1;
	private final int LOOPS = 2;
	private final int LINES = 3;
	private int proc;
	private int thread;
	
	/**
	 * The constructor.
	 */
	public ProfilerView() {
	}

	/**
	 * This is a callback that will allow us
	 * to create the viewer and initialize it.
	 */
	public void createPartControl(Composite parent) {
		table = new Table(parent,  SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		// Create the help context id for the viewer's control
		makeActions();
		contributeToActionBars();
	}
	
	/**
	 * Setting upside menu.
	 */
	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	/**
	 * Setting the right-upper-side menu.
	 * @param A manager
	 */
	private void fillLocalToolBar(IToolBarManager manager) {
		Action action1 = new Action() {
			public void run() {
				setProcedureTable();
			}
		};
		action1.setText("Procedures");
		action1.setToolTipText("Procedures Info");
		manager.add(action1);
		
		Action action2 = new Action() {
			public void run() {
				setLoopTable();
			}
		};
		action2.setText("Loops");
		action2.setToolTipText("Loops Info");
		manager.add(action2);
		
		Action action3 = new Action() {
			public void run() {
				setLineTable();
			}
		};
		action3.setText("Lines");
		action3.setToolTipText("Lines Info");
		manager.add(action3);
		
		manager.add(load);
	}
	
	/**
	 * Setting the right-triangle menu.
	 * @param A manager
	 */
	private void fillLocalPullDown(IMenuManager manager){
		manager.add(load);
	}
	
	/**
	 * Setting the right-triangle menu.
	 */
	private void fillLocalPullDown() {
		IMenuManager manager = getViewSite().getActionBars().getMenuManager();
		manager.removeAll();
		
		for(int i=0;i<datalist.size(); i++){
			int p = i;
			Action action = new Action() {
					public void run() {
						proc = p;
						thread = -1;
						setData();
					}
				};
			action.setText("Proc-"+datalist.get(i).procNo);
			manager.add(action);
			if(datalist.get(i).threadNum > 1){
				for(int j=0; j<datalist.get(i).threadNum; j++){
					int t = j;
					Action action2 = new Action() {
						public void run() {
							proc = p;
							thread = t;
							setData();
						}
					};
					action2.setText("Proc-"+datalist.get(i).procNo+" Thread-"+j);
					manager.add(action2);
				}
			}
		}
	}

	/**
	 * Make each action.
	 */
	private void makeActions() {
		load = new Action() {
			public void run() {
				Shell shell = Display.getCurrent() == null ? 
						new Shell(Display.getDefault()):new Shell(Display.getCurrent(), SWT.NO_TRIM);
				FileDialog dialog = new FileDialog(shell,SWT.OPEN | SWT.MULTI);

				if(dialog.open() != null){
					datalist = new ArrayList<DProfDataList>();
					DProfReader reader = new DProfReader();
					String path = dialog.getFilterPath();
					String[] names = dialog.getFileNames();
					for(int i=0; i<names.length; i++){
						try {
							DProfDataList data = reader.load(new File(path+"/"+names[i]));
							if(data != null) datalist.add(data);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					proc = 0; thread = -1;
					fillLocalPullDown();
					switch(state){
						case PROCEDURES :
							setProcedureTable();
							break;
						case LOOPS :
							setLoopTable();
							break;	
						case LINES :
							setLineTable();
							break;
						default : 
							setProcedureTable();
					}
				}
			}
		};
		load.setText("Load");
		load.setToolTipText("Load Profiler file");
		load.setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor("DMDLEditor.Load"));
	}

	/**
	 * Initialize the table.
	 * @param parent A parent composite
	 */
	private void initTable(Composite parent){
		if(table != null)
			for (Widget c : table.getParent().getChildren())
				c.dispose();
	
		table = new Table(parent,  SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
	}
	
	/**
	 * Set the procedure table.
	 */
	private void setProcedureTable(){
		//if(state == PROCEDURES) return;
		state = PROCEDURES;
		
		initTable(table.getParent());
		TableColumn col1 = new TableColumn(table, SWT.LEFT);
		col1.setText("Cost");
		col1.setWidth(100);

		TableColumn col2 = new TableColumn(table, SWT.LEFT);
		col2.setText("%");
		col2.setWidth(100);

		TableColumn col3 = new TableColumn(table, SWT.LEFT);
		col3.setText("Line");
		col3.setWidth(100);
		
		TableColumn col4 = new TableColumn(table, SWT.LEFT);
		col4.setText("Name");
		col4.setWidth(100);
		
		TableColumn col5 = new TableColumn(table, SWT.LEFT);
		col5.setText("File");
		int right = table.getParent().getSize().x-400;
		col5.setWidth(Math.max(100, right));
		
		setData();
		table.getParent().layout();
	}
	
	/**
	 * Set the loop table.
	 */
	private void setLoopTable(){
		//if(state == LOOPS) return;
		state = LOOPS;

		initTable(table.getParent());
		TableColumn col1 = new TableColumn(table, SWT.LEFT);
		col1.setText("Cost");
		col1.setWidth(100);

		TableColumn col2 = new TableColumn(table, SWT.LEFT);
		col2.setText("%");
		col2.setWidth(100);
		
		TableColumn col3 = new TableColumn(table, SWT.LEFT);
		col3.setText("Nest");
		col3.setWidth(100);

		TableColumn col4 = new TableColumn(table, SWT.LEFT);
		col4.setText("Kind");
		col4.setWidth(100);
		
		TableColumn col5 = new TableColumn(table, SWT.LEFT);
		col5.setText("Exec");
		col5.setWidth(100);
		
		TableColumn col6 = new TableColumn(table, SWT.LEFT);
		col6.setText("Line");
		col6.setWidth(100);
		
		TableColumn col7 = new TableColumn(table, SWT.LEFT);
		col7.setText("Name");
		col7.setWidth(100);
		
		TableColumn col8 = new TableColumn(table, SWT.LEFT);
		col8.setText("File");
		int right = table.getParent().getSize().x-700;
		col8.setWidth(Math.max(100, right));
		
		setData();
		table.getParent().layout();
	}

	/**
	 * Set the line table.
	 */
	private void setLineTable(){
		//if(state == LINES) return;
		state = LINES;

		initTable(table.getParent());
		TableColumn col1 = new TableColumn(table, SWT.LEFT);
		col1.setText("Cost");
		col1.setWidth(140);

		TableColumn col2 = new TableColumn(table, SWT.LEFT);
		col2.setText("%");
		col2.setWidth(100);
		
		TableColumn col3 = new TableColumn(table, SWT.LEFT);
		col3.setText("Line");
		col3.setWidth(100);

		TableColumn col4 = new TableColumn(table, SWT.LEFT);
		col4.setText("Name");
		col4.setWidth(100);
		
		TableColumn col5 = new TableColumn(table, SWT.LEFT);
		col5.setText("File");
		int right = table.getParent().getSize().x-400;
		col5.setWidth(Math.max(100, right));
		
		setData();
		table.getParent().layout();
	}

	/**
	 * Set the data.
	 */
	protected void setData(){
		if(datalist == null || datalist.isEmpty()) return;
		table.removeAll();
		if(datalist.size() <= proc) proc=0;
		if(datalist.get(proc).threadNum <= thread) thread = -1;
		float sum = 0;
		switch(state){
			case PROCEDURES :			
				ArrayList<String> symbollist = datalist.get(proc).getSymbolList(thread);
				for(int j = 0; j < symbollist.size(); j++)
					sum += Float.valueOf(symbollist.get(j).split(",")[1]);
				
				for(int j = 0; j < symbollist.size(); j++){
					String[] list = symbollist.get(j).split(",");
					TableItem item = new TableItem(table, SWT.NONE);
					item.setText(0, list[1]);
					item.setText(1, String.valueOf(Float.valueOf(list[1])/sum*100));
					item.setText(2, list[4].equals("-1")? "--":list[4]+":"+list[5]);
					item.setText(3, list[7]);
					if(!list[6].equals("-1")) 
						item.setText(4, datalist.get(proc).getFile(Integer.valueOf(list[6])));
				}
				break;
				
		case LOOPS :
			ArrayList<String> looplist = datalist.get(proc).getLoopList(thread);
			for(int j = 0; j < looplist.size(); j++)
				sum += Float.valueOf(looplist.get(j).split(",")[1]);
			
			for(int j = 0; j < looplist.size(); j++){
				String[] list = looplist.get(j).split(",");
				TableItem item = new TableItem(table, SWT.NONE);
				item.setText(0, list[1]);
				item.setText(1, String.valueOf(Float.valueOf(looplist.get(j).split(",")[1])/sum*100));
				item.setText(2, list[6].equals("32767")? "--":list[6]);
				item.setText(3, DProfDataList.getType(Integer.valueOf(list[7])));
				item.setText(4, DProfDataList.getParallel(Integer.valueOf(list[8])));
				item.setText(5, list[4]+":"+list[5]);
				item.setText(6, datalist.get(proc).getSymbol(Integer.valueOf(list[9])));
				if(!list[10].equals("-1")) 
					item.setText(7, datalist.get(proc).getFile(Integer.valueOf(list[10])));
			}
			break;	
			
		case LINES :
			ArrayList<String> linelist = datalist.get(proc).getLineList(thread);
			for(int j = 0; j < linelist.size(); j++)
				sum += Float.valueOf(linelist.get(j).split(",")[1]);
			
			for(int j = 0; j < linelist.size(); j++){
				String[] list = linelist.get(j).split(",");
				TableItem item = new TableItem(table, SWT.NONE);
				item.setText(0, list[1]);
				item.setText(1, String.valueOf(Float.valueOf(list[1])/sum*100));
				item.setText(2, list[4]);
				item.setText(3, datalist.get(proc).getSymbol(Integer.valueOf(list[5])));
				if(!list[6].equals("-1")) 
					item.setText(4, datalist.get(proc).getFile(Integer.valueOf(list[6])));
			}
			break;
		}
	}
	
	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
	}
}