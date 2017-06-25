package com.wuruoye.note.util

import android.content.Context
import com.wuruoye.note.model.Api
import com.wuruoye.note.model.AppCache
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by wuruoye on 2017/6/25.
 * this file is to do
 */

object UpdateUtil{

    fun requestUtil(context: Context){
        Thread({
            val appCache = AppCache(context)
            if (System.currentTimeMillis() - appCache.lastRequest > 1000 * 3600 * 6){
                val api = RetrofitCreateUtil.getRetrofit().create(Api::class.java)
                api.requestAppUpdate().enqueue(object : Callback<ResponseBody>{
                    override fun onResponse(p0: Call<ResponseBody>, p1: Response<ResponseBody>) {
                        val response = p1.body().string()
                        val jsonObject = JSONObject(response)
                        val remoteVersion = jsonObject.getInt("version_code")
                        if (remoteVersion > appCache.remoteVersionCode){
                            appCache.remoteVersionCode = remoteVersion
                            appCache.remoteVersion = jsonObject.getString("version")
                            appCache.appLog = jsonObject.getString("log")
                            appCache.isSeen = false
                            appCache.lastRequest = System.currentTimeMillis()
                        }
                    }

                    override fun onFailure(p0: Call<ResponseBody>?, p1: Throwable?) {

                    }

                })
            }
        }).start()
    }
}
