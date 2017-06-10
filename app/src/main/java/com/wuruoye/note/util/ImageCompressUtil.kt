package com.wuruoye.note.util

import android.graphics.Bitmap
import com.wuruoye.note.model.Config
import java.io.*

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
        val file = File(Config.imagePath + name)
        val outStream = FileOutputStream(file)
        bmp.compress(Bitmap.CompressFormat.JPEG, option, outStream)
        var length = file.length()
        while (length / 1024 > 100 && option > 0){
            option -= 10
            bmp.compress(Bitmap.CompressFormat.JPEG, option, outStream)
            length = file.length()
        }
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
//        val out = BufferedWriter(OutputStreamWriter(FileOutputStream(path)))
//        val write = FileWriter(path)
//        val byteOut = ByteArrayOutputStream()
//        byteOut.write(byte)
//        byteOut.writeTo(    )
    }
}
