package com.wuruoye.note.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.PorterDuff
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.FileProvider
import android.support.v7.app.AlertDialog
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ImageSpan
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import com.transitionseverywhere.Slide
import com.transitionseverywhere.TransitionManager
import com.umeng.analytics.MobclickAgent
import com.wuruoye.note.R
import com.wuruoye.note.base.BaseActivity
import com.wuruoye.note.model.Config
import com.wuruoye.note.model.Note
import com.wuruoye.note.model.NoteCache
import com.wuruoye.note.util.BackupUtil
import com.wuruoye.note.util.BitmapOverlay
import com.wuruoye.note.util.NoteUtil
import com.wuruoye.note.util.SQLiteUtil
import com.wuruoye.note.view.ShowNoteActivity.Companion.AUTHORITY
import com.wuruoye.note.widget.CustomRelativeLayout
import kotlinx.android.synthetic.main.activity_write.*
import java.io.File

/**
 * Created by wuruoye on 2017/5/27.
 * this file is to do
 */
class WriteActivity : BaseActivity(), View.OnClickListener ,CustomRelativeLayout.OnChangeListener{
    private lateinit var note: Note
    private var paperColor = 0
    private lateinit var date: String
    private var isShowPaper = false
    private var mDirect = 1
    private var fileName = ""


    override val contentView: Int
        get() = R.layout.activity_write

    override fun initData(bundle: Bundle?) {
        if (bundle != null){
            note = bundle.getParcelable("note")
        }else{
            note = Note(NoteUtil.getYear(),NoteUtil.getMonth(),NoteUtil.getDay(),NoteUtil.getWeek())
        }
        paperColor = note.style
        mDirect = note.direct
        date = Config.yearList[note.year - FIRST_YEAR] + "年" +
                Config.numList[note.month] + "月" +
                Config.numList[note.day] + "日"
    }

    override fun initView() {
        initPapers()
        tv_write_date.text = date
        iv_write.setColorFilter(ActivityCompat.getColor(this,Config.paperStyle[paperColor]),PorterDuff.Mode.MULTIPLY)

        tv_write_direct.text = if (mDirect == 1) "左" else if (mDirect == 2) "中" else "右"
        et_write.gravity = if (mDirect == 1) Gravity.START else if (mDirect == 2) Gravity.CENTER_HORIZONTAL else Gravity.END
        et_write.setText(note.content)
        et_write.setSelection(note.content.length)

        activity_write.setOnChangeListener(this)
        et_write.setOnClickListener(this)
        tv_write_back.setOnClickListener(this)
        tv_write_direct.setOnClickListener(this)
        tv_write_paper.setOnClickListener(this)
        tv_write_submit.setOnClickListener(this)
        tv_write_time.setOnClickListener(this)

//        val text = "ruoye"
//        val bitmap = BitmapFactory.decodeResource(resources,R.drawable.yuanti)
//        val imageSpan = ImageSpan(this,bitmap)
//        val span = SpannableString(text)
//        span.setSpan(imageSpan, 0, text.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
//        et_write.append(span)
    }

    override fun onClick(v: View?) {
        when (v!!.id){
            R.id.tv_write_back -> {
                closeActivity()
            }
            R.id.tv_write_time -> {
                val time = NoteUtil.getTime()
                val position = et_write.selectionStart
                et_write.text.insert(position,time)
            }
            R.id.tv_write_paper -> {
                closeInputMethod()
                if (!isShowPaper) {
                    hsv_paper.visibility = View.VISIBLE
                    isShowPaper = true
                }
                else{
                    hsv_paper.visibility = View.GONE
                    isShowPaper = false
                }
            }
            R.id.tv_write_direct -> {
                if (mDirect == 1){
                    mDirect = 2
                    tv_write_direct.text = "中"
                    et_write.gravity = Gravity.CENTER_HORIZONTAL
                }else if (mDirect == 2){
                    mDirect = 3
                    tv_write_direct.text = "右"
                    et_write.gravity = Gravity.END
                }else{
                    mDirect = 1
                    tv_write_direct.text = "左"
                    et_write.gravity = Gravity.START
                }
            }
            R.id.tv_write_submit -> {
                closeInputMethod()
                closeActivity()
//                TransitionManager.beginDelayedTransition(activity_write,Slide(Gravity.BOTTOM))
//                hsv_paper.visibility = View.GONE
//                ll_write_edit.visibility = View.GONE
//                tv_write_back.visibility = View.VISIBLE
//                isShowPaper = false
            }
            R.id.et_write -> {
                hsv_paper.visibility = View.GONE
            }
            else -> {
                val color = v.tag as Int
                paperColor = color
                iv_write.setColorFilter(ActivityCompat.getColor(this,Config.paperStyle[paperColor]),PorterDuff.Mode.MULTIPLY)

                val map = HashMap<String, String>()
                map.put("color",color.toString())
                MobclickAgent.onEvent(this,"paper_click",map)
            }
        }
    }

