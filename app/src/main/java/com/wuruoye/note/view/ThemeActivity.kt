package com.wuruoye.note.view

import android.app.Activity
import android.os.Build
import android.os.Bundle

import com.wuruoye.note.R
import com.wuruoye.note.base.BaseActivity
import kotlinx.android.synthetic.main.activity_theme.*

/**
 * Created by wuruoye on 2017/6/25.
 * this file is to do
 */

class ThemeActivity : BaseActivity() {
    private var isChange = false

    override val contentView: Int
        get() = R.layout.activity_theme

    override fun initData(bundle: Bundle?) {

    }

    override fun initView() {

        tv_theme_back.setOnClickListener { onBackPressed() }
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
}
