package com.wuruoye.note.view

import android.os.Build
import android.os.Bundle
import android.view.View

import com.wuruoye.note.R
import com.wuruoye.note.base.BaseActivity
import com.wuruoye.note.util.Extensions.toast
import com.wuruoye.note.util.FeedbackUtil
import kotlinx.android.synthetic.main.activity_feedback.*

/**
 * Created by wuruoye on 2017/6/20.
 * this file is to do
 */

class FeedbackActivity : BaseActivity(), View.OnClickListener{
    override val contentView: Int
        get() = R.layout.activity_feedback

    override fun initData(bundle: Bundle?) {

    }

    override fun initView() {

        iv_feedback.setOnClickListener(this)
        tv_feedback_back.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v!!.id){
            R.id.iv_feedback -> {
                feedback()
            }
            R.id.tv_feedback_back -> {
                onBackPressed()
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

    private fun feedback(){
        val text = et_feedback.text.toString()
        if (text == ""){
            toast("先写下你的想法吧!")
        }else{
            toast("发送中...")
            Thread({
                val result = FeedbackUtil.outFeedback(text)
                runOnUiThread {
                    if (result){
                        toast("发送成功")
                        et_feedback.text.clear()
                    }else{
                        toast("发送失败,请先检查是否登录...")
                    }
                }
            }).start()
        }
    }
}
