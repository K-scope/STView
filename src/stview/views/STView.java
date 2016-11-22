package stview.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.*;
import org.eclipse.jface.action.*;

import stview.Activator;
import stview.Controller;
import stview.MessageLog;
import stview.action.filtering.ActionDialog;
import stview.action.filtering.FilterAction;
import stview.action.filtering.SearchAction;
import stview.action.print.PrintViewer;
import stview.analyzer.Builder;

/**
 * STView class.
 */
public class STView extends ViewPart {
 
	/**
	 * The ID of the view as specified by the extension.
	 * http://www.ne.jp/asahi/hishidama/home/tech/eclipse/plugin/develop/index.html
	 * http://www.masatom.in/pukiwiki/Eclipse/%A5%D7%A5%E9%A5%B0%A5%A4%A5%F3%B3%AB%C8%AF%A4%CETIPS%BD%B8/
	 */
	public static final String ID = "stview.view.STView";
	private StructureTree viewer;
	private Builder createAction;
	private Action actions;
	private Action expand;
	private Action print;
	private SearchAction searchAction;

	public Controller ctr;

	/**
	 * The constructor.
	 */
	public STView() {
		ctr = new Controller(this);
	}

	/**
	 * This is a callback that will allow us
	 * to create the viewer and initialize it.
	 */
	public void createPartControl(Composite parent) {
		viewer = new StructureTree(ctr, parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);

		// Create the help context id for the viewer's control
		PlatformUI.getWorkbench().getHelpSystem().setHelp(viewer.getControl(), "STV.viewer");
		makeAction();		
		hookContextMenu();
		contributeToActionBars();
	}
	
	/**
	 * Make each action.
	 */
	private void makeAction(){
		createAction = new Builder(ctr);
		createAction.setText("Create Tree");
		createAction.setToolTipText("Create Tree"); //help text
		createAction.setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor("DMDLEditor.Create"));
		viewer.addAction();
		viewer.setProvider();
		
		expand = new Action() {
			public void run() {
				viewer.allExpand(null);
			}
		};
		expand.setText("Expand Tree");
		expand.setToolTipText("Expand Tree"); //help text
		expand.setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor("DMDLEditor.Expand"));
		
		searchAction = new SearchAction(ctr);
		actions = new Action() {
			public void run() {
				ActionDialog actionDialog = ActionDialog.getInstance();
				actionDialog.setRootList(createAction.getBlockListName());
				switch(actionDialog.open()){
					case (ActionDialog.SR) : 
						searchAction.search(actionDialog.getText());
						break;
					case (ActionDialog.FL) : 
						FilterAction.filtering(actionDialog.getText(),createAction.getBlock());
						createAction.changeTree();
						break;
					case (ActionDialog.RM) : 
						FilterAction.cutting(actionDialog.getText(),createAction.getBlock());
						createAction.changeTree();
						break;		
					case (ActionDialog.ROOT) : 
						if(!actionDialog.getText().equals("-1")){
							createAction.setRootNode(createAction.getBlock().get(Integer.valueOf(actionDialog.getText())));
							createAction.changeTree();
							}
						break;
				}
			}
		};
		actions.setText("Setting");
		actions.setToolTipText("Setting"); //help text
		actions.setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor("DMDLEditor.Setting"));
		
		print = new Action(){
			public void run(){
				PrintViewer.getInstance(ctr).open(null,ctr);
			}
		};
		print.setText("Print");
		print.setToolTipText("Print"); //help text
		print.setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor("DMDLEditor.Print"));
	}

	/**
	 * Setting menu.
	 */
	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				STView.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
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
	 * Setting the pop-up menu.
	 * @param A manager
	 */
	private void fillContextMenu(IMenuManager manager) {
		Action action4 = new Action(){
			public void run(){
				searchAction.nextItem();
			}
		};
		action4.setText(MessageLog.getString("popup.search.next"));
		manager.add(action4);
		
		Action action1 = new Action(){
			public void run(){
				viewer.expandFrom();
			}
		};
		action1.setText(MessageLog.getString("popup.expand"));
		manager.add(action1);
		
		Action action2 = new Action(){
			public void run(){
				PrintViewer.getInstance(ctr).open(viewer.getSelectNode(),ctr);
			}
		};
		action2.setText(MessageLog.getString("popup.print"));
		manager.add(action2);
		
		Action action3 = new Action(){
			public void run(){
				searchAction.search(viewer.getSelectNode().getState());
			}
		};
		action3.setText(MessageLog.getString("popup.search"));
		manager.add(action3);
		
		//manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	/**
	 * Setting the right-triangle menu.
	 * @param A manager
	 */
	private void fillLocalPullDown(IMenuManager manager) {
		manager.add(createAction);
		manager.add(expand);
		manager.add(new Separator());
		manager.add(actions);
		manager.add(print);
	}

	/**
	 * Setting the right-upper-side menu.
	 * @param A manager
	 */
	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(createAction);
		manager.add(expand);
		manager.add(actions);
		manager.add(print);
	}
	
	/**
	 * Return the Builder.
	 * @return The Builder
	 */
	public Builder getCreateAction(){
		return createAction;
	}
	
	/**
	 * Return the TreeView.
	 * @return The StructureTree.
	 */
	public StructureTree getTreeViewer(){
		return viewer;
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}
}