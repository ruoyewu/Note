package com.wuruoye.note.model

import com.droi.sdk.core.DroiExpose
import com.droi.sdk.core.DroiObject

/**
 * Created by wuruoye on 2017/6/20.
 * this file is to do
 */
class Feedback(
        @DroiExpose
        var user: String,
        @DroiExpose
        val content: String,
        @DroiExpose
        val time: Long
) : DroiObject() {
    constructor(): this("", "", 0)
}