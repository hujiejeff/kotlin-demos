package com.hujiejeff.musicplayer.data.source

import com.hujiejeff.musicplayer.data.entity.*


/**
 * Create by hujie on 2020/1/3
 */
interface NetDataSource {


    //获取歌单分类响应，包含大分类和小分类
    fun loadPlayListCatList(callback: Callback<PlayListCatlistResponse>)

    //获取普通歌单响应
    fun loadNormalPlayLists(
        cat: String,
        limit: Int,
        order: String,
        callback: Callback<PlayListsResponse>
    )

    //获取歌单详情响应
    fun loadPlaylistDetail(id: Long, callback: Callback<PlayListDetailResponse>)

    //获取具体歌曲信息
    fun loadTrackDetail(id: Long, callback: Callback<TrackResponse>)

    //首页推荐歌单
    fun loadRecommendPlaylists(limit: Int, callback: Callback<RecommendPlayListResponse>)

    //首页最新专辑
    fun loadRecommendNewAlbum(callback: Callback<RecommendNewAlbumResponse>)

    //首页最新歌曲
    fun loadRecommendNewSong(callback: Callback<RecommendNewSongResponse>)

    //热搜词
    fun loadHotSearchStrings(callback: Callback<HotSearchResponse>)

    //搜搜索
    fun <T> loadSearchResult(
        keywords: String,
        type: Int,
        offset: Int,
        limit: Int,
        cls: Class<T>,
        callback: Callback<T>
    )

}