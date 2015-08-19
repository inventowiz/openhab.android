package org.openhab.habdroid.core;

import android.app.Activity;
import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.util.Log;

import com.ford.syncV4.proxy.SyncProxyALM;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import org.openhab.habdroid.applink.AppLinkService;

import java.util.HashMap;

/**
 * Created by belovictor on 26/01/15.
 */

public class HABDroid extends Application {

    public enum TrackerName {
        APP_TRACKER // Tracker used only in this app.
    }
    private HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();
    private static final String PROPERTY_ID = "UA-39285202-1";
    public static final String TAG = "HABDroid";

    public synchronized Tracker getTracker(TrackerName trackerId) {
        if (!mTrackers.containsKey(trackerId)) {
            Log.d(TAG, "GoogleAnalytics.getInstance");
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            analytics.setDryRun(false);
            Log.d(TAG, "newTracker");
            Tracker t = analytics.newTracker(PROPERTY_ID);
//            Tracker t = (trackerId == TrackerName.APP_TRACKER) ? analytics.newTracker(PROPERTY_ID)
//                    : (trackerId == TrackerName.GLOBAL_TRACKER) ? analytics.newTracker(R.xml.global_tracker)
//                    : analytics.newTracker(R.xml.ecommerce_tracker);
            Log.d(TAG, "Tracker done");
            mTrackers.put(trackerId, t);

        }
        return mTrackers.get(trackerId);
    }

    /**
     * Applink Section
     */
    private static HABDroid instance;
    private static Activity currentUIActivity;

    static {
        instance = null;
    }

    private static synchronized void setInstance(HABDroid app) { instance = app; }
    public static synchronized HABDroid getInstance() { return instance; }
    public static synchronized void setCurrentActivity(Activity act) {
        currentUIActivity = act;
    }
    public static synchronized Activity getCurrentActivity() {
        return currentUIActivity;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        HABDroid.setInstance(this);
    }

    public void startSyncProxyService() {
        // Get the local Bluetooth adapter
        BluetoothAdapter mBtAdapter = BluetoothAdapter.getDefaultAdapter();

        // If BT adapter exists, is enabled, and there are paired devices, start service/proxy
        if (mBtAdapter != null)
        {
            if ((mBtAdapter.isEnabled() && mBtAdapter.getBondedDevices().isEmpty() == false))
            {
                Intent startIntent = new Intent(this, AppLinkService.class);
                startService(startIntent);
            }
        }
    }

    // Recycle the proxy
    public void endSyncProxyInstance() {
        AppLinkService serviceInstance = AppLinkService.getInstance();
        if (serviceInstance != null){
            SyncProxyALM proxyInstance = serviceInstance.getProxy();
            // if proxy exists, reset it
            if(proxyInstance != null){
                serviceInstance.reset();
                // if proxy == null create proxy
            } else {
                serviceInstance.startProxy();
            }
        }
    }

    // Stop the AppLinkService
    public void endSyncProxyService() {
        AppLinkService serviceInstance = AppLinkService.getInstance();
        if (serviceInstance != null){
            serviceInstance.stopService();
        }
    }

}
