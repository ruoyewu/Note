package com.wuruoye.note.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.AttributeSet;
import android.view.View;

import com.wuruoye.note.R;

/**
 * Created by wuruoye on 2017/4/17.
 * this file is to do
 */

public class HeartbeatView extends View {
    private static final float C = 0.551915024494f;

    private int color = ActivityCompat.getColor(getContext(), R.color.baby_pink);
    private float mDuration = 1000;
    private float mCurrent = 0;
    private float mCount = 30;
    private float mStep;

    private int mCenterX;
    private int mCenterY;
    private Paint mPaint;

    private float[] mData = new float[8];
    private float[] mCtrl = new float[16];

    private long dir = 1;

    public HeartbeatView(Context context) {
        super(context);
    }

    public HeartbeatView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public HeartbeatView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mCenterX = w / 2;
        mCenterY = h / 2;
        mStep = mCenterX / 10;
        initDraw();
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);

        float mPiece = mDuration / mCount;

        canvas.translate(mCenterY, mCenterX);
        canvas.scale(1,-1);

        mPaint.setColor(color);

        Path path = new Path();

        path.moveTo(mData[0],mData[1]);

        path.cubicTo(mCtrl[0], mCtrl[1], mCtrl[2], mCtrl[3], mData[2], mData[3]);
        path.cubicTo(mCtrl[4], mCtrl[5], mCtrl[6], mCtrl[7], mData[4], mData[5]);
        path.cubicTo(mCtrl[8], mCtrl[9], mCtrl[10], mCtrl[11], mData[6], mData[7]);
        path.cubicTo(mCtrl[12], mCtrl[13], mCtrl[14], mCtrl[15], mData[0], mData[1]);

        canvas.drawPath(path,mPaint);

        mCurrent += mPiece * dir;
        if (mCurrent >= mDuration){
            dir = - dir;
        }else if (mCurrent <= 0){
            dir = -dir;
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mData[1] -= (mStep * 7)/mCount * dir;
                mCtrl[7] += (mStep * 5)/mCount * dir;
                mCtrl[9] += (mStep * 5)/mCount * dir;
                mCtrl[4] -= mStep/mCount * dir;
                mCtrl[10] += mStep/mCount * dir;
                invalidate();
            }
        },0L);
    }

    private void initDraw(){
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        float radius = mCenterX;
        float difference = radius * C;

        mData[0] = 0;
        mData[1] = radius;
        mData[2] = radius;
        mData[3] = 0;
        mData[4] = 0;
        mData[5] = -radius;
        mData[6] = -radius;
        mData[7] = 0;

        mCtrl[0]  = mData[0] + difference;
        mCtrl[1]  = mData[1];
        mCtrl[2]  = mData[2];
        mCtrl[3]  = mData[3] + difference;
        mCtrl[4]  = mData[2];
        mCtrl[5]  = mData[3] - difference;
        mCtrl[6]  = mData[4] + difference;
        mCtrl[7]  = mData[5];
        mCtrl[8]  = mData[4] - difference;
        mCtrl[9]  = mData[5];
        mCtrl[10] = mData[6];
        mCtrl[11] = mData[7] - difference;
        mCtrl[12] = mData[6];
        mCtrl[13] = mData[7] + difference;
        mCtrl[14] = mData[0] - difference;
        mCtrl[15] = mData[1];
    }

    /**
     * 设置背景颜色
     * @param color 颜色的值
     */
    public void setColor(int color){
        this.color = color;
        invalidate();
    }

    /**
     * 设置单位时间重绘次数
     * @param count 单位时间重绘次数,值越大，跳动越慢
     */
    public void setCount(int count){
        mCount = count;
        invalidate();
    }
}
