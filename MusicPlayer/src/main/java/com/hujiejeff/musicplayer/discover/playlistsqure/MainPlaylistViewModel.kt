package com.hujiejeff.musicplayer.discover.playlistsqure

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hujiejeff.musicplayer.data.entity.PlayList
import com.hujiejeff.musicplayer.data.entity.SubCat
import com.hujiejeff.musicplayer.data.source.Callback
import com.hujiejeff.musicplayer.data.source.DataRepository
import com.hujiejeff.musicplayer.util.logD

/**
 * Create by hujie on 2020/3/13
 */
class MainPlaylistViewModel(private val dataRepository: DataRepository): ViewModel() {

    //歌单分类
    private val _subCatList = MutableLiveData<List<SubCat>>().apply { value = mutableListOf() }
    val subCatList: LiveData<List<SubCat>>
        get() = _subCatList


    //歌单Map
    val playListMap: MutableMap<SubCat, MutableLiveData<List<PlayList>>> = mutableMapOf()


    //isLoading
    val loadingMap: MutableMap<SubCat, MutableLiveData<Boolean>> = mutableMapOf()




    fun loadSubCat() {
        dataRepository.getSubCat(object : Callback<List<SubCat>> {
            override fun onLoaded(t: List<SubCat>) {
                t.forEach { subCat ->
                    playListMap[subCat] = MutableLiveData()
                    loadingMap[subCat] = MutableLiveData()
                }
                _subCatList.value = t.subList(0,10)
            }

            override fun onFailed(mes: String) {
                logD("onFailed: $mes")
            }

        })
    }


    fun loadPlaylists(subCat: SubCat) {
        loadingMap[subCat]?.value = true
        dataRepository.getPlayLists(subCat.name, 10, "hot", object : Callback<List<PlayList>> {
            override fun onLoaded(t: List<PlayList>) {
                playListMap[subCat]?.value = t
                loadingMap[subCat]?.value = false
            }

            override fun onFailed(mes: String) {
                logD(mes)
            }
        })
    }

}