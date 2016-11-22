package stview.analyzer;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import stview.Activator;
import stview.node.*;

/**
 * Provides the Tree Label class.
 */
public class ViewLabelProvider extends LabelProvider {
	
	/**
	 * Return the tree label.
	 * @param element A node
	 * @return The label
	 */
	public String getText(Object element) {
		String str = "";
		if (element instanceof STVNode)
			str = ((STVNode)element).getState();
		return str;
	}
	
	/**
	 * Return the icon.
	 * @param element A node
	 * @return The icon
	 */
	public Image getImage(Object element){
		ImageRegistry registry = Activator.getDefault().getImageRegistry();
		if(element instanceof Subroutine)
			return registry.get("DMDLEditor.Subroutine");
		else if(element instanceof SubroutineCall)
			return registry.get("DMDLEditor.SubroutineCall");
		else if(element instanceof Function)
			return registry.get("DMDLEditor.Function");
		else if(element instanceof FunctionCall)
			return registry.get("DMDLEditor.FunctionCall");	
		else if(element instanceof MainProgram)
			return registry.get("DMDLEditor.ProgramRoot");
		else if(element instanceof Condition)
			return registry.get("DMDLEditor.IF");
		else if(element instanceof Loop)
			return registry.get("DMDLEditor.Loop");
		return registry.get("DMDLEditor.ProgramRoot");
	}
}