    override fun onBackPressed() {
        if (isShowPaper){
            isShowPaper = false
            hsv_paper.visibility = View.GONE
        }else {
            closeActivity()
        }
    }

    override fun onChange() {
        changeMargin()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK){
            when (requestCode){
                OPEN_ALBUM -> {
                    setBackground(data!!.data)
                }
                OPEN_CAMERA -> {
                    setBackground(FileProvider.getUriForFile(this, AUTHORITY, File(fileName)))
                }
            }
        }
    }

    private fun closeActivity(){
        if (note.content != et_write.text.toString() ||
                note.style != paperColor ||
                note.direct != mDirect) {
            note.content = et_write.text.toString()
            note.style = paperColor
            note.direct = mDirect
            if (note.content == "" && note.style == 0){
                SQLiteUtil.deleteNote(this,note)
            }else{
                SQLiteUtil.saveNote(this,note)
                if (NoteCache(this).backup){
                    BackupUtil.upNote(applicationContext,note)
                }
            }
            setResult(Activity.RESULT_OK)
        }

        if (Build.VERSION.SDK_INT > 21) {
            finishAfterTransition()
        }else{
            finish()
        }
    }

    private fun changeMargin(){
        TransitionManager.beginDelayedTransition(activity_write,Slide(Gravity.BOTTOM))

        val rect = Rect()
        activity_write.rootView.getWindowVisibleDisplayFrame(rect)
        val margin = activity_write.rootView.height - rect.bottom

        if (!isShowPaper) {
            ll_write_edit.visibility = if (margin == 0) View.GONE else View.VISIBLE
            tv_write_back.visibility = if (margin == 0) View.VISIBLE else View.GONE
        }

        val param = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT)
        param.setMargins(0,0,0,margin)
        ll_write_edit.layoutParams = param
    }

    private fun addBackground(){
        AlertDialog.Builder(this)
                .setTitle("选择获取图片方式")
                .setItems(imageItem, { _, position ->
                    when (position){
                        0 -> {
                            openAlbum()
                        }
                        1 -> {
                            openCamera()
                        }
                    }
                })
                .show()
    }

    private fun setBackground(uri: Uri){
        val bmp1 = BitmapFactory.decodeResource(resources, R.drawable.paper)
        val bmp2 = BitmapFactory.decodeFile(fileName)
        val bmp3 = BitmapOverlay.overlay(bmp1, bmp2, this)
        iv_write.setImageBitmap(bmp3)
    }

    private fun openAlbum(){
        val intent: Intent =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    Intent(Intent.ACTION_OPEN_DOCUMENT)
                } else {
                    Intent(Intent.ACTION_GET_CONTENT)
                }
        intent.type = "image/*"
        intent.addCategory(Intent.CATEGORY_OPENABLE)

        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, OPEN_ALBUM)
        }
    }

    private fun openCamera(){
        fileName = Config.imagePath + "note_" + System.currentTimeMillis().toString() + ".jpg"
        val directory = File(Config.imagePath)
        if (!directory.exists()){
            directory.mkdirs()
        }
        val file = File(fileName)
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val uri: Uri =
        if (Build.VERSION.SDK_INT < 21) {
            Uri.fromFile(file)
        } else {
            FileProvider.getUriForFile(applicationContext, AUTHORITY, file)
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
        startActivityForResult(intent, OPEN_CAMERA)
    }

    private fun initPapers(){
        val param = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.MATCH_PARENT)
        param.width = 500
        val addImage = ImageView(this)
        addImage.setImageResource(R.drawable.ic_add)
        addImage.layoutParams = param
        addImage.scaleType = ImageView.ScaleType.CENTER_CROP
        addImage.setOnClickListener({
            addBackground()
        })
        ll_write_paper.addView(addImage)
        for (i in 0..Config.paperStyle.size - 1){
            val image = ImageView(this)
            image.tag = i
            image.scaleType = ImageView.ScaleType.CENTER_CROP
            image.setImageResource(R.drawable.paper)
            image.setColorFilter(ActivityCompat.getColor(this,Config.paperStyle[i]),PorterDuff.Mode.MULTIPLY)
            image.setOnClickListener(this)
            image.layoutParams = param
            ll_write_paper.addView(image)
        }
    }

    private fun closeInputMethod(){
        val view = window.peekDecorView()
        if (view != null){
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken,0)
        }
    }

    companion object{
        val FIRST_YEAR = 2013
        val OPEN_ALBUM = 1
        val OPEN_CAMERA = 2
        val imageItem = arrayOf(
                "打开相册",
                "打开相机"
        )
    }
}