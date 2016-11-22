package stview.action.print;

import java.awt.geom.Rectangle2D;
import java.awt.print.Book;
import java.awt.print.PageFormat;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;

import stview.Controller;
import stview.node.STVNode;

import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFPage;

/**
 * The print preview class.
 */
public class PrintViewer extends Dialog{
	private static PrintViewer instance = null;
	public static int CHANGE = 10;
	public static int SAVE = 20;
	public static int PRINT = 30;
	
	protected String nowPaperSize = "A4";
	protected int nowOrientation = PageFormat.PORTRAIT;
	protected int nowFSize = 12;
	
	protected Controller ctr;
	protected STVNode start;

	protected String fName;
	protected PDFFile pdfFile;
	
	private Composite pdfComp;	
	private Combo fontSize;
	private Combo paperSize;
	private Combo orientation;
	//private Combo extension;
	
	/**
	 * The constructor.
	 * @param parentShell A shell
	 */
	public PrintViewer(Shell parentShell, Controller c) {
		super(parentShell);    	
		setShellStyle(getShellStyle() | SWT.RESIZE);
		ctr = c;
	}

	/**
	 * Create instance.
	 * @param c A Controller
	 * @return this object
	 */
	public static PrintViewer getInstance(Controller c){
		if(instance == null){
			Shell shell = Display.getCurrent() == null ? 
					new Shell(Display.getDefault()):new Shell(Display.getCurrent(), SWT.NO_TRIM);
					instance = new PrintViewer(shell,c);
		}
		return instance;
	}
	
	@Override
	protected Point getInitialSize(){
		return new Point(650, 750);
	}

	@Override
	protected Control createContents(Composite parent) {
		Composite composite = new Composite(parent, 0);
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.verticalSpacing = 0;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		applyDialogFont(composite);
		initializeDialogUnits(composite);

		createUpSide(composite);	
		dialogArea = createDialogArea(composite);
		buttonBar = createButtonBar(composite);

		return composite;
	}

