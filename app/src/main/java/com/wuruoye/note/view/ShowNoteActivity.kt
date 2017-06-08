package com.wuruoye.note.view

import android.app.ProgressDialog
import android.os.Build
import android.os.Bundle
import android.view.View

import com.wuruoye.note.R
import com.wuruoye.note.base.BaseActivity
import com.wuruoye.note.util.TextOutUtil
import kotlinx.android.synthetic.main.activity_show_note.*

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
    }

    override fun onClick(v: View?) {
        when (v!!.id){
            R.id.tv_show_note_back -> {
                onBackPressed()
            }
        }
    }

    private fun getNoteText(){
        progressBar.setTitle("加载日记中...")
        progressBar.show()
        Thread({
            val string = TextOutUtil.getNoteString(applicationContext)
            progressBar.dismiss()
            tv_show_note.text = string
        }).start()
    }

    override fun onBackPressed() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            finishAfterTransition()
        }else {
            finish()
        }
    }
}
