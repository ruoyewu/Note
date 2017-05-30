package com.wuruoye.note.util

import android.content.Context
import android.support.v4.app.Fragment
import android.util.TypedValue
import android.widget.Toast

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

/**
 * 单位转换
 */
fun Context.dp2px(dp: Float): Int{
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
            dp,resources.displayMetrics).toInt()
}