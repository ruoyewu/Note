package com.wuruoye.note.view

import android.content.Intent
import android.os.Bundle
import android.support.v4.content.FileProvider
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.liulishuo.filedownloader.BaseDownloadTask
import com.liulishuo.filedownloader.FileDownloadListener
import com.wuruoye.note.R
import com.wuruoye.note.adapter.AppLogRVAdapter

import com.wuruoye.note.base.BaseActivity
import com.wuruoye.note.model.AppCache
import com.wuruoye.note.model.AppLog
import com.wuruoye.note.model.Config
import com.wuruoye.note.util.Extensions.toast
import com.wuruoye.note.util.FileUtil
import kotlinx.android.synthetic.main.activity_app_log.*
import org.json.JSONArray
import java.io.File

/**
 * Created by wuruoye on 2017/6/25.
 * this file is to do
 */

class AppLogActivity : BaseActivity(), View.OnClickListener {
    private lateinit var appCache: AppCache
    private var versionTip = ""

    private val downloadListener = object : FileDownloadListener() {
        override fun warn(p0: BaseDownloadTask?) {
            runOnUiThread { setWorn(p0!!.etag) }
        }

        override fun completed(p0: BaseDownloadTask?) {
            runOnUiThread { completeDownload(p0!!.targetFilePath) }
        }

        override fun pending(p0: BaseDownloadTask?, p1: Int, p2: Int) {

        }

        override fun error(p0: BaseDownloadTask?, p1: Throwable?) {
            runOnUiThread { setWorn(p1!!.message!!) }
        }

        override fun progress(p0: BaseDownloadTask?, p1: Int, p2: Int) {
            val i = p1.toFloat() / p2 * 100
            runOnUiThread { setDownloadProgress(i) }
        }

        override fun paused(p0: BaseDownloadTask?, p1: Int, p2: Int) {

        }

    }

    override val contentView: Int
        get() = R.layout.activity_app_log

    override fun initData(bundle: Bundle?) {
        appCache = AppCache(this)

        versionTip = "当前版本: " + getLocalVersion()
    }

    override fun initView() {
        tv_app_log_tip.text = versionTip

        setRecyclerView(getLogList())

        if (!appCache.isSeen){
            showUpdateDialog()
        }
        tv_app_log_back.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v!!.id){
            R.id.tv_app_log_back -> {
                onBackPressed()
            }
        }
    }

    private fun showUpdateDialog(){
        AlertDialog.Builder(this)
                .setTitle("应用有更新了!\n是否更新?")
                .setPositiveButton("更新"){ _, _ -> update() }
                .setNegativeButton("取消"){_, _ -> }
                .show()
        appCache.isSeen = true
    }

    private fun update(){
        val fromPath = Config.baseUrl + "jianji.apk"
        val toPath = Config.outDirect + "jianji.apk"
        FileUtil.downloadFile(fromPath, toPath, downloadListener)
    }

    private fun setWorn(worn: String){
        toast(worn)
    }

    private fun setDownloadProgress(progress: Float){

    }

    private fun completeDownload(path: String){
        toast("下载完成，准备安装...")
        val file = File(path)
        if (!file.exists()){
            toast("安装文件失败")
        }else {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.setDataAndType(FileProvider.getUriForFile(this, Config.AUTHORITY, file),
                    "application/*")
            startActivity(intent)
        }
    }

    private fun getLogList(): ArrayList<AppLog>{
        val list = ArrayList<AppLog>()
        val string = appCache.appLog
        val jsonArray = JSONArray(string)
        for (i in 0..jsonArray.length() - 1){
            val jsonObject = jsonArray.getJSONObject(i)
            val version = jsonObject.getString("version")
            val info = jsonObject.getString("info")
            list.add(AppLog(version, info))
        }
        return list
    }

    private fun setRecyclerView(list: ArrayList<AppLog>){
        val adapter = AppLogRVAdapter(list)
        val layout = object : LinearLayoutManager(this){
            override fun canScrollVertically(): Boolean {
                return false
            }
        }
        layout.isAutoMeasureEnabled = true
        rv_app_log.layoutManager = layout
        rv_app_log.adapter = adapter
    }

    private fun getLocalVersion(): String{
        return packageManager.getPackageInfo(packageName, 0)
                .versionName
    }
}
