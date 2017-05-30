package com.wuruoye.note.view

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import com.wuruoye.note.R
import com.wuruoye.note.base.BaseActivity
import com.wuruoye.note.model.Config
import com.wuruoye.note.model.NoteCache
import com.wuruoye.note.util.FontUtil
import kotlinx.android.synthetic.main.activity_show_font.*

/**
 * Created by wuruoye on 2017/5/30.
 * this file is to do
 */
class ShowFontActivity : BaseActivity(), View.OnClickListener{

    override val contentView: Int
        get() = R.layout.activity_show_font

    override fun initData(bundle: Bundle?) {
    }

    override fun initView() {

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

    private fun setIV(item: Int){
    }

    private fun setClick(item: Int){
        recreate()
    }
}