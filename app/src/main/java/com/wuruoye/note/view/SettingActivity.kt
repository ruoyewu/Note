package com.wuruoye.note.view

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.PorterDuff
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.graphics.drawable.DrawableCompat
import android.support.v7.app.AlertDialog
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.droi.sdk.core.DroiUser
import com.wuruoye.note.R
import com.wuruoye.note.base.BaseActivity
import com.wuruoye.note.base.IAbsView
import com.wuruoye.note.model.*
import com.wuruoye.note.presenter.NoteGet
import com.wuruoye.note.util.NoteUtil
import com.wuruoye.note.util.PermissionRequestUtil
import com.wuruoye.note.util.toast
import kotlinx.android.synthetic.main.activity_setting.*

/**
 * Created by wuruoye on 2017/5/29.
 * this file is to do
 */
class SettingActivity : BaseActivity(), View.OnClickListener, CompoundButton.OnCheckedChangeListener{
    private lateinit var noteGet: NoteGet
    private lateinit var noteCache: NoteCache
    private lateinit var appCache: AppCache
    //save the state if we should refresh main activity, if true, yes
    private var isChange = false
    //save the date of the first note we write
    private lateinit var dateFrom: Date
    //save the date of the last note we write
    private lateinit var dateTo: Date
    //save if there is no note written
    private var isNoteNull = false

    private val passView = ArrayList<ImageButton>()
    private lateinit var passEdit: EditText
    private lateinit var passDialog: AlertDialog
    private var isFirst = true
    private var previous = ""

    private var noteView = object : IAbsView<ArrayList<Note>>{
        override fun setModel(model: ArrayList<Note>) {
            val noteFrom: Note
            val noteTo: Note
            if (model.size > 0) {
                noteFrom = model[0]
                noteTo = model[model.size - 1]
                dateFrom = Date(noteFrom.year, noteFrom.month, noteFrom.day)
                dateTo = Date(noteTo.year, noteTo.month, noteTo.day)
            }else{
                isNoteNull = true
            }
            setNote(model)
        }

        override fun setWorn(message: String) {
            toast(message)
        }
    }

    override val contentView: Int
        get() = R.layout.activity_setting

    override fun initData(bundle: Bundle?) {
        noteCache = NoteCache(this)
        appCache = AppCache(this)
    }

