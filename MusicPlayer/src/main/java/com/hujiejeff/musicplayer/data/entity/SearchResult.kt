package com.hujiejeff.musicplayer.data.entity

/**
 * Create by hujie on 2020/3/9
 */
data class SearchAlbum(
    val id: Long,
    val name: String,
    val picUrl: String,
    val publishTime: Long,
    val containedSong: String,
    val artists: List<Ar>
)

data class SearchArtist(
    val id: Long,
    val name: String,
    val picUrl: String?
)

data class SearchUser(
    val userId: Long,
    val nickname: String,
    val signature: String,
    val avatarUrl: String?
)

data class SearchPlayList(
    val id: Long,
    val name: String,
    val coverImgUrl: String,
    val trackCount: Int,
    val creator: SearchUser,
    val playCount: Int
)


data class SearchSong(val id: Long, val name: String, val artists: List<Ar>, val album: SearchAlbum)
data class SearchSongResult(val songCount: Int, val songs: List<SearchSong>)
data class SearchSongResultResponse(val code: Int, val result: SearchSongResult)

data class SearchArtistResult(val artistCount: Int, val artists: List<SearchArtist>)
data class SearchArtistResultResponse(val code: Int, val result: SearchArtistResult)


data class SearchAlbumResult(val albumCount: Int, val albums: List<SearchAlbum>)
data class SearchAlbumResultResponse(val code: Int, val result: SearchAlbumResult)

data class SearchPlayListResult(val playlistCount: Int, val playlists: List<SearchPlayList>)
data class SearchPlayListResultResponse(val code: Int, val result: SearchPlayListResult)

data class SearchUserResult(val userprofileCount: Int, val userprofiles: List<SearchUser>)
data class SearchUserResultResponse(val code: Int, val result: SearchUserResult)



