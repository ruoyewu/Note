package com.wuruoye.note.util

import com.wuruoye.note.model.Config
import okhttp3.OkHttpClient
import retrofit2.Retrofit

/**
 * Created by wuruoye on 2017/6/25.
 * this file is to do
 */

object RetrofitCreateUtil{

    fun getRetrofit(): Retrofit{
        return Retrofit.Builder()
                .client(OkHttpClient())
                .baseUrl(Config.Github_baseUrl)
                .build()
    }
}
