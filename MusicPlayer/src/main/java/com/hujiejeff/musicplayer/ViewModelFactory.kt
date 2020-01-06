package com.hujiejeff.musicplayer

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hujiejeff.musicplayer.data.source.DataRepository
import com.hujiejeff.musicplayer.localmusic.LocalMusicViewModel

class ViewModelFactory private constructor(private val dataRepository: DataRepository) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>) =
        with(modelClass) {
            when {
                isAssignableFrom(LocalMusicViewModel::class.java) -> LocalMusicViewModel(

                )
                else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        } as T

    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null

        fun getInstance(application: Application) =
            INSTANCE ?: synchronized(ViewModelFactory::class.java) {
                INSTANCE
                    ?: ViewModelFactory(Injection.provideDataRepository(application.applicationContext))
                        .also { INSTANCE = it }
            }

    }

}