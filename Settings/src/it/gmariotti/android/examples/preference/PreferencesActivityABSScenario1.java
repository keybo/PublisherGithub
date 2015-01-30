
package it.gmariotti.android.examples.preference;
 
import android.os.Bundle;
import com.actionbarsherlock.app.SherlockPreferenceActivity;
 
public class PreferencesActivityABSScenario1 extends SherlockPreferenceActivity {
 
  @Override
  public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preference_scenario1);
	}
}