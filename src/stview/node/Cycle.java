package stview.node;

import org.eclipse.photran.internal.core.parser.ASTCycleStmtNode;

/**
 * Cycle stmt class.
 */
public class Cycle extends Loop{
	private static final long serialVersionUID = 5068778323406864817L;
	
	public Cycle(ASTCycleStmtNode ast){
			//1
			setName("cycle");
							
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
