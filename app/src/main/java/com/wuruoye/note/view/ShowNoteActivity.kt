package com.wuruoye.note.view

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.v4.content.FileProvider
import android.view.View

import com.wuruoye.note.R
import com.wuruoye.note.base.BaseActivity
import com.wuruoye.note.util.TextOutUtil
import kotlinx.android.synthetic.main.activity_show_note.*
import java.io.File

/**
 * Created by wuruoye on 2017/6/7.
 * this file is to do
 */

class ShowNoteActivity : BaseActivity(), View.OnClickListener{
    private var fileType = 0
    private lateinit var progressBar: ProgressDialog

    override val contentView: Int
        get() = R.layout.activity_show_note

    override fun initData(bundle: Bundle?) {
        fileType = bundle!!.getInt("type")
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
                shareNote()
            }
        }
    }

    private fun getNoteText(){
        progressBar.setTitle("加载日记中...")
        progressBar.show()
        Thread({
            val string = TextOutUtil.getNoteString(applicationContext)
            runOnUiThread {
                progressBar.dismiss()
                tv_show_note.text = string
            }
        }).start()
    }

    private fun shareNote(){
        val string = tv_show_note.text.toString()
        val intent = Intent(Intent.ACTION_SEND)
        intent.putExtra(Intent.EXTRA_TEXT, string)
        intent.type = "text/*"
        startActivity(Intent.createChooser(intent, "分享日记到:"))
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
    }
}
