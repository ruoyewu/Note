package com.wuruoye.note.view

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.view.View
import com.droi.sdk.DroiCallback
import com.droi.sdk.DroiError
import com.droi.sdk.core.DroiUser
import com.transitionseverywhere.TransitionManager
import com.wuruoye.note.R
import com.wuruoye.note.base.BaseActivity
import com.wuruoye.note.model.NoteCache
import com.wuruoye.note.util.isPhone
import com.wuruoye.note.util.toast
import kotlinx.android.synthetic.main.activity_user.*

/**
 * Created by wuruoye on 2017/6/3.
 * this file is to do
 */

class LoginActivity : BaseActivity(), View.OnClickListener{
    private lateinit var noteCache: NoteCache
    private var currentView = DEFAULT_VIEW
    private var isChange = false

    override val contentView: Int
        get() = R.layout.activity_user

    override fun initData(bundle: Bundle?) {
        noteCache = NoteCache(this)

        val user = DroiUser.getCurrentUser()
        if (user != null && user.isLoggedIn && !user.isAnonymous){
            currentView = DEFAULT_VIEW
        }else{
            currentView = LOGIN_VIEW
            user?.logout()
        }
    }

    override fun initView() {
        when (currentView){
            DEFAULT_VIEW -> defaultView()
            LOGIN_VIEW -> loginView()
        }

        et_login_pass

        tv_login_back.setOnClickListener(this)
        btn_login_login.setOnClickListener(this)
        btn_login_sign.setOnClickListener(this)
        btn_login_out.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        TransitionManager.beginDelayedTransition(ssv_login)
        when (v!!.id){
            R.id.tv_login_back -> {
                if (currentView == SIGN_VIEW){
                    currentView = LOGIN_VIEW
                    tv_login_back.text = "设"
                    loginView()
                }else{
                    onBackPressed()
                }
            }
            R.id.btn_login_login -> {
                login(et_login_name.text.toString(), et_login_pass.text.toString())
            }
            R.id.btn_login_sign -> {
                if (currentView == LOGIN_VIEW){
                    currentView = SIGN_VIEW
                    tv_login_back.text = "登"
                    signView()
                }else{
                    sign()
                }
            }
            R.id.btn_login_out -> {
                DroiUser.getCurrentUser().logoutInBackground(object : DroiCallback<Boolean>{
                    override fun result(p0: Boolean?, p1: DroiError?) {
                        if (p1!!.isOk){
                            currentView = LOGIN_VIEW
                            loginView()
                            isChange = true
                            toast("注销成功")
                            noteCache.isLogin = false
                        }
                    }
                })
            }
        }
    }

    private fun login(name: String, pass: String){
        clearError()
        when {
            name == "" -> til_login_name.error = "用户不能为空"
            pass == "" -> til_login_pass.error = "密码不能为空"
            else -> DroiUser.loginInBackground(name,pass) { p0, p1 ->
                if (p1!!.isOk && p0 != null && p0.isAuthorized){
                    currentView = DEFAULT_VIEW
                    defaultView()
                    toast("登录成功")
                    noteCache.isLogin = true
                    noteCache.userName = name
                    noteCache.userPass = pass
                }else{
                    val error =
                            when (p1.code){
                                DroiError.USER_NOT_EXISTS -> "用户不存在"
                                DroiError.USER_PASSWORD_INCORRECT -> "密码错误"
                                DroiError.USER_ALREADY_LOGIN -> "用户已登录"
                                else -> "其他错误"
                            }
                    til_login_name.error = error
                    if (error == "用户已登录"){
                        defaultView()
                    }
                }
            }
        }
    }

    private fun sign(){
        clearError()
        val name = et_login_name.text.toString()
        val pass = et_login_pass.text.toString()
        val phone = et_login_phone.text.toString()
        val email = et_login_email.text.toString()
        if (name == ""){
            til_login_name.error = "请输入用户名"
        }else if (pass.trim().length < 6){
            til_login_pass.error = "请输入大于六位的密码"
        }else if (!phone.isPhone()){
            til_login_phone.error = "请输入正确手机号"
        }else{
            val user = DroiUser()
            user.userId = name
            user.password = pass
            user.phoneNumber = phone
            user.email = email
            user.signUpInBackground(object : DroiCallback<Boolean>{
                override fun result(p0: Boolean?, p1: DroiError?) {
                    if (p1!!.isOk){
                        toast("注册成功")
                        login(name,pass)
                    }else{
                        val error =
                        when (p1.code){
                            DroiError.USER_ALREADY_EXISTS -> "用户已存在"
                            DroiError.USER_ALREADY_LOGIN -> "用户已登录"
                            else -> "其他错误"
                        }
                        til_login_name.error = error
                        noteCache.isLogin = DroiUser.getCurrentUser().isLoggedIn
                    }
                }
            })
        }
    }

    private fun loginView(){
        clearError()
        til_login_phone.visibility = View.GONE
        til_login_email.visibility = View.GONE
        btn_login_out.visibility = View.GONE
        tv_login_tip.visibility = View.GONE

        til_login_name.visibility = View.VISIBLE
        til_login_pass.visibility = View.VISIBLE
        btn_login_login.visibility = View.VISIBLE
        btn_login_sign.visibility = View.VISIBLE
    }

    private fun signView(){
        clearError()
        btn_login_login.visibility = View.GONE
        btn_login_out.visibility = View.GONE
        tv_login_tip.visibility = View.GONE

        til_login_name.visibility = View.VISIBLE
        til_login_pass.visibility = View.VISIBLE
        til_login_email.visibility = View.VISIBLE
        til_login_phone.visibility = View.VISIBLE
        btn_login_sign.visibility = View.VISIBLE
    }

    @SuppressLint("SetTextI18n")
    private fun defaultView(){
        clearError()
        til_login_name.visibility = View.GONE
        til_login_pass.visibility = View.GONE
        til_login_email.visibility = View.GONE
        til_login_phone.visibility = View.GONE
        btn_login_sign.visibility = View.GONE
        btn_login_login.visibility = View.GONE

        tv_login_tip.visibility = View.VISIBLE
        btn_login_out.visibility = View.VISIBLE

        val user = DroiUser.getCurrentUser()
        tv_login_tip.text = user.userId + "\t已登录"
    }

    private fun clearError(){
        til_login_name.error = ""
        til_login_email.error = ""
        til_login_phone.error = ""
        til_login_email.error = ""
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

    companion object {
        const val DEFAULT_VIEW = 1
        const val LOGIN_VIEW = 2
        const val SIGN_VIEW = 3
    }
}
