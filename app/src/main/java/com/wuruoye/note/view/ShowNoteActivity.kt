package com.wuruoye.note.view

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.wuruoye.note.R
import com.wuruoye.note.base.BaseActivity
import com.wuruoye.note.model.Date
import com.wuruoye.note.util.TextOutUtil
import com.wuruoye.note.util.toast
import kotlinx.android.synthetic.main.activity_show_note.*
import kotlinx.android.synthetic.main.activity_write.*

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
                val text = tv_show_note.text.toString()
                progressBar.setTitle("保存日记中...")
                progressBar.show()
                Thread{
                    TextOutUtil.outToText(text, object : TextOutUtil.TextOutListener{
                        override fun onOutSuccess(path: String) {
                            progressBar.dismiss()
                            val p = path.removePrefix(Environment.getExternalStorageDirectory().absolutePath + "/")
                            runOnUiThread {
                                progressBar.dismiss()
                                toast("请到 $p 查看文件")
                            }
                        }

                        override fun onOutFail(message: String) {
                            runOnUiThread {
                                progressBar.dismiss()
                                toast(message)
                            }
                        }

                    })
                }.start()
            }
            R.id.tv_show_note_share -> {
                val bitmap = getViewBitmap(tv_show_note)
                val path = MediaStore.Images.Media.insertImage(contentResolver, bitmap, "分享", null)
                val uri = Uri.parse(path)
                val intent = Intent(Intent.ACTION_SEND)
                intent.type = "image/*"
                intent.putExtra(Intent.EXTRA_STREAM, uri)
                startActivity(intent)
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
//                setNoteText(stringList)
                setNoteText(string)
                progressBar.dismiss()
//                tv_show_note.text = string
            }
        }).start()
    }

    private fun setNoteText(text: String){
        tv_show_note.text = text
    }

    private fun setNoteText(list: ArrayList<String>){
        ll_show_note.removeAllViews()
        for (string in list){
            val tv = LayoutInflater.from(this)
                    .inflate(R.layout.item_textview, null) as TextView
            tv.text = string
            val layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            tv.layoutParams = layoutParams
            ll_show_note.addView(tv)
        }
    }

    private fun getViewBitmap(v: View): Bitmap {
        val b = Bitmap.createBitmap(v.width, v.height, Bitmap.Config.ARGB_8888)
        val c = Canvas(b)
        val bgDrawable = v.background
        if (bgDrawable != null){
            bgDrawable.draw(c)
        }else {
            c.drawColor(Color.WHITE)
        }
        v.draw(c)
        return b
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
