package com.hujiejeff.musicplayer.data.source

import com.hujiejeff.musicplayer.data.entity.*
import com.hujiejeff.musicplayer.data.source.local.LocalMusicDataSource
import com.hujiejeff.musicplayer.data.source.remote.NetMusicDataSource

/**
 * Create by hujie on 2020/1/3
 */
class DataRepository(
    private val localMusicDataSource: LocalMusicDataSource,
    private val netMusicDataSource: NetMusicDataSource
) : LocalDataSource {

    private val cacheMusicList: MutableList<Music> = mutableListOf()
    private val cacheAlbumList: MutableList<Album> = mutableListOf()
    private val cacheArtistList: MutableList<Artist> = mutableListOf()

    private val cacheParentCat: MutableList<String> = mutableListOf()
    private val cacheSubCats: MutableList<SubCat> = mutableListOf()

    private val cachePlayListMap: MutableMap<String, List<PlayList>> = mutableMapOf()
    private  var cachePlayListDetail: PlayListDetail? = null

    private var cacheIsDirty: Boolean = false
    override fun getLocalMusicList(callback: Callback<List<Music>>) {
        if (cacheMusicList.isNotEmpty()) {
            callback.onLoaded(cacheMusicList)
            return
        }

        localMusicDataSource.getLocalMusicList(object : Callback<List<Music>> {
            override fun onLoaded(dataList: List<Music>) {
                cacheMusicList.addAll(dataList)
                callback.onLoaded(cacheMusicList)
            }

            override fun onFailed(mes: String) {
                callback.onFailed(mes)
            }
        })
    }

    override fun getLocalAlbumList(callback: Callback<List<Album>>) {
        if (cacheAlbumList.isNotEmpty()) {
            callback.onLoaded(cacheAlbumList)
            return
        }

        localMusicDataSource.getLocalAlbumList(object : Callback<List<Album>> {
            override fun onLoaded(dataList: List<Album>) {
                cacheAlbumList.addAll(dataList)
                callback.onLoaded(cacheAlbumList)
            }

            override fun onFailed(mes: String) {
                callback.onFailed(mes)
            }
        })
    }

    override fun getLocalArtistList(callback: Callback<List<Artist>>) {
        if (cacheArtistList.isNotEmpty()) {
            callback.onLoaded(cacheArtistList)
            return
        }

        localMusicDataSource.getLocalArtistList(object : Callback<List<Artist>> {
            override fun onLoaded(dataList: List<Artist>) {
                cacheArtistList.addAll(dataList)
                callback.onLoaded(cacheArtistList)
            }

            override fun onFailed(mes: String) {
                callback.onFailed(mes)
            }
        })
    }


    fun getParentCat(callback: Callback<List<String>>){
        if (cacheParentCat.isNotEmpty()) {
            callback.onLoaded(cacheParentCat)
            return
        }

        netMusicDataSource.loadPlayListCatList(object : Callback<PlayListCatlistResponse> {
            override fun onLoaded(t: PlayListCatlistResponse) {
                val parentCat = t.categories.values.toList()
                val subCats = t.sub
                cacheParentCat.addAll(parentCat)
                cacheSubCats.addAll(subCats)
                callback.onLoaded(parentCat)
            }

            override fun onFailed(mes: String) {
                callback.onFailed(mes)
            }
        })
    }

    fun getSubCat(callback: Callback<List<SubCat>>) {
        if (cacheSubCats.isNotEmpty()) {
            callback.onLoaded(cacheSubCats)
            return
        }

        netMusicDataSource.loadPlayListCatList(object : Callback<PlayListCatlistResponse> {
            override fun onLoaded(t: PlayListCatlistResponse) {
                val parentCat = t.categories.values.toList()
                val subCats = t.sub
                cacheParentCat.addAll(parentCat)
                cacheSubCats.addAll(subCats)
                callback.onLoaded(subCats)
            }

            override fun onFailed(mes: String) {
                callback.onFailed(mes)
            }
        })
    }

    fun getPlayLists(cat: String, limit: Int, order: String, callback: Callback<List<PlayList>>){
        val playLists = cachePlayListMap[cat]
        if (playLists != null && playLists.isNotEmpty()) {
            callback.onLoaded(playLists)
            return
        }

        netMusicDataSource.loadNormalPlayLists(cat, 10, "hot", object : Callback<PlayListsResponse> {
            override fun onLoaded(t: PlayListsResponse) {
                val cat = t.cat
                val playLists: List<PlayList> = t.playlists
                if (cachePlayListMap[cat] == null) {
                    cachePlayListMap[cat] = playLists
                }

                callback.onLoaded(playLists)

            }

            override fun onFailed(mes: String) {
                callback.onFailed(mes)
            }

        })
    }

    fun getPlayListDetail(id: Long, callback: Callback<PlayListDetail>) {
        if (cachePlayListDetail != null) {
            callback.onLoaded(cachePlayListDetail!!)
        }

        netMusicDataSource.loadPlaylistDetail(id, object : Callback<PlayListDetailResponse>{
            override fun onLoaded(t: PlayListDetailResponse) {
                cachePlayListDetail = t.playlist
                callback.onLoaded(cachePlayListDetail!!)
            }

            override fun onFailed(mes: String) {
                callback.onFailed(mes)
            }
        })
    }

    fun getTrackDetail(id: Long, callback: Callback<TrackData>) {
        netMusicDataSource.loadTrackDetail(id, object : Callback<TrackResponse> {
            override fun onLoaded(t: TrackResponse) {
                callback.onLoaded(t.data[0])
            }

            override fun onFailed(mes: String) {
                callback.onFailed(mes)
            }
        })
    }

}