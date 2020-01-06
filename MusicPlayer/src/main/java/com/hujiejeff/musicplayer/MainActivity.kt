package com.hujiejeff.musicplayer

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.os.SystemClock
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.hujiejeff.musicplayer.base.BaseActivity
import com.hujiejeff.musicplayer.data.Preference
import com.hujiejeff.musicplayer.data.entity.Music
import com.hujiejeff.musicplayer.fragment.AlbumListFragment
import com.hujiejeff.musicplayer.fragment.ArtistListFragment
import com.hujiejeff.musicplayer.fragment.MusicListFragment
import com.hujiejeff.musicplayer.fragment.MusicPlayFragment
import com.hujiejeff.musicplayer.localmusic.LocalMusicViewModel
import com.hujiejeff.musicplayer.service.PlayService
import com.hujiejeff.musicplayer.util.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.include_activity_main.*
import kotlinx.android.synthetic.main.include_play_bar.*

class MainActivity : BaseActivity() {
    private val fragmentList =
        listOf(MusicListFragment(), AlbumListFragment(), ArtistListFragment())
    private val titleList by lazy {
        listOf(
            getString(R.string.tab_music),
            getString(R.string.tab_album),
            getString(R.string.tab_artist)
        )
    }
    private val musicPlayFragment by lazy { MusicPlayFragment() }
    private var isAddMusicPlay = false
    private var isShowMusicPlay = false
    private var backPress = 0
    private var firstBackPressTime = 0L
    private var secondBackPressTime = 1L

    private lateinit var viewModel: LocalMusicViewModel


    override fun layoutResId() = R.layout.activity_main
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        logD("onCreate")
        initView()
        bindService()
        initClickListener()
        subscribe()
    }

    private fun initView() {
        tab_layout.apply {
            setTabTextColors(Color.BLACK, Color.WHITE)
            setupWithViewPager(view_pager)
        }
        view_pager.adapter = PagerAdapter()
        checkAndroidVersionAction(Build.VERSION_CODES.M, {
            window.setTransparentStatusBar(false)
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
        play_bar.setOnClickListener {
            viewModel.showOrHidePlayFragment()
        }

        iv_play_bar_play.setOnClickListener {
            viewModel.playOrPause()
        }

        iv_play_bar_next.setOnClickListener {
            viewModel.next()
        }
    }

    private fun subscribe() {
        viewModel = obtainViewModel().apply {
            //music onchange
            currentMusic.observe(this@MainActivity, Observer<Music> { music ->
                tv_play_bar_title.text = music.title
                tv_play_bar_artist.text = music.artist
                iv_play_bar_cover.loadCover(music.albumID)
                pb_play_bar.max = music.duration.toInt()
                pb_play_bar.progress = if (!isPlay.value!!) Preference.play_progress else 0
            })

            //music state
            isPlay.observe(this@MainActivity, Observer<Boolean> { state ->
                iv_play_bar_play.isSelected = state
            })

            //music progress
            playProgress.observe(this@MainActivity, Observer<Int> { progress ->
                pb_play_bar.progress = progress
            })

            //play fragment open or close
            isPlayFragmentShow.observe(this@MainActivity, Observer<Boolean> { isShow ->
                transparentStatusBar(isShow)
                if (isShow) {
                    openMusicPlayFragment()
                } else {
                    hideMusicPlayFragment()
                }
            })
            //start
            start()
        }
    }


    private fun transparentStatusBar(show: Boolean) {
        checkAndroidVersionAction(Build.VERSION_CODES.M, {
            window.setTransparentStatusBar(show)
        })
    }

    override fun onBackPressed() {
        if (isShowMusicPlay) {
            viewModel.showOrHidePlayFragment()
            return
        }
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawers()
            return
        }

        if (backPress == 1) {
            secondBackPressTime = SystemClock.elapsedRealtime()
            val s = secondBackPressTime - firstBackPressTime
            if (s < 1000L) {
                super.onBackPressed()
            } else {
                secondBackPressTime = 0
                firstBackPressTime = 0
                backPress = 0
            }
        } else {
            backPress++
            Toast.makeText(this, R.string.again_back_press, Toast.LENGTH_SHORT).show()
            firstBackPressTime = SystemClock.elapsedRealtime()
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

    fun obtainViewModel(): LocalMusicViewModel =
        ViewModelProviders.of(this).get(LocalMusicViewModel::class.java)


    inner class PagerAdapter :
        FragmentPagerAdapter(supportFragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        override fun getCount() = fragmentList.size
        override fun getItem(position: Int) = fragmentList[position]
        override fun getPageTitle(position: Int): CharSequence? = titleList[position]
    }
}
