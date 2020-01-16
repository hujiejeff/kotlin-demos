package com.hujiejeff.musicplayer.customview

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator

/**
 * Create by hujie on 2020/1/16
 * 仿网易云音乐loading
 */
class LoadingView(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
    View(context, attrs, defStyleAttr) {

    private val mPaint: Paint
    private val mStrokeWidth = 6F//画笔宽度
    private val mIntervalWidth = 10F//间隔
    private val mDynamicHeight: Int = 30//变化长度
    private val mY = 10//顶上间隔
    private var mDy: Int = 0//动态变化长度

    private val mValueAnimator: ValueAnimator

    //设置可见
    private var isVisible = true
        set(value) {
            field = value
            if (value) {
                visibility = VISIBLE
                mValueAnimator.start()
            } else {
                visibility = INVISIBLE
                mValueAnimator.cancel()
            }
        }

    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0)

    init {
        mPaint = Paint().apply {
            color = Color.RED
            isAntiAlias = true
            style = Paint.Style.FILL
            strokeWidth = mStrokeWidth
        }

        mValueAnimator = ValueAnimator.ofInt(0, mDynamicHeight).apply {
            duration = 400
            repeatMode = ValueAnimator.REVERSE
            repeatCount = ValueAnimator.INFINITE
            interpolator = LinearInterpolator()
            addUpdateListener {
                mDy = it.animatedValue as Int
                invalidate()
            }
            start()
        }
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        //画出四条线
        canvas?.drawLine(mIntervalWidth, height.toFloat(), mIntervalWidth, (height / 4f) + mY + mDy, mPaint)

        canvas?.drawLine(
            2 * mIntervalWidth + mStrokeWidth,
            height.toFloat(),
            2 * mIntervalWidth + mStrokeWidth,
            height / 2f - mDy,
            mPaint
        )

        canvas?.drawLine(
            3 * mIntervalWidth + 2 * mStrokeWidth,
            height.toFloat(),
            3 * mIntervalWidth + 2 * mStrokeWidth,
            mY.toFloat() + mDy,
            mPaint
        )

        canvas?.drawLine(
            4 * mIntervalWidth + 3 * mStrokeWidth,
            height.toFloat(),
            4 * mIntervalWidth + 3 * mStrokeWidth,
            height / 4f * 3 - mDy,
            mPaint
        )
    }

    fun show() {
        isVisible = true
    }

    fun hide() {
        isVisible = false
    }


}