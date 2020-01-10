package com.hujiejeff.musicplayer.localmusic

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hujiejeff.musicplayer.service.OnPlayerEventListener
import com.hujiejeff.musicplayer.base.App
import com.hujiejeff.musicplayer.data.Preference
import com.hujiejeff.musicplayer.data.entity.*
import com.hujiejeff.musicplayer.data.source.Callback
import com.hujiejeff.musicplayer.service.AudioPlayer
import com.hujiejeff.musicplayer.util.logD

/**
 * Create by hujie on 2020/1/3
 */
class LocalMusicViewModel : ViewModel(),
    OnPlayerEventListener {

    private val dataRepository by lazy { App.dateRepository }

    private val player: AudioPlayer = AudioPlayer.INSTANCE

    //数据加载
    private val _musicDataLoading = MutableLiveData<Boolean>()
    val musicDataLoading: LiveData<Boolean>
        get() = _musicDataLoading

    private val _albumDataLoading = MutableLiveData<Boolean>()
    val albumDataLoading: LiveData<Boolean>
        get() = _albumDataLoading

    private val _artistDataLoading = MutableLiveData<Boolean>()
    val artistDataLoading: LiveData<Boolean>
        get() = _artistDataLoading


    //错误提示
    private val _isDataLoadingError = MutableLiveData<Boolean>()
    val isDataLoadingError: LiveData<Boolean>
        get() = _isDataLoadingError

    //音乐列表
    private val _musicItems = MutableLiveData<List<Music>>()
    val musicItems: LiveData<List<Music>>
        get() = _musicItems

    //专辑列表
    private val _albumItems = MutableLiveData<List<Album>>().apply { value = emptyList() }
    val albumItems: LiveData<List<Album>>
        get() = _albumItems

    //歌手列表
    private val _artistItems = MutableLiveData<List<Artist>>().apply { value = emptyList() }
    val artistItems: LiveData<List<Artist>>
        get() = _artistItems

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

    fun start() {
        //注册播放监听
        registerListener()
    }

    fun loadDefaultMusic() {
        logD("loadDefaultMusic---" + player.currentMusic.toString())
        _currentMusic.value = player.currentMusic
    }

    //加载音乐列表
    fun loadMusicList() {
        _musicDataLoading.value = true
        dataRepository.getLocalMusicList(object : Callback<List<Music>> {
            override fun onLoaded(dataList: List<Music>) {
                //因为是立即回调，所以要先设置player得list再触发事件，不然后面会先出发回调，会导致player.currentMusic获得不到
                player.mMusicList.addAll(dataList)
                _musicItems.value = dataList
                _musicDataLoading.value = false
                _isDataLoadingError.value = false
            }

            override fun onFailed(mes: String) {
                _isDataLoadingError.value = true
            }
        })
    }

    //加载专辑列表
    fun loadAlbumList() {
        _albumDataLoading.value = true
        dataRepository.getLocalAlbumList(object : Callback<List<Album>>{
            override fun onLoaded(dataList: List<Album>) {
                _albumItems.value = dataList
                _albumDataLoading.value = false
                _isDataLoadingError.value = false
            }

            override fun onFailed(mes: String) {
                _isDataLoadingError.value = true
            }

        })
    }

    //加载歌手列表
    fun loadArtistList() {
        _artistDataLoading.value = true
        dataRepository.getLocalArtistList(object : Callback<List<Artist>>{
            override fun onLoaded(dataList: List<Artist>) {
                _artistItems.value = dataList
                _artistDataLoading.value = false
                _isDataLoadingError.value = false
            }

            override fun onFailed(mes: String) {
                _isDataLoadingError.value = true
            }

        })
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