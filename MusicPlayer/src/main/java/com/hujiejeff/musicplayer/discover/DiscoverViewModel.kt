package com.hujiejeff.musicplayer.discover

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hujiejeff.musicplayer.base.App
import com.hujiejeff.musicplayer.data.entity.*
import com.hujiejeff.musicplayer.data.source.Callback
import com.hujiejeff.musicplayer.data.source.DataRepository
import com.hujiejeff.musicplayer.util.logD

/**
 * Create by hujie on 2020/3/2
 */
class DiscoverViewModel(private val dataRepository: DataRepository) : ViewModel() {

    private val _recomendPlaylists =  MutableLiveData<List<RecommendPlayList>>()
    val recomendPlaylists: LiveData<List<RecommendPlayList>>
        get() = _recomendPlaylists

    private val _newSongs =  MutableLiveData<List<RecommendNewSong>>()
    val newSongs: LiveData<List<RecommendNewSong>>
        get() = _newSongs


    private val _newAlbums =  MutableLiveData<List<RecommendNewAlbum>>()
    val newAlbums: LiveData<List<RecommendNewAlbum>>
        get() = _newAlbums

    private val _loading = MutableLiveData<Int>()
    val loading: LiveData<Int>
        get() = _loading

    fun start() {
        _loading.value = 0
        loadRecomendPlaylists()
        loadNewAlbums(9)
        loadNewSongs(9)
    }

    fun loadRecomendPlaylists(count: Int = 6) {
        dataRepository.getRecommendPlaylists(count, object : Callback<List<RecommendPlayList>> {
            override fun onLoaded(t: List<RecommendPlayList>) {
                _recomendPlaylists.value = t
                _loading.value = _loading.value!! + 1
            }

            override fun onFailed(mes: String) {
            }

        })
    }


    fun loadNewAlbums(count: Int = 3) {
        dataRepository.getNewAlbums(count, object : Callback<List<RecommendNewAlbum>> {
            override fun onLoaded(t: List<RecommendNewAlbum>) {
                _newAlbums.value = t
                _loading.value = _loading.value!! + 1
            }

            override fun onFailed(mes: String) {
            }

        })
    }

    fun loadNewSongs(count: Int = 3) {
        dataRepository.getNewSongs(count, object : Callback<List<RecommendNewSong>> {
            override fun onLoaded(t: List<RecommendNewSong>) {
                _newSongs.value = t
                _loading.value = _loading.value!! + 1
            }

            override fun onFailed(mes: String) {
            }

        })
    }


}