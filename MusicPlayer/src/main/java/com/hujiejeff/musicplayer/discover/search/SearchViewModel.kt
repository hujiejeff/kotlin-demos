package com.hujiejeff.musicplayer.discover.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hujiejeff.musicplayer.data.entity.*
import com.hujiejeff.musicplayer.data.source.Callback
import com.hujiejeff.musicplayer.data.source.DataRepository
import com.hujiejeff.musicplayer.util.logD

/**
 * Create by hujie on 2020/3/4
 */
class SearchViewModel(private val dataRepository: DataRepository) : ViewModel() {
    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean>
        get() = _loading

    private val _searchHistory = MutableLiveData<MutableSet<String>>()
    val searchHistory: LiveData<MutableSet<String>>
        get() = _searchHistory

    private val _hotSearch = MutableLiveData<List<HotSearchString>>()
    val hotSearch: LiveData<List<HotSearchString>>
        get() = _hotSearch

    private val _currentSearchKey = MutableLiveData<String>()
    val currentSearchKey: LiveData<String>
        get() = _currentSearchKey

    private val _searchResultMap: MutableMap<SearchType, MutableLiveData<List<*>>> = mutableMapOf()
    val searchResultMap: Map<SearchType, LiveData<List<*>>>
        get() = _searchResultMap

    private val _searchLoadingMap: MutableMap<SearchType, MutableLiveData<Boolean>> = mutableMapOf()
    val searchLoadingMap: Map<SearchType, LiveData<Boolean>>
        get() = _searchLoadingMap



    fun loadHistory() {
        dataRepository.getSearchHistorySet(object : Callback<Set<String>> {
            override fun onLoaded(t: Set<String>) {

                _searchHistory.value = t.toMutableSet()
            }

            override fun onFailed(mes: String) {
            }

        })
    }

    fun clearHistory() {
        _searchHistory.value = mutableSetOf()
    }

    fun startSearch(key: String) {
        val set = _searchHistory.value?.apply { add(key) }
        _searchHistory.value = set
        dataRepository.saveSearchHistorySet(set!!)
        _currentSearchKey.value = key
    }

    fun loadHotSearch() {
        _loading.value = true
        dataRepository.loadHotSearchStrings(object : Callback<List<HotSearchString>> {
            override fun onLoaded(t: List<HotSearchString>) {
                _hotSearch.value = t
                _loading.value = false
            }

            override fun onFailed(mes: String) {

            }

        })
    }

    fun checkType(type: SearchType) {
        if (!_searchLoadingMap.containsKey(type) || !_searchResultMap.containsKey(type)) {
            _searchLoadingMap[type] = MutableLiveData()
            _searchResultMap[type] = MutableLiveData()
        }
    }





    fun loadSearchResult(type: SearchType, offset: Int, limit: Int) {
        val keywords = _currentSearchKey.value!!
        when (type) {
            SearchType.SONG -> {
                loadSearchResult<SearchSong, SearchSongResultResponse>(
                    type,
                    keywords,
                    offset,
                    limit,
                    SearchSongResultResponse::class.java
                )
            }
            SearchType.ARTIST -> {
                loadSearchResult<SearchArtist, SearchArtistResultResponse>(
                    type,
                    keywords,
                    offset,
                    limit,
                    SearchArtistResultResponse::class.java
                )
            }

            SearchType.ALBUM -> {
                loadSearchResult<SearchAlbum, SearchAlbumResultResponse>(
                    type,
                    keywords,
                    offset,
                    limit,
                    SearchAlbumResultResponse::class.java
                )
            }

            SearchType.PLAYLIST -> {
                loadSearchResult<SearchPlayList, SearchPlayListResultResponse>(
                    type,
                    keywords,
                    offset,
                    limit,
                    SearchPlayListResultResponse::class.java
                )
            }

            SearchType.USER -> {
                loadSearchResult<SearchUser, SearchUserResultResponse>(
                    type,
                    keywords,
                    offset,
                    limit,
                    SearchUserResultResponse::class.java
                )
            }
            else -> {

            }
        }

    }

    private fun <T, K> loadSearchResult(
        type: SearchType,
        keywords: String,
        offset: Int,
        limit: Int,
        cls: Class<K>
    ) {
        _searchLoadingMap[type]?.value = true
        dataRepository.loadSearchResult(
            type,
            keywords,
            offset,
            limit,
            cls,
            object : Callback<List<T>> {
                override fun onLoaded(t: List<T>) {
                    _searchResultMap[type]?.value = t
                    _searchLoadingMap[type]?.value = false
                }

                override fun onFailed(mes: String) {
                }

            })
    }

}