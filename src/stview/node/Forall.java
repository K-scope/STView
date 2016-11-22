package stview.node;

import java.util.ArrayList;

import org.eclipse.photran.internal.core.parser.ASTForallConstructNode;
import org.eclipse.photran.internal.core.parser.ASTForallConstructStmtNode;
import org.eclipse.photran.internal.core.parser.ASTForallStmtNode;
import org.eclipse.photran.internal.core.parser.ASTForallTripletSpecListNode;

/**
 * Forall Construct class.
 */
public class Forall extends Loop{
	private static final long serialVersionUID = 4022778282897327499L;
	protected int type;
	public static final int FORALL_STMT = 0;
	public static final int FORALL_CONSTRUCT = 1;
	
	ArrayList<String> condition = new ArrayList<String>();
	
	/**
	 * The constructor.
	 * @param ast A Photran-AST node
	 */
	public Forall(ASTForallStmtNode ast){
		//1
		setName("forall");
						
		//2
		setStartOffset(ast.findFirstToken().getFileOffset());
						
		//3
		setEndOffset(ast.findLastToken().getFileOffset());
						
		//4
		setFile(ast);
						
		//5
		setFlag(true);
		
		type = FORALL_STMT;
		if(ast.getForallTripletSpecList()!=null){
			for(ASTForallTripletSpecListNode list : ast.getForallTripletSpecList()){
				String tmp = list.toString().toLowerCase();
				if(tmp.startsWith(",")) tmp = tmp.substring(1);
				if(tmp.endsWith(",")) tmp = tmp.substring(0,tmp.length()-1);
				condition.add(tmp);
			}
		}
		if(ast.getScalarMaskExpr()!=null)
			condition.add(ast.getScalarMaskExpr().toString().toLowerCase());
	}
	
	/**
	 * The constructor.
	 * @param ast A Photran-AST node
	 */
	public Forall(ASTForallConstructNode ast){
		//1
		setName("forall");
						
		//2
		setStartOffset(ast.findFirstToken().getFileOffset());
						
		//3
		setEndOffset(ast.findLastToken().getFileOffset());
						
		//4
		setFile(ast);
						
		//5
		setFlag(true);
		
		type = FORALL_CONSTRUCT;
		ASTForallConstructStmtNode node = ast.getForallConstructStmt();
		if(node != null){		
			if(node.getForallTripletSpecList()!=null){
				for(ASTForallTripletSpecListNode list : node.getForallTripletSpecList()){
					String tmp = list.toString().toLowerCase();
					if(tmp.startsWith(",")) tmp = tmp.substring(1);
					if(tmp.endsWith(",")) tmp = tmp.substring(0,tmp.length()-1);
					condition.add(tmp);
				}
			}
			if(node.getScalarMaskExpr()!=null)
				condition.add(node.getScalarMaskExpr().toString().toLowerCase());
		}
	}
	
	@Override
	public String getState(){
		String str = getName();
		str += "(";
		str += getCondition();
		str += ")";
		
		return str;
	}
	
	/**
	 * Return the condition.
	 * @return The condition
	 */
	public String getCondition(){
		if(condition == null || condition.isEmpty()) return "";
		String item = condition.get(0);	
		for(int i=1; i<condition.size(); i++)
			item += ","+condition.get(i);
		return item;
	}
	
	/**
	 * Return the type of this node.
	 * @return The type
	 */
	public int getType(){
		return type;
	}
}
