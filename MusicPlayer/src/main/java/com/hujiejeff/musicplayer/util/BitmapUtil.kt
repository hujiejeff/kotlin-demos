package com.hujiejeff.musicplayer.util

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.os.Build
import java.io.File
import java.io.FileOutputStream

/**
 * 保存bitmap
 * */
fun saveBitmap(path: String, bitmap: Bitmap, format: Bitmap.CompressFormat) {
    FileOutputStream(File(path)).apply {
        bitmap.compress(format, 90, this)
        flush()
        close()
    }
}


/**
 * 获取矢量图bitmap
 * */
fun getVecotorBitmap(context: Context, vectorDrawableId: Int): Bitmap {
    return checkAndroidVersionAction<Bitmap>(
        Build.VERSION_CODES.O, {
            val vectorDrawable = context.getDrawable(vectorDrawableId)
            val bitmap = Bitmap.createBitmap(
                vectorDrawable!!.intrinsicWidth,
                vectorDrawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            vectorDrawable.setBounds(0, 0, canvas.width, canvas.height)
            vectorDrawable.draw(canvas)
            bitmap
        }, {
            val decodeResourceBitmap =
                BitmapFactory.decodeResource(context.resources, vectorDrawableId)
            decodeResourceBitmap
        })!!
}

/**
* 加载合适尺寸得bitmap防止浪费内存
* */
fun decodeSimpledBitmapFromResource(resources: Resources, redId: Int, reqWidth: Int, reqHeight: Int): Bitmap {
    val options = BitmapFactory.Options().apply {
        inJustDecodeBounds = true//只加载尺寸，不加载图片
        BitmapFactory.decodeResource(resources, redId, this)
        inSampleSize = calculateInSampleSize(this, reqWidth, reqHeight)
        inJustDecodeBounds = false//加载图片
    }
    return BitmapFactory.decodeResource(resources, redId, options)
}

/**
* 计算合适尺寸
* */
fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
    var inSampleSize = 1
    val width = options.outWidth
    val height = options.outHeight
    if (width > reqWidth && height > reqHeight) {
        val halfWidth = width / 2
        val halfHeight = height / 2
        while (halfWidth / inSampleSize > reqWidth && halfHeight / inSampleSize > reqHeight) {
            inSampleSize *= 2
        }
    }
    return  inSampleSize
}

