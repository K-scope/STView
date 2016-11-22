package stview.node;

import java.util.ArrayList;

import org.eclipse.core.resources.IMarker;
import org.eclipse.photran.internal.core.parser.ASTFunctionParNode;
import org.eclipse.photran.internal.core.parser.ASTFunctionStmtNode;
import org.eclipse.photran.internal.core.parser.ASTFunctionSubprogramNode;

/**
 * Function class.
 */
public class Function extends STVNode{
	private static final long serialVersionUID = 3006748854852777402L;
	protected IMarker marker;
	protected ArrayList<String> argList;
	protected String type;
	protected int count;
	
	/**
	 * The constructor.
	 * @param ast A Photran-AST node
	 */
	public Function(ASTFunctionSubprogramNode ast){
		//1
		setName(ast.getName().toLowerCase());
				
		//2
		setStartOffset(ast.findFirstToken().getFileOffset());
				
		//3
		setEndOffset(ast.findLastToken().getFileOffset());
				
		//4
		setFile(ast);
				
		//5
		setFlag(true);
				
		marker = ast.createMarker();
		parseArg(ast.getFunctionStmt());
		count = 0;
	}
	
	@Override
	public IMarker getMarker(){
		return marker;
	}
	
	/**
	 * Setter for count.
	 * @param a A count number
	 */
	public void setCount(int a){
		count = a;
	}
	
	/**
	 * Getter for count.
	 * @return The count
	 */
	public int getCount(){
		return count;
	}
	
	/**
	 * Create the argument list.
	 * @param ast A Photran-AST node
	 */
	private void parseArg(ASTFunctionStmtNode ast){
		if(ast == null || ast.getFunctionPars() == null) return;
		argList = new ArrayList<String>();
		for(ASTFunctionParNode node : ast.getFunctionPars())
			argList.add(node.getVariableName().getText());
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
