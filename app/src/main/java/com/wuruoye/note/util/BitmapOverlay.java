package com.wuruoye.note.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;

import com.wuruoye.note.R;

/**
 * Created by wuruoye on 2017/6/10.
 * this file is to do
 */

public class BitmapOverlay {
    /**
     * 图片效果叠加
     * @param bmp1 限制了尺寸大小的Bitmap
     * @return
     */
    public static Bitmap overlay(Bitmap bmp1, Bitmap overlay, Context mContext) {
        long start = System.currentTimeMillis();
        int width = bmp1.getWidth();
        int height = bmp1.getHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

        // 对边框图片进行缩放
        int w = overlay.getWidth();
        int h = overlay.getHeight();
        float scaleX = width * 1F / w;
        float scaleY = height * 1F / h;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleX, scaleY);

        Bitmap overlayCopy = Bitmap.createBitmap(overlay, 0, 0, w, h, matrix, true);

        int pixColor = 0;
        int layColor = 0;

        int pixR = 0;
        int pixG = 0;
        int pixB = 0;
        int pixA = 0;

        int newR = 0;
        int newG = 0;
        int newB = 0;
        int newA = 0;

        int layR = 0;
        int layG = 0;
        int layB = 0;
        int layA = 0;

        final float alpha = 0.5F;

        int[] srcPixels = new int[width * height];
        int[] layPixels = new int[width * height];
        bmp1.getPixels(srcPixels, 0, width, 0, 0, width, height);
        overlayCopy.getPixels(layPixels, 0, width, 0, 0, width, height);

        int pos = 0;
        for (int i = 0; i < height; i++)
        {
            for (int k = 0; k < width; k++)
            {
                pos = i * width + k;
                pixColor = srcPixels[pos];
                layColor = layPixels[pos];

                pixR = Color.red(pixColor);
                pixG = Color.green(pixColor);
                pixB = Color.blue(pixColor);
                pixA = Color.alpha(pixColor);

                layR = Color.red(layColor);
                layG = Color.green(layColor);
                layB = Color.blue(layColor);
                layA = Color.alpha(layColor);

                newR = (int) (pixR * alpha + layR * (1 - alpha));
                newG = (int) (pixG * alpha + layG * (1 - alpha));
                newB = (int) (pixB * alpha + layB * (1 - alpha));
                layA = (int) (pixA * alpha + layA * (1 - alpha));

                newR = Math.min(255, Math.max(0, newR));
                newG = Math.min(255, Math.max(0, newG));
                newB = Math.min(255, Math.max(0, newB));
                newA = Math.min(255, Math.max(0, layA));

                srcPixels[pos] = Color.argb(newA, newR, newG, newB);
            }
        }

        bitmap.setPixels(srcPixels, 0, width, 0, 0, width, height);
        long end = System.currentTimeMillis();
        return bitmap;
    }
}
