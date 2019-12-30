package com.hujiejeff.musicplayer

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.FragmentPagerAdapter
import com.hujiejeff.musicplayer.base.BaseActivity
import com.hujiejeff.musicplayer.fragment.AlbumListFragment
import com.hujiejeff.musicplayer.fragment.ArtistListFragment
import com.hujiejeff.musicplayer.fragment.MusicListFragment
import com.hujiejeff.musicplayer.fragment.MusicPlayFragment
import com.hujiejeff.musicplayer.service.AudioPlayer
import com.hujiejeff.musicplayer.util.logD
import com.hujiejeff.musicplayer.util.transaction
import kotlinx.android.synthetic.main.include_activity_main.*
import kotlinx.android.synthetic.main.include_play_bar.*

class MainActivity : BaseActivity() {
    private val fragmentList =
        listOf(MusicListFragment(), AlbumListFragment(), ArtistListFragment())
    private val titleList by lazy { listOf(getString(R.string.tab_music), getString(R.string.tab_album), getString(R.string.tab_artist)) }
    private val musicPlayFragment by lazy { MusicPlayFragment() }


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

        AudioPlayer.INSTANCE.addOnPlayerEventListener(ControlPanel(play_bar))
    }

    private fun openMusicPlayFragment() {
        transaction {
            setCustomAnimations(R.anim.fragment_slide_up, 0)
            show(musicPlayFragment)
        }
    }



    inner class PagerAdapter : FragmentPagerAdapter(supportFragmentManager) {
        override fun getCount() = fragmentList.size
        override fun getItem(position: Int) = fragmentList[position]
        override fun getPageTitle(position: Int): CharSequence? = titleList[position]
    }
}
