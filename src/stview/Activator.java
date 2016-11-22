package stview;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "STView"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;
	
	/**
	 * The constructor
	 */
	public Activator() {
	}

        @Override
	protected void initializeImageRegistry(ImageRegistry reg) {
	    reg.put("DMDLEditor.Create", getImageDescriptor("/icons/Create.gif"));
	    reg.put("DMDLEditor.Expand", getImageDescriptor("/icons/Expand.gif"));
	    reg.put("DMDLEditor.Collapse", getImageDescriptor("/icons/Collapse.gif"));
	    reg.put("DMDLEditor.Setting", getImageDescriptor("/icons/Setting.gif"));
	    reg.put("DMDLEditor.Print", getImageDescriptor("/icons/Print.gif"));
	    reg.put("DMDLEditor.Subroutine", getImageDescriptor("/icons/Subroutine.gif"));
	    reg.put("DMDLEditor.SubroutineCall", getImageDescriptor("/icons/SubroutineCall.gif"));
	    reg.put("DMDLEditor.Function", getImageDescriptor("/icons/Function.gif"));
	    reg.put("DMDLEditor.FunctionCall", getImageDescriptor("/icons/FunctionCall.gif"));
	    reg.put("DMDLEditor.ProgramRoot", getImageDescriptor("/icons/ProgramRoot.gif"));
	    reg.put("DMDLEditor.IF", getImageDescriptor("/icons/IF.gif"));
	    reg.put("DMDLEditor.Loop", getImageDescriptor("/icons/Loop.gif"));
	    reg.put("DMDLEditor.View", getImageDescriptor("/icons/View.gif"));
	    reg.put("DMDLEditor.Load", getImageDescriptor("/icons/Load.gif"));
        }
    
        public static ImageDescriptor getImageDescriptor(String path) {
	    return imageDescriptorFromPlugin(PLUGIN_ID, path);
        }

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

}
