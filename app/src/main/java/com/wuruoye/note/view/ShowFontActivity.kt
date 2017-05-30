package com.wuruoye.note.view

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import com.wuruoye.note.R
import com.wuruoye.note.base.BaseActivity
import com.wuruoye.note.model.Config
import com.wuruoye.note.model.FontCache
import com.wuruoye.note.model.NoteCache
import com.wuruoye.note.util.FontUtil
import com.wuruoye.note.util.dp2px
import com.wuruoye.note.util.toast
import kotlinx.android.synthetic.main.activity_show_font.*

/**
 * Created by wuruoye on 2017/5/30.
 * this file is to do
 */
class ShowFontActivity : BaseActivity(), View.OnClickListener{
    private lateinit var fontCache: FontCache

    private val llItem = ArrayList<LinearLayout>()
    private val ivItem = ArrayList<ImageView>()

    override val contentView: Int
        get() = R.layout.activity_show_font

    override fun initData(bundle: Bundle?) {
        fontCache = FontCache(this)
    }

    override fun initView() {
        initFontShow()

        tv_font_set_back.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v!!.id){
            R.id.tv_font_set_back -> {
                onBackPressed()
            }
            else ->{
                setClick(v.tag as Int)
            }
        }
    }

    override fun onBackPressed() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            finishAfterTransition()
        }else{
            finish()
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
    }

    private fun initFontShow(){
        val ivLayout = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT)
        ivLayout.width = 0
        ivLayout.weight = 1f
        val length = dp2px(25f)
        val ivvLayout = LinearLayout.LayoutParams(length,length)
        val llLayout = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT)

        val list = fontCache.getFontList()
        for (i in list){
            val iv = ImageView(this)
            iv.tag = i - 1
            iv.setImageResource(Config.fontList[i - 1])
            iv.layoutParams = ivLayout
            iv.setOnClickListener(this)

            val ivv = ImageView(this)
            ivv.layoutParams = ivvLayout
            ivv.setImageResource(R.drawable.ic_select)

            val ll = LinearLayout(this)
            ll.orientation = LinearLayout.HORIZONTAL
            ll.gravity = Gravity.CENTER_VERTICAL
            ll.layoutParams = llLayout

            ll.addView(iv)
            ll.addView(ivv)
        }
    }

    private fun setIV(item: Int){

    }

    private fun setClick(item: Int){
        toast(item.toString())
    }
}