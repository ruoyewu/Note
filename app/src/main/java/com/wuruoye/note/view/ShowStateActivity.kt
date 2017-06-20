package com.wuruoye.note.view

import android.os.Bundle
import android.view.View
import com.wuruoye.note.R
import com.wuruoye.note.base.BaseActivity
import com.wuruoye.note.model.NoteCache
import kotlinx.android.synthetic.main.activity_state.*

/**
 * Created by wuruoye on 2017/6/16.
 * this file is to do
 */

class ShowStateActivity : BaseActivity(), View.OnClickListener{
    private lateinit var noteCache: NoteCache

    override val contentView: Int
        get() = R.layout.activity_state

    override fun initData(bundle: Bundle?) {
        noteCache = NoteCache(this)
    }

    override fun initView() {
        rg_state.check(btnArray[noteCache.autoState])

        tv_state_back.setOnClickListener(this)
        rg_state.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId){
                R.id.state_expend -> {
                    noteCache.autoState = 0
                }
                R.id.state_close -> {
                    noteCache.autoState = 1
                }
            }
        }
    }

    override fun onClick(v: View?) {
        when (v!!.id){
            R.id.tv_state_back -> onBackPressed()
        }
    }

    companion object {
        val btnArray = intArrayOf(
                R.id.state_expend,
                R.id.state_close
        )
    }
}
