package com.wuruoye.note.view

import android.os.Bundle

import com.wuruoye.note.R
import com.wuruoye.note.base.BaseActivity
import com.wuruoye.note.model.NoteCache

/**
 * Created by wuruoye on 2017/5/29.
 * this file is to do
 */

class ShowItemActivity : BaseActivity() {
    private lateinit var noteCache: NoteCache

    override val contentView: Int
        get() = R.layout.activity_show_item

    override fun initData(bundle: Bundle?) {
        noteCache = NoteCache(this)
    }

    override fun initView() {

    }
}
