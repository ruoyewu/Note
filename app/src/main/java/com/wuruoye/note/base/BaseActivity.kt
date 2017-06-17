package com.wuruoye.note.base

import android.content.Context
import android.os.Handler
import android.view.Window
import android.view.WindowManager
import com.umeng.analytics.MobclickAgent
import com.wuruoye.note.model.NoteCache

/**
 * Created by wuruoye on 2017/5/27.
 * this file is to do
 */

abstract class BaseActivity : android.support.v7.app.AppCompatActivity(){
    /*
    这里是一个activity使用的 数据获取presenter 与 数据回调的view
    一个presenter对应着一个view
    如果一个activity需要使用到这些，在initPresenter中初始化
     */
    protected val presenterList = ArrayList<IBasePresenter>()
    protected val viewList = ArrayList<IBaseView>()

    /*
    获取activity的对应的 layout 的id
     */
    abstract val contentView: Int

    /*
    初始化一些数据，bundle 即 getIntent().getExtras()
     */
    abstract fun initData(bundle: android.os.Bundle?)

    /*
    初始化一个view的操作，比如数据嵌入， 设置监听等
     */
    abstract fun initView()

    /*
    activity 入口
     */
    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(contentView)
        initPresenter()
        initData(intent.extras)
        initView()
    }

    /*
    初始化 presenter 和 view
    需要在子类里面先初始化之后，再调用 super.initPresenter()
     */
    open fun initPresenter(){
        if (presenterList.size != viewList.size){
        }else {
            for (i in 0..presenterList.size - 1){
                presenterList[i].attachView(viewList[i])
            }
        }
    }

    override fun onResume() {
        super.onResume()
        MobclickAgent.onResume(this)
    }

    override fun onPause() {
        super.onPause()
        MobclickAgent.onPause(this)
    }

    /*
    用于 presenter 和 view 的解绑
     */
    override fun onDestroy() {
        super.onDestroy()

        for (i in presenterList){
            i.detachView()
        }
    }

}