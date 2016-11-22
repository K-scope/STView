package stview.action.print;

import stview.Controller;
import stview.analyzer.Builder;
import stview.node.*;
import stview.views.StructureTree;

import java.awt.Graphics2D;
import java.io.*;
import java.util.ArrayList;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfWriter;


/**
 * The creator PDF file.
 */
public class PDFCreator{
	private static PDFCreator instance = null;
	protected StructureTree view;
	public static final byte T = 1;
	public static final byte F = 0;

	/**
	 * The constructor.
	 * @param v A TreeView
	 */
	public PDFCreator(StructureTree v){
		view = v;
	}

	/**
	 * Create instance.
	 * @param ctr A controller
	 * @return This object
	 */
	public static PDFCreator getInstance(Controller ctr){
		if(instance == null){
			instance = new PDFCreator(ctr.getView().getTreeViewer());
		}
		return instance;
	}
	
	/**
	 * Setting the document size.
	 * @param s A font size
	 * @param paper A paper size
	 * @param m A paper direction
	 */
	public static PDFDocument setting(int s, String paper, boolean m){
		PDFDocument doc = null;
		if(m){
			if(paper.equals("A4")){
				doc = new PDFDocument(PageSize.A4,s);
			}else if(paper.equals("A3")){
				doc = new PDFDocument(PageSize.A3,s);
			}
		}else{
			if(paper.equals("A4")){
				doc = new PDFDocument(PageSize.A4.rotate(),s);
			}else if(paper.equals("A3")){
				doc = new PDFDocument(PageSize.A3.rotate(),s);
				}
		}
		return doc;
	}

	/**
	 * Write PDF file.
	 * @param file A PDF file
	 * @param start A top node to write
	 */
	public void write(File file, PDFDocument doc, STVNode start){
		FileOutputStream out = null;
		BufferedOutputStream bout = null;
		PdfWriter writer = null;

		try{
			out = new FileOutputStream(file);
			bout = new BufferedOutputStream(out);          
			writer = PdfWriter.getInstance(doc, bout);
			writer.setStrictImageSequence(true);
			doc.open();

			PdfContentByte cb = writer.getDirectContent(); 
			Graphics2D graphics = cb.createGraphics(doc.getPageSize().getWidth(),doc.getPageSize().getHeight());

			ArrayList<Byte> list = new ArrayList<Byte>();
			list.add(F);
			if(start == null)
				searchNode(graphics, doc, list, (STVNode)view.getRoot().getChildAt(0));
			else
				searchNode(graphics, doc, list, start);
			
			doc.close();
			writer.close();
			bout.close();
			out.close();
		} catch (DocumentException | IOException e) {
			e.printStackTrace();
		}
	}  

	/**
	 * Check whether the writing node or not.
	 * @param list A list of dot lines
	 * @param node A current node
	 */
	@SuppressWarnings("unchecked")
	private void searchNode(Graphics2D graphics, PDFDocument doc, ArrayList<Byte> list, STVNode node){
		if(node instanceof Dummy)
			node = ((Builder)view.getContentProvider()).getTrueth(node);
		if(node.getFlag()){
			writeNode(graphics,doc,list,node);
			if(view.getExpandedState(node)){
				ArrayList<Byte> nlist;
				for(int i = 0;i<node.getChildCount();i++){
					if(((STVNode)node.getChildAt(i)).getFlag()){
						nlist = (ArrayList<Byte>)list.clone();
						if(node.getAllChildren().length==i+1)
							nlist.add(F);	
						else
							nlist.add(T);	
						searchNode(graphics, doc, nlist, (STVNode)node.getChildAt(i));	
					}
				}
			}
		}
	}

	/**
	 * Write the node.
	 * @param list A list of dot lines
	 * @param node A current node
	 */
	public static void writeNode(Graphics2D graphics, PDFDocument doc, ArrayList<Byte> list, STVNode node){
		try{
			if(list.size() == 1)
				doc.add(doc.getLineChunk(3));
			else{
				for(int i = 0; i < list.size()-1 ; i++){
					if(list.get(i) == PDFCreator.T)
						doc.add(doc.getLineChunk(1));
					else 
						doc.add(new Phrase("  ",doc.getFont()));
				}
				
				if(list.get(list.size()-1) == PDFCreator.T)
					doc.add(doc.getLineChunk(2));
				else 
					doc.add(doc.getLineChunk(0));
			}
			
			if(node instanceof MainProgram)	
				doc.add(doc.getImageChunk(0));
			else if(node instanceof Subroutine)
				doc.add(doc.getImageChunk(1));
			else if(node instanceof SubroutineCall)
				doc.add(doc.getImageChunk(2)); 
			else if(node instanceof Function)
				doc.add(doc.getImageChunk(3)); 
			else if(node instanceof FunctionCall)
				doc.add(doc.getImageChunk(4)); 
			else if(node instanceof Condition)
				doc.add(doc.getImageChunk(5)); 
			else if(node instanceof Loop)
				doc.add(doc.getImageChunk(6));
			Phrase p2 = new Phrase(" "+node.getState()+"\n",doc.getFont());
			doc.add(p2);	
		
			graphics.dispose();
		}catch(DocumentException e){
			e.printStackTrace();
		}
	}
}