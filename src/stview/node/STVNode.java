package stview.node;

import java.util.ArrayList;

import javax.swing.tree.DefaultMutableTreeNode;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.photran.internal.core.parser.ASTBinaryExprNode;
import org.eclipse.photran.internal.core.parser.ASTDblConstNode;
import org.eclipse.photran.internal.core.parser.ASTIntConstNode;
import org.eclipse.photran.internal.core.parser.ASTLogicalConstNode;
import org.eclipse.photran.internal.core.parser.ASTNestedExprNode;
import org.eclipse.photran.internal.core.parser.ASTNode;
import org.eclipse.photran.internal.core.parser.ASTRealConstNode;
import org.eclipse.photran.internal.core.parser.ASTSectionSubscriptNode;
import org.eclipse.photran.internal.core.parser.ASTStringConstNode;
import org.eclipse.photran.internal.core.parser.ASTUnaryExprNode;
import org.eclipse.photran.internal.core.parser.ASTVarOrFnRefNode;
import org.eclipse.photran.internal.core.parser.IExpr;

/**
 * The basic class of Tree Node.
 */
public abstract class STVNode extends DefaultMutableTreeNode implements ISTVNode {
	private static final long serialVersionUID = 1141331455304248577L;
	protected String name;
	protected int startOffset; 
	protected int endOffset;
	protected IFile file;
	protected boolean flag;
	
	/**
	 * The constructor.
	 */
	public STVNode(){
		//1 setName(String n);
		//2 setStartLine(int s);
		//3 setEndLine(int e);	
		//4 setFile(IFile f);
		//5 
		setFlag(true);
	}
	
	public String getName(){
		return name;
	}
	public String getState(){
		return getName();
	}
	public int getStartOffset(){
		return startOffset;
	}
	public int getEndOffset(){
		return endOffset;
	}	
	public IFile getFile(){
		return file;
	}
	public boolean getFlag(){
		return flag;
	}
	
	public void setName(String n){
		name = n;
	}
	public void setStartOffset(int s){
		startOffset = s;
	}
	public void setEndOffset(int e){
		endOffset = e;
	}
	public void setFile(IFile f){
		file = f;
	}
	public void setFile(ASTNode ast){
		setFile(ast.findFirstToken().getLogicalFile());
		
		if(ast.findFirstToken().getPreprocessorDirective() !=null){
			String n = ast.findFirstToken().getPreprocessorDirective().toString().trim();
			if(n.toLowerCase().startsWith("include")){
				int s = n.indexOf("'")>0 ? n.indexOf("'") : n.indexOf('"');
				int e = n.lastIndexOf("'")>0 ? n.lastIndexOf("'") : n.lastIndexOf('"');
				file = file.getWorkspace().getRoot().getFile(getFile().getFullPath().removeLastSegments(1).append(n.substring(s+1,e)));
			}
		}
	}
	public void setFlag(boolean f){
		flag = f;
	}
	
	/**
	 * Return the maker for high-light.
	 * @return The marker
	 */
	public IMarker getMarker(){
		IMarker marker = null;
		try {
			marker = file.createMarker("org.eclipse.core.resources.textmarker");
			int startOffset = getStartOffset();
			int endOffset = getEndOffset();
			marker.setAttribute("charStart", startOffset);
			marker.setAttribute("charEnd", endOffset);
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return marker;
	}
	
	/**
	 * Change all children to array, and returns it.
	 * @return The array of children nodes
	 */
	public STVNode[] getAllChildren(){
		ArrayList<STVNode> list = new ArrayList<STVNode>();
			int count = this.getChildCount();
           for (int i = 0; i < count; i++) {
        	   if(((STVNode)getChildAt(i)).getFlag())
        		   list.add((STVNode)getChildAt(i));
        }
       if (list.size() <= 0)
            return null;

        return list.toArray(new STVNode[0]);
	}
	
	public int getDisplayIndex(STVNode node){
		int index = -1;
		STVNode[] clist = this.getAllChildren();
		for(int i=0; i<clist.length; i++){
			if(clist[i].getFlag()){
				index++;
				if(clist[i].equals(node))
					break;
			}
		}
		return index;
	}
	
	/**
	 * Return the string of expression.
	 * @param expr A Photran-AST node
	 * @return the string of expression
	 */
	protected String getExpr(IExpr expr){
		if(expr == null) return "";
		if(expr instanceof ASTBinaryExprNode){
			return getExpr(((ASTBinaryExprNode)expr).getLhsExpr())
					+((ASTBinaryExprNode)expr).getOperator().findFirstToken().getText()
					+getExpr(((ASTBinaryExprNode)expr).getRhsExpr());
		}else if(expr instanceof ASTNestedExprNode){
			return "("+getExpr(((ASTNestedExprNode)expr).getExpr())+")";
		}else if(expr instanceof ASTUnaryExprNode){
			if(((ASTUnaryExprNode)expr).getOperator()!=null)
				return ((ASTUnaryExprNode)expr).getOperator().findFirstToken().getText()
						+getExpr(((ASTUnaryExprNode)expr).getOperand());
			else
				return ((ASTUnaryExprNode)expr).getSign().findFirstToken()
						+getExpr(((ASTUnaryExprNode)expr).getOperand());
		}
		else if(expr instanceof ASTVarOrFnRefNode){
			String param = "";
			if(((ASTVarOrFnRefNode)expr).getPrimarySectionSubscriptList() != null){
				param = "(";
				for(ASTSectionSubscriptNode nn : ((ASTVarOrFnRefNode)expr).getPrimarySectionSubscriptList()){
					param += getExpr(nn.getExpr())+",";
				}
				param = param.substring(0, param.length()-1)+")";
			}
			return ((ASTVarOrFnRefNode)expr).findFirstToken().getText().toLowerCase()+param;
		}else if(expr instanceof ASTDblConstNode){
			return ((ASTDblConstNode)expr).findFirstToken().getText().toLowerCase();
		}
		else if(expr instanceof ASTIntConstNode){
			return ((ASTIntConstNode)expr).findFirstToken().getText().toLowerCase();
		}else if(expr instanceof ASTLogicalConstNode){
			return ((ASTLogicalConstNode)expr).findFirstToken().getText().toLowerCase();
		}
		else if(expr instanceof ASTRealConstNode){
			return ((ASTRealConstNode)expr).findFirstToken().getText().toLowerCase();
		}else if(expr instanceof ASTStringConstNode){
			return ((ASTStringConstNode)expr).findFirstToken().getText().toLowerCase();
		}
		return "";
	}
}
