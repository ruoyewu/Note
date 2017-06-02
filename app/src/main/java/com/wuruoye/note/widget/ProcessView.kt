package com.wuruoye.note.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import com.wuruoye.note.R

/**
 * Created by wuruoye on 2017/6/2.
 * this file is to do
 */

class ProcessView : View {
    private var mColor = 0
    private var mProcess = 0f
    private var mWidth = 0
    private var mHeight = 0

    private lateinit var mPaint: Paint

    fun setColor(color: Int){
        mColor = color
        mPaint.color = color
    }

    fun setProcess(process: Float){
        mProcess = process
        invalidate()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val length = mWidth.toFloat() * mProcess / MAX_PROCESS
        val rect = RectF(0f,0f,length,mHeight.toFloat())
        canvas!!.drawRoundRect(rect,5f,5f,mPaint)
    }

    private fun init(){
        mPaint = Paint()
        mPaint.isDither = true
        mPaint.color = mColor
        mPaint.style = Paint.Style.FILL
        mPaint.strokeWidth = 1f
        mPaint.alpha = 100

        alpha = 0.5f
    }

    constructor(context: Context) : super(context) {init()}

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {init()}

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mWidth = w
        mHeight = h
    }

    companion object{
        const val MAX_PROCESS = 100
    }
}
