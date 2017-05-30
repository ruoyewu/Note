package com.wuruoye.note.view

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import com.transitionseverywhere.Fade
import com.transitionseverywhere.TransitionManager

import com.wuruoye.note.R
import com.wuruoye.note.base.BaseActivity
import com.wuruoye.note.model.NoteCache
import kotlinx.android.synthetic.main.activity_show_item.*

/**
 * Created by wuruoye on 2017/5/29.
 * this file is to do
 */

class ShowItemActivity : BaseActivity() , View.OnClickListener{
    private lateinit var noteCache: NoteCache
    private lateinit var ivItemList: Array<ImageView>
    private lateinit var llItemList: Array<LinearLayout>
    private var currentItem = 0

    override val contentView: Int
        get() = R.layout.activity_show_item

    override fun initData(bundle: Bundle?) {
        noteCache = NoteCache(this)
        currentItem = noteCache.itemShow
    }

    override fun initView() {
        ivItemList = arrayOf(
                iv_item_set_1,
                iv_item_set_2,
                iv_item_set_3,
                iv_item_set_4
        )
        llItemList = arrayOf(
                ll_item_set_1,
                ll_item_set_2,
                ll_item_set_3,
                ll_item_set_4
        )

        ivItemList[noteCache.itemShow - 1].setImageResource(R.drawable.ic_select)

        for (i in 0..llItemList.size - 1){
            llItemList[i].tag = i
            llItemList[i].setOnClickListener(this)
        }

        tv_item_set_back.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v!!.id){
            R.id.tv_item_set_back -> {
                closeActivity()
            }
            else -> {
                setItemClick(v.tag as Int)
            }
        }
    }

    private fun setItemClick(item: Int){
        TransitionManager.beginDelayedTransition(ll_item_set,Fade(Fade.MODE_OUT))
        for (i in ivItemList){
            i.setImageResource(0)
        }
        ivItemList[item].setImageResource(R.drawable.ic_select)

        currentItem = item + 1
    }

    override fun onBackPressed() {
        closeActivity()
    }

    private fun closeActivity(){
        if (currentItem != noteCache.itemShow){
            noteCache.itemShow = currentItem
            setResult(Activity.RESULT_OK)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            finishAfterTransition()
        }else{
            finish()
        }
    }
}
