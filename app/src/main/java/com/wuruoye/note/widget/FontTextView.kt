package com.wuruoye.note.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.TextView
import com.wuruoye.note.base.App

/**
 * Created by wuruoye on 2017/11/12.
 * this file is to do
 */

class FontTextView : android.support.v7.widget.AppCompatTextView {

    private fun init() {
        if (App.mTypeFace != null){
            typeface = App.mTypeFace
        }
    }

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }
}
