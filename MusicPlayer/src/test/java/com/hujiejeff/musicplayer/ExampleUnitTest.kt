package com.hujiejeff.musicplayer

import com.hujiejeff.musicplayer.data.source.remote.Apis
import okhttp3.*
import org.junit.Test

import org.junit.Assert.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun testNormalPlayLists() {
        val response = getRetrofitApis().getNormalPlayLists().execute()
        println(response.body().toString())
    }

    @Test
    fun testGetPlayListDetail() {
        val response = getRetrofitApis().getPlayListDetail(3077041885).execute()
        println(response.body().toString())
    }

    fun getRetrofitApis(): Apis =
        Retrofit
            .Builder()
            .baseUrl("http://musicapi.leanapp.cn")
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(Apis::class.java)




    @Test
    fun testAsync() {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://www.wanandroid.com/article/list/0/json")
            .build()
        val call = client.newCall(request)
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                println("onFailure")
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                println(Thread.currentThread().id)
                println("ttt")
                println(response.code())
                println(response.message())
                println(response.body().string())
            }
        })

        println("out" + Thread.currentThread().id)

        try {
            Thread.sleep(10000)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

    }
}
