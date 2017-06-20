package com.wuruoye.note.view

import android.app.ProgressDialog
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View

import com.wuruoye.note.R
import com.wuruoye.note.adapter.BackupLocalRVAdapter
import com.wuruoye.note.base.BaseActivity
import com.wuruoye.note.model.Backup
import com.wuruoye.note.util.BackupUtil
import com.wuruoye.note.util.Extensions.toast
import com.wuruoye.note.util.SQLiteUtil
import kotlinx.android.synthetic.main.activity_show_local.*

/**
 * Created by wuruoye on 2017/6/19.
 * this file is to do
 */

class ShowLocalActivity : BaseActivity() {
    private lateinit var progressDialog: ProgressDialog

    private val itemClickListener = object : BackupLocalRVAdapter.OnItemClickListener{
        override fun onItemClick(v: View) {
            onItemClick(v.tag as String)
            toast(v.tag as String)
        }
    }

    override val contentView: Int
        get() = R.layout.activity_show_local

    override fun initData(bundle: Bundle?) {
        
    }

    override fun initView() {
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("正在加载本地备份")

        getBackupLocal()
    }

    private fun getBackupLocal(){
        progressDialog.show()
        Thread({
            val list = BackupUtil.readBackupLocal()
            runOnUiThread {
                progressDialog.dismiss()
                setBackupLocal(list)
            }
        }).start()
    }

    private fun setBackupLocal(list: ArrayList<Backup>){
        tv_backup_local_tip.text = "本地备份数量：" + list.size
        val adapter = BackupLocalRVAdapter(list, itemClickListener)
        val layoutManager = object : LinearLayoutManager(this) {
            override fun canScrollVertically(): Boolean {
                return false
            }
        }
        layoutManager.isAutoMeasureEnabled = true
        rv_backup_local.layoutManager = layoutManager
        rv_backup_local.adapter = adapter
    }

    private fun onItemClick(name: String){
        progressDialog.show()
        Thread({
            val list = BackupUtil.downloadNoteLocal(name)
            for (i in list){
                SQLiteUtil.saveNote(applicationContext, i)
            }
            runOnUiThread {
                progressDialog.dismiss()
                toast("同步成功")
            }
        }).start()
    }
}
