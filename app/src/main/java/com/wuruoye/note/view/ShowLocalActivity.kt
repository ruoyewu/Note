package com.wuruoye.note.view

import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.view.View

import com.wuruoye.note.R
import com.wuruoye.note.adapter.BackupLocalRVAdapter
import com.wuruoye.note.base.BaseActivity
import com.wuruoye.note.model.Backup
import com.wuruoye.note.model.Config
import com.wuruoye.note.util.BackupUtil
import com.wuruoye.note.util.Extensions.toast
import com.wuruoye.note.util.FileUtil
import com.wuruoye.note.util.SQLiteUtil
import kotlinx.android.synthetic.main.activity_show_local.*

/**
 * Created by wuruoye on 2017/6/19.
 * this file is to do
 */

class ShowLocalActivity : BaseActivity() {
    private lateinit var progressDialog: Dialog
    private var isChange = false

    private val itemClickListener = object : BackupLocalRVAdapter.OnItemClickListener{
        override fun onItemClick(v: View) {
            onItemClick(v.tag as String)
        }
    }

    override val contentView: Int
        get() = R.layout.activity_show_local

    override fun initData(bundle: Bundle?) {
        
    }

    override fun initView() {
        progressDialog = Dialog(this)
        progressDialog.setTitle("正在加载本地备份")

        getBackupLocal()
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

    private fun getBackupLocal(){
        if (requestPermission(Config.permissionWrite)) {
            progressDialog.show()
            Thread({
                val list = BackupUtil.readBackupLocal()
                runOnUiThread {
                    progressDialog.dismiss()
                    setBackupLocal(list)
                }
            }).start()
        }
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
        ssv_backup_local.post {
            ssv_backup_local.smoothScrollBy(0, ssv_backup_local.measuredHeight)
        }
    }

    private fun onItemClick(name: String){
        AlertDialog.Builder(this)
                .setTitle("选择操作:")
                .setItems(items) { _, which ->
                    when (which){
                        0 -> {
                            deleteLocal(name)
                        }
                        1 -> {
                            loadFromLocal(name)
                        }
                    }
                }
                .show()
    }

    private fun deleteLocal(name: String){
            progressDialog.setTitle("正在删除中...")
            progressDialog.show()
            Thread({
                BackupUtil.deleteNoteLocal(name)
                runOnUiThread {
                    progressDialog.dismiss()
                    toast("删除成功")
                    getBackupLocal()
                }
            }).start()
    }

    private fun loadFromLocal(name: String){
        if (requestPermission(Config.permissionWrite)) {
            isChange = true
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

    private fun requestPermission(permissions: Array<String>): Boolean{
        var isOk = true
        for (i in permissions){
            if (ActivityCompat.checkSelfPermission(this, i) == PackageManager.PERMISSION_DENIED){
                isOk = false
                ActivityCompat.requestPermissions(this, permissions, 1)
            }
        }
        return isOk
    }

    companion object{
        val items = arrayOf(
                "删除备份", "导入备份"
        )
    }
}
