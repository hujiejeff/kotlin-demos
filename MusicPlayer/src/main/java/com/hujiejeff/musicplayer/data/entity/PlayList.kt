package com.hujiejeff.musicplayer.data.entity


/**
 * Create by hujie on 2020/1/8
 */


data class PlayListCatlistResponse(
    val all: SubCat,
    val sub: List<SubCat>,
    val categories: Map<Int, String>,
    val code: Int
)

//小分类
data class SubCat(
    val name: String,
    val type: Int,
    val category: Int, //大分类
    val hot: Boolean
)



//歌单列表响应
data class PlayListsResponse(
    val code: Int,
    val playlists: List<PlayList>,
    val total: Int,
    val cat: String,
    val more: Boolean
)

//歌单
data class PlayList(val id: Long, val name: String, val coverImgUrl: String, val playCount: Int)

data class PlayListDetailResponse(val code: Int, val playlist: PlayListDetail)

//歌单详情
data class PlayListDetail(
    val id: Long,
    val name: String,
    val coverImgUrl: String,
    val playCount: Int,
    val description: String,
    val tracks: List<Track>,
    val shareCount: Int,
    val commentCount: Int,
    val tags: List<String>
)

//歌曲
data class Track(val id: Long, val name: String, val ar: List<Ar>, val al: Al)

//作者
data class Ar(val id: Long, val name: String)

//专辑
data class Al(val id: Long, val name: String, val picUrl: String)

