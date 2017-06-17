package com.wuruoye.note.view

import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.view.View

import com.wuruoye.note.R
import com.wuruoye.note.base.BaseActivity
import com.wuruoye.note.model.Config
import com.wuruoye.note.model.NoteCache
import com.wuruoye.note.util.BackupUtil
import com.wuruoye.note.util.Extensions.toast
import kotlinx.android.synthetic.main.activity_backup.*

/**
 * Created by wuruoye on 2017/6/4.
 * this file is to do
 */

class ShowBackupActivity : BaseActivity(), View.OnClickListener {
    private lateinit var noteCache: NoteCache
    private var isClick = true
    private var isChange = false

    private val backupListener = object : BackupUtil.OnBackupListener{
        override fun onBackupSuccess() {
            runOnUiThread {
                toast("备份成功")
                isClick = true
            }
        }

        override fun onBackupFail(message: String) {
            runOnUiThread {
                toast(message)
                isClick = true
            }
        }
    }

    override val contentView: Int
        get() = R.layout.activity_backup

    override fun initData(bundle: Bundle?) {
        noteCache = NoteCache(this)
    }

    override fun initView() {
        val tip =
                if (noteCache.isAutoBackup)
                    "自动备份\t已开启"
                else
                    "自动备份\t未开启"

        tv_backup_tip.text = tip

        tv_backup_back.setOnClickListener(this)
        ll_backup_download.setOnClickListener(this)
        ll_backup_upload.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v!!.id){
            R.id.tv_backup_back -> {
                onBackPressed()
            }
            R.id.ll_backup_upload -> {
                if (noteCache.isLogin) {
                    if (isClick) {
                        isClick = false
                        Thread({
                            BackupUtil.backupNote(applicationContext, backupListener)
                        }).start()
                        toast("备份中，请勿退出...")
                    }
                }else {
                    toast("您还未登录，请先登录...")
                }
            }
            R.id.ll_backup_download -> {
                if (noteCache.isLogin) {
                    var isOk = true
                    for (i in Config.permission){
                        if (ActivityCompat.checkSelfPermission(this,i) == PackageManager.PERMISSION_DENIED){
                            isOk = false
                            isClick = true
                            ActivityCompat.requestPermissions(this, arrayOf(i),1)
                        }
                    }
                    if (isOk) {
                        if (isClick){
                            isClick = false
                            Thread({
                                BackupUtil.downloadNote(applicationContext, backupListener)
                            }).start()
                            toast("同步中，请稍后...")
                            isChange = true
                        }
                    }
                }else{
                    toast("您还未登录，请先登录...")
                }
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
}
