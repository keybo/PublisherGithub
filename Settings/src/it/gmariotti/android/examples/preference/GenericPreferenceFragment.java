
package it.gmariotti.android.examples.preference;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.preference.PreferenceFragment;


@SuppressLint("NewApi")
public class GenericPreferenceFragment extends PreferenceFragment {
  
	@Override
    public void onCreate(Bundle savedInstanceState) {
		
		int preferenceFile_toLoad=-1;
		
		 String settings = getArguments().getString("settings");
	     if (Constants.SETTING_UPDATE.equals(settings)) 
	     {
		        // Load the preferences from an XML resource
		        preferenceFile_toLoad= R.xml.preference_update;
	     }
	     else if (Constants.SETTING_DISPLAY.equals(settings)) {
		        // Load the preferences from an XML resource
		        preferenceFile_toLoad=R.xml.preference_display;
	     }else if (Constants.SETTING_NOTIFY.equals(settings)) {
		        // Load the preferences from an XML resource
		        preferenceFile_toLoad=R.xml.preference_notify;
		 }
		
	     addPreferencesFromResource(preferenceFile_toLoad);
	}
}