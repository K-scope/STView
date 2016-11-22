package stview.action.filtering;

import java.util.ArrayList;

import stview.node.Dummy;
import stview.node.STVNode;

/**
 * Filtering Action class.
 */
public class FilterAction{
	
	/**
	 * Run Filtering action.
	 * @param match A regular expression for filtering
	 */
	public static void filtering(String match, ArrayList<STVNode> block){
		if(block == null || block.isEmpty()) return;
		ArrayList<String> compList = new ArrayList<String>();
		
		for(STVNode node : block){
			if(!match.isEmpty())
				if(!compList.contains(node.getName()))
					filteringCheck(node, match, block, compList);
		}
	}
	
	/**
	 * Confirmed whether or not to match the regular expression. 
	 * @param parent A parent node
	 * @param match A regular expression for filtering
	 * @return True :: matching
	 */
	private static boolean filteringCheck(STVNode parent, String match, ArrayList<STVNode> block, ArrayList<String> compList){
		if(parent == null) return false;
		parent.setFlag(false);
		
		for(int i=0;i<parent.getChildCount();i++){
			STVNode child = (STVNode)parent.getChildAt(i);
			if(child instanceof Dummy){
				//dummy
				STVNode node = null;
				for(STVNode n : block)
					if(n.getName().equals(parent.getName())){
						node = n;
						break;
					}
				if(node == null) break;
				if(!compList.contains(parent.getName())){
					compList.add(node.getName());
					filteringCheck(node, match, block, compList);
				}
				boolean f = node.getFlag();
				child.setFlag(f);
				parent.setFlag(f);
			}else{
				if(filteringCheck(child, match, block, compList))
					parent.setFlag(true);
			}
		}
		if(parent.getState().matches(match))
			parent.setFlag(true);
		return parent.getFlag();
	}
	
	/**
	 * Run Remove action.
	 * @param match A regular expression for remove
	 * @param op options
	 */
	public static void cutting(String match, ArrayList<STVNode> block){
		if(block == null || block.isEmpty()) return;
		for(STVNode node : block){
			cuttingCheck(node,match);
		}
	}
	
	/**
	 * Confirmed whether or not to match the regular expression. 
	 * @param parent A parent node
	 * @param match A regular expression for filtering
	 */
	private static void cuttingCheck(STVNode parent, String match){
		if(parent == null) return;
		parent.setFlag(!parent.getState().matches(match));
		
		for(int i=0;i<parent.getChildCount();i++)
			cuttingCheck((STVNode)parent.getChildAt(i),match);
	}
}
