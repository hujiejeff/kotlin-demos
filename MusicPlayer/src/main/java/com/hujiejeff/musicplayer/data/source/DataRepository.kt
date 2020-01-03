package com.hujiejeff.musicplayer.data.source

import com.hujiejeff.musicplayer.data.entity.Album
import com.hujiejeff.musicplayer.data.entity.Artist
import com.hujiejeff.musicplayer.data.entity.Music
import com.hujiejeff.musicplayer.data.source.local.LocalMusicDataSource

/**
 * Create by hujie on 2020/1/3
 */
class DataRepository(private val localMusicDataSource: LocalMusicDataSource) : LocalDataSource {

    private var cacheMusicList: MutableList<Music> = mutableListOf()
    private var cacheAlbumList: MutableList<Album> = mutableListOf()
    private var cacheArtistList: MutableList<Artist> = mutableListOf()

    private var cacheIsDirty: Boolean = false
    override fun getLocalMusicList(callback: LocalDataSource.Callback<Music>) {
        if (cacheMusicList.isNotEmpty()) {
            callback.onLoaded(cacheMusicList)
            return
        }

        localMusicDataSource.getLocalMusicList(object : LocalDataSource.Callback<Music> {
            override fun onLoaded(dataList: MutableList<Music>) {
                cacheMusicList.addAll(dataList)
                callback.onLoaded(cacheMusicList)
            }
            override fun onFailed(mes: String) {
                callback.onFailed(mes)
            }
        })
    }

    override fun getLocalAlbumList(callback: LocalDataSource.Callback<Album>) {
        if (cacheAlbumList.isNotEmpty()) {
            callback.onLoaded(cacheAlbumList)
            return
        }

        localMusicDataSource.getLocalAlbumList(object : LocalDataSource.Callback<Album> {
            override fun onLoaded(dataList: MutableList<Album>) {
                cacheAlbumList.addAll(dataList)
                callback.onLoaded(cacheAlbumList)
            }
            override fun onFailed(mes: String) {
                callback.onFailed(mes)
            }
        })
    }

    override fun getLocalArtistList(callback: LocalDataSource.Callback<Artist>) {
        if (cacheArtistList.isNotEmpty()) {
            callback.onLoaded(cacheArtistList)
            return
        }

        localMusicDataSource.getLocalArtistList(object : LocalDataSource.Callback<Artist> {
            override fun onLoaded(dataList: MutableList<Artist>) {
                cacheArtistList.addAll(dataList)
                callback.onLoaded(cacheArtistList)
            }
            override fun onFailed(mes: String) {
                callback.onFailed(mes)
            }
        })
    }

}