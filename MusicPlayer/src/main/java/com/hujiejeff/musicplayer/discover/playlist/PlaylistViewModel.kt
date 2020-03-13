package com.hujiejeff.musicplayer.discover.playlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hujiejeff.musicplayer.data.entity.PlayListDetail
import com.hujiejeff.musicplayer.data.source.Callback
import com.hujiejeff.musicplayer.data.source.DataRepository


class PlaylistViewModel(private val dataRepository: DataRepository):ViewModel() {
    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean>
        get() = _loading

    private val _playlistDetail = MutableLiveData<PlayListDetail>()
    val playlistDetail: LiveData<PlayListDetail>
        get() = _playlistDetail

    fun loadPlaylistDetail(id: Long) {
        _loading.value = true
        dataRepository.getPlayListDetail(id, object : Callback<PlayListDetail> {
            override fun onLoaded(t: PlayListDetail) {
                _playlistDetail.value = t

                _loading.value = false
            }

            override fun onFailed(mes: String) {
            }

        })
    }
}