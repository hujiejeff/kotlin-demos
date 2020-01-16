package com.hujiejeff.musicplayer.netmusic.playlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hujiejeff.musicplayer.base.App
import com.hujiejeff.musicplayer.data.entity.PlayList
import com.hujiejeff.musicplayer.data.entity.SubCat
import com.hujiejeff.musicplayer.data.source.Callback

/**
 * Create by hujie on 2020/1/10
 */
class PlaylistViewModel : ViewModel() {
    private val dataRepository by lazy { App.dateRepository }

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean>
        get() = _dataLoading
    private val _errorMsg = MutableLiveData<Boolean>()
    val errorMsg: LiveData<Boolean>
        get() = _errorMsg

    private val _subCats = MutableLiveData<List<SubCat>>()
    val subCats: LiveData<List<SubCat>>
        get() = _subCats
    private val _mapPlayLists = mutableMapOf<String, MutableLiveData<List<PlayList>>>()
    val mapPlayLists: Map<String, LiveData<List<PlayList>>>
        get() = _mapPlayLists

    //加载父分类
    fun loadParentCat() {

    }

    //加载子分类
    fun loadSubCat() {
        _dataLoading.value = true
        dataRepository.getSubCat(object : Callback<List<SubCat>> {

            override fun onLoaded(t: List<SubCat>) {
                _subCats.value = t
            }

            override fun onFailed(mes: String) {
                _errorMsg.value = true
            }
        })
    }

    //加载分类下的歌单列表
    fun loadPlayLists(cat: String, limit: Int, offset: Int, order: String) {
        _dataLoading.value = true
        if (_mapPlayLists[cat] == null) {
            _mapPlayLists[cat] = MutableLiveData()
        }

        dataRepository.getPlayLists(cat, 10, "hot", object : Callback<List<PlayList>> {
            override fun onLoaded(t: List<PlayList>) {
                _mapPlayLists[cat]?.value = t
                _dataLoading.value = false
            }

            override fun onFailed(mes: String) {
                _errorMsg.value = true
            }

        })
    }


}