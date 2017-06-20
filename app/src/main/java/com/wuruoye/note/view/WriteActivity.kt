package com.wuruoye.note.view

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.PorterDuff
import android.graphics.Rect
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.FileProvider
import android.support.v7.app.AlertDialog
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.LinearLayout
import com.transitionseverywhere.Slide
import com.transitionseverywhere.TransitionManager
import com.umeng.analytics.MobclickAgent
import com.wuruoye.note.R
import com.wuruoye.note.base.BaseActivity
import com.wuruoye.note.base.IAbsView
import com.wuruoye.note.model.Config
import com.wuruoye.note.model.Note
import com.wuruoye.note.model.NoteCache
import com.wuruoye.note.presenter.ImageGet
import com.wuruoye.note.util.*
import com.wuruoye.note.util.Extensions.toast
import com.wuruoye.note.view.ShowNoteActivity.Companion.AUTHORITY
import com.wuruoye.note.widget.CustomRelativeLayout
import kotlinx.android.synthetic.main.activity_write.*
import java.io.File

/**
 * Created by wuruoye on 2017/5/27.
 * this file is to do
 */
class WriteActivity : BaseActivity(), View.OnClickListener ,CustomRelativeLayout.OnChangeListener{
    //the view show the background image
    private lateinit var ivBk: ImageView

    //the current note being edit
    private lateinit var note: Note
    //the paper color we choose  {from 0 to paper.size}
    private var paperColor = 0
    //the date this note belong {year年month月day日}
    private lateinit var date: String
    //judge if open the view to choose paper
    private var isShowPaper = false
    //the gravity of the text in edit view {1,left; 2,center; 3,right}
    private var mDirect = 1
    //the background image's name {note_yera-month-day}
    private var fileName = ""
    //show if we changed the bk image
    private var isChangeImage = false
    //show if the note has a bk image
    private var haveBk = false
    //NoteCahce
    private lateinit var noteCache: NoteCache
    //if we open the auto save, it can show if we change the text of note when we close this activity
    private var isSave = false

    private lateinit var imageGet: ImageGet
    private val imageListener = object : IAbsView<Bitmap>{
        override fun setModel(model: Bitmap) {
            runOnUiThread {
                haveBk = true
                ivBk.setImageBitmap(model)
                iv_write.setImageBitmap(model)
                iv_write.setColorFilter(ActivityCompat.getColor(applicationContext,Config.paperStyle[paperColor]),
                        PorterDuff.Mode.MULTIPLY)
            }
        }

        override fun setWorn(message: String) {
            runOnUiThread {
                toast(message)
            }
        }

    }

    override val contentView: Int
        get() = R.layout.activity_write

    override fun initData(bundle: Bundle?) {
        if (bundle != null){
            note = bundle.getParcelable("note")
        }else{
            note = Note(NoteUtil.getYear(),NoteUtil.getMonth(),NoteUtil.getDay(),NoteUtil.getWeek())
        }
        noteCache = NoteCache(this)
        paperColor = note.style
        mDirect = note.direct
        date = Config.yearList[note.year - FIRST_YEAR] + "年" +
                Config.numList[note.month] + "月" +
                Config.numList[note.day] + "日"
        fileName = "note_${note.year}-${note.month}-${note.day}"
        if (note.bkImage != ""){
            haveBk = true
        }
    }

    override fun initPresenter() {
        imageGet = ImageGet(this)
        presenterList.add(imageGet)
        viewList.add(imageListener)
        super.initPresenter()
    }

