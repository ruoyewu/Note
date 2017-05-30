package com.wuruoye.note.view

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

import com.wuruoye.note.R
import com.wuruoye.note.base.BaseActivity
import com.wuruoye.note.model.Config
import com.wuruoye.note.model.FontCache
import com.wuruoye.note.util.toast
import kotlinx.android.synthetic.main.activity_font_download.*

/**
 * Created by wuruoye on 2017/5/30.
 * this file is to do
 */

class FontDownloadActivity : BaseActivity() ,View.OnClickListener{
    private lateinit var fontCache: FontCache

    private val ivList = ArrayList<ImageView>()
    private val tvList = ArrayList<TextView>()

    override val contentView: Int
        get() = R.layout.activity_font_download

    override fun initData(bundle: Bundle?) {
        fontCache = FontCache(this)
    }

    override fun initView() {
        initLayout()

        tv_font_download_back.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v!!.id){
            R.id.tv_font_download_back -> {
                onBackPressed()
            }
            else -> {
                val item = v.tag as Int
                toast(item.toString())
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

    private fun initLayout(){
        val list = fontCache.getFontDownloadList()
        for (i in list){
            @SuppressLint("InflateParams")
            val llView = LayoutInflater.from(this).inflate(R.layout.item_font_download,null) as LinearLayout
            val iv = llView.findViewById(R.id.iv_font_download) as ImageView
            val tv = llView.findViewById(R.id.tv_font_download) as TextView
            iv.setOnClickListener(this)
            iv.tag = i - 1
            iv.setImageResource(Config.fontList[i - 1])
            ivList.add(iv)
            tvList.add(tv)
            ll_font_download.addView(llView)
        }
    }
}
