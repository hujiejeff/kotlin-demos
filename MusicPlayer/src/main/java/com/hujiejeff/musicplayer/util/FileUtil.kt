package com.hujiejeff.musicplayer.util

import android.os.Build
import android.os.FileUtils
import androidx.annotation.RequiresApi
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

/**
 * Create by hujie on 2020/1/3
 */


/**
 * 文件复制
 * */
fun copy(src: String, des: String) {
    checkAndroidVersionAction(Build.VERSION_CODES.O, {
        copyByNio(src, des)
    }, {
        copyByIO(src, des)
    })
}


@RequiresApi(Build.VERSION_CODES.O)
fun copyByNio(src: String, des: String) {
    val srcPath = Paths.get(src)
    val desPath = Paths.get(des)
    Files.copy(srcPath, desPath, StandardCopyOption.REPLACE_EXISTING)

}


fun copyByIO(src: String, des: String) {
    val fis = FileInputStream(src)
    val fos = FileOutputStream(des)
    val buf = ByteArray(1024)
    var len = fis.read(buf)
    while (len != -1) {
        fos.write(buf, 0, len)
        len = fis.read(buf)
    }
    fis.close()
    fos.close()
}

fun copyByAndorid(src: String, des: String) {
    val srcFd = FileInputStream(src).fd
    val desFd  = FileOutputStream(des).fd
    FileUtils.copy(srcFd, desFd)
    //or
//    FileUtils.copy(fis, fos)
}

fun copyByChannel(src: String, des: String) {
    val srcChannel = FileInputStream(src).channel
    val desChannel = FileOutputStream(des).channel
    desChannel.transferFrom(srcChannel, 0, srcChannel.size())
    srcChannel.close()
    desChannel.close()
}