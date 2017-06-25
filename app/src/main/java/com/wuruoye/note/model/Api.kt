package com.wuruoye.note.model

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET

/**
 * Created by wuruoye on 2017/6/25.
 * this file is to do
 */

interface Api{
    @GET("app.json")
    fun requestAppUpdate(): Call<ResponseBody>
}
