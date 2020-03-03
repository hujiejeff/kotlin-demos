package com.hujiejeff.musicplayer.localmusic

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hujiejeff.musicplayer.base.App
import com.hujiejeff.musicplayer.data.entity.Album
import com.hujiejeff.musicplayer.data.entity.Artist
import com.hujiejeff.musicplayer.data.entity.Music
import com.hujiejeff.musicplayer.data.source.Callback
import com.hujiejeff.musicplayer.data.source.DataRepository
import com.hujiejeff.musicplayer.player.AudioPlayer

class LocalMusicViewModel(private val dataRepository: DataRepository): ViewModel() {
    private val player: AudioPlayer = AudioPlayer.INSTANCE

    //数据加载
    private val _musicDataLoading = MutableLiveData<Boolean>()
    val musicDataLoading: LiveData<Boolean>
        get() = _musicDataLoading

    private val _albumDataLoading = MutableLiveData<Boolean>()
    val albumDataLoading: LiveData<Boolean>
        get() = _albumDataLoading

    private val _artistDataLoading = MutableLiveData<Boolean>()
    val artistDataLoading: LiveData<Boolean>
        get() = _artistDataLoading

    //错误提示
    private val _isDataLoadingError = MutableLiveData<Boolean>()
    val isDataLoadingError: LiveData<Boolean>
        get() = _isDataLoadingError

    //音乐列表
    private val _musicItems = MutableLiveData<List<Music>>()
    val musicItems: LiveData<List<Music>>
        get() = _musicItems

    //专辑列表
    private val _albumItems = MutableLiveData<List<Album>>().apply { value = emptyList() }
    val albumItems: LiveData<List<Album>>
        get() = _albumItems

    //歌手列表
    private val _artistItems = MutableLiveData<List<Artist>>().apply { value = emptyList() }
    val artistItems: LiveData<List<Artist>>
        get() = _artistItems

    //加载音乐列表
    fun loadMusicList() {
        _musicDataLoading.value = true
        dataRepository.getLocalMusicList(object : Callback<List<Music>> {
            override fun onLoaded(dataList: List<Music>) {
                //因为是立即回调，所以要先设置player得list再触发事件，不然后面会先出发回调，会导致player.currentMusic获得不到
                player.mMusicList.addAll(dataList)
                _musicItems.value = dataList
                App.playerViewModel.musicItems.value = dataList
                _musicDataLoading.value = false
                _isDataLoadingError.value = false
            }

            override fun onFailed(mes: String) {
                _isDataLoadingError.value = true
            }
        })
    }

    //加载专辑列表
    fun loadAlbumList() {
        _albumDataLoading.value = true
        dataRepository.getLocalAlbumList(object : Callback<List<Album>> {
            override fun onLoaded(dataList: List<Album>) {
                _albumItems.value = dataList
                _albumDataLoading.value = false
                _isDataLoadingError.value = false
            }

            override fun onFailed(mes: String) {
                _isDataLoadingError.value = true
            }

        })
    }

    //加载歌手列表
    fun loadArtistList() {
        _artistDataLoading.value = true
        dataRepository.getLocalArtistList(object : Callback<List<Artist>> {
            override fun onLoaded(dataList: List<Artist>) {
                _artistItems.value = dataList
                _artistDataLoading.value = false
                _isDataLoadingError.value = false
            }

            override fun onFailed(mes: String) {
                _isDataLoadingError.value = true
            }

        })
    }
}