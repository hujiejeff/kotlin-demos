package com.hujiejeff.musicplayer.data.source

import com.hujiejeff.musicplayer.data.entity.PlayListCatlistResponse
import com.hujiejeff.musicplayer.data.entity.PlayListDetailResponse
import com.hujiejeff.musicplayer.data.entity.PlayListsResponse


/**
 * Create by hujie on 2020/1/3
 */
interface NetDataSource {


    //获取歌单分类响应，包含大分类和小分类
    fun loadPlayListCatList(callback: Callback<PlayListCatlistResponse>)

    //获取普通歌单响应
    fun loadNormalPlayLists(cat: String, limit: Int, order: String, callback: Callback<PlayListsResponse>)

    //获取歌单详情响应
    fun loadPlaylistDetail(id: Long, callback: Callback<PlayListDetailResponse>)

}