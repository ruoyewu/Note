package com.wuruoye.note.util

import android.graphics.Bitmap
import com.wuruoye.note.model.Config
import java.io.*
import java.nio.charset.Charset

/**
 * Created by wuruoye on 2017/6/19.
 * this file is to do file read and write
 */

object FileUtil{

    fun readText(path: String): String{
        var text = ""
        val input = InputStreamReader(FileInputStream(path), Charsets.UTF_8)
        text = input.readText()
        return text
    }

    fun writeTextUTF8(path: String, text: String): Boolean{
        val write = OutputStreamWriter(FileOutputStream(path), Charsets.UTF_8)
        val output = BufferedWriter(write)
        try {
            output.write(text)
            return true
        } catch(e: Exception) {
            return false
        } finally {
            output.close()
            write.close()
        }
    }

    fun writeTextGBK(path: String, text: String): Boolean{
        val write = OutputStreamWriter(FileOutputStream(path), Charset.forName("GBK"))
        val output = BufferedWriter(write)
        try {
            output.write(text)
            return true
        } catch(e: Exception) {
            return false
        } finally {
            output.close()
            write.close()
        }
    }

    fun writeImage(path: String, bmp: Bitmap){
        val option = 60
        val file = File(path)
        val outStream = FileOutputStream(file)
        bmp.compress(Bitmap.CompressFormat.JPEG, option, outStream)
        outStream.flush()
        outStream.close()
    }

    fun transportFile(from: String, to: String): Boolean{
        val fileFrom = File(from)
        val fileTo = File(to)
        if (fileFrom.exists()){
            val input = FileInputStream(fileFrom)
            val output = FileOutputStream(fileTo)
            val data = ByteArray(1024)
            while (input.read(data) != -1){
                output.write(data)
            }
            input.close()
            output.close()
            return true
        }
        return false
    }
}
