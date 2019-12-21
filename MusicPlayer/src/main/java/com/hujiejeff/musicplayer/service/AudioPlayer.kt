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
        const val STATUS_IDLE = 0
        const val STATUS_PREPARING = 1
        const val STATUS_PLAYING = 2
        const val STATUS_PAUSE = 3
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
    private val isPlaying: Boolean
        get() = state == STATUS_PLAYING
    private val isIdle: Boolean
        get() = state == STATUS_IDLE
    private val position: Int
        get() {
            //TODO Preference获取
            return 0
        }
    private val currentMusic: Music
        get() = mMusicList[position]
    private val audioPositon: Int
        get() = if (isPause || isPlaying)  mMediaPlayer.currentPosition else 0


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
        when {
            isPreparing -> stopPlayer()
            isPlaying -> pausePlayer()
            isPause -> startPlayer()
            else -> play(position)
        }
    }

    private fun startPlayer() {
        if (!isPreparing && !isPause) {
            return
        }
        mMediaPlayer.start()
        state = STATUS_PLAYING

    }

    private fun pausePlayer() {
        if (!isPlaying) {
            return
        }
        mMediaPlayer.pause()
        state = STATUS_PAUSE
    }

    private fun stopPlayer() {
        if (isIdle) {
            return
        }
        pausePlayer()
        mMediaPlayer.stop()
        state = STATUS_IDLE
    }

    fun next() {
        playNextOrPre(false)
    }

    fun pre() {
        playNextOrPre(true)
    }
    fun playNextOrPre(next: Boolean) {
        if (mMusicList.isEmpty()) {
            return
        }

    }
    fun seekTo(msec: Int) {
        if (isPlaying || isPause || isIdle) {
            if (isIdle) {
                play(position)
            }
            mMediaPlayer.seekTo(msec)
        }
    }

    fun setMode() {}

    private val runnable = Runnable{

    }

}