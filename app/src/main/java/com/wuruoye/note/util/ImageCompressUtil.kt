package com.wuruoye.note.util

import android.graphics.Bitmap
import com.wuruoye.note.model.Config
import java.io.*

/**
 * Created by wuruoye on 2017/6/10.
 * this file is to do
 */

object ImageCompressUtil {

    fun writeToFile(bmp: Bitmap, name: String){
        val directory = File(Config.imagePath)
        if (!directory.exists()){
            directory.mkdirs()
        }

        val option = 60
        val file = File(Config.imagePath + name)
        val outStream = FileOutputStream(file)
        bmp.compress(Bitmap.CompressFormat.JPEG, option, outStream)
//        while (file.length() / 1024 > 100 && option > 0){
//            option -= 10
//            outStream.flush()
//            bmp.compress(Bitmap.CompressFormat.JPEG, option, outStream)
//        }
        outStream.flush()
        outStream.close()
    }

    fun writeToFile(byte: ByteArray, path: String){
        val directory = File(Config.imagePath)
        if (!directory.exists()){
            directory.mkdirs()
        }

        val out = FileOutputStream(Config.imagePath + path)
        out.write(byte)
        out.flush()
        out.close()
    }
}
