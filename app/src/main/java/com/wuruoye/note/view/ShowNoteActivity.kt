package com.wuruoye.note.view

import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ShareCompat
import android.support.v4.content.FileProvider
import android.support.v4.view.ViewCompat
import android.support.v7.app.AlertDialog
import android.view.View

import com.wuruoye.note.R
import com.wuruoye.note.base.BaseActivity
import com.wuruoye.note.model.Config
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
        var isOk = true
        for (i in Config.permission){
            if (ActivityCompat.checkSelfPermission(this,i) == PackageManager.PERMISSION_DENIED){
                isOk = false
                ActivityCompat.requestPermissions(this, arrayOf(i),1)
            }
        }
        if (isOk) {
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
    }

    private fun shareNote(){
        val string = tv_show_note.text.toString()
        val intent = Intent(Intent.ACTION_SEND)
        intent.putExtra(Intent.EXTRA_TEXT, string)
        intent.type = "text/*"
        startActivity(Intent.createChooser(intent, "分享日记到:"))
    }

    private fun showSaveDialog(path: String){
//        if (saveDialog == null){
//            saveDialog = AlertDialog.Builder(this)
//                    .setTitle("导出完成,请到\n根目录/outNote/文件夹查看")
//                    .setPositiveButton("确定", { _, _ -> })
//                    .setNegativeButton("取消", { _, _ -> })
//        }
        if (saveDialog == null){
            saveDialog = AlertDialog.Builder(this)
                    .setTitle("导出完成,路径为\n根目录/noteOut/")
                    .setPositiveButton("确定", { _, _ ->
//                        val intent = Intent(Intent.ACTION_VIEW)
//                        intent.addCategory(Intent.CATEGORY_DEFAULT)
//                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//                        intent.setDataAndType(FileProvider.getUriForFile(this, AUTHORITY, File(path)), "text/*")
//                        startActivity(intent)
                    })
//                    .setNegativeButton("取消", { _, _ -> })
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
