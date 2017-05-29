package com.wuruoye.note.base

/**
 * Created by wuruoye on 2017/5/27.
 * this file is to do
 */

interface IListener<in T> {
    fun onSuccess(model: T)
    fun onFail(fail: String)
}
