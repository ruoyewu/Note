package com.wuruoye.note.util

import android.graphics.Bitmap
import com.wuruoye.note.model.Config
import java.io.*

/**
 * Created by wuruoye on 2017/6/19.
 * this file is to do file read and write
 */

object FileUtil{

    fun readText(path: String): String{
        val file = File(path)
        val read = InputStreamReader(FileInputStream(file), "gbk")
        val input = BufferedReader(read)
        var text = ""
        try {
            text = input.readText()
        } catch(e: Exception) {
        } finally {
            input.close()
            read.close()
        }
        return text
    }

    fun writeText(path: String, text: String): Boolean{
        val file = File(path)
        val write = OutputStreamWriter(FileOutputStream(file), "gbk")
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
