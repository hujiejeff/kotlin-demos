package com.hujiejeff.musicplayer.data.http

import com.hujiejeff.musicplayer.data.Preference
import okhttp3.Interceptor
import okhttp3.Response

/**
 * 附加cookie参数到请求
 * */
class ReadCookiesInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain?): Response {
        val builder = chain?.request()?.newBuilder()
        val cookies = Preference.cookies
        for (cookie in cookies) {
            builder?.addHeader("Cookie", cookie)
        }
        return chain?.proceed(builder?.build())!!
    }
}