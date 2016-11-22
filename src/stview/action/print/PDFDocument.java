package stview.action.print;

import java.io.IOException;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BaseFont;

/**
 * The PDF file class.
 */
public class PDFDocument extends Document{
	protected Image[] img;
	protected Image[] line;
	private float size;
	private Font font;
	
	/**
	 * The constructor.
	 * @param p Paper information
	 * @param s Font size
	 */
	public PDFDocument(Rectangle p, int s){
		super(p);
		size = s/12f;
		try {
			font = new Font(BaseFont.createFont(BaseFont.COURIER,BaseFont.WINANSI,BaseFont.NOT_EMBEDDED),s);
			setImage();
		} catch (DocumentException | IOException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Prepare image files.
	 */
	private void setImage(){
		img = new Image[7];
		line = new Image[4];
		try{ 
			Bundle bundle = Platform.getBundle("STView");
			line[0] = Image.getInstance(FileLocator.toFileURL(bundle.getEntry("icons/line1.gif")));
			line[1] = Image.getInstance(FileLocator.toFileURL(bundle.getEntry("icons/line2.gif")));
			line[2] = Image.getInstance(FileLocator.toFileURL(bundle.getEntry("icons/line3.gif")));
			line[3] = Image.getInstance(FileLocator.toFileURL(bundle.getEntry("icons/line4.gif")));
			for(int i = 0;i<line.length;i++)
				line[i].scalePercent(size*100f);
			img[0] = Image.getInstance(FileLocator.toFileURL(bundle.getEntry("icons/ProgramRoot.gif")));
			img[1] = Image.getInstance(FileLocator.toFileURL(bundle.getEntry("icons/Subroutine.gif")));
			img[2] = Image.getInstance(FileLocator.toFileURL(bundle.getEntry("icons/SubroutineCall.gif")));
			img[3] = Image.getInstance(FileLocator.toFileURL(bundle.getEntry("icons/Function.gif")));
			img[4] = Image.getInstance(FileLocator.toFileURL(bundle.getEntry("icons/FunctionCall.gif")));
			img[5] = Image.getInstance(FileLocator.toFileURL(bundle.getEntry("icons/IF.gif")));
			img[6] = Image.getInstance(FileLocator.toFileURL(bundle.getEntry("icons/Loop.gif")));
			for(int i = 0;i<img.length;i++)
				img[i].scalePercent(size*100f);
		}catch(DocumentException | IOException e){
			e.printStackTrace();
		}
	}
	
	/**
	 * Return the font size.
	 * @return The font size
	 */
	public float getSize(){
		return size;
	}
	
	/**
	 * Return the font format.
	 * @return The font format
	 */
	public Font getFont(){
		return font;
	}
	
	/**
	 * Return the chunk including the line image.
	 * @param i The index of the line type
	 * @return The chunk
	 */
	public Chunk getLineChunk(int i){
		if(line.length<i) return null;
		return new Chunk(getLine(i),0,-4*size);
	}
	
	/**
	 * Return the line icon.
	 * @param i The index of the line type
	 * @return The line icon
	 */
	public Image getLine(int i){
		if(line.length<i) return null;
		return line[i];
	}
	
	/**
	 * Return the chunk including the node image.
	 * @param i The index of the node type
	 * @return The chunk
	 */
	public Chunk getImageChunk(int i){
		if(img.length<i) return null;
		return new Chunk(getImage(i),0,-4*size);
	}
	
	/**
	 * Return the node icon.
	 * @param i The index of the node type
	 * @return The node icon
	 */
	public Image getImage(int i){
		if(img.length<i) return null;
		return img[i];
	}

}
