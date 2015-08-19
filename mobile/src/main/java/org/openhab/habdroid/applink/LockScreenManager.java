package org.openhab.habdroid.applink;

import android.content.Intent;

import org.openhab.habdroid.applink.LockScreenActivity;
import org.openhab.habdroid.core.HABDroid;

/**
 * This class manages the lockscreen for the app.
 */
public class LockScreenManager {
	// variable to contain the current state of the lockscreen
	private static boolean lockScreenUp = false;
	
	public static synchronized void showLockScreen() {
		// only show the lockscreen if main activity is currently on top
		// else, wait until onResume() to show the lockscreen so it doesn't 
		// pop-up while a user is using another app on the phone
		if (HABDroid.getCurrentActivity() != null) {
			if (((AppLinkActivity) HABDroid.getCurrentActivity()).isActivityonTop() == true) {
				Intent i = new Intent(HABDroid.getInstance(), LockScreenActivity.class);
				i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				i.addFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION);
				HABDroid.getInstance().startActivity(i);
			}
		}
		lockScreenUp = true;		
	}

	public static synchronized void clearLockScreen() {
		if (LockScreenActivity.getInstance() != null) {  
			LockScreenActivity.getInstance().exit();
		}
		lockScreenUp = false;
	}
	
	public static synchronized boolean getLockScreenStatus() {
		return lockScreenUp;
	}	
}
