package stview;

import java.util.ResourceBundle;

import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;

/**
 * Provide Message class.
 */
public class MessageLog{
	private static final String PROPERTIES_FILE = "message";
	private ResourceBundle bundle = null;
    private static MessageLog instance = new MessageLog();
	
    /**
     * The constructor.
     */
    private MessageLog() {
    	this.bundle = ResourceBundle.getBundle(PROPERTIES_FILE);
    }
    
    /**
     * Return the bundle for properties file.
     * @return The bundle
     */
    private static ResourceBundle getBundle() {
        try {
    		if (instance == null) instance = new MessageLog();
            return instance.bundle;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
	}
    
    /**
     * Return the message corresponding properties file.
     * @param key A key word
     * @return The message
     */
	public static String getString(String key) {
		if (key == null || key.isEmpty()) return null;
		if (getBundle() == null) return null;
		try {
			String msg = getBundle().getString(key);
			return msg;
		} catch (Exception ex) {
            ex.printStackTrace();
			return key;
		}
	}
	
	/**
	 * Display the message on the console.
	 * @param message A message
	 */
	public static void logConsole(String message) {
	    IConsoleManager conMan = ConsolePlugin.getDefault().getConsoleManager();
	    IConsole[] existing = conMan.getConsoles();
	    for (int i = 0; i < existing.length; i++)
	        if (Activator.PLUGIN_ID.equals(existing[i].getName())){
	            ((MessageConsole) existing[i]).newMessageStream().println(message);
	        	return;
	        }
	    
	    MessageConsole myConsole = new MessageConsole(Activator.PLUGIN_ID, null);
	    conMan.addConsoles(new IConsole[] { myConsole });
	    myConsole.newMessageStream().println(message);
	}
}
