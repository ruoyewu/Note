package com.wuruoye.note.view

import android.app.Dialog
import android.app.ProgressDialog
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View

import com.wuruoye.note.R
import com.wuruoye.note.adapter.NoteRVAdapter
import com.wuruoye.note.base.BaseActivity
import com.wuruoye.note.model.Note
import com.wuruoye.note.util.BackupUtil
import kotlinx.android.synthetic.main.activity_show_remote.*

/**
 * Created by wuruoye on 2017/6/20.
 * this file is to do
 */

class ShowRemoteActivity : BaseActivity() {
    private lateinit var progressDialog: ProgressDialog

    private val onItemClickListener = object : NoteRVAdapter.OnItemClickListener{
        override fun onLongItemClick(v: View) {

        }

        override fun onItemClick(v: View) {

        }

    }

    override val contentView: Int
        get() = R.layout.activity_show_remote

    override fun initData(bundle: Bundle?) {

    }

    override fun initView() {
        progressDialog = ProgressDialog(this)
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progressDialog.setCancelable(true)
        progressDialog.setCanceledOnTouchOutside(false)

        getBackupRemote()
    }

    private fun getBackupRemote(){
        progressDialog.setTitle("正在加载中...")
        progressDialog.show()
        Thread({
            val list = BackupUtil.readBackupRemote(this)
            runOnUiThread {
                setBackupRemote(list)
                progressDialog.dismiss()
            }
        }).start()
    }

    private fun setBackupRemote(list: ArrayList<Note>){
        tv_backup_remote_tip.text = "日记数目: " + list.size
        val adapter = NoteRVAdapter(list, onItemClickListener)
        val layoutManager = object : LinearLayoutManager(this) {
            override fun canScrollVertically(): Boolean {
                return false
            }
        }
        layoutManager.isAutoMeasureEnabled = true
        rv_backup_remote.layoutManager = layoutManager
        rv_backup_remote.adapter = adapter
        ssv_backup_remote.post {
            ssv_backup_remote.smoothScrollBy(0, ssv_backup_remote.measuredHeight * 2)
        }
    }
}
