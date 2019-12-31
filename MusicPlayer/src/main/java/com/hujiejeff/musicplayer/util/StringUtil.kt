package com.hujiejeff.musicplayer.util

/**
 * Create by hujie on 2019/12/31
 */

fun getMusicTimeFormatString(msc: Int): String {
    var sc = msc / 1000
    val minute = sc / 60
    sc %= 60
    return getForTenMoreOrLessTen(minute) + ":" + getForTenMoreOrLessTen(sc)
}

fun getForTenMoreOrLessTen(num : Int)  = if (num < 10) "0$num" else "$num"