package com.wuruoye.note.view

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.view.View
import com.wuruoye.note.R
import com.wuruoye.note.base.BaseActivity
import com.wuruoye.note.base.IAbsView
import com.wuruoye.note.model.Note
import com.wuruoye.note.presenter.NoteGet
import com.wuruoye.note.util.toast
import kotlinx.android.synthetic.main.activity_setting.*

/**
 * Created by wuruoye on 2017/5/29.
 * this file is to do
 */
class SettingActivity : BaseActivity(), View.OnClickListener{
    private lateinit var noteGet: NoteGet

    private var noteView = object : IAbsView<ArrayList<Note>>{
        override fun setModel(model: ArrayList<Note>) {
            setNote(model)
        }

        override fun setWorn(message: String) {
            toast(message)
        }
    }

    override val contentView: Int
        get() = R.layout.activity_setting

    override fun initData(bundle: Bundle?) {

    }

    override fun initView() {
        noteGet.requestAllNote()

        val color = ActivityCompat.getColor(this,R.color.gray)
        iv_setting_show.setColorFilter(color)
        iv_setting_show__.setColorFilter(color)

        tv_setting_back.setOnClickListener(this)
    }

    override fun initPresenter() {
        noteGet = NoteGet(this)

        presenterList.add(noteGet)
        viewList.add(noteView)

        super.initPresenter()
    }

    override fun onClick(v: View?) {
        when (v!!.id){
            R.id.tv_setting_back -> {
                closeActivity()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setNote(noteList: ArrayList<Note>){
        val size = noteList.size
        val count = noteList.sumBy { it.content.length }

        tv_setting_size.text = tv_setting_size.text.toString() + size
        tv_setting_count.text = tv_setting_count.text.toString() + count
    }

    private fun closeActivity(){
        if (Build.VERSION.SDK_INT > 21){
            finishAfterTransition()
        }else{
            finish()
        }
    }
}