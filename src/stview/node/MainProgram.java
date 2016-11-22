package stview.node;

import org.eclipse.core.resources.IMarker;
import org.eclipse.photran.internal.core.parser.ASTMainProgramNode;

/**
 * Program Unit class.
 */
public class MainProgram extends STVNode{
	private static final long serialVersionUID = 4459373976832948151L;
	protected IMarker marker;
	
	/**
	 * The constructor.
	 * @param ast A Photran-AST node
	 */
	public MainProgram(ASTMainProgramNode ast){
		//1
		setName(ast.getName());
		
		//2
		setStartOffset(ast.findFirstToken().getFileOffset());
		
		//3
		setEndOffset(ast.findLastToken().getFileOffset());
		
		//4
		setFile(ast);
		
		//5
		setFlag(true);
		
		marker = ast.createMarker();
	}
	
	@Override
	public String getName(){
		return name==null? "program" : name;
	}
	
	@Override
	public IMarker getMarker(){
		return marker;
	}
}
