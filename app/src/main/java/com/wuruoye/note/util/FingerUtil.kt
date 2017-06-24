package com.wuruoye.note.util

import android.content.Context
import android.hardware.fingerprint.FingerprintManager
import android.os.Build

/**
 * Created by wuruoye on 2017/6/24.
 * this file is to do
 */

object FingerUtil {

    @Throws(Exception::class)
    fun isFingerAvailable(context: Context): FingerprintManager {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M){
            val manager = context.getSystemService(Context.FINGERPRINT_SERVICE) as FingerprintManager
            if (manager.isHardwareDetected){
                if (manager.hasEnrolledFingerprints()){
                    return manager
                }else{
                    throw Exception("暂无可用指纹，请到手机设置添加指纹")
                }
            }else {
                throw Exception("请在有指纹识别的手机上使用")
            }
        }else {
            throw Exception("仅支持android6.0以上机型")
        }
    }
}
