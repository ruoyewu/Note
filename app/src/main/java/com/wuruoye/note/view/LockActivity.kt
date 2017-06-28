package com.wuruoye.note.view

import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.hardware.fingerprint.FingerprintManager
import android.os.Build
import android.os.Bundle
import android.os.CancellationSignal
import android.support.v4.app.ActivityCompat
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageButton

import com.wuruoye.note.R
import com.wuruoye.note.base.BaseActivity
import com.wuruoye.note.model.Config
import com.wuruoye.note.model.NoteCache
import com.wuruoye.note.util.Extensions.toast
import com.wuruoye.note.util.FingerUtil
import kotlinx.android.synthetic.main.activity_lock.*
import kotlinx.android.synthetic.main.dialog_change_pass.*

/**
 * Created by wuruoye on 2017/6/22.
 * this file is to do
 */

class LockActivity : BaseActivity() {
    private lateinit var noteCache: NoteCache
    private val passView = ArrayList<ImageButton>()
    private val mCancelSignal = CancellationSignal()
    private lateinit var mSelfCanceled: FingerprintManager.AuthenticationCallback

    override val contentView: Int
        get() = R.layout.activity_lock

    override fun initData(bundle: Bundle?) {
        noteCache = NoteCache(this)

        if (noteCache.isFinger) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mSelfCanceled = object : FingerprintManager.AuthenticationCallback(){
                    override fun onAuthenticationError(errorCode: Int, errString: CharSequence?) {
                        super.onAuthenticationError(errorCode, errString)
                        setTip("您已多次识别指纹失败，请输入密码", true)
                    }

                    override fun onAuthenticationSucceeded(result: FingerprintManager.AuthenticationResult?) {
                        super.onAuthenticationSucceeded(result)
                        setTip("指纹识别成功", false)
                        passTrue()
                    }

                    override fun onAuthenticationFailed() {
                        super.onAuthenticationFailed()
                        setTip("指纹错误，请重试", true)
                    }
                }
            }
        }
    }

    override fun initView() {
        if (noteCache.isFinger){
            getFinger()
        }else{
            iv_lock_fingerprint.visibility = View.GONE
            setTip("请输入密码", false)
        }

        passView.add(ib_pass_1)
        passView.add(ib_pass_2)
        passView.add(ib_pass_3)
        passView.add(ib_pass_4)
        et_pass.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val input = et_pass.text.toString()
                Log.e("ruoye", input)
                if (input.length > 4){
                    val sub = input.subSequence(0, 4)
                    Log.e("ruoye", sub.toString())
                    et_pass.setText(input.subSequence(0, 4))
                    et_pass.setSelection(et_pass.text.length)
                }
                inputPass(et_pass.text.toString())
            }

        })
    }

    private fun setTip(tip: String, worn: Boolean){
        tv_lock_tip.text = tip
        if (worn){
            tv_lock_tip.setTextColor(ActivityCompat.getColor(this, R.color.carnation))
        }else {
            tv_lock_tip.setTextColor(ActivityCompat.getColor(this, R.color.gray))
        }
    }

    private fun passTrue(){
        val intent = Intent(this, MainActivity::class.java)
        val bundle = Bundle()
        bundle.putBoolean("isOpen", true)
        intent.putExtras(bundle)
        startActivity(intent)
        finish()
    }

    private fun inputPass(text: String){
        setView(text.length)
        if (text == noteCache.lockPassword){
            passTrue()
        }
    }

    private fun setView(size: Int){
        for (i in 0..passView.size - 1){
            if (i < size){
                passView[i].setImageResource(R.drawable.circle_shape)
            }else {
                passView[i].setImageDrawable(BitmapDrawable())
            }
        }
    }

    private fun getFinger(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (requestPermission()){
                try {
                    val manager = FingerUtil.isFingerAvailable(this)
                    manager.authenticate(null, mCancelSignal, 0, mSelfCanceled, null)
                }catch (e: Exception){
                    setTip(e.message.toString(), true)
                }
            }
        }else {
            setTip("指纹识别只支持android6.0及以上，请输入密码", false)
        }
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

    private fun showSoftInput(boolean: Boolean){
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (boolean){
            imm.showSoftInput(window.decorView,
                    InputMethodManager.SHOW_FORCED)
        }else {
            imm.hideSoftInputFromWindow(window.peekDecorView().windowToken, 0)
        }
    }
}
