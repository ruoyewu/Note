package com.wuruoye.note.view

import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.PackageManager
import android.graphics.PorterDuff
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.liulishuo.filedownloader.BaseDownloadTask
import com.liulishuo.filedownloader.FileDownloadListener
import com.transitionseverywhere.Slide
import com.transitionseverywhere.TransitionManager

import com.wuruoye.note.R
import com.wuruoye.note.base.BaseActivity
import com.wuruoye.note.model.Config
import com.wuruoye.note.model.FontCache
import com.wuruoye.note.util.FontUtil
import com.wuruoye.note.util.toast
import com.wuruoye.note.widget.ProcessView
import kotlinx.android.synthetic.main.activity_font_download.*

/**
 * Created by wuruoye on 2017/5/30.
 * this file is to do
 */

class FontDownloadActivity : BaseActivity() ,View.OnClickListener{
    private lateinit var fontCache: FontCache
    private var currentItem = 0
    private var isChange = false
    private var isDownload = false
    private var backColor = 0

    private val ivList = ArrayList<ImageView>()
    private val tvList = ArrayList<TextView>()
    private val llList = ArrayList<LinearLayout>()
    private val pvList = ArrayList<ProcessView>()

    private val listener = object : FileDownloadListener() {
        override fun warn(p0: BaseDownloadTask?) {
        }

        override fun completed(p0: BaseDownloadTask?) {
            toast("下载完成")
            downloadComplete()
        }

        override fun pending(p0: BaseDownloadTask?, p1: Int, p2: Int) {
        }

        override fun error(p0: BaseDownloadTask?, p1: Throwable?) {
            isDownload = false
            toast(p1!!.message.toString())
        }

        @SuppressLint("SetTextI18n")
        override fun progress(p0: BaseDownloadTask?, p1: Int, p2: Int) {
            val i = p1.toFloat() / p2 * 100
            tvList[currentItem].text = i.toString().substring(0,4) + "%"

            pvList[currentItem].setProcess(i)
        }

        override fun paused(p0: BaseDownloadTask?, p1: Int, p2: Int) {
            toast("下载暂停")
        }
    }

    override val contentView: Int
        get() = R.layout.activity_font_download

    override fun initData(bundle: Bundle?) {
        fontCache = FontCache(this)
        backColor = ActivityCompat.getColor(this,R.color.athens_gray)
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
                if (!isDownload) {
                    downloadFont(v.tag as Int)
                }
            }
        }
    }

    override fun onBackPressed() {
        if (isChange)
            setResult(Activity.RESULT_OK)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            finishAfterTransition()
        }else{
            finish()
        }
    }

    private fun downloadFont(item: Int){
        var isOk = true
        for (i in Config.permission){
            if (ActivityCompat.checkSelfPermission(this,i) == PackageManager.PERMISSION_DENIED){
                isOk = false
                ActivityCompat.requestPermissions(this, arrayOf(i),1)
            }
        }
        if (isOk) {
            currentItem = item
            isDownload = true
            FontUtil.downloadFont(Config.fontNameList[item],listener)
            pvList[currentItem].visibility = View.VISIBLE
            toast("开始下载,请稍后...")
        }
    }

    private fun downloadComplete(){
        TransitionManager.beginDelayedTransition(ll_font_download,Slide(Gravity.BOTTOM))
        val item = ivList[currentItem].tag as Int + 1
        val list2 = fontCache.getFontList()
        if (!list2.contains(item)) {
            list2.add(item)
        }
        fontCache.setFontList(list2)

        tvList[currentItem].text = "已下载"
        ivList[currentItem].setColorFilter(backColor,PorterDuff.Mode.DARKEN)
        llList[currentItem].setBackgroundColor(backColor)

        isChange = true
        isDownload = false
        pvList[currentItem].visibility = View.GONE
    }

    private fun initLayout(){
        ll_font_download.removeAllViews()
        ivList.clear()
        tvList.clear()
        llList.clear()
        pvList.clear()
        val listToDownload = fontCache.getFontDownloadList()
        val listDownloaded = fontCache.getFontList()
        for (i in 0..listToDownload.size - 1){
            @SuppressLint("InflateParams")
            val llView = LayoutInflater.from(this).inflate(R.layout.item_font_download,null) as LinearLayout
            val iv = llView.findViewById(R.id.iv_font_download) as ImageView
            val tv = llView.findViewById(R.id.tv_font_download) as TextView
            val pv = llView.findViewById(R.id.pv_font_download) as ProcessView
            iv.tag = i
            iv.setImageResource(Config.fontList[listToDownload[i] - 1])
            pv.setColor(backColor)
            ivList.add(iv)
            tvList.add(tv)
            llList.add(llView)
            pvList.add(pv)
            ll_font_download.addView(llView)

            if (!listDownloaded.contains(listToDownload[i])) {
                iv.setOnClickListener(this)
            }else{
                llView.setBackgroundColor(backColor)
                iv.setColorFilter(backColor,PorterDuff.Mode.DARKEN)
                tv.text = "已下载"
            }
        }
    }
}
