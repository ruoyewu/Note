package com.wuruoye.note.widget

import android.content.Context
import android.support.animation.SpringAnimation
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.MotionEvent

/**
 * Created by wuruoye on 2017/6/2.
 * this file is to do
 */

class SpringRecyclerView : RecyclerView {
    private var onDragListener: OnDragListener? = null
    private var startDragY: Float = 0f
    private val springAnim: SpringAnimation = SpringAnimation(this, SpringAnimation.TRANSLATION_Y, 0f)
    private var dragHeight = 150
    private var lastTime: Long = 0
    init {
        //刚度 默认1200 值越大回弹的速度越快
        springAnim.spring.stiffness = 800.0f
        //阻尼 默认0.5 值越小，回弹之后来回的次数越多
        springAnim.spring.dampingRatio = 0.50f
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
                        translationY = (e.rawY - startDragY) / 3
                        if (translationY > dragHeight){
                            onDown()
                        }
                        return true
                    } else {
                        startDragY = 0f
                        springAnim.cancel()
                        translationY = 0f
                    }
                } else if (scrollY + height >= getChildAt(0).measuredHeight) {
                    //底部上拉
                    if (startDragY == 0f) {
                        startDragY = e.rawY
                    }
                    if (e.rawY - startDragY <= 0) {
                        translationY = (e.rawY - startDragY) / 3
                        if (translationY < -dragHeight){
                            onUp()
                        }
                        return true
                    } else {
                        startDragY = 0f
                        springAnim.cancel()
                        translationY = 0f
                    }
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                if (translationY != 0f) {
                    springAnim.start()
                }
                startDragY = 0f
            }
        }
        return super.onTouchEvent(e)
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


    interface OnDragListener {
        fun onUpDrag()
        fun onDownDrag()
    }

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {}
}