	@Override
	protected Control createDialogArea(Composite parent){
		Composite composite = (Composite)super.createDialogArea(parent);
		composite.setLayout(new FillLayout());

		ScrolledComposite scroll =  new ScrolledComposite(composite, SWT.V_SCROLL);
		pdfComp = new Composite(scroll, SWT.NONE);
		pdfComp.setLayout(new GridLayout(1, false));
		setPDF();
		scroll.setContent(pdfComp);
		scroll.setMinSize(pdfComp.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		scroll.setExpandHorizontal(true);
		scroll.setExpandVertical(true);	

		return composite;
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, PrintViewer.SAVE, "Save",     true);	
		createButton(parent, PrintViewer.PRINT, "Print",     true);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, true);
	}

	/**
	 * Create the upper side menu.
	 * @param parent A parent composite.
	 */
	private void createUpSide(Composite parent){
		Composite composite = new Composite(parent,SWT.NULL);
		composite.setLayout(new RowLayout(SWT.HORIZONTAL));
		
		Label l1 = new Label(composite,SWT.NORMAL);
		l1.setText("FontSize");
		fontSize = new Combo(composite,SWT.NORMAL);
		fontSize.setItems(new String[]{"10","12","14"});
		fontSize.select(1);
		
		Label l2 = new Label(composite,SWT.NORMAL);
		l2.setText("PaperSize");
		paperSize =new Combo(composite,SWT.NORMAL);
		paperSize.setItems(new String[]{"A3","A4"});
		paperSize.select(1);

		Label l3 = new Label(composite,SWT.NORMAL);
		l3.setText("Orientation");
		orientation = new Combo(composite,SWT.NORMAL);
		orientation.setItems(new String[]{"landscape","portrait"});
		orientation.select(1);

		//extension = new Combo(composite,SWT.NORMAL);
		//extension.setItems(new String[]{"pdf"});
		//extension.select(0);
		//paperSize.setText("Paper Size");

		Button button = new Button(composite,SWT.NORMAL);
		button.setText("Change");
		button.setData(PrintViewer.CHANGE);
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				buttonPressed(((Integer) event.widget.getData()).intValue());
			}
		});
	}

	/**
	 * Prepare the PDF file to preview.
	 */
	private void setPDF(){
		try {
			for (Widget c : pdfComp.getChildren())
				c.dispose();		
			File tmpf = File.createTempFile("temp", ".pdf");
			tmpf.deleteOnExit();
			PDFDocument doc = PDFCreator.setting(nowFSize, nowPaperSize, nowOrientation == PageFormat.PORTRAIT? true : false);
			PDFCreator.getInstance(ctr).write(tmpf,doc,start);
			
			RandomAccessFile raf = new RandomAccessFile(tmpf, "r");	
			FileChannel channel = raf.getChannel();
			ByteBuffer buf = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
			pdfFile = new PDFFile(buf);	
			
			int numpages = pdfFile.getNumPages();
			for (int i=1; i <= numpages; i++) {
				Label label = new Label(pdfComp, SWT.NULL);
				PDFPage pdfPage = pdfFile.getPage(i);
				label.setImage(generateImageFromPdfPage(pdfPage));
			}
			
			channel.close();
			raf.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	/**
	 * Getting the image from PDF file.
	 * @param page A PDF page
	 * @return the image(SWT)
	 */
	private Image generateImageFromPdfPage(PDFPage page) {
		Rectangle2D rect = page.getBBox();    
		java.awt.Image img_awt = page.getImage((int)rect.getWidth(), (int)rect.getHeight(), null, null, true, true);
		Image img_swt = new Image(Display.getCurrent() == null ? Display.getDefault():Display.getCurrent(),ConvertImage.convertSWT(img_awt));
		return img_swt;
	}

	@Override
	protected void buttonPressed(int buttonId) {
		if (buttonId == PrintViewer.CHANGE) {
			nowFSize = Integer.valueOf(fontSize.getText());
			nowPaperSize = paperSize.getText();
			nowOrientation = orientation.getText().equals("portrait")? PageFormat.PORTRAIT : PageFormat.LANDSCAPE;		
			setPDF();
			pdfComp.layout();
		} else if(buttonId == PrintViewer.SAVE)	{
			FileDialog dialog = new FileDialog(this.getParentShell(),SWT.SAVE);
			dialog.setFilterPath(ctr.getProject().getLocation().toString());
			dialog.setFileName(fName);
			fName = dialog.open();
			if(fName != null){
				PDFDocument doc = PDFCreator.setting(nowFSize, nowPaperSize, nowOrientation == PageFormat.PORTRAIT? true : false);
				PDFCreator.getInstance(ctr).write(new File(fName),doc,start);
			}
		} else if(buttonId == PrintViewer.PRINT){
			PrintPage pages = new PrintPage(pdfFile);
			PrinterJob pjob = PrinterJob.getPrinterJob();
			PageFormat format = pjob.defaultPage();
			format.setOrientation(nowOrientation);
			//PDFViewer.doPrint()        		
			/*  830 */     pjob.setJobName(fName);
			/*  831 */     Book book = new Book();
			/*  833 */     book.append(pages, format, pdfFile.getNumPages());
			/*      */     
			/*  835 */     pjob.setPageable(book);
			if(pjob.printDialog())
				try {
					pjob.print();
				} catch (PrinterException e1) {
					e1.printStackTrace();
				}	
		} else if(buttonId == IDialogConstants.CANCEL_ID){
			this.cancelPressed();
		}
	}

	/**
	 * Open this dialog.
	 * @param n A root node
	 * @param c A Controller
	 * @return Dialog.open()
	 */
	public int open(STVNode n, Controller c){
		start = n;
		fName = "temp.pdf";
		return super.open();
	}
}

