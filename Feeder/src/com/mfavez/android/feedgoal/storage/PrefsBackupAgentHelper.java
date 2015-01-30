

package com.mfavez.android.feedgoal.storage;

import android.annotation.SuppressLint;
import android.app.backup.BackupAgentHelper;
import android.app.backup.SharedPreferencesBackupHelper;


@SuppressLint("NewApi")
public class PrefsBackupAgentHelper extends BackupAgentHelper {
	private static final String LOG_TAG = "PrefsBackupAgentHelper";
	
	// The name of the file corresponding to FeedPrefActivity
    static final String PREFS_ACTIVITY_ENDING_COMMON_FILE_NAME = "_" + "preferences";

    // A key to uniquely identify the set of backup data
    static final String PREFS_BACKUP_KEY = "prefs_backup_key";

    // Allocate a helper and add it to the backup agent
    @Override
    public void onCreate()
    {
    	String prefsActivityFullFileName = this.getPackageName() + PREFS_ACTIVITY_ENDING_COMMON_FILE_NAME;
        SharedPreferencesBackupHelper helper = new SharedPreferencesBackupHelper(this, SharedPreferencesHelper.PREFS_FILE_NAME, prefsActivityFullFileName);
        addHelper(PREFS_BACKUP_KEY, helper);
    }
}
