package com.afif.lockscreen

import android.os.Build
import android.widget.EditText
import java.lang.reflect.Method


    fun EditText.shouldShowSoftInputOnFocus(show: Boolean) {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP -> {
                this.showSoftInputOnFocus = show
            }
            Build.VERSION.SDK_INT == Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1 -> {
                val method: Method = EditText::class.java.getMethod(
                    "setSoftInputShownOnFocus"
                    , *arrayOf<Class<*>?>(Boolean::class.javaPrimitiveType))
                method.isAccessible = true
                method.invoke(this, show)
            }
            else -> {
                val method: Method = EditText::class.java.getMethod(
                    "setShowSoftInputOnFocus"
                    , *arrayOf<Class<*>?>(Boolean::class.javaPrimitiveType))
                method.isAccessible = true
                method.invoke(this, show)
            }
        }
    }
