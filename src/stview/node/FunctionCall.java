package stview.node;

import java.util.ArrayList;

import org.eclipse.photran.internal.core.parser.ASTSectionSubscriptNode;
import org.eclipse.photran.internal.core.parser.ASTVarOrFnRefNode;
	
/**
 * FunctionCall class
 */
public class FunctionCall extends STVNode{
	private static final long serialVersionUID = -4304345797419131444L;	
	protected ArrayList<String> argList;

	/**
	 * The constructor.
	 * @param ast A Photran-AST node
	 */
	public FunctionCall(ASTVarOrFnRefNode ast){
		//1
		setName(ast.findFirstToken().getText().toLowerCase());
						
		//2
		setStartOffset(ast.findFirstToken().getFileOffset());
						
		//3
		setEndOffset(ast.findLastToken().getFileOffset());
						
		//4
		setFile(ast);
						
		//5
		setFlag(true);
		
		parseArg(ast);
	}
	
	/**
	 * Create the argument list.
	 * @param ast A Photran-AST node
	 */
	private void parseArg(ASTVarOrFnRefNode ast){
		if(ast == null || ast.getPrimarySectionSubscriptList() == null) return;
		argList = new ArrayList<String>();
		for(ASTSectionSubscriptNode node : ast.getPrimarySectionSubscriptList())
			if(node.getExpr() != null){
				String n = getExpr(node.getExpr());
				if(!n.equals("&") && !n.equals("&&"))
					argList.add(n);
			}
			else if(node.getSubscriptTriplet() != null)
				argList.add(node.getSubscriptTriplet().toString());
	}
	
	/**
	 * Getter for argList.
	 * @return The argList
	 */
	public ArrayList<String> getArgList(){
		if(argList == null) return new ArrayList<String>();
		return argList;
	}
}
