package stview.action.print;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;

import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFPage;
import com.sun.pdfview.PDFRenderer;

/**
 * The print job class.
 */
public class PrintPage implements Printable{
	private PDFFile file;
	
	/**
	 * The constructor.
	 * @param fÅ@A PDF file
	 */
	public PrintPage(PDFFile f){
		this.file = f;
	}

	@Override
	public int print(Graphics graphics, PageFormat pageFormat, int pageIndex)
			throws PrinterException {

		Graphics2D g2 = (Graphics2D) graphics;
		PDFPage page = this.file.getPage(pageIndex + 1);

		int height = (int) pageFormat.getImageableHeight();
		int width = (int) pageFormat.getImageableWidth();

		Rectangle imgbounds = new Rectangle((int) pageFormat.getImageableX(),
				(int) pageFormat.getImageableY(), width, height);
		PDFRenderer pgs = new PDFRenderer(page, g2, imgbounds, null, null);
		try {
			page.waitForFinish();
			pgs.run();
		} catch (InterruptedException ie) {
			ie.printStackTrace();
		}
		return 0;
	}
}
