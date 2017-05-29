package com.wuruoye.note.base

import android.os.Handler
import android.view.Window
import android.view.WindowManager

/**
 * Created by wuruoye on 2017/5/27.
 * this file is to do
 */

abstract class BaseActivity : android.support.v7.app.AppCompatActivity(){
    protected val presenterList = ArrayList<IBasePresenter>()
    protected val viewList = ArrayList<IBaseView>()

    abstract val contentView: Int
    abstract fun initData(bundle: android.os.Bundle?)
    abstract fun initView()

    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(contentView)
        initPresenter()
        initData(intent.extras)
        initView()
    }

    open fun initPresenter(){
        if (presenterList.size != viewList.size){
        }else {
            for (i in 0..presenterList.size - 1){
                presenterList[i].attachView(viewList[i])
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        for (i in presenterList){
            i.detachView()
        }
    }
}