package com.hujiejeff.musicplayer.data.source

import com.hujiejeff.musicplayer.data.entity.Album
import com.hujiejeff.musicplayer.data.entity.Artist
import com.hujiejeff.musicplayer.data.entity.Music

/**
 * Create by hujie on 2020/1/3
 */
interface LocalDataSource {
    fun getLocalMusicList(callback: Callback<List<Music>>)
    fun getLocalAlbumList(callback: Callback<List<Album>>)
    fun getLocalArtistList(callback: Callback<List<Artist>>)
    fun getSearchHistorySet(callback: Callback<Set<String>>)
    fun saveSearchHistorySet(historySet: Set<String>)

}