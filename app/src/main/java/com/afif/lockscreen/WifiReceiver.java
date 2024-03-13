package com.afif.lockscreen;

import android.Manifest;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import java.util.List;

public class WifiReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        WifiManager wifiManager = (WifiManager)
                context.getSystemService(Context.WIFI_SERVICE);

        Log.d("lock_device", "BroadcastReceiver onReceive action: "+action);
        if (action.equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
            String downloadPath = intent.getStringExtra(DownloadManager.COLUMN_URI);
            Log.d("lock_device", "onReceive ACTION_DOWNLOAD_COMPLETE");
        }

        if (action.equals(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION)) {
            if (intent.getBooleanExtra(WifiManager.EXTRA_SUPPLICANT_CONNECTED, false)) {
                Log.d("lock_device", "onReceive EXTRA_SUPPLICANT_CONNECTED");
                //do stuff
                NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);

            } else {
                // wifi connection was lost
            }

        }
        if (action.equals("android.net.wifi.STATE_CHANGE")) {
            NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            Log.d("lock_device", "wifi changed " );


            if (info != null && info.isConnected()) {
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                String ssid = wifiInfo.getSSID();
                Log.d("lock_device", "wifi name: " + ssid);
            }
        }


        else {
            Log.d("lock_device", "scan action");

            boolean success = intent.getBooleanExtra(
                    WifiManager.EXTRA_RESULTS_UPDATED, false);
            if (success) {
                scanSuccess(context, wifiManager);
            } else {
                // scan failure handling
                scanFailure(context,wifiManager);
            }

        }

    }

    private void scanSuccess(Context context, WifiManager wifiManager) {
        Log.d("lock_device", "scanSuccess");

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            List<ScanResult> results = wifiManager.getScanResults();
            Log.d("lock_device", "results length: "+results.size());

            for (ScanResult network : results)
            {
                String Capabilities =  network.capabilities;
                Log.w ("lock_device", network.SSID + " capabilities : " + Capabilities);
            }
            return;
        }
    }

    private void scanFailure(Context context,WifiManager wifiManager) {

        Log.d("lock_device", "scanFailure");
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        List<ScanResult> results = wifiManager.getScanResults();
    }

}

