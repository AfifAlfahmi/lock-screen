package com.afif.lockscreen

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Build
import android.telephony.TelephonyManager
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat

class ConnectionInfo {

     fun isWifiConnected(context: Context): Boolean {
        val connManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
        var isWifiConnected = false
        if (mWifi!!.isConnected) {
            // Do whatever
            isWifiConnected = true
        }

        ////////
        val networkInfo = connManager.activeNetworkInfo
        if (networkInfo != null) {
            if (networkInfo.typeName == "WIFI") {
                Log.d("lock_device", "connection type: " + networkInfo.typeName)
                isWifiConnected = true
            } else {
                Log.d("lock_device", "connection type: " + networkInfo.typeName)
            }
        }
        return isWifiConnected
    }

    fun getCellularType(context: Context): String? {
        val connManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connManager.activeNetworkInfo
        var cellNetworkType = ""
        if (networkInfo != null) {
            cellNetworkType = when (networkInfo.subtype) {
                TelephonyManager.NETWORK_TYPE_GPRS, TelephonyManager.NETWORK_TYPE_EDGE, TelephonyManager.NETWORK_TYPE_CDMA, TelephonyManager.NETWORK_TYPE_1xRTT, TelephonyManager.NETWORK_TYPE_IDEN, TelephonyManager.NETWORK_TYPE_GSM -> {
                    Log.d("lock_device", "2G")
                    Toast.makeText(context, "2G", Toast.LENGTH_LONG).show()
                    "2G"
                }
                TelephonyManager.NETWORK_TYPE_UMTS, TelephonyManager.NETWORK_TYPE_EVDO_0, TelephonyManager.NETWORK_TYPE_EVDO_A, TelephonyManager.NETWORK_TYPE_HSDPA, TelephonyManager.NETWORK_TYPE_HSUPA, TelephonyManager.NETWORK_TYPE_HSPA, TelephonyManager.NETWORK_TYPE_EVDO_B, TelephonyManager.NETWORK_TYPE_EHRPD, TelephonyManager.NETWORK_TYPE_HSPAP, TelephonyManager.NETWORK_TYPE_TD_SCDMA -> {
                    Log.d("lock_device", "3G")
                    Toast.makeText(context, "3G", Toast.LENGTH_LONG).show()
                    "3G"
                }
                TelephonyManager.NETWORK_TYPE_LTE, TelephonyManager.NETWORK_TYPE_IWLAN, 19 -> {
                    Log.d("lock_device", "4G")
//                    Toast.makeText(context, "4G", Toast.LENGTH_LONG).show()
                    "4G"
                }
                TelephonyManager.NETWORK_TYPE_NR -> {
                    Log.d("lock_device", "5G")
                    Toast.makeText(context, "5G", Toast.LENGTH_LONG).show()
                    "5G"
                }
                else -> {
                    Log.d("lock_device", "unknown network")
                    "unknown"
                }
            }
        }
        return cellNetworkType
    }
     @RequiresApi(Build.VERSION_CODES.S)
     fun getWifiName(context: Context): String? {
        getNetworkCallBack(context)

         val wifiManager =
            context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiInfo = wifiManager.connectionInfo
        return wifiInfo.ssid



    }
    private fun getNetworkCallBack(context: Context): ConnectivityManager.NetworkCallback {
        return object : ConnectivityManager.NetworkCallback() {
            @RequiresApi(Build.VERSION_CODES.Q)
            override fun onAvailable(network: Network) {    //when Wifi is on
                super.onAvailable(network)
                val cm: ConnectivityManager = context.getSystemService<ConnectivityManager>(
                    ConnectivityManager::class.java
                )
                val netCaps: NetworkCapabilities? = cm.getNetworkCapabilities(network)
                val info = netCaps!!.transportInfo as WifiInfo?
                var ssid = info?.getSSID();


                Toast.makeText(context, "Wifi is on! "+ssid, Toast.LENGTH_SHORT).show()
                Log.d("lock_device", "wifi name k: "+ssid)

            }

            override fun onLost(network: Network) {    //when Wifi 【turns off】
                super.onLost(network)

                Toast.makeText(context, "Wifi turns off!", Toast.LENGTH_SHORT).show()
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.S)
    fun getWifiSecurityType(context: Context): String? {
        var securityType = 0
        var typeResult = "open"
        val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiInfo = wifiManager.connectionInfo
        securityType = wifiInfo.currentSecurityType
        Log.w("lock_device", "currentSecurityType: $securityType")
        if (securityType != 0 && securityType != -1) {
            typeResult = "secure"
        }
        return typeResult
    }
}