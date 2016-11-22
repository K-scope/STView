package stview.node;

import java.util.ArrayList;

import org.eclipse.photran.internal.core.parser.ASTCallStmtNode;
import org.eclipse.photran.internal.core.parser.ASTSubroutineArgNode;
	
/**
 * SubroutineCall class.
 */
public class SubroutineCall extends STVNode{
	private static final long serialVersionUID = -5203659672217303857L;
	protected ArrayList<String> argList;
	
	/**
	 * The constructor.
	 * @param ast A Photran-AST node
	 */
	public SubroutineCall(ASTCallStmtNode ast){
		//1
		setName(ast.getSubroutineName().getText().toLowerCase());
						
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

	@Override
	public String getState(){	
		return "call "+getName();
	}

	/**
	 * Create the argument list.
	 * @param ast A Photran-AST node
	 */
	private void parseArg(ASTCallStmtNode ast){
		if(ast == null || ast.getArgList() == null) return;
		argList = new ArrayList<String>();
		for(ASTSubroutineArgNode node : ast.getArgList())
				argList.add( node.getExpr().toString().trim().toLowerCase());
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
