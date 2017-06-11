package com.wuruoye.note.util

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Matrix

/**
 * Created by wuruoye on 2017/6/10.
 * this file is to do
 */

object BitmapOverlay {
    /**
     * 图片效果叠加
     * @param bmp1 限制了尺寸大小的Bitmap
     * *
     * @return
     */
    fun overlay(bmp1: Bitmap, over: Bitmap): Bitmap {
        val ow = over.width
        val oh = over.height
        var tow = 0
        var toh = 0
        var sx = 0
        var sy = 0
        if ((ow / oh) < (9 / 16)){
            tow = ow
            toh = tow * 16 / 9
            sy = (oh - toh) / 2
        }else {
            toh = oh
            tow = oh * 9 / 16
            sx = (ow - tow) / 2
        }
        if (sx < 0){
            sx = 0
        }
        if (sy < 0){
            sy = 0
        }
        if (sx + tow > over.width){
            tow = over.width - sx
        }
        if (sy + toh > over.height){
            toh = over.height - sy
        }
        val overlay = Bitmap.createBitmap(over, sx, sy, tow, toh)
        val width = bmp1.width
        val height = bmp1.height
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)

        // 对边框图片进行缩放
        val w = overlay.width
        val h = overlay.height
        val scaleX = width * 1f / w
        val scaleY = height * 1f / h
        val matrix = Matrix()
        matrix.postScale(scaleX, scaleY)

        val overlayCopy = Bitmap.createBitmap(overlay, 0, 0, w, h, matrix, true)

        var pixColor = 0
        var layColor = 0

        var pixR = 0
        var pixG = 0
        var pixB = 0
        var pixA = 0

        var newR = 0
        var newG = 0
        var newB = 0
        var newA = 0

        var layR = 0
        var layG = 0
        var layB = 0
        var layA = 0

        val alpha = 0.5f

        val srcPixels = IntArray(width * height)
        val layPixels = IntArray(width * height)
        bmp1.getPixels(srcPixels, 0, width, 0, 0, width, height)
        overlayCopy.getPixels(layPixels, 0, width, 0, 0, width, height)

        var pos = 0
        for (i in 0..height - 1) {
            for (k in 0..width - 1) {
                pos = i * width + k
                pixColor = srcPixels[pos]
                layColor = layPixels[pos]

                pixR = Color.red(pixColor)
                pixG = Color.green(pixColor)
                pixB = Color.blue(pixColor)
                pixA = Color.alpha(pixColor)

                layR = Color.red(layColor)
                layG = Color.green(layColor)
                layB = Color.blue(layColor)
                layA = Color.alpha(layColor)

                newR = (pixR * alpha + layR * (1 - alpha)).toInt()
                newG = (pixG * alpha + layG * (1 - alpha)).toInt()
                newB = (pixB * alpha + layB * (1 - alpha)).toInt()
                layA = (pixA * alpha + layA * (1 - alpha)).toInt()

                newR = Math.min(255, Math.max(0, newR))
                newG = Math.min(255, Math.max(0, newG))
                newB = Math.min(255, Math.max(0, newB))
                newA = Math.min(255, Math.max(0, layA))

                srcPixels[pos] = Color.argb(newA, newR, newG, newB)
            }
        }

        bitmap.setPixels(srcPixels, 0, width, 0, 0, width, height)
        return bitmap
    }
}
