package stview.analyzer;

import java.util.ArrayList;

import org.eclipse.core.resources.IFile;
import org.eclipse.photran.core.IFortranAST;
import org.eclipse.photran.internal.core.analysis.binding.ScopingNode;
import org.eclipse.photran.internal.core.analysis.loops.ASTProperLoopConstructNode;
import org.eclipse.photran.internal.core.analysis.loops.LoopReplacer;
import org.eclipse.photran.internal.core.lang.intrinsics.*;
import org.eclipse.photran.internal.core.parser.*;
import org.eclipse.photran.internal.core.vpg.PhotranVPG;

import stview.Controller;
import stview.MessageLog;
import stview.node.*;
import stview.preferences.PreferenceConstants;

/**
 * The Photran-AST parser.
 */
public class ASTParser {
	protected Controller ctr;
	private ArrayList<String> funcList;
	
	/**
	 * The constructor.
	 * @param c A controller
	 */
	public ASTParser(Controller c){
		ctr = c;
	}
	
	/**
	 * Analyze Photran-AST.
	 */
	public void analyze() {
		if(ctr.getFilelist() == null) return;
		if(ctr.getFilelist().isEmpty()) return;

		fnListCheck();
	
		for (IFile file : ctr.getFilelist()) {
			// access rule ÇÃçXêV
			IFortranAST ast = PhotranVPG.getInstance().acquirePermanentAST(file);
			// programÇ™Ç»Ç¢èÍçá
			if (ast == null) {
				MessageLog.logConsole(file.getName()+" "+MessageLog.getString("error.noAST"));
				continue;
			}
			MessageLog.logConsole(file.getName()+" "+MessageLog.getString("parse"));
			LoopReplacer.replaceAllLoopsIn(ast.getRoot());
			createItem(ast.getRoot(), null);
			PhotranVPG.getInstance().releaseAST(file);
		}
	}
	
	/**
	 * Create the function name list.
	 */
	protected void fnListCheck(){
		funcList = new ArrayList<String>();
		//Add instrinsic functions
		if(PreferenceConstants.getInstMode())
			for(IntrinsicProcDescription intri : Intrinsics.getAllIntrinsicProcedures())
				funcList.add(intri.genericName.toLowerCase());
		
		for(IFile file : ctr.getFilelist()){
			IFortranAST ast = PhotranVPG.getInstance().acquirePermanentAST(file);				
			if(ast == null)
				continue;

			searchfnList(ast.getRoot());
			PhotranVPG.getInstance().releaseAST(file);			
		}
	}
	
	/**
	 * Search the function node.
	 * @param node A check node
	 */
	private void searchfnList(ScopingNode node){
		if(node instanceof ASTModuleNode || node instanceof ASTSubmoduleNode)
			if(node.getBody() == null) return;
		
		if(node instanceof IProgramUnit){
			if(node instanceof ASTFunctionSubprogramNode)
				funcList.add(node.getName().toLowerCase());	
			if(node.getInternalSubprograms() == null) return;
			if(node.getInternalSubprograms().isEmpty()) return;
			for(IInternalSubprogram sub : node.getInternalSubprograms()){
				if(ScopingNode.isScopingNode(sub))
					searchfnList((ScopingNode)sub);
			}
		}
		else if(node instanceof ASTExecutableProgramNode){
			ASTExecutableProgramNode exNode = (ASTExecutableProgramNode)node;
			if(exNode.getProgramUnitList() == null) return;
			for(IProgramUnit child : exNode.getProgramUnitList()){
					if(child instanceof ASTErrorProgramUnitNode)
						MessageLog.logConsole("error");
					else if(ScopingNode.isScopingNode(child))
						searchfnList((ScopingNode)child);
			}
		}	
	}
		
