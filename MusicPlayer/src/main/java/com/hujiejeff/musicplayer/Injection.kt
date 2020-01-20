package com.hujiejeff.musicplayer

import android.content.Context
import com.hujiejeff.musicplayer.data.source.DataRepository
import com.hujiejeff.musicplayer.data.source.local.LocalMusicDataSource
import com.hujiejeff.musicplayer.data.source.remote.Apis
import com.hujiejeff.musicplayer.data.source.remote.NetMusicDataSource
import com.hujiejeff.musicplayer.data.source.remote.baseUrl
import com.hujiejeff.musicplayer.execute.AppExecutors
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Injection {
    val appExecutors = AppExecutors()
    fun provideDataRepository(context: Context): DataRepository {
        return DataRepository(
            LocalMusicDataSource(appExecutors),
            NetMusicDataSource(provideRetrofitApis(), appExecutors)
        )
    }

    private fun provideRetrofitApis(): Apis =
        Retrofit
            .Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(Apis::class.java)
}