    override fun initView() {
        initDialog()
        noteGet.requestAllNote()

        switch_backup.isChecked = noteCache.isAutoBackup
        switch_auto_save.isChecked = noteCache.isAutoSave
        switch_lock.isChecked = noteCache.isLock

        ll_setting_show.setOnClickListener(this)
        ll_setting_font.setOnClickListener(this)
        ll_setting_feedback.setOnClickListener(this)
        tv_setting_back.setOnClickListener(this)
        ll_setting_user.setOnClickListener(this)
        ll_setting_backup.setOnClickListener(this)
        ll_setting_out.setOnClickListener(this)
        ll_setting_state.setOnClickListener(this)
        ll_setting_font_size.setOnClickListener(this)
        ll_setting_lock.setOnClickListener(this)
        switch_backup.setOnCheckedChangeListener(this)
        switch_auto_save.setOnCheckedChangeListener(this)
        switch_lock.setOnClickListener { changeLock() }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode){
            CHANGE_ITEM -> {
                if (resultCode == Activity.RESULT_OK){
                    isChange = true
                }
            }
            CHANGE_FONT -> {
                if (resultCode == Activity.RESULT_OK){
                    isChange = true
                    recreate()
                }
            }
            USER_MANAGER -> {
                if (resultCode == Activity.RESULT_OK){
                    switch_backup.isChecked = false
                    noteCache.isAutoBackup = false
                }
            }
            BACKUP_MANAGER -> {
                if (resultCode == Activity.RESULT_OK){
                    isChange = true
                    recreate()
                }
            }
            CHANGE_FONT_SIZE -> {
                if (resultCode == Activity.RESULT_OK){
                    isChange = true
                    recreate()
                }
            }
            LOCK -> {
                if (resultCode == Activity.RESULT_OK){
                    switch_lock.isChecked = noteCache.isLock
                }
            }
            CHANGE_THEME -> {
                isChange = true
                recreate()
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        outState?.putBoolean("isChange",isChange)
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        isChange = savedInstanceState?.getBoolean("isChange")!!
    }

    override fun initPresenter() {
        noteGet = NoteGet(this)

        presenterList.add(noteGet)
        viewList.add(noteView)

        super.initPresenter()
    }

    override fun onClick(v: View?) {
        when (v!!.id){
            R.id.tv_setting_back -> {
                closeActivity()
            }
            R.id.ll_setting_show -> {
                startAc(Intent(this,ShowItemActivity::class.java), CHANGE_ITEM)
            }
            R.id.ll_setting_font -> {
                if (PermissionRequestUtil(this).requestPermission(Config.permissionWrite)){
                    startAc(Intent(this,ShowFontActivity::class.java), CHANGE_FONT)
                }
            }
            R.id.ll_setting_feedback -> {
                startAc(Intent(this, FeedbackActivity::class.java), FEEDBACK)
            }
            R.id.ll_setting_user -> {
                startAc(Intent(this, LoginActivity::class.java), USER_MANAGER)
            }
            R.id.ll_setting_backup -> {
                startAc(Intent(this, ShowBackupActivity::class.java), BACKUP_MANAGER)
            }
            R.id.ll_setting_state -> {
                startAc(Intent(this, ShowStateActivity::class.java), CHANGE_STATE)
            }
            R.id.ll_setting_font_size -> {
                startAc(Intent(this, ShowFontSizeActivity::class.java), CHANGE_FONT_SIZE)
            }
            R.id.ll_setting_lock -> {
                startAc(Intent(this, ShowLockActivity::class.java), LOCK)
            }
            R.id.ll_setting_out -> {
                if (isNoteNull){
                    toast("还没有日记呢！\n先去写一篇日记吧")
                }else{
                    showTimeDialog()
                }
            }
//            R.id.ll_setting_app -> {
//                startAc(Intent(this, AppLogActivity::class.java), APP_LOG)
//            }
        }
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        when (buttonView!!.id){
            R.id.switch_backup -> {
                when (isChecked){
                    true -> {
                        val user = DroiUser.getCurrentUser()
                        if (user != null && user.isLoggedIn && user.isAuthorized && !user.isAnonymous){
                            noteCache.isAutoBackup = true
                        }else{
                            goToLogin()
                            switch_backup.isChecked = false
                        }
                    }
                    false -> {
                        noteCache.isAutoBackup = false
                    }
                }
            }
            R.id.switch_auto_save -> {
                noteCache.isAutoSave = isChecked
            }
        }
    }

    private fun initDialog(){
        val view = LayoutInflater.from(this)
                .inflate(R.layout.dialog_change_pass, null)
        with(passView) {
            add(view.findViewById<ImageButton>(R.id.ib_pass_1) as ImageButton)
            add(view.findViewById<ImageButton>(R.id.ib_pass_2) as ImageButton)
            add(view.findViewById<ImageButton>(R.id.ib_pass_3) as ImageButton)
            add(view.findViewById<ImageButton>(R.id.ib_pass_4) as ImageButton)
        }
        for (i in passView){
            i.setOnClickListener({
                showSoftInput(true)
            })
        }
        passEdit = view.findViewById<EditText>(R.id.et_pass) as EditText
        passEdit.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val input = passEdit.text.toString()
                if (input.length > 4){
                    passEdit.setText(input.subSequence(0, 4))
                    passEdit.setSelection(passEdit.text.length)
                }
                inputPass(passEdit.text.toString())
            }

        })
        passDialog = AlertDialog.Builder(this)
                .setView(view)
                .create()
        passDialog.setOnDismissListener {
            passEdit.setText("")
            showSoftInput(false)
            switch_lock.isChecked = noteCache.isLock
        }
    }


    private fun inputPass(text: String){
        if (text.length < 4){
            setPass(text.length)
        }else {
            judgePass(text)
        }
    }

    private fun judgePass(text: String){
        passEdit.setText("")
        if (noteCache.isLock){
            if (text == noteCache.lockPassword){
                passDialog.dismiss()
                switch_lock.isChecked = false
                noteCache.isLock = false
            }else {
                passDialog.setTitle("输入密码错误...")
            }
        }else{
            if (isFirst){
                previous = text
                passDialog.setTitle("再次输入密码...")
                isFirst = false
            }else {
                if (text == previous){
                    passDialog.dismiss()
                    switch_lock.isChecked = true
                    noteCache.isLock = true
                    noteCache.lockPassword = text
                    isFirst = true
                }else{
                    passDialog.setTitle("密码不相同，请重新输入")
                    isFirst = true
                }
            }
        }
    }

    private fun setPass(size: Int){
        for (i in 0..passView.size - 1){
            if (i < size){
                passView[i].setImageResource(R.drawable.circle_shape)
            }else {
                passView[i].setImageDrawable(BitmapDrawable())
            }
        }
    }

    private fun changeLock() {
        passDialog.setTitle("输入密码")
        passDialog.show()
        showSoftInput(true)
    }

    private fun goToLogin(){
        AlertDialog.Builder(this)
                .setTitle("您还未登录账号，是否前往登录？")
                .setPositiveButton("是") { _, _ ->
                    startAc(Intent(this,LoginActivity::class.java), USER_MANAGER)
                }
                .setNegativeButton("否") { _, _ ->
                }
                .show()
    }

    private fun showTimeDialog(){
        val view = LayoutInflater.from(this)
        .inflate(R.layout.dialog_show_time, null)
        val dp1 = view.findViewById<DatePicker>(R.id.dp_data_from) as DatePicker
        val dp2 = view.findViewById<DatePicker>(R.id.dp_data_to) as DatePicker
        initDatePicker(dp1, dp2)
        AlertDialog.Builder(this)
                .setView(view)
                .setPositiveButton("确定", { _, _ ->
                    val yearF = dp1.year
                    val monthF = dp1.month + 1
                    val dayF = dp1.dayOfMonth
                    val yearT = dp2.year
                    val monthT = dp2.month + 1
                    val dayT = dp2.dayOfMonth
                    val timeF = NoteUtil.getTime(yearF, monthF, dayF)
                    val timeT = NoteUtil.getTime(yearT, monthT, dayT)
                    if (timeF > timeT){
                        toast("开始时间不能大于结束时间")
                    }else{
                        outTo(0, Date(yearF, monthF, dayF), Date(yearT, monthT, dayT))
                    }
                })
                .setNegativeButton("取消", { _, _ ->

                })
                .show()
    }

    private fun outTo(flag: Int, from: Date, to: Date){
        val intent = Intent(this, ShowNoteActivity::class.java)
        val bundle = Bundle()
        bundle.putInt("type", flag)
        bundle.putParcelable("from", from)
        bundle.putParcelable("to", to)
        intent.putExtras(bundle)
        startAc(intent, OPEN_NOTE)
    }

    override fun onBackPressed() {
        closeActivity()
    }

    @SuppressLint("SetTextI18n")
    private fun setNote(noteList: ArrayList<Note>){
        val size = noteList.size
        val count = noteList.sumBy { it.content.length }

        tv_setting_size.text = tv_setting_size.text.toString() + size
        tv_setting_count.text = tv_setting_count.text.toString() + count
    }

    private fun startAc(intent: Intent, requestCode: Int){
        val compat = ActivityOptionsCompat.makeSceneTransitionAnimation(this,
                tv_setting_back,getString(R.string.translate_note_button))
        ActivityCompat.startActivityForResult(this,intent,requestCode,compat.toBundle())
    }

    private fun closeActivity(){
        if (isChange){
            setResult(Activity.RESULT_OK)
        }
        if (Build.VERSION.SDK_INT > 21){
            finishAfterTransition()
        }else{
            finish()
        }
    }

    private fun deleteDivider(datePicker: DatePicker){
        try {
            val llFirst = datePicker.getChildAt(0) as LinearLayout
            val spinner = llFirst.getChildAt(0) as LinearLayout
            for (i in 0..spinner.childCount){
                if (spinner.getChildAt(i) is NumberPicker) {
                    val picker = spinner.getChildAt(i) as NumberPicker
                    val pickerFields = NumberPicker::class.java.declaredFields
                    for (j in pickerFields){
                        if (j.name == "mSelectionDivider"){
                            j.isAccessible = true
                            j.set(picker, ColorDrawable())
                        }
                    }
                }
            }
        } catch(e: Exception) {
        }

    }

    private fun initDatePicker(dp1: DatePicker, dp2: DatePicker){
        dp1.init(dateFrom.year, dateFrom.month - 1, dateFrom.day, null)
        dp2.init(dateTo.year, dateTo.month - 1, dateTo.day, null)
        val maxTime = NoteUtil.getTime(dateTo.year, dateTo.month, dateTo.day + 1)
        val minTime = NoteUtil.getTime(dateFrom.year, dateFrom.month, dateFrom.day - 1)
        dp1.maxDate = maxTime
        dp1.minDate = minTime
        dp2.maxDate = maxTime
        dp2.minDate = minTime
        deleteDivider(dp1)
        deleteDivider(dp2)
    }

    private fun showSoftInput(boolean: Boolean){
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (boolean){
            passEdit.isActivated = true
            passEdit.requestFocus()
            imm.showSoftInput(window.decorView,
                    InputMethodManager.SHOW_FORCED)
        }else {
            imm.hideSoftInputFromWindow(window.peekDecorView().windowToken, 0)
        }
    }

    companion object{
        val CHANGE_ITEM = 1
        val CHANGE_FONT = 2
        val USER_MANAGER = 3
        val BACKUP_MANAGER = 4
        val OPEN_NOTE = 5
        val CHANGE_STATE = 6
        val CHANGE_FONT_SIZE = 7
        val FEEDBACK = 8
        val LOCK = 9
        val APP_LOG = 10
        val CHANGE_THEME = 11
    }
}