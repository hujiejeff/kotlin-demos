package com.hujiejeff.musicplayer.player

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hujiejeff.musicplayer.base.App
import com.hujiejeff.musicplayer.data.Preference
import com.hujiejeff.musicplayer.data.entity.Music
import com.hujiejeff.musicplayer.data.entity.PlayMode
import com.hujiejeff.musicplayer.data.entity.toPlayMode
import com.hujiejeff.musicplayer.util.logD

class PlayerViewModel: ViewModel(), OnPlayerEventListener {
    private val dataRepository by lazy { App.dateRepository }

    private val player: AudioPlayer = AudioPlayer.INSTANCE

    //当前音乐
    private val _currentMusic = MutableLiveData<Music>()
    val currentMusic: LiveData<Music>
        get() = _currentMusic

    //播放状态
    private val _isPlay = MutableLiveData<Boolean>().apply { value = false }
    val isPlay: LiveData<Boolean>
        get() = _isPlay

    //播放进度
    private val _playProgress = MutableLiveData<Int>().apply { value = Preference.play_progress }
    val playProgress: LiveData<Int>
        get() = _playProgress

    //播放模式
    private val _playMode = MutableLiveData<PlayMode>().apply { value = Preference.play_mode.toPlayMode() }
    val playMode: LiveData<PlayMode>
        get() = _playMode

    //MusicPlayFragment状态
    private val _isShow = MutableLiveData<Boolean>().apply { value = false }
    val isPlayFragmentShow: LiveData<Boolean>
        get() = _isShow

    val playPosition get() = player.playPosition

    //音乐列表
    val musicItems = MutableLiveData<List<Music>>()

    fun start() {
        //注册播放监听
        registerListener()
    }
    fun loadDefaultMusic() {
        logD("loadDefaultMusic---" + player.currentMusic.toString())
        _currentMusic.value = player.currentMusic
    }

    //注册播放事件监听
    private fun registerListener() {
        player.addOnPlayerEventListener(this)
    }

    //播放
    fun play(position: Int) {
        player.play(position)
    }

    //播放或暂停
    fun playOrPause() {
        player.playOrPause()
    }

    //暂停
    fun pause() {
        player.pause()
    }

    //下一首
    fun next() {
        player.next()
    }

    //上一首
    fun pre() {
        player.pre()
    }

    fun seekTo(mesc: Int) {
        player.seekTo(mesc)
    }

    //打开播放页面
    fun showOrHidePlayFragment() {
        _isShow.value = !_isShow.value!!
    }

    //修改播放循环模式
    fun changeLoopMode() {
        val playMode = PlayMode.valueOf(Preference.play_mode)
        if (playMode == PlayMode.SHUFFLE) {
            player.setMode(PlayMode.LOOP)
        } else {
            var m = playMode.value
            m = (++m) % 3
            player.setMode(PlayMode.valueOf(m))
        }
    }

    //随机模式
    fun changeShuffleMode() {
        val playMode = PlayMode.valueOf(Preference.play_mode)
        if (playMode != PlayMode.SHUFFLE) {
            player.setMode(PlayMode.SHUFFLE)
        } else {
            player.setMode(PlayMode.SINGLE_LOOP)
        }
    }


    /**
     * 播放监听回调
     * */
    override fun onChange(music: Music) {
        _currentMusic.value = music
    }

    override fun onPlayerStart() {
        _isPlay.value = true
    }

    override fun onPlayerPause() {
        _isPlay.value = false
    }

    override fun onPublish(progress: Int) {
        _playProgress.value = progress
    }

    override fun onBufferingUpdate(percent: Int) {
        //暂不需要
    }

    override fun onModeChange(value: Int) {
        _playMode.value = PlayMode.valueOf(value)
    }
}