package com.wuruoye.note.view

import android.app.ProgressDialog
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.wuruoye.note.R
import com.wuruoye.note.base.BaseActivity
import com.wuruoye.note.model.Date
import com.wuruoye.note.util.TextOutUtil
import kotlinx.android.synthetic.main.activity_show_note.*

/**
 * Created by wuruoye on 2017/6/7.
 * this file is to do
 */

class ShowNoteActivity : BaseActivity(), View.OnClickListener{
    private var fileType = 0
    private lateinit var dateFrom: Date
    private lateinit var dateTo: Date
    private lateinit var progressBar: ProgressDialog

    override val contentView: Int
        get() = R.layout.activity_show_note

    override fun initData(bundle: Bundle?) {
        fileType = bundle!!.getInt("type")
        dateFrom = bundle.getParcelable("from")
        dateTo = bundle.getParcelable("to")
    }

    override fun initView() {
        progressBar = ProgressDialog(this)
        getNoteText()

        tv_show_note_back.setOnClickListener(this)
        tv_show_note_save.setOnClickListener(this)
        tv_show_note_share.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v!!.id){
            R.id.tv_show_note_back -> {
                onBackPressed()
            }
            R.id.tv_show_note_save -> {

            }
            R.id.tv_show_note_share -> {

            }
        }
    }

    private fun getNoteText(){
        progressBar.setTitle("加载日记中...")
        progressBar.show()
        Thread({
            val string = TextOutUtil.getNoteString(applicationContext, dateFrom, dateTo)
            val stringList = TextOutUtil.getNoteStringList(applicationContext, dateFrom, dateTo)
            runOnUiThread {
                setNoteText(stringList)
                progressBar.dismiss()
//                tv_show_note.text = string
            }
        }).start()
    }

    private fun setNoteText(list: ArrayList<String>){
        ll_show_note.removeAllViews()
        for (string in list){
            val tv = LayoutInflater.from(this)
                    .inflate(R.layout.item_textview, null) as TextView
            tv.text = string
            ll_show_note.addView(tv)
        }
    }

    override fun onBackPressed() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            finishAfterTransition()
        }else {
            finish()
        }
    }

    companion object{
        val AUTHORITY = "com.wuruoye.note.fileprovider"
        val itemSaveNote = arrayOf(
                "UTF-8（推荐）", "GBK"
        )
    }
}
