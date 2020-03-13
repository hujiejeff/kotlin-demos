package com.hujiejeff.musicplayer

import android.content.Context
import com.hujiejeff.musicplayer.data.source.DataRepository
import com.hujiejeff.musicplayer.data.source.local.LocalDataSourceImpl
import com.hujiejeff.musicplayer.data.source.remote.Apis
import com.hujiejeff.musicplayer.data.source.remote.NetDataSourceImpl
import com.hujiejeff.musicplayer.data.source.remote.baseUrl
import com.hujiejeff.musicplayer.component.AppExecutors
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Injection {
    val appExecutors = AppExecutors()
    fun provideDataRepository(context: Context): DataRepository {
        return DataRepository(
            LocalDataSourceImpl(appExecutors),
            NetDataSourceImpl(provideRetrofitApis(), appExecutors)
        )
    }

    private fun provideRetrofitApis(): Apis =
        Retrofit
            .Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(Apis::class.java)
}