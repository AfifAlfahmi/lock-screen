package com.afif.lockscreen;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import android.Manifest;
import android.app.DownloadManager;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.net.TransportInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

//import com.acp.jbrd.NativeCore;
//import com.acp.jbrd.RootDetector;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private ContentObserver downloadObserver;
    EditText etName;

    @RequiresApi(api = Build.VERSION_CODES.S)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        NativeCore nativeCore = new NativeCore();
//
//        //setContentView(binding.getRoot());
//
//        RootDetector rootDetector = null;
//
//        try {
//            rootDetector = new RootDetector(getApplicationContext().getPackageManager());
//            rootDetector.getConsolidatedIsRooted(null,false);
//            Log.d("rootjbrd","getSuStatus :"+nativeCore.getBadApps().length);
//            Log.d("rootjbrd","checkBusybox :"+nativeCore.checkBusybox());
//            Log.d("rootjbrd","checkBadPermission :"+nativeCore.checkBadPermission());
//            Log.d("rootjbrd","getSuDirStatus :"+nativeCore.getSuDirStatus());
//            Log.d("rootjbrd","accessFiles :"+nativeCore.accessFiles());
//            Log.d("rootjbrd","checkDebug :"+nativeCore.checkDebug());
//            Log.d("rootjbrd","hiddenFiles :"+nativeCore.hiddenFiles());
//            Log.d("rootjbrd","checkBadApps :"+nativeCore.getBadApps());
//            Log.d("rootjbrd","checkManyUnread :"+nativeCore.checkManyUnread());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        rootDetector.getBadApps();


        ConnectionInfo connectionInfo = new ConnectionInfo();
        DeviceChecker deviceChecker = new DeviceChecker();
        WifiManager wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);

        Button btnLock = findViewById(R.id.btn_lock);
        Button btnEnable = findViewById(R.id.btn_enable);
        Button btnPerms = findViewById(R.id.btn_perms);
        etName = findViewById(R.id.et_name);
        ImageView imgKeygaurd = findViewById(R.id.img_keyguard);
        ImageView imgDebugMode = findViewById(R.id.img_debug_mode);
        ImageView imgKeyboard = findViewById(R.id.img_keyboard);
        ImageView imgWifi = findViewById(R.id.img_wifi_sec);


        //etName.setOnTouchListener(exitSoftKeyBoard);
         //etName.setShowSoftInputOnFocus(false);
         //etName.getShowSoftInputOnFocus();

        //startService();
        registerConnReceiver();
        Log.d("dwonload_det","periodicWorkRequest");

        startDownObserverWorker();

        deviceChecker.isBiometricSupported(this);

        if (deviceChecker.isDeviceSecured(this)) {
            Log.d("lock_device", "device protected using passcode");
            imgKeygaurd.setImageResource(R.drawable.ts);
        } else {
            imgKeygaurd.setImageResource(R.drawable.ws);
            Log.d("lock_device", "device not protected using passcode");
        }

        if(!deviceChecker.isDebugModeEnabled(this)){
            imgDebugMode.setImageResource(R.drawable.ts);
        }
        else{
            imgDebugMode.setImageResource(R.drawable.ws);
        }
        if(deviceChecker.isDefaultTrustedKeyboardJava(this)){
            imgKeyboard.setImageResource(R.drawable.ts);
        }
        else{
            imgKeyboard.setImageResource(R.drawable.ws);
        }


        DevicePolicyManager devicePolicyManager = (DevicePolicyManager) this.getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName adminComponentName = new ComponentName(this, MyDeviceAdminReceiver.class);

        btnEnable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, adminComponentName);
                intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "enable admin");
                startActivityForResult(intent, 1);
            }
        });



        btnLock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (devicePolicyManager.isAdminActive(adminComponentName)) {
                    Log.d("lock_device", "active admin: " + devicePolicyManager.getActiveAdmins());
                    devicePolicyManager.lockNow();
                } else {
                    Log.d("lock_device", "not active admin");
                }
            }
        });
        Intent intent = new Intent(this,KotActivity.class);

        btnPerms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(intent);
            }
        });



        if (connectionInfo.isWifiConnected(this)) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Log.w ("lock_device",  "ACCESS_COARSE_LOCATION permision not granted" );

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);

            }
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Log.w ("lock_device",  "ACCESS_FINE_LOCATION permision not granted" );

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION
                ,Manifest.permission.ACCESS_WIFI_STATE,Manifest.permission.CHANGE_WIFI_STATE,Manifest.permission.DUMP,Manifest.permission.ACCESS_BACKGROUND_LOCATION}, 1);

            }
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_WIFI_STATE) != PackageManager.PERMISSION_GRANTED) {
                Log.w ("lock_device",  "ACCESS_WIFI_STATE permision not granted" );

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_WIFI_STATE}, 1);

            }
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CHANGE_WIFI_STATE) != PackageManager.PERMISSION_GRANTED) {
                Log.w ("lock_device",  "CHANGE_WIFI_STATE permision not granted" );

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CHANGE_WIFI_STATE}, 1);

            }
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.DUMP) != PackageManager.PERMISSION_GRANTED) {
                Log.w ("lock_device",  "DUMP permision not granted" );

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.DUMP}, 1);

            }
                Log.w ("lock_device",  "wifi name: "+getWifiName());
                Log.w ("lock_device",  "reslut current wifi Security Type: "+connectionInfo.getWifiSecurityType(this) );
                if(connectionInfo.getWifiSecurityType(this).equals("secure")){
                    imgWifi.setImageResource(R.drawable.ts);
                }
                else{
                    imgWifi.setImageResource(R.drawable.ws);
                }

        } else {
            connectionInfo.getCellularType(this);
            Log.d("lock_device", "wifi not connected");
        }


        wifiManager.startScan();
        boolean success = wifiManager.startScan();
        if (success) {
            // scan failure handling
            Log.d("lock_device", "Main scan success");
        }
        else{
            Log.d("lock_device", "Main scan not success");
        }



    }


    @RequiresApi(api = Build.VERSION_CODES.R)
    private void accessDownloadsFolder() throws IOException {

        StorageManager storageManager = (StorageManager) getSystemService(STORAGE_SERVICE);
        StorageVolume storageVolume = storageManager.getStorageVolumes().get(0);
        File down_det = new File(storageVolume.getDirectory().getPath()+"/Download");
        File file = new File(Environment.getExternalStorageDirectory()+"/Download/download_detection.txt");
        File[] files = file.listFiles();
        String[] listOfFiles = Environment.getExternalStoragePublicDirectory (Environment.DIRECTORY_DOWNLOADS).list();
        Log.d("lock_device", "FileName:" + file.getPath());
        BufferedReader reader = new BufferedReader(new FileReader(file));
        StringBuilder textBuilder = new StringBuilder();
        String line;
        while((line = reader.readLine()) != null) {
            textBuilder.append(line);
            Log.d("lock_device", "FileName line:" + line);

            textBuilder.append("\n");

        }

    }



    private void startDownObserverWorker(){
        PeriodicWorkRequest periodicWorkRequest =
                new PeriodicWorkRequest.Builder(DownloadObserveWorker.class,
                        15, TimeUnit.MINUTES,
                        15,TimeUnit.MINUTES)
                        .build();
        WorkManager.getInstance().enqueue(periodicWorkRequest);

    }
    private void stopDownloadObserver() {
        if (downloadObserver != null) {
            getContentResolver().unregisterContentObserver(downloadObserver);
            downloadObserver = null;
        }
    }

    private void registerConnReceiver() {

        Uri uri = Uri.parse("https://drive.google.com/file/d/17sodUDT1rXhHwEkIOF0cWtErPeH2SJG-/view?usp=drive_link");
//        DownloadManager.Request request = new DownloadManager.Request(uri);
//        request.setDescription("Your App Title").setTitle("");
//        request.setDestinationInExternalFilesDir(this, Environment.DIRECTORY_DOWNLOADS, "<file title>");
//        request.setVisibleInDownloadsUi(false); //the content will not shown in Download Manager App
//        mydownlodreference = downloadManager.enqueue(request);

        // Create request for android download manager
//        DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
//        DownloadManager.Request request = new DownloadManager.Request(uri);
//        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI |
//                DownloadManager.Request.NETWORK_MOBILE);
//
//        // set title and description
//        request.setTitle("Data Download");
//        request.setDescription("Android Data download using DownloadManager.");
//
//        request.allowScanningByMediaScanner();
//        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
//
//        //set the local destination for download file to a path within the application's external files directory
//        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,"downfileName");
//        request.setMimeType("*/*");
//        downloadManager.enqueue(request);


        IntentFilter filter = new IntentFilter();
        filter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        WifiReceiver wifiReceiver = new WifiReceiver();
        registerReceiver(wifiReceiver, filter);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        registerReceiver(wifiReceiver, intentFilter);


        IntentFilter scanIntentFilter = new IntentFilter();
        scanIntentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        registerReceiver(wifiReceiver, filter);


    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private String getWifiName() {

        ConnectivityManager cm =
                this.getSystemService(ConnectivityManager.class);
        Network n = cm.getActiveNetwork();
        NetworkInfo networkInfo = cm.getNetworkInfo(n);
        networkInfo.getExtraInfo();


        NetworkCapabilities netCaps = cm.getNetworkCapabilities(n);
        WifiInfo info = (WifiInfo) netCaps.getTransportInfo();

//        String ssid = wifiInfo.getSSID();
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        String ssid = wifiInfo.getSSID()+"";

        new ConnectivityManager.NetworkCallback(ConnectivityManager.NetworkCallback.FLAG_INCLUDE_LOCATION_INFO) {

            @Override
            public void onCapabilitiesChanged(Network network, NetworkCapabilities networkCapabilities) {
                Log.w ("lock_device",  "onCapabilitiesChanged: ");

                final TransportInfo transportInfo = networkCapabilities.getTransportInfo();
                if (!(transportInfo instanceof WifiInfo)) return;
                final WifiInfo wifiInfo = (WifiInfo) transportInfo;
                String ssid = wifiInfo.getSSID();
                Log.w ("lock_device",  "ssid: "+ssid);

            }

            @Override
            public void onAvailable(@NonNull Network network) {
                super.onAvailable(network);
                Log.w ("lock_device",  "onAvailable: "+ssid);

//                final TransportInfo transportInfo = network.getAllByName("d");
//                if (!(transportInfo instanceof WifiInfo)) return;
//                final WifiInfo wifiInfo = (WifiInfo) transportInfo;

            }
        };

        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkRequest.Builder builder = new NetworkRequest.Builder();

        connectivityManager.registerDefaultNetworkCallback(new ConnectivityManager.NetworkCallback(ConnectivityManager.NetworkCallback.FLAG_INCLUDE_LOCATION_INFO){
            @Override
            public void onAvailable(Network network) {
                Log.w ("lock_device",  "new onAvailable : ");
                NetworkCapabilities netCaps  = connectivityManager.getNetworkCapabilities(network);
                WifiInfo wifiInfo = (WifiInfo)netCaps.getTransportInfo();

                String newSSID = wifiInfo.getSSID();
                Log.w ("lock_device",  "ssid: "+newSSID);


            }
            @Override
            public void onLost(Network network) {
            }
        });


        return ssid;
    }



    public  List<PackageInfo> getPackagesListSHell(int i) {
        Throwable th;
        Exception e;
        ArrayList arrayList = new ArrayList();

//        m8153d(AppsUtils.class, "getInstalledPackages");
        PackageManager m20009i = getApplicationContext().getPackageManager();
//        try {
//            return m20009i.getInstalledPackages(i);
//        } catch (Exception e2) {
//            Log.i("Permission", String.format("catch getPackagesListSHell: "));

            //m8150g("AppUtils", "getInstalledPackages - getInstalledPackages from pm", e2);
            BufferedReader bufferedReader = null;
            try {
                try {
                    try {
                        Process exec = Runtime.getRuntime().exec("pm list packages");
                        BufferedReader bufferedReader2 = new BufferedReader(new InputStreamReader(exec.getInputStream()));
                        while (true) {
                            try {
                                String readLine = bufferedReader2.readLine();
                                if (readLine == null) {
                                    break;
                                }
                                arrayList.add(m20009i.getPackageInfo(readLine.substring(readLine.indexOf(58) + 1), i));
                            } catch (Exception e3) {
                                e = e3;
                                bufferedReader = bufferedReader2;
                                //m8150g("AppUtils", "getInstalledPackages", e);
                                if (bufferedReader != null) {
                                    bufferedReader.close();
                                }
                                Collections.sort(arrayList, v13.f18645n);
                                return arrayList;
                            } catch (Throwable th2) {
                                th = th2;
                                bufferedReader = bufferedReader2;
                                if (bufferedReader != null) {
                                    try {
                                        bufferedReader.close();
                                    } catch (IOException e4) {
                                        //m8150g("AppUtils", "getInstalledPackages - closing buffer", e4);
                                    }
                                }
                                throw th;
                            }
                        }
                        exec.waitFor();
                        bufferedReader2.close();
                    } catch (IOException e5) {
                        //m8150g("AppUtils", "getInstalledPackages - closing buffer", e5);
                        Collections.sort(arrayList, v13.f18645n);
                        return arrayList;
                    }
                } catch (Exception e6) {
                    e = e6;
                }
                Collections.sort(arrayList, v13.f18645n);
                return arrayList;
            } catch (Throwable th3) {
                th = th3;
            }
        //}
        return arrayList;
    }

//    public void m8153d(Object obj, String str) {
//        m8155b(3, obj, str, null, false);
//    }
//
//    public final void m8155b(int i, Object obj, String str, Throwable th, boolean z) {
//        if (z && str != null) {
//            int ceil = (int) Math.ceil(str.length() / 700.0f);
//            m8154c(3, "L", "The next message is broken into " + ceil + " individual chunks due to length " + str.length() + ".", null);
//            int i2 = 0;
//            while (i2 < ceil) {
//                int i3 = i2 * 700;
//                i2++;
//                int i4 = i2 * 700;
//                if (i4 > str.length() - 1) {
//                    i4 = str.length() - 1;
//                }
//                m8154c(i, m8147j(obj) + " [Chunk " + i2 + "]", str.substring(i3, i4), null);
//            }
//            if (th == null) {
//                return;
//            }
//            m8156a(i, obj, "Exception for above message: ", th);
//            return;
//        }
//        m8156a(i, obj, str, th);
//    }
//
//    public static void m8150g(Object obj, String str, Throwable th) {
//        m8149h(obj, str, th, false);
//        if (th == null) {
//            m8151f(obj, "Null Error was provided, unexpectedly");
//        }
//    }




    private final View.OnTouchListener exitSoftKeyBoard = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
//            InputMethodManager imm = (InputMethodManager)getApplicationContext().getSystemService(android.content.Context.INPUT_METHOD_SERVICE);
//            KeyboardView keyboardView = (KeyboardView) getLayoutInflater().inflate(R.layout.keyboard_view, null);
//            Keyboard keyboard = new Keyboard(getApplicationContext(), R.xml.keys_layout);
//            keyboardView.setKeyboard(keyboard);

            //etName.setInputType(InputType.TYPE_NULL); // Disable standard keyboard
            //keyboardView.setVisibility(View.VISIBLE);

            //imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            //etName.requestFocus();

//            if(v.equals(edtxtName)){
//                edtxtName.requestFocus();
//                relLayKeyboard.setVisibility(View.VISIBLE);
//            }
            return true;
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopDownloadObserver();
    }




}