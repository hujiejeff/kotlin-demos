package com.hujiejeff.musicplayer

import android.app.Activity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hujiejeff.musicplayer.data.entity.SearchSongResultResponse
import com.hujiejeff.musicplayer.data.source.remote.Apis
import com.hujiejeff.musicplayer.util.logD
import okhttp3.*
import org.junit.Test

import org.junit.Assert.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import java.lang.reflect.ParameterizedType


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
    fun testGetPlayListCatList() {
        val response = getRetrofitApis().getPlayListCatList().execute()
        println(response.body().toString())
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

    @Test
    fun testGetMusicUrl() {
        val  response = getRetrofitApis().getMusicUrl(513359463).execute()
        println(response.body().toString())
    }

    @Test
    fun testGetRecommendNewSong(){
        val response = getRetrofitApis().getNewSong().execute()
        println(response.body().toString())
    }

    @Test
    fun testGetRecommendNewAlbum(){
        val response = getRetrofitApis().getNewAlbum().execute()
        println(response.body().toString())
    }

    @Test
    fun testHotSearch(){
        val response = getRetrofitApis().getHotSerach().execute()
        println(response.body().toString())
    }

    @Test
    fun testResponseBody() {
        val re = getRetrofitApis().getSearchResult("天下", 1, 0, 10).execute()

        val t =
            Gson().fromJson<SearchSongResultResponse>(re.body().string(), object : TypeToken<SearchSongResultResponse>(){}.type)
        println("loadSearchResult $t")
        //todo 更新API的版本
    }

    @Test
    fun testR() {
        test(SearchSongResultResponse::class.java)
    }

    private fun <T> test(clazz: Class<T>) {

        val re = getRetrofitApis().getSearchResult("天下", 1, 0, 10).execute()
        println(clazz.toString())
        val t =
            Gson().fromJson(re.body().string(), clazz)
        println("loadSearchResult $t")
    }

    @Test
    fun tstGen() {

    }

    private fun <T> testGen2(clazz: Class<T>) {
        val parametclass = clazz.genericSuperclass as ParameterizedType?
        val actualTypeArguments = parametclass!!.getActualTypeArguments()
//        clazz = actualTypeArguments[0] as Class<T>
    }


    fun getRetrofitApis(): Apis =
        Retrofit
            .Builder()
            .baseUrl("http://localhost:3000")
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

    @Test
    fun testClass() {
        println(Activity::class.java.canonicalName)
    }
}
