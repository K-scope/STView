package stview.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import stview.Activator;
import stview.MessageLog;

public class FilePreferencePage 
	extends FieldEditorPreferencePage 
	implements IWorkbenchPreferencePage {

	public FilePreferencePage() {
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription(MessageLog.getString("preference.file"));
	}

	public void createFieldEditors() {
		addField(new BooleanFieldEditor(PreferenceConstants.S_F, "&.f", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.S_F77, "&.f77", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.S_F90, "&.f90", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.S_F95, "&.f95", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.S_F03, "&.f03", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.S_F08, "&.f08", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.S_FOR, "&.for", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.S_FTN, "&.ftn", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.S_FIX, "&.fix", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.S_FPP, "&.fpp", getFieldEditorParent()));

		addField(new BooleanFieldEditor(PreferenceConstants.F, "&.F", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.F77, "&.F77", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.F90, "&.F90", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.F95, "&.F95", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.F03, "&.F03", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.F08, "&.F08", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.FOR, "&.FOR", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.FTN, "&.FTN", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.FIX, "&.FIX", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.FPP, "&.FPP", getFieldEditorParent()));
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
	}
}
