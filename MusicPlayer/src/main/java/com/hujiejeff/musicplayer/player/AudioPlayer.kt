package com.hujiejeff.musicplayer.player

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import android.os.Handler
import android.os.Looper
import com.hujiejeff.musicplayer.component.Notifier
import com.hujiejeff.musicplayer.data.entity.Music
import com.hujiejeff.musicplayer.data.entity.PlayMode
import com.hujiejeff.musicplayer.data.Preference
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
        const val TIME_UPDATE = 100L
    }

    private lateinit var mContext: Context
    private lateinit var mMediaPlayer: MediaPlayer
    var mMusicList: MutableList<Music> = mutableListOf()
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
    private var _position
        set(value) {
            Preference.play_position = value
        }
        get() = Preference.play_position
    val playPosition get() = _position

    val currentMusic: Music?
        get() {
            if (mMusicList.size == 0) {
                logD("size is 0")
                return null
            }
            if (_position !in 0 until mMusicList.size) {
                _position = 0
            }
            return mMusicList[_position]
        }
    private val audioPosition: Int
        get() = if (isPause || isPlaying) mMediaPlayer.currentPosition else 0
    private val playListeners = mutableSetOf<OnPlayerEventListener>()
    private var isResume: Boolean = false

    /**
     * 音频管理服务
     * */
    private val audioManager: AudioManager by lazy {
        mContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    }

    /**
     * 音频属性
     * */
    private val audioAttributes: AudioAttributes? by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build()
        } else {
            null
        }
    }

    /**
     * 焦点请求
     * */
    private val focusRequest: AudioFocusRequest? by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            audioAttributes?.let {
                AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                    .setAudioAttributes(it)
                    .setWillPauseWhenDucked(true)
                    .setOnAudioFocusChangeListener {
                        handleFocusChange(it)
                    }.build()
            }
        } else {
            null
        }
    }

    /**
     * 初始化
     * */
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
                    startPlayerWithRequestAudioFocus()
                }
            }
            setOnBufferingUpdateListener { _, percent ->
                triggerListener {
                    onBufferingUpdate(percent)
                }
            }
        }
    }

    /**
     * 播放哪一首
     * @param position 索引
     * */
    fun play(position: Int) {
        var pos = position + mMusicList.size
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
        _position = position
        //通知
        Notifier.getInstance().showPlay(music)
    }

    /**
     * 播放或暂停
     * */
    fun playOrPause() {
        when {
            isPreparing -> stopPlayerWithReleaseAudioFocus()
            isPlaying -> pausePlayerWithReleaseAudioFocus()
            isPause -> startPlayerWithRequestAudioFocus()
            else -> {
                play(_position)
                isResume = true
            }
        }
    }

    fun pause() {
        pausePlayerWithReleaseAudioFocus()
    }

    /**
     * 请求焦点并startPlayer
     * */
    private fun startPlayerWithRequestAudioFocus() {
        if (requestAudioFocus()) {
            startPlayer()
        } else {
            logD("fail to request AudioFocus")
        }
    }

    /**
     * 释放焦点并pausePlayer
     * */
    private fun pausePlayerWithReleaseAudioFocus() {
        releaseAudioFocus()
        pausePlayer()
    }

    private fun stopPlayerWithReleaseAudioFocus() {
        releaseAudioFocus()
        stopPlayer()
    }

    /**
     * 开始MediaPlayer
     * */
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
        Notifier.getInstance().showPlay(currentMusic!!)
    }

    /**
     * 暂停MediaPlayer
     * */
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
        Notifier.getInstance().showPause(currentMusic!!)
    }

    /**
     * 暂停MediaPlayer
     * */
    private fun stopPlayer() {
        if (isIdle) {
            return
        }
        pausePlayer()
        mMediaPlayer.stop()
        state = STATUS_IDLE
    }

    /**
     * 下一首
     * */
    fun next() {
        playNextOrPre(false)
    }

    /**
     * 上一首
     * */
    fun pre() {
        playNextOrPre(true)
    }

    /**
     * 上一首或下一首
     * */
    private fun playNextOrPre(next: Boolean) {
        if (mMusicList.isEmpty()) {
            return
        }
        when (PlayMode.valueOf(Preference.play_mode)) {
            PlayMode.SHUFFLE -> play(Random(1).nextInt(mMusicList.size))
            PlayMode.SINGLE -> stopPlayerWithReleaseAudioFocus()
            PlayMode.SINGLE_LOOP -> play(_position)
            PlayMode.LOOP -> {
                if (next) {
                    play(_position - 1)
                } else {
                    play(_position + 1)
                }
            }
        }

    }

    /**
     * 定位
     * */
    fun seekTo(msec: Int) {
        if (isPlaying || isPause || isIdle) {
            if (isIdle) {
                play(_position)
            }
            mMediaPlayer.seekTo(msec)
            triggerListener {
                onPublish(msec)
            }
        }
    }

    /**
     * 设置播放模式
     * */
    fun setMode(playMode: PlayMode) {
        Preference.play_mode = playMode.value
        triggerListener {
            onModeChange(playMode.value)
        }
    }

    /**
     * 设置监听
     * */
    fun addOnPlayerEventListener(listener: OnPlayerEventListener) {
        playListeners.add(listener)
    }

    /**
     * 移除监听
     * */
    fun removePlayerEventListener(listener: OnPlayerEventListener) {
        playListeners.remove(listener)
    }

    /**
     * 遍历触发
     * */
    private fun triggerListener(action: OnPlayerEventListener.() -> Unit) {
        playListeners.forEach(action)
    }

    /**
     * 请求音频焦点
     * */
    private fun requestAudioFocus(): Boolean {
        val result: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            focusRequest?.let { audioManager.requestAudioFocus(it) }!!
        } else {
            audioManager.requestAudioFocus({
                handleFocusChange(it)
            }, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN)
        }
        return result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
    }

    /**
     * 释放音频焦点
     * */
    private fun releaseAudioFocus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            audioManager.abandonAudioFocusRequest(focusRequest!!)
        } else {
            audioManager.abandonAudioFocus {
                logD("abandonAudioFocusRequest")
            }
        }
    }

    /**
     * 处理焦点变化
     * */
    private fun handleFocusChange(focusChange: Int) {
        when (focusChange) {
            AudioManager.AUDIOFOCUS_GAIN -> {
                startPlayer()
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                pausePlayer()
            }
            AudioManager.AUDIOFOCUS_LOSS -> {
                stopPlayer()
            }
        }
    }


    /**
     * 释放
     * */
    fun release() {
        stopPlayer()
        mMediaPlayer.release()
    }


    /**
     * 更新进度
     * */
    private val publishRunnable = object : Runnable {
        override fun run() {
            if (isPlaying) {
                triggerListener {
                    onPublish(audioPosition)
                }
            }
            mHandler.postDelayed(this,
                TIME_UPDATE
            )
        }
    }

}