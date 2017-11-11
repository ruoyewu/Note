package com.wuruoye.note.widget

import android.content.Context
import android.support.animation.SpringAnimation
import android.support.v4.widget.NestedScrollView
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent

/**
 * Created by SouthernBox on 2017/3/23 0023.
 * 下拉后有回弹动画的滚动控件
 */

class SpringScrollView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyle: Int = 0)
    : NestedScrollView(context, attrs, defStyle) {
    private var onDragListener: OnDragListener? = null
    private var startDragY: Float = 0f
    private var startDragX: Float = 0f
    private val springAnimH: SpringAnimation = SpringAnimation(this, SpringAnimation.TRANSLATION_Y, 0f)
    private val springAnimW: SpringAnimation = SpringAnimation(this, SpringAnimation.TRANSLATION_X, 0f)
    private var dragHeight = 150
    private var dragWidth = 100
    private var xAy = 3
    private var lastTime: Long = 0
    init {
        //刚度 默认1200 值越大回弹的速度越快
        springAnimH.spring.stiffness = 800.0f
        springAnimW.spring.stiffness = 800.0f
        //阻尼 默认0.5 值越小，回弹之后来回的次数越多
        springAnimH.spring.dampingRatio = 0.50f
        springAnimW.spring.dampingRatio = 0.50f
    }

    override fun onTouchEvent(e: MotionEvent): Boolean {
        when (e.action) {
            MotionEvent.ACTION_MOVE -> {
                if (scrollY <= 0) {
                    //顶部下拉
                    if (startDragY == 0f) {
                        startDragY = e.rawY
                    }
                    if (e.rawY - startDragY >= 0) {
                        translationY = (e.rawY - startDragY) / xAy
                        if (translationY > dragHeight){
                            onDown()
                        }
                    } else {
                        startDragY = 0f
                        springAnimH.cancel()
                        translationY = 0f
                    }
                } else if (scrollY + height >= getChildAt(0).measuredHeight) {
                    //底部上拉
                    if (startDragY == 0f) {
                        startDragY = e.rawY
                    }
                    if (e.rawY - startDragY <= 0) {
                        translationY = (e.rawY - startDragY) / xAy
                        if (translationY < -dragHeight){
                            onUp()
                        }
                    } else {
                        startDragY = 0f
                        springAnimH.cancel()
                        translationY = 0f
                    }
                }

                //width
//                if (startDragX == 0f){
//                    startDragX = e.rawX
//                }
//                if (e.rawX - startDragX >= 0){
//                    translationX = (e.rawX - startDragX) / xAy
//                    if (translationX > dragWidth){
//                        onRight()
//                    }
//                }else if (e.rawX - startDragX < 0){
//                    translationX = (e.rawX - startDragX) / xAy
//                    if (translationX < -dragWidth){
//                        onLeft()
//                    }
//                }
            }
            MotionEvent.ACTION_UP,
            MotionEvent.ACTION_CANCEL -> {
                if (translationY != 0f) {
                    springAnimH.start()
                }
                if (translationX != 0f){
                    springAnimW.start()
                }
                startDragY = 0f
                startDragX = 0f
            }
        }
        return super.onTouchEvent(e)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return super.onInterceptTouchEvent(ev)
    }

    fun setDragListener(listener: OnDragListener){
        this.onDragListener = listener
    }

    private fun onUp(){
        if (onDragListener != null) {
            if (System.currentTimeMillis() - lastTime > 1000){
                onDragListener!!.onUpDrag()
                lastTime = System.currentTimeMillis()
            }
        }
    }

    private fun onDown(){
        if (onDragListener != null) {
            if (System.currentTimeMillis() - lastTime > 1000){
                onDragListener!!.onDownDrag()
                lastTime = System.currentTimeMillis()
            }
        }
    }

    private fun onLeft(){
        if (onDragListener != null){
            if (System.currentTimeMillis() - lastTime > 1000){
                onDragListener!!.onLeftDrag()
                lastTime = System.currentTimeMillis()
            }
        }
    }

    private fun onRight(){
        if (onDragListener != null){
            if (System.currentTimeMillis() - lastTime > 1000){
                onDragListener!!.onRightDrag()
                lastTime = System.currentTimeMillis()
            }
        }
    }


    interface OnDragListener {
        fun onUpDrag()
        fun onDownDrag()
        fun onLeftDrag()
        fun onRightDrag()
    }
}
