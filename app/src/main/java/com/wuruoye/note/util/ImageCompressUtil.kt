package com.wuruoye.note.util

import android.graphics.Bitmap
import com.wuruoye.note.model.Config
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

/**
 * Created by wuruoye on 2017/6/10.
 * this file is to do
 */

object ImageCompressUtil {

    fun compressAndSave(bmp: Bitmap, name: String){
        val directory = File(Config.imagePath)
        if (!directory.exists()){
            directory.mkdirs()
        }

        var option = 50
        val file = File(name)
        val outStream = FileOutputStream(file)
        bmp.compress(Bitmap.CompressFormat.JPEG, option, outStream)
        var length = file.length()
        while (length / 1024 > 100 && option > 0){
            option -= 10
            bmp.compress(Bitmap.CompressFormat.JPEG, option, outStream)
            length = file.length()
        }
    }
}
