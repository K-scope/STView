package stview.views;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.TreeEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

import stview.Controller;
import stview.analyzer.ViewLabelProvider;
import stview.node.*;

/**
 * Tree View class.
 */
public class StructureTree extends TreeViewer{
	private STVNode root;
	private Controller ctr;
	private Point point;

	/**
	 * The constructor.
	 * @param c A controller
	 * @param parent A composite
	 * @param style A style
	 */
	public StructureTree(Controller c, Composite parent, int style) {
		super(parent, style);
		this.expandToLevel(2);
		ctr = c;	
		root = new Dummy();
		Dummy first = new Dummy();
		root.add(first);
		
		getTree().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				point = new Point(e.x,e.y);
			}
		});
	}
	
	/**
	 * Return the tree item corresponding the click point.
	 * @return The selecting tree item
	 */
	public TreeItem getSelectTreeItem(){
		TreeItem select = (TreeItem)getItemAt(point);
		if(select == null) select = (TreeItem)getItemAt(new Point(point.x+20,point.y));
		return select;
	}

	/**
	 * Setting each provider.
	 */
	public void setProvider(){
		setContentProvider(ctr.getView().getCreateAction());
		setLabelProvider(new ViewLabelProvider());
		
		setInput(root);
	}

	/**
	 * Resetting the tree.
	 * @param child A new tree root
	 */
	public void resetTree(STVNode child){
		root.removeAllChildren();
		if(child == null)
			child = new Dummy();
		root.add(child);
		setInput(root);
		refresh();
	}
	
	/**
	 * Return the root node.
	 * @return the root node
	 */
	public STVNode getRoot(){
		return root;
	}
	
	/**
	 * Return the root item of the tree.
	 * @return root tree item
	 */
	public TreeItem getRootItem(){
		if(getTree() == null) return null;
		TreeItem item = getTree().getTopItem();
		while(item.getParentItem()!=null)
			item = item.getParentItem();
		
		return item;
	}
	
	/**
	 * Return the node corresponding now selecting tree item.
	 * @return the node corresponding now selecting
	 */
	public STVNode getSelectNode(){
		return (STVNode)((StructuredSelection) this.getSelection()).getFirstElement();
	}
		
	@Override
	protected void handleTreeExpand(TreeEvent event) {
		TreeItem select = (TreeItem)event.item;
		
		if(select == null) return;
		if(isRecursive(select)) return;
		super.handleTreeExpand(event);
	}
	
	/**
	 * Check recursive.
	 * @param treeitem check item
	 * @return True :: recursive
	 */
	protected boolean isRecursive(TreeItem treeitem){
		Object obj = treeitem.getData();
		if(obj instanceof SubroutineCall || obj instanceof FunctionCall){
			String name = treeitem.getText();
			TreeItem item = treeitem.getParentItem();			
			while(item != null){
				if(item.getText().equals(name)){
					treeitem.removeAll();
					return true;
				}
				item = item.getParentItem();
			}
		}
		return false;
	}
	
	/**
	 * Expand from the tree item.
	 * @param item A top tree item
	 */
	public void allExpand(TreeItem item){
		if(item == null)
			item = getRootItem();
		if(!isRecursive(item))
			setExpandedState(getTreePathFromItem(item), true);
		for(TreeItem t : item.getItems())
			allExpand(t);
	}
	
	/**
	 * Expand from now select tree item within ProgramUnit.
	 */
	public void expandFrom(){
		TreeItem select = getSelectTreeItem();
		if(!isRecursive(select))
			setExpandedState(getTreePathFromItem(select), true);
		for(TreeItem t : select.getItems())
			expandFromChild(t);
	}
	
	/**
	 * Expand from the tree item within ProgramUnit.
	 * @param item An expanding tree item
	 */
	public void expandFromChild(TreeItem item){
		Object obj = item.getData();
		if(obj instanceof SubroutineCall)
			return;
		if(obj instanceof FunctionCall)
			return;
		setExpandedState(getTreePathFromItem(item), true);
		for(TreeItem t : item.getItems())
			expandFromChild(t);
	}
	
	/**
	 * Expand the TreeItem corresponding the path.
	 * @param parent A top tree item
	 * @param path A number-path
	 */
	public void expandForPathIndex(TreeItem parent, String path){
		String[] pathIndex = path.split(",");
		TreeItem child = parent;

		if(pathIndex.length > 1){
		for(int i=1; i<pathIndex.length; i++){
			setExpandedState(getTreePathFromItem(child), true);
			child = child.getItem(Integer.valueOf(pathIndex[i]));
			}
		}
		getTree().setSelection(child);
	}

	/**
	 * Add Mouse actions.
	 */
	public void addAction(){
		addDoubleClickListener(new IDoubleClickListener() {
			@Override
			public void doubleClick(DoubleClickEvent event) {
				Object obj = ((StructuredSelection)getSelection()).getFirstElement();
				TreeItem select = getSelectTreeItem();
				if(!isRecursive(select))
					setExpandedState(getTreePathFromItem(select), true);
				
				if(obj instanceof STVNode){
					try {
						STVNode node = (STVNode)obj;
						if(node.getFile() == null){
							if(node.getName().equals("programStructure"))
								ctr.getView().getCreateAction().run();
						}else{
							IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
							IEditorPart editorPart = IDE.openEditor(page, node.getFile());
							IDE.gotoMarker(editorPart, node.getMarker());
						}
					} catch (CoreException e) {
						e.printStackTrace();
					}
				}
			}
		});
		/*addTreeListener(new ITreeViewerListener(){
			@Override
			public void treeCollapsed(TreeExpansionEvent event) {}
			@Override
			public void treeExpanded(TreeExpansionEvent event) {
				System.out.println("aaaaa");
				if(event.getElement() instanceof INode){
					INode select = (INode) event.getElement();
					StructureTree viewer = (StructureTree) event.getTreeViewer();
				}		
			}});*/
	}
}
