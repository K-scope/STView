package stview.node;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;

/**
 * Interface for STVNode
 */
public abstract interface ISTVNode{

	public abstract String getName();
	public abstract String getState();
	public abstract int getStartOffset();
	public abstract int getEndOffset();
	public abstract IFile getFile();
	public abstract boolean getFlag();
	public abstract void setName(String n);
	public abstract void setStartOffset(int s);
	public abstract void setEndOffset(int e);
	public abstract void setFile(IFile f);
	public abstract void setFlag(boolean f);
	
	public abstract IMarker getMarker();
	public abstract STVNode[] getAllChildren();

}
