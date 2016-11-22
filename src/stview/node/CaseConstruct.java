package stview.node;

import org.eclipse.photran.internal.core.parser.ASTCaseConstructNode;
	
/**
 * Case construct class.
 */
public class CaseConstruct extends Condition{
	private static final long serialVersionUID = -8277159634249170922L;

	/**
	 * The constructor.
	 * @param ast A Photran-AST node
	 */
	public CaseConstruct(ASTCaseConstructNode ast){
		//1 
		if(ast.getSelectCaseStmt().getSelectionExpression()==null)
			setName("case");
		else
			setName("select("+ast.getSelectCaseStmt().getSelectionExpression().toString()+")");
				
		//2 
		setStartOffset(ast.findFirstToken().getFileOffset());
		
		//3
		setEndOffset(ast.findLastToken().getFileOffset());
				
		//4 
		setFile(ast);
				
		//5 
		setFlag(true);
	}
}
