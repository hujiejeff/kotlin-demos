package com.hujiejeff.musicplayer

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.hujiejeff.musicplayer.base.App
import com.hujiejeff.musicplayer.base.BaseActivity
import com.hujiejeff.musicplayer.data.Preference
import com.hujiejeff.musicplayer.data.entity.Music
import com.hujiejeff.musicplayer.discover.DiscoverFragment
import com.hujiejeff.musicplayer.video.VideoFragment
import com.hujiejeff.musicplayer.localmusic.LocalMusicFragment
import com.hujiejeff.musicplayer.player.MusicPlayFragment
import com.hujiejeff.musicplayer.service.PlayService
import com.hujiejeff.musicplayer.util.checkAndroidVersionAction
import com.hujiejeff.musicplayer.util.getCover
import com.hujiejeff.musicplayer.util.setTransparentStatusBar
import com.hujiejeff.musicplayer.util.transaction
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : BaseActivity() {
    override fun layoutResId(): Int = R.layout.activity_home

    private val fragments = arrayListOf<Fragment>(
        DiscoverFragment(),
        LocalMusicFragment(), VideoFragment()
    )
    private var preIndex: Int = 0

    private val musicPlayFragment by lazy { MusicPlayFragment() }
    private var isAddMusicPlay = false
    private var isShowMusicPlay = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        initView()
        bindService()
        initClickListener()
        subscribe()
    }

    private fun initView() {
        loadFragment(0)
        checkAndroidVersionAction(Build.VERSION_CODES.M, {
            window.setTransparentStatusBar(true)
        })
    }

    private fun bindService() {
        val bindIntent = Intent(this, PlayService::class.java)
        val serviceConnection = object : ServiceConnection {
            override fun onServiceDisconnected(p0: ComponentName?) {
            }

            override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
            }
        }
        bindService(bindIntent, serviceConnection, Context.BIND_AUTO_CREATE)
    }


    private fun initClickListener() {
        bnv_bottom.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.menu_bottom_item_discover -> showAndHidFragment(0, preIndex)
                R.id.menu_bottom_item_my -> showAndHidFragment(1, preIndex)
                R.id.menu_bottom_item_video -> showAndHidFragment(2, preIndex)
            }
            true
        }

        ppv_player.setOnClickListener {
            App.playerViewModel.showOrHidePlayFragment()
        }
    }

    private fun subscribe() {
        App.playerViewModel.apply {
            currentMusic.observe(this@HomeActivity,
                Observer<Music> { music ->
                    ppv_player.max = music.duration.toInt()
                    ppv_player.progress = if (!isPlay.value!!) Preference.play_progress else 0

                    if (music.type == 1) {
                        ppv_player.setSrc(music.coverSrc!!)
                    } else {
                        ppv_player.setBitmap(getCover(music.albumID))
                    }
                })

            playProgress.observe(this@HomeActivity, Observer<Int> { progress ->
                ppv_player.progress = progress
            })

            isPlayFragmentShow.observe(this@HomeActivity, Observer {isShow ->
                if (isShow) {
                    openMusicPlayFragment()
                } else {
                    hideMusicPlayFragment()
                }
            })
        }
    }

    private fun openMusicPlayFragment() {
        transaction {
            if (!isAddMusicPlay) {
                add(android.R.id.content, musicPlayFragment)
                isAddMusicPlay = true
            }
            setCustomAnimations(R.anim.fragment_slide_up, 0)
            show(musicPlayFragment)
            isShowMusicPlay = true
        }
    }

    private fun hideMusicPlayFragment() {
        transaction {
            setCustomAnimations(0, R.anim.fragment_slide_down)
            hide(musicPlayFragment)
            isShowMusicPlay = false
        }
    }


    private fun loadFragment(showIndex: Int) {
        transaction {
            for (i in fragments.indices) {
                add(R.id.fragment_container, fragments[i], fragments[i].tag)
                if (i != showIndex) {
                    hide(fragments[i])
                }
            }
            preIndex = showIndex
        }
    }

    private fun showAndHidFragment(showIndex: Int, hideIndex: Int) {
        if (showIndex != hideIndex) {
            transaction {
                supportFragmentManager.popBackStackImmediate()
                hide(fragments[hideIndex])
                show(fragments[showIndex])
            }
            preIndex = showIndex
        }
    }

    override fun onBackPressed() {
        if (isShowMusicPlay) {
            App.playerViewModel.showOrHidePlayFragment()
            return
        }
        super.onBackPressed()
    }
}