	/**
	 * Create the STVNode from the Photran-AST node.
	 * @param node A Photran-AST node
	 * @param parent A parent node of STVNode
	 */
	private void createItem(IASTNode node, STVNode parent){
		if(node instanceof ScopingNode){
			if(node instanceof IProgramUnit){
				if(node instanceof ASTMainProgramNode) createItem((ASTMainProgramNode)node);
				else if(node instanceof ASTSubroutineSubprogramNode) createItem((ASTSubroutineSubprogramNode)node);	
				else if(node instanceof ASTFunctionSubprogramNode) createItem((ASTFunctionSubprogramNode)node);
				else if(node instanceof ASTModuleNode) createItem((ASTModuleNode)node, parent);
				else if(node instanceof ASTSubmoduleNode) createItem((ASTSubmoduleNode)node, parent);
				else if(node instanceof ASTInterfaceBlockNode) createItem((ASTInterfaceBlockNode)node);
			}else if(node instanceof ASTExecutableProgramNode) createItem((ASTExecutableProgramNode)node, parent);
		}else if(node instanceof IExpr){
			if(node instanceof ASTBinaryExprNode) createItem((ASTBinaryExprNode)node, parent);
			else if(node instanceof ASTNestedExprNode) createItem((ASTNestedExprNode)node, parent);
			else if(node instanceof ASTUnaryExprNode) createItem((ASTUnaryExprNode)node, parent);
			else if(node instanceof ASTVarOrFnRefNode) createItem((ASTVarOrFnRefNode)node, parent);
		}
		else if(node instanceof ASTAssignmentStmtNode) createItem((ASTAssignmentStmtNode)node, parent);
		else if(node instanceof ASTCallStmtNode) createItem((ASTCallStmtNode)node, parent);	
		else if(node instanceof ASTCaseConstructNode) createItem((ASTCaseConstructNode)node, parent);
		else if(node instanceof ASTCriticalConstructNode) createItem((ASTCriticalConstructNode)node, parent);
		else if(node instanceof ASTCycleStmtNode) createItem((ASTCycleStmtNode)node, parent);
		else if(node instanceof ASTElseConstructNode) createItem((ASTElseConstructNode)node, parent);
		else if(node instanceof ASTElseIfConstructNode) createItem((ASTElseIfConstructNode)node, parent);
		else if(node instanceof ASTForallConstructNode) createItem((ASTForallConstructNode)node, parent);
		else if(node instanceof ASTForallStmtNode) createItem((ASTForallStmtNode)node, parent);
		else if(node instanceof ASTIfConstructNode) createItem((ASTIfConstructNode)node, parent);
		else if(node instanceof ASTIfStmtNode) createItem((ASTIfStmtNode)node, parent);
		else if(node instanceof ASTProperLoopConstructNode) createItem((ASTProperLoopConstructNode)node, parent);
		else if(node instanceof ASTSelectTypeConstructNode) createItem((ASTSelectTypeConstructNode)node, parent);
		else if(node instanceof ASTWhereConstructNode) createItem((ASTWhereConstructNode)node, parent);
		else if(node instanceof ASTWhereStmtNode) createItem((ASTWhereStmtNode)node, parent);
		
		else if(node instanceof ASTErrorConstructNode);
	}
	
	/**
	 * Create the STVNode from the Photran-AST node for MainProgram.
	 * @param node A Photran-AST node
	 */
	private void createItem(ASTMainProgramNode node){
		MainProgram stv = new MainProgram(node);

		if(node.getBody() != null)
			for(IBodyConstruct child : node.getBody())
				createItem(child, stv);
		ctr.addBlock(stv);
		
		if(node.getInternalSubprograms() == null || node.getInternalSubprograms().isEmpty()) return;
		for(IInternalSubprogram sub : node.getInternalSubprograms())
			createItem(sub, stv);
	}
	
	/**
	 * Create the STVNode from the Photran-AST node for Subroutine.
	 * @param node A Photran-AST node
	 */
	private void createItem(ASTSubroutineSubprogramNode node){
		Subroutine stv = new Subroutine(node);
		
		if(node.getBody() != null)
			for(IBodyConstruct child : node.getBody())
				createItem(child, stv);
		ctr.addBlock(stv);
		
		if(node.getInternalSubprograms() == null || node.getInternalSubprograms().isEmpty()) return;
		for(IInternalSubprogram sub : node.getInternalSubprograms())
			createItem(sub, stv);
	}
	
	/**
	 * Create the STVNode from the Photran-AST node for Function.
	 * @param node A Photran-AST node
	 */
	private void createItem(ASTFunctionSubprogramNode node){
		Function stv = new Function(node);
		
		if(node.getBody() != null)
			for(IBodyConstruct child : node.getBody())
				createItem(child, stv);
		ctr.addBlock(stv);
		
		if(node.getInternalSubprograms() == null || node.getInternalSubprograms().isEmpty()) return;
		for(IInternalSubprogram sub : node.getInternalSubprograms())
			createItem(sub, stv);
	}
	
