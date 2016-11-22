package stview.node;

import java.util.ArrayList;

import org.eclipse.core.resources.IMarker;
import org.eclipse.photran.internal.core.parser.ASTSubroutineParNode;
import org.eclipse.photran.internal.core.parser.ASTSubroutineStmtNode;
import org.eclipse.photran.internal.core.parser.ASTSubroutineSubprogramNode;

/**
 * Subroutine class.
 */
public class Subroutine extends STVNode{	
	private static final long serialVersionUID = 4137723188567950525L;
	protected IMarker marker;
	protected ArrayList<String> argList;
	protected int count;
	
	/**
	 * The constructor.
	 * @param ast A Photran-AST node
	 */
	public Subroutine(ASTSubroutineSubprogramNode ast){
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
		parseArg(ast.getSubroutineStmt());
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
	private void parseArg(ASTSubroutineStmtNode ast){
		if(ast == null || ast.getSubroutinePars() == null) return;
		argList = new ArrayList<String>();
		for(ASTSubroutineParNode node : ast.getSubroutinePars())
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
	

