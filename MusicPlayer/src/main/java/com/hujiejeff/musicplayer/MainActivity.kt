package com.hujiejeff.musicplayer

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Color
import android.os.Bundle
import android.os.IBinder
import android.os.SystemClock
import android.view.Gravity
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.fragment.app.FragmentPagerAdapter
import com.hujiejeff.musicplayer.base.BaseActivity
import com.hujiejeff.musicplayer.fragment.AlbumListFragment
import com.hujiejeff.musicplayer.fragment.ArtistListFragment
import com.hujiejeff.musicplayer.fragment.MusicListFragment
import com.hujiejeff.musicplayer.fragment.MusicPlayFragment
import com.hujiejeff.musicplayer.service.AudioPlayer
import com.hujiejeff.musicplayer.service.PlayService
import com.hujiejeff.musicplayer.util.logD
import com.hujiejeff.musicplayer.util.transaction
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.include_activity_main.*
import kotlinx.android.synthetic.main.include_play_bar.*

class MainActivity : BaseActivity() {
    private val fragmentList =
        listOf(MusicListFragment(), AlbumListFragment(), ArtistListFragment())
    private val titleList by lazy { listOf(getString(R.string.tab_music), getString(R.string.tab_album), getString(R.string.tab_artist)) }
    private val musicPlayFragment by lazy { MusicPlayFragment() }
    private var isShowMusicPlay = false
    private var backPress = 0
    private var firstBackPressTime = 0L
    private var secondBackPressTime = 1L


    override fun layoutResId() = R.layout.activity_main
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        logD("onCreate")
        tab_layout.apply {
            setTabTextColors(Color.BLACK, Color.WHITE)
            setupWithViewPager(view_pager)
        }

        view_pager.adapter = PagerAdapter()
        play_bar.setOnClickListener {
            openMusicPlayFragment()
        }

        transaction {
            add(android.R.id.content, musicPlayFragment)
            hide(musicPlayFragment)
        }

        val bindIntent = Intent(this, PlayService::class.java)
        val serviceConnection = object : ServiceConnection{
            override fun onServiceDisconnected(p0: ComponentName?) {
            }

            override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
                AudioPlayer.INSTANCE.addOnPlayerEventListener(ControlPanel(play_bar))
            }
        }
        bindService(bindIntent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    override fun onBackPressed() {
        if (isShowMusicPlay) {
            hideMusicPlayFragment()
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



    inner class PagerAdapter : FragmentPagerAdapter(supportFragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        override fun getCount() = fragmentList.size
        override fun getItem(position: Int) = fragmentList[position]
        override fun getPageTitle(position: Int): CharSequence? = titleList[position]
    }
}
