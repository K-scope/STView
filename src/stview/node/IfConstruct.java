package stview.node;

import org.eclipse.photran.internal.core.parser.ASTElseConstructNode;
import org.eclipse.photran.internal.core.parser.ASTElseIfConstructNode;
import org.eclipse.photran.internal.core.parser.ASTIfConstructNode;
import org.eclipse.photran.internal.core.parser.ASTIfStmtNode;

/**
 * If construct class.
 */
public class IfConstruct extends Condition{
	private static final long serialVersionUID = 2336125378671843396L;
	protected int type;
	public static final int IF = 0;
	public static final int IF_THEN = 2;
	public static final int ELSE_IF = 3;
	public static final int ELSE = 4;
	
	/**
	 * The constructor.
	 * @param ast A Photran-AST node
	 */
	public IfConstruct(ASTIfStmtNode ast){
		//1 
		setName("if");
				
		//2 
		setStartOffset(ast.findFirstToken().getFileOffset());
		
		//3
		setEndOffset(ast.findLastToken().getFileOffset());
				
		//4 
		setFile(ast);
				
		//5 
		setFlag(true);
				
		if(ast.getGuardingExpression() != null)
			setCondition(getExpr(ast.getGuardingExpression()));
		
		type = IF;
	}
	
	/**
	 * The constructor.
	 * @param ast A Photran-AST node
	 */
	public IfConstruct(ASTIfConstructNode ast){
		//1 
		setName("if then");
		
		//2 
		setStartOffset(ast.findFirstToken().getFileOffset());
		
		//3
		setEndOffset(ast.findLastToken().getFileOffset());
		
		//4 
		setFile(ast);
		
		//5 
		setFlag(true);
		
		if(ast.getIfThenStmt().getGuardingExpression() != null)
			setCondition(getExpr(ast.getIfThenStmt().getGuardingExpression()));

		type = IF_THEN;
	}
	
	/**
	 * The constructor.
	 * @param ast A Photran-AST node
	 */
	public IfConstruct(ASTElseIfConstructNode ast){
		//1 
		setName("else if");
		
		//2 
		setStartOffset(ast.findFirstToken().getFileOffset());
		
		//3
		setEndOffset(ast.findLastToken().getFileOffset());
		
		//4 
		setFile(ast);
		
		//5 
		setFlag(true);
		
		if(ast.getElseIfStmt().getGuardingExpression() != null)
			setCondition(getExpr(ast.getElseIfStmt().getGuardingExpression()));
		
		type = ELSE_IF;
	}
	
	/**
	 * The constructor.
	 * @param ast A Photran-AST node
	 */
	public IfConstruct(ASTElseConstructNode ast){
		//1 
		setName("else");
		
		//2 
		setStartOffset(ast.findFirstToken().getFileOffset());
		
		//3
		setEndOffset(ast.findLastToken().getFileOffset());
		
		//4 
		setFile(ast);
		
		//5 
		setFlag(true);
		
		type = ELSE;
	}
	
	@Override
	public String getState(){
		String str = getName();
		if(type<4){
			str += "(";
			str += getCondition();
			str += ")";
		}
		return str;
	}
	
	/**
	 * Return the type of this node.
	 * @return The type
	 */
	public int getType(){
		return type;
	}
}


