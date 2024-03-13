package com.afif.lockscreen

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PermInfo(var packageInfo:String,var permissionName: String, var isGranted :Boolean,var isSystemApp :Boolean):Parcelable
