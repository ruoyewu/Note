package com.wuruoye.note.view

import android.os.Build
import android.os.Bundle
import android.view.View
import com.wuruoye.note.R

import com.wuruoye.note.base.BaseActivity
import kotlinx.android.synthetic.main.activity_user.*

/**
 * Created by wuruoye on 2017/6/3.
 * this file is to do
 */

class LoginActivity : BaseActivity(), View.OnClickListener{
    override val contentView: Int
        get() = R.layout.activity_user

    override fun initData(bundle: Bundle?) {

    }

    override fun initView() {
        tv_login_back.setOnClickListener(this)
        btn_login_login.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v!!.id){
            R.id.tv_login_back -> {
                onBackPressed()
            }
            R.id.btn_login_login -> {

            }
        }
    }

    private fun login(){

    }

    override fun onBackPressed() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            finishAfterTransition()
        }else{
            finish()
        }
    }
}
