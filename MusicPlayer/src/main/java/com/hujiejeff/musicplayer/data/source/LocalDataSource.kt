package com.hujiejeff.musicplayer.data.source

import com.hujiejeff.musicplayer.data.entity.Album
import com.hujiejeff.musicplayer.data.entity.Artist
import com.hujiejeff.musicplayer.data.entity.Music

/**
 * Create by hujie on 2020/1/3
 */
interface LocalDataSource {
    fun getLocalMusicList(callback: Callback<Music>)
    fun getLocalAlbumList(callback: Callback<Album>)
    fun getLocalArtistList(callback: Callback<Artist>)

    interface Callback<T>{
        fun onLoaded(dataList: MutableList<T>)
        fun onFailed(mes: String)
    }
}