package com.wuruoye.note.widget

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.widget.RelativeLayout

/**
 * Created by wuruoye on 2017/5/27.
 * this file is to do
 */

class CustomRelativeLayout : RelativeLayout {
    private var listener: OnChangeListener? = null

    public fun setOnChangeListener(listener: OnChangeListener){
        this.listener = listener
    }

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {}

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        Log.e("ruoyenote", "sizeChange")
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        if (listener != null){
            listener!!.onChange()
        }
        Log.e("ruoyenote","onLayout")
    }

    interface OnChangeListener{
        fun onChange()
    }
}
