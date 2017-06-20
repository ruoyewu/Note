package com.wuruoye.note.view

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
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

    private lateinit var progressDialog: ProgressDialog

    override val contentView: Int
        get() = R.layout.activity_backup

    override fun initData(bundle: Bundle?) {
        noteCache = NoteCache(this)
    }

    override fun initView() {
        progressDialog = ProgressDialog(this)
        val tip =
                if (noteCache.isAutoBackup)
                    "自动备份\t已开启"
                else
                    "自动备份\t未开启"

        tv_backup_tip.text = tip

        tv_backup_back.setOnClickListener(this)
        ll_backup_remote_show.setOnClickListener(this)
        ll_backup_remote.setOnClickListener(this)
        ll_backup_local.setOnClickListener(this)
        ll_backup_local_show.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v!!.id){
            R.id.tv_backup_back -> {
                onBackPressed()
            }
            R.id.ll_backup_remote -> {
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
            R.id.ll_backup_remote_show -> {

            }
            R.id.ll_backup_local -> {
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
            R.id.ll_backup_local_show -> {
                startActivity(Intent(this, ShowLocalActivity::class.java))
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
