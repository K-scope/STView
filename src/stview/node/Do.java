package stview.node;

import org.eclipse.photran.internal.core.analysis.loops.ASTProperLoopConstructNode;
import org.eclipse.photran.internal.core.parser.ASTLoopControlNode;
import org.eclipse.photran.internal.core.parser.IExpr;

/**
 * Do(While) Loop class.
 */
public class Do extends Loop{
	private static final long serialVersionUID = 4147602205612148069L;
	protected boolean isControl;
	protected boolean isDoWhile;
	protected String var;
	protected String lower;
	protected String upper;
	protected String step;

	/**
	 * The constructor.
	 * @param ast A Photran-AST node
	 */
	public Do(ASTProperLoopConstructNode ast){
		//1
		setName("do");
				
		//2
		setStartOffset(ast.findFirstToken().getFileOffset());
				
		//3
		setEndOffset(ast.findLastToken().getFileOffset());
				
		//4
		setFile(ast);
				
		//5
		setFlag(true);
		
		if(ast.getLoopHeader().getLoopControl() != null){
			isControl = true;
			isDoWhile = ast.isDoWhileLoop();
			setCondition(ast.getLoopHeader().getLoopControl());
		}else{
			isControl = false;
			isDoWhile = false;
		}
	}
	
	@Override
	public String getState(){
		String stat = "do ";
		
		if(isControl){
			if(isDoWhile){
				stat += "while ("+var+")";
			}else{
				stat += getVar()+"=";
				stat += getLower()+",";
				stat += getUpper()+",";
				stat += getStep();
			}
		}
		return stat;
	}
	
	/**
	 * Setting the loop condition.
	 * @param node A Photran-AST node
	 */
	protected void setCondition(ASTLoopControlNode node){
		if(isDoWhile){
			var = node.getWhileExpr().toString();
			return;
		}
		
		var = node.getVariableName().getText().toLowerCase();
		upper = getBoundary(node.getUb());
		lower = getBoundary(node.getLb());
		step = getBoundary(node.getStep());
	}
	
	/**
	 * Return the string from expr.
	 * @param ex An expr
	 * @return The string of expr
	 */
	private String getBoundary(IExpr ex){
		if (ex == null) 
			return String.valueOf(1);
		return getExpr(ex);
		
	}
	
	/**
	 * Getter for upper.
	 * @return The upper
	 */
	public String getUpper(){
		return upper;
	}
	
	/**
	 * Getter for lower.
	 * @return The lower
	 */
	public String getLower(){	
		return lower;
	}
	
	/**
	 * Getter for step.
	 * @return The step
	 */
	public String getStep(){
		return step;
	}
	
	/**
	 * Getter for var.
	 * @return The var
	 */
	public String getVar(){
		return var;
	}
	
}