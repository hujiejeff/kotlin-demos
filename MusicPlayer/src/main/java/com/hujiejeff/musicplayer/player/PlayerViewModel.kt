package com.hujiejeff.musicplayer.player

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hujiejeff.musicplayer.data.Preference
import com.hujiejeff.musicplayer.data.entity.*
import com.hujiejeff.musicplayer.data.source.Callback
import com.hujiejeff.musicplayer.data.source.DataRepository
import com.hujiejeff.musicplayer.util.logD

class PlayerViewModel(private val dataRepository: DataRepository): ViewModel(), OnPlayerEventListener {

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

    private val _bufferProgress = MutableLiveData<Int>()
    val bufferProgress: LiveData<Int>
        get() = _bufferProgress

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

    //播放索引
    val position = MutableLiveData<Int>()



    //当前播放列表模式，网络 or 本地
    private val isPlayNetMusic = false

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
        val music = musicItems.value?.get(position)
        logD(music.toString())
        if (music?.type == 1) {
            //网络歌曲需要根据ID加载播放url等信息
            dataRepository.getTrackDetail(music.id, object : Callback<TrackData>{
                override fun onLoaded(t: TrackData) {
                    music.filePath = t.url
                    music.fileSize = t.size
                    music.duration = t.size * 8 * 1000 / t.br
                    player.play(position)
                }

                override fun onFailed(mes: String) {
                }
            })
            return
        }
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


    //加载本地音乐列表
    fun loadLocalMusicList(musicList: List<Music>) {
        musicItems.value = musicList
    }

    //加载网络音乐列表
    fun loadNetMusicList(trackList: List<Track>) {

        val list = MutableList(trackList.size) { index ->
            val track = trackList[index]
            val al = track.al
            val ar = track.ar
            val music = Music(track.id, 1, ar[0].name, "", "", al.id, al.name, track.name, 0L,0L)
            music.coverSrc = al.picUrl
            music
        }
        musicItems.value = list
        AudioPlayer.INSTANCE.mMusicList = list
    }


    /**
     * 播放监听回调
     * */
    override fun onChange(music: Music, p: Int) {
        _currentMusic.value = music
        position.value = p
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
        _bufferProgress.value = (percent * _currentMusic.value!!.duration / 100).toInt()
    }

    override fun onModeChange(value: Int) {
        _playMode.value = PlayMode.valueOf(value)
    }
}