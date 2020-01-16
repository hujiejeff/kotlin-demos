package com.hujiejeff.musicplayer.customview

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.annotation.RequiresApi
import com.hujiejeff.musicplayer.R

/**
 * Create by hujie on 2020/1/16
 * 旋转进度条封面，仿网易云音乐ios
 */
class PlayerProgressView(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
    View(context, attrs, defStyleAttr) {

    private val mPaint: Paint
    private var mBitmap: Bitmap
    private var mBitmapShader: BitmapShader
    private val mMatrix: Matrix
    private var mRotate: Int = 0

    private val mProcessBarPaint: Paint
    private val mProgressWidth = 6F

    private val mProgressAnimator: ValueAnimator

    private var mProgressAngle = 0f

    var maxProgress = 1000
    var currentProgress = 0
        set(value) {
            field = value
            mProgressAngle = (currentProgress / maxProgress) * 360f
            mRotate = (mRotate + 1) % 360
            invalidate()
        }



    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0)

    init {
        mPaint = Paint()
        mMatrix = Matrix()
        mBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.default_cover)
        mBitmapShader = BitmapShader(mBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)

        mProcessBarPaint = Paint().apply {
            color = Color.RED
            style = Paint.Style.STROKE
            isAntiAlias = true
            strokeWidth = mProgressWidth
        }


        //测试动画
        mProgressAnimator = ValueAnimator.ofInt(0,1000).apply {
            duration = 180000
            interpolator = LinearInterpolator()
            addUpdateListener {

                mProgressAngle = (it.animatedValue as Int) * 360 / 1000f
                mRotate = (mRotate + 1) % 360
                invalidate()
            }
        }

        testAnimator()
    }

    private fun refreshShader() {
        mBitmapShader = BitmapShader(mBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        invalidate()
    }


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.save()
        //绘制圆形图片

        val scale = width / mBitmap.width
        val half = (width / 2).toFloat()
        canvas?.rotate(mRotate.toFloat(), half, half)
        mMatrix.setScale(scale.toFloat(), scale.toFloat())
        mBitmapShader.setLocalMatrix(mMatrix)
        mPaint.shader = mBitmapShader
        canvas?.drawCircle(half, half, half - mProgressWidth, mPaint)
        canvas?.restore()
        //绘制圆环processBar
        mProcessBarPaint.color = Color.GRAY
        canvas?.drawCircle(half, half, half - mProgressWidth, mProcessBarPaint)
        //绘制进度
        mProcessBarPaint.color = Color.RED
        canvas?.drawArc(
            mProgressWidth,
            mProgressWidth,
            2 * half - mProgressWidth,
            2 * half - mProgressWidth,
            270f,
            mProgressAngle,
            false,
            mProcessBarPaint
        )
    }



    fun testAnimator() {
        mProgressAnimator.start()
    }

    fun setBitmap(bitmap: Bitmap) {
        mBitmap = bitmap
        refreshShader()
    }
}