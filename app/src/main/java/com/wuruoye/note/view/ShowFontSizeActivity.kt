package com.wuruoye.note.view

import android.app.Activity
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.widget.NumberPicker

import com.wuruoye.note.R
import com.wuruoye.note.base.BaseActivity
import com.wuruoye.note.model.FontCache
import kotlinx.android.synthetic.main.activity_font_size.*

/**
 * Created by wuruoye on 2017/6/17.
 * this file is to do
 */

class ShowFontSizeActivity : BaseActivity() {
    private lateinit var fontCache: FontCache
    private var fontSize = 0f

    override val contentView: Int
        get() = R.layout.activity_font_size

    override fun initData(bundle: Bundle?) {
        fontCache = FontCache(this)
        fontSize = fontCache.fontSize
    }

    override fun initView() {
        tv_font_size.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontCache.fontSize)
        tv_font_size_back.setOnClickListener { onBackPressed() }

        deleteDivider(np_font_size)
        np_font_size.displayedValues = itemSize
        np_font_size.minValue = 0
        np_font_size.maxValue = itemSize.size - 1
        np_font_size.value = itemSize.indexOf(fontCache.fontSize.toInt().toString())
        np_font_size.isFocusable = true
        np_font_size.isFocusableInTouchMode = true
        np_font_size.setOnScrollListener { _, _ ->
            setSize(itemSize[np_font_size.value].toFloat())
        }
    }

    override fun onBackPressed() {
        if (fontCache.fontSize != fontSize){
            setResult(Activity.RESULT_OK)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            finishAfterTransition()
        }else {
            finish()
        }
    }

    private fun setSize(size: Float){
        tv_font_size.setTextSize(TypedValue.COMPLEX_UNIT_SP, size)
        fontCache.fontSize = size
        setFontSize(size / 15)
    }

    private fun deleteDivider(picker: NumberPicker){
        val pickerFields = NumberPicker::class.java.declaredFields
        for (j in pickerFields){
            if (j.name == "mSelectionDivider"){
                j.isAccessible = true
                j.set(picker, ColorDrawable())
            }
        }
    }

    private fun setFontSize(scale: Float){
        val config = resources.configuration
        config.fontScale = scale
        resources.updateConfiguration(config, resources.displayMetrics)
    }

    companion object {
        val itemSize = arrayOf(
                "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20"
        )
    }
}
