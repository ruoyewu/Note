package com.wuruoye.note.base

/**
 * Created by wuruoye on 2017/5/27.
 * this file is to do
 */

interface IAbsView<T> : IBaseView {
    fun setModel(model: T)
    fun setWorn(message: String)
}
