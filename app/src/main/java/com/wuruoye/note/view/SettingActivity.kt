package com.wuruoye.note.view

import android.annotation.SuppressLint
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityOptionsCompat
import android.support.v7.app.AlertDialog
import android.support.v7.widget.AlertDialogLayout
import android.view.View
import android.widget.CompoundButton
import com.droi.sdk.core.DroiUser
import com.wuruoye.note.R
import com.wuruoye.note.base.BaseActivity
import com.wuruoye.note.base.IAbsView
import com.wuruoye.note.model.Note
import com.wuruoye.note.model.NoteCache
import com.wuruoye.note.presenter.NoteGet
import com.wuruoye.note.util.Extensions.toast
import kotlinx.android.synthetic.main.activity_setting.*

/**
 * Created by wuruoye on 2017/5/29.
 * this file is to do
 */
class SettingActivity : BaseActivity(), View.OnClickListener, CompoundButton.OnCheckedChangeListener{
    private lateinit var noteGet: NoteGet
    private lateinit var noteCache: NoteCache
    private var isChange = false

    private var noteView = object : IAbsView<ArrayList<Note>>{
        override fun setModel(model: ArrayList<Note>) {
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

        switch_backup.isChecked = noteCache.backup

        ll_setting_show.setOnClickListener(this)
        ll_setting_font.setOnClickListener(this)
        ll_setting_feedback.setOnClickListener(this)
        tv_setting_back.setOnClickListener(this)
        ll_setting_user.setOnClickListener(this)
        ll_setting_backup.setOnClickListener(this)
        switch_backup.setOnCheckedChangeListener(this)
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
                    noteCache.backup = false
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
        }
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        when (isChecked){
            true -> {
                val user = DroiUser.getCurrentUser()
                if (user != null && user.isLoggedIn && user.isAuthorized && !user.isAnonymous){
                    noteCache.backup = true
                }else{
                    goToLogin()
                    switch_backup.isChecked = false
                }
            }
            false -> {
                noteCache.backup = false
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

    companion object{
        val CHANGE_ITEM = 1
        val CHANGE_FONT = 2
        val USER_MANAGER = 3
        val BACKUP_MANAGER = 4

        val CREATE_EMAIL = "2455929518@qq.com"
    }
}