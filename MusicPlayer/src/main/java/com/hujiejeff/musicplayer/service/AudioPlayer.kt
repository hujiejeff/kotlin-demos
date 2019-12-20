package com.hujiejeff.musicplayer.service

import android.content.Context
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import com.hujiejeff.musicplayer.entity.Music

class AudioPlayer private constructor() {
    companion object {
        val INSTANCE: AudioPlayer by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            AudioPlayer()
        }
        val STATUS_IDLE = 0;
        val STATUS_PREPARING = 1
        val STATUS_PLAYING = 2
        val STATUS_PAUSE = 3
    }

    private lateinit var mContext: Context
    private lateinit var mMediaPlayer: MediaPlayer
    private lateinit var mMusicList: List<Music>
    private lateinit var mHandler: Handler
    private var state: Int = STATUS_IDLE
    private val isPreparing: Boolean
        get() = state == STATUS_PREPARING
    private val isPause: Boolean
        get() = state == STATUS_PAUSE

    fun init(context: Context) {
        mContext = context.applicationContext
        mMediaPlayer = MediaPlayer()
        mHandler = Handler(Looper.getMainLooper())
        mMediaPlayer.apply {
            setOnCompletionListener {
                next()
            }
            setOnPreparedListener {
                if (isPreparing) {
                    startPlayer()
                }
            }
            setOnBufferingUpdateListener { _, _ ->

            }
        }
    }

    fun play(position: Int) {
        var pos = position
        if (mMusicList.isEmpty()) {
            return
        }
        pos %= mMusicList.size
        val music = mMusicList[pos]
        mMediaPlayer.apply {
            reset()
            setDataSource(music.filePath)
            prepareAsync()
            state = STATUS_PLAYING
        }
        //TODO 回调切换监听
        //TODO 通知
        //TODO 存储当前播放信息
    }

    fun playOrPause() {

    }

    fun startPlayer() {
        if (!isPreparing && !isPause) {
            return
        }
        mMediaPlayer.start()
        state = STATUS_PLAYING

    }

    fun pausePlayer() {}
    fun stopPlayer() {}
    fun next() {}
    fun pre() {}
    fun playNextOrPre(next: Boolean) {}
    fun seekTo(msec: Int) {}
    fun setMode() {}


}