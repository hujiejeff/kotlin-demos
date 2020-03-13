package com.hujiejeff.musicplayer.data.source

import com.hujiejeff.musicplayer.data.entity.*
import com.hujiejeff.musicplayer.discover.search.SearchType
import com.hujiejeff.musicplayer.util.logD

/**
 * Create by hujie on 2020/1/3
 */
class DataRepository(
    private val localDataSource: LocalDataSource,
    private val netDataSource: NetDataSource
) : LocalDataSource {

    private val cacheMusicList: MutableList<Music> = mutableListOf()
    private val cacheAlbumList: MutableList<Album> = mutableListOf()
    private val cacheArtistList: MutableList<Artist> = mutableListOf()

    private val cacheParentCat: MutableList<String> = mutableListOf()
    private val cacheSubCats: MutableList<SubCat> = mutableListOf()

    private val cachePlayListMap: MutableMap<String, List<PlayList>> = mutableMapOf()
    private var cachePlayListDetail: PlayListDetail? = null

    private var cacheIsDirty: Boolean = false
    override fun getLocalMusicList(callback: Callback<List<Music>>) {
        if (cacheMusicList.isNotEmpty()) {
            callback.onLoaded(cacheMusicList)
            return
        }

        localDataSource.getLocalMusicList(object : Callback<List<Music>> {
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

        localDataSource.getLocalAlbumList(object : Callback<List<Album>> {
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

        localDataSource.getLocalArtistList(object : Callback<List<Artist>> {
            override fun onLoaded(dataList: List<Artist>) {
                cacheArtistList.addAll(dataList)
                callback.onLoaded(cacheArtistList)
            }

            override fun onFailed(mes: String) {
                callback.onFailed(mes)
            }
        })
    }


    fun getParentCat(callback: Callback<List<String>>) {
        if (cacheParentCat.isNotEmpty()) {
            callback.onLoaded(cacheParentCat)
            return
        }

        netDataSource.loadPlayListCatList(object : Callback<PlayListCatlistResponse> {
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

        netDataSource.loadPlayListCatList(object : Callback<PlayListCatlistResponse> {
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

    fun getPlayLists(cat: String, limit: Int, order: String, callback: Callback<List<PlayList>>) {
        val playLists = cachePlayListMap[cat]
        if (playLists != null && playLists.isNotEmpty()) {
            callback.onLoaded(playLists)
            return
        }

        netDataSource.loadNormalPlayLists(cat, 10, "hot", object : Callback<PlayListsResponse> {
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

        netDataSource.loadPlaylistDetail(id, object : Callback<PlayListDetailResponse> {
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
        netDataSource.loadTrackDetail(id, object : Callback<TrackResponse> {
            override fun onLoaded(t: TrackResponse) {
                callback.onLoaded(t.data[0])
            }

            override fun onFailed(mes: String) {
                callback.onFailed(mes)
            }
        })
    }

    fun getRecommendPlaylists(limit: Int, callback: Callback<List<RecommendPlayList>>) {
        netDataSource.loadRecommendPlaylists(limit, object : Callback<RecommendPlayListResponse> {
            override fun onLoaded(t: RecommendPlayListResponse) {
                callback.onLoaded(t.result)
            }

            override fun onFailed(mes: String) {
                callback.onFailed(mes)
            }
        })
    }

    fun getNewAlbums(count: Int, callback: Callback<List<RecommendNewAlbum>>) {
        netDataSource.loadRecommendNewAlbum(object : Callback<RecommendNewAlbumResponse> {
            override fun onLoaded(t: RecommendNewAlbumResponse) {
                callback.onLoaded(t.albums.take(count))
            }

            override fun onFailed(mes: String) {
                callback.onFailed(mes)
            }
        })
    }

    fun getNewSongs(count: Int, callback: Callback<List<RecommendNewSong>>) {
        netDataSource.loadRecommendNewSong(object : Callback<RecommendNewSongResponse> {
            override fun onLoaded(t: RecommendNewSongResponse) {
                callback.onLoaded(t.result.take(count))
            }

            override fun onFailed(mes: String) {
                callback.onFailed(mes)
            }
        })
    }

    override fun getSearchHistorySet(callback: Callback<Set<String>>) {
        localDataSource.getSearchHistorySet(object : Callback<Set<String>> {
            override fun onLoaded(t: Set<String>) {
                callback.onLoaded(t)
            }

            override fun onFailed(mes: String) {
            }
        })
    }

    override fun saveSearchHistorySet(historySet: Set<String>) {
        localDataSource.saveSearchHistorySet(historySet)
    }

    fun loadHotSearchStrings(callback: Callback<List<HotSearchString>>) {
        netDataSource.loadHotSearchStrings(object : Callback<HotSearchResponse> {
            override fun onLoaded(t: HotSearchResponse) {
                callback.onLoaded(t.result.hots)
            }

            override fun onFailed(mes: String) {
                callback.onFailed(mes)
            }

        })
    }

    fun <T, K> loadSearchResult(
        type: SearchType,
        keywords: String,
        offset: Int,
        limit: Int,
        cls: Class<K>,
        callback: Callback<List<T>>
    ) {
        logD("loadSearchResult2")

        netDataSource.loadSearchResult(keywords, type.value, offset,limit, cls, object : Callback<K> {
            override fun onLoaded(t: K) {

                when (t) {
                    is SearchSongResultResponse -> {
                        callback.onLoaded(t.result.songs as List<T>)
                    }

                    is SearchArtistResultResponse -> {
                        callback.onLoaded(t.result.artists as List<T>)
                    }

                    is SearchAlbumResultResponse -> {
                        callback.onLoaded(t.result.albums as List<T>)
                    }

                    is SearchPlayListResultResponse -> {
                        callback.onLoaded(t.result.playlists as List<T>)
                    }

                    is SearchUserResultResponse -> {
                        callback.onLoaded(t.result.userprofiles as List<T>)
                    }

                }
            }

            override fun onFailed(mes: String) {
                callback.onFailed(mes)
            }
        })
    }


//    fun loadSearchSongResult(keywords: String, limit: Int, callback: Callback<List<SearchSong>>) {
//        netDataSource.loadSearchResult(
//            keywords,
//            1,
//            limit,
//            object : Callback<SearchSongResultResponse> {
//                override fun onLoaded(t: SearchSongResultResponse) {
//                    callback.onLoaded(t.result.songs)
//                }
//
//                override fun onFailed(mes: String) {
//                    callback.onFailed(mes)
//                }
//            })
//    }
//
//    fun loadSearchArtistResult(
//        keywords: String,
//        limit: Int,
//        callback: Callback<List<SearchArtist>>
//    ) {
//        netDataSource.loadSearchResult(
//            keywords,
//            100,
//            limit,
//            object : Callback<SearchArtistResultResponse> {
//                override fun onLoaded(t: SearchArtistResultResponse) {
//                    callback.onLoaded(t.result.artists)
//                }
//
//                override fun onFailed(mes: String) {
//                    callback.onFailed(mes)
//                }
//            })
//    }
//
//    fun loadSearchAlbumResult(keywords: String, limit: Int, callback: Callback<List<SearchAlbum>>) {
//        netDataSource.loadSearchResult(
//            keywords,
//            10,
//            limit,
//            object : Callback<SearchAlbumResultResponse> {
//                override fun onLoaded(t: SearchAlbumResultResponse) {
//                    callback.onLoaded(t.result.albums)
//                }
//
//                override fun onFailed(mes: String) {
//                    callback.onFailed(mes)
//                }
//            })
//    }
//
//    fun loadSearchPlayListResult(
//        keywords: String,
//        limit: Int,
//        callback: Callback<List<SearchPlayList>>
//    ) {
//        netDataSource.loadSearchResult(
//            keywords,
//            1000,
//            limit,
//            object : Callback<SearchPlayListResultResponse> {
//                override fun onLoaded(t: SearchPlayListResultResponse) {
//                    callback.onLoaded(t.result.playlists)
//                }
//
//                override fun onFailed(mes: String) {
//                    callback.onFailed(mes)
//                }
//            })
//    }
//
//    fun loadSearchUserResult(keywords: String, limit: Int, callback: Callback<List<SearchUser>>) {
//        netDataSource.loadSearchResult(
//            keywords,
//            1002,
//            limit,
//            object : Callback<SearchUserResultResponse> {
//                override fun onLoaded(t: SearchUserResultResponse) {
//                    callback.onLoaded(t.result.userprofiles)
//                }
//
//                override fun onFailed(mes: String) {
//                    callback.onFailed(mes)
//                }
//            })
//    }

}