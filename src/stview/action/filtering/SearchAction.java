package stview.action.filtering;

import java.util.ArrayList;

import org.eclipse.swt.widgets.TreeItem;

import stview.Controller;
import stview.node.Dummy;
import stview.node.STVNode;

/**
 * Search Action class.
 */
public class SearchAction {
	private Controller ctr;
	private ArrayList<String> list;
	private int index;
	
	/**
	 * The constructor.
	 * @param c　A controller
	 */
	public SearchAction(Controller c){
		ctr = c;
	}
	
	/**
	 * Run Search action.
	 * @param match　A regular expression for serching 
	 */
	public void search(String match){
		list = new ArrayList<String>();
		index = 0;
		searchCheck(ctr.getView().getCreateAction().getRootNode(),"0",match);
		
		if(!list.isEmpty()){
			TreeItem top = ctr.getView().getTreeViewer().getRootItem();
			String path = list.get(0);
			ctr.getView().getTreeViewer().expandForPathIndex(top, path);
		}
	}
	
	/**
	 * Confirmed whether or not to match the regular expression. 
	 * @param parent　A parent node
	 * @param path　A path to the node
	 */
	private void searchCheck(STVNode parent, String path, String match){
		if(parent.getState().matches(match))
			list.add(path);
		
		int index = -1;
		for(int i=0;i<parent.getChildCount();i++){
			STVNode child = (STVNode)parent.getChildAt(i);
			if(child instanceof Dummy){
				child = ctr.getView().getCreateAction().getTrueth(child);
			}
			if(child.getFlag()){
				index++;
				searchCheck(child,path+","+index,match);	
			}
		}
	}
	
	/**
	 * Return the next result index number.
	 * @return The index
	 */
	private int count(){
		index = (index+1)%list.size();
		return index;
	}
	
	/**
	 * Select the next result item.
	 */
	public void nextItem(){
		if(list == null) return;
		if(list.isEmpty()) return;
		
		int i = count();
		TreeItem top = ctr.getView().getTreeViewer().getRootItem();
		String path = list.get(i);
		ctr.getView().getTreeViewer().expandForPathIndex(top, path);
	}
}
