package stview.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import stview.Activator;

/**
 * Class used to initialize default preference values.
 */
public class PreferenceConstants extends AbstractPreferenceInitializer {
	/**
	 * Constant definitions for plug-in preferences
	 */
		public static final String FULL_BOOLEAN = "FULL_BOOLEAN";
		public static final String INST_BOOLEAN = "INST_BOOLEAN";
		
		public static final String S_F = ".f";
		public static final String S_F90 = ".f90";
		public static final String S_F77 = ".f77";
		public static final String S_F95 = ".f95";
		public static final String S_F03 = ".f03";
		public static final String S_F08 = ".f08";
		public static final String S_FOR = ".for";
		public static final String S_FTN = ".ftn";
		public static final String S_FIX = ".fix";
		public static final String S_FPP = ".fpp";
		
		public static final String F = ".F";
		public static final String F90 = ".F90";
		public static final String F77 = ".F77";
		public static final String F95 = ".F95";
		public static final String F03 = ".F03";
		public static final String F08 = ".F08";
		public static final String FOR = ".FOR";
		public static final String FTN = ".FTN";
		public static final String FIX = ".FIX";
		public static final String FPP = ".FPP";

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	public void initializeDefaultPreferences() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		store.setDefault(FULL_BOOLEAN, false);
		store.setDefault(INST_BOOLEAN, false);
		
		store.setDefault(S_F, true);
		store.setDefault(S_F90, true);
		store.setDefault(S_F77, true);
		store.setDefault(S_F95, true);
		store.setDefault(S_F03, true);
		store.setDefault(S_F08, true);
		store.setDefault(S_FOR, true);
		store.setDefault(S_FTN, true);
		store.setDefault(S_FIX, true);
		store.setDefault(S_FPP, true);
		
		store.setDefault(F, false);
		store.setDefault(F90, false);
		store.setDefault(F77, false);
		store.setDefault(F95, false);
		store.setDefault(F03, false);
		store.setDefault(F08, false);
		store.setDefault(FOR, false);
		store.setDefault(FTN, false);
		store.setDefault(FIX, false);
		store.setDefault(FPP, false);
	}
	
	public static boolean getFullMode(){
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		return store.getBoolean(FULL_BOOLEAN);
	}
	
	public static boolean getInstMode(){
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		return store.getBoolean(INST_BOOLEAN);
	}
	
	public static boolean[] getSmall_Extension(){
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		boolean[] ext = new boolean[10];
		ext[0] = store.getBoolean(S_F);
		ext[1] = store.getBoolean(S_F77);
		ext[2] = store.getBoolean(S_F90);
		ext[3] = store.getBoolean(S_F95);
		ext[4] = store.getBoolean(S_F03);
		ext[5] = store.getBoolean(S_F08);
		ext[6] = store.getBoolean(S_FOR);
		ext[7] = store.getBoolean(S_FTN);
		ext[8] = store.getBoolean(S_FIX);
		ext[9] = store.getBoolean(S_FPP);
		return ext;
	}
	
	public static boolean[] getLarge_Extension(){
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		boolean[] ext = new boolean[10];
		ext[0] = store.getBoolean(F);
		ext[1] = store.getBoolean(F77);
		ext[2] = store.getBoolean(F90);
		ext[3] = store.getBoolean(F95);
		ext[4] = store.getBoolean(F03);
		ext[5] = store.getBoolean(F08);
		ext[6] = store.getBoolean(FOR);
		ext[7] = store.getBoolean(FTN);
		ext[8] = store.getBoolean(FIX);
		ext[9] = store.getBoolean(FPP);
		return ext;
	}
}
