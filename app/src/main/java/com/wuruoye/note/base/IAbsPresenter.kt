package com.wuruoye.note.base

import android.content.Context
import java.lang.ref.Reference
import java.lang.ref.WeakReference

/**
 * Created by wuruoye on 2017/5/27.
 * this file is to do
 */

@Suppress("UNCHECKED_CAST")
abstract class IAbsPresenter<V : IBaseView>(context: Context) : IBasePresenter {
    protected var mViewRef: Reference<V>? = null
    protected val mContext = context.applicationContext!!

    override fun attachView(baseView: IBaseView) {
        mViewRef = WeakReference(baseView as V)
    }

    override fun detachView() {
        if (mViewRef != null) {
            mViewRef!!.clear()
            mViewRef = null
        }
    }

    protected fun getView(): V?{
        if (mViewRef != null){
            return mViewRef!!.get()
        }else{
            return null
        }
    }
}