    override fun initView() {
        initPapers()
        tv_write_date.text = date
        iv_write.setColorFilter(ActivityCompat.getColor(this,Config.paperStyle[paperColor]),PorterDuff.Mode.MULTIPLY)
        if (note.bkImage != ""){
            setBackground()
        }

        tv_write_direct.text = if (mDirect == 1) "左" else if (mDirect == 2) "中" else "右"
        et_write.gravity = if (mDirect == 3) Gravity.END else if (mDirect == 2) Gravity.CENTER_HORIZONTAL else Gravity.START
        et_write.setText(note.content)
        et_write.setSelection(note.content.length)
        ssv_write.post {
            ssv_write.smoothScrollTo(0, 0)
        }

        activity_write.setOnChangeListener(this)
        et_write.setOnClickListener(this)
        tv_write_back.setOnClickListener(this)
        tv_write_direct.setOnClickListener(this)
        tv_write_paper.setOnClickListener(this)
        tv_write_submit.setOnClickListener(this)
        tv_write_time.setOnClickListener(this)

        if (noteCache.isAutoSave){
            autoSave()
        }
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
            }
            R.id.et_write -> {
                hsv_paper.visibility = View.GONE
                isShowPaper = false
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
            isChangeImage = true
            when (requestCode){
                OPEN_ALBUM -> {
                    toast("正在修改中...")
                    val path = FilePathUtil.getPathFromUri(this, data!!.data)
                    imageGet.writeFile(File(path), fileName)
                }
                OPEN_CAMERA -> {
                    toast("正在修改中...")
                    imageGet.writeFile(File(Config.imagePath + fileName), fileName)
                }
            }
        }
    }

    private fun closeActivity(){
        if (saveNote(true) || isSave)
            setResult(Activity.RESULT_OK)

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

    private fun showAddBackground(){
        val item = if (haveBk) imageItem2 else imageItem
        AlertDialog.Builder(this)
                .setTitle("选择获取图片方式")
                .setItems(item, { _, position ->
                    when (position){
                        0 -> {
                            openAlbum()
                        }
                        1 -> {
                            openCamera()
                        }
                        2 -> {
                            clearBack()
                        }
                    }
                })
                .show()
    }

    private fun setBackground() {
        imageGet.readFile(fileName)
    }

    private fun openAlbum(){
        if (requestPermission()) {
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
    }

    private fun openCamera(){
        if (requestPermission()) {
            val directory = File(Config.imagePath)
            if (!directory.exists()){
                directory.mkdirs()
            }
            val file = File(Config.imagePath + fileName)
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
    }

    private fun clearBack(){
        haveBk = false
        isChangeImage = true
        ivBk.setImageResource(R.drawable.ic_add)
        iv_write.setImageResource(R.drawable.paper)
        iv_write.setColorFilter(ActivityCompat.getColor(this@WriteActivity,Config.paperStyle[paperColor]),PorterDuff.Mode.MULTIPLY)
    }

    private fun initPapers(){
        val param = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.MATCH_PARENT)
        param.width = 500
        ivBk = ImageView(this)
        ivBk.setImageResource(R.drawable.ic_add)
        ivBk.layoutParams = param
        ivBk.scaleType = ImageView.ScaleType.CENTER_CROP
        ivBk.setOnClickListener({
            showAddBackground()
        })
        ll_write_paper.addView(ivBk)
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

    private fun requestPermission(): Boolean{
        var isOk = true
        for (i in Config.permissionWrite){
            if (ActivityCompat.checkSelfPermission(this,i) == PackageManager.PERMISSION_DENIED){
                isOk = false
                ActivityCompat.requestPermissions(this, Config.permissionWrite, 1)
            }
        }
        return isOk
    }

    private fun saveNote(isClose: Boolean): Boolean{
        if (note.content != et_write.text.toString() ||
            note.style != paperColor ||
            note.direct != mDirect ||
            isChangeImage) {
            isSave = true
            note.content = et_write.text.toString()
            note.style = paperColor
            note.direct = mDirect
            if (isChangeImage){
                if (haveBk){
                    note.bkImage = fileName
                }else{
                    note.bkImage = ""
                }
            }
            if (note.content == "" && note.style == 0){
                SQLiteUtil.deleteNote(this,note)
            }else{
                SQLiteUtil.saveNote(this,note)
                if (isClose) {
                    if (NoteCache(this).isAutoBackup){
                        Thread({
                            BackupUtil.upNote(applicationContext,note)
                        }).start()
                    }
                }
            }
            return true
        }else
            return false
    }

    private fun autoSave(){
        et_write.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                saveNote(false)
            }

        })
    }

    companion object{
        val FIRST_YEAR = 2013
        val OPEN_ALBUM = 1
        val OPEN_CAMERA = 2
        val imageItem = arrayOf(
                "打开相册",
                "打开相机"
        )
        val imageItem2 = arrayOf(
                "打开相册",
                "打开相机",
                "清除背景"
        )
        val permission = arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    }
}