	/**
	 * Analyze a Module of Photran-AST.
	 * @param node A Photran-AST node
	 * @param parent A parent node of STVNode
	 */
	private void createItem(ASTModuleNode node, STVNode parent){
		if(node.getBody() == null || 
				node.getInternalSubprograms() == null || 
				node.getInternalSubprograms().isEmpty()) return;
		for(IInternalSubprogram sub : node.getInternalSubprograms())
			createItem(sub, parent);
	}
	
	/**
	 * Analyze a SubModule of Photran-AST.
	 * @param node A Photran-AST node
	 * @param parent A parent node of STVNode
	 */
	private void createItem(ASTSubmoduleNode node, STVNode parent){
		if(node.getBody() == null || 
				node.getInternalSubprograms() == null || 
				node.getInternalSubprograms().isEmpty()) return;
		for(IInternalSubprogram sub : node.getInternalSubprograms())
			createItem(sub, parent);
	}
	
	/**
	 * Analyze a Interface of Photran-AST.
	 * @param node A Photran-AST node
	 */
	private void createItem(ASTInterfaceBlockNode node){
		//TODO
	}
	
	/**
	 * Analyze a ExecutableProgram of Photran-AST.
	 * @param node A Photran-AST node
	 * @param parent A parent node of STVNode
	 */
	private void createItem(ASTExecutableProgramNode node, STVNode parent){
		if(node.getProgramUnitList() == null) return;
		for(IProgramUnit child : node.getProgramUnitList()){
				if(child instanceof ASTErrorProgramUnitNode)
					MessageLog.logConsole("error");
				else
					createItem(child, parent);
		}
	}
	
	/**
	 * Analyze a BinaryExpr of Photran-AST.
	 * @param node A Photran-AST node
	 * @param parent A parent node of STVNode
	 */
	private void createItem(ASTBinaryExprNode node, STVNode parent){
		if(node.getLhsExpr() != null)
			createItem(node.getLhsExpr(),parent);
		
		if(node.getRhsExpr() != null)
			createItem(node.getRhsExpr(),parent);
	}
	
	/**
	 * Analyze a NestedExpr of Photran-AST.
	 * @param node A Photran-AST node
	 * @param parent A parent node of STVNode
	 */
	private void createItem(ASTNestedExprNode node, STVNode parent){
		if(node.getExpr() != null)
			createItem(node.getExpr(),parent);
	}
	
	/**
	 * Analyze a UnaryExpr of Photran-AST.
	 * @param node A Photran-AST node
	 * @param parent A parent node of STVNode
	 */
	private void createItem(ASTUnaryExprNode node, STVNode parent){
		if(node.getOperand() != null)
			createItem(node.getOperand(),parent);
	}
	
	/**
	 * Create the STVNode from the Photran-AST node for FnRef.
	 * @param node A Photran-AST node
	 * @param parent A parent node of STVNode
	 */
	private void createItem(ASTVarOrFnRefNode node, STVNode parent){	
		if(funcList.contains(node.getName().getName().getText().toLowerCase()) && node.getPrimarySectionSubscriptList()!=null){
			FunctionCall stv = new FunctionCall(node);
			parent.add(stv);
		}
	}
	
	/**
	 * Analyze a AssignmentStmt of Photran-AST.
	 * @param node A Photran-AST node
	 * @param parent A parent node of STVNode
	 */
	private void createItem(ASTAssignmentStmtNode node, STVNode parent){
		if(node.getRhs() != null) 
			createItem(node.getRhs(),parent);
	}
	
	/**
	 * Create the STVNode from the Photran-AST node for Call.
	 * @param node A Photran-AST node
	 * @param parent A parent node of STVNode
	 */
	private void createItem(ASTCallStmtNode node, STVNode parent){
		SubroutineCall stv = new SubroutineCall(node);
		parent.add(stv);
	}
	
	/**
	 * Create the STVNode from the Photran-AST node for Case.
	 * @param node A Photran-AST node
	 * @param parent A parent node of STVNode
	 */
	private void createItem(ASTCaseConstructNode node, STVNode parent){
		CaseConstruct stv = new CaseConstruct(node);
	
		if(node.getSelectCaseBody() == null) return;
		Case cas = null;
		for(ICaseBodyConstruct child : node.getSelectCaseBody()){
			if(child instanceof ASTCaseStmtNode){
				cas = new Case((ASTCaseStmtNode)child);
				stv.add(cas);
			}else{
				if(cas != null)
				createItem(child, cas);
			}
		}
		parent.add(stv);
	}
	
