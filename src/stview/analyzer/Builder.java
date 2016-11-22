package stview.analyzer;

import java.util.ArrayList;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import stview.Controller;
import stview.node.*;
import stview.preferences.PreferenceConstants;

/**
 * Create the structure tree.
 */
public class Builder extends Action implements ITreeContentProvider {
	
	private Controller ctr;
	private ArrayList<STVNode> treeList;
	private STVNode root;

	/**
	 * The constructor.
	 * @param c A controller
	 */
	public Builder(Controller c){
		ctr = c;
	}

	/**
	 * Run the action.
	 */	
	public void run(){
		treeList = new ArrayList<STVNode>();
		root = null;
		//create file list
		if(ctr.createFilelist()){
			ASTParser parser = new ASTParser(ctr);
			parser.analyze();
		}

		//create tree
		if(treeList.isEmpty()) return;
		changeTree();
	}

	/**
	 * Add a node to the block list.
	 * @param node An additional node
	 */		
	public void addBlock(STVNode node){
		if(node != null)
			treeList.add(node);
	}

	/**
	 * Return the node list of Procedures.
	 * @return The node list
	 */
	public ArrayList<STVNode> getBlock(){
		return treeList;
	}

	/**
	 * Return the mainProgram node.
	 * @return The MainProgramNode
	 */
	public MainProgram getMainProgram(){
		for(STVNode b : treeList)
			if(b instanceof MainProgram)
				return (MainProgram)b;
		return null;
	}
	
	/**
	 * Set a root node.
	 * @param node A STVNode as the root node
	 */
	public void setRootNode(STVNode node){
		root = node;
		resetFlag(node);
	}
	
	/**
	 * Return the root node.
	 * @return The root node
	 */
	public STVNode getRootNode(){
		return root;
	}
	
	/**
	 * Create the list of the ProgramUnit names.
	 * @return The list of the ProgramUnit names
	 */
	public ArrayList<String> getBlockListName(){
		ArrayList<String> list = new ArrayList<String>();
		if(treeList == null || treeList.isEmpty()) return list;
		for(STVNode b : treeList){
			if(b instanceof MainProgram)
				list.add(b.getState()+" --Program");
			else if(b instanceof Subroutine)
				list.add(b.getState()+" --Subroutine");
			else if(b instanceof Function)
				list.add(b.getState()+" --Function");
			else
				list.add(b.getState()+" --Others");
		}
		return list;
	}
	
	/**
	 * Change the tree.
	 */
	public void changeTree(){
		if(treeList == null || treeList.isEmpty()) return;
		if(root == null) root = getMainProgram();
		if(root == null) return;
		resetCall();
		
		if(PreferenceConstants.getFullMode()){ //fullMode
			for(STVNode node : treeList)
				for(int i = 0; i<node.getChildCount(); i++)
					dummySet((STVNode)node.getChildAt(i));
		}else{ //echoMode
			chain(root);
		}
		ctr.getView().getTreeViewer().resetTree(root);
	}
	
	/**
	 * Reset all call count.
	 */
	protected void resetCall(){
		for(STVNode node : treeList){
			if(node instanceof Subroutine) ((Subroutine)node).setCount(0);
			else if(node instanceof Function) ((Function)node).setCount(0);
		}
	}
	
	/**
	 * Reset the flag of the node as true.
	 * @param node Current node
	 */
	protected void resetFlag(STVNode node){
		if(node == null) return;
		node.setFlag(true);
		for(int i = 0; i<node.getChildCount(); i++)
			resetFlag((STVNode)node.getChildAt(i));
	}

	/**
	 * Chain the corresponding procedure.
	 * @param node Current node
	 */
	private void chain(STVNode node){
		if(node instanceof SubroutineCall){
			for(STVNode n : treeList)
			if(checkSubCall((SubroutineCall)node,n)){
				((Subroutine)n).setCount(((Subroutine)n).getCount()+1);
				if(((Subroutine)n).getCount()==1)
					node.add(n);
			}	
		}else if(node instanceof FunctionCall){
				for(STVNode n : treeList)
				if(checkFnCall((FunctionCall)node,n))	{
					((Function)n).setCount(((Function)n).getCount()+1);
					if(((Function)n).getCount()==1)
						node.add(n);
				}
		}
		for(int i = 0; i<node.getChildCount(); i++)
			chain((STVNode)node.getChildAt(i));
	}
	
