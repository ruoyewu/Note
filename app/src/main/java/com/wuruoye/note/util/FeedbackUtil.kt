package com.wuruoye.note.util

import com.droi.sdk.core.DroiUser
import com.wuruoye.note.model.Feedback

/**
 * Created by wuruoye on 2017/6/20.
 * this file is to do
 */
object FeedbackUtil {

    fun outFeedback(text: String): Boolean{
        val user = DroiUser.getCurrentUser()
        val name =
        if (user != null){
            user.userId
        }else ""

        val time = System.currentTimeMillis()
        val feedback = Feedback(name, text, time)
        val error = feedback.save()
        return error.isOk
    }
}