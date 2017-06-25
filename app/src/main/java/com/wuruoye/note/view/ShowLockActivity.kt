package com.wuruoye.note.view

import android.app.Activity
import android.app.KeyguardManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.BitmapDrawable
import android.hardware.fingerprint.FingerprintManager
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AlertDialog
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton

import com.wuruoye.note.R
import com.wuruoye.note.base.BaseActivity
import com.wuruoye.note.model.Config
import com.wuruoye.note.model.NoteCache
import com.wuruoye.note.util.Extensions.toast
import com.wuruoye.note.util.FingerUtil
import kotlinx.android.synthetic.main.activity_setting.*
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
    private var currentState = 0
    private var isChange = false

    private val passView = ArrayList<ImageButton>()
    private lateinit var passEdit: EditText
    private lateinit var passDialog: AlertDialog
    private var isFirst = true
    private var previous = ""

    override val contentView: Int
        get() = R.layout.activity_show_lock

    override fun initData(bundle: Bundle?) {
        noteCache = NoteCache(this)
    }

    override fun initView() {
        initDialog()
        initButton()

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
        if (isChange)
            setResult(Activity.RESULT_OK)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            finishAfterTransition()
        }else{
            finish()
        }
    }

    private fun changeLock(){
        currentState = CHANGE_LOCK
        passDialog.setTitle("输入密码")
        passDialog.show()
        passEdit.isActivated = true
        passEdit.requestFocus()
        showSoftInput(true)
    }

    private fun changePassword(){
        currentState = CHANGE_PASS
        passDialog.setTitle("输入密码")
        passDialog.show()
        passEdit.isActivated = true
        passEdit.requestFocus()
        showSoftInput(true)
    }


    private fun initDialog(){
        val view = LayoutInflater.from(this)
                .inflate(R.layout.dialog_change_pass, null)
        with(passView) {
            add(view.findViewById(com.wuruoye.note.R.id.ib_pass_1) as ImageButton)
            add(view.findViewById(com.wuruoye.note.R.id.ib_pass_2) as ImageButton)
            add(view.findViewById(com.wuruoye.note.R.id.ib_pass_3) as ImageButton)
            add(view.findViewById(com.wuruoye.note.R.id.ib_pass_4) as ImageButton)
        }
        passEdit = view.findViewById(R.id.et_pass) as EditText
        passEdit.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val input = passEdit.text.toString()
                if (input.length > 4){
                    passEdit.setText(input.subSequence(0, 4))
                    passEdit.setSelection(passEdit.text.length)
                }
                inputPass(passEdit.text.toString())
            }
        })
        passDialog = AlertDialog.Builder(this)
                .setView(view)
                .create()
        passDialog.setOnDismissListener {
            passEdit.setText("")
            showSoftInput(false)
            initButton()
        }
    }

    private fun initButton(){
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
        tv_show_lock_tip.text = lockTip
        btn_lock_lock.text = isLock
        btn_lock_finger.text = fingerTip
    }

    private fun inputPass(text: String){
        if (text.length < 4){
            setPass(text.length)
        }else {
            judgePass(text)
        }
    }

    private fun judgePass(text: String){
        passEdit.setText("")
        if (currentState == CHANGE_LOCK) {
            if (noteCache.isLock){
                if (text == noteCache.lockPassword){
                    passDialog.dismiss()
                    initButton()
                    isChange = true
                    noteCache.isLock = false
                }else {
                    passDialog.setTitle("输入密码错误...")
                }
            }else{
                if (isFirst){
                    previous = text
                    passDialog.setTitle("再次输入密码...")
                    isFirst = false
                }else {
                    if (text == previous){
                        passDialog.dismiss()
                        initButton()
                        isChange = true
                        noteCache.isLock = true
                        noteCache.lockPassword = text
                        isFirst = true
                    }else{
                        passDialog.setTitle("密码不相同，请重新输入")
                        isFirst = true
                    }
                }
            }
        }else if (currentState == CHANGE_PASS) {
            if (isFirst){
                if (noteCache.lockPassword == text){
                    isFirst = false
                    passEdit.setText("")
                    passDialog.setTitle("输入新密码")
                }else {
                    passDialog.setTitle("密码错误")
                    passEdit.setText("")
                }
            }else {
                noteCache.lockPassword = text
                passDialog.dismiss()
                initButton()
            }
        }
    }

    private fun setPass(size: Int){
        for (i in 0..passView.size - 1){
            if (i < size){
                passView[i].setImageResource(R.drawable.circle_shape)
            }else {
                passView[i].setImageDrawable(BitmapDrawable())
            }
        }
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
            try {
                FingerUtil.isFingerAvailable(this)
                return true
            } catch(e: Exception) {
                toast(e.message.toString())
            }
        }else {
            toast("指纹识别只支持android6.0及以上，请输入密码")
        }
        return false
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

    companion object{
        val CHANGE_LOCK = 1
        val CHANGE_PASS = 2
    }
}
