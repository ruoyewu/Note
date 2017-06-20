package com.wuruoye.note.view

import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityOptionsCompat
import android.view.View

import com.wuruoye.note.R
import com.wuruoye.note.base.BaseActivity
import com.wuruoye.note.model.Config
import com.wuruoye.note.model.NoteCache
import com.wuruoye.note.util.BackupUtil
import com.wuruoye.note.util.Extensions.toast
import kotlinx.android.synthetic.main.activity_backup.*
import kotlinx.android.synthetic.main.activity_setting.*

/**
 * Created by wuruoye on 2017/6/4.
 * this file is to do
 */

class ShowBackupActivity : BaseActivity(), View.OnClickListener {
    private lateinit var noteCache: NoteCache
    private var isClick = true
    private var isChange = false

    private lateinit var progressDialog: ProgressDialog

    override val contentView: Int
        get() = R.layout.activity_backup

    override fun initData(bundle: Bundle?) {
        noteCache = NoteCache(this)
    }

    override fun initView() {
        progressDialog = ProgressDialog(this)
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progressDialog.setCancelable(true)
        progressDialog.setCanceledOnTouchOutside(false)
        val tip =
                if (noteCache.isAutoBackup)
                    "自动备份\t已开启"
                else
                    "自动备份\t未开启"

        tv_backup_tip.text = tip

        tv_backup_back.setOnClickListener(this)
        btn_backup_remote.setOnClickListener(this)
        btn_show_remote.setOnClickListener(this)
        btn_backup_local.setOnClickListener(this)
        btn_show_local.setOnClickListener(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode){
            SHOW_REMOTE -> {
                if (resultCode == Activity.RESULT_OK){
                    isChange = true
                }
            }
            SHOW_LOCAL -> {
                if (resultCode == Activity.RESULT_OK){
                    isChange = true
                }
            }
        }
    }

    override fun onClick(v: View?) {
        when (v!!.id){
            R.id.tv_backup_back -> {
                onBackPressed()
            }
            R.id.btn_backup_remote -> {
                if (noteCache.isLogin) {
                    if (isClick) {
                        isClick = false
                        progressDialog.setTitle("正在备份中...")
                        progressDialog.show()
                        Thread({
                            BackupUtil.backupNoteRemote(applicationContext)
                            runOnUiThread {
                                progressDialog.dismiss()
                                toast("备份成功")
                            }
                        }).start()
                    }
                }else {
                    toast("您还未登录，请先登录...")
                }
            }
            R.id.btn_show_remote -> {
                startAc(Intent(this, ShowRemoteActivity::class.java), SHOW_REMOTE)
            }
            R.id.btn_backup_local -> {
                if (requestPermission()) {
                    progressDialog.setTitle("正在备份中...")
                    progressDialog.show()
                    Thread({
                        BackupUtil.backupNoteLocal(applicationContext)
                        runOnUiThread {
                            progressDialog.dismiss()
                            toast("备份成功")
                        }
                    }).start()
                }
            }
            R.id.btn_show_local -> {
                startAc(Intent(this, ShowLocalActivity::class.java), SHOW_LOCAL)
            }
        }
    }

    override fun onBackPressed() {
        if (isChange){
            setResult(Activity.RESULT_OK)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            finishAfterTransition()
        }else{
            finish()
        }
    }

    private fun startAc(intent: Intent, requestCode: Int){
        val compat = ActivityOptionsCompat.makeSceneTransitionAnimation(this,
                tv_backup_back,getString(R.string.translate_note_button))
        ActivityCompat.startActivityForResult(this,intent,requestCode,compat.toBundle())
    }

    private fun requestPermission(): Boolean{
        var isOk = true
        for (i in Config.permissionWrite){
            if (ActivityCompat.checkSelfPermission(this, i) == PackageManager.PERMISSION_DENIED){
                isOk = false
                ActivityCompat.requestPermissions(this, Config.permissionWrite, 1)
            }
        }
        return isOk
    }

    companion object{
        val SHOW_REMOTE = 1
        val SHOW_LOCAL = 2
    }
}
