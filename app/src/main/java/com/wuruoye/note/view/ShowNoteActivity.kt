package com.wuruoye.note.view

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.v4.content.FileProvider
import android.support.v7.app.AlertDialog
import android.view.View

import com.wuruoye.note.R
import com.wuruoye.note.base.BaseActivity
import com.wuruoye.note.model.Date
import com.wuruoye.note.util.Extensions.toast
import com.wuruoye.note.util.TextOutUtil
import kotlinx.android.synthetic.main.activity_show_note.*
import java.io.File

/**
 * Created by wuruoye on 2017/6/7.
 * this file is to do
 */

class ShowNoteActivity : BaseActivity(), View.OnClickListener{
    private var fileType = 0
    private lateinit var dateFrom: Date
    private lateinit var dateTo: Date
    private lateinit var progressBar: ProgressDialog
    private var saveDialog: AlertDialog.Builder? = null

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
                saveNote()
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
            val string = TextOutUtil.getNoteString(applicationContext, dateFrom, dateTo)
            runOnUiThread {
                progressBar.dismiss()
                tv_show_note.text = string
            }
        }).start()
    }

    private fun saveNote(){
        val string = tv_show_note.text.toString()
        TextOutUtil.outToText(string, object : TextOutUtil.TextOutListener{
            override fun onOutSuccess(path: String) {
                showSaveDialog(path)
            }

            override fun onOutFail(message: String) {
                toast(message)
            }
        })
    }

    private fun shareNote(){
        val string = tv_show_note.text.toString()
        val intent = Intent(Intent.ACTION_SEND)
        intent.putExtra(Intent.EXTRA_TEXT, string)
        intent.type = "text/*"
        startActivity(Intent.createChooser(intent, "分享日记到:"))
    }

    private fun showSaveDialog(path: String){
        if (saveDialog == null){
            saveDialog = AlertDialog.Builder(this)
                    .setTitle("导出完成\n是否打开？")
                    .setPositiveButton("确定", { _, _ ->
                        val intent = Intent(Intent.ACTION_VIEW)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            intent.flags = Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
                        }
                        intent.setDataAndType(FileProvider.getUriForFile(this, AUTHORITY, File(path)), "application/txt")
                        startActivity(intent)

                    })
                    .setNegativeButton("取消", { _, _ -> })
        }
        saveDialog!!.show()
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
