package com.hujiejeff.musicplayer.data.http

import com.hujiejeff.musicplayer.data.Preference
import okhttp3.Interceptor
import okhttp3.Response

/**
 * 读取Cookie保持到文件
 * */
class SaveCookiesInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain?): Response {
        val originResponse = chain?.proceed(chain.request())
        if (originResponse?.headers("Set-Cookie")?.isNotEmpty()!!) {
            val cookies = mutableSetOf<String>()
            cookies.addAll(originResponse.headers("Set-Cookie"))
            Preference.cookies = cookies
        }
        return originResponse
    }
}