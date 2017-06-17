package com.wuruoye.note.view

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityOptionsCompat
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.widget.CompoundButton
import android.widget.DatePicker
import android.widget.LinearLayout
import android.widget.NumberPicker
import com.droi.sdk.core.DroiUser
import com.wuruoye.note.R
import com.wuruoye.note.base.BaseActivity
import com.wuruoye.note.base.IAbsView
import com.wuruoye.note.model.Date
import com.wuruoye.note.model.Note
import com.wuruoye.note.model.NoteCache
import com.wuruoye.note.presenter.NoteGet
import com.wuruoye.note.util.Extensions.toast
import com.wuruoye.note.util.NoteUtil
import kotlinx.android.synthetic.main.activity_setting.*

/**
 * Created by wuruoye on 2017/5/29.
 * this file is to do
 */
class SettingActivity : BaseActivity(), View.OnClickListener, CompoundButton.OnCheckedChangeListener{
    private lateinit var noteGet: NoteGet
    private lateinit var noteCache: NoteCache
    //save the state if we should refresh main activity, if true, yes
    private var isChange = false
    //save the date of the first note we write
    private lateinit var dateFrom: Date
    //save the date of the last note we write
    private lateinit var dateTo: Date
    //save if there is no note written
    private var isNoteNull = false

    private var outDialog: AlertDialog.Builder? = null

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
    }

    override fun initView() {
        noteGet.requestAllNote()

        switch_backup.isChecked = noteCache.isAutoBackup
        switch_auto_save.isChecked = noteCache.isAutoSave

        ll_setting_show.setOnClickListener(this)
        ll_setting_font.setOnClickListener(this)
        ll_setting_feedback.setOnClickListener(this)
        tv_setting_back.setOnClickListener(this)
        ll_setting_user.setOnClickListener(this)
        ll_setting_backup.setOnClickListener(this)
        ll_setting_out.setOnClickListener(this)
        ll_setting_state.setOnClickListener(this)
        switch_backup.setOnCheckedChangeListener(this)
        switch_auto_save.setOnCheckedChangeListener(this)
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
                startAc(Intent(this,ShowFontActivity::class.java), CHANGE_FONT)
            }
            R.id.ll_setting_feedback -> {
                startFeedback()
            }
            R.id.ll_setting_user -> {
                startAc(Intent(this, LoginActivity::class.java), USER_MANAGER)
            }
            R.id.ll_setting_backup -> {
                startAc(Intent(this, BackupActivity::class.java), BACKUP_MANAGER)
            }
            R.id.ll_setting_state -> {
                startAc(Intent(this, ShowStateActivity::class.java), CHANGE_STATE)
            }
            R.id.ll_setting_out -> {
                if (isNoteNull){
                    toast("还没有日记呢！\n先去写一篇日记吧")
                }else{
                    showTimeDialog()
                }
            }
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

    private fun showOutDialog(from: Date, to: Date){
        if (outDialog == null){
            outDialog = AlertDialog.Builder(this)
                    .setTitle("选择导出格式:")
                    .setItems(outItem) { _, position ->
                        outTo(position, from, to)
                    }
        }
        outDialog!!.show()
    }

    private fun showTimeDialog(){
        val view = LayoutInflater.from(this)
        .inflate(R.layout.item_time, null)
        val dp1 = view.findViewById(R.id.dp_data_from) as DatePicker
        val dp2 = view.findViewById(R.id.dp_data_to) as DatePicker
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

    private fun startFeedback(){
        val intent = Intent(Intent.ACTION_SENDTO)
        val name = "设备名: " +  Build.MODEL
        val sdk = "版本号: " + Build.VERSION.SDK_INT
        intent.data = Uri.parse("mailto:" + CREATE_EMAIL)
        intent.putExtra(Intent.EXTRA_TEXT,name + "\n" + sdk + "\n")
        startActivity(intent)
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

    companion object{
        val CHANGE_ITEM = 1
        val CHANGE_FONT = 2
        val USER_MANAGER = 3
        val BACKUP_MANAGER = 4
        val OPEN_NOTE = 5
        val CHANGE_STATE = 6

        val CREATE_EMAIL = "2455929518@qq.com"
        val outItem = arrayOf(
                "导出到文本"
        )
        val shareItem = arrayOf(
                "打开文件",
                "分享文件",
                "不操作"
        )
    }
}