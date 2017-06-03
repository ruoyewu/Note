package com.wuruoye.note.model

import com.droi.sdk.core.*

/**
 * Created by wuruoye on 2017/6/3.
 * this file is to do
 */

@DroiObjectName("user_note")
class UpNote (
        @DroiReference var userName: DroiUser,
        @DroiExpose var content: String,
        @DroiExpose var color: Int,
        @DroiExpose var year: Int,
        @DroiExpose var month: Int,
        @DroiExpose var day: Int
) : DroiObject() {

}
