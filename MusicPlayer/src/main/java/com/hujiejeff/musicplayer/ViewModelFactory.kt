package com.hujiejeff.musicplayer

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hujiejeff.musicplayer.data.source.DataRepository
import com.hujiejeff.musicplayer.discover.DiscoverViewModel
import com.hujiejeff.musicplayer.discover.playlistsqure.MainPlaylistViewModel
import com.hujiejeff.musicplayer.discover.playlist.PlaylistViewModel
import com.hujiejeff.musicplayer.discover.search.SearchViewModel
import com.hujiejeff.musicplayer.localmusic.LocalMusicViewModel
import com.hujiejeff.musicplayer.player.PlayerViewModel

class ViewModelFactory private constructor(private val dataRepository: DataRepository) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>) =
        with(modelClass) {
            when {
                isAssignableFrom(PlayerViewModel::class.java) -> PlayerViewModel(dataRepository)
                isAssignableFrom(MainPlaylistViewModel::class.java) -> MainPlaylistViewModel(
                    dataRepository
                )
                isAssignableFrom(PlaylistViewModel::class.java) -> PlaylistViewModel(
                    dataRepository
                )
                isAssignableFrom(DiscoverViewModel::class.java) -> DiscoverViewModel(dataRepository)
                isAssignableFrom(LocalMusicViewModel::class.java) -> LocalMusicViewModel(dataRepository)
                isAssignableFrom(SearchViewModel::class.java) -> SearchViewModel(dataRepository)
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