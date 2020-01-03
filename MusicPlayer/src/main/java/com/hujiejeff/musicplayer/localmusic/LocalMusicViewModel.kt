package com.hujiejeff.musicplayer.localmusic

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hujiejeff.musicplayer.data.entity.Music
import com.hujiejeff.musicplayer.data.source.DataRepository
import com.hujiejeff.musicplayer.data.source.LocalDataSource

/**
 * Create by hujie on 2020/1/3
 */
class LocalMusicViewModel(private val dataRepository: DataRepository) : ViewModel() {

    //数据加载
    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean>
        get() = _dataLoading

    private val _items = MutableLiveData<List<Music>>().apply { value = emptyList() }
    val items: LiveData<List<Music>>
        get() = _items

    fun start() {

    }

    fun loadMusicList() {
        _dataLoading.value = true
        dataRepository.getLocalMusicList(object : LocalDataSource.Callback<Music> {
            override fun onLoaded(dataList: MutableList<Music>) {
                _items.value = dataList
                _dataLoading.value = false
            }

            override fun onFailed(mes: String) {

            }
        })
    }

}