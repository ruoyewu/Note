package com.wuruoye.note.view

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.wuruoye.note.R
import com.wuruoye.note.adapter.NoteRVAdapter
import com.wuruoye.note.base.BaseActivity
import com.wuruoye.note.model.Note
import com.wuruoye.note.model.UpNote
import com.wuruoye.note.util.BackupUtil
import com.wuruoye.note.util.Extensions.toast
import kotlinx.android.synthetic.main.activity_show_remote.*

/**
 * Created by wuruoye on 2017/6/20.
 * this file is to do
 */

class ShowRemoteActivity : BaseActivity() {
    private lateinit var waitDialog: AlertDialog
    private lateinit var noteDialog: AlertDialog
    private var isChange = false
    private lateinit var noteList: ArrayList<Note>

    private val onItemClickListener = object : NoteRVAdapter.OnItemClickListener{
        override fun onLongItemClick(v: View) {

        }

        override fun onItemClick(v: View) {
            showNoteDialog(v.tag as Note)
        }

    }

    override val contentView: Int
        get() = R.layout.activity_show_remote

    override fun initData(bundle: Bundle?) {

    }

    override fun initView() {
        waitDialog = AlertDialog.Builder(this)
                .setView(R.layout.dialog_wait)
                .create()
        waitDialog.setCanceledOnTouchOutside(false)


        tv_backup_remote_back.setOnClickListener { onBackPressed() }
        tv_backup_remote_input.setOnClickListener { inputAll() }

        getBackupRemote()
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

    private fun getBackupRemote(){
        waitDialog.setTitle("正在加载中...")
        waitDialog.show()
        Thread({
            val list = BackupUtil.readBackupRemote(this)
            runOnUiThread {
                setBackupRemote(list)
                waitDialog.dismiss()
            }
        }).start()
    }

    private fun setBackupRemote(list: ArrayList<Note>){
        tv_backup_remote_tip.text = "日记数目: " + list.size
        noteList = list
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

    private fun showNoteDialog(note: Note){
        val view = LayoutInflater.from(this)
                .inflate(R.layout.dialog_show_note, null)
        val tvNote = view.findViewById(R.id.tv_dialog_show_note) as TextView
        tvNote.text = note.content
        noteDialog = AlertDialog.Builder(this)
                .setView(view)
                .setTitle("日记详情:")
                .setPositiveButton("导入本地") { _, _ ->
                    if (note.getUpNote() != null) {
                        inputNote(arrayListOf(note.getUpNote()!!))
                    }else{
                        toast("操作失败，请稍后重试...")
                    }
                }
                .setNegativeButton("取消") { _, _ -> }
                .show()
    }

    private fun inputAll(){
        AlertDialog.Builder(this)
                .setTitle("此操作会覆盖本地数据，\n是否继续？")
                .setPositiveButton("确定"){ _, _ ->
                    val list = ArrayList<UpNote>()
                    for (i in noteList){
                        if (i.getUpNote() != null) {
                            list.add(i.getUpNote()!!)
                        }else{
                            toast("操作失败，请稍后重试...")
                        }
                    }
                    inputNote(list)
                }
                .setNegativeButton("取消") { _, _ -> }
                .show()
    }

    private fun inputNote(list: ArrayList<UpNote>){
        isChange = true
        waitDialog.setTitle("正在导入中...")
        waitDialog.show()
        Thread({
            BackupUtil.saveCloudNote(list, this)
            runOnUiThread {
                waitDialog.dismiss()
                toast("导入成功")
            }
        }).start()
    }
}
