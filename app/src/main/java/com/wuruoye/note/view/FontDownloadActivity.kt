package com.wuruoye.note.view

import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.liulishuo.filedownloader.BaseDownloadTask
import com.liulishuo.filedownloader.FileDownloadListener

import com.wuruoye.note.R
import com.wuruoye.note.base.BaseActivity
import com.wuruoye.note.model.Config
import com.wuruoye.note.model.FontCache
import com.wuruoye.note.util.FontUtil
import com.wuruoye.note.util.toast
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

    private val ivList = ArrayList<ImageView>()
    private val tvList = ArrayList<TextView>()

    private val listener = object : FileDownloadListener() {
        override fun warn(p0: BaseDownloadTask?) {
            toast("下载失败")
            isDownload = false
        }

        override fun completed(p0: BaseDownloadTask?) {
            downloadComplete()
        }

        override fun pending(p0: BaseDownloadTask?, p1: Int, p2: Int) {

        }

        override fun error(p0: BaseDownloadTask?, p1: Throwable?) {

        }

        @SuppressLint("SetTextI18n")
        override fun progress(p0: BaseDownloadTask?, p1: Int, p2: Int) {
            val i = p1.toFloat() / p2
            tvList[currentItem - 1].text = i.toString().substring(0,3) + "%"
        }

        override fun paused(p0: BaseDownloadTask?, p1: Int, p2: Int) {
            toast("下载暂停")
        }
    }

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
        for (i in Config.permission){
            if (ActivityCompat.checkSelfPermission(this,i) == PackageManager.PERMISSION_DENIED){
                ActivityCompat.requestPermissions(this, arrayOf(i),1)
            }
        }
        currentItem = item
        isDownload = true
        FontUtil.downloadFont(Config.fontNameList[item],listener)
        toast("开始下载")
    }

    private fun downloadComplete(){
        val list1 = fontCache.getFontDownloadList()
        if (list1.contains(currentItem + 1)) {
            list1.remove(currentItem + 1)
        }
        val list2 = fontCache.getFontList()
        if (!list2.contains(currentItem + 1)) {
            list2.add(currentItem + 1)
        }
        fontCache.setFontDownloadList(list1)
        fontCache.setFontList(list2)

        initLayout()

        isChange = true
        isDownload = false
    }

    private fun initLayout(){
        ll_font_download.removeAllViews()
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
