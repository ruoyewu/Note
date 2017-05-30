package com.wuruoye.note.view

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityOptionsCompat
import android.view.View
import com.wuruoye.note.R
import com.wuruoye.note.base.BaseActivity
import com.wuruoye.note.base.IAbsView
import com.wuruoye.note.model.Note
import com.wuruoye.note.presenter.NoteGet
import com.wuruoye.note.util.toast
import kotlinx.android.synthetic.main.activity_setting.*
import uk.co.chrisjenx.calligraphy.CalligraphyConfig

/**
 * Created by wuruoye on 2017/5/29.
 * this file is to do
 */
class SettingActivity : BaseActivity(), View.OnClickListener{
    private lateinit var noteGet: NoteGet
    private var isChangeItem = false

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

        ll_setting_show.setOnClickListener(this)
        ll_setting_font.setOnClickListener(this)
        tv_setting_back.setOnClickListener(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CHANGE_ITEM){
            if (resultCode == Activity.RESULT_OK){
                isChangeItem = true
            }
        }
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
            R.id.ll_setting_show -> {
                startAc(Intent(this,ShowItemActivity::class.java), CHANGE_ITEM)
            }
            R.id.ll_setting_font -> {
                startAc(Intent(this,ShowFontActivity::class.java), CHANGE_FONT)
            }
        }
    }

    override fun onBackPressed() {
        closeActivity()
    }

    @SuppressLint("SetTextI18n")
    private fun setNote(noteList: ArrayList<Note>){
        val size = noteList.size
        val count = noteList.sumBy { it.content.length }

        tv_setting_size.text = tv_setting_size.text.toString() + size
        tv_setting_count.text = tv_setting_count.text.toString() + count
    }

    private fun startAc(intent: Intent, requestCode: Int){
        val compat = ActivityOptionsCompat.makeSceneTransitionAnimation(this,
                tv_setting_back,getString(R.string.translate_note_button))
        ActivityCompat.startActivityForResult(this,intent,requestCode,compat.toBundle())
    }

    private fun closeActivity(){
        if (isChangeItem){
            setResult(Activity.RESULT_OK)
        }
        if (Build.VERSION.SDK_INT > 21){
            finishAfterTransition()
        }else{
            finish()
        }
    }

    companion object{
        val CHANGE_ITEM = 1
        val CHANGE_FONT = 2
    }
}