	/**
	 * Analyze a Critical of Photran-AST.
	 * @param node A Photran-AST node
	 * @param parent A parent node of STVNode
	 */
	private void createItem(ASTCriticalConstructNode node, STVNode parent){
		System.out.println("Critical");
		if(node.getBody() == null) return;
		for(IBodyConstruct child : node.getBody())
			createItem(child, parent);
	}

	private void createItem(ASTCycleStmtNode node, STVNode parent){
		Cycle stv = new Cycle(node);
		parent.add(stv);	
	}
	
	/**
	 * Create the STVNode from the Photran-AST node for ElseIf.
	 * @param node A Photran-AST node
	 * @param parent A parent node of STVNode
	 */
	private void createItem(ASTElseIfConstructNode node, STVNode parent){
		IfConstruct stv = new IfConstruct(node);
		
		if(node.getConditionalBody() != null)
			for(IExecutionPartConstruct child : node.getConditionalBody())
				createItem(child, stv);
		parent.add(stv);
		
		if(node.getElseIfConstruct() != null)
			createItem(node.getElseIfConstruct(), parent);
		if(node.getElseConstruct() != null)
			createItem(node.getElseConstruct(), parent);
	}
	
	/**
	 * Create the STVNode from the Photran-AST node for Else.
	 * @param node A Photran-AST node
	 * @param parent A parent node of STVNode
	 */
	private void createItem(ASTElseConstructNode node, STVNode parent){
		IfConstruct stv = new IfConstruct(node);
		
		if(node.getConditionalBody() != null)
			for(IExecutionPartConstruct child : node.getConditionalBody())
				createItem(child, stv);
		
		parent.add(stv);
	}
	
	private void createItem(ASTForallConstructNode node, STVNode parent){
		Forall stv = new Forall(node);
		
		if(node.getForallBody() != null)
			for(IForallBodyConstruct child : node.getForallBody())
				createItem(child, stv);
		
		parent.add(stv);
	}
	
	private void createItem(ASTForallStmtNode node, STVNode parent){
		Forall stv = new Forall(node);
		createItem(node.getAssignment(), stv);
		
		parent.add(stv);
	}
	
	/**
	 * Create the STVNode from the Photran-AST node for Ifthen.
	 * @param node A Photran-AST node
	 * @param parent A parent node of STVNode
	 */
	private void createItem(ASTIfConstructNode node, STVNode parent){
		IfConstruct stv = new IfConstruct(node);
		
		if(node.getConditionalBody() != null)
			for(IExecutionPartConstruct child : node.getConditionalBody())
				createItem(child, stv);
		
		if(node.getElseIfConstruct() != null)
			createItem(node.getElseIfConstruct(), stv);
		if(node.getElseConstruct() != null)
			createItem(node.getElseConstruct(), stv);
		
		parent.add(stv);
	}
	
	/**
	 * Create the STVNode from the Photran-AST node for If.
	 * @param node A Photran-AST node
	 * @param parent A parent node of STVNode
	 */
	private void createItem(ASTIfStmtNode node, STVNode parent){
		IfConstruct stv = new IfConstruct(node);
		createItem(node.getActionStmt(), stv);
		
		parent.add(stv);
	}
	
	/**
	 * Create the STVNode from the Photran-AST node for Loop.
	 * @param node A Photran-AST node
	 * @param parent A parent node of STVNode
	 */
	private void createItem(ASTProperLoopConstructNode node, STVNode parent){
		Do stv = new Do(node);
		
		if(node.getBody() != null)
			for(IExecutionPartConstruct child : node.getBody())
				createItem(child, stv);
			
		parent.add(stv);
	}
	
	/**
	 * Analyze a SelectType of Photran-AST.
	 * @param node A Photran-AST node
	 * @param parent A parent node of STVNode
	 */
	private void createItem(ASTSelectTypeConstructNode node, STVNode parent){
		System.out.println("SelectType");
		if(node.getSelectTypeBody() == null) return;
		for(ASTSelectTypeBodyNode child : node.getSelectTypeBody()){
			for(IExecutionPartConstruct nn : child.getTypeGuardBlock())
				createItem(nn, parent);
		}
		
	}
	
	private void createItem(ASTWhereConstructNode node, STVNode parent){
		//TODO
	}
	
	private void createItem(ASTWhereStmtNode node, STVNode parent){
		//TODO
	}
	
}