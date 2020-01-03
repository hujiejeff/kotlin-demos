package com.hujiejeff.musicplayer.data.source.local

import com.hujiejeff.musicplayer.data.entity.Album
import com.hujiejeff.musicplayer.data.entity.Artist
import com.hujiejeff.musicplayer.data.entity.Music
import com.hujiejeff.musicplayer.data.source.LocalDataSource
import com.hujiejeff.musicplayer.execute.AppExecutors
import com.hujiejeff.musicplayer.util.getAlbumList
import com.hujiejeff.musicplayer.util.getArtistList
import com.hujiejeff.musicplayer.util.getMusicList

/**
 * Create by hujie on 2020/1/3
 */
class LocalMusicDataSource(val appExecutors: AppExecutors): LocalDataSource {
    override fun getLocalMusicList(callback: LocalDataSource.Callback<Music>) {
        appExecutors.diskIO.execute {
            val list = getMusicList()
            appExecutors.mainThread.execute {
                if (list.isEmpty()) {
                    callback.onFailed("is empty")
                } else {
                    callback.onLoaded(list)
                }
            }
        }
    }

    override fun getLocalAlbumList(callback: LocalDataSource.Callback<Album>) {
        appExecutors.diskIO.execute {
            val list = getAlbumList()
            appExecutors.mainThread.execute {
                if (list.isEmpty()) {
                    callback.onFailed("is empty")
                } else {
                    callback.onLoaded(list)
                }
            }
        }
    }

    override fun getLocalArtistList(callback: LocalDataSource.Callback<Artist>) {
        appExecutors.diskIO.execute {
            val list = getArtistList()
            appExecutors.mainThread.execute {
                if (list.isEmpty()) {
                    callback.onFailed("is empty")
                } else {
                    callback.onLoaded(list)
                }
            }
        }

    }

}