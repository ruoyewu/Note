package com.wuruoye.note.view

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityOptionsCompat
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import com.transitionseverywhere.Slide
import com.transitionseverywhere.TransitionManager
import com.umeng.analytics.MobclickAgent
import com.wuruoye.note.R
import com.wuruoye.note.base.App
import com.wuruoye.note.base.BaseActivity
import com.wuruoye.note.model.Config
import com.wuruoye.note.model.FontCache
import com.wuruoye.note.util.FontUtil
import com.wuruoye.note.util.toast
import kotlinx.android.synthetic.main.activity_show_font.*

/**
 * Created by wuruoye on 2017/5/30.
 * this file is to do
 */
class ShowFontActivity : BaseActivity(), View.OnClickListener{
    private lateinit var fontCache: FontCache
    private var lastFont: Int = 0
    private lateinit var fontList: ArrayList<Int>

    private val llItem = ArrayList<LinearLayout>()
    private val ivList = ArrayList<ImageView>()

    override val contentView: Int
        get() = R.layout.activity_show_font

    override fun initData(bundle: Bundle?) {
        fontCache = FontCache(this)
        lastFont = fontCache.font
        fontList = FontUtil.getFontFromStorage()
    }

    override fun initView() {
        initFontShow()

        if (fontCache.font > 0){
            val item = fontList.indexOf(fontCache.font)
            setIV(item)
        }
        tv_font_set_back.setOnClickListener(this)
        tv_font_download.setOnClickListener(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK){
            recreate()
        }
    }

    override fun onClick(v: View?) {
        when (v!!.id){
            R.id.tv_font_set_back -> {
                onBackPressed()
            }
            R.id.tv_font_download -> {
                val intent = Intent(this,FontDownloadActivity::class.java)
                val compat = ActivityOptionsCompat.makeSceneTransitionAnimation(this,
                        tv_font_set_back,getString(R.string.translate_note_button))
                ActivityCompat.startActivityForResult(this,intent, DOWNLOAD_FONT,compat.toBundle())
            }
            else ->{
                setClick(v.tag as Int)
            }
        }
    }

    override fun onBackPressed() {
        if (lastFont != fontCache.font){
            setResult(Activity.RESULT_OK)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            finishAfterTransition()
        }else{
            finish()
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        outState!!.putInt("lastFont", lastFont)
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        lastFont = savedInstanceState!!.getInt("lastFont")
    }

    private fun initFontShow(){
        TransitionManager.beginDelayedTransition(ll_font_show,Slide(Gravity.BOTTOM))
        for (i in 0..fontList.size - 1){
            @SuppressLint("InflateParams")
            val llView = LayoutInflater.from(this).inflate(R.layout.item_font_show,null) as LinearLayout
            val iv = llView.findViewById<ImageView>(R.id.iv_font_show) as ImageView
            val ivv = llView.findViewById<ImageView>(R.id.iv_font_select) as ImageView
            iv.setImageResource(Config.fontList[fontList[i] - 1])
            ivList.add(ivv)
            iv.tag = i
            iv.setOnClickListener(this)
            llItem.add(llView)
            ll_font_show.addView(llView)
        }
    }

    private fun setIV(item: Int){
        for (i in ivList){
            i.setImageResource(0)
        }
        ivList[item].setImageResource(R.drawable.ic_select)
    }

    private fun setClick(item: Int){
        val num = fontList[item]
        val name = Config.fontNameList[num - 1]
        try {
            val map = HashMap<String,String>()
            map.put("font",name)
            MobclickAgent.onEvent(this,"font_click",map)

            App.mTypeFace = FontUtil.setFont(this,false,name)
            fontCache.font = num
        } catch(e: Exception) {
            toast("字体加载出错")
            FontUtil.deleteFontFromStorage(name)
            fontList = FontUtil.getFontFromStorage()
            initFontShow()
        }
        recreate()
    }

    companion object{
        val DOWNLOAD_FONT = 1
    }
}