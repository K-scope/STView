package stview.node;

import org.eclipse.photran.internal.core.lexer.Token;
import org.eclipse.photran.internal.core.parser.ASTCaseStmtNode;
import org.eclipse.photran.internal.core.parser.ASTCaseValueRangeNode;

/**
 * Case stmt class.
 */
public class Case extends Condition{
	private static final long serialVersionUID = -3620601241408453359L;

	/**
	 * The constructor.
	 * @param ast A Photran-AST node
	 */
	public Case(ASTCaseStmtNode ast){
		//1
		setName("case");
		
		//2
		setStartOffset(ast.findFirstToken().getFileOffset());
						
		//3
		setEndOffset(ast.findLastToken().getFileOffset());
						
		//4
		setFile(ast);
						
		//5
		setFlag(true);
		
		if(ast.hasDefaultSelector())
			setCondition("default");
		else{
			String str = "";
			for(ASTCaseValueRangeNode node : ast.getCaseValueRangeListSelector())
				for(Token t : node.findAll(Token.class))
					str += t.getText().toLowerCase();
			setCondition(str);
		}
	}
	
	@Override
	public String getState(){
		return getName()+" "+getCondition();
	}
}
