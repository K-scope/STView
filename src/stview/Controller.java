package stview;

import java.util.ArrayList;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;

import stview.node.STVNode;
import stview.preferences.PreferenceConstants;
import stview.views.STView;

/**
 * The control class.
 */
public class Controller {
	
	private STView viewer;
	private IProject project;
	private ArrayList<IFile> fileList;

	/**
	 * The constructor.
	 * @param v STView
	 */
	public Controller(STView v){
		viewer = v;
		project = null;
	}
	
	/**
	 * Return the view object.
	 * @return STView
	 */
	public STView getView(){
		return viewer;
	}

	/**
	 * Hold a project which the edited file belongs.
	 */
	public void setProject(){
		project = null;
		IEditorPart editorPart = viewer.getSite().getWorkbenchWindow().getActivePage().getActiveEditor();
		if(editorPart == null) return;

		IEditorInput input = editorPart.getEditorInput();
		if (input instanceof IFileEditorInput) {
			IFileEditorInput fileInput = (IFileEditorInput) input;
			IFile ifile = fileInput.getFile();
			project = ifile.getProject();
		}
		fileList = new ArrayList<IFile>();   
	}

	/**
	 * Add a node to the block list.
	 * @param node An additional node
	 */
	public void addBlock(STVNode node){
		if(node != null)
			viewer.getCreateAction().addBlock(node);
	}

	/**
	 * Return the current project.
	 * @return The project
	 */
	public IProject getProject(){
		return project;
	}

	/**
	 * Return the list of Fortran files.
	 * @return The file list
	 */
	public ArrayList<IFile> getFilelist(){
		return fileList;
	}

	/**
	 * Create a list of Fortran files.
	 * @return True :: There is a Fortran file.
	 */
	public boolean createFilelist(){
		setProject();
		if(project == null) return false;

		IResource src = project.findMember("src");
		try {
			if(src != null && src.getType()==IResource.FOLDER){
				//src folder			
				IFolder folder = project.getFolder("src");
				for(IResource resource : folder.members()){
					if(resource.getType()==IResource.FILE){
						if(isFortranFile((IFile)resource))
							fileList.add((IFile)resource);
						
					}else
						searchFile(resource);
				}
			}else{
				//no src folder	
				for(IResource resource : project.members()){
					if(resource.getType()==IResource.FILE){
						if(isFortranFile((IFile)resource))
							fileList.add((IFile)resource);
					}else
						searchFile(resource);
				}	
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return !fileList.isEmpty();
	}
	
	/**
	 * Look for a list of Fortran files.
	 * @param r A resource
	 */
	public void searchFile(IResource r){
		if(r.getType()==IResource.FOLDER){
			try {
				IFolder folder = (IFolder)r;
				for(IResource resource : folder.members()){
					if(resource.getType()==IResource.FILE){
						if(isFortranFile((IFile)resource))
							fileList.add((IFile)resource);
					}else
						searchFile(resource);
				}
			} catch (CoreException e) {
				e.printStackTrace();
			} 
		}
	}
	
	/**
	 * Check the file extension.
	 * @param file A file
	 * @return True :: fortran file
	 */
	public static boolean isFortranFile(IFile file){
		String ex = file.getFileExtension();
		if(ex == null) return false;
		boolean[] small = PreferenceConstants.getSmall_Extension();
		boolean[] large = PreferenceConstants.getLarge_Extension();
		
		if(ex.equals("F")) return large[0];//MessageLog.logConsole(file.getName());
		else if(ex.equals("F77")) return large[1];//MessageLog.logConsole(file.getName());
		else if(ex.equals("F90")) return large[2];//MessageLog.logConsole(file.getName());
		else if(ex.equals("F95")) return large[3];//MessageLog.logConsole(file.getName());
		else if(ex.equals("F03")) return large[4];//MessageLog.logConsole(file.getName());
		else if(ex.equals("F08")) return large[5];//MessageLog.logConsole(file.getName());
		else if(ex.equals("FOR")) return large[6];//MessageLog.logConsole(file.getName());
		else if(ex.equals("FTN")) return large[7];//MessageLog.logConsole(file.getName());
		else if(ex.equals("FIX")) return large[8];//MessageLog.logConsole(file.getName());
		else if(ex.equals("FPP")) return large[9];//MessageLog.logConsole(file.getName());
		
		if(ex.equals("f")) return small[0];
		else if(ex.equals("f77")) return small[1];
		else if(ex.equals("f90")) return small[2];
		else if(ex.equals("f95")) return small[3];
		else if(ex.equals("f03")) return small[4];
		else if(ex.equals("f08")) return small[5];
		else if(ex.equals("for")) return small[6];
		else if(ex.equals("ftn")) return small[7];
		else if(ex.equals("fix")) return small[8];
		else if(ex.equals("fpp")) return small[9];
		
		else return false;
	}
}
