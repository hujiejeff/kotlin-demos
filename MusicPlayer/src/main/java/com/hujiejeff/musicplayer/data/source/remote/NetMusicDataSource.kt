package com.hujiejeff.musicplayer.data.source.remote

import com.hujiejeff.musicplayer.data.entity.*
import com.hujiejeff.musicplayer.data.source.Callback
import com.hujiejeff.musicplayer.data.source.NetDataSource
import com.hujiejeff.musicplayer.component.AppExecutors


/**
 * Create by hujie on 2020/1/3
 */
class NetMusicDataSource(
    private val apis: Apis,
    private val appExecutors: AppExecutors
) : NetDataSource {


    override fun loadPlayListCatList(callback: Callback<PlayListCatlistResponse>) {
        networkIOExecute {
            val response = apis.getPlayListCatList().execute()
            mainThreadExecute {
                if (response.isSuccessful && response.body().code == 200) {
                    callback.onLoaded(response.body())
                } else {
                    callback.onFailed(response.errorBody().string())
                }
            }
        }
    }

    override fun loadNormalPlayLists(
        cat: String,
        limit: Int,
        order: String,
        callback: Callback<PlayListsResponse>
    ) {
        networkIOExecute {
            val response = apis.getNormalPlayLists(cat, limit, order).execute()
            mainThreadExecute {
                if (response.isSuccessful && response.body().code == 200) {
                    callback.onLoaded(response.body())
                } else {
                    callback.onFailed(response.errorBody().string())
                }
            }
        }
    }

    override fun loadPlaylistDetail(
        id: Long,
        callback: Callback<PlayListDetailResponse>
    ) {
        networkIOExecute {
            val response = apis.getPlayListDetail(id).execute()
            mainThreadExecute {
                if (response.isSuccessful && response.body().code == 200) {
                    callback.onLoaded(response.body())
                } else {
                    callback.onFailed(response.errorBody().string())
                }
            }
        }
    }


    override fun loadTrackDetail(id: Long, callback: Callback<TrackResponse>) {
        networkIOExecute {
            val response = apis.getMusicUrl(id).execute()
            mainThreadExecute {
                if (response.isSuccessful && response.body().code == 200) {
                    callback.onLoaded(response.body())
                } else {
                    callback.onFailed(response.errorBody().string())
                }
            }
        }
    }

    private fun networkIOExecute(action: () -> Unit) {
        appExecutors.networkIO.execute(action)
    }

    private fun mainThreadExecute(action: () -> Unit) {
        appExecutors.mainThread.execute(action)
    }

    override fun loadRecommendPlaylists(limit: Int, callback: Callback<RecommendPlayListResponse>) {
        networkIOExecute {
            val response = apis.getRecommendPlaylist(limit).execute()
            mainThreadExecute {
                if (response.isSuccessful && response.body().code == 200) {
                    callback.onLoaded(response.body())
                } else {
                    callback.onFailed(response.errorBody().string())
                }
            }
        }
    }

    override fun loadRecommendNewAlbum(callback: Callback<RecommendNewAlbumResponse>) {
        networkIOExecute {
            val response = apis.getNewAlbum().execute()
            mainThreadExecute {
                if (response.isSuccessful && response.body().code == 200) {
                    callback.onLoaded(response.body())
                } else {
                    callback.onFailed(response.errorBody().string())
                }
            }
        }
    }

    override fun loadRecommendNewSong(callback: Callback<RecommendNewSongResponse>) {
        networkIOExecute {
            val response = apis.getNewSong().execute()
            mainThreadExecute {
                if (response.isSuccessful && response.body().code == 200) {
                    callback.onLoaded(response.body())
                } else {
                    callback.onFailed(response.errorBody().string())
                }
            }
        }
    }
}