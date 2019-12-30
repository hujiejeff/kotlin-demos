package com.hujiejeff.musicplayer.service

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import androidx.core.os.postDelayed
import com.hujiejeff.musicplayer.OnPlayerEventListener
import com.hujiejeff.musicplayer.entity.Music
import com.hujiejeff.musicplayer.entity.PlayMode
import com.hujiejeff.musicplayer.storage.Preference
import com.hujiejeff.musicplayer.util.getMusicList
import com.hujiejeff.musicplayer.util.logD
import kotlin.random.Random

class AudioPlayer private constructor() {
    companion object {
        val INSTANCE: AudioPlayer by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            AudioPlayer()
        }
        const val STATUS_IDLE = 0
        const val STATUS_PREPARING = 1
        const val STATUS_PLAYING = 2
        const val STATUS_PAUSE = 3
        const val TIME_UPDATE = 300L
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
        get()  = Preference.play_position
    val currentMusic: Music
        get() = mMusicList[position]
    private val audioPosition: Int
        get() = if (isPause || isPlaying) mMediaPlayer.currentPosition else 0
    private val playListeners = mutableSetOf<OnPlayerEventListener>()
    private var isResume: Boolean = false


    fun init(context: Context) {
        mContext = context.applicationContext
        mMediaPlayer = MediaPlayer()
        mMusicList = getMusicList()//TODO 两次获取有问题
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
            setOnBufferingUpdateListener { _, percent ->
                triggerListener {
                    onBufferingUpdate(percent)
                }
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
            state = STATUS_PREPARING
            if (!isResume) {
                triggerListener {
                    onChange(music)
                }
            }
        }
        Preference.play_position = position
        //TODO 通知
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
        logD("startPlayer")
        if (!isPreparing && !isPause) {
            return
        }
        mMediaPlayer.start()
        state = STATUS_PLAYING

        if (isResume) {
            seekTo(Preference.play_progress)
            isResume = false
        }

        mHandler.post(publishRunnable)
        triggerListener {
            onPlayerStart()
        }

    }

    private fun pausePlayer() {
        if (!isPlaying) {
            return
        }
        mMediaPlayer.pause()
        state = STATUS_PAUSE
        Preference.play_progress = audioPosition
        triggerListener {
            onPlayerPause()
        }
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

    private fun playNextOrPre(next: Boolean) {
        if (mMusicList.isEmpty()) {
            return
        }
        when(PlayMode.valueOf(Preference.play_mode)) {
            PlayMode.SHUFFLE -> play(Random(1).nextInt(mMusicList.size))
            PlayMode.SINGLE -> stopPlayer()
            PlayMode.SINGLE_LOOP -> play(position)
            PlayMode.LOOP -> {
                if (next) {
                    play(position - 1)
                } else{
                    play(position + 1)
                }
            }
        }

    }

    fun seekTo(msec: Int) {
        if (isPlaying || isPause || isIdle) {
            if (isIdle) {
                play(position)
            }
            mMediaPlayer.seekTo(msec)
            triggerListener {
                onPublish(msec)
            }
        }
    }

    fun setMode(playMode: PlayMode) {
        Preference.play_mode = playMode.value
        triggerListener {
            onModeChange(playMode.value)
        }
    }

    fun addOnPlayerEventListener(listener: OnPlayerEventListener) {
        playListeners.add(listener)
    }

    fun removePlayerEventListener(listener: OnPlayerEventListener) {
        playListeners.remove(listener)
    }

    private fun triggerListener(action: OnPlayerEventListener.() -> Unit) {
        playListeners.forEach(action)
    }

    private val publishRunnable = object : Runnable {
        override fun run() {
            if (isPlaying) {
                triggerListener {
                    onPublish(audioPosition)
                }
            }
            mHandler.postDelayed(this, TIME_UPDATE)
        }
    }

}