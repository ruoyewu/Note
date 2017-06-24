package com.wuruoye.note.view

import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AlertDialog
import android.view.View

import com.wuruoye.note.R
import com.wuruoye.note.base.BaseActivity
import com.wuruoye.note.model.Config
import com.wuruoye.note.model.Date
import com.wuruoye.note.util.Extensions.toast
import com.wuruoye.note.util.FileUtil
import com.wuruoye.note.util.NoteUtil
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
        if (requestPermission()) {
            AlertDialog.Builder(this)
                    .setTitle("请选择编码方式")
                    .setItems(itemSaveNote) { _, which ->
                        var toast = "保存失败"
                        val text = tv_show_note.text.toString()
                        val name = NoteUtil.getDate() + ".txt"
                        val path = Config.outDirect + name
                        when (which){
                            0 -> {
                                if (FileUtil.writeTextUTF8(path, text)){
                                    toast = "保存成功, 请到 noteOut/$path 查看"
                                }
                            }
                            1 -> {
                                if (FileUtil.writeTextGBK(path, text)){
                                    toast = "保存成功, 请到 noteOut/$name 查看"
                                }
                            }
                        }
                        toast(toast)
                    }
                    .show()
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

    }

    private fun requestPermission(): Boolean{
        var isOk = true
        for (i in Config.permissionWrite){
            if (ActivityCompat.checkSelfPermission(this,i) == PackageManager.PERMISSION_DENIED){
                isOk = false
                ActivityCompat.requestPermissions(this, arrayOf(i),1)
            }
        }
        return isOk
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
