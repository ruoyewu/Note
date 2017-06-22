package com.wuruoye.note.view

import android.app.KeyguardManager
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.fingerprint.FingerprintManager
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.view.View

import com.wuruoye.note.R
import com.wuruoye.note.base.BaseActivity
import com.wuruoye.note.model.Config
import com.wuruoye.note.model.NoteCache
import com.wuruoye.note.util.Extensions.toast
import kotlinx.android.synthetic.main.activity_show_lock.*

/**
 * Created by wuruoye on 2017/6/22.
 * this file is to do
 */

class ShowLockActivity : BaseActivity(), View.OnClickListener {
    private lateinit var noteCache: NoteCache
    private lateinit var lockTip: String
    private lateinit var fingerTip: String
    private lateinit var isLock: String

    override val contentView: Int
        get() = R.layout.activity_show_lock

    override fun initData(bundle: Bundle?) {
        noteCache = NoteCache(this)
        lockTip =
                if (noteCache.isLock){
                    "密码锁：开启"
                }else {
                    "密码锁：关闭"
                }
        isLock =
                if (noteCache.isLock){
                    "关闭密码锁"
                }else{
                    "开启密码锁"
                }
        fingerTip =
                if (noteCache.isFinger){
                    "关闭指纹"
                }else{
                    "使用指纹"
                }
    }

    override fun initView() {
        tv_show_lock_tip.text = lockTip
        btn_lock_lock.text = isLock
        btn_lock_finger.text = fingerTip

        tv_lock_back.setOnClickListener(this)
        btn_lock_lock.setOnClickListener(this)
        btn_lock_pass.setOnClickListener(this)
        btn_lock_finger.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.tv_lock_back -> {
                onBackPressed()
            }
            R.id.btn_lock_lock -> {
                changeLock()
            }
            R.id.btn_lock_pass -> {
                changePassword()
            }
            R.id.btn_lock_finger -> {
                changeFinger()
            }
        }
    }

    override fun onBackPressed() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            finishAfterTransition()
        }else{
            finish()
        }
    }

    private fun changeLock(){

    }

    private fun changePassword(){

    }

    private fun changeFinger(){
        if (getFinger()){
            if (noteCache.isFinger){
                btn_lock_finger.text = "使用指纹"
                noteCache.isFinger = false
            }else{
                btn_lock_finger.text = "关闭指纹"
                noteCache.isFinger = true
            }
        }
    }

    private fun getFinger(): Boolean{
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val manager = getSystemService(Context.FINGERPRINT_SERVICE) as FingerprintManager
            getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
            if (requestPermission()){
                if (manager.isHardwareDetected){
                    if (!manager.hasEnrolledFingerprints()){
                        toast("暂无可用指纹，请到系统设置录入指纹后再使用")
                    }else{
                        return true
                    }
                }
            }
        }else {
            toast("指纹识别只支持android6.0及以上，请输入密码")
        }
        return false
    }

    private fun requestPermission():  Boolean{
        var isOk = true
        for (i in Config.permissionFinger){
            if (ActivityCompat.checkSelfPermission(this, i) == PackageManager.PERMISSION_DENIED){
                isOk = false
                ActivityCompat.requestPermissions(this, Config.permissionFinger, 1)
            }
        }
        return isOk
    }
}
