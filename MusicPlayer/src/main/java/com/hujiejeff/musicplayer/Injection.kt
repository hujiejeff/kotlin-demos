package com.hujiejeff.musicplayer

import android.content.Context
import com.hujiejeff.musicplayer.data.source.DataRepository
import com.hujiejeff.musicplayer.data.source.local.LocalMusicDataSource
import com.hujiejeff.musicplayer.execute.AppExecutors

object Injection {
    fun provideDataRepository(context: Context): DataRepository {
        return DataRepository(LocalMusicDataSource(AppExecutors()))
    }
}