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
    private val springAnimH: SpringAnimation = SpringAnimation(this, SpringAnimation.TRANSLATION_Y, 0f)
    private val springAnimW: SpringAnimation = SpringAnimation(this, SpringAnimation.TRANSLATION_X, 0f)
    private var lastTime: Long = 0
    init {
        //刚度 默认1200 值越大回弹的速度越快
        springAnimH.spring.stiffness = 800.0f
        springAnimW.spring.stiffness = 800.0f
        //阻尼 默认0.5 值越小，回弹之后来回的次数越多
        springAnimH.spring.dampingRatio = 0.50f
        springAnimW.spring.dampingRatio = 0.50f
    }

    private var startX = 0F
    private var startY = 0F
    private var mDirect = Direct.NONE
    private val mLastedMove = 10F
    private val mListenerMove = 200F
    enum class Direct{
        NONE,
        HORIZONTAL,
        VERTICAL
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        when (ev!!.action){
            MotionEvent.ACTION_DOWN -> {
                startX = ev.x
                startY = ev.y
            }
            MotionEvent.ACTION_MOVE -> {
                val moveX = ev.x - startX
                val moveY = ev.y - startY
                if (Math.abs(moveX) > mLastedMove && Math.abs(moveX) > Math.abs(moveY)){
                    mDirect = Direct.HORIZONTAL
                }
                if (Math.abs(moveY) > mLastedMove && Math.abs(moveY) > Math.abs(moveX)){
                    if (scrollY == 0 && moveY > 0){
                        mDirect = Direct.VERTICAL
                    }else if ((scrollY == getChildAt(0).height - height || getChildAt(0).height <= height)
                            && moveY < 0){
                        mDirect = Direct.VERTICAL
                    }
                }
                if (mDirect != Direct.NONE){
                    return true
                }
            }
        }
        return super.onInterceptTouchEvent(ev)
    }

    override fun onTouchEvent(e: MotionEvent?): Boolean {
        when (e!!.action) {
            MotionEvent.ACTION_MOVE -> {
                val moveX = e.x - startX - mLastedMove;
                val moveY = e.y - startY - mLastedMove;
                if (mDirect == Direct.HORIZONTAL){
                    translationX = moveX / 3;
                    if (moveX > mListenerMove){
                        onRight()
                    }else if (moveX < -mListenerMove){
                        onLeft()
                    }
                }else if (mDirect == Direct.VERTICAL){
                    translationY = moveY / 3;
                    if (moveY > mListenerMove){
                        onDown()
                    }else if (moveY < -mListenerMove){
                        onUp()
                    }
                }

            }
            MotionEvent.ACTION_UP,
            MotionEvent.ACTION_CANCEL -> {
                if (translationY != 0f) {
                    springAnimH.start()
                }
                if (translationX != 0f){
                    springAnimW.start()
                }
                mDirect = Direct.NONE
                startX = 0F
                startY = 0F
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
