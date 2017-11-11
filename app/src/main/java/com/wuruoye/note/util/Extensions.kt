package com.wuruoye.note.util

import android.content.Context
import android.support.v4.app.Fragment
import android.util.Log
import android.util.TypedValue
import android.widget.Toast
import java.util.regex.Pattern

/**
 * Created by wuruoye on 2017/5/27.
 * this file is to do
 */
    fun Context.toast(message: String){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show()
    }
    fun Fragment.toast(message: String){
        context.toast(message)
    }

    fun loge(message: String){
        Log.e("wuruoye", message)
    }

    /**
     * 单位转换
     */
    fun Context.dp2px(dp: Float): Int{
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dp,resources.displayMetrics).toInt()
    }

    fun String.isPhone(): Boolean{
        val p = Pattern.compile("^1(3[0-9]|4[579]|5[0-3,5-9]|7[0135678]|8[0-9])\\d{8}$")
        val m = p.matcher(this)
        return m.matches()
    }