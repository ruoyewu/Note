package com.wuruoye.note.util

import android.annotation.TargetApi
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build

/**
 * Created by wuruoye on 2017/9/13.
 * this file is to do
 */
class PermissionRequestUtil(
        private val activity: Activity
) {

    public fun requestPermission(permissions: Array<String>): Boolean{
        var isOk = true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            for (i in permissions){
                if (activity.checkSelfPermission(i) == PackageManager.PERMISSION_DENIED){
                    isOk = false
                    activity.requestPermissions(permissions, 0)
                }
            }
        }
        return isOk
    }
}