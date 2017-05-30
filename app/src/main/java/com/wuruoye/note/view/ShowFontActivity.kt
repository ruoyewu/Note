package com.wuruoye.note.view

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import com.wuruoye.note.R
import com.wuruoye.note.base.BaseActivity
import com.wuruoye.note.model.Config
import com.wuruoye.note.model.NoteCache
import kotlinx.android.synthetic.main.activity_show_font.*
import uk.co.chrisjenx.calligraphy.CalligraphyConfig
import uk.co.chrisjenx.calligraphy.CalligraphyUtils

/**
 * Created by wuruoye on 2017/5/30.
 * this file is to do
 */
class ShowFontActivity : BaseActivity(), View.OnClickListener{
    private lateinit var noteCache: NoteCache
    private var fontShow = 0
    private val llFontShow = ArrayList<LinearLayout>()
    private val ivFontShow = ArrayList<ImageView>()

    override val contentView: Int
        get() = R.layout.activity_show_font

    override fun initData(bundle: Bundle?) {
        noteCache = NoteCache(this)
        fontShow = noteCache.fontShow
    }

    override fun initView() {
        llFontShow.add(ll_font_set_1)
        llFontShow.add(ll_font_set_2)
        ivFontShow.add(iv_font_set_1)
        ivFontShow.add(iv_font_set_2)

        ivFontShow[fontShow].setImageResource(R.drawable.ic_select)

        for (i in 0..llFontShow.size - 1){
            llFontShow[i].tag = i
            llFontShow[i].setOnClickListener(this)
        }

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

    private fun setClick(item: Int){
        CalligraphyConfig.initDefault(
                CalligraphyConfig.Builder()
                        .setDefaultFontPath(Config.fontList[item])
                        .build()
        )
        noteCache.fontShow = item
        activity_font_set.invalidate()

        for (i in ivFontShow){
            i.setImageResource(0)
        }
        ivFontShow[item].setImageResource(R.drawable.ic_select)
    }
}