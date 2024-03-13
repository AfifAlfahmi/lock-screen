package com.afif.lockscreen

import android.Manifest
import android.annotation.SuppressLint
import android.app.KeyguardManager
import android.content.Context
import android.content.pm.*
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat


class DeviceChecker {

     fun isDeviceSecured(context: Context): Boolean {
        val keyguardManager =
            context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
         //api 16+
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            keyguardManager.isDeviceSecure
        } else keyguardManager.isKeyguardSecure
    }

     fun isBiometricSupported(context: Context): Boolean? {
        val keyguardManager = context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        val packageManager: PackageManager = context.getPackageManager()
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            Log.d("lock_device", "this Android version does not support fingerprint authentication.")
            return false
        }

        if (!packageManager.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT)) {
            Log.d("lock_device", "fingerprint Sensor not supported")

            return false;
        }
        if (ActivityCompat.checkSelfPermission(context,
                Manifest.permission.USE_BIOMETRIC) != PackageManager.PERMISSION_GRANTED)
        {
            Log.d("lock_device", "fingerprint authentication permission not enabled")
        }
        return true
    }

     fun isDefaultTrustedKeyboardJava(context: Context): Boolean {
        val defInp =
            Settings.Secure.getString(context.getContentResolver(), Settings.Secure.DEFAULT_INPUT_METHOD)
        Log.d("lock_device", "def Inp keyboard: $defInp")
        if (defInp == "com.afif.lockscreen/.MyInputMethodService") {
            Log.d("lock_device", "Our Trusted Keyboard")
            return true
        }
        if (defInp == "com.google.android.inputmethod.latin/com.android.inputmethod.latin.LatinIME" || defInp == "com.google.android.googlequicksearchbox/com.google.android.voicesearch.ime.VoiceInputMethodService") {
            Log.d("lock_device", "google Keyboard detected")
            return false
        }
        Log.d("lock_device", "Not Trusted Keyboard")
        return false
    }

     fun isDebugModeEnabled(context: Context): Boolean? {
        var isDebugEnabled = false
        isDebugEnabled = if (Settings.Secure.getInt(context.contentResolver, Settings.Secure.ADB_ENABLED, 0) == 1) {
            // debugging enabled
            Toast.makeText(context, "Debugging Mode is Enabled", Toast.LENGTH_LONG).show()
            Log.d("lock_device", "Debugging Mode is Enabled")
            true
        } else {
            //;debugging does not enabled
            Toast.makeText(context, "Debugging Mode is not Enabled", Toast.LENGTH_LONG).show()
            Log.d("lock_device", "Debugging Mode is not enabled")
            false
        }
        return isDebugEnabled
    }

    @SuppressLint("SoonBlockedPrivateApi")
    @Throws(PackageManager.NameNotFoundException::class)
    fun getRequestedPermissionsStatus(context: Context?): MutableList<PermInfo>? {
        var permissionInfoList: MutableList<PermInfo>? = mutableListOf()

        // get all installed apps with info about what permissions they requested.
        if(context != null){
        val pm: PackageManager = context.getApplicationContext().getPackageManager()

        val packageInfos = getInstalledPackages(context)


        // loop through all installed apps

        for (packageInfo in packageInfos) {
            val appName = packageInfo.applicationInfo.loadLabel(pm).toString()
            val packageName = packageInfo.packageName
            if (packageInfo.requestedPermissions == null) {
                // No permissions are requested in the AndroidManifest
                continue
            }
            val requestedPermissions = packageInfo.requestedPermissions

            // loop through all requested permissions in the AndroidManifest
            for (i in requestedPermissions.indices) {
                val requestedPerm = requestedPermissions[i]
                var permissionInfo: PermissionInfo?
                permissionInfo = try {
                    pm.getPermissionInfo(requestedPerm, 0)
                } catch (e: PackageManager.NameNotFoundException) {
                    Log.d("permissions_ui", String.format("unknown permission '%s' found in '%s'", requestedPerm, packageName))
                    continue
                }

                // convert the protectionLevel to a string (not necessary, but useful info)

                var protLevel: String
                protLevel = try {
                    pm.getPermissionInfo(requestedPerm, 0).protectionLevel.toString() + ""
                } catch (ignored: Exception) {
                    "????"
                }

                // Create the package's context to check if the package has the requested permission
//                var packageContext: Context
//                packageContext = try {
//                    context.createPackageContext(packageName, 0)
//                } catch (wtf: PackageManager.NameNotFoundException) {
//                    continue
//                }
                var gr = permissionInfo?.group
                permissionInfo?.group
//                if(gr != null){
//                    val permissionGroupInfo: PermissionGroupInfo? =
//                        pm.getPermissionGroupInfo(gr, 0)
////
////                    if (permissionGroupInfo != null){
////                        Log.d("permissionGroupInfo", "permissionGroupInfo -> ${permissionGroupInfo.loadLabel(pm)}")
////
////                    }
//                }





//                val ret = packageContext.checkCallingPermission(requestedPerm)
//                val system = protLevel === PermissionInfo.PROTECTION_SIGNATURE.toString() + ""
//                val dangerous = protLevel === PermissionInfo.PROTECTION_DANGEROUS.toString() + ""

                val isSystemApp =
                    packageInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM != 0
                val isGranted =
                    (packageInfo.requestedPermissionsFlags[i] and PackageInfo.REQUESTED_PERMISSION_GRANTED) != 0
                if(requestedPerm.contains("REQUEST_INSTALL_PACKAGES") or requestedPerm.contains("certinstaller")
                or requestedPerm.contains("packageinstaller") or requestedPerm.contains("install_status_permission")){
                    Log.d("permissions_ui", "installPerName: "+requestedPerm)
                }
                if (isGranted) {
                    if(requestedPerm.contains("install")){
                        Log.d("permissions_ui", "installPerName: "+requestedPerm)
                    }
                    Log.d("permissions_ui", String.format("app name %s package [%s] is system %s has granted permission %s (%s)",
                            appName, packageName, isSystemApp, "", protLevel
                        ))
                    permissionInfoList?.add(PermInfo(packageName, requestedPerm, true,isSystemApp))
                } else {
                    if(requestedPerm.contains("install") or requestedPerm.contains("REQUEST_INSTALL_PACKAGES")){
                        Log.d("permissions_ui", "installPerName: "+requestedPerm)
                    }
                    Log.d("permissions_ui", String.format("app name %s package [%s] is system %s is denied permission %s (%s)",
                            appName, packageName, isSystemApp, permissionInfo?.name, protLevel))
                    permissionInfoList?.add(PermInfo(packageName, requestedPerm, false,isSystemApp))

                }
            }
        }

        }

        return permissionInfoList
    }

    fun getInstalledPackages(context: Context):List<PackageInfo>{
        val pm: PackageManager = context.getApplicationContext().getPackageManager()

        // get all installed apps with info about what permissions they requested.
        val packageInfos = pm.getInstalledPackages(4098)
        return packageInfos
    }
}