	/**
	 * Set dummy nodes.
	 * @param node Current node
	 */
	private void dummySet(STVNode node){
		if(node instanceof SubroutineCall && node.getChildCount()==0){
			for(STVNode n : treeList)
				if(checkSubCall((SubroutineCall)node,n)){
					node.add(new Dummy());
				}
		}else if(node instanceof FunctionCall && node.getChildCount()==0){
			for(STVNode n : treeList)
				if(checkFnCall((FunctionCall)node,n)){
					node.add(new Dummy());
				}
		}else
			for(int i = 0; i<node.getChildCount(); i++)
				dummySet((STVNode)node.getChildAt(i));
	}
	
	/**
	 * Return the corresponding procedure.
	 * @param dummy Current node
	 * @return The Corresponding node
	 */
	public STVNode getTrueth(STVNode dummy){
		if(dummy instanceof Dummy){
			STVNode parent = (STVNode)dummy.getParent();
			if(parent instanceof SubroutineCall){
				for(STVNode node : treeList)
					if(checkSubCall((SubroutineCall)parent,node)){
						return node;
					}
			}else if(parent instanceof FunctionCall){
				for(STVNode node : treeList)
					if(checkFnCall((FunctionCall)parent,node)){
						return node;
					}
			}
		}
		return dummy;
	}
	
	/**
	 * Check whether the call and the subroutine are really agreement.
	 * @param parent A STVNode
	 * @param child A call
	 * @return True :: agreement
	 */
	public boolean checkSubCall(SubroutineCall parent, STVNode child){
		if(child.getName().equals(parent.getName()))
			if(child instanceof Subroutine)
				if(((Subroutine)child).getArgList().size() >= ((SubroutineCall)parent).getArgList().size())
					return true;
		return false;
	}
	
	/**
	 * Check whether the call and the function are really agreement.
	 * @param parent A STVNode
	 * @param child A call
	 * @return True :: agreement
	 */
	public boolean checkFnCall(FunctionCall parent, STVNode child){
		if(child.getName().equals(parent.getName()))
			if(child instanceof Function)
				if(((Function)child).getArgList().size() >= ((FunctionCall)parent).getArgList().size())
					return true;
		return false;
	}
	
	/** No using */
	public void inputChanged(Viewer v, Object oldInput, Object newInput) {}
	
	/** No using */
	public void dispose() {}
	
	/**
	 * Return the children.
	 * @return The children
	 */
	public Object[] getElements(Object inputElement) {
		return (STVNode[])getChildren(inputElement);
	}
	
	@Override
	public Object[] getChildren(Object parentElement) {
		STVNode[] children = ((STVNode)parentElement).getAllChildren();	
		if(children == null) return new STVNode[0];

		if(children[0] instanceof Dummy){
			STVNode parent = (STVNode)children[0].getParent();
			if(parent instanceof SubroutineCall){
				for(STVNode node : treeList)
					if(checkSubCall((SubroutineCall)parent,node)){
						if(!node.getFlag()) return new Object[0];
						children = new STVNode[1];
						children[0] = node;
						break;
					}
			}else if(parent instanceof FunctionCall){
				for(STVNode node : treeList)
					if(checkFnCall((FunctionCall)parent,node)){
						if(!node.getFlag()) return new Object[0];
						children = new STVNode[1];
						children[0] = node;
						break;
					}
			}
		}					
		return children==null ? new STVNode[0] : children;
	}
	
	@Override
	public Object getParent(Object element) {
		return ((STVNode)element).getParent();
	}
	
	@Override
	public boolean hasChildren(Object element) {
		return ((STVNode)element).getChildCount()==0 ? false : true;
	}
}
