package com.wuruoye.note.base

/**
 * Created by wuruoye on 2017/5/27.
 * this file is to do
 */

/*
basePresenter 声明与 view 绑定 和 解绑 两个函数
 */
interface IBasePresenter {
    fun attachView(baseView: IBaseView)
    fun detachView()
}
